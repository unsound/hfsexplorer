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

package org.catacombae.storage.ps.apm;

import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.storage.ps.apm.types.ApplePartitionMap;
import org.catacombae.storage.ps.apm.types.DriverDescriptorRecord;
import org.catacombae.storage.io.DataLocator;
import org.catacombae.storage.ps.Partition;
import org.catacombae.storage.ps.PartitionSystemHandler;

/**
 * @author <a href="https://catacombae.org" target="_top">Erik Larsson</a>
 */
public class APMHandler extends PartitionSystemHandler {

    private DataLocator partitionData;

    public APMHandler(DataLocator partitionData) {
        this.partitionData = partitionData;
    }

    @Override
    public long getPartitionCount() {
        ApplePartitionMap apm = readPartitionMap();
        return apm.getUsedPartitionCount();
    }

    @Override
    public Partition[] getPartitions() {
        ApplePartitionMap apm = readPartitionMap();
        return apm.getUsedPartitionEntries();
    }

    @Override
    public void close() {
        partitionData.close();
    }

    public DriverDescriptorRecord readDriverDescriptorRecord() {
        ReadableRandomAccessStream llf = null;
        try {
            llf = partitionData.createReadOnlyFile();
            byte[] firstBlock = new byte[512];

            llf.readFully(firstBlock);

            DriverDescriptorRecord ddr = new DriverDescriptorRecord(firstBlock, 0);
            return ddr;
        } finally {
            if(llf != null)
                llf.close();
        }
    }

    public ApplePartitionMap readPartitionMap() {
        ReadableRandomAccessStream llf = null;
        try {
            llf = partitionData.createReadOnlyFile();

            // Look for APM
            int blockSize = 0;
            long numberOfBlocksOnDevice = 0;

            try {
                DriverDescriptorRecord ddr = readDriverDescriptorRecord();
                if(ddr.isValid()) {
                    blockSize = ddr.getSbBlkSize();
                    numberOfBlocksOnDevice = ddr.getSbBlkCount();
                }
            } catch(Exception e) {
            }

            if(blockSize == 0) {
                /* Check if the second block has a valid partition signature. */
                byte[] secondBlock = new byte[512];
                llf.seek(512);
                llf.readFully(secondBlock);
                if(secondBlock[0] == 'P' && secondBlock[1] == 'M') {
                    blockSize = 512;
                    numberOfBlocksOnDevice =
                            (llf.length() + blockSize - 1) / blockSize;
                }
            }

            if(blockSize > 0) {
                //bitStream.seek(blockSize*1); // second block, first partition in list
                ApplePartitionMap apm = new ApplePartitionMap(llf,
                        blockSize * 1, blockSize);

                if(apm.getPartitionCount() == 0) {
                    apm = null;
                }

                if(apm == null || blockSize > 512) {
                    long lastPartitionBlock = 0;

                    if(apm != null) {
                        for(int i = 0; i < apm.getPartitionCount(); ++i) {
                            final long curLastPartitionBlock =
                                    (apm.getPartitionEntry(i).getStartOffset() +
                                    apm.getPartitionEntry(i).getLength() +
                                    blockSize - 1) / blockSize;
                            if(curLastPartitionBlock > lastPartitionBlock) {
                                lastPartitionBlock = curLastPartitionBlock;
                            }
                        }
                    }

                    if(apm == null || (numberOfBlocksOnDevice > 0 &&
                            lastPartitionBlock > numberOfBlocksOnDevice))
                    {
                        /* Last partition extends beyond the end of the device
                         * as specified in the DDR. Chances are that the
                         * partition layout refers to a 512-byte sector size. */
                        try {
                            final ApplePartitionMap fallbackApm =
                                    new ApplePartitionMap(llf, 512, 512);
                            if(fallbackApm.getPartitionCount() > 0) {
                                apm = fallbackApm;
                            }
                        } catch(Exception e) {
                            /* Ignore errors. */
                        }
                    }
                }

                return apm;
            }
            else
                return null;
        } finally {
            if(llf != null)
                llf.close();
        }
    }

}
