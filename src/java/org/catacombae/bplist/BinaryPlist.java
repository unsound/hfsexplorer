/*-
 * Copyright (C) 2021 Erik Larsson
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

package org.catacombae.bplist;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import org.catacombae.bplist.types.BinaryPlistFooter;
import org.catacombae.bplist.types.BinaryPlistHeader;
import org.catacombae.util.Util;

/**
 * Class wrapping binary plist data for easier access to the structured data
 * inside it.
 *
 * @author Erik Larsson
 */
public class BinaryPlist {
    private final BinaryPlistHeader header;
    private final byte[] offsetMap;
    private final BinaryPlistFooter footer;
    private final Entry rootEntry;

    public BinaryPlist(byte[] data, int offset, int size) {
        if(size < 8 + 32) {
            throw new RuntimeException("Buffer is too small for binary plist " +
                    "header.");
        }

        this.header = new BinaryPlistHeader(data, offset);

        if(!Util.toASCIIString(this.header.getSignature()).equals("bplist")) {
            throw new RuntimeException("Signature mismatch for binary plist.");
        }
        else if(!Util.toASCIIString(this.header.getVersion()).equals("00")) {
            throw new RuntimeException("Unsupported binary plist version: " +
                    Util.toASCIIString(this.header.getVersion()));
        }

        this.footer =
                new BinaryPlistFooter(data, offset + size -
                BinaryPlistFooter.STRUCTSIZE);

        if(footer.getRawOffsetTableStart() > Integer.MAX_VALUE) {
            throw new RuntimeException("Unreasonably large or unsupported " +
                    "offset table start: " + footer.getRawOffsetTableStart());
        }

        /* Read the offset map. */
        this.offsetMap =
                new byte[size - 32 - (int) footer.getRawOffsetTableStart()];
        Util.arrayCopy(data, offset + (int) footer.getRawOffsetTableStart(),
                this.offsetMap, 0, this.offsetMap.length);

        /* Read the property tree. */
        this.rootEntry = parseBinaryPlistValue(data, offset, size, 0);
    }

    public final BinaryPlistHeader getHeader() {
        return header;
    }

    public final BinaryPlistFooter getFooter() {
        return footer;
    }

    public final int getOffsetMapping(int source) {
        int sourceOffset = source * footer.getOffsetTableOffsetSize();
        int result = 0;

        for(int i = 0; i < footer.getOffsetTableOffsetSize(); ++i) {
            result = (result << 8) | (this.offsetMap[sourceOffset + i] & 0xFF);
        }

        return result;
    }

    public final Entry getRootEntry() {
        return rootEntry;
    }

