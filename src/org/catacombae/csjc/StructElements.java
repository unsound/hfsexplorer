/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.catacombae.csjc;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Hashtable;
import java.util.LinkedList;
import org.catacombae.hfsexplorer.Util;

/**
 *
 * @author Erik
 */
public interface StructElements {
    public static abstract class StructElement {
        //protected final String name;
        protected final String typeName;

        public StructElement(/*String name, */String typeName) {
            //this.name = name;
            this.typeName = typeName;
        }

        /*
        public String getName() {
            return name;
        }
         * */

        public String getTypeName() {
            return typeName;
        }
    }

    public static class Dictionary extends StructElement {
        private final String[] keys;
        private final Hashtable<String, StructElement> mappings;

        public Dictionary(String typeName, String[] keys,
                Hashtable<String, StructElement> mappings) {
            super(typeName);

            this.keys = new String[keys.length];
            System.arraycopy(keys, 0, this.keys, 0, keys.length);

            this.mappings = new Hashtable<String, StructElement>();
            for(String key : keys) {
                this.mappings.put(key, mappings.get(key));
            }
        }

        public StructElement getElement(String name) {
            return mappings.get(name);
        }

        public String[] getKeys() {
            return Util.arrayCopy(keys, new String[keys.length]);
        }
    }
    
    public static class DictionaryBuilder {
        private final String typeName;
        private final LinkedList<String> keys = new LinkedList<String>();
        private final Hashtable<String, StructElement> mappings =
                new Hashtable<String, StructElement>();
        
        public DictionaryBuilder(String typeName) {
            this.typeName = typeName;
        }
        
        public void add(String key, StructElement mapping) {
            if(mappings.get(key) != null)
                throw new IllegalArgumentException("A mapping already exists for key \"" + key + "\"!");
            mappings.put(key, mapping);
            keys.add(key);
        }
        
        public Dictionary getResult() {
            return new Dictionary(typeName, keys.toArray(new String[keys.size()]),
                    mappings);
        }
    }
    
    public static class Array extends StructElement {
        private final StructElement[] elements;

        public Array(String typeName, StructElement[] elements) {
            super(typeName + "[" + elements.length + "]");
            this.elements = new StructElement[elements.length];
            for(int i = 0; i < this.elements.length; ++i) {
                this.elements[i] = elements[i];
            }
        }

        public StructElement[] getElements() {
            return Util.arrayCopy(elements, new StructElement[elements.length]);
        }
    }

    public static class ArrayBuilder {
        private final String typeName;
        private final LinkedList<StructElement> elements = new LinkedList<StructElement>();
        
        public ArrayBuilder(String typeName) {
            this.typeName = typeName;
        }
        
        public void add(StructElement... elements) {
            for(StructElement element : elements)
                this.elements.add(element);
        }
        
        public Array getResult() {
            return new Array(typeName, elements.toArray(new StructElement[elements.size()]));
        }
    }
    
    public static enum FieldType {
        INTEGER, BYTEARRAY, ASCIISTRING, CUSTOM_CHARSET_STRING;
    }

    public static abstract class Field extends StructElement {
        private final FieldType type;

        public Field(String typeName, FieldType type) {
            super(typeName);

            this.type = type;
            //this.value = value;
        }

        public FieldType getType() {
            return type;
        }

        public abstract String getValueAsString();

        public abstract void setStringValue(String value) throws IllegalArgumentException;

        public abstract String validateStringValue(String s);
    }

    public static enum IntegerFieldBits {
        BITS_8(8), BITS_16(16), BITS_32(32), BITS_64(64);
        private final int bitCount;

        private IntegerFieldBits(int bitCount) {
            this.bitCount = bitCount;
        }

        public int getBits() {
            return bitCount;
        }

        public int getBytes() {
            return bitCount / 8;
        }
    }

    public static class IntegerField extends Field {
        private final byte[] fieldData;
        private final IntegerFieldBits bits;
        private final boolean signed;
        private final boolean littleEndian;
        private final BigInteger maxValue;
        private final BigInteger minValue;

