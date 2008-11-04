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

package org.catacombae.hfsexplorer;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.io.RuntimeIOException;

//package org.catacombae.rarx;
// Ripped from junrarlib

public class Util {
    public static int sectorSize = 0x800;
    
    public static String byteArrayToHexString(byte[] array) { 
	return byteArrayToHexString(array, 0, array.length);
    }
    public static String byteArrayToHexString(byte[] array, int offset, int length) { 
	String result = "";
	for(int i = offset; i < (offset+length); ++i) {
	    byte b = array[i];
	    String currentHexString = Integer.toHexString(b & 0xFF);
	    if(currentHexString.length() == 1)
		currentHexString = "0" + currentHexString;
	    result += currentHexString;
	}
	return result;
    }

    public static String toHexStringBE(char[] array) {
	return toHexStringBE(array, 0, array.length);
    }
    public static String toHexStringBE(char[] array, int offset, int length) {
	StringBuilder result = new StringBuilder();
	for(int i = offset; i < length; ++i)
	    result.append(toHexStringBE(array[i]));
	return result.toString();
    }
    public static String toHexStringBE(short[] array) {
	return toHexStringBE(array, 0, array.length);
    }
    public static String toHexStringBE(short[] array, int offset, int length) {
	StringBuilder result = new StringBuilder();
	for(int i = offset; i < length; ++i)
	    result.append(toHexStringBE(array[i]));
	return result.toString();
    }
    public static String toHexStringBE(int[] array) {
	return toHexStringBE(array, 0, array.length);
    }
    public static String toHexStringBE(int[] array, int offset, int length) {
	StringBuilder result = new StringBuilder();
	for(int i = offset; i < length; ++i)
	    result.append(toHexStringBE(array[i]));
	return result.toString();
    }
    
    public static String toHexStringLE(byte n) { return byteArrayToHexString(toByteArrayLE(n)); }
    public static String toHexStringLE(short n) { return byteArrayToHexString(toByteArrayLE(n)); }
    public static String toHexStringLE(char n) { return byteArrayToHexString(toByteArrayLE(n)); }
    public static String toHexStringLE(int n) { return byteArrayToHexString(toByteArrayLE(n)); }
    public static String toHexStringLE(long n) { return byteArrayToHexString(toByteArrayLE(n)); }
    public static String toHexStringBE(byte n) { return byteArrayToHexString(toByteArrayBE(n)); }
    public static String toHexStringBE(short n) { return byteArrayToHexString(toByteArrayBE(n)); }
    public static String toHexStringBE(char n) { return byteArrayToHexString(toByteArrayBE(n)); }
    public static String toHexStringBE(int n) { return byteArrayToHexString(toByteArrayBE(n)); }
    public static String toHexStringBE(long n) { return byteArrayToHexString(toByteArrayBE(n)); }
    
    public static byte[] invert(byte[] array) {
	byte[] newArray = new byte[array.length];
	for(int i = 0; i < array.length; ++i)
	    newArray[newArray.length-i-1] = array[i];
	return newArray;
    }
    
