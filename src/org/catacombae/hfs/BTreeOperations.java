/*-
 * Copyright (C) 2006-2009 Erik Larsson
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

package org.catacombae.hfs;

import org.catacombae.io.Readable;
import org.catacombae.hfs.types.hfscommon.CommonBTHeaderNode;
import org.catacombae.hfs.types.hfscommon.CommonBTHeaderRecord;
import org.catacombae.hfs.types.hfscommon.CommonBTNodeDescriptor;

/**
 *
 * @author erik
 */
public interface BTreeOperations {
    public CommonBTHeaderNode createCommonBTHeaderNode(byte[] currentNodeData,
            int offset, int nodeSize);
    public CommonBTNodeDescriptor readNodeDescriptor(Readable rd);
    public CommonBTHeaderRecord readHeaderRecord(Readable rd);
    public CommonBTNodeDescriptor createCommonBTNodeDescriptor(
            byte[] currentNodeData, int offset);

}
