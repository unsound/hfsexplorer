package org.catacombae.hfsexplorer.partitioning;

public class MutableGUIDPartitionTable extends GUIDPartitionTable {
    protected final MutableGPTHeader mutableHeader;
    protected final MutableGPTEntry[] mutableEntries;
    
    public MutableGUIDPartitionTable(GUIDPartitionTable source) {
	super(new MutableGPTHeader(source.header), source.entries.length);
	this.mutableHeader = (MutableGPTHeader)header;
	this.mutableEntries = new MutableGPTEntry[source.entries.length];
	for(int i = 0; i < this.entries.length; ++i) {
	    this.mutableEntries[i] = new MutableGPTEntry(source.entries[i]);
	    this.entries[i] = this.mutableEntries[i];
	}
	
// 	this.header = mutableHeader;
// 	this.entries = mutableEntries;
	
// 	this.headerChecksum = source.headerChecksum;
// 	this.entriesChecksum = source.entriesChecksum;
    }
    
    
}
