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

import org.catacombae.hfsexplorer.Util;
import java.io.PrintStream;
import org.catacombae.csjc.StructElements;
import org.catacombae.csjc.structelements.Dictionary;

public class HFSCatalogNodeID implements StructElements {
    /*
     * HFSCatalogNodeID (typedef UInt32)
     * size: 4 bytes
     *
     * BP   Size  Type              Variable name     Description
     * -----------------------------------------------------------
     * 0    4     UInt32            hfsCatalogNodeID
     */
    
    public static final HFSCatalogNodeID kHFSRootParentID            = new HFSCatalogNodeID(1);
    public static final HFSCatalogNodeID kHFSRootFolderID            = new HFSCatalogNodeID(2);
    public static final HFSCatalogNodeID kHFSExtentsFileID           = new HFSCatalogNodeID(3);
    public static final HFSCatalogNodeID kHFSCatalogFileID           = new HFSCatalogNodeID(4);
    public static final HFSCatalogNodeID kHFSBadBlockFileID          = new HFSCatalogNodeID(5);
    public static final HFSCatalogNodeID kHFSAllocationFileID        = new HFSCatalogNodeID(6);
    public static final HFSCatalogNodeID kHFSStartupFileID           = new HFSCatalogNodeID(7);
    public static final HFSCatalogNodeID kHFSAttributesFileID        = new HFSCatalogNodeID(8);
    public static final HFSCatalogNodeID kHFSRepairCatalogFileID     = new HFSCatalogNodeID(14);
    public static final HFSCatalogNodeID kHFSBogusExtentFileID       = new HFSCatalogNodeID(15);
    public static final HFSCatalogNodeID kHFSFirstUserCatalogNodeID  = new HFSCatalogNodeID(16);
    
    private final byte[] hfsCatalogNodeID = new byte[4];
	
    public HFSCatalogNodeID(byte[] data, int offset) {
	System.arraycopy(data, offset, hfsCatalogNodeID, 0, 4);
    }
    public HFSCatalogNodeID(int nodeID) {
	System.arraycopy(Util.toByteArrayBE(nodeID), 0, hfsCatalogNodeID, 0, 4);
    }
    
    public static int length() { return 4; }

    public int toInt() { return Util.readIntBE(hfsCatalogNodeID); }
    public long toLong() { return Util.unsign(toInt()); }
    public String getDescription() {
	/*
	 * kHFSRootParentID            = 1,
	 * kHFSRootFolderID            = 2,
	 * kHFSExtentsFileID           = 3,
	 * kHFSCatalogFileID           = 4,
	 * kHFSBadBlockFileID          = 5,
	 * kHFSAllocationFileID        = 6,
	 * kHFSStartupFileID           = 7,
	 * kHFSAttributesFileID        = 8,
	 * kHFSRepairCatalogFileID     = 14,
	 * kHFSBogusExtentFileID       = 15,
	 * kHFSFirstUserCatalogNodeID  = 16
	 */
	String result;
	switch(toInt()) {
	case 1:
	    result = "kHFSRootParentID";
	    break;
	case 2:
	    result = "kHFSRootFolderID";
	    break;
	case 3:
	    result = "kHFSExtentsFileID";
	    break;
	case 4:
	    result = "kHFSCatalogFileID";
	    break;
	case 5:
	    result = "kHFSBadBlockFileID";
	    break;
	case 6:
	    result = "kHFSAllocationFileID";
	    break;
	case 7:
	    result = "kHFSStartupFileID";
	    break;
	case 8:
	    result = "kHFSAttributesFileID";
	    break;
	case 14:
	    result = "kHFSRepairCatalogFileID";
	    break;
	case 15:
	    result = "kHFSBogusExtentFileID";
	    break;
	case 16:
	    result = "kHFSFirstUserCatalogNodeID";
	    break;
	default:
	    result = "User Defined ID";
	    break;
	}
	return result;
    }
    
    @Override
    public String toString() {
	return "" + Util.unsign(toInt());// + " (" + getDescription() + ")";
    }
    
    public void printFields(PrintStream ps, String prefix) {
	ps.println(prefix + " hfsCatalogNodeID: " + toString() + " (" + getDescription() + ")");
    }
    
    public void print(PrintStream ps, String prefix) {
	ps.println(prefix + "HFSCatalogNodeID:");
	printFields(ps, prefix);
    }

    public byte[] getBytes() {
        return Util.createCopy(hfsCatalogNodeID);
    }

    public Dictionary getStructElements() {
        DictionaryBuilder db = new DictionaryBuilder(HFSCatalogNodeID.class.getSimpleName());

        db.addUIntBE("hfsCatalogNodeID", hfsCatalogNodeID);

        return db.getResult();
    }
}
    
