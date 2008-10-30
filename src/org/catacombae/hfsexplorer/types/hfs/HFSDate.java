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

package org.catacombae.hfsexplorer.types.hfs;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author <a href="mailto:erik82@kth.se">Erik Larsson</a>
 */
public class HFSDate {
    /**
     * Converts a HFS local date to a Java date, treated as if it had been
     * stored in the current locale/time zone. (I.e. if the date had been stored
     * 11:30 in Manaus, Brazil local time, and your computer's location is set
     * to Magadan, Russia, it will appear as if it was stored 11:30 in Magadan
     * time.)
     * 
     * @param hfsTimestamp the HFS timestamp value (seconds elapsed since Jan 1 1904, local time).
     * @return a java.util.Date representation of the HFS timestamp.
     */
    public static Date localTimestampToDate(int hfsTimestamp) {
	Date baseDate = getBaseDate(TimeZone.getDefault());
	return new Date(baseDate.getTime() + (hfsTimestamp & 0xFFFFFFFFL)*1000);
        /*
	Calendar c = Calendar.getInstance(); // Get Calendar for current time zone and locale
        c.setLenient(true);
        c.clear();
	c.setTimeZone(TimeZone.getDefault());
        c.set(Calendar.YEAR, 1904);
	c.set(Calendar.DAY_OF_YEAR, 1);
	if(hfsPlusTimestamp < 0)
	    c.add(Calendar.SECOND, 0x7FFFFFFF);
	c.add(Calendar.SECOND, hfsPlusTimestamp & 0x7FFFFFFF);
        return c.getTime();
	*/
	//return timestampToDate(hfsPlusTimestamp, TimeZone.getDefault());
    }
    
    protected static Date getBaseDate(TimeZone tz) {
	Calendar c = Calendar.getInstance(tz);
	c.clear();
        c.set(Calendar.YEAR, 1904);
	return c.getTime();
    }
}
