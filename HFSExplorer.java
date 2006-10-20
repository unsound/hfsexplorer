import java.util.*;
import java.io.*;
import java.math.BigInteger;

public class HFSExplorer {
    public static BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
    private static class Options {
	public boolean readAPM = false;
	public boolean verbose = false;
    }
    private static Options options = new Options();
    
    public static void main(String[] args) throws IOException {
	println("HFS Explorer 0.1");
	if(args.length == 0) {
	    println("  displays information about an HFS filesystem.");
	    println("  usage: java HFSExplorer [options] <isofile>");
	    println();
	    println("  Options:");
	    println("    -apm  Specifies that the HFS partition is embedded within an Apple");
	    println("          Partition Map. The user will be allowed to choose which partition in");
	    println("          the map to attempt reading.");
	    println("    -v    Verbose operation.");
	    System.exit(0);
	}
	
	parseOptions(args, options, 0, args.length-1);
	
	//RandomAccessFile isoRaf = new RandomAccessFile(args[0], "r");
	WindowsLowLevelIO isoRaf = new WindowsLowLevelIO(args[args.length-1]);
	
	long offset = 0;
	if(options.readAPM) {
	    println("Reading the Apple Partition Map...");
	    isoRaf.seek(0x200);
	    byte[] currentBlock = new byte[512];
	    byte[] signature = new byte[2];
	    //APMPartition p = new APMPartition(isoRaf);
	    ArrayList<APMPartition> partitions = new ArrayList<APMPartition>();
	    for(int i = 0; i < 20; ++i) {
		isoRaf.readFully(currentBlock);
		signature[0] = currentBlock[0];
		signature[1] = currentBlock[1];
		if(new String(signature, "ASCII").equals("PM")) {
		    println("Partition " + i + ":");
		    APMPartition p = new APMPartition(currentBlock, 0);
		    partitions.add(p);
		    if(options.verbose)
			p.printPartitionInfo(System.out);
		}
		else break;
	    }
	    print("Which partition do you wish to explore [0-" + (partitions.size()-1) + "]? ");
	    int partNum = Integer.parseInt(stdin.readLine());
	    APMPartition chosenPartition = partitions.get(partNum);
	    String partitionType = new String(chosenPartition.pmParType, "ASCII");
	    if(!partitionType.trim().equals("Apple_HFS")) {
		println("The partition is not an HFS partition!");
		System.exit(0);
	    }
	    println("Parsing partition " + partNum + " (" + new String(chosenPartition.pmPartName, "ASCII").trim() + "/" + partitionType.trim() + ")");
	    offset = chosenPartition.getPmPyPartStart()*0x200;
	}
	byte[] currentBlock = new byte[512];
	isoRaf.seek(offset + 1024);
	isoRaf.read(currentBlock);
	HFSPlusVolumeHeader header = new HFSPlusVolumeHeader(currentBlock);
	header.print(System.out, 2);
    }
    public static void println() {
	//System.out.print(BACKSPACE79);
	System.out.println();
    }
    public static void println(String s) {
	//System.out.print(BACKSPACE79);
	System.out.println(s);
    }
    public static void print(String s) {
	//System.out.print(BACKSPACE79);
	System.out.print(s);
    }
    
    public static void parseOptions(String[] arguments, Options op, int offset, int length) {
	for(int i = offset; i < length; ++i) {
	    String currentArg = arguments[i];
	    if(currentArg.equals("-apm"))
		options.readAPM = true;
	    if(currentArg.equals("-v"))
		options.verbose = true;
	    else
		println("\"" + currentArg + "\" is not a valid parameter.");
	}
    }
    
