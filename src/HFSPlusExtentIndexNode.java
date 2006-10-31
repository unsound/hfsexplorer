public class HFSPlusExtentIndexNode extends BTIndexNode {
    public HFSPlusExtentIndexNode(byte[] data, int offset, int nodeSize) {
	super(data, offset, nodeSize);
	
	// Populate record list
	// we loop offsets.length-1 times, since last offset is offset to free space
	for(int i = 0; i < records.length; ++i) {
	    int currentOffset = Util2.unsign(offsets[i]);
	    HFSPlusExtentKey currentKey = new HFSPlusExtentKey(data, offset+currentOffset);
	    records[i] = new BTIndexRecord(currentKey, data, offset+currentOffset);
	}
    }
    //public static 
}