        /**
         * We only support value types with bit length divisible by 8 (octets).
         * 
         * @param fieldData the raw bytes that make up the field.
         * @param signed whether the integer is to be interpreted as a signed or
         * unsigned value.
         * @param littleEndian whether the data for this field was stored in
         * little endian form (true) or big endian form (false).
         */
        public IntegerField(byte[] fieldData, IntegerFieldBits bits, boolean signed,
                boolean littleEndian) {
            super((signed ? "S" : "U") + "Int" + bits.getBits(), FieldType.INTEGER);

            this.fieldData = fieldData;
            this.bits = bits;
            this.signed = signed;
            this.littleEndian = littleEndian;

            byte[] maxValueBytes = new byte[bits.getBytes()];
            byte[] minValueBytes = new byte[bits.getBytes()];

            Util.set(maxValueBytes, (byte) 0xFF);
            Util.zero(minValueBytes);

            if(signed) {
                maxValueBytes[0] = (byte) (maxValueBytes[0] & 0x7F);
                minValueBytes[0] = (byte) 0x80;
            }

            this.maxValue = new BigInteger(1, maxValueBytes);
            this.minValue = new BigInteger(minValueBytes);

            String validateMsg = validateData();
            if(validateMsg != null) {
                throw new IllegalArgumentException("Invalid value passed to constructor! Message: " +
                        validateMsg);
            }
        }
        
        private String validateData() {
            return validate(getValueAsBigInteger());
        }
        
        private String validate(BigInteger bi) {
            if(!signed && bi.signum() == -1) {
                return "Tried to insert signed integer into unsigned field.";
            }
            else if(bi.compareTo(maxValue) > 0) {
                return "Value too large for field! Maximum value is " +
                        maxValue.toString() + ".";
            }
            else if(bi.compareTo(minValue) < 0) {
                return "Value too small for this field. Minimum value is " +
                        minValue.toString() + ".";
            }
            else {
                return null; // Success
            }
        }

        public BigInteger getValueAsBigInteger() {
            byte[] data;
            if(littleEndian)
                data = Util.createReverseCopy(fieldData);
            else
                data = Util.createCopy(fieldData);
            
            if(signed)
                return new BigInteger(data);
            else
                return new BigInteger(1, data);
        }
        
        @Override
        public String getValueAsString() {
            return getValueAsBigInteger().toString();
        }

        @Override
        public void setStringValue(String value) throws IllegalArgumentException {
            String validateMsg = validateStringValue(value);
            if(validateMsg == null) {
                BigInteger bi = new BigInteger(value);
                byte[] ba = bi.toByteArray();
                
                /* ba is now in big endian, signed two-complement
                 * we need to cut away the extra sign byte, if any
                 * and swap endianness, if needed */
                
                // First some sanity checks
                if(signed && ba.length != fieldData.length)
                    throw new RuntimeException("UNEXPECTED: ba.length (" + ba.length +
                            ") != fieldData.length(" + fieldData.length + ")");
                
                if(!signed && ba.length != (fieldData.length+1))
                    throw new RuntimeException("UNEXPECTED: ba.length (" + ba.length +
                            ") != (fieldData.length+1)(" + fieldData.length + "+1=" +
                            (fieldData.length+1) + ")");
                
                byte[] trueContents;
                if(littleEndian)
                    trueContents = Util.createReverseCopy(ba, ba.length-fieldData.length, fieldData.length);
                else
                    trueContents = Util.createCopy(ba, ba.length-fieldData.length, fieldData.length);
                
                System.arraycopy(trueContents, 0, fieldData, 0, fieldData.length);
            }
            else
                throw new IllegalArgumentException("Invalid string value! Message: " +
                        validateMsg);
        }

        @Override
        public String validateStringValue(String s) {
            try {
                BigInteger bi = new BigInteger(s);
                return validate(bi);
            } catch(NumberFormatException nfe) {
                return "Invalid integer string.";
            }
        }
    }

    public static class ByteArrayField extends Field {
        private final byte[] fieldData;
        
        public ByteArrayField(byte[] fieldData) {
            super("Byte[" + fieldData.length + "]", FieldType.BYTEARRAY);
            
            this.fieldData = fieldData;
        }

        @Override
        public String getValueAsString() {
            return null;
        }

        @Override
        public void setStringValue(String value) throws IllegalArgumentException {
            throw new UnsupportedOperationException("Can't set byte string to string value at this point.");
        }

        @Override
        public String validateStringValue(String s) {
            return "Can't set a byte string to a string value.";
        }
    }
    
    public static class ASCIIStringField extends Field {
        private final byte[] fieldData;
        
        public ASCIIStringField(byte[] fieldData) {
            super("Char[" + fieldData.length + "]", FieldType.ASCIISTRING);
            
            this.fieldData = fieldData;

            String validateMsg = validate(fieldData);
            if(validateMsg != null) {
                throw new IllegalArgumentException("Invalid value passed to constructor! Message: " +
                        validateMsg);
            }
        }

