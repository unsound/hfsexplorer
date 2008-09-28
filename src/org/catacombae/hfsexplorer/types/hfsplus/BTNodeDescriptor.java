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

public class BTNodeDescriptor {
    /*
     * struct BTNodeDescriptor
     * size: 14 bytes
     *
     * BP   Size  Type              Variable name
     * --------------------------------------------
     * 0    4     UInt32            fLink
     * 4    4     UInt32            bLink
     * 8    1     SInt8             kind
     * 9    1     UInt8             height
     * 10   2     UInt16            numRecords
     * 12   2     UInt16            reserved
     */
    
    public static final int BT_LEAF_NODE = -1;
    public static final int BT_INDEX_NODE = 0;
    public static final int BT_HEADER_NODE = 1;
    public static final int BT_MAP_NODE = 2;

    private static final int STRUCTSIZE = 14;
    
    private final byte[] fLink = new byte[4];
    private final byte[] bLink = new byte[4];
    private final byte[] kind = new byte[1];
    private final byte[] height = new byte[1];
    private final byte[] numRecords = new byte[2];
    private final byte[] reserved = new byte[2];
    
    public BTNodeDescriptor(byte[] data, int offset) {
	System.arraycopy(data, offset+0, fLink, 0, 4);
	System.arraycopy(data, offset+4, bLink, 0, 4);
	System.arraycopy(data, offset+8, kind, 0, 1);
	System.arraycopy(data, offset+9, height, 0, 1);
	System.arraycopy(data, offset+10, numRecords, 0, 2);
	System.arraycopy(data, offset+12, reserved, 0, 2);
    }

    public static int length() { return STRUCTSIZE; }

    /** Returns the node number of the next node of the same kind. 0 for the last node. */
    public int getFLink() { return Util.readIntBE(fLink); }
    /** Returns the node number of the previous node of the same kind. 0 for the first node. */
    public int getBLink() { return Util.readIntBE(bLink); }
    /** Returns one of BT_LEAF_NODE, BT_INDEX_NODE, BT_HEADER_NODE or BT_MAP_NODE if the data is correct. */
    public byte getKind() { return Util.readByteBE(kind); }
    /**
     * Returns the height of the subtree below this node. For leaf nodes, it is always 1,
     * for index nodes it is one greater than the height of its children, and for the header
     * node and map nodes, it is 0.
     */
    public byte getHeight() { return Util.readByteBE(height); }
    /** Returns the number of records contained in this node. */
    public short getNumRecords() { return Util.readShortBE(numRecords); }
    public short getReserved() { return Util.readShortBE(reserved); }

    public String getKindAsString() {
	byte kind = getKind();
	String result;
	if(kind == BT_LEAF_NODE)
	    result = "kBTLeafNode";
	else if(kind == BT_INDEX_NODE)
	    result = "kBTIndexNode";
	else if(kind == BT_HEADER_NODE)
	    result = "kBTHeaderNode";
	else if(kind == BT_MAP_NODE)
	    result = "kBTMapNode";
	else
	    result = "UNKNOWN!";
	return result;
    }
    
    public void printFields(PrintStream ps, String prefix) {
	ps.println(prefix + " fLink: " + getFLink() + "");
	ps.println(prefix + " bLink: " + getBLink() + "");
	ps.println(prefix + " kind: " + getKind() + " (" + getKindAsString() + ")");
	ps.println(prefix + " height: " + getHeight() + "");
	ps.println(prefix + " numRecords: " + getNumRecords() + "");
	ps.println(prefix + " reserved: 0x" + Util.toHexStringBE(getReserved()) + "");
    }
    public void print(PrintStream ps, String prefix) {
	ps.println(prefix + "BTNodeDescriptor:");
	printFields(ps, prefix);
    }
}
