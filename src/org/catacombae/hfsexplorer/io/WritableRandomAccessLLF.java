/*-
 * Copyright (C) 2007-2008 Erik Larsson
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
import java.io.*;

/** This class wraps a java.io.RandomAccessFile (opened in read/write mode) and maps its operations
    to the operations of WritableLowLevelFile. */
public class WritableRandomAccessLLF extends RandomAccessLLF implements WritableLowLevelFile {
    public WritableRandomAccessLLF(String filename) {
	try {
	    this.raf = new RandomAccessFile(filename, "rw");
	} catch(Exception e) { throw new RuntimeException(e); }	
    }
    
    public WritableRandomAccessLLF(File file) {
	try {
	    this.raf = new RandomAccessFile(file, "rw");
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
