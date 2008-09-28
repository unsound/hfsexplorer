/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.types.hfscommon;

import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusExtentKey;
import org.catacombae.hfsexplorer.types.hfs.ExtKeyRec;

/**
 *
 * @author erik
 */
public abstract class CommonHFSExtentIndexNode extends CommonBTIndexNode {

    protected CommonHFSExtentIndexNode(byte[] data, int offset, int nodeSize, FSType type) {
        super(data, offset, nodeSize, type);
    }
    
    public static CommonHFSExtentIndexNode createHFS(byte[] data, int offset, int nodeSize) {
        return new HFSImplementation(data, offset, nodeSize);
    }
    
    public static CommonHFSExtentIndexNode createHFSPlus(byte[] data, int offset, int nodeSize) {
        return new HFSPlusImplementation(data, offset, nodeSize);
    }
    
    public static class HFSImplementation extends CommonHFSExtentIndexNode {
        public HFSImplementation(byte[] data, int offset, int nodeSize) {
            super(data, offset, nodeSize, FSType.HFS);
        }

        @Override
        protected CommonBTRecord createBTRecord(int recordNumber, byte[] data, int offset, int length) {
            final CommonHFSExtentKey key = CommonHFSExtentKey.create(new ExtKeyRec(data, offset));
            
            return CommonBTIndexRecord.createHFS(key, data, offset);
        }
    }

    public static class HFSPlusImplementation extends CommonHFSExtentIndexNode {
        public HFSPlusImplementation(byte[] data, int offset, int nodeSize) {
            super(data, offset, nodeSize, FSType.HFS_PLUS);
        }
        
        @Override
        protected CommonBTRecord createBTRecord(int recordNumber, byte[] data, int offset, int length) {
            final CommonHFSExtentKey key = CommonHFSExtentKey.create(new HFSPlusExtentKey(data, offset));
            
            return CommonBTIndexRecord.createHFSPlus(key, data, offset);
        }
    }    
}
