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

import org.catacombae.hfsexplorer.types.hfsx.HFSXCatalogKey;
import org.catacombae.hfsexplorer.Util;

public class HFSPlusCatalogIndexNode extends BTIndexNode {
    public HFSPlusCatalogIndexNode(byte[] data, int offset, int nodeSize) {
	this(data, offset, nodeSize, null);
    }
    protected HFSPlusCatalogIndexNode(byte[] data, int offset, int nodeSize, BTHeaderRec catalogHeaderRec) {
	super(data, offset, nodeSize);
	
	// Populate record list
	// we loop offsets.length-1 times, since last offset is offset to free space
	for(int i = 0; i < records.length; ++i) {
	    int currentOffset = Util.unsign(offsets[i]);
	    HFSPlusCatalogKey currentKey;
	    if(catalogHeaderRec == null)
		currentKey = new HFSPlusCatalogKey(data, offset+currentOffset);
	    else
		currentKey = new HFSXCatalogKey(data, offset+currentOffset, catalogHeaderRec);
	    records[i] = new BTIndexRecord(currentKey, data, offset+currentOffset);
	}
    }
    //public static 
}
