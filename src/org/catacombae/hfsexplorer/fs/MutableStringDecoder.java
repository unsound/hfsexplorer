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

import org.catacombae.hfsexplorer.types.hfscommon.StringDecoder;

/**
 *
 * @author Erik Larsson
 */
public class MutableStringDecoder <A extends StringDecoder>  implements StringDecoder {
    private A underlying;
    
    public MutableStringDecoder(A initialDecoder) {
        if(initialDecoder == null)
            throw new IllegalArgumentException("Can not construct a MutableStringDecoder with a " +
                    "null initial decoder.");
        this.underlying = initialDecoder;
    }
    
    @Override
    public String decode(byte[] data, int off, int len) {
        return underlying.decode(data, off, len);
    }
    
    public void setDecoder(A newDecoder) {
        this.underlying = newDecoder;
    }
    
    public A getDecoder() {
        return underlying;
    }
}