    public static class APMPartition {
	/*
	 * struct Partition
	 * size: 512 bytes
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
	
	public APMPartition(byte[] data, int offset) {
	    System.arraycopy(data, offset+0, pmSig, 0, 2);
	    System.arraycopy(data, offset+2, pmSigPad, 0, 2);
	    System.arraycopy(data, offset+4, pmMapBlkCnt, 0, 4);
	    System.arraycopy(data, offset+8, pmPyPartStart, 0, 4);
	    System.arraycopy(data, offset+12, pmPartBlkCnt, 0, 4);
	    System.arraycopy(data, offset+16, pmPartName, 0, 32);
	    System.arraycopy(data, offset+48, pmParType, 0, 32);
	    System.arraycopy(data, offset+80, pmLgDataStart, 0, 4);
	    System.arraycopy(data, offset+84, pmDataCnt, 0, 4);
	    System.arraycopy(data, offset+88, pmPartStatus, 0, 4);
	    System.arraycopy(data, offset+92, pmLgBootStart, 0, 4);
	    System.arraycopy(data, offset+96, pmBootSize, 0, 4);
	    System.arraycopy(data, offset+100, pmBootAddr, 0, 4);
	    System.arraycopy(data, offset+104, pmBootAddr2, 0, 4);
	    System.arraycopy(data, offset+108, pmBootEntry, 0, 4);
	    System.arraycopy(data, offset+112, pmBootEntry2, 0, 4);
	    System.arraycopy(data, offset+116, pmBootCksum, 0, 4);
	    System.arraycopy(data, offset+120, pmProcessor, 0, 16);
	    System.arraycopy(data, offset+136, pmPad, 0, 2*188);
	}
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
	public short[] getPmPad()      { return Util2.readShortArrayBE(pmPad); }
	
	public void printPartitionInfo(PrintStream ps) {
	    printPartitionInfo(ps, "");
	}
	public void printPartitionInfo(PrintStream ps, String prefix) {
	    ps.println("pmSig: \"" + Util2.toASCIIString(pmSig) + "\"");
	    ps.println("pmSigPad: " + getPmSigPad());
	    ps.println("pmMapBlkCnt: " + getPmMapBlkCnt());
	    ps.println("pmPyPartStart: " + getPmPyPartStart());
	    ps.println("pmPartBlkCnt: " + getPmPartBlkCnt());
	    ps.println("pmPartName: \"" +  Util2.toASCIIString(pmPartName) + "\"");
	    ps.println("pmParType: \"" +  Util2.toASCIIString(pmParType) + "\"");
	    ps.println("pmLgDataStart: " + getPmLgDataStart());
	    ps.println("pmDataCnt: " + getPmDataCnt());
	    ps.println("pmPartStatus: " + getPmPartStatus());
	    ps.println("pmLgBootStart: " + getPmLgBootStart());
	    ps.println("pmBootSize: " + getPmBootSize());
	    ps.println("pmBootAddr: " + getPmBootAddr());
	    ps.println("pmBootAddr2: " + getPmBootAddr2());
	    ps.println("pmBootEntry: " + getPmBootEntry());
	    ps.println("pmBootEntry2: " + getPmBootEntry2());
	    ps.println("pmBootCksum: " + getPmBootCksum());
	    ps.println("pmProcessor: \"" + Util2.toASCIIString(pmProcessor) + "\"");
	    ps.println("pmPad: " + getPmPad());
	}
    }
    
    public static class HFSPlusVolumeHeader {
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
	    this(Util2.fillBuffer(is, new byte[_getSize()]), 0);
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
	public int[] getFinderInfo()               { return Util2.readIntArrayBE(finderInfo); } // UInt32[8] 0x50
 
	public HFSPlusForkData getAllocationFile() { return allocationFile; } // 0x70
	public HFSPlusForkData getExtentsFile()    { return extentsFile; } // 0xC0
	public HFSPlusForkData getCatalogFile()    { return catalogFile; } // 0x110
	public HFSPlusForkData getAttributesFile() { return attributesFile; } // 0x160
	public HFSPlusForkData getStartupFile()    { return startupFile; } // 0x1B0

	public void print(PrintStream ps, int pregap) {
	    String pregapString = "";
	    for(int i = 0; i < pregap; ++i)
		pregapString += " ";
	    print(ps, pregapString);
	}
	public void print(PrintStream ps, String prefix) {
	    
	    ps.println(prefix + "signature: \"" + Util2.toASCIIString(getSignature()) + "\"");
	    ps.println(prefix + "version: " + getVersion());
	    ps.println(prefix + "attributes: " + getAttributes());
	    printAttributes(ps, prefix + "  ");
	    ps.println(prefix + "lastMountedVersion: " + getLastMountedVersion());
	    ps.println(prefix + "journalInfoBlock: " + getJournalInfoBlock());
	    ps.println(prefix + "createDate: " + getCreateDate());
	    ps.println(prefix + "modifyDate: " + getModifyDate());
	    ps.println(prefix + "backupDate: " + getBackupDate());
	    ps.println(prefix + "checkedDate: " + getCheckedDate());
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

	    int[] finderInfo = getFinderInfo();
	    for(int i = 0; i < finderInfo.length; ++i)
		ps.println(prefix + "finderInfo[" + i + "]: " + finderInfo[i]);
	    
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
	    
	    int attributes = getAttributes();
	    ps.println(prefix + "kHFSVolumeHardwareLockBit = " + ((attributes >> 7) & 0x1));
	    ps.println(prefix + "kHFSVolumeUnmountedBit = " + ((attributes >> 8) & 0x1));
	    ps.println(prefix + "kHFSVolumeSparedBlocksBit = " + ((attributes >> 9) & 0x1));
	    ps.println(prefix + "kHFSVolumeNoCacheRequiredBit = " + ((attributes >> 10) & 0x1));
	    ps.println(prefix + "kHFSBootVolumeInconsistentBit = " + ((attributes >> 11) & 0x1));
	    ps.println(prefix + "kHFSCatalogNodeIDsReusedBit = " + ((attributes >> 12) & 0x1));
	    ps.println(prefix + "kHFSVolumeJournaledBit = " + ((attributes >> 13) & 0x1));
	    ps.println(prefix + "kHFSVolumeSoftwareLockBit = " + ((attributes >> 15) & 0x1));	    
	}
    }
    
    public static class HFSCatalogNodeID {
	private final byte[] hfsCatalogNodeID = new byte[4];
	
	public HFSCatalogNodeID(byte[] data, int offset) {
	    System.arraycopy(data, offset, hfsCatalogNodeID, 0, 4);
	}
	
	public int toInt() { return Util.readIntBE(hfsCatalogNodeID); }
	public String getDescription() {
	    /*
	     * kHFSRootParentID            = 1,
	     * kHFSRootFolderID            = 2,
	     * kHFSExtentsFileID           = 3,
	     * kHFSCatalogFileID           = 4,
	     * kHFSBadBlockFileID          = 5,
	     * kHFSAllocationFileID        = 6,
	     * kHFSStartupFileID           = 7,
	     * kHFSAttributesFileID        = 8,
	     * kHFSRepairCatalogFileID     = 14,
	     * kHFSBogusExtentFileID       = 15,
	     * kHFSFirstUserCatalogNodeID  = 16
	     */
	    String result;
	    switch(toInt()) {
	    case 1:
		result = "kHFSRootParentID";
		break;
	    case 2:
		result = "kHFSRootFolderID";
		break;
	    case 3:
		result = "kHFSExtentsFileID";
		break;
	    case 4:
		result = "kHFSCatalogFileID";
		break;
	    case 5:
		result = "kHFSBadBlockFileID";
		break;
	    case 6:
		result = "kHFSAllocationFileID";
		break;
	    case 7:
		result = "kHFSStartupFileID";
		break;
	    case 8:
		result = "kHFSAttributesFileID";
		break;
	    case 14:
		result = "kHFSRepairCatalogFileID";
		break;
	    case 15:
		result = "kHFSBogusExtentFileID";
		break;
	    case 16:
		result = "kHFSFirstUserCatalogNodeID";
		break;
	    default:
		result = "User Defined ID";
		break;
	    }
	    return result;
	}
	public String toString() {
	    return toInt() + " (" + getDescription() + ")";
	}
    }
    
    public static class HFSPlusForkData {
	private final byte[] logicalSize = new byte[8];
	private final byte[] clumpSize = new byte[4];
	private final byte[] totalBlocks = new byte[4];
	private final HFSPlusExtentRecord extents;

	public HFSPlusForkData(byte[] data, int offset) {
	    System.arraycopy(data, offset+0, logicalSize, 0, 8);
	    System.arraycopy(data, offset+8, clumpSize, 0, 4);
	    System.arraycopy(data, offset+12, totalBlocks, 0, 4);
	    extents = new HFSPlusExtentRecord(data, offset+16);
	}
	
	public long getLogicalSize() {
	    return Util.readLongBE(logicalSize);
	}
	public long getClumpSize() {
	    return Util.readIntBE(clumpSize);
	}
	public long getTotalBlocks() {
	    return Util.readIntBE(totalBlocks);
	}
	
	public void print(PrintStream ps, int pregap) {
	    String pregapString = "";
	    for(int i = 0; i < pregap; ++i)
		pregapString += " ";
	    print(ps, pregapString);
	}
	
	public void print(PrintStream ps, String prefix) {
	    ps.println(prefix + "logicalSize: " + getLogicalSize());
	    ps.println(prefix + "clumpSize: " + getClumpSize());
	    ps.println(prefix + "totalBlocks: " + getTotalBlocks());
	    ps.println(prefix + "extents:");
	    extents.print(ps, prefix + "  ");
	}
    }
    
    public static class HFSPlusExtentRecord {
	// 8*8 = 64 bytes
	private final HFSPlusExtentDescriptor[] array = new HFSPlusExtentDescriptor[8];

	public HFSPlusExtentRecord(byte[] data, int offset) {
	    for(int i = 0; i < array.length; ++i)
		array[i] = new HFSPlusExtentDescriptor(data, offset+i*HFSPlusExtentDescriptor.getSize());
	}
	public void print(PrintStream ps, int pregap) {
	    String pregapString = "";
	    for(int i = 0; i < pregap; ++i)
		pregapString += " ";
	    print(ps, pregapString);
	}
	public void print(PrintStream ps, String prefix) {
	    
	    for(int i = 0; i < array.length; ++i) {
		ps.println(prefix + "array[" + i + "]:");
		array[i].print(ps, prefix + "  ");
	    }
	}
    }
    
    public static class HFSPlusExtentDescriptor {
	// 4+4 = 8 bytes
	private final byte[] startBlock = new byte[4]; // UInt32
	private final byte[] blockCount = new byte[4]; // UInt32

	public HFSPlusExtentDescriptor(byte[] data, int offset) {
	    System.arraycopy(data, offset, startBlock, 0, 4);
	    System.arraycopy(data, offset+4, blockCount, 0, 4);
	}
	
	public static int getSize() {
	    return 8;
	}
	
	public int getStartBlock() { return Util.readIntBE(startBlock); }
	public int getBlockCount() { return Util.readIntBE(blockCount); }
	
	public void print(PrintStream ps, int pregap) {
	    String pregapString = "";
	    for(int i = 0; i < pregap; ++i)
		pregapString += " ";
	    print(ps, pregapString);
	}
	public void print(PrintStream ps, String prefix) {
 	    ps.println(prefix + "startBlock: " + getStartBlock());
 	    ps.println(prefix + "blockCount: " + getBlockCount());
	}
    }

    // Legacy code
