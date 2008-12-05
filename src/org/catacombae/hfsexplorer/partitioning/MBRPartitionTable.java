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

package org.catacombae.hfsexplorer.partitioning;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.io.ReadableByteArrayStream;
import java.io.PrintStream;
import java.util.LinkedList;
import org.catacombae.jparted.lib.ps.mbr.MBRPartitionType;

/**
 * This class includes support for the MBR partition scheme, including the
 * common "Extended Boot Record" scheme (parsing of extended boot
 * records can be turned off, if desired).
 * 
 * @author Erik
 */
public class MBRPartitionTable implements PartitionSystem {
    /* Until I figure out a way to detect sector size, it will be 512... */
    public static final int DEFAULT_SECTOR_SIZE = 512;
    
    private final MasterBootRecord masterBootRecord;
    private final PartitionSystem[] embeddedPartitionSystems;
 
    public MBRPartitionTable(byte[] data, int offset) {
        this(new ReadableByteArrayStream(data), offset, false);
    }
    public MBRPartitionTable(byte[] data, int offset, int sectorSize) {
        this(new ReadableByteArrayStream(data), offset, sectorSize, false);
    }
    public MBRPartitionTable(ReadableRandomAccessStream raf, int offset) {
        this(raf, offset, true);
    }
    public MBRPartitionTable(ReadableRandomAccessStream raf, int offset, boolean parseEmbeddedPartitionSystems) {
        this(raf, offset, DEFAULT_SECTOR_SIZE, parseEmbeddedPartitionSystems);
    }
    public MBRPartitionTable(ReadableRandomAccessStream raf, int offset, int sectorSize) {
        this(raf, offset, sectorSize, true);
    }
    public MBRPartitionTable(ReadableRandomAccessStream raf, int offset, int sectorSize, boolean parseEmbeddedPartitionSystems) {
        byte[] block = new byte[sectorSize];
        raf.seek(offset);
        raf.readFully(block);
        masterBootRecord = new MasterBootRecord(block, 0, sectorSize);
        
        // Check for embedded partition systems
        MBRPartition[] mbrPartitions = masterBootRecord.getPartitions();
        embeddedPartitionSystems = new PartitionSystem[mbrPartitions.length];
        for(int i = 0; i < mbrPartitions.length; ++i) {
            MBRPartition p = mbrPartitions[i];
            PartitionSystem embeddedPS = null;

            if(!parseEmbeddedPartitionSystems); // Disable all other elses
            else if(p.getPartitionTypeAsEnum() == MBRPartitionType.DOS_EXTENDED ||
                    p.getPartitionTypeAsEnum() == MBRPartitionType.DOS_EXTENDED_INT13HX) {
                try {
                    embeddedPS =
                        new DOSExtendedPartitionSystem(raf, p.getStartOffset(), p.getLength(), sectorSize);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }

            embeddedPartitionSystems[i] = embeddedPS;
        }
    }
    
    public MasterBootRecord getMasterBootRecord() { return masterBootRecord; }
    
    /**
     * This will return an array of exactly 4 elements, corresponding to the
     * four entries in the Master Boot Record. If the element at position i is
     * null, it means there is no embedded partition system at that MBR entry.
     * If no embedded partition systems exists, then this array will consist of
     * all null elements.
     * @return a four element array containing the embedded partition systems
     * of this MBR layout, if any.
     */
    public PartitionSystem[] getEmbeddedPartitionSystems() {
        PartitionSystem[] result = new PartitionSystem[embeddedPartitionSystems.length];
        System.arraycopy(embeddedPartitionSystems, 0, result, 0, result.length);
        return result;
    }
    
    public PartitionSystem getEmbeddedPartitionSystem(int index) {
        return embeddedPartitionSystems[index];
    }
    
    public boolean isValid() {
        if(masterBootRecord.isValid()) {
            for(PartitionSystem ebr : embeddedPartitionSystems) {
                if(ebr != null && !ebr.isValid())
                    return false;
            }
            return true;
        }
        return false;
    }

    public int getPartitionCount() {
 	int num = masterBootRecord.getPartitionCount();
	for(PartitionSystem ps : embeddedPartitionSystems) {
	    if(ps != null)
                num += ps.getPartitionCount();
	}
	return num;
   }
    
    public int getUsedPartitionCount() {
	int num = masterBootRecord.getUsedPartitionCount();
	for(PartitionSystem ps : embeddedPartitionSystems) {
	    if(ps != null)
                num += ps.getUsedPartitionCount();
	}
	return num;
    }
    
    public Partition[] getUsedPartitionEntries() {
        LinkedList<Partition> tempList = new LinkedList<Partition>();
        for(Partition p : masterBootRecord.getUsedPartitionEntries())
            tempList.addLast(p);
        
	for(PartitionSystem ps : embeddedPartitionSystems) {
	    if(ps != null) {
                for(Partition p : ps.getUsedPartitionEntries())
                    tempList.addLast(p);
            }
	}
        
        return tempList.toArray(new Partition[tempList.size()]);
    }

    public Partition getPartitionEntry(int index) {
        if(index >= 0 && index < 4) {
            return masterBootRecord.getPartitionEntry(index);
        }
        else if(index >= 4) {
            int curIndex = 4;
            for(PartitionSystem ps : embeddedPartitionSystems) {
                int psPartitions = ps.getPartitionCount();
                if(index < curIndex+psPartitions)
                    return ps.getPartitionEntry(index-curIndex);
                curIndex += psPartitions;
            }
        }
        
        throw new IllegalArgumentException("index out of bounds (index=" + index + ")");
    }

    public Partition[] getPartitionEntries() {
        LinkedList<Partition> tempList = new LinkedList<Partition>();
        for(Partition p : masterBootRecord.getPartitionEntries())
            tempList.addLast(p);
        
	for(PartitionSystem ps : embeddedPartitionSystems) {
	    if(ps != null) {
                for(Partition p : ps.getPartitionEntries())
                    tempList.addLast(p);
            }
	}
        
        return tempList.toArray(new Partition[tempList.size()]);
    }


    public String getLongName() { return "Master Boot Record"; }
    
    public String getShortName() { return "MBR"; }

    public void printFields(PrintStream ps, String prefix) {
        ps.println(prefix + " masterBootRecord:");
        masterBootRecord.print(ps, prefix + "  ");
        ps.println(prefix + " embeddedPartitionSystems:");
        for(int i = 0; i < embeddedPartitionSystems.length; ++i) {
            PartitionSystem partSys = embeddedPartitionSystems[i];
            ps.print(prefix + "  [" + i + "]:");
            if(partSys == null)
                ps.println(" null");
            else {
                ps.println();
                partSys.print(ps, prefix + "   ");
            }
        }
    }

    public void print(PrintStream ps, String prefix) {
        ps.println(prefix + this.getClass().getSimpleName() + ":");
        printFields(ps, prefix);
    }

    
}
