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
 * @author <a href="https://catacombae.org" target="_top">Erik Larsson</a>
 */
public class BlockList implements DynamicStruct, PrintableStruct {
    public final BlockListHeader header;
    public final BlockInfo[] binfo;
    public final byte[] reserved;
    public final byte[][] bdata;

    public BlockList(BlockListHeader header, BlockInfo[] binfo, byte[] reserved,
            byte[][] data)
    {
        this.header = header;
        this.binfo = binfo;
        this.reserved = reserved;
        this.bdata = data;
    }

    public BlockList(byte[] data, int offset, int blockListHeaderSize,
            boolean littleEndian)
    {
        int curOffset = offset;

        this.header = new BlockListHeader(data, curOffset, littleEndian);
        curOffset += this.header.size();

        this.binfo = new BlockInfo[this.header.getNumBlocks()];
        for(int i = 0; i < binfo.length; ++i) {
            this.binfo[i] = new BlockInfo(data, curOffset, littleEndian);
            curOffset += this.binfo[i].size();
        }

        this.reserved = new byte[blockListHeaderSize - (curOffset - offset)];
        System.arraycopy(data, curOffset, this.reserved, 0,
                this.reserved.length);

        this.bdata = new byte[this.header.getNumBlocks()][];
        for(int i = 0; i < binfo.length; ++i) {
            final int bsize = binfo[i].getRawBsize();
            if(bsize < 0) {
                throw new RuntimeException("'int' overflow in 'bsize' (" +
                            bsize + ").");
            }

            this.bdata[i] = new byte[bsize];
            System.arraycopy(data, curOffset, this.bdata[i], 0, bsize);
            curOffset += bsize;
        }
    }

    public int maxSize() {
        return Integer.MAX_VALUE;
    }

    public int occupiedSize() {
        int occupiedSize =
                BlockListHeader.length() + binfo.length * BlockInfo.length() +
                reserved.length;

        for(byte[] curData : bdata) {
            occupiedSize += curData.length;
        }

        return occupiedSize;
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
        ps.println(prefix + " reserved: { ... [length=" + reserved.length +
                "] }");
        ps.println(prefix + " bdata: ");
        for(int i = 0; i < bdata.length; ++i) {
            ps.println(prefix + "  [" + i + "]: ");
            ps.println(prefix + "   { ... [length=" + bdata[i].length + "] }");
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
        System.arraycopy(reserved, 0, result, offset, reserved.length);
        offset += reserved.length;
        for(byte[] data : bdata) {
            System.arraycopy(data, 0, result, offset, data.length);
            offset += data.length;
        }

        return offset - originalOffset;
    }
}
