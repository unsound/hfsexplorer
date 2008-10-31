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
//import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
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

    protected static String format(double d, DecimalFormat unitFormatter) {
        final double rounded =
                roundDoubleToDecimals(d, unitFormatter.getMaximumFractionDigits());
        return unitFormatter.format(rounded);
    }
    
    public static double roundDoubleToDecimals(double d, int decimals) {
        long integerPart = (long)d;
        double remaining = d - integerPart;
        if(decimals > 0) {
            BigDecimal bd = new BigDecimal(remaining, new MathContext(decimals, RoundingMode.DOWN));
            return integerPart + bd.doubleValue();
        }
        else
            return (double)integerPart;
    }

    public static String bytesToBinaryUnit(long size) {
        return bytesToBinaryUnit(size, standardUnitFormatter);
    }

    public static String bytesToBinaryUnit(long size, DecimalFormat unitFormatter) {
        String result;

        if(size >= exbibyte)
            result = format(size / (double) exbibyte, unitFormatter) + " EiB";
        else if(size >= pebibyte)
            result = format(size / (double) pebibyte, unitFormatter) + " PiB";
        else if(size >= tebibyte)
            result = format(size / (double) tebibyte, unitFormatter) + " TiB";
        else if(size >= gibibyte)
            result = format(size / (double) gibibyte, unitFormatter) + " GiB";
        else if(size >= mebibyte)
            result = format(size / (double) mebibyte, unitFormatter) + " MiB";
        else if(size >= kibibyte)
            result = format(size / (double) kibibyte, unitFormatter) + " KiB";
        else
            result = size + " B";

        return result;
    }

    /*
    public static String bytesToBinaryUnit(double size) {
        return bytesToBinaryUnit(size, standardUnitFormatter);
    }

    public static String bytesToBinaryUnit(double size, DecimalFormat unitFormatter) {
        String result;
        RoundingMode oldRm = unitFormatter.getRoundingMode();
        unitFormatter.setRoundingMode(RoundingMode.DOWN);

        if(size >= exbibyte)
            result = unitFormatter.format(size / (double) exbibyte) + " EiB";
        else if(size >= pebibyte)
            result = unitFormatter.format(size / (double) pebibyte) + " PiB";
        else if(size >= tebibyte)
            result = unitFormatter.format(size / (double) tebibyte) + " TiB";
        else if(size >= gibibyte)
            result = unitFormatter.format(size / (double) gibibyte) + " GiB";
        else if(size >= mebibyte)
            result = unitFormatter.format(size / (double) mebibyte) + " MiB";
        else if(size >= kibibyte)
            result = unitFormatter.format(size / (double) kibibyte) + " KiB";
        else
            result = unitFormatter.format(size) + " B";

        if(Util.isJava6OrHigher())
            unitFormatter.setRoundingMode(oldRm);
        return result;
    }*/

    public static String bytesToDecimalBitUnit(long bytes) {
        return bytesToDecimalBitUnit(bytes, standardUnitFormatter);
    }

    public static String bytesToDecimalBitUnit(long bytes, DecimalFormat unitFormatter) {
        long bits = bytes * 8;
        String result;

        if(bits >= exa)
            result = format(bits / (double) exa, unitFormatter) + " Ebit";
        else if(bits >= peta)
            result = format(bits / (double) peta, unitFormatter) + " Pbit";
        else if(bits >= tera)
            result = format(bits / (double) tera, unitFormatter) + " Tbit";
        else if(bits >= giga)
            result = format(bits / (double) giga, unitFormatter) + " Gbit";
        else if(bits >= mega)
            result = format(bits / (double) mega, unitFormatter) + " Mbit";
        else if(bits >= kilo)
            result = format(bits / (double) kilo, unitFormatter) + " Kbit";
        else
            result = bits + " bit";

        return result;
    }

    /*
    public static String bytesToDecimalBitUnit(double bytes) {
        return bytesToDecimalBitUnit(bytes, standardUnitFormatter);
    }

    public static String bytesToDecimalBitUnit(double bytes, DecimalFormat unitFormatter) {
        double bits = bytes * 8;
        String result;
        RoundingMode oldRm = unitFormatter.getRoundingMode();
        unitFormatter.setRoundingMode(RoundingMode.DOWN);

        if(bits >= exa)
            result = unitFormatter.format(bits / (double) exa) + " Ebit";
        else if(bits >= peta)
            result = unitFormatter.format(bits / (double) peta) + " Pbit";
        else if(bits >= tera)
            result = unitFormatter.format(bits / (double) tera) + " Tbit";
        else if(bits >= giga)
            result = unitFormatter.format(bits / (double) giga) + " Gbit";
        else if(bits >= mega)
            result = unitFormatter.format(bits / (double) mega) + " Mbit";
        else if(bits >= kilo)
            result = unitFormatter.format(bits / (double) kilo) + " Kbit";
        else
            result = unitFormatter.format(bits) + " bit";

        if(Util.isJava6OrHigher())
            unitFormatter.setRoundingMode(oldRm);
        return result;
    }
     * */

    /**
     * Returns the value of a^n by multiplying a with itself n times. This is an
     * integer only operation, so only positive exponents are allowed.
     *
     * @param a base.
     * @param n exponent.
     * @return a^n.
     * @throws IllegalArgumentException if the exponent is out of range (&lt;0).
     */
    protected static long mypow(long a, long n) throws IllegalArgumentException {
        if(n < 0)
            throw new IllegalArgumentException("b can not be negative");

        long result = 1;
        for(long i = 0; i < n; ++i)
            result *= a;
        return result;
    }

    /*
    public static void main(String[] args) {
        PrintStream ps = System.err;
        ps.println("Testing roundDoubleToDecimals:");
        ps.println("  Pi: " + Math.PI);
        ps.println("  Pi 0: " + roundDoubleToDecimals(Math.PI, 0));
        ps.println("  Pi 1: " + roundDoubleToDecimals(Math.PI, 1));
        ps.println("  Pi 2: " + roundDoubleToDecimals(Math.PI, 2));
        ps.println("  Pi 4: " + roundDoubleToDecimals(Math.PI, 4));
        ps.println("  Pi 44: " + roundDoubleToDecimals(Math.PI, 44));

        ps.println("Testing different DecimalFormat variants:");
        DecimalFormat[] fmts = new DecimalFormat[] {
            new DecimalFormat("0"),
            new DecimalFormat("0.0"),
            new DecimalFormat("0.00"),
            new DecimalFormat("0.000"),
            new DecimalFormat("0.0000"),
            new DecimalFormat("0.00000"),
        };

        for(int i = 0; i < fmts.length; ++i) {
            DecimalFormat fmt = fmts[i];
            ps.println("  Entry " + i + ":");
            ps.println("    getMaximumIntegerDigits()=" + fmt.getMaximumIntegerDigits());
            ps.println("    getMinimumIntegerDigits()=" + fmt.getMinimumIntegerDigits());
            ps.println("    getMaximumFractionDigits()=" + fmt.getMaximumFractionDigits());
            ps.println("    getMinimumFractionDigits()=" + fmt.getMinimumFractionDigits());
            ps.println("    format(Pi)=" + fmt.format(Math.PI));
            ps.println("    format(rounded Pi)=" + fmt.format(roundDoubleToDecimals(Math.PI, fmt.getMaximumFractionDigits())));
        }
    }
    */
}
