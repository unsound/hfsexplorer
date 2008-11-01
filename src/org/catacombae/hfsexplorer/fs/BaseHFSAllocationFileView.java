/*-
 * Copyright (C) 2008 Erik Larsson
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

package org.catacombae.hfsexplorer.fs;

import java.util.LinkedList;
import org.catacombae.hfsexplorer.ObjectContainer;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSExtentDescriptor;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSVolumeHeader;
import org.catacombae.io.ReadableRandomAccessStream;

/**
 * Accessor class that works directly on the allocation file / volume bitmap and
 * retrieves information from it.
 *
 * @author Erik Larsson
 */
public abstract class BaseHFSAllocationFileView {
    protected final BaseHFSFileSystemView parentView;
    protected final ReadableRandomAccessStream allocationFileStream;

    protected BaseHFSAllocationFileView(BaseHFSFileSystemView parentView,
            ReadableRandomAccessStream allocationFileStream) {
        this.parentView = parentView;
        this.allocationFileStream = allocationFileStream;

        if(this.parentView == null)
            throw new IllegalArgumentException("parentView == null");
    }

    /**
     * Tells if the allocation block addressed by <code>blockNumber</code> is
     * used or not.
     *
     * @param blockNumber the block number to probe for allocation status.
     * @return whether the block <code>blockNumber</code> is used (true) or not
     * (false).
     * @throws java.lang.IllegalArgumentException if <code>blockNumber</code>
     * is out of range.
     */
    public synchronized boolean isAllocationBlockUsed(long blockNumber) throws IllegalArgumentException {
        CommonHFSVolumeHeader vh = parentView.getVolumeHeader();
        return isAllocationBlockUsed(blockNumber, vh);
    }

    private synchronized boolean isAllocationBlockUsed(long blockNumber, CommonHFSVolumeHeader vh) {
        long numAllocationBlocks = vh.getTotalBlocks();
        if(blockNumber >= numAllocationBlocks)
            throw new IllegalArgumentException("Block number (" + blockNumber +
                    ") is beyond the highest block of the volume (" +
                    (numAllocationBlocks-1) + ").");


        long byteIndex = blockNumber / 8;
        allocationFileStream.seek(byteIndex);
        int currentByte = allocationFileStream.read();
        if(currentByte >= 0)
            return (currentByte & (1 << 7 - (blockNumber % 8))) != 0;
        else
            throw new RuntimeException("No data left in stream! allocationFileStream.getFilePointer()=" +
                    allocationFileStream.getFilePointer() + " allocationFileStream.length()=" +
                    allocationFileStream.length());
    }

    /**
     * Loops through the entire allocation file and counts the number of blocks
     * that are marked as free.
     *
     * @return the number of free blocks in the allocation file.
     */
    /*
    public synchronized long countFreeBlocks() {
        return countBlocks(false);
    }
    */
    
    /**
     * Loops through the entire allocation file and counts the number of blocks
     * that are marked as allocated.
     *
     * @return the number of allocated blocks in the allocation file.
     */
    /*
    public synchronized long countAllocatedBlocks() {
        return countBlocks(true);
    }
    */

    /**
     * Loops through the entire allocation file to count the number of used and free blocks on the
     * volume. The output is placed in two <code>ObjectContainer</code>s
     *
     * @param oFreeBlocks (optional) variable where the algorithm stores the free block count.
     * @param oUsedBlocks (optional) variable where the algorithm stores the used block count.
     * @param stop (optional) variable which can be set to abort the block counting process. Must
     * initally be set to <code>false</code> or no work will be done whatsoever.
     * @return the total number of allocation blocks on the volume.
     */
    public long countBlocks(ObjectContainer<Long> oFreeBlocks, ObjectContainer<Long> oUsedBlocks, ObjectContainer<Boolean> stop) {
        CommonHFSVolumeHeader vh = parentView.getVolumeHeader();
        byte[] currentBlock = new byte[128*1024];
        final long totalBlocks = vh.getTotalBlocks();
        long blockCount = 0;
        //long allocatedBlockCount = 0;
        long usedBlockCount = 0;
        //int blockValue = (usedBlocks?0x1:0x0);
        if(stop == null)
            stop = new ObjectContainer<Boolean>(false);

        //System.err.println("countBlocks(): totalBlocks=" + totalBlocks);
        //System.err.println("countBlocks(): allocationFileStream.length()=" + allocationFileStream.length());
        allocationFileStream.seek(0);
        while(blockCount < totalBlocks && !stop.o) {
            //System.err.println("countBlocks():   blockCount=" + blockCount);
            //System.err.println("countBlocks():   allocationFileStream.getFilePointer()=" + allocationFileStream.getFilePointer());
            //System.err.println("countBlocks():   =" + );

            //System.out.println("countBlocks():   Reading a blob (" + currentBlock.length + " bytes)...");
            int bytesRead = allocationFileStream.read(currentBlock);
            //System.out.println("countBlocks():   ..." + bytesRead + " bytes read.");

            if(bytesRead >= 0) {
                for(int i = 0; i < bytesRead && blockCount < totalBlocks && !stop.o; ++i) {
                    byte currentByte = currentBlock[i];
                    for(int j = 0; j < 8 && blockCount < totalBlocks && !stop.o; ++j) {
                        ++blockCount;
                        if(((currentByte >> (7 - j)) & 0x1) == 0x1)
                            ++usedBlockCount;
                    }
                }
            }
            else
                throw new RuntimeException("Could not read all blocks from allocation file!");
        }
        
        if(blockCount != totalBlocks)
            throw new RuntimeException("[INTERNAL ERROR] blockCount(" + blockCount +
                    ") != totalBlocks(" + totalBlocks + ")");
        
        if(oFreeBlocks != null)
            oFreeBlocks.o = blockCount-usedBlockCount;
        if(oUsedBlocks != null)
            oUsedBlocks.o = usedBlockCount;
        
        return totalBlocks;
    }

