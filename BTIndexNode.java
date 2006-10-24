public abstract class BTIndexNode {
    protected final BTNodeDescriptor nodeDescriptor;
    protected final BTIndexRecord[] records;
    protected final short[] offsets;
    
    protected BTIndexNode(byte[] data, int offset, int nodeSize) {
	nodeDescriptor = new BTNodeDescriptor(data, offset);
	offsets = new short[Util2.unsign(nodeDescriptor.getNumRecords())];
	for(int i = 0; i < offsets.length; ++i) {
	    offsets[i] = Util.readShortBE(data, offset+nodeSize-((i+1)*2));
	}
	records = new BTIndexRecord[offsets.length-1];
    }

    public BTNodeDescriptor getNodeDescriptor() { return nodeDescriptor; }
    public BTIndexRecord getIndexRecord(int index) { return records[index]; }
    public BTIndexRecord[] getIndexRecords() {
	BTIndexRecord[] copy = new BTIndexRecord[records.length];
	for(int i = 0; i < copy.length; ++i)
	    copy[i] = records[i];
	return copy;
    }
}