        @Override
        public String validateStringValue(String s) {
            char[] sArray = s.toCharArray();
            
            byte[] asciiArray = new byte[sArray.length];
            for(int i = 0; i < asciiArray.length; ++i) {
                char curChar = sArray[i];
                
                // Knowing that UTF-16 code units are ASCII compatible, we will do a simple check.
                if(curChar < 0x00 || curChar > 0x7F) {
                    return "Invalid ASCII character at position " + i;
                }

                asciiArray[i] = (byte)curChar;
            }
            
            return validate(asciiArray);
        }
        
        private String validate(byte[] data) {
            if(data.length != fieldData.length)
                return "Invalid length for string. Was: " + data.length + " Should be: " +
                        fieldData.length;
            
            // Check that the bytes are 7-bit only.
            for(int i = 0; i < data.length; ++i) {
                if(data[i] < 0x00 || data[i] > 0x7F) {
                    return "Invalid ASCII character at position " + i;
                }
            }
            
            return null; // Success
        }

        @Override
        public String getValueAsString() {
            int[] codepoints = new int[fieldData.length];
            for(int i = 0; i < codepoints.length; ++i)
                codepoints[i] = fieldData[i] & 0x7F;
            return new String(codepoints, 0, codepoints.length);
        }

        @Override
        public void setStringValue(String value) throws IllegalArgumentException {
            String validateMsg = validateStringValue(value);
            if(validateMsg == null) {
                char[] valueArray = value.toCharArray();
                if(valueArray.length != fieldData.length)
                    throw new RuntimeException("You should not see this.");
                
                byte[] asciiChars = new byte[fieldData.length];
                for(int i = 0; i < asciiChars.length; ++i) {
                    asciiChars[i] = (byte)(valueArray[i] & 0x7F);
                }
                System.arraycopy(asciiChars, 0, fieldData, 0, fieldData.length);
            }
            else
                throw new IllegalArgumentException("Invalid string value! Message: " +
                        validateMsg);
        }
    }
    
    public static class EncodedStringField extends Field {
        private final byte[] fieldData;
        private final Charset charset;
        
        public EncodedStringField(byte[] fieldData, String encoding) {
            super("Byte[" + fieldData.length + "]", FieldType.CUSTOM_CHARSET_STRING);
            
            this.fieldData = fieldData;
            this.charset = Charset.forName(encoding);

            String validateMsg = validate(fieldData);
            if(validateMsg != null) {
                throw new IllegalArgumentException("Invalid value passed to constructor! Message: " +
                        validateMsg);
            }
        }

        @Override
        public String validateStringValue(String s) {
            try {
                CharsetEncoder enc = charset.newEncoder();
                ByteBuffer bb = enc.encode(CharBuffer.wrap(s));
                return validate(bb.array());
            } catch(CharacterCodingException cce) {
                return "Exception while encoding string data: " + cce.toString();
            }
        }
        
        private String validate(byte[] data) {
            if(data.length != fieldData.length)
                return "Invalid length for string. Was: " + data.length + " Should be: " +
                        fieldData.length;
            
            // Attempt to decode data
            try {
                CharsetDecoder dec = charset.newDecoder();
                dec.decode(ByteBuffer.wrap(data));
            } catch(Exception e) {
                return "Decode operation failed! Exception: " + e.toString();
            }
            
            return null; // Success
        }

        @Override
        public String getValueAsString() {
            try {
                CharsetDecoder dec = charset.newDecoder();
                return dec.decode(ByteBuffer.wrap(fieldData)).toString();
            } catch(CharacterCodingException cce) {
                throw new RuntimeException("Exception while decoding data...", cce);
            }
        }

        @Override
        public void setStringValue(String value) throws IllegalArgumentException {
            String validateMsg = validateStringValue(value);
            if(validateMsg == null) {
                try {
                    CharsetEncoder enc = charset.newEncoder();
                    ByteBuffer bb = enc.encode(CharBuffer.wrap(value));
                    byte[] encodedData = bb.array();
                    if(encodedData.length != fieldData.length)
                        throw new RuntimeException("You should not see this.");
                    System.arraycopy(encodedData, 0, fieldData, 0, fieldData.length);
                } catch(CharacterCodingException cce) {
                    throw new RuntimeException("Exception while encoding string data: ", cce);
                }
            }
            else
                throw new IllegalArgumentException("Invalid string value! Message: " +
                        validateMsg);
        }
    }
}