    private Entry parseBinaryPlistValue(byte[] data, int offset, int size,
            int virtualOffset)
    {
        Entry result = null;
        int physicalOffset = getOffsetMapping(virtualOffset);

        byte marker = data[offset + physicalOffset];

        /*
        System.out.println(insetString + "Reading element at " +
                virtualOffset + " -> " + physicalOffset);
        System.out.println(insetString + "  (marker: " +
                "0x" + Util.toHexStringBE(marker) + ")");
        */

        switch(marker & 0xF0) {
        case 0x00:
            if(marker == 0x00) {
                result = new NullEntry();
            }
            else if(marker == 0x08) {
                result = new BooleanEntry(false);
            }
            else if(marker == 0x09) {
                result = new BooleanEntry(true);
            }
            else if(marker == 0x0F) {
                /* TODO: Find out what this marker is supposed to mean. */
                result = new FillEntry();
            }
            else {
                result = new UnknownEntry(marker);
            }

            break;
        case 0x10: {
            /* Integer */
            int valueSize = 1 << (marker & 0x0F);
            byte[] valueData =
                    Util.arrayCopy(data, offset + physicalOffset + 1,
                    new byte[valueSize], 0, valueSize);
            BigInteger value = new BigInteger(valueData);

            result = new IntegerEntry(marker, valueData, value);
            break;
        }
        case 0x20: {
            /* Real number */
            int valueSize = 1 << (marker & 0x0F);
            byte[] valueData =
                Util.arrayCopy(data, offset + physicalOffset + 1,
                new byte[valueSize], 0, valueSize);
            BigDecimal value = null;
            if(valueSize == 4) {
                /*
                 * "float" format: Big endian with 1-bit sign, 8-bit
                 * exponent, 23-bit significand.
                 */

                int rawValue = Util.readIntBE(valueData);
                int unscaledValue =
                        (rawValue & 0x807FFFFF);
                int scale =
                        (rawValue & 0x7F800000) >>> 23;

                value = BigDecimal.valueOf(unscaledValue, scale);
            }
            else if(valueSize == 8) {
                /*
                 * "double" format: Big endian with 1-bit sign, 11-bit
                 * exponent, 52-bit significand.
                 */

                long rawValue = Util.readLongBE(valueData);
                long unscaledValue =
                        (rawValue & 0x800FFFFFFFFFFFFFL);
                int scale =
                        (int) ((rawValue & 0x7FF0000000000000L) >>> 52);

                value = BigDecimal.valueOf(unscaledValue, scale);
            }

            result = new DecimalEntry(marker, valueData, value);
            break;
        }
        case 0x30: {
            /* Date */
            int valueSize = 1 << (marker & 0x0F);
            byte[] valueData =
                Util.arrayCopy(data, offset + physicalOffset + 1,
                new byte[valueSize], 0, valueSize);
            Date d = null;
            if(valueSize == 8) {
                /*
                 * "double" format: Big endian with 1-bit sign, 11-bit
                 * exponent, 52-bit significand.
                 */

                long rawValue = Util.readLongBE(valueData);

                BigDecimal value =
                        new BigDecimal(Double.longBitsToDouble(rawValue));

                long unixTimestamp =
                        978307200 +
                        value.multiply(BigDecimal.valueOf(1000)).longValue();

                d = new Date(unixTimestamp);
            }

            result = new DateEntry(marker, valueData, d);
            break;
        }
        case 0x40:
            /* Data */
        case 0x50:
            /* ASCII string */
        case 0x60:
            /* Unicode string */
        case 0xA0:
            /* Array */
        case 0xC0:
            /* Set */
        case 0xD0:
            /* Dictionary */
        {
            /*
             * All of these types have in common that they have a variable
             * sized count field describing the length of the data.
             */
            int valueSize = marker & 0x0F;
            int dataOffset = 1;

            if(valueSize == 0x0F) {
                /*
                 * All bits set in the lower nibble means the size is encoded as
                 * a variable-sized integer.
                 * First comes one byte containing the size of the size field in
                 * its low nibble (the high nibble should always be 0x1 as a
                 * marker). The size of the size field is encoded as the power
                 * of two of the size.
                 * I.e. a value of 3 means the integer is 2^3 = 8 bytes.
                 *
                 * After that comes the size field itself, encoded as a
                 * big-endian integer.
                 */
                byte sizeSizeByte = data[offset + physicalOffset + 1];
                if((sizeSizeByte & 0xF0) == 0x10) {
                    int sizeSize = 1 << (sizeSizeByte & 0x0F);
                    BigInteger sizeBigInt =
                            new BigInteger(Util.arrayCopy(data,
                            offset + physicalOffset + 2, new byte[sizeSize], 0,
                            sizeSize));
                    if(sizeBigInt.bitCount() > 31) {
                        System.err.println("Unexpected number of bits (" +
                                sizeBigInt.bitCount() + ") in size bits " +
                                "nibble! Byte: 0x" +
                                Util.toHexStringBE(sizeSizeByte));
                        dataOffset = 0;
                    }
                    else {
                        valueSize = sizeBigInt.intValue();
                        dataOffset += 1 + sizeSize;
                    }
                }
                else {
                    System.err.println("Unexpected marker in size bits " +
                            "nibble! Byte: 0x" +
                            Util.toHexStringBE(sizeSizeByte));
                    dataOffset = 0;
                }
            }

            if(dataOffset != 0) {
                switch(marker & 0xF0) {
                case 0x40: {
                    /* Data */
                    byte[] valueData =
                            Util.arrayCopy(data,
                            offset + physicalOffset + dataOffset,
                            new byte[valueSize], 0, valueSize);
                    result = new DataEntry(marker, valueData);
                    break;
                }
                case 0x50: {
                    /* ASCII string */
                    byte[] valueData =
                            Util.arrayCopy(data,
                            offset + physicalOffset + dataOffset,
                            new byte[valueSize], 0, valueSize);
                    result = new ASCIIStringEntry(marker, valueData);
                    break;
                }
                case 0x60: {
                    /* Unicode string */

                    /*
                     * Here the size field should be interpreted as the number
                     * of UTF-16BE units, i.e. the data size in bytes divided by
                     * 2. So to get the byte size back we multiply by 2.
                     */
                    byte[] valueData =
                            Util.arrayCopy(data,
                            offset + physicalOffset + dataOffset,
                            new byte[valueSize * 2], 0, valueSize * 2);
                    result = new UnicodeStringEntry(marker, valueData);
                    break;
                }
                case 0xA0:
                    /* Array */
                case 0xC0: {
                    /* Set */

                    /*
                     * Here the size field should be interpreted as the number
                     * of value refs.
                     */

                    short refSize = getFooter().getObjectRefSize();
                    byte[] valueData =
                            Util.arrayCopy(data,
                            offset + physicalOffset + dataOffset,
                            new byte[valueSize * refSize], 0,
                            valueSize * refSize);

                    LinkedList<Entry> entries = new LinkedList<Entry>();
                    for(int i = 0; i < valueSize; ++i) {
                        BigInteger valueRef =
                                new BigInteger(Util.arrayCopy(data,
                                offset + physicalOffset + dataOffset +
                                i * refSize,
                                new byte[refSize], 0, refSize));

                        entries.addLast(parseBinaryPlistValue(data, offset,
                                size, valueRef.intValue()));
                    }

                    result =
                            (marker & 0xF0) == 0xC0 ?
                                new SetEntry(marker, valueData, entries) :
                                new ArrayEntry(marker, valueData, entries);
                    break;
                }
                case 0xD0: {
                    /* Dictionary */

                    /*
                     * Here the size field should be interpreted as the number
                     * of key ref - value ref pairs.
                     */
                    short refSize =
                            getFooter().getObjectRefSize();
                    byte[] valueData =
                            Util.arrayCopy(data,
                            offset + physicalOffset + dataOffset,
                            new byte[valueSize * refSize * 2], 0,
                            valueSize * refSize * 2);

                    LinkedList<Entry> keys = new LinkedList<Entry>();
                    LinkedList<Entry> values = new LinkedList<Entry>();
                    for(int i = 0; i < valueSize; ++i) {
                        BigInteger keyRef = new BigInteger(Util.arrayCopy(
                                data,
                                offset + physicalOffset + dataOffset +
                                i * refSize,
                                new byte[refSize], 0, refSize));

                        BigInteger valueRef = new BigInteger(Util.arrayCopy(
                                data,
                                offset + physicalOffset + dataOffset +
                                (valueSize + i) * refSize,
                                new byte[refSize], 0, refSize));

                        keys.addLast(parseBinaryPlistValue(data, offset,
                                size, keyRef.intValue()));

                        values.addLast(parseBinaryPlistValue(data, offset,
                                size, valueRef.intValue()));
                    }

                    result =
                            new DictionaryEntry(marker, valueData, keys,
                            values);
                    break;
                }
                default:
                    throw new RuntimeException("Should not get here.");
                }
            }
            else {
                result = new UnknownEntry(marker);
            }
            break;
        }
        case 0x80:
            /* uid (?) */
            result = new UidEntry(marker);
            break;
        default:
            result = new UnknownEntry(marker);
            break;
        }

        return result;
    }