//     public static class APMPartition {
// 	public int pmSig; // {partition signature}
// 	public int pmSigPad; // {reserved}
// 	public long pmMapBlkCnt; // {number of blocks in partition map}
// 	public long pmPyPartStart; // {first physical block of partition}
// 	public long pmPartBlkCnt; // {number of blocks in partition}
// 	public final byte[] pmPartName = new byte[32]; // {partition name}
// 	public final byte[] pmParType = new byte[32]; // {partition type}
// 	public long pmLgDataStart; // {first logical block of data area}
// 	public long pmDataCnt; // {number of blocks in data area}
// 	public long pmPartStatus; // {partition status information}
// 	public long pmLgBootStart; // {first logical block of boot code}
// 	public long pmBootSize; // {size of boot code, in bytes}
// 	public long pmBootAddr; // {boot code load address}
// 	public long pmBootAddr2; // {reserved}
// 	public long pmBootEntry; // {boot code entry point}
// 	public long pmBootEntry2; // {reserved}
// 	public long pmBootCksum; // {boot code checksum}
// 	public final byte[] pmProcessor = new byte[16]; // {processor type}
// 	public final int[] pmPad = new int[188]; // {reserved}
	
// 	public APMPartition(byte[] entryData) throws IOException {
// 	    this(new DataInputStream(new ByteArrayInputStream(entryData)));
// 	}
	
