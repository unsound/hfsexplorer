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

import org.catacombae.hfsexplorer.Util;
import org.catacombae.hfsexplorer.Util2;
import org.catacombae.hfsexplorer.LowLevelFile;
import java.util.ArrayList;
import java.io.PrintStream;

public class ApplePartitionMap implements PartitionSystem {
    private APMPartition[] partitions;
    
    public ApplePartitionMap(LowLevelFile isoRaf, long pmOffset, int blockSize) {
	isoRaf.seek(pmOffset);
	byte[] currentBlock = new byte[/*512*/blockSize];
	ArrayList<APMPartition> partitionList = new ArrayList<APMPartition>();
	while(true) { // Loop while the signature is correct ("PM")
	    isoRaf.readFully(currentBlock);
	    if((currentBlock[0] & 0xFF) == 0x50 && 
	       (currentBlock[1] & 0xFF) == 0x4D) {
		APMPartition p = new APMPartition(currentBlock, 0, blockSize);
		partitionList.add(p);
// 		if(options.verbose) {
// 		    println();
// 		    p.printPartitionInfo(System.out);
// 		}
// 		else
// 		    println("\"" + p.getPmPartNameAsString() + "\" (" + p.getPmParTypeAsString() + ")");
	    }
	    else break;
	}
	partitions = partitionList.toArray(new APMPartition[partitionList.size()]);
    }
    public int getUsedPartitionCount() {
	return partitions.length;
    }
    /** index must be between 0 and getNumPartitions()-1. */
    public APMPartition getAPMPartition(int index) {
	return partitions[index];
    }
    public APMPartition[] getPartitionEntries() {
	APMPartition[] copy = new APMPartition[partitions.length];
	for(int i = 0; i < partitions.length; ++i)
	    copy[i] = partitions[i];
	return copy;
    }
    public Partition[] getUsedPartitionEntries() {
	return getPartitionEntries();
    }
    
    public void printFields(PrintStream ps, String prefix) {
	for(int i = 0; i < partitions.length; ++i) {
	    ps.println(prefix + " partitions[" + i + "]:");
	    partitions[i].print(ps, prefix + "  ");
	}
    }

    public void print(PrintStream ps, String prefix) {
	ps.println("Apple Partition Map:");
	printFields(ps, prefix);
    }
    
    public Partition getPartitionEntry(int index) {
	return getAPMPartition(index);
    }
    public String getLongName() { return "Apple Partition Map"; }
    public String getShortName() { return "APM"; }
}
