/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.storage.fs.hfs;

import org.catacombae.hfsexplorer.types.hfscommon.CommonBTHeaderRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogIndexNode;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogKey;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogLeafNode;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogLeafRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogNodeID;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogString;

/**
 *
 * @author erik
 */
public interface CatalogOperations {
    public CommonHFSCatalogIndexNode newCatalogIndexNode(byte[] data,
            int offset, int nodeSize, CommonBTHeaderRecord bthr);

    public CommonHFSCatalogKey newCatalogKey(CommonHFSCatalogNodeID nodeID,
            CommonHFSCatalogString searchString, CommonBTHeaderRecord bthr);

    public CommonHFSCatalogLeafNode newCatalogLeafNode(byte[] data,
            int offset, int nodeSize, CommonBTHeaderRecord bthr);

    public CommonHFSCatalogLeafRecord newCatalogLeafRecord(byte[] data,
            int offset, CommonBTHeaderRecord bthr);

}
