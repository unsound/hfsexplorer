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

package org.catacombae.csjc.structelements;

/**
 * @author <a href="http://hem.bredband.net/catacombae">Erik Larsson</a>
 */
public enum IntegerFieldRepresentation {
    DECIMAL("", 10), HEXADECIMAL("0x", 16), OCTAL("0", 8), BINARY("0b", 2);

    private final String prefix;
    private final int radix;

    private IntegerFieldRepresentation(String prefix, int radix) {
        this.prefix = prefix;
        this.radix = radix;
    }

    public String getPrefix() {
        return prefix;
    }

    public int getRadix() {
        return radix;
    }
}
