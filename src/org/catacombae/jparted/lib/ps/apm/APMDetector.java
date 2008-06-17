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
import org.catacombae.jparted.lib.ps.PartitionSystemDetector;

/**
 *
 * @author erik
 */
public class APMDetector extends PartitionSystemDetector {
    private DataLocator data;
    
    public APMDetector(DataLocator pData) {
        this.data = pData;
    }
    
    @Override
    public boolean existsPartitionSystem() {
        try {
            LowLevelFile llf = data.createReadOnlyFile();
            byte[] firstBlock = new byte[512];
            
            llf.readFully(firstBlock);
            
            // Look for APM
            DriverDescriptorRecord ddr = new DriverDescriptorRecord(firstBlock, 0);
            if(ddr.isValid()) {
                int blockSize = Util.unsign(ddr.getSbBlkSize());
                //long numberOfBlocksOnDevice = Util.unsign(ddr.getSbBlkCount());
                //bitStream.seek(blockSize*1); // second block, first partition in list
                ApplePartitionMap apm = new ApplePartitionMap(llf, blockSize * 1, blockSize);
                if(apm.getUsedPartitionCount() > 0) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }
}
