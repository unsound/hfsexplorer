/*-
 * Copyright (C) 2006-2007 Erik Larsson
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

package org.catacombae.hfsexplorer.partitioning;
import org.catacombae.hfsexplorer.Util;
import java.io.PrintStream;

public class MBRPartitionTable implements PartitionSystem {
    /* Until I figure out a way to detect sector size, it will be 512... */
    public static final int SECTOR_SIZE = 512;
    
    public static final short MBR_SIGNATURE = 0x55AA;
    public static final int IBM_EXTENDED_DATA_OFFSET = 0x018A;
    public static final int DISK_SIGNATURE_OFFSET = 0x01B8;
    public static final int MBR_PARTITIONS_OFFSET = 0x01BE;
    /*
     * struct MBRPartitionTable
     * size: 512 bytes
     *
     * BP   Size  Type              Variable name        Description
     * --------------------------------------------------------------
     * 394  9     byte[9]           optIBMExtendedData1  
     * 403  9     byte[9]           optIBMExtendedData2  
     * 412  9     byte[9]           optIBMExtendedData3  
     * 421  9     byte[9]           optIBMExtendedData4  
     * ~~~~~~~~~~~~~~~~~~~~~~~~[Not continuous]~~~~~~~~~~~~~~~~~~~~~~
     * 440  4     UInt32            optDiskSignature     
     * ~~~~~~~~~~~~~~~~~~~~~~~~[Not continuous]~~~~~~~~~~~~~~~~~~~~~~
     * 446  16*4  MBRPartition[4]   partitions           
     * 510  2     UInt16            mbrSignature         
     *
     */
    private final byte[] optIBMExtendedData1 = new byte[9];
    private final byte[] optIBMExtendedData2 = new byte[9];
    private final byte[] optIBMExtendedData3 = new byte[9];
    private final byte[] optIBMExtendedData4 = new byte[9];
    private final byte[] optDiskSignature = new byte[4];
    private final MBRPartition[] partitions = new MBRPartition[4];
    private final byte[] mbrSignature = new byte[2];
    
    /** <code>data</code> is assumed to be at least (<code>offset</code>+512) bytes in length. */
    public MBRPartitionTable(byte[] data, int offset) {
	System.arraycopy(data, offset+IBM_EXTENDED_DATA_OFFSET+0, optIBMExtendedData1, 0, 9);
	System.arraycopy(data, offset+IBM_EXTENDED_DATA_OFFSET+9, optIBMExtendedData2, 0, 9);
	System.arraycopy(data, offset+IBM_EXTENDED_DATA_OFFSET+18, optIBMExtendedData3, 0, 9);
	System.arraycopy(data, offset+IBM_EXTENDED_DATA_OFFSET+27, optIBMExtendedData4, 0, 9);
	System.arraycopy(data, offset+DISK_SIGNATURE_OFFSET, optDiskSignature, 0, 4);
	for(int i = 0; i < 4; ++i)
	    partitions[i] = new MBRPartition(data, offset+MBR_PARTITIONS_OFFSET+i*16, SECTOR_SIZE);
	System.arraycopy(data, offset+MBR_PARTITIONS_OFFSET+64, mbrSignature, 0, 2);
    }
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
    
    public boolean isValid() {
	return getMBRSignature() == MBR_SIGNATURE;
	// More validity contraints could be added later... like that the partitions should be in order and
	// that their lengths should be non negative and stay within device borders...
    }
    
    public int getPartitionCount() {
	int num = 0;
	for(MBRPartition mp : getPartitions()) {
	    if(mp.isValid()) ++num;
	    else break; // we don't check the later ones
	}
	return num;
    }
    
    public void printFields(PrintStream ps, String prefix) {
	ps.println(prefix + " diskSignature: 0x" + Util.toHexStringBE(getOptionalDiskSignature()) + " (optional, and possibly incorrect)");
	for(int i = 0; i < partitions.length; ++i) {
	    ps.println(prefix + " partitions[" + i + "]:");
	    if(partitions[i].isValid()) {
		partitions[i].print(ps, prefix + "  ");
	    }
	    else
		ps.println(prefix + "  [Invalid data]");
	}
	ps.println(prefix + " mbrSignature: 0x" + Util.toHexStringBE(getMBRSignature()));
    }

    public void print(PrintStream ps, String prefix) {
	ps.println("MBRPartitionTable:");
	printFields(ps, prefix);
    }
    
    public Partition getPartition(int index) {
	MBRPartition p = partitions[index];
	if(p.isValid())
	    return p;
	else
	    throw new ArrayIndexOutOfBoundsException(index);
    }
    
    public String getLongName() { return "Master Boot Record"; }
    public String getShortName() { return "MBR"; }
}
