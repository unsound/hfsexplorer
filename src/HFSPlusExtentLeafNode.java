public class HFSPlusExtentLeafNode {
    protected BTNodeDescriptor nodeDescriptor;
    protected HFSPlusExtentLeafRecord[] leafRecords;
    
    public HFSPlusExtentLeafNode(byte[] data, int offset, int nodeSize) {
	nodeDescriptor = new BTNodeDescriptor(data, offset);
	short[] offsets = new short[Util2.unsign(nodeDescriptor.getNumRecords())];
	for(int i = 0; i < offsets.length; ++i) {
	    offsets[i] = Util.readShortBE(data, offset+nodeSize-((i+1)*2));
	}
	leafRecords = new HFSPlusExtentLeafRecord[offsets.length-1];
	// we loop offsets.length-1 times, since last offset is offset to free space
	for(int i = 0; i < leafRecords.length; ++i) {
	    int currentOffset = Util2.unsign(offsets[i]);
	    leafRecords[i] = new HFSPlusExtentLeafRecord(data, offset+currentOffset);
	}
	
    }
    
    public BTNodeDescriptor getNodeDescriptor() { return nodeDescriptor; }
    public HFSPlusExtentLeafRecord getLeafRecord(int index) { return leafRecords[index]; }
    public HFSPlusExtentLeafRecord[] getLeafRecords() {
	HFSPlusExtentLeafRecord[] copy = new HFSPlusExtentLeafRecord[leafRecords.length];
	for(int i = 0; i < copy.length; ++i)
	    copy[i] = leafRecords[i];
	return copy;
    }
}
