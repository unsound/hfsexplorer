/*-
 * Copyright (C) 2006-2009 Erik Larsson
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

package org.catacombae.hfs;

import java.io.IOException;
import java.io.OutputStream;
import org.catacombae.hfs.io.ForkFilter;
import org.catacombae.hfs.io.ReadableBlockCachingStream;
import org.catacombae.io.ReadableRandomAccessSubstream;
import org.catacombae.io.SynchronizedReadableRandomAccess;
import org.catacombae.io.SynchronizedReadableRandomAccessStream;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogFile;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogFileRecord;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogFolderRecord;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogLeafRecord;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogNodeID;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogNodeID.ReservedID;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogString;
import org.catacombae.hfs.types.hfscommon.CommonHFSExtentDescriptor;
import org.catacombae.hfs.types.hfscommon.CommonHFSExtentKey;
import org.catacombae.hfs.types.hfscommon.CommonHFSForkData;
import org.catacombae.hfs.types.hfscommon.CommonHFSVolumeHeader;
import org.catacombae.io.ReadableRandomAccessStream;

/**
 *
 * @author erik
 */
public abstract class HFSVolume {
    /*
     * The idea is to make few assumptions about static data in the file system.
     * No operations should be cached, since it would provide an inaccurate view
     * of a live file system.
     *
     * I don't know why I'm doing it this way... It would probably limit no one
     * if I just assumed exclusive access, but I can write another class for
     * that, providing better performance.
     *
     * Note: It has been shown that this approach has actually made it possible
     * to track changes in a live filesystem, for example when opening
     * /dev/disk0s2 in superuser mode in OS X.
     *
     * 2007-09-18: We now assume block size to be static. This can't possibly be
     *             a problem... ever. Variable staticBlockSize contains the
     *             block size.
     */

    /**
     * Debug variable which is mainly used to adjust the block offsets of file
     * data when recovering data from a corrupted volume image with 'gaps'.
     */
    public static volatile long fileReadOffset = 0;

    protected volatile SynchronizedReadableRandomAccess hfsFile;
    private volatile SynchronizedReadableRandomAccessStream hfsStream;
    //private final SynchronizedReadableRandomAccessStream backingFile;
    private final ReadableRandomAccessStream sourceStream;
    protected final int physicalBlockSize;

    // Variables for reading cached files.
    //private ReadableBlockCachingStream catalogCache = null;

    protected final CatalogFile catalogFile;
    protected final ExtentsOverflowFile extentsOverflowFile;

    protected HFSVolume(ReadableRandomAccessStream hfsFile,
            boolean cachingEnabled,
            BTreeOperations btreeOperations,
            CatalogOperations catalogOperations,
            ExtentsOverflowOperations extentsOverflowOperations) {

        this.sourceStream = hfsFile;
        this.hfsStream =
                new SynchronizedReadableRandomAccessStream(sourceStream);
        this.hfsFile = hfsStream;

        /* This seems to be a built in assumption of HFSish file systems, even
         * when using media with other physical block sizes (for instance CDs,
         * 2 KiB). */
        this.physicalBlockSize = 512;

        if(cachingEnabled)
            enableFileSystemCaching();

        this.catalogFile = new CatalogFile(this,
                btreeOperations, catalogOperations);

        this.extentsOverflowFile = new ExtentsOverflowFile(this,
                btreeOperations, extentsOverflowOperations);
    }

    //public static HFSVolume open(DataLocator loc, boolean writable) {
    //}

    /*
    public static HFSVolume openHFS(DataLocator loc, boolean writable,
            String encodingName) {
        return new HFSOriginalVolume(
                writable ? loc.createReadWriteFile() : loc.createReadOnlyFile(),
                0, false, encodingName);
    }

    public static HFSVolume openHFSPlus(DataLocator loc, boolean writable) {
        return new HFSPlusVolume(
                writable ? loc.createReadWriteFile() : loc.createReadOnlyFile(),
                0, false);
    }

    public static HFSVolume openHFSX(DataLocator loc, boolean writable) {
        return new HFSXVolume(
                writable ? loc.createReadWriteFile() : loc.createReadOnlyFile(),
                0, false);
    }
    
    public static HFSVolume openHFSWrappedHFSPlus(DataLocator loc,
            boolean writable) {
        // TODO
    }
    */

