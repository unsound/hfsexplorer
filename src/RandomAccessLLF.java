import java.io.*;

public class RandomAccessLLF implements LowLevelFile {
    private RandomAccessFile raf;
    public RandomAccessLLF(String filename) {
	try {
	    this.raf = new RandomAccessFile(filename, "r");
	} catch(Exception e) { throw new RuntimeException(e); }
    }
    public void seek(long pos) {
	try {
	    raf.seek(pos);
	} catch(IOException ioe) { throw new RuntimeException(ioe); }
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
