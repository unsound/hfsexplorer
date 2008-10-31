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
    };

    public ImplHFSXFileSystemView(ReadableRandomAccessStream hfsFile, long fsOffset, boolean cachingEnabled) {
        super(hfsFile, fsOffset, HFSX_OPERATIONS, cachingEnabled);
    }
}