// 	public APMPartition(DataInput di) throws IOException {
// 	    // 2*2 + 4*3 + 32*2 + 10*4 + 16 + 188*2 = 512
// 	    pmSig = di.readShort() & 0xffff;
// 	    pmSigPad = di.readShort() & 0xffff;
// 	    pmMapBlkCnt = di.readInt() & 0xffffffffL;
// 	    pmPyPartStart = di.readInt() & 0xffffffffL;
// 	    pmPartBlkCnt = di.readInt() & 0xffffffffL;
// 	    di.readFully(pmPartName);
// 	    di.readFully(pmParType);
// 	    pmLgDataStart = di.readInt() & 0xffffffffL;
// 	    pmDataCnt = di.readInt() & 0xffffffffL;
// 	    pmPartStatus = di.readInt() & 0xffffffffL;
// 	    pmLgBootStart = di.readInt() & 0xffffffffL;
// 	    pmBootSize = di.readInt() & 0xffffffffL;
// 	    pmBootAddr = di.readInt() & 0xffffffffL;
// 	    pmBootAddr2 = di.readInt() & 0xffffffffL;
// 	    pmBootEntry = di.readInt() & 0xffffffffL;
// 	    pmBootEntry2 = di.readInt() & 0xffffffffL;
// 	    pmBootCksum = di.readInt() & 0xffffffffL;
// 	    di.readFully(pmProcessor);
// 	    for(int i = 0; i < pmPad.length; ++i)
// 		pmPad[i] = di.readShort() & 0xffff;
// 	}
	
