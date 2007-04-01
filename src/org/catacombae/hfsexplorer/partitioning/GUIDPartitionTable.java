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

public class GUIDPartitionTable implements PartitionSystem {
    private static final int BLOCK_SIZE = 512;
    private final GPTHeader header;
    private final GPTEntry[] entries;
    
    public GUIDPartitionTable(LowLevelFile llf, int offset) {
	byte[] headerData = new byte[512];
	llf.seek(offset+512);
	llf.readFully(headerData);
	header = new GPTHeader(headerData, 0);
	header.print(System.err, "");
	entries = new GPTEntry[header.getNumberOfPartitionEntries()];
	byte[] currentEntryData = new byte[128];
	for(int i = 0; i < entries.length; ++i) {
	    llf.readFully(currentEntryData);
	    entries[i] = new GPTEntry(currentEntryData, 0, BLOCK_SIZE);
	}
    }
    public GPTEntry[] getEntries() {
	GPTEntry[] result = new GPTEntry[entries.length];
	for(int i = 0; i < result.length; ++i) {
	    result[i] = entries[i];
	}
	return result;
    }
    public int getPartitionCount() { return entries.length; }
    public Partition getPartition(int index) { return entries[index]; }
}