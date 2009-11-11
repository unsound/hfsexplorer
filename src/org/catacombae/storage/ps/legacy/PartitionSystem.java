/*-
 * Copyright (C) 2006-2008 Erik Larsson
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

package org.catacombae.hfsexplorer.partitioning;
import java.io.PrintStream;

/** Generalization of the features that a simple partitioning system should provide. */
public interface PartitionSystem {
    /**
     * Performs partition system specific validitity and/or sanity checks to
     * determine if the partition system is valid and correct.
     */
    public boolean isValid();
    
    /**
     * Returns the number of partition entries represented in this partition system.
     * @return the number of partition entries represented in this partition system.
     */
    public int getPartitionCount();
    
    /**
     * Fetches the partition entry at index <code>index</code>.
     * <code>index<code> must be larger than or equal to 0 and less than
     * <code>getPartitionCount()</code>.
     * @param index the requested index.
     * @return the requested Partition.
     */
    public Partition getPartitionEntry(int index);
    
    /**
     * Returns all Partition entries represented in this partition system.
     * @return all Partition entries represented in this partition system.
     */
    public Partition[] getPartitionEntries();
    
    /**
     * Returns the number of partition entries that contain partition data.
     * @return the number of partition entries that contain partition data.
     */
    public int getUsedPartitionCount();
    
    /**
     * Sorts out those partition entries that contain valid partition data and
     * returns them in an array.
     * @return an array of valid, usable partition entries.
     */
    public Partition[] getUsedPartitionEntries();
    
    /**
     * Returns the long name of this partition system, for example "Master Boot
     * Record".
     * @return the long name of this partition system.
     */
    public String getLongName();
    
    /**
     * Returns the short name of this partition system, for example "MBR".
     * @return the short name of this partition system.
     */
    public String getShortName();
    
    /**
     * Prints the fields of this structure to the supplied
     * <code>PrintStream</code>, prepending <code>prefix</code> to each new
     * line.
     * @param ps the output <code>PrintStream</code>.
     * @param prefix the prefix to prepend to each line.
     */
    public void printFields(PrintStream ps, String prefix);

    /**
     * Prints the name and fields of this structure to the supplied
     * <code>PrintStream</code>, prepending <code>prefix</code> to each new
     * line.
     * @param ps the output <code>PrintStream</code>.
     * @param prefix the prefix to prepend to each line.
     */
    public void print(PrintStream ps, String prefix);

}
