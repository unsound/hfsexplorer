/*-
 * Copyright (C) 2012 Erik Larsson
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catacombae.hfsexplorer.tools;

import java.io.IOException;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;
import org.catacombae.hfs.HFSVolume;
import org.catacombae.hfs.Journal;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogNodeID;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogNodeID.ReservedID;
import org.catacombae.hfs.types.hfscommon.CommonHFSExtentDescriptor;
import org.catacombae.hfs.types.hfscommon.CommonHFSForkData;
import org.catacombae.hfs.types.hfscommon.CommonHFSVolumeHeader;
import org.catacombae.hfs.types.hfsplus.JournalInfoBlock;
import org.catacombae.io.ReadableFileStream;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.io.RuntimeIOException;
import org.catacombae.storage.fs.FileSystemDetector;
import org.catacombae.storage.fs.FileSystemHandler;
import org.catacombae.storage.fs.FileSystemHandlerFactory;
import org.catacombae.storage.fs.FileSystemMajorType;
import org.catacombae.storage.fs.hfs.HFSFileSystemHandler;
import org.catacombae.storage.fs.hfsplus.HFSPlusFileSystemHandler;
import org.catacombae.storage.fs.hfsx.HFSXFileSystemHandler;
import org.catacombae.storage.io.DataLocator;
import org.catacombae.storage.io.ReadableStreamDataLocator;
import org.catacombae.storage.io.win32.ReadableWin32FileStream;
import org.catacombae.util.Util;
import org.catacombae.util.Util.Pair;

/**
 *
 * @author Erik Larsson
 */
public class DumpHfs {
    private static void printUsage() {
        System.err.println("usage: DumpHfs <device|file>");
    }

