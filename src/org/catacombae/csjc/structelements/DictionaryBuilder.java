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

import java.util.Hashtable;
import java.util.LinkedList;
import static org.catacombae.csjc.structelements.IntegerFieldBits.*;
import static org.catacombae.csjc.structelements.Signedness.*;
import static org.catacombae.csjc.structelements.Endianness.*;
import static org.catacombae.csjc.structelements.IntegerFieldRepresentation.*;

/**
 * @author <a href="http://hem.bredband.net/catacombae">Erik Larsson</a>
 */
public class DictionaryBuilder {

    private final String typeName;
    private final String typeDescription;
    private final LinkedList<String> keys = new LinkedList<String>();
    private final Hashtable<String,StructElement> mappings = new Hashtable<String,StructElement>();
    private final Hashtable<String,String> descriptions = new Hashtable<String,String>();

    public DictionaryBuilder(String typeName) {
        this(typeName, null);
    }
    
    public DictionaryBuilder(String typeName, String typeDescription) {
        this.typeName = typeName;
        this.typeDescription = typeDescription;
    }

    public void addIntArray(String key, byte[] data, IntegerFieldBits bits,
            Signedness signedness, Endianness endianness) {
        addIntArray(key, data, 0, data.length, bits, signedness, endianness);
    }

    public void addIntArray(String key, byte[] data, IntegerFieldBits bits,
            Signedness signedness, Endianness endianness, String description,
            IntegerFieldRepresentation rep) {
        addIntArray(key, data, 0, data.length, bits, signedness, endianness, description, rep);
    }

    public void addIntArray(String key, byte[] data, int offset, int length,
            IntegerFieldBits bits, Signedness signedness, Endianness endianness) {
        addIntArray(key, data, offset, length, bits, signedness, endianness, null, DECIMAL);
    }
    
    public void addIntArray(String key, byte[] data, int offset, int length,
            IntegerFieldBits bits, Signedness signedness, Endianness endianness, String description,
            IntegerFieldRepresentation rep) {
        if(length % bits.getBytes() != 0)
            throw new RuntimeException("Supplied data is not aligned to size of type.");
        String arrayTypeName;
        switch(signedness) {
            case SIGNED:
                arrayTypeName = "S";
                break;
            case UNSIGNED:
                arrayTypeName = "U";
                break;
            default:
                throw new IllegalArgumentException("signedness == null!");
        }
        arrayTypeName += "Int" + bits.getBits() + "[" + (length / bits.getBytes()) + "]";
        //System.err.println("DictionaryBuilder.addIntArray(): length = " + length);
        //System.err.println("DictionaryBuilder.addIntArray(): bits.getBytes() = " + bits.getBytes());
        ArrayBuilder ab = new ArrayBuilder(arrayTypeName);
        int i;
        for(i = 0; i < length; i += bits.getBytes()) {
            //System.err.println("DictionaryBuilder.addIntArray():  i = " + i);
            ab.add(new IntegerField(data, offset + i, bits, signedness, endianness, rep, null));
        }
        //System.err.println("DictionaryBuilder.addIntArray():  i = " + i);
        //System.err.println("DictionaryBuilder.addIntArray():  length/bits.getBytes() = " + (length / bits.getBytes()));
        add(key, ab.getResult(), description);
    }

    public Dictionary getResult() {
        return new Dictionary(typeName, typeDescription, keys.toArray(new String[keys.size()]), mappings, descriptions);
    }

    public void add(String key, StructElement mapping) {
        add(key, mapping, null);
    }
    
    public void add(String key, StructElement mapping, String description) {
        //System.err.println(this + ": add(" + key + ", " + mapping + ", " + description + ");");
        if(mappings.get(key) != null)
            throw new IllegalArgumentException("A mapping already exists for key \"" + key + "\"!");
        mappings.put(key, mapping);
        if(description != null)
            descriptions.put(key, description);
        keys.add(key);
    }

    /*
     * Only braindead convenience methods below!
     */
    public void addSIntBE(String key, byte[] data) {
        addSIntBE(key, data, null);
    }

    public void addSIntLE(String key, byte[] data) {
        addSIntLE(key, data, null);
    }

    public void addUIntBE(String key, byte[] data) {
        addUIntBE(key, data, null);
    }

    public void addUIntLE(String key, byte[] data) {
        addUIntLE(key, data, null);
    }

    public void addSIntBE(String key, byte[] data, String description) {
        addSIntBE(key, data, 0, data.length, description, null, DECIMAL);
    }

