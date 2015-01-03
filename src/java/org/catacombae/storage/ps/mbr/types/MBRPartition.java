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

package org.catacombae.storage.ps.mbr.types;
import org.catacombae.storage.ps.Partition;
import org.catacombae.util.Util;
import java.io.PrintStream;
import org.catacombae.storage.ps.PartitionType;
import org.catacombae.storage.ps.mbr.MBRPartitionType;

public class MBRPartition implements Partition {
    protected static final byte PARTITION_NOT_BOOTABLE = (byte)0x00;
    protected static final byte PARTITION_BOOTABLE     = (byte)0x80;

    protected final byte[] status = new byte[1];
    protected final byte[] firstSector = new byte[3];
    protected final byte[] partitionType = new byte[1];
    protected final byte[] lastSector = new byte[3];
    protected final byte[] lbaFirstSector = new byte[4];
    protected final byte[] lbaPartitionLength = new byte[4];

    private final int sectorSize;

    /** <code>data</code> is assumed to be at least (<code>offset</code>+16) bytes in length. */
    public MBRPartition(byte[] data, int offset, int sectorSize) {
	this(sectorSize);
	System.arraycopy(data, offset+0, status, 0, 1);
	System.arraycopy(data, offset+1, firstSector, 0, 3);
	System.arraycopy(data, offset+4, partitionType, 0, 1);
	System.arraycopy(data, offset+5, lastSector, 0, 3);
	System.arraycopy(data, offset+8, lbaFirstSector, 0, 4);
	System.arraycopy(data, offset+12, lbaPartitionLength, 0, 4);
    }
    protected MBRPartition(int sectorSize) {
	this.sectorSize = sectorSize;
    }
    public MBRPartition(MBRPartition source) {
	this(source, source.sectorSize);
    }
    public MBRPartition(MBRPartition source, int sectorSize) {
	this(sectorSize);
	System.arraycopy(source.status, 0, status, 0, 1);
	System.arraycopy(source.firstSector, 0, firstSector, 0, 3);
	System.arraycopy(source.partitionType, 0, partitionType, 0, 1);
	System.arraycopy(source.lastSector, 0, lastSector, 0, 3);
	System.arraycopy(source.lbaFirstSector, 0, lbaFirstSector, 0, 4);
	System.arraycopy(source.lbaPartitionLength, 0, lbaPartitionLength, 0, 4);
    }

    // Defined in Partition
    public long getStartOffset() { return Util.unsign(getLBAFirstSector())*sectorSize; }
    public long getLength() { return Util.unsign(getLBAPartitionLength())*sectorSize; }
    public PartitionType getType() {
        return getPartitionTypeAsEnum().getGeneralType();
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

    public MBRPartitionType getPartitionTypeAsEnum() {
        return MBRPartitionType.fromMBRType(getPartitionType());
    }

    public boolean isBootable() {
	return getStatus() == PARTITION_BOOTABLE;
    }
    public boolean isValid() {
	/* Calculate the coefficients numHead and numSec.
	 * (C*numHead*numSec) + (H*numSec) + (S-1) = LBA.
	 *
	 * Say that we have c1, h1, s1 and lba1, as well as c2, h2, s2, lba2.
	 */
	int beginLBA = getLBAFirstSector();
	byte[] beginCHS = getFirstSector();
	int beginS = beginCHS[1] & 0x3F;
	int beginH = beginCHS[0];
	int beginC = ((beginCHS[1] & 0xC0) >> 6) * 0xFF + beginCHS[2];

	int endLBA = beginLBA + getLBAPartitionLength() - 1;
	byte[] endCHS = getLastSector();
	int endS = endCHS[1] & 0x3F;
	int endH = endCHS[0];
	int endC = ((endCHS[1] & 0xC0) >> 6) * 0xFF + endCHS[2];





	byte statusByte = getStatus();
	return (statusByte == PARTITION_NOT_BOOTABLE || statusByte == PARTITION_BOOTABLE);
    }
    /** Returns true if isValid() evaluates to true, and partition type is not 0x00 (PARTITION_TYPE_UNUSED). */
    public boolean isUsed() {
	return isValid() && getPartitionTypeAsEnum() != MBRPartitionType.UNUSED;
    }

    @Override
    public String toString() {
	MBRPartitionType mpt = getPartitionTypeAsEnum();
	return (isBootable()?"Bootable ":"") + "MBR Partition (" + mpt +
	    (mpt == null?" [0x"+Util.toHexStringBE(getPartitionType())+"]":"") + ")";
    }

    public void printFields(PrintStream ps, String prefix) {
	ps.println(prefix + " status: 0x" + Util.toHexStringBE(getStatus()));
	ps.println(prefix + " firstSector: 0x" + Util.byteArrayToHexString(getFirstSector()));
	ps.println(prefix + " partitionType: 0x" + Util.toHexStringBE(getPartitionType()) + " (" + getPartitionTypeAsEnum().toString() + ")");
	ps.println(prefix + " lastSector: 0x" + Util.byteArrayToHexString(getLastSector()));
	ps.println(prefix + " lbaFirstSector: " + Util.unsign(getLBAFirstSector()));
	ps.println(prefix + " lbaPartitionLength: " + Util.unsign(getLBAPartitionLength()));
    }

    public void print(PrintStream ps, String prefix) {
        ps.println(prefix + this.getClass().getSimpleName() + ":");
	printFields(ps, prefix);
    }

    public byte[] getBytes() {
	byte[] result = new byte[16];
	int i = 0;
	System.arraycopy(status, 0, result, i, status.length); i += status.length;
	System.arraycopy(firstSector, 0, result, i, firstSector.length); i += firstSector.length;
	System.arraycopy(partitionType, 0, result, i, partitionType.length); i += partitionType.length;
	System.arraycopy(lastSector, 0, result, i, lastSector.length); i += lastSector.length;
	System.arraycopy(lbaFirstSector, 0, result, i, lbaFirstSector.length); i += lbaFirstSector.length;
	System.arraycopy(lbaPartitionLength, 0, result, i, lbaPartitionLength.length); i += lbaPartitionLength.length;

	if(i != result.length)
	    throw new RuntimeException("Internal error!");
	return result;
    }
}
