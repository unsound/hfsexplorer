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

package org.catacombae.hfsexplorer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.catacombae.io.InputStreamReadable;
import org.catacombae.io.OutputStreamWritable;
import org.catacombae.io.Readable;
import org.catacombae.io.RuntimeIOException;
import org.catacombae.io.Writable;

/**
 * IO-specific utility class.
 * 
 * @author Erik Larsson
 */
public class IOUtil extends org.catacombae.util.IOUtil {
    public static long streamCopy(InputStream is, OutputStream os, int bufferSize) throws IOException {
        try {
            return streamCopy(new InputStreamReadable(is), new OutputStreamWritable(os), bufferSize);
        } catch(RuntimeIOException e) {
            IOException cause = e.getIOCause();
            if(cause != null)
                throw cause;
            else
                throw e;
        }
    }

    public static long streamCopy(Readable is, OutputStream os, int bufferSize) throws IOException {
        try {
            return streamCopy(is, new OutputStreamWritable(os), bufferSize);
        } catch(RuntimeIOException e) {
            IOException cause = e.getIOCause();
            if(cause != null)
                throw cause;
            else
                throw e;
        }
    }

    public static long streamCopy(InputStream is, Writable os, int bufferSize) throws IOException {
        try {
            return streamCopy(new InputStreamReadable(is), os, bufferSize);
        } catch(RuntimeIOException e) {
            IOException cause = e.getIOCause();
            if(cause != null)
                throw cause;
            else
                throw e;
        }
    }

    /**
     * Copies the entire readable stream <code>is</code> to the writable stream <code>os</code>.
     *
     * @param is
     * @param os
     * @param blockSize
     * @throws java.io.IOException
     */
    public static long streamCopy(Readable is, Writable os, int bufferSize) throws RuntimeIOException {
        byte[] buffer = new byte[bufferSize];
        long totalBytesCopied = 0;
        int bytesRead;

        while((bytesRead = is.read(buffer)) > 0) {
            os.write(buffer, 0, bytesRead);
            totalBytesCopied += bytesRead;
        }

        return totalBytesCopied;
    }
}
