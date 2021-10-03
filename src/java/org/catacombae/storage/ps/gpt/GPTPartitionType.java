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
import org.catacombae.util.Util;

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
    PARTITION_TYPE_UNUSED_ENTRY("00000000-0000-0000-0000-000000000000",
        0x0000000000000000L, 0x0000000000000000L,
        PartitionType.EMPTY),

    /**
     * MBR partition scheme partition type:
     *     <code>024DEE41-33E7-11D3-9D69-0008C781F39F</code>
     */
    PARTITION_TYPE_MBR("024DEE41-33E7-11D3-9D69-0008C781F39F",
        PartitionType.EFI_SYSTEM),

    /**
     * EFI system partition type:
     *     <code>C12A7328-F81F-11D2-BA4B-00A0C93EC93B</code>
     */
    PARTITION_TYPE_EFI_SYSTEM("C12A7328-F81F-11D2-BA4B-00A0C93EC93B",
        0x28732AC11FF8D211L, 0xBA4B00A0C93EC93BL,
        PartitionType.EFI_SYSTEM),

    /**
     * BIOS boot partition type:
     *     <code>21686148-6449-6E6F-744E-656564454649</code>
     */
    PARTITION_TYPE_BIOS_BOOT("21686148-6449-6E6F-744E-656564454649",
        PartitionType.EFI_SYSTEM),

    /**
     * Intel Fast Flash (iFFS) partition type:
     *     <code>D3BFE2DE-3DAF-11DF-BA40-E3A556D89593</code>
     */
    PARTITION_TYPE_INTEL_FAST_FLASH("D3BFE2DE-3DAF-11DF-BA40-E3A556D89593",
        PartitionType.EFI_SYSTEM),

    /**
     * Sony boot partition type:
     *     <code>F4019732-066E-4E12-8273-346C5641494F</code>
     */
    PARTITION_TYPE_SONY_BOOT("F4019732-066E-4E12-8273-346C5641494F",
        PartitionType.EFI_SYSTEM),

    /**
     * Lenovo boot partition type:
     *     <code>BFBFAFE7-A34F-448A-9A5B-6213EB736C22</code>
     */
    PARTITION_TYPE_LENOVO_BOOT("BFBFAFE7-A34F-448A-9A5B-6213EB736C22",
        PartitionType.EFI_SYSTEM),

    /**
     * Microsoft reserved partition:
     *     <code>E3C9E316-0B5C-4DB8-817D-F92DF00215AE</code>
     */
    PARTITION_TYPE_MICROSOFT_RESERVED("E3C9E316-0B5C-4DB8-817D-F92DF00215AE",
        0x16E3C9E35C0BB84DL, 0x817DF92DF00215AEL,
        PartitionType.SPECIAL),

    /**
     * Microsoft basic data partition (also used for Linux native filesystems):
     *     <code>EBD0A0A2-B9E5-4433-87C0-68B6B72699C7</code>
     */
    PARTITION_TYPE_PRIMARY_PARTITION("EBD0A0A2-B9E5-4433-87C0-68B6B72699C7",
        0xA2A0D0EBE5B93344L, 0x87C068B6B72699C7L,
        PartitionType.NT_OS2_IFS),

    /**
     * Microsoft Logical Disk Manager metadata partition type:
     *     <code>5808C8AA-7E8F-42E0-85D2-E1E90434CFB3</code>
     */
    PARTITION_TYPE_LDM_METADATA("5808C8AA-7E8F-42E0-85D2-E1E90434CFB3",
        0xAAC808588F7EE042L, 0x85D2E1E90434CFB3L,
        PartitionType.SPECIAL),

    /**
     * Microsoft Logical Disk Manager data partition type:
     *     <code>AF9B60A0-1431-4F62-BC68-3311714A69AD</code>
     */
    PARTITION_TYPE_LDM_DATA("AF9B60A0-1431-4F62-BC68-3311714A69AD",
        0xA0609BAF3114624FL, 0xBC683311714A69ADL,
        PartitionType.SPECIAL),

    /**
     * Microsoft Windows Recovery Environment partition type:
     *     <code>DE94BBA4-06D1-4D40-A16A-BFD50179D6AC</code>
     */
    PARTITION_TYPE_MICROSOFT_WINDOWS_RECOVERY_ENVIRONMENT(
        "DE94BBA4-06D1-4D40-A16A-BFD50179D6AC",
        PartitionType.SPECIAL),

    /**
     * IBM General Parallel File System (GPFS) partition type:
     *     <code>37AFFC90-EF7D-4E96-91C3-2D7AE055B174</code>
     */
    PARTITION_TYPE_IBM_GPFS("37AFFC90-EF7D-4E96-91C3-2D7AE055B174",
        PartitionType.SPECIAL),

    /**
     * Microsoft Windows Storage Spaces partition type:
     *     <code>E75CAF8F-F680-4CEE-AFA3-B001E56EFC2D</code>
     */
    PARTITION_TYPE_MICROSOFT_WINDOWS_STORAGE_SPACES(
        "E75CAF8F-F680-4CEE-AFA3-B001E56EFC2D",
        PartitionType.SPECIAL),

    /**
     * Microsoft Windows Storage Replica partition type:
     *     <code>558D43C5-A1AC-43C0-AAC8-D1472B2923D1</code>
     */
    PARTITION_TYPE_MICROSOFT_WINDOWS_STORAGE_REPLICA(
        "558D43C5-A1AC-43C0-AAC8-D1472B2923D1",
        PartitionType.SPECIAL),

    /**
     * Apple HFS(+/X) partition type:
     *     <code>48465300-0000-11AA-AA11-00306543ECAC</code>
     */
    PARTITION_TYPE_APPLE_HFS("48465300-0000-11AA-AA11-00306543ECAC",
        0x005346480000AA11L, 0xAA1100306543ECACL,
        PartitionType.APPLE_HFS_CONTAINER),

    /**
     * AppleRAID partition type:
     *     <code>52414944-0000-11AA-AA11-00306543ECAC</code>
     */
    PARTITION_TYPE_APPLERAID("52414944-0000-11AA-AA11-00306543ECAC",
        PartitionType.SPECIAL),

    /**
     * AppleRAID (offline) partition type:
     *     <code>52414944-0000-11AA-AA11-00306543ECAC</code>
     */
    PARTITION_TYPE_APPLERAID_OFFLINE("52414944-5F4F-11AA-AA11-00306543ECAC",
        PartitionType.SPECIAL),

    /**
     * Apple boot/recovery partition type:
     *     <code>426F6F74-0000-11AA-AA11-00306543ECAC</code>
     */
    PARTITION_TYPE_APPLE_BOOT_RECOVERY("426F6F74-0000-11AA-AA11-00306543ECAC",
        PartitionType.SPECIAL),

    /**
     * Apple label partition type:
     *     <code>4C616265-6C00-11AA-AA11-00306543ECAC</code>
     */
    PARTITION_TYPE_APPLE_LABEL("4C616265-6C00-11AA-AA11-00306543ECAC",
        PartitionType.SPECIAL),

    /**
     * AppleTV recovery partition type:
     *     <code>5265636F-7665-11AA-AA11-00306543ECAC</code>
     */
    PARTITION_TYPE_APPLETV_RECOVERY("5265636F-7665-11AA-AA11-00306543ECAC",
        PartitionType.SPECIAL),

    /**
     * Apple CoreStorage partition type:
     *     <code>53746F72-6167-11AA-AA11-00306543ECAC</code>
     */
    PARTITION_TYPE_APPLE_CORESTORAGE("53746F72-6167-11AA-AA11-00306543ECAC",
        PartitionType.SPECIAL),

    /**
     * Apple APFS partition type:
     *     <code>7C3457EF-0000-11AA-AA11-00306543ECAC</code>
     */
    PARTITION_TYPE_APPLE_APFS("7C3457EF-0000-11AA-AA11-00306543ECAC",
        0xEF57347C0000AA11L, 0xAA1100306543ECACL,
        PartitionType.SPECIAL),

    /**
     * Apple APFS preboot partition type:
     *     <code>69646961-6700-11AA-AA11-00306543ECAC</code>
     */
    PARTITION_TYPE_APPLE_APFS_PREBOOT("69646961-6700-11AA-AA11-00306543ECAC",
        PartitionType.SPECIAL),

    /**
     * Apple APFS recovery partition type:
     *     <code>52637672-7900-11AA-AA11-00306543ECAC</code>
     */
    PARTITION_TYPE_APPLE_APFS_RECOVERY("52637672-7900-11AA-AA11-00306543ECAC",
        PartitionType.SPECIAL),

    /**
     * HP-UX data partition type:
     *     <code>75894C1E-3AEB-11D3-B7C1-7B03A0000000</code>
     */
    PARTITION_TYPE_HPUX_DATA("75894C1E-3AEB-11D3-B7C1-7B03A0000000",
        PartitionType.SPECIAL),

    /**
     * HP-UX service partition type:
     *     <code>E2A1E728-32E3-11D6-A682-7B03A0000000</code>
     */
    PARTITION_TYPE_HPUX_SERVICE("E2A1E728-32E3-11D6-A682-7B03A0000000",
        PartitionType.SPECIAL),

    /**
     * Linux data partition type:
     *     <code>0FC63DAF-8483-4772-8E79-3D69D8477DE4</code>
     */
    PARTITION_TYPE_LINUX_DATA("0FC63DAF-8483-4772-8E79-3D69D8477DE4",
        PartitionType.LINUX_NATIVE),

    /**
     * Linux RAID partition type:
     *     <code>A19D880F-05FC-4D3B-A006-743F0F84911E</code>
     */
    PARTITION_TYPE_LINUX_RAID("A19D880F-05FC-4D3B-A006-743F0F84911E",
        PartitionType.SPECIAL),

    /**
     * Linux root (x86) partition type:
     *     <code>44479540-F297-41B2-9AF7-D131D5F0458A</code>
     */
    PARTITION_TYPE_LINUX_ROOT_X86("44479540-F297-41B2-9AF7-D131D5F0458A",
        PartitionType.SPECIAL),

    /**
     * Linux root (x86_64) partition type:
     *     <code>4F68BCE3-E8CD-4DB1-96E7-FBCAF984B709</code>
     */
    PARTITION_TYPE_LINUX_ROOT_X86_64("4F68BCE3-E8CD-4DB1-96E7-FBCAF984B709",
        PartitionType.SPECIAL),

    /**
     * Linux root (arm) partition type:
     *     <code>69DAD710-2CE4-4E3C-B16C-21A1D49ABED3</code>
     */
    PARTITION_TYPE_LINUX_ROOT_ARM("69DAD710-2CE4-4E3C-B16C-21A1D49ABED3",
        PartitionType.SPECIAL),

    /**
     * Linux root (aarch64) partition type:
     *     <code>B921B045-1DF0-41C3-AF44-4C6F280D3FAE</code>
     */
    PARTITION_TYPE_LINUX_ROOT_AARCH64("B921B045-1DF0-41C3-AF44-4C6F280D3FAE",
        PartitionType.SPECIAL),

    /**
     * Linux /boot partition type:
     *     <code>BC13C2FF-59E6-4262-A352-B275FD6F7172</code>
     */
    PARTITION_TYPE_LINUX_BOOT("BC13C2FF-59E6-4262-A352-B275FD6F7172",
        PartitionType.SPECIAL),

    /**
     * Linux Swap partition type:
     *     <code>0657FD6D-A4AB-43C4-84E5-0933C84B4F4F</code>
     */
    PARTITION_TYPE_LINUX_SWAP("0657FD6D-A4AB-43C4-84E5-0933C84B4F4F",
        0x6DFD5706ABA4C443L, 0x84E50933C84B4F4FL,
        PartitionType.LINUX_SWAP),

    /**
     * Linux LVM partition type:
     *     <code>E6D6D379-F507-44C2-A23C-238F2A3DF928</code>
     */
    PARTITION_TYPE_LINUX_LVM("E6D6D379-F507-44C2-A23C-238F2A3DF928",
        PartitionType.LINUX_LVM),

    /**
     * Linux /home partition type:
     *     <code>933AC7E1-2EB4-4F13-B844-0E14E2AEF915</code>
     */
    PARTITION_TYPE_LINUX_HOME("933AC7E1-2EB4-4F13-B844-0E14E2AEF915",
        PartitionType.SPECIAL),

    /**
     * Linux /srv partition type:
     *     <code>3B8F8425-20E0-4F3B-907F-1A25A76F98E8</code>
     */
    PARTITION_TYPE_LINUX_SRV("3B8F8425-20E0-4F3B-907F-1A25A76F98E8",
        PartitionType.SPECIAL),

    /**
     * Linux plain dm-crypt partition type:
     *     <code>7FFEC5C9-2D00-49B7-8941-3EA10A5586B7</code>
     */
    PARTITION_TYPE_LINUX_DM_CRYPT("7FFEC5C9-2D00-49B7-8941-3EA10A5586B7",
        PartitionType.SPECIAL),

    /**
     * Linux LUKS partition type:
     *     <code>CA7D7CCB-63ED-4C53-861C-1742536059CC</code>
     */
    PARTITION_TYPE_LINUX_LUKS("CA7D7CCB-63ED-4C53-861C-1742536059CC",
        PartitionType.SPECIAL),

    /**
     * Linux reserved partition type:
     *     <code>8DA63339-0007-60C0-C436-083AC8230908</code>
     */
    PARTITION_TYPE_LINUX_RESERVED("8DA63339-0007-60C0-C436-083AC8230908",
        PartitionType.SPECIAL),

    /**
     * Solaris boot partition
     *     <code>6A82CB45-1DD2-11B2-99A6-080020736631</code>
     */
    PARTITION_TYPE_SOLARIS_BOOT("6A82CB45-1DD2-11B2-99A6-080020736631",
        PartitionType.SPECIAL),

    /**
     * Solaris root partition
     *     <code>6A85CF4D-1DD2-11B2-99A6-080020736631</code>
     */
    PARTITION_TYPE_SOLARIS_ROOT("6A85CF4D-1DD2-11B2-99A6-080020736631",
        PartitionType.SPECIAL),

    /**
     * Solaris swap partition
     *     <code>6A87C46F-1DD2-11B2-99A6-080020736631</code>
     */
    PARTITION_TYPE_SOLARIS_SWAP("6A87C46F-1DD2-11B2-99A6-080020736631",
        PartitionType.SPECIAL),

    /**
     * Solaris Backup partition
     *     <code>6A8B642B-1DD2-11B2-99A6-080020736631</code>
     */
    PARTITION_TYPE_SOLARIS_BACKUP("6A8B642B-1DD2-11B2-99A6-080020736631",
        PartitionType.SPECIAL),

    /**
     * Solaris /usr partition[h]
     *     <code>6A898CC3-1DD2-11B2-99A6-080020736631</code>
     */
    PARTITION_TYPE_SOLARIS_USR("6A898CC3-1DD2-11B2-99A6-080020736631",
        PartitionType.SPECIAL),

    /**
     * Solaris /var partition
     *     <code>6A8EF2E9-1DD2-11B2-99A6-080020736631</code>
     */
    PARTITION_TYPE_SOLARIS_VAR("6A8EF2E9-1DD2-11B2-99A6-080020736631",
        PartitionType.SPECIAL),

    /**
     * Solaris /home partition
     *     <code>6A90BA39-1DD2-11B2-99A6-080020736631</code>
     */
    PARTITION_TYPE_SOLARIS_HOME("6A90BA39-1DD2-11B2-99A6-080020736631",
        PartitionType.SPECIAL),

    /**
     * Solaris alternate sector
     *     <code>6A9283A5-1DD2-11B2-99A6-080020736631</code>
     */
    PARTITION_TYPE_SOLARIS_ALTERNATE_SECTOR(
        "6A9283A5-1DD2-11B2-99A6-080020736631",
        PartitionType.SPECIAL),

    /**
     * Solaris reserved partition
     *     <code>6A945A3B-1DD2-11B2-99A6-080020736631</code>
     */
    PARTITION_TYPE_SOLARIS_RESERVED1("6A945A3B-1DD2-11B2-99A6-080020736631",
        PartitionType.SPECIAL),

    /**
     * Solaris reserved partition
     *     <code>6A9630D1-1DD2-11B2-99A6-080020736631</code>
     */
    PARTITION_TYPE_SOLARIS_RESERVED2("6A9630D1-1DD2-11B2-99A6-080020736631",
        PartitionType.SPECIAL),

    /**
     * Solaris reserved partition
     *     <code>6A980767-1DD2-11B2-99A6-080020736631</code>
     */
    PARTITION_TYPE_SOLARIS_RESERVED3("6A980767-1DD2-11B2-99A6-080020736631",
        PartitionType.SPECIAL),

    /**
     * Solaris reserved partition
     *     <code>6A96237F-1DD2-11B2-99A6-080020736631</code>
     */
    PARTITION_TYPE_SOLARIS_RESERVED4("6A96237F-1DD2-11B2-99A6-080020736631",
        PartitionType.SPECIAL),

    /**
     * Solaris reserved partition
     *     <code>6A8D2AC7-1DD2-11B2-99A6-080020736631</code>
     */
    PARTITION_TYPE_SOLARIS_RESERVED5("6A8D2AC7-1DD2-11B2-99A6-080020736631",
        PartitionType.SPECIAL),

    /**
     * FreeBSD boot partition type:
     *     <code>83BD6B9D-7F41-11DC-BE0B-001560B84F0F</code>
     */
    PARTITION_TYPE_FREEBSD_BOOT("83BD6B9D-7F41-11DC-BE0B-001560B84F0F",
        PartitionType.SPECIAL),

    /**
     * FreeBSD disklabel partition type:
     *     <code>516E7CB4-6ECF-11D6-8FF8-00022D09712B</code>
     */
    PARTITION_TYPE_FREEBSD_DISKLABEL("516E7CB4-6ECF-11D6-8FF8-00022D09712B",
        PartitionType.SPECIAL),

    /**
     * FreeBSD swap partition type:
     *     <code>516E7CB5-6ECF-11D6-8FF8-00022D09712B</code>
     */
    PARTITION_TYPE_FREEBSD_SWAP("516E7CB5-6ECF-11D6-8FF8-00022D09712B",
        PartitionType.SPECIAL),

    /**
     * FreeBSD UFS partition type:
     *     <code>516E7CB6-6ECF-11D6-8FF8-00022D09712B</code>
     */
    PARTITION_TYPE_FREEBSD_UFS("516E7CB6-6ECF-11D6-8FF8-00022D09712B",
        PartitionType.SPECIAL),

    /**
     * FreeBSD Vinum partition type:
     *     <code>516E7CB8-6ECF-11D6-8FF8-00022D09712B</code>
     */
    PARTITION_TYPE_FREEBSD_VINUM("516E7CB8-6ECF-11D6-8FF8-00022D09712B",
        PartitionType.SPECIAL),

    /**
     * FreeBSD ZFS partition type:
     *     <code>516E7CBA-6ECF-11D6-8FF8-00022D09712B</code>
     */
    PARTITION_TYPE_FREEBSD_ZFS("516E7CBA-6ECF-11D6-8FF8-00022D09712B",
        PartitionType.SPECIAL),

    /**
     * FreeBSD nandfs partition type:
     *     <code>74BA7DD9-A689-11E1-BD04-00E081286ACF</code>
     */
    PARTITION_TYPE_FREEBSD_NANDFS("74BA7DD9-A689-11E1-BD04-00E081286ACF",
        PartitionType.SPECIAL),

    /**
     * NetBSD Swap partition:
     *     <code>49F48D32-B10E-11DC-B99B-0019D1879648</code>
     */
    PARTITION_TYPE_NETBSD_SWAP("49F48D32-B10E-11DC-B99B-0019D1879648",
        PartitionType.SPECIAL),

    /**
     * NetBSD FFS partition:
     *     <code>49F48D5A-B10E-11DC-B99B-0019D1879648</code>
     */
    PARTITION_TYPE_NETBSD_FFS("49F48D5A-B10E-11DC-B99B-0019D1879648",
        PartitionType.SPECIAL),

    /**
     * NetBSD LFS partition:
     *     <code>49F48D82-B10E-11DC-B99B-0019D1879648</code>
     */
    PARTITION_TYPE_NETBSD_LFS("49F48D82-B10E-11DC-B99B-0019D1879648",
        PartitionType.SPECIAL),

    /**
     * NetBSD RAID partition:
     *     <code>49F48DAA-B10E-11DC-B99B-0019D1879648</code>
     */
    PARTITION_TYPE_NETBSD_RAID("49F48DAA-B10E-11DC-B99B-0019D1879648",
        PartitionType.SPECIAL),

    /**
     * NetBSD Concatenated partition:
     *     <code>2DB519C4-B10F-11DC-B99B-0019D1879648</code>
     */
    PARTITION_TYPE_NETBSD_CONCATENATED("2DB519C4-B10F-11DC-B99B-0019D1879648",
        PartitionType.SPECIAL),

    /**
     * NetBSD Encrypted partition:
     *     <code>2DB519EC-B10F-11DC-B99B-0019D1879648</code>
     */
    PARTITION_TYPE_NETBSD_ENCRYPTED("2DB519EC-B10F-11DC-B99B-0019D1879648",
        PartitionType.SPECIAL),

    /**
     * Chrome OS kernel:
     *     <code>FE3A2A5D-4F32-41A7-B725-ACCC3285A309</code>
     */
    PARTITION_TYPE_CHROMEOS_KERNEL("FE3A2A5D-4F32-41A7-B725-ACCC3285A309",
        PartitionType.SPECIAL),

    /**
     * Chrome OS rootfs:
     *     <code>3CB8E202-3B7E-47DD-8A3C-7FF2A13CFCEC</code>
     */
    PARTITION_TYPE_CHROMEOS_ROOTFS("3CB8E202-3B7E-47DD-8A3C-7FF2A13CFCEC",
        PartitionType.SPECIAL),

    /**
     * Chrome OS future use:
     *     <code>2E0A753D-9E48-43B0-8337-B15192CB1B5E</code>
     */
    PARTITION_TYPE_CHROMEOS_FUTURE_USE("2E0A753D-9E48-43B0-8337-B15192CB1B5E",
        PartitionType.SPECIAL),

    /**
     * Container Linux by CoreOS /usr partition (coreos-usr):
     *     <code>5DFBF5F4-2848-4BAC-AA5E-0D9A20B745A6</code>
     */
    PARTITION_TYPE_COREOS_USR("5DFBF5F4-2848-4BAC-AA5E-0D9A20B745A6",
        PartitionType.SPECIAL),

    /**
     * Container Linux by CoreOS Resizable rootfs (coreos-resize):
     *     <code>3884DD41-8582-4404-B9A8-E9B84F2DF50E</code>
     */
    PARTITION_TYPE_COREOS_RESIZABLE_ROOTFS(
        "3884DD41-8582-4404-B9A8-E9B84F2DF50E",
        PartitionType.SPECIAL),

    /**
     * Container Linux by CoreOS OEM customizations (coreos-reserved):
     *     <code>C95DC21A-DF0E-4340-8D7B-26CBFA9A03E0</code>
     */
    PARTITION_TYPE_COREOS_OEM("C95DC21A-DF0E-4340-8D7B-26CBFA9A03E0",
        PartitionType.SPECIAL),

    /**
     * Container Linux by CoreOS Root filesystem on RAID (coreos-root-raid):
     *     <code>BE9067B9-EA49-4F15-B4F6-F36F8C9E1818</code>
     */
    PARTITION_TYPE_COREOS_ROOT_ON_RAID("BE9067B9-EA49-4F15-B4F6-F36F8C9E1818",
        PartitionType.SPECIAL),

    /**
     * Haiku BFS:
     *     <code>42465331-3BA3-10F1-802A-4861696B7521</code>
     */
    PARTITION_TYPE_HAIKU_BFS("42465331-3BA3-10F1-802A-4861696B7521",
        PartitionType.SPECIAL),

    /**
     * MidnightBSD boot partition:
     *     <code>85D5E45E-237C-11E1-B4B3-E89A8F7FC3A7</code>
     */
    PARTITION_TYPE_MIDNIGHTBSD_BOOT("85D5E45E-237C-11E1-B4B3-E89A8F7FC3A7",
        PartitionType.SPECIAL),

    /**
     * MidnightBSD data partition:
     *     <code>85D5E45A-237C-11E1-B4B3-E89A8F7FC3A7</code>
     */
    PARTITION_TYPE_MIDNIGHTBSD_DATA("85D5E45A-237C-11E1-B4B3-E89A8F7FC3A7",
        PartitionType.SPECIAL),

    /**
     * MidnightBSD swap partition:
     *     <code>85D5E45B-237C-11E1-B4B3-E89A8F7FC3A7</code>
     */
    PARTITION_TYPE_MIDNIGHTBSD_SWAP("85D5E45B-237C-11E1-B4B3-E89A8F7FC3A7",
        PartitionType.SPECIAL),

    /**
     * MidnightBSD Unix File System (UFS) partition:
     *     <code>0394EF8B-237E-11E1-B4B3-E89A8F7FC3A7</code>
     */
    PARTITION_TYPE_MIDNIGHTBSD_UFS("0394EF8B-237E-11E1-B4B3-E89A8F7FC3A7",
        PartitionType.SPECIAL),

    /**
     * MidnightBSD Vinum volume manager partition:
     *     <code>85D5E45C-237C-11E1-B4B3-E89A8F7FC3A7</code>
     */
    PARTITION_TYPE_MIDNIGHTBSD_VINUM("85D5E45C-237C-11E1-B4B3-E89A8F7FC3A7",
        PartitionType.SPECIAL),

    /**
     * MidnightBSD ZFS partition:
     *     <code>85D5E45D-237C-11E1-B4B3-E89A8F7FC3A7</code>
     */
    PARTITION_TYPE_MIDNIGHTBSD_ZFS("85D5E45D-237C-11E1-B4B3-E89A8F7FC3A7",
        PartitionType.SPECIAL),

    /**
     * Ceph journal:
     *     <code>45B0969E-9B03-4F30-B4C6-B4B80CEFF106</code>
     */
    PARTITION_TYPE_CEPH_JOURNAL("45B0969E-9B03-4F30-B4C6-B4B80CEFF106",
        PartitionType.SPECIAL),

    /**
     * Ceph dm-crypt journal:
     *     <code>45B0969E-9B03-4F30-B4C6-5EC00CEFF106</code>
     */
    PARTITION_TYPE_CEPH_DM_CRYPT_JOURNAL("45B0969E-9B03-4F30-B4C6-5EC00CEFF106",
        PartitionType.SPECIAL),

    /**
     * Ceph OSD:
     *     <code>4FBD7E29-9D25-41B8-AFD0-062C0CEFF05D</code>
     */
    PARTITION_TYPE_CEPH_OSD("4FBD7E29-9D25-41B8-AFD0-062C0CEFF05D",
        PartitionType.SPECIAL),

    /**
     * Ceph dm-crypt OSD:
     *     <code>4FBD7E29-9D25-41B8-AFD0-5EC00CEFF05D</code>
     */
    PARTITION_TYPE_CEPH_DM_CRYPT_OSD("4FBD7E29-9D25-41B8-AFD0-5EC00CEFF05D",
        PartitionType.SPECIAL),

    /**
     * Ceph Disk in creation:
     *     <code>89C57F98-2FE5-4DC0-89C1-F3AD0CEFF2BE</code>
     */
    PARTITION_TYPE_CEPH_DISK_IN_CREATION("89C57F98-2FE5-4DC0-89C1-F3AD0CEFF2BE",
        PartitionType.SPECIAL),

    /**
     * Ceph dm-crypt disk in creation:
     *     <code>89C57F98-2FE5-4DC0-89C1-5EC00CEFF2BE</code>
     */
    PARTITION_TYPE_CEPH_DM_CRYPT_DISK_IN_CREATION(
        "89C57F98-2FE5-4DC0-89C1-5EC00CEFF2BE",
        PartitionType.SPECIAL),

    /**
     * Ceph Block:
     *     <code>CAFECAFE-9B03-4F30-B4C6-B4B80CEFF106</code>
     */
    PARTITION_TYPE_CEPH_BLOCK("CAFECAFE-9B03-4F30-B4C6-B4B80CEFF106",
        PartitionType.SPECIAL),

    /**
     * Ceph Block DB:
     *     <code>30CD0809-C2B2-499C-8879-2D6B78529876</code>
     */
    PARTITION_TYPE_CEPH_BLOCK_DB(
        "30CD0809-C2B2-499C-8879-2D6B78529876",
        PartitionType.SPECIAL),

    /**
     * Ceph Block write-ahead log:
     *     <code>5CE17FCE-4087-4169-B7FF-056CC58473F9</code>
     */
    PARTITION_TYPE_CEPH_WRITE_AHEAD_LOG(
        "5CE17FCE-4087-4169-B7FF-056CC58473F9",
        PartitionType.SPECIAL),

    /**
     * Ceph Lockbox for dm-crypt keys:
     *     <code>FB3AABF9-D25F-47CC-BF5E-721D1816496B</code>
     */
    PARTITION_TYPE_CEPH_LOCKBOX_FOR_DM_CRYPT_KEYS(
        "FB3AABF9-D25F-47CC-BF5E-721D1816496B",
        PartitionType.SPECIAL),

    /**
     * Ceph Multipath OSD:
     *     <code>4FBD7E29-8AE0-4982-BF9D-5A8D867AF560</code>
     */
    PARTITION_TYPE_CEPH_MULTIPATH_OSD("4FBD7E29-8AE0-4982-BF9D-5A8D867AF560",
        PartitionType.SPECIAL),

    /**
     * Ceph Multipath journal:
     *     <code>45B0969E-8AE0-4982-BF9D-5A8D867AF560</code>
     */
    PARTITION_TYPE_CEPH_MULTIPATH_JOURNAL(
        "45B0969E-8AE0-4982-BF9D-5A8D867AF560",
        PartitionType.SPECIAL),

    /**
     * Ceph Multipath block (1):
     *     <code>CAFECAFE-8AE0-4982-BF9D-5A8D867AF560</code>
     */
    PARTITION_TYPE_CEPH_MULTIPATH_BLOCK1("CAFECAFE-8AE0-4982-BF9D-5A8D867AF560",
        PartitionType.SPECIAL),

    /**
     * Ceph Multipath block (2):
     *     <code>7F4A666A-16F3-47A2-8445-152EF4D03F6C</code>
     */
    PARTITION_TYPE_CEPH_MULTIPATH_BLOCK2("7F4A666A-16F3-47A2-8445-152EF4D03F6C",
        PartitionType.SPECIAL),

    /**
     * Ceph Multipath block DB:
     *     <code>EC6D6385-E346-45DC-BE91-DA2A7C8B3261</code>
     */
    PARTITION_TYPE_CEPH_MULTIPATH_BLOCK_DB(
        "EC6D6385-E346-45DC-BE91-DA2A7C8B3261",
        PartitionType.SPECIAL),

    /**
     * Ceph Multipath block write-ahead log:
     *     <code>01B41E1B-002A-453C-9F17-88793989FF8F</code>
     */
    PARTITION_TYPE_CEPH_BLOCK_WRITE_AHEAD_LOG(
        "01B41E1B-002A-453C-9F17-88793989FF8F",
        PartitionType.SPECIAL),

    /**
     * Ceph dm-crypt block:
     *     <code>CAFECAFE-9B03-4F30-B4C6-5EC00CEFF106</code>
     */
    PARTITION_TYPE_CEPH_DM_CRYPT_BLOCK("CAFECAFE-9B03-4F30-B4C6-5EC00CEFF106",
        PartitionType.SPECIAL),

    /**
     * Ceph dm-crypt block DB:
     *     <code>93B0052D-02D9-4D8A-A43B-33A3EE4DFBC3</code>
     */
    PARTITION_TYPE_CEPH_DM_CRYPT_BLOCK_DB(
        "93B0052D-02D9-4D8A-A43B-33A3EE4DFBC3",
        PartitionType.SPECIAL),

    /**
     * Ceph dm-crypt block write-ahead log:
     *     <code>306E8683-4FE2-4330-B7C0-00A917C16966</code>
     */
    PARTITION_TYPE_CEPH_DMG_CRYPT_BLOCK_WRITE_AHEAD_LOG(
        "306E8683-4FE2-4330-B7C0-00A917C16966",
        PartitionType.SPECIAL),

    /**
     * Ceph dm-crypt LUKS journal:
     *     <code>45B0969E-9B03-4F30-B4C6-35865CEFF106</code>
     */
    PARTITION_TYPE_CEPH_DMG_CRYPT_LUKS_JOURNAL(
        "45B0969E-9B03-4F30-B4C6-35865CEFF106",
        PartitionType.SPECIAL),

    /**
     * Ceph dm-crypt LUKS block:
     *     <code>CAFECAFE-9B03-4F30-B4C6-35865CEFF106</code>
     */
    PARTITION_TYPE_CEPH_DM_CRYPT_LUKS_BLOCK(
        "CAFECAFE-9B03-4F30-B4C6-35865CEFF106",
        PartitionType.SPECIAL),

    /**
     * Ceph dm-crypt LUKS block DB:
     *     <code>166418DA-C469-4022-ADF4-B30AFD37F176</code>
     */
    PARTITION_TYPE_CEPH_DMG_CRYPT_LUKS_BLOCK_DB(
        "166418DA-C469-4022-ADF4-B30AFD37F176",
        PartitionType.SPECIAL),

    /**
     * Ceph dm-crypt LUKS block write-ahead log:
     *     <code>86A32090-3647-40B9-BBBD-38D8C573AA86</code>
     */
    PARTITION_TYPE_CEPH_DM_CRYPT_LUKS_BLOCK_WRITE_AHEAD_LOG(
        "86A32090-3647-40B9-BBBD-38D8C573AA86",
        PartitionType.SPECIAL),

    /**
     * Ceph dm-crypt LUKS OSD:
     *     <code>4FBD7E29-9D25-41B8-AFD0-35865CEFF05D</code>
     */
    PARTITION_TYPE_CEPH_DM_CRYPT_LUKS_OSD(
        "4FBD7E29-9D25-41B8-AFD0-35865CEFF05D",
        PartitionType.SPECIAL),

    /**
     * OpenBSD data partition:
     *     <code>824CC7A0-36A8-11E3-890A-952519AD3F61</code>
     */
    PARTITION_TYPE_OPENBSD_DATA("824CC7A0-36A8-11E3-890A-952519AD3F61",
        PartitionType.SPECIAL),

    /**
     * QNX power-safe (QNX6) file system[51]:
     *     <code>CEF5A9AD-73BC-4601-89F3-CDEEEEE321A1</code>
     */
    PARTITION_TYPE_QNX6("CEF5A9AD-73BC-4601-89F3-CDEEEEE321A1",
        PartitionType.SPECIAL),

    /**
     * Plan 9 partition:
     *     <code>C91818F9-8025-47AF-89D2-F030D7000C2C</code>
     */
    PARTITION_TYPE_PLAN9("C91818F9-8025-47AF-89D2-F030D7000C2C",
        PartitionType.SPECIAL),

    /**
     * VMware ESX vmkcore (coredump partition):
     *     <code>9D275380-40AD-11DB-BF97-000C2911D1B8</code>
     */
    PARTITION_TYPE_VMWARE_ESX_VMKCORE("9D275380-40AD-11DB-BF97-000C2911D1B8",
        PartitionType.SPECIAL),

    /**
     * VMware ESX VMFS filesystem partition:
     *     <code>AA31E02A-400F-11DB-9590-000C2911D1B8</code>
     */
    PARTITION_TYPE_VMWARE_ESX_VMFS("AA31E02A-400F-11DB-9590-000C2911D1B8",
        PartitionType.SPECIAL),

    /**
     * VMware ESX Reserved:
     *     <code>9198EFFC-31C0-11DB-8F78-000C2911D1B8</code>
     */
    PARTITION_TYPE_VMWARE_ESX_RESERVED("9198EFFC-31C0-11DB-8F78-000C2911D1B8",
        PartitionType.SPECIAL),

    /**
     * Android-IA Bootloader:
     *     <code>2568845D-2332-4675-BC39-8FA5A4748D15</code>
     */
    PARTITION_TYPE_ANDROID_IA_BOOTLOADER("2568845D-2332-4675-BC39-8FA5A4748D15",
        PartitionType.SPECIAL),

    /**
     * Android-IA Bootloader2:
     *     <code>114EAFFE-1552-4022-B26E-9B053604CF84</code>
     */
    PARTITION_TYPE_ANDROID_IA_BOOTLOADER2(
        "114EAFFE-1552-4022-B26E-9B053604CF84",
        PartitionType.SPECIAL),

    /**
     * Android-IA Boot:
     *     <code>49A4D17F-93A3-45C1-A0DE-F50B2EBE2599</code>
     */
    PARTITION_TYPE_ANDROID_IA_BOOT("49A4D17F-93A3-45C1-A0DE-F50B2EBE2599",
        PartitionType.SPECIAL),

    /**
     * Android-IA Recovery:
     *     <code>4177C722-9E92-4AAB-8644-43502BFD5506</code>
     */
    PARTITION_TYPE_ANDROID_IA_RECOVERY("4177C722-9E92-4AAB-8644-43502BFD5506",
        PartitionType.SPECIAL),

    /**
     * Android-IA Misc:
     *     <code>EF32A33B-A409-486C-9141-9FFB711F6266</code>
     */
    PARTITION_TYPE_ANDROID_IA_MISC("EF32A33B-A409-486C-9141-9FFB711F6266",
        PartitionType.SPECIAL),

    /**
     * Android-IA Metadata:
     *     <code>20AC26BE-20B7-11E3-84C5-6CFDB94711E9</code>
     */
    PARTITION_TYPE_ANDROID_IA_METADATA("20AC26BE-20B7-11E3-84C5-6CFDB94711E9",
        PartitionType.SPECIAL),

    /**
     * Android-IA System:
     *     <code>38F428E6-D326-425D-9140-6E0EA133647C</code>
     */
    PARTITION_TYPE_ANDROID_IA_SYSTEM("38F428E6-D326-425D-9140-6E0EA133647C",
        PartitionType.SPECIAL),

    /**
     * Android-IA Cache:
     *     <code>A893EF21-E428-470A-9E55-0668FD91A2D9</code>
     */
    PARTITION_TYPE_ANDROID_IA_CACHE("A893EF21-E428-470A-9E55-0668FD91A2D9",
        PartitionType.SPECIAL),

    /**
     * Android-IA Data:
     *     <code>DC76DDA9-5AC1-491C-AF42-A82591580C0D</code>
     */
    PARTITION_TYPE_ANDROID_IA_DATA("DC76DDA9-5AC1-491C-AF42-A82591580C0D",
        PartitionType.SPECIAL),

    /**
     * Android-IA Persistent:
     *     <code>EBC597D0-2053-4B15-8B64-E0AAC75F4DB1</code>
     */
    PARTITION_TYPE_ANDROID_IA_PERSISTENT("EBC597D0-2053-4B15-8B64-E0AAC75F4DB1",
        PartitionType.SPECIAL),

    /**
     * Android-IA Vendor:
     *     <code>C5A0AEEC-13EA-11E5-A1B1-001E67CA0C3C</code>
     */
    PARTITION_TYPE_ANDROID_IA_VENDOR("C5A0AEEC-13EA-11E5-A1B1-001E67CA0C3C",
        PartitionType.SPECIAL),

    /**
     * Android-IA Config:
     *     <code>BD59408B-4514-490D-BF12-9878D963F378</code>
     */
    PARTITION_TYPE_ANDROID_IA_CONFIG("BD59408B-4514-490D-BF12-9878D963F378",
        PartitionType.SPECIAL),

    /**
     * Android-IA Factory:
     *     <code>8F68CC74-C5E5-48DA-BE91-A0C8C15E9C80</code>
     */
    PARTITION_TYPE_ANDROID_IA_FACTORY("8F68CC74-C5E5-48DA-BE91-A0C8C15E9C80",
        PartitionType.SPECIAL),

    /**
     * Android-IA Factory (alt):
     *     <code>9FDAA6EF-4B3F-40D2-BA8D-BFF16BFB887B</code>
     */
    PARTITION_TYPE_ANDROID_IA_FACTORY_ALT(
        "9FDAA6EF-4B3F-40D2-BA8D-BFF16BFB887B",
        PartitionType.SPECIAL),

    /**
     * Android-IA Fastboot / Tertiary:
     *     <code>767941D0-2085-11E3-AD3B-6CFDB94711E9</code>
     */
    PARTITION_TYPE_ANDROID_IA_FASTBOOT_TERTIARY(
        "767941D0-2085-11E3-AD3B-6CFDB94711E9",
        PartitionType.SPECIAL),

    /**
     * Android-IA OEM:
     *     <code>AC6D7924-EB71-4DF8-B48D-E267B27148FF</code>
     */
    PARTITION_TYPE_ANDROID_IA_OEM("AC6D7924-EB71-4DF8-B48D-E267B27148FF",
        PartitionType.SPECIAL),

    /**
     * Android 6.0+ ARM Meta:
     *     <code>19A710A2-B3CA-11E4-B026-10604B889DCF</code>
     */
    PARTITION_TYPE_ANDROID_ARM_META("19A710A2-B3CA-11E4-B026-10604B889DCF",
        PartitionType.SPECIAL),

    /**
     * Android 6.0+ ARM EXT:
     *     <code>193D1EA4-B3CA-11E4-B075-10604B889DCF</code>
     */
    PARTITION_TYPE_ANDROID_ARM_EXT("193D1EA4-B3CA-11E4-B075-10604B889DCF",
        PartitionType.SPECIAL),

    /**
     * Open Network Install Environment (ONIE) Boot:
     *     <code>7412F7D5-A156-4B13-81DC-867174929325</code>
     */
    PARTITION_TYPE_ONIE_BOOT("7412F7D5-A156-4B13-81DC-867174929325",
        PartitionType.SPECIAL),

    /**
     * Open Network Install Environment (ONIE) Config:
     *     <code>D4E6E2CD-4469-46F3-B5CB-1BFF57AFC149</code>
     */
    PARTITION_TYPE_ONIE_CONFIG("D4E6E2CD-4469-46F3-B5CB-1BFF57AFC149",
        PartitionType.SPECIAL),

    /**
     * PowerPC PReP boot:
     *     <code>9E1A2D38-C612-4316-AA26-8B49521E5A8B</code>
     */
    PARTITION_TYPE_POWERPC_PREP_BOOT("9E1A2D38-C612-4316-AA26-8B49521E5A8B",
        PartitionType.SPECIAL),

    /**
     * Atari TOS basic data partition (GEM, BGM, F32):
     *     <code>734E5AFE-F61A-11E6-BC64-92361F002671</code>
     */
    PARTITION_TYPE_ATARI_TOS_BASIC_DATA("734E5AFE-F61A-11E6-BC64-92361F002671",
        PartitionType.SPECIAL),

    /**
     * VeraCrypt encrypted data partition:
     *     <code>8C8F8EFF-AC95-4770-814A-21994F2DBC8F</code>
     */
    PARTITION_TYPE_VERACRYPT_DATA("8C8F8EFF-AC95-4770-814A-21994F2DBC8F",
        PartitionType.SPECIAL),

    /**
     * OS/2 ArcaOS Type 1:
     *     <code>90B6FF38-B98F-4358-A21F-48F35B4A8AD3</code>
     */
    PARTITION_TYPE_OS2_ARCAOS_TYPE_1("90B6FF38-B98F-4358-A21F-48F35B4A8AD3",
        PartitionType.SPECIAL),

    /**
     * Storage Performance Development Kit (SPDK) block device:
     *     <code>7C5222BD-8F5D-4087-9C00-BF9843C7B58C</code>
     */
    PARTITION_TYPE_SPDK_BLOCK_DEVICE("7C5222BD-8F5D-4087-9C00-BF9843C7B58C",
        PartitionType.SPECIAL),

    /**
     * barebox bootloader barebox-state:
     *     <code>4778ED65-BF42-45FA-9C5B-287A1DC4AAB1</code>
     */
    PARTITION_TYPE_BAREBOX_STATE("4778ED65-BF42-45FA-9C5B-287A1DC4AAB1",
        PartitionType.SPECIAL),

    /**
     * U-Boot bootloader environment:
     *     <code>3DE21764-95BD-54BD-A5C3-4ABE786F38A8</code>
     */
    PARTITION_TYPE_UBOOT_ENVIRONMENT("3DE21764-95BD-54BD-A5C3-4ABE786F38A8",
        PartitionType.SPECIAL),

    /**
     * SoftRAID Status:
     *     <code>B6FA30DA-92D2-4A9A-96F1-871EC6486200</code>
     */
    PARTITION_TYPE_SOFTRAID_STATUS("B6FA30DA-92D2-4A9A-96F1-871EC6486200",
        PartitionType.SPECIAL),

    /**
     * SoftRAID Scratch:
     *     <code>2E313465-19B9-463F-8126-8A7993773801</code>
     */
    PARTITION_TYPE_SOFTRAID_SCRATCH("2E313465-19B9-463F-8126-8A7993773801",
        PartitionType.SPECIAL),

    /**
     * SoftRAID Volume:
     *     <code>FA709C7E-65B1-4593-BFD5-E71D61DE9B02</code>
     */
    PARTITION_TYPE_SOFTRAID_VOLUME("FA709C7E-65B1-4593-BFD5-E71D61DE9B02",
        PartitionType.SPECIAL),

    /**
     * SoftRAID Cache:
     *     <code>BBBA6DF5-F46F-4A89-8F59-8765B2727503</code>
     */
    PARTITION_TYPE_SOFTRAID_CACHE("BBBA6DF5-F46F-4A89-8F59-8765B2727503",
        PartitionType.SPECIAL),

    /** Returned when no known type can be matched. */
    UNKNOWN_PARTITION_TYPE;

    private static HashMap<LongBuffer, GPTPartitionType> reverseLookupTable;

    private final Long typeGUIDMsb;
    private final Long typeGUIDLsb;
    private final PartitionType enumType;

    private GPTPartitionType(String guidString, PartitionType enumType)
    {
        this(guidString, null, null, enumType);
    }

    private GPTPartitionType(String guidString, Long typeGUIDMsb,
            Long typeGUIDLsb, PartitionType enumType)
    {
        final char[] guidStringChars = guidString.toCharArray();

        if(guidStringChars.length != 36) {
            throw new RuntimeException("Invalid length " +
                    "(" + guidStringChars.length + ") for GUID: " + guidString +
                    " (expected 36 characters)");
        }

        for(int i = 0; i < guidStringChars.length; ++i) {
            boolean invalid = true;

            if(i == 8 || i == 13 || i == 18 || i == 23) {
                if(guidStringChars[i] == '-') {
                    invalid = false;
                }
            }
            else {
                if(guidStringChars[i] >= '0' && guidStringChars[i] <= '9') {
                    invalid = false;
                }
                else if(guidStringChars[i] >= 'A' && guidStringChars[i] <= 'F')
                {
                    invalid = false;
                }
            }

            if(invalid) {
                throw new RuntimeException("Invalid GUID character " +
                        "'" + guidStringChars[i] + "' at index " + i + " in " +
                        "GUID: " + guidString + " (expected hexadecimal " +
                        "nibble: 0-9, A-F)");
            }
        }

        this.typeGUIDMsb =
                (parseHexDataLE(guidStringChars, 0, 8) << 32) |
                (parseHexDataLE(guidStringChars, 9, 4) << 16) |
                parseHexDataLE(guidStringChars, 14, 4);
        this.typeGUIDLsb =
                (parseHexDataBE(guidStringChars, 19, 4) << 48) |
                parseHexDataBE(guidStringChars, 24, 12);
        this.enumType = enumType;

        if((typeGUIDMsb != null &&
                typeGUIDMsb.longValue() != this.typeGUIDMsb) ||
                (typeGUIDLsb != null &&
                typeGUIDLsb.longValue() != this.typeGUIDLsb))
        {
            throw new RuntimeException("GUID parsed value differs from " +
                    "reference value. GUID: " + guidString + " parsed: " +
                    "{ 0x" + Util.toHexStringBE(this.typeGUIDMsb) + ", 0x" +
                    Util.toHexStringBE(this.typeGUIDLsb) + " } reference: " +
                    "{ 0x" + Util.toHexStringBE(typeGUIDMsb) + ", 0x" +
                    Util.toHexStringBE(typeGUIDLsb) + " }");
        }

        addReverseLookupReference(LongBuffer.wrap(
                new long[] { this.typeGUIDMsb, this.typeGUIDLsb }), this);
    }

    private GPTPartitionType() {
        this.typeGUIDMsb = null;
        this.typeGUIDLsb = null;
        this.enumType = null;
    }

    private static long parseHexDataBE(char[] data, int index, int length) {
        long result = 0;

        for(int i = 0; i < length; ++i) {
            final char cur = data[index + i];
            byte value;

            if(cur >= '0' && cur <= '9') {
                value = (byte) (cur - '0');
            }
            else {
                value = (byte) (0xA + (cur - 'A'));
            }

            result <<= 4;
            result |= value;
        }

        return result;
    }

    private static long parseHexDataLE(char[] data, int index, int length) {
        long result = 0;

        for(int i = 0; i < length; ++i) {
            final char cur = data[index + i];
            byte value;

            if(cur >= '0' && cur <= '9') {
                value = (byte) (cur - '0');
            }
            else {
                value = (byte) (0xA + (cur - 'A'));
            }

            result |= value << (i + (((i % 2) == 0) ? 1 : -1)) * 4;
        }

        return result;
    }

    private static void addReverseLookupReference(LongBuffer lb,
            GPTPartitionType t)
    {
        GPTPartitionType conflictingType;
        if(reverseLookupTable == null) {
            reverseLookupTable = new HashMap<LongBuffer, GPTPartitionType>();
        }
        else if((conflictingType = reverseLookupTable.get(lb)) != null) {
            throw new RuntimeException("Invalid duplicate partition type: " +
                    t + " (duplicates " + conflictingType + ")");
        }

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