    public static long readLongLE(byte[] data) {
	return readLongLE(data, 0);
    }
    public static long readLongLE(byte[] data, int offset) {
	return (((long)data[offset+7] & 0xFF) << 56 |
		((long)data[offset+6] & 0xFF) << 48 |
		((long)data[offset+5] & 0xFF) << 40 |
		((long)data[offset+4] & 0xFF) << 32 |
		((long)data[offset+3] & 0xFF) << 24 |
		((long)data[offset+2] & 0xFF) << 16 |
		((long)data[offset+1] & 0xFF) << 8 |
		((long)data[offset+0] & 0xFF) << 0);
    }
    public static int readIntLE(byte[] data) {
	return readIntLE(data, 0);
    }
    public static int readIntLE(byte[] data, int offset) {
	return ((data[offset+3] & 0xFF) << 24 |
		(data[offset+2] & 0xFF) << 16 |
		(data[offset+1] & 0xFF) << 8 |
		(data[offset+0] & 0xFF) << 0);
    }
    public static short readShortLE(byte[] data) {
	return readShortLE(data, 0);
    }
    public static short readShortLE(byte[] data, int offset) {
	return (short) ((data[offset+1] & 0xFF) << 8 |
			(data[offset+0] & 0xFF) << 0);
    }
    public static byte readByteLE(byte[] data) {
	return readByteLE(data, 0);
    }
    public static byte readByteLE(byte[] data, int offset) {
	return data[offset];
    }
    public static long readLongBE(byte[] data) {
	return readLongBE(data, 0);
    }

//     public static void main(String[] args) {
// 	byte[] longTest1 = { 0, 0, 0, 0, 0, 0, 0, 13 };
// 	byte[] longTest2 = { 0, 31, 0, 0, 0, 0, 0, 0 };
// 	byte[] longTest3 = { 127, 0, 0, 0, 0, 0, 0, 0 };
// 	byte[] longTest4 = { 0, 0, 0, 1, 1, 1, 1, 1 };
// 	System.out.println("longTest1: " + readLongBE(longTest1));
// 	System.out.println("longTest2: " + readLongBE(longTest2));
// 	System.out.println("longTest3: " + readLongBE(longTest3));
// 	System.out.println("longTest4: " + readLongBE(longTest4));
//     }

    public static long readLongBE(byte[] data, int offset) {
// 	if(false) {
// 	    long d1 = (data[offset+0] & 0xFFL) << 56;
// 	    long d2 = (data[offset+1] & 0xFFL) << 48;
// 	    long d3 = (data[offset+2] & 0xFFL) << 40;
// 	    long d4 = (data[offset+3] & 0xFFL) << 32;
// 	    long d5 = (data[offset+4] & 0xFFL) << 24;
// 	    long d6 = (data[offset+5] & 0xFFL) << 16;
// 	    long d7 = (data[offset+6] & 0xFFL) << 8;
// 	    long d8 = (data[offset+7] & 0xFFL) << 0;
// 	    System.out.println("1. 0x" + toHexStringBE(d1));
// 	    System.out.println("2. 0x" + toHexStringBE(d2));
// 	    System.out.println("3. 0x" + toHexStringBE(d3));
// 	    System.out.println("4. 0x" + toHexStringBE(d4));
// 	    System.out.println("5. 0x" + toHexStringBE(d5));
// 	    System.out.println("6. 0x" + toHexStringBE(d6));
// 	    System.out.println("7. 0x" + toHexStringBE(d7));
// 	    System.out.println("8. 0x" + toHexStringBE(d8));
// 	    return d1 | d2 | d3 | d4 | d5 | d6 | d7 | d8; 
// 	}
// 	else {
	return (((long)data[offset+0] & 0xFF) << 56 |
		((long)data[offset+1] & 0xFF) << 48 |
		((long)data[offset+2] & 0xFF) << 40 |
		((long)data[offset+3] & 0xFF) << 32 |
		((long)data[offset+4] & 0xFF) << 24 |
		((long)data[offset+5] & 0xFF) << 16 |
		((long)data[offset+6] & 0xFF) << 8 |
		((long)data[offset+7] & 0xFF) << 0);
// 	}
    }
    public static int readIntBE(byte[] data) {
	return readIntBE(data, 0);
    }
    public static int readIntBE(byte[] data, int offset) {
	return ((data[offset+0] & 0xFF) << 24 |
		(data[offset+1] & 0xFF) << 16 |
		(data[offset+2] & 0xFF) << 8 |
		(data[offset+3] & 0xFF) << 0);
    }
    public static short readShortBE(byte[] data) {
	return readShortBE(data, 0);
    }
    public static short readShortBE(byte[] data, int offset) {
	return (short) ((data[offset+0] & 0xFF) << 8 |
			(data[offset+1] & 0xFF) << 0);
    }
    public static byte readByteBE(byte[] data) {
	return readByteBE(data, 0);
    }
    public static byte readByteBE(byte[] data, int offset) {
	return data[offset];
    }

