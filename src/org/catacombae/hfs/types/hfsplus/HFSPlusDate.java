/*-
 * Copyright (C) 2006 Erik Larsson
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

package org.catacombae.hfsexplorer.types.hfsplus;

import org.catacombae.hfsexplorer.types.hfs.HFSDate;
import org.catacombae.hfsexplorer.types.*;
import java.util.Date;
import java.util.TimeZone;

/** In the future, this could wrap a 32 bit HFS+ date. */
public class HFSPlusDate extends HFSDate {
    /** 
     * Pre-calculated. This is the amount of milliseconds between 01-01-1904 00:00:00.0000
     * (HFS+ starting date) and 01-01-1970 00:00:00.0000 (the start of the java "epoch").
     */
    //public static final long DIFF_TO_JAVA_DATE_IN_MILLIS = 2082844800000L;
    
    /**
     * Converts a HFS+ date stored in GMT to a Java Date.
     * 
     * @param hfsPlusTimestamp an HFS+ timestamp stored in GMT.
     * @return a java.util.Date set to the time of the HFS+ GMT timestamp.
     */
    public static Date gmtTimestampToDate(int hfsPlusTimestamp) {
	Date baseDate = getBaseDate(TimeZone.getTimeZone("GMT"));
	return new Date(baseDate.getTime() + (hfsPlusTimestamp & 0xFFFFFFFFL)*1000);
	/*
	Calendar c = Calendar.getInstance();
	c.clear();
	c.setLenient(true);
	c.setTimeZone(TimeZone.getTimeZone("GMT"));
	c.set(Calendar.YEAR, 1904);
	c.set(Calendar.DAY_OF_YEAR, 1);
	if(hfsPlusTimestamp < 0)
	    c.add(Calendar.SECOND, 0x7FFFFFFF);
	c.add(Calendar.SECOND, hfsPlusTimestamp & 0x7FFFFFFF);
	return c.getTime();
	*/
	//return timestampToDate(hfsPlusTimestamp, TimeZone.getTimeZone("GMT"));
	//return new Date((hfsPlusTimestamp & 0xFFFFFFFFL)*1000 - DIFF_TO_JAVA_DATE_IN_MILLIS);
    }
    
    
}
