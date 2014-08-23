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

import org.catacombae.util.Util;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.storage.ps.apm.types.ApplePartitionMap;
import org.catacombae.storage.ps.apm.types.DriverDescriptorRecord;
import org.catacombae.storage.io.DataLocator;
import org.catacombae.storage.ps.Partition;
import org.catacombae.storage.ps.PartitionSystemHandler;

/**
 *
 * @author erik
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
            DriverDescriptorRecord ddr = readDriverDescriptorRecord();
            if(ddr.isValid()) {
                int blockSize = ddr.getSbBlkSize();
                //long numberOfBlocksOnDevice = Util.unsign(ddr.getSbBlkCount());
                //bitStream.seek(blockSize*1); // second block, first partition in list
                ApplePartitionMap apm = new ApplePartitionMap(llf,
                        blockSize * 1, blockSize);
                if(apm.getPartitionCount() > 0)
                    return apm;
                else
                    return null;
            }
            else
                return null;
        } finally {
            if(llf != null)
                llf.close();
        }
    }

}
