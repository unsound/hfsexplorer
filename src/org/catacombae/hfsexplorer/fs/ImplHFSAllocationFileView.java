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

package org.catacombae.hfsexplorer.fs;

import org.catacombae.hfsexplorer.types.hfs.ExtDescriptor;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSExtentDescriptor;
import org.catacombae.io.ReadableRandomAccessStream;

/**
 *
 * @author erik
 */
public class ImplHFSAllocationFileView extends BaseHFSAllocationFileView {
    private final ImplHFSFileSystemView hfsParentView;
    private final ReadableRandomAccessStream volumeBitmap;

    public ImplHFSAllocationFileView(ImplHFSFileSystemView parentView,
            ReadableRandomAccessStream volumeBitmap) {
        super(parentView, volumeBitmap);

        this.hfsParentView = parentView;
        this.volumeBitmap = volumeBitmap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAllocationBlockUsed(long blockNumber) throws IllegalArgumentException {
        if(blockNumber < 0 || blockNumber > ((long)Short.MAX_VALUE)*2)
            throw new IllegalArgumentException("Block number (" + blockNumber +
                    ") out of range for UInt16!");

        return super.isAllocationBlockUsed(blockNumber);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommonHFSExtentDescriptor createExtentDescriptor(long startBlock, long blockCount) {
        if(startBlock < 0 || startBlock > ((long)Short.MAX_VALUE)*2)
            throw new IllegalArgumentException("startBlock(" + startBlock +
                    ") out of range for UInt16!");
        if(blockCount < 0 || blockCount > ((long)Short.MAX_VALUE)*2)
            throw new IllegalArgumentException("blockCount(" + blockCount +
                    ") out of range for UInt16!");

        ExtDescriptor ed = new ExtDescriptor((short)startBlock, (short)blockCount);
        return CommonHFSExtentDescriptor.create(ed);
    }
}
