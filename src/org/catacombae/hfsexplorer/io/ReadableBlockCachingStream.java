/*-
 * Copyright (C) 2007 Erik Larsson
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

import java.util.HashMap;
import org.catacombae.io.ReadableFilterStream;
import org.catacombae.io.ReadableRandomAccessStream;

public class ReadableBlockCachingStream extends ReadableFilterStream {
    /*
     * Keep track of the access count for every (?) block. The <itemCount> blocks with the highest
     * access count are kept in the cache.
     * This means that when we determine if a block should go in the cache or not, we need to find
     * the cache block with the lowest access count.
     *
     * Blocks in the cache are kept on basis of their access count, except for one block which is
     * the history block. In the future the implementation should allow for tuning how many of the
     * cache blocks are history blocks.
     */
    
    /** The maximum age of an entry in the cache. When an entry's last accessed time has passed
	this age, it is thrown out, regardless of its access count. Unit: milliseconds. */
    private static final long TIME_TO_KEEP_IN_CACHE = 5000; // Tune this later
    
    /** Block size. */
    private final int blockSize;
    
    /** The logical file pointer. */
    private long virtualFP;
    
    /** Length of the file. If the length of the underlying file should change, this one doesn't. */
    private final long virtualLength;
    
    /** Hashtable mapping block numbers to BlockStore objects. Every block that has ever been
	accessed will get an entry here, which leads to uncontrollable memory allocation up to
	a maximum of length()/blockSize entries. TODO: Think out a smarter solution with a space
	limited data structure. */
    private final HashMap<Long, BlockStore> blockMap = new HashMap<Long, BlockStore>();

    /** Holds the cache items. One entry in the array is reserved for the previously read block,
	regardless of its access count, so that subsequent sequential reads won't suffer if
	accessCount isn't high enough. */
    private final BlockStore[] cache;
    
    /** Set when the close method is called. Prohibits further access. */
    private boolean closed = false;
    
    private static class BlockStore/* implements Comparable<LongContainer>*/ {
	public long accessCount = 0;
	public long lastAccessTime = Long.MAX_VALUE;
	public final long blockNumber;
	/** Might be null at any time when the data is thrown out of the cache. */
	public byte[] data = null;
	
	public BlockStore(long blockNumber) {
	    this.blockNumber = blockNumber;
	}
    }
			    
    public ReadableBlockCachingStream(ReadableRandomAccessStream backing, int blockSize, int maxItemCount) {
	super(backing);
        //System.err.println("ReadableBlockCachingStream(" + backing + ", " + blockSize + ", " + maxItemCount + ");");
	if(backing == null)
	    throw new IllegalArgumentException("backing can not be null");
	if(blockSize <= 0)
	    throw new IllegalArgumentException("blockSize must be positive and non-zero");
	if(maxItemCount < 1)
	    throw new IllegalArgumentException("maxItemCount must be at least 1");
	
	this.blockSize = blockSize;
    long length;
	try {
        length = backing.length();
    } catch(Exception e) { length = -1; }
    if(length > 0)
        this.virtualLength = length; // Immutable
    else
        this.virtualLength = -1;

	int actualItemCount = maxItemCount;
	//System.err.println("ReadableBlockCachingStream created. virtualLength: " + virtualLength + " maxItemCount*blockSize: " + (maxItemCount*blockSize));
	if(virtualLength > 0 && actualItemCount*blockSize > virtualLength) {
	    actualItemCount = (int)( virtualLength/blockSize + ((virtualLength%blockSize != 0) ? 1 : 0) );
	    //System.err.println("Adjusted actualItemCount to " + actualItemCount);
	}
	this.cache = new BlockStore[actualItemCount];
    }
    
    @Override
    public void seek(long pos) {
	if(closed) throw new RuntimeException("File is closed.");
	if((virtualLength == -1 || pos < virtualLength) && pos >= 0)
	    virtualFP = pos;
	else
	    throw new IllegalArgumentException("pos out of range (pos=" + pos +
                ",virtualLength=" + virtualLength + ")");
    }
    @Override
    public int read() {
	// Generic read() method
	byte[] b = new byte[1];
	int res = read(b, 0, 1);
	if(res == 1)
	    return b[0] & 0xFF;
	else
	    return -1;
    }
    @Override
    public int read(byte[] data) {
	// Generic read(byte[]) method
	return read(data, 0, data.length);
    }
    @Override
    public int read(final byte[] data, final int pos, final int len) {
	if(closed) throw new RuntimeException("File is closed.");
	//System.out.println("ReadableBlockCachingStream.read(data, " + pos + ", " + len + ");");
	//long fp = getFilePointer();
	int bytesProcessed = 0;
	while(bytesProcessed < len) {
	    byte[] blockData = getCachedBlock(virtualFP);
	    int posInBlock = (int)(virtualFP - (virtualFP/blockSize)*blockSize); // Will deviate from fp with at most blockSize bytes, so int
	    int bytesLeftInBlock = blockData.length-posInBlock;
	    int bytesLeftInTransfer = len-bytesProcessed;
	    int bytesToCopy = (bytesLeftInTransfer < bytesLeftInBlock ? bytesLeftInTransfer : bytesLeftInBlock);
	    System.arraycopy(blockData, posInBlock, data, pos+bytesProcessed, bytesToCopy);
	    bytesProcessed += bytesToCopy;
	    virtualFP += bytesToCopy;
	}
	
	return bytesProcessed;
    }
    @Override
    public void readFully(byte[] data) {
	// Generic readFully(byte[]) method
	readFully(data, 0, data.length);
    }

    @Override
    public void readFully(byte[] data, int offset, int length) {
	// Generic readFully(byte[], int, int) method
	int bytesRead = 0;
	while(bytesRead < length) {
	    int curBytesRead = read(data, offset+bytesRead, length-bytesRead);
	    if(curBytesRead > 0) bytesRead += curBytesRead;
	    else 
		throw new RuntimeException("Couldn't read the entire length.");
	}
    }
    @Override
    public long length() {
	if(closed) throw new RuntimeException("File is closed.");
	return virtualLength;
    }
    @Override
    public long getFilePointer() {
	if(closed) throw new RuntimeException("File is closed.");
	return virtualFP;
    }
    @Override
    public void close() {
	closed = true;
	backingStore.close();
    }
    
    /**
     * If the block is present in the cache, return it immediately. Otherwise
     * read the block from the backing store, put it the backing store
     */
    private byte[] getCachedBlock(long filePointer) {
	final long blockNumber = filePointer / blockSize;
	
	// 1. Increment access count and access time
	BlockStore cur = blockMap.get(blockNumber);
	if(cur == null) {
	    cur = new BlockStore(blockNumber);
	    blockMap.put(blockNumber, cur);
	}
	++cur.accessCount;
	cur.lastAccessTime = System.currentTimeMillis();
	
	// 2. Get the data
	if(cur.data != null) {
	    //System.out.println("  HIT at block number " + blockNumber + "!");
	    // 2.1 Just return the data that's in the cache
	    return cur.data;
	}
	else { // If we get here, cur is not present in the cache. (It only has data if it's present in the cache)
	    //System.out.println("  MISS at block number " + blockNumber + "!");
	    // 2.2 Fetch data from backing store and put in cache IF it has a high enough access count.
	    // (We should maintain a "last accessed" block as well)
	    
	    // Throw out the last entry (if any) from the cache and fetch its data array.
	    // Also remove its data array. If it is a standard sized array, we can reuse it and save the garbage
	    // collector and heap allocator some work.
	    BlockStore lastCacheEntry = cache[cache.length-1];
	    cache[cache.length-1] = null;
	    byte[] recoveredData = null;
	    if(lastCacheEntry != null) {
		recoveredData = lastCacheEntry.data;
		lastCacheEntry.data = null; // Stole your array.
		if(recoveredData == null)
		    throw new RuntimeException("Entry in cache had a null array, which should never happen!");
	    }
	    
	    
	    // Read data from backing store
	    long blockPos = blockNumber*blockSize;
	    long remainingSize = length()-blockPos;
	    int dataSize = (int)(remainingSize < blockSize ? remainingSize : blockSize);
	    byte[] data;
	    if(recoveredData != null && dataSize == recoveredData.length)
		data = recoveredData;
	    else // Will only happen if (1) cache isn't full or (2) if we are dealing with the last block
		data = new byte[dataSize <= 0?blockSize:dataSize]; // TODO: Investigate the effect of this approach (setting the array size to blockSize for all block in -1 virtualLength streams).
	    //System.err.println("  Seeking to " + blockPos + " (block number: " + blockNumber + ", blockSize: " + blockSize + ", data.length: " + data.length + ")");
	    backingStore.seek(blockPos);
	    backingStore.read(data, 0, data.length);
	    
	    
	    // Place cur in the cache and make sure it goes to the right position. Time is O(cache.length)
	    cur.data = data; 
	    cache[cache.length-1] = cur;
	    bubbleIntoPosition(cache, cache.length-1);
	    
	    return cur.data;
	}
    }
    
    private static void bubbleIntoPosition(BlockStore[] array, int startIndex) {
	long timestamp = System.currentTimeMillis();
	for(int i = startIndex; i >= 1; --i) {
	    BlockStore low = array[i];
	    BlockStore high = array[i-1];
	    if(high == null || // Array has not been filled
	       low.accessCount > high.accessCount || // The access count of the new item is greater than the old one's
	       (timestamp - high.lastAccessTime) >= TIME_TO_KEEP_IN_CACHE) { // The old one is too old to be kept in cache
		/* //DEBUG
		if(!(high == null || low.accessCount > high.accessCount))
		    System.out.println("Moving down a block in cache because of age! Age=" + (timestamp - high.lastAccessTime));
		*/
		// Switch places
		array[i] = high;
		array[i-1] = low;
	    }
	}
    }
    
    /** Loads as much data as possible into memory starting at position 0. */
    public void preloadBlocks() {
	preloadBlocks(0, cache.length);
    }
    /** Not exposed as public interface because the outside might not know how many blocks there are. */
    private void preloadBlocks(int startBlock, int blockCount) {
	for(int i = 0; i < blockCount; ++i) {
	    System.err.println("Preloading block " + (startBlock+i) + "...");
	    getCachedBlock((startBlock+i)*blockSize);
	}
    }
}
