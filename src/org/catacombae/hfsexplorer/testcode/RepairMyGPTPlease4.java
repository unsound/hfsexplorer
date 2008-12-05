/*-
 * Copyright (C) 2007 Erik Larsson
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
import org.catacombae.jparted.lib.ps.gpt.GPTPartitionType;

/**
 * This class was specifically written to repair my hard disk GPT table, which had become inconsistent with
 * the MBR table.
 * <pre>
 * When I connected my drive to Windows and reformatted my HFS+ partition as NTFS, only the MBR entry was
 * changed by Windows (due to 32-bit Windows XP being incapable of dealing with GPT drives). So I had an
 * inconsistent partition table, meaning Windows XP could recognize the NTFS partition, but OS X saw it as a
 * faulty HFS partition.
 * Instead of deleting the partition in OS X and recreate it as NTFS, which I couldn't do at the time as I had
 * managed to put some important data on that partition, I decided to write a program to change the "partition
 * type" entry in the GPT entry for my partition. This was easier said than done, since GPT has two similar
 * headers on the disk, but with different contents, and both with different checksums.
 * 
 * Finally though, I figured it all out, and at the first attempt of running the complete version of this
 * program, it did its job! (If it hadn't, I might have been left with a corrupt GPT table, so it was very
 * importand that it worked out of the box, which is why I do so many checks during the process, and backs up
 * a lot of data)
 *
 * So what this program does is:
 * - Read the current GPT data into memory.
 * - Change the partition type for the second partition (index 1) to "Microsoft basic data", which is the type
 *   for Windows partitions (NTFS, FAT32(?)).
 * - Update all checksums so that the GPT table is valid.
 * - Write the modified GPT data back to disk.
 * 
 * I ran it on a Windows XP SP2 (x86) system, with the partitions mounted. Since I knew Windows wouldn't bother
 * about the GPT data, I thought it would be safe, and it was. (A little strange that Windows actually allows
 * one to write individual sectors of data to the raw disk while it is being used, but it's nice that it
 * worked...)
 * 
 * Command line: test.bat testcode.RepairMyGPTPlease \\?\GLOBALROOT\Device\Harddisk2\Partition0
 * (test.bat just says java -cp lib\hfsx.jar org.catacombae.hfsexplorer.%1 %2 %3 etc.)
 * 
 * </pre>
 */

