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
import org.catacombae.storage.ps.apm.types.ApplePartitionMap;
import org.catacombae.storage.ps.apm.types.DriverDescriptorRecord;
import org.catacombae.storage.io.win32.ReadableWin32FileStream;
import org.catacombae.io.ReadableFileStream;
import org.catacombae.io.ReadableRandomAccessStream;

/**
 * @author <a href="https://catacombae.org" target="_top">Erik Larsson</a>
 */
public class PrintAPMPartitions {

    public static void main(String[] args) throws Exception {
        ReadableRandomAccessStream llf;
        if(ReadableWin32FileStream.isSystemSupported())
            llf = new ReadableWin32FileStream(args[0]);
        else
            llf = new ReadableFileStream(args[0]);

        DriverDescriptorRecord ddr = new DriverDescriptorRecord(llf, 0);
        ddr.print(System.out, "");

        // Dump DDR to file for debug purposes
        FileOutputStream ddrFile = new FileOutputStream("ddr.debug");
        ddrFile.write(ddr.getData());
        ddrFile.close();
        System.out.println("Wrote the Driver Descriptor Record to file: ddr.debug");

        System.out.println("Length of file: " + llf.length());

        final int blockSize = ddr.getSbBlkSize();
        //final int blockSize = 512;
        ApplePartitionMap apm = new ApplePartitionMap(llf, blockSize, blockSize);
        apm.print(System.out, "");

        FileOutputStream apmFile = new FileOutputStream("apm.debug");
        apmFile.write(apm.getData());
        apmFile.close();
        System.out.println("Wrote the raw Apple Partition Map to file: apm.debug");

        llf.close();
    }
}
