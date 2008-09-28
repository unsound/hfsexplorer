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

package org.catacombae.jparted.lib.fs.hfsx;

import org.catacombae.hfsexplorer.fs.ImplHFSXFileSystemView;
import org.catacombae.jparted.lib.DataLocator;
import org.catacombae.jparted.lib.fs.hfscommon.HFSCommonFileSystemHandler;

/**
 * HFSX implementation of a FileSystemHandler. This implementation can be used
 * to access HFSX file systems. (HFSX file systems are very similar to HFS+,
 * but with a few extensions, like the ability to treat file names in a case
 * sensitive manner).
 * 
 * @author Erik Larsson
 */
public class HFSXFileSystemHandler extends HFSCommonFileSystemHandler {
    
    public HFSXFileSystemHandler(DataLocator fsLocator, boolean useCaching, 
            boolean doUnicodeFileNameComposition) {
        super(new ImplHFSXFileSystemView(fsLocator.createReadOnlyFile(), 0, useCaching),
                doUnicodeFileNameComposition);
    }
}
