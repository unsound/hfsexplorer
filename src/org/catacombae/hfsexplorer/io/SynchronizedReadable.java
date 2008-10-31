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

import org.catacombae.io.RuntimeIOException;

/**
 * Interface that defines methods to access a ReadableRandomAccessStream in a thread-safe way.
 * 
 * @author Erik Larsson
 */
public interface SynchronizedReadable {
    /** Atomic seek+read. Does <b>not</b> change the file pointer of the stream permanently! */
    public int readFrom(final long pos) throws RuntimeIOException;
    
    /** Atomic seek+read. Does <b>not</b> change the file pointer of the stream permanently! */
    public int readFrom(final long pos, byte[] b) throws RuntimeIOException;
    
    /** Atomic seek+read. Does <b>not</b> change the file pointer of the stream permanently! */
    public int readFrom(final long pos, byte[] b, int off, int len) throws RuntimeIOException;
    
    /** Atomic seek+read. Does <b>not</b> change the file pointer of the stream permanently! */
    public void readFullyFrom(final long pos, byte[] data) throws RuntimeIOException;
    
    /** Atomic seek+read. Does <b>not</b> change the file pointer of the stream permanently! */
    public void readFullyFrom(final long pos, byte[] data, int offset, int length) throws RuntimeIOException;
    
    /** Atomic seek+skip. Does <b>not</b> change the file pointer of the stream permanently! */
    public long skipFrom(final long pos, final long length) throws RuntimeIOException;
    
    /** Atomic length() - getFilePointer(). */
    public long remainingLength() throws RuntimeIOException;
}
