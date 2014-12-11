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

package org.catacombae.hfs.original;

import org.catacombae.io.ReadableRandomAccessSubstream;
import org.catacombae.hfs.types.hfs.BTHdrRec;
import org.catacombae.hfs.types.hfs.CatKeyRec;
import org.catacombae.hfs.types.hfs.ExtKeyRec;
import org.catacombae.hfs.types.hfs.MasterDirectoryBlock;
import org.catacombae.hfs.types.hfs.NodeDescriptor;
import org.catacombae.hfs.types.hfscommon.CommonBTHeaderNode;
import org.catacombae.hfs.types.hfscommon.CommonBTHeaderRecord;
import org.catacombae.hfs.types.hfscommon.CommonBTNodeDescriptor;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogIndexNode;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogKey;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogLeafNode;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogLeafRecord;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogNodeID;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogNodeID.ReservedID;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogString;
import org.catacombae.hfs.types.hfscommon.CommonHFSExtentIndexNode;
import org.catacombae.hfs.types.hfscommon.CommonHFSExtentKey;
import org.catacombae.hfs.types.hfscommon.CommonHFSExtentLeafNode;
import org.catacombae.hfs.types.hfscommon.CommonHFSForkType;
import org.catacombae.hfs.types.hfscommon.CommonHFSVolumeHeader;
import org.catacombae.io.Readable;
import org.catacombae.io.ReadableConcatenatedStream;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.hfs.AllocationFile;
import org.catacombae.hfs.AttributesFile;
import org.catacombae.hfs.HFSVolume;
import org.catacombae.hfs.HotFilesFile;
import org.catacombae.hfs.Journal;
import org.catacombae.util.Util;

/**
 * @author <a href="http://www.catacombae.org/" target="_top">Erik Larsson</a>
 */
public class HFSOriginalVolume extends HFSVolume {
    private static final CommonHFSCatalogString EMPTY_STRING =
            CommonHFSCatalogString.createHFS(new byte[0]);

    private final HFSOriginalAllocationFile allocationFile;
    private final MutableStringCodec<CharsetStringCodec> stringCodec;

    public HFSOriginalVolume(ReadableRandomAccessStream hfsFile,
            boolean cachingEnabled, String encodingName) {

        super(hfsFile, cachingEnabled);

        this.stringCodec = new MutableStringCodec<CharsetStringCodec>(
                new CharsetStringCodec(encodingName));

        this.allocationFile = createAllocationFile();
    }

    public MasterDirectoryBlock getHFSMasterDirectoryBlock() {
        byte[] currentBlock = new byte[512];
        hfsFile.readFrom(1024, currentBlock);
        return new MasterDirectoryBlock(currentBlock, 0);
    }

    @Override
    public CommonHFSVolumeHeader getVolumeHeader() {
        return CommonHFSVolumeHeader.create(getHFSMasterDirectoryBlock());
    }

    private HFSOriginalAllocationFile createAllocationFile() {
        MasterDirectoryBlock mdb = getHFSMasterDirectoryBlock();

        int numAllocationBlocks = Util.unsign(mdb.getDrNmAlBlks());
        int volumeBitmapSize = numAllocationBlocks/8 + (numAllocationBlocks%8 != 0 ? 1 : 0);

        ReadableConcatenatedStream volumeBitmapStream =
                new ReadableConcatenatedStream(new ReadableRandomAccessSubstream(hfsFile),
                512*Util.unsign(mdb.getDrVBMSt()),
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
    public CommonHFSCatalogNodeID createCommonHFSCatalogNodeID(int cnid)
    {
        return CommonHFSCatalogNodeID.create(cnid);
    }

    @Override
    public CommonHFSExtentKey createCommonHFSExtentKey(boolean isResource,
            int cnid, long startBlock)
    {
        if(startBlock > 0xFFFF) {
            throw new IllegalArgumentException("Value of 'startBlock' is too " +
                    "large for an HFS extent key.");
        }

        return CommonHFSExtentKey.create(new ExtKeyRec(
                isResource ? ExtKeyRec.FORK_TYPE_RESOURCE :
                    ExtKeyRec.FORK_TYPE_DATA,
                cnid,
                (short) startBlock));
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

    /* @Override */
    public CommonBTHeaderNode createCommonBTHeaderNode(byte[] currentNodeData,
            int offset, int nodeSize)
    {
        return CommonBTHeaderNode.createHFS(currentNodeData, offset, nodeSize);
    }

    /* @Override */
    public CommonBTNodeDescriptor readNodeDescriptor(Readable rd) {
        byte[] data = new byte[NodeDescriptor.length()];
        rd.readFully(data);

        return createCommonBTNodeDescriptor(data, 0);
    }

    /* @Override */
    public CommonBTHeaderRecord readHeaderRecord(Readable rd) {
        byte[] data = new byte[BTHdrRec.length()];
        rd.readFully(data);
        BTHdrRec bthr = new BTHdrRec(data, 0);

        return CommonBTHeaderRecord.create(bthr);
    }

    /* @Override */
    public CommonBTNodeDescriptor createCommonBTNodeDescriptor(
            byte[] currentNodeData, int i)
    {
        final NodeDescriptor nd = new NodeDescriptor(currentNodeData, i);
        return CommonBTNodeDescriptor.create(nd);
    }

    /* @Override */
    public CommonHFSCatalogIndexNode newCatalogIndexNode(byte[] data,
            int offset, int nodeSize)
    {
        return CommonHFSCatalogIndexNode.createHFS(data, offset, nodeSize);
    }

    /* @Override */
    public CommonHFSCatalogKey newCatalogKey(CommonHFSCatalogNodeID nodeID,
            CommonHFSCatalogString searchString)
    {
        return CommonHFSCatalogKey.create(new CatKeyRec((int) nodeID.toLong(),
                searchString.getStringBytes()));
    }

    /* @Override */
    public CommonHFSCatalogLeafNode newCatalogLeafNode(byte[] data, int offset,
            int nodeSize)
    {
        return CommonHFSCatalogLeafNode.createHFS(data, offset, nodeSize);
    }

    /* @Override */
    public CommonHFSCatalogLeafRecord newCatalogLeafRecord(byte[] data,
            int offset)
    {
        return CommonHFSCatalogLeafRecord.createHFS(data, offset,
                data.length - offset);
    }

    /* @Override */
    public CommonHFSExtentIndexNode createCommonHFSExtentIndexNode(
            byte[] currentNodeData, int i, int nodeSize)
    {
        return CommonHFSExtentIndexNode.createHFS(currentNodeData, i, nodeSize);
    }

    /* @Override */
    public CommonHFSExtentLeafNode createCommonHFSExtentLeafNode(
            byte[] currentNodeData, int i, int nodeSize)
    {
        return CommonHFSExtentLeafNode.createHFS(currentNodeData, i, nodeSize);
    }

    /* @Override */
    public CommonHFSExtentKey createCommonHFSExtentKey(
            CommonHFSForkType forkType, CommonHFSCatalogNodeID fileID,
            int startBlock)
    {
        if(startBlock < Short.MIN_VALUE || startBlock > Short.MAX_VALUE)
            throw new IllegalArgumentException("start block out of range for " +
                    "short (signed 16-bit integer)");
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
