/*-
 * Copyright (C) 2011-2012 Erik Larsson
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

package org.catacombae.storage.ps.gpt.types;

/**
 * @author <a href="http://www.catacombae.org/" target="_top">Erik Larsson</a>
 */
public class MutableGUID extends GUID {
    public MutableGUID() {}
    public MutableGUID(GUID guid) {
        super(guid);
    }

    public void setBytes(byte[] data, int offset) {
        if(data.length - offset < length())
            throw new IllegalArgumentException("Not enough data (need > 16, " +
                    "got " + (data.length - offset) + ").");

        copyBytes(data, 0);
    }
}
