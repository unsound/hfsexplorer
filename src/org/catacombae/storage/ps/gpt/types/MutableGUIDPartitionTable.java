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

package org.catacombae.hfsexplorer.partitioning;

public class MutableGUIDPartitionTable extends GUIDPartitionTable {
    protected MutableGPTHeader mutableHeader;
    protected MutableGPTEntry[] mutableEntries;
    protected MutableGPTHeader mutableBackupHeader;
    protected MutableGPTEntry[] mutableBackupEntries;
    
    public MutableGUIDPartitionTable(GUIDPartitionTable source) {
	super(new MutableGPTHeader(source.header), new MutableGPTHeader(source.backupHeader),
	      source.entries.length, source.backupEntries.length);
	
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
    
    public void setMutablePrimaryHeader(MutableGPTHeader mutableHeader) {
	this.mutableHeader = mutableHeader;
	this.header = mutableHeader;
    }
    
    public void setMutableBackupHeader(MutableGPTHeader mutableBackupHeader) {
	this.mutableBackupHeader = mutableBackupHeader;
	this.backupHeader = mutableBackupHeader;
    }
    
    public void setMutablePrimaryEntries(MutableGPTEntry[] mutableEntries) {
	this.mutableEntries = mutableEntries;
	this.entries = mutableEntries;
    }
    
    public void setMutableBackupEntries(MutableGPTEntry[] mutableBackupEntries) {
	this.mutableBackupEntries = mutableBackupEntries;
	this.backupEntries = mutableBackupEntries;
    }
}
