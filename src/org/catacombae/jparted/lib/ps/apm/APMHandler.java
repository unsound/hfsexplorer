/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.jparted.lib.ps.apm;

import org.catacombae.hfsexplorer.Util;
import org.catacombae.hfsexplorer.io.LowLevelFile;
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
                    apmParts[i].getLength(), convertType(apmParts[i].getType()).getGeneralType());
        }
        return result;
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

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

    private ApplePartitionMap readPartitionMap() {
        try {
            LowLevelFile llf = partitionData.createReadOnlyFile();
            byte[] firstBlock = new byte[512];
            
            llf.readFully(firstBlock);
            
            // Look for APM
            DriverDescriptorRecord ddr = new DriverDescriptorRecord(firstBlock, 0);
            if(ddr.isValid()) {
                int blockSize = Util.unsign(ddr.getSbBlkSize());
                //long numberOfBlocksOnDevice = Util.unsign(ddr.getSbBlkCount());
                //bitStream.seek(blockSize*1); // second block, first partition in list
                ApplePartitionMap apm = new ApplePartitionMap(llf, blockSize * 1, blockSize);
                return apm;
            }
        } catch (Exception e) {
        }
        return null;
    }

}
