import java.util.*;
import java.io.*;
import java.math.BigInteger;

public class HFSExplorer {
    public static BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
    
    public static void main(String[] args) throws IOException {
	println("HFS Explorer 0.1");
	if(args.length != 1) {
	    println("  displays information about an HFS filesystem contained within an Apple");
	    println("  Partition Map");
	    println("  usage: java HFSExplorer <isofile>");
	    System.exit(0);
	}
	
	RandomAccessFile isoRaf = new RandomAccessFile(args[0], "r");
	
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
		APMPartition p = new APMPartition(currentBlock);
		partitions.add(p);
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
	
	println("Exploring partition " + partNum + " (" + new String(chosenPartition.pmPartName, "ASCII").trim() + "/" + partitionType.trim() + ")");
	isoRaf.seek(chosenPartition.pmPyPartStart*0x200 + 1024);
	isoRaf.readFully(currentBlock);
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
    
    public static class APMPartition {
	public int pmSig; // {partition signature}
	public int pmSigPad; // {reserved}
	public long pmMapBlkCnt; // {number of blocks in partition map}
	public long pmPyPartStart; // {first physical block of partition}
	public long pmPartBlkCnt; // {number of blocks in partition}
	public final byte[] pmPartName = new byte[32]; // {partition name}
	public final byte[] pmParType = new byte[32]; // {partition type}
	public long pmLgDataStart; // {first logical block of data area}
	public long pmDataCnt; // {number of blocks in data area}
	public long pmPartStatus; // {partition status information}
	public long pmLgBootStart; // {first logical block of boot code}
	public long pmBootSize; // {size of boot code, in bytes}
	public long pmBootAddr; // {boot code load address}
	public long pmBootAddr2; // {reserved}
	public long pmBootEntry; // {boot code entry point}
	public long pmBootEntry2; // {reserved}
	public long pmBootCksum; // {boot code checksum}
	public final byte[] pmProcessor = new byte[16]; // {processor type}
	public final int[] pmPad = new int[188]; // {reserved}
	
	public APMPartition(byte[] entryData) throws IOException {
	    this(new DataInputStream(new ByteArrayInputStream(entryData)));
	}
	
	public APMPartition(DataInput di) throws IOException {
	    // 2*2 + 4*3 + 32*2 + 10*4 + 16 + 188*2 = 512
	    pmSig = di.readShort() & 0xffff;
	    pmSigPad = di.readShort() & 0xffff;
	    pmMapBlkCnt = di.readInt() & 0xffffffffL;
	    pmPyPartStart = di.readInt() & 0xffffffffL;
	    pmPartBlkCnt = di.readInt() & 0xffffffffL;
	    di.readFully(pmPartName);
	    di.readFully(pmParType);
	    pmLgDataStart = di.readInt() & 0xffffffffL;
	    pmDataCnt = di.readInt() & 0xffffffffL;
	    pmPartStatus = di.readInt() & 0xffffffffL;
	    pmLgBootStart = di.readInt() & 0xffffffffL;
	    pmBootSize = di.readInt() & 0xffffffffL;
	    pmBootAddr = di.readInt() & 0xffffffffL;
	    pmBootAddr2 = di.readInt() & 0xffffffffL;
	    pmBootEntry = di.readInt() & 0xffffffffL;
	    pmBootEntry2 = di.readInt() & 0xffffffffL;
	    pmBootCksum = di.readInt() & 0xffffffffL;
	    di.readFully(pmProcessor);
	    for(int i = 0; i < pmPad.length; ++i)
		pmPad[i] = di.readShort() & 0xffff;
	}
	
	public void printPartitionInfo(PrintStream ps) {
// 	    String result = "";
// 	    result += "Partition name: \"" + new String(pmPartName) + "\"\n";
// 	    result += "Partition type: \"" + new String(pmParType) + "\"\n";
// 	    result += "Processor type: \"" + new String(pmProcessor) + "\"\n";
// 	    return result;
	    try {
		ps.println("pmSig: " + pmSig);
		ps.println("pmSigPad: " + pmSigPad);
		ps.println("pmMapBlkCnt: " + pmMapBlkCnt);
		ps.println("pmPyPartStart: " + pmPyPartStart);
		ps.println("pmPartBlkCnt: " + pmPartBlkCnt);
		ps.println("pmPartName: \"" +  new String(pmPartName, "US-ASCII") + "\"");
		ps.println("pmParType: \"" +  new String(pmParType, "US-ASCII") + "\"");
		ps.println("pmLgDataStart: " + pmLgDataStart);
		ps.println("pmDataCnt: " + pmDataCnt);
		ps.println("pmPartStatus: " + pmPartStatus);
		ps.println("pmLgBootStart: " + pmLgBootStart);
		ps.println("pmBootSize: " + pmBootSize);
		ps.println("pmBootAddr: " + pmBootAddr);
		ps.println("pmBootAddr2: " + pmBootAddr2);
		ps.println("pmBootEntry: " + pmBootEntry);
		ps.println("pmBootEntry2: " + pmBootEntry2);
		ps.println("pmBootCksum: " + pmBootCksum);
		ps.println("pmProcessor: \"" + new String(pmProcessor, "US-ASCII") + "\"");
		ps.println("pmPad: " + pmPad);
	    } catch(UnsupportedEncodingException uee) {
		uee.printStackTrace();
	    } // Will never happen. Ever. Period.
	}
    }

    public static class HFSPlusVolumeHeader {
	// 2*2+4*17+8+4*8+80*5 = 512 = 1 block
	int signature; // UInt16 0x0
	int version; // UInt16 0x2
	long attributes; // UInt32 0x4
	long lastMountedVersion; // UInt32 0x8
	long journalInfoBlock; // UInt32 0xC
 
	long createDate; // UInt32 0x10
	long modifyDate; // UInt32 0x14
	long backupDate; // UInt32 0x18
	long checkedDate; // UInt32 0x1C
 
	long fileCount; // UInt32 0x20
	long folderCount; // UInt32 0x24
 
	long blockSize; // UInt32 0x28
	long totalBlocks; // UInt32 0x2C
	long freeBlocks; // UInt32 0x30
 
	long nextAllocation; // UInt32 0x34
	long rsrcClumpSize; // UInt32 0x38
	long dataClumpSize; // UInt32 0x3C
	long nextCatalogID; // UInt32 0x40 -> typedef HFSCatalogNodeID
 
	long writeCount; // UInt32 0x44
	byte[] encodingsBitmap = new byte[8]; // UInt64 0x48
 
	long[] finderInfo = new long[8]; // UInt32[8] 0x50
 
	HFSPlusForkData allocationFile; // 0x70
	HFSPlusForkData extentsFile; // 0xC0
	HFSPlusForkData catalogFile; // 0x110
	HFSPlusForkData attributesFile; // 0x160
	HFSPlusForkData startupFile; // 0x1B0
	// 0x200 is the end

	public void print(PrintStream ps, int pregap) {
	    String pregapString = "";
	    for(int i = 0; i < pregap; ++i)
		pregapString += " ";
	    
	    ps.println(pregapString + "signature = " + signature); // UInt16
	    ps.println(pregapString + "version = " + version); // UInt16
	    ps.println(pregapString + "attributes = " + attributes); // UInt32
	    printAttributes(ps, pregap+2);
	    ps.println(pregapString + "lastMountedVersion = " + lastMountedVersion); // UInt32
	    ps.println(pregapString + "journalInfoBlock = " + journalInfoBlock); // UInt32
 
	    ps.println(pregapString + "createDate = " + createDate); // UInt32
	    ps.println(pregapString + "modifyDate = " + modifyDate); // UInt32
	    ps.println(pregapString + "backupDate = " + backupDate); // UInt32
	    ps.println(pregapString + "checkedDate = " + checkedDate); // UInt32
 
	    ps.println(pregapString + "fileCount = " + fileCount); // UInt32
	    ps.println(pregapString + "folderCount = " + folderCount); // UInt32
 
	    ps.println(pregapString + "blockSize = " + blockSize); // UInt32
	    ps.println(pregapString + "totalBlocks = " + totalBlocks); // UInt32
	    ps.println(pregapString + "freeBlocks = " + freeBlocks); // UInt32
 
	    ps.println(pregapString + "nextAllocation = " + nextAllocation); // UInt32
	    ps.println(pregapString + "rsrcClumpSize = " + rsrcClumpSize); // UInt32
	    ps.println(pregapString + "dataClumpSize = " + dataClumpSize); // UInt32
	    ps.println(pregapString + "nextCatalogID = " + nextCatalogID); // UInt32 -> typedef HFSCatalogNodeID
 
	    ps.println(pregapString + "writeCount = " + writeCount); // UInt32
	    
	    ps.println(pregapString + "encodingsBitmap = " + new BigInteger(encodingsBitmap)); // UInt64
	    ps.println(pregapString + "encodingsBitmap (hex) = 0x" + largeIntToHexString(encodingsBitmap)); // UInt64

	    for(int i = 0; i < finderInfo.length; ++i)
		ps.println(pregapString + "finderInfo[" + i + "] = " + finderInfo[i]);
	    
	    ps.println(pregapString + "allocationFile: ");
	    allocationFile.print(ps, pregap+2);
	    ps.println(pregapString + "extentsFile: ");
	    extentsFile.print(ps, pregap+2);
	    ps.println(pregapString + "catalogFile: ");
	    catalogFile.print(ps, pregap+2);
	    ps.println(pregapString + "attributesFile: ");
	    attributesFile.print(ps, pregap+2);
	    ps.println(pregapString + "startupFile: ");
	    startupFile.print(ps, pregap+2);
// 	    ps.println(pregapString + " = " + );
	}

	public void printAttributes(PrintStream ps, int pregap) {
	    String pregapString = "";
	    for(int i = 0; i < pregap; ++i)
		pregapString += " ";
	    
	    /* 32 bits of attributes exist. Bits 0-6, 14 and 16-31 are reserved. */
	    
	    ps.println(pregapString + "kHFSVolumeHardwareLockBit = " + ((attributes >> 7) & 0x1));
	    ps.println(pregapString + "kHFSVolumeUnmountedBit = " + ((attributes >> 8) & 0x1));
	    ps.println(pregapString + "kHFSVolumeSparedBlocksBit = " + ((attributes >> 9) & 0x1));
	    ps.println(pregapString + "kHFSVolumeNoCacheRequiredBit = " + ((attributes >> 10) & 0x1));
	    ps.println(pregapString + "kHFSBootVolumeInconsistentBit = " + ((attributes >> 11) & 0x1));
	    ps.println(pregapString + "kHFSCatalogNodeIDsReusedBit = " + ((attributes >> 12) & 0x1));
	    ps.println(pregapString + "kHFSVolumeJournaledBit = " + ((attributes >> 13) & 0x1));
	    ps.println(pregapString + "kHFSVolumeSoftwareLockBit = " + ((attributes >> 15) & 0x1));	    
	}
	
	public String largeIntToHexString(byte[] uint) {
	    String result = "";
	    for(byte b : uint) {
		for(int i = 0; i < 2; ++i) {
		    int value = (b >> (1-i)*4) & 0xF;
		    if(value < 10)
			result += value;
		    else
			result += (char)('A'+(value-10));
		}
	    }
	    return result;
	}
	
	public HFSPlusVolumeHeader(byte[] data) throws IOException {
	    if(data.length != 512)
		throw new RuntimeException("Data length != 512");
	    DataInput di = new DataInputStream(new ByteArrayInputStream(data));
	    signature = di.readShort() & 0xffff; // UInt16
	    version = di.readShort() & 0xffff; // UInt16
	    attributes = di.readInt() & 0xffffffffL; // UInt32
	    lastMountedVersion = di.readInt() & 0xffffffffL; // UInt32
	    journalInfoBlock = di.readInt() & 0xffffffffL; // UInt32
 
	    createDate = di.readInt() & 0xffffffffL; // UInt32
	    modifyDate = di.readInt() & 0xffffffffL; // UInt32
	    backupDate = di.readInt() & 0xffffffffL; // UInt32
	    checkedDate = di.readInt() & 0xffffffffL; // UInt32
 
	    fileCount = di.readInt() & 0xffffffffL; // UInt32
	    folderCount = di.readInt() & 0xffffffffL; // UInt32
 
	    blockSize = di.readInt() & 0xffffffffL; // UInt32
	    totalBlocks = di.readInt() & 0xffffffffL; // UInt32
	    freeBlocks = di.readInt() & 0xffffffffL; // UInt32
 
	    nextAllocation = di.readInt() & 0xffffffffL; // UInt32
	    rsrcClumpSize = di.readInt() & 0xffffffffL; // UInt32
	    dataClumpSize = di.readInt() & 0xffffffffL; // UInt32
	    nextCatalogID = di.readInt() & 0xffffffffL; // UInt32 -> typedef HFSCatalogNodeID
 
	    writeCount = di.readInt() & 0xffffffffL; // UInt32
	    di.readFully(encodingsBitmap); // UInt64
	    for(int i = 0; i < finderInfo.length; ++i)
		finderInfo[i] = di.readInt() & 0xffffffffL;
	    
	    allocationFile = new HFSPlusForkData(di);
	    extentsFile = new HFSPlusForkData(di);
	    catalogFile = new HFSPlusForkData(di);
	    attributesFile = new HFSPlusForkData(di);
	    startupFile = new HFSPlusForkData(di);
	    
	    // Just a quick check to see if we really read all 512 bytes.
	    try {
		di.readByte();
		throw new RuntimeException("Didn't read 512 bytes!");
	    } catch(EOFException ee) {}
	}
    }

    public static class HFSPlusForkData {
	// 8+4+4+64 = 80 bytes
	byte[] logicalSize = new byte[8]; // UInt64
	long clumpSize; // UInt32
	long totalBlocks; // UInt32
	HFSPlusExtentRecord extents; // Size: 64 bytes
	
	public HFSPlusForkData(DataInput di) throws IOException {
	    di.readFully(logicalSize);
	    clumpSize = di.readInt() & 0xffffffffL;
	    totalBlocks = di.readInt() & 0xffffffffL;
	    extents = new HFSPlusExtentRecord(di);
	}
	
	public void print(PrintStream ps, int pregap) {
	    String pregapString = "";
	    for(int i = 0; i < pregap; ++i)
		pregapString += " ";
	    
	    ps.println(pregapString + "logicalSize = " + new BigInteger(logicalSize));
	    ps.println(pregapString + "clumpSize = " + clumpSize);
	    ps.println(pregapString + "totalBlocks = " + totalBlocks);
	    ps.println(pregapString + "extents:");
	    extents.print(ps, pregap+2);
	}
    }
    public static class HFSPlusExtentRecord {
	// 8*8 = 64 bytes
	HFSPlusExtentDescriptor[] array = new HFSPlusExtentDescriptor[8];

	public HFSPlusExtentRecord(DataInput di) throws IOException {
	    for(int i = 0; i < array.length; ++i)
		array[i] = new HFSPlusExtentDescriptor(di);
	}
	public void print(PrintStream ps, int pregap) {
	    String pregapString = "";
	    for(int i = 0; i < pregap; ++i)
		pregapString += " ";
	    
	    for(int i = 0; i < array.length; ++i) {
		ps.println(pregapString + "array[" + i + "]:");
		array[i].print(ps, pregap+2);
	    }
	}
    }
    public static class HFSPlusExtentDescriptor {
	// 4+4 = 8 bytes
	long startBlock; // UInt32
	long blockCount; // UInt32

	public HFSPlusExtentDescriptor(DataInput di) throws IOException {
	    startBlock = di.readInt() & 0xffffffffL;
	    blockCount = di.readInt() & 0xffffffffL;
	}
	
	public void print(PrintStream ps, int pregap) {
	    String pregapString = "";
	    for(int i = 0; i < pregap; ++i)
		pregapString += " ";
	    
 	    ps.println(pregapString + "startBlock = " + startBlock);
 	    ps.println(pregapString + "blockCount = " + blockCount);
	}
    }
}
