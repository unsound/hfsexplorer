package org.catacombae.hfsexplorer;
import java.util.*;

public class ConcatenatedFile implements LowLevelFile {
    private class Part {
	public Part(LowLevelFile file, long startOffset, long length) {
	    this.file = file;
	    this.startOffset = startOffset;
	    this.length = length;
	}
	public LowLevelFile file;
	public long startOffset, length;
    }
    private final List<Part> parts = new ArrayList<Part>();
    private Part currentPart;
    private int currentPartIndex;
    
    public ConcatenatedFile(LowLevelFile firstPart, long startOffset, long length) {
	currentPart = new Part(firstPart, startOffset, length);
	parts.add(currentPart);
	currentPartIndex = 0;
    }
    public void seek(long pos) {
	long curPos = 0;
	for(Part p : parts) {
	    if(curPos+p.length > pos) {
		currentPart = p;
		currentPart.file.seek(currentPart.startOffset + (pos-curPos));
		break;
	    }
	    else
		curPos += p.length;
	}
    }
    public int read() {
	byte[] tmp = new byte[1];
	int res = read(tmp, 0, 1);
	if(res == 1)
	    return tmp[0] & 0xFF;
	else
	    throw new RuntimeException("Could not read.");
    }
    public int read(byte[] data) {
	return read(data, 0, data.length);
    }
    public int read(byte[] data, int pos, int len) {
	int bytesRead = 0;
	while(true) {
	    long bytesLeftInFile = currentPart.length - (currentPart.file.getFilePointer() - currentPart.startOffset);
	    int bytesLeftToRead = len - bytesRead;
	    int bytesToRead = (int)((bytesLeftInFile < bytesLeftToRead) ? bytesLeftInFile : bytesLeftToRead);
	    int res = currentPart.file.read(data, pos+bytesRead, bytesToRead);
	    if(res > 0) {
		bytesRead += res;
		if(bytesRead < len) {
		    // move pointer forward, so that currentPart advances.
		    currentPartIndex++;
		    currentPart = parts.get(currentPartIndex);
		    currentPart.file.seek(currentPart.startOffset);
		}
		else if(bytesRead == len)
		    return bytesRead;
		else
		    throw new RuntimeException("Read more than I was supposed to! This can't happen.");
	    }
	    else
		throw new RuntimeException("Error while reading.");
	}
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
	long result = 0;
	for(Part p : parts)
	    result += p.length;
	return result;
    }
    public long getFilePointer() {
	long fp = 0;
	for(int i = 0; i < currentPartIndex; ++i)
	    fp += parts.get(i).length;
	
	Part currentPart = parts.get(currentPartIndex);
	fp += currentPart.file.getFilePointer() + currentPart.startOffset;
	
	return fp;
    }
    /** Closes all the files constituting this ConcatenatedFile. */
    public void close() {
	for(Part p : parts)
	    p.file.close();
    }
}