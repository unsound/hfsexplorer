/* Designed to mimic RandomAccessFile. */

public interface LowLevelFile {
    public void seek(long pos);
    public int read();
    public int read(byte[] data);
    public int read(byte[] data, int pos, int len);
    public void readFully(byte[] data);
    public void readFully(byte[] data, int offset, int length);
    public long length();
    public long getFilePointer();
    public void close();
}
