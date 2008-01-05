/*-
 * Copyright (C) 2006-2007 Erik Larsson
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

package org.catacombae.hfsexplorer.partitioning;

import java.io.PrintStream;
import java.util.LinkedList;
import org.catacombae.hfsexplorer.Util;

/**
 *
 * @author Erik
 */
public class MasterBootRecord {
    public static final short MBR_SIGNATURE = 0x55AA;
    public static final int IBM_EXTENDED_DATA_OFFSET = 0x018A;
    public static final int OPTIONAL_DISK_SIGNATURE_OFFSET = 0x01B8;
    public static final int MBR_PARTITIONS_OFFSET = 0x01BE;
    /*
     * struct MasterBootRecord
     * size: 512 bytes
     *
     * BP   Size  Type              Variable name        Description
     * --------------------------------------------------------------
     * 0    394   byte[394]         reserved1            possibly executable code
     * 394  9     byte[9]           optIBMExtendedData1  either part of the executable code, or IBM extended data
     * 403  9     byte[9]           optIBMExtendedData2  either part of the executable code, or IBM extended data
     * 412  9     byte[9]           optIBMExtendedData3  either part of the executable code, or IBM extended data
     * 421  9     byte[9]           optIBMExtendedData4  either part of the executable code, or IBM extended data
     * 430  10    byte[10]          reserved2            possibly executable code
     * 440  4     UInt32            optDiskSignature     either part of the executable code, or IBM extended data
     * 444  2     byte[2]           reserved3            possibly executable code
     * 446  16*4  MBRPartition[4]   partitions           the MBR partition table
     * 510  2     UInt16            mbrSignature         signature that should always be present
     *
     */
    protected final byte[] reserved1 = new byte[394];
    protected final byte[] optIBMExtendedData1 = new byte[9];
    protected final byte[] optIBMExtendedData2 = new byte[9];
    protected final byte[] optIBMExtendedData3 = new byte[9];
    protected final byte[] optIBMExtendedData4 = new byte[9];
    protected final byte[] reserved2 = new byte[10];
    protected final byte[] optDiskSignature = new byte[4];
    protected final byte[] reserved3 = new byte[2];
    protected final MBRPartition[] partitions = new MBRPartition[4];
    protected final byte[] mbrSignature = new byte[2];
    
    private final LinkedList<Partition> tempList = new LinkedList<Partition>(); // getUsedPartitionEntries()
    
    /** <code>data</code> is assumed to be at least (<code>offset</code>+512) bytes in length. */
    public MasterBootRecord(byte[] data, int offset, int sectorSize) {
	System.arraycopy(data, offset+0, reserved1, 0, reserved1.length);
	System.arraycopy(data, offset+IBM_EXTENDED_DATA_OFFSET+0, optIBMExtendedData1, 0, 9);
	System.arraycopy(data, offset+IBM_EXTENDED_DATA_OFFSET+9, optIBMExtendedData2, 0, 9);
	System.arraycopy(data, offset+IBM_EXTENDED_DATA_OFFSET+18, optIBMExtendedData3, 0, 9);
	System.arraycopy(data, offset+IBM_EXTENDED_DATA_OFFSET+27, optIBMExtendedData4, 0, 9);
	System.arraycopy(data, offset+430, reserved2, 0, reserved2.length);
	System.arraycopy(data, offset+OPTIONAL_DISK_SIGNATURE_OFFSET, optDiskSignature, 0, 4);
	System.arraycopy(data, offset+444, reserved3, 0, reserved3.length);
	for(int i = 0; i < 4; ++i)
	    partitions[i] = new MBRPartition(data, offset+MBR_PARTITIONS_OFFSET+i*16, sectorSize);
	System.arraycopy(data, offset+MBR_PARTITIONS_OFFSET+64, mbrSignature, 0, 2);
	
	if(!Util.arrayRegionsEqual(getBytes(), 0, getStructSize(), data, offset, getStructSize()))
	    throw new RuntimeException("Internal error!");
    }
    
    public MasterBootRecord(MasterBootRecord source) {
	System.arraycopy(source.reserved1, 0, reserved1, 0, reserved1.length);
	System.arraycopy(source.optIBMExtendedData1, 0, optIBMExtendedData1, 0, optIBMExtendedData1.length);
	System.arraycopy(source.optIBMExtendedData2, 0, optIBMExtendedData2, 0, optIBMExtendedData2.length);
	System.arraycopy(source.optIBMExtendedData3, 0, optIBMExtendedData3, 0, optIBMExtendedData3.length);
	System.arraycopy(source.optIBMExtendedData4, 0, optIBMExtendedData4, 0, optIBMExtendedData4.length);
	System.arraycopy(source.reserved2, 0, reserved2, 0, reserved2.length);
	System.arraycopy(source.optDiskSignature, 0, optDiskSignature, 0, optDiskSignature.length);
	System.arraycopy(source.reserved3, 0, reserved3, 0, reserved3.length);
	for(int i = 0; i < 4; ++i)
	    partitions[i] = new MBRPartition(source.partitions[i]);
	System.arraycopy(source.mbrSignature, 0, mbrSignature, 0, mbrSignature.length);
    }
    
