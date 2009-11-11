/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfs.original;

import org.catacombae.hfsexplorer.io.ReadableRandomAccessSubstream;
import org.catacombae.hfsexplorer.types.hfs.BTHdrRec;
import org.catacombae.hfsexplorer.types.hfs.CatKeyRec;
import org.catacombae.hfsexplorer.types.hfs.ExtKeyRec;
import org.catacombae.hfsexplorer.types.hfs.MasterDirectoryBlock;
import org.catacombae.hfsexplorer.types.hfs.NodeDescriptor;
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
import org.catacombae.io.ReadableConcatenatedStream;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.hfs.AllocationFile;
import org.catacombae.hfs.AttributesFile;
import org.catacombae.hfs.BTreeOperations;
import org.catacombae.hfs.CatalogOperations;
import org.catacombae.hfs.ExtentsOverflowOperations;
import org.catacombae.hfs.HFSVolume;
import org.catacombae.hfs.HotFilesFile;
import org.catacombae.hfs.Journal;
import org.catacombae.util.Util;

/**
 *
 * @author erik
 */
public class HFSOriginalVolume extends HFSVolume {
    private static final CommonHFSCatalogString EMPTY_STRING =
            CommonHFSCatalogString.createHFS(new byte[0]);

    private final HFSOriginalAllocationFile allocationFile;
    private final MutableStringCodec<CharsetStringCodec> stringCodec;

    public HFSOriginalVolume(ReadableRandomAccessStream hfsFile, long fsOffset,
            boolean cachingEnabled, String encodingName) {
        
        super(hfsFile, fsOffset, cachingEnabled, new HFSBTreeOperations(),
                new HFSCatalogOperations(), new HFSExtentsOverflowOperations());

        this.stringCodec = new MutableStringCodec<CharsetStringCodec>(
                new CharsetStringCodec(encodingName));

        this.allocationFile = createAllocationFile();
    }
    
    public MasterDirectoryBlock getHFSMasterDirectoryBlock() {
        byte[] currentBlock = new byte[512];
        hfsFile.readFrom(fsOffset + 1024, currentBlock);
        return new MasterDirectoryBlock(currentBlock, 0);
    }

    @Override
    public CommonHFSVolumeHeader getVolumeHeader() {
        return CommonHFSVolumeHeader.create(getHFSMasterDirectoryBlock());
    }

    public HFSOriginalAllocationFile createAllocationFile() {
        MasterDirectoryBlock mdb = getHFSMasterDirectoryBlock();

        int numAllocationBlocks = Util.unsign(mdb.getDrNmAlBlks());
        int volumeBitmapSize = numAllocationBlocks/8 + (numAllocationBlocks%8 != 0 ? 1 : 0);

        ReadableConcatenatedStream volumeBitmapStream =
                new ReadableConcatenatedStream(new ReadableRandomAccessSubstream(hfsFile),
                fsOffset + 512*Util.unsign(mdb.getDrVBMSt()),
                volumeBitmapSize);

        return new HFSOriginalAllocationFile(this, volumeBitmapStream);
    }

    public AllocationFile getAllocationFile() {
        return allocationFile;
    }

    @Override
    public boolean hasAttributesFile() {
        return false;
    }

    @Override
    public boolean hasJournal() {
        return false;
    }

    @Override
    public boolean hasHotFilesFile() {
        return false; // right? TODO: check this assumption
    }

    @Override
    public AttributesFile getAttributesFile() {
        return null;
    }

    @Override
    public Journal getJournal() {
        return null;
    }

    @Override
    public HotFilesFile getHotFilesFile() {
        return null;
    }

    @Override
    public CommonHFSCatalogNodeID getCommonHFSCatalogNodeID(
            ReservedID requestedNodeID) {
        return CommonHFSCatalogNodeID.getHFSReservedID(requestedNodeID);
    }

    @Override
    public CommonHFSCatalogString getEmptyString() {
        return EMPTY_STRING;
    }

    /**
     * Sets the charset that should be used when transforming HFS file names
     * to java Strings, and reverse.
     *
     * @param encodingName the charset to use
     */
    public void setStringEncoding(String encodingName) {
        this.stringCodec.setDecoder(new CharsetStringCodec(encodingName));
    }

