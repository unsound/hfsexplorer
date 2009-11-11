/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.storage.fs.hfs.plus;

import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSExtentDescriptor;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusExtentDescriptor;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.storage.fs.hfs.AllocationFile;

/**
 *
 * @author erik
 */
public class HFSPlusAllocationFile extends AllocationFile {
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
        if(blockNumber < 0 || blockNumber > ((long)Integer.MAX_VALUE)*2)
            throw new IllegalArgumentException("Block number (" + blockNumber +
                    ") out of range for UInt32!");

        return super.isAllocationBlockUsed(blockNumber);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommonHFSExtentDescriptor createExtentDescriptor(long startBlock, long blockCount) {
        if(startBlock < 0 || startBlock > ((long)Integer.MAX_VALUE)*2)
            throw new IllegalArgumentException("startBlock(" + startBlock +
                    ") out of range for UInt32!");
        if(blockCount < 0 || blockCount > ((long)Integer.MAX_VALUE)*2)
            throw new IllegalArgumentException("blockCount(" + blockCount +
                    ") out of range for UInt32!");

        HFSPlusExtentDescriptor hped =
                new HFSPlusExtentDescriptor((int)startBlock, (int)blockCount);
        return CommonHFSExtentDescriptor.create(hped);
    }
}
