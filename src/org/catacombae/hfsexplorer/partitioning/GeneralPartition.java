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

package org.catacombae.hfsexplorer.partitioning;

import java.io.PrintStream;
import org.catacombae.jparted.lib.ps.PartitionType;

/**
 * A general implementation of a partition, not bound to any specific partition
 * system.
 * @author Erik Larsson, erik82@kth.se
 */
public class GeneralPartition implements Partition {
    private final long startOffset;
    private final long length;
    private final PartitionType type;
    
    public GeneralPartition(long startOffset, long length) {
        this(startOffset, length, PartitionType.UNKNOWN);
    }
    
    public GeneralPartition(long startOffset, long length, PartitionType type) {
        this.startOffset = startOffset;
        this.length = length;
        this.type = type;
    }
    
    public long getStartOffset() {
        return startOffset;
    }

    public long getLength() {
        return length;
    }

    public PartitionType getType() {
        return type;
    }

    public void printFields(PrintStream ps, String prefix) {
        ps.println(prefix + " startOffset: " + startOffset);
        ps.println(prefix + " length: " + length);
    }

    public void print(PrintStream ps, String prefix) {
        ps.println(prefix + "GeneralPartition:");
        printFields(ps, prefix);
    }

}
