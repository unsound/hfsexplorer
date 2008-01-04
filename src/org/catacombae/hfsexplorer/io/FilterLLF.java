/*-
 * Copyright (C) 2007 Erik Larsson
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

package org.catacombae.hfsexplorer.io;

/**
 * Wraps a LowLevelFile inside this one and maps all operations one to
 * one to the underlying LowLevelFile.
 * There's no practical use for this class other than to facilitate
 * filtering subclasses which can override all operations.
 */
public class FilterLLF implements LowLevelFile {
    protected LowLevelFile backingStore;
    
    public FilterLLF(LowLevelFile backing) {
	this.backingStore = backing;
    }
    public void seek(long pos) {
	backingStore.seek(pos);
    }
    public int read() {
	return backingStore.read();
    }
    public int read(byte[] data) {
	return backingStore.read(data);
    }
    public int read(byte[] data, int pos, int len) {
	return backingStore.read(data, pos, len);
    }
    public void readFully(byte[] data) {
	backingStore.readFully(data);
    }
    public void readFully(byte[] data, int offset, int length) {
	backingStore.readFully(data, offset, length);
    }
    public long length() {
	return backingStore.length();
    }
    public long getFilePointer() {
	return backingStore.getFilePointer();
    }
    public void close() {
	backingStore.close();
    }
}
