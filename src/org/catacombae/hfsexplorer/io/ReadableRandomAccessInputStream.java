/*-
 * Copyright (C) 2007-2008 Erik Larsson
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

import java.io.*;
import org.catacombae.io.RuntimeIOException;

/**
 * This class subclasses java.io.InputStream to transform a part of a RandomAccessStream
 * into an ordinary InputStream.
 */
public class ReadableRandomAccessInputStream extends InputStream {
    private final SynchronizedReadableRandomAccessStream ras;
    private long streamPos;
    private final long endPos;
    
    /** length == -1 means length == ras.length() */
    public ReadableRandomAccessInputStream(SynchronizedReadableRandomAccessStream ras, long offset, long length) {
	try {
	    long rasLength = ras.length();
	    if(length == -1)
		length = rasLength;
	    if(offset > rasLength || offset < 0)
		throw new IllegalArgumentException("offset out of bounds (offset=" +
                        offset + " length=" + length + ")");
	    if(length > rasLength-offset || length < 0)
		throw new IllegalArgumentException("length out of bounds (offset=" +
                        offset + " length=" + length + ")");
	    this.ras = ras;
	    this.streamPos = offset;
	    this.endPos = offset+length;
	} catch(Exception e) { throw new RuntimeException(e); }
    }
    
    /**
     * Constructs an InputStream that covers the data contained in the underlying
     * RandomAccessStream, from the beginning, to the end. */
    public ReadableRandomAccessInputStream(SynchronizedReadableRandomAccessStream ras) {
	this(ras, 0, -1);
    }
    
    @Override
    public int available() throws IOException {
	long remaining = endPos - streamPos;
	if(remaining > Integer.MAX_VALUE)
	    return Integer.MAX_VALUE;
	else if(remaining < Integer.MIN_VALUE)
	    return Integer.MIN_VALUE;
	else
	    return (int)remaining;
    }
    
    /** Does not do anything. The underlying SynchronizedReadableRandomAccessStream might be in use by others. */
    @Override    
    public void close() throws IOException {}
    
    /** Not supported, not implemented (not needed). */
    @Override
    public void mark(int readlimit) {
        throw new UnsupportedOperationException("Not supported");
    }

    /** Not supported, not implemented (not needed). */
    @Override
    public boolean markSupported() {
        return false;
    }
    
    @Override
    public int read() throws IOException {
	final byte[] tmp = new byte[1];
	int res = read(tmp, 0, 1);
	if(res == 1)
	    return tmp[0] & 0xFF;
	else
	    return -1;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }
    
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        try {
            int bytesToRead = (int)((streamPos+len > endPos)?endPos-streamPos:len);
            if(bytesToRead == 0)
                return -1;
            int res = ras.readFrom(streamPos, b, off, bytesToRead);
            if(res > 0) streamPos += res;
            return res;
        } catch(RuntimeIOException ex) {
            IOException ioe = ex.getIOCause();
            if(ioe != null) {
                ex.printStackTrace();
                throw ioe;
            }
            else
                throw ex;
        }
    }
    
    /** Not supported, not implemented (not needed). */
    @Override
    public void reset() throws IOException {
        throw new UnsupportedOperationException("Not supported");
    }
    
    @Override
    public long skip(long n) throws IOException {
        try {
            long res = ras.skipFrom(streamPos, n);
            if(res > 0) streamPos += res;
            return res;
        } catch(RuntimeIOException ex) {
            IOException ioe = ex.getIOCause();
            if(ioe != null) {
                ex.printStackTrace();
                throw ioe;
            }
            else
                throw ex;
        }
    }
}
