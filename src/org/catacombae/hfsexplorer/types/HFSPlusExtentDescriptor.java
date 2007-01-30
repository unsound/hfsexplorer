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

package org.catacombae.hfsexplorer.types;

import org.catacombae.hfsexplorer.Util;
import org.catacombae.hfsexplorer.Util2;
import java.io.PrintStream;

public class HFSPlusExtentDescriptor {
    /*
     * struct HFSPlusExtentDescriptor
     * size: 8 bytes
     *
     * BP   Size  Type              Variable name   Description
     * --------------------------------------------------------------
     * 0    4     UInt32            startBlock
     * 4    4     UInt32            blockCount
     */
	
    private final byte[] startBlock = new byte[4]; // UInt32
    private final byte[] blockCount = new byte[4]; // UInt32

    public HFSPlusExtentDescriptor(byte[] data, int offset) {
	System.arraycopy(data, offset, startBlock, 0, 4);
	System.arraycopy(data, offset+4, blockCount, 0, 4);
    }
	
    public static int getSize() {
	return 8;
    }
	
    public int getStartBlock() { return Util.readIntBE(startBlock); }
    public int getBlockCount() { return Util.readIntBE(blockCount); }
	
    public void print(PrintStream ps, int pregap) {
	String pregapString = "";
	for(int i = 0; i < pregap; ++i)
	    pregapString += " ";
	print(ps, pregapString);
    }
    public void print(PrintStream ps, String prefix) {
	ps.println(prefix + "startBlock: " + getStartBlock());
	ps.println(prefix + "blockCount: " + getBlockCount());
    }
}

/* Maximal filstorlek i HFS+ måste vara blockSize*2^32*8. Dvs. vid blockSize = 4096:
 * 140737488355328 B
 * 137438953472 KiB
 * 134217728 MiB
 * 131072 GiB
 * 128 TiB
 *
 * vid blocksize 32768:
 * 1125899906842624 B
 * 1099511627776 KiB
 * 1073741824 MiB
 * 1048576 GiB
 * 1024 TiB
 * 1 PiB
 */
