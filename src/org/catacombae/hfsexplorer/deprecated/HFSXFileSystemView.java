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

import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusCatalogKey;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusCatalogIndexNode;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusCatalogLeafNode;
import org.catacombae.hfsexplorer.types.hfsplus.HFSCatalogNodeID;
import org.catacombae.hfsexplorer.types.hfsplus.HFSUniStr255;
import org.catacombae.hfsexplorer.types.hfsx.HFSXCatalogIndexNode;
import org.catacombae.hfsexplorer.types.hfsx.HFSXCatalogLeafRecord;
import org.catacombae.hfsexplorer.types.hfsplus.BTHeaderRec;
import org.catacombae.hfsexplorer.types.hfsx.HFSXCatalogLeafNode;
import org.catacombae.hfsexplorer.types.hfsx.HFSXCatalogKey;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusCatalogLeafRecord;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.hfsexplorer.types.*;

/**
 * Extension to support HFSX file systems. Designed to modify as little
 * as possible to avoid code duplication.
 * @deprecated
 */
public class HFSXFileSystemView extends HFSPlusFileSystemView {
    protected static final CatalogOperations HFSX_OPERATIONS = new CatalogOperations() {
	    public HFSPlusCatalogIndexNode newCatalogIndexNode(byte[] data, int offset, int nodeSize, BTHeaderRec bthr) {
		return new HFSXCatalogIndexNode(data, offset, nodeSize, bthr);
	    }
	    public HFSPlusCatalogKey newCatalogKey(HFSCatalogNodeID nodeID, HFSUniStr255 searchString, BTHeaderRec bthr) {
		return new HFSXCatalogKey(nodeID, searchString, bthr);
	    }
	    public HFSPlusCatalogLeafNode newCatalogLeafNode(byte[] data, int offset, int nodeSize, BTHeaderRec bthr) {
		return new HFSXCatalogLeafNode(data, offset, nodeSize, bthr);
	    }
	    public HFSPlusCatalogLeafRecord newCatalogLeafRecord(byte[] data, int offset, BTHeaderRec bthr) {
		return new HFSXCatalogLeafRecord(data, offset, bthr);
	    }
  	};

    public HFSXFileSystemView(ReadableRandomAccessStream hfsFile, long fsOffset, boolean cachingEnabled) {
	super(hfsFile, fsOffset, HFSX_OPERATIONS, cachingEnabled);
    }
}
