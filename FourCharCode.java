import java.io.PrintStream;

public class FourCharCode {
    /*
     * struct FourCharCode
     * size: 4 bytes
     * description: a typedef originally
     * 
     * BP  Size  Type    Identifier    Description
     * -------------------------------------------
     * 0   4     UInt32  fourCharCode             
     */
    
    private final byte[] fourCharCode = new byte[4];
    
    public FourCharCode(byte[] data, int offset) {
	System.arraycopy(data, offset+0, fourCharCode, 0, 4);
    }

    public int getFourCharCode() { return Util.readIntBE(fourCharCode); }
    
    public String getFourCharCodeAsString() { return Util2.toASCIIString(getFourCharCode()); }

    public void printFields(PrintStream ps, String prefix) {
	ps.println(prefix + " fourCharCode: \"" + getFourCharCodeAsString() + "\"");
    }
    public void print(PrintStream ps, String prefix) {
	ps.println(prefix + "FourCharCode:");
	printFields(ps, prefix);
    }
}