    /**
     * Creates an implementation specific extent descriptor from the given data.
     *
     * @param startBlock the first block number of the extent.
     * @param blockCount the number of blocks that the extent ranges over.
     * @return an implementation specific representation of an extent descriptor
     * created from the <code>startBlock</code> and <code>allocatedBlockCount</code>
     * parameters.
     * @throws java.lang.IllegalArgumentException if any of the values of <code>
     * startBlock</code> or <code>allocatedBlockCount</code> are out of range for the
     * implementation (16-bit signed integer value for HFS, 32-bit signed
     * integer value for HFS+).
     */
    protected abstract CommonHFSExtentDescriptor createExtentDescriptor(long startBlock,
            long blockCount) throws IllegalArgumentException;

    /**
     * Calculates an array of extents that are currently free, and that matches
     * the supplied file size. Every effort will be made to find the most
     * perfectly fitting match, i.e. with as few extents as possible.<br>
     * If no match is found (the volume does not have enough free blocks to hold
     * the specified size), <code>null</code> is returned.
     *
     * @param fileSize the size of the data region to be allocated, in bytes.
     * @return an array of descriptors of the extents where the data region
     * can be stored on disk.
     */
    public synchronized CommonHFSExtentDescriptor[] findFreeSpace(final long fileSize) {
        if(fileSize < 0)
            throw new IllegalArgumentException("Negative file size: " + fileSize);
        
        CommonHFSVolumeHeader vh = parentView.getVolumeHeader();
        final long blockSize = vh.getAllocationBlockSize();
        final long totalBlocks = vh.getTotalBlocks();
        final long blocksToAllocate = fileSize / blockSize +
                (fileSize % blockSize != 0 ? 1 : 0);
        long blocksLeft = blocksToAllocate;

        /*
         * Search for the closest matching region, i.e. the smallest region that
         * is larger than or equal to fileSize, or if none can be found, the
         * largest region that is smaller than fileSize.
         */

        // Loop through the entire allocation file
        ByteRegion closestMatchAbove = new ByteRegion();
        ByteRegion closestMatchBelow = new ByteRegion();
        LinkedList<ByteRegion> allocations =
                new LinkedList<ByteRegion>();

        while(blocksLeft > 0) {
            if(closestMatchAbove == null)
                closestMatchAbove = new ByteRegion();
            if(closestMatchBelow == null)
                closestMatchBelow = new ByteRegion();
            
            closestMatchAbove.reset();
            closestMatchBelow.reset();

            long regionStart = -1;
            for(int i = 0; i < totalBlocks; ++i) {
                // Check that we do not collide with any already allocated regions
                int j = 0;
                for(ByteRegion br : allocations) {
                    if(i >= br.offset && i < (br.offset+br.length))
                        break;
                    ++j;
                }
                if(j != allocations.size())
                    continue;
                
                if(!isAllocationBlockUsed(i, vh)) {
                    if(regionStart == -1)
                        regionStart = i;
                }
                else {
                    if(regionStart != -1) {
                        long length = i-regionStart;
                        if(length > blocksLeft) {
                            if(closestMatchAbove.length < 0 || closestMatchAbove.length > length) {
                                closestMatchAbove.offset = regionStart;
                                closestMatchAbove.length = length;
                            }
                        }
                        else if(length < blocksLeft) {
                            if(closestMatchBelow.length < 0 || closestMatchBelow.length < length) {
                                closestMatchBelow.offset = regionStart;
                                closestMatchBelow.length = length;
                            }
                        }
                        else {
                            closestMatchAbove.offset = regionStart;
                            closestMatchAbove.length = length;
                            break; // A perfect match, so we don't need to search more.
                        }
                    }
                    regionStart = -1;
                }
            }

            if(closestMatchAbove.isValid()) {
                // We found a fitting region for the rest of the data
                //result.add(createExtentDescriptor(closestMatchAbove.offset,
                //        blocksLeft));
                closestMatchAbove.length = blocksLeft;
                allocations.add(closestMatchAbove);

                blocksLeft = 0;
                closestMatchAbove = null; // It is taken
            }
            else if(closestMatchBelow.isValid()) {
                //result.add(createExtentDescriptor(closestMatchBelow.offset,
                //        closestMatchBelow.length));
                allocations.add(closestMatchBelow);

                blocksLeft -= closestMatchBelow.length;
                closestMatchBelow = null; // It is taken
            }
            else {
                // We're out of free blocks...
                return null;
            }
        }
        
        if(blocksLeft != 0)
            throw new RuntimeException("[INTERNAL ERROR] blocksLeft(" +
                    blocksLeft + ") != 0 [closestMatchAbove.offset=" +
                    closestMatchAbove.offset + ",closestMatchAbove.length=" +
                    closestMatchAbove.length + "]");

        CommonHFSExtentDescriptor[] result = new CommonHFSExtentDescriptor[allocations.size()];
        int i = 0;
        for(ByteRegion br : allocations)
            result[i++] = createExtentDescriptor(br.offset, br.length);

        return result;
    }

    private class ByteRegion {
        public long offset;
        public long length;

        public void reset() {
            offset = -1;
            length = -1;
        }

        public boolean isValid() {
            return offset > 0 && length > 0;
        }
    }
}
