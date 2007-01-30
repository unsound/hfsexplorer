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
import java.io.PrintStream;

public class HFSPlusForkData {
    /*
     * struct HFSPlusForkData
     * size: 80 bytes
     *
     * BP   Size  Type                 Variable name   Description
     * --------------------------------------------------------------
     * 0    8     UInt64               logicalSize
     * 8    4     UInt32               clumpSize
     * 12   4     UInt32               totalBlocks
     * 16   64    HFSPlusExtentRecord  extents
     */
	
    private final byte[] logicalSize = new byte[8];
    private final byte[] clumpSize = new byte[4];
    private final byte[] totalBlocks = new byte[4];
    private final HFSPlusExtentRecord extents;

    public HFSPlusForkData(byte[] data, int offset) {
	System.arraycopy(data, offset+0, logicalSize, 0, 8);
	System.arraycopy(data, offset+8, clumpSize, 0, 4);
	System.arraycopy(data, offset+12, totalBlocks, 0, 4);
	extents = new HFSPlusExtentRecord(data, offset+16);
    }
	
    public long getLogicalSize() {
	return Util.readLongBE(logicalSize);
    }
    public long getClumpSize() {
	return Util.readIntBE(clumpSize);
    }
    public long getTotalBlocks() {
	return Util.readIntBE(totalBlocks);
    }
    public HFSPlusExtentRecord getExtents() { return extents; }
	
    public void print(PrintStream ps, int pregap) {
	String pregapString = "";
	for(int i = 0; i < pregap; ++i)
	    pregapString += " ";
	print(ps, pregapString);
    }
	
    public void print(PrintStream ps, String prefix) {
	ps.println(prefix + "logicalSize: " + getLogicalSize()/* + " (0x" + Util.byteArrayToHexString(logicalSize) + ")"*/);
	ps.println(prefix + "clumpSize: " + getClumpSize());
	ps.println(prefix + "totalBlocks: " + getTotalBlocks());
	ps.println(prefix + "extents:");
	extents.print(ps, prefix + "  ");
    }
}
