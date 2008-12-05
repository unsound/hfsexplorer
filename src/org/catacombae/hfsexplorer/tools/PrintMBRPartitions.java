/*-
 * Copyright (C) 2007-2008 Erik Larsson
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

package org.catacombae.hfsexplorer.tools;
import java.io.FileOutputStream;
import org.catacombae.hfsexplorer.partitioning.MBRPartitionTable;
import org.catacombae.hfsexplorer.win32.WindowsLowLevelIO;
import org.catacombae.io.ReadableFileStream;
import org.catacombae.io.ReadableRandomAccessStream;

public class PrintMBRPartitions {

    public static void main(String[] args) throws Exception {
        ReadableRandomAccessStream llf;
        if(WindowsLowLevelIO.isSystemSupported())
            llf = new WindowsLowLevelIO(args[0]);
        else
            llf = new ReadableFileStream(args[0]);

        byte[] referencetable = new byte[512];
        llf.seek(0);
        llf.readFully(referencetable);
        FileOutputStream refFile = new FileOutputStream("mbr_table.debug");
        refFile.write(referencetable);
        refFile.close();
        System.out.println("Wrote the raw MBR table to file: mbr_table.debug");

        System.out.println("Length of file: " + llf.length());
        MBRPartitionTable mbr = new MBRPartitionTable(llf, 0);
        mbr.print(System.out, "");
        System.out.println("Is this parititon table valid? " + mbr.isValid() + ".");

        llf.close();
    }
}
