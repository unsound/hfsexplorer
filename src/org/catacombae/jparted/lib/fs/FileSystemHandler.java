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
 *
 * @author erik
 */
public abstract class FileSystemHandler {
    
    /**
     * Lists all entries present under the <code>path</code> supplied. Path must
     * point to a folder, and is composed of a variable arguments list with each
     * pathname component as a separate String.<br>
     * Invoking this method with no arguments gives the file list of the
     * root directory of the file system.<br>
     * Examples:
     * <ul>
     * <li>
     * Getting the contents of <code>/usr/bin</code> in a UNIX-style filesystem:
     * <code>listFiles("usr", "bin");</code></li>
     * <li>
     * Getting the contents of <code>\Windows\System32</code> in a Windows-style
     * file system: <code>listFiles("Windows", "System32");</code>
     * </li>
     * <li>
     * Getting the contents of <code>Users:joe</code> in an old Macintosh-style
     * file system: <code>listFiles("Users", "joe");</code>
     * </li>
     * </ul>
     * 
     * @param path the path to the requested folder with each path component
     * as a separate string. The first component under the root dir will be
     * leftmost in the argument list.
     * @return an array with file system entries that represents the contents of
     * the requested folder, or <code>null</code> if the folder can't be found.
     */
    public abstract FSEntry[] list(String... path);
    
    /**
     * Returns the root folder of the file system hierarchy.
     * 
     * @return the root folder of the file system hierarchy.
     */
    public abstract FSFolder getRoot();
    
    /**
     * Returns the predefined fork types that this file system recognizes and
     * supports. Note that this does not mean that every file in the file system
     * will have these fork types, it just means that these forks may be present
     * in any file. Any fork type not returned by this method is unknown to the
     * file system handler and has no meaning in its context.<br>
     * <b>Note:</b> All implementations must support the FSForkType.DATA type,
     * as the main fork, the data fork, must always be defined for a file.
     * 
     * @return the predefined fork types that this file system recognizes and
     * supports.
     */
    public abstract FSForkType[] getSupportedForkTypes();
    
    /**
     * Closes the file system handler and frees allocated resources.
     */
    public abstract void close();
}