public class RepairMyGPTPlease4 {
    private static BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
    public static void main(String[] args) throws Exception {
	long runTimeStamp = System.currentTimeMillis();
	RandomAccessStream llf;
	if(System.getProperty("os.name").toLowerCase().startsWith("windows"))
	    llf = new WritableWin32File(args[0]);
	else
	    llf = new FileStream(args[0]);
	
	final GUIDPartitionTable originalGpt = new GUIDPartitionTable(llf, 0);
	MutableGUIDPartitionTable gpt = new MutableGUIDPartitionTable(originalGpt);

	if(originalGpt.isValid() && gpt.isValid()) {
	    final int blockSize = 512;
	    GPTHeader hdr = gpt.getHeader();
	    
	    // Backup the entire partition table part of the disk, in case something goes wrong
	    // First the MBR and GPT tables at the beginning of the disk.
	    final byte[] mbr = new byte[blockSize];
	    byte[] backup1 = new byte[blockSize + hdr.getNumberOfPartitionEntries()*hdr.getSizeOfPartitionEntry()];
	    llf.seek(0);
	    llf.readFully(mbr);
	    llf.readFully(backup1);
	    String backupFilename1 = "gpt_mbr_tables-" + runTimeStamp + ".backup";
	    System.out.print("Backing up MBR and GPT primary header and table to \"" + backupFilename1 + "\"...");
	    FileOutputStream backupFile1 = new FileOutputStream(backupFilename1);
	    backupFile1.write(mbr);
	    backupFile1.write(backup1);
	    backupFile1.close();
	    System.out.println("done!");
	    
	    // Then the backup GPT table at the end of the disk.
	    byte[] backup2 = new byte[hdr.getNumberOfPartitionEntries()*hdr.getSizeOfPartitionEntry() + blockSize];
	    llf.seek(hdr.getBackupLBA()*blockSize - hdr.getNumberOfPartitionEntries()*hdr.getSizeOfPartitionEntry());
	    llf.read(backup2);
	    String backupFilename2 = "gpt_backup_table-" + runTimeStamp + ".backup";
	    System.out.print("Backing up GPT backup header and table to \"" + backupFilename2 + "\"...");
	    FileOutputStream backupFile2 = new FileOutputStream(backupFilename2);
	    backupFile2.write(backup2);
	    backupFile2.close();
	    System.out.println("done!");
	    	
	    /* Now we want to change the partition type for the second partition from:
	     *   Hierarchical File System (HFS+) partition  {48465300-0000-11AA-AA11-00306543ECAC}
	     * to:
	     *   Basic Data Partition                       {EBD0A0A2-B9E5-4433-87C0-68B6B72699C7}
	     */
	    byte[] efiSystemPartitionType = GPTPartitionType.PARTITION_TYPE_EFI_SYSTEM.getBytes();
	    byte[] microsoftBasicDataType = GPTPartitionType.PARTITION_TYPE_PRIMARY_PARTITION.getBytes();
	    byte[] appleHfsType = GPTPartitionType.PARTITION_TYPE_APPLE_HFS.getBytes();
	    //byte[] microsoftReservedType = GPTEntry.GPTPartitionType.PARTITION_TYPE_MICROSOFT_RESERVED.getBytes();
	    
	    // First we verify that the current data is as we expect it to be.
	    
	    System.out.print("Checking if the first partition has type \"EFI System Partition\"...");
	    byte[] currentType1 = gpt.getEntry(0).getPartitionTypeGUID();
	    if(!Util.arraysEqual(currentType1, efiSystemPartitionType)) {
		System.out.println("failed! Halting program.");
		System.exit(0);
	    }
	    System.out.println("yes.");
	    
	    System.out.print("Checking if the second partition has type \"Apple HFS\"...");
	    byte[] currentType2 = gpt.getEntry(1).getPartitionTypeGUID();
	    if(!Util.arraysEqual(currentType2, appleHfsType)) {
		System.out.println("failed! Halting program.");
		System.exit(0);
	    }
	    System.out.println("yes.");
	    
	    System.out.print("Checking if the third partition has type \"Microsoft Basic Data\"...");
	    byte[] currentType3 = gpt.getEntry(2).getPartitionTypeGUID();
	    if(!Util.arraysEqual(currentType3, microsoftBasicDataType)) {
		System.out.println("failed! Halting program.");
		System.exit(0);
	    }
	    System.out.println("yes.");
	    
	    System.out.print("Checking if the fourth partition has type \"Microsoft Basic Data\"...");
	    byte[] currentType4 = gpt.getEntry(3).getPartitionTypeGUID();
	    if(!Util.arraysEqual(currentType4, microsoftBasicDataType)) {
		System.out.println("failed! Halting program.");
		System.exit(0);
	    }
	    System.out.println("yes.");
	    
	    System.out.println("All seems to be as expected.");
	    
	    // Now let's modify the table in memory
	    
	    System.out.println("Modifying GPT data in memory:");
	    
	    System.out.print("  - Swapping third and fourth partition...");
	    MutableGPTEntry modifiedPrimaryEntry3 = gpt.getMutablePrimaryEntry(2);
	    MutableGPTEntry modifiedBackupEntry3 = gpt.getMutableBackupEntry(2);
	    MutableGPTEntry modifiedPrimaryEntry4 = gpt.getMutablePrimaryEntry(3);
	    MutableGPTEntry modifiedBackupEntry4 = gpt.getMutableBackupEntry(3);
	    
	    GPTEntry tempPrimaryEntry = new GPTEntry(modifiedPrimaryEntry3);
	    GPTEntry tempBackupEntry = new GPTEntry(modifiedBackupEntry3);	
	    modifiedPrimaryEntry3.setFields(modifiedPrimaryEntry4);
	    modifiedBackupEntry3.setFields(modifiedBackupEntry4);
	    modifiedPrimaryEntry4.setFields(tempPrimaryEntry);
	    modifiedBackupEntry4.setFields(tempBackupEntry);
	    System.out.println("done.");
	    
	    MutableGPTHeader primaryHeader = gpt.getMutablePrimaryHeader();
	    MutableGPTHeader backupHeader = gpt.getMutableBackupHeader();
	    System.out.print("  - Checking if calculated entries checksums match...");
	    int entriesChecksum1 = gpt.calculatePrimaryEntriesChecksum();
	    int entriesChecksum2 = gpt.calculateBackupEntriesChecksum();
	    if(entriesChecksum1 != entriesChecksum2) {
		System.out.println("failed! Halting program.");
		System.exit(0);
	    }
	    System.out.println("yes.");
	    
	    primaryHeader.setPartitionEntryArrayCRC32(entriesChecksum1);
	    backupHeader.setPartitionEntryArrayCRC32(entriesChecksum1);
	    
	    System.out.print("  - Checking if gpt.isValid() == false as it should be...");
	    if(gpt.isValid()) {
		System.out.println("failed! Halting program.");
		System.exit(0);
	    }
	    System.out.println("yes.");
	    
	    System.out.print("  - Calculating header checksums...");
	    primaryHeader.setCRC32Checksum(gpt.calculatePrimaryHeaderChecksum());
	    backupHeader.setCRC32Checksum(gpt.calculateBackupHeaderChecksum());
	    System.out.println("done.");
	    
	    System.out.print("  - Checking if gpt.isValid() == true as it now should be...");
	    if(!gpt.isValid()) {
		System.out.println("failed! Halting program.");
		System.exit(0);
	    }
	    System.out.println("yes.");
	    
	    // If we have got to this point, the table should be valid and ready to be written to disk!
	    System.out.println("The table is now ready to be written down to disk.");
	    
	    System.out.print("Press enter to view the original table:");
	    stdin.readLine();
	    originalGpt.print(System.out, "");
	    
	    System.out.print("Press enter to view the modified table:");
	    stdin.readLine();
	    gpt.print(System.out, "");
	    
	    System.out.print("If you want to write this table to disk, type \"yes\" here: ");
	    String answer = stdin.readLine();
	    if(answer.equals("yes")) {
		System.out.print("Getting binary data for primary and backup tables...");
		byte[] newPrimaryGPT = gpt.getPrimaryTableBytes();
		byte[] newBackupGPT = gpt.getBackupTableBytes();
		System.out.println("done.");
		
		// Write the MBR + the new primary GPT data to a file.
		String newdataFilename1 = "gpt_mbr_tables-" + runTimeStamp + ".new";
		System.out.print("Writing old MBR and new GPT primary header and table to \"" + newdataFilename1 + "\"...");
		FileOutputStream newdataFile1 = new FileOutputStream(newdataFilename1);
		newdataFile1.write(mbr);
		newdataFile1.write(newPrimaryGPT);
		newdataFile1.close();
		System.out.println("done!");
	    
		// Write the new backup GPT data to a file.
		String newdataFilename2 = "gpt_backup_table-" + runTimeStamp + ".new";
		System.out.print("Writing new GPT backup header and table to \"" + newdataFilename2 + "\"...");
		FileOutputStream newdataFile2 = new FileOutputStream(newdataFilename2);
		newdataFile2.write(newBackupGPT);
		newdataFile2.close();
		System.out.println("done!");
		
		// Write to disk! Dangerous stuff...
		System.out.print("Writing primary table...");
		llf.seek(gpt.getPrimaryTableBytesOffset());
		llf.write(newPrimaryGPT);
		System.out.println("done!");
		
		System.out.print("Writing backup table...");
		llf.seek(gpt.getBackupTableBytesOffset());
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
		    System.out.println("Try to restore the original GPT tables from the backups files:");
		    System.out.println("  " + backupFilename1);
		    System.out.println("  " + backupFilename2);
		    System.out.println("(dd in linux might do the job)");
		}
	    }
	    else
		System.out.println("Exiting program without modifying anything.");
	    
	}
	else
	    System.out.println("Could not proceed! Detected an invalid GUID Partition Table on disk.");
	llf.close();
    }
    
}
