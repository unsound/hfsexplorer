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

package org.catacombae.hfs.types.hfsplus;

import org.catacombae.util.Util;
import java.io.PrintStream;
import org.catacombae.csjc.PrintableStruct;
import org.catacombae.csjc.StructElements;
import org.catacombae.csjc.structelements.Dictionary;

public class HFSPlusExtentDescriptor implements StructElements,
        PrintableStruct {
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
        System.arraycopy(data, offset + 4, blockCount, 0, 4);
    }

    public HFSPlusExtentDescriptor(int startBlock, int blockCount) {
        System.arraycopy(Util.toByteArrayBE(startBlock), 0, this.startBlock, 0, 4);
        System.arraycopy(Util.toByteArrayBE(blockCount), 0, this.blockCount, 0, 4);
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
        ps.println(prefix + getClass().getSimpleName());
        printFields(ps, prefix);
    }

    public void printFields(PrintStream ps, String prefix) {
        ps.println(prefix + " startBlock: " + getStartBlock());
        ps.println(prefix + " blockCount: " + getBlockCount());
    }

    byte[] getBytes() {
        byte[] result = new byte[getSize()];
        int offset = 0;

        System.arraycopy(startBlock, 0, result, offset, startBlock.length); offset += startBlock.length;
        System.arraycopy(blockCount, 0, result, offset, blockCount.length); offset += blockCount.length;

        return result;
    }

    public Dictionary getStructElements() {
        DictionaryBuilder sb = new DictionaryBuilder(HFSPlusExtentDescriptor.class.getSimpleName());

        sb.addUIntBE("startBlock", startBlock, "Start block");
        sb.addUIntBE("blockCount", blockCount, "Block count");

        return sb.getResult();
    }

    private void _setStartBlock(int startBlock) {
        Util.arrayPutBE(this.startBlock, 0, (int) startBlock);
    }

    private void _setBlockCount(int blockCount) {
        Util.arrayPutBE(this.blockCount, 0, (int) blockCount);
    }

    private void _set(HFSPlusExtentDescriptor desc) {
        Util.arrayCopy(desc.startBlock, this.startBlock);
        Util.arrayCopy(desc.blockCount, this.blockCount);
    }

    public static class Mutable extends HFSPlusExtentDescriptor {
        public Mutable(byte[] data, int offset) {
            super(data, offset);
        }

        public Mutable(int startBlock, int blockCount) {
            super(startBlock, blockCount);
        }

        public void set(HFSPlusExtentDescriptor desc) {
            super._set(desc);
        }

        public void setStartBlock(int startBlock) {
            super._setStartBlock(startBlock);
        }

        public void setBlockCount(int blockCount) {
            super._setBlockCount(blockCount);
        }
    }
}

/* Maxium file size in HFS+ has got to be blockSize*2^32*8.
 * I.e. for block size 4096:
 * 140737488355328 B
 * 137438953472 KiB
 * 134217728 MiB
 * 131072 GiB
 * 128 TiB
 *
 * ...and for block size 32768:
 * 1125899906842624 B
 * 1099511627776 KiB
 * 1073741824 MiB
 * 1048576 GiB
 * 1024 TiB
 * 1 PiB
 */
