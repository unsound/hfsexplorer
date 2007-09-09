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

package org.catacombae.hfsexplorer.types;

import org.catacombae.hfsexplorer.Util;
import org.catacombae.hfsexplorer.FastUnicodeCompare;

public class HFSXCatalogKey extends HFSPlusCatalogKey {
    private final BTHeaderRec catalogHeaderRec;
    
    public HFSXCatalogKey(byte[] data, int offset, BTHeaderRec catalogHeaderRec) {
	super(data, offset);
	this.catalogHeaderRec = catalogHeaderRec;
    }
    
    public HFSXCatalogKey(HFSCatalogNodeID parentID, HFSUniStr255 nodeName, BTHeaderRec catalogHeaderRec) {
	super(parentID, nodeName);
	this.catalogHeaderRec = catalogHeaderRec;
    }
    public HFSXCatalogKey(int parentIDInt, String nodeNameString, BTHeaderRec catalogHeaderRec) {
	super(parentIDInt, nodeNameString);
	this.catalogHeaderRec = catalogHeaderRec;
    }
    public int compareTo(BTKey btk) {
	if(btk instanceof HFSPlusCatalogKey) {
	    HFSPlusCatalogKey catKey = (HFSPlusCatalogKey) btk;
	    if(Util.unsign(getParentID().toInt()) == Util.unsign(catKey.getParentID().toInt())) {
		switch(catalogHeaderRec.getKeyCompareType()) {
		case BTHeaderRec.kHFSCaseFolding:
		    return FastUnicodeCompare.compare(getNodeName().getUnicode(), catKey.getNodeName().getUnicode());
		case BTHeaderRec.kHFSBinaryCompare:
		    return Util.unsignedArrayCompare(getNodeName().getUnicode(), catKey.getNodeName().getUnicode());
		default:
		    throw new RuntimeException("Invalid value in file system structure! BTHeaderRec.getKeyCompareType() = " + 
					       catalogHeaderRec.getKeyCompareType());
		}
	    }
	    else return super.compareTo(btk);
	}
	else
	    return super.compareTo(btk);
    }
}
