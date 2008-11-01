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

package org.catacombae.hfsexplorer.types.finder;

import java.io.PrintStream;
import org.catacombae.csjc.PrintableStruct;
import org.catacombae.csjc.StructElements;
import org.catacombae.csjc.structelements.Dictionary;
import org.catacombae.hfsexplorer.Util;
import org.catacombae.hfsexplorer.types.carbon.Point;

/**
 *
 * @author Erik Larsson
 */
public abstract class CommonFinderInfo implements StructElements, PrintableStruct {
    public static final int[] COLOR_1 = { 169, 169, 169 }; // Grey
    public static final int[] COLOR_2 = { 149, 228, 40 }; // Green
    public static final int[] COLOR_3 = { 255, 108, 185 }; // Violet
    public static final int[] COLOR_4 = { 0, 176, 229 }; // Blue
    public static final int[] COLOR_5 = { 255, 230, 32 }; // Yellow
    public static final int[] COLOR_6 = { 255, 64, 84 }; // Red
    public static final int[] COLOR_7 = { 255, 184, 31 }; // Orange
    
    public static final int kIsOnDesk       = 0x0001;
    public static final int kColor          = 0x000E;
    public static final int kIsShared       = 0x0040;
    public static final int kHasNoINITs     = 0x0080;
    public static final int kHasBeenInited  = 0x0100;
    public static final int kHasCustomIcon  = 0x0400;
    public static final int kIsStationery   = 0x0800;
    public static final int kNameLocked     = 0x1000;
    public static final int kHasBundle      = 0x2000;
    public static final int kIsInvisible    = 0x4000;
    public static final int kIsAlias        = 0x8000;

    protected final byte[] finderFlags = new byte[2];
    protected final Point location;
    protected final byte[] reservedField = new byte[2];
    
    protected CommonFinderInfo(byte[] data, int offset) {
	System.arraycopy(data, offset+8, finderFlags, 0, 2);
	location = new Point(data, offset+10);
	System.arraycopy(data, offset+14, reservedField, 0, 2);        
    }
    
    public static int length() { return 8; }
    
    public short getFinderFlags() { return Util.readShortBE(finderFlags); }
    public Point getLocation() { return location; }
    public short getReservedField() { return Util.readShortBE(reservedField); }
    
    // These should be placed in a common superclass of FolderInfo and FileInfo...
    public boolean getFinderFlagIsOnDesk() { return (getFinderFlags() & kIsOnDesk) != 0; }
    public byte getFinderFlagColor() { return (byte) ((getFinderFlags() & kColor) >> 1); }
    public int[] getFinderFlagColorRGB() {
	int color = getFinderFlagColor();
	switch(color) {
	case 0: return null;
	case 1: return COLOR_1;
	case 2: return COLOR_2;
	case 3: return COLOR_3;
	case 4: return COLOR_4;
	case 5: return COLOR_5;
	case 6: return COLOR_6;
	case 7: return COLOR_7;
	default: throw new RuntimeException("Color out of range! (" + color + ")");
	}
    }
    public boolean getFinderFlagIsShared() { return (getFinderFlags() & kIsShared) != 0; }
    public boolean getFinderFlagHasNoINITs() { return (getFinderFlags() & kHasNoINITs) != 0; }
    public boolean getFinderFlagHasBeenInited() { return (getFinderFlags() & kHasBeenInited) != 0; }
    public boolean getFinderFlagHasCustomIcon() { return (getFinderFlags() & kHasCustomIcon) != 0; }
    public boolean getFinderFlagIsStationery() { return (getFinderFlags() & kIsStationery) != 0; }
    public boolean getFinderFlagNameLocked() { return (getFinderFlags() & kNameLocked) != 0; }
    public boolean getFinderFlagHasBundle() { return (getFinderFlags() & kHasBundle) != 0; }
    public boolean getFinderFlagIsInvisible() { return (getFinderFlags() & kIsInvisible) != 0; }
    public boolean getFinderFlagIsAlias() { return (getFinderFlags() & kIsAlias) != 0; }
    
    public byte[] getBytes() {
        byte[] result = new byte[length()];
	byte[] tempData;
	int offset = 0;
        
        System.arraycopy(finderFlags, 0, result, offset, finderFlags.length); offset += finderFlags.length;
        tempData = location.getBytes();
	System.arraycopy(tempData, 0, result, offset, tempData.length); offset += tempData.length;
	System.arraycopy(reservedField, 0, result, offset, reservedField.length); offset += reservedField.length;
        
        return result;
    }
    
    @Override
    public void printFields(PrintStream ps, String prefix) {
	ps.println(prefix + " finderFlags: " + getFinderFlags());
	ps.println(prefix + " location: ");
	getLocation().print(ps, prefix+"  ");
	ps.println(prefix + " reservedField: " + getReservedField());
    }    
    
    @Override
    public Dictionary getStructElements() {
        DictionaryBuilder db = new DictionaryBuilder(CommonFinderInfo.class.getSimpleName());
        
        Dictionary finderFlagsDictionary;
        {
            DictionaryBuilder dbFinderFlags = new DictionaryBuilder("UInt16");
            /*
             * public static final int kIsOnDesk       = 0x0001;
             * public static final int kColor          = 0x000E;
             * public static final int kIsShared       = 0x0040;
             * public static final int kHasNoINITs     = 0x0080;
             * public static final int kHasBeenInited  = 0x0100;
             * public static final int kHasCustomIcon  = 0x0400;
             * public static final int kIsStationery   = 0x0800;
             * public static final int kNameLocked     = 0x1000;
             * public static final int kHasBundle      = 0x2000;
             * public static final int kIsInvisible    = 0x4000;
             * public static final int kIsAlias        = 0x8000;
             */
            dbFinderFlags.addFlag("kIsAlias", finderFlags, 15);
            dbFinderFlags.addFlag("kIsInvisible", finderFlags, 14);
            dbFinderFlags.addFlag("kHasBundle", finderFlags, 13);
            dbFinderFlags.addFlag("kNameLocked", finderFlags, 12);
            dbFinderFlags.addFlag("kIsStationery", finderFlags, 11);
            dbFinderFlags.addFlag("kHasCustomIcon", finderFlags, 10);
            dbFinderFlags.addFlag("kHasBeenInited", finderFlags, 8);
            dbFinderFlags.addFlag("kHasNoINITs", finderFlags, 7);
            dbFinderFlags.addFlag("kIsShared", finderFlags, 6);
            //dbFinderFlags.add(new IntegerField(finderFlags, 0, 1, 4, SIGNED, BIG_ENDIAN)); //TODO: improve IntegerField to handle arbitrary bitlength
            dbFinderFlags.addFlag("kIsOnDesk", finderFlags, 1);
            
            finderFlagsDictionary = db.getResult();
        }
        db.add("finderFlags", finderFlagsDictionary);
        db.add("location", location.getStructElements());
        db.addUIntBE("reservedField", reservedField);

        return db.getResult();
    }

}
