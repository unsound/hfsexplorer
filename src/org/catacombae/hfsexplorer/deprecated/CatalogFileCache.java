/*-
 * Copyright (C) 2007 Erik Larsson
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

package org.catacombae.hfsexplorer.deprecated;

import org.catacombae.hfsexplorer.types.hfsplus.BTNode;
import org.catacombae.hfsexplorer.types.*;
import java.util.*;

/**
 * Convenience class to hold copies of the catalog file's data.
 */
class CatalogFileCache {
    private HashMap<Integer, BTNode> cacheMap = new HashMap<Integer, BTNode>();
    
    public void put(int nodeNumber, BTNode node) {
	cacheMap.put(nodeNumber, node);
    }
    public BTNode get(int nodeNumber) {
	return cacheMap.get(nodeNumber);
    }
}
