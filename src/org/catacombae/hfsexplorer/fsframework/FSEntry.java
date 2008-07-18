/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.fsframework;

import java.util.Date;
import org.catacombae.hfsexplorer.Util;

/**
 *
 * @author Erik
 */
public class FSEntry {
    private FileSystem parentFileSystem;
    
    protected byte[] fsId;
    protected String name;
    protected FSFolder parent; // Will be null only for the root node
    protected Date modifyDate; // Optional
    protected Date createDate; // Optional
    
    /**
     * Gets the internal ID of this file in the context of the associated
     * file system. This may be an internal node ID that has no relevance
     * except for uniquely identifying an entry in a file system.
     * @return the internal ID of this file.
     */
    public byte[] getFileSystemID() {
        return Util.createCopy(fsId);
    }
    
    public String getName() {
        return name;
    }
    
    public FSFolder getParent() {
        return parent;
    }
    
    public boolean hasModifyDate() {
        return modifyDate != null;
    }
    
    public boolean hasCreateDate() {
        return createDate != null;
    }
    
    protected FileSystem getParentFileSystem() {
        return parentFileSystem;
    }
}
