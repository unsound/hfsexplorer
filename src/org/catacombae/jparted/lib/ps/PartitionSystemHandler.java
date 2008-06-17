/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.jparted.lib.ps;

/**
 * Stub.
 * @author erik
 */
public abstract class PartitionSystemHandler {
    
    public abstract long getPartitionCount();
    public abstract Partition[] getPartitions();
    public abstract void close();
}
