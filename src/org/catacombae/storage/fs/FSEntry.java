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

package org.catacombae.storage.fs;

/**
 * A file system entry in our hierarchical file system model. This corresponds
 * to one of the nodes in a file system that denote a file, folder, device,
 * socket, etc.
 *
 * @author <a href="mailto:catacombae@gmail.com">Erik Larsson</a>
 */
public interface FSEntry {
    public static enum Type {
        FILE, FOLDER, SYMLINK, CHARACTER_DEVICE, BLOCK_DEVICE, FIFO, SOCKET;
    }

    /**
     * Returns the attributes of this file system entry. Which attributes are
     * available for a specific file system varies enourmously.
     *
     * @return the attributes of this file system entry.
     */
    public FSAttributes getAttributes();

    public String getName();

    //public abstract FSFolder getParent();

    /**
     * Returns all available forks for this file.
     *
     * @return all available forks for this file.
     */
    public FSFork[] getAllForks();

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
    public FSFork getForkByType(FSForkType type);

    /**
     * Returns the length of the data of all of the file's forks put together.
     * For instance if a file has a data fork of 12 bytes and a resource fork of
     * 8 bytes, this method will return the value 20.
     *
     * @return the length of the data of all of the file's forks put together.
     */
    public long getCombinedLength();
}
