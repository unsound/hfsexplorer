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

public class MBRPartition {
    protected static final byte PARTITION_NOT_BOOTABLE = 0x00;
    protected static final byte PARTITION_BOOTABLE = (byte)0x80;
	
    private final byte[] status = new byte[1];
    private final byte[] firstSector = new byte[3];
    private final byte[] partitionType = new byte[1];
    private final byte[] lastSector = new byte[3];
    private final byte[] lbaFirstSector = new byte[4];
    private final byte[] lbaPartitionLength = new byte[4];
    /** <code>data</code> is assumed to be at least (<code>offset</code>+16) bytes in length. */
    public MBRPartition(byte[] data, int offset) {
	System.arraycopy(data, offset+0, status, 0, 1);
	System.arraycopy(data, offset+1, firstSector, 0, 3);
	System.arraycopy(data, offset+4, partitionType, 0, 1);
	System.arraycopy(data, offset+5, lastSector, 0, 3);
	System.arraycopy(data, offset+8, lbaFirstSector, 0, 4);
	System.arraycopy(data, offset+12, lbaPartitionLength, 0, 4);
    }
	
    public byte getStatus() { return Util.readByteLE(status); }
    /** The result is returned in CHS form. This representation is deprecated and cannot handle
	today's storage sizes. Preferrably use the getLBA-methods.*/
    public byte[] getFirstSector() { return Util.createCopy(firstSector); }
    public byte getPartitionType() { return Util.readByteLE(partitionType); }
    /** The result is returned in CHS form. This representation is deprecated and cannot handle
	today's storage sizes. Preferrably, use the getLBA-methods. */
    public byte[] getLastSector() { return Util.createCopy(lastSector); }
    public int getLBAFirstSector() { return Util.readIntLE(lbaFirstSector); }
    public int getLBAPartitionLength() { return Util.readIntLE(lbaPartitionLength); }
    public boolean isValid() {
	byte status = getStatus();
	return (status == PARTITION_NOT_BOOTABLE || status == PARTITION_BOOTABLE);
    }
}