    public static abstract class Entry {
        private final byte marker;

        private Entry(byte marker) {
            this.marker = marker;
        }

        public byte getMarker() {
            return marker;
        }
    }

    public static class NullEntry extends Entry {
        private NullEntry() {
            super((byte) 0x00);
        }
    }

    public static class BooleanEntry extends Entry {
        private final boolean value;

        private BooleanEntry(boolean value) {
            super((byte) (value ? 0x09 : 0x08));
            this.value = value;
        }

        public boolean getValue() {
            return value;
        }
    }

    public static class FillEntry extends Entry {
        private FillEntry() {
            super((byte) 0x0F);
        }
    }

    public static class IntegerEntry extends Entry {
        private final byte[] valueData;
        private final BigInteger value;

        private IntegerEntry(byte marker, byte[] valueData, BigInteger value) {
            super(marker);
            this.valueData = valueData;
            this.value = value;
        }

        public byte[] getValueData() {
            return Util.arrayCopy(valueData, new byte[valueData.length]);
        }

        public BigInteger getValue() {
            return value;
        }
    }

    public static class DecimalEntry extends Entry {
        private final byte[] valueData;
        private final BigDecimal value;

        private DecimalEntry(byte marker, byte[] valueData, BigDecimal value) {
            super(marker);
            this.valueData = valueData;
            this.value = value;
        }

