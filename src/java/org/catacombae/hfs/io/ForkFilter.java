/*-
 * Copyright (C) 2006-2008 Erik Larsson
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

package org.catacombae.hfs.io;

import java.util.ArrayList;
import java.util.Arrays;
import org.catacombae.hfs.ExtentsOverflowFile;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.hfs.types.hfscommon.CommonHFSExtentDescriptor;
import org.catacombae.hfs.types.hfscommon.CommonHFSExtentLeafRecord;
import org.catacombae.hfs.types.hfscommon.CommonHFSForkData;
import org.catacombae.io.RuntimeIOException;

/**
 * Facilitates reading the data of a file system fork by abstracting the extents
 * and presents it to the programmer as if it was a continious, seekable stream.
 * <br>
 * Note: If you modify the state of the underlying stream (i.e. adding, removing,
 * changing extents) while using this filter, the behavior of the filter is
 * undefined. The read methods will probably not return the correct data.
 * <pre>
 * Model:
 *
 * - seeking does not do anything except setting a pointer value
 * - when read is called:
 *   - if logicalPosition is different from our last position
 *     - seek to the right position
 *   - else if file pointer is different from our last file pointer
 *     - seek to last fp
 * </pre>
 *
 * @author <a href="https://catacombae.org" target="_top">Erik Larsson</a>
 */
public class ForkFilter implements ReadableRandomAccessStream {

    private final long forkLength;
    private final ArrayList<CommonHFSExtentDescriptor> extentDescriptors;
    private final OverflowExtentsStore overflowExtentsStore;
    private final ReadableRandomAccessStream sourceFile;
    private final long fsOffset;
    private final long allocationBlockSize;
    private final long firstBlockByteOffset;
    private long logicalPosition; // The current position in the fork
    private long lastLogicalPos; // The position in the fork where we stopped reading last time
    private long lastPhysicalPos; // The position in the fork where we stopped reading last time
    private boolean all_extents_mapped = false;

    public enum ForkType { DATA, RESOURCE };

    /**
     * Creates a new ForkFilter. This class assumes that it has exclusive access
     * to <code>sourceFile</code>.
     *
     * @param forkType
     *      <b>(in)</b> The type of the fork (for constructing extents overflow
     *      file keys).
     * @param cnid
     *      <b>(in)</b> The catalog node ID of the fork's parent (for
     *      constructing extents overflow file keys).
     * @param forkData
     *      <b>(in)</b> The fork data of this fork, containing the fork length
     *      and the first basic extents.
     * @param extentsOverflowFile
     *      <b>(in)</b> The file systems's {@link ExtentsOverflowFile} (for
     *      looking up extents beyond the first basic ones).
     * @param sourceFile
     *      <b>(in)</b> A {@link ReadableRandomAccessStream} for accessing the
     *      whole volume.
     * @param fsOffset
     *      <b>(in)</b> The offset within <code>sourceFile</code> where the file
     *      system starts.
     * @param allocationBlockSize
     *      <b>(in)</b> The allocation block size of the file system.
     * @param firstBlockByteOffset
     *      <b>(in)</b> The byte offset from the start of the volume to the
     *      first allocation block of the file system.
     */
    public ForkFilter(ForkType forkType, long cnid, CommonHFSForkData forkData,
            ExtentsOverflowFile extentsOverflowFile,
            ReadableRandomAccessStream sourceFile, long fsOffset, long allocationBlockSize,
            long firstBlockByteOffset) {
        this(forkType, cnid, forkData.getLogicalSize(),
                forkData.getBasicExtents(), extentsOverflowFile, sourceFile,
                fsOffset, allocationBlockSize, firstBlockByteOffset);
    }

