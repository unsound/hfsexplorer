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

package org.catacombae.hfs.types.hfsplus;

import java.io.PrintStream;
import org.catacombae.csjc.StructElements;
import org.catacombae.csjc.structelements.ArrayBuilder;
import org.catacombae.csjc.structelements.Dictionary;

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
        this(false, data, offset);
    }

    private HFSPlusExtentRecord(final boolean mutable, byte[] data, int offset)
    {
        for(int i = 0; i < array.length; ++i) {
            if(mutable)
                array[i] = new HFSPlusExtentDescriptor.Mutable(data,
                        offset+i*HFSPlusExtentDescriptor.getSize());
            else
                array[i] = new HFSPlusExtentDescriptor(data,
                        offset+i*HFSPlusExtentDescriptor.getSize());
        }
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

    private void _set(HFSPlusExtentRecord rec) {
        for(int i = 0; i < this.array.length; ++i) {
            ((HFSPlusExtentDescriptor.Mutable) this.array[i]).set(
                    rec.array[i]);
        }
    }

    private void _setExtentDescriptor(int index,
            HFSPlusExtentDescriptor extentDescriptor)
    {
        if(index < 0 || index > this.array.length) {
            throw new RuntimeException("index out of range: " + index);
        }

        ((HFSPlusExtentDescriptor.Mutable) this.array[index]).set(
                extentDescriptor);
    }

    private void _setExtentDescriptors(
            HFSPlusExtentDescriptor[] extentDescriptors)
    {
        if(extentDescriptors.length != this.array.length) {
            throw new RuntimeException("Invalid length of array " +
                    "'extentDescriptors': " + extentDescriptors.length);
        }

        for(int i = 0; i < this.array.length; ++i) {
            this._setExtentDescriptor(i, extentDescriptors[i]);
        }
    }

    private HFSPlusExtentDescriptor.Mutable[] _getMutableExtentDescriptors() {
	HFSPlusExtentDescriptor.Mutable[] result =
                new HFSPlusExtentDescriptor.Mutable[array.length];
	for(int i = 0; i < array.length; ++i)
	    result[i] = (HFSPlusExtentDescriptor.Mutable) array[i];
	return result;
    }


    public static class Mutable extends HFSPlusExtentRecord {
        public Mutable(byte[] data, int offset) {
            super(true, data, offset);
        }

        public void set(HFSPlusExtentRecord rec) {
            super._set(rec);
        }

        public void setExtentDescriptor(int index,
                HFSPlusExtentDescriptor extentDescriptor)
        {
            super._setExtentDescriptor(index, extentDescriptor);
        }

        public void setExtentDescriptors(
                HFSPlusExtentDescriptor[] extentDescriptors)
        {
            super._setExtentDescriptors(extentDescriptors);
        }

        public HFSPlusExtentDescriptor.Mutable[] getMutableExtentDescriptors() {
            return super._getMutableExtentDescriptors();
        }
    }
}
