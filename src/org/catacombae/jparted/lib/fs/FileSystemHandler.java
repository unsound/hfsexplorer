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

import java.util.LinkedList;

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
     * Returns the FSEntry present on the location <code>path</code>. If the
     * path is invalid in this file system, <code>null</code> is returned.
     *
     * @param path the file system path to the requested entry, path element by
     * path element (ex. <code>getEntry("usr", "local", "bin", "java");</code>).
     * @return the FSEntry present on the location <code>path</code>, or
     * <code>null</code> if no such entry exists.
     */
    public abstract FSEntry getEntry(String... path);
    
    /**
     * Looks up the FSEntry denoted by the supplied POSIX path. Since POSIX
     * paths may be relative, a root folder is needed to resolve the relative
     * path structure. If the POSIX pathname is absolute, the root folder
     * parameter will not be used.
     *
     * @param posixPath the POSIX pathname.
     * @param rootFolder the root folder from which we should start resolving
     * the path.
     * @return the FSEntry corresponding to the supplied POSIX path, or <code>
     * null</code> if no such pathname could be found.
     * @throws java.lang.IllegalArgumentException if <code>posixPath</code> is
     * an invalid pathname.
     */
    public FSEntry getEntryByPosixPath(final String posixPath,
            final FSFolder rootFolder) throws IllegalArgumentException {
        String[] components = posixPath.split("/");

        int i = 0;
        FSEntry curEntry;
        LinkedList<String[]> visitedLinks = null;

        // If we encounter a '/' as the first character, we have an absolute path
        if(posixPath.startsWith("/")) {
            i = 1;
            curEntry = getRoot();
        }
        else
            curEntry = rootFolder;

        for(; i < components.length; ++i) {
            FSFolder curFolder;
            if(curEntry instanceof FSFolder)
                curFolder = (FSFolder) curEntry;
            else if(curEntry instanceof FSLink) {
                FSLink curLink = (FSLink) curEntry;
                // Resolve links.
                if(visitedLinks == null)
                    visitedLinks = new LinkedList<String[]>();
                else
                    visitedLinks.clear();

                visitedLinks.add(curEntry.getAbsolutePath());
                FSEntry linkTarget = curLink.getLinkTarget();
                while(linkTarget != null && linkTarget instanceof FSLink) {
                    curLink = (FSLink) linkTarget;
                    String[] curPath = curLink.getAbsolutePath();
                    for(String[] visitedPath : visitedLinks) {
                        if(curPath.length == visitedPath.length) {
                            int j = 0;
                            for(; j < curPath.length; ++j) {
                                if(!curPath[j].equals(visitedPath[j]))
                                    break;
                            }
                            if(j == curPath.length)
                                return null; // We have been here before! Circular linking...
                        }
                    }

                    visitedLinks.add(curPath);
                    linkTarget = curLink.getLinkTarget();
                }

                if(linkTarget == null)
                    return null; // Invalid link target.
                if(linkTarget instanceof FSFolder)
                    curFolder = (FSFolder) linkTarget;
                else
                    return null; // Link is pointing to a file or some unknown creature from the future

                visitedLinks.clear();
            }
            else
                return null; // Invalid pathname

            String curPathComponent = components[i];

            if(curPathComponent.length() == 0 || curPathComponent.equals(".")) {
                // We allow empty components (multiple slashes between components)
            }
            else if(curPathComponent.equals("..")) {
                curFolder = curFolder.getParent();
            }
            else {
                String fsPathnameComponent = parsePosixPathnameComponent(curPathComponent);

                FSEntry nextEntry = null;
                for(FSEntry entry : curFolder.list()) {
                    if(entry.getName().equals(fsPathnameComponent)) {
                        nextEntry = entry;
                        break;
                    }

                }

                if(nextEntry != null)
                    curEntry = nextEntry;
                else
                    return null; // Invalid pathname
            }
        }

        return curEntry;
    }

    /**
     * Converts the supplied POSIX pathname component into the proper file
     * system pathname component form. For example, the ':' character in HFS+
     * POSIX pathname components represent the character '/' in the file system.
     * <br>
     * For a strictly POSIX file system, this method should just bounce the
     * input string.
     *
     * @param posixPathnameComponent
     * @return
     */
    public abstract String parsePosixPathnameComponent(String posixPathnameComponent);

    /**
     * Converts the supplied file system pathname component into a corresponding
     * POSIX pathname component. This may involve converting certain
     * POSIX-incompatible characters into suitable replacements, such as
     * pathname components containing the character '/'.
     * <br>
     * For a strictly POSIX file system, this method should just bounce the
     * input string.
     *
     * @param fsPathnameComponent
     * @return
     */
    public abstract String generatePosixPathnameComponent(String fsPathnameComponent);

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
