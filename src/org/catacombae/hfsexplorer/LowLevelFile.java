/*-
 * Copyright (C) 2006 Erik Larsson
 * 
 * All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 */

package org.catacombae.hfsexplorer;

/* Designed to mimic a subset of RandomAccessFile. */

public interface LowLevelFile {
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
