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

public class ApplePartitionMap {
    private APMPartition[] partitions;
    
    public ApplePartitionMap(LowLevelFile isoRaf, long pmOffset, int blockSize) {
	isoRaf.seek(pmOffset);
	byte[] currentBlock = new byte[512]; // This is not an actual device block. It's always 512 bytes.
// 	byte[] signature = new byte[2];
	//APMPartition p = new APMPartition(isoRaf);
	ArrayList<APMPartition> partitionList = new ArrayList<APMPartition>();
	//for(int i = 0; i < 20; ++i) {
	while(true) { // Loop while the signature is correct ("PM")
	    isoRaf.readFully(currentBlock);
// 	    signature[0] = currentBlock[0];
// 	    signature[1] = currentBlock[1];
	    if((currentBlock[0] & 0xFF) == 0x50 && 
	       (currentBlock[1] & 0xFF) == 0x4D) { // new String(currentBlock, 0, 2, "ASCII").equals("PM")
		//print("Partition " + i + ": ");
		APMPartition p = new APMPartition(currentBlock, 0);
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
// 	//print("Which partition do you wish to explore [0-" + (partitions.size()-1) + "]? ");
// 	int partNum = Integer.parseInt(stdin.readLine());
// 	APMPartition chosenPartition = partitions.get(partNum);
// 	String partitionType = chosenPartition.getPmParTypeAsString();
// 	if(!partitionType.trim().equals("Apple_HFS")) {
// 	    //println("The partition is not an HFS partition!");
// 	    System.exit(0);
// 	}
// 	//println("Parsing partition " + partNum + " (" + chosenPartition.getPmPartNameAsString().trim() + "/" + partitionType.trim() + ")");
// 	offset = (chosenPartition.getPmPyPartStart()+chosenPartition.getPmLgDataStart())*0x200;
// 	length = chosenPartition.getPmDataCnt()*0x200;
    }
    public int getPartitionCount() {
	return partitions.length;
    }
    /** index must be between 0 and getNumPartitions()-1. */
    public APMPartition getPartition(int index) {
	return partitions[index];
    }
    public APMPartition[] getPartitions() {
	APMPartition[] copy = new APMPartition[partitions.length];
	for(int i = 0; i < partitions.length; ++i)
	    copy[i] = partitions[i];
	return copy;
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
}
