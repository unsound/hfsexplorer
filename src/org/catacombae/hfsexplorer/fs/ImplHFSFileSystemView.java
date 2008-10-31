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
import org.catacombae.hfsexplorer.io.ReadableRandomAccessSubstream;
import org.catacombae.hfsexplorer.fs.StringCodec;
import org.catacombae.hfsexplorer.types.hfsplus.JournalInfoBlock;
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
import org.catacombae.io.ConcatenatedStream;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.io.Readable;
import org.catacombae.io.ReadableConcatenatedStream;

/**
 *
 * @author erik
 */
public class ImplHFSFileSystemView extends BaseHFSFileSystemView {
    
    private static final CommonHFSCatalogString EMPTY_STRING =
            CommonHFSCatalogString.createHFS(new byte[0]);
    protected static final CatalogOperations HFS_OPERATIONS = new CatalogOperations() {

        @Override
        public CommonHFSCatalogIndexNode newCatalogIndexNode(
                byte[] data, int offset, int nodeSize, CommonBTHeaderRecord bthr) {
            return CommonHFSCatalogIndexNode.createHFS(data, offset, nodeSize);
        }

        @Override
        public CommonHFSCatalogKey newCatalogKey(
                CommonHFSCatalogNodeID nodeID, CommonHFSCatalogString searchString, CommonBTHeaderRecord bthr) {
            return CommonHFSCatalogKey.create(new CatKeyRec((int)nodeID.toLong(), searchString.getStringBytes()));
        }

        @Override
        public CommonHFSCatalogLeafNode newCatalogLeafNode(
                byte[] data, int offset, int nodeSize, CommonBTHeaderRecord bthr) {
            return CommonHFSCatalogLeafNode.createHFS(data, offset, nodeSize);
        }

        @Override
        public CommonHFSCatalogLeafRecord newCatalogLeafRecord(
                byte[] data, int offset, CommonBTHeaderRecord bthr) {
            return CommonHFSCatalogLeafRecord.createHFS(data, offset, data.length-offset);
        }
    };

    private final MutableStringCodec<CharsetStringCodec> stringCodec;

    public ImplHFSFileSystemView(ReadableRandomAccessStream hfsFile, long fsOffset, boolean cachingEnabled, String encodingName) {
        this(hfsFile, fileReadOffset, HFS_OPERATIONS, cachingEnabled, encodingName);
    }
    
    protected ImplHFSFileSystemView(ReadableRandomAccessStream hfsFile, long fsOffset, CatalogOperations catOps, boolean cachingEnabled, String encodingName) {
        super(hfsFile, fsOffset, catOps, cachingEnabled);

        this.stringCodec = new MutableStringCodec<CharsetStringCodec>(new CharsetStringCodec(encodingName));
    }


    public MasterDirectoryBlock getMasterDirectoryBlock() {
        byte[] currentBlock = new byte[512]; // Could be made a global var? (thread war?)
        hfsFile.readFrom(fsOffset + 1024, currentBlock);
        return new MasterDirectoryBlock(currentBlock, 0);
    }
    
    @Override
    public CommonHFSVolumeHeader getVolumeHeader() {
        return CommonHFSVolumeHeader.create(getMasterDirectoryBlock());
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
        ExtKeyRec key = new ExtKeyRec(forkTypeByte, (int)fileID.toLong(), startBlockShort);
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
        this.stringCodec.setDecoder(new CharsetStringCodec(encodingName));
    }

    public String getStringEncoding() {
        return stringCodec.getDecoder().getCharsetName();
    }

    @Override
    public CommonHFSCatalogString encodeString(String str) {
        byte[] bytes = stringCodec.encode(str);
        return CommonHFSCatalogString.createHFS(bytes);
    }
    
    @Override
    public String decodeString(CommonHFSCatalogString str) {
        if(str instanceof CommonHFSCatalogString.HFSImplementation)
            return stringCodec.decode(str.getStringBytes());
        else
            throw new RuntimeException("Invalid string type: " + str.getClass());
    }

    @Override
    protected CommonBTHeaderNode createCommonBTHeaderNode(byte[] currentNodeData,
            int offset, int nodeSize) {
        return CommonBTHeaderNode.createHFS(currentNodeData, offset, nodeSize);
    }

    @Override
    public BaseHFSAllocationFileView getAllocationFileView() {
        MasterDirectoryBlock mdb = getMasterDirectoryBlock();

        int numAllocationBlocks = Util.unsign(mdb.getDrNmAlBlks());
        int volumeBitmapSize = numAllocationBlocks/8 + (numAllocationBlocks%8 != 0 ? 1 : 0);
        
        ReadableConcatenatedStream volumeBitmapStream =
                new ReadableConcatenatedStream(new ReadableRandomAccessSubstream(hfsFile),
                fsOffset + 512*Util.unsign(mdb.getDrVBMSt()),
                volumeBitmapSize);
        
        return new ImplHFSAllocationFileView(this, volumeBitmapStream);
    }

    @Override
    public CommonHFSCatalogString getEmptyString() {
        return EMPTY_STRING;
    }
}
