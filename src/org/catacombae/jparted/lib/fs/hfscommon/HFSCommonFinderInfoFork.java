/*-
 * Copyright (C) 2009 Erik Larsson
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
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSFinderInfo;
import org.catacombae.io.RandomAccessStream;
import org.catacombae.io.ReadableByteArrayStream;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.io.TruncatableRandomAccessStream;
import org.catacombae.io.WritableRandomAccessStream;
import org.catacombae.jparted.lib.fs.FSFork;

/**
 *
 * @author erik
 */
public class HFSCommonFinderInfoFork implements FSFork {
    private final CommonHFSFinderInfo finderInfo;

    public HFSCommonFinderInfoFork(CommonHFSFinderInfo finderInfo) {
        this.finderInfo = finderInfo;
    }

    public long getLength() {
        return 32;
    }

    public boolean isWritable() {
        return false;
    }

    public boolean isTruncatable() {
        return false;
    }

    public String getForkIdentifier() {
        return "FinderInfo";
    }

    public boolean hasXattrName() {
        return true;
    }

    public String getXattrName() {
        return "com.apple.FinderInfo";
    }

    public InputStream getInputStream() {
        return new ReadableRandomAccessInputStream(
                new SynchronizedReadableRandomAccessStream(
                getReadableRandomAccessStream()));
    }

    public ReadableRandomAccessStream getReadableRandomAccessStream() {
        return new ReadableByteArrayStream(finderInfo.getBytes());
    }

    public WritableRandomAccessStream getWritableRandomAccessStream() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public RandomAccessStream getRandomAccessStream() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public OutputStream getOutputStream() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public TruncatableRandomAccessStream getForkStream() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
