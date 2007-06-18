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
