/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.types.hfscommon;

import org.catacombae.hfsexplorer.types.HFSPlusExtentDescriptor;
import org.catacombae.hfsexplorer.types.HFSPlusExtentKey;
import org.catacombae.hfsexplorer.types.HFSPlusExtentRecord;
import org.catacombae.hfsexplorer.types.hfs.ExtDataRec;
import org.catacombae.hfsexplorer.types.hfs.ExtDescriptor;
import org.catacombae.hfsexplorer.types.hfs.ExtKeyRec;

/**
 *
 * @author erik
 */
public abstract class CommonHFSExtentLeafRecord extends CommonBTRecord /*implements Comparable<CommonHFSExtentLeafRecord>*/ {

    public static CommonBTRecord create(ExtKeyRec key, ExtDataRec recordData) {
        return new HFSImplementation(key, recordData);
    }

    public static CommonBTRecord create(HFSPlusExtentKey key, HFSPlusExtentRecord recordData) {
        return new HFSPlusImplementation(key, recordData);
    }

    public abstract CommonHFSExtentKey getKey();
    
    public abstract CommonHFSExtentDescriptor[] getRecordData();
    
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
        
    }
}
