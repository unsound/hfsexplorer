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

package org.catacombae.jparted.lib.fs.hfscommon;

import java.io.InputStream;
import java.io.OutputStream;
import org.catacombae.hfsexplorer.io.ReadableRandomAccessInputStream;
import org.catacombae.hfsexplorer.io.SynchronizedReadableRandomAccessStream;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSForkData;
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
public class HFSCommonFSFork extends FSFork {
    private final HFSCommonFSFile parent;
    private final FSForkType type;
    private final CommonHFSForkData forkData;
    
    HFSCommonFSFork(HFSCommonFSFile iParent, FSForkType iType, CommonHFSForkData iForkData) {
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
