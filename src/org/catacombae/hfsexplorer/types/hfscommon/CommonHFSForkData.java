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

import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusExtentDescriptor;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusForkData;
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
