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
 * Enumerates the fork types that are recognized by the API as having a special
 * meaning.
 * 
 * @author Erik Larsson
 */
public enum FSForkType {
    /**
     * The data fork, the "main" fork of a file. All file system
     * implementations must support this fork type.
     */
    DATA,
    /**
     * The resource fork in a MacOS file system. The resource fork has a special
     * meaning for MacOS operating systems, as content type, thumbnails and
     * other metadata are stored there.
     */
    MACOS_RESOURCE;
}
