/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.jparted.lib.ps;

import org.catacombae.jparted.lib.DataLocator;

/**
 * A subclass of PartitionSystemHandlerFactory must always have a empty
 * constructor in order to be used as such.
 * @author Erik Larsson
 */
public abstract class PartitionSystemHandlerFactory {
    public abstract PartitionSystemHandler createHandler(DataLocator partitionData);
    public abstract PartitionSystemDetector createDetector(DataLocator partitionData);
    public abstract PartitionSystemImplementationInfo getInfo();
}