    public static int getStructSize() { return 512; }
    
    /** This is an optional field, and might contain unexpected and invalid data. */
    public byte[] getOptionalIBMExtendedData1() { return Util.createCopy(optIBMExtendedData1); }
    /** This is an optional field, and might contain unexpected and invalid data. */
    public byte[] getOptionalIBMExtendedData2() { return Util.createCopy(optIBMExtendedData2); }
    /** This is an optional field, and might contain unexpected and invalid data. */
    public byte[] getOptionalIBMExtendedData3() { return Util.createCopy(optIBMExtendedData3); }
    /** This is an optional field, and might contain unexpected and invalid data. */
    public byte[] getOptionalIBMExtendedData4() { return Util.createCopy(optIBMExtendedData4); }
    /** This is an optional field, and might contain unexpected and invalid data. */
    public int getOptionalDiskSignature() { return Util.readIntBE(optDiskSignature); }
    public MBRPartition[] getPartitions() {
	MBRPartition[] result = new MBRPartition[partitions.length];
	for(int i = 0; i < result.length; ++i)
	    result[i] = partitions[i];
	return result;
    }
    public short getMBRSignature() { return Util.readShortBE(mbrSignature); }
    
    public void printFields(PrintStream ps, String prefix) {
	ps.println(prefix + " diskSignature: 0x" + Util.toHexStringBE(getOptionalDiskSignature()) + " (optional, and possibly incorrect)");
        ps.println(prefix + " partitions:");
	for(int i = 0; i < partitions.length; ++i) {
	    ps.println(prefix + "  [" + i + "]:");
	    if(partitions[i].isValid()) {
		partitions[i].print(ps, prefix + "   ");
	    }
	    else
		ps.println(prefix + "   [Invalid data]");
	}
	ps.println(prefix + " mbrSignature: 0x" + Util.toHexStringBE(getMBRSignature()));
    }

    public void print(PrintStream ps, String prefix) {
	ps.println(prefix + this.getClass().getSimpleName() + ":");
	printFields(ps, prefix);
    }
    
    public boolean isValid() {
	return (getMBRSignature() == MBR_SIGNATURE) && (getValidPartitionCount() == 4);
	// More validity contraints could be added later... like that the partitions should be in order and
	// that their lengths should be non negative and stay within device borders...
    }
    
    public int getPartitionCount() { return partitions.length; }
    public int getValidPartitionCount() {
	int num = 0;
	for(MBRPartition mp : getPartitions()) {
	    if(mp.isValid()) ++num;
	    //else break; // we don't check the later ones
	}
	return num;
    }
    
    public int getUsedPartitionCount() {
	int num = 0;
	for(MBRPartition mp : getPartitions()) {
	    if(mp.isUsed()) ++num;
	    //else break; // we don't check the later ones
	}
	return num;
    }
    
    public Partition[] getPartitionEntries() {
	return getPartitions();
    }
    
    public Partition getPartitionEntry(int index) {
	MBRPartition p = partitions[index];
	if(p.isValid())
	    return p;
	else
	    throw new ArrayIndexOutOfBoundsException(index);
    }
    
    public Partition[] getUsedPartitionEntries() {
	tempList.clear();
	for(MBRPartition mp : getPartitions()) {
	    if(mp.isUsed()) tempList.addLast(mp);
	    //else break; // we don't check the later ones
	}
	return tempList.toArray(new Partition[tempList.size()]);
    }


    public byte[] getBytes() {
	byte[] result = new byte[512];
	int i = 0;
	System.arraycopy(reserved1, 0, result, i, reserved1.length); i += reserved1.length;
	System.arraycopy(optIBMExtendedData1, 0, result, i, optIBMExtendedData1.length); i += optIBMExtendedData1.length;
	System.arraycopy(optIBMExtendedData2, 0, result, i, optIBMExtendedData2.length); i += optIBMExtendedData2.length;
	System.arraycopy(optIBMExtendedData3, 0, result, i, optIBMExtendedData3.length); i += optIBMExtendedData3.length;
	System.arraycopy(optIBMExtendedData4, 0, result, i, optIBMExtendedData4.length); i += optIBMExtendedData4.length;
	System.arraycopy(reserved2, 0, result, i, reserved2.length); i += reserved2.length;
	System.arraycopy(optDiskSignature, 0, result, i, optDiskSignature.length); i += optDiskSignature.length;
	System.arraycopy(reserved3, 0, result, i, reserved3.length); i += reserved3.length;
	for(int j = 0; j < partitions.length; ++j) {
	    byte[] curData = partitions[j].getBytes();
	    System.arraycopy(curData, 0, result, i, curData.length); i += curData.length;
	}
	System.arraycopy(mbrSignature, 0, result, i, mbrSignature.length); i += mbrSignature.length;
	
	if(i != result.length)
	    throw new RuntimeException("Internal error!");
	return result;
    }

}
