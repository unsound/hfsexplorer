/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
