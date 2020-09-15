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

import java.io.PrintStream;
import org.catacombae.csjc.PrintableStruct;
import org.catacombae.util.Util;

/**
 * @author <a href="https://catacombae.org" target="_top">Erik Larsson</a>
 */
public class BTIndexRecord implements PrintableStruct {
    private final BTKey key;
    private final byte[] index = new byte[4];

    public BTIndexRecord(BTKey key, byte[] data, int offset) {
	this.key = key;
	System.arraycopy(data, offset+key.length(), index, 0, 4);
    }

    public BTKey getKey() { return key; }
    public int getIndex() { return Util.readIntBE(index); }

    public void printFields(PrintStream ps, String prefix) {
        ps.println(prefix + " key:");
        key.print(ps, prefix + "  ");
        ps.println(prefix + " index: " + Util.unsign(getIndex()));
    }

    public void print(PrintStream ps, String prefix) {
        ps.println(prefix + "BTIndexRecord:");
        printFields(ps, prefix);
    }
}
