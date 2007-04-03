/*-
 * Copyright (C) 2006-2007 Erik Larsson
 * 
 * All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 */

package org.catacombae.hfsexplorer.partitioning;
import java.io.PrintStream;

/** Generalization of the features that a simple partitioning system should provide. */
public interface PartitionSystem {
    public int getUsedPartitionCount();
    public Partition getPartitionEntry(int index);
    public Partition[] getPartitionEntries();
    public Partition[] getUsedPartitionEntries();
    public String toString();
    public String getLongName();
    public String getShortName();
    public void printFields(PrintStream ps, String prefix);
    public void print(PrintStream ps, String prefix);

}
