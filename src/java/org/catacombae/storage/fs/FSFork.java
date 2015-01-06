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

package org.catacombae.storage.fs;

import java.io.InputStream;
import java.io.OutputStream;
import org.catacombae.io.RandomAccessStream;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.io.TruncatableRandomAccessStream;
import org.catacombae.io.WritableRandomAccessStream;

/**
 * @author <a href="http://www.catacombae.org/" target="_top">Erik Larsson</a>
 */
public interface FSFork {

    /* Returns the type of this fork (if any).
     *
     * @return the type of this fork (if any).
     */
    public abstract FSForkType getType();

    /**
     * Returns the length (in bytes) of this fork.
     *
     * @return the length (in bytes) of this fork.
     */
    public abstract long getLength();

    /**
     * Returns whether or not the underlying implementation allows writing to
     * this fork. If this method returns true, you can use the methods:
     * <ul>
     * <li>getWritableRandomAccessStream()</li>
     * <li>getRandomAccessStream()</li>
     * </ul>
     *
     * @return whether or not the underlying implementation allows writing to
     * this file.
     */
    public abstract boolean isWritable();

    /**
     * Returns whether or not the underlying implementation allows changing the
     * length of this fork. If this method returns true, you can use the
     * methods:
     * <ul>
     * <li>getOutputStream()</li>
     * <li>getForkStream()</li>
     * </ul>
     * This property implies isWritable(). There should never be a situation
     * where isWritable() returns false while isTruncatable returns true.
     *
     * @return whether or not the underlying implementation allows writing to
     * this file.
     */
    public abstract boolean isTruncatable();

    /**
     * Returns whether or not the fork is compressed on disk.
     *
     * This does not affect the way that this fork can be used, so this is
     * purely advisory information that upper layers can use to indicate whether
     * compression is applied.
     *
     * @return whether or not the fork is compressed on disk.
     */
    public abstract boolean isCompressed();

    /**
     * Returns an identifier which distinguishes this fork from the other
     * available forks. It's up to the implementor to decide what strings to
     * return. In a NTFS file system, forks (called Alternate Data Streams) have
     * names, so the natural approach would be to return the name
     * "[filename]:[forkname]", Windows-style, or just "[filename]" for the main
     * fork.<br>
     * File systems with a fixed number of forks, like HFS+ which has two forks
     * for each file entry, the data fork and the resource fork, can simply
     * return the strings "Data fork" and "Resource fork". These strings will be
     * used as descriptions in any user interface using this library.
     *
     * @return an identifier which distinguishes this fork from the other
     * available forks.
     */
    public abstract String getForkIdentifier();

    /**
     * Returns whether or not this fork can be mapped to a UNIX extended
     * attribute, i.e. if there is a defined name for that extended attribute.
     *
     * @return whether or not this fork can be mapped to a UNIX extended
     * attribute.
     */
    public abstract boolean hasXattrName();

    /**
     * Returns the UNIX 'xattr'-style name of this fork, if applicable, or
     * <code>null</code> if no such mapping can be made.
     *
     * @return the UNIX 'xattr'-style name of this fork if possible, or
     * <code>null</code> otherwise.
     */
    public abstract String getXattrName();

    /**
     * Creates an input stream from which the file's contents can be read.
     * All implementations must support this method, and it is assumed that it's
     * properly implemented.
     *
     * @return an input stream from which the file's contents can be read.
     */
    public abstract InputStream getInputStream();

    /**
     * Creates a ReadableRandomAccessStream with access to the fork's contents.
     * All implementations must support this method, and it is assumed that it's
     * properly implemented.
     *
     * @return a ReadableRandomAccessStream with access to the fork's contents.
     */
    public abstract ReadableRandomAccessStream getReadableRandomAccessStream();

    /**
     * Opens a WritableRandomAccessStream with access to the fork contents
     * (optional operation).
     *
     * @return a WritableRandomAccessStream with access to the fork contents.
     * @throws java.lang.UnsupportedOperationException if the implementation
     * does not support writing to files. You can check this using
     * <code>isWritable()</code>.
     */
    public abstract WritableRandomAccessStream getWritableRandomAccessStream()
            throws UnsupportedOperationException;

    /**
     * Opens a RandomAccessStream with access to the fork contents (optional
     * operation). This does not allow changing the size of the fork, only
     * reading and writing those bytes that are already allocated.
     *
     * @return a RandomAccessStream with access to the fork contents.
     * @throws java.lang.UnsupportedOperationException if the implementation
     * does not support writing to files. You can check this using
     * <code>isWritable()</code>.
     */
    public abstract RandomAccessStream getRandomAccessStream()
            throws UnsupportedOperationException;

    /**
     * Truncates the file to length 0 and opens a new output stream where you
     * can write the new fork contents (optional operation).
     *
     * @return a new output stream where you can write the new fork contents.
     * @throws java.lang.UnsupportedOperationException if the implementation
     * does not support writing to files or changing their size. You can check
     * this using <code>isTruncatable()</code>.
     */
    public abstract OutputStream getOutputStream()
            throws UnsupportedOperationException;

    /**
     * Opens a read/write TruncatableRandomAccessStream with access to the fork
     * contents (optional operation). This is the most complete way of accessing
     * files on a file system as it supports all the data operations you can
     * perform on a file.
     *
     * @return a TruncatableRandomAccessStream with access to the fork contents.
     * @throws java.lang.UnsupportedOperationException if the implementation
     * does not support writing to files or changing their size. You can check
     * this using <code>isTruncatable()</code>.
     */
    public abstract TruncatableRandomAccessStream getForkStream()
            throws UnsupportedOperationException;

}
