/*-
 * Copyright (C) 2006-2008 Erik Larsson
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

package org.catacombae.storage.ps.gpt;

import java.nio.LongBuffer;
import java.util.HashMap;
import org.catacombae.storage.ps.PartitionType;

/**
 * @author <a href="https://catacombae.org" target="_top">Erik Larsson</a>
 */
public enum GPTPartitionType {
    /*
     * GUID Partition types
     * --------------------
     *
     * Wikipedia info (as always, it can be wrong... especially as people seem
     * to write GUIDs with differing endianness):
     *   EFI Specification:
     *     Unused entry                     00000000-0000-0000-0000-000000000000
     *     MBR partition scheme             024DEE41-33E7-11D3-9D69-0008C781F39F
     *     EFI System Partition             C12A7328-F81F-11D2-BA4B-00A0C93EC93B
     *   Windows:
     *     Microsoft Reserved Partition     E3C9E316-0B5C-4DB8-817D-F92DF00215AE
     *     Basic Data Partition             EBD0A0A2-B9E5-4433-87C0-68B6B72699C7
     *     LDM metadata partition           5808C8AA-7E8F-42E0-85D2-E1E90434CFB3
     *     LDM data partition               AF9B60A0-1431-4F62-BC68-3311714A69AD
     *   HP-UX:
     *     Data partition                   75894C1E-3AEB-11D3-B7C1-7B03A0000000
     *     Service Partition                E2A1E728-32E3-11D6-A682-7B03A0000000
     *   Linux:
     *     Data partition                   EBD0A0A2-B9E5-4433-87C0-68B6B72699C7
     *     RAID partition                   A19D880F-05FC-4D3B-A006-743F0F84911E
     *     Swap partition                   0657FD6D-A4AB-43C4-84E5-0933C84B4F4F
     *     Logical Volume Manager partition E6D6D379-F507-44C2-A23C-238F2A3DF928
     *     Reserved                         8DA63339-0007-60C0-C436-083AC8230908
     *   FreeBSD:
     *     Data partition                   516E7CB4-6ECF-11D6-8FF8-00022D09712B
     *     Swap partition                   516E7CB5-6ECF-11D6-8FF8-00022D09712B
     *     Unix File System (UFS) partition 516E7CB6-6ECF-11D6-8FF8-00022D09712B
     *     Vinum volume manager partition   516E7CB8-6ECF-11D6-8FF8-00022D09712B
     *   Mac OS X:
     *     Apple HFS/HFS+/HFSX partition    48465300-0000-11AA-AA11-00306543ECAC
     *     Apple UFS                        55465300-0000-11AA-AA11-00306543ECAC
     *     Apple RAID partition             52414944-0000-11AA-AA11-00306543ECAC
     *     Apple RAID partition, offline    52414944-5F4F-11AA-AA11-00306543ECAC
     *     Apple Boot partition             426F6F74-0000-11AA-AA11-00306543ECAC
     *     Apple Label                      4C616265-6C00-11AA-AA11-00306543ECAC
     *     Apple TV Recovery partition      5265636F-7665-11AA-AA11-00306543ECAC
     *   Solaris:
     *     Boot partition                   6A82CB45-1DD2-11B2-99A6-080020736631
     *     Root partition                   6A85CF4D-1DD2-11B2-99A6-080020736631
     *     Swap partition                   6A87C46F-1DD2-11B2-99A6-080020736631
     *     Backup partition                 6A8B642B-1DD2-11B2-99A6-080020736631
     *     /usr partition                   6A898CC3-1DD2-11B2-99A6-080020736631
     *     /var partition                   6A8EF2E9-1DD2-11B2-99A6-080020736631
     *     /home partition                  6A90BA39-1DD2-11B2-99A6-080020736631
     *     EFI_ALTSCTR                      6A9283A5-1DD2-11B2-99A6-080020736631
     *     Reserved partition types         6A945A3B-1DD2-11B2-99A6-080020736631
     *                                      6A9630D1-1DD2-11B2-99A6-080020736631
     *                                      6A980767-1DD2-11B2-99A6-080020736631
     *                                      6A96237F-1DD2-11B2-99A6-080020736631
     *                                      6A8D2AC7-1DD2-11B2-99A6-080020736631
     *
     * More info on GUID textual representations:
     *   GUIDs written on the form xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx can be a
     *   little confusing for the average big endian-user. The first field (4
     *   bytes) is supposed to be interpreted as the little endian-form of a
     *   32-bit integer, and the next two bytes are interpreted as little endian
     *   16-bit integers. Then comes 2 individual bytes (like a big endian
     *   16-bit integer), followed by 6 individual bytes (like a big endian
     *   48-bit integer). All in hexadecimal form of course, uppercase, with
     *   hyphens to separate the different parts.
     *
     *   NOTE: This means that the information supplied by microsoft (see
     *   "Microsoft info" above) in:
     *     http://technet2.microsoft.com/WindowsServer/en/library/bdeda920-1f08-
     *     4683-9ffb-7b4b50df0b5a1033.mspx#w2k3tr_basic_how_fgkm
     *   is inaccurate, given the little endian representation. The first,
     *   second and third fields should be swapped in order for the
     *   representation to be correct.
     *
     *   The enum fields in this implementation should be accurate.
     */

