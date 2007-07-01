/*-
 * Copyright (C) 2006-2007 Erik Larsson
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

/** This class just detects if the file system is of type HFS, HFS+ or HFSX. */
public class FileSystemRecognizer {
    private static final short SIGNATURE_MFS =      (short)0xD2D7; // Extreme legacy... won't be used
    private static final short SIGNATURE_HFS =      (short)0x4244; // ASCII: 'BD' Legacy...
    private static final short SIGNATURE_HFS_PLUS = (short)0x482B; // ASCII: 'H+'
    private static final short SIGNATURE_HFSX =     (short)0x4858; // ASCII: 'HX'
    
    public static enum FileSystemType { MFS, HFS, HFS_PLUS, HFS_WRAPPED_HFS_PLUS, HFSX, UNKNOWN };
    
    private LowLevelFile bitstream;
    private long offset;
    public FileSystemRecognizer(LowLevelFile bitstream, long offset) {
	this.bitstream = bitstream;
	this.offset = offset;
    }
    
    public FileSystemType detectFileSystem() {
	bitstream.seek(offset+1024);
	byte[] signatureData = new byte[2];
	bitstream.readFully(signatureData);
	short signature = Util.readShortBE(signatureData);
	switch(signature) {
	case SIGNATURE_MFS: return FileSystemType.MFS;
	case SIGNATURE_HFS:
	    bitstream.seek(offset+1024+124);
	    bitstream.readFully(signatureData);
	    short embeddedSignature = Util.readShortBE(signatureData);
	    if(embeddedSignature == SIGNATURE_HFS_PLUS)
		return FileSystemType.HFS_WRAPPED_HFS_PLUS;
	    else
		return FileSystemType.HFS;
	case SIGNATURE_HFS_PLUS: return FileSystemType.HFS_PLUS;
	case SIGNATURE_HFSX: return FileSystemType.HFSX;
	default: return FileSystemType.UNKNOWN;
	}
    }
}
