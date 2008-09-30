/*-
 * Copyright (C) 2006-2007 Erik Larsson
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

package org.catacombae.hfsexplorer.partitioning;

import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.hfsexplorer.*;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.zip.CRC32;
import org.catacombae.csjc.StructElements;
import org.catacombae.csjc.structelements.ArrayBuilder;
import org.catacombae.csjc.structelements.Dictionary;
import org.catacombae.csjc.structelements.DictionaryBuilder;

/**
 * Notes about the structure of GPT:
 *   The backup GPT header in the end of the partition is NOT identical to the main
 *   header, as would be implied by the word "backup". The backup header considers
 *   itself to be the main header (fields primaryLBA and backupLBA are swapped).
 *   Furthermore, the field partitionEntryLBA points to the backup partition array,
 *   instead of the main partition array at LBA 2.
 *   This also implies that the field crc32Checksum is different, as it is a checksum
 *   of the fields in the header (except for itself, which is regarded as zeroed in
 *   the calculation).
 *   So: crc32Checksum, primaryLBA, backupLBA and partitionEntryLBA will differ.
 *   The two partition arrays though, are supposed to be completely identical.
 */
public class GUIDPartitionTable implements PartitionSystem, StructElements {
    private static final int BLOCK_SIZE = 512; // carved in stone!
    protected GPTHeader header;
    protected GPTEntry[] entries;
    
    protected GPTEntry[] backupEntries;
    protected GPTHeader backupHeader;
    
    /** Method-private to getUsedPartitionEntries(). */
    private final LinkedList<GPTEntry> tempList = new LinkedList<GPTEntry>();
    
    public GUIDPartitionTable(ReadableRandomAccessStream llf, int offset) {
	// Common temporary storage variables
	byte[] headerData = new byte[512];
	byte[] currentEntryData = new byte[128];
	
	// 1. Read the primary header and table
	llf.seek(offset+BLOCK_SIZE); // BLOCK_SIZE is added to skip the first block, the (dummy) MBR
	llf.readFully(headerData);
	this.header = new GPTHeader(headerData, 0, BLOCK_SIZE);
	
	if(header.isValid()) { // Before we use any values from the header, we must check its validity
	    llf.seek(offset + header.getPartitionEntryLBA()*BLOCK_SIZE);
	    this.entries = new GPTEntry[header.getNumberOfPartitionEntries()];
	    for(int i = 0; i < entries.length; ++i) {
		llf.readFully(currentEntryData);
		entries[i] = new GPTEntry(currentEntryData, 0, BLOCK_SIZE);
	    }

	    // 2. Read the backup header and table
            GPTHeader tBackupHeader;
            GPTEntry[] tBackupEntries;
	    try {
		llf.seek(offset+BLOCK_SIZE*header.getBackupLBA());
		llf.readFully(headerData);
		tBackupHeader = new GPTHeader(headerData, 0, BLOCK_SIZE);
		
		if(tBackupHeader.isValid()) { // Before we use any values from the backup header, we must check its validity
		    llf.seek(offset + tBackupHeader.getPartitionEntryLBA()*BLOCK_SIZE);
		    tBackupEntries = new GPTEntry[tBackupHeader.getNumberOfPartitionEntries()];
		    for(int i = 0; i < tBackupEntries.length; ++i) {
			llf.readFully(currentEntryData);
			tBackupEntries[i] = new GPTEntry(currentEntryData, 0, BLOCK_SIZE);
		    }
		}
		else
		    tBackupEntries = new GPTEntry[0];
	    } catch(Exception e) {
		tBackupHeader = new GPTHeader(new byte[GPTHeader.getSize()], 0, BLOCK_SIZE);
		tBackupEntries = new GPTEntry[0];
	    }
            this.backupHeader = tBackupHeader;
            this.backupEntries = tBackupEntries;
 	}
	else {
	    // If header is invalid, we don't attempt to read any partitions, and place dummy values as members
	    // Note: Make sure that the dummy values won't evaluate as "valid" (would be very unlikely but...)
	    this.entries = new GPTEntry[0];
	    this.backupHeader = new GPTHeader(new byte[GPTHeader.getSize()], 0, BLOCK_SIZE);
	    this.backupEntries = new GPTEntry[0];
	}
	
    }
    protected GUIDPartitionTable(GUIDPartitionTable source) {
	this.header = new GPTHeader(source.header);
	this.entries = new GPTEntry[source.entries.length];
	for(int i = 0; i < this.entries.length; ++i)
	    this.entries[i] = new GPTEntry(source.entries[i]);
	
	this.backupHeader = new GPTHeader(source.backupHeader);
	this.backupEntries = new GPTEntry[source.backupEntries.length];
	for(int i = 0; i < this.backupEntries.length; ++i)
	    this.backupEntries[i] = new GPTEntry(source.backupEntries[i]);
    }
    
