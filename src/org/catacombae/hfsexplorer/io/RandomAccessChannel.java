/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.io;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

/**
 *
 * @author Erik
 */
public interface RandomAccessChannel extends ReadableByteChannel {

    public abstract long position()
            throws IOException;

    public abstract FileChannel position(long newPosition)
            throws IOException;

    public abstract long size()
            throws IOException;
}
