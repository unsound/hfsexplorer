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
 * This interface represents an entry corresponding to a file in the file system
 * presented by a FileSystemHandler. A file is an entry that does not have
 * subentries, like a folder, but that has associated data. A file can have one
 * or more "forks" of data, where one of the forks always must be considered the
 * "main" fork or the "data" fork. Some file systems do not support forks, in
 * which case the only fork available is the "main" fork.
 * 
 * @author <a href="mailto:erik82@kth.se">Erik Larsson</a>
 */
public interface FSFile extends FSEntry {
    /**
     * Returns the main (data) fork of this file, containing the data that users
     * normally will see. All implementors must make sure that this FSFork
     * always exists.
     * 
     * @return the main (data) fork of this file.
     */
    public abstract FSFork getMainFork();
}
