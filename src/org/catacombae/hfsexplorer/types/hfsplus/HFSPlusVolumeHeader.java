/*-
 * Copyright (C) 2006 Erik Larsson
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

package org.catacombae.hfsexplorer.types.hfsplus;

import org.catacombae.hfsexplorer.Util;
import org.catacombae.csjc.MutableStruct;
import java.util.Date;
import java.io.*;
import org.catacombae.csjc.StructElements;
import org.catacombae.csjc.structelements.ASCIIStringField;
import org.catacombae.csjc.structelements.Dictionary;
import org.catacombae.csjc.structelements.DictionaryBuilder;

public class HFSPlusVolumeHeader extends MutableStruct implements StructElements {
    public static final short SIGNATURE_HFS_PLUS = 0x482B;
    public static final short SIGNATURE_HFSX = 0x4858;
    /* 
     * struct HFSPlusVolumeHeader
     * size: 512 bytes
     *
     * BP   Size  Type              Variable name
     * --------------------------------------------------------------
     * 0    2     UInt16            signature
     * 2    2     UInt16            version
     * 4    4     UInt32            attributes
     * 8    4     UInt32            lastMountedVersion
     * 12   4     UInt32            journalInfoBlock
     * 16   4     UInt32            createDate
     * 20   4     UInt32            modifyDate
     * 24   4     UInt32            backupDate
     * 28   4     UInt32            checkedDate
     * 32   4     UInt32            fileCount
     * 36   4     UInt32            folderCount
     * 40   4     UInt32            blockSize
     * 44   4     UInt32            totalBlocks
     * 48   4     UInt32            freeBlocks
     * 52   4     UInt32            nextAllocation
     * 56   4     UInt32            rsrcClumpSize
     * 60   4     UInt32            dataClumpSize
     * 64   4     HFSCatalogNodeID  nextCatalogID (HFSCatalogNodeID)
     * 68   4     UInt32            writeCount
     * 72   8     UInt64            encodingsBitmap
     * 80   4*8   UInt32[8]         finderInfo[8]
     * 112  80    HFSPlusForkData   allocationFile
     * 192  80    HFSPlusForkData   extentsFile
     * 272  80    HFSPlusForkData   catalogFile
     * 352  80    HFSPlusForkData   attributesFile
     * 432  80    HFSPlusForkData   startupFile
     */
	
    private final byte[] signature = new byte[2];
    private final byte[] version = new byte[2];
    private final byte[] attributes = new byte[4];
    private final byte[] lastMountedVersion = new byte[4];
    private final byte[] journalInfoBlock = new byte[4];
    private final byte[] createDate = new byte[4];
    private final byte[] modifyDate = new byte[4];
    private final byte[] backupDate = new byte[4];
    private final byte[] checkedDate = new byte[4];
    private final byte[] fileCount = new byte[4];
    private final byte[] folderCount = new byte[4];
    private final byte[] blockSize = new byte[4];
    private final byte[] totalBlocks = new byte[4];
    private final byte[] freeBlocks = new byte[4];
    private final byte[] nextAllocation = new byte[4];
    private final byte[] rsrcClumpSize = new byte[4];
    private final byte[] dataClumpSize = new byte[4];
    private final HFSCatalogNodeID nextCatalogID;
    private final byte[] writeCount = new byte[4];
    private final byte[] encodingsBitmap = new byte[8];
    private final byte[] finderInfo = new byte[4*8];
    private final HFSPlusForkData allocationFile;
    private final HFSPlusForkData extentsFile;
    private final HFSPlusForkData catalogFile;
    private final HFSPlusForkData attributesFile;
    private final HFSPlusForkData startupFile;
	
    public HFSPlusVolumeHeader(byte[] data) {
	this(data, 0);
    }
	
    public HFSPlusVolumeHeader(byte[] data, int offset) {
	//this(new ByteArrayInputStream(data, offset, _getSize()));
	System.arraycopy(data, offset+0, signature, 0, 2);
	System.arraycopy(data, offset+2, version, 0, 2);
	System.arraycopy(data, offset+4, attributes, 0, 4);
	System.arraycopy(data, offset+8, lastMountedVersion, 0, 4);
	System.arraycopy(data, offset+12, journalInfoBlock, 0, 4);
	System.arraycopy(data, offset+16, createDate, 0, 4);
	System.arraycopy(data, offset+20, modifyDate, 0, 4);
	System.arraycopy(data, offset+24, backupDate, 0, 4);
	System.arraycopy(data, offset+28, checkedDate, 0, 4);
	System.arraycopy(data, offset+32, fileCount, 0, 4);
	System.arraycopy(data, offset+36, folderCount, 0, 4);
	System.arraycopy(data, offset+40, blockSize, 0, 4);
	System.arraycopy(data, offset+44, totalBlocks, 0, 4);
	System.arraycopy(data, offset+48, freeBlocks, 0, 4);
	System.arraycopy(data, offset+52, nextAllocation, 0, 4);
	System.arraycopy(data, offset+56, rsrcClumpSize, 0, 4);
	System.arraycopy(data, offset+60, dataClumpSize, 0, 4);
	//System.arraycopy(data, 64, nextCatalogID, 0, 4);// (HFSCatalogNodeID)
	nextCatalogID = new HFSCatalogNodeID(data, offset+64);
	System.arraycopy(data, offset+68, writeCount, 0, 4);
	System.arraycopy(data, offset+72, encodingsBitmap, 0, 4);
	System.arraycopy(data, offset+80, finderInfo, 0, 4*8);
	//System.arraycopy(data, 112, allocationFile, 0, 80);
	allocationFile = new HFSPlusForkData(data, offset+112);
	//System.arraycopy(data, 192, extentsFile, 0, 80);
	extentsFile = new HFSPlusForkData(data, offset+192);
	//System.arraycopy(data, 272, catalogFile, 0, 80);
	catalogFile = new HFSPlusForkData(data, offset+272);
	//System.arraycopy(data, 352, attributesFile, 0, 80);
	attributesFile = new HFSPlusForkData(data, offset+352);
	//System.arraycopy(data, 432, startupFile, 0, 80);
	startupFile = new HFSPlusForkData(data, offset+432);
    }
	
    public HFSPlusVolumeHeader(InputStream is) throws IOException {
	this(Util.fillBuffer(is, new byte[_getSize()]), 0);
    }
	
    private static int _getSize() {
	return 512;
    }
	
    public short getSignature()                { return Util.readShortBE(signature); } // UInt16 0x0
    public short getVersion()                  { return Util.readShortBE(version); } // UInt16 0x2
    public int getAttributes()                 { return Util.readIntBE(attributes); } // UInt32 0x4
    public int getLastMountedVersion()         { return Util.readIntBE(lastMountedVersion); } // UInt32 0x8
    public int getJournalInfoBlock()           { return Util.readIntBE(journalInfoBlock); } // UInt32 0xC
    public int getCreateDate()                 { return Util.readIntBE(createDate); } // UInt32 0x10
    public int getModifyDate()                 { return Util.readIntBE(modifyDate); } // UInt32 0x14
    public int getBackupDate()                 { return Util.readIntBE(backupDate); } // UInt32 0x18
    public int getCheckedDate()                { return Util.readIntBE(checkedDate); } // UInt32 0x1C
    public int getFileCount()                  { return Util.readIntBE(fileCount); } // UInt32 0x20
    public int getFolderCount()                { return Util.readIntBE(folderCount); } // UInt32 0x24
    public int getBlockSize()                  { return Util.readIntBE(blockSize); } // UInt32 0x28
    public int getTotalBlocks()                { return Util.readIntBE(totalBlocks); } // UInt32 0x2C
    public int getFreeBlocks()                 { return Util.readIntBE(freeBlocks); } // UInt32 0x30
    public int getNextAllocation()             { return Util.readIntBE(nextAllocation); } // UInt32 0x34
    public int getRsrcClumpSize()              { return Util.readIntBE(rsrcClumpSize); } // UInt32 0x38
    public int getDataClumpSize()              { return Util.readIntBE(dataClumpSize); } // UInt32 0x3C
    public HFSCatalogNodeID getNextCatalogID() { return nextCatalogID; } // typedef HFSCatalogNodeID UInt32 0x40
    public int getWriteCount()                 { return Util.readIntBE(writeCount); } // UInt32 0x44
    public long getEncodingsBitmap()           { return Util.readLongBE(encodingsBitmap); } // UInt64 0x48
    public int[] getFinderInfo()               { return Util.readIntArrayBE(finderInfo); } // UInt32[8] 0x50
 
    public HFSPlusForkData getAllocationFile() { return allocationFile; } // 0x70
    public HFSPlusForkData getExtentsFile()    { return extentsFile; } // 0xC0
    public HFSPlusForkData getCatalogFile()    { return catalogFile; } // 0x110
    public HFSPlusForkData getAttributesFile() { return attributesFile; } // 0x160
    public HFSPlusForkData getStartupFile()    { return startupFile; } // 0x1B0

    public Date getCreateDateAsDate() {
        /* Dates in the volume header are, contrary to elsewhere is HFS+, stored
         * in local time. */
        return HFSPlusDate.localTimestampToDate(getCreateDate());
    }
    public Date getModifyDateAsDate() {
        /* Dates in the volume header are, contrary to elsewhere is HFS+, stored
         * in local time. */
        return HFSPlusDate.localTimestampToDate(getModifyDate());
    }
    public Date getBackupDateAsDate() {
        /* Dates in the volume header are, contrary to elsewhere is HFS+, stored
         * in local time. */
        return HFSPlusDate.localTimestampToDate(getBackupDate());
    }
    public Date getCheckedDateAsDate() {
        /* Dates in the volume header are, contrary to elsewhere is HFS+, stored
         * in local time. */
        return HFSPlusDate.localTimestampToDate(getCheckedDate());
    }
    
    public boolean getAttributeVolumeHardwareLock()     { return ((getAttributes() >> 7) & 0x1) != 0; }
    public boolean getAttributeVolumeUnmounted()        { return ((getAttributes() >> 8) & 0x1) != 0; }
    public boolean getAttributeVolumeSparedBlocks()     { return ((getAttributes() >> 9) & 0x1) != 0; }
    public boolean getAttributeVolumeNoCacheRequired()  { return ((getAttributes() >> 10) & 0x1) != 0; }
    public boolean getAttributeBootVolumeInconsistent() { return ((getAttributes() >> 11) & 0x1) != 0; }
    public boolean getAttributeCatalogNodeIDsReused()   { return ((getAttributes() >> 12) & 0x1) != 0; }
    public boolean getAttributeVolumeJournaled()        { return ((getAttributes() >> 13) & 0x1) != 0; }
    public boolean getAttributeVolumeSoftwareLock()     { return ((getAttributes() >> 15) & 0x1) != 0; }

    public void print(PrintStream ps, String prefix) {

        ps.println(prefix + "signature: \"" + Util.toASCIIString(getSignature()) + "\"");
        ps.println(prefix + "version: " + getVersion());
        ps.println(prefix + "attributes: " + getAttributes());
        printAttributes(ps, prefix + "  ");
        ps.println(prefix + "lastMountedVersion: " + getLastMountedVersion());
        ps.println(prefix + "journalInfoBlock: " + getJournalInfoBlock());
        ps.println(prefix + "createDate: " + getCreateDateAsDate());
        ps.println(prefix + "modifyDate: " + getModifyDateAsDate());
        ps.println(prefix + "backupDate: " + getBackupDateAsDate());
        ps.println(prefix + "checkedDate: " + getCheckedDateAsDate());
        ps.println(prefix + "fileCount: " + getFileCount());
        ps.println(prefix + "folderCount: " + getFolderCount());
        ps.println(prefix + "blockSize: " + getBlockSize());
        ps.println(prefix + "totalBlocks: " + getTotalBlocks());
        ps.println(prefix + "freeBlocks: " + getFreeBlocks());
        ps.println(prefix + "nextAllocation: " + getNextAllocation());
        ps.println(prefix + "rsrcClumpSize: " + getRsrcClumpSize());
        ps.println(prefix + "dataClumpSize: " + getDataClumpSize());
        ps.println(prefix + "nextCatalogID: " + getNextCatalogID().toString());
        ps.println(prefix + "writeCount: " + getWriteCount());
        ps.println(prefix + "encodingsBitmap: " + getEncodingsBitmap());
        ps.println(prefix + "encodingsBitmap (hex): 0x" + Util.toHexStringBE(getEncodingsBitmap()));

        int[] finderInfoInts = getFinderInfo();
        for(int i = 0; i < finderInfoInts.length; ++i)
            ps.println(prefix + "finderInfo[" + i + "]: " + finderInfoInts[i]);

        ps.println(prefix + "allocationFile: ");
        allocationFile.print(ps, prefix + "  ");
        ps.println(prefix + "extentsFile: ");
        extentsFile.print(ps, prefix + "  ");
        ps.println(prefix + "catalogFile: ");
        catalogFile.print(ps, prefix + "  ");
        ps.println(prefix + "attributesFile: ");
        attributesFile.print(ps, prefix + "  ");
        ps.println(prefix + "startupFile: ");
        startupFile.print(ps, prefix + "  ");
    // 	    ps.println(prefix + ": " + );
    }
	
    public void printAttributes(PrintStream ps, int pregap) {
        String pregapString = "";
        for(int i = 0; i < pregap; ++i)
            pregapString += " ";
    }

    public void printAttributes(PrintStream ps, String prefix) {
        /* 32 bits of attributes exist. Bits 0-6, 14 and 16-31 are reserved. */

        int attributesInt = getAttributes();
        ps.println(prefix + "kHFSVolumeHardwareLockBit = " + ((attributesInt >> 7) & 0x1));
        ps.println(prefix + "kHFSVolumeUnmountedBit = " + ((attributesInt >> 8) & 0x1));
        ps.println(prefix + "kHFSVolumeSparedBlocksBit = " + ((attributesInt >> 9) & 0x1));
        ps.println(prefix + "kHFSVolumeNoCacheRequiredBit = " + ((attributesInt >> 10) & 0x1));
        ps.println(prefix + "kHFSBootVolumeInconsistentBit = " + ((attributesInt >> 11) & 0x1));
        ps.println(prefix + "kHFSCatalogNodeIDsReusedBit = " + ((attributesInt >> 12) & 0x1));
        ps.println(prefix + "kHFSVolumeJournaledBit = " + ((attributesInt >> 13) & 0x1));
        ps.println(prefix + "kHFSVolumeSoftwareLockBit = " + ((attributesInt >> 15) & 0x1));
    }

    /*
    public static void main(String[] args) throws Exception {
        if(args.length != 2)
            System.err.println("usage: main <file> <byte position>");
        else {
            Long pos = Long.parseLong(args[1]);
            
            RandomAccessFile raf = new RandomAccessFile(args[0], "r");
            raf.seek(pos);
            byte[] buf = new byte[_getSize()];
            raf.readFully(buf);
            raf.close();
            
            new HFSPlusVolumeHeader(buf).print(System.out, "");
        }
    }
    */
    
    private Dictionary getAttributeElements() {
        DictionaryBuilder db = new DictionaryBuilder("Attributes");

        db.addFlag("kHFSVolumeHardwareLockBit", attributes, 7);
        db.addFlag("kHFSVolumeUnmountedBit", attributes, 8);
        db.addFlag("kHFSVolumeSparedBlocksBit", attributes, 9);
        db.addFlag("kHFSVolumeNoCacheRequiredBit", attributes, 10);
        db.addFlag("kHFSBootVolumeInconsistentBit", attributes, 11);
        db.addFlag("kHFSCatalogNodeIDsReusedBit", attributes, 12);
        db.addFlag("kHFSVolumeJournaledBit", attributes, 13);
        db.addFlag("kHFSVolumeSoftwareLockBit", attributes, 15);

        return db.getResult();
    }

    public Dictionary getStructElements() {
        DictionaryBuilder db = new DictionaryBuilder(HFSPlusVolumeHeader.class.getSimpleName());

        db.add("signature", new ASCIIStringField(signature));
        db.addUIntBE("version", version);
        db.add("attributes", getAttributeElements());
        db.addUIntBE("lastMountedVersion", lastMountedVersion);
        db.addUIntBE("journalInfoBlock", journalInfoBlock);
        db.add("createDate", new HFSPlusDateField(createDate, true));
        db.add("modifyDate", new HFSPlusDateField(modifyDate, true));
        db.add("backupDate", new HFSPlusDateField(backupDate, true));
        db.add("checkedDate", new HFSPlusDateField(checkedDate, true));
        db.addUIntBE("fileCount", fileCount);
        db.addUIntBE("folderCount", folderCount);
        db.addUIntBE("blockSize", blockSize);
        db.addUIntBE("totalBlocks", totalBlocks);
        db.addUIntBE("freeBlocks", freeBlocks);
        db.addUIntBE("nextAllocation", nextAllocation);
        db.addUIntBE("rsrcClumpSize", rsrcClumpSize);
        db.addUIntBE("dataClumpSize", dataClumpSize);
        db.add("nextCatalogID", nextCatalogID.getStructElements());
        db.addUIntBE("writeCount", writeCount);
        db.addUIntBE("encodingsBitmap", encodingsBitmap);
        db.addIntArray("finderInfo", finderInfo, BITS_32, SIGNED, BIG_ENDIAN);
        db.add("allocationFile", allocationFile.getStructElements());
        db.add("extentsFile", extentsFile.getStructElements());
        db.add("catalogFile", catalogFile.getStructElements());
        db.add("attributesFile", attributesFile.getStructElements());
        db.add("startupFile", startupFile.getStructElements());

        return db.getResult();
    }
}
