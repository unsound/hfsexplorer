/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.jparted.lib.ps.gpt;

import org.catacombae.jparted.lib.DataLocator;
import org.catacombae.jparted.lib.ps.PartitionSystemDetector;
import org.catacombae.jparted.lib.ps.PartitionSystemHandler;
import org.catacombae.jparted.lib.ps.PartitionSystemHandlerFactory;
import org.catacombae.jparted.lib.ps.PartitionSystemImplementationInfo;

/**
 *
 * @author erik
 */
public class GPTHandlerFactory extends PartitionSystemHandlerFactory {

    @Override
    public PartitionSystemHandler createHandler(DataLocator data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PartitionSystemDetector createDetector(DataLocator partitionData) {
        return new GPTDetector(partitionData);
    }
    
    @Override
    public PartitionSystemImplementationInfo getInfo() {
        return new PartitionSystemImplementationInfo("GUID Partition Table",
                "Catacombae GPT PS Handler", "1.0", "Catacombae");
    }

}
