import java.io.PrintStream;

public class OSType {
    /*
     * struct OSType
     * size: 4 bytes
     * description: a typedef originally
     * 
     * BP  Size  Type          Identifier  Description
     * -----------------------------------------------
     * 0   4     FourCharCode  osType                 
     */
    
    private static FourCharCode osType;
    
    public OSType(byte[] data, int offset) {
	osType = new FourCharCode(data, offset);
    }
    
    public FourCharCode getOSType() { return osType; }

    public void printFields(PrintStream ps, String prefix) {
	ps.println(prefix + " osType: ");
	osType.print(ps, prefix+"  ");
    }
    public void print(PrintStream ps, String prefix) {
	ps.println(prefix + "OSType:");
	printFields(ps, prefix);
    }
}
