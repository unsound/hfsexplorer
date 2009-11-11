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

import java.io.PrintStream;

/**
 *
 * @author erik
 */
public abstract class Partition {
    /** Returns the start offset in bytes. */
    public abstract long getStartOffset();
    
    /** Returns the length of the partition in bytes. */
    public abstract long getLength();
    
    /** Returns the type of the partition. */
    public abstract PartitionType getType();
    
    /** Prints the values of the fields of this partition. */
    public abstract void printFields(PrintStream ps, String prefix);
    
    /** Prints the type name followed by the values of its fields. */
    public abstract void print(PrintStream ps, String prefix);
}
