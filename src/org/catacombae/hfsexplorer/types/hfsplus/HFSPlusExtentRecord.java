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

package org.catacombae.hfsexplorer.types.hfsplus;

import java.io.PrintStream;
import org.catacombae.csjc.StructElements;
import org.catacombae.csjc.structelements.ArrayBuilder;
import org.catacombae.csjc.structelements.Dictionary;
import org.catacombae.csjc.structelements.DictionaryBuilder;

public class HFSPlusExtentRecord implements StructElements {
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

    public int length() {
        int res = 0;
        for(HFSPlusExtentDescriptor desc : array)
            res += desc.getSize();
        return res;
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

    public byte[] getBytes() {
        byte[] result = new byte[length()];
	byte[] tempData;
	int offset = 0;
        
        for(HFSPlusExtentDescriptor desc : array) {
            tempData = desc.getBytes();
            System.arraycopy(tempData, 0, result, offset, tempData.length); offset += tempData.length;
        }
        
        return result;
    }

    public Dictionary getStructElements() {
        DictionaryBuilder db = new DictionaryBuilder(HFSPlusExtentRecord.class.getSimpleName());

        {
            ArrayBuilder ab = new ArrayBuilder("HFSPlusExtentDescriptor[8]");

            for(HFSPlusExtentDescriptor descriptor : array)
                ab.add(descriptor.getStructElements());
            
            db.add("array", ab.getResult());
        }

        return db.getResult();
    }
}
