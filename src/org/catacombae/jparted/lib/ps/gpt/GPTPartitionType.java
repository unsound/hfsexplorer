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

package org.catacombae.jparted.lib.ps.gpt;

import java.nio.LongBuffer;
import java.util.Hashtable;
import org.catacombae.jparted.lib.ps.PartitionType;

/**
 *
 * @author Erik Larsson
 */
public enum GPTPartitionType {
    /*
     * GUID Partition types
     * --------------------
     *
     * Microsoft info (first three fields byteswapped):
     *   Unused entry                                 {00000000-0000-0000-0000-000000000000}
     *   EFI System partition                         {28732AC1-1FF8-D211-BA4B-00A0C93EC93B}
     *   Microsoft Reserved partition                 {16E3C9E3-5C0B-B84D-817D-F92DF00215AE}
     *   Primary partition on a basic disk            {A2A0D0EB-E5B9-3344-87C0-68B6B72699C7}
     *   LDM Metadata partition on a dynamic disk     {AAC80858-8F7E-E042-85D2-E1E90434CFB3}
     *   LDM Data partition on a dynamic disk         {A0609BAF-3114-624F-BC68-3311714A69AD}
     *
     * Wikipedia info (as always, it can be wrong... especially as people seem to write
     * GUIDS with different endianness):
     *   EFI Specification:
     *     Unused entry                               {00000000-0000-0000-0000-000000000000}
     *     MBR partition scheme                       {024DEE41-33E7-11D3-9D69-0008C781F39F}
     *     EFI System Partition                       {C12A7328-F81F-11D2-BA4B-00A0C93EC93B}
     *   Windows:
     *     Microsoft Reserved Partition               {E3C9E316-0B5C-4DB8-817D-F92DF00215AE}
     *     Basic Data Partition                       {EBD0A0A2-B9E5-4433-87C0-68B6B72699C7}
     *     Logical Disk Manager metadata partition    {5808C8AA-7E8F-42E0-85D2-E1E90434CFB3}
     *     Logical Disk Manager data partition        {AF9B60A0-1431-4F62-BC68-3311714A69AD}
     *   HP-UX:
     *     Data partition                             {75894C1E-3AEB-11D3-B7C1-7B03A0000000}
     *     Service Partition                          {E2A1E728-32E3-11D6-A682-7B03A0000000}
     *   Linux:
     *     Data partition                             {EBD0A0A2-B9E5-4433-87C0-68B6B72699C7}
     *     RAID partition                             {A19D880F-05FC-4D3B-A006-743F0F84911E}
     *     Swap partition                             {0657FD6D-A4AB-43C4-84E5-0933C84B4F4F}
     *     Logical Volume Manager (LVM) partition     {E6D6D379-F507-44C2-A23C-238F2A3DF928}
     *     Reserved                                   {8DA63339-0007-60C0-C436-083AC8230908}
     *   FreeBSD:
     *     Data partition                             {516E7CB4-6ECF-11D6-8FF8-00022D09712B}
     *     Swap partition                             {516E7CB5-6ECF-11D6-8FF8-00022D09712B}
     *     Unix File System (UFS) partition           {516E7CB6-6ECF-11D6-8FF8-00022D09712B}
     *     Vinum volume manager partition             {516E7CB8-6ECF-11D6-8FF8-00022D09712B}
     *   Mac OS X:
     *     Hierarchical File System (HFS+) partition  {48465300-0000-11AA-AA11-00306543ECAC}
     *     Apple UFS                                  {55465300-0000-11AA-AA11-00306543ECAC}
     *     Apple RAID partition                       {52414944-0000-11AA-AA11-00306543ECAC}
     *     Apple RAID partition, offline              {52414944-5F4F-11AA-AA11-00306543ECAC}
     *     Apple Boot partition                       {426F6F74-0000-11AA-AA11-00306543ECAC}
     *     Apple Label                                {4C616265-6C00-11AA-AA11-00306543ECAC}
     *     Apple TV Recovery partition                {5265636F-7665-11AA-AA11-00306543ECAC}
     *   Solaris:
     *     Boot partition                             {6A82CB45-1DD2-11B2-99A6-080020736631}
     *     Root partition                             {6A85CF4D-1DD2-11B2-99A6-080020736631}
     *     Swap partition                             {6A87C46F-1DD2-11B2-99A6-080020736631}
     *     Backup partition                           {6A8B642B-1DD2-11B2-99A6-080020736631}
     *     /usr partition                             {6A898CC3-1DD2-11B2-99A6-080020736631}
     *     /var partition                             {6A8EF2E9-1DD2-11B2-99A6-080020736631}
     *     /home partition                            {6A90BA39-1DD2-11B2-99A6-080020736631}
     *     EFI_ALTSCTR 	                              {6A9283A5-1DD2-11B2-99A6-080020736631}
     *     Reserved partition types                   {6A945A3B-1DD2-11B2-99A6-080020736631}
     *                                                {6A9630D1-1DD2-11B2-99A6-080020736631}
     *                                                {6A980767-1DD2-11B2-99A6-080020736631}
     *                                                {6A96237F-1DD2-11B2-99A6-080020736631}
     *                                                {6A8D2AC7-1DD2-11B2-99A6-080020736631}
     *
     * More info on GUID textual representations:
     *   GUIDs written on the form xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx can be a little
     *   confusing for the average big endian-user. The first field (4 bytes) is supposed
     *   to be interpreted as the little endian-form of a 32-bit integer, and the next two
     *   bytes are interpreted as little endian 16-bit integers. Then comes 2 individual
     *   bytes (like a big endian 16-bit integer), followed by 6 individual bytes (like a
     *   big endian 48-bit integer). All in hexadecimal form of course, uppercase, with
     *   hyphens to separate the different parts.
     *
     *   NOTE: This means that the information supplied by microsoft (see "Microsoft info"
     *   above) in:
     *     http://technet2.microsoft.com/WindowsServer/en/library/bdeda920-1f08-4683-9ffb-
     *     7b4b50df0b5a1033.mspx#w2k3tr_basic_how_fgkm
     *   is inaccurate, given the little endian representation. The first, second and third
     *   fields should be swapped in order for the representation to be correct.
     *
     *   The enum fields in this implementation should be accurate.
     */

