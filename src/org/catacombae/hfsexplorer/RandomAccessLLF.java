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

package org.catacombae.hfsexplorer;

import java.io.*;

/** This class wraps a java.io.RandomAccessFile (opened in read-only mode) and maps its operations
    to the operations of LowLevelFile. */
public class RandomAccessLLF implements LowLevelFile {
    protected RandomAccessFile raf;
    public RandomAccessLLF(String filename) {
	try {
	    this.raf = new RandomAccessFile(filename, "r");
	} catch(Exception e) { throw new RuntimeException(e); }
    }
    protected RandomAccessLLF() {
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
	try {
	    raf.readFully(data);
	} catch(IOException ioe) { throw new RuntimeException(ioe); }
    }
    public void readFully(byte[] data, int offset, int length) {
	try {
	    raf.readFully(data, offset, length);
	} catch(IOException ioe) { throw new RuntimeException(ioe); }
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
