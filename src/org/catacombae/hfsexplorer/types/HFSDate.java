/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.types;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author erik
 */
public class HFSDate {
    /**
     * Converts a HFS local date to a Java date, treated as if it had been
     * stored in the current locale/time zone. (I.e. if the date had been stored
     * 11:30 in Manaus, Brazil local time, and your computer's location is set
     * to Magadan, Russia, it will appear as if it was stored 11:30 in Magadan
     * time.)
     * 
     * @param hfsPlusTimestamp
     * @return
     */
    public static Date localTimestampToDate(int hfsPlusTimestamp) {
	Date baseDate = getBaseDate(TimeZone.getDefault());
	return new Date(baseDate.getTime() + (hfsPlusTimestamp & 0xFFFFFFFFL)*1000);
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
