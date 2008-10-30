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

import org.catacombae.hfsexplorer.Util;

/**
 * @author <a href="http://hem.bredband.net/catacombae">Erik Larsson</a>
 */
public class Array extends StructElement {

    private final StructElement[] elements;

    public Array(String typeName, StructElement[] elements) {
        super(typeName + "[" + elements.length + "]");
        this.elements = new StructElement[elements.length];
        for(int i = 0; i < this.elements.length; ++i) {
            this.elements[i] = elements[i];
        }
    }

    public StructElement[] getElements() {
        return Util.arrayCopy(elements, new StructElement[elements.length]);
    }
}
