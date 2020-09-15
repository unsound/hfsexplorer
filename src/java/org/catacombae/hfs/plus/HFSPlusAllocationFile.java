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

package org.catacombae.hfs.plus;

import org.catacombae.hfs.types.hfscommon.CommonHFSExtentDescriptor;
import org.catacombae.hfs.types.hfsplus.HFSPlusExtentDescriptor;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.hfs.AllocationFile;
import org.catacombae.hfs.Limits;

/**
 * @author <a href="https://catacombae.org" target="_top">Erik Larsson</a>
 */
public class HFSPlusAllocationFile extends AllocationFile implements Limits {
    private HFSPlusVolume hfsPlusParentView;
    private ReadableRandomAccessStream allocationFile;

    public HFSPlusAllocationFile(HFSPlusVolume parentView,
            ReadableRandomAccessStream allocationFile) {
        super(parentView, allocationFile);

        this.hfsPlusParentView = parentView;
        this.allocationFile = allocationFile;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAllocationBlockUsed(long blockNumber) throws IllegalArgumentException {
        if(blockNumber < 0 || blockNumber > UINT32_MAX) {
            throw new IllegalArgumentException("Block number (" + blockNumber +
                    ") out of range for UInt32!");
        }

        return super.isAllocationBlockUsed(blockNumber);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommonHFSExtentDescriptor createExtentDescriptor(long startBlock, long blockCount) {
        if(startBlock < 0 || startBlock > UINT32_MAX) {
            throw new IllegalArgumentException("startBlock(" + startBlock +
                    ") out of range for UInt32!");
        }
        else if(blockCount < 0 || blockCount > UINT32_MAX) {
            throw new IllegalArgumentException("blockCount(" + blockCount +
                    ") out of range for UInt32!");
        }

        HFSPlusExtentDescriptor hped =
                new HFSPlusExtentDescriptor((int)startBlock, (int)blockCount);
        return CommonHFSExtentDescriptor.create(hped);
    }
}