    /**
     * Performs some sanity checks, like reading from different parts of the
     * volume, in order to ensure that the underlying data storage contains the
     * entire file system, and that the file system is usable.<br/>
     * This method returns normally if all went well, and otherwise throws an
     * exception with a description of the test that failed.
     *
     * @throws Exception with a message describing which check failed.
     */
    public void runSanityChecks() throws Exception {
        byte[] block = new byte[512];

        // Get our private stream covering the entire file system.
        ReadableRandomAccessStream fsStream = createFSStream();

        // Checks if the length of the stream is block-aligned.
        {
            long res = fsStream.length() % 512;
            if(res != 0)
                throw new Exception("Length of file system is not " +
                        "block-aligned. Found " + res + " extra bytes.");
        }
        
        // Reads the first block of the file system.
        {
            fsStream.seek(0);

            int bytesRead = fsStream.read(block);
            if(bytesRead != 512) {
                throw new Exception("Failed to read first block. Managed to " +
                        "read " + bytesRead + " bytes from the beginning of " +
                        "the volume.");
            }
        }

        // Reads the last block of the file system.
        {
            fsStream.seek(fsStream.length()-512);

            int bytesRead = fsStream.read(block);
            if(bytesRead != 512) {
                throw new Exception("Failed to read last block. Managed to " +
                        "read " + bytesRead + " bytes from the end of " +
                        "the volume.");
            }
        }

        fsStream.close();
    }

    /**
     * Returns a stream covering the entire file system, from start to end.
     * This means creating a substream of hfsStream starting at
     * <code>fsOffset</code> and ending at the end of the file system.
     * This stream must be closed after usage.
     *
     * @return a stream covering the entire file system, from start to end.
     */
    public ReadableRandomAccessStream createFSStream() {
        ReadableRandomAccessSubstream subs =
                new ReadableRandomAccessSubstream(hfsFile);

        return subs;
        //long fsLength = getVolumeHeader().getFileSystemEnd();
        //return new ReadableConcatenatedStream(subs, fsOffset, fsLength);
    }

    public abstract CommonHFSVolumeHeader getVolumeHeader();

    //public abstract VolumeHeader getVolumeHeader();
    public CatalogFile getCatalogFile() {
        return catalogFile;
    }

    public ExtentsOverflowFile getExtentsOverflowFile() {
        return extentsOverflowFile;
    }

    public abstract AllocationFile getAllocationFile();

    public abstract boolean hasAttributesFile();
    public abstract boolean hasJournal();
    public abstract boolean hasHotFilesFile();

    public abstract AttributesFile getAttributesFile();
    public abstract Journal getJournal();
    public abstract HotFilesFile getHotFilesFile();

    public abstract CommonHFSCatalogNodeID getCommonHFSCatalogNodeID(
            ReservedID requestedNodeID);

    /*protected CommonHFSCatalogString createCommonHFSCatalogString(
            String name);*/

    /**
     * Returns an encoded representation of the empty string. This is a
     * statically allocated instance, and thus more efficient than calling
     * <code>encodeString("")</code>.
     */
    public abstract CommonHFSCatalogString getEmptyString();

    /**
     * Returns the default StringDecoder instance for this view. For HFS file
     * systems the decoder can be set in the HFS-specific subclass, but in HFS+
     * and HFSX file systems it will always return a UTF-16BE string decoder.
     *
     * @return the default StringDecoder instance for this view.
     */
    //public abstract StringDecoder getDefaultStringDecoder();

    /**
     * Decodes the supplied CommonHFSCatalogString according to the current
     * settings of the volume.
     *
     * @param str the CommonHFSCatalogString to decode.
     * @return a decoded representation of <code>str</code>.
     */
    public abstract String decodeString(CommonHFSCatalogString str);

   /**
     * Encodes the supplied CommonHFSCatalogString according to the current
     * settings of the view.
     *
     * @param str the String to encode.
     * @return an encoded representation of <code>str</code>.
     */
    public abstract CommonHFSCatalogString encodeString(String str);


    /**
     * Closes the volume and flushes any data not yet committed to disk (if
     * opened in writable mode).
     */
    public abstract void close();

