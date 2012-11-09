/*-
 * Copyright (C) 2012 Erik Larsson
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

import java.io.PrintStream;
import org.catacombae.csjc.DynamicStruct;
import org.catacombae.csjc.PrintableStruct;

/**
 *
 * @author Erik Larsson
 */
public class BlockList implements DynamicStruct, PrintableStruct {
    public final BlockListHeader header;
    public final BlockInfo[] binfo;

    public BlockList(BlockListHeader header, BlockInfo[] binfo) {
        this.header = header;
        this.binfo = binfo;
    }

    public BlockList(byte[] data, int offset, boolean littleEndian) {
        this.header = new BlockListHeader(data, offset, littleEndian);
        this.binfo = new BlockInfo[this.header.getNumBlocks() + 1];
        for(int i = 0; i < binfo.length; ++i) {
            this.binfo[i] = new BlockInfo(data,
                    offset+16 + i*BlockInfo.length(), littleEndian);
        }
    }

    public int maxSize() {
        return BlockListHeader.length() + 65535 * BlockInfo.length();
    }

    public int occupiedSize() {
        return BlockListHeader.length() + binfo.length * BlockInfo.length();
    }

    public BlockListHeader getHeader() { return header; }

    public int getBlockInfoCount() { return binfo.length; }
    public BlockInfo getBlockInfo(int index) { return binfo[index]; }

    public void printFields(PrintStream ps, String prefix) {
        ps.println(prefix + " header: ");
        header.print(ps, prefix + "  ");
        ps.println(prefix + " binfo: ");
        for(int i = 0; i < binfo.length; ++i) {
            ps.println(prefix + "  [" + i + "]: ");
            binfo[i].print(ps, prefix + "   ");
        }
    }

    public void print(PrintStream ps, String prefix) {
        ps.println(prefix + "BlockList:");
        printFields(ps, prefix);
    }

    public byte[] getBytes() {
        byte[] result = new byte[occupiedSize()];
        getBytes(result, 0);
        return result;
    }

    public int getBytes(byte[] result, int offset) {
        final int originalOffset = offset;

        offset += header.getBytes(result, offset);
        for(BlockInfo bi : binfo) {
            offset += bi.getBytes(result, offset);
        }

        return offset - originalOffset;
    }
}
