/*-
 * Copyright (C) 2006-2013 Erik Larsson
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

public class HFSPlusAttributesLeafNode extends BTLeafNode {
    protected HFSPlusAttributesLeafRecord[] leafRecords;

    public HFSPlusAttributesLeafNode(byte[] data, int offset, int nodeSize) {
        this(data, offset, nodeSize, null);
    }

    protected HFSPlusAttributesLeafNode(byte[] data, int offset, int nodeSize,
            BTHeaderRec catalogHeaderRec)
    {
        super(data, offset, nodeSize);

        short[] offsets =
                new short[Util.unsign(nodeDescriptor.getNumRecords()) + 1];
        for(int i = 0; i < offsets.length; ++i) {
            offsets[i] = Util.readShortBE(data, offset + nodeSize -
                    ((i + 1) * 2));
        }

        leafRecords = new HFSPlusAttributesLeafRecord[offsets.length - 1];
        /* We loop offsets.length-1 times, since last offset is offset to free
         * space. */
        for(int i = 0; i < leafRecords.length; ++i) {
            int currentOffset = Util.unsign(offsets[i]);

            leafRecords[i] = new HFSPlusAttributesLeafRecord(data,
                    offset + currentOffset);
        }
    }

    public HFSPlusAttributesLeafRecord getLeafRecord(int index) {
        return leafRecords[index];
    }

    public HFSPlusAttributesLeafRecord[] getLeafRecords() {
        HFSPlusAttributesLeafRecord[] copy =
                new HFSPlusAttributesLeafRecord[leafRecords.length];

        System.arraycopy(leafRecords, 0, copy, 0, leafRecords.length);

        return copy;
    }

    @Override
    public void printFields(PrintStream ps, String prefix) {
        super.printFields(ps, prefix);
        for(int i = 0; i < leafRecords.length; ++i) {
            ps.println(prefix + " leafRecords[" + i + "]:");
            leafRecords[i].printFields(ps, prefix + "  ");
        }
    }

    @Override
    public void print(PrintStream ps, String prefix) {
        ps.println(prefix + this.getClass().getSimpleName() + ":");
        printFields(ps, prefix);
    }
}
