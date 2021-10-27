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

package org.catacombae.hfs.original.macjapanese;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map.Entry;
import org.catacombae.hfs.original.SingleByteCodepageStringCodec;
import org.catacombae.hfs.original.StringCodec;
import org.catacombae.util.Log;
import org.catacombae.util.Util;

/**
 * StringCodec that decodes Mac OS Japanese.
 *
 * @author <a href="https://catacombae.org" target="_top">Erik Larsson</a>
 */
public class MacJapaneseStringCodec implements StringCodec {

    private static final HashMap<Short, String> macJapaneseToUnicodeMap;

    private static final HashMap<String, Short> unicodeToMacJapaneseMap;

    private static final Log log =
            Log.getInstance(MacJapaneseStringCodec.class);

    private final SingleByteCodepageStringCodec fallbackCodec;

    /**
     * Creates a new {@link MacJapaneseStringCodec}.
     *
     * @param fallbackCodec
     *      (optional) The fully mapped 8-bit charset codec to use for byte
     *      sequences that don't exist in the MacJapanese encoding. This can be
     *      <code>null</code> in which case an exception is thrown when
     *      encountering such sequences.
     */
    public MacJapaneseStringCodec(SingleByteCodepageStringCodec fallbackCodec) {
        this.fallbackCodec = fallbackCodec;
    }

    /**
     * Creates a new {@link MacJapaneseStringCodec} without a fallback codec.
     */
    public MacJapaneseStringCodec() {
        this(null);
    }

    /**
     * {@inheritDoc}
     */
    /* @Override */
    public String decode(byte[] data) {
        return decode(data, 0, data.length);
    }

    /**
     * {@inheritDoc}
     */
    /* @Override */
    public String decode(byte[] data, int off, int len) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < len;) {
            short sequence = 0;
            String replacement = null;

            log.debug("data[" + (off + i) + "]: " +
                    "0x" + Util.toHexStringBE(data[off + i]));

            if(i + 1 < len) {
                /*
                 * First check if there's a match for the 2-byte sequence
                 * (greedy matching).
                 * Note: Big endian
                 */

                sequence =
                        (short) (((data[off + i] & 0xFF) << 8) |
                        (data[off + i + 1] & 0xFF));
                replacement = macJapaneseToUnicodeMap.get(sequence);
                if(replacement != null) {
                    log.debug("data[" + (off + i) + "]: " +
                            "0x" + Util.toHexStringBE(data[off + i]));
                    i += 2;
                }
            }

            if(replacement == null) {
                sequence = (short) (data[off + i] & 0xFF);
                if(sequence < 0x20) {
                    replacement = new String(new char[] { (char) sequence });
                }
                else {
                    replacement = macJapaneseToUnicodeMap.get(sequence);
                }

                if(replacement != null) {
                    ++i;
                }
            }

            if(replacement == null && fallbackCodec != null) {
                sequence = (short) (data[off + i] & 0xFF);
                log.debug("Querying fallback codec for missing replacement " +
                        "for 0x" + Util.toHexStringBE((byte) sequence) + "...");
                replacement = fallbackCodec.decode(data, off + i - 1, 1);

                if(replacement != null) {
                    ++i;
                }
            }

            if(replacement == null) {
                throw new StringCodecException("Unable to decode sequence at " +
                        "byte " + i + ": 0x" + Util.toHexStringBE(sequence));
            }

            if(log.debug) {
                StringBuilder messageBuilder = new StringBuilder();

                messageBuilder.append("Found replacement: 0x").
                        append(Util.toHexStringBE(sequence)).append(" ->");
                for(int j = 0; j < replacement.length(); ++j) {
                    messageBuilder.append(" 0x").append(
                            Util.toHexStringBE(replacement.charAt(j)));
                }
                messageBuilder.append(" (\"").append(replacement).append("\")");

                log.debug(messageBuilder.toString());
            }

            sb.append(replacement);
        }

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    /* @Override */
    public byte[] encode(String str) {
        return encode(str, 0, str.length());
    }

    /**
     * {@inheritDoc}
     */
    /* @Override */
    public byte[] encode(String str, int off, int len) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        for(int i = 0; i < len;) {
            final int remaining = len - i;
            char firstChar;
            Short replacement = null;

            firstChar = str.charAt(off + i);
            if(firstChar < 0x20) {
                replacement = (short) firstChar;
            }
            else {
                for(int j = 5; j > 0; --j) {
                    if(remaining >= j) {
                        /* Check if the j-character substring has a match. */
                        replacement = unicodeToMacJapaneseMap.get(str.substring(
                                off + i, j));
                        if(replacement != null) {
                            i += j;
                            break;
                        }
                    }
                }
            }

            if(replacement == null && fallbackCodec != null) {
                byte[] data = fallbackCodec.encode(str.substring(off + i, 1));
                if(data == null || data.length != 1) {
                    throw new StringCodecException("Unexpected data length " +
                            "from fallback codec: " +
                            (data != null ? data.length : 0));
                }

                replacement = (short) (data[0] & 0xFF);
            }

            if(replacement == null) {
                throw new StringCodecException("Unable to encode sequence at " +
                        "character " + i + ": " +
                        "0x" + Util.toHexStringBE(str.charAt(i)));
            }

            if(replacement > 0xFF) {
                os.write((replacement >>> 8) & 0xFF);
            }

            os.write(replacement & 0xFF);
        }

        return os.toByteArray();
    }

    /**
     * Returns the charset name as it was passed to the constructor.
     * @return the charset name as it was passed to the constructor.
     */
    /* @Override */
    public String getCharsetName() {
        return "MacJapanese";
    }

    static {
        macJapaneseToUnicodeMap = new HashMap<Short, String>();
        for(int i = 0; i < MacJapaneseStringCodecData.mappingTable.length; ++i)
        {
            short key = (short) MacJapaneseStringCodecData.mappingTable[i][0];
            String value = new String(
                    MacJapaneseStringCodecData.mappingTable[i],
                    1,
                    MacJapaneseStringCodecData.mappingTable[i].length - 1);
            macJapaneseToUnicodeMap.put(key, value);
        }

        for(int i = 0; i < MacJapaneseStringCodecData2.mappingTable.length; ++i)
        {
            short key = (short) MacJapaneseStringCodecData2.mappingTable[i][0];
            String value = new String(
                    MacJapaneseStringCodecData2.mappingTable[i],
                    1,
                    MacJapaneseStringCodecData2.mappingTable[i].length - 1);
            macJapaneseToUnicodeMap.put(key, value);
        }

        for(int i = 0; i < MacJapaneseStringCodecData3.mappingTable.length; ++i)
        {
            short key = (short) MacJapaneseStringCodecData3.mappingTable[i][0];
            String value = new String(
                    MacJapaneseStringCodecData3.mappingTable[i],
                    1,
                    MacJapaneseStringCodecData3.mappingTable[i].length - 1);
            macJapaneseToUnicodeMap.put(key, value);
        }

        unicodeToMacJapaneseMap = new HashMap<String, Short>();
        for(Entry<Short, String> entry : macJapaneseToUnicodeMap.entrySet()) {
            unicodeToMacJapaneseMap.put(entry.getValue(), entry.getKey());
        }
    }
}
