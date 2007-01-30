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

public abstract class BTKey implements Comparable<BTKey> {
    public abstract short getKeyLength();
    public abstract int length();
    public int compareTo(BTKey btk) {
	byte[] thisData = getData();
	byte[] thatData = btk.getData();
	for(int i = 0; i < Math.min(thisData.length, thatData.length); ++i) {
	    if(thisData[i] < thatData[i])
		return -1;
	    else if(thisData[i] > thatData[i])
		return 1;
	}
	if(thisData.length < thatData.length)
	    return -1;
	else if(thisData.length > thatData.length)
	    return 1;
	else
	    return 0;
    }
    public abstract byte[] getData();
}
