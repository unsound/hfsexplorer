/*-
 * Copyright (C) 2006-2012 Erik Larsson
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

package org.catacombae.hfs.types.hfsplus;

import org.catacombae.util.Util;

/**
 * @author <a href="https://catacombae.org" target="_top">Erik Larsson</a>
 */
public class HFSPlusAttributesIndexNode extends BTIndexNode {
    public HFSPlusAttributesIndexNode(byte[] data, int offset, int nodeSize) {
        super(data, offset, nodeSize);

        for(int i = 0; i < records.length; ++i) {
            final int currentOffset = Util.unsign(offsets[i]);
            HFSPlusAttributesKey currentKey =
                    new HFSPlusAttributesKey(data, offset + currentOffset);

            records[i] =
                    new BTIndexRecord(currentKey, data, offset + currentOffset);
        }
    }
}
