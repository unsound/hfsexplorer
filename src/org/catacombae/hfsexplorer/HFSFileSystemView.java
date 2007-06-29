/*-
 * Copyright (C) 2006 Erik Larsson
 * 
 * All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 */

package org.catacombae.hfsexplorer;

import org.catacombae.hfsexplorer.types.*;
import java.util.List;
import java.util.LinkedList;
import java.io.OutputStream;
import java.io.IOException;

/** Provides a view over the data in an HFS Plus file system (HFS and HFSX future
    support is planned), making data access easier.
    (Unrelated to javax.swing.jfilechooser.FileSystemView) */

public class HFSFileSystemView {
    /*
     * The idea is to make few assumptions about static data in the file system.
     * No operations should be cached, since it would provide an inaccurate view
     * of a live file system.
     *
     * I don't know why I'm doing it this way... It would probably limit no one
     * if I just assumed exclusive access, but I can write another class for
     * that, providing better performance.
     */

    /** Internal class. */
    private abstract class InitProcedure {
	public HFSPlusVolumeHeader header;
	public ForkFilter forkFilterFile;
	public BTNodeDescriptor btnd;
	public BTHeaderRec bthr;
	
	public InitProcedure() {
	    this.header = getVolumeHeader();
	    this.forkFilterFile = getForkFilterFile(header);
	    
	    forkFilterFile.seek(0);
	    byte[] nodeDescriptorData = new byte[14];
	    if(forkFilterFile.read(nodeDescriptorData) != nodeDescriptorData.length)
		System.out.println("ERROR: Did not read nodeDescriptor completely.");
	    this.btnd = new BTNodeDescriptor(nodeDescriptorData, 0);
	    
	    byte[] headerRec = new byte[BTHeaderRec.length()];
	    forkFilterFile.readFully(headerRec);
	    this.bthr = new BTHeaderRec(headerRec, 0);
	    
	}
	public abstract ForkFilter getForkFilterFile(HFSPlusVolumeHeader header);
    }
    
    /** Internal class. */
    private class CatalogInitProcedure extends InitProcedure {
	public ForkFilter catalogFile;
	
	public CatalogInitProcedure() {
	    this.catalogFile = forkFilterFile;
	}
	
	public ForkFilter getForkFilterFile(HFSPlusVolumeHeader header) {
	    HFSPlusExtentDescriptor[] allCatalogFileDescriptors = getAllDataExtentDescriptors(HFSCatalogNodeID.kHFSCatalogFileID, header.getCatalogFile());
	    return new ForkFilter(header.getCatalogFile(), allCatalogFileDescriptors, hfsFile, fsOffset, header.getBlockSize());
	}
    }
    
    /** Internal class. */
    private class ExtentsInitProcedure extends InitProcedure {
	public ForkFilter extentsFile;
	
	public ExtentsInitProcedure() {
	    this.extentsFile = forkFilterFile;
	}
	
	public ForkFilter getForkFilterFile(HFSPlusVolumeHeader header) {
	    return new ForkFilter(header.getExtentsFile(), header.getExtentsFile().getExtents().getExtentDescriptors(),
				  hfsFile, fsOffset, header.getBlockSize());
	}
    }

    private final LowLevelFile hfsFile;
    private final long fsOffset;
    
    public HFSFileSystemView(LowLevelFile hfsFile, long fsOffset) {
	this.hfsFile = hfsFile;
	this.fsOffset = fsOffset;
    }
    
    public LowLevelFile getStream() {
	return hfsFile;
    }
    
