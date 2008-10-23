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

package org.catacombae.jparted.lib.fs.hfscommon;

import java.util.Date;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogAttributes;
import org.catacombae.jparted.lib.fs.FSAttributes;
import org.catacombae.jparted.lib.fs.FSEntry;
import org.catacombae.jparted.lib.fs.WindowsFileAttributes;

/**
 *
 * @author Erik Larsson
 */
class HFSCommonFSAttributes extends FSAttributes {
    
    private final FSEntry parentEntry;
    private final CommonHFSCatalogAttributes attributes;
    
    public HFSCommonFSAttributes(FSEntry parentEntry, CommonHFSCatalogAttributes attributes) {
        this.parentEntry = parentEntry;
        this.attributes = attributes;
    }
    
    @Override
    public POSIXFileAttributes getPOSIXAttributes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public WindowsFileAttributes getWindowsFileAttributes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Date getModifyDate() {
        return attributes.getContentModDateAsDate();
    }

}
