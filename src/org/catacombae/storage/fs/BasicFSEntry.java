/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.storage.fs;

/**
 *
 * @author erik
 */
public abstract class BasicFSEntry implements FSEntry {

    /**
     * Implementations of this abstract class must always retain a reference to
     * the parent file system of the entry.
     */
    protected final FileSystemHandler parentFileSystem;

    /**
     * Implementations of this abstract class must always retain a reference to
     * the parent file system of the entry.
     */
    protected BasicFSEntry(FileSystemHandler iParentFileSystem) {
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
