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

/**
 *
 * @author Erik Larsson
 */
public abstract class CommonExtendedFinderInfo implements StructElements, PrintableStruct {
    public static final int kExtendedFlagsAreInvalid    = 0x8000;
    public static final int kExtendedFlagHasCustomBadge = 0x0100;
    public static final int kExtendedFlagHasRoutingInfo = 0x0004;
    
    protected final byte[] extendedFinderFlags = new byte[2];
    protected final byte[] reserved2 = new byte[2];
    protected final byte[] putAwayFolderID = new byte[4];
    
    protected CommonExtendedFinderInfo(byte[] data, int offset) {
	System.arraycopy(data, offset+8, extendedFinderFlags, 0, 2);
	System.arraycopy(data, offset+10, reserved2, 0, 2);
	System.arraycopy(data, offset+12, putAwayFolderID, 0, 4);        
    }
    
    private static final int length() { return 8; }
    
    public short getExtendedFinderFlags() { return Util.readShortBE(extendedFinderFlags); }
    public short getReserved2() { return Util.readShortBE(reserved2); }
    public int getPutAwayFolderID() { return Util.readIntBE(putAwayFolderID); }
    
    public boolean getExtendedFinderFlagExtendedFlagsAreInvalid() {
	return (getExtendedFinderFlags() & kExtendedFlagsAreInvalid) != 0;
    }
    public boolean getExtendedFinderFlagExtendedFlagHasCustomBadge() {
	return (getExtendedFinderFlags() & kExtendedFlagHasCustomBadge) != 0;
    }
    public boolean getExtendedFinderFlagExtendedFlagHasRoutingInfo() {
	return (getExtendedFinderFlags() & kExtendedFlagHasRoutingInfo) != 0;
    }

    @Override
    public void printFields(PrintStream ps, String prefix) {
	ps.println(prefix + " extendedFinderFlags: " + getExtendedFinderFlags());
	ps.println(prefix + " reserved2: " + getReserved2());
	ps.println(prefix + " putAwayFolderID: " + getPutAwayFolderID());
    }
    
    @Override
    public Dictionary getStructElements() {
         DictionaryBuilder db = new DictionaryBuilder(CommonExtendedFinderInfo.class.getName());
         
         Dictionary extendedFinderFlagsDict;
         {
             DictionaryBuilder dbExtendedFinderFlags = new DictionaryBuilder("UInt16");
             
             dbExtendedFinderFlags.addFlag("kExtendedFlagsAreInvalid", extendedFinderFlags, 15);
             dbExtendedFinderFlags.addFlag("kExtendedFlagHasCustomBadge", extendedFinderFlags, 8);
             dbExtendedFinderFlags.addFlag("kExtendedFlagHasRoutingInfo", extendedFinderFlags, 3);
             
             extendedFinderFlagsDict = dbExtendedFinderFlags.getResult();
         }
         db.add("extendedFinderFlags", extendedFinderFlagsDict);
         db.addUIntBE("reserved2", reserved2);
         db.addUIntBE("putAwayFolderID", putAwayFolderID);
         
         return db.getResult();
    }
    
    public byte[] getBytes() {
        byte[] result = new byte[length()];
	int offset = 0;
        
	System.arraycopy(extendedFinderFlags, 0, result, offset, extendedFinderFlags.length); offset += extendedFinderFlags.length;
	System.arraycopy(reserved2, 0, result, offset, reserved2.length); offset += reserved2.length;
	System.arraycopy(putAwayFolderID, 0, result, offset, putAwayFolderID.length); offset += putAwayFolderID.length;
        
        return result;
    }
}
