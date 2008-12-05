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

package org.catacombae.jparted.lib.ps.apm;

import org.catacombae.hfsexplorer.Util;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.hfsexplorer.partitioning.ApplePartitionMap;
import org.catacombae.hfsexplorer.partitioning.DriverDescriptorRecord;
import org.catacombae.jparted.lib.DataLocator;
import org.catacombae.jparted.lib.ps.Partition;
import org.catacombae.jparted.lib.ps.PartitionSystemHandler;
import org.catacombae.jparted.lib.ps.StandardPartition;

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
        Partition[] result = new Partition[apm.getUsedPartitionCount()];
        org.catacombae.hfsexplorer.partitioning.Partition[] apmParts =
                apm.getUsedPartitionEntries();
        for(int i = 0; i < result.length; ++i) {
            result[i] = new StandardPartition(apmParts[i].getStartOffset(),
                    apmParts[i].getLength(), apmParts[i].getType());
        }
        return result;
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /*
    private APMPartitionType convertType(org.catacombae.hfsexplorer.partitioning.Partition.PartitionType type) {
        switch(type) {
            case APPLE_APM:
                return APMPartitionType.APPLE_PARTITION_MAP;
            case APPLE_DRIVER:
                return APMPartitionType.APPLE_DRIVER;
            case APPLE_DRIVER43:
                return APMPartitionType.APPLE_DRIVER43;
            case APPLE_MFS:
                return APMPartitionType.APPLE_MFS;
            case APPLE_HFS:
                return APMPartitionType.APPLE_HFS;
            case APPLE_HFSX:
                return APMPartitionType.APPLE_HFSX;
            case APPLE_UNIX_SVR2:
                return APMPartitionType.APPLE_UNIX_SVR2;
            case APPLE_PRODOS:
                return APMPartitionType.APPLE_PRODOS;
            case APPLE_SCRATCH:
                return APMPartitionType.APPLE_SCRATCH;
            case APPLE_FREE:
                return APMPartitionType.APPLE_FREE;
            default:
                return null;
        }
    }
    */

    private ApplePartitionMap readPartitionMap() {
        ReadableRandomAccessStream llf = null;
        try {
            llf = partitionData.createReadOnlyFile();
            byte[] firstBlock = new byte[512];
            
            llf.readFully(firstBlock);
            
            // Look for APM
            DriverDescriptorRecord ddr = new DriverDescriptorRecord(firstBlock, 0);
            if(ddr.isValid()) {
                int blockSize = Util.unsign(ddr.getSbBlkSize());
                //long numberOfBlocksOnDevice = Util.unsign(ddr.getSbBlkCount());
                //bitStream.seek(blockSize*1); // second block, first partition in list
                ApplePartitionMap apm = new ApplePartitionMap(llf, blockSize * 1, blockSize);
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