    public HFSPlusVolumeHeader getVolumeHeader() {
	byte[] currentBlock = new byte[512]; // Could be made a global var? (war?)
	hfsFile.seek(fsOffset + 1024);
	hfsFile.read(currentBlock);
	return new HFSPlusVolumeHeader(currentBlock);
    }
    
    
    public HFSPlusCatalogLeafRecord getRoot() {
	CatalogInitProcedure init = new CatalogInitProcedure();

	// Search down through the layers of indices to the record with parentID 1.
	HFSCatalogNodeID parentID = new HFSCatalogNodeID(1);
	int currentNodeNumber = init.bthr.getRootNode();
	
	byte[] currentNodeData = new byte[init.bthr.getNodeSize()];
	init.catalogFile.seek(Util2.unsign(currentNodeNumber)*init.bthr.getNodeSize());
	init.catalogFile.readFully(currentNodeData);
	BTNodeDescriptor nodeDescriptor = new BTNodeDescriptor(currentNodeData, 0);
	while(nodeDescriptor.getKind() == BTNodeDescriptor.BT_INDEX_NODE) {
	    HFSPlusCatalogIndexNode currentNode = new HFSPlusCatalogIndexNode(currentNodeData, 0, init.bthr.getNodeSize());
	    BTIndexRecord matchingRecord = findKey(currentNode, parentID);
	    
	    currentNodeNumber = matchingRecord.getIndex();
	    init.catalogFile.seek(Util2.unsign(currentNodeNumber)*init.bthr.getNodeSize());
	    init.catalogFile.readFully(currentNodeData);
	    nodeDescriptor = new BTNodeDescriptor(currentNodeData, 0);
	}
	
	// Leaf node reached. Find record with parent id 1. (or whatever value is in the parentID variable :) )
	if(nodeDescriptor.getKind() == BTNodeDescriptor.BT_LEAF_NODE) {
	    HFSPlusCatalogLeafNode leaf = new HFSPlusCatalogLeafNode(currentNodeData, 0, init.bthr.getNodeSize());
	    HFSPlusCatalogLeafRecord[] recs = leaf.getLeafRecords();
	    for(HFSPlusCatalogLeafRecord rec : recs)
		if(rec.getKey().getParentID().toInt() == parentID.toInt())
		    return rec;
	    return null;
	}
	else
	    throw new RuntimeException("Expected leaf node. Found other kind: " + 
				       nodeDescriptor.getKind());
    }
    
    /**
     * Returns the requested node in the catalog file. If the requested node is not an index node, and not
     * a leaf node, <code>null</code> is returned because they are the only ones that are implemented at
     * the moment. Otherwise the returned BTNode object will be of subtype HFSPlusCatalogIndexNode or
     * HFSPlusCatalogLeafNode.<br>
     * Calling this method with a negative <code>nodeNumber</code> argument returns the root node.
     * @param nodeNumber the node number inside the catalog file, or a negative value if we want the root
     * @return the requested node if it exists and has type index node or leaf node, null otherwise
     */
    public BTNode getCatalogNode(int nodeNumber) {
	CatalogInitProcedure init = new CatalogInitProcedure();

	int currentNodeNumber;
	if(nodeNumber < 0) // Means that we should get the root node
	    currentNodeNumber = init.bthr.getRootNode();
	else
	    currentNodeNumber = nodeNumber;
	
	int nodeSize = init.bthr.getNodeSize();
	
	byte[] currentNodeData = new byte[init.bthr.getNodeSize()];
	init.catalogFile.seek(Util2.unsign(currentNodeNumber)*Util2.unsign(init.bthr.getNodeSize()));
	init.catalogFile.readFully(currentNodeData);
	BTNodeDescriptor nodeDescriptor = new BTNodeDescriptor(currentNodeData, 0);
	
	if(nodeDescriptor.getKind() == BTNodeDescriptor.BT_INDEX_NODE)
	    return new HFSPlusCatalogIndexNode(currentNodeData, 0, init.bthr.getNodeSize());
	else if(nodeDescriptor.getKind() == BTNodeDescriptor.BT_LEAF_NODE)
	    return new HFSPlusCatalogLeafNode(currentNodeData, 0, init.bthr.getNodeSize());
	else
	    return null;
    }
    public List<HFSPlusCatalogLeafRecord> getPathTo(HFSCatalogNodeID leafID) {
	HFSPlusCatalogLeafRecord leafRec = getRecord(leafID, new HFSUniStr255(""));
	if(leafRec != null)
	    return getPathTo(leafRec);
	else
	    throw new RuntimeException("No folder thread found!");
    }
    public List<HFSPlusCatalogLeafRecord> getPathTo(HFSPlusCatalogLeafRecord leaf) {
	LinkedList<HFSPlusCatalogLeafRecord> pathList = new LinkedList<HFSPlusCatalogLeafRecord>();
	pathList.addLast(leaf);
	HFSCatalogNodeID parentID = leaf.getKey().getParentID();
	while(parentID.toLong() != 1) {
	    HFSPlusCatalogLeafRecord parent = getRecord(parentID, new HFSUniStr255("")); // Look for the thread record associated with the parent dir
	    if(parent == null)
		throw new RuntimeException("No folder thread found!");
	    HFSPlusCatalogLeafRecordData data = parent.getData();
	    if(data.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER_THREAD &&
	       data instanceof HFSPlusCatalogThread) {
		HFSPlusCatalogThread threadData = (HFSPlusCatalogThread)data;
		pathList.addFirst(getRecord(threadData.getParentID(), threadData.getNodeName()));
		parentID = threadData.getParentID();
	    }
	    else if(data.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FILE_THREAD &&
		    data instanceof HFSPlusCatalogThread)
		throw new RuntimeException("Tried to get folder thread (" + parentID + ",\"\") but found a file thread!");
	    else
		throw new RuntimeException("Tried to get folder thread (" + parentID + ",\"\") but found a " + data.getClass() + "!");		
	}
	return pathList;
    }
	
