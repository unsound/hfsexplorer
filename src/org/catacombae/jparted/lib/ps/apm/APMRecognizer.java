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

package org.catacombae.jparted.lib.ps.apm;

import org.catacombae.hfsexplorer.Util;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.hfsexplorer.partitioning.ApplePartitionMap;
import org.catacombae.hfsexplorer.partitioning.DriverDescriptorRecord;
import org.catacombae.jparted.lib.ps.PartitionSystemRecognizer;

/**
 *
 * @author erik
 */
public class APMRecognizer implements PartitionSystemRecognizer {
    
    public boolean detect(ReadableRandomAccessStream fsStream, long offset, long length) {
        try {
            //ReadableRandomAccessStream llf = data.createReadOnlyFile();
            byte[] firstBlock = new byte[512];

            fsStream.seek(0);
            fsStream.readFully(firstBlock);

            // Look for APM
            DriverDescriptorRecord ddr = new DriverDescriptorRecord(firstBlock, 0);
            if(ddr.isValid()) {
                int blockSize = Util.unsign(ddr.getSbBlkSize());
                //long numberOfBlocksOnDevice = Util.unsign(ddr.getSbBlkCount());
                //bitStream.seek(blockSize*1); // second block, first partition in list
                ApplePartitionMap apm = new ApplePartitionMap(fsStream, blockSize * 1, blockSize);
                if(apm.getUsedPartitionCount() > 0) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }
}