    /*
    public boolean isFileSystemCachingEnabled() {
        ReadableRandomAccessStream currentSourceStream = hfsStream.getSourceStream();
        return currentSourceStream != this.sourceStream &&
                currentSourceStream instanceof ReadableBlockCachingStream;
    }
    */

    public void enableFileSystemCaching() {
        enableFileSystemCaching(256 * 1024, 64); // 64 pages of 256 KiB each is the default setting
    }

    public void enableFileSystemCaching(int blockSize, int blocksInCache) {
        hfsStream = new SynchronizedReadableRandomAccessStream(
                new ReadableBlockCachingStream(sourceStream, blockSize, blocksInCache));
        hfsFile = hfsStream;
    }

    public void disableFileSystemCaching() {
        hfsStream = new SynchronizedReadableRandomAccessStream(sourceStream);
        hfsFile = hfsStream;
    }

    /*
     /**
     * Returns the underlying stream, serving the view with HFS+ file system
     * data.
     * @return the underlying stream.
     */
    /*
    public ReadableRandomAccessStream getStream() {
        return hfsFile;
    }
    */


    public long extractDataForkToStream(CommonHFSCatalogLeafRecord fileRecord,
            OutputStream os, ProgressMonitor pm) throws IOException {
        // = fileRecord.getData();
        if(fileRecord instanceof CommonHFSCatalogFileRecord) {
            CommonHFSCatalogFile catFile =
                    ((CommonHFSCatalogFileRecord) fileRecord).getData();
            CommonHFSForkData dataFork = catFile.getDataFork();
            return extractForkToStream(dataFork,
                    extentsOverflowFile.getAllDataExtentDescriptors(fileRecord),
                    os, pm);
        }
        else
            throw new IllegalArgumentException("fileRecord.getData() it not " +
                    "of type RECORD_TYPE_FILE");
    }

    public long extractResourceForkToStream(
            CommonHFSCatalogLeafRecord fileRecord, OutputStream os,
            ProgressMonitor pm) throws IOException {
        //CommonHFSCatalogLeafRecordData recData = fileRecord.getData();
        if(fileRecord instanceof CommonHFSCatalogFileRecord) {
            CommonHFSCatalogFile catFile =
                    ((CommonHFSCatalogFileRecord) fileRecord).getData();
            CommonHFSForkData resFork = catFile.getResourceFork();
            return extractForkToStream(resFork,
                    extentsOverflowFile.getAllResourceExtentDescriptors(fileRecord),
                    os, pm);
        }
        else
            throw new IllegalArgumentException("fileRecord.getData() it not " +
                    "of type RECORD_TYPE_FILE");
    }

    public long extractForkToStream(CommonHFSForkData forkData,
            CommonHFSExtentDescriptor[] extentDescriptors, OutputStream os,
            ProgressMonitor pm) throws IOException {
        CommonHFSVolumeHeader header = getVolumeHeader();
        ForkFilter forkFilter = new ForkFilter(forkData, extentDescriptors,
                new ReadableRandomAccessSubstream(hfsFile), 0,
                header.getAllocationBlockSize(),
                header.getAllocationBlockStart() * physicalBlockSize);
        long bytesToRead = forkData.getLogicalSize();
        byte[] buffer = new byte[4096];
        while(bytesToRead > 0) {
            if(pm.cancelSignaled())
                break;

//          System.out.print("forkFilter.read([].length=" + buffer.length +
//                    ", 0, " +
//                    (bytesToRead < buffer.length ? (int)bytesToRead : buffer.length) +
//                    "...");
            int bytesRead = forkFilter.read(buffer, 0,
                    (bytesToRead < buffer.length ? (int)bytesToRead : buffer.length));
//          System.out.println("done. bytesRead = " + bytesRead);
            if(bytesRead < 0)
                break;
            else {
                pm.addDataProgress(bytesRead);
                os.write(buffer, 0, bytesRead);
                bytesToRead -= bytesRead;
            }
        }
        return forkData.getLogicalSize() - bytesToRead;
    }