// 	public void printPartitionInfo(PrintStream ps) {
// // 	    String result = "";
// // 	    result += "Partition name: \"" + new String(pmPartName) + "\"\n";
// // 	    result += "Partition type: \"" + new String(pmParType) + "\"\n";
// // 	    result += "Processor type: \"" + new String(pmProcessor) + "\"\n";
// // 	    return result;
// 	    try {
// 		ps.println("pmSig: " + pmSig);
// 		ps.println("pmSigPad: " + pmSigPad);
// 		ps.println("pmMapBlkCnt: " + pmMapBlkCnt);
// 		ps.println("pmPyPartStart: " + pmPyPartStart);
// 		ps.println("pmPartBlkCnt: " + pmPartBlkCnt);
// 		ps.println("pmPartName: \"" +  new String(pmPartName, "US-ASCII") + "\"");
// 		ps.println("pmParType: \"" +  new String(pmParType, "US-ASCII") + "\"");
// 		ps.println("pmLgDataStart: " + pmLgDataStart);
// 		ps.println("pmDataCnt: " + pmDataCnt);
// 		ps.println("pmPartStatus: " + pmPartStatus);
// 		ps.println("pmLgBootStart: " + pmLgBootStart);
// 		ps.println("pmBootSize: " + pmBootSize);
// 		ps.println("pmBootAddr: " + pmBootAddr);
// 		ps.println("pmBootAddr2: " + pmBootAddr2);
// 		ps.println("pmBootEntry: " + pmBootEntry);
// 		ps.println("pmBootEntry2: " + pmBootEntry2);
// 		ps.println("pmBootCksum: " + pmBootCksum);
// 		ps.println("pmProcessor: \"" + new String(pmProcessor, "US-ASCII") + "\"");
// 		ps.println("pmPad: " + pmPad);
// 	    } catch(UnsupportedEncodingException uee) {
// 		throw new RuntimeException(uee);
// 	    } // Will never happen. Ever. Period.
// 	}
//     }

//     public static class HFSPlusVolumeHeader {
	
// 	// 2*2+4*17+8+4*8+80*5 = 512 = 1 block
// 	int signature; // UInt16 0x0
// 	int version; // UInt16 0x2
// 	long attributes; // UInt32 0x4
// 	long lastMountedVersion; // UInt32 0x8
// 	long journalInfoBlock; // UInt32 0xC
 
