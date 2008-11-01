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
