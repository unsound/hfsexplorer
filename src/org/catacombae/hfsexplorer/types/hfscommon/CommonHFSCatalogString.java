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
    public static final CommonHFSCatalogString EMPTY = createHFS(new byte[0]);

    public static CommonHFSCatalogString createHFSPlus(HFSUniStr255 nodeName) {
        return new HFSPlusImplementation(nodeName);
    }
    
    public static CommonHFSCatalogString createHFS(byte[] ckrCName) {
        return new HFSImplementation(ckrCName);
    }
    
    /**
     * Decodes the string data with a specified StringDecoder. This method is mostly for debug use.
     * Normal applications should use the BaseHFSFileSystemView.decodeString(...) method to get a
     * Java string from a CommonHFSCatalogString.
     * 
     * @param sd the StringDecoder to use for decoding.
     * @return the string data, decoded with the specified StringDecoder.
     */
    public String decode(StringDecoder sd) {
        byte[] data = getBytes();
        return sd.decode(data, 0, data.length);
    }
    
    /**
     * Returns the raw bytes that make up this string. They need to be interpreted in a context
     * specific manner in order to make any sense.
     * 
     * @return the raw bytes that make up this string.
     */
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
