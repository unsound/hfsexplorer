/*-
 * Copyright (C) 2008 Erik Larsson
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

package org.catacombae.hfsexplorer.types.hfscommon;

import org.catacombae.hfsexplorer.types.hfsplus.BTHeaderRec;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusCatalogKey;
import org.catacombae.hfsexplorer.types.hfsx.HFSXCatalogKey;
import org.catacombae.hfsexplorer.types.hfs.CatKeyRec;

/**
 *
 * @author erik
 */
public abstract class CommonHFSCatalogIndexNode extends CommonBTIndexNode<CommonHFSCatalogKey> {
    protected CommonHFSCatalogIndexNode(byte[] data, int offset, int nodeSize,
            FSType type) {
	super(data, offset, nodeSize, type);
    }
    
    public static CommonHFSCatalogIndexNode createHFS(byte[] data, int offset, int nodeSize) {
        return new HFSImplementation(data, offset, nodeSize).getNode();
    }
    public static CommonHFSCatalogIndexNode createHFSPlus(byte[] data, int offset, int nodeSize) {
        return new HFSPlusImplementation(data, offset, nodeSize).getNode();
    }
    public static CommonHFSCatalogIndexNode createHFSX(byte[] data, int offset, int nodeSize, BTHeaderRec bthr) {
        return new HFSXImplementation(data, offset, nodeSize, bthr).getNode();
    }
    
    private static class HFSImplementation {
        private final Internal i;
        
        private class Internal extends CommonHFSCatalogIndexNode {
            public Internal(byte[] data, int offset, int nodeSize) {
                super(data, offset, nodeSize, FSType.HFS);
            }
            @Override
            protected CommonBTIndexRecord<CommonHFSCatalogKey> createBTRecord(int recordNumber, byte[] data, int offset, int length) {
                CommonHFSCatalogKey currentKey =
                        CommonHFSCatalogKey.create(new CatKeyRec(data, offset));
                return CommonBTIndexRecord.createHFS(currentKey, data, offset);
            }
        }
        public HFSImplementation(byte[] data, int offset, int nodeSize) {
            this.i = new Internal(data, offset, nodeSize);
        }
        
        public CommonHFSCatalogIndexNode getNode() {
            return i;
        }
    }
    
    private static class HFSPlusImplementation {
        private final Internal i;
        
        private class Internal extends CommonHFSCatalogIndexNode {
            public Internal(byte[] data, int offset, int nodeSize) {
                super(data, offset, nodeSize, FSType.HFS_PLUS);
            }
            @Override
            protected CommonBTIndexRecord<CommonHFSCatalogKey> createBTRecord(int recordNumber, byte[] data, int offset, int length) {
                CommonHFSCatalogKey currentKey =
                        CommonHFSCatalogKey.create(new HFSPlusCatalogKey(data, offset));
                return CommonBTIndexRecord.createHFSPlus(currentKey, data, offset);
            }
        }
        public HFSPlusImplementation(byte[] data, int offset, int nodeSize) {
            this.i = new Internal(data, offset, nodeSize);
        }
        
        public CommonHFSCatalogIndexNode getNode() {
            return i;
        }
    }
    
    private static class HFSXImplementation {
        private final Internal i;
        private final BTHeaderRec catalogHeaderRec;

        private class Internal extends CommonHFSCatalogIndexNode {
            public Internal(byte[] data, int offset, int nodeSize) {
                super(data, offset, nodeSize, FSType.HFS_PLUS);
            }
            @Override
            protected CommonBTIndexRecord<CommonHFSCatalogKey> createBTRecord(int recordNumber, byte[] data, int offset, int length) {
                CommonHFSCatalogKey currentKey =
                        CommonHFSCatalogKey.create(new HFSXCatalogKey(data, offset, catalogHeaderRec));
                return CommonBTIndexRecord.createHFSPlus(currentKey, data, offset);
            }
        }
        public HFSXImplementation(byte[] data, int offset, int nodeSize,
                BTHeaderRec catalogHeaderRec) {
            this.catalogHeaderRec = catalogHeaderRec;
            this.i = new Internal(data, offset, nodeSize);
        }
        
        public CommonHFSCatalogIndexNode getNode() {
            return i;
        }
    }
}
