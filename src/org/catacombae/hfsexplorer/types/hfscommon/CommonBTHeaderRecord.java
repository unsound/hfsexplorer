/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.types.hfscommon;

import org.catacombae.hfsexplorer.Util;
import org.catacombae.hfsexplorer.types.BTHeaderRec;
import org.catacombae.hfsexplorer.types.hfs.BTHdrRec;

/**
 *
 * @author erik
 */
public abstract class CommonBTHeaderRecord {
    public enum CompareType {
        CASE_FOLDING, BINARY_COMPARE;
    }
    
    public abstract int getTreeDepth();
    public abstract long getRootNodeNumber();
    public abstract long getNumberOfLeafRecords();
    public abstract long getFirstLeafNodeNumber();
    public abstract long getLastLeafNodeNumber();
    public abstract int getNodeSize();
    public abstract int getMaximumKeyLength();
    public abstract long getTotalNodes();
    public abstract long getFreeNodes();
    public abstract CompareType getKeyCompareType();
    
    public abstract byte[] getBytes();
    
    public static CommonBTHeaderRecord create(BTHeaderRec bthr) {
        return new HFSPlusImplementation(bthr);
    }
    
    public static CommonBTHeaderRecord create(BTHdrRec bthr) {
        return new HFSImplementation(bthr);
    }
    
    public static class HFSPlusImplementation extends CommonBTHeaderRecord {
        private BTHeaderRec bthr;
        
        public HFSPlusImplementation(BTHeaderRec bthr) {
            this.bthr = bthr;
        }

        @Override
        public int getTreeDepth() {
            return Util.unsign(bthr.getTreeDepth());
        }

        @Override
        public long getRootNodeNumber() {
            return Util.unsign(bthr.getRootNode());
        }

        @Override
        public long getNumberOfLeafRecords() {
            return Util.unsign(bthr.getLeafRecords());
        }

        @Override
        public long getFirstLeafNodeNumber() {
            return Util.unsign(bthr.getFirstLeafNode());
        }

        @Override
        public long getLastLeafNodeNumber() {
            return Util.unsign(bthr.getLastLeafNode());
        }

        @Override
        public int getNodeSize() {
            return Util.unsign(bthr.getNodeSize());
        }

        @Override
        public int getMaximumKeyLength() {
            return Util.unsign(bthr.getMaxKeyLength());
        }

        @Override
        public long getTotalNodes() {
            return Util.unsign(bthr.getTotalNodes());
        }

        @Override
        public long getFreeNodes() {
            return Util.unsign(bthr.getFreeNodes());
        }

        @Override
        public CompareType getKeyCompareType() {
            if(bthr.getKeyCompareType() == BTHeaderRec.kHFSBinaryCompare)
                return CompareType.BINARY_COMPARE;
            else if(bthr.getKeyCompareType() == BTHeaderRec.kHFSCaseFolding)
                return CompareType.CASE_FOLDING;
            else
                throw new RuntimeException("Unknown key compare type!");
        }

        @Override
        public byte[] getBytes() {
            return bthr.getBytes();
        }
    }
    
    public static class HFSImplementation extends CommonBTHeaderRecord {
        private BTHdrRec bthr;
        
        public HFSImplementation(BTHdrRec bthr) {
            this.bthr = bthr;
        }

        @Override
        public int getTreeDepth() {
            return Util.unsign(bthr.getBthDepth());
        }

        @Override
        public long getRootNodeNumber() {
            return Util.unsign(bthr.getBthRoot());
        }

        @Override
        public long getNumberOfLeafRecords() {
            return Util.unsign(bthr.getBthNRecs());
        }

        @Override
        public long getFirstLeafNodeNumber() {
            return Util.unsign(bthr.getBthFNode());
        }

        @Override
        public long getLastLeafNodeNumber() {
            return Util.unsign(bthr.getBthLNode());
        }

        @Override
        public int getNodeSize() {
            return Util.unsign(bthr.getBthNodeSize());
        }

        @Override
        public int getMaximumKeyLength() {
            return Util.unsign(bthr.getBthKeyLen());
        }

        @Override
        public long getTotalNodes() {
            return Util.unsign(bthr.getBthNNodes());
        }

        @Override
        public long getFreeNodes() {
            return Util.unsign(bthr.getBthFree());
        }

        @Override
        public CompareType getKeyCompareType() {
            // Is this correct? Can not find any info on this...
            return CompareType.BINARY_COMPARE;
        }
        
        @Override
        public byte[] getBytes() {
            return bthr.getBytes();
        }
    }
}
