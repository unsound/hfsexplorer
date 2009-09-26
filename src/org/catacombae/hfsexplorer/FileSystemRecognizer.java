/*-
 * Copyright (C) 2006-2007 Erik Larsson
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
package org.catacombae.hfsexplorer;

import org.catacombae.jparted.lib.fs.hfscommon.HFSCommonFileSystemRecognizer.FileSystemType;

/** This class just detects if the file system is of type HFS, HFS+ or HFSX. */
class FileSystemRecognizer {

    /** Change this array to tell the recognizer which types HFSExplorer supports. */
    public static final FileSystemType[] supportedTypes = {
        FileSystemType.HFS,
        FileSystemType.HFS_PLUS,
        FileSystemType.HFS_WRAPPED_HFS_PLUS,
        FileSystemType.HFSX,
    };

    public static boolean isTypeSupported(FileSystemType fst) {
        for(FileSystemType cur : supportedTypes)
            if(cur == fst)
                return true;
        return false;
    }
}
