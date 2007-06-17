/*-
 * Copyright (C) 2006-2007 Erik Larsson
 * 
 * All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 */

package org.catacombae.hfsexplorer.partitioning;

import org.catacombae.hfsexplorer.*;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.zip.CRC32;

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
 *   The two partition arrays though, are supposed to be completely identical.
 */
public class GUIDPartitionTable implements PartitionSystem {
    private static final int BLOCK_SIZE = 512; // carved in stone!
    protected final GPTHeader header;
    protected final GPTEntry[] entries;
    
//     protected final GPTEntry[] backupEntries;
//     protected final GPTHeader backupHeader;
    
//     protected final int headerChecksum;
//     protected final int entriesChecksum;
    
    /** Method-private to getUsedPartitionEntries(). */
    private final LinkedList<GPTEntry> tempList = new LinkedList<GPTEntry>();
    
    public GUIDPartitionTable(LowLevelFile llf, int offset) {
	//final int crcPos = 20;
	byte[] headerData = new byte[512];
	llf.seek(offset+512);
	llf.readFully(headerData, 0, 512);
	this.header = new GPTHeader(headerData, 0);
// 	this.headerChecksum = header.calculateCRC32();
// 	try { //Fuling
// 	    java.io.FileOutputStream fos = new java.io.FileOutputStream("debug.gpt");
// 	    fos.write(headerData);
// 	    fos.close();
// 	} catch(Exception e) { e.printStackTrace(); }

	llf.seek(offset + header.getPartitionEntryLBA()*BLOCK_SIZE);
	//CRC32FilterLLF checksumStream = new CRC32FilterLLF(llf);
	this.entries = new GPTEntry[header.getNumberOfPartitionEntries()];
	byte[] currentEntryData = new byte[128];
	for(int i = 0; i < entries.length; ++i) {
	    llf.readFully(currentEntryData);
	    entries[i] = new GPTEntry(currentEntryData, 0, BLOCK_SIZE);
	}
// 	this.entriesChecksum = checksumStream.getChecksumValue();
    }
    public GUIDPartitionTable(GUIDPartitionTable source) {
	this.header = new GPTHeader(source.header);
	this.entries = new GPTEntry[source.entries.length];
	for(int i = 0; i < this.entries.length; ++i)
	    this.entries[i] = new GPTEntry(source.entries[i]);
	
// 	this.headerChecksum = source.headerChecksum;
// 	this.entriesChecksum = source.entriesChecksum;
    }
    
    protected GUIDPartitionTable(GPTHeader header, int numberOfEntries/*, int headerChecksum, int entriesChecksum*/) {
	this.header = header;
	this.entries = new GPTEntry[numberOfEntries];
// 	this.headerChecksum = headerChecksum;
//  	this.entriesChecksum = entriesChecksum;
   }
    
    public GPTHeader getHeader() {
	return header;
    }
    
    public GPTEntry[] getEntries() {
	GPTEntry[] result = new GPTEntry[entries.length];
	for(int i = 0; i < result.length; ++i) {
	    result[i] = entries[i];
	}
	return result;
    }
    public int getUsedPartitionCount() {
	int count = 0;
	for(GPTEntry ge : entries) {
	    if(ge.isUsed()) ++count;
	}
	return count;
    }
    public Partition getPartitionEntry(int index) { return entries[index]; }
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
    
    /** Checks the validity of GUID Partition Table data. */
    public boolean isValid() {
	return header.isValid() && 
	    (header.getCRC32Checksum() == calculateHeaderChecksum()) &&
	    (header.getPartitionEntryArrayCRC32() == calculateEntriesChecksum());
    }
    
    public int calculateHeaderChecksum() {
	return header.calculateCRC32();
    }
    public int calculateEntriesChecksum() {
	CRC32 checksum = new CRC32();
	for(GPTEntry entry : entries)
	    checksum.update(entry.getBytes());
	return (int)(checksum.getValue() & 0xFFFFFFFF);
    }
    
    public String getLongName() { return "GUID Partition Table"; }
    public String getShortName() { return "GPT"; }

    public void printFields(PrintStream ps, String prefix) {
	ps.println(prefix + " header:");
	header.print(ps, prefix + "  ");
	for(int i = 0; i < entries.length; ++i) {
	    if(entries[i].isUsed()) {
		ps.println(prefix + " entries[" + i + "]:");
		entries[i].print(ps, prefix + "  ");
	    }
	}
    }
    
    public void print(PrintStream ps, String prefix) {
	ps.println(prefix + "GUIDPartitionTable:");
	printFields(ps, prefix);
    }
    
    /** Returns the data in the main table of the disk. (LBA1-LBAx) */
    public byte[] getPrimaryTableBytes() {
	int offset = 0;
	byte[] result = new byte[GPTHeader.getSize() + GPTEntry.getSize()*entries.length];
	byte[] headerData = header.getBytes();
	System.arraycopy(headerData, 0, result, offset, headerData.length);
	offset += GPTHeader.getSize();
	
	for(GPTEntry ge : entries) {
	    byte[] entryData = ge.getBytes();
	    System.arraycopy(entryData, 0, result, offset, entryData.length);
	    offset += GPTEntry.getSize();
	}
	
	return result;
    }
    public byte[] getBackupTableBytes() {
	return new byte[0];
    }
    
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
