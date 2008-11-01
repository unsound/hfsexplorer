/*-
 * Copyright (C) 2008 Erik Larsson
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
