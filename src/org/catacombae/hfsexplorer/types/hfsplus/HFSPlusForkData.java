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
import org.catacombae.csjc.StructElements;
import org.catacombae.csjc.structelements.Dictionary;
import org.catacombae.csjc.structelements.DictionaryBuilder;

public class HFSPlusForkData implements StructElements {
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

    public static int length() { return 80; }

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

    byte[] getBytes() {
        byte[] result = new byte[length()];
	byte[] tempData;
	int offset = 0;
        
	System.arraycopy(logicalSize, 0, result, offset, logicalSize.length); offset += logicalSize.length;
	System.arraycopy(clumpSize, 0, result, offset, clumpSize.length); offset += clumpSize.length;
	System.arraycopy(totalBlocks, 0, result, offset, totalBlocks.length); offset += totalBlocks.length;
        tempData = extents.getBytes();
	System.arraycopy(tempData, 0, result, offset, tempData.length); offset += tempData.length;
        
        return result;
    }

    public Dictionary getStructElements() {
        DictionaryBuilder db = new DictionaryBuilder(HFSPlusForkData.class.getSimpleName());

        db.addUIntBE("logicalSize", logicalSize);
        db.addUIntBE("clumpSize", clumpSize);
        db.addUIntBE("totalBlocks", totalBlocks);
        db.add("extents", extents.getStructElements());

        return db.getResult();
    }
}
