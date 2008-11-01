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

import java.io.PrintStream;
import org.catacombae.csjc.structelements.Dictionary;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusCatalogFolder;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusCatalogKey;
import org.catacombae.hfsexplorer.types.hfs.CatKeyRec;
import org.catacombae.hfsexplorer.types.hfs.CdrDirRec;

/**
 *
 * @author erik
 */
public abstract class CommonHFSCatalogFolderRecord extends CommonHFSCatalogLeafRecord {
    protected final CommonHFSCatalogKey key;
    protected final CommonHFSCatalogFolder data;

    protected CommonHFSCatalogFolderRecord(CommonHFSCatalogKey key, CommonHFSCatalogFolder data) {
        this.key = key;
        this.data = data;
    }

    public CommonHFSCatalogKey getKey() {
        return key;
    }

    public CommonHFSCatalogFolder getData() {
        return data;
    }
    
    @Override
    public void print(PrintStream ps, String prefix) {
        ps.println(prefix + CommonHFSCatalogFolderRecord.class.getSimpleName() + ":");
        printFields(ps, prefix + " ");
    }

    public void printFields(PrintStream ps, String prefix) {
        ps.println(prefix + "key:");
        getKey().print(ps, prefix + " ");
        ps.println(prefix + "data:");
        getData().print(ps, prefix + " ");
    }

    public Dictionary getStructElements() {
        DictionaryBuilder db =
                new DictionaryBuilder(CommonHFSCatalogFolderRecord.class.getSimpleName(),
                "Folder record");

        db.add("key", key.getStructElements(), "Catalog key");
        db.add("data", data.getStructElements(), "Folder data");

        return db.getResult();
    }

    @Override
    public byte[] getBytes() {
        byte[] result = new byte[getSize()];
        byte[] tempData;
        int offset = 0;

        tempData = key.getBytes();
        System.arraycopy(tempData, 0, result, offset, tempData.length);
        offset += tempData.length;
        tempData = data.getBytes();
        System.arraycopy(tempData, 0, result, offset, tempData.length);
        offset += tempData.length;
        return result;
    }


    public static CommonHFSCatalogFolderRecord create(HFSPlusCatalogKey key,
            HFSPlusCatalogFolder data) {
        return new HFSPlusImplementation(key, data);
    }
    
    public static CommonHFSCatalogFolderRecord create(CatKeyRec key, CdrDirRec data) {
        return new HFSImplementation(key, data);
    }

    public static class HFSPlusImplementation extends CommonHFSCatalogFolderRecord {
        
        public HFSPlusImplementation(HFSPlusCatalogKey key, HFSPlusCatalogFolder data) {
            super(CommonHFSCatalogKey.create(key), CommonHFSCatalogFolder.create(data));
        }
        
        @Override
        public int getSize() {
            return key.occupiedSize() + data.length();
        }
    }
    
    public static class HFSImplementation extends CommonHFSCatalogFolderRecord {
        public HFSImplementation(CatKeyRec key, CdrDirRec data) {
            super(CommonHFSCatalogKey.create(key), CommonHFSCatalogFolder.create(data));
        }
        
        @Override
        public int getSize() {
            return key.occupiedSize() + data.length();
        }
    }
}