// 	long createDate; // UInt32 0x10
// 	long modifyDate; // UInt32 0x14
// 	long backupDate; // UInt32 0x18
// 	long checkedDate; // UInt32 0x1C
 
// 	long fileCount; // UInt32 0x20
// 	long folderCount; // UInt32 0x24
 
// 	long blockSize; // UInt32 0x28
// 	long totalBlocks; // UInt32 0x2C
// 	long freeBlocks; // UInt32 0x30
 
// 	long nextAllocation; // UInt32 0x34
// 	long rsrcClumpSize; // UInt32 0x38
// 	long dataClumpSize; // UInt32 0x3C
// 	long nextCatalogID; // UInt32 0x40 -> typedef HFSCatalogNodeID
 
// 	long writeCount; // UInt32 0x44
// 	byte[] encodingsBitmap = new byte[8]; // UInt64 0x48
 
// 	long[] finderInfo = new long[8]; // UInt32[8] 0x50
 
// 	HFSPlusForkData allocationFile; // 0x70
// 	HFSPlusForkData extentsFile; // 0xC0
// 	HFSPlusForkData catalogFile; // 0x110
// 	HFSPlusForkData attributesFile; // 0x160
// 	HFSPlusForkData startupFile; // 0x1B0
// 	// 0x200 is the end
	
// 	byte[] data = new byte[512];
	
// 	public void print(PrintStream ps, int pregap) {
// 	    String pregapString = "";
// 	    for(int i = 0; i < pregap; ++i)
// 		pregapString += " ";
// 	    print(ps, pregapString);
// 	}
// 	public void print(PrintStream ps, String prefix) {
	    
// 	    ps.println(prefix + "signature = \"" + Util2.toASCIIString((short)signature) + "\""); // UInt16
// 	    ps.println(prefix + "version = " + version); // UInt16
// 	    ps.println(prefix + "attributes = " + attributes); // UInt32
// 	    printAttributes(ps, prefix + "  ");
// 	    ps.println(prefix + "lastMountedVersion = " + lastMountedVersion); // UInt32
// 	    ps.println(prefix + "journalInfoBlock = " + journalInfoBlock); // UInt32
 
// 	    ps.println(prefix + "createDate = " + createDate); // UInt32
// 	    ps.println(prefix + "modifyDate = " + modifyDate); // UInt32
// 	    ps.println(prefix + "backupDate = " + backupDate); // UInt32
// 	    ps.println(prefix + "checkedDate = " + checkedDate); // UInt32
 
// 	    ps.println(prefix + "fileCount = " + fileCount); // UInt32
// 	    ps.println(prefix + "folderCount = " + folderCount); // UInt32
 
// 	    ps.println(prefix + "blockSize = " + blockSize); // UInt32
// 	    ps.println(prefix + "totalBlocks = " + totalBlocks); // UInt32
// 	    ps.println(prefix + "freeBlocks = " + freeBlocks); // UInt32
 
// 	    ps.println(prefix + "nextAllocation = " + nextAllocation); // UInt32
// 	    ps.println(prefix + "rsrcClumpSize = " + rsrcClumpSize); // UInt32
// 	    ps.println(prefix + "dataClumpSize = " + dataClumpSize); // UInt32
// 	    ps.println(prefix + "nextCatalogID = " + nextCatalogID); // UInt32 -> typedef HFSCatalogNodeID
 
// 	    ps.println(prefix + "writeCount = " + writeCount); // UInt32
	    
// 	    ps.println(prefix + "encodingsBitmap = " + new BigInteger(encodingsBitmap)); // UInt64
// 	    ps.println(prefix + "encodingsBitmap (hex) = 0x" + largeIntToHexString(encodingsBitmap)); // UInt64

// 	    for(int i = 0; i < finderInfo.length; ++i)
// 		ps.println(prefix + "finderInfo[" + i + "] = " + finderInfo[i]);
	    
