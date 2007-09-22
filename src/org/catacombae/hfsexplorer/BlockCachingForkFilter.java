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

package org.catacombae.hfsexplorer;

import java.util.*;

public class BlockCachingForkFilter extends FilterLLF {
    private final int blockSize;
    //private final long[]
    private final Hashtable<Long, BlockStore> bsTable;
    
    private static class BlockStore {
	byte[] blockData;
	long blockOffset;
	long accessCount;
    }
			    
    public BlockCachingForkFilter(ForkFilter backing, int blockSize, int cacheSize) {
	super(backing);
	this.blockSize = blockSize;
	this.bsTable = null; //??
    }
    public void seek(long pos) {
	backingStore.seek(pos);
    }
    public int read() {
	byte[] b = new byte[1];
	int res = read(b, 0, 1);
	if(res == 1)
	    return b[0] & 0xFF;
	else
	    return -1;
    }
    public int read(byte[] data) {
	return read(data, 0, data.length);
    }
    public int read(byte[] data, int pos, int len) {
	long fp = super.getFilePointer();
	byte[] blockData = getCachedBlock(fp);
	long blockStart = (fp/blockSize)*blockSize;
	return backingStore.read(data, pos, len);
    }
    public void readFully(byte[] data) {
	backingStore.readFully(data);
    }
    public void readFully(byte[] data, int offset, int length) {
	backingStore.readFully(data, offset, length);
    }
    public long length() {
	return backingStore.length();
    }
    public long getFilePointer() {
	return backingStore.getFilePointer();
    }
    public void close() {
	backingStore.close();
    }
    
    private byte[] getCachedBlock(long filePointer) { return null; }
}
