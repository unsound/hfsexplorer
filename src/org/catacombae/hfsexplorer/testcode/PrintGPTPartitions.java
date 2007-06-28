/*-
 * Copyright (C) 2007 Erik Larsson
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

package org.catacombae.hfsexplorer.testcode;
import org.catacombae.hfsexplorer.*;
import org.catacombae.hfsexplorer.partitioning.*;
import org.catacombae.hfsexplorer.win32.*;
import java.io.*;

public class PrintGPTPartitions {
    public static void main(String[] args) throws Exception {
	LowLevelFile llf;
	if(System.getProperty("os.name").toLowerCase().startsWith("windows")) {
	    llf = new WritableWin32File(args[0]);
	}
	else {
	    llf = new WritableRandomAccessLLF(args[0]);
	}
	byte[] referencetable = new byte[16896];
	llf.seek(512);
	llf.readFully(referencetable);
	FileOutputStream refFile = new FileOutputStream("gpt_table_ref.debug");
	refFile.write(referencetable);
	refFile.close();
	System.out.println("Wrote the raw GPT table to file: gpt_table_ref.debug");
	    
	System.out.println("Length of file: " + llf.length());
	GUIDPartitionTable gpt = new GUIDPartitionTable(llf, 0);
	System.out.println("Calculated checksum for header: 0x" + Util.toHexStringBE(gpt.calculatePrimaryHeaderChecksum()));
	System.out.println("Calculated checksum for entries: 0x" + Util.toHexStringBE(gpt.calculatePrimaryEntriesChecksum()));
	gpt.print(System.out, "");
	System.out.println("Is this parititon table valid? " + gpt.isValid() + ".");
	FileOutputStream debug = new FileOutputStream("gpt_table.debug");
	debug.write(gpt.getPrimaryTableBytes());
	debug.close();
	System.out.println("Wrote the leading GPT table to file: gpt_table.debug");
	    
	llf.close();
	
    }
    
}
