/*-
 * Copyright (C) 2006 Erik Larsson
 * 
 * All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 */

/* A first filter, not adapted for using the extents overflow file. */

/**
 * Facilitates reading the data of a fork by abstracting the extents and presents it to the
 * programmer as if it was a continious, seekable stream.<br>
 * Note: If you modify the state of the underlying stream while using this filter, the
 * behavior of the filter is undefined. The read methods will not return the correct data.
 * <pre>
 * Model:
 * 
 * - seeking does not do anything except setting a pointer value
 * - when read is called:
 *   - if logicalPosition is different from our last position
 *     - seek to the right position
 *   - else if file pointer is different from our last file pointer
 *     - seek to last fp
 *   
 * </pre>
 */

package org.catacombae.hfsexplorer;

import org.catacombae.hfsexplorer.types.*;

public class ForkFilter implements LowLevelFile {
    private HFSPlusForkData forkData;
    private HFSPlusExtentDescriptor[] extentDescriptors;
    private LowLevelFile sourceFile;
    private long fsOffset;
    private long blockSize;
    private long logicalPosition; // The current position in the fork
    private long lastLogicalPos; // The position in the fork where we stopped reading last time
    private long lastPhysicalPos; // The position in the fork where we stopped reading last time
    
//     public ForkFilter(HFSPlusForkData forkData, LowLevelFile sourceFile, long fsOffset, long blockSize) {
// 	this(forkData, forkData.getExtents().getExtentDescriptors(), sourceFile, fsOffset, blockSize);
//     }
    public ForkFilter(HFSPlusForkData forkData, HFSPlusExtentDescriptor[] extentDescriptors, 
		      LowLevelFile sourceFile, long fsOffset, long blockSize) {
	this.forkData = forkData;
	this.extentDescriptors = extentDescriptors;
	this.sourceFile = sourceFile;
	this.fsOffset = fsOffset;
	this.blockSize = blockSize;
	this.logicalPosition = 0;
	this.lastLogicalPos = -1; // Set differently from logicalPosition to trigger a seek at first read
	this.lastPhysicalPos = 0; // Set differently from logicalPosition to trigger a seek at first read
    }

    public void seek(long pos) {
	logicalPosition = pos;
    }
    public int read() {
	byte[] oneByte = new byte[1];
	if(read(oneByte) == 1)
	    return oneByte[0] & 0xFF;
	else
	    return -1;
    }
    public int read(byte[] data) {
	return read(data, 0, data.length);
    }
    public int read(byte[] data, int pos, int len) {
	long offset = fsOffset;
	long bytesToSkip = logicalPosition;
	int extIndex;
	long currentExtentLength;
	HFSPlusExtentDescriptor[] extents = extentDescriptors;
	
	// Skip all extents whose range is located before the requested position (logicalPosition)
	//System.out.print("ForkFilter.read: skipping extents...");
	for(extIndex = 0; extIndex < extents.length; ++extIndex) {
	    HFSPlusExtentDescriptor cur = extents[extIndex];
	    currentExtentLength = Util2.unsign(cur.getBlockCount())*blockSize;
	    if(bytesToSkip >= currentExtentLength) {
		if(extIndex < extents.length-1)
		    bytesToSkip -= currentExtentLength;
		else
		    throw new RuntimeException("Extent out of bounds!");
	    }
	    else {
		offset = fsOffset + Util2.unsign(cur.getStartBlock())*blockSize + bytesToSkip;
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
	
	int bytesLeftToRead = len;
	// Start reading. Extent by extent if needed.
	for(; extIndex < extents.length; ++extIndex) {
	    //System.out.println("ForkFilter.read: reading extent " + extIndex + ".");
	    HFSPlusExtentDescriptor cur = extents[extIndex];

	    long bytesInExtent = Util2.unsign(cur.getBlockCount())*blockSize - bytesToSkip;
	    int bytesToReadFromExtent = (bytesInExtent < bytesLeftToRead) ? (int)bytesInExtent : bytesLeftToRead;

	    int bytesReadFromExtent = 0;
	    while(bytesReadFromExtent < bytesToReadFromExtent) {
		int bytesToRead = bytesToReadFromExtent - bytesReadFromExtent;
		int positionInArray = pos + (len-bytesLeftToRead) + bytesReadFromExtent;
		
		int bytesRead = sourceFile.read(data, positionInArray, bytesToRead);
		if(bytesRead > 0)
		    bytesReadFromExtent += bytesRead;
		else {
		    // Update tracker variables before returning
		    lastPhysicalPos = sourceFile.getFilePointer();
		    int totalBytesRead = positionInArray-pos;
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
	logicalPosition += len;
	
	return len;
    }
    public void readFully(byte[] data) {
	readFully(data, 0, data.length);
    }
    public void readFully(byte[] data, int offset, int length) {
	int bytesRead = 0;
	while(bytesRead < length) {
	    int curBytesRead = read(data, bytesRead, length-bytesRead);
	    if(curBytesRead > 0) bytesRead += curBytesRead;
	    else 
		throw new RuntimeException("Couldn't read the entire length.");
	}
    }
    public long length() { return forkData.getLogicalSize(); }
    public long getFilePointer() { return logicalPosition; }
    
    public LowLevelFile getUnderlyingStream() { return sourceFile; }
    
    /** Closes the underlying stream. Equivalent to <code>getUnderlyingStream().close()</code>. Nothing more is done. */
    public void close() { sourceFile.close(); }

 //    private int seekLogical(long logicalPosition, HFSPlusExtentDescriptor[] extents) {
// 	/*
// 	 * foreach extent e:
// 	 *   
// 	 */
// 	// Seek forward to requested position
// 	long bytesToSkip = logicalPosition;
// 	//HFSPlusExtentDescriptor[] extents = forkData.getExtents().getExtentDescriptors();
	
// 	for(int extIndex = 0; extIndex < extents.length; ++extIndex) {
// 	    HFSPlusExtentDescriptor cur = extents[extIndex];
// 	    long currentExtentLength = Util2.unsign(cur.getBlockCount())*blockSize
// 	    if(bytesToSkip >= currentExtentLength)
// 		bytesToSkip -= currentExtentLength;
// 	    else {
// 		sourceFile.seek(fsOffset + Util2.unsign(cur.getStartBlock())*blockSize + bytesToSkip);
// 		return;
// 	    }
// 	}
// 	// If we got here, logical position was not in the main fork data
	
//     }
}
