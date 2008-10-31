/*-
 * Copyright (C) 2006-2008 Erik Larsson
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

import org.catacombae.hfsexplorer.Util;
import org.catacombae.hfsexplorer.io.ForkFilter;
import org.catacombae.hfsexplorer.io.ReadableRandomAccessSubstream;
import org.catacombae.hfsexplorer.types.hfsplus.BTHeaderRec;
import org.catacombae.hfsexplorer.types.hfsplus.BTNodeDescriptor;
import org.catacombae.hfsexplorer.types.hfsplus.HFSCatalogNodeID;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusCatalogKey;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusExtentKey;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusVolumeHeader;
import org.catacombae.hfsexplorer.types.hfsplus.HFSUniStr255;
import org.catacombae.hfsexplorer.types.hfsplus.JournalInfoBlock;
import org.catacombae.hfsexplorer.types.hfscommon.CommonBTHeaderNode;
import org.catacombae.hfsexplorer.types.hfscommon.CommonBTHeaderRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonBTNodeDescriptor;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogIndexNode;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogKey;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogLeafNode;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogLeafRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogNodeID;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogNodeID.ReservedID;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogString;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSExtentDescriptor;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSExtentIndexNode;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSExtentKey;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSExtentLeafNode;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSForkData;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSForkType;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSVolumeHeader;
import org.catacombae.io.Readable;
import org.catacombae.io.ReadableRandomAccessStream;

/**
 *
 * @author erik
 */
public class ImplHFSPlusFileSystemView extends BaseHFSFileSystemView {
    private static final CommonHFSCatalogString EMPTY_STRING =
            CommonHFSCatalogString.createHFSPlus(new HFSUniStr255(""));
    
    protected static final CatalogOperations HFS_PLUS_OPERATIONS = new CatalogOperations() {

        @Override
        public CommonHFSCatalogIndexNode newCatalogIndexNode(
                byte[] data, int offset, int nodeSize, CommonBTHeaderRecord bthr) {
            return CommonHFSCatalogIndexNode.createHFSPlus(data, offset, nodeSize);
        }

        @Override
        public CommonHFSCatalogKey newCatalogKey(
                CommonHFSCatalogNodeID nodeID, CommonHFSCatalogString searchString, CommonBTHeaderRecord bthr) {
            return CommonHFSCatalogKey.create(new HFSPlusCatalogKey(
                    new HFSCatalogNodeID((int)nodeID.toLong()), new HFSUniStr255(searchString.getStructBytes(), 0)));
        }
        
        @Override
        public CommonHFSCatalogLeafNode newCatalogLeafNode(
                byte[] data, int offset, int nodeSize, CommonBTHeaderRecord bthr) {
            return CommonHFSCatalogLeafNode.createHFSPlus(data, offset, nodeSize);
        }

        @Override
        public CommonHFSCatalogLeafRecord newCatalogLeafRecord(
                byte[] data, int offset, CommonBTHeaderRecord bthr) {
            return CommonHFSCatalogLeafRecord.createHFSPlus(data, offset, data.length-offset);
        }
    };

    public ImplHFSPlusFileSystemView(ReadableRandomAccessStream hfsFile, long fsOffset, boolean cachingEnabled) {
        this(hfsFile, fileReadOffset, HFS_PLUS_OPERATIONS, cachingEnabled);
    }
    
    protected ImplHFSPlusFileSystemView(ReadableRandomAccessStream hfsFile, long fsOffset, CatalogOperations catOps, boolean cachingEnabled) {
        super(hfsFile, fsOffset, catOps, cachingEnabled);
    }

    public HFSPlusVolumeHeader getHFSPlusVolumeHeader() {
        //System.err.println("getHFSPlusVolumeHeader()");
	byte[] currentBlock = new byte[512]; // Could be made a global var? (thread war?)
        //System.err.println("  hfsFile.seek(" + (fsOffset + 1024) + ")");
        //System.err.println("  hfsFile.read(byte[" + currentBlock.length + "])");
	hfsFile.readFrom(fsOffset + 1024, currentBlock);
        return new HFSPlusVolumeHeader(currentBlock);
    }
    
    @Override
    public CommonHFSVolumeHeader getVolumeHeader() {
        return CommonHFSVolumeHeader.create(getHFSPlusVolumeHeader());
    }

    @Override
    protected CommonBTNodeDescriptor getNodeDescriptor(Readable rd) {
        byte[] data = new byte[BTNodeDescriptor.length()];
        rd.readFully(data);
        final BTNodeDescriptor btnd = new BTNodeDescriptor(data, 0);

        return CommonBTNodeDescriptor.create(btnd);
    }

