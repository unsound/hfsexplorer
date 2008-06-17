/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.jparted.lib.ps.mbr;

import org.catacombae.jparted.lib.DataLocator;
import org.catacombae.jparted.lib.ps.PartitionSystemDetector;
import org.catacombae.jparted.lib.ps.PartitionSystemHandler;
import org.catacombae.jparted.lib.ps.PartitionSystemHandlerFactory;
import org.catacombae.jparted.lib.ps.PartitionSystemImplementationInfo;

/**
 *
 * @author erik
 */
public class MBRHandlerFactory extends PartitionSystemHandlerFactory {

    @Override
    public PartitionSystemHandler createHandler(DataLocator data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PartitionSystemDetector createDetector(DataLocator partitionData) {
        return new MBRDetector(partitionData);
    }

    @Override
    public PartitionSystemImplementationInfo getInfo() {
        return new PartitionSystemImplementationInfo("Master Boot Record",
                "Catacombae MBR PS Handler", "1.0", "Catacombae");
    }

}
