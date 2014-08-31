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

import java.lang.reflect.Field;
import org.catacombae.util.Util;

/**
 *
 * @author erik
 */
class IntegerFieldDataHandle implements DataHandle {

    private final Field field;
    private final Object object;
    private final int length;

    public IntegerFieldDataHandle(Object object, Field field, int length) {

        switch(length) {
            case 1:
            case 2:
            case 4:
            case 8:
                break;
            default:
                throw new IllegalArgumentException("Invalid length: " + length);
        }

        this.object = object;
        this.field = field;
        this.length = length;
    }

    public byte[] getBytesAsCopy() {
        try {
            byte[] res;

            switch(length) {
                case 1:
                    res = Util.toByteArrayBE(field.getByte(object));
                    break;
                case 2:
                    res = Util.toByteArrayBE(field.getShort(object));
                    break;
                case 4:
                    res = Util.toByteArrayBE(field.getInt(object));
                    break;
                case 8:
                    res = Util.toByteArrayBE(field.getLong(object));
                    break;
                default:
                    throw new RuntimeException(); // Won't happen.
            }

            return res;
        } catch(IllegalAccessException e) {
            throw new RuntimeException("Illegal access while trying to " +
                    "read field: [" + field, e);
        }
    }

    public byte[] getBytesAsCopy(int offset, int length) {
        return Util.createCopy(getBytesAsCopy(), offset, length);
    }

    public int getLength() {
        return length;
    }
}
