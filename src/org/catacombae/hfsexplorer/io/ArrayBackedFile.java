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

package org.catacombae.hfsexplorer.io;

/**
 * A LowLevelFile implementation backed by an array.
 * 
 * @author Erik Larsson, erik82@kth.se
 */
public class ArrayBackedFile implements LowLevelFile {
    private final byte[] backingArray;
    private final int startOffset;
    private final int length;
    private int filePointer;
    private boolean closed = false;
    
    public ArrayBackedFile(byte[] array) {
        this(array, 0, array.length);
    }
    public ArrayBackedFile(byte[] array, int off, int len) {
        if(off >= array.length || off < 0)
            throw new IllegalArgumentException("parameter off out of bounds (off=" + off + ")");
        if(off+len > array.length || len < 0)
            throw new IllegalArgumentException("parameter len out of bounds (len=" + len + ")");
        this.backingArray = array;
        this.startOffset = off;
        this.length = len;
        this.filePointer = 0;
    }
    
    public void seek(long pos) {
        if(closed)
            throw new RuntimeException("File has been closed!");
        
        if(pos >= length || pos < 0)
            throw new IllegalArgumentException("parameter pos out of bounds");
        else
            filePointer = (int)pos;
    }

    public int read() {
	// Generic read() method
	byte[] b = new byte[1];
	int res = read(b, 0, 1);
	if(res == 1)
	    return b[0] & 0xFF;
	else
	    return -1;
    }
    public int read(byte[] data) {
	// Generic read(byte[]) method
	return read(data, 0, data.length);
    }

    public int read(byte[] data, int pos, int len) {
        if(closed)
            throw new RuntimeException("File has been closed!");
        
        int remainingBytes = length-filePointer;
        if(remainingBytes == 0)
            return -1;
        
        int trueLen = Math.min(remainingBytes, len);
        System.arraycopy(backingArray, startOffset+filePointer, data, pos, trueLen);
        filePointer += trueLen;
        return trueLen;
    }

    public void readFully(byte[] data) {
	// Generic readFully(byte[]) method
	readFully(data, 0, data.length);
    }

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

    public long length() {
        if(closed)
            throw new RuntimeException("File has been closed!");
        
        return length;
    }

    public long getFilePointer() {
        if(closed)
            throw new RuntimeException("File has been closed!");
        
        return filePointer;
    }

    public void close() {
        if(closed)
            throw new RuntimeException("File has been closed!");
        
        closed = true;
    }

}