    public static byte[] toByteArrayLE(byte b) {
	byte[] result = new byte[1];
	result[0] = b;
	return result;
    }
    public static byte[] toByteArrayLE(short s) {
	byte[] result = new byte[2];
	result[0] = (byte) ((s >> 0) & 0xFF);
	result[1] = (byte) ((s >> 8) & 0xFF);
	return result;
    }
    public static byte[] toByteArrayLE(char c) {
	byte[] result = new byte[2];
	result[0] = (byte) ((c >> 0) & 0xFF);
	result[1] = (byte) ((c >> 8) & 0xFF);
	return result;
    }
    public static byte[] toByteArrayLE(int i) {
	byte[] result = new byte[4];
	result[0] = (byte) ((i >> 0) & 0xFF);
	result[1] = (byte) ((i >> 8) & 0xFF);
	result[2] = (byte) ((i >> 16) & 0xFF);
	result[3] = (byte) ((i >> 24) & 0xFF);
	return result;
    }
    public static byte[] toByteArrayLE(long l) {
	byte[] result = new byte[8];
	result[0] = (byte) ((l >> 0) & 0xFF);
	result[1] = (byte) ((l >> 8) & 0xFF);
	result[2] = (byte) ((l >> 16) & 0xFF);
	result[3] = (byte) ((l >> 24) & 0xFF);
	result[4] = (byte) ((l >> 32) & 0xFF);
	result[5] = (byte) ((l >> 40) & 0xFF);
	result[6] = (byte) ((l >> 48) & 0xFF);
	result[7] = (byte) ((l >> 56) & 0xFF);
	return result;
    }
    public static byte[] toByteArrayBE(byte b) {
	byte[] result = new byte[1];
	result[0] = b;
	return result;
    }
    public static byte[] toByteArrayBE(short s) {
	byte[] result = new byte[2];
	result[0] = (byte) ((s >> 8) & 0xFF);
	result[1] = (byte) ((s >> 0) & 0xFF);
	return result;
    }
    public static byte[] toByteArrayBE(char c) {
	byte[] result = new byte[2];
	result[0] = (byte) ((c >> 8) & 0xFF);
	result[1] = (byte) ((c >> 0) & 0xFF);
	return result;
    }
    public static byte[] toByteArrayBE(int i) {
	byte[] result = new byte[4];
	result[0] = (byte) ((i >> 24) & 0xFF);
	result[1] = (byte) ((i >> 16) & 0xFF);
	result[2] = (byte) ((i >> 8) & 0xFF);
	result[3] = (byte) ((i >> 0) & 0xFF);
	return result;
    }
    public static byte[] toByteArrayBE(long l) {
	byte[] result = new byte[8];
	result[0] = (byte) ((l >> 56) & 0xFF);
	result[1] = (byte) ((l >> 48) & 0xFF);
	result[2] = (byte) ((l >> 40) & 0xFF);
	result[3] = (byte) ((l >> 32) & 0xFF);
	result[4] = (byte) ((l >> 24) & 0xFF);
	result[5] = (byte) ((l >> 16) & 0xFF);
	result[6] = (byte) ((l >> 8) & 0xFF);
	result[7] = (byte) ((l >> 0) & 0xFF);
	return result;
    }

    public static boolean zeroed(byte[] ba) {
	for(byte b : ba)
	    if(b != 0)
		return false;
	return true;
    }
    
    public static void zero(byte[]... arrays) {
        for(byte[] ba : arrays)
            set(ba, 0, ba.length, (byte)0);
    }
    public static void zero(byte[] ba, int offset, int length) {
        set(ba, offset, length, (byte)0);
    }
    public static void zero(short[]... arrays) {
        for(short[] array : arrays)
            set(array, 0, array.length, (short)0);
    }
    public static void zero(short[] ba, int offset, int length) {
        set(ba, offset, length, (short)0);
    }
    public static void zero(int[]... arrays) {
        for(int[] array : arrays)
            set(array, 0, array.length, (int) 0);
    }
    public static void zero(int[] ba, int offset, int length) {
        set(ba, offset, length, (int) 0);
    }
    public static void zero(long[]... arrays) {
        for(long[] array : arrays)
            set(array, 0, array.length, (long) 0);
    }
    public static void zero(long[] ba, int offset, int length) {
        set(ba, offset, length, (long) 0);
    }
    
