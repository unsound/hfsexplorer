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

package org.catacombae.storage.ps.mbr;

import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.storage.ps.mbr.types.MBRPartitionTable;
import org.catacombae.storage.io.DataLocator;
import org.catacombae.storage.ps.Partition;
import org.catacombae.storage.ps.PartitionSystemHandler;

/**
 *
 * @author erik
 */
public class MBRHandler extends PartitionSystemHandler {

    private DataLocator partitionData;

    public MBRHandler(DataLocator partitionData) {
        this.partitionData = partitionData;
    }

    @Override
    public long getPartitionCount() {
        MBRPartitionTable apm = readPartitionTable();
        return apm.getUsedPartitionCount();
    }

    @Override
    public Partition[] getPartitions() {
        MBRPartitionTable partitionTable = readPartitionTable();
        return partitionTable.getUsedPartitionEntries();
    }

    @Override
    public void close() {
        partitionData.close();
    }

    public MBRPartitionTable readPartitionTable() {
        ReadableRandomAccessStream llf = null;
        try {
            llf = partitionData.createReadOnlyFile();
            byte[] firstBlock = new byte[512];

            llf.readFully(firstBlock);

            MBRPartitionTable mbt = new MBRPartitionTable(firstBlock, 0);
            if(mbt.isValid())
                return mbt;
            else
                return null;
        } finally {
            if(llf != null)
                llf.close();
        }
    }

}
