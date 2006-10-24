import java.io.PrintStream;

public class HFSPlusCatalogLeafRecord {
    public static final int HFS_PLUS_FOLDER_RECORD = 0x0001;
    public static final int HFS_PLUS_FILE_RECORD = 0x0002;
    public static final int HFS_PLUS_FOLDER_THREAD_RECORD = 0x0003;
    public static final int HFS_PLUS_FILE_THREAD_RECORD = 0x0004;
    
    private HFSPlusCatalogKey key;
    private HFSPlusCatalogLeafRecordData recordData;
    
    public HFSPlusCatalogLeafRecord(byte[] data, int offset) {
	key = new HFSPlusCatalogKey(data, offset);
	short recordType = Util.readShortBE(data, offset+key.length());
	if(recordType == HFS_PLUS_FOLDER_RECORD)
	    recordData = new HFSPlusCatalogFolder(data, offset+key.length());
	else if(recordType == HFS_PLUS_FILE_RECORD)
	    recordData = new HFSPlusCatalogFile(data, offset+key.length());
	else if(recordType == HFS_PLUS_FOLDER_THREAD_RECORD)
	    recordData = new HFSPlusCatalogThread(data, offset+key.length());
	else if(recordType == HFS_PLUS_FILE_THREAD_RECORD)
	    recordData = new HFSPlusCatalogThread(data, offset+key.length());
	else
	    throw new RuntimeException("Ivalid record type!");
    }
    
    public HFSPlusCatalogKey getKey() { return key; }
    public HFSPlusCatalogLeafRecordData getData() { return recordData; }
    
    public void printFields(PrintStream ps, String prefix) {
	ps.println(prefix + " key:");
	key.printFields(ps, prefix + "  ");
	ps.println(prefix + " recordData:");
	recordData.printFields(ps, prefix + "  ");
    }
    public void print(PrintStream ps, String prefix) {
	ps.println(prefix + "HFSPlusCatalogLeafRecord:");
	printFields(ps, prefix);
    }
}
