/*-
 * Copyright (C) 2006 Erik Larsson
 * 
 * All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 */

package org.catacombae.hfsexplorer.types;

import org.catacombae.hfsexplorer.Util;
import org.catacombae.hfsexplorer.Util2;
import java.io.PrintStream;

public abstract class HFSPlusCatalogLeafRecordData {
    public static final int RECORD_TYPE_FOLDER = 0x0001;
    public static final int RECORD_TYPE_FILE = 0x0002;
    public static final int RECORD_TYPE_FOLDER_THREAD = 0x0003;
    public static final int RECORD_TYPE_FILE_THREAD = 0x0004;

    public abstract short getRecordType();
    public String getRecordTypeAsString() {
	int recordType = Util2.unsign(getRecordType());
	if(recordType == RECORD_TYPE_FOLDER) return "kHFSPlusFolderRecord";
	else if(recordType == RECORD_TYPE_FILE) return "kHFSPlusFileRecord";
	else if(recordType == RECORD_TYPE_FOLDER_THREAD) return "kHFSPlusFolderThreadRecord";
	else if(recordType == RECORD_TYPE_FILE_THREAD) return "kHFSPlusFileThreadRecord";
	else return "UNKNOWN!";
    }
    public abstract void printFields(PrintStream ps, String prefix);
    public abstract void print(PrintStream ps, String prefix);
}
