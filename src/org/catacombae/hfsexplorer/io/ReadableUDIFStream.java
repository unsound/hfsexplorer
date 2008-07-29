/*-
 * Copyright (C) 2006-2007 Erik Larsson
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

package org.catacombae.hfsexplorer.io;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import org.catacombae.io.ReadableFileStream;
import org.catacombae.io.ReadableFilterStream;
import org.catacombae.udif.UDIFFile;
import org.catacombae.udif.UDIFRandomAccessStream;

/**
 * This class acts as the bridge between the libraries of DMGExtractor and
 * HFSExplorer.
 */
public class ReadableUDIFStream extends ReadableFilterStream {
    public ReadableUDIFStream(String filename) throws FileNotFoundException {
        super(new UDIFRandomAccessStream(new UDIFFile(new ReadableFileStream(new RandomAccessFile(filename, "r")))));
    }
}
