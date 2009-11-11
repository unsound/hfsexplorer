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

import java.io.PrintStream;
import java.util.LinkedList;
import org.catacombae.hfsexplorer.partitioning.MBRPartitionTable;
import org.catacombae.hfsexplorer.partitioning.Partition;
import org.catacombae.hfsexplorer.partitioning.PartitionSystem;
import org.catacombae.hfsexplorer.win32.WindowsLowLevelIO;
import org.catacombae.io.ReadableFileStream;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.jparted.lib.ps.PartitionType;

/**
 *
 * @author Erik Larsson
 */
public class EBRPartitionSystem implements PartitionSystem {
    private final ExtendedBootRecord[] bootRecords;

    public EBRPartitionSystem(ReadableRandomAccessStream psStream, final long ebrPartitionOffset, int sectorSize) {
        this(psStream, ebrPartitionOffset, -1, sectorSize);
    }
    public EBRPartitionSystem(ReadableRandomAccessStream psStream, final long ebrPartitionOffset, final long ebrPartitionLength, int sectorSize) {
        byte[] tempBuffer = new byte[512];

        long curOffset = ebrPartitionOffset;
        psStream.seek(curOffset);
        psStream.readFully(tempBuffer);

        LinkedList<ExtendedBootRecord> recordList = new LinkedList<ExtendedBootRecord>();
        ExtendedBootRecord ebr;
        while((ebr = new ExtendedBootRecord(tempBuffer, 0, ebrPartitionOffset, curOffset, sectorSize)).isValid()) {
            //System.err.println("EBR partition " + recordList.size() + ":");
            //ebr.print(System.err, "  ");
            if(recordList.size() > 10000)
                throw new RuntimeException("Number of EBR partitions capped at 10000.");
            recordList.add(ebr);
            
            if(ebr.isTerminator())
                break; // We have reached the end of the EBR linked list.
            else {
                //EBRPartition firstEntry = ebr.getFirstEntry();
                curOffset = ebr.getSecondEntry().getStartOffset();

                if(ebrPartitionLength > 0 && curOffset > ebrPartitionOffset+ebrPartitionLength)
                    throw new RuntimeException("Invalid DOS Extended partition system (curOffset=" + curOffset + ").");

                //System.err.println("Seeking to offset(" + offset + ") + secondEntryStart(" + ebr.getSecondEntry().getStartOffset() + ") = " + curOffset);
                psStream.seek(curOffset);
                psStream.readFully(tempBuffer);
            }
        }

        if(!ebr.isValid())
            throw new RuntimeException("Invalid extended partition table at index " +
                    recordList.size() + ".");
        else
            this.bootRecords = recordList.toArray(new ExtendedBootRecord[recordList.size()]);
    }

    public boolean isValid() {
        return true; // We check this at creation time.
    }

    public int getPartitionCount() {
        return bootRecords.length;
    }

    public Partition getPartitionEntry(int index) {
        return bootRecords[index].getFirstEntry();
    }

    public Partition[] getPartitionEntries() {
        Partition[] result = new Partition[bootRecords.length];
        for(int i = 0; i < result.length; ++i) {
            result[i] = bootRecords[i].getFirstEntry();
        }
        return result;
    }

    public int getUsedPartitionCount() {
        return getPartitionCount();
    }

    public Partition[] getUsedPartitionEntries() {
        return getPartitionEntries();
    }

    public String getLongName() {
        return "Extended Boot Record";
    }

    public String getShortName() {
        return "EBR";
    }

    public void printFields(PrintStream ps, String prefix) {
        ps.println(prefix + " bootRecords:");
        for(int i = 0; i < bootRecords.length; ++i) {
            ExtendedBootRecord ebr = bootRecords[i];
            ps.print(prefix + "  [" + i + "]:");
            ebr.print(ps, prefix + "   ");
        }
    }

    public void print(PrintStream ps, String prefix) {
        ps.println(prefix + this.getClass().getSimpleName() + ":");
        printFields(ps, prefix);
    }

    public static void main(String[] args) {
        System.err.println("Test code for Extended Boot Record");

        String inputFilename = args[0];
        ReadableRandomAccessStream inputStream;
        if(WindowsLowLevelIO.isSystemSupported())
            inputStream = new WindowsLowLevelIO(inputFilename);
        else
            inputStream = new ReadableFileStream(inputFilename);

        MBRPartitionTable mpt = new MBRPartitionTable(inputStream, 0);
        if(!mpt.isValid())
            throw new RuntimeException("Invalid MBR.");

        int i = 0;
        for(Partition p : mpt.getUsedPartitionEntries()) {
            if(p.getType() == PartitionType.DOS_EXTENDED) {
                System.err.println("Found extended partition system at MBR partition " + i + ":");
                EBRPartitionSystem ebt = new EBRPartitionSystem(inputStream, p.getStartOffset(), p.getLength(), 512);
                ebt.print(System.err, "  ");
            }
            ++i;
        }

        inputStream.close();
    }
}
