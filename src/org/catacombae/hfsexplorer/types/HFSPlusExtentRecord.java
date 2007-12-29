/*-
 * Copyright (C) 2006 Erik Larsson
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

package org.catacombae.hfsexplorer.types;

import java.io.PrintStream;

public class HFSPlusExtentRecord {
    /*
     * HFSPlusExtentDescriptor (typedef HFSPlusExtentDescriptor[8])
     * size: 64 bytes
     *
     * BP   Size  Type                        Variable name             Description
     * ----------------------------------------------------------------------------
     * 0    8*8   HFSPlusExtentDescriptor[8]  array
     */
    private final HFSPlusExtentDescriptor[] array = new HFSPlusExtentDescriptor[8];

    public HFSPlusExtentRecord(byte[] data, int offset) {
	for(int i = 0; i < array.length; ++i)
	    array[i] = new HFSPlusExtentDescriptor(data, offset+i*HFSPlusExtentDescriptor.getSize());
    }

    public HFSPlusExtentDescriptor getExtentDescriptor(int index) {
	return array[index];
    }
    public HFSPlusExtentDescriptor[] getExtentDescriptors() {
	HFSPlusExtentDescriptor[] arrayCopy = new HFSPlusExtentDescriptor[array.length];
	for(int i = 0; i < array.length; ++i)
	    arrayCopy[i] = array[i];
	return arrayCopy;
    }
    
    /**
     * Returns the number of extents that are in use, i.e. non-zero block count
     * and start block.
     * @return the number of extents that are in use.
     */
    public int getNumExtentsInUse() {
        for(int i = 0; i < array.length; ++i) {
            HFSPlusExtentDescriptor cur = array[i];
            if(cur.getBlockCount() == 0 &&
               cur.getStartBlock() == 0) {
                return i;
            }
        }
        return array.length;
    }
	
    public void print(PrintStream ps, int pregap) {
	String pregapString = "";
	for(int i = 0; i < pregap; ++i)
	    pregapString += " ";
	print(ps, pregapString);
    }
    public void print(PrintStream ps, String prefix) {
	    
	for(int i = 0; i < array.length; ++i) {
	    ps.println(prefix + "array[" + i + "]:");
	    array[i].print(ps, prefix + "  ");
	}
    }
}