    public static void main(String[] args) {
        if(args.length != 1) {
            printUsage();
            System.exit(1);
            return;
        }

        String devicePath = args[0];

        ReadableRandomAccessStream stream;
        if(ReadableWin32FileStream.isSystemSupported())
            stream = new ReadableWin32FileStream(devicePath);
        else
            stream = new ReadableFileStream(devicePath);

        DataLocator inputDataLocator =
                new ReadableStreamDataLocator(stream);

        FileSystemMajorType[] fsTypes =
                FileSystemDetector.detectFileSystem(inputDataLocator);

        FileSystemHandlerFactory fact = null;
        outer:
        for(FileSystemMajorType type : fsTypes) {
            switch(type) {
                case APPLE_HFS:
                case APPLE_HFS_PLUS:
                case APPLE_HFSX:
                    fact = type.createDefaultHandlerFactory();
                    break outer;
                default:
            }
        }

        if(fact == null) {
            System.err.println("No HFS file system found.");
            System.exit(1);
            return;
        }

        FileSystemHandler fsHandler = fact.createHandler(inputDataLocator);
        final HFSVolume vol;
        final boolean isHfsPlus;
        if(fsHandler instanceof HFSFileSystemHandler) {
            vol = ((HFSFileSystemHandler) fsHandler).getFSView();
            isHfsPlus = false;
        }
        else if(fsHandler instanceof HFSPlusFileSystemHandler) {
            vol = ((HFSPlusFileSystemHandler) fsHandler).getFSView();
            isHfsPlus = true;
        }
        else if(fsHandler instanceof HFSXFileSystemHandler) {
            vol = ((HFSXFileSystemHandler) fsHandler).getFSView();
            isHfsPlus = true;
        }
        else {
            throw new RuntimeException("Unexpected handler type: " +
                    fsHandler.getClass());
        }

        /* HFS assumes 512 byte sectors, regardless of the actual physical
         * sector size. */
        final CommonHFSVolumeHeader volumeHeader = vol.getVolumeHeader();
        final short sectorSize = 512;
        final long allocationBlockSize =
                volumeHeader.getAllocationBlockSize();
        final long sectorsPerAllocationBlock = allocationBlockSize / sectorSize;
        final long allocationBlockStart =
                volumeHeader.getAllocationBlockStart();
        final long allocationBlockCount =
                volumeHeader.getTotalBlocks();
        SortedSet<Long> inUseSectors = new TreeSet<Long>();
        final ReadableRandomAccessStream fsStream = vol.createFSStream();
        final byte[] buffer = new byte[sectorSize];

        if((allocationBlockSize % sectorSize) != 0) {
            throw new RuntimeException("Uneven block size: " +
                    allocationBlockSize);
        }

        /* Mark all sectors before the start of the allocation block area as
         * 'in use'. */
        for(long i = 0; i < allocationBlockStart; ++i) {
            inUseSectors.add(i);
        }

        /* If the allocation block area covers the first three sectors, mark
         * them as in use since they contain boot code and the volume header. */
        if(allocationBlockStart < 3) {
            for(long i = allocationBlockStart; i < 3; ++i) {
                inUseSectors.add(i);
            }
        }

        /* Mark last allocation block of the volume as in use (it's reserved for
         * the backup volume header, at least in HFS+). */
        for(long i = 0; i < sectorsPerAllocationBlock; ++i) {
            inUseSectors.add(allocationBlockStart +
                    (allocationBlockCount - 1) * sectorsPerAllocationBlock + i);
        }

        /* Determine the last sector of the volume.
         *
         * There may be up to one allocation block of data after the last
         * allocation block before the volume actually ends. This is due to
         * alignment issues and that the last sector (512 bytes) must be
         * considered reserved because of legacy stuff.
         */
        long sectorCount =
                ((volumeHeader.getFileSystemEnd() - 1) / sectorSize) + 1;

        /* Mark the sector following the last allocation block as in use.
         * We know that there's always at least one sector after the last
         * allocation block. If there's only one, then it's reserved. Otherwise,
         * we want to include it in the output anyway because it may hold the
         * backup boot sector. */
        inUseSectors.add(sectorCount - 1);

        /* Iterate to determine where the volume ends (this is not specified in
         * the volume header). */
        for(int i = 0; i < sectorsPerAllocationBlock - 1; ++i) {
            int res;
            try {
                fsStream.seek(sectorCount * sectorSize);
                res = fsStream.read(buffer);
            } catch(Exception e) { break; }

            if(res == -1)
                break;

            /* Mark sector as in use to make sure it is included in the
             * resulting output (may contain reserved data or backup volume
             * header). */
            inUseSectors.add(sectorCount);
            ++sectorCount;
        }

        /* Now proceed to gather the allocations connected to system files. */
        final LinkedList<Pair<CommonHFSForkData, ReservedID>> metadataForks =
            new LinkedList<Pair<CommonHFSForkData, ReservedID>>();
        metadataForks.add(new Pair<CommonHFSForkData, ReservedID>(
                    volumeHeader.getCatalogFile(), ReservedID.CATALOG_FILE));
        metadataForks.add(new Pair<CommonHFSForkData, ReservedID>(
                    volumeHeader.getExtentsOverflowFile(),
                    ReservedID.EXTENTS_FILE));
        metadataForks.add(new Pair<CommonHFSForkData, ReservedID>(
                    volumeHeader.getAllocationFile(),
                    ReservedID.ALLOCATION_FILE));
        metadataForks.add(new Pair<CommonHFSForkData, ReservedID>(
                    volumeHeader.getAttributesFile(),
                    ReservedID.ATTRIBUTES_FILE));
        metadataForks.add(new Pair<CommonHFSForkData, ReservedID>(
                    volumeHeader.getStartupFile(), ReservedID.STARTUP_FILE));

        for(Pair<CommonHFSForkData, ReservedID> curFork : metadataForks) {
            final CommonHFSForkData curForkData = curFork.getA();
            final ReservedID curId = curFork.getB();

            if(curForkData == null) {
                continue;
            }

            final CommonHFSCatalogNodeID nodeId;
            if(isHfsPlus) {
                nodeId = CommonHFSCatalogNodeID.getHFSPlusReservedID(curId);
            }
            else {
                nodeId = CommonHFSCatalogNodeID.getHFSReservedID(curId);
            }

            final CommonHFSExtentDescriptor[] allExtents =
                    vol.getExtentsOverflowFile().getAllDataExtentDescriptors(
                    nodeId, curForkData);
            for(CommonHFSExtentDescriptor curExtent : allExtents) {
                final long startSector = allocationBlockStart +
                        curExtent.getStartBlock() * sectorsPerAllocationBlock;
                final long endSector = startSector +
                        curExtent.getBlockCount() * sectorsPerAllocationBlock;
                for(long i = startSector; i < endSector; ++i) {
                    inUseSectors.add(i);
                }
            }
        }

        /* In addition to the system files described by the forks in the volume
         * header, we have the additional metadata:
         * - The journal.
         * - Symlink targets (debatable).
         * - (Extended attributes and resource forks? Probably not, but they can
         *   hold a certain significance to the file system itself... for
         *   instance compressed files rely on extended attributes.)
         */

        /* Mark all the components of the journal 'in use'. */
        final Journal journal = vol.getJournal();
        if(isHfsPlus && journal != null) {
            /* Mark journal info block 'in use'. */
            final long journalInfoBlockSector = allocationBlockStart +
                    ((CommonHFSVolumeHeader.HFSPlusImplementation)
                    volumeHeader).getJournalInfoBlock() *
                    sectorsPerAllocationBlock;
            inUseSectors.add(journalInfoBlockSector);

            final JournalInfoBlock jib = vol.getJournal().getJournalInfoBlock();

            /* Mark journal 'in use'. */
            final long journalStartSector = jib.getRawOffset() / sectorSize;
            final long journalLastSector =
                    (jib.getRawOffset() + jib.getRawSize() - 1) / sectorSize;

            for(long i = journalStartSector; i <= journalLastSector; ++i) {
                inUseSectors.add(i);
            }
        }

        /* TODO: Mark all symlink targets 'in use'. */

        /* We have gathered all allocations. Time to dump the data. */
        final byte[] zeroBuffer = new byte[sectorSize];

        Util.zero(zeroBuffer);
        for(long i = 0; i < sectorCount; ++i) {
            final byte[] curBuffer;

            if(inUseSectors.contains(i)) {
                fsStream.seek(i * sectorSize);
                fsStream.readFully(buffer);
                curBuffer = buffer;
            }
            else {
                curBuffer = zeroBuffer;
            }

            try {
                System.out.write(curBuffer);
            } catch(IOException ioe) {
                throw new RuntimeIOException(ioe);
            }
        }

        fsStream.close();
    }
}
