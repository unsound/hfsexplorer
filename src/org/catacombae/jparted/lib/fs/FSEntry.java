/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.jparted.lib.fs;

import org.catacombae.hfsexplorer.Util;

/**
 * Hierarchical file system model.
 * 
 * @author Erik
 */
public abstract class FSEntry {
    public static enum Type {
        FILE, FOLDER, SYMLINK, CHARACTER_DEVICE, BLOCK_DEVICE, FIFO, SOCKET;
    }
    private FileSystemHandler parentFileSystem;
    
    protected byte[] fsId;
    protected String name;
    
    /**
     * Gets the internal ID of this file in the context of the associated
     * file system. This may be an internal node ID that has no relevance
     * except for uniquely identifying an entry in a file system.
     * @return the internal ID of this file.
     */
    public byte[] getFileSystemID() {
        return Util.createCopy(fsId);
    }
    
    public abstract String getName();
    
    public abstract FSFolder getParent();
    
    public abstract boolean hasModifyDate();
    
    public abstract boolean hasCreateDate();
    
    protected FileSystemHandler getParentFileSystem() {
        return parentFileSystem;
    }
}