    public static void set(boolean[] array, boolean value) {
        set(array, 0, array.length, value);
    }
    
    public static void set(byte[] array, byte value) {
        set(array, 0, array.length, value);
    }
    
    public static void set(short[] array, short value) {
        set(array, 0, array.length, value);
    }
    
    public static void set(char[] array, char value) {
        set(array, 0, array.length, value);
    }
    
    public static void set(int[] array, int value) {
        set(array, 0, array.length, value);
    }
    
    public static void set(long[] array, long value) {
        set(array, 0, array.length, value);
    }
    
    public static <T> void set(T[] array, T value) {
        set(array, 0, array.length, value);
    }
    
    public static void set(boolean[] ba, int offset, int length, boolean value) {
	for(int i = offset; i < length; ++i)
	    ba[i] = value;
    }
    
    public static void set(byte[] ba, int offset, int length, byte value) {
	for(int i = offset; i < length; ++i)
	    ba[i] = value;
    }
    
    public static void set(short[] ba, int offset, int length, short value) {
	for(int i = offset; i < length; ++i)
	    ba[i] = value;
    }
    
    public static void set(char[] ba, int offset, int length, char value) {
	for(int i = offset; i < length; ++i)
	    ba[i] = value;
    }
    
    public static void set(int[] ba, int offset, int length, int value) {
	for(int i = offset; i < length; ++i)
	    ba[i] = value;
    }
    
    public static void set(long[] ba, int offset, int length, long value) {
	for(int i = offset; i < length; ++i)
	    ba[i] = value;
    }

    public static <T> void set(T[] ba, int offset, int length, T value) {
	for(int i = offset; i < length; ++i)
	    ba[i] = value;
    }

    public static byte[] createCopy(byte[] data) {
	return createCopy(data, 0, data.length);
    }
    
    public static byte[] createCopy(byte[] data, int offset, int length) {
	byte[] copy = new byte[length];
	System.arraycopy(data, offset, copy, 0, length);
	return copy;
    }
    
    /** 
     * Creates a copy of the input data reversed byte by byte. This is helpful for endian swapping.
     * 
     * @param data
     * @return a copy of the input data reversed byte by byte.
     */     
    public static byte[] createReverseCopy(byte[] data) {
	return createReverseCopy(data, 0, data.length);
    }
    
    /**
     * Creates a copy of the input data reversed byte by byte. This is helpful for endian swapping.
     * 
     * @param data
     * @param offset
     * @param length
     * @return a copy of the input data reversed byte by byte.
     */
    public static byte[] createReverseCopy(byte[] data, int offset, int length) {
	byte[] copy = new byte[length];
	for(int i = 0; i < copy.length; ++i) {
            copy[i] = data[offset+(length-i-1)];
        } 
	return copy;
    }
    
    public static byte[] arrayCopy(byte[] source, byte[] dest) {
	return arrayCopy(source, dest, 0);
    }
    
    public static byte[] arrayCopy(byte[] source, byte[] dest, int destPos) {
	if(dest.length-destPos < source.length)
	    throw new RuntimeException("Destination array not large enough.");
	System.arraycopy(source, 0, dest, 0, source.length);
        return dest;
    }
    
    public static <T> T[] arrayCopy(T[] source, T[] dest) {
        return arrayCopy(source, dest, 0);
    }
    
    public static <T> T[] arrayCopy(T[] source, T[] dest, int destPos) {
        return arrayCopy(source, 0, dest, destPos, source.length);
    }
    
