/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.types.hfscommon;

import org.catacombae.hfsexplorer.types.HFSPlusExtentDescriptor;
import org.catacombae.hfsexplorer.types.HFSPlusForkData;
import org.catacombae.hfsexplorer.types.hfs.ExtDataRec;
import org.catacombae.hfsexplorer.types.hfs.ExtDescriptor;

/**
 *
 * @author erik
 */
public abstract class CommonHFSForkData {
    public abstract long getLogicalSize();
    
    public abstract CommonHFSExtentDescriptor[] getBasicExtents();
    
    public static CommonHFSForkData create(ExtDataRec edr, long logicalSize) {
        return new HFSImplementation(edr, logicalSize);
    }
    
    public static CommonHFSForkData create(HFSPlusForkData hper) {
        return new HFSPlusImplementation(hper);
    }
    
    public static class HFSImplementation extends CommonHFSForkData {
        private final ExtDataRec edr;
        private final long logicalSize;
        
        public HFSImplementation(ExtDataRec edr, long logicalSize) {
            this.edr = edr;
            this.logicalSize = logicalSize;
        }
        
        public long getLogicalSize() {
            return logicalSize;
        }
        
        public CommonHFSExtentDescriptor[] getBasicExtents() {
            ExtDescriptor[] src = edr.getExtDataRecs();
            CommonHFSExtentDescriptor[] result = new CommonHFSExtentDescriptor[src.length];
            for(int i = 0; i < result.length; ++i) {
                result[i] = CommonHFSExtentDescriptor.create(src[i]);
            }
            return result;
        }
    }
    
    public static class HFSPlusImplementation extends CommonHFSForkData {
        private final HFSPlusForkData hper;
        
        public HFSPlusImplementation(HFSPlusForkData hper) {
            this.hper = hper;
        }
        
        public long getLogicalSize() {
            return hper.getLogicalSize();
        }
        
        public CommonHFSExtentDescriptor[] getBasicExtents() {
            HFSPlusExtentDescriptor[] src = hper.getExtents().getExtentDescriptors();
            CommonHFSExtentDescriptor[] result = new CommonHFSExtentDescriptor[src.length];
            for(int i = 0; i < result.length; ++i) {
                result[i] = CommonHFSExtentDescriptor.create(src[i]);
            }
            return result;
        }
    }
}