    public void addSIntLE(String key, byte[] data, String description) {
        addSIntLE(key, data, 0, data.length, description, null, DECIMAL);
    }

    public void addUIntBE(String key, byte[] data, String description) {
        addUIntBE(key, data, 0, data.length, description, null, DECIMAL);
    }

    public void addUIntLE(String key, byte[] data, String description) {
        addUIntLE(key, data, 0, data.length, description, null, DECIMAL);
    }

    public void addSIntBE(String key, byte[] data, String description, String unit) {
        addSIntBE(key, data, 0, data.length, description, unit, DECIMAL);
    }

    public void addSIntLE(String key, byte[] data, String description, String unit) {
        addSIntLE(key, data, 0, data.length, description, unit, DECIMAL);
    }

    public void addUIntBE(String key, byte[] data, String description, String unit) {
        addUIntBE(key, data, 0, data.length, description, unit, DECIMAL);
    }

    public void addUIntLE(String key, byte[] data, String description, String unit) {
        addUIntLE(key, data, 0, data.length, description, unit, DECIMAL);
    }

    public void addSIntBE(String key, byte[] data, String description, IntegerFieldRepresentation rep) {
        addSIntBE(key, data, 0, data.length, description, null, rep);
    }

    public void addSIntLE(String key, byte[] data, String description, IntegerFieldRepresentation rep) {
        addSIntLE(key, data, 0, data.length, description, null, rep);
    }

    public void addUIntBE(String key, byte[] data, String description, IntegerFieldRepresentation rep) {
        addUIntBE(key, data, 0, data.length, description, null, rep);
    }

    public void addUIntLE(String key, byte[] data, String description, IntegerFieldRepresentation rep) {
        addUIntLE(key, data, 0, data.length, description, null, rep);
    }

    public void addSIntBE(String key, byte[] data, int offset, int length, String description, String unit, IntegerFieldRepresentation rep) {
        addInt(key, data, offset, length, SIGNED, BIG_ENDIAN, description, unit, rep);
    }

    public void addSIntLE(String key, byte[] data, int offset, int length, String description, String unit, IntegerFieldRepresentation rep) {
        addInt(key, data, offset, length, SIGNED, LITTLE_ENDIAN, description, unit, rep);
    }

    public void addUIntBE(String key, byte[] data, int offset, int length, String description, String unit, IntegerFieldRepresentation rep) {
        addInt(key, data, offset, length, UNSIGNED, BIG_ENDIAN, description, unit, rep);
    }

    public void addUIntLE(String key, byte[] data, int offset, int length, String description, String unit, IntegerFieldRepresentation rep) {
        addInt(key, data, offset, length, UNSIGNED, LITTLE_ENDIAN, description, unit, rep);
    }

    private void addInt(String key, byte[] data, int offset, int length,
            Signedness signedness, Endianness endianness, String description, String unit, IntegerFieldRepresentation rep) {
        switch(length) {
            case 1:
                add(key, new IntegerField(data, offset, BITS_8, signedness,
                        endianness, rep, unit), description);
                break;
            case 2:
                add(key, new IntegerField(data, offset, BITS_16, signedness,
                        endianness, rep, unit), description);
                break;
            case 4:
                add(key, new IntegerField(data, offset, BITS_32, signedness,
                        endianness, rep, unit), description);
                break;
            case 8:
                add(key, new IntegerField(data, offset, BITS_64, signedness,
                        endianness, rep, unit), description);
                break;
            default:
                throw new IllegalArgumentException("You supplied a " + (length * 8) +
                        "-bit value. Only 64, 32, 16 and 8-bit values are supported.");
        }
    }