    protected GUIDPartitionTable(GPTHeader header, GPTHeader backupHeader, int numberOfPrimaryEntries, int numberOfBackupEntries/*, int headerChecksum, int entriesChecksum*/) {
	this.header = header;
	this.backupHeader = backupHeader;
	this.entries = new GPTEntry[numberOfPrimaryEntries];
	this.backupEntries = new GPTEntry[numberOfBackupEntries];
   }
    
    public GPTHeader getHeader() {
	return header;
    }
    
    public GPTHeader getBackupHeader() {
	return backupHeader;
    }
    
    public GPTEntry getEntry(int index) {
	return entries[index];
    }
    public GPTEntry[] getEntries() {
	return Util.arrayCopy(entries, new GPTEntry[entries.length]);
    }
    
    public GPTEntry getBackupEntry(int index) {
	return backupEntries[index];
    }
    
    public GPTEntry[] getBackupEntries() {
	return Util.arrayCopy(backupEntries, new GPTEntry[backupEntries.length]);
    }
    
    public int getPartitionCount() {
        return entries.length;
    }
    public int getUsedPartitionCount() {
	int count = 0;
	for(GPTEntry ge : entries) {
	    if(ge.isUsed()) ++count;
	}
	return count;
    }
    public Partition getPartitionEntry(int index) { return getEntry(index); }
    public Partition[] getPartitionEntries() {
	return getEntries();
    }
    /** Returns only those partition entries that are non-null. */
    public Partition[] getUsedPartitionEntries() {
	tempList.clear();
	for(GPTEntry ge : entries) {
	    if(ge.isUsed()) tempList.addLast(ge);
	}
	return tempList.toArray(new GPTEntry[tempList.size()]);
    }
    
    /** Checks the validity of GUID Partition Table data. Don't call this method too often,
	because it does some allocations and wastes some CPU cycles due to lazy implementation. */
    public boolean isValid() {
	boolean primaryTableValid =
	    header.isValid() && 
	    (header.getCRC32Checksum() == calculatePrimaryHeaderChecksum()) &&
	    (header.getPartitionEntryArrayCRC32() == calculatePrimaryEntriesChecksum());
	
	boolean backupTableValid =
	    backupHeader.isValid() && 
	    (backupHeader.getCRC32Checksum() == calculateBackupHeaderChecksum()) &&
	    (backupHeader.getPartitionEntryArrayCRC32() == calculateBackupEntriesChecksum());
	
	boolean entryTablesEqual = true;
	if(backupEntries.length != entries.length)
	    entryTablesEqual = false;
	else {
	    for(int i = 0; i < entries.length; ++i) {
		if(!entries[i].equals(backupEntries[i])) {
		    entryTablesEqual = false;
		    break;
		}
	    }
	}
	
	boolean validBackupHeader =
	    header.isValidBackup(backupHeader) &&
	    backupHeader.isValidBackup(header);
	
	return primaryTableValid && backupTableValid && entryTablesEqual && validBackupHeader;
    }
    
    public int calculatePrimaryHeaderChecksum() {
	return header.calculateCRC32();
    }
    public int calculatePrimaryEntriesChecksum() {
	CRC32 checksum = new CRC32();
	for(GPTEntry entry : entries)
	    checksum.update(entry.getBytes());
	return (int)(checksum.getValue() & 0xFFFFFFFF);
    }
    public int calculateBackupHeaderChecksum() {
	return backupHeader.calculateCRC32();
    }
    public int calculateBackupEntriesChecksum() {
	CRC32 checksum = new CRC32();
	for(GPTEntry entry : backupEntries)
	    checksum.update(entry.getBytes());
	return (int)(checksum.getValue() & 0xFFFFFFFF);
    }
    
    public String getLongName() { return "GUID Partition Table"; }
    public String getShortName() { return "GPT"; }

    public void printFields(PrintStream ps, String prefix) {
	printPrimaryFields(ps, prefix);
	printBackupFields(ps, prefix);
    }
    
