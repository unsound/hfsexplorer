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

import org.catacombae.hfsexplorer.types.hfsplus.BTHeaderRec;
import org.catacombae.hfsexplorer.types.hfsplus.HFSCatalogNodeID;
import org.catacombae.hfsexplorer.types.hfsplus.HFSUniStr255;
import org.catacombae.hfsexplorer.types.hfsx.HFSXCatalogKey;
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
