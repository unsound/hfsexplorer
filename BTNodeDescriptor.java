import java.io.PrintStream;

public class BTNodeDescriptor {
    /*
     * struct BTNodeDescriptor
     * size: 14 bytes
     *
     * BP   Size  Type              Variable name
     * --------------------------------------------
     * 0    4     UInt32            fLink
     * 4    4     UInt32            bLink
     * 8    1     SInt8             kind
     * 9    1     UInt8             height
     * 10   2     UInt16            numRecords
     * 12   2     UInt16            reserved
     */
    
    public static final int BT_LEAF_NODE = -1;
    public static final int BT_INDEX_NODE = 0;
    public static final int BT_HEADER_NODE = 1;
    public static final int BT_MAP_NODE = 2;
    
    private final byte[] fLink = new byte[4];
    private final byte[] bLink = new byte[4];
    private final byte[] kind = new byte[1];
    private final byte[] height = new byte[1];
    private final byte[] numRecords = new byte[2];
    private final byte[] reserved = new byte[2];
    
    public BTNodeDescriptor(byte[] data, int offset) {
	System.arraycopy(data, offset+0, fLink, 0, 4);
	System.arraycopy(data, offset+4, bLink, 0, 4);
	System.arraycopy(data, offset+8, kind, 0, 1);
	System.arraycopy(data, offset+9, height, 0, 1);
	System.arraycopy(data, offset+10, numRecords, 0, 2);
	System.arraycopy(data, offset+12, reserved, 0, 2);
    }
    public int getFLink() { return Util.readIntBE(fLink); }
    public int getBLink() { return Util.readIntBE(bLink); }
    public byte getKind() { return Util.readByteBE(kind); }
    public byte getHeight() { return Util.readByteBE(height); }
    public short getNumRecords() { return Util.readShortBE(numRecords); }
    public short getReserved() { return Util.readShortBE(reserved); }

    public String getKindAsString() {
	byte kind = getKind();
	String result;
	if(kind == BT_LEAF_NODE)
	    result = "kBTLeafNode";
	else if(kind == BT_INDEX_NODE)
	    result = "kBTIndexNode";
	else if(kind == BT_HEADER_NODE)
	    result = "kBTHeaderNode";
	else if(kind == BT_MAP_NODE)
	    result = "kBTMapNode";
	else
	    result = "UNKNOWN!";
	return result;
    }
    
    public void printFields(PrintStream ps, String prefix) {
	ps.println(prefix + " fLink: " + getFLink() + "");
	ps.println(prefix + " bLink: " + getBLink() + "");
	ps.println(prefix + " kind: " + getKind() + " (" + getKindAsString() + ")");
	ps.println(prefix + " height: " + getHeight() + "");
	ps.println(prefix + " numRecords: " + getNumRecords() + "");
	ps.println(prefix + " reserved: 0x" + Util.toHexStringBE(getReserved()) + "");
    }
    public void print(PrintStream ps, String prefix) {
	ps.println(prefix + "BTNodeDescriptor:");
	printFields(ps, prefix);
    }
}
