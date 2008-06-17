/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.jparted.lib.ps.container;

import org.catacombae.jparted.lib.fs.FileSystemMajorType;
import org.catacombae.jparted.lib.ps.PartitionSystemType;

/**
 *
 * @author erik
 */
public abstract class ContainerHandler {
    /** Returns true if the container contains a file system. */
    public abstract boolean containsFileSystem();

    /** Returns true if the container contains a partition system. */
    public abstract boolean containsPartitionSystem();
    
    /** Returns true if the container contains another container. */
    public abstract boolean containsContainer();
    
    /**
     * Probes for the file system type contained within this container. Returns
     * null if the file system type could not be determined.
     */
    public abstract FileSystemMajorType detectFileSystemType();

    /**
     * Probes for the partition system type contained within this container.
     * Returns null if the partition system type could not be determined.
     */
    public abstract PartitionSystemType detectPartitionSystemType();
        
    /**
     * Probes for the container type contained within this container.
     * Returns null if the container type could not be determined.
     */
    public abstract ContainerType detectContainerType();
}
