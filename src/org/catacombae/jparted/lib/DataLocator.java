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
    public abstract RandomAccessStream createReadWriteFile();
}