    PARTITION_TYPE_UNUSED_ENTRY((long) 0x0000000000000000L, (long) 0x0000000000000000L, PartitionType.EMPTY),
    PARTITION_TYPE_EFI_SYSTEM((long) 0x28732AC11FF8D211L, (long) 0xBA4B00A0C93EC93BL, PartitionType.EFI_SYSTEM),
    PARTITION_TYPE_MICROSOFT_RESERVED((long) 0x16E3C9E35C0BB84DL, (long) 0x817DF92DF00215AEL, PartitionType.SPECIAL),
    PARTITION_TYPE_PRIMARY_PARTITION((long) 0xA2A0D0EBE5B93344L, (long) 0x87C068B6B72699C7L, PartitionType.NT_OS2_IFS),
    PARTITION_TYPE_LDM_METADATA((long) 0xAAC808588F7EE042L, (long) 0x85D2E1E90434CFB3L, PartitionType.SPECIAL),
    PARTITION_TYPE_LDM_DATA((long) 0xA0609BAF3114624FL, (long) 0xBC683311714A69ADL, PartitionType.SPECIAL),
    PARTITION_TYPE_APPLE_HFS((long) 0x005346480000AA11L, (long) 0xAA1100306543ECACL, PartitionType.APPLE_HFS_CONTAINER),
    UNKNOWN_PARTITION_TYPE; // Returned when no known type can be matched

    private static Hashtable<LongBuffer, GPTPartitionType> reverseLookupTable;

    private final Long typeGUIDMsb;
    private final Long typeGUIDLsb;
    private final PartitionType enumType;

    private GPTPartitionType(long typeGUIDMsb, long typeGUIDLsb, PartitionType enumType) {
        this.typeGUIDMsb = typeGUIDMsb;
        this.typeGUIDLsb = typeGUIDLsb;
        this.enumType = enumType;
        addReverseLookupReference(LongBuffer.wrap(new long[] { typeGUIDMsb, typeGUIDLsb }), this);
    }

    private GPTPartitionType() {
        this.typeGUIDMsb = null;
        this.typeGUIDLsb = null;
        this.enumType = null;
    }

    private static void addReverseLookupReference(LongBuffer lb, GPTPartitionType t) {
        if(reverseLookupTable == null)
            reverseLookupTable = new Hashtable<LongBuffer, GPTPartitionType>();
        reverseLookupTable.put(lb, t);
    }
    
    public static GPTPartitionType getType(long typeGUIDMsb, long typeGUIDLsb) {
        GPTPartitionType type = reverseLookupTable.get(LongBuffer.wrap(new long[] { typeGUIDMsb, typeGUIDLsb }));
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
