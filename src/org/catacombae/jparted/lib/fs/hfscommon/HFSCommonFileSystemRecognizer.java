/*-
 * Copyright (C) 2006-2008 Erik Larsson
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

package org.catacombae.jparted.lib.fs.hfscommon;

import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.util.Util;

/**
 * This contains methods to detect if the file system is of type MFS, HFS, HFS+
 * or HFSX.
 */
public class HFSCommonFileSystemRecognizer {

    private static final short SIGNATURE_MFS = (short) 0xD2D7; // Extreme legacy... won't be used
    private static final short SIGNATURE_HFS = (short) 0x4244; // ASCII: 'BD' Legacy...
    private static final short SIGNATURE_HFS_PLUS = (short) 0x482B; // ASCII: 'H+'
    private static final short SIGNATURE_HFSX = (short) 0x4858; // ASCII: 'HX'

    public static enum FileSystemType {
        MFS, HFS, HFS_PLUS, HFS_WRAPPED_HFS_PLUS, HFSX, UNKNOWN
    };

    /**
     * Detects one of the following file systems:
     * <ul>
     * <li>HFS+</li>
     * <li>HFSX</li>
     * <li>HFS</li>
     * <li>HFS+ wrapped inside a HFS file system</li>
     * <li>MFS</li>
     * </ul>
     * NOTE: This method should never ever throw an exception, and instead just returns UNKNOWN.
     *
     * @param bitstream the stream to check for a file system.
     * @param offset the offset in the stream to the start of the file system.
     * @return the detected file system type (UNKNOWN if none could be detected).
     */
    public static FileSystemType detectFileSystem(ReadableRandomAccessStream bitstream, long offset) {
        try {
            bitstream.seek(offset + 1024);
            byte[] signatureData = new byte[2];
            bitstream.readFully(signatureData);
            short signature = Util.readShortBE(signatureData);
            switch(signature) {
                case SIGNATURE_MFS:
                    return FileSystemType.MFS;
                case SIGNATURE_HFS:
                    try {
                        bitstream.seek(offset + 1024 + 124);
                        bitstream.readFully(signatureData);
                        short embeddedSignature = Util.readShortBE(signatureData);
                        if(embeddedSignature == SIGNATURE_HFS_PLUS)
                            return FileSystemType.HFS_WRAPPED_HFS_PLUS;
                        else
                            return FileSystemType.HFS;
                    } catch(Exception e) {
                        return FileSystemType.HFS;
                    }
                case SIGNATURE_HFS_PLUS:
                    return FileSystemType.HFS_PLUS;
                case SIGNATURE_HFSX:
                    return FileSystemType.HFSX;
                default:
                    return FileSystemType.UNKNOWN;
            }
        } catch(Exception e) {
            System.err.println("Exception while detecting file system:");
            e.printStackTrace();
            return FileSystemType.UNKNOWN;
        }
    }
}
