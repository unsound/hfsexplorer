/** This class is taylored to repair my hard disk, whose GPT table has become inconsistent with the
    MBR... */

package org.catacombae.hfsexplorer.testcode;
import org.catacombae.hfsexplorer.*;
import org.catacombae.hfsexplorer.partitioning.*;
import org.catacombae.hfsexplorer.win32.*;
import java.io.*;

public class RepairMyGPTPlease {
    public static void main(String[] args) throws Exception {
	if(!System.getProperty("os.name").toLowerCase().startsWith("windows")) {
	    System.out.println("This program is Windows only. Your OS: " + System.getProperty("os.name"));
	}
	else {
	    long runTimeStamp = System.currentTimeMillis();
	    LowLevelFile llf = new WritableWin32File(args[0]);
	    
	    MutableGUIDPartitionTable gpt = new MutableGUIDPartitionTable(new GUIDPartitionTable(llf, 0));
	    
	    // Backup the entire partition table part of the disk, in case something goes wrong
	    // First the MBR and GPT tables at the beginning of the disk.
	    byte[] backup1 = new byte[512+16896];
	    llf.seek(0);
	    llf.readFully(backup1);
	    FileOutputStream backupFile1 = new FileOutputStream("gpt_mbr_tables-" + runTimeStamp + ".backup");
	    backupFile1.write(backup1);
	    backupFile1.close();
	    
	    // Then the backup GPT table at the end of the disk.
	    int blockSize = 512;
	    GPTHeader hdr = gpt.getHeader();
	    byte[] backup2 = new byte[hdr.getNumberOfPartitionEntries()*hdr.getSizeOfPartitionEntry() + blockSize];
	    llf.seek(hdr.getBackupLBA()*blockSize - hdr.getNumberOfPartitionEntries()*hdr.getSizeOfPartitionEntry());
	    llf.read(backup2);
	    FileOutputStream backupFile2 = new FileOutputStream("gpt_backup_table-" + runTimeStamp + ".backup");
	    backupFile2.write(backup2);
	    backupFile2.close();
	    
	    llf.close();
	}
    }
    
}
