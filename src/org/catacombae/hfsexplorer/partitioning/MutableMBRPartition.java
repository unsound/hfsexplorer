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

public class MutableMBRPartition extends MBRPartition {
//     public MutableMBRPartition(int blockSize) {
// 	super(blockSize);
//     }
    public MutableMBRPartition(MBRPartition source) {
	super(source);
    }
    public void setStatus(byte b) { status[0] = b; }
    public void setFirstSector(byte[] chs, int off) { copyData(chs, off, firstSector); }
    public void setPartitionType(byte b) { partitionType[0] = b; }
    public void setLastSector(byte[] chs, int off) { copyData(chs, off, lastSector); }
    public void setLBAFirstSector(int lba) { Util.arrayCopy(Util.toByteArrayLE(lba), lbaFirstSector); }
    public void setLBAPartitionLength(int lba) { Util.arrayCopy(Util.toByteArrayLE(lba), lbaPartitionLength); }
    
    private static void copyData(byte[] data, int off, byte[] dest) {
	copyData(data, off, dest, dest.length);
    }
    private static void copyData(byte[] data, int off, byte[] dest, int len) {
	if(off+len > data.length)
	    throw new IllegalArgumentException("Length of input data must be " + len + ".");
	System.arraycopy(data, off, dest, 0, len);
    }
}
