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
 * This class represents a link in a file system, i.e. a point of redirection to another part of
 * the file system. In Unix, this is equivalent to a "symbolic link". Unix hard links should be
 * modeled as FSFile or FSFolder types, as they're not strictly links, but just two equal file
 * system entries that point to the same location (there is no "source file" for the link, but
 * instead an inode).
 *
 * @author Erik Larsson
 */
public abstract class FSLink extends FSEntry {

    protected FSLink(FileSystemHandler parentFileSystem) {
        super(parentFileSystem);
    }
    
    /**
     * Tries to resolve the target of the link in the context of its file system. If the link is
     * invalid, this method will return null.
     *
     * @param parentDir a path to the parent directory of the link (needed for links that use
     * relative pathnames).
     * @return the target of this link, if possible, or <code>null</code> if the link was invalid.
     */
    public abstract FSEntry getLinkTarget(String[] parentDir);
    
    /**
     * Returns a context specific string which shows the intended target for this link in the syntax
     * of its file system. This string is only for display purposes in info boxes or debug messages.
     * It doesn't have any specified format.
     *
     * @return a context specific string which shows the intended target for this link.
     */
    public abstract String getLinkTargetString();
}
