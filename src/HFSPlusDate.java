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