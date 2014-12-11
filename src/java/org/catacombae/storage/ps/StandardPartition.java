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

package org.catacombae.storage.ps;

import java.io.PrintStream;

/**
 * A basic implementation of the Partition interface, which just takes the
 * variables as parameters.
 *
 * @author <a href="http://www.catacombae.org/" target="_top">Erik Larsson</a>
 */
public class StandardPartition implements Partition {
    private final long startOffset;
    private final long length;
    private final PartitionType type;

    public StandardPartition(long startOffset, long length, PartitionType type) {
        this.startOffset = startOffset;
        this.length = length;
        this.type = type;
    }

    /* @Override */
    public long getStartOffset() {
        return startOffset;
    }

    /* @Override */
    public long getLength() {
        return length;
    }

    /* @Override */
    public PartitionType getType() {
        return type;
    }

    /* @Override */
    public void printFields(PrintStream ps, String prefix) {
        ps.println(prefix + "startOffset: " + startOffset);
        ps.println(prefix + "length: " + length);
        ps.println(prefix + "type: " + type);
    }

    /* @Override */
    public void print(PrintStream ps, String prefix) {
        ps.println(prefix + getClass().getSimpleName());
        printFields(ps, prefix + " ");
    }

}
