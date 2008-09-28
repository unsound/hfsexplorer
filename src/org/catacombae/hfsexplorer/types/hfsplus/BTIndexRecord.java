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

package org.catacombae.hfsexplorer.types.hfsplus;

import org.catacombae.hfsexplorer.Util;

public class BTIndexRecord {
    private final BTKey key;
    private final byte[] index = new byte[4];

    public BTIndexRecord(BTKey key, byte[] data, int offset) {
	this.key = key;
	System.arraycopy(data, offset+key.length(), index, 0, 4);
    }
    
    public BTKey getKey() { return key; }
    public int getIndex() { return Util.readIntBE(index); }
}
