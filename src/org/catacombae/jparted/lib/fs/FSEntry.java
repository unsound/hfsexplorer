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

package org.catacombae.jparted.lib.fs;

/**
 * A file system entry in our hierarchical file system model. This corresponds
 * to one of the nodes in a file system that denote a file, folder, device,
 * socket, etc.
 * 
 * @author <a href="mailto:erik82@kth.se">Erik Larsson</a>
 */
public abstract class FSEntry {
    public static enum Type {
        FILE, FOLDER, SYMLINK, CHARACTER_DEVICE, BLOCK_DEVICE, FIFO, SOCKET;
    }
    
    /**
     * Implementations of this abstract class must always retain a reference to
     * the parent file system of the entry.
     */
    protected final FileSystemHandler parentFileSystem;
    
    /**
     * Implementations of this abstract class must always retain a reference to
     * the parent file system of the entry.
     */
    protected FSEntry(FileSystemHandler iParentFileSystem) {
        this.parentFileSystem = iParentFileSystem;
    }
    
    /**
     * Gets the internal ID of this file in the context of the associated
     * file system. This may be an internal node ID that has no relevance
     * except for uniquely identifying an entry in a file system.
     * @return the internal ID of this file.
     */
    /*public byte[] getFileSystemID() {
        return Util.createCopy(fsId);
    }*/
    
    /**
     * Returns the attributes of this file system entry. Which attributes are
     * available for a specific file system varies enourmously.
     * 
     * @return the attributes of this file system entry.
     */
    public abstract FSAttributes getAttributes();
    
    public abstract String getName();
    
    //public abstract FSFolder getParent();
    
    protected FileSystemHandler getParentFileSystem() {
        return parentFileSystem;
    }
    
    /*
     * The methods below are for convenience purposes. Code involving a lot of
     * instanceof statements tend to get a bit ugly...
     */
    
    /**
     * Returns whether or not this FSEntry denotes a file. If this method
     * returns true, you can safely call the <code>asFile()</code> method in
     * order to get the full file object.
     * 
     * @return whether or not this FSEntry denotes a file.
     */
    public boolean isFile() {
        return this instanceof FSFile;
    }
    
    /**
     * Returns whether or not this FSEntry denotes a folder (directory). If this
     * method returns true, you can safely call the <code>asFolder()</code>
     * method in order to get the full folder object.
     * 
     * @return whether or not this FSEntry denotes a folder (directory).
     */
    public boolean isFolder() {
        return this instanceof FSFolder;
    }
    
    /**
     * Returns this object as an FSFile object, if it can be casted, and throws
     * a <code>RuntimeException</code> otherwise. You should call the isFile()
     * method first to make sure the cast is valid.
     * 
     * @return this object as an FSFile object, if possible.
     */
    public FSFile asFile() {
        if(this instanceof FSFile)
            return (FSFile) this;
        else
            throw new RuntimeException("Not a file!");
    }

    /**
     * Returns this object as an FSFolder object, if it can be casted, and
     * throws a <code>RuntimeException</code> otherwise. You should call the
     * isFolder() method first to make sure the cast is valid.
     * 
     * @return this object as an FSFolder object, if possible.
     */
    public FSFolder asFolder() {
        if(this instanceof FSFolder)
            return (FSFolder) this;
        else
            throw new RuntimeException("Not a folder!");
    }

    /**
     * Returns the absolute path to this entry in the context of its file system.
     * @return the absolute path to this entry in the context of its file system.
     */
    /*
    public String[] getAbsolutePath() {
        LinkedList<String> pathBuilder = new LinkedList<String>();
        getCanonicalPathInternal(pathBuilder);
        return pathBuilder.toArray(new String[pathBuilder.size()]);
    }

    void getCanonicalPathInternal(LinkedList<String> components) {
        FSFolder parentFolder = getParent();
        if(parentFolder != null)
            parentFolder.getCanonicalPathInternal(components);
        
        components.addLast(getName());
    }
     * */

    /*
    public String getAbsolutePosixPath() {
        String[] fsPath = getAbsolutePath();
        StringBuilder sb = new StringBuilder();

        for(String s : fsPath) {
            sb.append("/");
            sb.append(parentFileSystem.generatePosixPathnameComponent(s));
        }

        return sb.toString();
    }
     * */
}
