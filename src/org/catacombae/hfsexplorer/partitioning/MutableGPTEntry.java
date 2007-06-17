package org.catacombae.hfsexplorer.partitioning;
import org.catacombae.hfsexplorer.Util;
import java.io.PrintStream;

public class MutableGPTEntry extends GPTEntry {
    
    public MutableGPTEntry(int blockSize) {
	super(blockSize);
    }
    public MutableGPTEntry(GPTEntry source) {
	super(source);
    }
    
    public void setPartitionTypeGUID(byte[] data, int off) { copyData(data, off, partitionTypeGUID); }
    public void setUniquePartitionGUID(byte[] data, int off) { copyData(data, off, uniquePartitionGUID); }
    public void setStartingLBA(long i) { Util.arrayCopy(Util.toByteArrayBE(i), startingLBA); }
    public void setEndingLBA(long i) { Util.arrayCopy(Util.toByteArrayBE(i), endingLBA); }
    public void setAttributeBits(long i) { Util.arrayCopy(Util.toByteArrayBE(i), attributeBits); }
    public void setPartitionName(byte[] data, int off) { copyData(data, off, partitionName); }
    
    private static void copyData(byte[] data, int off, byte[] dest) {
	copyData(data, off, dest, dest.length);
    }
    private static void copyData(byte[] data, int off, byte[] dest, int len) {
	if(off+len > data.length)
	    throw new IllegalArgumentException("Length of input data must be " + len + ".");
	System.arraycopy(data, off, dest, 0, len);
    }
    public void print(PrintStream ps, String prefix) {
	ps.println(prefix + "MutableGPTEntry:");
	printFields(ps, prefix);
    }
}
