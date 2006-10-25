public class HFSPlusCatalogIndexNode extends BTIndexNode {
    public HFSPlusCatalogIndexNode(byte[] data, int offset, int nodeSize) {
	super(data, offset, nodeSize);
	
	// Populate record list
	// we loop offsets.length-1 times, since last offset is offset to free space
	for(int i = 0; i < records.length; ++i) {
	    int currentOffset = Util2.unsign(offsets[i]);
	    HFSPlusCatalogKey currentKey = new HFSPlusCatalogKey(data, offset+currentOffset);
	    records[i] = new BTIndexRecord(currentKey, data, offset+currentOffset);
	}
    }
    //public static 
}
