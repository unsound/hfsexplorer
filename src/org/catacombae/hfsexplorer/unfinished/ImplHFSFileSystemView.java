/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.unfinished;

import java.io.UnsupportedEncodingException;
import org.catacombae.hfsexplorer.types.hfsplus.JournalInfoBlock;
import org.catacombae.hfsexplorer.types.hfs.BTHdrRec;
import org.catacombae.hfsexplorer.types.hfs.CatKeyRec;
import org.catacombae.hfsexplorer.types.hfs.ExtKeyRec;
import org.catacombae.hfsexplorer.types.hfs.MasterDirectoryBlock;
import org.catacombae.hfsexplorer.types.hfs.NodeDescriptor;
import org.catacombae.hfsexplorer.types.hfscommon.CommonBTHeaderNode;
import org.catacombae.hfsexplorer.types.hfscommon.CommonBTHeaderRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonBTNodeDescriptor;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogFolderRecord;
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
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.io.Readable;

/**
 *
 * @author erik
 */
public class ImplHFSFileSystemView extends BaseHFSFileSystemView {
    
    protected static final CatalogOperations HFS_OPERATIONS = new CatalogOperations() {

        public CommonHFSCatalogIndexNode newCatalogIndexNode(
                byte[] data, int offset, int nodeSize, CommonBTHeaderRecord bthr) {
            return CommonHFSCatalogIndexNode.createHFS(data, offset, nodeSize);
        }

        public CommonHFSCatalogKey newCatalogKey(
                CommonHFSCatalogNodeID nodeID, CommonHFSCatalogString searchString, CommonBTHeaderRecord bthr) {
            return CommonHFSCatalogKey.create(new CatKeyRec(nodeID.toInt(), searchString.getBytes()));
        }

        public CommonHFSCatalogLeafNode newCatalogLeafNode(
                byte[] data, int offset, int nodeSize, CommonBTHeaderRecord bthr) {
            return CommonHFSCatalogLeafNode.createHFS(data, offset, nodeSize);
        }

        public CommonHFSCatalogLeafRecord newCatalogLeafRecord(
                byte[] data, int offset, CommonBTHeaderRecord bthr) {
            return CommonHFSCatalogLeafRecord.createHFS(data, offset, data.length-offset);
        }
    };

    private CharsetStringDecoder stringDecoder;

    public ImplHFSFileSystemView(ReadableRandomAccessStream hfsFile, long fsOffset, boolean cachingEnabled, String encodingName) {
        this(hfsFile, fileReadOffset, HFS_OPERATIONS, cachingEnabled);
        this.stringDecoder = new CharsetStringDecoder(encodingName);
    }
    
    protected ImplHFSFileSystemView(ReadableRandomAccessStream hfsFile, long fsOffset, CatalogOperations catOps, boolean cachingEnabled) {
        super(hfsFile, fsOffset, catOps, cachingEnabled);
    }
    
    @Override
    public CommonHFSVolumeHeader getVolumeHeader() {
        byte[] currentBlock = new byte[512]; // Could be made a global var? (thread war?)
        hfsFile.seek(fsOffset + 1024);
        hfsFile.read(currentBlock);
        return CommonHFSVolumeHeader.create(new MasterDirectoryBlock(currentBlock, 0));
    }

    @Override
    protected CommonBTNodeDescriptor getNodeDescriptor(Readable rd) {
        byte[] data = new byte[NodeDescriptor.length()];
        rd.readFully(data);

        return createCommonBTNodeDescriptor(data, 0);
    }

    @Override
    protected CommonBTHeaderRecord getHeaderRecord(Readable rd) {
        byte[] data = new byte[BTHdrRec.length()];
        rd.readFully(data);
        BTHdrRec bthr = new BTHdrRec(data, 0);

        return CommonBTHeaderRecord.create(bthr);
    }

    @Override
    protected CommonBTNodeDescriptor createCommonBTNodeDescriptor(byte[] currentNodeData, int i) {
        final NodeDescriptor nd = new NodeDescriptor(currentNodeData, i);
        return CommonBTNodeDescriptor.create(nd);
    }

    @Override
    protected CommonHFSExtentIndexNode createCommonHFSExtentIndexNode(byte[] currentNodeData, int i, int nodeSize) {
        return CommonHFSExtentIndexNode.createHFS(currentNodeData, i, nodeSize);
    }

    @Override
    protected CommonHFSExtentLeafNode createCommonHFSExtentLeafNode(byte[] currentNodeData, int i, int nodeSize) {
        return CommonHFSExtentLeafNode.createHFS(currentNodeData, i, nodeSize);
    }

    @Override
    protected CommonHFSExtentKey createCommonHFSExtentKey(CommonHFSForkType forkType, CommonHFSCatalogNodeID fileID, int startBlock) {
        if(startBlock < Short.MIN_VALUE || startBlock > Short.MAX_VALUE)
            throw new IllegalArgumentException("start block out of range for short (signed 16-bit integer)");
        short startBlockShort = (short)startBlock;

        final byte forkTypeByte;
        switch(forkType) {
            case DATA_FORK:
                forkTypeByte = ExtKeyRec.FORK_TYPE_DATA;
                break;
            case RESOURCE_FORK:
                forkTypeByte = ExtKeyRec.FORK_TYPE_RESOURCE;
                break;
            default:
                throw new RuntimeException("Invalid fork type");
        }
        ExtKeyRec key = new ExtKeyRec(forkTypeByte, fileID.toInt(), startBlockShort);
        return CommonHFSExtentKey.create(key);
    }

    @Override
    protected CommonHFSCatalogNodeID getCommonHFSCatalogNodeID(ReservedID requestedNodeID) {
        return CommonHFSCatalogNodeID.getHFSReservedID(requestedNodeID);
    }

    /*
    @Override
    protected CommonHFSCatalogString createCommonHFSCatalogString(String name) {
        try {
            return CommonHFSCatalogString.create(name.getBytes(encodingName));
        } catch(UnsupportedEncodingException e) {
            throw new RuntimeException("Unsupported encoding: " + encodingName, e);
        }
    }
     * */

    @Override
    public JournalInfoBlock getJournalInfoBlock() {
        return null; // Journaling is not supported on HFS
    }

    /**
     * Sets the charset that should be used when transforming HFS file names
     * to java Strings, and reverse.
     *
     * @param encodingName the charset to use
     */
    public void setStringEncoding(String encodingName) {
        this.stringDecoder = new CharsetStringDecoder(encodingName);
    }

    public String getStringEncoding() {
        return stringDecoder.getCharsetName();
    }

    @Override
    public String getString(CommonHFSCatalogString str) {
        if(str instanceof CommonHFSCatalogString.HFSImplementation)
            return str.decode(stringDecoder);
        else
            throw new RuntimeException("Invalid string type: " + str.getClass());
    }

    @Override
    protected CommonBTHeaderNode createCommonBTHeaderNode(byte[] currentNodeData,
            int offset, int nodeSize) {
        return CommonBTHeaderNode.createHFS(currentNodeData, offset, nodeSize);
    }
}
