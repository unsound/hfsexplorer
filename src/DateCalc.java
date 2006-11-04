import java.util.*;
import java.text.*;

/**
 * Reference to how the field HFSPlusDate.DIFF_TO_JAVA_DATE_IN_MILLIS is calculated.
 */
public class DateCalc {
    public static void main(String[] args) {
	Date d = new Date(0); // the exact date of the start of the epoch
	// HFS+ dates start at January 1, 1904, GMT, so we need to
	// find out the difference in milliseconds between those two dates.
	TimeZone usedTZ = TimeZone.getTimeZone("GMT+00:00");
	System.out.println(usedTZ);
	System.out.println(usedTZ.useDaylightTime());
	Calendar c1 = Calendar.getInstance(usedTZ);
	c1.setTime(d);
	c1.set(1904, 0, 1);
	Calendar c2 = Calendar.getInstance(usedTZ);
	c2.setTime(d);
	System.out.println("c1: " + c1);
	System.out.println("c2: " + c2);
	DateFormat dti = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
	dti.setTimeZone(usedTZ);
	System.out.println("c1.getTime(): " + dti.format(c1.getTime()));
	System.out.println("c2.getTime(): " + dti.format(c2.getTime()));
	System.out.println("d: " + dti.format(d));
	System.out.println("c1.getTime().getTime(): " + c1.getTime().getTime());
	System.out.println("c2.getTime().getTime(): " + c2.getTime().getTime());
	System.out.println("d.getTime(): " + d.getTime());
	System.out.println("Diff in millis: " + (c2.getTime().getTime()-c1.getTime().getTime()));
    }
}