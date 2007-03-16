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

package org.catacombae.hfsexplorer;

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
    public static String toHexStringBE(short[] array) {
	return toHexStringBE(array, 0, array.length);
    }
    public static String toHexStringBE(short[] array, int offset, int length) {
	StringBuilder result = new StringBuilder();
	for(short s : array)
	    result.append(toHexStringBE(s));
	return result.toString();
    }
    public static String toHexStringBE(int[] array) {
	return toHexStringBE(array, 0, array.length);
    }
    public static String toHexStringBE(int[] array, int offset, int length) {
	StringBuilder result = new StringBuilder();
	for(int i : array)
	    result.append(toHexStringBE(i));
	return result.toString();
    }
    
    public static String toHexStringLE(byte n) { return byteArrayToHexString(toByteArrayLE(n)); }
    public static String toHexStringLE(short n) { return byteArrayToHexString(toByteArrayLE(n)); }
    public static String toHexStringLE(int n) { return byteArrayToHexString(toByteArrayLE(n)); }
    public static String toHexStringLE(long n) { return byteArrayToHexString(toByteArrayLE(n)); }
    public static String toHexStringBE(byte n) { return byteArrayToHexString(toByteArrayBE(n)); }
    public static String toHexStringBE(short n) { return byteArrayToHexString(toByteArrayBE(n)); }
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
    
    public static void zero(byte[] ba) {
	set(ba, 0, ba.length, (byte)0);
    }
    public static void zero(byte[] ba, int offset, int length) {
	set(ba, offset, length, (byte)0);
    }
    public static void zero(short[] ba) {
	set(ba, 0, ba.length, (short)0);
    }
    public static void zero(short[] ba, int offset, int length) {
	set(ba, offset, length, (short)0);
    }
    public static void zero(int[] ba) {
	set(ba, 0, ba.length, (int)0);
    }
    public static void zero(int[] ba, int offset, int length) {
	set(ba, offset, length, (int)0);
    }
    public static void zero(long[] ba) {
	set(ba, 0, ba.length, (long)0);
    }
    public static void zero(long[] ba, int offset, int length) {
	set(ba, offset, length, (long)0);
    }
    
    public static void set(byte[] ba, int offset, int length, byte value) {
	for(int i = offset; i < length; ++i)
	    ba[i] = value;
    }
    public static void set(short[] ba, int offset, int length, short value) {
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

    public static byte[] createCopy(byte[] data) {
	return createCopy(data, 0, data.length);
    }
    
    public static byte[] createCopy(byte[] data, int offset, int length) {
	byte[] copy = new byte[length];
	System.arraycopy(data, offset, copy, 0, length);
	return copy;
    }

    public static boolean arraysEqual(boolean[] a, boolean[] b) {
	if(a.length != b.length)
	    return false;
	else {
	    for(int i = 0; i < a.length; ++i)
		if(a[i] != b[i])
		    return false;
	    return true;
	}
    }
    public static boolean arraysEqual(byte[] a, byte[] b) {
	if(a.length != b.length)
	    return false;
	else {
	    for(int i = 0; i < a.length; ++i)
		if(a[i] != b[i])
		    return false;
	    return true;
	}
    }
    public static boolean arraysEqual(char[] a, char[] b) {
	if(a.length != b.length)
	    return false;
	else {
	    for(int i = 0; i < a.length; ++i)
		if(a[i] != b[i])
		    return false;
	    return true;
	}
    }
    public static boolean arraysEqual(short[] a, short[] b) {
	if(a.length != b.length)
	    return false;
	else {
	    for(int i = 0; i < a.length; ++i)
		if(a[i] != b[i])
		    return false;
	    return true;
	}
    }
    public static boolean arraysEqual(int[] a, int[] b) {
	if(a.length != b.length)
	    return false;
	else {
	    for(int i = 0; i < a.length; ++i)
		if(a[i] != b[i])
		    return false;
	    return true;
	}
    }
    public static boolean arraysEqual(long[] a, long[] b) {
	if(a.length != b.length)
	    return false;
	else {
	    for(int i = 0; i < a.length; ++i)
		if(a[i] != b[i])
		    return false;
	    return true;
	}
    }
    public static boolean arraysEqual(Object[] a, Object[] b) {
	if(a.length != b.length)
	    return false;
	else {
	    for(int i = 0; i < a.length; ++i)
		if(!a[i].equals(b[i]))
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
}
