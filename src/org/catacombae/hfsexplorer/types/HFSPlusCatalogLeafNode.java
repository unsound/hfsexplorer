/*-
 * Copyright (C) 2006 Erik Larsson
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

package org.catacombae.hfsexplorer.types;

import org.catacombae.hfsexplorer.Util;
import org.catacombae.hfsexplorer.Util2;

public class HFSPlusCatalogLeafNode extends BTLeafNode {
    //protected BTNodeDescriptor nodeDescriptor;
    protected HFSPlusCatalogLeafRecord[] leafRecords;
    
    public HFSPlusCatalogLeafNode(byte[] data, int offset, int nodeSize) {
	//nodeDescriptor = new BTNodeDescriptor(data, offset);
	super(data, offset, nodeSize);
	short[] offsets = new short[Util2.unsign(nodeDescriptor.getNumRecords())+1];
	for(int i = 0; i < offsets.length; ++i) {
	    offsets[i] = Util.readShortBE(data, offset+nodeSize-((i+1)*2));
	}
	leafRecords = new HFSPlusCatalogLeafRecord[offsets.length-1];
	// we loop offsets.length-1 times, since last offset is offset to free space
	for(int i = 0; i < leafRecords.length; ++i) {
	    int currentOffset = Util2.unsign(offsets[i]);
	    //HFSPlusCatalogKey currentKey = new HFSPlusCatalogKey(currentNode, offset+currentOffset);
	    leafRecords[i] = new HFSPlusCatalogLeafRecord(data, offset+currentOffset);
	}
	
    }
    
    //public BTNodeDescriptor getNodeDescriptor() { return nodeDescriptor; }
    public HFSPlusCatalogLeafRecord getLeafRecord(int index) { return leafRecords[index]; }
    public HFSPlusCatalogLeafRecord[] getLeafRecords() {
	HFSPlusCatalogLeafRecord[] copy = new HFSPlusCatalogLeafRecord[leafRecords.length];
	for(int i = 0; i < copy.length; ++i)
	    copy[i] = leafRecords[i];
	return copy;
    }
}
