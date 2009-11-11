/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfs.original;

import org.catacombae.hfsexplorer.types.hfs.ExtDescriptor;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSExtentDescriptor;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.hfs.AllocationFile;

/**
 *
 * @author erik
 */
public class HFSOriginalAllocationFile extends AllocationFile {
    private final HFSOriginalVolume hfsParentView;
    private final ReadableRandomAccessStream volumeBitmap;

    public HFSOriginalAllocationFile(HFSOriginalVolume parentView,
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