    public static <T> T[] arrayCopy(T[] source, int sourcePos, T[] dest, int destPos, int length) {
        if(source.length - sourcePos < length)
            throw new RuntimeException("Source array not large enough.");
        if(dest.length - destPos < length)
            throw new RuntimeException("Destination array not large enough.");
        System.arraycopy(source, sourcePos, dest, destPos, length);
        return dest;
    }

    public static boolean arraysEqual(boolean[] a, boolean[] b) {
	return arrayRegionsEqual(a, 0, a.length, b, 0, b.length);
    }
    public static boolean arrayRegionsEqual(boolean[] a, int aoff, int alen,
					    boolean[] b, int boff, int blen) {
	if(alen != blen)
	    return false;
	else {
	    for(int i = 0; i < alen; ++i)
		if(a[aoff+i] != b[boff+i])
		    return false;
	    return true;
	}
    }
    public static boolean arraysEqual(byte[] a, byte[] b) {
	return arrayRegionsEqual(a, 0, a.length, b, 0, b.length);
    }
    public static boolean arrayRegionsEqual(byte[] a, int aoff, int alen,
					    byte[] b, int boff, int blen) {
	if(a.length != blen)
	    return false;
	else {
	    for(int i = 0; i < alen; ++i)
		if(a[aoff+i] != b[boff+i])
		    return false;
	    return true;
	}
    }
    public static boolean arraysEqual(char[] a, char[] b) {
	return arrayRegionsEqual(a, 0, a.length, b, 0, b.length);
    }
    public static boolean arrayRegionsEqual(char[] a, int aoff, int alen,
					    char[] b, int boff, int blen) {
	if(alen != blen)
	    return false;
	else {
	    for(int i = 0; i < alen; ++i)
		if(a[aoff+i] != b[boff+i])
		    return false;
	    return true;
	}
    }
    public static boolean arraysEqual(short[] a, short[] b) {
	return arrayRegionsEqual(a, 0, a.length, b, 0, b.length);
    }
    public static boolean arrayRegionsEqual(short[] a, int aoff, int alen,
					    short[] b, int boff, int blen) {
	if(alen != blen)
	    return false;
	else {
	    for(int i = 0; i < alen; ++i)
		if(a[aoff+i] != b[boff+i])
		    return false;
	    return true;
	}
    }
    public static boolean arraysEqual(int[] a, int[] b) {
	return arrayRegionsEqual(a, 0, a.length, b, 0, b.length);
    }
    public static boolean arrayRegionsEqual(int[] a, int aoff, int alen,
					    int[] b, int boff, int blen) {
	if(alen != blen)
	    return false;
	else {
	    for(int i = 0; i < alen; ++i)
		if(a[aoff+i] != b[boff+i])
		    return false;
	    return true;
	}
    }
    public static boolean arraysEqual(long[] a, long[] b) {
	return arrayRegionsEqual(a, 0, a.length, b, 0, b.length);
    }
    public static boolean arrayRegionsEqual(long[] a, int aoff, int alen,
					    long[] b, int boff, int blen) {
	if(alen != blen)
	    return false;
	else {
	    for(int i = 0; i < alen; ++i)
		if(a[aoff+i] != b[boff+i])
		    return false;
	    return true;
	}
    }
    public static boolean arraysEqual(Object[] a, Object[] b) {
	return arrayRegionsEqual(a, 0, a.length, b, 0, b.length);
    }
    public static boolean arrayRegionsEqual(Object[] a, int aoff, int alen,
					    Object[] b, int boff, int blen) {
	if(alen != blen)
	    return false;
	else {
	    for(int i = 0; i < alen; ++i)
		if(!a[aoff+i].equals(b[boff+i]))
		    return false;
	    return true;
	}
    }
    
    public static long pow(long a, long b) {
	if(b < 0) throw new IllegalArgumentException("b can not be negative");
	
	long result = 1;
	for(long i = 0; i < b; ++i)
	    result *= a;
	return result;
    }

    public static int strlen(byte[] data) {
	int length = 0;
	for(byte b : data) {
	    if(b == 0)
		break;
	    else
		++length;
	}
	return length;
    }
    
