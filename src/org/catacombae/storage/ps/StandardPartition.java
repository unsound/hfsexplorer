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
public class StandardPartition extends Partition {
    private final long startOffset;
    private final long length;
    private final PartitionType type;
    
    public StandardPartition(long startOffset, long length, PartitionType type) {
        this.startOffset = startOffset;
        this.length = length;
        this.type = type;
               
    }
    
    @Override
    public long getStartOffset() {
        return startOffset;
    }

    @Override
    public long getLength() {
        return length;
    }

    @Override
    public PartitionType getType() {
        return type;
    }

    @Override
    public void printFields(PrintStream ps, String prefix) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void print(PrintStream ps, String prefix) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
