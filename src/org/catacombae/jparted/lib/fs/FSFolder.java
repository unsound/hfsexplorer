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
 * This class represents an entry corresponding to a folder (directory) in the
 * file system presented by a FileSystemHandler. A folder is an entry which
 * holds subentries, like other files, folders or special files. A folder has no
 * associated data, other than the file system metadata such as attributes.
 * 
 * @author <a href="mailto:erik82@kth.se">Erik Larsson</a>
 */
public abstract class FSFolder extends FSEntry {
    protected FSFolder(FileSystemHandler iParentFileSystem) {
        super(iParentFileSystem);
    }
    
    /**
     * Returns the subentries of this folder as an array.
     * 
     * @return the subentries of this folder as an array.
     */
    public abstract FSEntry[] list();
    
    /**
     * Returns the valence of this folder, i.e. how many subentries this folder
     * holds.
     * 
     * @return the valence of this folder.
     */
    public abstract long getValence();
}
