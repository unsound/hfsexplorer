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

package org.catacombae.jparted.lib.ps;

/**
 * Stub.
 * @author erik
 */
public abstract class PartitionSystemHandler {
    
    public abstract long getPartitionCount();
    public abstract Partition[] getPartitions();
    public abstract void close();
    
    /**
     * Returns the partition with number <code>partitionNumber</code>, or
     * <code>null</code> if there is no partition with the specified number.
     * 
     * @param partitionNumber the requested partition number.
     * @return the partition with number <code>partitionNumber</code>, or
     * <code>null</code> if there is no partition with the specified number.
     */
    public Partition getPartition(int partitionNumber) {
        Partition[] parts = getPartitions();
        if(partitionNumber >= 0 && partitionNumber < parts.length)
            return parts[partitionNumber];
        else
            return null;
    }
}
