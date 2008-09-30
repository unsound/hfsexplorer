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

package org.catacombae.csjc;

import org.catacombae.csjc.structelements.Signedness;
import org.catacombae.csjc.structelements.Endianness;
import org.catacombae.csjc.structelements.IntegerFieldBits;
import org.catacombae.csjc.structelements.Dictionary;
import org.catacombae.csjc.structelements.FieldType;

/**
 * Interface to implement when a struct wants to present detailed information on
 * its members to an external program, also allowing its members to be modified
 * externally in a controlled way.
 *
 * @see org.catacombae.csjc.structelements.StructElement
 * @author Erik Larsson
 */
public interface StructElements {
    // Some shorthand constants
    public static final Endianness BIG_ENDIAN = Endianness.BIG_ENDIAN;
    public static final Endianness LITTLE_ENDIAN = Endianness.LITTLE_ENDIAN;
    public static final Signedness SIGNED = Signedness.SIGNED;
    public static final Signedness UNSIGNED = Signedness.UNSIGNED;
    public static final FieldType BOOLEAN = FieldType.BOOLEAN;
    public static final FieldType INTEGER = FieldType.INTEGER;
    public static final FieldType BYTEARRAY = FieldType.BYTEARRAY;
    public static final FieldType ASCIISTRING = FieldType.ASCIISTRING;
    public static final FieldType CUSTOM_CHARSET_STRING = FieldType.CUSTOM_CHARSET_STRING;
    public static final FieldType DATE = FieldType.DATE;
    public static final IntegerFieldBits BITS_8 = IntegerFieldBits.BITS_8;
    public static final IntegerFieldBits BITS_16 = IntegerFieldBits.BITS_16;
    public static final IntegerFieldBits BITS_32 = IntegerFieldBits.BITS_32;
    public static final IntegerFieldBits BITS_64 = IntegerFieldBits.BITS_64;

    public Dictionary getStructElements();
}
