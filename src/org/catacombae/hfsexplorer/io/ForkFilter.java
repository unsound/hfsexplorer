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

package org.catacombae.hfsexplorer.io;

import org.catacombae.hfsexplorer.Util;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusExtentDescriptor;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusForkData;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSExtentDescriptor;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSForkData;

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
 */
public class ForkFilter implements ReadableRandomAccessStream {

    private final long forkLength;
    private final CommonHFSExtentDescriptor[] extentDescriptors;
    private final ReadableRandomAccessStream sourceFile;
    private final long fsOffset;
    private final long allocationBlockSize;
    private final long firstBlockByteOffset;
    private long logicalPosition; // The current position in the fork
    private long lastLogicalPos; // The position in the fork where we stopped reading last time
    private long lastPhysicalPos; // The position in the fork where we stopped reading last time
    
    /**
     * Creates a new ForkFilter. This class assumes that it has exclusive access to
     * <code>sourceFile</code>.
     * 
     * @param forkData
     * @param extentDescriptors
     * @param sourceFile
     * @param fsOffset
     * @param allocationBlockSize
     * @param firstBlockByteOffset
     */
    public ForkFilter(CommonHFSForkData forkData, CommonHFSExtentDescriptor[] extentDescriptors,
            ReadableRandomAccessStream sourceFile, long fsOffset, long allocationBlockSize,
            long firstBlockByteOffset) {
        this(forkData.getLogicalSize(), extentDescriptors, sourceFile, fsOffset,
                allocationBlockSize, firstBlockByteOffset);
    }
    
    /**
     * Creates a new ForkFilter. This class assumes that it has exclusive access to
     * <code>sourceFile</code>.

     * @param forkLength
     * @param extentDescriptors
     * @param sourceFile
     * @param fsOffset
     * @param allocationBlockSize
     * @param firstBlockByteOffset
     */
    public ForkFilter(long forkLength, CommonHFSExtentDescriptor[] extentDescriptors,
            ReadableRandomAccessStream sourceFile, long fsOffset, long allocationBlockSize,
            long firstBlockByteOffset) {
        //System.err.println("ForkFilter.<init>(" + forkLength + ", " + extentDescriptors + ", " +
        //        sourceFile + ", " + fsOffset + ", " + allocationBlockSize + ", " + firstBlockByteOffset + ");");
        //System.err.println("  fork has " + extentDescriptors.length + " extents.");
        this.forkLength = forkLength;
        this.extentDescriptors = Util.arrayCopy(extentDescriptors,
                new CommonHFSExtentDescriptor[extentDescriptors.length]);

        this.sourceFile = sourceFile;
        this.fsOffset = fsOffset;
        this.allocationBlockSize = allocationBlockSize;
        this.firstBlockByteOffset = firstBlockByteOffset;
        this.logicalPosition = 0;
        this.lastLogicalPos = -1; // Set differently from logicalPosition to trigger a seek at first read
        this.lastPhysicalPos = 0; // Set differently from logicalPosition to trigger a seek at first read
    }

    @Deprecated
    public ForkFilter(HFSPlusForkData forkData, HFSPlusExtentDescriptor[] extentDescriptors,
            ReadableRandomAccessStream sourceFile, long fsOffset, long allocationBlockSize,
            long firstBlockByteOffset) {
        //System.err.println("ForkFilter.<init>(" + forkData + ", " + extentDescriptors + ", " +
        //        sourceFile + ", " + fsOffset + ", " + blockSize + ");");
        //System.err.println("  fork has " + extentDescriptors.length + " extents.");
        this.forkLength = forkData.getLogicalSize();
        this.extentDescriptors = new CommonHFSExtentDescriptor[extentDescriptors.length];
        for(int i = 0; i < this.extentDescriptors.length; ++i)
            this.extentDescriptors[i] = CommonHFSExtentDescriptor.create(extentDescriptors[i]);

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
    @Override
    public void seek(long pos) {
        //System.err.println("ForkFilter.seek(" + pos + ");");
        logicalPosition = pos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
    public int read(byte[] data) {
        return read(data, 0, data.length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(byte[] data, int pos, int len) {
        //System.err.println("ForkFilter.read(" + data + ", " + pos + ", " + len);
        long offset = fsOffset;
        long bytesToSkip = logicalPosition;
        int extIndex;
        long currentExtentLength;

        // Skip all extents whose range is located before the requested position (logicalPosition)
        //System.out.println("ForkFilter.read: skipping extents (bytesToSkip=" +
        //        bytesToSkip + ")...");
        for(extIndex = 0; extIndex < extentDescriptors.length; ++extIndex) {
            CommonHFSExtentDescriptor cur = extentDescriptors[extIndex];
            currentExtentLength = cur.getBlockCount() * allocationBlockSize;
            if(bytesToSkip >= currentExtentLength) {
                if(extIndex < extentDescriptors.length - 1)
                    bytesToSkip -= currentExtentLength;
                else {
                    //System.err.println("Extent descriptors:");
                    //for(int i = 0; i < extentDescriptors.length; ++i) {
                    //    extentDescriptors[i].print(System.err, "");
                    //}

                    //throw new RuntimeException("Extent out of bounds!");
                    return -1; // This is the proper way
                }
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
        for(; extIndex < extentDescriptors.length; ++extIndex) {
            //System.out.println("ForkFilter.read: reading extent " + extIndex + ".");
            CommonHFSExtentDescriptor cur = extentDescriptors[extIndex];

            long bytesInExtent = cur.getBlockCount() * allocationBlockSize - bytesToSkip;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void readFully(byte[] data) {
        readFully(data, 0, data.length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
    public long length() {
        return forkLength;
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
    public void close() {
        //sourceFile.close(); // <- bad idea.
    }
}
