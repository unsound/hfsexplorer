public abstract class BTKey implements Comparable<BTKey> {
    public abstract short getKeyLength();
    public abstract int length();
    public int compareTo(BTKey btk) {
	byte[] thisData = getData();
	byte[] thatData = btk.getData();
	for(int i = 0; i < Math.min(thisData.length, thatData.length); ++i) {
	    if(thisData[i] < thatData[i])
		return -1;
	    else if(thisData[i] > thatData[i])
		return 1;
	}
	if(thisData.length < thatData.length)
	    return -1;
	else if(thisData.length > thatData.length)
	    return 1;
	else
	    return 0;
    }
    public abstract byte[] getData();
}
