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

public abstract class BTKey implements Comparable<BTKey> {
    public abstract short getKeyLength();
    public abstract int length();    
    public abstract byte[] getBytes();

    @Override
    public int compareTo(BTKey btk) {
	byte[] thisData = getBytes();
	byte[] thatData = btk.getBytes();
        
	for(int i = 0; i < Math.min(thisData.length, thatData.length); ++i) {
            int a = thisData[i] & 0xFF;
            int b = thatData[i] & 0xff;
	    if(a < b)
		return -1;
	    else if(a > b)
		return 1;
	}
	if(thisData.length < thatData.length)
	    return -1;
	else if(thisData.length > thatData.length)
	    return 1;
	else
	    return 0;
    }
}
