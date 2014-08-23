/*-
 * Copyright (C) 2006-2011 Erik Larsson
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

package org.catacombae.storage.ps.apm.types;

import org.catacombae.util.Util;
import java.io.PrintStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import org.catacombae.storage.ps.Partition;
import org.catacombae.storage.ps.PartitionType;

public class APMPartition implements Partition {
    public static final short APM_PARTITION_SIGNATURE = 0x504D;
    public static final short APM_PARTITION_OLD_SIGNATURE = 0x5453;

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

    /**
     * Creates a new APMPartition with the specified parameters.
     *
     * @param partitionMapBlockCount
     *      The number of blocks in the partition map that contains this
     *      partition.
     * @param partitionStartBlock
     *      The first block of the partition's data.
     * @param partitionBlockCount
     *      The number of blocks in the partition.
     * @param partitionName
     *      The name of the partition.
     * @param partitionType
     *      The type of the partition (Apple_HFS, Apple_partition_map, ...).
     * @param partitionStatus
     *      The status bits of the partition (see source code for more info).
     * @param blockSize
     *      The block size of the device, as specified in the Driver Descriptor
     *      Record.
     */
    public APMPartition(long partitionMapBlockCount, long partitionStartBlock,
            long partitionBlockCount, String partitionName,
            String partitionType, int partitionStatus, int blockSize) {

        /* Input check: Number ranges. */

        if(partitionMapBlockCount < 0 || partitionMapBlockCount >= 0xFFFFFFFFL)
            throw new IllegalArgumentException("'partitionMapBlockCount' out " +
                    "of range: " + partitionMapBlockCount);

        if(partitionStartBlock < 0 || partitionStartBlock >= 0xFFFFFFFFL)
            throw new IllegalArgumentException("'partitionStartBlock' out of " +
                    "range: " + partitionStartBlock);

        if(partitionBlockCount < 0 || partitionBlockCount >= 0xFFFFFFFFL)
            throw new IllegalArgumentException("'partitionBlockCount' out of " +
                    "range: " + partitionBlockCount);

        int partitionNameEncodedLength =
                partitionName.codePointCount(0, partitionName.length());
        if(partitionNameEncodedLength < 0 ||
                partitionNameEncodedLength > pmPartName.length)
            throw new IllegalArgumentException("'partitionName' string too " +
                    "long: " + partitionName.length());

        /* Input check: String lengths. */

        int partitionTypeEncodedLength =
                partitionType.codePointCount(0, partitionType.length());
        if(partitionTypeEncodedLength < 0 ||
                partitionTypeEncodedLength > pmParType.length)
            throw new IllegalArgumentException("'partitionType' string too " +
                    "long: " + partitionType.length());

        /* Filling in the APMPartition fields. */

        Util.arrayPutBE(pmSig, 0, APM_PARTITION_SIGNATURE);
        Arrays.fill(pmSigPad, (byte) 0);
        Util.arrayPutBE(pmMapBlkCnt, 0, (int) partitionMapBlockCount);
        Util.arrayPutBE(pmPyPartStart, 0, (int) partitionStartBlock);
        Util.arrayPutBE(pmPartBlkCnt, 0, (int) partitionBlockCount);

        try {
            Util.encodeASCIIString(partitionName, 0, pmPartName, 0,
                    partitionNameEncodedLength);
            if(partitionNameEncodedLength < pmPartName.length)
                Arrays.fill(pmPartName, partitionNameEncodedLength,
                        pmPartName.length, (byte) 0);
        } catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("'partitionName' has illegal " +
                    "(non-ASCII) characters.");
        }

        try {
            Util.encodeASCIIString(partitionType, 0, pmParType, 0,
                    partitionTypeEncodedLength);
            if(partitionTypeEncodedLength < pmParType.length)
                Arrays.fill(pmParType, partitionTypeEncodedLength,
                        pmParType.length, (byte) 0);
        } catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("'partitionType' has illegal " +
                    "(non-ASCII) characters.");
        }

        Util.arrayPutBE(pmLgDataStart, 0, (int) 0);
        Util.arrayPutBE(pmDataCnt, 0, (int) partitionBlockCount);
        Util.arrayPutBE(pmPartStatus, 0, partitionStatus);
        Util.arrayPutBE(pmLgBootStart, 0, (int) 0);
        Util.arrayPutBE(pmBootSize, 0, (int) 0);
        Util.arrayPutBE(pmBootAddr, 0, (int) 0);
        Util.arrayPutBE(pmBootAddr2, 0, (int) 0);
        Util.arrayPutBE(pmBootEntry, 0, (int) 0);
        Util.arrayPutBE(pmBootEntry2, 0, (int) 0);
        Util.arrayPutBE(pmBootCksum, 0, (int) 0);
        Arrays.fill(pmProcessor, (byte) 0);
        Arrays.fill(pmPad, (byte) 0);

        this.blockSize = blockSize;
    }

    public static int structSize() { return 512; }
    // Defined in Partition
    public long getStartOffset() { return ( Util.unsign(getPmPyPartStart())+
					    Util.unsign(getPmLgDataStart()) )*blockSize; }
    public long getLength() { return Util.unsign(getPmDataCnt())*blockSize; }
    public PartitionType getType() { return convertPartitionType(getPmParType()); }

    /** partition signature */
    public short getPmSig()        { return Util.readShortBE(pmSig); }
    /** reserved */
    public short getPmSigPad()     { return Util.readShortBE(pmSigPad); }
    /** number of blocks in partition map */
    public int getPmMapBlkCnt()    { return Util.readIntBE(pmMapBlkCnt); }
    /** first physical block of partition */
    public int getPmPyPartStart()  { return Util.readIntBE(pmPyPartStart); }
    /** number of blocks in partition */
    public int getPmPartBlkCnt()   { return Util.readIntBE(pmPartBlkCnt); }
    /** partition name */
    public byte[] getPmPartName()  { return Util.createCopy(pmPartName); }
    /** partition type */
    public byte[] getPmParType()   { return Util.createCopy(pmParType); }
    /** first logical block of data area */
    public int getPmLgDataStart()  { return Util.readIntBE(pmLgDataStart); }
    /** number of blocks in data area */
    public int getPmDataCnt()      { return Util.readIntBE(pmDataCnt); }
    /** partition status information */
    public int getPmPartStatus()   { return Util.readIntBE(pmPartStatus); }
    /** first logical block of boot code */
    public int getPmLgBootStart()  { return Util.readIntBE(pmLgBootStart); }
    /** size of boot code, in bytes */
    public int getPmBootSize()     { return Util.readIntBE(pmBootSize); }
    /** boot code load address */
    public int getPmBootAddr()     { return Util.readIntBE(pmBootAddr); }
    /** reserved */
    public int getPmBootAddr2()    { return Util.readIntBE(pmBootAddr2); }
    /** boot code entry point */
    public int getPmBootEntry()    { return Util.readIntBE(pmBootEntry); }
    /** reserved */
    public int getPmBootEntry2()   { return Util.readIntBE(pmBootEntry2); }
    /** boot code checksum */
    public int getPmBootCksum()    { return Util.readIntBE(pmBootCksum); }
    /** processor type */
    public byte[] getPmProcessor() { return Util.createCopy(pmProcessor); }
    /** reserved */
    public short[] getPmPad()      { return Util.readShortArrayBE(pmPad); }

    private static boolean getBit(byte[] array, int bit) {
        final long bitLength = ((long) array.length) << 3;
        if(bit < 0 || bit >= bitLength)
            throw new IllegalArgumentException("'bit' out of range: " + bit);

        final int arrayIndex = (int)((bitLength - 1 - bit) >>> 3);
        final int bitIndex = (bit & 0x7);

        return (array[arrayIndex] & (0x1 << bitIndex)) != 0;
    }

    public boolean getPmPartStatusValid() { return getBit(pmPartStatus, 0); }
    public boolean getPmPartStatusAllocated() { return getBit(pmPartStatus, 1); }
    public boolean getPmPartStatusInUse() { return getBit(pmPartStatus, 2); }
    public boolean getPmPartStatusBootable() { return getBit(pmPartStatus, 3); }
    public boolean getPmPartStatusReadable() { return getBit(pmPartStatus, 4); }
    public boolean getPmPartStatusWritable() { return getBit(pmPartStatus, 5); }
    public boolean getPmPartStatusOSPicCode() { return getBit(pmPartStatus, 6); }
    public boolean getPmPartStatusOSSpecific1() { return getBit(pmPartStatus, 7); }
    public boolean getPmPartStatusOSSpecific2() { return getBit(pmPartStatus, 8); }

    /**
     * Returns the partition signature as a String. (Should always be "PM".)
     * @return the partition signature as a String.
     */
    public String getPmSigAsString() { return Util.toASCIIString(pmSig); }

    /**
     * Returns the partition name as a String.
     * @return the partition name as a String.
     */
    public String getPmPartNameAsString() { return Util.readNullTerminatedASCIIString(pmPartName); }

    /**
     * Returns the partition type as a String.
     * @return the partition type as a String.
     */
    public String getPmParTypeAsString() { return Util.readNullTerminatedASCIIString(pmParType); }

    /**
     * Returns the processor type as a String.
     * @return the processor type as a String.
     */
    public String getPmProcessorAsString() { return Util.readNullTerminatedASCIIString(pmProcessor); }
    public byte[] getPmPadRaw()      { return Util.createCopy(pmPad); }

    public boolean isValid() {
        // Signature check
        int pmSigInt = getPmSig() & 0xFFFF;
        if(pmSigInt != APM_PARTITION_SIGNATURE && // Signature "PM", in ASCII
           pmSigInt != APM_PARTITION_OLD_SIGNATURE) // Older signature, but still supported.
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
        ps.println(prefix + "pmPartStatus: 0x" + Util.toHexStringBE(getPmPartStatus()));
        ps.println(prefix + "  valid: " + getPmPartStatusValid());
        ps.println(prefix + "  allocated: " + getPmPartStatusAllocated());
        ps.println(prefix + "  in use: " + getPmPartStatusInUse());
        ps.println(prefix + "  bootable: " + getPmPartStatusBootable());
        ps.println(prefix + "  readable: " + getPmPartStatusReadable());
        ps.println(prefix + "  writable: " + getPmPartStatusWritable());
        ps.println(prefix + "  OS pic code: " + getPmPartStatusOSPicCode());
        ps.println(prefix + "  OS specific 1: " + getPmPartStatusOSSpecific1());
        ps.println(prefix + "  OS specific 2: " + getPmPartStatusOSSpecific2());
        ps.println(prefix + "pmLgBootStart: " + getPmLgBootStart());
        ps.println(prefix + "pmBootSize: " + getPmBootSize());
        ps.println(prefix + "pmBootAddr: " + getPmBootAddr());
        ps.println(prefix + "pmBootAddr2: " + getPmBootAddr2());
        ps.println(prefix + "pmBootEntry: " + getPmBootEntry());
        ps.println(prefix + "pmBootEntry2: " + getPmBootEntry2());
        ps.println(prefix + "pmBootCksum: " + getPmBootCksum());
        ps.println(prefix + "pmProcessor: \"" + getPmProcessorAsString() + "\"");
        ps.println(prefix + "pmPad:");
        ps.print(prefix + " byte[" + pmPad.length + "] {");
        for(int i = 0; i < pmPad.length; ++i) {
            if(i % 16 == 0) {
                ps.println();
                ps.print(prefix + " ");
            }
            ps.print(" " + Util.toHexStringBE(pmPad[i]));
        }
        ps.println();
        ps.println(prefix + " }");
        try {
            byte[] md5sum = MessageDigest.getInstance("MD5").digest(pmPad);
            ps.println(prefix + " MD5: " + Util.byteArrayToHexString(md5sum));
        } catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
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
