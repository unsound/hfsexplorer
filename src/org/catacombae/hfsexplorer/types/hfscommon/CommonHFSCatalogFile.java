/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.types.hfscommon;

import org.catacombae.csjc.StaticStruct;
import org.catacombae.hfsexplorer.types.HFSPlusCatalogFile;
import org.catacombae.hfsexplorer.types.hfs.CdrFilRec;

/**
 *
 * @author erik
 */
public abstract class CommonHFSCatalogFile implements StaticStruct {
    
    public abstract CommonHFSCatalogNodeID getFileID();
    public abstract CommonHFSForkData getDataFork();
    public abstract CommonHFSForkData getResourceFork();
    public abstract byte[] getBytes();
    
    public static CommonHFSCatalogFile create(HFSPlusCatalogFile data) {
        return new HFSPlusImplementation(data);
    }

    public static CommonHFSCatalogFile create(CdrFilRec data) {
        return new HFSImplementation(data);
    }

    private static class HFSPlusImplementation extends CommonHFSCatalogFile {
        private HFSPlusCatalogFile data;
        
        public HFSPlusImplementation(HFSPlusCatalogFile data) {
            this.data = data;
        }

        @Override
        public CommonHFSCatalogNodeID getFileID() {
            return CommonHFSCatalogNodeID.create(data.getFileID());
        }

        @Override
        public CommonHFSForkData getDataFork() {
            return CommonHFSForkData.create(data.getDataFork());
        }

        @Override
        public CommonHFSForkData getResourceFork() {
            return CommonHFSForkData.create(data.getResourceFork());
        }

        @Override
        public int size() {
            return data.length();
        }

        @Override
        public byte[] getBytes() {
            return data.getBytes();
        }
    }
    
    private static class HFSImplementation extends CommonHFSCatalogFile {
        private CdrFilRec data;
        
        public HFSImplementation(CdrFilRec data) {
            this.data = data;
        }

        @Override
        public CommonHFSCatalogNodeID getFileID() {
            return CommonHFSCatalogNodeID.create(data.getFilFlNum());
        }

        @Override
        public CommonHFSForkData getDataFork() {
            return CommonHFSForkData.create(data.getFilExtRec(), data.getFilLgLen());
        }

        @Override
        public CommonHFSForkData getResourceFork() {
            return CommonHFSForkData.create(data.getFilRExtRec(), data.getFilRLgLen());
        }

        @Override
        public int size() {
            return data.length();
        }

        @Override
        public byte[] getBytes() {
            return data.getBytes();
        }
    }
}
