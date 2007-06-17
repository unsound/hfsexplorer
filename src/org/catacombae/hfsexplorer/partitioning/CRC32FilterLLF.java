package org.catacombae.hfsexplorer.partitioning;
import org.catacombae.hfsexplorer.LowLevelFile;
import java.util.zip.CRC32;

/** Updates a CRC32 checksum for each byte you read from the underlying stream.
    Seeking does not reset the checksum, it is only the read methods and what
    they return that alter the value of the checksum. */
public class CRC32FilterLLF implements LowLevelFile {
    private LowLevelFile source;
    private CRC32 checksum;
	
    public CRC32FilterLLF(LowLevelFile source) {
	this.source = source;
	this.checksum = new CRC32();
    }
	
    public int getChecksumValue() { return (int)(checksum.getValue() & 0xFFFFFFFF); }
    public void resetChecksum() { checksum.reset(); }
    
    public void seek(long pos) {
	source.seek(pos);
    }
    public int read() {
	int res = source.read();
	if(res > 0) checksum.update(res);
	return res;
    }
    public int read(byte[] data) {
	int res = source.read(data);
	if(res > 0) checksum.update(data, 0, res);
	return res;
    }
    public int read(byte[] data, int pos, int len) {
	int res = source.read(data);
	if(res > 0) checksum.update(data, pos, res);
	return res;
    }
    public void readFully(byte[] data) {
	source.read(data);
	checksum.update(data);
    }
    public void readFully(byte[] data, int offset, int length) {
	source.read(data, offset, length);
	checksum.update(data, offset, length);
    }
    public long length() { return source.length(); }
    public long getFilePointer() { return source.getFilePointer(); }
    public void close() { source.close(); }
}
