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

package org.catacombae.jparted.lib.ps.mbr;

import java.util.HashMap;
import org.catacombae.jparted.lib.ps.PartitionType;

/**
 *
 * @author erik
 */
public enum MBRPartitionType {
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

    UNUSED((byte) 0x00, PartitionType.EMPTY),
    FAT12((byte) 0x01, PartitionType.FAT12),
    FAT16_SMALL((byte) 0x04, PartitionType.FAT16),
    DOS_EXTENDED((byte) 0x05, PartitionType.DOS_EXTENDED),
    FAT16_LARGE((byte) 0x06, PartitionType.FAT16),
    NT_INSTALLABLE_FS((byte) 0x07, PartitionType.NT_OS2_IFS),
    FAT32((byte) 0x0B, PartitionType.FAT32),
    FAT32_INT13HX((byte) 0x0C, PartitionType.FAT32),
    FAT16_LARGE_INT13HX((byte) 0x0E, PartitionType.FAT16),
    DOS_EXTENDED_INT13HX((byte) 0x0F, PartitionType.DOS_EXTENDED),
    EISA_OR_OEM((byte) 0x12, PartitionType.SPECIAL),
    DYNAMIC_VOLUME((byte) 0x42, PartitionType.SPECIAL),
    PM_HIBERNATION((byte) 0x84, PartitionType.SPECIAL),
    NT_MULTIDISK_FAT16((byte) 0x86, PartitionType.SPECIAL),
    NT_MULTIDISK_NTFS((byte) 0x87, PartitionType.SPECIAL),
    LAPTOP_HIBERNATION((byte) 0xA0, PartitionType.SPECIAL),
    DELL_OEM((byte) 0xDE, PartitionType.SPECIAL),
    IBM_OEM((byte) 0xFE, PartitionType.SPECIAL),
    GPT_PROTECTIVE((byte) 0xEE, PartitionType.GPT_PROTECTIVE),
    EFI_SYSTEM_ON_MBR((byte) 0xEF, PartitionType.EFI_SYSTEM),
    APPLE_UFS((byte) 0xA8, PartitionType.APPLE_UFS), // UFS in NeXT format (only slightly different)
    APPLE_HFS((byte) 0xAF, PartitionType.APPLE_HFS_CONTAINER), // Used for HFS, HFS+ and HFSX
    LINUX_SWAP((byte) 0x82, PartitionType.LINUX_SWAP),
    LINUX_NATIVE((byte) 0x83, PartitionType.LINUX_NATIVE), // Used for reiserfs, ext2, ext3 and many more
    UNKNOWN;

    private static HashMap<Byte,MBRPartitionType> reverseLookupTable;
    private final Byte mbrType;
    private final PartitionType enumType;

    private MBRPartitionType(byte mbrType, PartitionType enumType) {
        if(enumType == null)
            throw new IllegalArgumentException("enumType == null");
        this.mbrType = mbrType;
        this.enumType = enumType;
        
        addReverseLookupReference(mbrType, this);
    }

    private MBRPartitionType() {
        this.mbrType = null;
        this.enumType = null;
    }

    public Byte getMBRType() {
        return mbrType;
    }

    public PartitionType getGeneralType() {
        return enumType;
    }

    private static void addReverseLookupReference(byte b, MBRPartitionType t) {
        if(reverseLookupTable == null)
            reverseLookupTable = new HashMap<Byte,MBRPartitionType>();
        reverseLookupTable.put(b, t);
    }

    /**
     * Does a reverse lookup from an MBR type byte to a MBRPartitionType.
     * @param mbrType
     * @return the MBRPartitionType corresponding to <code>mbrType</code>, or
     * <code>null</code> if the
     */
    public static MBRPartitionType fromMBRType(byte mbrType) {
        MBRPartitionType type = reverseLookupTable.get(mbrType);
        if(type == null)
            return UNKNOWN;
        else
            return type;
    }
}

