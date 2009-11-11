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

package org.catacombae.hfsexplorer.types.hfsx;

import org.catacombae.hfsexplorer.Util;
import org.catacombae.hfsexplorer.FastUnicodeCompare;
import org.catacombae.hfsexplorer.types.hfsplus.BTHeaderRec;
import org.catacombae.hfsexplorer.types.hfsplus.BTKey;
import org.catacombae.hfsexplorer.types.hfsplus.HFSCatalogNodeID;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusCatalogKey;
import org.catacombae.hfsexplorer.types.hfsplus.HFSUniStr255;

public class HFSXCatalogKey extends HFSPlusCatalogKey {
    private final byte keyCompareType;
    
    public HFSXCatalogKey(byte[] data, int offset, BTHeaderRec catalogHeaderRec) {
	super(data, offset);
        if(catalogHeaderRec == null)
            throw new IllegalArgumentException("catalogHeaderRec == null");
        
	this.keyCompareType = catalogHeaderRec.getKeyCompareType();
        if(keyCompareType != BTHeaderRec.kHFSBinaryCompare && keyCompareType != BTHeaderRec.kHFSCaseFolding)
            throw new IllegalArgumentException("Illegal key compare type: " + keyCompareType);
    }
    
    public HFSXCatalogKey(HFSCatalogNodeID parentID, HFSUniStr255 nodeName, BTHeaderRec catalogHeaderRec) {
	super(parentID, nodeName);
        if(catalogHeaderRec == null)
            throw new IllegalArgumentException("catalogHeaderRec == null");

        this.keyCompareType = catalogHeaderRec.getKeyCompareType();
        if(keyCompareType != BTHeaderRec.kHFSBinaryCompare && keyCompareType != BTHeaderRec.kHFSCaseFolding)
            throw new IllegalArgumentException("Illegal key compare type: " + keyCompareType);
    }
    
    public HFSXCatalogKey(int parentIDInt, String nodeNameString, BTHeaderRec catalogHeaderRec) {
	super(parentIDInt, nodeNameString);
        if(catalogHeaderRec == null)
            throw new IllegalArgumentException("catalogHeaderRec == null");
        
	this.keyCompareType = catalogHeaderRec.getKeyCompareType();
        if(keyCompareType != BTHeaderRec.kHFSBinaryCompare && keyCompareType != BTHeaderRec.kHFSCaseFolding)
            throw new IllegalArgumentException("Illegal key compare type: " + keyCompareType);
    }
    
    @Override
    public int compareTo(BTKey btk) {
	if(btk instanceof HFSPlusCatalogKey) {
	    HFSPlusCatalogKey catKey = (HFSPlusCatalogKey) btk;
	    if(Util.unsign(getParentID().toInt()) == Util.unsign(catKey.getParentID().toInt())) {
		switch(keyCompareType) {
		case BTHeaderRec.kHFSCaseFolding:
		    return FastUnicodeCompare.compare(getNodeName().getUnicode(), catKey.getNodeName().getUnicode());
		case BTHeaderRec.kHFSBinaryCompare:
		    return Util.unsignedArrayCompareLex(getNodeName().getUnicode(), catKey.getNodeName().getUnicode());
		default:
		    throw new RuntimeException("Invalid value in file system structure! keyCompareType = " + 
					       keyCompareType);
		}
	    }
	    else return super.compareTo(btk);
	}
	else
	    return super.compareTo(btk);
    }
}
