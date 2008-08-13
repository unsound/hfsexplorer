/*-
 * Copyright (C) Erik Larsson
 *
 * All rights reserved.
 */
package org.catacombae.hfsexplorer.io;

import org.catacombae.io.BasicReadableRandomAccessStream;
import org.catacombae.io.RuntimeIOException;

/**
 *
 * @author Erik Larsson
 */
public class ReadableRandomAccessSubstream extends BasicReadableRandomAccessStream {
    private SynchronizedReadableRandomAccessStream sourceStream;
    private long internalFP;
    
    public ReadableRandomAccessSubstream(SynchronizedReadableRandomAccessStream iSourceStream) {
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
        //System.err.println("ReadableRandomAccessSubstream.read(" + b + ", " + pos + ", " + len + ");");
        int bytesRead = sourceStream.readFrom(internalFP, b, pos, len);
        if(bytesRead > 0) {
            internalFP += bytesRead;
            return bytesRead;
        }
        else
            return -1;
    }

}
