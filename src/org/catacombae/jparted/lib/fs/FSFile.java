/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.jparted.lib.fs;

import java.io.InputStream;
import java.io.OutputStream;
import org.catacombae.io.RandomAccessStream;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.io.WritableRandomAccessStream;

/**
 *
 * @author Erik
 */
public abstract class FSFile extends FSEntry {
    /**
     * Returns the length of this file, in bytes.
     * @return the length of this file, in bytes.
     */
    public abstract long getLength();
    
    /**
     * Returns whether or not the underlying implementation allows writing to
     * this file.
     * @return whether or not the underlying implementation allows writing to
     * this file.
     */
    public abstract boolean isWritable();
    
    /**
     * Creates an input stream from which the file's contents can be read.
     * 
     * @return an input stream from which the file's contents can be read.
     */
    public abstract InputStream getInputStream();
    
    public abstract ReadableRandomAccessStream getReadableRandomAccessStream();
    
    /**
     * Truncates the file to length 0 and opens a new output stream where you
     * can write the new file contents (optional operation).
     * 
     * @return
     * @throws java.lang.UnsupportedOperationException
     */
    public abstract OutputStream getOutputStream()
            throws UnsupportedOperationException;

    /**
     * Opens a WritableRandomAccessStream
     * @return
     * @throws java.lang.UnsupportedOperationException
     */
    public abstract WritableRandomAccessStream getWritableRandomAccessStream()
            throws UnsupportedOperationException;
    
    public abstract RandomAccessStream getRandomAccessStream()
            throws UnsupportedOperationException;
}
