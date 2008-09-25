/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.types.hfscommon;

import org.catacombae.hfsexplorer.types.HFSPlusCatalogFolder;
import org.catacombae.hfsexplorer.types.hfs.CdrDirRec;

/**
 *
 * @author erik
 */
public abstract class CommonHFSCatalogFolder {
    public abstract CommonHFSCatalogNodeID getFolderID();
    
    public static CommonHFSCatalogFolder create(HFSPlusCatalogFolder data) {
        return new HFSPlusImplementation(data);
    }

    public static CommonHFSCatalogFolder create(CdrDirRec data) {
        return new HFSImplementation(data);
    }
    
    public abstract int length();

    public abstract byte[] getBytes();
    
    private static class HFSPlusImplementation extends CommonHFSCatalogFolder {
        private HFSPlusCatalogFolder data;
        
        public HFSPlusImplementation(HFSPlusCatalogFolder data) {
            this.data = data;
        }
        
        @Override
        public CommonHFSCatalogNodeID getFolderID() {
            return CommonHFSCatalogNodeID.create(data.getFolderID());
        }

        @Override
        public int length() {
            return data.length();
        }

        @Override
        public byte[] getBytes() {
            return data.getBytes();
        }
    }
    
    private static class HFSImplementation extends CommonHFSCatalogFolder {
        private CdrDirRec data;
        
        public HFSImplementation(CdrDirRec data) {
            this.data = data;
        }

        @Override
        public CommonHFSCatalogNodeID getFolderID() {
            return CommonHFSCatalogNodeID.create(data.getDirDirID());
        }

        @Override
        public int length() {
            return data.length();
        }

        @Override
        public byte[] getBytes() {
            return data.getBytes();
        }
    }
}
