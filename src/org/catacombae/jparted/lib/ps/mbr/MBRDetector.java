/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.jparted.lib.ps.mbr;

import org.catacombae.jparted.lib.DataLocator;
import org.catacombae.jparted.lib.ps.PartitionSystemDetector;
import org.catacombae.hfsexplorer.io.LowLevelFile;
import org.catacombae.hfsexplorer.partitioning.MBRPartitionTable;

/**
 *
 * @author erik
 */
public class MBRDetector extends PartitionSystemDetector {
    private DataLocator data;
    
    public MBRDetector(DataLocator pData) {
        this.data = pData;
    }
    
    @Override
    public boolean existsPartitionSystem() {
        try {
            LowLevelFile llf = data.createReadOnlyFile();
            byte[] firstBlock = new byte[512];
            llf.read(firstBlock);
            
            // Look for MBR
            MBRPartitionTable mpt = new MBRPartitionTable(firstBlock, 0);
            if(mpt.isValid()) {
                return true;
            }
        } catch(Exception e) {
        }
            
        return false;
    }
}