    public HFSPlusCatalogLeafRecord getRecord(HFSCatalogNodeID parentID, HFSUniStr255 nodeName) {
	CatalogInitProcedure init = new CatalogInitProcedure();
	
	int nodeSize = init.bthr.getNodeSize();
	
	int currentNodeNumber = init.bthr.getRootNode();
	
	// Search down through the layers of indices (O(log n) steps, where n is the size of the tree)
	
	byte[] currentNodeData = new byte[init.bthr.getNodeSize()];
	init.catalogFile.seek(Util2.unsign(currentNodeNumber)*Util2.unsign(init.bthr.getNodeSize()));
	init.catalogFile.readFully(currentNodeData);
	BTNodeDescriptor nodeDescriptor = new BTNodeDescriptor(currentNodeData, 0);
	
	while(nodeDescriptor.getKind() == BTNodeDescriptor.BT_INDEX_NODE) {
	    HFSPlusCatalogIndexNode currentNode = new HFSPlusCatalogIndexNode(currentNodeData, 0, init.bthr.getNodeSize());
	    BTIndexRecord matchingRecord = findLEKey(currentNode, parentID, nodeName);
	    
	    currentNodeNumber = matchingRecord.getIndex();
	    init.catalogFile.seek(Util2.unsign(currentNodeNumber)*Util2.unsign(init.bthr.getNodeSize()));
	    init.catalogFile.readFully(currentNodeData);
	    nodeDescriptor = new BTNodeDescriptor(currentNodeData, 0);
	}
	
	// Leaf node reached. Find record.
	if(nodeDescriptor.getKind() == BTNodeDescriptor.BT_LEAF_NODE) {
	    HFSPlusCatalogLeafNode leaf = new HFSPlusCatalogLeafNode(currentNodeData, 0, init.bthr.getNodeSize());
	    HFSPlusCatalogLeafRecord[] recs = leaf.getLeafRecords();
	    for(HFSPlusCatalogLeafRecord rec : recs)
		if(rec.getKey().compareTo(new HFSPlusCatalogKey(parentID, nodeName)) == 0)
		    return rec;
	    return null;
	}
	else
	    throw new RuntimeException("Expected leaf node. Found other kind: " + 
				       nodeDescriptor.getKind());
    }
    
    
    /** More typesafe than <code>listRecords(HFSCatalogNodeID)</code> since it checks that folderRecord
	is of appropriate type first. */
    public HFSPlusCatalogLeafRecord[] listRecords(HFSPlusCatalogLeafRecord folderRecord) {
	HFSPlusCatalogFolder folder;
	if(folderRecord.getData().getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER &&
	   folderRecord.getData() instanceof HFSPlusCatalogFolder) {
	    folder = (HFSPlusCatalogFolder)folderRecord.getData();
	    return listRecords(folder.getFolderID());
	}
	else
	    throw new RuntimeException("Invalid input (not a folder record).");
    }
    /** You really should use the method above to access folder listings. However, the folderID is all
	that's really needed. */
    public HFSPlusCatalogLeafRecord[] listRecords(HFSCatalogNodeID folderID) {	
	CatalogInitProcedure init = new CatalogInitProcedure();
	final ForkFilter catalogFile = init.getForkFilterFile(init.header);
	return collectFilesInDir(folderID, init.bthr.getRootNode(), hfsFile, fsOffset, init.header, init.bthr, catalogFile);
    }

