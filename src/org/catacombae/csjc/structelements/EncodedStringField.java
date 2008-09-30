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

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class EncodedStringField extends StringRepresentableField {

    private final byte[] fieldData;
    private final Charset charset;

    public EncodedStringField(byte[] fieldData, String encoding) {
        super("Byte[" + fieldData.length + "]", FieldType.CUSTOM_CHARSET_STRING);
        this.fieldData = fieldData;
        this.charset = Charset.forName(encoding);
        String validateMsg = validate(fieldData);
        if(validateMsg != null) {
            throw new IllegalArgumentException("Invalid value passed to constructor! Message: " + validateMsg);
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
            return "Invalid length for string. Was: " + data.length + " Should be: " + fieldData.length;
        // Attempt to decode data
        try {
            CharsetDecoder dec = charset.newDecoder();
            dec.decode(ByteBuffer.wrap(data));
        } catch(Exception e) {
            return "Decode operation failed! Exception: " + e.toString();
        }
        return null;
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
            throw new IllegalArgumentException("Invalid string value! Message: " + validateMsg);
    }
}
