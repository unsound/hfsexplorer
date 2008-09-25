/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.unfinished;

import org.catacombae.hfsexplorer.types.BTHeaderRec;
import org.catacombae.hfsexplorer.types.HFSCatalogNodeID;
import org.catacombae.hfsexplorer.types.HFSUniStr255;
import org.catacombae.hfsexplorer.types.HFSXCatalogKey;
import org.catacombae.hfsexplorer.types.hfscommon.CommonBTHeaderRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogIndexNode;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogKey;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogLeafNode;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogLeafRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogNodeID;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogString;
import org.catacombae.io.ReadableRandomAccessStream;

/**
 *
 * @author erik
 */
public class ImplHFSXFileSystemView extends ImplHFSPlusFileSystemView {
    protected static final CatalogOperations HFSX_OPERATIONS = new CatalogOperations() {

        public CommonHFSCatalogIndexNode newCatalogIndexNode(byte[] data,
                int offset, int nodeSize, CommonBTHeaderRecord bthr) {
            byte[] bthrData = bthr.getBytes();
            BTHeaderRec bthr2 = new BTHeaderRec(bthrData, 0); // ugly reconstructing solution
            
            return CommonHFSCatalogIndexNode.createHFSX(data, offset, nodeSize, bthr2);
        }

        public CommonHFSCatalogKey newCatalogKey(CommonHFSCatalogNodeID nodeID,
                CommonHFSCatalogString searchString, CommonBTHeaderRecord bthr) {
            byte[] bthrData = bthr.getBytes();
            BTHeaderRec bthr2 = new BTHeaderRec(bthrData, 0); // ugly reconstructing solution
            return CommonHFSCatalogKey.create(new HFSXCatalogKey(
                    new HFSCatalogNodeID(nodeID.toInt()),
                        new HFSUniStr255(searchString.getBytes(), 0), bthr2));
        }

        public CommonHFSCatalogLeafNode newCatalogLeafNode(byte[] data,
                int offset, int nodeSize, CommonBTHeaderRecord bthr) {
            byte[] bthrData = bthr.getBytes();
            BTHeaderRec bthr2 = new BTHeaderRec(bthrData, 0); // ugly reconstructing solution

            return CommonHFSCatalogLeafNode.createHFSX(data, offset, nodeSize, bthr2);
        }

        public CommonHFSCatalogLeafRecord newCatalogLeafRecord(byte[] data,
                int offset, CommonBTHeaderRecord bthr) {
            byte[] bthrData = bthr.getBytes();
            BTHeaderRec bthr2 = new BTHeaderRec(bthrData, 0); // ugly reconstructing solution
            
            return CommonHFSCatalogLeafRecord.createHFSX(data, offset, offset+data.length, bthr2);
        }
    };

    public ImplHFSXFileSystemView(ReadableRandomAccessStream hfsFile, long fsOffset, boolean cachingEnabled) {
        super(hfsFile, fsOffset, HFSX_OPERATIONS, cachingEnabled);
    }
}
