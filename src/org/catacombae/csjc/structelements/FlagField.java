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
public class FlagField extends BooleanRepresentableField {

    private final byte[] fieldData;
    private final int offset;
    private final int length;
    private final int bitNumber;

    public FlagField(byte[] fieldData, int bitNumber) {
        this(fieldData, 0, fieldData.length, bitNumber);
    }

    public FlagField(byte[] fieldData, int offset, int length, int bitNumber) {
        super("Bit[1]", FieldType.BOOLEAN);
        this.fieldData = fieldData;
        this.offset = offset;
        this.length = length;
        this.bitNumber = bitNumber;
        if(bitNumber < 0 || bitNumber > length * 8)
            throw new IllegalArgumentException("Illegal bit address! Valid " + "addresses are in the range 0 to " + (length * 8 - 1));
    }

    @Override
    public boolean getValueAsBoolean() {
        //System.err.println("getValueAsBoolean(): bitNumber/8 = " + (bitNumber/8));
        int byteNumber = (length - 1) - (bitNumber / 8);
        //System.err.println("getValueAsBoolean(): byteNumber = " + byteNumber);
        byte flagByte = fieldData[offset + byteNumber];
        //System.err.println("getValueAsBoolean(): bitNumber%8 = " + (bitNumber%8));
        int flag = (flagByte >> (bitNumber % 8)) & 1;
        return flag != 0;
    }

    @Override
    public void setBooleanValue(boolean b) {
        int byteNumber = (length - 1) - (bitNumber / 8);
        byte flagByte = fieldData[offset + byteNumber];
        int bitmask = 1 << (bitNumber % 8);
        final byte modifiedFlagByte;
        if(b)
            modifiedFlagByte = (byte) (flagByte | bitmask);
        else
            modifiedFlagByte = (byte) ~((~flagByte) | bitmask);
        fieldData[offset + byteNumber] = modifiedFlagByte;
    }

    /**
     * Testcode.
     * 
     * @param args command line arguments.
     */
    /*
    public static void main(String[] args) {
        byte[] i = new byte[] { 0x00, (byte) 0xFF, 0x00, (byte) 0xEE };
        System.err.println("(1) 0x" + Util.byteArrayToHexString(i));

        FlagField ff = new FlagField(i, 1, 2, 15);

        System.err.println("Flag set to: " + ff.getValueAsBoolean());
        System.err.println("Setting flag to true.");
        ff.setBooleanValue(true);
        System.err.println("(2) 0x" + Util.byteArrayToHexString(i));

        System.err.println("Setting flag to false.");
        ff.setBooleanValue(false);
        System.err.println("(3) 0x" + Util.byteArrayToHexString(i));

        System.err.println("Setting flag to true again.");
        ff.setBooleanValue(true);
        System.err.println("(4) 0x" + Util.byteArrayToHexString(i));
    }
    */
}
