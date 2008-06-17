/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.jparted.lib.ps.gpt;

import org.catacombae.jparted.lib.DataLocator;
import org.catacombae.jparted.lib.ps.PartitionSystemDetector;
import org.catacombae.hfsexplorer.io.LowLevelFile;
import org.catacombae.hfsexplorer.partitioning.GPTHeader;

/**
 *
 * @author erik
 */
public class GPTDetector extends PartitionSystemDetector {
    private DataLocator data;
    
    public GPTDetector(DataLocator pData) {
        this.data = pData;
    }
    
    @Override
    public boolean existsPartitionSystem() {
        try {
            LowLevelFile llf = data.createReadOnlyFile();
            byte[] secondBlock = new byte[512];
            llf.seek(512);
            llf.readFully(secondBlock);
            
            // Look for GPT
            // Let's assume that blocks are always 512 bytes in size with MBR and GPT. I don't know
            // how to detect the actual block size (at least when reading from a file, otherwise I
            // guess there are system specific ways)...
            GPTHeader gh = new GPTHeader(secondBlock, 0);
            if(gh.isValid()) {
                return true;
            }
        } catch(Exception e) {
        }
            
        return false;
    }

}
