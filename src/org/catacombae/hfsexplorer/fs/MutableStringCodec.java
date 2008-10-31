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

/**
 * An implementation of StringCodec that wraps another StringCodec. Which codec is being wrapped can
 * be changed over time. This allows you to maintain one StringCodec instance across your app, while
 * still enabling you to alter the method used for coding.
 * 
 * @author <a href="http://hem.bredband.net/catacombae">Erik Larsson</a>
 */
public class MutableStringCodec <A extends StringCodec> implements StringCodec {
    private A underlying;
    
    public MutableStringCodec(A initialDecoder) {
        if(initialDecoder == null)
            throw new IllegalArgumentException("Can not construct a MutableStringDecoder with a " +
                    "null initial decoder.");
        this.underlying = initialDecoder;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String decode(byte[] data) {
        return underlying.decode(data);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String decode(byte[] data, int off, int len) {
        return underlying.decode(data, off, len);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] encode(String str) {
        return underlying.encode(str);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] encode(String str, int off, int len) {
        return underlying.encode(str, off, len);
    }
    
    public void setDecoder(A newDecoder) {
        this.underlying = newDecoder;
    }
    
    public A getDecoder() {
        return underlying;
    }
}