    /*
    public void addSInt8(String key, byte[] data) {
    addSInt8(key, data, 0);
    }
    public void addSInt8(String key, byte[] data, int offset) {
    add(key, new IntegerField(data, BITS_8, SIGNED, BIG_ENDIAN));
    }
    public void addUInt8(String key, byte[] data) {
    addUInt8(key, data, 0);
    }
    public void addUInt8(String key, byte[] data, int offset) {
    add(key, new IntegerField(data, BITS_8, UNSIGNED, BIG_ENDIAN));
    }
    public void addSInt16LE(String key, byte[] data) {
    addUInt16LE(key, data, 0);
    }
    public void addSInt16LE(String key, byte[] data, int offset) {
    add(key, new IntegerField(data, BITS_16, SIGNED, LITTLE_ENDIAN));
    }
    public void addSInt16BE(String key, byte[] data) {
    addSInt16BE(key, data, 0);
    }
    public void addSInt16BE(String key, byte[] data, int offset) {
    add(key, new IntegerField(data, BITS_16, SIGNED, BIG_ENDIAN));
    }
    public void addUInt16LE(String key, byte[] data) {
    addUInt16LE(key, data, 0);
    }
    public void addUInt16LE(String key, byte[] data, int offset) {
    add(key, new IntegerField(data, BITS_16, UNSIGNED, LITTLE_ENDIAN));
    }
    public void addUInt16BE(String key, byte[] data) {
    addUInt16BE(key, data, 0);
    }
    public void addUInt16BE(String key, byte[] data, int offset) {
    add(key, new IntegerField(data, BITS_16, UNSIGNED, BIG_ENDIAN));
    }
    public void addSInt32LE(String key, byte[] data) {
    addUInt32LE(key, data, 0);
    }
    public void addSInt32LE(String key, byte[] data, int offset) {
    add(key, new IntegerField(data, BITS_32, SIGNED, LITTLE_ENDIAN));
    }
    public void addSInt32BE(String key, byte[] data) {
    addSInt32BE(key, data, 0);
    }
    public void addSInt32BE(String key, byte[] data, int offset) {
    add(key, new IntegerField(data, BITS_32, SIGNED, BIG_ENDIAN));
    }
    public void addUInt32LE(String key, byte[] data) {
    addUInt32LE(key, data, 0);
    }
    public void addUInt32LE(String key, byte[] data, int offset) {
    add(key, new IntegerField(data, BITS_32, UNSIGNED, LITTLE_ENDIAN));
    }
    public void addUInt32BE(String key, byte[] data) {
    addUInt32BE(key, data, 0);
    }
    public void addUInt32BE(String key, byte[] data, int offset) {
    add(key, new IntegerField(data, BITS_32, UNSIGNED, BIG_ENDIAN));
    }
    public void addSInt64LE(String key, byte[] data) {
    addUInt64LE(key, data, 0);
    }
    public void addSInt64LE(String key, byte[] data, int offset) {
    add(key, new IntegerField(data, BITS_64, SIGNED, LITTLE_ENDIAN));
    }
    public void addSInt64BE(String key, byte[] data) {
    addSInt64BE(key, data, 0);
    }
    public void addSInt64BE(String key, byte[] data, int offset) {
    add(key, new IntegerField(data, BITS_64, SIGNED, BIG_ENDIAN));
    }
    public void addUInt64LE(String key, byte[] data) {
    addUInt64LE(key, data, 0);
    }
    public void addUInt64LE(String key, byte[] data, int offset) {
    add(key, new IntegerField(data, BITS_64, UNSIGNED, LITTLE_ENDIAN));
    }
    public void addUInt64BE(String key, byte[] data) {
    addUInt64BE(key, data, 0);
    }
    public void addUInt64BE(String key, byte[] data, int offset) {
    add(key, new IntegerField(data, BITS_64, UNSIGNED, BIG_ENDIAN));
    }
     */
    public void addByteArray(String key, byte[] data) {
        addByteArray(key, data, 0, data.length);
    }

    public void addByteArray(String key, byte[] data, int offset, int length) {
        addByteArray(key, data, offset, length, null);
    }

    public void addByteArray(String key, byte[] data, int offset, int length, String description) {
        add(key, new ByteArrayField(data, offset, length), description);
    }

    public void addFlag(String key, byte[] data, int bitOffset, String description) {
        addFlag(key, data, 0, data.length, bitOffset, description);
    }
    
    public void addFlag(String key, byte[] data, int bitOffset) {
        addFlag(key, data, 0, data.length, bitOffset);
    }

    public void addFlag(String key, byte[] data, int offset, int length, int bitOffset) {
        addFlag(key, data, offset, length, bitOffset, null);
    }
    
    public void addFlag(String key, byte[] data, int offset, int length, int bitOffset, String description) {
        add(key, new FlagField(data, offset, length, bitOffset), description);
    }
    
    public void addEncodedString(String key, byte[] data, String encoding) {
        addEncodedString(key, data, 0, data.length, encoding);
    }
    public void addEncodedString(String key, byte[] data, int offset, int length, String encoding) {
        add(key, new EncodedStringField(data, offset, length, encoding));
    }
    
    /** Adds all the key-value mappings present in <code>d</code> in order. */
    public void addAll(Dictionary d) {
        for(String key : d.getKeys()) {
            String description = d.getDescription(key);
            if(description != null)
                add(key, d.getElement(key), description);
            else
                add(key, d.getElement(key));
        }
    }
}
