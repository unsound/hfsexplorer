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

/**
 *
 * @author erik
 */
public interface AbstractFile {
    public void seek(long pos);
    public int read();
    public int read(byte[] data);
    public int read(byte[] data, int pos, int len);
    public void readFully(byte[] data);
    public void readFully(byte[] data, int offset, int length);
    public long length();
    public long getFilePointer();
    public void close();
}
