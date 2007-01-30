/*-
 * Copyright (C) 2006 Erik Larsson
 * 
 * All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 */

package org.catacombae.hfsexplorer.types;

import org.catacombae.hfsexplorer.Util;
import org.catacombae.hfsexplorer.Util2;

public class HFSPlusCatalogIndexNode extends BTIndexNode {
    public HFSPlusCatalogIndexNode(byte[] data, int offset, int nodeSize) {
	super(data, offset, nodeSize);
	
	// Populate record list
	// we loop offsets.length-1 times, since last offset is offset to free space
	for(int i = 0; i < records.length; ++i) {
	    int currentOffset = Util2.unsign(offsets[i]);
	    HFSPlusCatalogKey currentKey = new HFSPlusCatalogKey(data, offset+currentOffset);
	    records[i] = new BTIndexRecord(currentKey, data, offset+currentOffset);
	}
    }
    //public static 
}