    public void printPrimaryFields(PrintStream ps, String prefix) {
	ps.println(prefix + " header:");
	header.print(ps, prefix + "  ");
	for(int i = 0; i < entries.length; ++i) {
	    if(entries[i].isUsed()) {
		ps.println(prefix + " entries[" + i + "]:");
		entries[i].print(ps, prefix + "  ");
	    }
	}
    }
    
    public void printBackupFields(PrintStream ps, String prefix) {
	for(int i = 0; i < backupEntries.length; ++i) {
	    if(backupEntries[i].isUsed()) {
		ps.println(prefix + " backupEntries[" + i + "]:");
		backupEntries[i].print(ps, prefix + "  ");
	    }
	}
	ps.println(prefix + " backupHeader:");
	backupHeader.print(ps, prefix + "  ");
    }
    
    public void print(PrintStream ps, String prefix) {
	ps.println(prefix + "GUIDPartitionTable:");
	printFields(ps, prefix);
    }
    
    public long getPrimaryTableBytesOffset() { return 512; }
    public long getBackupTableBytesOffset() { return backupHeader.getPartitionEntryLBA()*BLOCK_SIZE; }
   
    /** Returns the data in the main table of the disk. (LBA1-LBAx) */
    public byte[] getPrimaryTableBytes() {
// 	if(BLOCK_SIZE+GPTHeader.getSize() != BLOCK_SIZE*header.getPartitionEntryLBA())
// 	    throw new RuntimeException("Primary header and primary entries are not coherent!");
	
	int offset = 0;
	byte[] result = new byte[GPTHeader.getSize() + GPTEntry.getSize()*entries.length];
	byte[] headerData = header.getBytes();
	System.arraycopy(headerData, 0, result, offset, GPTHeader.getSize());
	offset += GPTHeader.getSize();
	
	for(GPTEntry ge : entries) {
	    byte[] entryData = ge.getBytes();
	    System.arraycopy(entryData, 0, result, offset, entryData.length);
	    offset += GPTEntry.getSize();
	}
	
	return result;
    }
    public byte[] getBackupTableBytes() {
// 	long endOfBackupEntries =
// 	    getBackupTableBytesOffset() +
// 	    backupHeader.getNumberOfPartitionEntries()*backupHeader.getSizeOfPartitionEntry();
// 	if(endOfBackupEntries != BLOCK_SIZE*backupHeader.getPrimaryLBA())
// 	    throw new RuntimeException("Backup entries and backup header are not coherent! endOfBackupEntries=" + endOfBackupEntries + " backupHeader.getPrimaryLBA()=" + backupHeader.getPrimaryLBA());
	
	int offset = 0;
	byte[] result = new byte[GPTEntry.getSize()*entries.length + GPTHeader.getSize()];
	
	for(GPTEntry ge : backupEntries) {
	    byte[] entryData = ge.getBytes();
	    System.arraycopy(entryData, 0, result, offset, entryData.length);
	    offset += GPTEntry.getSize();
	}
	
	byte[] headerData = backupHeader.getBytes();
	System.arraycopy(headerData, 0, result, offset, GPTHeader.getSize());
	offset += GPTHeader.getSize();	
	
	return result;
    }
    

    public Dictionary getStructElements() {
        DictionaryBuilder dbStruct = new DictionaryBuilder(getClass().getSimpleName());
        dbStruct.add("header", header.getStructElements());
        {
            ArrayBuilder ab = new ArrayBuilder(GPTEntry.class.getSimpleName());
            for(int i = 0; i < entries.length; ++i) {
                GPTEntry ge = entries[i];
                ab.add(ge.getStructElements());
            }
            dbStruct.add("entries", ab.getResult());
        }
        dbStruct.add("backupHeader",  backupHeader.getStructElements());
        {
            ArrayBuilder ab = new ArrayBuilder(GPTEntry.class.getSimpleName());
            for(int i = 0; i < backupEntries.length; ++i) {
                GPTEntry ge = backupEntries[i];
                ab.add(ge.getStructElements());
            }
            dbStruct.add("backupEntries", ab.getResult());
        }
        return dbStruct.getResult();
    }
    
    @Override
    public boolean equals(Object obj) {
	if(obj instanceof GUIDPartitionTable) {
	    GUIDPartitionTable gpt = (GUIDPartitionTable)obj;
	    return Util.arraysEqual(getPrimaryTableBytes(), gpt.getPrimaryTableBytes()) &&
		Util.arraysEqual(getBackupTableBytes(), gpt.getBackupTableBytes());
	    // Lazy man's method, generating new allocations and work for the GC. But it's convenient.
	}
	else
	    return false;
    }
}
