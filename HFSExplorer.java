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
    private static BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
    
    public static void main(String[] args) throws IOException {
	println("HFS Explorer 0.1");
	if(args.length == 0) {
	    println("  displays information about an HFS filesystem.");
	    println("  usage: java HFSExplorer [options] <file/device>");
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
		    print("Partition " + i + ": ");
		    APMPartition p = new APMPartition(currentBlock, 0);
		    partitions.add(p);
		    if(options.verbose) {
			println();
			p.printPartitionInfo(System.out);
		    }
		    else
			println("\"" + p.getPmPartNameAsString().trim() + "\" (" + p.getPmParTypeAsString().trim() + ")");
		}
		else break;
	    }
	    print("Which partition do you wish to explore [0-" + (partitions.size()-1) + "]? ");
	    int partNum = Integer.parseInt(stdin.readLine());
	    APMPartition chosenPartition = partitions.get(partNum);
	    String partitionType = chosenPartition.getPmParTypeAsString();
	    if(!partitionType.trim().equals("Apple_HFS")) {
		println("The partition is not an HFS partition!");
		System.exit(0);
	    }
	    println("Parsing partition " + partNum + " (" + chosenPartition.getPmPartNameAsString().trim() + "/" + partitionType.trim() + ")");
	    offset = chosenPartition.getPmPyPartStart()*0x200;
	}
	byte[] currentBlock = new byte[512];
	isoRaf.seek(offset + 1024);
	isoRaf.read(currentBlock);
	HFSPlusVolumeHeader header = new HFSPlusVolumeHeader(currentBlock);
	header.print(System.out, 2);
	long catalogFilePosition = header.getBlockSize()*header.getCatalogFile().getExtents().getExtentDescriptor(0).getStartBlock();
	long catalogFileLength = header.getBlockSize()*header.getCatalogFile().getExtents().getExtentDescriptor(0).getBlockCount();
	System.out.println("Catalog file offset: " + catalogFilePosition);
	System.out.println("Catalog file length: " + catalogFileLength + " bytes");
	System.out.println("Seeking...");
	isoRaf.seek(catalogFilePosition);
	System.out.println("Current file pointer: " + isoRaf.getFilePointer());
	System.out.println("length of file: " + isoRaf.length());
	byte[] nodeDescriptorData = new byte[14];
	if(isoRaf.read(nodeDescriptorData) != nodeDescriptorData.length)
	    System.out.println("ERROR: Did not read nodeDescriptor completely.");
	BTNodeDescriptor btnd = new BTNodeDescriptor(nodeDescriptorData, 0);
	btnd.print(System.out, "");

	byte[] headerRec = new byte[BTHeaderRec.length()];
	if(isoRaf.read(headerRec) != headerRec.length)
	    System.out.println("ERROR: Did not read headerRec completely.");
	BTHeaderRec bthr = new BTHeaderRec(headerRec, 0);
	bthr.print(System.out, "");

	// Now we have the node size, so we could just list the nodes and see what types are there.
	// Btw, does the length of the catalog file containing this b-tree align to the node size?
	if(catalogFileLength % bthr.getNodeSize() != 0) {
	    System.out.println("catalogFileLength is not aligned to node size! (" + catalogFileLength + 
			       " % " + bthr.getNodeSize() + " = " + catalogFileLength % bthr.getNodeSize());
	    return;
	}
	else
	    System.out.println("Number of nodes in the catalog file: " + (catalogFileLength / bthr.getNodeSize()));

	int nodeSize = bthr.getNodeSize();
	System.out.println("Reading node by node...");
	byte[] currentNode = new byte[nodeSize];
	isoRaf.seek(catalogFilePosition);
	int nodeNumber = 0;
	int bytesRead = nodeSize;
	while((isoRaf.getFilePointer()-catalogFilePosition+nodeSize) <= catalogFileLength) {
	    //System.out.println("FP: " + isoRaf.getFilePointer() + " diff: " + (isoRaf.getFilePointer()-catalogFilePosition) + " (catalogFileLength: " + catalogFileLength + ")");
	    ++nodeNumber;
	    System.out.println("Reading node " + nodeNumber + "...");
	    isoRaf.readFully(currentNode);
	    bytesRead += nodeSize;
	    
	    BTNodeDescriptor nodeDescriptor = new BTNodeDescriptor(currentNode, 0);
	    if(false && nodeDescriptor.getKind() == BTNodeDescriptor.BT_LEAF_NODE) {
		String filename = "node" + nodeNumber + ".bin";
		System.out.println("Dumping node to file: \"" + filename + "\"");
		FileOutputStream fos = new FileOutputStream(filename);
		fos.write(currentNode);
		fos.close();
	    }
	    System.out.println("  Kind: " + nodeDescriptor.getKindAsString());
	    System.out.println("  Number of records: " + nodeDescriptor.getNumRecords());
	    short[] offsets = new short[nodeDescriptor.getNumRecords()];
	    for(int i = 0; i < offsets.length; ++i) {
		offsets[i] = Util.readShortBE(currentNode, currentNode.length-((i+1)*2));
	    }
	    
	    for(int i = 0; i < offsets.length; ++i) {
		int currentOffset = Util2.unsign(offsets[i]);

		if(i < offsets.length-1) {
		    System.out.println("  [" + nodeNumber + "] Offset to record " + i + ": " + currentOffset);
		    System.out.println("  [" + nodeNumber + "]  Size of record: " + (Util2.unsign(offsets[i+1])-currentOffset) + " bytes");
		    if(nodeDescriptor.getKind() == BTNodeDescriptor.BT_LEAF_NODE) {
			int keyLength;
			int keyLengthSize;
			if(bthr.isBTBigKeysSet()) {
			    keyLength = Util2.unsign(Util.readShortBE(currentNode, currentOffset));
			    keyLengthSize = 2;
			}
			else {
			    keyLength = Util2.unsign(Util.readByteBE(currentNode, currentOffset));
			    keyLengthSize = 1;
			}
			HFSPlusCatalogKey currentKey = new HFSPlusCatalogKey(currentNode, currentOffset);
			System.out.println("  [" + nodeNumber + "]  Key:");
			currentKey.print(System.out, "  [" + nodeNumber + "]   ");
			
			
			short recordType = Util.readShortBE(currentNode, currentOffset+currentKey.length());
			System.out.print("  [" + nodeNumber + "]  Record type: ");
			if(recordType == 0x0001) System.out.print("kHFSPlusFolderRecord");
			else if(recordType == 0x0002) System.out.print("kHFSPlusFileRecord");
			else if(recordType == 0x0003) System.out.print("kHFSPlusFolderThreadRecord");
			else if(recordType == 0x0004) System.out.print("kHFSPlusFileThreadRecord");
			else System.out.print("UNKNOWN! (" + recordType + ")");
			System.out.println();

			System.out.println("  [" + nodeNumber + "]  Record:");
			if(recordType == 0x0001) {
			    HFSPlusCatalogFolder folderRec = new HFSPlusCatalogFolder(currentNode, currentOffset+currentKey.length());
			    folderRec.print(System.out, "  [" + nodeNumber + "]   ");
			}
			else if(recordType == 0x0002) {
			    HFSPlusCatalogFile fileRec = new HFSPlusCatalogFile(currentNode, currentOffset+currentKey.length());
			    fileRec.print(System.out, "  [" + nodeNumber + "]   ");			    
			}
			else if(recordType == 0x0003) {
			    HFSPlusCatalogThread folderThreadRec = new HFSPlusCatalogThread(currentNode, currentOffset+currentKey.length());
			    folderThreadRec.print(System.out, "  [" + nodeNumber + "]   ");
			}
			else if(recordType == 0x0004) {
			    HFSPlusCatalogThread fileThreadRec = new HFSPlusCatalogThread(currentNode, currentOffset+currentKey.length());
			    fileThreadRec.print(System.out, "  [" + nodeNumber + "]   ");
			}
		    }
		}
		else {
		    System.out.println("  [" + nodeNumber + "] Offset to free space: " + currentOffset);
		    System.out.println("  [" + nodeNumber + "]  Size of free space: " + (nodeSize-currentOffset-2*nodeDescriptor.getNumRecords()) + " bytes");
		    
		}
	    }
	    
	    
	    
	    if(true) {
		System.out.print("Press enter to read next node (q and enter to exit)...");
		if(stdIn.readLine().trim().equalsIgnoreCase("q"))
		    return;
	    }
	}
	System.out.println("FP: " + isoRaf.getFilePointer() + " diff: " + (isoRaf.getFilePointer()-catalogFilePosition) + " (catalogFileLength: " + catalogFileLength + ")");
	System.out.println("bytesRead: " + bytesRead + " nodeSize: " + nodeSize + " number of nodes: " + (catalogFileLength / nodeSize));
	System.out.println("Nodes read: " + nodeNumber);

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
	    else if(currentArg.equals("-v"))
		options.verbose = true;
	    else
		println("\"" + currentArg + "\" is not a valid parameter.");
	}
    }
}
