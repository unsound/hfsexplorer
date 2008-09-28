/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.types.hfscommon;

import org.catacombae.hfsexplorer.types.BTHeaderRec;
import org.catacombae.hfsexplorer.types.HFSPlusCatalogKey;
import org.catacombae.hfsexplorer.types.HFSXCatalogKey;
import org.catacombae.hfsexplorer.types.hfs.CatKeyRec;

/**
 *
 * @author erik
 */
public abstract class CommonHFSCatalogIndexNode extends CommonBTIndexNode {
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
            protected CommonBTRecord createBTRecord(int recordNumber, byte[] data, int offset, int length) {
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
            protected CommonBTRecord createBTRecord(int recordNumber, byte[] data, int offset, int length) {
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
            protected CommonBTRecord createBTRecord(int recordNumber, byte[] data, int offset, int length) {
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
