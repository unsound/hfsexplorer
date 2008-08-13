/*-
 * Copyright (C) 2006 Erik Larsson
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
import org.catacombae.io.BasicReadableRandomAccessStream;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.io.RuntimeIOException;

/**
 * This class adds concurrency safety to a random access stream. It includes a seek+read
 * atomic operation. All operations on this object is synchronized on its own monitor.
 */
public class SynchronizedReadableRandomAccessStream extends BasicReadableRandomAccessStream {
    /** The underlying stream. */
    private ReadableRandomAccessStream ras;
    private long refCount;
    private boolean closed = false;
    
    public SynchronizedReadableRandomAccessStream(ReadableRandomAccessStream ras) {
	this.ras = ras;
    }
     
    /** Atomic seek+read. */
    public synchronized int readFrom(long pos, byte[] b, int off, int len) throws RuntimeIOException {
        //System.err.println("SynchronizedReadableRandomAccessStream.readFrom(" + pos + ", " + b + ", " + off + ", " + len + ");");

	if(getFilePointer() != pos)
	    seek(pos);
	return read(b, off, len);
    }
    
    /** Atomic seek+skip. */
    public synchronized long skipFrom(final long pos, final long length) throws RuntimeIOException {
	long streamLength = length();
	long newPos = pos+length;

	if(newPos > streamLength) {
	    seek(streamLength);
	    return streamLength-pos;
	}
	else {
	    seek(newPos);
	    return length;
	}
    }
    
    /** Atomic length() - getFilePointer(). */
    public synchronized long remainingLength() throws RuntimeIOException {
	return length()-getFilePointer();
    }
    
    /** @see java.io.RandomAccessFile */
    public synchronized void close() throws RuntimeIOException {
        if(refCount == 0) {
            ras.close();
            closed = true;
        }
        else
            throw new RuntimeIOException(refCount + " instances are still using this stream!");
    }

    /** @see java.io.RandomAccessFile */
    public synchronized long getFilePointer() throws RuntimeIOException {
	return ras.getFilePointer();
    }

    /** @see java.io.RandomAccessFile */
    public synchronized long length() throws RuntimeIOException {
	return ras.length();
    }

    /** @see java.io.RandomAccessFile */
    @Override
    public synchronized int read() throws RuntimeIOException {
	return ras.read();
    }

    /** @see java.io.RandomAccessFile */
    @Override
    public synchronized int read(byte[] b) throws RuntimeIOException {
	return ras.read(b);
    }

    /** @see java.io.RandomAccessFile */
    public synchronized int read(byte[] b, int off, int len) throws RuntimeIOException {
        //System.err.println("SynchronizedReadableRandomAccessStream.read(" + b + ", " + off + ", " + len + ");");

	return ras.read(b, off, len);
    }

    /** @see java.io.RandomAccessFile */
    public synchronized void seek(long pos) throws RuntimeIOException {
	ras.seek(pos);
    }
    
    public synchronized void addReference(Object referrer) {
        if(!closed)
            ++refCount;
        else
            throw new RuntimeIOException("Stream is closed!");
    }
    
    public synchronized void removeReference(Object referrer) {
        --refCount;
    }
}
