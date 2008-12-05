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
package org.catacombae.hfsexplorer;

import org.catacombae.hfsexplorer.partitioning.ApplePartitionMap;
import org.catacombae.hfsexplorer.partitioning.DriverDescriptorRecord;
import org.catacombae.hfsexplorer.partitioning.GPTHeader;
import org.catacombae.hfsexplorer.partitioning.GUIDPartitionTable;
import org.catacombae.hfsexplorer.partitioning.MBRPartitionTable;
import org.catacombae.hfsexplorer.partitioning.PartitionSystem;
//import org.catacombae.io.ReadableFileStream;
import org.catacombae.io.ReadableRandomAccessStream;

/**
 * This class will contain code to identify which partitioning scheme is used, and methods
 * to retreive a list of partitions. It will operate on a simple seekable bitstream, a
 * finite string of bytes.
 */
public class PartitionSystemRecognizer {

    public static enum PartitionSystemType {

        APPLE_PARTITION_MAP, MASTER_BOOT_RECORD, GUID_PARTITION_TABLE, NONE_FOUND
    };
    private ReadableRandomAccessStream bitstream;

    public PartitionSystemRecognizer(ReadableRandomAccessStream bitstream) {
        this.bitstream = bitstream;
    }

    /**
     * Detects one of the following partition systems:
     * <ul>
     * <li>Apple Partition Map</li>
     * <li>Master Boot Record</li>
     * <li>GUID Partition Table</li>
     * </ul>
     * NOTE: This method will never ever throw an exception.
     * When no partition system can be found, or an exception is thrown that makes
     * detecting partition systems impossible, PartitionSystemType.NONE_FOUND is returned. 
     */
    public PartitionSystemType detectPartitionSystem() {
        try {
            // First we read the blocks that we will use to determine partition systems into memory.
            byte[] piece1 = null;
            byte[] piece2 = null;
            try {
                bitstream.seek(0);
                piece1 = new byte[512];
                bitstream.readFully(piece1);
            } catch(Exception e) {
                piece1 = null;
            }
            try {
                bitstream.seek(512);
                piece2 = new byte[512];
                bitstream.readFully(piece2);
            } catch(Exception e) {
                piece2 = null;
            }

            if(piece1 != null) {
                try {
                    // Look for APM
                    DriverDescriptorRecord ddr = new DriverDescriptorRecord(piece1, 0);
                    if(ddr.isValid()) {
                        int blockSize = Util.unsign(ddr.getSbBlkSize());
                        //long numberOfBlocksOnDevice = Util.unsign(ddr.getSbBlkCount());
                        //bitStream.seek(blockSize*1); // second block, first partition in list
                        ApplePartitionMap apm = new ApplePartitionMap(bitstream, blockSize * 1, blockSize);
                        if(apm.getUsedPartitionCount() > 0)
                            return PartitionSystemType.APPLE_PARTITION_MAP;
                    }
                } catch(Exception e) {
                }
            }

            if(piece2 != null) {
                try {
                    // Look for GPT
                    // Let's assume that blocks are always 512 bytes in size with MBR and GPT. I don't know
                    // how to detect the actual block size (at least when reading from a file, otherwise I
                    // guess there are system specific ways)...
                    GPTHeader gh = new GPTHeader(piece2, 0, 512);
                    if(gh.isValid()) {
                        return PartitionSystemType.GUID_PARTITION_TABLE;
                    }
                } catch(Exception e) {
                }
            }

            if(piece1 != null) {
                try {
                    // Look for MBR (always after looking for GPT, because both may be present,
                    // in which case GPT has higher priority)
                    MBRPartitionTable mpt = new MBRPartitionTable(piece1, 0);
                    if(mpt.isValid()) {
                        // Here we should look for extended partitions, BSD disk labels, LVM volumes etc. TODO!

                        return PartitionSystemType.MASTER_BOOT_RECORD;
                    }
                } catch(Exception e) {
                }
            }
        } catch(Exception e) {
        }

        // If we haven't returned by now, then no partition system could be found...
        return PartitionSystemType.NONE_FOUND;
    }

    /** If none can be found, null is returned. If the underlying file can't be read, a
    RuntimeException is thrown. */
    public PartitionSystem getPartitionSystem() {
        PartitionSystemType type = detectPartitionSystem();
        switch(type) {
            case APPLE_PARTITION_MAP:
                byte[] ddrData = new byte[512];
                bitstream.seek(0);
                bitstream.readFully(ddrData);
                DriverDescriptorRecord ddr = new DriverDescriptorRecord(ddrData, 0);
                int blockSize = Util.unsign(ddr.getSbBlkSize());
                return new ApplePartitionMap(bitstream, blockSize * 1, blockSize);
            case GUID_PARTITION_TABLE:
                return new GUIDPartitionTable(bitstream, 0);
            case MASTER_BOOT_RECORD:
                return new MBRPartitionTable(bitstream, 0);
            default:
                return null;
        }
    }
    /*
    public static void main(String[] args) throws Exception {
    ReadableRandomAccessStream llf = new ReadableFileStream(args[0]);
    new PartitionSystemRecognizer(llf).getPartitionSystem().print(System.out, "");
    }
     */
}
