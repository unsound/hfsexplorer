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

import java.io.PrintStream;

public class OSType {
    /*
     * struct OSType
     * size: 4 bytes
     * description: a typedef originally
     * 
     * BP  Size  Type          Identifier  Description
     * -----------------------------------------------
     * 0   4     FourCharCode  osType                 
     */
    
    private FourCharCode osType;
    
    public OSType(byte[] data, int offset) {
	osType = new FourCharCode(data, offset);
    }
    
    public FourCharCode getOSType() { return osType; }
    
    public String toString() {
	return osType.getFourCharCodeAsString();
    }

    public void printFields(PrintStream ps, String prefix) {
	ps.println(prefix + " osType: ");
	osType.print(ps, prefix+"  ");
    }
    public void print(PrintStream ps, String prefix) {
	ps.println(prefix + "OSType:");
	printFields(ps, prefix);
    }
}
