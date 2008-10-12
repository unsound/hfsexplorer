/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.types.hfscommon;

import java.io.PrintStream;
import org.catacombae.csjc.StructElements;
import org.catacombae.csjc.structelements.Dictionary;
import org.catacombae.hfsexplorer.FastUnicodeCompare;
import org.catacombae.hfsexplorer.Util;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusCatalogKey;
import org.catacombae.hfsexplorer.types.hfsx.HFSXKeyCompareType;
import org.catacombae.hfsexplorer.types.hfs.CatKeyRec;

/**
 *
 * @author erik
 */
public abstract class CommonHFSCatalogKey extends CommonBTKey implements StructElements {
    public abstract CommonHFSCatalogNodeID getParentID();
    public abstract CommonHFSCatalogString getNodeName();
    
    @Override
    public void print(PrintStream ps, String prefix) {
        ps.println(prefix + CommonHFSCatalogKey.class.getSimpleName() + ":");
        printFields(ps, prefix + " ");
    }

    public static CommonHFSCatalogKey create(HFSPlusCatalogKey key) {
        return new HFSPlusImplementation(key);
    }
    
    public static CommonHFSCatalogKey create(HFSPlusCatalogKey key, HFSXKeyCompareType compType) {
        return new HFSXImplementation(key, compType);
    }
    
    public static CommonHFSCatalogKey create(CatKeyRec key) {
        return new HFSImplementation(key);
    }
    
    public static class HFSPlusImplementation extends CommonHFSCatalogKey {
        private final HFSPlusCatalogKey key;
        private HFSXKeyCompareType compType;
        
        public HFSPlusImplementation(HFSPlusCatalogKey key) {
            this(key, HFSXKeyCompareType.CASE_FOLDING);
        }
        protected HFSPlusImplementation(HFSPlusCatalogKey key,
                HFSXKeyCompareType compType) {
            this.key = key;
            this.compType = compType;
        }

        @Override
        public CommonHFSCatalogNodeID getParentID() {
            return CommonHFSCatalogNodeID.create(key.getParentID());
        }

        @Override
        public CommonHFSCatalogString getNodeName() {
            return CommonHFSCatalogString.createHFSPlus(key.getNodeName());
        }

        @Override
        public byte[] getBytes() {
            return key.getBytes();
        }
        
        @Override
        public int compareTo(CommonBTKey o) {
            if(o instanceof HFSPlusImplementation) {
                HFSPlusImplementation k = (HFSPlusImplementation) o;
                long res = getParentID().toLong() - k.getParentID().toLong();
                if(res == 0) {
                    switch(compType) {
                        case CASE_FOLDING:
                            return FastUnicodeCompare.compare(key.getNodeName().getUnicode(),
                                    k.key.getNodeName().getUnicode());
                        case BINARY_COMPARE:
                            return Util.unsignedArrayCompareLex(key.getNodeName().getUnicode(),
                                    k.key.getNodeName().getUnicode());
                        default:
                            throw new RuntimeException("Invalid value in file system structure! " +
                                    "Compare type = " + compType);
                    }
                }
                else if(res > 0)
                    return 1;
                else
                    return -1;
            }
            else {
                throw new RuntimeException("Can't compare a " + o.getClass() +
                        " with a " + this.getClass());
            }
        }

        @Override
        public int maxSize() {
            return key.maxSize();
        }

        @Override
        public int occupiedSize() {
            return key.occupiedSize();
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
    }
    
    public static class HFSXImplementation extends HFSPlusImplementation {
        public HFSXImplementation(HFSPlusCatalogKey key,
                HFSXKeyCompareType compType) {
            super(key, compType);
        }
    }
    
    public static class HFSImplementation extends CommonHFSCatalogKey {
        private final CatKeyRec key;
        
        public HFSImplementation(CatKeyRec key) {
            this.key = key;
        }

        @Override
        public CommonHFSCatalogNodeID getParentID() {
            return CommonHFSCatalogNodeID.create(key.getCkrParID());
        }

        @Override
        public CommonHFSCatalogString getNodeName() {
            return CommonHFSCatalogString.createHFS(key.getCkrCName());
        }

        @Override
        public int maxSize() {
            return key.maxSize();
        }

        @Override
        public int occupiedSize() {
            return key.occupiedSize();
        }

        @Override
        public byte[] getBytes() {
            return key.getBytes();
        }
        
        @Override
        public int compareTo(CommonBTKey o) {
            if(o instanceof HFSImplementation) {
                HFSImplementation k = (HFSImplementation) o;
                long res = getParentID().toLong() - k.getParentID().toLong();
                if(res == 0) {
                    return Util.arrayCompareLex(key.getCkrCName(), k.key.getCkrCName());
                }
                else if(res > 0)
                    return 1;
                else
                    return -1;
            }
            else {
                throw new RuntimeException("Can't compare a " + o.getClass() +
                        " with a " + this.getClass());
            }
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
    }
}
