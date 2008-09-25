/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.types.hfscommon;

import org.catacombae.hfsexplorer.types.HFSPlusCatalogKey;
import org.catacombae.hfsexplorer.types.HFSPlusCatalogThread;
import org.catacombae.hfsexplorer.types.hfs.CatKeyRec;
import org.catacombae.hfsexplorer.types.hfs.CdrThdRec;

/**
 *
 * @author erik
 */
public class CommonHFSCatalogFolderThreadRecord extends CommonHFSCatalogLeafRecord {

    private CommonHFSCatalogKey key;
    private CommonHFSCatalogFolderThread data;
    
    private CommonHFSCatalogFolderThreadRecord(CommonHFSCatalogKey key,
            CommonHFSCatalogFolderThread data) {
        this.key = key;
        this.data = data;
    }
    
    @Override
    public CommonHFSCatalogKey getKey() {
        return key;
    }

    public CommonHFSCatalogFolderThread getData() {
        return data;
    }
    
    public static CommonHFSCatalogFolderThreadRecord create(HFSPlusCatalogKey key,
            HFSPlusCatalogThread data) {
        return new CommonHFSCatalogFolderThreadRecord(CommonHFSCatalogKey.create(key),
                CommonHFSCatalogFolderThread.create(data));
    }
    
    public static CommonHFSCatalogFolderThreadRecord create(CatKeyRec key, CdrThdRec data) {
        return new CommonHFSCatalogFolderThreadRecord(CommonHFSCatalogKey.create(key),
                CommonHFSCatalogFolderThread.create(data));        
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

        tempData = getKey().getBytes();
        System.arraycopy(tempData, 0, result, offset, tempData.length);
        offset += tempData.length;
        tempData = data.getBytes();
        System.arraycopy(tempData, 0, result, offset, tempData.length);
        offset += tempData.length;

        return result;
    }
}
