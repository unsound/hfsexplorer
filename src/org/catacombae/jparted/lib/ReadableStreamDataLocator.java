/*-
 * Copyright (C) Erik Larsson
 *
 * All rights reserved.
 */
package org.catacombae.jparted.lib;

import org.catacombae.hfsexplorer.io.ReadableRandomAccessSubstream;
import org.catacombae.hfsexplorer.io.SynchronizedReadableRandomAccessStream;
import org.catacombae.io.RandomAccessStream;
import org.catacombae.io.ReadableRandomAccessStream;

/**
 *
 * @author Erik Larsson
 */
public class ReadableStreamDataLocator extends DataLocator {
    private SynchronizedReadableRandomAccessStream backingStream;
    
    public ReadableStreamDataLocator(ReadableRandomAccessStream sourceStream) {
        this.backingStream =
                new SynchronizedReadableRandomAccessStream(sourceStream);
    }
    
    @Override
    public ReadableRandomAccessStream createReadOnlyFile() {
        return new ReadableRandomAccessSubstream(backingStream);
    }

    @Override
    public RandomAccessStream createReadWriteFile() {
        throw new UnsupportedOperationException("Not supported for this implementation.");
    }

    @Override
    public boolean isWritable() {
        return false;
    }
    
    public SynchronizedReadableRandomAccessStream getBackingStream() {
        return backingStream;
    }
}
