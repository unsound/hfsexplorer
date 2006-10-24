public class BTIndexRecord {
    private final BTKey key;
    private final byte[] index = new byte[4];

    public BTIndexRecord(BTKey key, byte[] data, int offset) {
	this.key = key;
	System.arraycopy(data, offset+key.length(), index, 0, 4);
    }
    
    public BTKey getKey() { return key; }
    public int getIndex() { return Util.readIntBE(index); }
}
