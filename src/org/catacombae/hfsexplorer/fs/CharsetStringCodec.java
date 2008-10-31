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

package org.catacombae.hfsexplorer.fs;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;

/**
 * StringCodec that uses a CharsetDecoder internally.
 * 
 * @author Erik Larsson
 */
public class CharsetStringCodec implements StringCodec {
    private final String charsetName;
    private final CharsetDecoder decoder;
    private final CharsetEncoder encoder;
    
    /**
     * Creates a new CharsetStringCodec.
     * 
     * @param charsetName the name of the charset, for instance "ISO-8859-1", "UTF-16BE", "KOI8-R"
     * @throws java.nio.charset.IllegalCharsetNameException if the charset name doesn't match any
     * known charset.
     * @throws java.nio.charset.UnsupportedCharsetException if the requested charset is unsupported
     * by the Java libraries.
     */
    public CharsetStringCodec(String charsetName) throws IllegalCharsetNameException, UnsupportedCharsetException {
        this.charsetName = charsetName;
        Charset cs = Charset.forName(charsetName);
        this.decoder = cs.newDecoder();
        this.encoder = cs.newEncoder();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String decode(byte[] data) {
        return decode(data, 0, data.length);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String decode(byte[] data, int off, int len) {
        try {
            return decoder.decode(ByteBuffer.wrap(data, off, len)).toString();
        } catch(CharacterCodingException e) {
            throw new RuntimeException("Could not decode data!", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] encode(String str) {
        return encode(str, 0, str.length());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] encode(String str, int off, int len) {
        try {
            return encoder.encode(CharBuffer.wrap(str.toCharArray(), off, len)).array();
        } catch(CharacterCodingException e) {
            throw new RuntimeException("Could not encode data!", e);
        }
    }
    
    /**
     * Returns the charset name as it was passed to the constructor.
     * @return the charset name as it was passed to the constructor.
     */
    public String getCharsetName() {
        return charsetName;
    }
}
