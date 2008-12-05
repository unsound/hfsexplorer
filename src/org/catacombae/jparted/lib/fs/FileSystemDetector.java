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

package org.catacombae.jparted.lib.fs;

import java.util.LinkedList;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.io.RuntimeIOException;
import org.catacombae.jparted.lib.DataLocator;

/**
 *
 * @author erik
 */
public class FileSystemDetector {

    public static FileSystemMajorType[] detectFileSystem(DataLocator inputDataLocator) {
        ReadableRandomAccessStream dlStream = inputDataLocator.createReadOnlyFile();
        FileSystemMajorType[] result = detectFileSystem(dlStream);
        dlStream.close();
        return result;
    }
    
    public static FileSystemMajorType[] detectFileSystem(ReadableRandomAccessStream fsStream) {
        long len;
        try {
            len = fsStream.length();
        } catch(RuntimeIOException e) {
            len = -1;
        }
        return detectFileSystem(fsStream, 0, len);
    }

    /**
     * Runs a file system detection test on <code>fsStream</code> to determine
     * what file system is present. As the detection engines are used defined
     * and may return false positives, a list of all positive detection results
     * is returned. It is up to the caller to sort out any false positives.
     *
     * @param fsStream the stream to search for known file systems.
     * @param off
     * @param len the length of the data area to scan for file systems, or -1 if
     * the length isn't currently known.
     * @return a list of matching file systems. If no matches were found, this
     * list will be empty (0 elements).
     */
    public static FileSystemMajorType[] detectFileSystem(ReadableRandomAccessStream fsStream,
            long off, long len) {

        LinkedList<FileSystemMajorType> result = new LinkedList<FileSystemMajorType>();
        
        for(FileSystemMajorType type : FileSystemMajorType.values()) {
            FileSystemHandlerFactory fact = type.createDefaultHandlerFactory();
            if(fact != null) {
                if(fact.getRecognizer().detect(fsStream, off, len))
                    result.add(type);
            }
        }

        return result.toArray(new FileSystemMajorType[result.size()]);
    }
}
