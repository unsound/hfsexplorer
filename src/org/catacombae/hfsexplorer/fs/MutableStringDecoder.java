/*-
 * Copyright (C) Erik Larsson
 *
 * All rights reserved.
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
