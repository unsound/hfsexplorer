/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.storage.fs.hfs;

import org.catacombae.io.Readable;
import org.catacombae.hfsexplorer.fs.BaseHFSAllocationFileView;
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
import org.catacombae.hfsexplorer.types.hfsplus.JournalInfoBlock;

/**
 *
 * @author erik
 */
interface HFSOperations {
    public CommonHFSVolumeHeader getVolumeHeader();
    public CommonBTHeaderNode createCommonBTHeaderNode(byte[] currentNodeData,
            int offset, int nodeSize);
    public CommonBTNodeDescriptor readNodeDescriptor(Readable rd);
    public CommonBTHeaderRecord readHeaderRecord(Readable rd);
    public CommonBTNodeDescriptor createCommonBTNodeDescriptor(
            byte[] currentNodeData, int offset);

    public CommonHFSExtentIndexNode createCommonHFSExtentIndexNode(
            byte[] currentNodeData, int offset, int nodeSize);

    public CommonHFSExtentLeafNode createCommonHFSExtentLeafNode(
            byte[] currentNodeData, int offset, int nodeSize);

    public CommonHFSExtentKey createCommonHFSExtentKey(
            CommonHFSForkType forkType, CommonHFSCatalogNodeID fileID,
            int startBlock);

    public CommonHFSCatalogNodeID getCommonHFSCatalogNodeID(
            ReservedID requestedNodeID);
    /*protected CommonHFSCatalogString createCommonHFSCatalogString(
            String name);*/

    public BaseHFSAllocationFileView getAllocationFileView();

    public CommonHFSCatalogString getEmptyString();


    /**
     * Returns the default StringDecoder instance for this view. For HFS file systems the decoder
     * can be set in the HFS-specific subclass, but in HFS+ and HFSX file systems it will always
     * return a UTF-16BE string decoder.
     *
     * @return the default StringDecoder instance for this view.
     */
    //public abstract StringDecoder getDefaultStringDecoder();

    /**
     * Decodes the supplied CommonHFSCatalogString according to the current
     * settings of the view.
     *
     * @param str the CommonHFSCatalogString to decode.
     * @return a decoded representation of <code>str</code>.
     */
    public abstract String decodeString(CommonHFSCatalogString str);

   /**
     * Encodes the supplied CommonHFSCatalogString according to the current
     * settings of the view.
     *
     * @param str the CommonHFSCatalogString to encode.
     * @return an encoded representation of <code>str</code>.
     */
    public abstract CommonHFSCatalogString encodeString(String str);

    /** Returns the journal info block if a journal is present, null otherwise. */
    public abstract JournalInfoBlock getJournalInfoBlock();

    public CommonHFSCatalogIndexNode newCatalogIndexNode(byte[] data,
            int offset, int nodeSize, CommonBTHeaderRecord bthr);

    public CommonHFSCatalogKey newCatalogKey(CommonHFSCatalogNodeID nodeID,
            CommonHFSCatalogString searchString, CommonBTHeaderRecord bthr);

    public CommonHFSCatalogLeafNode newCatalogLeafNode(byte[] data,
            int offset, int nodeSize, CommonBTHeaderRecord bthr);

    public CommonHFSCatalogLeafRecord newCatalogLeafRecord(byte[] data,
            int offset, CommonBTHeaderRecord bthr);
}
