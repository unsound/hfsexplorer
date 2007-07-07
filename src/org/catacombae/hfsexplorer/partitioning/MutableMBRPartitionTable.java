package org.catacombae.hfsexplorer.partitioning;
import org.catacombae.hfsexplorer.*;

public class MutableMBRPartitionTable extends MBRPartitionTable {
//     private MutableMBRPartitionTable() {
// 	throw new RuntimeException("Default boot code not yet written.");
//     }
    public MutableMBRPartitionTable(MBRPartitionTable mbr) {
	super(mbr);
    }
    
    public void setOptionalIBMExtendedData1(byte[] data) { copyData(data, 0, optIBMExtendedData1); }
    public void setOptionalIBMExtendedData2(byte[] data) { copyData(data, 0, optIBMExtendedData2); }
    public void setOptionalIBMExtendedData3(byte[] data) { copyData(data, 0, optIBMExtendedData3); }
    public void setOptionalIBMExtendedData4(byte[] data) { copyData(data, 0, optIBMExtendedData4); }
    public void setOptionalDiskSignature(int sig) {
	Util.arrayCopy(Util.toByteArrayBE(sig), optDiskSignature);
    }
    public void setPartition(int i, MBRPartition partition) {
	partitions[i] = partition;
    }
    public void setMBRSignature(short sig) {
	Util.arrayCopy(Util.toByteArrayBE(sig), mbrSignature);
    }
    
    private static void copyData(byte[] data, int off, byte[] dest) {
	copyData(data, off, dest, dest.length);
    }
    private static void copyData(byte[] data, int off, byte[] dest, int len) {
	if(off+len > data.length)
	    throw new IllegalArgumentException("Length of input data must be " + len + ".");
	System.arraycopy(data, off, dest, 0, len);
    }
}
