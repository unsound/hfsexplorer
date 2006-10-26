import java.io.PrintStream;

public class APMPartition {
    /*
     * struct Partition
     * size: 512 bytes
     *
     * BP   Size  Type              Variable name   Description
     * --------------------------------------------------------------
     * 0    2     UInt16            pmSig           partition signature
     * 2    2     UInt16            pmSigPad        reserved
     * 4    4     UInt32            pmMapBlkCnt     number of blocks in partition map
     * 8    4     UInt32            pmPyPartStart   first physical block of partition
     * 12   4     UInt32            pmPartBlkCnt    number of blocks in partition
     * 16   1*32  Char[32]          pmPartName      partition name
     * 48   1*32  Char[32]          pmParType       partition type
     * 80   4     UInt32            pmLgDataStart   first logical block of data area
     * 84   4     UInt32            pmDataCnt       number of blocks in data area
     * 88   4     UInt32            pmPartStatus    partition status information
     * 92   4     UInt32            pmLgBootStart   first logical block of boot code
     * 96   4     UInt32            pmBootSize      size of boot code, in bytes
     * 100  4     UInt32            pmBootAddr      boot code load address
     * 104  4     UInt32            pmBootAddr2     reserved
     * 108  4     UInt32            pmBootEntry     boot code entry point
     * 112  4     UInt32            pmBootEntry2    reserved
     * 116  4     UInt32            pmBootCksum     boot code checksum
     * 120  1*16  Char[16]          pmProcessor     processor type
     * 136  2*188 UInt16[188]       pmPad           reserved
     */
    private final byte[] pmSig = new byte[2];
    private final byte[] pmSigPad = new byte[2];
    private final byte[] pmMapBlkCnt = new byte[4];
    private final byte[] pmPyPartStart = new byte[4];
    private final byte[] pmPartBlkCnt = new byte[4];
    private final byte[] pmPartName = new byte[32];
    private final byte[] pmParType = new byte[32];
    private final byte[] pmLgDataStart = new byte[4];
    private final byte[] pmDataCnt = new byte[4];
    private final byte[] pmPartStatus = new byte[4];
    private final byte[] pmLgBootStart = new byte[4];
    private final byte[] pmBootSize = new byte[4];
    private final byte[] pmBootAddr = new byte[4];
    private final byte[] pmBootAddr2 = new byte[4];
    private final byte[] pmBootEntry = new byte[4];
    private final byte[] pmBootEntry2 = new byte[4];
    private final byte[] pmBootCksum = new byte[4];
    private final byte[] pmProcessor = new byte[16];
    private final byte[] pmPad = new byte[2*188];
	