    public static boolean getBit(long data, int bitNumber) {
	return ((data >>> bitNumber) & 0x1) == 0x1;
    }
    
    public static int arrayCompareLex(byte[] a, byte[] b) {
        return arrayCompareLex(a, 0, a.length, b, 0, b.length);
    }
    
    public static int arrayCompareLex(byte[] a, int aoff, int alen, byte[] b, int boff, int blen) {
        int compareLen = alen < blen ? alen : blen; // equiv. Math.min
	for(int i = 0; i < compareLen; ++i) {
	    byte curA = a[aoff+i];
	    byte curB = b[boff+i];
	    if(curA != curB)
		return curA - curB;
	}
	return alen-blen; // The shortest array gets higher priority
    }
    
    public static int unsignedArrayCompareLex(byte[] a, byte[] b) {
        return unsignedArrayCompareLex(a, 0, a.length, b, 0, b.length);
    }
    
    public static int unsignedArrayCompareLex(byte[] a, int aoff, int alen, byte[] b, int boff, int blen) {
        int compareLen = alen < blen ? alen : blen; // equiv. Math.min
	for(int i = 0; i < compareLen; ++i) {
	    int curA = a[aoff+i] & 0xFF;
	    int curB = b[boff+i] & 0xFF;
	    if(curA != curB)
		return curA - curB;
	}
	return alen-blen; // The shortest array gets higher priority
    }
    
    public static int unsignedArrayCompareLex(char[] a, char[] b) {
	return unsignedArrayCompareLex(a, 0, a.length, b, 0, b.length);
    }
    
    public static int unsignedArrayCompareLex(char[] a, int aoff, int alen, char[] b, int boff, int blen) {
	int compareLen = alen < blen ? alen : blen; // equiv. Math.min
	for(int i = 0; i < compareLen; ++i) {
	    int curA = a[aoff+i] & 0xFFFF; // Unsigned char values represented as int
	    int curB = b[boff+i] & 0xFFFF;
	    if(curA != curB)
		return curA - curB;
	}
	return alen-blen; // The shortest array gets higher priority
    }

    // All below is from Util2 (got tired of having two Util classes...)
    public static String toASCIIString(byte[] data) {
	return toASCIIString(data, 0, data.length);
    }
    public static String toASCIIString(byte[] data, int offset, int length) {
	return readString(data, offset, length, "US-ASCII");
    }
    public static String toASCIIString(short i) {
	return toASCIIString(Util.toByteArrayBE(i));
    }
    public static String toASCIIString(int i) {
	return toASCIIString(Util.toByteArrayBE(i));
    }
    public static String readString(byte[] data, String encoding) {
	return readString(data, 0, data.length, encoding);
    }
    public static String readString(byte[] data, int offset, int length, String encoding) {
	try {
	    return new String(data, offset, length, encoding);
	} catch(Exception e) {
	    return null;
	}
    }
    public static String readString(short i, String encoding) {
	return readString(Util.toByteArrayBE(i), encoding);
    }
    public static String readString(int i, String encoding) {
	return readString(Util.toByteArrayBE(i), encoding);
    }

    public static String readNullTerminatedASCIIString(byte[] data) {
	return readNullTerminatedASCIIString(data, 0, data.length);
    }
    
    public static String readNullTerminatedASCIIString(byte[] data, int offset, int maxLength) {
	int i;
	for(i = offset; i < (offset+maxLength); ++i)
	    if(data[i] == 0) break;
	return toASCIIString(data, offset, i-offset);
    }

    public static char readCharLE(byte[] data) {
	return readCharLE(data, 0);
    }
    public static char readCharLE(byte[] data, int offset) {
	return (char) ((data[offset+1] & 0xFF) << 8 |
		       (data[offset+0] & 0xFF) << 0);
    }
    public static char readCharBE(byte[] data) {
	return readCharBE(data, 0);
    }
    public static char readCharBE(byte[] data, int offset) {
	return (char) ((data[offset+0] & 0xFF) << 8 |
		       (data[offset+1] & 0xFF) << 0);
    }
    
