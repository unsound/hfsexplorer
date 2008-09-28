/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.types.hfscommon;

import org.catacombae.hfsexplorer.types.BTHeaderRec;
import org.catacombae.hfsexplorer.types.HFSPlusCatalogKey;

/**
 *
 * @author erik
 */
public abstract class CommonHFSCatalogLeafNode extends CommonBTNode {
    
    protected CommonHFSCatalogLeafNode(byte[] data, int offset, int nodeSize, FSType type) {
        super(data, offset, nodeSize, type);
    }
    
    public CommonHFSCatalogLeafRecord[] getLeafRecords() {
        CommonHFSCatalogLeafRecord[] res = new CommonHFSCatalogLeafRecord[ic.records.length];

        for (int i = 0; i < res.length; ++i) {
            CommonBTRecord rec = ic.records[i];
            if (rec instanceof CommonHFSCatalogLeafRecord) {
                res[i] = (CommonHFSCatalogLeafRecord) ic.records[i];
            } else {
                throw new RuntimeException("Internal error: Unexpected CommonBTRecord subtype: " +
                        rec.getClass() + " (expected CommonHFSCatalogLeafRecord)");
            }
        }

        return res;
    }

    public static CommonHFSCatalogLeafNode createHFSPlus(byte[] data, int offset, int nodeSize) {
        return new HFSPlusImplementation(data, offset, nodeSize);
    }
    
    public static CommonHFSCatalogLeafNode createHFSX(byte[] data, int offset, int nodeSize, BTHeaderRec bthr) {
        return new HFSXImplementation(data, offset, nodeSize, bthr);
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
        protected CommonBTRecord createBTRecord(int recordNumber, byte[] data, int offset, int length) {
            return CommonHFSCatalogLeafRecord.createHFSPlus(data, offset, length);
        }
    }
    
    public static class HFSXImplementation extends CommonHFSCatalogLeafNode {
        private final BTHeaderRec bthr;
        
        public HFSXImplementation(byte[] data, int offset, int nodeSize, BTHeaderRec bthr) {
            super(data, offset, nodeSize, FSType.HFS_PLUS);
            this.bthr = bthr;
        }
        
        @Override
        protected CommonBTRecord createBTRecord(int recordNumber, byte[] data, int offset, int length) {
            return CommonHFSCatalogLeafRecord.createHFSX(data, offset, length, bthr);
        }
    }
    
    public static class HFSImplementation extends CommonHFSCatalogLeafNode {
        public HFSImplementation(byte[] data, int offset, int nodeSize) {
            super(data, offset, nodeSize, FSType.HFS);
        }

        @Override
        protected CommonBTRecord createBTRecord(int recordNumber, byte[] data, int offset, int length) {
            return CommonHFSCatalogLeafRecord.createHFS(data, offset, length);
        }
    }
}
