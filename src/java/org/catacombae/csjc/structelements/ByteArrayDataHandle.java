/*-
 * Copyright (C) 2009 Erik Larsson
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

package org.catacombae.csjc.structelements;

import org.catacombae.util.Util;

/**
 * @author <a href="http://www.catacombae.org/" target="_top">Erik Larsson</a>
 */
class ByteArrayDataHandle implements DataHandle {

    private byte[] data;

    public ByteArrayDataHandle(byte[] data) {
        this.data = data;
    }

    public byte[] getBytesAsCopy() {
        return getBytesAsCopy(0, data.length);
    }

    public byte[] getBytesAsCopy(int offset, int length) {
        return Util.createCopy(data, offset, length);
    }

    public int getLength() {
        return data.length;
    }
}
