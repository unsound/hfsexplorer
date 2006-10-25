public abstract class BTKey implements Comparable<BTKey> {
    public abstract short getKeyLength();
    public abstract int length();
    public abstract int compareTo(BTKey btk);
    public abstract byte[] getData();
}
