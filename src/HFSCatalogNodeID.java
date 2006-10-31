import java.io.PrintStream;

public class HFSCatalogNodeID {
    /*
     * HFSCatalogNodeID (typedef UInt32)
     * size: 4 bytes
     *
     * BP   Size  Type              Variable name     Description
     * -----------------------------------------------------------
     * 0    4     UInt32            hfsCatalogNodeID
     */

    private final byte[] hfsCatalogNodeID = new byte[4];
	
    public HFSCatalogNodeID(byte[] data, int offset) {
	System.arraycopy(data, offset, hfsCatalogNodeID, 0, 4);
    }
    public HFSCatalogNodeID(int nodeID) {
	System.arraycopy(Util.toByteArrayBE(nodeID), 0, hfsCatalogNodeID, 0, 4);
    }
    
    public static int length() { return 4; }

    public int toInt() { return Util.readIntBE(hfsCatalogNodeID); }
    public long toLong() { return Util2.unsign(toInt()); }
    public String getDescription() {
	/*
	 * kHFSRootParentID            = 1,
	 * kHFSRootFolderID            = 2,
	 * kHFSExtentsFileID           = 3,
	 * kHFSCatalogFileID           = 4,
	 * kHFSBadBlockFileID          = 5,
	 * kHFSAllocationFileID        = 6,
	 * kHFSStartupFileID           = 7,
	 * kHFSAttributesFileID        = 8,
	 * kHFSRepairCatalogFileID     = 14,
	 * kHFSBogusExtentFileID       = 15,
	 * kHFSFirstUserCatalogNodeID  = 16
	 */
	String result;
	switch(toInt()) {
	case 1:
	    result = "kHFSRootParentID";
	    break;
	case 2:
	    result = "kHFSRootFolderID";
	    break;
	case 3:
	    result = "kHFSExtentsFileID";
	    break;
	case 4:
	    result = "kHFSCatalogFileID";
	    break;
	case 5:
	    result = "kHFSBadBlockFileID";
	    break;
	case 6:
	    result = "kHFSAllocationFileID";
	    break;
	case 7:
	    result = "kHFSStartupFileID";
	    break;
	case 8:
	    result = "kHFSAttributesFileID";
	    break;
	case 14:
	    result = "kHFSRepairCatalogFileID";
	    break;
	case 15:
	    result = "kHFSBogusExtentFileID";
	    break;
	case 16:
	    result = "kHFSFirstUserCatalogNodeID";
	    break;
	default:
	    result = "User Defined ID";
	    break;
	}
	return result;
    }
    public String toString() {
	return "" + Util2.unsign(toInt());// + " (" + getDescription() + ")";
    }
    
    public void printFields(PrintStream ps, String prefix) {
	ps.println(prefix + " hfsCatalogNodeID: " + toString() + " (" + getDescription());
    }
    
    public void print(PrintStream ps, String prefix) {
	ps.println(prefix + "HFSCatalogNodeID:");
	printFields(ps, prefix);
    }
}
    