    /**
     * Returns the charset that is currently used when transforming HFS file
     * names to java Strings, and reverse.
     * @return the current tranformation charset name.
     */
    public String getStringEncoding() {
        return stringCodec.getDecoder().getCharsetName();
    }

    @Override
    public String decodeString(CommonHFSCatalogString str) {
        if(str instanceof CommonHFSCatalogString.HFSImplementation)
            return stringCodec.decode(str.getStringBytes());
        else
            throw new RuntimeException("Invalid string type: " +
                    str.getClass());
    }

    @Override
    public CommonHFSCatalogString encodeString(String str) {
        byte[] bytes = stringCodec.encode(str);
        return CommonHFSCatalogString.createHFS(bytes);
    }

    @Override
    public void close() {
        // Do something here?
    }

    private static class HFSBTreeOperations implements BTreeOperations {

        public CommonBTHeaderNode createCommonBTHeaderNode(
                byte[] currentNodeData, int offset, int nodeSize) {

            return CommonBTHeaderNode.createHFS(currentNodeData, offset,
                    nodeSize);
        }

        public CommonBTNodeDescriptor readNodeDescriptor(Readable rd) {

            byte[] data = new byte[NodeDescriptor.length()];
            rd.readFully(data);

            return createCommonBTNodeDescriptor(data, 0);
        }

        public CommonBTHeaderRecord readHeaderRecord(Readable rd) {

            byte[] data = new byte[BTHdrRec.length()];
            rd.readFully(data);
            BTHdrRec bthr = new BTHdrRec(data, 0);

            return CommonBTHeaderRecord.create(bthr);
        }

        public CommonBTNodeDescriptor createCommonBTNodeDescriptor(
                byte[] currentNodeData, int i) {
            
            final NodeDescriptor nd = new NodeDescriptor(currentNodeData, i);
            return CommonBTNodeDescriptor.create(nd);
        }

    }

    private static class HFSCatalogOperations implements CatalogOperations {

        @Override
        public CommonHFSCatalogIndexNode newCatalogIndexNode(byte[] data,
                int offset, int nodeSize, CommonBTHeaderRecord bthr) {

            return CommonHFSCatalogIndexNode.createHFS(data, offset, nodeSize);
        }

        @Override
        public CommonHFSCatalogKey newCatalogKey(CommonHFSCatalogNodeID nodeID,
                CommonHFSCatalogString searchString,
                CommonBTHeaderRecord bthr) {

            return CommonHFSCatalogKey.create(new CatKeyRec(
                    (int)nodeID.toLong(), searchString.getStringBytes()));
        }

        @Override
        public CommonHFSCatalogLeafNode newCatalogLeafNode(byte[] data,
                int offset, int nodeSize, CommonBTHeaderRecord bthr) {

            return CommonHFSCatalogLeafNode.createHFS(data, offset, nodeSize);
        }

        @Override
        public CommonHFSCatalogLeafRecord newCatalogLeafRecord(
                byte[] data, int offset, CommonBTHeaderRecord bthr) {

            return CommonHFSCatalogLeafRecord.createHFS(data, offset,
                    data.length-offset);
        }
    }

    private static class HFSExtentsOverflowOperations implements ExtentsOverflowOperations {

        public CommonHFSExtentIndexNode createCommonHFSExtentIndexNode(
                byte[] currentNodeData, int i, int nodeSize) {

            return CommonHFSExtentIndexNode.createHFS(currentNodeData, i,
                    nodeSize);
        }

        public CommonHFSExtentLeafNode createCommonHFSExtentLeafNode(
                byte[] currentNodeData, int i, int nodeSize) {

            return CommonHFSExtentLeafNode.createHFS(currentNodeData, i,
                    nodeSize);
        }

        public CommonHFSExtentKey createCommonHFSExtentKey(
                CommonHFSForkType forkType, CommonHFSCatalogNodeID fileID,
                int startBlock) {
            
            if(startBlock < Short.MIN_VALUE || startBlock > Short.MAX_VALUE)
                throw new IllegalArgumentException("start block out of range " +
                        "for short (signed 16-bit integer)");
            short startBlockShort = (short) startBlock;

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
            ExtKeyRec key = new ExtKeyRec(forkTypeByte, (int) fileID.toLong(),
                    startBlockShort);
            return CommonHFSExtentKey.create(key);
        }
    }
}