    @Override
    protected CommonBTHeaderRecord getHeaderRecord(Readable rd) {
        byte[] data = new byte[BTHeaderRec.length()];
        rd.readFully(data);
        BTHeaderRec bthr = new BTHeaderRec(data, 0);

        return CommonBTHeaderRecord.create(bthr);
    }

    @Override
    protected CommonBTNodeDescriptor createCommonBTNodeDescriptor(byte[] currentNodeData, int offset) {
        final BTNodeDescriptor btnd = new BTNodeDescriptor(currentNodeData, offset);
        return CommonBTNodeDescriptor.create(btnd);
    }

    @Override
    protected CommonHFSExtentIndexNode createCommonHFSExtentIndexNode(byte[] currentNodeData, int offset, int nodeSize) {
        return CommonHFSExtentIndexNode.createHFSPlus(currentNodeData, offset, nodeSize);
    }

    @Override
    protected CommonHFSExtentLeafNode createCommonHFSExtentLeafNode(byte[] currentNodeData, int offset, int nodeSize) {
        return CommonHFSExtentLeafNode.createHFSPlus(currentNodeData, offset, nodeSize);
    }

    @Override
    protected CommonHFSExtentKey createCommonHFSExtentKey(CommonHFSForkType forkType, CommonHFSCatalogNodeID fileID, int startBlock) {
        final byte forkTypeByte;
        switch(forkType) {
            case DATA_FORK:
                forkTypeByte = HFSPlusExtentKey.DATA_FORK;
                break;
            case RESOURCE_FORK:
                forkTypeByte = HFSPlusExtentKey.RESOURCE_FORK;
                break;
            default:
                throw new RuntimeException("Invalid fork type");
        }
        HFSPlusExtentKey key = new HFSPlusExtentKey(forkTypeByte,
                new HFSCatalogNodeID((int)fileID.toLong()), startBlock);
        return CommonHFSExtentKey.create(key);
    }

    @Override
    protected CommonHFSCatalogNodeID getCommonHFSCatalogNodeID(ReservedID requestedNodeID) {
        return CommonHFSCatalogNodeID.getHFSPlusReservedID(requestedNodeID);
    }

    /*
    @Override
    protected CommonHFSCatalogString createCommonHFSCatalogString(String name) {
        return CommonHFSCatalogString.create(new HFSUniStr255(name));
    }
     * */

    @Override
    public JournalInfoBlock getJournalInfoBlock() {
        HFSPlusVolumeHeader vh = getHFSPlusVolumeHeader();
        if(vh.getAttributeVolumeJournaled()) {
            long blockNumber = Util.unsign(vh.getJournalInfoBlock());
            //hfsFile.seek();
            byte[] data = new byte[JournalInfoBlock.getStructSize()];
            hfsFile.readFullyFrom(fsOffset + blockNumber * vh.getBlockSize(), data);
            return new JournalInfoBlock(data, 0);
        }
        else
            return null;
    }
    
    @Override
    public CommonHFSCatalogString encodeString(String str) {
        return CommonHFSCatalogString.HFSPlusImplementation.createHFSPlus(new HFSUniStr255(str));
    }
    
    @Override
    public String decodeString(CommonHFSCatalogString str) {
        if(str instanceof CommonHFSCatalogString.HFSPlusImplementation) {
            CommonHFSCatalogString.HFSPlusImplementation hStr =
                    (CommonHFSCatalogString.HFSPlusImplementation)str;
            return new String(hStr.getInternal().getUnicode());
        }
        else
            throw new RuntimeException("Invalid string type: " + str.getClass());
    }

    @Override
    protected CommonBTHeaderNode createCommonBTHeaderNode(byte[] currentNodeData,
            int offset, int nodeSize) {
        return CommonBTHeaderNode.createHFSPlus(currentNodeData, offset, nodeSize);
    }

    @Override
    public BaseHFSAllocationFileView getAllocationFileView() {
        HFSPlusVolumeHeader vh = getHFSPlusVolumeHeader();

        CommonHFSForkData allocationFileFork = CommonHFSForkData.create(vh.getAllocationFile());
        CommonHFSExtentDescriptor[] extDescriptors = getAllExtentDescriptors(
                CommonHFSCatalogNodeID.getHFSPlusReservedID(ReservedID.ALLOCATION_FILE),
                allocationFileFork,
                CommonHFSForkType.DATA_FORK);

        ForkFilter allocationFileStream = new ForkFilter(allocationFileFork, extDescriptors,
                new ReadableRandomAccessSubstream(hfsFile), fsOffset, Util.unsign(vh.getBlockSize()), 0);

        return new ImplHFSPlusAllocationFileView(this, allocationFileStream);
    }

    @Override
    public CommonHFSCatalogString getEmptyString() {
        return EMPTY_STRING;
    }
}
