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

package org.catacombae.jparted.lib.ps;

import org.catacombae.io.ReadableRandomAccessStream;

/**
 *
 * @author Erik Larsson
 */
public interface PartitionSystemRecognizer {
    /* TODO: detect should take a sectorSize argument... */
    /**
     * Detects whether there is a partition system located at the specified
     * offset in fsStream. Which partition system is being detected is dependent
     * on the context.
     *
     * @param fsStream
     * @param offset the offset in fsStream to the start of the file system.
     * @param length this parameter may be set to -1 if the length isn't
     * currently known.
     * @return <code>true</code> if a file system can be detected at the
     * specified offset in fsStream, and <code>false</code> otherwise.
     */
    public boolean detect(ReadableRandomAccessStream fsStream, long offset, long length);
}
