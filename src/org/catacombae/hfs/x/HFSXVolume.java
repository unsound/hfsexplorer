/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfs.x;

import org.catacombae.hfsexplorer.types.hfscommon.CommonBTHeaderRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogIndexNode;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogKey;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogLeafNode;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogLeafRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogNodeID;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogString;
import org.catacombae.hfsexplorer.types.hfsplus.BTHeaderRec;
import org.catacombae.hfsexplorer.types.hfsplus.HFSCatalogNodeID;
import org.catacombae.hfsexplorer.types.hfsplus.HFSUniStr255;
import org.catacombae.hfsexplorer.types.hfsx.HFSXCatalogKey;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.hfs.CatalogOperations;
import org.catacombae.hfs.plus.HFSPlusVolume;

/**
 *
 * @author erik
 */
public class HFSXVolume extends HFSPlusVolume {
    public HFSXVolume(ReadableRandomAccessStream hfsFile, long fsOffset,
            boolean cachingEnabled) {

        super(hfsFile, fsOffset, cachingEnabled, new HFSXCatalogOperations());
    }

    private static class HFSXCatalogOperations implements CatalogOperations {
        private BTHeaderRec getBTHeaderRec(CommonBTHeaderRecord bthr) {
            if(bthr instanceof CommonBTHeaderRecord.HFSPlusImplementation) {
                return ((CommonBTHeaderRecord.HFSPlusImplementation)bthr).getInternal();
            }
            else
                throw new IllegalArgumentException("Invalid type of bthr: " + bthr);
        }

        public CommonHFSCatalogIndexNode newCatalogIndexNode(byte[] data,
                int offset, int nodeSize, CommonBTHeaderRecord bthr) {

            BTHeaderRec trueBthr = getBTHeaderRec(bthr);
            return CommonHFSCatalogIndexNode.createHFSX(data, offset, nodeSize, trueBthr);
        }

        public CommonHFSCatalogKey newCatalogKey(CommonHFSCatalogNodeID nodeID,
                CommonHFSCatalogString searchString, CommonBTHeaderRecord bthr) {
        
            BTHeaderRec trueBthr = getBTHeaderRec(bthr);
            return CommonHFSCatalogKey.create(new HFSXCatalogKey(
                    new HFSCatalogNodeID((int) nodeID.toLong()),
                    new HFSUniStr255(searchString.getStructBytes(), 0), trueBthr));
        }

        public CommonHFSCatalogLeafNode newCatalogLeafNode(byte[] data,
                int offset, int nodeSize, CommonBTHeaderRecord bthr) {

            BTHeaderRec trueBthr = getBTHeaderRec(bthr);
            return CommonHFSCatalogLeafNode.createHFSX(data, offset, nodeSize, trueBthr);
        }

        public CommonHFSCatalogLeafRecord newCatalogLeafRecord(byte[] data,
                int offset, CommonBTHeaderRecord bthr) {

            BTHeaderRec trueBthr = getBTHeaderRec(bthr);
            return CommonHFSCatalogLeafRecord.createHFSX(data, offset, offset + data.length, trueBthr);
        }

    }
}
