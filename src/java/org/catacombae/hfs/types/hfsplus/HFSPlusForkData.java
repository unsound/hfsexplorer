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

import org.catacombae.util.Util;
import java.io.PrintStream;
import org.catacombae.csjc.PrintableStruct;
import org.catacombae.csjc.StructElements;
import org.catacombae.csjc.structelements.Dictionary;

/**
 * @author <a href="https://catacombae.org" target="_top">Erik Larsson</a>
 */
public class HFSPlusForkData implements StructElements, PrintableStruct {
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
        this(false, data, offset);
    }

    private HFSPlusForkData(boolean mutable, byte[] data, int offset) {
	System.arraycopy(data, offset+0, logicalSize, 0, 8);
	System.arraycopy(data, offset+8, clumpSize, 0, 4);
	System.arraycopy(data, offset+12, totalBlocks, 0, 4);
        if(mutable)
            extents = new HFSPlusExtentRecord.Mutable(data, offset+16);
        else
            extents = new HFSPlusExtentRecord(data, offset+16);
    }

    public static int length() { return 80; }

    public long getLogicalSize() {
	return Util.readLongBE(logicalSize);
    }
    public int getClumpSize() {
	return Util.readIntBE(clumpSize);
    }
    public int getTotalBlocks() {
	return Util.readIntBE(totalBlocks);
    }
    public HFSPlusExtentRecord getExtents() { return extents; }
	
    public void print(PrintStream ps, int pregap) {
	String pregapString = "";
	for(int i = 0; i < pregap; ++i)
	    pregapString += " ";
	print(ps, pregapString);
    }
	
    private void _printFields(PrintStream ps, String prefix) {
	ps.println(prefix + "logicalSize: " + getLogicalSize()/* + " (0x" + Util.byteArrayToHexString(logicalSize) + ")"*/);
	ps.println(prefix + "clumpSize: " + getClumpSize());
	ps.println(prefix + "totalBlocks: " + getTotalBlocks());
	ps.println(prefix + "extents:");
	extents.print(ps, prefix + "  ");
    }

    public void printFields(PrintStream ps, String prefix) {
        _printFields(ps, prefix + " ");
    }

    public void print(PrintStream ps, String prefix) {
        ps.println(prefix + "HFSPlusForkData:");
        _printFields(ps, prefix + " ");
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

        db.addUIntBE("logicalSize", logicalSize, "Logical size", "bytes");
        db.addUIntBE("clumpSize", clumpSize, "Clump size", "bytes");
        db.addUIntBE("totalBlocks", totalBlocks, "Total blocks");
        db.add("extents", extents.getStructElement(), "Extents");

        return db.getResult();
    }

    private void _setLogicalSize(long logicalSize) {
        Util.arrayPutBE(this.logicalSize, 0, (long) logicalSize);
    }

    private void _setClumpSize(int clumpSize) {
        Util.arrayPutBE(this.clumpSize, 0, (int) clumpSize);
    }

    private void _setTotalBlocks(int totalBlocks) {
        Util.arrayPutBE(this.totalBlocks, 0, (int) totalBlocks);
    }

    private void _setExtents(HFSPlusExtentRecord extents) {
        ((HFSPlusExtentRecord.Mutable) this.extents).set(extents);
    }

    private void _set(HFSPlusForkData forkData) {
        Util.arrayCopy(forkData.logicalSize, this.logicalSize);
        Util.arrayCopy(forkData.clumpSize, this.clumpSize);
        Util.arrayCopy(forkData.totalBlocks, this.totalBlocks);
        this._setExtents(forkData.extents);
    }

    private HFSPlusExtentRecord.Mutable _getMutableExtents() {
        return (HFSPlusExtentRecord.Mutable) this.extents;
    }

    public static class Mutable extends HFSPlusForkData {
        public Mutable(byte[] data, int offset) {
            super(data, offset);
        }

        public void set(HFSPlusForkData forkData) {
            super._set(forkData);
        }

        public void setLogicalSize(long logicalSize) {
            super._setLogicalSize(logicalSize);
        }

        public void setClumpSize(int clumpSize) {
            super._setClumpSize(clumpSize);
        }

        public void setTotalBlocks(int totalBlocks) {
            super._setTotalBlocks(totalBlocks);
        }

        public void setExtents(HFSPlusExtentRecord extents) {
            super._setExtents(extents);
        }

        public HFSPlusExtentRecord.Mutable getMutableExtents() {
            return super._getMutableExtents();
        }
    }
}
