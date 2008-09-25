/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.types.hfscommon;

import org.catacombae.hfsexplorer.types.HFSPlusCatalogThread;
import org.catacombae.hfsexplorer.types.hfs.CdrFThdRec;

/**
 *
 * @author erik
 */
public abstract class CommonHFSCatalogFileThread {
    public abstract CommonHFSCatalogNodeID getParentID();
    public abstract CommonHFSCatalogString getNodeName();
    public abstract int length();
    public abstract byte[] getBytes();


    public static CommonHFSCatalogFileThread create(HFSPlusCatalogThread data) {
        return new HFSPlusImplementation(data);
    }

    public static CommonHFSCatalogFileThread create(CdrFThdRec data) {
        return new HFSImplementation(data);
    }
    
    private static class HFSPlusImplementation extends CommonHFSCatalogFileThread {
        private final HFSPlusCatalogThread data;
        
        public HFSPlusImplementation(HFSPlusCatalogThread data) {
            this.data = data;
        }

        public int length() {
            return data.length();
        }

        @Override
        public CommonHFSCatalogNodeID getParentID() {
            return CommonHFSCatalogNodeID.create(data.getParentID());
        }

        @Override
        public CommonHFSCatalogString getNodeName() {
            return CommonHFSCatalogString.create(data.getNodeName());
        }

        @Override
        public byte[] getBytes() {
            return data.getBytes();
        }
    }
    
    private static class HFSImplementation extends CommonHFSCatalogFileThread {
        private final CdrFThdRec data;
        
        public HFSImplementation(CdrFThdRec data) {
            this.data = data;
        }
        
        public int length() {
            return data.length();
        }

        @Override
        public CommonHFSCatalogNodeID getParentID() {
            return CommonHFSCatalogNodeID.create(data.getFthdParID());
        }

        @Override
        public CommonHFSCatalogString getNodeName() {
            return CommonHFSCatalogString.create(data.getFthdCName());
        }

        @Override
        public byte[] getBytes() {
            return data.getBytes();
        }
    }
}
