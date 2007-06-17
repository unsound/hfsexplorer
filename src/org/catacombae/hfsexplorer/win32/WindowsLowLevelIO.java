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

package org.catacombae.hfsexplorer.win32;

import org.catacombae.hfsexplorer.Util;
import java.io.*;

public class WindowsLowLevelIO implements org.catacombae.hfsexplorer.LowLevelFile {
    protected byte[] fileHandle;
    protected int sectorSize = 512; //Detect this later..
    protected long filePointer = 0;

    static {
	try {
	    System.loadLibrary("llio");
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }
    public WindowsLowLevelIO(String filename) {
	boolean verbose = false;
	fileHandle = open(filename);
	//System.out.println("fileHandle: 0x" + Util.byteArrayToHexString(fileHandle));
	int tmpSectorSize = getSectorSize(fileHandle);
	if(tmpSectorSize > 0) {
	    if(verbose) System.out.println("Sector size determined: " + tmpSectorSize);
	    sectorSize = tmpSectorSize;
	}
	else
	    if(verbose) System.out.println("Could not determine sector size.");
    }
    public void seek(long pos) {
	if(fileHandle != null) {
	    // We seek to the beginning of the sector containing pos
	    seek((pos/sectorSize)*sectorSize, fileHandle);
	    filePointer = pos;
	}
	else
	    throw new RuntimeException("File closed!");
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
	if(fileHandle != null) {
	    /* First make sure that we are at the beginning of the sector containing
	       filePointer. */
	    seek((filePointer/sectorSize)*sectorSize, fileHandle);
	    
	    /* Calculate how many bytes we have to skip in order to get to the data that
	       filePointer references. (fpDiff) */
	    long trueFp = getFilePointer(fileHandle);
	    long fpDiff = filePointer-trueFp;
	    if(fpDiff < 0)
		throw new RuntimeException("Program error: fpDiff < 0 (" + fpDiff + " < 0)");
	    else if(fpDiff > sectorSize)
		throw new RuntimeException("Program error: fpDiff > sectorSize (" + fpDiff + " > " + sectorSize + ")");
	    
	    /* Add the bytes that we will have to skip to the total read length. */
	    int alignedLen = (int)fpDiff + len;
	    
	    /* Allocate a sufficiently large temp buffer aligned to the sector size. */
	    byte[] tmp = new byte[(alignedLen/sectorSize+(alignedLen%sectorSize!=0?1:0))*sectorSize];
	    
	    /* Read into the array tmp, which now should be aligned to sector size. Our
	       position in the file should also be aligned to sector size through the
	       initial seek. No problem should occur. I hope. */
	    int bytesRead = read(tmp, 0, tmp.length, fileHandle);
	    
	    /* Trim away the unnecessary leading and trailing data length. */
	    bytesRead = (bytesRead >= alignedLen)?len:bytesRead-(int)fpDiff; // trim bytesRead to len if >= len
	    filePointer += bytesRead; // update the (virtual) file pointer
	    System.arraycopy(tmp, (int)fpDiff, data, pos, bytesRead);
	    return bytesRead;
	}
	else
	    throw new RuntimeException("File closed!");
    }
    
    public void readFully(byte[] data) {
	readFully(data, 0, data.length);
    }

    public void readFully(byte[] data, int offset, int length) {
	if(fileHandle != null) {
	    int bytesRead = 0;
	    while(bytesRead < length) {
		int curBytesRead = read(data, offset+bytesRead, length-bytesRead);
		if(curBytesRead > 0) bytesRead += curBytesRead;
		else 
		    throw new RuntimeException("Couldn't read the entire length.");
	    }
	}
	else
	    throw new RuntimeException("File closed!");
    }
    
    public long length() {
	if(fileHandle != null) {
	    return length(fileHandle);
	}
	else
	    throw new RuntimeException("File closed!");
    }
	
    public long getFilePointer() {
	if(fileHandle != null) {
	    return filePointer;//getFilePointer(fileHandle);
	}
	else
	    throw new RuntimeException("File closed!");
    }
    
    public void close() {
	if(fileHandle != null) {
	    close(fileHandle);
	    fileHandle = null;
	}
	else
	    throw new RuntimeException("File closed!");
    }

    public void ejectMedia() {
	if(fileHandle != null)
	    ejectMedia(fileHandle);
	else
	    throw new RuntimeException("File closed!");
    }
    public void loadMedia() {
	if(fileHandle != null)
	    loadMedia(fileHandle);
	else
	    throw new RuntimeException("File closed!");
    }
    
    protected byte[] open(String filename) {
	//System.out.println("Java: WindowsLowLevelIO.open(" + filename + ");");
	return openNative(filename);
    }
    
    protected static native byte[] openNative(String filename);
    protected static native void seek(long pos, byte[] handle);
    protected static native int read(byte[] data, int pos, int len, byte[] handle);
    protected static native void close(byte[] handle);
    protected static native void ejectMedia(byte[] handle);
    protected static native void loadMedia(byte[] handle);
    protected static native long length(byte[] handle);
    protected static native long getFilePointer(byte[] handle);
    protected static native int getSectorSize(byte[] handle);
//     protected static native void getHandleType(byte[] handle);
//     protected static native void getDeviceLength(byte[] handle);
//     protected static native void getFileLength(byte[] handle);

    public static void main(String[] args) {
	BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
	WindowsLowLevelIO wllio1 = new WindowsLowLevelIO(args[0]);
	
	try {
	    if(args[1].equals("testread")) {
		// When reading directly from block devices, the buffer must be a multiple of the sector size of the device. Also, reading must start at a value dividable by the sector size. Calling DeviceIoControl with IOCTL_DISK_GET_DRIVE_GEOMETRY_EX will get the drive geometry for the device.
		System.out.println("Seeking to 1024...");
		wllio1.seek(1024);
		byte[] buf = new byte[4096];
		System.out.println("Reading " + buf.length + " bytes from file: ");
		int bytesRead = wllio1.read(buf);
		System.out.println(" Bytes read: " + bytesRead);
		System.out.println(" As hex:    0x" + Util.byteArrayToHexString(buf));
		System.out.println(" As string: \"" + new String(buf, "US-ASCII") + "\"");
	    }
	    else if(args[1].equals("eject")) {
		System.out.print("Press any key to eject media...");
		stdin.readLine();
		wllio1.ejectMedia();
		System.out.print("Press any key to load media...");
		stdin.readLine();
		wllio1.loadMedia();
	    }
	    else
		System.out.println("Nothing to do.");
	} catch(Exception e) { e.printStackTrace(); }
	wllio1.close();
    }
}
