/*-
 * Copyright (C) 2006-2007 Erik Larsson
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

import org.catacombae.hfsexplorer.Util;
import java.io.PrintStream;
import org.catacombae.jparted.lib.ps.PartitionType;

public class APMPartition implements Partition {
    /*
     * struct Partition
     * size: 512 bytes
     *
     * BP   Size  Type              Variable name   Description
     * --------------------------------------------------------------
     * 0    2     UInt16            pmSig           partition signature
     * 2    2     UInt16            pmSigPad        reserved
     * 4    4     UInt32            pmMapBlkCnt     number of blocks in partition map
     * 8    4     UInt32            pmPyPartStart   first physical block of partition
     * 12   4     UInt32            pmPartBlkCnt    number of blocks in partition
     * 16   1*32  Char[32]          pmPartName      partition name
     * 48   1*32  Char[32]          pmParType       partition type
     * 80   4     UInt32            pmLgDataStart   first logical block of data area
     * 84   4     UInt32            pmDataCnt       number of blocks in data area
     * 88   4     UInt32            pmPartStatus    partition status information
     * 92   4     UInt32            pmLgBootStart   first logical block of boot code
     * 96   4     UInt32            pmBootSize      size of boot code, in bytes
     * 100  4     UInt32            pmBootAddr      boot code load address
     * 104  4     UInt32            pmBootAddr2     reserved
     * 108  4     UInt32            pmBootEntry     boot code entry point
     * 112  4     UInt32            pmBootEntry2    reserved
     * 116  4     UInt32            pmBootCksum     boot code checksum
     * 120  1*16  Char[16]          pmProcessor     processor type
     * 136  2*188 UInt16[188]       pmPad           reserved
     */
    private final byte[] pmSig = new byte[2];
    private final byte[] pmSigPad = new byte[2];
    private final byte[] pmMapBlkCnt = new byte[4];
    private final byte[] pmPyPartStart = new byte[4];
    private final byte[] pmPartBlkCnt = new byte[4];
    private final byte[] pmPartName = new byte[32];
    private final byte[] pmParType = new byte[32];
    private final byte[] pmLgDataStart = new byte[4];
    private final byte[] pmDataCnt = new byte[4];
    private final byte[] pmPartStatus = new byte[4];
    private final byte[] pmLgBootStart = new byte[4];
    private final byte[] pmBootSize = new byte[4];
    private final byte[] pmBootAddr = new byte[4];
    private final byte[] pmBootAddr2 = new byte[4];
    private final byte[] pmBootEntry = new byte[4];
    private final byte[] pmBootEntry2 = new byte[4];
    private final byte[] pmBootCksum = new byte[4];
    private final byte[] pmProcessor = new byte[16];
    private final byte[] pmPad = new byte[2*188];
    
    private final int blockSize;
    
    public APMPartition(byte[] data, int offset, int blockSize) {
        System.arraycopy(data, offset + 0, pmSig, 0, 2);
        System.arraycopy(data, offset + 2, pmSigPad, 0, 2);
        System.arraycopy(data, offset + 4, pmMapBlkCnt, 0, 4);
        System.arraycopy(data, offset + 8, pmPyPartStart, 0, 4);
        System.arraycopy(data, offset + 12, pmPartBlkCnt, 0, 4);
        System.arraycopy(data, offset + 16, pmPartName, 0, 32);
        System.arraycopy(data, offset + 48, pmParType, 0, 32);
        System.arraycopy(data, offset + 80, pmLgDataStart, 0, 4);
        System.arraycopy(data, offset + 84, pmDataCnt, 0, 4);
        System.arraycopy(data, offset + 88, pmPartStatus, 0, 4);
        System.arraycopy(data, offset + 92, pmLgBootStart, 0, 4);
        System.arraycopy(data, offset + 96, pmBootSize, 0, 4);
        System.arraycopy(data, offset + 100, pmBootAddr, 0, 4);
        System.arraycopy(data, offset + 104, pmBootAddr2, 0, 4);
        System.arraycopy(data, offset + 108, pmBootEntry, 0, 4);
        System.arraycopy(data, offset + 112, pmBootEntry2, 0, 4);
        System.arraycopy(data, offset + 116, pmBootCksum, 0, 4);
        System.arraycopy(data, offset + 120, pmProcessor, 0, 16);
        System.arraycopy(data, offset + 136, pmPad, 0, 2 * 188);

        this.blockSize = blockSize;
    }
    public static int structSize() { return 512; }
    // Defined in Partition
    public long getStartOffset() { return ( Util.unsign(getPmPyPartStart())+
					    Util.unsign(getPmLgDataStart()) )*blockSize; }
    public long getLength() { return Util.unsign(getPmDataCnt())*blockSize; }
    public PartitionType getType() { return convertPartitionType(getPmParType()); }
    
    public short getPmSig()        { return Util.readShortBE(pmSig); }
    public short getPmSigPad()     { return Util.readShortBE(pmSigPad); }
    public int getPmMapBlkCnt()    { return Util.readIntBE(pmMapBlkCnt); }
    public int getPmPyPartStart()  { return Util.readIntBE(pmPyPartStart); }
    public int getPmPartBlkCnt()   { return Util.readIntBE(pmPartBlkCnt); }
    public byte[] getPmPartName()  { return Util.createCopy(pmPartName); }
    public byte[] getPmParType()   { return Util.createCopy(pmParType); }
    public int getPmLgDataStart()  { return Util.readIntBE(pmLgDataStart); }
    public int getPmDataCnt()      { return Util.readIntBE(pmDataCnt); }
    public int getPmPartStatus()   { return Util.readIntBE(pmPartStatus); }
    public int getPmLgBootStart()  { return Util.readIntBE(pmLgBootStart); }
    public int getPmBootSize()     { return Util.readIntBE(pmBootSize); }
    public int getPmBootAddr()     { return Util.readIntBE(pmBootAddr); }
    public int getPmBootAddr2()    { return Util.readIntBE(pmBootAddr2); }
    public int getPmBootEntry()    { return Util.readIntBE(pmBootEntry); }
    public int getPmBootEntry2()   { return Util.readIntBE(pmBootEntry2); }
    public int getPmBootCksum()    { return Util.readIntBE(pmBootCksum); }
    public byte[] getPmProcessor() { return Util.createCopy(pmProcessor); }
    public short[] getPmPad()      { return Util.readShortArrayBE(pmPad); }
    
    public String getPmSigAsString() { return Util.toASCIIString(pmSig); }
    public String getPmPartNameAsString() { return Util.readNullTerminatedASCIIString(pmPartName); }
    public String getPmParTypeAsString() { return Util.readNullTerminatedASCIIString(pmParType); }
    public String getPmProcessorAsString() { return Util.readNullTerminatedASCIIString(pmProcessor); }
    
    public boolean isValid() {
        // Signature check
        int pmSigInt = getPmSig() & 0xFFFF;
        if(pmSigInt != 0x504D && // Signature "PM", in ASCII
           pmSigInt != 0x5453) // Older signature, but still supported.
            return false;
        
        return true;
    }
    
    public void printPartitionInfo(PrintStream ps) {
        printPartitionInfo(ps, "");
    }

    public void printPartitionInfo(PrintStream ps, String prefix) {
        ps.println(prefix + "pmSig: \"" + getPmSigAsString() + "\"");
        ps.println(prefix + "pmSigPad: " + getPmSigPad());
        ps.println(prefix + "pmMapBlkCnt: " + getPmMapBlkCnt());
        ps.println(prefix + "pmPyPartStart: " + getPmPyPartStart());
        ps.println(prefix + "pmPartBlkCnt: " + getPmPartBlkCnt());
        ps.println(prefix + "pmPartName: \"" + getPmPartNameAsString() + "\"");
        ps.println(prefix + "pmParType: \"" + getPmParTypeAsString() + "\"");
        ps.println(prefix + "pmLgDataStart: " + getPmLgDataStart());
        ps.println(prefix + "pmDataCnt: " + getPmDataCnt());
        ps.println(prefix + "pmPartStatus: " + getPmPartStatus());
        ps.println(prefix + "pmLgBootStart: " + getPmLgBootStart());
        ps.println(prefix + "pmBootSize: " + getPmBootSize());
        ps.println(prefix + "pmBootAddr: " + getPmBootAddr());
        ps.println(prefix + "pmBootAddr2: " + getPmBootAddr2());
        ps.println(prefix + "pmBootEntry: " + getPmBootEntry());
        ps.println(prefix + "pmBootEntry2: " + getPmBootEntry2());
        ps.println(prefix + "pmBootCksum: " + getPmBootCksum());
        ps.println(prefix + "pmProcessor: \"" + getPmProcessorAsString() + "\"");
        ps.println(prefix + "pmPad: " + getPmPad());
    }

    public byte[] getData() {
        byte[] result = new byte[structSize()];
        int offset = 0;
        System.arraycopy(pmSig, 0, result, offset, pmSig.length); offset += pmSig.length;
        System.arraycopy(pmSigPad, 0, result, offset, pmSigPad.length); offset += pmSigPad.length;
        System.arraycopy(pmMapBlkCnt, 0, result, offset, pmMapBlkCnt.length); offset += pmMapBlkCnt.length;
        System.arraycopy(pmPyPartStart, 0, result, offset, pmPyPartStart.length); offset += pmPyPartStart.length;
        System.arraycopy(pmPartBlkCnt, 0, result, offset, pmPartBlkCnt.length); offset += pmPartBlkCnt.length;
        System.arraycopy(pmPartName, 0, result, offset, pmPartName.length); offset += pmPartName.length;
        System.arraycopy(pmParType, 0, result, offset, pmParType.length); offset += pmParType.length;
        System.arraycopy(pmLgDataStart, 0, result, offset, pmLgDataStart.length); offset += pmLgDataStart.length;
        System.arraycopy(pmDataCnt, 0, result, offset, pmDataCnt.length); offset += pmDataCnt.length;
        System.arraycopy(pmPartStatus, 0, result, offset, pmPartStatus.length); offset += pmPartStatus.length;
        System.arraycopy(pmLgBootStart, 0, result, offset, pmLgBootStart.length); offset += pmLgBootStart.length;
        System.arraycopy(pmBootSize, 0, result, offset, pmBootSize.length); offset += pmBootSize.length;
        System.arraycopy(pmBootAddr, 0, result, offset, pmBootAddr.length); offset += pmBootAddr.length;
        System.arraycopy(pmBootAddr2, 0, result, offset, pmBootAddr2.length); offset += pmBootAddr2.length;
        System.arraycopy(pmBootEntry, 0, result, offset, pmBootEntry.length); offset += pmBootEntry.length;
        System.arraycopy(pmBootEntry2, 0, result, offset, pmBootEntry2.length); offset += pmBootEntry2.length;
        System.arraycopy(pmBootCksum, 0, result, offset, pmBootCksum.length); offset += pmBootCksum.length;
        System.arraycopy(pmProcessor, 0, result, offset, pmProcessor.length); offset += pmProcessor.length;
        System.arraycopy(pmPad, 0, result, offset, pmPad.length); offset += pmPad.length;
	       //System.arraycopy(, 0, result, offset, .length); offset += .length;
        if(offset != result.length)
            throw new RuntimeException("Internal miscalculation...");
        else
            return result;
    }
    
    public void printFields(PrintStream ps, String prefix) {
        printPartitionInfo(ps, prefix + " ");
        ps.println(prefix + " Partition methods: ");
        ps.println(prefix + "  getStartOffset(): " + getStartOffset());
        ps.println(prefix + "  getLength(): " + getLength());
        ps.println(prefix + "  getType(): " + getType());
    }

    public void print(PrintStream ps, String prefix) {
        ps.println(prefix + "APMPartition:");
        printFields(ps, prefix);
    }

    @Override
    public String toString() {
        return "\"" + getPmPartNameAsString() + "\" (" + getPmParTypeAsString() + ")";
    }

    public PartitionType convertPartitionType(byte[] parTypeData) {
        String typeString = Util.readNullTerminatedASCIIString(parTypeData);
        if(typeString.equals("Apple_partition_map")) // Partition contains a partition map
            return PartitionType.APPLE_PARTITION_MAP;
        else if(typeString.equals("Apple_Driver")) // Partition contains a device driver
            return PartitionType.APPLE_DRIVER;
        else if(typeString.equals("Apple_Driver43")) // Partition contains a SCSI Manager 4.3 device driver
            return PartitionType.APPLE_DRIVER43;
        else if(typeString.equals("Apple_MFS")) // Partition uses the original Macintosh File System (64K ROM version)
            return PartitionType.APPLE_MFS;
        else if(typeString.equals("Apple_HFS")) // Partition uses the Hierarchical File System (128K and later ROM versions)
            return PartitionType.APPLE_HFS_CONTAINER;
        else if(typeString.equals("Apple_HFSX")) // Partition uses HFSX. Presently, we report it as HFS+, and let the mounter decide.
            return PartitionType.APPLE_HFSX;
        else if(typeString.equals("Apple_Unix_SVR2")) // Partition uses the Unix file system
            return PartitionType.APPLE_UNIX_SVR2;
        else if(typeString.equals("Apple_PRODOS")) // Partition uses the ProDOS file system
            return PartitionType.APPLE_PRODOS;
        else if(typeString.equals("Apple_Free")) // Partition is unused
            return PartitionType.EMPTY;
        else if(typeString.equals("Apple_Scratch")) // Partition is empty
            return PartitionType.EMPTY;
        else
            return PartitionType.UNKNOWN;
    }
}