    /**
     * Creates a new ForkFilter. This class assumes that it has exclusive access
     * to <code>sourceFile</code>.

     * @param forkType
     *      <b>(in)</b> The type of the fork (for constructing extents overflow
     *      file keys).
     * @param cnid
     *      <b>(in)</b> The catalog node ID of the fork's parent (for
     *      constructing extents overflow file keys).
     * @param forkLength
     *      <b>(in)</b> The length of the fork.
     * @param basicExtents
     *      <b>(in)</b> The basic extents of the fork, stored in the file entry
     *      of the fork's parent.
     * @param extentsOverflowFile
     *      <b>(in)</b> The file systems's {@link ExtentsOverflowFile} (for
     *      looking up extents beyond the first basic ones).
     * @param sourceFile
     *      <b>(in)</b> A {@link ReadableRandomAccessStream} for accessing the
     *      whole volume.
     * @param fsOffset
     *      <b>(in)</b> The offset within <code>sourceFile</code> where the file
     *      system starts.
     * @param allocationBlockSize
     *      <b>(in)</b> The allocation block size of the file system.
     * @param firstBlockByteOffset
     *      <b>(in)</b> The byte offset from the start of the volume to the
     *      first allocation block of the file system.
     */
    public ForkFilter(ForkType forkType, long cnid, long forkLength,
            CommonHFSExtentDescriptor[] basicExtents,
            ExtentsOverflowFile extentsOverflowFile,
            ReadableRandomAccessStream sourceFile, long fsOffset, long allocationBlockSize,
            long firstBlockByteOffset) {
        this(forkLength, basicExtents,
                new ExtentsOverflowFileStore(extentsOverflowFile, forkType,
                cnid), sourceFile, fsOffset, allocationBlockSize,
                firstBlockByteOffset);
    }

    /**
     * Creates a new ForkFilter. This class assumes that it has exclusive access
     * to <code>sourceFile</code>.

     * @param forkLength
     *      <b>(in)</b> The length of the fork.
     * @param allExtents
     *      <b>(in)</b> All the extents of the fork. All extents must have been
     *      resolved prior to calling this constructor since no dynamic lookup
     *      will be possible.
     * @param sourceFile
     *      <b>(in)</b> A {@link ReadableRandomAccessStream} for accessing the
     *      whole volume.
     * @param fsOffset
     *      <b>(in)</b> The offset within <code>sourceFile</code> where the file
     *      system starts.
     * @param allocationBlockSize
     *      <b>(in)</b> The allocation block size of the file system.
     * @param firstBlockByteOffset
     *      <b>(in)</b> The byte offset from the start of the volume to the
     *      first allocation block of the file system.
     */
    public ForkFilter(long forkLength, CommonHFSExtentDescriptor[] allExtents,
            ReadableRandomAccessStream sourceFile, long fsOffset,
            long allocationBlockSize, long firstBlockByteOffset)
    {
        this(forkLength, allExtents, null, sourceFile, fsOffset,
                allocationBlockSize, firstBlockByteOffset);
    }

    private ForkFilter(long forkLength,
            CommonHFSExtentDescriptor[] initialExtents,
            OverflowExtentsStore overflowExtentsStore,
            ReadableRandomAccessStream sourceFile, long fsOffset, long allocationBlockSize,
            long firstBlockByteOffset)
    {
        //System.err.println("ForkFilter.<init>(" + forkLength + ", " +
        //        extentDescriptors + ", " + sourceFile + ", " + fsOffset +
        //        ", " + allocationBlockSize + ", " + firstBlockByteOffset +
        //        ");");
        //System.err.println("  fork has " + extentDescriptors.length +
        //        " extents.");

        this.forkLength = forkLength;
        this.extentDescriptors =
                new ArrayList<CommonHFSExtentDescriptor>(Arrays.asList(
                initialExtents));
        this.overflowExtentsStore = overflowExtentsStore;
        this.sourceFile = sourceFile;
        this.fsOffset = fsOffset;
        this.allocationBlockSize = allocationBlockSize;
        this.firstBlockByteOffset = firstBlockByteOffset;
        this.logicalPosition = 0;
        this.lastLogicalPos = -1; // Set differently from logicalPosition to trigger a seek at first read
        this.lastPhysicalPos = 0; // Set differently from logicalPosition to trigger a seek at first read
    }

    /**
     * {@inheritDoc}
     */
    /* @Override */
    public void seek(long pos) {
        //System.err.println("ForkFilter.seek(" + pos + ");");
        logicalPosition = pos;
    }

    /**
     * {@inheritDoc}
     */
    /* @Override */
    public int read() {
        byte[] oneByte = new byte[1];
        if(read(oneByte) == 1)
            return oneByte[0] & 0xFF;
        else
            return -1;
    }

    /**
     * {@inheritDoc}
     */
    /* @Override */
    public int read(byte[] data) {
        return read(data, 0, data.length);
    }

