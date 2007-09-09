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

// Ripped from TypherTransfer. Relevant changes should be backported.

package org.catacombae.hfsexplorer;
import java.text.DecimalFormat;

public class SpeedUnitUtils {
    private static final long kibibyte = mypow(2, 10);
    private static final long mebibyte = mypow(2, 20);
    private static final long gibibyte = mypow(2, 30);
    private static final long tebibyte = mypow(2, 40);
    private static final long pebibyte = mypow(2, 50);
    private static final long exbibyte = mypow(2, 60);
    private static final long kilo = mypow(10, 3);
    private static final long mega = mypow(10, 6);
    private static final long giga = mypow(10, 9);
    private static final long tera = mypow(10, 12);
    private static final long peta = mypow(10, 15);
    private static final long exa = mypow(10, 18);
    private static final DecimalFormat standardUnitFormatter = new DecimalFormat("0");
    
    public static String bytesToBinaryUnit(long size) {
	return bytesToBinaryUnit(size, standardUnitFormatter);
    }
    public static String bytesToBinaryUnit(long size, DecimalFormat unitFormatter) {
	String result;

	if(size >= exbibyte)
	    result = unitFormatter.format(size / (double)exbibyte) + " EiB";
	else if(size >= pebibyte)
	    result = unitFormatter.format(size / (double)pebibyte) + " PiB";
	else if(size >= tebibyte)
	    result = unitFormatter.format(size / (double)tebibyte) + " TiB";
	else if(size >= gibibyte)
	    result = unitFormatter.format(size / (double)gibibyte) + " GiB";
	else if(size >= mebibyte)
	    result = unitFormatter.format(size / (double)mebibyte) + " MiB";
	else if(size >= kibibyte)
	    result = unitFormatter.format(size / (double)kibibyte) + " KiB";
	else
	    result = size + " B";
	
	return result;
    }
    
    public static String bytesToBinaryUnit(double size) {
	return bytesToBinaryUnit(size, standardUnitFormatter);
    }
    public static String bytesToBinaryUnit(double size, DecimalFormat unitFormatter) {
	String result;

	if(size >= exbibyte)
	    result = unitFormatter.format(size / (double)exbibyte) + " EiB";
	else if(size >= pebibyte)
	    result = unitFormatter.format(size / (double)pebibyte) + " PiB";
	else if(size >= tebibyte)
	    result = unitFormatter.format(size / (double)tebibyte) + " TiB";
	else if(size >= gibibyte)
	    result = unitFormatter.format(size / (double)gibibyte) + " GiB";
	else if(size >= mebibyte)
	    result = unitFormatter.format(size / (double)mebibyte) + " MiB";
	else if(size >= kibibyte)
	    result = unitFormatter.format(size / (double)kibibyte) + " KiB";
	else
	    result = unitFormatter.format(size) + " B";
	
	return result;
    }
    
    public static String bytesToDecimalBitUnit(long bytes) {
	return bytesToDecimalBitUnit(bytes, standardUnitFormatter);
    }
    public static String bytesToDecimalBitUnit(long bytes, DecimalFormat unitFormatter) {
	long bits = bytes*8;
	String result;
	if(bits >= exa)
	    result = unitFormatter.format(bits / (double)exa) + " Ebit";
	else if(bits >= peta)
	    result = unitFormatter.format(bits / (double)peta) + " Pbit";
	else if(bits >= tera)
	    result = unitFormatter.format(bits / (double)tera) + " Tbit";
	else if(bits >= giga)
	    result = unitFormatter.format(bits / (double)giga) + " Gbit";
	else if(bits >= mega)
	    result = unitFormatter.format(bits / (double)mega) + " Mbit";
	else if(bits >= kilo)
	    result = unitFormatter.format(bits / (double)kilo) + " Kbit";
	else
	    result = bits + " bit";
	return result;
    }
    
    public static String bytesToDecimalBitUnit(double bytes) {
	return bytesToDecimalBitUnit(bytes, standardUnitFormatter);
    }
    public static String bytesToDecimalBitUnit(double bytes, DecimalFormat unitFormatter) {
	double bits = bytes*8;
	String result;
	if(bits >= exa)
	    result = unitFormatter.format(bits / (double)exa) + " Ebit";
	else if(bits >= peta)
	    result = unitFormatter.format(bits / (double)peta) + " Pbit";
	else if(bits >= tera)
	    result = unitFormatter.format(bits / (double)tera) + " Tbit";
	else if(bits >= giga)
	    result = unitFormatter.format(bits / (double)giga) + " Gbit";
	else if(bits >= mega)
	    result = unitFormatter.format(bits / (double)mega) + " Mbit";
	else if(bits >= kilo)
	    result = unitFormatter.format(bits / (double)kilo) + " Kbit";
	else
	    result = unitFormatter.format(bits) + " bit";
	return result;
    }

    private static long mypow(long a, long b) {
	if(b < 0) throw new IllegalArgumentException("b can not be negative");
	
	long result = 1;
	for(long i = 0; i < b; ++i)
	    result *= a;
	return result;
    }
    
}
