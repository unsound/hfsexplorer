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

import java.util.Hashtable;
import org.catacombae.hfsexplorer.Util;

/**
 * @author <a href="http://hem.bredband.net/catacombae">Erik Larsson</a>
 */
public class Dictionary extends StructElement {

    private final String[] keys;
    private final Hashtable<String,StructElement> mappings;
    private final Hashtable<String,String> descriptions;
        
    Dictionary(String typeName, String[] keys, Hashtable<String,StructElement> mappings,
            Hashtable<String,String> descriptions) {
        this(typeName, null, keys, mappings, descriptions);
    }
    Dictionary(String typeName, String typeDescription, String[] keys,
            Hashtable<String,StructElement> mappings, Hashtable<String,String> descriptions) {
        super(typeName, typeDescription);
        this.keys = new String[keys.length];
        System.arraycopy(keys, 0, this.keys, 0, keys.length);
        this.mappings = new Hashtable<String,StructElement>();
        this.descriptions = new Hashtable<String,String>();
        for(String key : keys) {
            this.mappings.put(key, mappings.get(key));
            String description = descriptions.get(key);
            if(description != null) {
                this.descriptions.put(key, description);

            }
        }
    }

    public StructElement getElement(String name) {
        return mappings.get(name);
    }
    
    /**
     * Returns the associated description with variable <code>name</code> if there exists any, and
     * <code>null</code> otherwise.
     * 
     * @param name the variable name, which must be equal to one of the elements in the array
     * returned by <code>getKeys()</code>.
     * @return the associated description, if any, or null if no description exists.
     */
    public String getDescription(String name) {
        return descriptions.get(name);
    }

    public int getElementCount() {
        return keys.length;
    }

    public String[] getKeys() {
        return Util.arrayCopy(keys, new String[keys.length]);
    }
}
