/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.types.hfscommon;

import org.catacombae.hfsexplorer.Util;
import org.catacombae.hfsexplorer.types.hfsplus.HFSCatalogNodeID;

/**
 *
 * @author erik
 */
public abstract class CommonHFSCatalogNodeID {
    public enum ReservedID {
        ROOT_PARENT,
        ROOT_FOLDER,
        EXTENTS_FILE,
        CATALOG_FILE,
        BAD_BLOCKS_FILE,
        ALLOCATION_FILE,
        STARTUP_FILE,
        ATTRIBUTES_FILE,
        REPAIR_CATALOG_FILE,
        BOGUS_EXTENT_FILE,
        FIRST_USER_CATALOG_NODE_ID
    }
    
    public abstract int toInt();
    public abstract long toLong();
    
    /**
     * Returns a CommonHFSCatalogNodeID for a specified reserved ID, if the
     * reserved ID is supported for the implementation (otherwise null is
     * returned). HFS only supports ROOT_PARENT, ROOT_FOLDER, EXTENTS_FILE,
     * CATALOG_FILE and BAD_BLOCKS_FILE while HFS+ supports all ReservedID
     * values.
     * 
     * @param id
     * @return
     */
    public static CommonHFSCatalogNodeID getHFSReservedID(ReservedID id) {
        return HFSImplementation.getReservedIDStatic(id);
    }
    
    /**
     * Returns a CommonHFSCatalogNodeID for a specified reserved ID, if the
     * reserved ID is supported for the implementation (otherwise null is
     * returned). HFS only supports ROOT_PARENT, ROOT_FOLDER, EXTENTS_FILE,
     * CATALOG_FILE and BAD_BLOCKS_FILE while HFS+ supports all ReservedID
     * values.
     * 
     * @param id
     * @return
     */
    public static CommonHFSCatalogNodeID getHFSPlusReservedID(ReservedID id) {
        return HFSImplementation.getReservedIDStatic(id);
    }
    
    /**
     * Returns a CommonHFSCatalogNodeID for a specified reserved ID, if the
     * reserved ID is supported for the implementation (otherwise null is
     * returned). HFS only supports ROOT_PARENT, ROOT_FOLDER, EXTENTS_FILE,
     * CATALOG_FILE and BAD_BLOCKS_FILE while HFS+ supports all ReservedID
     * values.
     * 
     * @param id
     * @return
     */
    public abstract CommonHFSCatalogNodeID getReservedID(ReservedID id);
     
    @Override
    public boolean equals(Object o) {
        if(o instanceof CommonHFSCatalogNodeID)
            return ((CommonHFSCatalogNodeID)o).toLong() == this.toLong();
        else
            return false;
    }
    
    @Override
    public int hashCode() {
        return toInt();
    }

    
    public static CommonHFSCatalogNodeID create(HFSCatalogNodeID fileID) {
        return new HFSPlusImplementation(fileID);
    }
    
    public static CommonHFSCatalogNodeID create(int filFlNum) {
        return new HFSImplementation(filFlNum);
    }
    
    public static class HFSPlusImplementation extends CommonHFSCatalogNodeID {
        private static final HFSPlusImplementation ROOT_PARENT_ID = new HFSPlusImplementation(HFSCatalogNodeID.kHFSRootParentID);
        private static final HFSPlusImplementation ROOT_FOLDER_ID = new HFSPlusImplementation(HFSCatalogNodeID.kHFSRootFolderID);
        private static final HFSPlusImplementation EXTENTS_FILE_ID = new HFSPlusImplementation(HFSCatalogNodeID.kHFSExtentsFileID);
        private static final HFSPlusImplementation CATALOG_FILE_ID = new HFSPlusImplementation(HFSCatalogNodeID.kHFSCatalogFileID);
        private static final HFSPlusImplementation BAD_BLOCKS_FILE_ID = new HFSPlusImplementation(HFSCatalogNodeID.kHFSBadBlockFileID);
        private static final HFSPlusImplementation ALLOCATION_FILE_ID = new HFSPlusImplementation(HFSCatalogNodeID.kHFSAllocationFileID);
        private static final HFSPlusImplementation STARTUP_FILE_ID = new HFSPlusImplementation(HFSCatalogNodeID.kHFSStartupFileID);
        private static final HFSPlusImplementation ATTRIBUTES_FILE_ID = new HFSPlusImplementation(HFSCatalogNodeID.kHFSAttributesFileID);
        private static final HFSPlusImplementation REPAIR_CATALOG_FILE_ID = new HFSPlusImplementation(HFSCatalogNodeID.kHFSRepairCatalogFileID);
        private static final HFSPlusImplementation BOGUS_EXTENT_FILE_ID = new HFSPlusImplementation(HFSCatalogNodeID.kHFSBogusExtentFileID);
        private static final HFSPlusImplementation FIRST_USER_CATALOG_NODE_ID = new HFSPlusImplementation(HFSCatalogNodeID.kHFSFirstUserCatalogNodeID);
        
