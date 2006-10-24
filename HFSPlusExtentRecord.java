import java.io.PrintStream;

public class HFSPlusExtentRecord {
    /*
     * HFSPlusExtentDescriptor (typedef HFSPlusExtentDescriptor[8])
     * size: 64 bytes
     *
     * BP   Size  Type                        Variable name             Description
     * ----------------------------------------------------------------------------
     * 0    8*8   HFSPlusExtentDescriptor[8]  hfsPlusExtentDescriptors
     */
    private final HFSPlusExtentDescriptor[] array = new HFSPlusExtentDescriptor[8];

    public HFSPlusExtentRecord(byte[] data, int offset) {
	for(int i = 0; i < array.length; ++i)
	    array[i] = new HFSPlusExtentDescriptor(data, offset+i*HFSPlusExtentDescriptor.getSize());
    }

    public HFSPlusExtentDescriptor getExtentDescriptor(int index) {
	return array[index];
    }
    public HFSPlusExtentDescriptor[] getExtentDescriptors() {
	HFSPlusExtentDescriptor[] arrayCopy = new HFSPlusExtentDescriptor[array.length];
	for(int i = 0; i < array.length; ++i)
	    arrayCopy[i] = array[i];
	return arrayCopy;
    }
	
    public void print(PrintStream ps, int pregap) {
	String pregapString = "";
	for(int i = 0; i < pregap; ++i)
	    pregapString += " ";
	print(ps, pregapString);
    }
    public void print(PrintStream ps, String prefix) {
	    
	for(int i = 0; i < array.length; ++i) {
	    ps.println(prefix + "array[" + i + "]:");
	    array[i].print(ps, prefix + "  ");
	}
    }
}