    public long extractDataForkToStream(HFSPlusCatalogLeafRecord fileRecord, OutputStream os) throws IOException {
	return extractDataForkToStream(fileRecord, os, NullProgressMonitor.getInstance());
    }
    public long extractDataForkToStream(HFSPlusCatalogLeafRecord fileRecord, OutputStream os,
					ProgressMonitor pm) throws IOException {
	HFSPlusCatalogLeafRecordData recData = fileRecord.getData();
	if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FILE &&
	   recData instanceof HFSPlusCatalogFile) {
	    HFSPlusCatalogFile catFile = (HFSPlusCatalogFile)recData;
	    HFSPlusForkData dataFork = catFile.getDataFork();
	    return extractForkToStream(dataFork, getAllDataExtentDescriptors(fileRecord), os, pm);
	}
	else
	    throw new IllegalArgumentException("fileRecord.getData() it not of type RECORD_TYPE_FILE");
    }
    public long extractResourceForkToStream(HFSPlusCatalogLeafRecord fileRecord, OutputStream os) throws IOException {
	return extractResourceForkToStream(fileRecord, os, NullProgressMonitor.getInstance());
    }
    public long extractResourceForkToStream(HFSPlusCatalogLeafRecord fileRecord, OutputStream os,
					    ProgressMonitor pm) throws IOException {
	HFSPlusCatalogLeafRecordData recData = fileRecord.getData();
	if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FILE &&
	   recData instanceof HFSPlusCatalogFile) {
	    HFSPlusCatalogFile catFile = (HFSPlusCatalogFile)recData;
	    HFSPlusForkData resFork = catFile.getResourceFork();
	    return extractForkToStream(resFork, getAllResourceExtentDescriptors(fileRecord), os, pm);
	}
	else
	    throw new IllegalArgumentException("fileRecord.getData() it not of type RECORD_TYPE_FILE");
    }
    public long extractForkToStream(HFSPlusForkData forkData, HFSPlusExtentDescriptor[] extentDescriptors,
				    OutputStream os) throws IOException {
	return extractForkToStream(forkData, extentDescriptors, os, NullProgressMonitor.getInstance());
    }
    public long extractForkToStream(HFSPlusForkData forkData, HFSPlusExtentDescriptor[] extentDescriptors,
				    OutputStream os, ProgressMonitor pm) throws IOException {
	CatalogInitProcedure init = new CatalogInitProcedure();	
	
// 	if(false) { // Deprecated method
// 	    int blockSize = init.header.getBlockSize(); // Okay, I should unsign this but.. seriously (:
// 	    long totalBytesRemaining = forkData.getLogicalSize();
// 	    HFSPlusExtentDescriptor[] descs = forkData.getExtents().getExtentDescriptors();
// 	    byte[] buffer = new byte[4096];
// 	    for(HFSPlusExtentDescriptor desc : descs) {
// 		if(totalBytesRemaining == 0)
// 		    break;
// 		desc.print(System.out, "");
// 		long startBlock = Util2.unsign(desc.getStartBlock());
// 		long blockCount = Util2.unsign(desc.getBlockCount());
// 		if(blockCount == 0)
// 		    continue;
// 		else {
// 		    if(totalBytesRemaining < 1)
// 			System.err.println("WTF! totalBytesRemaining == " + totalBytesRemaining + " and I'm trying to read? WHAT WAS _I_ THINKING?! DUUUH!!!"); // Yes I've been sitting here for too long...
// 		    hfsFile.seek(fsOffset + startBlock*blockSize);
		    
// 		    long nrBytesToRead = blockCount*blockSize;
// 		    if(nrBytesToRead > totalBytesRemaining)
// 			nrBytesToRead = totalBytesRemaining;
		
// 		    long bytesRead = 0;
// 		    while(bytesRead < nrBytesToRead) {
// 			int currentBytesRead = hfsFile.read(buffer, 0, 
// 							    (nrBytesToRead-bytesRead < buffer.length?
// 							     (int)(nrBytesToRead-bytesRead):buffer.length));
// 			if(currentBytesRead < 0)
// 			    throw new RuntimeException("Unexpectedly reached end of file!");
// 			else {
// 			    bytesRead += currentBytesRead;
// 			    pm.addDataProgress(currentBytesRead);
// 			    os.write(buffer, 0, currentBytesRead);
// 			}
// 		    }
		
// 		    totalBytesRemaining -= bytesRead;
// 		}
// 	    }
// 	    if(totalBytesRemaining != 0)
// 		System.err.println("WARNING: At end of extractForkToStream and totalBytesRemaining == " + totalBytesRemaining + " == not 0!");
// 	    return forkData.getLogicalSize()-totalBytesRemaining;
// 	}
// 	else { // Use the new ForkFilter class instead.
	ForkFilter forkFilter = new ForkFilter(forkData, extentDescriptors, hfsFile, fsOffset, init.header.getBlockSize());
	long bytesToRead = forkData.getLogicalSize();
	byte[] buffer = new byte[4096];
	while(bytesToRead > 0) {
	    if(pm.cancelSignaled()) break;
	    //System.out.print("forkFilter.read([].length=" + buffer.length + ", 0, " + (bytesToRead < buffer.length ? (int)bytesToRead : buffer.length) + "...");
	    int bytesRead = forkFilter.read(buffer, 0, (bytesToRead < buffer.length ? (int)bytesToRead : buffer.length));
	    //System.out.println("done. bytesRead = " + bytesRead);
	    if(bytesRead < 0)
		break;
	    else {
		pm.addDataProgress(bytesRead);
		os.write(buffer, 0, bytesRead);
		bytesToRead -= bytesRead;
	    }
	}
	return forkData.getLogicalSize()-bytesToRead;
// 	}
    }
    
    public HFSPlusExtentLeafRecord getOverflowExtent(HFSPlusExtentKey key) {
	//System.out.println("getOverflowExtent(..)");
	//System.err.println("my key:");
	//key.printFields(System.err, "");
	ExtentsInitProcedure init = new ExtentsInitProcedure();	
	
	int nodeSize = init.bthr.getNodeSize();
	
	int currentNodeNumber = init.bthr.getRootNode();
	
	// Search down through the layers of indices (O(log n) steps, where n is the size of the tree)
	
	byte[] currentNodeData = new byte[init.bthr.getNodeSize()];
	init.extentsFile.seek(Util2.unsign(currentNodeNumber)*Util2.unsign(init.bthr.getNodeSize()));
	init.extentsFile.readFully(currentNodeData);
	BTNodeDescriptor nodeDescriptor = new BTNodeDescriptor(currentNodeData, 0);
	
	while(nodeDescriptor.getKind() == BTNodeDescriptor.BT_INDEX_NODE) {
	    BTIndexNode currentNode = new HFSPlusExtentIndexNode(currentNodeData, 0, init.bthr.getNodeSize());
	    BTIndexRecord matchingRecord = findLEKey(currentNode, key);
	    
	    currentNodeNumber = matchingRecord.getIndex();
	    init.extentsFile.seek(Util2.unsign(currentNodeNumber)*Util2.unsign(init.bthr.getNodeSize()));
	    init.extentsFile.readFully(currentNodeData);
	    nodeDescriptor = new BTNodeDescriptor(currentNodeData, 0);
	}
	
	// Leaf node reached. Find record.
	if(nodeDescriptor.getKind() == BTNodeDescriptor.BT_LEAF_NODE) {
	    HFSPlusExtentLeafNode leaf = new HFSPlusExtentLeafNode(currentNodeData, 0, init.bthr.getNodeSize());
	    HFSPlusExtentLeafRecord[] recs = leaf.getLeafRecords();
	    for(HFSPlusExtentLeafRecord rec : recs) {
		//rec.getKey().print(System.err, "");
		if(rec.getKey().compareTo(key) == 0)
		    return rec;
	    }
// 	    try {
// 		java.io.FileOutputStream dataDump = new java.io.FileOutputStream("node_dump.dmp");
// 		dataDump.write(currentNodeData);
// 		dataDump.close();
// 		System.err.println("A dump of the node has been written to node_dump.dmp");
// 	    } catch(Exception e) { e.printStackTrace(); }
	    return null;
	}
	else
	    throw new RuntimeException("Expected leaf node. Found other kind: " + 
				       nodeDescriptor.getKind());
    }
    
    public HFSPlusExtentRecord[] getAllExtentRecords(HFSPlusCatalogLeafRecord requestFile, byte forkType) {
	HFSPlusCatalogLeafRecordData recData = requestFile.getData();
	if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FILE &&
	   recData instanceof HFSPlusCatalogFile) {
	    //int blockSize = getVolumeHeader().getBlockSize();
	    HFSPlusCatalogFile catFile = (HFSPlusCatalogFile) recData;
	    //	    HFSPlusExtentRecord[] result;
	    HFSPlusForkData forkData;
	    if(forkType == HFSPlusExtentKey.DATA_FORK)
		forkData = catFile.getDataFork();
	    else if(forkType == HFSPlusExtentKey.RESOURCE_FORK)
		forkData = catFile.getResourceFork();
	    else
		throw new IllegalArgumentException("Illegal fork type!");
	    return getAllExtentRecords(catFile.getFileID(), forkData, forkType);
	}
	else
	    throw new IllegalArgumentException("Not a file record!");
    }

    public HFSPlusExtentRecord[] getAllExtentRecords(HFSCatalogNodeID fileID, HFSPlusForkData forkData, byte forkType) {
	//HFSPlusCatalogLeafRecordData recData = requestFile.getData();
	//if(true) {
// 	if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FILE &&
// 	   recData instanceof HFSPlusCatalogFile) {
// 	    //int blockSize = getVolumeHeader().getBlockSize();
// 	    HFSPlusCatalogFile catFile = (HFSPlusCatalogFile) recData;
	HFSPlusExtentRecord[] result;
// 	    HFSPlusForkData forkData;
// 	    if(forkType == HFSPlusExtentKey.DATA_FORK)
// 		forkData = catFile.getDataFork();
// 	    else if(forkType == HFSPlusExtentKey.RESOURCE_FORK)
// 		forkData = catFile.getResourceFork();
// 	    else
// 		throw new IllegalArgumentException("Illegal fork type!");

	long basicExtentsBlockCount = 0;
	for(int i = 0; i < 8; ++i)
	    basicExtentsBlockCount += Util2.unsign(forkData.getExtents().getExtentDescriptor(i).getBlockCount());
	    
	if(basicExtentsBlockCount == forkData.getTotalBlocks()) {
	    result = new HFSPlusExtentRecord[1];
	    result[0] = forkData.getExtents();
	}
	else if(basicExtentsBlockCount > forkData.getTotalBlocks())
	    throw new RuntimeException("Weird programming error. (basicExtentsBlockCount > forkData.getTotalBlocks()) (" + basicExtentsBlockCount + " > " + forkData.getTotalBlocks() + ")");
	else {
	    //System.err.println("Reading overflow extent for file " + fileID.toString());
	    LinkedList<HFSPlusExtentRecord> resultList = new LinkedList<HFSPlusExtentRecord>();
	    resultList.add(forkData.getExtents());
	    long currentBlock = basicExtentsBlockCount;
		
	    while(currentBlock < forkData.getTotalBlocks()) {
		//System.err.println("  Reading 8 extents... (currentblock: " + currentBlock + " total: " + forkData.getTotalBlocks() + ")");
		// Construct key to find next extent record
		HFSPlusExtentKey extentKey = new HFSPlusExtentKey(forkType, fileID, (int)currentBlock);
		
		HFSPlusExtentLeafRecord currentRecord = getOverflowExtent(extentKey);
		if(currentRecord == null)
		    System.err.println("WARNING: currentRecord == null!!");
		HFSPlusExtentRecord currentRecordData = currentRecord.getRecordData();
		resultList.addLast(currentRecordData);
		for(int i = 0; i < 8; ++i)
		    currentBlock += Util2.unsign(currentRecordData.getExtentDescriptor(i).getBlockCount());
	    }
	    //System.err.println("  Finished reading extents... (currentblock: " + currentBlock + " total: " + forkData.getTotalBlocks() + ")");
		
	    result = resultList.toArray(new HFSPlusExtentRecord[resultList.size()]);
	}
	return result;
    }
    public HFSPlusExtentDescriptor[] getAllExtentDescriptors(HFSPlusCatalogLeafRecord requestFile, byte forkType) {
	return getAllExtentDescriptors(getAllExtentRecords(requestFile, forkType));
    }
    public HFSPlusExtentDescriptor[] getAllExtentDescriptors(HFSCatalogNodeID fileID, HFSPlusForkData forkData, 
							     byte forkType) {
	return getAllExtentDescriptors(getAllExtentRecords(fileID, forkData, forkType));
    }
    protected HFSPlusExtentDescriptor[] getAllExtentDescriptors(HFSPlusExtentRecord[] records) { //
	LinkedList<HFSPlusExtentDescriptor> descTmp = new LinkedList<HFSPlusExtentDescriptor>();
	mainLoop:
	for(HFSPlusExtentRecord rec : records) {
	    for(int i = 0; i < 8; ++i) {
		HFSPlusExtentDescriptor desc = rec.getExtentDescriptor(i);
		if(desc.getStartBlock() == 0 &&  desc.getBlockCount() == 0)
		    break mainLoop;
		else
		    descTmp.addLast(desc);
	    }
	}
	return descTmp.toArray(new HFSPlusExtentDescriptor[descTmp.size()]);
    }
    public HFSPlusExtentDescriptor[] getAllDataExtentDescriptors(HFSCatalogNodeID fileID, HFSPlusForkData forkData) {
	return getAllExtentDescriptors(fileID, forkData, HFSPlusExtentKey.DATA_FORK);
    }
    public HFSPlusExtentDescriptor[] getAllDataExtentDescriptors(HFSPlusCatalogLeafRecord requestFile) {
	return getAllExtentDescriptors(requestFile, HFSPlusExtentKey.DATA_FORK);
    }
    public HFSPlusExtentDescriptor[] getAllResourceExtentDescriptors(HFSCatalogNodeID fileID, HFSPlusForkData forkData) {
	return getAllExtentDescriptors(fileID, forkData, HFSPlusExtentKey.RESOURCE_FORK);
    }
    public HFSPlusExtentDescriptor[] getAllResourceExtentDescriptors(HFSPlusCatalogLeafRecord requestFile) {
	return getAllExtentDescriptors(requestFile, HFSPlusExtentKey.RESOURCE_FORK);
    }

    /** Returns the journal info block if a journal is present, null otherwise. */
    public JournalInfoBlock getJournalInfoBlock() {
	HFSPlusVolumeHeader vh = getVolumeHeader();
	if(vh.getAttributeVolumeJournaled()) {
	    long blockNumber = Util2.unsign(vh.getJournalInfoBlock());
	    hfsFile.seek(fsOffset + blockNumber*vh.getBlockSize());
	    byte[] data = new byte[JournalInfoBlock.getStructSize()];
	    hfsFile.readFully(data);
	    return new JournalInfoBlock(data, 0);
	}
	else
	    return null;
    }

    // Utility methods
    public static HFSPlusCatalogLeafRecord[] collectFilesInDir(HFSCatalogNodeID dirID, int currentNodeNumber, 
							       LowLevelFile hfsFile, long fsOffset, 
							       final HFSPlusVolumeHeader header, final BTHeaderRec bthr,
							       final ForkFilter catalogFile) {
	//final HFSPlusVolumeHeader header = init.header;
	//final BTHeaderRec bthr = init.bthr;
	// Try to list contents in specified dir
	//HFSPlusForkData catalogFile = header.getCatalogFile();
	//HFSPlusExtentDescriptor[] catalogExtents = getAllDataExtentDescriptors(HFSCatalogNodeID.kHFSCatalogFileID, catalogFile);
		
	byte[] currentNodeData = new byte[bthr.getNodeSize()];
	catalogFile.seek(Util2.unsign(currentNodeNumber)*bthr.getNodeSize());
	catalogFile.readFully(currentNodeData);
	
	BTNodeDescriptor nodeDescriptor = new BTNodeDescriptor(currentNodeData, 0);
	if(nodeDescriptor.getKind() == BTNodeDescriptor.BT_INDEX_NODE) {
	    BTIndexNode currentNode = new HFSPlusCatalogIndexNode(currentNodeData, 0, bthr.getNodeSize());
	    BTIndexRecord[] matchingRecords = findLEChildKeys(currentNode, dirID);
	    //System.out.println("Matching records: " + matchingRecords.length);
	    
	    LinkedList<HFSPlusCatalogLeafRecord> results = new LinkedList<HFSPlusCatalogLeafRecord>();
	    for(BTIndexRecord bir : matchingRecords) {
		HFSPlusCatalogLeafRecord[] partResult = collectFilesInDir(dirID, bir.getIndex(), hfsFile, 
									  fsOffset, header, bthr, catalogFile);
		for(HFSPlusCatalogLeafRecord curRes : partResult)
		    results.addLast(curRes);
	    }
	    return results.toArray(new HFSPlusCatalogLeafRecord[results.size()]);
	}
	else if(nodeDescriptor.getKind() == BTNodeDescriptor.BT_LEAF_NODE) {
	    HFSPlusCatalogLeafNode currentNode = new HFSPlusCatalogLeafNode(currentNodeData, 0, Util2.unsign(bthr.getNodeSize()));
	    
	    return getChildrenTo(currentNode, dirID);
	}
	else
	    throw new RuntimeException("Illegal type for node! (" + nodeDescriptor.getKind() + ")");
    }
    
    private static BTIndexRecord[] findLEChildKeys(BTIndexNode indexNode, HFSCatalogNodeID rootFolderID) {
	LinkedList<BTIndexRecord> result = new LinkedList<BTIndexRecord>();
	BTIndexRecord records[] = indexNode.getIndexRecords();
	BTIndexRecord largestMatchingRecord = null;//records[0];
	HFSPlusCatalogKey largestMatchingKey = null;
	for(int i = 0; i < records.length; ++i) {
	    if(records[i].getKey() instanceof HFSPlusCatalogKey) {
		HFSPlusCatalogKey key = (HFSPlusCatalogKey)records[i].getKey();
		if( key.getParentID().toLong() < rootFolderID.toLong() && 
		    (largestMatchingKey == null || key.compareTo(largestMatchingKey) > 0) ) {
		    largestMatchingKey = key;
		    largestMatchingRecord = records[i];
		}
		else if(key.getParentID().toLong() == rootFolderID.toLong())
		    result.addLast(records[i]);
	    }
	    else
		throw new RuntimeException("UNKNOWN KEY TYPE IN findLEChildKeys");
	}
	
	if(largestMatchingKey != null)
	    result.addFirst(largestMatchingRecord);
	return result.toArray(new BTIndexRecord[result.size()]);
    }
    
    /**
     * Find the actual entry in the index node corresponding to the id parentID.
     * This can only be used to find the root node (parentID 1), since that node would
     * always exist in the root index node at position 0, and the same all the way
     * down the tree.
     * So this method is actually too complicated for its purpose. (: But whatever...
     * @return the corresponding index record if found, null otherwise
     */
    private static BTIndexRecord findKey(HFSPlusCatalogIndexNode indexNode, HFSCatalogNodeID parentID) {
	for(BTIndexRecord rec : indexNode.getIndexRecords()) {
	    BTKey btKey = rec.getKey();
	    if(btKey instanceof HFSPlusCatalogKey) {
		HFSPlusCatalogKey key = (HFSPlusCatalogKey)btKey;
		if(key.getParentID().toInt() == parentID.toInt())
		    return rec;
	    }
	    else
		throw new RuntimeException("Unexpected key in HFSPlusCatalogIndexNode record.");
	}
	return null;
    }
    
    private static BTIndexRecord findLEKey(BTIndexNode indexNode, HFSCatalogNodeID nodeID, HFSUniStr255 searchString) {
	return findLEKey(indexNode, new HFSPlusCatalogKey(nodeID, searchString));
    }
    private static BTIndexRecord findLEKey(BTIndexNode indexNode, BTKey searchKey) {
	/* 
	 * Algorithm:
	 *   Key searchKey
	 *   Key greatestMatchingKey;
	 *   For each n : records
	 *     If n.key <= searchKey && n.key > greatestMatchingKey
	 *       greatestMatchingKey = n.key
	 */
	BTIndexRecord records[] = indexNode.getIndexRecords();
	BTIndexRecord largestMatchingRecord = null;
	for(int i = 0; i < records.length; ++i) {
	    if(records[i].getKey().compareTo(searchKey) <= 0 && 
	       (largestMatchingRecord == null || records[i].getKey().compareTo(largestMatchingRecord.getKey()) > 0)) {
		largestMatchingRecord = records[i];
	    }
	}
	return largestMatchingRecord;
    }
    
    private static HFSPlusCatalogLeafRecord[] getChildrenTo(HFSPlusCatalogLeafNode leafNode, HFSCatalogNodeID nodeID) {
	LinkedList<HFSPlusCatalogLeafRecord> children = new LinkedList<HFSPlusCatalogLeafRecord>();
	HFSPlusCatalogLeafRecord[] records = leafNode.getLeafRecords();
	for(int i = 0; i < records.length; ++i) {
	    HFSPlusCatalogLeafRecord curRec = records[i];
	    if(curRec.getKey().getParentID().toInt() == nodeID.toInt())
		children.addLast(curRec);
	}
	return children.toArray(new HFSPlusCatalogLeafRecord[children.size()]);
    }
    
    /*
    private static HFSPlusCatalogLeafRecord findRecordID(HFSPlusCatalogLeafNode leafNode, HFSCatalogNodeID nodeID) {
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
    */
    
}
