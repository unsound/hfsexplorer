/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.types.hfscommon;

import java.io.PrintStream;
import org.catacombae.hfsexplorer.types.HFSPlusExtentKey;
import org.catacombae.hfsexplorer.types.hfs.ExtKeyRec;

/**
 *
 * @author erik
 */
public abstract class CommonHFSExtentKey extends CommonBTKey {

    public void print(PrintStream ps, String prefix) {
        ps.println(prefix + getClass().getSimpleName() + ":");
        printFields(ps, prefix + " ");
    }
    
    public static CommonHFSExtentKey create(HFSPlusExtentKey key) {
        return new HFSPlusImplementation(key);
    }

    public static CommonHFSExtentKey create(ExtKeyRec key) {
        return new HFSImplementation(key);
    }
    
    public static class HFSPlusImplementation extends CommonHFSExtentKey {
        private final HFSPlusExtentKey key;
        
        public HFSPlusImplementation(HFSPlusExtentKey key) {
            this.key = key;
        }
        
        @Override
        public byte[] getBytes() {
            return key.getBytes();
        }

        public int maxSize() {
            return key.length();
        }

        public int occupiedSize() {
            return key.length();
        }

        public void printFields(PrintStream ps, String prefix) {
            ps.println(prefix + "key:");
            key.print(ps, prefix + " ");
        }
    }
    
    public static class HFSImplementation extends CommonHFSExtentKey {
        private final ExtKeyRec key;
        
        public HFSImplementation(ExtKeyRec key) {
            this.key = key;
        }
        
        @Override
        public byte[] getBytes() {
            return key.getBytes();
        }

        public int maxSize() {
            return key.length();
        }

        public int occupiedSize() {
            return key.length();
        }

        public void printFields(PrintStream ps, String prefix) {
            ps.println(prefix + "key:");
            key.print(ps, prefix + " ");
        }
    }
}
    
