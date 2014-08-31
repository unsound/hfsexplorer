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

package org.catacombae.hfs.types.hfsplus;

import java.io.PrintStream;
import org.catacombae.util.Util;

public abstract class BTIndexNode extends BTNode {
    //protected final BTNodeDescriptor nodeDescriptor;
    protected final BTIndexRecord[] records;
    protected final short[] offsets;

    protected BTIndexNode(byte[] data, int offset, int nodeSize) {
	//nodeDescriptor = new BTNodeDescriptor(data, offset);
	super(data, offset, nodeSize);
	this.offsets = new short[Util.unsign(nodeDescriptor.getNumRecords())+1]; //Last one is free space index
	for(int i = 0; i < this.offsets.length; ++i) {
	    this.offsets[i] = Util.readShortBE(data, offset+nodeSize-((i+1)*2));
	}
	this.records = new BTIndexRecord[this.offsets.length-1];
    }

    //public BTNodeDescriptor getNodeDescriptor() { return nodeDescriptor; }
    public BTIndexRecord getIndexRecord(int index) {
        return this.records[index];
    }

    public BTIndexRecord[] getIndexRecords() {
	BTIndexRecord[] copy = new BTIndexRecord[records.length];
	for(int i = 0; i < copy.length; ++i)
	    copy[i] = records[i];
	return copy;
    }

    @Override
    public void printFields(PrintStream ps, String prefix) {
        super.printFields(ps, prefix);
        ps.println(prefix + " records:");
        for(int i = 0; i < records.length; ++i) {
            ps.println(prefix + "  [" + i + "]:");
            records[i].printFields(ps, prefix + "   ");
        }
    }

    @Override
    public void print(PrintStream ps, String prefix) {
        ps.println(prefix + "BTIndexNode:");
        printFields(ps, prefix);
    }
}
