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

package org.catacombae.hfsexplorer.io;

import org.catacombae.io.BasicReadableRandomAccessStream;
import org.catacombae.io.RuntimeIOException;

/**
 * Basic implementation of convenience methods in a SynchronizedReadableRandomAccess.
 * 
 * @author <a href="http://hem.bredband.net/catacombae">Erik Larsson</a>
 */
public abstract class BasicSynchronizedReadableRandomAccessStream extends BasicReadableRandomAccessStream implements SynchronizedReadableRandomAccess {
    /** {@inheritDoc} */
    @Override
    public int readFrom(long pos) throws RuntimeIOException {
        byte[] res = new byte[1];
        if(readFrom(pos, res, 0, 1) == 1)
            return res[0] & 0xFF;
        else
            return -1;
    }

    /** {@inheritDoc} */
    @Override
    public int readFrom(long pos, byte[] b) throws RuntimeIOException {
        return readFrom(pos, b, 0, b.length);
    }
    
    /** {@inheritDoc} */
    @Override
    public void readFullyFrom(long pos, byte[] data) throws RuntimeIOException {
	readFullyFrom(pos, data, 0, data.length);
    }

    /** {@inheritDoc} */
    @Override
    public void readFullyFrom(long pos, byte[] data, int offset, int length) throws RuntimeIOException {
        if(length < 0)
            throw new IllegalArgumentException("length is negative: " + length);
	int bytesRead = 0;
	while(bytesRead < length) {
	    int curBytesRead = readFrom(pos+bytesRead, data, offset+bytesRead, length-bytesRead);
	    if(curBytesRead > 0) bytesRead += curBytesRead;
	    else 
		throw new RuntimeIOException("Couldn't read the entire length.");
	}
    }
}
