/*-
 * Copyright (C) 2008-2012 Erik Larsson
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

import org.catacombae.hfs.types.hfsplus.HFSPlusAttributesKey;

/**
 *
 * @author erik
 */
public abstract class CommonHFSAttributesIndexNode
        extends CommonBTKeyedNode<CommonBTIndexRecord<CommonHFSAttributesKey>>
{
    protected CommonHFSAttributesIndexNode(byte[] data, int offset,
            int nodeSize, FSType type)
    {
        super(data, offset, nodeSize, type);
    }

    public static CommonHFSAttributesIndexNode createHFSPlus(byte[] data,
            int offset, int nodeSize)
    {
        return new HFSPlusImplementation(data, offset, nodeSize);
    }

    public static class HFSPlusImplementation
            extends CommonHFSAttributesIndexNode
    {
        public HFSPlusImplementation(byte[] data, int offset, int nodeSize) {
            super(data, offset, nodeSize, FSType.HFS_PLUS);
        }

        @Override
        protected CommonBTIndexRecord<CommonHFSAttributesKey> createBTRecord(
                int recordNumber, byte[] data, int offset, int length)
        {
            final CommonHFSAttributesKey key = CommonHFSAttributesKey.create(
                    new HFSPlusAttributesKey(data, offset));

            return CommonBTIndexRecord.createHFSPlus(key, data, offset);
        }
    }
}
