/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.jparted.lib;

import org.catacombae.io.ConcatenatedStream;
import org.catacombae.io.RandomAccessStream;
import org.catacombae.io.ReadableConcatenatedStream;
import org.catacombae.io.ReadableRandomAccessStream;

/**
 *
 * @author erik
 */
public class SubDataLocator extends DataLocator {
    private DataLocator source;
    private long offset;
    private long length;

    public SubDataLocator(DataLocator source, long offset, long length) {
        this.source = source;
        this.offset = offset;
        this.length = length;
    }

    @Override
    public ReadableRandomAccessStream createReadOnlyFile() {
        return new ReadableConcatenatedStream(source.createReadOnlyFile(), offset, length);
    }

    @Override
    public boolean isWritable() {
        return source.isWritable();
    }

    @Override
    public RandomAccessStream createReadWriteFile() throws UnsupportedOperationException {
        return new ConcatenatedStream(source.createReadWriteFile(), offset, length);
    }

}
