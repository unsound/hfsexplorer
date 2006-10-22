import java.io.PrintStream;

public class HFSPlusExtentDescriptor {
    /*
     * struct HFSPlusExtentDescriptor
     * size: 8 bytes
     *
     * BP   Size  Type              Variable name   Description
     * --------------------------------------------------------------
     * 0    4     UInt32            startBlock
     * 4    4     UInt32            blockCount
     */
	
    private final byte[] startBlock = new byte[4]; // UInt32
    private final byte[] blockCount = new byte[4]; // UInt32

    public HFSPlusExtentDescriptor(byte[] data, int offset) {
	System.arraycopy(data, offset, startBlock, 0, 4);
	System.arraycopy(data, offset+4, blockCount, 0, 4);
    }
	
    public static int getSize() {
	return 8;
    }
	
    public int getStartBlock() { return Util.readIntBE(startBlock); }
    public int getBlockCount() { return Util.readIntBE(blockCount); }
	
    public void print(PrintStream ps, int pregap) {
	String pregapString = "";
	for(int i = 0; i < pregap; ++i)
	    pregapString += " ";
	print(ps, pregapString);
    }
    public void print(PrintStream ps, String prefix) {
	ps.println(prefix + "startBlock: " + getStartBlock());
	ps.println(prefix + "blockCount: " + getBlockCount());
    }
}