    public static byte[] readByteArrayBE(byte[] b) {
	return createCopy(b);
    }
    public static char[] readCharArrayBE(byte[] b) {
	char[] result = new char[b.length/2];
	for(int i = 0; i < result.length; ++i)
	    result[i] = Util.readCharBE(b, i*2);
	return result;
    }
    public static short[] readShortArrayBE(byte[] b) {
	short[] result = new short[b.length/2];
	for(int i = 0; i < result.length; ++i)
	    result[i] = Util.readShortBE(b, i*2);
	return result;
    }
    public static int[] readIntArrayBE(byte[] b) {
	int[] result = new int[b.length/4];
	for(int i = 0; i < result.length; ++i)
	    result[i] = Util.readIntBE(b, i*4);
	return result;
    }
    public static long[] readLongArrayBE(byte[] b) {
	long[] result = new long[b.length/8];
	for(int i = 0; i < result.length; ++i)
	    result[i] = Util.readLongBE(b, i*8);
	return result;
    }
    
    public static byte[] readByteArrayLE(char[] data) {
	return readByteArrayLE(data, 0, data.length);
    }
    public static byte[] readByteArrayLE(char[] data, int offset, int size) {
	byte[] result = new byte[data.length*2];
	for(int i = 0; i < data.length; ++i) {
	    byte[] cur = toByteArrayLE(data[i]);
	    result[i*2] = cur[0];
	    result[i*2+1] = cur[1];
	}
	return result;
    }
    public static byte[] readByteArrayBE(char[] data) {
	return readByteArrayBE(data, 0, data.length);
    }
    public static byte[] readByteArrayBE(char[] data, int offset, int size) {
	byte[] result = new byte[data.length*2];
	for(int i = 0; i < data.length; ++i) {
	    byte[] cur = toByteArrayBE(data[i]);
	    result[i*2] = cur[0];
	    result[i*2+1] = cur[1];
	}
	return result;
    }

    public static byte[] fillBuffer(InputStream is, byte[] buffer) throws IOException {
	DataInputStream dis = new DataInputStream(is);
	dis.readFully(buffer);
	return buffer;
    }
    
    public static short unsign(byte b) {
	return (short)(b & 0xFF);
    }
    public static int unsign(short s) {
	return s & 0xFFFF;
    }
    public static int unsign(char s) {
	return s & 0xFFFF;
    }
    public static long unsign(int i) {
	return i & 0xFFFFFFFFL;
    }

    /**
     * Reads the supplied ReadableRandomAccessStream from its current position
     * until the end of the stream.
     *
     * @param s
     * @return the contents of the remainder of the stream.
     * @throws org.catacombae.io.RuntimeIOException if an I/O error occurred
     * when reading the stream.
     */
    public static byte[] readFully(ReadableRandomAccessStream s) throws RuntimeIOException {
        if(s.length() < 0 || s.length() > Integer.MAX_VALUE)
            throw new IllegalArgumentException("Length of s is out of range: " + s.length());

        byte[] res = new byte[(int)(s.length()-s.getFilePointer())];
        s.readFully(res);
        return res;
    }

    // Added 2007-06-24 for DMGExtractor
    public static String readFully(Reader r) throws IOException {
        StringBuilder sb = new StringBuilder();
        char[] temp = new char[512];
        long bytesRead = 0;
        int curBytesRead = r.read(temp, 0, temp.length);
        while(curBytesRead >= 0) {
            sb.append(temp, 0, curBytesRead);
            curBytesRead = r.read(temp, 0, temp.length);
        }
        return sb.toString();
    }