    /**
     * Unused GPT partition entry type:
     *     <code>00000000-0000-0000-0000-000000000000</code>
     */
    PARTITION_TYPE_UNUSED_ENTRY(0x0000000000000000L, 0x0000000000000000L,
        PartitionType.EMPTY),

    /**
     * EFI system partition type:
     *     <code>C12A7328-F81F-11D2-BA4B-00A0C93EC93B</code>
     */
    PARTITION_TYPE_EFI_SYSTEM(0x28732AC11FF8D211L, 0xBA4B00A0C93EC93BL,
        PartitionType.EFI_SYSTEM),

    /**
     * Microsoft reserved partition:
     *     <code>E3C9E316-0B5C-4DB8-817D-F92DF00215AE</code>
     */
    PARTITION_TYPE_MICROSOFT_RESERVED(0x16E3C9E35C0BB84DL, 0x817DF92DF00215AEL,
        PartitionType.SPECIAL),

    /**
     * Microsoft basic data partition (also used for Linux native filesystems):
     *     <code>EBD0A0A2-B9E5-4433-87C0-68B6B72699C7</code>
     */
    PARTITION_TYPE_PRIMARY_PARTITION(0xA2A0D0EBE5B93344L, 0x87C068B6B72699C7L,
        PartitionType.NT_OS2_IFS),

    /**
     * Microsoft Logical Disk Manager metadata partition type:
     *     <code>5808C8AA-7E8F-42E0-85D2-E1E90434CFB3</code>
     */
    PARTITION_TYPE_LDM_METADATA(0xAAC808588F7EE042L, 0x85D2E1E90434CFB3L,
        PartitionType.SPECIAL),

    /**
     * Microsoft Logical Disk Manager data partition type:
     *     <code>AF9B60A0-1431-4F62-BC68-3311714A69AD</code>
     */
    PARTITION_TYPE_LDM_DATA(0xA0609BAF3114624FL, 0xBC683311714A69ADL,
        PartitionType.SPECIAL),

    /**
     * Apple HFS(+/X) partition type:
     *     <code>48465300-0000-11AA-AA11-00306543ECAC</code>
     */
    PARTITION_TYPE_APPLE_HFS(0x005346480000AA11L, 0xAA1100306543ECACL,
        PartitionType.APPLE_HFS_CONTAINER),

    /**
     * Apple Boot partition type:
     *     <code>426F6F74-0000-11AA-AA11-00306543ECAC</code>
     */
    PARTITION_TYPE_APPLE_BOOT(0x746F6F420000AA11L, 0xAA1100306543ECACL,
        PartitionType.SPECIAL),

    /**
     * Apple APFS partition type:
     *     <code>7C3457EF-0000-11AA-AA11-00306543ECAC</code>
     */
    PARTITION_TYPE_APPLE_APFS(0xEF57347C0000AA11L, 0xAA1100306543ECACL,
        PartitionType.SPECIAL),

    /**
     * Linux Swap partition type:
     *     <code>0657FD6D-A4AB-43C4-84E5-0933C84B4F4F</code>
     */
    PARTITION_TYPE_LINUX_SWAP(0x6DFD5706ABA4C443L, 0x84E50933C84B4F4FL,
        PartitionType.LINUX_SWAP),

    /** Returned when no known type can be matched. */
    UNKNOWN_PARTITION_TYPE;

    private static HashMap<LongBuffer, GPTPartitionType> reverseLookupTable;

    private final Long typeGUIDMsb;
    private final Long typeGUIDLsb;
    private final PartitionType enumType;

    private GPTPartitionType(long typeGUIDMsb, long typeGUIDLsb,
            PartitionType enumType)
    {
        this.typeGUIDMsb = typeGUIDMsb;
        this.typeGUIDLsb = typeGUIDLsb;
        this.enumType = enumType;
        addReverseLookupReference(
                LongBuffer.wrap(new long[] { typeGUIDMsb, typeGUIDLsb }), this);
    }

    private GPTPartitionType() {
        this.typeGUIDMsb = null;
        this.typeGUIDLsb = null;
        this.enumType = null;
    }

    private static void addReverseLookupReference(LongBuffer lb,
            GPTPartitionType t)
    {
        if(reverseLookupTable == null)
            reverseLookupTable = new HashMap<LongBuffer, GPTPartitionType>();
        reverseLookupTable.put(lb, t);
    }

    public static GPTPartitionType getType(long typeGUIDMsb, long typeGUIDLsb) {
        GPTPartitionType type =
                reverseLookupTable.get(LongBuffer.wrap(
                new long[] { typeGUIDMsb, typeGUIDLsb }));
        if(type != null)
            return type;
        else
            return UNKNOWN_PARTITION_TYPE;
    }

    public byte[] getBytes() {
        if(typeGUIDMsb != null && typeGUIDLsb != null) {
            byte[] result = new byte[16];
            for(int i = 0; i < 8; ++i) {
                result[i] = (byte) ((typeGUIDMsb >> ((7 - i) * 8)) & 0xFF);
            }
            for(int i = 0; i < 8; ++i) {
                result[8 + i] = (byte) ((typeGUIDLsb >> ((7 - i) * 8)) & 0xFF);
            }
            return result;
        }
        else
            return null;
    }
}
