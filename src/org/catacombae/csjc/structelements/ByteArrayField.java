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

package org.catacombae.csjc.structelements;

import org.catacombae.hfsexplorer.Util;

/**
 * @author <a href="http://hem.bredband.net/catacombae">Erik Larsson</a>
 */
public class ByteArrayField extends StringRepresentableField {

    private final byte[] fieldData;
    private final int offset;
    private final int length;

    public ByteArrayField(byte[] fieldData) {
        this(fieldData, 0, fieldData.length);
    }

    public ByteArrayField(byte[] fieldData, int offset, int length) {
        super("Byte[" + fieldData.length + "]", FieldType.BYTEARRAY);
        this.fieldData = fieldData;
        this.offset = offset;
        this.length = length;
    }

    @Override
    public String getValueAsString() {
        return "0x" + Util.byteArrayToHexString(fieldData, offset, length);
    }

    @Override
    public void setStringValue(String value) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Can\'t set byte string to string value at this point.");
    }

    @Override
    public String validateStringValue(String s) {
        return "Can\'t set a byte string to a string value.";
    }
}
