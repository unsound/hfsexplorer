/*-
 * Copyright (C) 2006 Erik Larsson
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

package org.catacombae.hfsexplorer.types.hfsplus;

import org.catacombae.hfsexplorer.Util;
import java.io.PrintStream;

public class HFSPlusExtentLeafNode extends BTLeafNode {
    //protected BTNodeDescriptor nodeDescriptor;
    protected HFSPlusExtentLeafRecord[] leafRecords;
    protected short[] leafRecordOffsets;
    
    public HFSPlusExtentLeafNode(byte[] data, int offset, int nodeSize) {
	//nodeDescriptor = new BTNodeDescriptor(data, offset);
	super(data, offset, nodeSize);
	leafRecordOffsets = new short[Util.unsign(nodeDescriptor.getNumRecords())+1]; // The last offset is offset to free space
	for(int i = 0; i < leafRecordOffsets.length; ++i) {
	    leafRecordOffsets[i] = Util.readShortBE(data, offset+nodeSize-((i+1)*2));
	}
	leafRecords = new HFSPlusExtentLeafRecord[leafRecordOffsets.length-1];
	// we loop offsets.length-1 times, since last offset is offset to free space
	for(int i = 0; i < leafRecords.length; ++i) {
	    int currentOffset = Util.unsign(leafRecordOffsets[i]);
	    leafRecords[i] = new HFSPlusExtentLeafRecord(data, offset+currentOffset);
	}
	
    }
    
    public short[] getLeafRecordOffsets() { 
	short[] offsets = new short[leafRecordOffsets.length];
	for(int i = 0; i < offsets.length; ++i) {
	    offsets[i] = leafRecordOffsets[i];
	}
	return offsets;
    }
    //public BTNodeDescriptor getNodeDescriptor() { return nodeDescriptor; }
    public HFSPlusExtentLeafRecord getLeafRecord(int index) { return leafRecords[index]; }
    public HFSPlusExtentLeafRecord[] getLeafRecords() {
	HFSPlusExtentLeafRecord[] copy = new HFSPlusExtentLeafRecord[leafRecords.length];
	for(int i = 0; i < copy.length; ++i)
	    copy[i] = leafRecords[i];
	return copy;
    }

    public void printFields(PrintStream ps, String prefix) {
	ps.println(prefix + " nodeDescriptor:");
	nodeDescriptor.printFields(ps, prefix + "  ");
	for(int i = 0; i < leafRecords.length; ++i) {
	    ps.println(prefix + " leafRecords[" + i + "]:");
	    leafRecords[i].printFields(ps, prefix + "  ");
	}
    }
    
    public void print(PrintStream ps, String prefix) {
	ps.println(prefix + "HFSPlusExtentLeafNode:");
	printFields(ps, prefix);
    }
}
