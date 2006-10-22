import java.io.PrintStream;

public class HFSPlusForkData {
    /*
     * struct HFSPlusForkData
     * size: 80 bytes
     *
     * BP   Size  Type                 Variable name   Description
     * --------------------------------------------------------------
     * 0    8     UInt64               logicalSize
     * 8    4     UInt32               clumpSize
     * 12   4     UInt32               totalBlocks
     * 16   64    HFSPlusExtentRecord  extents
     */
	
    private final byte[] logicalSize = new byte[8];
    private final byte[] clumpSize = new byte[4];
    private final byte[] totalBlocks = new byte[4];
    private final HFSPlusExtentRecord extents;

    public HFSPlusForkData(byte[] data, int offset) {
	System.arraycopy(data, offset+0, logicalSize, 0, 8);
	System.arraycopy(data, offset+8, clumpSize, 0, 4);
	System.arraycopy(data, offset+12, totalBlocks, 0, 4);
	extents = new HFSPlusExtentRecord(data, offset+16);
    }
	
    public long getLogicalSize() {
	return Util.readLongBE(logicalSize);
    }
    public long getClumpSize() {
	return Util.readIntBE(clumpSize);
    }
    public long getTotalBlocks() {
	return Util.readIntBE(totalBlocks);
    }
    public HFSPlusExtentRecord getExtents() { return extents; }
	
    public void print(PrintStream ps, int pregap) {
	String pregapString = "";
	for(int i = 0; i < pregap; ++i)
	    pregapString += " ";
	print(ps, pregapString);
    }
	
    public void print(PrintStream ps, String prefix) {
	ps.println(prefix + "logicalSize: " + getLogicalSize());
	ps.println(prefix + "clumpSize: " + getClumpSize());
	ps.println(prefix + "totalBlocks: " + getTotalBlocks());
	ps.println(prefix + "extents:");
	extents.print(ps, prefix + "  ");
    }
}
