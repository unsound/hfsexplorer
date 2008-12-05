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
import org.catacombae.hfsexplorer.Util;
import java.io.PrintStream;
import java.util.Hashtable;
import org.catacombae.jparted.lib.ps.PartitionType;
import org.catacombae.jparted.lib.ps.mbr.MBRPartitionType;

public class MBRPartition implements Partition {
    protected static final byte PARTITION_NOT_BOOTABLE = (byte)0x00;
    protected static final byte PARTITION_BOOTABLE     = (byte)0x80;  
    
    /** By some reason I couldn't put this variable inside MBRPartitionType. Initalizer error. */
    private static final Hashtable<Byte,MBRPartitionType> byteMap = new Hashtable<Byte,MBRPartitionType>();
    //public static enum MBRPartitionType {
	/* Partition type data from microsoft 
         * (http://technet2.microsoft.com/WindowsServer/en/library/bdeda920-1f08-4683-9ffb-7b4b50df0b5a1033.mspx?mfr=true)
         * 
	 * 0x01 FAT12 primary partition or logical drive (fewer than 32,680 sectors in the volume)
	 * 0x04 FAT16 partition or logical drive (32,680-65,535 sectors or 16 MB-33 MB)
	 * 0x05 Extended partition
	 * 0x06 BIGDOS FAT16 partition or logical drive (33 MB-4 GB)
	 * 0x07 Installable File System (NTFS partition or logical drive)
	 * 0x0B FAT32 partition or logical drive
	 * 0x0C FAT32 partition or logical drive using BIOS INT 13h extensions
	 * 0x0E BIGDOS FAT16 partition or logical drive using BIOS INT 13h extensions
	 * 0x0F Extended partition using BIOS INT 13h extensions
	 * 0x12 EISA partition or OEM partition
	 * 0x42 Dynamic volume
	 * 0x84 Power management hibernation partition
	 * 0x86 Multidisk FAT16 volume created by using Windows NT 4.0
	 * 0x87 Multidisk NTFS volume created by using Windows NT 4.0
	 * 0xA0 Laptop hibernation partition
	 * 0xDE Dell OEM partition
	 * 0xFE IBM OEM partition
	 * 0xEE GPT partition
	 * 0xEF EFI System partition on an MBR disk
	 */
	/*PARTITION_TYPE_UNUSED               ((byte)0x00),
	PARTITION_TYPE_FAT12                ((byte)0x01),
	PARTITION_TYPE_FAT16_SMALL          ((byte)0x04),
	PARTITION_TYPE_DOS_EXTENDED         ((byte)0x05),
	PARTITION_TYPE_FAT16_LARGE          ((byte)0x06),
	PARTITION_TYPE_NT_INSTALLABLE_FS    ((byte)0x07),
	PARTITION_TYPE_FAT32                ((byte)0x08),
	PARTITION_TYPE_FAT32_INT13HX        ((byte)0x0C),
	PARTITION_TYPE_FAT16_LARGE_INT13HX  ((byte)0x0E),
	PARTITION_TYPE_DOS_EXTENDED_INT13HX ((byte)0x0F),
	PARTITION_TYPE_EISA_OR_OEM          ((byte)0x12),
	PARTITION_TYPE_DYNAMIC_VOLUME       ((byte)0x42),
	PARTITION_TYPE_PM_HIBERNATION       ((byte)0x84),
	PARTITION_TYPE_NT_MULTIDISK_FAT16   ((byte)0x86),
	PARTITION_TYPE_NT_MULTIDISK_NTFS    ((byte)0x87),
	PARTITION_TYPE_LAPTOP_HIBERNATION   ((byte)0xA0),
	PARTITION_TYPE_DELL_OEM             ((byte)0xDE),
	PARTITION_TYPE_IBM_OEM              ((byte)0xFE),
	PARTITION_TYPE_GPT                  ((byte)0xEE),
	PARTITION_TYPE_EFI_SYSTEM_ON_MBR    ((byte)0xEF),
	PARTITION_TYPE_APPLE_UFS            ((byte)0xA8), // UFS in NeXT format (only slightly different)
	PARTITION_TYPE_APPLE_HFS            ((byte)0xAF), // Used for HFS, HFS+ and HFSX
	PARTITION_TYPE_LINUX_SWAP           ((byte)0x82),
	PARTITION_TYPE_LINUX_NATIVE         ((byte)0x83), // Used for reiserfs, ext2, ext3 and many more
	UNKNOWN_PARTITION_TYPE; // Returned when no known type can be matched
	
	private byte type;
	
	private MBRPartitionType(byte type) {
	    this.type = type;
	    byteMap.put(type, this);
	}
	private MBRPartitionType() {}
	
	private void register(byte type) {
	    byteMap.put(type, this);
	}
	
	public static MBRPartitionType getType(byte b) {
	    MBRPartitionType type = byteMap.get(b);
	    if(type != null)
		return type;
	    else
		return UNKNOWN_PARTITION_TYPE;
	}
    }*/
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

    /*
    private PartitionType convertPartitionType(MBRPartitionType mpt) {
	// I haven't bothered to generalize the partition types in detail...
	switch(mpt) {
	case FAT12:
	    return PartitionType.FAT12;
	case FAT16_SMALL:
	    return PartitionType.FAT16;
	case DOS_EXTENDED:
	    return PartitionType.DOS_EXTENDED;
	case FAT16_LARGE:
	    return PartitionType.FAT16;
	case NT_INSTALLABLE_FS:
	    return PartitionType.NT_OS2_IFS; // Very simplified...
	case FAT32:
	    return PartitionType.FAT32;
	case FAT32_INT13HX:
	    return PartitionType.FAT32;
	case FAT16_LARGE_INT13HX:
	    return PartitionType.FAT16;
	case DOS_EXTENDED_INT13HX:
	    return PartitionType.DOS_EXTENDED;
	case EISA_OR_OEM:
	    return PartitionType.UNKNOWN;
	case DYNAMIC_VOLUME:
	    return PartitionType.UNKNOWN;
	case PM_HIBERNATION:
	    return PartitionType.UNKNOWN;
	case NT_MULTIDISK_FAT16:
	    return PartitionType.UNKNOWN;
	case NT_MULTIDISK_NTFS:
	    return PartitionType.UNKNOWN;
	case LAPTOP_HIBERNATION:
	    return PartitionType.UNKNOWN;
	case DELL_OEM:
	    return PartitionType.UNKNOWN;
	case IBM_OEM:
	    return PartitionType.UNKNOWN;
	case GPT_PROTECTIVE:
	    return PartitionType.UNKNOWN;
	case EFI_SYSTEM_ON_MBR:
	    return PartitionType.UNKNOWN;
	case APPLE_UFS:
	    return PartitionType.APPLE_UNIX_SVR2;
	case APPLE_HFS:
	    return PartitionType.APPLE_HFS_CONTAINER;
	case LINUX_NATIVE:
	    return PartitionType.LINUX_NATIVE;
	case LINUX_SWAP:
	    return PartitionType.LINUX_SWAP;
	default:
	    return PartitionType.UNKNOWN;
	}
    }*/
    
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
