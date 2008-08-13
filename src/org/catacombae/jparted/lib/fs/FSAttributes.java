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

import java.util.Date;

/**
 *
 * @author <a href="mailto:erik82@kth.se">Erik Larsson</a>
 */
public abstract class FSAttributes {
    public static class POSIXFileAttributes {}
    
    public abstract POSIXFileAttributes getPOSIXAttributes();
    
    public abstract WindowsFileAttributes getWindowsFileAttributes();
    
    public abstract Date getModifyDate();
    //public abstract FSAccessControlList getAccessControlList();
}
