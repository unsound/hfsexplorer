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

import org.catacombae.hfsexplorer.types.hfsx.HFSXCatalogKey;
import org.catacombae.hfsexplorer.Util;
import java.io.PrintStream;

public class HFSPlusCatalogLeafRecord {
    public static final int HFS_PLUS_FOLDER_RECORD = 0x0001;
    public static final int HFS_PLUS_FILE_RECORD = 0x0002;
    public static final int HFS_PLUS_FOLDER_THREAD_RECORD = 0x0003;
    public static final int HFS_PLUS_FILE_THREAD_RECORD = 0x0004;
    
    protected final HFSPlusCatalogKey key;
    protected final HFSPlusCatalogLeafRecordData recordData;
    
    public HFSPlusCatalogLeafRecord(byte[] data, int offset) {
	this(data, offset, null);
    }
    protected HFSPlusCatalogLeafRecord(byte[] data, int offset, BTHeaderRec catalogHeaderRec) {
	if(catalogHeaderRec == null)
	    key = new HFSPlusCatalogKey(data, offset);
	else
	    key = new HFSXCatalogKey(data, offset, catalogHeaderRec);
	
	short recordType = Util.readShortBE(data, offset+key.length());
	if(recordType == HFS_PLUS_FOLDER_RECORD)
	    recordData = new HFSPlusCatalogFolder(data, offset+key.length());
	else if(recordType == HFS_PLUS_FILE_RECORD)
	    recordData = new HFSPlusCatalogFile(data, offset+key.length());
	else if(recordType == HFS_PLUS_FOLDER_THREAD_RECORD)
	    recordData = new HFSPlusCatalogThread(data, offset+key.length());
	else if(recordType == HFS_PLUS_FILE_THREAD_RECORD)
	    recordData = new HFSPlusCatalogThread(data, offset+key.length());
	else
	    throw new RuntimeException("Ivalid record type!");
    }
    
    public HFSPlusCatalogKey getKey() { return key; }
    public HFSPlusCatalogLeafRecordData getData() { return recordData; }
    
    public void printFields(PrintStream ps, String prefix) {
	ps.println(prefix + " key:");
	key.printFields(ps, prefix + "  ");
	ps.println(prefix + " recordData:");
	recordData.printFields(ps, prefix + "  ");
    }
    public void print(PrintStream ps, String prefix) {
	ps.println(prefix + "HFSPlusCatalogLeafRecord:");
	printFields(ps, prefix);
    }
}