// 	    ps.println(prefix + "allocationFile: ");
// 	    allocationFile.print(ps, prefix + "  "/*pregap+2*/);
// 	    ps.println(prefix + "extentsFile: ");
// 	    extentsFile.print(ps, prefix + "  "/*pregap+2*/);
// 	    ps.println(prefix + "catalogFile: ");
// 	    catalogFile.print(ps, prefix + "  "/*pregap+2*/);
// 	    ps.println(prefix + "attributesFile: ");
// 	    attributesFile.print(ps, prefix + "  "/*pregap+2*/);
// 	    ps.println(prefix + "startupFile: ");
// 	    startupFile.print(ps, prefix + "  "/*pregap+2*/);
// // 	    ps.println(prefix + " = " + );
// 	}

// 	public void printAttributes(PrintStream ps, int pregap) {
// 	    String pregapString = "";
// 	    for(int i = 0; i < pregap; ++i)
// 		pregapString += " ";
// 	}
// 	public void printAttributes(PrintStream ps, String prefix) {
// 	    /* 32 bits of attributes exist. Bits 0-6, 14 and 16-31 are reserved. */
	    
// 	    ps.println(prefix + "kHFSVolumeHardwareLockBit = " + ((attributes >> 7) & 0x1));
// 	    ps.println(prefix + "kHFSVolumeUnmountedBit = " + ((attributes >> 8) & 0x1));
// 	    ps.println(prefix + "kHFSVolumeSparedBlocksBit = " + ((attributes >> 9) & 0x1));
// 	    ps.println(prefix + "kHFSVolumeNoCacheRequiredBit = " + ((attributes >> 10) & 0x1));
// 	    ps.println(prefix + "kHFSBootVolumeInconsistentBit = " + ((attributes >> 11) & 0x1));
// 	    ps.println(prefix + "kHFSCatalogNodeIDsReusedBit = " + ((attributes >> 12) & 0x1));
// 	    ps.println(prefix + "kHFSVolumeJournaledBit = " + ((attributes >> 13) & 0x1));
// 	    ps.println(prefix + "kHFSVolumeSoftwareLockBit = " + ((attributes >> 15) & 0x1));	    
// 	}
	
// 	public String largeIntToHexString(byte[] uint) {
// 	    String result = "";
// 	    for(byte b : uint) {
// 		for(int i = 0; i < 2; ++i) {
// 		    int value = (b >> (1-i)*4) & 0xF;
// 		    if(value < 10)
// 			result += value;
// 		    else
// 			result += (char)('A'+(value-10));
// 		}
// 	    }
// 	    return result;
// 	}
	
// 	public HFSPlusVolumeHeader(byte[] data) throws IOException {
// 	    if(data.length != 512)
// 		throw new RuntimeException("Data length != 512");
// 	    System.arraycopy(data, 0, this.data, 0, data.length);
// 	    DataInput di = new DataInputStream(new ByteArrayInputStream(data));
// 	    signature = di.readShort() & 0xffff; // UInt16
// 	    version = di.readShort() & 0xffff; // UInt16
// 	    attributes = di.readInt() & 0xffffffffL; // UInt32
// 	    lastMountedVersion = di.readInt() & 0xffffffffL; // UInt32
// 	    journalInfoBlock = di.readInt() & 0xffffffffL; // UInt32
 
// 	    createDate = di.readInt() & 0xffffffffL; // UInt32
// 	    modifyDate = di.readInt() & 0xffffffffL; // UInt32
// 	    backupDate = di.readInt() & 0xffffffffL; // UInt32
// 	    checkedDate = di.readInt() & 0xffffffffL; // UInt32
 
// 	    fileCount = di.readInt() & 0xffffffffL; // UInt32
// 	    folderCount = di.readInt() & 0xffffffffL; // UInt32
 
// 	    blockSize = di.readInt() & 0xffffffffL; // UInt32
// 	    totalBlocks = di.readInt() & 0xffffffffL; // UInt32
// 	    freeBlocks = di.readInt() & 0xffffffffL; // UInt32
 
// 	    nextAllocation = di.readInt() & 0xffffffffL; // UInt32
// 	    rsrcClumpSize = di.readInt() & 0xffffffffL; // UInt32
// 	    dataClumpSize = di.readInt() & 0xffffffffL; // UInt32
// 	    nextCatalogID = di.readInt() & 0xffffffffL; // UInt32 -> typedef HFSCatalogNodeID
 
