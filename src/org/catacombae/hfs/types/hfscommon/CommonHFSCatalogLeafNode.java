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

/**
 *
 * @author erik
 */
public abstract class CommonHFSCatalogLeafNode extends CommonBTNode<CommonHFSCatalogLeafRecord> {
    
    protected CommonHFSCatalogLeafNode(byte[] data, int offset, int nodeSize, FSType type) {
        super(data, offset, nodeSize, type);
    }
    
    public CommonHFSCatalogLeafRecord[] getLeafRecords() {
        return ic.records.toArray(new CommonHFSCatalogLeafRecord[ic.records.size()]);
    }

    public static CommonHFSCatalogLeafNode createHFSPlus(byte[] data, int offset, int nodeSize) {
        return new HFSPlusImplementation(data, offset, nodeSize);
    }
    
    public static CommonHFSCatalogLeafNode createHFSX(byte[] data, int offset, int nodeSize, BTHeaderRec bthr) {
        return new HFSXImplementation(data, offset, nodeSize, bthr).getInternal();
    }
    
    public static CommonHFSCatalogLeafNode createHFS(byte[] data, int offset, int nodeSize) {
        return new HFSImplementation(data, offset, nodeSize);
    }
    
    private static class HFSPlusImplementation extends CommonHFSCatalogLeafNode {        
        public HFSPlusImplementation(byte[] data, int offset, int nodeSize) {
            super(data, offset, nodeSize, FSType.HFS_PLUS);
        }
        
        protected HFSPlusCatalogKey createKey(byte[] data, int offset, int length) {
            return new HFSPlusCatalogKey(data, offset);
        }
        
        @Override
        protected CommonHFSCatalogLeafRecord createBTRecord(int recordNumber, byte[] data, int offset, int length) {
            return CommonHFSCatalogLeafRecord.createHFSPlus(data, offset, length);
        }
    }
    
    public static class HFSXImplementation {
        private final BTHeaderRec bthr;
        private final Internal internal;
        
        private class Internal extends CommonHFSCatalogLeafNode {
            public Internal(byte[] data, int offset, int nodeSize) {
                super(data, offset, nodeSize, FSType.HFS_PLUS);
                if(bthr == null)
                    throw new IllegalArgumentException("bthr == null");
                
            }

            @Override
            protected CommonHFSCatalogLeafRecord createBTRecord(int recordNumber, byte[] data, int offset, int length) {
                if(bthr == null)
                    throw new IllegalArgumentException("bthr == null");
                return CommonHFSCatalogLeafRecord.createHFSX(data, offset, length, bthr);
            }
        }
        
        public HFSXImplementation(byte[] data, int offset, int nodeSize, BTHeaderRec bthr) {
            this.bthr = bthr;
            this.internal = new Internal(data, offset, nodeSize);
        }
        
        private Internal getInternal() { return internal; }
    }
    
    public static class HFSImplementation extends CommonHFSCatalogLeafNode {
        public HFSImplementation(byte[] data, int offset, int nodeSize) {
            super(data, offset, nodeSize, FSType.HFS);
        }

        @Override
        protected CommonHFSCatalogLeafRecord createBTRecord(int recordNumber, byte[] data, int offset, int length) {
            return CommonHFSCatalogLeafRecord.createHFS(data, offset, length);
        }
    }
}
