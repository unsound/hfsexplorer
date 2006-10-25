import java.io.PrintStream;

public abstract class HFSPlusCatalogLeafRecordData {
    public static final int RECORD_TYPE_FOLDER = 0x0001;
    public static final int RECORD_TYPE_FILE = 0x0002;
    public static final int RECORD_TYPE_FOLDER_THREAD = 0x0003;
    public static final int RECORD_TYPE_FILE_THREAD = 0x0004;

    public abstract short getRecordType();
    public String getRecordTypeAsString() {
	int recordType = Util2.unsign(getRecordType());
	if(recordType == RECORD_TYPE_FOLDER) return "kHFSPlusFolderRecord";
	else if(recordType == RECORD_TYPE_FILE) return "kHFSPlusFileRecord";
	else if(recordType == RECORD_TYPE_FOLDER_THREAD) return "kHFSPlusFolderThreadRecord";
	else if(recordType == RECORD_TYPE_FILE_THREAD) return "kHFSPlusFileThreadRecord";
	else return "UNKNOWN!";
    }
    public abstract void printFields(PrintStream ps, String prefix);
    public abstract void print(PrintStream ps, String prefix);
}
