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

//import org.catacombae.hfsexplorer.fs.StringDecoder;
import org.catacombae.hfsexplorer.Util;
import org.catacombae.hfsexplorer.types.hfsplus.HFSUniStr255;

/**
 *
 * @author erik
 */
public abstract class CommonHFSCatalogString {
    //public static final CommonHFSCatalogString EMPTY = createHFS(new byte[0]);

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
    /*
    public String decode(StringDecoder sd) {
        byte[] data = getStringBytes();
        return sd.decode(data, 0, data.length);
    }
    */
    
    /**
     * Returns the raw bytes that make up this string. They need to be interpreted in a context
     * specific manner in order to make any sense.
     * 
     * @return the raw bytes that make up this string.
     */
    public abstract byte[] getStringBytes();
    
    /**
     * Returns the bytes that make up the struct. May include string size and
     * padding in addition to the bytes that make up the string.
     * @return the bytes that make up the struct.
     */
    public abstract byte[] getStructBytes();


    public static class HFSPlusImplementation extends CommonHFSCatalogString {
        private HFSUniStr255 nodeName;
        
        private HFSPlusImplementation(HFSUniStr255 nodeName) {
            this.nodeName = nodeName;
        }

        public HFSUniStr255 getInternal() {
            return nodeName;
        }
        
        @Override
        public byte[] getStringBytes() {
            return nodeName.getRawUnicode();
        }

        @Override
        public byte[] getStructBytes() {
            return nodeName.getBytes();
        }
    }
    
    public static class HFSImplementation extends CommonHFSCatalogString {
        private final byte[] ckrCName;
        
        private HFSImplementation(byte[] ckrCName) {
            this.ckrCName = ckrCName;
        }

        @Override
        public byte[] getStringBytes() {
            return Util.createCopy(ckrCName);
        }

        @Override
        public byte[] getStructBytes() {
            return Util.createCopy(ckrCName);
        }
    }
}
