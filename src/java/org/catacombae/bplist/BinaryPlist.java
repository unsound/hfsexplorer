/*-
 * Copyright (C) 2021 Erik Larsson
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

package org.catacombae.bplist;

import org.catacombae.bplist.types.BinaryPlistFooter;
import org.catacombae.bplist.types.BinaryPlistHeader;
import org.catacombae.util.Util;

/**
 * Class wrapping binary plist data for easier access to the structured data
 * inside it.
 *
 * @author Erik Larsson
 */
public class BinaryPlist {
    private final BinaryPlistHeader header;
    private final byte[] offsetMap;
    private final BinaryPlistFooter footer;

    public BinaryPlist(byte[] data, int offset, int size) {
        if(size < 8 + 32) {
            throw new RuntimeException("Buffer is too small for binary plist " +
                    "header.");
        }

        this.header = new BinaryPlistHeader(data, offset);

        if(!Util.toASCIIString(this.header.getSignature()).equals("bplist")) {
            throw new RuntimeException("Signature mismatch for binary plist.");
        }
        else if(!Util.toASCIIString(this.header.getVersion()).equals("00")) {
            throw new RuntimeException("Unsupported binary plist version: " +
                    Util.toASCIIString(this.header.getVersion()));
        }

        this.footer =
                new BinaryPlistFooter(data, offset + size -
                BinaryPlistFooter.STRUCTSIZE);

        if(footer.getRawOffsetTableStart() > Integer.MAX_VALUE) {
            throw new RuntimeException("Unreasonably large or unsupported " +
                    "offset table start: " + footer.getRawOffsetTableStart());
        }

        /* Read the offset map. */
        this.offsetMap =
                new byte[size - 32 - (int) footer.getRawOffsetTableStart()];
        Util.arrayCopy(data, offset + (int) footer.getRawOffsetTableStart(),
                this.offsetMap, 0, this.offsetMap.length);
    }

    public final BinaryPlistHeader getHeader() {
        return header;
    }

    public final BinaryPlistFooter getFooter() {
        return footer;
    }

    public final int getOffsetMapping(int source) {
        int sourceOffset = source * footer.getOffsetTableOffsetSize();
        int result = 0;

        for(int i = 0; i < footer.getOffsetTableOffsetSize(); ++i) {
            result = (result << 8) | (this.offsetMap[sourceOffset + i] & 0xFF);
        }

        return result;
    }
}
