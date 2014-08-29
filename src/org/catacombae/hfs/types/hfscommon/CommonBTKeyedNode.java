/*-
 * Copyright (C) 2014 Erik Larsson
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

package org.catacombae.hfs.types.hfscommon;

import java.util.List;

/**
 * @author <a href="http://www.catacombae.org">Erik Larsson</a>
 */
public abstract class CommonBTKeyedNode <R extends CommonBTKeyedRecord>
        extends CommonBTNode<R>
{
    protected CommonBTKeyedNode(byte[] data, int offset, int nodeSize,
            FSType type)
    {
        super(data, offset, nodeSize, type);
    }

    public R getBTKeyedRecord(int index) {
        return getBTRecord(index);
    }

    public List<R> getBTKeyedRecords() {
        return getBTRecords();
    }
}
