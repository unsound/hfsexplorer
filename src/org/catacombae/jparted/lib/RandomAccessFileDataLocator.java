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

package org.catacombae.jparted.lib;

import java.io.File;
import org.catacombae.io.ReadableConcatenatedStream;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.io.ReadableFileStream;
import org.catacombae.io.ConcatenatedStream;
import org.catacombae.io.RandomAccessStream;
import org.catacombae.io.FileStream;

/**
 *
 * @author erik
 */
public class RandomAccessFileDataLocator extends DataLocator {
    private final File file;
    private final Long pos, len;
    
    public RandomAccessFileDataLocator(String pPath) {
        this(new File(pPath));
    }
    
    public RandomAccessFileDataLocator(String pPath, long pPos, long pLen) {
        this(new File(pPath), pPos, pLen);
    }
    public RandomAccessFileDataLocator(File pFile) {
        this(pFile, null, null);
    }
    
    public RandomAccessFileDataLocator(File pFile, long pPos, long pLen) {
        this(pFile, new Long(pPos), new Long(pLen));
    }
    
    private RandomAccessFileDataLocator(File pFile, Long pPos, Long pLen) {
        if(!pFile.canRead())
            throw new RuntimeException("Can not read from file!");

        this.file = pFile;
        this.pos = pPos;
        this.len = pLen;
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
        return true;
    }    
}
