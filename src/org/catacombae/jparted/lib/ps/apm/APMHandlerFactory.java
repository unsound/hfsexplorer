/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.jparted.lib.ps.apm;

import org.catacombae.jparted.lib.DataLocator;
import org.catacombae.jparted.lib.ps.PartitionSystemDetector;
import org.catacombae.jparted.lib.ps.PartitionSystemHandler;
import org.catacombae.jparted.lib.ps.PartitionSystemHandlerFactory;
import org.catacombae.jparted.lib.ps.PartitionSystemImplementationInfo;

/**
 *
 * @author erik
 */
public class APMHandlerFactory extends PartitionSystemHandlerFactory {

    @Override
    public PartitionSystemHandler createHandler(DataLocator partitionData) {
        return new APMHandler(partitionData);
    }

    @Override
    public PartitionSystemDetector createDetector(DataLocator partitionData) {
        return new APMDetector(partitionData);
    }

    @Override
    public PartitionSystemImplementationInfo getInfo() {
        return new PartitionSystemImplementationInfo("Apple Partition Map",
                "Catacombae APM PS Handler", "1.0", "Catacombae");
    }

}
