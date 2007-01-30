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

public class Util2 {
    public static String toASCIIString(byte[] data) {
	return toASCIIString(data, 0, data.length);
    }
    public static String toASCIIString(byte[] data, int offset, int length) {
	try {
	    return new String(data, offset, length, "US-ASCII");
	} catch(Exception e) {
	    return null;
	}
    }
    public static String toASCIIString(short i) {
	return toASCIIString(Util.toByteArrayBE(i));
    }
    public static String toASCIIString(int i) {
	return toASCIIString(Util.toByteArrayBE(i));
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
    
    public static byte[] toByteArrayLE(char c) {
	byte[] result = new byte[2];
	result[0] = (byte) ((c >> 0) & 0xFF);
	result[1] = (byte) ((c >> 8) & 0xFF);
	return result;
    }
    public static byte[] toByteArrayBE(char c) {
	byte[] result = new byte[2];
	result[0] = (byte) ((c >> 8) & 0xFF);
	result[1] = (byte) ((c >> 0) & 0xFF);
	return result;
    }

    public static char[] readCharArrayBE(byte[] b) {
	char[] result = new char[b.length/2];
	for(int i = 0; i < result.length; ++i)
	    result[i] = Util2.readCharBE(b, i*2);
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
}
