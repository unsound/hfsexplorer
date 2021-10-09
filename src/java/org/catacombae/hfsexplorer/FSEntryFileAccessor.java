/*-
 * Copyright (C) 2021 Erik Larsson
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

import org.catacombae.dmg.sparsebundle.FileAccessor;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.storage.fs.FSEntry;
import org.catacombae.storage.fs.FSFile;
import org.catacombae.storage.fs.FSFolder;
import org.catacombae.storage.fs.FSFork;

/**
 * Bridge class between an {@link FSEntry} and a sparse bundle
 * {@link FileAccessor}.
 *
 * @author Erik Larsson
 */
public class FSEntryFileAccessor implements FileAccessor {

    final FSEntry e;

    public FSEntryFileAccessor(FSEntry e) {
        this.e = e;
    }

    public FileAccessor[] listFiles() {
        if(!(e instanceof FSFolder)) {
            return null;
        }

        FSFolder f = (FSFolder) e;
        FSEntry[] subEntries = f.listEntries();
        FileAccessor[] subAccessors = new FileAccessor[subEntries.length];
        for(int i = 0; i < subEntries.length; ++i) {
            subAccessors[i] = new FSEntryFileAccessor(subEntries[i]);
        }

        return subAccessors;
    }

    public boolean isFile() {
        return (e instanceof FSFile);
    }

    public boolean isDirectory() {
        return (e instanceof FSFolder);
    }

    public String getName() {
        return e.getName();
    }

    public String getAbsolutePath() {
        /*
         * We don't actually have access to the full path, but it's only used
         * for error reporting purposes so we can just return the name here.
         */
        return getName();
    }

    public boolean exists() {
        return true;
    }

    public FileAccessor lookupChild(String name) {
        if(!(e instanceof FSFolder)) {
            return null;
        }

        FSFolder f = (FSFolder) e;
        return new FSEntryFileAccessor(f.getChild(name));
    }

    public long length() {
        if(!(e instanceof FSFile)) {
            return 0;
        }

        FSFile f = (FSFile) e;
        FSFork mainFork = f.getMainFork();
        if(mainFork == null) {
            return 0;
        }

        return mainFork.getLength();
    }

    public ReadableRandomAccessStream createReadableStream() {
        if(!(e instanceof FSFile)) {
            throw new RuntimeException("Can only create a stream for files.");
        }

        FSFile f = (FSFile) e;
        FSFork mainFork = f.getMainFork();
        return mainFork.getReadableRandomAccessStream();

    }

    public void lock() {
        /* Note: No-op now. Would be needed if we ever implemented write
         * support. */
    }

    public void unlock() {
        /* Note: No-op now. Would be needed if we ever implemented write
         * support. */
    }

    public void close() {
        /* Note: No-op now and possibly forever. */
    }
}
