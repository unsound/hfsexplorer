/*-
 * Copyright (C) 2006-2009 Erik Larsson
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

package org.catacombae.hfsexplorer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import org.catacombae.io.ReadableFileStream;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.hfsexplorer.win32.WindowsLowLevelIO;
import org.catacombae.hfsexplorer.fs.NullProgressMonitor;
import org.catacombae.hfsexplorer.partitioning.APMPartition;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogFile;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogFileRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogFileThread;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogFileThreadRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogFolder;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogFolderRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogFolderThread;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogFolderThreadRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogLeafRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogNodeID.ReservedID;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSExtentDescriptor;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSForkData;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSVolumeHeader;
import org.catacombae.io.ReadableConcatenatedStream;
import org.catacombae.jparted.lib.DataLocator;
import org.catacombae.jparted.lib.ReadableStreamDataLocator;
import org.catacombae.jparted.lib.fs.FileSystemDetector;
import org.catacombae.jparted.lib.fs.FileSystemHandler;
import org.catacombae.jparted.lib.fs.FileSystemHandlerFactory;
import org.catacombae.jparted.lib.fs.FileSystemMajorType;
import org.catacombae.jparted.lib.fs.hfscommon.HFSCommonFileSystemHandler;
import org.catacombae.storage.fs.hfs.HFSVolume;

@SuppressWarnings("deprecation") // TODO: Fix HFSExplorer so it doesn't use deprecated methods...
public class HFSExplorer {
    public static final String VERSION = "0.22pre";
    public static final String COPYRIGHT = "Copyright \u00A9 Erik Larsson 2006-2009";
    public static final String[] NOTICES = {
        "This program is distributed under the GNU General Public License version 3.",
        "See <http://www.gnu.org/copyleft/gpl.html> for the details.",
        "",
        "Libraries used:",
        "    swing-layout <https://swing-layout.dev.java.net/>",
        "        Copyright \u00A9 2005-2006 Sun Microsystems, Inc. Licensed under",
        "        the Lesser General Public License.",
        "        See <http://www.gnu.org/licenses/lgpl.html> for the details.",
        "    iHarder Base64 encoder/decoder <http://iharder.sourceforge.net>",
        "        Public domain software.",
        "    Apache Ant bzip2 library <http://ant.apache.org/>",
        "        Copyright \u00A9 the Apache Software Foundation (ASF). Licensed",
        "        under the Apache License, Version 2.0.",
        "        See <http://www.apache.org/licenses/LICENSE-2.0> for the details.",
    };
    
    public static BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
    private static class Options {
	public boolean readAPM = false;
	public boolean verbose = false;
    }
    private static enum Operation {
	BROWSE,
	FRAGCHECK,
	TEST,
	SYSTEMFILEINFO;
	
	private final LinkedList<String> argsList = new LinkedList<String>();
	public void addArg(String argument) {
	    argsList.add(argument);
	}
	
	public String[] getArgs() {
	    return argsList.toArray(new String[argsList.size()]);
	}
	
	public String getFilename() { return argsList.getLast(); }
    }
    
    private static Options options = new Options();
    private static Operation operation;
    private static BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
    
    public static void main(String[] args) throws IOException {
	if(args.length == 0) {
	    printUsageInfo();
	    System.exit(0);
	}

	if(!parseOptions(args, 0, args.length)) {
            System.exit(1);
            return;
        }

	//RandomAccessFile isoRaf = new RandomAccessFile(args[0], "r");
	ReadableRandomAccessStream isoRaf;
	if(WindowsLowLevelIO.isSystemSupported())
	    isoRaf = new WindowsLowLevelIO(operation.getFilename());
	else
	    isoRaf = new ReadableFileStream(operation.getFilename());

	long offset; // Offset in isoRaf where the file system starts
	long length; // Length of the file system data
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
		    APMPartition p = new APMPartition(currentBlock, 0, 0x200);
		    partitions.add(p);
		    if(options.verbose) {
			println();
			p.printPartitionInfo(System.out);
		    }
		    else
			println("\"" + p.getPmPartNameAsString() + "\" (" + p.getPmParTypeAsString() + ")");
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
	    offset = (chosenPartition.getPmPyPartStart()+chosenPartition.getPmLgDataStart())*0x200;
	    length = chosenPartition.getPmDataCnt()*0x200;
	}
	else {
	    offset = 0;
	    length = isoRaf.length();
	}

        switch(operation) {
            case BROWSE:
                operationBrowse(operation, isoRaf, offset, length);
                break;
            case FRAGCHECK:
                operationFragCheck(operation, isoRaf, offset, length);
                break;
//            case TEST:
//                operationTest(operation, isoRaf, offset, length);
//                break;
            case SYSTEMFILEINFO:
                operationSystemFileInfo(operation, isoRaf, offset, length);
                break;
            default:
                throw new RuntimeException("Unknown operation: " + operation);
        }
    }

//    private static void operationTest(Operation operation, ReadableRandomAccessStream isoRaf, long offset, long length) throws IOException {
//	    System.out.println("Reading partition data starting at " + offset + "...");
//	    byte[] currentBlock = new byte[512];
//	    isoRaf.seek(offset + 1024);
//	    isoRaf.read(currentBlock);
//	    HFSPlusVolumeHeader header = new HFSPlusVolumeHeader(currentBlock);
//	    header.print(System.out, "  ");
//	    long catalogFilePosition = header.getBlockSize()*header.getCatalogFile().getExtents().getExtentDescriptor(0).getStartBlock();
//	    long catalogFileLength = header.getBlockSize()*header.getCatalogFile().getExtents().getExtentDescriptor(0).getBlockCount();
//	    System.out.println("Catalog file offset: " + catalogFilePosition);
//	    System.out.println("Catalog file length: " + catalogFileLength + " bytes");
//	    System.out.println("Seeking...");
//	    isoRaf.seek(offset + catalogFilePosition);
//	    System.out.println("Current file pointer: " + isoRaf.getFilePointer());
//	    System.out.println("length of file: " + isoRaf.length());
//	    byte[] nodeDescriptorData = new byte[14];
//	    if(isoRaf.read(nodeDescriptorData) != nodeDescriptorData.length)
//		System.out.println("ERROR: Did not read nodeDescriptor completely.");
//	    BTNodeDescriptor btnd = new BTNodeDescriptor(nodeDescriptorData, 0);
//	    btnd.print(System.out, "");
//
//	    byte[] headerRec = new byte[BTHeaderRec.length()];
//	    if(isoRaf.read(headerRec) != headerRec.length)
//		System.out.println("ERROR: Did not read headerRec completely.");
//	    BTHeaderRec bthr = new BTHeaderRec(headerRec, 0);
//	    bthr.print(System.out, "");
//
//	    // Now we have the node size, so we could just list the nodes and see what types are there.
//	    // Btw, does the length of the catalog file containing this b-tree align to the node size?
//	    if(catalogFileLength % bthr.getNodeSize() != 0) {
//		System.out.println("catalogFileLength is not aligned to node size! (" + catalogFileLength +
//				   " % " + bthr.getNodeSize() + " = " + catalogFileLength % bthr.getNodeSize());
//		return;
//	    }
//	    else
//		System.out.println("Number of nodes in the catalog file: " + (catalogFileLength / bthr.getNodeSize()));
//
//	    int nodeSize = bthr.getNodeSize();
//	    byte[] currentNode = new byte[nodeSize];
//
//
//
//	    // collect all records belonging to directory 1 (= ls)
//	    System.out.println();
//	    System.out.println();
//	    ForkFilter catalogFile = new ForkFilter(header.getCatalogFile(),
//						    header.getCatalogFile().getExtents().getExtentDescriptors(),
//						    isoRaf, offset, header.getBlockSize(), 0);
//	    HFSPlusCatalogLeafRecord[] f = HFSPlusFileSystemView.collectFilesInDir(new HFSCatalogNodeID(1), bthr.getRootNode(), isoRaf,
//									       offset, header, bthr, catalogFile);
//	    System.out.println("Found " + f.length + " items in subroot.");
//	    for(HFSPlusCatalogLeafRecord rec : f) {
//		//rec.print(System.out, "  ");
//		System.out.print("  \"" + rec.getKey().getNodeName().toString() + "\"");
//		HFSPlusCatalogLeafRecordData data = rec.getData();
//		if(data.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER &&
//		   data instanceof HFSPlusCatalogFolder) {
//		    HFSPlusCatalogFolder folderData = (HFSPlusCatalogFolder)data;
//		    System.out.println(" (dir, id: " + folderData.getFolderID().toInt() + ")");
//		    // Print contents of folder
//		    HFSPlusCatalogLeafRecord[] f2 = HFSPlusFileSystemView.collectFilesInDir(folderData.getFolderID(), bthr.getRootNode(), isoRaf, offset, header, bthr, catalogFile);
//		    System.out.println("  Found " + f2.length + " items in " + rec.getKey().getNodeName().toString() + ".");
//		    for(HFSPlusCatalogLeafRecord rec2 : f2)
//			System.out.println("    \"" + rec2.getKey().getNodeName() + "\"");
//		    //System.out.println();
//		}
//		else if(data.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FILE &&
//			data instanceof HFSPlusCatalogFile) {
//		    HFSPlusCatalogFile fileData = (HFSPlusCatalogFile)data;
//		    System.out.println(" (file, id: " + fileData.getFileID().toInt() + ")");
//		}
//		else if(data.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER_THREAD &&
//			data instanceof HFSPlusCatalogThread) {
//		    System.out.println(" (folder thread)");
//		}
//		else if(data.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FILE_THREAD &&
//			data instanceof HFSPlusCatalogThread) {
//		    System.out.println(" (file thread)");
//		}
//		else {
//		    System.out.println(" (ENCOUNTERED UNKNOWN DATA. record type: " + data.getRecordType() + " rec: " + rec + ")");
//		}
//	    }
//	    System.out.println();
//	    System.out.println();
//
//
//
//	    System.out.println("Reading node by node...");
//	    isoRaf.seek(offset + catalogFilePosition);
//	    int nodeNumber = 0;
//	    int bytesRead = nodeSize;
//	    while((isoRaf.getFilePointer()-catalogFilePosition+nodeSize) <= catalogFileLength) {
//		//System.out.println("FP: " + isoRaf.getFilePointer() + " diff: " + (isoRaf.getFilePointer()-catalogFilePosition) + " (catalogFileLength: " + catalogFileLength + ")");
//		System.out.println("Reading node " + nodeNumber + "...");
//		isoRaf.readFully(currentNode);
//		bytesRead += nodeSize;
//
//		BTNodeDescriptor nodeDescriptor = new BTNodeDescriptor(currentNode, 0);
//		if(false && nodeDescriptor.getKind() == BTNodeDescriptor.BT_LEAF_NODE) {
//		    String filename = "node" + nodeNumber + ".bin";
//		    System.out.println("Dumping node to file: \"" + filename + "\"");
//		    FileOutputStream fos = new FileOutputStream(filename);
//		    fos.write(currentNode);
//		    fos.close();
//		}
//		System.out.println("  Kind: " + nodeDescriptor.getKindAsString());
//		System.out.println("  Number of records: " + nodeDescriptor.getNumRecords());
//		short[] offsets = new short[nodeDescriptor.getNumRecords()];
//		for(int i = 0; i < offsets.length; ++i) {
//		    offsets[i] = Util.readShortBE(currentNode, currentNode.length-((i+1)*2));
//		}
//
//		for(int i = 0; i < offsets.length; ++i) {
//		    int currentOffset = Util.unsign(offsets[i]);
//
//		    if(i < offsets.length-1) {
//		    //System.out.println("  [" + nodeNumber + "] Offset to record " + i + ": " + currentOffset);
//		    //System.out.println("  [" + nodeNumber + "]  Size of record: " + (Util.unsign(offsets[i+1])-currentOffset) + " bytes");
//		    /*
//		    int keyLength;
//		    int keyLengthSize;
//		    if(bthr.isBTBigKeysSet()) {
//			keyLength = Util.unsign(Util.readShortBE(currentNode, currentOffset));
//			keyLengthSize = 2;
//		    }
//		    else {
//			keyLength = Util.unsign(Util.readByteBE(currentNode, currentOffset));
//			keyLengthSize = 1;
//		    }
//		    */
//			if(nodeDescriptor.getKind() != BTNodeDescriptor.BT_HEADER_NODE) {
//			    HFSPlusCatalogKey currentKey = new HFSPlusCatalogKey(currentNode, currentOffset);
//
//
//			    if(nodeDescriptor.getKind() == BTNodeDescriptor.BT_LEAF_NODE) {
//				System.out.println("  [" + nodeNumber + "]  Key: " + currentKey.getKeyLength() +
//						   ", " + currentKey.getParentID().toString() +
//						   ", \"" + currentKey.getNodeName().toString() + "\"");
//				//currentKey.print(System.out, "  [" + nodeNumber + "]   ");
//
//				short recordType = Util.readShortBE(currentNode, currentOffset+currentKey.length());
//				System.out.print("  [" + nodeNumber + "]   Record type: ");
//				if(recordType == 0x0001) System.out.print("kHFSPlusFolderRecord");
//				else if(recordType == 0x0002) System.out.print("kHFSPlusFileRecord");
//				else if(recordType == 0x0003) System.out.print("kHFSPlusFolderThreadRecord");
//				else if(recordType == 0x0004) System.out.print("kHFSPlusFileThreadRecord");
//				else System.out.print("UNKNOWN! (" + recordType + ")");
//				System.out.println();
//
//				//System.out.println("  [" + nodeNumber + "]  Record:");
//				if(recordType == 0x0001) {
//				    HFSPlusCatalogFolder folderRec = new HFSPlusCatalogFolder(currentNode, currentOffset+currentKey.length());
//				    System.out.println("  [" + nodeNumber + "]   Node ID: " + folderRec.getFolderID());
//				    System.out.println("  [" + nodeNumber + "]   Valence: " + folderRec.getValence());
//				    //folderRec.print(System.out, "  [" + nodeNumber + "]   ");
//				}
//				else if(recordType == 0x0002) {
//				    HFSPlusCatalogFile fileRec = new HFSPlusCatalogFile(currentNode, currentOffset+currentKey.length());
//				    System.out.println("  [" + nodeNumber + "]   Node ID: " + fileRec.getFileID());
//				    //fileRec.print(System.out, "  [" + nodeNumber + "]   ");
//				}
//				else if(recordType == 0x0003) {
//				    HFSPlusCatalogThread folderThreadRec = new HFSPlusCatalogThread(currentNode, currentOffset+currentKey.length());
//				    //folderThreadRec.print(System.out, "  [" + nodeNumber + "]   ");
//				}
//				else if(recordType == 0x0004) {
//				    HFSPlusCatalogThread fileThreadRec = new HFSPlusCatalogThread(currentNode, currentOffset+currentKey.length());
//				    //fileThreadRec.print(System.out, "  [" + nodeNumber + "]   ");
//				}
//			    }
//			    else if(nodeDescriptor.getKind() == BTNodeDescriptor.BT_INDEX_NODE) {
//// 			    System.out.println("  [" + nodeNumber + "]  Remaining data for index: " +
//// 					       ((Util.unsign(offsets[i+1])-currentOffset)-
//// 						Util.unsign(currentKey.length())) + " bytes");
//				System.out.println("  [" + nodeNumber + "]    \"" + currentKey.getNodeName().toString() + "\" (parent: " + currentKey.getParentID() + ") -> " + Util.unsign(Util.readIntBE(currentNode, currentOffset+currentKey.length())));
//			    }
//			}
//		    }
//		    else {
//// 		    System.out.println("  [" + nodeNumber + "] Offset to free space: " + currentOffset);
//// 		    System.out.println("  [" + nodeNumber + "]  Size of free space: " + (nodeSize-currentOffset-2*nodeDescriptor.getNumRecords()) + " bytes");
//
//		    }
//		}
//
//		if(true) {
//		    System.out.print("Press enter to read next node (q and enter to exit)...");
//		    if(stdIn.readLine().trim().equalsIgnoreCase("q"))
//			return;
//		}
//		++nodeNumber;
//	    }
//	    System.out.println("FP: " + isoRaf.getFilePointer() + " diff: " + (isoRaf.getFilePointer()-catalogFilePosition) + " (catalogFileLength: " + catalogFileLength + ")");
//	    System.out.println("bytesRead: " + bytesRead + " nodeSize: " + nodeSize + " number of nodes: " + (catalogFileLength / nodeSize));
//	    System.out.println("Nodes read: " + nodeNumber);
//
//    }
    
    private static void operationBrowse(Operation op, ReadableRandomAccessStream hfsFile, long fsOffset, long fsLength) {
	//HFSPlusFileSystemView fsView = new HFSPlusFileSystemView(hfsFile, fsOffset);
        DataLocator inputDataLocator = new ReadableStreamDataLocator(
                new ReadableConcatenatedStream(hfsFile, fsOffset, fsLength));

        FileSystemMajorType[] fsTypes =
                FileSystemDetector.detectFileSystem(inputDataLocator);

        FileSystemMajorType hfsType = null;
        outer:
        for(FileSystemMajorType type : fsTypes) {
            switch(type) {
                case APPLE_HFS:
                case APPLE_HFS_PLUS:
                case APPLE_HFSX:
                    if(hfsType != null)
                        throw new RuntimeException("Conflicting file system " +
                                "types: Detected both " + hfsType + " and " +
                                type + ".");
                    hfsType = type;
                    break;
                default:
                    break;
            }
        }

        if(hfsType == null) {
            System.err.println("No HFS file system found.");
            System.exit(1);
        }
        else
            System.out.println("Detected a " + hfsType + " file system.");

        FileSystemHandlerFactory fact = hfsType.createDefaultHandlerFactory();
        FileSystemHandler fsHandler = fact.createHandler(inputDataLocator);

        HFSCommonFileSystemHandler hfsHandler;
        if(fsHandler instanceof HFSCommonFileSystemHandler)
            hfsHandler = (HFSCommonFileSystemHandler) fsHandler;
        else
            throw new RuntimeException("Unexpected HFS fsHandler type: " +
                    fsHandler.getClass());

	HFSVolume fsView = hfsHandler.getFSView();

	CommonHFSCatalogLeafRecord rootRecord = fsView.getCatalogFile().getRootFolder();
	CommonHFSCatalogLeafRecord currentDir = rootRecord;
	//HFSCatalogNodeID = new HFSCatalogNodeID(1); //rootRecord.getFolderID();
	LinkedList<String> pathStack = new LinkedList<String>();
	LinkedList<CommonHFSCatalogLeafRecord> pathThread =
                new LinkedList<CommonHFSCatalogLeafRecord>();
	pathStack.addLast("");

	while(true) {
	    CommonHFSCatalogFolderThread currentThread = null;
	    StringBuilder currentPath = new StringBuilder();
	    for(String pathComponent : pathStack) {
		currentPath.append(pathComponent);
		currentPath.append("/");
	    }
	    println("Listing files in \"" + currentPath.toString() + "\":");

	    boolean atLeastOneNonThreadEntryFound = false;
	    CommonHFSCatalogLeafRecord[] recordsInDir =
                    fsView.getCatalogFile().listRecords(currentDir);

	    for(CommonHFSCatalogLeafRecord rec : recordsInDir) {

		if(rec instanceof CommonHFSCatalogFileRecord) {
		    CommonHFSCatalogFileRecord catFileRec =
                            (CommonHFSCatalogFileRecord) rec;
                    CommonHFSCatalogFile catFile = catFileRec.getData();

		    println("  [" + catFile.getFileID() + "] \"" +
                            rec.getKey().getNodeName() + "\" (" +
                            catFile.getDataFork() .getLogicalSize() + " B)");

		    if(!atLeastOneNonThreadEntryFound)
			atLeastOneNonThreadEntryFound = true;
		}
		else if(rec instanceof CommonHFSCatalogFolderRecord) {
		    CommonHFSCatalogFolderRecord catFolderRec =
                            (CommonHFSCatalogFolderRecord) rec;
                    CommonHFSCatalogFolder catFolder = catFolderRec.getData();

		    println("  [" + catFolder.getFolderID() + "] \"" +
                            catFolderRec.getKey().getNodeName() + "/\"");

		    if(!atLeastOneNonThreadEntryFound)
			atLeastOneNonThreadEntryFound = true;
		}
		else if(rec instanceof CommonHFSCatalogFolderThreadRecord) {
		    CommonHFSCatalogFolderThreadRecord catThreadRec =
                            (CommonHFSCatalogFolderThreadRecord) rec;
		    CommonHFSCatalogFolderThread catThread =
                            catThreadRec.getData();

		    println("  [Folder Thread: [" + catThread.getParentID() +
                            "] \"" + catThread.getNodeName() + "\"]");

		    if(currentThread == null)
			currentThread = catThreadRec.getData();
		    else
			println("WARNING: Found more than one folder thread " +
                                "in " + currentPath + "!");
		    //println("  [" + catFolder.getFolderID() + "] <Folder Thread: [" + catThread.getParentID() + "] \"" + catThread.getNodeName() + "\"");
		}
		else if(rec instanceof CommonHFSCatalogFileThreadRecord) {
		    CommonHFSCatalogFileThreadRecord catThreadRec =
                            (CommonHFSCatalogFileThreadRecord) rec;
                    CommonHFSCatalogFileThread catThread =
                            catThreadRec.getData();

		    println("  [File Thread: [" + catThread.getParentID() +
                            "] \"" + catThread.getNodeName() + "\"]");
		    // This thread probably does not exist in directories...?
		}
	    }

	    if(currentThread == null && atLeastOneNonThreadEntryFound)
		println("WARNING: Found no folder thread in " + currentPath + "! Won't be able to go back from children in hierarchy.");

	    //long nextID = -1;
	    while(true) {
		print("Command[?]: ");
		
		String input = null;
		try {
		    input = stdIn.readLine().trim();
		} catch(IOException ioe) {
		    ioe.printStackTrace();
		    return;
		}
		if(input.equalsIgnoreCase("?")) {
		    println("Available commands:");
		    println(" ls                List contents of current directory");
		    println(" cd <dirName>      Changes directory by name");
		    println(" cdn <dirID>       Changes directory by ID");
		    println(" info <fileID>     Gets extensive information about the file.");
		    println(" extract <fileID>  Extracts <fileID> to current directory");
		    println(" q                 Quits program");
		}
		else if(input.equals("q"))
		    return;
		else if(input.equals("ls"))
		    break;
		else if(input.startsWith("extract ")) {
		    input = input.substring("extract ".length()).trim();
		    try {
			long nextID = Long.parseLong(input);

			CommonHFSCatalogLeafRecord selectedFileRecord = null;
			CommonHFSCatalogFile selectedFile = null;
			for(CommonHFSCatalogLeafRecord rec : recordsInDir) {
			    if(rec instanceof CommonHFSCatalogFileRecord) {
				CommonHFSCatalogFileRecord catFileRec =
                                        (CommonHFSCatalogFileRecord)rec;
				CommonHFSCatalogFile catFile =
                                        catFileRec.getData();

				if(catFile.getFileID().toLong() == nextID) {
				    selectedFileRecord = rec;
				    selectedFile = catFile;
				    break;
				}
			    }
			}
			if(selectedFileRecord == null) {
			    println("ID not present in dir.");
			    //nextID = -1;
			}
			else {
			    String dataForkFilename = selectedFileRecord.getKey().getNodeName().toString();
			    FileOutputStream dataOut = new FileOutputStream(dataForkFilename);
			    print("Extracting data fork to file \"" + dataForkFilename + "\"...");
			    try {
				long bytesExtracted =
                                        fsView.extractDataForkToStream(selectedFileRecord, dataOut,
                                        NullProgressMonitor.getInstance());
				println("extracted " + bytesExtracted + " bytes.");
				dataOut.close();
			    } catch(IOException ioe) {
				ioe.printStackTrace();
				try { dataOut.close(); } catch(IOException ioe2) {}
				continue; // or rather... don't continue (: mwahaha
			    }
			    
			    String resourceForkFilename = dataForkFilename + ".resourcefork";
			    FileOutputStream resourceOut = new FileOutputStream(resourceForkFilename);
			    print("Extracting resource fork to file \"" + resourceForkFilename + "\"...");
			    try {
				long bytesExtracted =
                                        fsView.extractResourceForkToStream(selectedFileRecord,
                                        resourceOut, NullProgressMonitor.getInstance());
				println("extracted " + bytesExtracted + " bytes.");
				resourceOut.close();
			    } catch(IOException ioe) {
				ioe.printStackTrace();
				try { dataOut.close(); } catch(IOException ioe2) {}
			    }
			    //break; // to reread the directory
			}
			
		    } catch(FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		    } catch(NumberFormatException nfe) {
			//nextID = -1;
			println("Invalid input!");
		    }
		}
		else if(input.startsWith("info ")) {
		    input = input.substring("info ".length()).trim();
		    try {
			long nextID = Long.parseLong(input);
			
			CommonHFSCatalogLeafRecord selectedFileRecord = null;
			for(CommonHFSCatalogLeafRecord rec : recordsInDir) {
			    
			    if(rec instanceof CommonHFSCatalogFileRecord) {
				CommonHFSCatalogFileRecord catFileRec =
                                        (CommonHFSCatalogFileRecord) rec;
                                CommonHFSCatalogFile catFile =
                                        catFileRec.getData();

				if(catFile.getFileID().toLong() == nextID) {
				    selectedFileRecord = rec;
				    rec.print(System.out, "");
				    break;
				}
			    }
			}
			if(selectedFileRecord == null) {
			    println("ID not present in dir.");
			    //nextID = -1;
			}
		    } catch(NumberFormatException nfe) {
			//nextID = -1;
			println("Invalid input!");
		    }
		}
		else if(input.startsWith("cdn ")) {
		    input = input.substring("cdn ".length()).trim();
		    if(input.equals("..")) {
			println("Not yet implemented.");
			// TODO: Implement this.
		    }
		    else {
			try {
			    long nextID = Long.parseLong(input);
			    CommonHFSCatalogLeafRecord nextDir = null;
			    for(CommonHFSCatalogLeafRecord rec : recordsInDir) {

				if(rec instanceof CommonHFSCatalogFolderRecord) {
				    //System.out.println(rec.getKey().getNodeName() + ".equals(" + input +")");
				    CommonHFSCatalogFolderRecord catFolderRec =
                                            (CommonHFSCatalogFolderRecord) rec;
				    CommonHFSCatalogFolder catFolder =
                                            catFolderRec.getData();

				    if(catFolder.getFolderID().toLong() == nextID) {
					nextDir = rec;
					break;
				    }
				}
			    }
			    if(nextDir == null) {
				println("ID not present in dir.");
				//nextID = -1;
			    }
			    else {
				pathStack.addLast(nextDir.getKey().getNodeName().toString());
				pathThread.addLast(currentDir);
				currentDir = nextDir;//nextFolder.getFolderID();
				break;
			    }
			} catch(Exception e) {
			    //nextID = -1;
			    println("Invalid input!");
			}
		    }
		}
		else if(input.startsWith("cd ")) {
		    input = input.substring("cd ".length());
		    if(input.equals("..")) {
// 			HFSPlusCatalogLeafRecord nextDir = null;
// 			for(HFSPlusCatalogLeafRecord rec : recordsInDir) {
// 			    HFSPlusCatalogLeafRecordData recData = rec.getData();
// 			    if((recData.getRecordType() == 
// 				HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER_THREAD) &&
// 			       recData instanceof HFSPlusCatalogThread) {
// 				HFSPlusCatalogThread catThread = (HFSPlusCatalogThread)recData;
// 				nextDir = fsView.getRecord(catThread.getParentID(), catThread.getNodeName());
// 				if(nextDir == null)
// 				    System.err.println("OCULD NOTT FIAAND DIDIIRR!!!");
// 			    }
// 			}
// 			if(nextDir == null) {
// 			    println("ID not present in dir.");
// 			    //nextID = -1;
// 			}
// 			else {
			    pathStack.removeLast();
			    
			    currentDir = pathThread.removeLast();//nextDir;//nextFolder.getFolderID();
			    break;
// 			}
		    }
		    else {
			try {
			    CommonHFSCatalogLeafRecord nextDir = null;
			    for(CommonHFSCatalogLeafRecord rec : recordsInDir) {

				if(rec instanceof CommonHFSCatalogFolderRecord) {
                                    CommonHFSCatalogFolderRecord folderRec =
                                            (CommonHFSCatalogFolderRecord) rec;
				    //System.out.println(rec.getKey().getNodeName() + ".equals(" + input +")");
				    if(rec.getKey().getNodeName().toString().equals(input)) {
					nextDir = rec;
					break;
				    }
				}
			    }
			    if(nextDir == null) {
				println("Unknown directory.");
				//nextID = -1;
			    }
			    else {
				pathStack.addLast(nextDir.getKey().getNodeName().toString());
				pathThread.addLast(currentDir);
				currentDir = nextDir;//nextFolder.getFolderID();
				break;
			    }
			} catch(Exception e) {
			    //nextID = -1;
			    println("Invalid input!");
			}
		    }
		}
		else
		    println("Unknown command.");
	    }
	    println();
	}
    }

    private static void operationFragCheck(Operation op, ReadableRandomAccessStream hfsFile, long fsOffset, long fsLength) {
	println("Gathering information about the files on the volume...");
	final int numberOfFilesToDisplay = 10;
	ArrayList<Pair<CommonHFSCatalogLeafRecord, Integer>> mostFragmentedList =
                new ArrayList<Pair<CommonHFSCatalogLeafRecord, Integer>>(numberOfFilesToDisplay+1);
	
	/*
	 * This is the deal:
	 *   - Go to catalog file
	 *   - Find root dir
	 *   - Depth first search starting at root dir
	 *     - When a file is found that has more fragments than mostFragmentedList.getLast(),
	 *       let the file bubble upwards in the list until it is at the right position.
	 *     - If list.size() > numberOfFilesToDisplay: do removeLast() until they match.
	 */
	
        DataLocator inputDataLocator = new ReadableStreamDataLocator(
                new ReadableConcatenatedStream(hfsFile, fsOffset, fsLength));

        FileSystemMajorType[] fsTypes =
                FileSystemDetector.detectFileSystem(inputDataLocator);

        FileSystemMajorType hfsType = null;
        outer:
        for(FileSystemMajorType type : fsTypes) {
            switch(type) {
                case APPLE_HFS:
                case APPLE_HFS_PLUS:
                case APPLE_HFSX:
                    if(hfsType != null)
                        throw new RuntimeException("Conflicting file system " +
                                "types: Detected both " + hfsType + " and " +
                                type + ".");
                    hfsType = type;
                    break;
                default:
                    break;
            }
        }

        if(hfsType == null) {
            System.err.println("No HFS file system found.");
            System.exit(1);
        }
        else
            System.out.println("Detected a " + hfsType + " file system.");

        FileSystemHandlerFactory fact = hfsType.createDefaultHandlerFactory();
        FileSystemHandler fsHandler = fact.createHandler(inputDataLocator);

        HFSCommonFileSystemHandler hfsHandler;
        if(fsHandler instanceof HFSCommonFileSystemHandler)
            hfsHandler = (HFSCommonFileSystemHandler) fsHandler;
        else
            throw new RuntimeException("Unexpected HFS fsHandler type: " +
                    fsHandler.getClass());

	HFSVolume fsView = hfsHandler.getFSView();
	CommonHFSCatalogFolderRecord rootRecord = fsView.getCatalogFile().getRootFolder();
	CommonHFSCatalogFolderRecord currentDir = rootRecord;
	recursiveFragmentSearch(fsView, rootRecord, mostFragmentedList, numberOfFilesToDisplay, options.verbose);
	if(!options.verbose) println();
	
	println("Most fragmented files: ");
	for(Pair<CommonHFSCatalogLeafRecord, Integer> phi : mostFragmentedList) {
	    println(phi.b + " - \"" + phi.a.getKey().getNodeName() + "\"");
	}
    }
    
    private static void recursiveFragmentSearch(HFSVolume fsView, CommonHFSCatalogLeafRecord currentDir,
						ArrayList<Pair<CommonHFSCatalogLeafRecord, Integer>> mostFragmentedList,
						final int listMaxLength, final boolean verbose) {
	for(CommonHFSCatalogLeafRecord rec : fsView.getCatalogFile().listRecords(currentDir)) {
	    if(rec instanceof CommonHFSCatalogFileRecord) {
		CommonHFSCatalogFile catFile =
                        ((CommonHFSCatalogFileRecord)rec).getData();

		CommonHFSExtentDescriptor[] descs =
                        fsView.getExtentsOverflowFile().
                        getAllDataExtentDescriptors(rec);

		mostFragmentedList.add(new Pair<CommonHFSCatalogLeafRecord, Integer>(rec, descs.length));
		
		// Let the new item bubble up to its position in the list
		for(int i = mostFragmentedList.size()-1; i > 0; --i) {
		    Pair<CommonHFSCatalogLeafRecord, Integer> lower = mostFragmentedList.get(i);
		    Pair<CommonHFSCatalogLeafRecord, Integer> higher = mostFragmentedList.get(i-1);
		    
		    if(lower.b.intValue() > higher.b.intValue()) {
			// Switch places.
			mostFragmentedList.set(i-1, lower);
			mostFragmentedList.set(i, higher);
		    }
		    else
			break;
		}
		while(mostFragmentedList.size() > listMaxLength)
		    mostFragmentedList.remove(mostFragmentedList.size()-1);
	    }
	    else if(rec instanceof CommonHFSCatalogFolderRecord) {
		CommonHFSCatalogFolder catFolder =
                        ((CommonHFSCatalogFolderRecord)rec).getData();
		if(verbose) println("  Processing folder \"" + rec.getKey().getNodeName().toString() + "\"");
		else print(".");
		recursiveFragmentSearch(fsView, rec, mostFragmentedList, listMaxLength, verbose);
	    }
	    else if(rec instanceof CommonHFSCatalogFolderThreadRecord) {
		CommonHFSCatalogFolderThread catThread =
                        ((CommonHFSCatalogFolderThreadRecord)rec).getData();
	    }
	    else if(rec instanceof CommonHFSCatalogFileThreadRecord) {
		CommonHFSCatalogFileThread catThread =
                        ((CommonHFSCatalogFileThreadRecord)rec).getData();
	    }
            else
                throw new RuntimeException("Unknown record type: " +
                        rec.getClass());
	}
    }
    
    private static void operationSystemFileInfo(Operation op,
            ReadableRandomAccessStream hfsFile, long fsOffset, long fsLength) {
//	ReadableRandomAccessStream oldHfsFile = hfsFile;
// 	System.err.println("Opening hack UDIF file...");
// 	hfsFile = new UDIFRandomAccessLLF("/Users/erik/documents.dmg");
// 	System.err.println("Opened.");

        DataLocator inputDataLocator = new ReadableStreamDataLocator(
                new ReadableConcatenatedStream(hfsFile, fsOffset, fsLength));

        FileSystemMajorType[] fsTypes =
                FileSystemDetector.detectFileSystem(inputDataLocator);

        FileSystemMajorType hfsType = null;
        outer:
        for(FileSystemMajorType type : fsTypes) {
            switch(type) {
                case APPLE_HFS:
                case APPLE_HFS_PLUS:
                case APPLE_HFSX:
                    if(hfsType != null)
                        throw new RuntimeException("Conflicting file system " +
                                "types: Detected both " + hfsType + " and " +
                                type + ".");
                    hfsType = type;
                    break;
                default:
                    break;
            }
        }

        if(hfsType == null) {
            System.err.println("No HFS file system found.");
            System.exit(1);
        }
        else
            System.out.println("Detected a " + hfsType + " file system.");

        FileSystemHandlerFactory fact = hfsType.createDefaultHandlerFactory();
        FileSystemHandler fsHandler = fact.createHandler(inputDataLocator);

        HFSCommonFileSystemHandler hfsHandler;
        if(fsHandler instanceof HFSCommonFileSystemHandler)
            hfsHandler = (HFSCommonFileSystemHandler) fsHandler;
        else
            throw new RuntimeException("Unexpected HFS fsHandler type: " +
                    fsHandler.getClass());

	HFSVolume fsView = hfsHandler.getFSView();
	CommonHFSVolumeHeader header = fsView.getVolumeHeader();


        ReservedID[] ids;
        CommonHFSForkData[] interestingFiles;
        String[] labels;

        if(header instanceof CommonHFSVolumeHeader.HFSPlusImplementation) {
            CommonHFSVolumeHeader.HFSPlusImplementation plusHeader =
                    (CommonHFSVolumeHeader.HFSPlusImplementation) header;
            ids = new ReservedID[] {
                ReservedID.ALLOCATION_FILE,
                ReservedID.EXTENTS_FILE,
                ReservedID.CATALOG_FILE,
                ReservedID.ATTRIBUTES_FILE,
                ReservedID.STARTUP_FILE,
            };

            interestingFiles = new CommonHFSForkData[] {
                plusHeader.getAllocationFile(),
                plusHeader.getExtentsOverflowFile(),
                plusHeader.getCatalogFile(),
                plusHeader.getAttributesFile(),
                plusHeader.getStartupFile(),
            };

            labels = new String[] {
                "Allocation file",
                "Extents file",
                "Catalog file",
                "Attributes file",
                "Startup file",
            };
        }
        else {
            ids = new ReservedID[] {
                ReservedID.EXTENTS_FILE,
                ReservedID.CATALOG_FILE,
            };

            interestingFiles = new CommonHFSForkData[] {
                header.getExtentsOverflowFile(),
                header.getCatalogFile(),
            };

            labels = new String[] {
                "Extents file",
                "Catalog file",
            };

        }

	for(CommonHFSForkData f : interestingFiles) { f.print(System.out, ""); }
// 	HFSPlusForkData allocationFile = header.getAllocationFile();
// 	HFSPlusForkData extentsFile = header.getExtentsFile();
// 	HFSPlusForkData catalogFile = header.getCatalogFile();
// 	HFSPlusForkData attributesFile = header.getAttributesFile();
// 	HFSPlusForkData File = header.getStartupFile();

	//HFSPlusCatalogLeafRecord rootRecord = fsView.getRoot();
	//HFSPlusCatalogLeafRecord currentDir = rootRecord;
	
	for(int i = 0; i < interestingFiles.length; ++i) {
	    System.out.println(labels[i] + ":");
	    CommonHFSForkData currentFile = interestingFiles[i];
	    long basicExtentsBlockCount = 0;
	    CommonHFSExtentDescriptor[] basicExtents =
                    currentFile.getBasicExtents();
	    long numberOfExtents = 0;
	    for(CommonHFSExtentDescriptor cur : basicExtents) {
		if(cur.getStartBlock() == 0 && cur.getBlockCount() == 0)
		    break;
		else {
		    basicExtentsBlockCount += cur.getBlockCount();
		    ++numberOfExtents;
		}
	    }
	    
	    if(currentFile.getLogicalSize() <= basicExtentsBlockCount*header.getAllocationBlockSize()) {
		// All blocks are in basic extents
		System.out.println("  Number of extents: " + numberOfExtents +
                        " (all in basic)");
	    }
	    else {
		ReservedID currentID = ids[i];
		if(currentID == ReservedID.EXTENTS_FILE) {
		    System.out.println("  OVERFLOW IN EXTENTS OVERFLOW FILE!!");
		}
		else {
		    CommonHFSExtentDescriptor[] allDescriptors = fsView.
                            getExtentsOverflowFile().
                            getAllDataExtentDescriptors(
                            fsView.getCommonHFSCatalogNodeID(currentID),
                            currentFile);
		    System.out.println("  Number of extents: " +
                            allDescriptors.length + " (overflowed)");
		}
	    }
	}
    }
    
    public static void printUsageInfo() {
	// For measurement of the standard terminal width in fixed width environments:
	// 80:  <-------------------------------------------------------------------------------->
	println("hfsx - HFSExplorer Command Line Interface");
        println("Version " + VERSION + " Build #" + BuildNumber.BUILD_NUMBER);
	println(COPYRIGHT);
        println();
	println("Utility to explore various aspects of an HFS/HFS+/HFSX filesystem.");
	println("usage: hfsx [common options] <verb> [verb options] <file/device>");
	println();
	println("  Common options:");
	println("    -apm  Specifies that the HFS partition is embedded within an Apple");
	println("          Partition Map. The user will be allowed to choose which partition in");
	println("          the map to attempt reading.");
	println("    -v    Verbose operation.");
	println();
	println("  Verbs:");
	println("    browse  Launches a mode where the user can browse the files in a HFS+ file");
	println("            system.");
	println("    chfrag  Lists the 10 most fragmented files of the volume.");
//	println("    test    Launches a test mode for extensive exploration of file system");
//	println("            structures. Only for debugging purposes.");
	println();
	println("  Verb options:");
	println("    <none currently defined>");
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
    public static void vprintln() {
	//System.out.print(BACKSPACE79);
	if(options.verbose) System.out.println();
    }
    public static void vprintln(String s) {
	//System.out.print(BACKSPACE79);
	if(options.verbose) System.out.println(s);
    }
    public static void vprint(String s) {
	//System.out.print(BACKSPACE79);
	if(options.verbose) System.out.print(s);
    }
    
    public static boolean parseOptions(String[] arguments, int offset,
            int length) {
	int i;
	String currentArg = null;
	for(i = offset; i < length; ++i) {
	    currentArg = arguments[i];
	    if(!currentArg.startsWith("-"))
		break;
	    else if(currentArg.equals("-apm"))
		options.readAPM = true;
	    else if(currentArg.equals("-v"))
		options.verbose = true;
	    else
		println("\"" + currentArg + "\" is not a valid parameter.");
	}

	// Now comes the verb
	if(currentArg.equals("browse"))
	    operation = Operation.BROWSE;
	else if(currentArg.equals("chfrag"))
	    operation = Operation.FRAGCHECK;
	//else if(currentArg.equals("test"))
	//    operation = Operation.TEST;
	else if(currentArg.equals("systemfileinfo"))
	    operation = Operation.SYSTEMFILEINFO;
        else {
            System.err.println("Unknown operation: " + currentArg);
            return false;
        }

        if(operation != null) {
            for(++i; i < length; ++i)
                operation.addArg(arguments[i]);
        }

	//System.out.println("SETTING FILENAME TO!! ::: " + arguments[length-1]);
	//operation.setFilename(arguments[length-1]);

        return true;
    }

    /*
    public static HFSPlusCatalogFile findFileID(HFSPlusCatalogLeafNode leafNode, HFSCatalogNodeID nodeID) {
	HFSPlusCatalogLeafRecord[] records = leafNode.getLeafRecords();
	for(int i = 0; i < records.length; ++i) {
	    HFSPlusCatalogLeafRecord curRec = records[i];
	    HFSPlusCatalogLeafRecordData curRecData = curRec.getData();
	    if(curRecData instanceof HFSPlusCatalogFile && 
	       ((HFSPlusCatalogFile)curRecData).getFileID().toInt() == nodeID.toInt()) {
		return (HFSPlusCatalogFile)curRecData;
	    }
	}
	return null;
    }
    public static HFSPlusCatalogFolder findFolderID(HFSPlusCatalogLeafNode leafNode, HFSCatalogNodeID nodeID) {
	HFSPlusCatalogLeafRecord[] records = leafNode.getLeafRecords();
	for(int i = 0; i < records.length; ++i) {
	    HFSPlusCatalogLeafRecord curRec = records[i];
	    HFSPlusCatalogLeafRecordData curRecData = curRec.getData();
	    if(curRecData instanceof HFSPlusCatalogFolder && 
	       ((HFSPlusCatalogFolder)curRecData).getFolderID().toInt() == nodeID.toInt()) {
		return (HFSPlusCatalogFolder)curRecData;
	    }
	}
	return null;
    }
    */
}
