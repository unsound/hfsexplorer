/*-
 * Copyright (C) Erik Larsson
 *
 * All rights reserved.
 */
package org.catacombae.jparted.lib.fs.hfsplus;

import java.io.InputStream;
import java.io.OutputStream;
import org.catacombae.hfsexplorer.ForkFilter;
import org.catacombae.hfsexplorer.io.ReadableRandomAccessInputStream;
import org.catacombae.hfsexplorer.io.SynchronizedReadableRandomAccessStream;
import org.catacombae.hfsexplorer.types.HFSPlusForkData;
import org.catacombae.io.RandomAccessStream;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.io.TruncatableRandomAccessStream;
import org.catacombae.io.WritableRandomAccessStream;
import org.catacombae.jparted.lib.fs.FSFork;
import org.catacombae.jparted.lib.fs.FSForkType;

/**
 *
 * @author Erik Larsson
 */
public class HFSPlusFSFork extends FSFork {
    private final HFSPlusFSFile parent;
    private final FSForkType type;
    private final HFSPlusForkData forkData;
    
    HFSPlusFSFork(HFSPlusFSFile iParent, FSForkType iType, HFSPlusForkData iForkData) {
        super(iParent);
        
        // Input check
        if(iParent == null)
            throw new IllegalArgumentException("iParent must not be null!");
        if(iType == null)
            throw new IllegalArgumentException("iType must not be null!");
        else if(iType != FSForkType.DATA && iType != FSForkType.MACOS_RESOURCE)
            throw new IllegalArgumentException("iType is unsupported!");
        if(iForkData == null)
            throw new IllegalArgumentException("iForkData must not be null!");
        
        this.parent = iParent;
        this.type = iType;
        this.forkData = iForkData;
    }
    
    @Override
    public long getLength() {
        return forkData.getLogicalSize();
    }

    @Override
    public boolean isWritable() {
        return false; // Will be implemented in the future
    }

    @Override
    public boolean isTruncatable() {
        return false; // Will be implemented in the future
    }

    @Override
    public String getForkIdentifier() {
        switch(type) {
            case DATA:
                return "Data fork";
            case MACOS_RESOURCE:
                return "Resource fork";
            default:
                throw new RuntimeException("INTERNAL ERROR: Incorrect fork " +
                        "type: " + type);
        }
    }

    @Override
    public InputStream getInputStream() {
        return new ReadableRandomAccessInputStream(
                new SynchronizedReadableRandomAccessStream(
                        getReadableRandomAccessStream()));
    }

    @Override
    public ReadableRandomAccessStream getReadableRandomAccessStream() {
        switch(type) {
            case DATA:
                return parent.getReadableDataForkStream();
            case MACOS_RESOURCE:
                return parent.getReadableResourceForkStream();
            default:
                throw new RuntimeException("INTERNAL ERROR: Incorrect fork " +
                        "type: " + type);
        }
    }

    @Override
    public WritableRandomAccessStream getWritableRandomAccessStream() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public RandomAccessStream getRandomAccessStream() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public OutputStream getOutputStream() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TruncatableRandomAccessStream getForkStream() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
