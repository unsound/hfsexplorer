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

public abstract class BTIndexNode extends BTNode {
    //protected final BTNodeDescriptor nodeDescriptor;
    protected final BTIndexRecord[] records;
    protected final short[] offsets;
    
    protected BTIndexNode(byte[] data, int offset, int nodeSize) {
	//nodeDescriptor = new BTNodeDescriptor(data, offset);
	super(data, offset, nodeSize);
	offsets = new short[Util.unsign(nodeDescriptor.getNumRecords())+1]; //Last one is free space index
	for(int i = 0; i < offsets.length; ++i) {
	    offsets[i] = Util.readShortBE(data, offset+nodeSize-((i+1)*2));
	}
	records = new BTIndexRecord[offsets.length-1];
    }

    //public BTNodeDescriptor getNodeDescriptor() { return nodeDescriptor; }
    public BTIndexRecord getIndexRecord(int index) { return records[index]; }
    public BTIndexRecord[] getIndexRecords() {
	BTIndexRecord[] copy = new BTIndexRecord[records.length];
	for(int i = 0; i < copy.length; ++i)
	    copy[i] = records[i];
	return copy;
    }
}
