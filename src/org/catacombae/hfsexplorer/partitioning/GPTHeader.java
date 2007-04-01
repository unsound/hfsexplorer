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

public class GPTHeader {
    // Header is 92 bytes long. The rest of the 512 bytes (420 bytes) is reserved.
    private static final long GPT_SIGNATURE = 0x4546492050415254L;

    /* Structure of the GPT Header:
     *
     * 0x00  8 bytes
     *   Signature. Used to identify all EFI-compatible GPT headers. The value must always be 45 46 49 20 50 41 52 54.
     *
     * 0x08  4 bytes
     *   Revision. The revision number of the EFI specification to which the GPT header complies. For version 1.0, the value
     *   is 00 00 01 00.
     *
     * 0x0C  4 bytes
     *   Header Size. The size, in bytes, of the GPT header. The size is always 5C 00 00 00 or 92 bytes. The remaining
     *   bytes in LBA 1 are reserved.
     *
     * 0x10  4 bytes
     *   CRC32 Checksum. Used to verify the integrity of the GPT header. The 32-bit cyclic redundancy check (CRC)
     *   algorithm is used to perform this calculation.
     *
     * 0x14  4 bytes
     *   Reserved. Must be 0.
     *
     * 0x18  8 bytes
     *   Primary LBA. The LBA that contains the primary GPT header. The value is always equal to LBA 1.
     *
     * 0x20  8 bytes
     *   Backup LBA. The LBA address of the backup GPT header. This value is always equal to the last LBA on the disk.
     *
     * 0x28  8 bytes
     *   First Usable LBA. The first usable LBA that can be contained in a GUID partition entry. In other words, 
     *   the first partition begins at this LBA. In the 64-bit versions of Windows Server 2003, this number is always LBA 34.
     *
     * 0x30  8 bytes
     *   Last Usable LBA. The last usable LBA that can be contained in a GUID partition entry.
     *
     * 0x38  16 bytes
     *   Disk GUID. A unique number that identifies the partition table header and the disk itself.
     *
     * 0x48  8 bytes
     *   Partition Entry LBA. The starting LBA of the GUID partition entry array. This number is always LBA 2.
     *
     * 0x50  4 bytes
     *   Number of Partition Entries. The maximum number of partition entries that can be contained in the GUID partition
     *   entry array. In the 64-bit versions of Windows Server 2003, this number is equal to 128.
     *
     * 0x54  4 bytes
     *   Size of Partition Entry. The size, in bytes, of each partition entry in the GUID partition entry array. Each
     *   partition entry is 128 bytes.
     *
     * 0x58  4 bytes
     *   Partition Entry Array CRC32. Used to verify the integrity of the GUID partition entry array. The 32-bit CRC
     *   algorithm is used to perform this calculation.
     *
     * 0x5C  420 bytes
     *   Reserved. Must be 0.
     */
    private final byte[] signature = new byte[8];
    private final byte[] revision = new byte[4];
    private final byte[] headerSize = new byte[4];
    private final byte[] crc32Checksum = new byte[4];
    private final byte[] reserved1 = new byte[4];
    private final byte[] primaryLBA = new byte[8];
    private final byte[] backupLBA = new byte[8];
    private final byte[] firstUsableLBA = new byte[8];
    private final byte[] lastUsableLBA = new byte[8];
    private final byte[] diskGUID = new byte[16];
    private final byte[] partitionEntryLBA = new byte[8];
    private final byte[] numberOfPartitionEntries = new byte[4];
    private final byte[] sizeOfPartitionEntry = new byte[4];
    private final byte[] partitionEntryArrayCRC32 = new byte[4];
    private final byte[] reserved2 = new byte[420];
    
