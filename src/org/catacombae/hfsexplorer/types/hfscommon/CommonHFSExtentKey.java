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
import org.catacombae.hfsexplorer.Util;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusExtentKey;
import org.catacombae.hfsexplorer.types.hfs.ExtKeyRec;

/**
 *
 * @author erik
 */
public abstract class CommonHFSExtentKey extends CommonBTKey<CommonHFSExtentKey> implements StructElements {

    @Override
    public void print(PrintStream ps, String prefix) {
        ps.println(prefix + getClass().getSimpleName() + ":");
        printFields(ps, prefix + " ");
    }
    
    public static CommonHFSExtentKey create(HFSPlusExtentKey key) {
        return new HFSPlusImplementation(key);
    }

    public static CommonHFSExtentKey create(ExtKeyRec key) {
        return new HFSImplementation(key);
    }
    
    private static int commonCompare(CommonHFSExtentKey k1, CommonHFSExtentKey k2) {
        int forkType1 = k1.getForkType();
        int forkType2 = k2.getForkType();
        if(forkType1 == forkType2) {
            long fileID1 = k1.getFileID().toLong();
            long fileID2 = k2.getFileID().toLong();
            if(fileID1 == fileID2) {
                long startBlock1 = k1.getStartBlock();
                long startBlock2 = k2.getStartBlock();
                if(startBlock1 == startBlock2)
                    return 0;
                else if(startBlock1 < startBlock2)
                    return -1;
                else
                    return 1;
            }
            else if(fileID1 < fileID2)
                return -1;
            else
                return 1;
        }
        else if(forkType1 < forkType2)
            return -1;
        else
            return 1;
    }
    
    public abstract int getForkType();

    public abstract CommonHFSCatalogNodeID getFileID();

    public abstract long getStartBlock();
    
    public static class HFSPlusImplementation extends CommonHFSExtentKey {
        private final HFSPlusExtentKey key;
        
        public HFSPlusImplementation(HFSPlusExtentKey key) {
            this.key = key;
        }
        
        @Override
        public int getForkType() {
            return key.getUnsignedForkType();
        }
        
        @Override
        public CommonHFSCatalogNodeID getFileID() {
            return CommonHFSCatalogNodeID.create(key.getFileID());
        }
        
        @Override
        public long getStartBlock() {
            return key.getUnsignedStartBlock();
        }
        
        @Override
        public byte[] getBytes() {
            return key.getBytes();
        }

        @Override
        public int maxSize() {
            return key.length();
        }

        @Override
        public int occupiedSize() {
            return key.length();
        }

        @Override
        public void printFields(PrintStream ps, String prefix) {
            ps.println(prefix + "key:");
            key.print(ps, prefix + " ");
        }

        @Override
        public Dictionary getStructElements() {
            return key.getStructElements();
        }

        @Override
        public int compareTo(CommonHFSExtentKey o) {
            if(o instanceof HFSPlusImplementation) {
                return commonCompare(this, o);
            }
            else {
                if(o != null)
                    throw new RuntimeException("Can't compare a " + o.getClass() +
                            " with a " + this.getClass());
                else
                    throw new RuntimeException("o == null !!");
            }
        }
    }
    
    public static class HFSImplementation extends CommonHFSExtentKey {
        private final ExtKeyRec key;
        
        public HFSImplementation(ExtKeyRec key) {
            this.key = key;
        }
        
        @Override
        public int getForkType() {
            return Util.unsign(key.getXkrFkType());
        }

        @Override
        public CommonHFSCatalogNodeID getFileID() {
            return CommonHFSCatalogNodeID.create(key.getXkrFNum());
        }

        @Override
        public long getStartBlock() {
            return Util.unsign(key.getXkrFABN());
        }
        
        @Override
        public byte[] getBytes() {
            return key.getBytes();
        }

        @Override
        public int maxSize() {
            return key.length();
        }

        @Override
        public int occupiedSize() {
            return key.length();
        }

        @Override
        public void printFields(PrintStream ps, String prefix) {
            ps.println(prefix + "key:");
            key.print(ps, prefix + " ");
        }

        @Override
        public Dictionary getStructElements() {
            return key.getStructElements();
        }

        @Override
        public int compareTo(CommonHFSExtentKey o) {
            if(o instanceof HFSImplementation) {
                return commonCompare(this, o);
            }
            else {
                if(o != null)
                    throw new RuntimeException("Can't compare a " + o.getClass() +
                            " with a " + this.getClass());
                else
                    throw new RuntimeException("o == null !!");
            }
        }
    }
}
    
