/*-
 * Copyright (C) 2006-2009 Erik Larsson
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

package org.catacombae.storage.fs.hfscommon;

import org.catacombae.io.AbstractFileStream;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.io.RuntimeIOException;
import org.catacombae.util.Util;

/**
 * This contains methods to detect if the file system is of type MFS, HFS, HFS+
 * or HFSX.
 *
 * @author <a href="http://www.catacombae.org/" target="_top">Erik Larsson</a>
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
            bitstream.seek(offset);
            byte[] signatureData = new byte[4096];
            int bytesRead = bitstream.read(signatureData);
            if(bytesRead < 4096) {
                return FileSystemType.UNKNOWN;
            }

            short signature = Util.readShortBE(signatureData, 1024);
            switch(signature) {
                case SIGNATURE_MFS:
                    return FileSystemType.MFS;
                case SIGNATURE_HFS:
                    short embeddedSignature =
                            Util.readShortBE(signatureData, 1024 + 124);
                    if(embeddedSignature == SIGNATURE_HFS_PLUS) {
                        return FileSystemType.HFS_WRAPPED_HFS_PLUS;
                    }
                    else {
                        return FileSystemType.HFS;
                    }
                case SIGNATURE_HFS_PLUS:
                    return FileSystemType.HFS_PLUS;
                case SIGNATURE_HFSX:
                    return FileSystemType.HFSX;
                default:
                    return FileSystemType.UNKNOWN;
            }
        } catch(RuntimeIOException e) {
            final String streamString =
                    !(bitstream instanceof AbstractFileStream) ? "" :
                    (" at " + ((AbstractFileStream) bitstream).getOpenPath());
            System.err.println("Error while detecting file system" +
                    streamString + ": " + e.getIOCause().getMessage());
            if(!(bitstream instanceof AbstractFileStream)) {
                e.printStackTrace();
            }

            return FileSystemType.UNKNOWN;
        } catch(Exception e) {
            final String streamString =
                    !(bitstream instanceof AbstractFileStream) ? "" :
                    (" at " + ((AbstractFileStream) bitstream).getOpenPath());
            System.err.println("Exception while detecting file system" +
                    streamString + ": " + e);
            e.printStackTrace();
            return FileSystemType.UNKNOWN;
        }
    }

    /**
     * Change this array to tell the recognizer which types the
     * FileSystemHandler supports.
     */
    public static final FileSystemType[] supportedTypes = {
        FileSystemType.HFS,
        FileSystemType.HFS_PLUS,
        FileSystemType.HFS_WRAPPED_HFS_PLUS,
        FileSystemType.HFSX,
    };

    public static boolean isTypeSupported(FileSystemType fst) {
        for(FileSystemType cur : supportedTypes)
            if(cur == fst)
                return true;
        return false;
    }
}