    public GPTHeader(byte[] data, int offset) {
	System.arraycopy(data, offset+0, signature, 0, 8);
	System.arraycopy(data, offset+8, revision, 0, 4);
	System.arraycopy(data, offset+12, headerSize, 0, 4);
	System.arraycopy(data, offset+16, crc32Checksum, 0, 4);
	System.arraycopy(data, offset+20, reserved1, 0, 4);
	System.arraycopy(data, offset+24, primaryLBA, 0, 8);
	System.arraycopy(data, offset+32, backupLBA, 0, 8);
	System.arraycopy(data, offset+40, firstUsableLBA, 0, 8);
	System.arraycopy(data, offset+48, lastUsableLBA, 0, 8);
	System.arraycopy(data, offset+56, diskGUID, 0, 16);
	System.arraycopy(data, offset+72, partitionEntryLBA, 0, 8);
	System.arraycopy(data, offset+80, numberOfPartitionEntries, 0, 4);
	System.arraycopy(data, offset+84, sizeOfPartitionEntry, 0, 4);
	System.arraycopy(data, offset+88, partitionEntryArrayCRC32, 0, 4);
	System.arraycopy(data, offset+92, reserved2, 0, 420);
    }
    
    public long getSignature() { return Util.readLongBE(signature); }
    public int getRevision() { return Util.readIntBE(revision); }
    public int getHeaderSize() { return Util.readIntLE(headerSize); }
    public int getCRC32Checksum() { return Util.readIntBE(crc32Checksum); }
    public int getReserved1() { return Util.readIntBE(reserved1); }
    public long getPrimaryLBA() { return Util.readLongLE(primaryLBA); }
    public long getBackupLBA() { return Util.readLongLE(backupLBA); }
    public long getFirstUsableLBA() { return Util.readLongLE(firstUsableLBA); }
    public long getLastUsableLBA() { return Util.readLongLE(lastUsableLBA); }
    public byte[] getDiskGUID() { return Util.createCopy(diskGUID); }
    public long getPartitionEntryLBA() { return Util.readLongLE(partitionEntryLBA); }
    public int getNumberOfPartitionEntries() { return Util.readIntLE(numberOfPartitionEntries); }
    public int getSizeOfPartitionEntry() { return Util.readIntLE(sizeOfPartitionEntry); }
    public int getPartitionEntryArrayCRC32() { return Util.readIntBE(partitionEntryArrayCRC32); }
    public byte[] getReserved2() { return Util.createCopy(reserved2); }
    
    public boolean isValid() { return getSignature() == GPT_SIGNATURE; }
    
    public void printFields(PrintStream ps, String prefix) {
	ps.println(prefix + " signature: 0x" + Util.toHexStringBE(getSignature()));
	ps.println(prefix + " revision: 0x" + Util.toHexStringBE(getRevision()));
	ps.println(prefix + " headerSize: " + getHeaderSize());
	ps.println(prefix + " crc32Checksum: 0x" + Util.toHexStringBE(getCRC32Checksum()));
	ps.println(prefix + " reserved1: 0x" + Util.toHexStringBE(getReserved1()));
	ps.println(prefix + " primaryLBA: " + getPrimaryLBA());
	ps.println(prefix + " backupLBA: " + getBackupLBA());
	ps.println(prefix + " firstUsableLBA: " + getFirstUsableLBA());
	ps.println(prefix + " lastUsableLBA: " + getLastUsableLBA());
	ps.println(prefix + " diskGUID: " + GPTEntry.getGUIDAsString(getDiskGUID()));
	ps.println(prefix + " partitionEntryLBA: " + getPartitionEntryLBA());
	ps.println(prefix + " numberOfPartitionEntries: " + getNumberOfPartitionEntries());
	ps.println(prefix + " partitionEntryArrayCRC32: 0x" + Util.toHexStringBE(getPartitionEntryArrayCRC32()));
	ps.println(prefix + " reserved2: [too much data to display...]");
    }
    
    public void print(PrintStream ps, String prefix) {
	ps.println(prefix + "GPTHeader:");
	printFields(ps, prefix);
    }
}