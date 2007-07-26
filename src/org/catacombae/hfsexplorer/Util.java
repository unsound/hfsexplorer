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
import java.io.*;

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
    
    public static void arrayCopy(byte[] source, byte[] dest, int destPos) {
	if(dest.length-destPos < source.length)
	    throw new RuntimeException("Destination array not large enough.");
	System.arraycopy(source, 0, dest, 0, source.length);
    }
    public static void arrayCopy(byte[] source, byte[] dest) {
	arrayCopy(source, dest, 0);
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
    
    public static int unsign(byte b) {
	return b & 0xFF;
    }
    public static int unsign(short s) {
	return s & 0xFFFF;
    }
    public static long unsign(int i) {
	return i & 0xFFFFFFFFL;
    }
    
    // End of Util2 stuff
}
