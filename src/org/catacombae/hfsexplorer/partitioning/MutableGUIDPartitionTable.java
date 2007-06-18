package org.catacombae.hfsexplorer.partitioning;

public class MutableGUIDPartitionTable extends GUIDPartitionTable {
    protected final MutableGPTHeader mutableHeader;
    protected final MutableGPTEntry[] mutableEntries;
    protected final MutableGPTHeader mutableBackupHeader;
    protected final MutableGPTEntry[] mutableBackupEntries;
    
    public MutableGUIDPartitionTable(GUIDPartitionTable source) {
	super(new MutableGPTHeader(source.header), new MutableGPTHeader(source.backupHeader), source.entries.length);
	
	this.mutableHeader = (MutableGPTHeader)header;
	this.mutableEntries = new MutableGPTEntry[source.entries.length];
	for(int i = 0; i < this.entries.length; ++i) {
	    this.mutableEntries[i] = new MutableGPTEntry(source.entries[i]);
	    this.entries[i] = this.mutableEntries[i];
	}

	this.mutableBackupHeader = (MutableGPTHeader)backupHeader;
	this.mutableBackupEntries = new MutableGPTEntry[source.backupEntries.length];
	for(int i = 0; i < this.backupEntries.length; ++i) {
	    this.mutableBackupEntries[i] = new MutableGPTEntry(source.backupEntries[i]);
	    this.backupEntries[i] = this.mutableBackupEntries[i];
	}
    }
    
    public MutableGPTHeader getMutablePrimaryHeader() { return mutableHeader; }
    public MutableGPTHeader getMutableBackupHeader() { return mutableBackupHeader; }
    public MutableGPTEntry getMutablePrimaryEntry(int index) { return mutableEntries[index]; }
    public MutableGPTEntry getMutableBackupEntry(int index) { return mutableBackupEntries[index]; }
    public MutableGPTEntry[] getMutablePrimaryEntries() { return mutableEntries; }
    public MutableGPTEntry[] getMutableBackupEntries() { return mutableBackupEntries; }
}
