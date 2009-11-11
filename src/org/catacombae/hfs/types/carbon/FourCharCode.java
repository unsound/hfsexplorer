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

package org.catacombae.hfsexplorer.types.carbon;

import org.catacombae.csjc.structelements.Dictionary;
import org.catacombae.hfsexplorer.Util;
import java.io.PrintStream;
import org.catacombae.csjc.StructElements;

public class FourCharCode implements StructElements {
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
        System.arraycopy(data, offset + 0, fourCharCode, 0, 4);
    }

    public int getFourCharCode() {
        return Util.readIntBE(fourCharCode);
    }

    public String getFourCharCodeAsString() {
        return Util.toASCIIString(getFourCharCode());
    }

    public void printFields(PrintStream ps, String prefix) {
        ps.println(prefix + " fourCharCode: \"" + getFourCharCodeAsString() + "\"");
    }

    public void print(PrintStream ps, String prefix) {
        ps.println(prefix + "FourCharCode:");
        printFields(ps, prefix);
    }

    public byte[] getBytes() {
        return Util.createCopy(fourCharCode);
    }

    @Override
    public Dictionary getStructElements() {
        DictionaryBuilder db = new DictionaryBuilder(FourCharCode.class.getSimpleName());
        
        db.addEncodedString("fourCharCode", fourCharCode, "US-ASCII");
        
        return db.getResult();
    }
}
