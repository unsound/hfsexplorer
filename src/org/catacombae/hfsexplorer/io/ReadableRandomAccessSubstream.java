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
 *
 * @author Erik Larsson
 */
public class ReadableRandomAccessSubstream extends BasicReadableRandomAccessStream {
    private SynchronizedReadableRandomAccess sourceStream;
    private long internalFP;
    
    public ReadableRandomAccessSubstream(SynchronizedReadableRandomAccess iSourceStream) {
        this.sourceStream = iSourceStream;
        this.internalFP = 0;
        
        sourceStream.addReference(this);
    }
    
    @Override
    public void close() throws RuntimeIOException {
        sourceStream.removeReference(this);
    }

    @Override
    public void seek(long pos) throws RuntimeIOException {
        internalFP = pos;
    }

    @Override
    public long length() throws RuntimeIOException {
        return sourceStream.length();
    }

    @Override
    public long getFilePointer() throws RuntimeIOException {
        return internalFP;
    }

    @Override
    public int read(byte[] b, int pos, int len) throws RuntimeIOException {
        //System.err.println("ReadableRandomAccessSubstream.read(byte[" + b.length + "], " + pos + ", " + len + ");");
        //System.err.println("  readFrom: " + internalFP);
        int bytesRead = sourceStream.readFrom(internalFP, b, pos, len);
        if(bytesRead > 0) {
            internalFP += bytesRead;
            //System.err.println("  returning: " + bytesRead);
            return bytesRead;
        }
        else {
            //System.err.println("  returning: -1");
            return -1;
        }
    }

}
