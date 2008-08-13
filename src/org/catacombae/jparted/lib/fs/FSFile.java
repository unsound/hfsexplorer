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
 * This class represents an entry corresponding to a file in the file system
 * presented by a FileSystemHandler. A file is an entry that does not have
 * subentries, like a folder, but that has associated data. A file can have one
 * or more "forks" of data, where one of the forks always must be considered the
 * "main" fork or the "data" fork. Some file systems do not support forks, in
 * which case the only fork available is the "main" fork.
 * 
 * @author <a href="mailto:erik82@kth.se">Erik Larsson</a>
 */
public abstract class FSFile extends FSEntry {
    protected FSFile(FileSystemHandler iParentFileSystem) {
        super(iParentFileSystem);
    }
    
    /**
     * Returns the main (data) fork of this file, containing the data that users
     * normally will see. All implementors must make sure that this FSFork
     * always exists.
     * 
     * @return the main (data) fork of this file.
     */
    public abstract FSFork getMainFork();
    
    /**
     * Returns all available forks for this file.
     * 
     * @return all available forks for this file.
     */
    public abstract FSFork[] getAllForks();
    
    /**
     * Returns the fork corresponding to a predefined fork type, if supported,
     * or <code>null</code> if no fork corresponding to the fork type can be
     * found or the file system does not support the specified fork type.<br>
     * To check which fork types the file system handler generally supports,
     * use FileSystemHandler.getSupportedForkTypes().
     * 
     * @param type the FSForkType corresponding to the requested fork.
     * @return the requested fork, if existent, or <code>null</code> otherwise.
     */
    public abstract FSFork getForkByType(FSForkType type);
    
    /**
     * Returns the length of the data of all of the file's forks put together.
     * For instance if a file has a data fork of 12 bytes and a resource fork of
     * 8 bytes, this method will return the value 20.
     * 
     * @return the length of the data of all of the file's forks put together.
     */
    public abstract long getCombinedLength();
}
