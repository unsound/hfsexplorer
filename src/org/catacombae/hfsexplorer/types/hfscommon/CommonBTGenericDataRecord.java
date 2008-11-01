/*-
 * Copyright (C) 2008 Erik Larsson
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

package org.catacombae.hfsexplorer.types.hfscommon;

import java.io.PrintStream;
import org.catacombae.hfsexplorer.Util;

/**
 * Record with unspecified data.
 *
 * @author erik
 */
public class CommonBTGenericDataRecord extends CommonBTRecord {
    private final byte[] data;

    public CommonBTGenericDataRecord(byte[] data, int offset, int length) {
        this.data = new byte[length];
        System.arraycopy(data, offset, this.data, 0, length);
    }

    @Override
    public int getSize() {
        return data.length;
    }

    @Override
    public byte[] getBytes() {
        return Util.createCopy(data);
    }

    public void print(PrintStream ps, String prefix) {
        ps.println(prefix + "CommonBTGenericDataRecord:");
        printFields(ps, prefix + " ");
    }

    public void printFields(PrintStream ps, String prefix) {
        ps.println(prefix + "data: byte[" + data.length + "] (" + data + ")");
    }

}
