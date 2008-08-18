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

public class MutableGPTEntry extends GPTEntry {
    
    public MutableGPTEntry(int blockSize) {
	super(blockSize);
    }
    public MutableGPTEntry(GPTEntry source) {
	super(source);
    }
    
    public void setPartitionTypeGUID(byte[] data, int off) { copyData(data, off, partitionTypeGUID); }
    public void setUniquePartitionGUID(byte[] data, int off) { copyData(data, off, uniquePartitionGUID); }
    public void setStartingLBA(long i) { Util.arrayCopy(Util.toByteArrayLE(i), startingLBA); }
    public void setEndingLBA(long i) { Util.arrayCopy(Util.toByteArrayLE(i), endingLBA); }
    public void setAttributeBits(long i) { Util.arrayCopy(Util.toByteArrayBE(i), attributeBits); }
    public void setPartitionName(byte[] data, int off) { copyData(data, off, partitionName); }
    
    public void setFields(GPTEntry gptEntry) {
	super.copyFields(gptEntry);
    }
    
    private static void copyData(byte[] data, int off, byte[] dest) {
	copyData(data, off, dest, dest.length);
    }
    private static void copyData(byte[] data, int off, byte[] dest, int len) {
	if(off+len > data.length)
	    throw new IllegalArgumentException("Length of input data must be " + len + ".");
	System.arraycopy(data, off, dest, 0, len);
    }
    public void print(PrintStream ps, String prefix) {
	ps.println(prefix + "MutableGPTEntry:");
	printFields(ps, prefix);
    }
}