    /**
     * Returns a stream from which the data fork of the specified file record
     * can be accessed.
     *
     * @return a stream from which the data fork of the specified file record
     * can be accessed.
     * @throws IllegalArgumentException if fileRecord is not a file record.
     */
    public ReadableRandomAccessStream getReadableDataForkStream(CommonHFSCatalogLeafRecord fileRecord) {
	if(fileRecord instanceof CommonHFSCatalogFileRecord) {
	    CommonHFSCatalogFile catFile = ((CommonHFSCatalogFileRecord)fileRecord).getData();
	    CommonHFSForkData fork = catFile.getDataFork();
	    return getReadableForkStream(fork,
                    extentsOverflowFile.getAllDataExtentDescriptors(fileRecord));
	}
	else
	    throw new IllegalArgumentException("fileRecord.getData() it not of type RECORD_TYPE_FILE");
    }

    /**
     * Returns a stream from which the resource fork of the specified file record
     * can be accessed.
     *
     * @return a stream from which the resource fork of the specified file record
     * can be accessed.
     * @throws IllegalArgumentException if fileRecord is not a file record.
     */
    public ReadableRandomAccessStream getReadableResourceForkStream(CommonHFSCatalogLeafRecord fileRecord) {
	if(fileRecord instanceof CommonHFSCatalogFileRecord) {
	    CommonHFSCatalogFile catFile = ((CommonHFSCatalogFileRecord)fileRecord).getData();
	    CommonHFSForkData fork = catFile.getResourceFork();
	    return getReadableForkStream(fork,
                    extentsOverflowFile.getAllResourceExtentDescriptors(fileRecord));
	}
	else
	    throw new IllegalArgumentException("fileRecord.getData() it not of type RECORD_TYPE_FILE");
    }

    private ReadableRandomAccessStream getReadableForkStream(CommonHFSForkData forkData,
            CommonHFSExtentDescriptor[] extentDescriptors) {
        CommonHFSVolumeHeader header = getVolumeHeader();
        return new ForkFilter(forkData, extentDescriptors, new ReadableRandomAccessSubstream(hfsFile),
                fileReadOffset,
                header.getAllocationBlockSize(),
                header.getAllocationBlockStart() * physicalBlockSize);
    }

    private static String getDebugString(CommonHFSExtentKey key) {
        return key.getForkType() + ":" + key.getFileID().toLong() + ":" + key.getStartBlock();
    }


    // Utility methods

    protected long calculateDataForkSizeRecursive(CommonHFSCatalogLeafRecord[] recs) {
	return calculateForkSizeRecursive(recs, false);
    }
    protected long calculateDataForkSizeRecursive(CommonHFSCatalogLeafRecord rec) {
	return calculateForkSizeRecursive(rec, false);
    }
    protected long calculateResourceForkSizeRecursive(CommonHFSCatalogLeafRecord[] recs) {
	return calculateForkSizeRecursive(recs, true);
    }
    protected long calculateResourceForkSizeRecursive(CommonHFSCatalogLeafRecord rec) {
	return calculateForkSizeRecursive(rec, true);
    }

    /**
     * Calculates the complete size of the trees rooted in <code>recs</code>.
     */
    protected long calculateForkSizeRecursive(CommonHFSCatalogLeafRecord[] recs, boolean resourceFork) {
	long totalSize = 0;
	for(CommonHFSCatalogLeafRecord rec : recs)
	    totalSize += calculateForkSizeRecursive(rec, resourceFork);
	return totalSize;
    }

    /**
     * Calculates the complete size of the tree represented by <code>rec</code>.
     */
    protected long calculateForkSizeRecursive(CommonHFSCatalogLeafRecord rec, boolean resourceFork) {
	if(rec instanceof CommonHFSCatalogFileRecord) {
	    if(!resourceFork)
		return ((CommonHFSCatalogFileRecord)rec).getData().getDataFork().getLogicalSize();
	    else
		return ((CommonHFSCatalogFileRecord)rec).getData().getResourceFork().getLogicalSize();
	}
	else if(rec instanceof CommonHFSCatalogFolderRecord) {
	    CommonHFSCatalogNodeID requestedID =
                    ((CommonHFSCatalogFolderRecord)rec).getData().getFolderID();
	    CommonHFSCatalogLeafRecord[] contents = catalogFile.listRecords(requestedID);
	    long totalSize = 0;
	    for(CommonHFSCatalogLeafRecord outRec : contents) {
		totalSize += calculateForkSizeRecursive(outRec, resourceFork);
	    }
	    return totalSize;
	}
	else
	    return 0;
    }
}
