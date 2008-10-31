/*-
 * Copyright (C) 2006-2008 Erik Larsson
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
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.io.RuntimeIOException;

/**
 * This class adds concurrency safety to a random access stream. It includes a seek+read
 * atomic operation. All operations on this object is synchronized on its own monitor.
 */
public class SynchronizedReadableRandomAccessStream extends BasicSynchronizedReadableRandomAccessStream implements SynchronizedReadableRandomAccess {
    /** The underlying stream. */
    private ReadableRandomAccessStream ras;
    private long refCount;
    private boolean closed = false;
    
    public SynchronizedReadableRandomAccessStream(ReadableRandomAccessStream sourceStream) {
	this.ras = sourceStream;
    }
    
    /**
     * Returns the backing stream for this SynchronizedReadableRandomAccessStream.
     * @return the backing stream for this SynchronizedReadableRandomAccessStream.
     */
    public ReadableRandomAccessStream getSourceStream() {
        return ras;
    }
    
    /** {@inheritDoc} */
    @Override
    public synchronized int readFrom(final long pos, byte[] b, int off, int len) throws RuntimeIOException {
        //System.err.println("SynchronizedReadableRandomAccessStream.readFrom(" + pos + ", byte[" + b.length + "], " + off + ", " + len + ");");
        final long oldFP = getFilePointer();
	if(oldFP != pos)
	    seek(pos);
	int res = read(b, off, len);
        
        if(oldFP != pos)
            seek(oldFP); // Reset file pointer to previous position
        return res;
    }
    
    /** {@inheritDoc} */
    @Override
    public synchronized long skipFrom(final long pos, final long length) throws RuntimeIOException {
	final long streamLength = length();
	final long newPos = pos+length;
        
        final long res;
	if(newPos > streamLength) {
	    //seek(streamLength);
	    res = streamLength-pos;
	}
	else {
	    //seek(newPos);
	    res = length;
	}
        
        return res;
    }
    
    /** {@inheritDoc} */
    @Override
    public synchronized long remainingLength() throws RuntimeIOException {
	return length()-getFilePointer();
    }
    
    /** {@inheritDoc} */
    @Override
    public synchronized void close() throws RuntimeIOException {
        if(refCount == 0) {
            ras.close();
            closed = true;
        }
        else
            throw new RuntimeIOException(refCount + " instances are still using this stream!");
    }

    /** {@inheritDoc} */
    @Override
    public synchronized long getFilePointer() throws RuntimeIOException {
	return ras.getFilePointer();
    }

    /** {@inheritDoc} */
    @Override
    public synchronized long length() throws RuntimeIOException {
	return ras.length();
    }

    /** {@inheritDoc} */
    @Override
    public synchronized int read() throws RuntimeIOException {
	return ras.read();
    }

    /** {@inheritDoc} */
    @Override
    public synchronized int read(byte[] b) throws RuntimeIOException {
	return ras.read(b);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized int read(byte[] b, int off, int len) throws RuntimeIOException {
        //System.err.println("SynchronizedReadableRandomAccessStream.read(byte[" + b.length + "], " + off + ", " + len + ");");

	return ras.read(b, off, len);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void seek(long pos) throws RuntimeIOException {
	ras.seek(pos);
    }
    
    /** {@inheritDoc} */
    @Override
    public synchronized void addReference(Object referrer) {
        if(!closed)
            ++refCount;
        else
            throw new RuntimeIOException("Stream is closed!");
    }
    
    /** {@inheritDoc} */
    @Override
    public synchronized void removeReference(Object referrer) {
        --refCount;
    }
}
