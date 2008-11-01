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

package org.catacombae.hfsexplorer.types.hfscommon;

import java.io.PrintStream;
import org.catacombae.csjc.DynamicStruct;
import org.catacombae.csjc.PrintableStruct;

/**
 *
 * @author erik
 */
public abstract class CommonBTKey implements Comparable<CommonBTKey>, DynamicStruct, PrintableStruct {
    //public abstract int getMaxLength();
    //public abstract int length();
    
    public int compareTo(CommonBTKey btk) {
	byte[] thisData = getBytes();
	byte[] thatData = btk.getBytes();
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
    
    public abstract byte[] getBytes();
}
