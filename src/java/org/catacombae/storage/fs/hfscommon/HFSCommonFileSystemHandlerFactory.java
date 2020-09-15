/*-
 * Copyright (C) 2009-2014 Erik Larsson
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

package org.catacombae.storage.fs.hfscommon;

import org.catacombae.storage.fs.FileSystemHandlerFactory;

/**
 * @author <a href="https://catacombae.org" target="_top">Erik Larsson</a>
 */
public abstract class HFSCommonFileSystemHandlerFactory  extends FileSystemHandlerFactory {
    protected static final CustomAttribute posixFilenamesAttribute =
            createCustomAttribute(AttributeType.BOOLEAN, "POSIX_FILENAMES",
                    "Controls whether filenames should be translated from " +
                    "their on-disk format to POSIX format, swapping the ':' " +
                    "and '/' characters.", false);

    protected static final CustomAttribute sfmSubstitutionsAttribute =
            createCustomAttribute(AttributeType.BOOLEAN, "SFM_SUBSTITUTIONS",
                    "Controls whether filename characters that are invalid " +
                    "in Windows filesystems should be remapped to characters " +
                    "in the Unicode private range using the same method used " +
                    "in Microsoft's now defunct Services for Mac software.",
                    false);

    public CustomAttribute[] getSupportedCustomAttributes() {
        return new CustomAttribute[] {
            posixFilenamesAttribute,
            sfmSubstitutionsAttribute,
        };
    }
}