        private HFSCatalogNodeID fileID;
        
        public HFSPlusImplementation(HFSCatalogNodeID fileID) {
            this.fileID = fileID;
        }

        @Override
        public int toInt() {
            return fileID.toInt();
        }

        @Override
        public long toLong() {
            return fileID.toLong();
        }
        
        public CommonHFSCatalogNodeID getReservedID(ReservedID id) {
            return getReservedIDStatic(id);
        }
        
        public static CommonHFSCatalogNodeID getReservedIDStatic(ReservedID id) {
            switch(id) {
                case ROOT_PARENT:
                    return ROOT_PARENT_ID;
                case ROOT_FOLDER:
                    return ROOT_FOLDER_ID;
                case EXTENTS_FILE:
                    return EXTENTS_FILE_ID;
                case CATALOG_FILE:
                    return CATALOG_FILE_ID;
                case BAD_BLOCKS_FILE:
                    return BAD_BLOCKS_FILE_ID;
                case ALLOCATION_FILE:
                    return ALLOCATION_FILE_ID;
                case STARTUP_FILE:
                    return STARTUP_FILE_ID;
                case ATTRIBUTES_FILE:
                    return ATTRIBUTES_FILE_ID;
                case REPAIR_CATALOG_FILE:
                    return REPAIR_CATALOG_FILE_ID;
                case BOGUS_EXTENT_FILE:
                    return BOGUS_EXTENT_FILE_ID;
                case FIRST_USER_CATALOG_NODE_ID:
                    return FIRST_USER_CATALOG_NODE_ID;
                default:
                    throw new RuntimeException("Unknown reserved id: " + id +
                            "!");
            }
        }
    }
    
    public static class HFSImplementation extends CommonHFSCatalogNodeID {
        private static final HFSImplementation ROOT_PARENT_ID = new HFSImplementation(1);
        private static final HFSImplementation ROOT_FOLDER_ID = new HFSImplementation(2);
        private static final HFSImplementation EXTENTS_FILE_ID = new HFSImplementation(3);
        private static final HFSImplementation CATALOG_FILE_ID = new HFSImplementation(4);
        private static final HFSImplementation BAD_BLOCKS_FILE_ID = new HFSImplementation(5);
        private static final HFSImplementation FIRST_USER_CATALOG_NODE_ID = new HFSImplementation(16);

        private int filFlNum;
        
        public HFSImplementation(int filFlNum) {
            this.filFlNum = filFlNum;
        }

        @Override
        public int toInt() {
            return filFlNum;
        }

        @Override
        public long toLong() {
            return Util.unsign(filFlNum);
        }

        public CommonHFSCatalogNodeID getReservedID(ReservedID id) {
            return getReservedIDStatic(id);
        }
        
        public static CommonHFSCatalogNodeID getReservedIDStatic(ReservedID id) {
            switch(id) {
                case ROOT_PARENT:
                    return ROOT_PARENT_ID;
                case ROOT_FOLDER:
                    return ROOT_FOLDER_ID;
                case EXTENTS_FILE:
                    return EXTENTS_FILE_ID;
                case CATALOG_FILE:
                    return CATALOG_FILE_ID;
                case BAD_BLOCKS_FILE:
                    return BAD_BLOCKS_FILE_ID;
                case ALLOCATION_FILE:
                case STARTUP_FILE:
                case ATTRIBUTES_FILE:
                case REPAIR_CATALOG_FILE:
                case BOGUS_EXTENT_FILE:
                    return null; // Not applicable for HFS.
                case FIRST_USER_CATALOG_NODE_ID:
                    return FIRST_USER_CATALOG_NODE_ID;
                default:
                    throw new RuntimeException("Unknown reserved id: " + id +
                            "!");
            }
        }
    }
}
