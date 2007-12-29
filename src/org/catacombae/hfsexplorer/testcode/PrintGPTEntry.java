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

package org.catacombae.hfsexplorer.testcode;

import org.catacombae.hfsexplorer.partitioning.*;
import java.io.*;

public class PrintGPTEntry {
    public static void main(String[] args) throws Exception {
	long offset = 0;
	File sourceFile;
	if(args.length > 0)
	    sourceFile = new File(args[0]);
	else {
	    System.out.println("usage: PrintGPTEntry <sourceFile> [<offset>]");
	    System.exit(1);
	    return;
	}
	if(args.length > 1)
	    offset = Long.parseLong(args[1]);
	
	byte[] data = new byte[GPTEntry.getSize()];
	RandomAccessFile sourceRaf = new RandomAccessFile(sourceFile, "r");
	sourceRaf.seek(offset);
	sourceRaf.read(data);
	
	GPTEntry gph = new GPTEntry(data, 0, 512);
	gph.print(System.out, "");
    }
}
