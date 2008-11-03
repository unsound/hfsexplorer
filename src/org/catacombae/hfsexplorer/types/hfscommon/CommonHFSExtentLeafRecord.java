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
import org.catacombae.csjc.StructElements;
import org.catacombae.csjc.structelements.Dictionary;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusExtentDescriptor;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusExtentKey;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusExtentRecord;
import org.catacombae.hfsexplorer.types.hfs.ExtDataRec;
import org.catacombae.hfsexplorer.types.hfs.ExtDescriptor;
import org.catacombae.hfsexplorer.types.hfs.ExtKeyRec;

/**
 *
 * @author erik
 */
public abstract class CommonHFSExtentLeafRecord extends CommonBTRecord implements StructElements {

    public static CommonHFSExtentLeafRecord create(ExtKeyRec key, ExtDataRec recordData) {
        return new HFSImplementation(key, recordData);
    }

    public static CommonHFSExtentLeafRecord create(HFSPlusExtentKey key, HFSPlusExtentRecord recordData) {
        return new HFSPlusImplementation(key, recordData);
    }

    public abstract CommonHFSExtentKey getKey();
    
    public abstract CommonHFSExtentDescriptor[] getRecordData();
    
    @Override
    public void print(PrintStream ps, String prefix) {
        ps.println(prefix + getClass().getSimpleName() + ":");
        printFields(ps, prefix + " ");
    }
    
    /*public int compareTo(CommonHFSExtentLeafRecord o) {
        throw new UnsupporrtedOperationException("Not supported yet.");
    }*/
    
    private static class HFSImplementation extends CommonHFSExtentLeafRecord {
        private final ExtKeyRec key;
        private final ExtDataRec recordData;
        
        public HFSImplementation(ExtKeyRec key, ExtDataRec recordData) {
            this.key = key;
            this.recordData = recordData;
        }
        
        @Override
        public CommonHFSExtentKey getKey() {
            return CommonHFSExtentKey.create(key);
        }

        @Override
        public CommonHFSExtentDescriptor[] getRecordData() {
            ExtDescriptor[] extDescs = recordData.getExtDataRecs();
            CommonHFSExtentDescriptor[] res = new CommonHFSExtentDescriptor[extDescs.length];
            
            for(int i = 0; i < res.length; ++i) {
                res[i] = CommonHFSExtentDescriptor.create(extDescs[i]);
            }
            
            return res;
        }

        @Override
        public int getSize() {
            return key.length()+recordData.length();
        }

        @Override
        public byte[] getBytes() {
            byte[] res = new byte[getSize()];
            byte[] tempArray;
            int i = 0;
            
            tempArray = key.getBytes();
            System.arraycopy(tempArray, 0, res, i, tempArray.length); i += tempArray.length;
            tempArray = recordData.getBytes();
            System.arraycopy(tempArray, 0, res, 0, tempArray.length); i += tempArray.length;

            if(i != res.length)
                throw new RuntimeException("Internal error. See stacktrace.");

            return res;
        }

        @Override
        public void printFields(PrintStream ps, String prefix) {
            ps.println(prefix + "key:");
            key.print(ps, prefix + " ");
            ps.println(prefix + "recordData:");
            recordData.print(ps, prefix + " ");
        }

        @Override
        public Dictionary getStructElements() {
            DictionaryBuilder db = new DictionaryBuilder("CommonHFSExtentLeafRecord.HFSImplementation",
                    "HFS extents overflow file leaf record");
            
            db.add("key", key.getStructElements(), "Key");
            db.add("recordData", recordData.getStructElements(), "Record data");
            
            return db.getResult();
        }
    }
    
    private static class HFSPlusImplementation extends CommonHFSExtentLeafRecord {
        private final HFSPlusExtentKey key;
        private final HFSPlusExtentRecord recordData;
        
        public HFSPlusImplementation(HFSPlusExtentKey key, HFSPlusExtentRecord recordData) {
            this.key = key;
            this.recordData = recordData;
        }
        
        @Override
        public CommonHFSExtentKey getKey() {
            return CommonHFSExtentKey.create(key);
        }

        @Override
        public CommonHFSExtentDescriptor[] getRecordData() {
            HFSPlusExtentDescriptor[] extDescs = recordData.getExtentDescriptors();
            CommonHFSExtentDescriptor[] res = new CommonHFSExtentDescriptor[extDescs.length];
            
            for(int i = 0; i < res.length; ++i) {
                res[i] = CommonHFSExtentDescriptor.create(extDescs[i]);
            }
            
            return res;
        }

        @Override
        public int getSize() {
            return key.length()+recordData.length();
        }

        @Override
        public byte[] getBytes() {
            byte[] res = new byte[getSize()];
            byte[] tempArray;
            int i = 0;
            
            tempArray = key.getBytes();
            System.arraycopy(tempArray, 0, res, i, tempArray.length); i += tempArray.length;
            tempArray = recordData.getBytes();
            System.arraycopy(tempArray, 0, res, 0, tempArray.length); i += tempArray.length;

            if(i != res.length)
                throw new RuntimeException("Internal error. See stacktrace.");

            return res;
        }

        @Override
        public void printFields(PrintStream ps, String prefix) {
            ps.println(prefix + "key:");
            key.print(ps, prefix + " ");
            ps.println(prefix + "recordData:");
            recordData.print(ps, prefix + " ");
        }

        @Override
        public Dictionary getStructElements() {
            DictionaryBuilder db = new DictionaryBuilder("CommonHFSExtentLeafRecord.HFSPlusImplementation",
                    "HFS+ extents overflow file leaf record");
            
            db.add("key", key.getStructElements(), "Key");
            db.add("recordData", recordData.getStructElements(), "Record data");
            
            return db.getResult();
        }
    }
}
