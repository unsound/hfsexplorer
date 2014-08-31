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

package org.catacombae.storage.io;

//import java.io.InputStream;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.io.RandomAccessStream;

/**
 *
 * @author erik
 */
public abstract class DataLocator {
    private boolean closed = false;
    private long references;

    public DataLocator() {
        this.references = 1;
    }

    /*
    public InputStream createStream() {
        // RandomAccessFileInputStream doesn't exist yet
        return new RandomAccessFileInputStream(createReadOnlyFile());
    }
    */
    public abstract ReadableRandomAccessStream createReadOnlyFile();

    /**
     * Returns whether or not this DataLocator supports creating writable
     * streams or if it's read only. If this method returns false
     *
     * @return whether or not this DataLocator supports creating writable
     * streams.
     */
    public abstract boolean isWritable();

    /**
     *
     * @return a readable and writable stream.
     * @throws java.lang.UnsupportedOperationException if this DataLocator is
     * not writable.
     */
    public abstract RandomAccessStream createReadWriteFile()
            throws UnsupportedOperationException;

    public final synchronized void close() {
        if(closed) {
            throw new RuntimeException("Already closed.");
        }

        dereference(true);
        closed = true;
    }

    private synchronized void dereference(final boolean closing) {
        if(((closed || closing) && references <= 0) ||
                (!(closed || closing) && references <= 1))
        {
            throw new RuntimeException("Too few references left!");
        }

        --references;

        if(references == 0) {
            releaseResources();
        }
    }

    protected abstract void releaseResources();

    public final synchronized void addReference(SubDataLocator l) {
        ++references;
    }

    public final synchronized void removeReference(SubDataLocator l) {
        dereference(false);
    }
}