// 	    writeCount = di.readInt() & 0xffffffffL; // UInt32
// 	    di.readFully(encodingsBitmap); // UInt64
// 	    for(int i = 0; i < finderInfo.length; ++i)
// 		finderInfo[i] = di.readInt() & 0xffffffffL;
	    
// 	    allocationFile = new HFSPlusForkData(di);
// 	    extentsFile = new HFSPlusForkData(di);
// 	    catalogFile = new HFSPlusForkData(di);
// 	    attributesFile = new HFSPlusForkData(di);
// 	    startupFile = new HFSPlusForkData(di);
	    
// 	    // Just a quick check to see if we really read all 512 bytes.
// 	    try {
// 		di.readByte();
// 		throw new RuntimeException("Didn't read 512 bytes!");
// 	    } catch(EOFException ee) {}
// 	}
//     }
//     public static class HFSPlusForkData {
// 	// 8+4+4+64 = 80 bytes
// 	byte[] logicalSize = new byte[8]; // UInt64
// 	long clumpSize; // UInt32
// 	long totalBlocks; // UInt32
// 	HFSPlusExtentRecord extents; // Size: 64 bytes

// // 	public HFSPlusForkData(byte[] data, int offset) throws IOException {
// // 	    try {
// // 		this(new DataInputStream(new ByteArrayInputStream(data, offset, 80)));
// // 	    } catch(Exception e) { throw new RuntimeException(e); }
// // 	}
	
// 	public HFSPlusForkData(DataInput di) throws IOException {
// 	    di.readFully(logicalSize);
// 	    clumpSize = di.readInt() & 0xffffffffL;
// 	    totalBlocks = di.readInt() & 0xffffffffL;
// 	    extents = new HFSPlusExtentRecord(di);
// 	}
	
// 	public void print(PrintStream ps, int pregap) {
// 	    String pregapString = "";
// 	    for(int i = 0; i < pregap; ++i)
// 		pregapString += " ";
// 	    print(ps, pregapString);
// 	}
	
// 	public void print(PrintStream ps, String prefix) {
// 	    ps.println(prefix + "logicalSize = " + new BigInteger(logicalSize));
// 	    ps.println(prefix + "clumpSize = " + clumpSize);
// 	    ps.println(prefix + "totalBlocks = " + totalBlocks);
// 	    ps.println(prefix + "extents:");
// 	    extents.print(ps, prefix + "  ");
// 	}
//     }
//     public static class HFSPlusExtentRecord {
// 	// 8*8 = 64 bytes
// 	HFSPlusExtentDescriptor[] array = new HFSPlusExtentDescriptor[8];

// 	public HFSPlusExtentRecord(DataInput di) throws IOException {
// 	    for(int i = 0; i < array.length; ++i)
// 		array[i] = new HFSPlusExtentDescriptor(di);
// 	}
// 	public void print(PrintStream ps, int pregap) {
// 	    String pregapString = "";
// 	    for(int i = 0; i < pregap; ++i)
// 		pregapString += " ";
// 	    print(ps, pregapString);
// 	}
// 	public void print(PrintStream ps, String prefix) {
	    
// 	    for(int i = 0; i < array.length; ++i) {
// 		ps.println(prefix + "array[" + i + "]:");
// 		array[i].print(ps, prefix + "  ");
// 	    }
// 	}
//     }
//     public static class HFSPlusExtentDescriptor {
// 	// 4+4 = 8 bytes
// 	long startBlock; // UInt32
// 	long blockCount; // UInt32

// 	public HFSPlusExtentDescriptor(DataInput di) throws IOException {
// 	    startBlock = di.readInt() & 0xffffffffL;
// 	    blockCount = di.readInt() & 0xffffffffL;
// 	}
	
// 	public void print(PrintStream ps, int pregap) {
// 	    String pregapString = "";
// 	    for(int i = 0; i < pregap; ++i)
// 		pregapString += " ";
// 	    print(ps, pregapString);
// 	}
// 	public void print(PrintStream ps, String prefix) {
//  	    ps.println(prefix + "startBlock = " + startBlock);
//  	    ps.println(prefix + "blockCount = " + blockCount);
// 	}
//     }

    
}
