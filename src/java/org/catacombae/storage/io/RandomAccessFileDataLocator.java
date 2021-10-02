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

package org.catacombae.storage.io;

import java.io.File;
import org.catacombae.io.ReadableConcatenatedStream;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.io.ReadableFileStream;
import org.catacombae.io.ConcatenatedStream;
import org.catacombae.io.RandomAccessStream;
import org.catacombae.io.FileStream;

/**
 * @author <a href="https://catacombae.org" target="_top">Erik Larsson</a>
 */
public class RandomAccessFileDataLocator extends DataLocator {
    private final File file;
    private final Long pos, len;
    private final boolean writable;

    /**
     * Creates a new DataLocator with a path to the backing file.
     *
     * @param pPath     path to the backing file.
     */
    public RandomAccessFileDataLocator(String pPath) {
        this(new File(pPath));
    }

    /**
     * Creates a new DataLocator with a path to the backing file.
     *
     * @param pPath     path to the backing file.
     * @param pWritable whether or not the data locator should be able to open
     *                  writable streams.
     */
    public RandomAccessFileDataLocator(String pPath, boolean pWritable) {
        this(new File(pPath), pWritable);
    }

    /**
     * Creates a new DataLocator with a path to the backing file.
     *
     * @param pPath     path to the backing file.
     * @param pPos      the start offset in the file for the data.
     * @param pLen      the length of the data being addressed.
     */
    public RandomAccessFileDataLocator(String pPath, long pPos, long pLen) {
        this(new File(pPath), pPos, pLen, true);
    }

    /**
     * Creates a new DataLocator with a path to the backing file.
     *
     * @param pPath     path to the backing file.
     * @param pPos      the start offset in the file for the data.
     * @param pLen      the length of the data being addressed.
     * @param pWritable whether or not the data locator should be able to open
     *                  writable streams.
     */
    public RandomAccessFileDataLocator(String pPath, long pPos, long pLen,
            boolean pWritable) {
        this(new File(pPath), pPos, pLen, pWritable);
    }

    /**
     * Creates a new DataLocator with File object pointing at the backing file.
     * The DataLocator will be writable.
     *
     * @param pFile the backing file.
     */
    public RandomAccessFileDataLocator(File pFile) {
        this(pFile, null, null, true);
    }

    /**
     * Creates a new DataLocator with File object pointing at the backing file.
     *
     * @param pFile     the backing file.
     * @param pWritable whether or not the data locator should be able to open
     *                  writable streams.
     */
    public RandomAccessFileDataLocator(File pFile, boolean pWritable) {
        this(pFile, null, null, pWritable);
    }

    /**
     * Creates a new DataLocator with File object pointing at the backing file.
     *
     * @param pFile     the backing file.
     * @param pPos      the start offset in the file for the data.
     * @param pLen      the length of the data being addressed.
     */
    public RandomAccessFileDataLocator(File pFile, long pPos, long pLen) {
        this(pFile, Long.valueOf(pPos), Long.valueOf(pLen), true);
    }

    /**
     * Creates a new DataLocator with File object pointing at the backing file.
     *
     * @param pFile     the backing file.
     * @param pPos      the start offset in the file for the data.
     * @param pLen      the length of the data being addressed.
     * @param pWritable whether or not the data locator should be able to open
     *                  writable streams.
     */
    public RandomAccessFileDataLocator(File pFile, long pPos, long pLen,
            boolean pWritable) {
        this(pFile, Long.valueOf(pPos), Long.valueOf(pLen), pWritable);
    }

    private RandomAccessFileDataLocator(File pFile, Long pPos, Long pLen,
            boolean writable) {
        if(!pFile.canRead())
            throw new RuntimeException("Can not read from file!");
        if(writable && !pFile.canWrite())
            throw new RuntimeException("Requested write mode but file is not " +
                    "writable.");

        this.file = pFile;
        this.pos = pPos;
        this.len = pLen;
        this.writable = writable;
    }

    @Override
    public ReadableRandomAccessStream createReadOnlyFile() {
        ReadableRandomAccessStream llf = new ReadableFileStream(file);
        if(pos != null && len != null)
            return new ReadableConcatenatedStream(llf, pos, len);
        else
            return llf;
    }

    @Override
    public RandomAccessStream createReadWriteFile() {
        RandomAccessStream wllf = new FileStream(file);
        if(pos != null && len != null)
            return new ConcatenatedStream(wllf, pos, len);
        else
            return wllf;
    }

    @Override
    public boolean isWritable() {
        return writable;
    }

    @Override
    public void releaseResources() {
        /* Our only persistent reference is the File object, which does not need
         * closing. */
    }
}
