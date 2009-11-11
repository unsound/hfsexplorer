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

package org.catacombae.jparted.lib.ps.mbr;

import org.catacombae.jparted.lib.DataLocator;
import org.catacombae.jparted.lib.ps.PartitionSystemRecognizer;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.hfsexplorer.partitioning.MBRPartitionTable;

/**
 *
 * @author erik
 */
public class MBRRecognizer implements PartitionSystemRecognizer {
    /*
    private DataLocator data;
    
    public MBRRecognizer(DataLocator pData) {
        this.data = pData;
    }
    
    @Override
    public boolean existsPartitionSystem() {
        try {
            ReadableRandomAccessStream llf = data.createReadOnlyFile();
            byte[] firstBlock = new byte[512];
            llf.read(firstBlock);
            
            // Look for MBR
            MBRPartitionTable mpt = new MBRPartitionTable(firstBlock, 0);
            if(mpt.isValid()) {
                return true;
            }
        } catch(Exception e) {
        }
            
        return false;
    }
    */

    public boolean detect(ReadableRandomAccessStream fsStream, long offset, long length) {
        try {
            //ReadableRandomAccessStream llf = data.createReadOnlyFile();
            byte[] firstBlock = new byte[512];
            fsStream.read(firstBlock);

            // Look for MBR
            MBRPartitionTable mpt = new MBRPartitionTable(firstBlock, 0);
            if(mpt.isValid()) {
                return true;
            }
        } catch(Exception e) {
        }

        return false;
    }
}
