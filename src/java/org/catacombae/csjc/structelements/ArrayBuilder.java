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

import java.util.LinkedList;
import static org.catacombae.csjc.structelements.IntegerFieldBits.*;
import static org.catacombae.csjc.structelements.Signedness.*;
import static org.catacombae.csjc.structelements.Endianness.*;

/**
 * @author <a href="http://hem.bredband.net/catacombae">Erik Larsson</a>
 */
public class ArrayBuilder {

    private final String typeName;
    private final LinkedList<StructElement> elements = new LinkedList<StructElement>();

    public ArrayBuilder(String typeName) {
        super();
        this.typeName = typeName;
    }

    public void add(StructElement... elements) {
        for(StructElement element : elements)
            this.elements.add(element);
    }

    /*
     * Only braindead convenience methods below!
     */
    public void addSIntBE(byte[] data) {
        addSIntBE(data, 0, data.length);
    }

    public void addSIntLE(byte[] data) {
        addSIntLE(data, 0, data.length);
    }

    public void addUIntBE(byte[] data) {
        addUIntBE(data, 0, data.length);
    }

    public void addUIntLE(byte[] data) {
        addUIntLE(data, 0, data.length);
    }

    public void addSIntBE(byte[] data, int offset, int length) {
        addInt(data, offset, length, SIGNED, BIG_ENDIAN);
    }

    public void addSIntLE(byte[] data, int offset, int length) {
        addInt(data, offset, length, SIGNED, LITTLE_ENDIAN);
    }

    public void addUIntBE(byte[] data, int offset, int length) {
        addInt(data, offset, length, UNSIGNED, BIG_ENDIAN);
    }

    public void addUIntLE(byte[] data, int offset, int length) {
        addInt(data, offset, length, UNSIGNED, LITTLE_ENDIAN);
    }

    private void addInt(byte[] data, int offset, int length, Signedness signedness, Endianness endianness) {
        switch(length) {
            case 1:
                add(new IntegerField(data, offset, BITS_8, signedness, endianness));
                break;
            case 2:
                add(new IntegerField(data, offset, BITS_16, signedness, endianness));
                break;
            case 4:
                add(new IntegerField(data, offset, BITS_32, signedness, endianness));
                break;
            case 8:
                add(new IntegerField(data, offset, BITS_64, signedness, endianness));
                break;
            default:
                throw new IllegalArgumentException("You supplied a " + (length * 8) + "-bit value. Only 64, 32, 16 and 8-bit values are supported.");
        }
    }

    public Array getResult() {
        return new Array(typeName, elements.toArray(new StructElement[elements.size()]));
    }
}
