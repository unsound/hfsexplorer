/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.types.hfscommon;

import java.io.PrintStream;
import org.catacombae.hfsexplorer.types.HFSPlusCatalogKey;
import org.catacombae.hfsexplorer.types.HFSPlusCatalogThread;
import org.catacombae.hfsexplorer.types.hfs.CatKeyRec;
import org.catacombae.hfsexplorer.types.hfs.CdrFThdRec;

/**
 *
 * @author erik
 */
public class CommonHFSCatalogFileThreadRecord extends CommonHFSCatalogLeafRecord {
    private CommonHFSCatalogKey key;
    private CommonHFSCatalogFileThread data;
    
    private CommonHFSCatalogFileThreadRecord(CommonHFSCatalogKey key,
            CommonHFSCatalogFileThread data) {
        this.key = key;
        this.data = data;
    }
    
    @Override
    public CommonHFSCatalogKey getKey() {
        return key;
    }

    public CommonHFSCatalogFileThread getData() {
        return data;
    }
    
    @Override
    public void print(PrintStream ps, String prefix) {
        ps.println(prefix + getClass().getSimpleName() + ":");
        printFields(ps, prefix + " ");
    }

    public void printFields(PrintStream ps, String prefix) {
        ps.println(prefix + "key:");
        key.print(ps, prefix + " ");
        ps.println(prefix + "data:");
        data.print(ps, prefix + " ");
    }

    public static CommonHFSCatalogFileThreadRecord create(HFSPlusCatalogKey key,
            HFSPlusCatalogThread data) {
        return new CommonHFSCatalogFileThreadRecord(CommonHFSCatalogKey.create(key),
                CommonHFSCatalogFileThread.create(data));
    }
    
    public static CommonHFSCatalogFileThreadRecord create(CatKeyRec key, CdrFThdRec data) {
        return new CommonHFSCatalogFileThreadRecord(CommonHFSCatalogKey.create(key),
                CommonHFSCatalogFileThread.create(data));        
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
