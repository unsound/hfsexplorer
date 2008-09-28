/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.types.hfscommon;

import org.catacombae.hfsexplorer.Util;
import org.catacombae.hfsexplorer.types.hfsplus.HFSUniStr255;

/**
 *
 * @author erik
 */
public abstract class CommonHFSCatalogString {
    public static final CommonHFSCatalogString EMPTY = create(new byte[0]);

    public static CommonHFSCatalogString create(HFSUniStr255 nodeName) {
        return new HFSPlusImplementation(nodeName);
    }
    
    public static CommonHFSCatalogString create(byte[] ckrCName) {
        return new HFSImplementation(ckrCName);
    }
    
    public String decode(StringDecoder sd) {
        byte[] data = getBytes();
        return sd.decode(data, 0, data.length);
    }
        
    public abstract byte[] getBytes();
    
    public static class HFSPlusImplementation extends CommonHFSCatalogString {
        private HFSUniStr255 nodeName;
        
        private HFSPlusImplementation(HFSUniStr255 nodeName) {
            this.nodeName = nodeName;
        }
        
        @Override
        public byte[] getBytes() {
            return nodeName.getRawUnicode();
        }
    }
    
    public static class HFSImplementation extends CommonHFSCatalogString {
        private final byte[] ckrCName;
        
        private HFSImplementation(byte[] ckrCName) {
            this.ckrCName = ckrCName;
        }

        @Override
        public byte[] getBytes() {
            return Util.createCopy(ckrCName);
        }
    }
}
