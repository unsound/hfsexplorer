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
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusCatalogKey;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusCatalogFile;
import org.catacombae.hfsexplorer.types.hfs.CatKeyRec;
import org.catacombae.hfsexplorer.types.hfs.CdrFilRec;

/**
 *
 * @author erik
 */
public abstract class CommonHFSCatalogFileRecord extends CommonHFSCatalogLeafRecord {
    protected CommonHFSCatalogKey key;
    protected CommonHFSCatalogFile data;
    
    private CommonHFSCatalogFileRecord(CommonHFSCatalogKey key,
            CommonHFSCatalogFile data) {
        this.key = key;
        this.data = data;
    }
    
    @Override
    public CommonHFSCatalogKey getKey() {
        return key;
    }

    public CommonHFSCatalogFile getData() {
        return data;
    }

    @Override
    public void print(PrintStream ps, String prefix) {
        ps.println(prefix + CommonHFSCatalogFileRecord.class.getSimpleName() + ":");
        printFields(ps, prefix + " ");
    }

    public void printFields(PrintStream ps, String prefix) {
        ps.println(prefix + "key:");
        key.print(ps, prefix + " ");
        ps.println(prefix + "data:");
        data.print(ps, prefix + " ");
    }

    @Override
    public Dictionary getStructElements() {
        DictionaryBuilder db =
                new DictionaryBuilder(CommonHFSCatalogFileRecord.class.getSimpleName(),
                "File record");

        db.add("key", key.getStructElements(), "Catalog key");
        db.add("data", data.getStructElements(), "File data");
            
        return db.getResult();
    }

    public static CommonHFSCatalogFileRecord create(HFSPlusCatalogKey key,
            HFSPlusCatalogFile data) {
        return new HFSPlusImplementation(key, data);
    }
    
    public static CommonHFSCatalogFileRecord create(CatKeyRec key, CdrFilRec data) {
        return new HFSImplementation(key, data);
    }
    
    public static class HFSImplementation extends CommonHFSCatalogFileRecord {
        public HFSImplementation(CatKeyRec key, CdrFilRec data) {
            super(CommonHFSCatalogKey.create(key), CommonHFSCatalogFile.create(data));
        }
        
        protected HFSImplementation(CommonHFSCatalogKey key,
            CommonHFSCatalogFile data) {
            super(key, data);
        }
        
        @Override
        public int getSize() {
            return key.occupiedSize() + data.size();
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
    
    public static class HFSPlusImplementation extends HFSImplementation {
        public HFSPlusImplementation(HFSPlusCatalogKey key, HFSPlusCatalogFile data) {
            super(CommonHFSCatalogKey.create(key), CommonHFSCatalogFile.create(data));
        }
    }
}