    private CommonHFSExtentDescriptor getExtent(int extIndex, long startBlock) {
        long curStartBlock = startBlock;

        while(extIndex >= extentDescriptors.size()) {
            /* Need to read the next overflow extent into
             * extentDescriptors. */

            if(overflowExtentsStore == null) {
                throw new RuntimeIOException("No overflow extents store to " +
                        "query for overflow extents.");
            }

            CommonHFSExtentLeafRecord extentRecord;

            if(all_extents_mapped) {
                extentRecord = null;
            }
            else {
                extentRecord =
                        overflowExtentsStore.getExtentRecord(curStartBlock);
            }

            final CommonHFSExtentDescriptor[] descriptors =
                    extentRecord.getRecordData();

            for(int i = 0; i < descriptors.length; ++i) {
                final CommonHFSExtentDescriptor curDescriptor = descriptors[i];
                final long blockCount = curDescriptor.getBlockCount();

                if(blockCount == 0) {
                    /* End-of-fork at first occurrence of block count 0. */
                    all_extents_mapped = true;
                    break;
                }

                extentDescriptors.add(curDescriptor);
                curStartBlock += blockCount;
            }
        }

        return extentDescriptors.get(extIndex);
    }

    /**
     * {@inheritDoc}
     */
    /* @Override */
    public int read(byte[] data, int pos, int len) {
        //System.err.println("ForkFilter.read(" + data + ", " + pos + ", " + len);
        long offset = Long.MAX_VALUE; // MAX_VALUE as a sentinel for seek
        long bytesToSkip = logicalPosition;
        long curLogicalBlock = 0;
        int extIndex;
        long currentExtentLength;

        if(extentDescriptors.size() < 1 || logicalPosition >= forkLength) {
            return -1; // EOF
        }

        // Skip all extents whose range is located before the requested position (logicalPosition)
        //System.out.println("ForkFilter.read: skipping extents (bytesToSkip=" +
        //        bytesToSkip + ")...");
        for(extIndex = 0; ; ++extIndex) {
            CommonHFSExtentDescriptor cur =
                    getExtent(extIndex, curLogicalBlock);
            if(cur == null) {
                /* No such extent available. */
                return -1;
            }

            long currentBlockCount = cur.getBlockCount();
            currentExtentLength = currentBlockCount * allocationBlockSize;

            if(bytesToSkip >= currentExtentLength) {
                bytesToSkip -= currentExtentLength;
                curLogicalBlock += currentBlockCount;
            }
            else {
                offset = fsOffset + firstBlockByteOffset +
                        (cur.getStartBlock() * allocationBlockSize) + bytesToSkip;
                break;
            }
        }
        //System.out.println("done. skipped " + extIndex + " extents.");

        if(logicalPosition != lastLogicalPos) {
            //System.out.print("ForkFilter.read: (1)seeking to " + offset + "...");
            sourceFile.seek(offset); // Seek to the correct position
            //System.out.println("done.");
        }
        else if(sourceFile.getFilePointer() != lastPhysicalPos) {
            //System.out.print("ForkFilter.read: (2)seeking to " + offset + "...");
            sourceFile.seek(lastPhysicalPos);
            //System.out.println("done.");
        }

        long bytesLeftInStream = forkLength - logicalPosition;
        //System.err.println("bytesLeftInStream: " + bytesLeftInStream + " len: " + len);
        int totalBytesToRead = bytesLeftInStream < len ? (int)bytesLeftInStream : len;
        int bytesLeftToRead = totalBytesToRead;
        //System.err.println("bytesLeftToRead: " + bytesLeftToRead);
        // Start reading. Extent by extent if needed.
        for(; ; ++extIndex) {
            //System.out.println("ForkFilter.read: reading extent " + extIndex + ".");

            CommonHFSExtentDescriptor cur;
            try {
                cur = getExtent(extIndex, curLogicalBlock);
            } catch(RuntimeException e) {
                if(bytesLeftToRead == totalBytesToRead) {
                    throw e;
                }
                else {
                    break;
                }
            }

            sourceFile.seek(fsOffset + firstBlockByteOffset +
                    (cur.getStartBlock() * allocationBlockSize) + bytesToSkip);

            long blockCount = cur.getBlockCount();
            long bytesInExtent = blockCount * allocationBlockSize - bytesToSkip;
            int bytesToReadFromExtent = (bytesInExtent < bytesLeftToRead) ? (int) bytesInExtent : bytesLeftToRead;

            int bytesReadFromExtent = 0;
            while(bytesReadFromExtent < bytesToReadFromExtent) {
                int bytesToRead = bytesToReadFromExtent - bytesReadFromExtent;
                int positionInArray = pos + (totalBytesToRead - bytesLeftToRead) + bytesReadFromExtent;

                int bytesRead = sourceFile.read(data, positionInArray, bytesToRead);
                if(bytesRead > 0)
                    bytesReadFromExtent += bytesRead;
                else {
                    // Update tracker variables before returning
                    lastPhysicalPos = sourceFile.getFilePointer();
                    int totalBytesRead = positionInArray - pos;
                    logicalPosition += totalBytesRead;
                    return totalBytesRead;
                }
            }

            bytesLeftToRead -= bytesReadFromExtent;
            bytesToSkip = 0;
            curLogicalBlock += blockCount;

            if(bytesLeftToRead == 0)
                break;
        }

        // Update tracker variables before returning
        lastPhysicalPos = sourceFile.getFilePointer();
        logicalPosition += totalBytesToRead - bytesLeftToRead;

        if(bytesLeftToRead < totalBytesToRead) {
            int bytesRead = totalBytesToRead - bytesLeftToRead;
            //System.err.println("final bytesRead: " + bytesRead);
            return bytesRead;
        }
        else
            return -1;
    }

