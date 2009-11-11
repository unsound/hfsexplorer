/*-
 * Copyright (C) 2008 Erik Larsson
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

package org.catacombae.jparted.lib.ps.ebr;

import java.io.PrintStream;
//import java.util.LinkedList;
import org.catacombae.hfsexplorer.Util;

/**
 *
 * @author Erik
 */
public class ExtendedBootRecord {
    public static final short MBR_SIGNATURE = 0x55AA;
    
    /*
     * struct ExtendedBootRecord
     * size: 512 bytes
     *
     * BP   Size  Type              Variable name        Description
     * --------------------------------------------------------------
     * 0    394   byte[394]         reserved1            most likely zeroed
     * 394  9     byte[9]           optIBMBootmgrEntry   possible IBM Boot Manager menu entry 
     * 403  37    byte[37]          reserved2            most likely zeroed
     * 440  4     UInt32            optDiskSignature     possibly IBM extended data
     * 444  2     byte[2]           reserved3            most likely zeroed
     * 446  16    MBRPartition      firstEntry           partition table's first entry
     * 462  16    MBRPartition      secondEntry          partition table's second entry
     * 478  32    byte[32]          reserved4            should be zeroed
     * 510  2     UInt16            mbrSignature         signature that should always be present
     *
     */
    protected final byte[] reserved1 = new byte[394];
    protected final byte[] optIBMBootmgrEntry = new byte[9];
    protected final byte[] reserved2 = new byte[37];
    protected final byte[] optDiskSignature = new byte[4];
    protected final byte[] reserved3 = new byte[2];
    protected final EBRPartition firstEntry;
    protected final EBRPartition secondEntry;
    protected final byte[] reserved4 = new byte[32];
    protected final byte[] mbrSignature = new byte[2];
    
    //private final LinkedList<Partition> tempList = new LinkedList<Partition>(); // getUsedPartitionEntries()
    
    /** <code>data</code> is assumed to be at least (<code>offset</code>+512) bytes in length. */
    public ExtendedBootRecord(byte[] data, int offset, long extendedPartitionOffset, long thisRecordOffset, int sectorSize) {
        //System.err.println("ExtendedBootRecord...");
        System.arraycopy(data, offset + 0, reserved1, 0, reserved1.length);
        System.arraycopy(data, offset + 394, optIBMBootmgrEntry, 0, optIBMBootmgrEntry.length);
        System.arraycopy(data, offset + 403, reserved2, 0, reserved2.length);
        System.arraycopy(data, offset + 440, optDiskSignature, 0, optDiskSignature.length);
        System.arraycopy(data, offset + 444, reserved3, 0, reserved3.length);
        firstEntry = new EBRPartition(data, offset + 446, thisRecordOffset, sectorSize);
        secondEntry = new EBRPartition(data, offset + 462, extendedPartitionOffset, sectorSize);
        System.arraycopy(data, offset + 478, reserved4, 0, reserved4.length);
        System.arraycopy(data, offset + 510, mbrSignature, 0, mbrSignature.length);

        if(!Util.arrayRegionsEqual(getBytes(), 0, getStructSize(), data, offset, getStructSize()))
            throw new RuntimeException("Internal error!");
    }
    
    public ExtendedBootRecord(ExtendedBootRecord source) {
        System.arraycopy(source.reserved1, 0, reserved1, 0, reserved1.length);
        System.arraycopy(source.optIBMBootmgrEntry, 0, optIBMBootmgrEntry, 0, optIBMBootmgrEntry.length);
        System.arraycopy(source.reserved2, 0, reserved2, 0, reserved2.length);
        System.arraycopy(source.optDiskSignature, 0, optDiskSignature, 0, optDiskSignature.length);
        System.arraycopy(source.reserved3, 0, reserved3, 0, reserved3.length);
        firstEntry = new EBRPartition(source.firstEntry);
        secondEntry = new EBRPartition(source.secondEntry);
        System.arraycopy(source.reserved4, 0, reserved3, 0, reserved4.length);
        System.arraycopy(source.mbrSignature, 0, mbrSignature, 0, mbrSignature.length);
    }
    
    public static int getStructSize() { return 512; }
    
    /** This is an optional field, and might contain unexpected and invalid data. */
    public byte[] getOptionalIBMBootManagerEntry() { return Util.createCopy(optIBMBootmgrEntry); }
    
    /** This is an optional field, and might contain unexpected and invalid data. */
    public int getOptionalDiskSignature() { return Util.readIntBE(optDiskSignature); }
    
    public EBRPartition getFirstEntry() { return firstEntry; }
    
    public EBRPartition getSecondEntry() { return secondEntry; }
    
    public short getMBRSignature() { return Util.readShortBE(mbrSignature); }
    
    public void printFields(PrintStream ps, String prefix) {
        ps.println(prefix + " diskSignature: 0x" + Util.toHexStringBE(getOptionalDiskSignature()) + " (optional, and possibly incorrect)");

        ps.println(prefix + " firstEntry:");
        if(firstEntry.isValid()) {
            firstEntry.print(ps, prefix + "  ");
        }
        else {
            ps.println(prefix + "  [Invalid data]");
        }

        ps.println(prefix + " secondEntry:");
        if(secondEntry.isValid()) {
            secondEntry.print(ps, prefix + "  ");
        }
        else {
            ps.println(prefix + "  [Invalid data]");
        }

        ps.println(prefix + " mbrSignature: 0x" + Util.toHexStringBE(getMBRSignature()));
    }

    public void print(PrintStream ps, String prefix) {
        ps.println(prefix + this.getClass().getSimpleName() + ":");
        printFields(ps, prefix);
    }

    public boolean isValid() {
        // More validity contraints could be added later... like that the partitions should be in order and
        // that their lengths should be non negative and stay within device borders...
        return (getMBRSignature() == MBR_SIGNATURE) && isPartitionInfoValid();
    }

    public boolean isPartitionInfoValid() {
        if(!firstEntry.isValid() || !secondEntry.isValid())
            return false;
        else
            return true;
    }
    
    public byte[] getBytes() {
        byte[] result = new byte[512];
        byte[] curData;
        int i = 0;
        System.arraycopy(reserved1, 0, result, i, reserved1.length); i += reserved1.length;
        System.arraycopy(optIBMBootmgrEntry, 0, result, i, optIBMBootmgrEntry.length); i += optIBMBootmgrEntry.length;
        System.arraycopy(reserved2, 0, result, i, reserved2.length); i += reserved2.length;
        System.arraycopy(optDiskSignature, 0, result, i, optDiskSignature.length); i += optDiskSignature.length;
        System.arraycopy(reserved3, 0, result, i, reserved3.length); i += reserved3.length;
        curData = firstEntry.getBytes();
        System.arraycopy(curData, 0, result, i, curData.length); i += curData.length;
        curData = secondEntry.getBytes();
        System.arraycopy(curData, 0, result, i, curData.length); i += curData.length;
        System.arraycopy(reserved4, 0, result, i, reserved4.length); i += reserved4.length;
        System.arraycopy(mbrSignature, 0, result, i, mbrSignature.length); i += mbrSignature.length;

        if(i != result.length)
            throw new RuntimeException("Internal error!");
        return result;
    }

    public boolean isTerminator() {
        return secondEntry.getLBAFirstSector() == 0 && secondEntry.getLBAPartitionLength() == 0;
    }

}
