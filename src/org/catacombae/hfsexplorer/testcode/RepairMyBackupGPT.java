/*-
 * Copyright (C) 2008 Erik Larsson
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catacombae.hfsexplorer.testcode;

import org.catacombae.io.*;
import org.catacombae.hfsexplorer.*;
import org.catacombae.hfsexplorer.partitioning.*;
import org.catacombae.hfsexplorer.win32.*;
import java.io.*;

/**
 * This application reads the primary GPT header from the specified disk and writes a
 * corresponding backup header at the end of the disk. Useful for when a disk image
 * does not contain the entire length of the drive, due to read errors and such.
 */

public class RepairMyBackupGPT {
    private static BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
    
    public static void main(String[] args) throws Exception {
	long runTimeStamp = System.currentTimeMillis();
	RandomAccessStream llf;
	if(System.getProperty("os.name").toLowerCase().startsWith("windows") &&
	   !new File(args[0]).exists()) {
	    System.out.println("Win32 extended stream loading...");
	    llf = new WritableWin32File(args[0]); // In case we have specified a windows device path
	}
	else
	    llf = new FileStream(args[0]);
	
	final GUIDPartitionTable originalGpt = new GUIDPartitionTable(llf, 0);
	MutableGUIDPartitionTable gpt = new MutableGUIDPartitionTable(originalGpt);

	if(!originalGpt.isValid() && !gpt.isValid()) {
	    final int blockSize = 512;
	    GPTHeader hdr = gpt.getHeader();
	    GPTHeader backupHdr = gpt.getBackupHeader();
	    if(!hdr.isValid()) {
		System.out.println("The GPT primary header is not valid! Exiting...");
		System.exit(-1);
		return;
	    }
	    else if(backupHdr.isValid()) {
		System.out.println("The backup header is already valid. Exiting...");
		System.exit(-1);
		return;
	    }
	    
	    // Backup the GPT backup table at the end of the disk.
	    String backupFilename2 = null;
	    {
		long backupGPTPos = hdr.getBackupLBA()*blockSize - hdr.getNumberOfPartitionEntries()*hdr.getSizeOfPartitionEntry();
		int backupGPTLen = hdr.getNumberOfPartitionEntries()*hdr.getSizeOfPartitionEntry() + blockSize;
		
		byte[] backup2 = new byte[backupGPTLen];
		llf.seek(backupGPTPos);
		int bytesRead = llf.read(backup2);
		if(bytesRead > 0) {
		    backupFilename2 = "gpt_backup_table-" + runTimeStamp + ".backup";
		    System.out.print("Backing up GPT backup header and table to \"" + backupFilename2 + "\"...");
		    FileOutputStream backupFile2 = new FileOutputStream(backupFilename2);
		    backupFile2.write(backup2, 0, bytesRead);
		    backupFile2.close();
		    System.out.println("done!");
		}
		else {
		    System.out.println("Could not read the backup GPT table data area!");
		    String s = "";
		    while(!(s.equals("y") || s.equals("n"))) {
			System.out.print("Continue without backup (y/n)? ");
			s = stdin.readLine();
		    }
		    if(s.equals("n")) {
			System.exit(-1);
			return;
		    }
		}
	    }
	    	
	    // Now let's modify the table in memory
	    
	    System.out.println("Modifying GPT data in memory:");
	    
	    System.out.print("  - Creating a GPT backup header from the primary header...");
	    GPTHeader newBackupHeader = hdr.createValidBackupHeader();
	    System.out.println("done.");
	    
	    System.out.print("  - Setting contents of mutable backup header to newly created backup header...");
	    MutableGPTHeader backupHeader = gpt.getMutableBackupHeader();
	    backupHeader.setFields(newBackupHeader);
	    System.out.println("done.");
	    
	    System.out.print("  - Setting contents of all backup table entries to match primary entries...");
	    MutableGPTEntry[] primaryEntries = gpt.getMutablePrimaryEntries();
	    MutableGPTEntry[] backupEntries = Util.arrayCopy(primaryEntries, new MutableGPTEntry[primaryEntries.length]);
	    gpt.setMutableBackupEntries(backupEntries);
	    System.out.println("done.");
	    
	    // At this point, all data in gpt should have been set to intended values.
	    // Finalizing and checking the resulting table...
	    
	    System.out.print("  - Checking if gpt.isValid() == true as it now should be...");
	    if(!gpt.isValid()) {
		System.out.println("failed! Halting program.");
		System.exit(0);
	    }
	    System.out.println("yes.");
	    
	    // If we have got to this point, the table should be valid and ready to be written to disk!
	    System.out.println("The backup table is now ready to be written down to disk.");
	    
	    System.out.print("Press enter to view the original table:");
	    stdin.readLine();
	    originalGpt.printBackupFields(System.out, "");
	    
	    System.out.print("Press enter to view the modified table:");
	    stdin.readLine();
	    gpt.printBackupFields(System.out, "");
	    
	    System.out.print("If you want to write this table to disk, type \"yes\" here: ");
	    String answer = stdin.readLine();
	    if(answer.equals("yes")) {
		System.out.print("Getting binary data for backup table...");
		byte[] newBackupGPT = gpt.getBackupTableBytes();
		System.out.println("done.");
		
		// Write the new backup GPT data to a file.
		String newdataFilename2 = "gpt_backup_table-" + runTimeStamp + ".new";
		System.out.print("Writing new GPT backup header and table to \"" + newdataFilename2 + "\"...");
		FileOutputStream newdataFile2 = new FileOutputStream(newdataFilename2);
		newdataFile2.write(newBackupGPT);
		newdataFile2.close();
		System.out.println("done!");
		
		// Write to disk! Dangerous stuff...
		
		System.out.print("Writing backup table...");
		long backupTableLocation = gpt.getBackupTableBytesOffset();
		int backupTableSize = newBackupGPT.length;
		long targetLength = backupTableLocation + backupTableSize;
		if(targetLength > llf.length()) {
		    System.out.println("The target storage size is reported to be");
		    System.out.println(llf.length() + " bytes, which is smaller than what the GPT specifies");
		    System.out.println("(" + (backupTableLocation+backupTableSize) + " bytes).");
		    System.out.println("This may be because you are accessing a block device, or an incomplete disk");
		    System.out.println("image. If you proceed, the disk image file, if any, may be extended, or the");
		    System.out.println("write will fail.");
		    String s = "";
		    while(!(s.equals("y") || s.equals("n"))) {
			System.out.print("Proceed anyway (y/n)? ");
			s = stdin.readLine();
		    }
		    if(s.equals("n")) {
			System.exit(-1);
			return;
		    }
		    else if(llf instanceof TruncatableRandomAccess) {
			System.out.println("Setting length of file to: " + targetLength);
			((TruncatableRandomAccess)llf).setLength(targetLength);
		    }
		}
		else if(targetLength < llf.length()) {
		    System.out.println("Warning: The backup table is not situated at the end of the target storage.");
		    String s = "";
		    while(!(s.equals("y") || s.equals("n"))) {
			System.out.print("Proceed anyway (y/n)? ");
			s = stdin.readLine();
		    }
		    if(s.equals("n")) {
			System.exit(-1);
			return;
		    }
		}
		
		System.out.print("Seeking to " + backupTableLocation + "...");
		llf.seek(backupTableLocation);
		System.out.println("done!");
		System.out.print("Writing backup GPT data...");
		llf.write(newBackupGPT);
		System.out.println("done!");
		
		// Check to see if we have succeeded.
		System.out.println();
		System.out.println("Checking the newly written GPT...");
		GUIDPartitionTable newGpt = new GUIDPartitionTable(llf, 0);
		newGpt.print(System.out, "");
		if(newGpt.isValid())
		    System.out.println("The GPT on disk is valid!");
		else {
		    System.out.println("INVALID GPT ON DISK! FATAL ERROR!");
		    System.out.println("Try to restore the original GPT tables from the backup files:");
		    if(backupFilename2 != null)
			System.out.println("  " + backupFilename2);
		    System.out.println("(dd in linux can probably do the job)");
		}
	    }
	    else
		System.out.println("Exiting program without modifying anything.");
	    
	}
	else
	    System.out.println("The GUID Partition Table on disk seems to be valid. No changes will be made.");
	llf.close();
    }
    
}
