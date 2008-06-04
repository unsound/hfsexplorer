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
import java.util.zip.CRC32;

/** Updates a CRC32 checksum for each byte you read from the underlying stream.
    Seeking does not reset the checksum. It is only the read methods and what
    they return that alter the value of the checksum. */
public class CRC32FilterLLF implements LowLevelFile {
    private LowLevelFile source;
    private CRC32 checksum;
	
    public CRC32FilterLLF(LowLevelFile source) {
	this.source = source;
	this.checksum = new CRC32();
    }
	
    public int getChecksumValue() { return (int)(checksum.getValue() & 0xFFFFFFFF); }
    public void resetChecksum() { checksum.reset(); }
    
    public void seek(long pos) {
	source.seek(pos);
    }
    public int read() {
	int res = source.read();
	if(res > 0) checksum.update(res);
	return res;
    }
    public int read(byte[] data) {
	int res = source.read(data);
	if(res > 0) checksum.update(data, 0, res);
	return res;
    }
    public int read(byte[] data, int pos, int len) {
	int res = source.read(data);
	if(res > 0) checksum.update(data, pos, res);
	return res;
    }
    public void readFully(byte[] data) {
	source.readFully(data);
	checksum.update(data);
    }
    public void readFully(byte[] data, int offset, int length) {
	source.readFully(data, offset, length);
	checksum.update(data, offset, length);
    }
    public long length() { return source.length(); }
    public long getFilePointer() { return source.getFilePointer(); }
    public void close() { source.close(); }
}
