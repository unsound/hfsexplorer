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

package org.catacombae.hfsexplorer.types;

import org.catacombae.hfsexplorer.Util;
import org.catacombae.hfsexplorer.Util2;
import java.io.PrintStream;

public class FourCharCode {
    /*
     * struct FourCharCode
     * size: 4 bytes
     * description: a typedef originally
     * 
     * BP  Size  Type    Identifier    Description
     * -------------------------------------------
     * 0   4     UInt32  fourCharCode             
     */
    
    private final byte[] fourCharCode = new byte[4];
    
    public FourCharCode(byte[] data, int offset) {
	System.arraycopy(data, offset+0, fourCharCode, 0, 4);
    }

    public int getFourCharCode() { return Util.readIntBE(fourCharCode); }
    
    public String getFourCharCodeAsString() { return Util2.toASCIIString(getFourCharCode()); }

    public void printFields(PrintStream ps, String prefix) {
	ps.println(prefix + " fourCharCode: \"" + getFourCharCodeAsString() + "\"");
    }
    public void print(PrintStream ps, String prefix) {
	ps.println(prefix + "FourCharCode:");
	printFields(ps, prefix);
    }
}
