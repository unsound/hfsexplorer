/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.jparted.lib;

//import java.io.InputStream;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.io.RandomAccessStream;

/**
 *
 * @author erik
 */
public abstract class DataLocator {
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
}