    /* @Override */
    public byte readFully() throws RuntimeIOException {
        byte[] data = new byte[1];
        readFully(data);
        return data[0];
    }

    /**
     * {@inheritDoc}
     */
    /* @Override */
    public void readFully(byte[] data) {
        readFully(data, 0, data.length);
    }

    /**
     * {@inheritDoc}
     */
    /* @Override */
    public void readFully(byte[] data, int offset, int length) {
        int bytesRead = 0;
        while(bytesRead < length) {
            int curBytesRead = read(data, bytesRead, length - bytesRead);
            if(curBytesRead > 0)
                bytesRead += curBytesRead;
            else
                throw new RuntimeException("Couldn't read the entire length.");
        }
    }

    /**
     * {@inheritDoc}
     */
    /* @Override */
    public long length() {
        return forkLength;
    }

    /**
     * {@inheritDoc}
     */
    /* @Override */
    public long getFilePointer() {
        return logicalPosition;
    }

    /**
     * Returns the underlying stream serving ForkFilter with file system data.
     * @return the underlying stream serving ForkFilter with file system data.
     */
    public ReadableRandomAccessStream getUnderlyingStream() {
        return sourceFile;
    }

    /**
     * Does nothing.
     */
    /* @Override */
    public void close() {
        sourceFile.close();
    }

    private static abstract class OverflowExtentsStore {
        public abstract CommonHFSExtentLeafRecord getExtentRecord(
                long startBlock);
    }

    private static class ExtentsOverflowFileStore extends OverflowExtentsStore {
        private final ExtentsOverflowFile extentsOverflowFile;
        private final ForkType forkType;
        private final long cnid;

        public ExtentsOverflowFileStore(ExtentsOverflowFile extentsOverflowFile,
                ForkType forkType, long cnid)
        {
            if(forkType == null) {
                throw new IllegalArgumentException("A null value is not " +
                        "allowed in 'forkType'.");
            }

            if(cnid > 0xFFFFFFFFL) {
                throw new IllegalArgumentException("Value of 'cnid' is too " +
                        "large: " + cnid);
            }

            this.extentsOverflowFile = extentsOverflowFile;
            this.forkType = forkType;
            this.cnid = cnid;
        }

        @Override
        public CommonHFSExtentLeafRecord getExtentRecord(long startBlock) {
            final CommonHFSExtentLeafRecord rec =
                    extentsOverflowFile.getOverflowExtent(
                    forkType == ForkType.RESOURCE ? true : false,
                    (int) cnid, startBlock);

            if(rec == null) {
                throw new RuntimeIOException("Unable to find extent record " +
                        "for " + (forkType == ForkType.RESOURCE ? "resource" :
                            "data") + " fork of CNID " + cnid + ", start " +
                            "block " + startBlock + ".");
            }

            return rec;
        }
    }
}