    public APMPartition(byte[] data, int offset) {
	System.arraycopy(data, offset+0, pmSig, 0, 2);
	System.arraycopy(data, offset+2, pmSigPad, 0, 2);
	System.arraycopy(data, offset+4, pmMapBlkCnt, 0, 4);
	System.arraycopy(data, offset+8, pmPyPartStart, 0, 4);
	System.arraycopy(data, offset+12, pmPartBlkCnt, 0, 4);
	System.arraycopy(data, offset+16, pmPartName, 0, 32);
	System.arraycopy(data, offset+48, pmParType, 0, 32);
	System.arraycopy(data, offset+80, pmLgDataStart, 0, 4);
	System.arraycopy(data, offset+84, pmDataCnt, 0, 4);
	System.arraycopy(data, offset+88, pmPartStatus, 0, 4);
	System.arraycopy(data, offset+92, pmLgBootStart, 0, 4);
	System.arraycopy(data, offset+96, pmBootSize, 0, 4);
	System.arraycopy(data, offset+100, pmBootAddr, 0, 4);
	System.arraycopy(data, offset+104, pmBootAddr2, 0, 4);
	System.arraycopy(data, offset+108, pmBootEntry, 0, 4);
	System.arraycopy(data, offset+112, pmBootEntry2, 0, 4);
	System.arraycopy(data, offset+116, pmBootCksum, 0, 4);
	System.arraycopy(data, offset+120, pmProcessor, 0, 16);
	System.arraycopy(data, offset+136, pmPad, 0, 2*188);
    }
    public short getPmSig()        { return Util.readShortBE(pmSig); }
    public short getPmSigPad()     { return Util.readShortBE(pmSigPad); }
    public int getPmMapBlkCnt()    { return Util.readIntBE(pmMapBlkCnt); }
    public int getPmPyPartStart()  { return Util.readIntBE(pmPyPartStart); }
    public int getPmPartBlkCnt()   { return Util.readIntBE(pmPartBlkCnt); }
    public byte[] getPmPartName()  { return Util.createCopy(pmPartName); }
    public byte[] getPmParType()   { return Util.createCopy(pmParType); }
    public int getPmLgDataStart()  { return Util.readIntBE(pmLgDataStart); }
    public int getPmDataCnt()      { return Util.readIntBE(pmDataCnt); }
    public int getPmPartStatus()   { return Util.readIntBE(pmPartStatus); }
    public int getPmLgBootStart()  { return Util.readIntBE(pmLgBootStart); }
    public int getPmBootSize()     { return Util.readIntBE(pmBootSize); }
    public int getPmBootAddr()     { return Util.readIntBE(pmBootAddr); }
    public int getPmBootAddr2()    { return Util.readIntBE(pmBootAddr2); }
    public int getPmBootEntry()    { return Util.readIntBE(pmBootEntry); }
    public int getPmBootEntry2()   { return Util.readIntBE(pmBootEntry2); }
    public int getPmBootCksum()    { return Util.readIntBE(pmBootCksum); }
    public byte[] getPmProcessor() { return Util.createCopy(pmProcessor); }
    public short[] getPmPad()      { return Util2.readShortArrayBE(pmPad); }
    
    public String getPmSigAsString() { return Util2.toASCIIString(pmSig); }
    public String getPmPartNameAsString() { return Util2.readNullTerminatedASCIIString(pmPartName); }
    public String getPmParTypeAsString() { return Util2.readNullTerminatedASCIIString(pmParType); }
    public String getPmProcessorAsString() { return Util2.readNullTerminatedASCIIString(pmProcessor); }
    
    public void printPartitionInfo(PrintStream ps) {
	printPartitionInfo(ps, "");
    }
    public void printPartitionInfo(PrintStream ps, String prefix) {
	ps.println("pmSig: \"" + getPmSigAsString() + "\"");
	ps.println("pmSigPad: " + getPmSigPad());
	ps.println("pmMapBlkCnt: " + getPmMapBlkCnt());
	ps.println("pmPyPartStart: " + getPmPyPartStart());
	ps.println("pmPartBlkCnt: " + getPmPartBlkCnt());
	ps.println("pmPartName: \"" +  getPmPartNameAsString() + "\"");
	ps.println("pmParType: \"" +  getPmParTypeAsString() + "\"");
	ps.println("pmLgDataStart: " + getPmLgDataStart());
	ps.println("pmDataCnt: " + getPmDataCnt());
	ps.println("pmPartStatus: " + getPmPartStatus());
	ps.println("pmLgBootStart: " + getPmLgBootStart());
	ps.println("pmBootSize: " + getPmBootSize());
	ps.println("pmBootAddr: " + getPmBootAddr());
	ps.println("pmBootAddr2: " + getPmBootAddr2());
	ps.println("pmBootEntry: " + getPmBootEntry());
	ps.println("pmBootEntry2: " + getPmBootEntry2());
	ps.println("pmBootCksum: " + getPmBootCksum());
	ps.println("pmProcessor: \"" + getPmProcessorAsString() + "\"");
	ps.println("pmPad: " + getPmPad());
    }

    public String toString() {
	return "\"" + getPmPartNameAsString() + "\" (" + getPmParTypeAsString() + ")";
    }
}
