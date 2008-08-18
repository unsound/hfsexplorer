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

package org.catacombae.hfsexplorer.partitioning;
import org.catacombae.hfsexplorer.Util;
import java.io.PrintStream;

public class MutableGPTHeader extends GPTHeader {
    public MutableGPTHeader(int blockSize) {
	super(blockSize);
    }
    public MutableGPTHeader(GPTHeader source) {
	super(source);
    }
    
    public void setFields(GPTHeader source) {
	setFieldsInternal(source);
    }
    
    /** DON'T EVER USE THIS METHOD! The signature is set automatically when an object
	is created so this method should not be called under normal circumstances. */
    public void setSignature(byte[] data, int off) { copyData(data, off, signature); }
    public void setRevision(int i)                 { Util.arrayCopy(Util.toByteArrayLE(i), revision); }
    public void setHeaderSize(int i)               { Util.arrayCopy(Util.toByteArrayLE(i), headerSize); }
    public void setCRC32Checksum(int i)            { Util.arrayCopy(Util.toByteArrayLE(i), crc32Checksum); }
    /** DON'T EVER USE THIS METHOD! At least not with any other argument than 0.
	(Won't be checked, but EFI 1.10 specification only allows 0) */
    public void setReserved1(int i)                { Util.arrayCopy(Util.toByteArrayBE(i), reserved1); }
    public void setPrimaryLBA(long i)              { Util.arrayCopy(Util.toByteArrayLE(i), primaryLBA); }
    public void setBackupLBA(long i)               { Util.arrayCopy(Util.toByteArrayLE(i), backupLBA); }
    public void setFirstUsableLBA(long i)          { Util.arrayCopy(Util.toByteArrayLE(i), firstUsableLBA); }
    public void setLastUsableLBA(long i)           { Util.arrayCopy(Util.toByteArrayLE(i), lastUsableLBA); }
    public void setDiskGUID(byte[] data, int off)   { copyData(data, off, diskGUID); }
    public void setPartitionEntryLBA(long i)       { Util.arrayCopy(Util.toByteArrayLE(i), partitionEntryLBA); }
    public void setNumberOfPartitionEntries(int i) { Util.arrayCopy(Util.toByteArrayLE(i), numberOfPartitionEntries); }
    public void setSizeOfPartitionEntry(int i)     { Util.arrayCopy(Util.toByteArrayLE(i), sizeOfPartitionEntry); }
    public void setPartitionEntryArrayCRC32(int i) { Util.arrayCopy(Util.toByteArrayLE(i), partitionEntryArrayCRC32); }
    /** DON'T EVER USE THIS METHOD! At least not with any other argument than 000....
	(Won't be checked, but EFI 1.10 specification only allows this field to be zeroed) */
    public void setReserved2(byte[] data, int off) { copyData(data, off, reserved2); }
    
    private static void copyData(byte[] data, int off, byte[] dest) {
	copyData(data, off, dest, dest.length);
    }
    private static void copyData(byte[] data, int off, byte[] dest, int len) {
	if(off+len > data.length)
	    throw new IllegalArgumentException("Length of input data must be " + len + ".");
	System.arraycopy(data, off, dest, 0, len);
    }
    public void print(PrintStream ps, String prefix) {
	ps.println(prefix + "MutableGPTHeader:");
	printFields(ps, prefix);
    }
}
