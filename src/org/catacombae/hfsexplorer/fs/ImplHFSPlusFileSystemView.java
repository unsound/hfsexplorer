/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.fs;

import org.catacombae.hfsexplorer.Util;
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
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSExtentIndexNode;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSExtentKey;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSExtentLeafNode;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSForkType;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSVolumeHeader;
import org.catacombae.io.Readable;
import org.catacombae.io.ReadableRandomAccessStream;

/**
 *
 * @author erik
 */
public class ImplHFSPlusFileSystemView extends BaseHFSFileSystemView {
    
    protected static final CatalogOperations HFS_PLUS_OPERATIONS = new CatalogOperations() {

        public CommonHFSCatalogIndexNode newCatalogIndexNode(
                byte[] data, int offset, int nodeSize, CommonBTHeaderRecord bthr) {
            return CommonHFSCatalogIndexNode.createHFSPlus(data, offset, nodeSize);
        }

        public CommonHFSCatalogKey newCatalogKey(
                CommonHFSCatalogNodeID nodeID, CommonHFSCatalogString searchString, CommonBTHeaderRecord bthr) {
            return CommonHFSCatalogKey.create(new HFSPlusCatalogKey(
                    new HFSCatalogNodeID(nodeID.toInt()), new HFSUniStr255(searchString.getBytes(), 0)));
        }

        public CommonHFSCatalogLeafNode newCatalogLeafNode(
                byte[] data, int offset, int nodeSize, CommonBTHeaderRecord bthr) {
            return CommonHFSCatalogLeafNode.createHFSPlus(data, offset, nodeSize);
        }

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

    private HFSPlusVolumeHeader getHFSPlusVolumeHeader() {
	byte[] currentBlock = new byte[512]; // Could be made a global var? (thread war?)
	hfsFile.seek(fsOffset + 1024);
	hfsFile.read(currentBlock);
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
                new HFSCatalogNodeID(fileID.toInt()), startBlock);
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
	    hfsFile.seek(fsOffset + blockNumber*staticBlockSize);
	    byte[] data = new byte[JournalInfoBlock.getStructSize()];
	    hfsFile.readFully(data);
	    return new JournalInfoBlock(data, 0);
	}
	else
	    return null;
    }

    @Override
    public String getString(CommonHFSCatalogString str) {
        if(str instanceof CommonHFSCatalogString.HFSPlusImplementation) {
            char[] ca = Util.readCharArrayBE(str.getBytes());
            return new String(ca);
        }
        else
            throw new RuntimeException("Invalid string type: " + str.getClass());
    }

    @Override
    protected CommonBTHeaderNode createCommonBTHeaderNode(byte[] currentNodeData,
            int offset, int nodeSize) {
        return CommonBTHeaderNode.createHFSPlus(currentNodeData, offset, nodeSize);
    }
}