        public byte[] getValueData() {
            return Util.arrayCopy(valueData, new byte[valueData.length]);
        }

        public BigDecimal getValue() {
            return value;
        }
    }

    public static class DateEntry extends Entry {
        private final byte[] valueData;
        private final Date value;

        private DateEntry(byte marker, byte[] valueData, Date value) {
            super(marker);
            this.valueData = valueData;
            this.value = value;
        }

        public byte[] getValueData() {
            return Util.arrayCopy(valueData, new byte[valueData.length]);
        }

        public Date getValue() {
            return value;
        }
    }

    public static class DataEntry extends Entry {
        private final byte[] valueData;

        private DataEntry(byte marker, byte[] valueData) {
            super(marker);
            this.valueData = valueData;
        }

        public byte[] getValueData() {
            return Util.arrayCopy(valueData, new byte[valueData.length]);
        }

        public byte[] getValue() {
            return getValueData();
        }
    }

    public static class ASCIIStringEntry extends Entry {
        private final byte[] valueData;
        private final String value;

        private ASCIIStringEntry(byte marker, byte[] valueData) {
            super(marker);
            this.valueData = valueData;

            /*
             * Note: If there are characters outside the ASCII range they are
             * likely to be in a legacy Mac encoding, the most common of which
             * is MacRoman.
             */
            this.value = Util.readString(valueData, "MacRoman");
        }

        public byte[] getValueData() {
            return Util.arrayCopy(valueData, new byte[valueData.length]);
        }

        public String getValue() {
            return value;
        }
    }

    public static class UnicodeStringEntry extends Entry {
        private final byte[] valueData;
        private final String value;

        private UnicodeStringEntry(byte marker, byte[] valueData) {
            super(marker);
            this.valueData = valueData;
            this.value = Util.readString(valueData, "UTF-16BE");
        }

        public byte[] getValueData() {
            return Util.arrayCopy(valueData, new byte[valueData.length]);
        }

        public String getValue() {
            return value;
        }
    }

    public static class ArrayEntry extends Entry {
        private final byte[] valueData;
        private final LinkedList<Entry> entries;

        private ArrayEntry(byte marker, byte[] valueData,
                LinkedList<Entry> entries)
        {
            super(marker);
            this.valueData = valueData;
            this.entries = entries;
        }

        public byte[] getValueData() {
            return Util.arrayCopy(valueData, new byte[valueData.length]);
        }

        public LinkedList<Entry> getEntries() {
            return new LinkedList<Entry>(entries);
        }
    }

    public static class SetEntry extends ArrayEntry {
        /*
         * A Set is stored the same way as an array. Semantically I assume that
         * the difference is mainly that a set cannot have duplicates, but for
         * the purpose of reading the contents there is no difference.
         */

        public SetEntry(byte marker, byte[] valueData,
                LinkedList<Entry> entries)
        {
            super(marker, valueData, entries);
        }
    }

    public static class DictionaryEntry extends Entry {
        private final byte[] valueData;
        private final LinkedList<Entry> keys;
        private final ArrayList<Entry> values;

        private DictionaryEntry(byte marker, byte[] valueData,
                LinkedList<Entry> keys, LinkedList<Entry> values)
        {
            super(marker);
            this.valueData = valueData;
            this.keys = keys;
            /*
             * Put values in an array list since they will be looked up by index
             * during a normal iteration.
             */
            this.values = new ArrayList<Entry>(values);
        }

        public byte[] getValueData() {
            return Util.arrayCopy(valueData, new byte[valueData.length]);
        }

        public LinkedList<Entry> getKeys() {
            return new LinkedList<Entry>(keys);
        }

        public Entry getValue(int index) {
            return values.get(index);
        }
    }

    public static class UidEntry extends Entry {
        private UidEntry(byte marker) {
            super(marker);
        }
    }

    public static class UnknownEntry extends Entry {
        private UnknownEntry(byte marker) {
            super(marker);
        }
    }
}