    // Added 2007-06-26 for DMGExtractor
    public static String[] concatenate(String[] a, String... b) {
        String[] c = new String[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }
    
    public static <T> T[] concatenate(T[] a, T[] b, T[] target) {
        System.arraycopy(a, 0, target, 0, a.length);
        System.arraycopy(b, 0, target, a.length, b.length);
        return target;
    }

    // From IRCForME
    public static byte[] encodeString(String string, String encoding) {
        try {
            return string.getBytes(encoding);
        } catch(Exception e) {
            return null;
        }
    }

    /**
     * Checks if the given <code>array</code> contains the specified <code>element</code> at least
     * once.
     * 
     * @param array the array to search.
     * @param element the element to look for.
     * @return true if <code>element</code> was present in <code>array</code>, and false otherwise.
     */
    public static boolean contains(int[] array, int element) {
        for(int i : array) {
            if(i == element)
                return true;
        }
        return false;

    }

    /**
     * Checks if the given list of arrays contains an array that is equal to <code>array</code> by
     * the definition of Arrays.equal(..) (both arrays must have the same number of elements, and
     * every pair of elements must be equal according to Object.equals).
     *
     * @param <A> the type of the array.
     * @param listOfArrays the list of arrays to search.
     * @param array the array to match.
     * @return <code>true</code> if an equal to <code>array</code> was found in
     * <code>listOfArrays</code>, otherwise <code>false</code>.
     */
    public static <A> boolean contains(List<A[]> listOfArrays, A[] array) {
        for(A[] curArray : listOfArrays) {
            if(Arrays.equals(curArray, array))
                return true;
        }
        return false;
    }

    /**
     * Concatenates the <code>strings</code> into one big string, putting <code>glueString</code>
     * between each pair. Example:
     * <code>concatenateStrings(new String[] {"joe", "lisa", "bob"}, " and ");</code> yields the
     * string "joe and lisa and bob".
     * 
     * @param strings
     * @param glueString
     * @return the input strings concatenated into one string, adding the <code>glueString</code>
     * between each pair.
     */
    public static String concatenateStrings(Object[] strings, String glueString) {
        if(strings.length > 0) {
            StringBuilder sb = new StringBuilder(strings[0].toString());
            for(int i = 1; i < strings.length; ++i)
                sb.append(glueString).append(strings[i].toString());
            return sb.toString();
        }
        else
            return "";
    }

    public static String concatenateStrings(List<? extends Object> strings, String glueString) {
        if(strings.size() > 0) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for(Object s : strings) {
                if(!first)
                    sb.append(glueString);
                else
                    first = false;
                sb.append(s.toString());
            }
            return sb.toString();
        }
        else
            return "";
    }
    
    public static String addUnitSpaces(String string, int unitSize) {
        int parts = string.length() / unitSize;
        StringBuilder sizeStringBuilder = new StringBuilder();
        String head = string.substring(0, string.length() - parts * unitSize);
        if(head.length() > 0)
            sizeStringBuilder.append(head);
        for(int i = parts - 1; i >= 0; --i) {
            if(i < parts-1 || (i == parts-1 && head.length() > 0))
                sizeStringBuilder.append(" ");
            sizeStringBuilder.append(string.substring(string.length() - (i + 1) * unitSize,
                    string.length() - i * unitSize));
        }
        return sizeStringBuilder.toString();
    }
    
    public static void buildStackTrace(Throwable t, int maxStackTraceLines, StringBuilder sb) {
        int stackTraceLineCount = 0;
        Throwable curThrowable = t;
        while(curThrowable != null && stackTraceLineCount < maxStackTraceLines) {
            sb.append(curThrowable.toString()).append("\n");
            ++stackTraceLineCount;
            for(StackTraceElement ste : curThrowable.getStackTrace()) {
                if(stackTraceLineCount < maxStackTraceLines) {
                    sb.append("        ").append(ste.toString()).append("\n");
                }
                ++stackTraceLineCount;
            }

            Throwable cause = curThrowable.getCause();
            if(cause != null) {
                if(stackTraceLineCount < maxStackTraceLines) {
                    sb.append("Caused by:\n");
                    ++stackTraceLineCount;
                }
            }
            curThrowable = cause;
        }
        
        if(stackTraceLineCount >= maxStackTraceLines)
            sb.append("...and ").append(stackTraceLineCount-maxStackTraceLines).append(" more.");
    }    
}
