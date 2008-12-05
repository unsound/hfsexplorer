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

package org.catacombae.jparted.lib.ps.ebr;

import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.hfsexplorer.partitioning.PartitionSystem;
import org.catacombae.jparted.lib.DataLocator;
import org.catacombae.jparted.lib.ps.Partition;
import org.catacombae.jparted.lib.ps.PartitionSystemHandler;
import org.catacombae.jparted.lib.ps.StandardPartition;

/**
 *
 * @author erik
 */
public class EBRHandler extends PartitionSystemHandler {
    
    private DataLocator partitionData;
    
    public EBRHandler(DataLocator partitionData) {
        this.partitionData = partitionData;
    }

    @Override
    public long getPartitionCount() {
        PartitionSystem ps = readPartitionTable();
        return ps.getUsedPartitionCount();
    }
    
    @Override
    public Partition[] getPartitions() {
        EBRPartitionSystem partitionTable = readPartitionTable();
        Partition[] result = new Partition[partitionTable.getUsedPartitionCount()];
        org.catacombae.hfsexplorer.partitioning.Partition[] parts =
                partitionTable.getUsedPartitionEntries();
        for(int i = 0; i < result.length; ++i) {
            result[i] = new StandardPartition(parts[i].getStartOffset(),
                    parts[i].getLength(), parts[i].getType());
        }
        return result;
    }

    @Override
    public void close() {
        
    }

    private EBRPartitionSystem readPartitionTable() {
        try {
            ReadableRandomAccessStream llf = partitionData.createReadOnlyFile();
            byte[] firstBlock = new byte[512];
            
            llf.readFully(firstBlock);

            EBRPartitionSystem ebs = new EBRPartitionSystem(llf, 0, 512);
            if(ebs.isValid())
                return ebs;
            
        } catch (Exception e) {
        }
        return null;
    }

}
