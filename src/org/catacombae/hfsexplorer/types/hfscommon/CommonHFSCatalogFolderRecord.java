/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.types.hfscommon;

import java.io.PrintStream;
import org.catacombae.hfsexplorer.types.HFSPlusCatalogFolder;
import org.catacombae.hfsexplorer.types.HFSPlusCatalogKey;
import org.catacombae.hfsexplorer.types.hfs.CatKeyRec;
import org.catacombae.hfsexplorer.types.hfs.CdrDirRec;

/**
 *
 * @author erik
 */
public abstract class CommonHFSCatalogFolderRecord extends CommonHFSCatalogLeafRecord {
    
    
    public abstract CommonHFSCatalogFolder getData();
    
    @Override
    public void print(PrintStream ps, String prefix) {
        ps.println(prefix + getClass().getSimpleName() + ":");
        printFields(ps, prefix + " ");
    }

    public void printFields(PrintStream ps, String prefix) {
        ps.println(prefix + "key:");
        getKey().print(ps, prefix + " ");
        ps.println(prefix + "data:");
        getData().print(ps, prefix + " ");
    }

    public static CommonHFSCatalogFolderRecord create(HFSPlusCatalogKey key,
            HFSPlusCatalogFolder data) {
        return new HFSPlusImplementation(key, data);
    }
    
    public static CommonHFSCatalogFolderRecord create(CatKeyRec key, CdrDirRec data) {
        return new HFSImplementation(key, data);
    }

    public static class HFSPlusImplementation extends CommonHFSCatalogFolderRecord {
        private final HFSPlusCatalogKey key;
        private final HFSPlusCatalogFolder data;
        
        public HFSPlusImplementation(HFSPlusCatalogKey key, HFSPlusCatalogFolder data) {
            this.key = key;
            this.data = data;
        }
        
        @Override
        public CommonHFSCatalogFolder getData() {
            return CommonHFSCatalogFolder.create(data);
        }

        @Override
        public CommonHFSCatalogKey getKey() {
            return CommonHFSCatalogKey.create(key);
        }

        @Override
        public int getSize() {
            return key.occupiedSize() + data.length();
        }
        
        @Override
        public byte[] getBytes() {
            byte[] result = new byte[getSize()];
            byte[] tempData;
            int offset = 0;

            tempData = key.getBytes();
            System.arraycopy(tempData, 0, result, offset, tempData.length); offset += tempData.length;
            tempData = data.getBytes();
            System.arraycopy(tempData, 0, result, offset, tempData.length); offset += tempData.length;
            return result;
        }
    }
    
    public static class HFSImplementation extends CommonHFSCatalogFolderRecord {
        private final CatKeyRec key;
        private final CdrDirRec data;
        
        public HFSImplementation(CatKeyRec key, CdrDirRec data) {
            this.key = key;
            this.data = data;
        }

        @Override
        public CommonHFSCatalogFolder getData() {
            return CommonHFSCatalogFolder.create(data);
        }

        @Override
        public CommonHFSCatalogKey getKey() {
            return CommonHFSCatalogKey.create(key);
        }

        @Override
        public int getSize() {
            return key.occupiedSize() + data.length();
        }

        @Override
        public byte[] getBytes() {
            byte[] result = new byte[getSize()];
            byte[] tempData;
            int offset = 0;

            tempData = key.getBytes();
            System.arraycopy(tempData, 0, result, offset, tempData.length); offset += tempData.length;
            tempData = data.getBytes();
            System.arraycopy(tempData, 0, result, offset, tempData.length); offset += tempData.length;
            return result;
        }
    }
}
