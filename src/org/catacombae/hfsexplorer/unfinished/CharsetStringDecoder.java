/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.unfinished;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import org.catacombae.hfsexplorer.types.hfscommon.StringDecoder;

/**
 *
 * @author erik
 */
public class CharsetStringDecoder implements StringDecoder {
    private final CharsetDecoder decoder;
    private final String charsetName;

    public CharsetStringDecoder(String charsetName) throws IllegalCharsetNameException, IllegalArgumentException, UnsupportedCharsetException {
        this.charsetName = charsetName;
        Charset cs = Charset.forName(charsetName);
        this.decoder = cs.newDecoder();
    }

    public String decode(byte[] data, int off, int len) {
        try {
            return decoder.decode(ByteBuffer.wrap(data, off, len)).toString();
        } catch(CharacterCodingException e) {
            throw new RuntimeException("Could not decode data!", e);
        }
    }

    public String getCharsetName() {
        return charsetName;
    }
}
