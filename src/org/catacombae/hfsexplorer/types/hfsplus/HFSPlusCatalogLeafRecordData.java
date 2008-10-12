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

import org.catacombae.hfsexplorer.Util;
import org.catacombae.csjc.PrintableStruct;
import org.catacombae.csjc.StructElements;

public abstract class HFSPlusCatalogLeafRecordData implements PrintableStruct, StructElements {
    public static final int RECORD_TYPE_FOLDER = 0x0001;
    public static final int RECORD_TYPE_FILE = 0x0002;
    public static final int RECORD_TYPE_FOLDER_THREAD = 0x0003;
    public static final int RECORD_TYPE_FILE_THREAD = 0x0004;

    public abstract short getRecordType();
    public String getRecordTypeAsString() {
	int recordType = Util.unsign(getRecordType());
	if(recordType == RECORD_TYPE_FOLDER) return "kHFSPlusFolderRecord";
	else if(recordType == RECORD_TYPE_FILE) return "kHFSPlusFileRecord";
	else if(recordType == RECORD_TYPE_FOLDER_THREAD) return "kHFSPlusFolderThreadRecord";
	else if(recordType == RECORD_TYPE_FILE_THREAD) return "kHFSPlusFileThreadRecord";
	else return "UNKNOWN!";
    }
}
