package org.catacombae.hfsexplorer.testcode;
import org.catacombae.hfsexplorer.*;
import org.catacombae.hfsexplorer.partitioning.*;
import org.catacombae.hfsexplorer.win32.*;
import java.io.*;

public class PrintGPTPartitions {
    public static void main(String[] args) throws Exception {
	LowLevelFile llf;
	if(System.getProperty("os.name").toLowerCase().startsWith("windows")) {
	    llf = new WritableWin32File(args[0]);
	}
	else {
	    llf = new WritableRandomAccessLLF(args[0]);
	}
	byte[] referencetable = new byte[16896];
	llf.seek(512);
	llf.readFully(referencetable);
	FileOutputStream refFile = new FileOutputStream("gpt_table_ref.debug");
	refFile.write(referencetable);
	refFile.close();
	System.out.println("Wrote the raw GPT table to file: gpt_table_ref.debug");
	    
	System.out.println("Length of file: " + llf.length());
	GUIDPartitionTable gpt = new GUIDPartitionTable(llf, 0);
	System.out.println("Calculated checksum for header: 0x" + Util.toHexStringBE(gpt.calculateHeaderChecksum()));
	System.out.println("Calculated checksum for entries: 0x" + Util.toHexStringBE(gpt.calculateEntriesChecksum()));
	gpt.print(System.out, "");
	System.out.println("Is this parititon table valid? " + gpt.isValid() + ".");
	FileOutputStream debug = new FileOutputStream("gpt_table.debug");
	debug.write(gpt.getPrimaryTableBytes());
	debug.close();
	System.out.println("Wrote the leading GPT table to file: gpt_table.debug");
	    
	llf.close();
	
    }
    
}
