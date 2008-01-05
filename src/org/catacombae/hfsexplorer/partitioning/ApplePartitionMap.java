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

import org.catacombae.hfsexplorer.io.RandomAccessLLF;
import org.catacombae.hfsexplorer.io.LowLevelFile;
import org.catacombae.hfsexplorer.io.ArrayBackedFile;
import org.catacombae.hfsexplorer.*;
import java.util.ArrayList;
import java.io.PrintStream;

public class ApplePartitionMap implements PartitionSystem {
    private APMPartition[] partitions;
    
    public ApplePartitionMap(LowLevelFile isoRaf, long pmOffset, int blockSize) {
	isoRaf.seek(pmOffset);
	byte[] currentBlock = new byte[/*512*/blockSize];
	ArrayList<APMPartition> partitionList = new ArrayList<APMPartition>();
	while(isoRaf.length() > isoRaf.getFilePointer()+currentBlock.length) { // Loop while we have data left in the file
	    isoRaf.readFully(currentBlock);
            APMPartition p = new APMPartition(currentBlock, 0, blockSize);
            if(p.isValid())
		partitionList.add(p);
	    else break;
	}
	partitions = partitionList.toArray(new APMPartition[partitionList.size()]);
    }
    
    public ApplePartitionMap(byte[] data, int off, int blockSize) {
        this(new ArrayBackedFile(data, 0, data.length), off, blockSize);
        /*
	byte[] currentBlock = new byte[blockSize];
	ArrayList<APMPartition> partitionList = new ArrayList<APMPartition>();
	while(data.length > off+currentBlock.length) { // Loop while we have data left in the array
	    System.arraycopy(data, off, currentBlock, 0, currentBlock.length);
            off += currentBlock.length;
            APMPartition p = new APMPartition(currentBlock, 0, blockSize);
            if(p.isValid())
		partitionList.add(p);
	    else break;
	}
	partitions = partitionList.toArray(new APMPartition[partitionList.size()]);
        */
    }

    public boolean isValid() {
        if(partitions.length > 0) {
            for(APMPartition p : partitions) {
                if(!p.isValid())
                    return false;
            }
            return true;
        }
        else // An empty partition system is really not a partition system.
            return false;
    }

    public int getPartitionCount() {
	return partitions.length;
    }
    
    public int getUsedPartitionCount() {
	return getPartitionCount();
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
    
    public byte[] getData() {
	byte[] result = new byte[partitions.length*APMPartition.structSize()];
	int offset = 0;
	for(APMPartition ap : partitions) {
	    byte[] tmp = ap.getData();
	    System.arraycopy(tmp, 0, result, offset, tmp.length); offset += tmp.length;
	}
	//System.arraycopy(, 0, result, offset, .length); offset += .length;
	if(offset != result.length)
	    throw new RuntimeException("Internal miscalculation...");
	else
	    return result;
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
    
    /** This main method prints the fields of the DDR and APM in the file specified in args[0], offset 0. */
    public static void main(String[] args) {
	RandomAccessLLF fin = new RandomAccessLLF(args[0]);
	byte[] curBlock = new byte[DriverDescriptorRecord.length()];
	
	if(fin.read(curBlock) != curBlock.length) throw new RuntimeException("Could not read all...");
	DriverDescriptorRecord ddr = new DriverDescriptorRecord(curBlock, 0);
	ddr.print(System.out, "");

	final int blockSize = ddr.getSbBlkSize();
	curBlock = new byte[blockSize];
	
	ApplePartitionMap apm = new ApplePartitionMap(fin, blockSize, blockSize);
	apm.print(System.out, "");
    }
}
