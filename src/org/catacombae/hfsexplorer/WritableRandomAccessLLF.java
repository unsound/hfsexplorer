/*-
 * Copyright (C) 2007 Erik Larsson
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

/** This class wraps a java.io.RandomAccessFile (opened in read/write mode) and maps its operations
    to the operations of WritableLowLevelFile. */
public class WritableRandomAccessLLF extends RandomAccessLLF implements WritableLowLevelFile {
    public WritableRandomAccessLLF(String filename) {
	try {
	    this.raf = new RandomAccessFile(filename, "rw");
	} catch(Exception e) { throw new RuntimeException(e); }	
    }
    public void write(byte[] b) {
	try {
	    raf.write(b);
	} catch(IOException ioe) { throw new RuntimeException(ioe); }
    }
    public void write(byte[] b, int off, int len) {
	try {
	    raf.write(b, off, len);
	} catch(IOException ioe) { throw new RuntimeException(ioe); }
    }
    public void write(int b) {
	try {
	    raf.write(b);
	} catch(IOException ioe) { throw new RuntimeException(ioe); }
    }    
}
