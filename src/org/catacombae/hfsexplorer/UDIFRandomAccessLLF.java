/*-
 * Copyright (C) 2006-2007 Erik Larsson
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

package org.catacombae.hfsexplorer;

import org.catacombae.dmgx.*;
import org.catacombae.io.*;
import java.io.*;

/**
 * This class acts as the bridge between the libraries of DMGExtractor and
 * HFSExplorer.
 */
public class UDIFRandomAccessLLF implements LowLevelFile {
    private DmgRandomAccessStream raf;
    public UDIFRandomAccessLLF(String filename) {
	try {
	    //System.err.println("opening rafStream");
	    RandomAccessFileStream rafStream = new RandomAccessFileStream(new RandomAccessFile(filename, "r"));
	    //System.err.println("opening dmgf");
	    DmgFile dmgf = new DmgFile(rafStream);
	    //System.err.println("opening raf");
	    this.raf = new DmgRandomAccessStream(dmgf);
	    //System.err.println("constructed");
	} catch(Exception e) { throw new RuntimeException(e); }
    }
    public void seek(long pos) {
	try {
	    raf.seek(pos);
	} catch(IOException ioe) { throw new RuntimeException("pos=" + pos + "," + ioe.toString(), ioe); }
    }
    public int read() {
	try {
	    return raf.read();
	} catch(IOException ioe) { throw new RuntimeException(ioe); }
    }
    public int read(byte[] data) {
	try {
	    return raf.read(data);
	} catch(IOException ioe) { throw new RuntimeException(ioe); }
    }
    public int read(byte[] data, int pos, int len) {
	try {
	    return raf.read(data, pos, len);
	} catch(IOException ioe) { throw new RuntimeException(ioe); }
    }
    public void readFully(byte[] data) {
	readFully(data, 0, data.length);
    }

    public void readFully(byte[] data, int offset, int length) {
	int bytesRead = 0;
	while(bytesRead < length) {
	    int curBytesRead = read(data, offset+bytesRead, length-bytesRead);
	    if(curBytesRead > 0) bytesRead += curBytesRead;
	    else 
		throw new RuntimeException("Couldn't read the entire length.");
	}
    }
    public long length() {
	try {
	    return raf.length();
	} catch(IOException ioe) { throw new RuntimeException(ioe); }
    }
    public long getFilePointer() {
	try {
	    return raf.getFilePointer();
	} catch(IOException ioe) { throw new RuntimeException(ioe); }
    }
    public void close() {
	try {
	    raf.close();
	} catch(IOException ioe) { throw new RuntimeException(ioe); }
    }
}
