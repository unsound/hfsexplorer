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

import java.lang.reflect.Field;
import org.catacombae.csjc.StaticStruct;
import org.catacombae.csjc.StructElements;
import org.catacombae.csjc.structelements.Dictionary;
import org.catacombae.csjc.structelements.IntegerFieldRepresentation;
import org.catacombae.util.Util;

/**
 * @author <a href="http://www.catacombae.org/" target="_top">Erik Larsson</a>
 */
public class GUID implements StaticStruct, StructElements {
    private int part1;
    private short part2;
    private short part3;
    private long part4;

    public GUID(byte[] data, int offset) {
        if(data.length - offset < 16)
            throw new IllegalArgumentException("Not enough bytes for a " +
                    "GUID (need 16, got " + (data.length - offset) + ").");

        copyBytesInternal(data, offset);
    }

    public GUID(GUID source) {
        copyFieldsInternal(source);
    }

    public GUID(byte[] data) {
        this(data, 0);
    }

    protected GUID() {
        this.part1 = 0;
        this.part2 = 0;
        this.part3 = 0;
        this.part4 = 0;
    }

    private void copyFieldsInternal(GUID source) {
        this.part1 = source.part1;
        this.part2 = source.part2;
        this.part3 = source.part3;
        this.part4 = source.part4;
    }

    private void copyBytesInternal(byte[] data, int offset) {
        this.part1 = Util.readIntLE(data, offset + 0);
        this.part2 = Util.readShortLE(data, offset + 4);
        this.part3 = Util.readShortLE(data, offset + 6);
        this.part4 = Util.readLongBE(data, offset + 8);
    }

    protected void copyFields(GUID source) {
        copyFieldsInternal(source);
    }

    protected void copyBytes(byte[] data, int offset) {
        copyBytesInternal(data, offset);
    }

    public byte[] getBytes() {
        byte[] data = new byte[16];

        getBytes(data, 0);

        return data;
    }

    public void getBytes(byte[] data, int offset) {
        Util.arrayCopy(Util.toByteArrayLE(part1), data, offset + 0);
        Util.arrayCopy(Util.toByteArrayLE(part2), data, offset + 4);
        Util.arrayCopy(Util.toByteArrayLE(part3), data, offset + 6);
        Util.arrayCopy(Util.toByteArrayBE(part4), data, offset + 8);
    }

    @Override
    public String toString() {
        return GPTEntry.getGUIDAsString(getBytes());
    }

    private Field getPrivateField(String name) throws NoSuchFieldException {
        Field f = getClass().getDeclaredField(name);
        f.setAccessible(true);
        return f;
    }

    public Dictionary getStructElements() {
        DictionaryBuilder db = new DictionaryBuilder(GPTEntry.class.getSimpleName());

        try {
            db.addUIntLE("part1", getPrivateField("part1"), this, null, null,
                    IntegerFieldRepresentation.HEXADECIMAL);
            db.addUIntLE("part2", getPrivateField("part2"), this, null, null,
                    IntegerFieldRepresentation.HEXADECIMAL);
            db.addUIntLE("part3", getPrivateField("part3"), this, null, null,
                    IntegerFieldRepresentation.HEXADECIMAL);
            db.addUIntLE("part4", getPrivateField("part4"), this, null, null,
                    IntegerFieldRepresentation.HEXADECIMAL);
        } catch(NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        return db.getResult();
    }

    public int size() { return length(); }

    public static int length() { return 16; }
}
