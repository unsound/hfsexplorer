/*-
 * Copyright (C) 2007 Erik Larsson
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
import org.catacombae.hfsexplorer.*;

public class MutableMasterBootRecord extends MasterBootRecord {
//     private MutableMBRPartitionTable() {
// 	throw new RuntimeException("Default boot code not yet written.");
//     }
    public MutableMasterBootRecord(MasterBootRecord mbr) {
	super(mbr);
    }
    
    public void setOptionalIBMExtendedData1(byte[] data) { copyData(data, 0, optIBMExtendedData1); }
    public void setOptionalIBMExtendedData2(byte[] data) { copyData(data, 0, optIBMExtendedData2); }
    public void setOptionalIBMExtendedData3(byte[] data) { copyData(data, 0, optIBMExtendedData3); }
    public void setOptionalIBMExtendedData4(byte[] data) { copyData(data, 0, optIBMExtendedData4); }
    public void setOptionalDiskSignature(int sig) {
	Util.arrayCopy(Util.toByteArrayBE(sig), optDiskSignature);
    }
    public void setPartition(int i, MBRPartition partition) {
	partitions[i] = partition;
    }
    public void setMBRSignature(short sig) {
	Util.arrayCopy(Util.toByteArrayBE(sig), mbrSignature);
    }
    
    private static void copyData(byte[] data, int off, byte[] dest) {
	copyData(data, off, dest, dest.length);
    }
    private static void copyData(byte[] data, int off, byte[] dest, int len) {
	if(off+len > data.length)
	    throw new IllegalArgumentException("Length of input data must be " + len + ".");
	System.arraycopy(data, off, dest, 0, len);
    }
}
