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

/**
 * @author <a href="http://hem.bredband.net/catacombae">Erik Larsson</a>
 */
public class ASCIIStringField extends StringRepresentableField {

    private final byte[] fieldData;

    public ASCIIStringField(byte[] fieldData) {
        super("Char[" + fieldData.length + "]", FieldType.ASCIISTRING);
        this.fieldData = fieldData;
        //String validateMsg = validate(fieldData);
        /*
        if(validateMsg != null) {
            throw new IllegalArgumentException("Invalid value passed to constructor! Message: " + validateMsg);
        }
         * */
    }

    @Override
    public String validateStringValue(String s) {
        char[] sArray = s.toCharArray();
        byte[] asciiArray = new byte[sArray.length];
        for(int i = 0; i < asciiArray.length; ++i) {
            char curChar = sArray[i];
            // Knowing that UTF-16 code units are ASCII compatible, we will do a simple check.
            if(curChar < 0 || curChar > 127) {
                return "Invalid ASCII character at position " + i;
            }
            asciiArray[i] = (byte) curChar;
        }
        return validate(asciiArray);
    }

    private String validate(byte[] data) {
        if(data.length != fieldData.length)
            return "Invalid length for string. Was: " + data.length + " Should be: " + fieldData.length;
        // Check that the bytes are 7-bit only.
        for(int i = 0; i < data.length; ++i) {
            if(data[i] < 0 || data[i] > 127) {
                return "Invalid ASCII character at position " + i;
            }
        }
        return null;
    }

    @Override
    public String getValueAsString() {
        int[] codepoints = new int[fieldData.length];
        for(int i = 0; i < codepoints.length; ++i) {
            int cp = fieldData[i] & 127;
            if(cp != fieldData[i])
                cp = '?';
            codepoints[i] = cp;
        }
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
                asciiChars[i] = (byte) (valueArray[i] & 127);
            }
            System.arraycopy(asciiChars, 0, fieldData, 0, fieldData.length);
        }
        else
            throw new IllegalArgumentException("Invalid string value! Message: " + validateMsg);
    }
}
