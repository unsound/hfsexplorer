/*-
 * Copyright (C) 2006 Erik Larsson
 * 
 * All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 */

package org.catacombae.hfsexplorer.types;

import org.catacombae.hfsexplorer.Util;
import org.catacombae.hfsexplorer.Util2;
import java.util.Date;

/** In the future, this should wrap a 32 bit HFS+ date. */

public class HFSPlusDate {
    /** 
     * Pre-calculated. This is the amount of milliseconds between 01-01-1904 00:00:00.0000
     * (HFS+ starting date) and 01-01-1970 00:00:00.0000 (the start of the java "epoch").
     */
    public static final long DIFF_TO_JAVA_DATE_IN_MILLIS = 2082844800000L;
    
    /** Converts a HFS+ date to a Java Date. */
    public static Date toDate(int hfsPlusTimestamp) {
	return new Date(Util2.unsign(hfsPlusTimestamp)*1000 - DIFF_TO_JAVA_DATE_IN_MILLIS);
    }
}
