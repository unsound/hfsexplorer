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

package org.catacombae.hfsexplorer.unfinished;
import org.catacombae.hfsexplorer.*; // until we move it back

import org.catacombae.hfsexplorer.io.ReadableBlockCachingStream;
import org.catacombae.io.Readable;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.hfsexplorer.types.hfscommon.*;
import java.util.LinkedList;
import java.io.OutputStream;
import java.io.IOException;
import org.catacombae.hfsexplorer.types.JournalInfoBlock;
import org.catacombae.hfsexplorer.types.hfscommon.CommonBTNodeDescriptor.NodeType;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogNodeID.ReservedID;

/**
 * Provides a view over the data in an HFS file system, making data access
 * easier.<br>
 * (Unrelated to javax.swing.jfilechooser.FileSystemView)
 */
public abstract class BaseHFSFileSystemView {
    /*
     * The idea is to make few assumptions about static data in the file system.
     * No operations should be cached, since it would provide an inaccurate view
     * of a live file system.
     *
     * I don't know why I'm doing it this way... It would probably limit no one
     * if I just assumed exclusive access, but I can write another class for
     * that, providing better performance.
     *
     * Note: It has been shown that this approach has actually made it possible
     * to track changes in a live filesystem, for example when opening
     * /dev/disk0s2 in superuser mode in OS X.
     *
     * 2007-09-18: We now assume block size to be static. This can't possibly be
     *             a problem... ever. Variable staticBlockSize contains the
     *             block size.
     */
     
    public static long fileReadOffset = 0;

    /** Internal class. */
    private abstract class InitProcedure {

        public final CommonHFSVolumeHeader header;
        public final ReadableRandomAccessStream forkFilterFile;
        public final CommonBTNodeDescriptor btnd;
        public final CommonBTHeaderRecord bthr;

        public InitProcedure() {
            this.header = getVolumeHeader();
            header.print(System.err, "    ");
            this.forkFilterFile = getForkFilterFile(header);

            forkFilterFile.seek(0);
            //byte[] nodeDescriptorData = new byte[14];
            //if(forkFilterFile.read(nodeDescriptorData) != nodeDescriptorData.length)
            //	System.out.println("ERROR: Did not read nodeDescriptor completely.");
            this.btnd = getNodeDescriptor(forkFilterFile);
            this.bthr = getHeaderRecord(forkFilterFile);
        //byte[] headerRec = new byte[BTHeaderRec.length()];
        //forkFilterFile.readFully(headerRec);
        //this.bthr = new BTHeaderRec(headerRec, 0);

        }

        protected abstract ReadableRandomAccessStream getForkFilterFile(CommonHFSVolumeHeader header);
    }
    
    /** Internal class. */
    private class CatalogInitProcedure extends InitProcedure {

        public final ReadableRandomAccessStream catalogFile;

        public CatalogInitProcedure() {
            this.catalogFile = forkFilterFile;
        }

        protected ReadableRandomAccessStream getForkFilterFile(CommonHFSVolumeHeader header) {
            if(catalogCache != null)
                return catalogCache;
            CommonHFSExtentDescriptor[] allCatalogFileDescriptors =
                    getAllDataExtentDescriptors(getCommonHFSCatalogNodeID(ReservedID.CATALOG_FILE),
                    header.getCatalogFile());
            return new ForkFilter(header.getCatalogFile(), allCatalogFileDescriptors,
                    hfsFile, fsOffset, staticBlockSize, header.getFirstAllocationBlock());
        }
    }
    
    /** Internal class. */
    private class ExtentsInitProcedure extends InitProcedure {
        public final ReadableRandomAccessStream extentsFile;

        public ExtentsInitProcedure() {
            this.extentsFile = forkFilterFile;
        }

        protected ReadableRandomAccessStream getForkFilterFile(CommonHFSVolumeHeader header) {
            return new ForkFilter(header.getExtentsOverflowFile(),
                    header.getExtentsOverflowFile().getBasicExtents(),
                    hfsFile, fsOffset, staticBlockSize, header.getFirstAllocationBlock());
        }
    }

    protected static interface CatalogOperations {
        public CommonHFSCatalogIndexNode newCatalogIndexNode(byte[] data,
                int offset, int nodeSize, CommonBTHeaderRecord bthr);

        public CommonHFSCatalogKey newCatalogKey(CommonHFSCatalogNodeID nodeID,
                CommonHFSCatalogString searchString, CommonBTHeaderRecord bthr);

        public CommonHFSCatalogLeafNode newCatalogLeafNode(byte[] data,
                int offset, int nodeSize, CommonBTHeaderRecord bthr);

        public CommonHFSCatalogLeafRecord newCatalogLeafRecord(byte[] data,
                int offset, CommonBTHeaderRecord bthr);
    }
    /*protected static final CatalogOperations HFS_PLUS_OPERATIONS = new CatalogOperations() {
	    public CommonHFSCatalogIndexNode newCatalogIndexNode(
                    byte[] data, int offset, int nodeSize, CommonBTHeaderRecord bthr) {
		return createCommonHFSCatalogIndexNode(data, offset, nodeSize);
	    }
	    public CommonHFSCatalogKey newCatalogKey(
                    CommonHFSCatalogNodeID nodeID, CommonHFSCatalogString searchString, CommonBTHeaderRecord bthr) {
		return new CommonHFSCatalogKey(nodeID, searchString);
	    }
	    public CommonHFSCatalogLeafNode newCatalogLeafNode(
                    byte[] data, int offset, int nodeSize, CommonBTHeaderRecord bthr) {
		return new CommonHFSCatalogLeafNode(data, offset, nodeSize);
	    }
	    public CommonHFSCatalogLeafRecord newCatalogLeafRecord(
                    byte[] data, int offset, CommonBTHeaderRecord bthr) {
		return new CommonHFSCatalogLeafRecord(data, offset);
	    }
  	};*/

    protected ReadableRandomAccessStream hfsFile;
    private final ReadableRandomAccessStream backingFile;
    protected final long fsOffset;
    protected final CatalogOperations catOps;
    protected final long staticBlockSize;
    
    // Variables for reading cached files.
    private ReadableBlockCachingStream catalogCache = null;
    
    protected BaseHFSFileSystemView(ReadableRandomAccessStream hfsFile, long fsOffset, CatalogOperations ops, boolean cachingEnabled) {
	this.hfsFile = hfsFile;
	this.backingFile = hfsFile;
	this.fsOffset = fsOffset;
	this.catOps = ops;
	this.staticBlockSize = getVolumeHeader().getBlockSize();
	
	if(cachingEnabled)
	    enableFileSystemCaching();
    }
    public boolean isFileSystemCachingEnabled() {
	return hfsFile != backingFile && backingFile instanceof ReadableBlockCachingStream;
    }
    public void enableFileSystemCaching() {
	enableFileSystemCaching(256*1024, 64); // 64 pages of 256 KiB each is the default setting
    }
    public void enableFileSystemCaching(int blockSize, int blocksInCache) {
	hfsFile = new ReadableBlockCachingStream(backingFile, blockSize, blocksInCache);
    }
    public void disableFileSystemCaching() {
	hfsFile = backingFile;
    }
    
    /** Switches to cached mode for reading the catalog file. */
    public void retainCatalogFile() {
	CatalogInitProcedure init = new CatalogInitProcedure();
	ReadableRandomAccessStream ff = init.forkFilterFile;
	catalogCache = new ReadableBlockCachingStream(ff, 512*1024, 32); // 512 KiB blocks, 32 of them
	catalogCache.preloadBlocks();
    }
    
    /** Disables cached mode for reading the catalog file. */
    public void releaseCatalogFile() {
        catalogCache = null;
    }
    
    public ReadableRandomAccessStream getStream() {
        return hfsFile;
    }
    
    public abstract CommonHFSVolumeHeader getVolumeHeader();
    protected abstract CommonBTHeaderNode createCommonBTHeaderNode(byte[] currentNodeData,
            int offset, int nodeSize);
    protected abstract CommonBTNodeDescriptor getNodeDescriptor(Readable rd);
    protected abstract CommonBTHeaderRecord getHeaderRecord(Readable rd);
    protected abstract CommonBTNodeDescriptor createCommonBTNodeDescriptor(
            byte[] currentNodeData, int offset);
    
    protected abstract CommonHFSExtentIndexNode createCommonHFSExtentIndexNode(
            byte[] currentNodeData, int offset, int nodeSize);
    
    protected abstract CommonHFSExtentLeafNode createCommonHFSExtentLeafNode(
            byte[] currentNodeData, int offset, int nodeSize);
    
    protected abstract CommonHFSExtentKey createCommonHFSExtentKey(
            CommonHFSForkType forkType, CommonHFSCatalogNodeID fileID, int startBlock);
    
    protected abstract CommonHFSCatalogNodeID getCommonHFSCatalogNodeID(
            ReservedID requestedNodeID);
    /*protected abstract CommonHFSCatalogString createCommonHFSCatalogString(
            String name);*/
    
    /**
     * Decodes the supplied CommonHFSCatalogString according to the current
     * settings of the view.
     * 
     * @param str
     * @return
     */
    public abstract String getString(CommonHFSCatalogString str);
    
    public CommonHFSCatalogFolderRecord getRoot() {
        CatalogInitProcedure init = new CatalogInitProcedure();

        // Search down through the layers of indices to the record with parentID 1.
        CommonHFSCatalogNodeID parentID = getCommonHFSCatalogNodeID(ReservedID.ROOT_PARENT);
        final int nodeSize = init.bthr.getNodeSize();
        long currentNodeOffset = init.bthr.getRootNodeNumber() * init.bthr.getNodeSize();

        System.err.println("Got header record: ");
        init.bthr.print(System.err, " ");

        byte[] currentNodeData = new byte[nodeSize];
        init.catalogFile.seek(currentNodeOffset);
        init.catalogFile.readFully(currentNodeData);
        CommonBTNodeDescriptor nodeDescriptor = createCommonBTNodeDescriptor(currentNodeData, 0);
        while(nodeDescriptor.getNodeType() == NodeType.INDEX) {
            CommonHFSCatalogIndexNode currentNode =
                    catOps.newCatalogIndexNode(currentNodeData, 0, init.bthr.getNodeSize(), init.bthr);
            System.err.println("currentNode:");
            currentNode.print(System.err, "  ");
            CommonBTIndexRecord matchingRecord = findKey(currentNode, parentID);
            
            //currentNodeNumber = matchingRecord.getIndex();
            currentNodeOffset = matchingRecord.getIndexAsOffset(nodeSize);
            init.catalogFile.seek(currentNodeOffset);
            init.catalogFile.readFully(currentNodeData);
            nodeDescriptor = createCommonBTNodeDescriptor(currentNodeData, 0);
        }

        // Leaf node reached. Find record with parent id 1. (or whatever value is in the parentID variable :) )
        if(nodeDescriptor.getNodeType() == NodeType.LEAF) {
            CommonHFSCatalogLeafNode leaf =
                    catOps.newCatalogLeafNode(currentNodeData, 0, nodeSize, init.bthr);
            CommonHFSCatalogLeafRecord[] recs = leaf.getLeafRecords();
            for(CommonHFSCatalogLeafRecord rec : recs) {
                if(rec.getKey().getParentID().toInt() == parentID.toInt()) {
                    if(rec instanceof CommonHFSCatalogFolderRecord)
                        return (CommonHFSCatalogFolderRecord)rec;
                    else
                        throw new RuntimeException("Error in internal structures: " +
                                " root node is not a folder record, but a " +
                                rec.getClass());
                }
            }
            return null;
        }
        else {
            throw new RuntimeException("Expected leaf node. Found other kind: " +
                    nodeDescriptor.getNodeType());
        }
    }
    
    public CommonBTHeaderNode getCatalogHeaderNode() {
        CommonBTNode firstNode = getCatalogNode(0);
        if(firstNode instanceof CommonBTHeaderNode) {
            return (CommonBTHeaderNode)firstNode;
        }
        else
            throw new RuntimeException("Unexpected node type at catalog node 0: " +
                    firstNode.getClass());
    }

    /**
     * Returns the requested node in the catalog file. If the requested node is not a header, index or
     * leaf node, <code>null</code> is returned because they are the only ones that are implemented at
     * the moment. Otherwise the returned BTNode object will be of subtype HFSPlusCatalogIndexNode or
     * HFSPlusCatalogLeafNode.<br>
     * Calling this method with a negative <code>nodeNumber</code> argument returns the root node.
     * 
     * @param nodeNumber the node number inside the catalog file, or a negative value if we want the root
     * @return the requested node if it exists and has type index node or leaf node, null otherwise
     */
    public CommonBTNode getCatalogNode(long nodeNumber) {
        CatalogInitProcedure init = new CatalogInitProcedure();

        long currentNodeNumber;
        if(nodeNumber < 0) // Means that we should get the root node
            currentNodeNumber = init.bthr.getRootNodeNumber();
        else
            currentNodeNumber = nodeNumber;

        int nodeSize = init.bthr.getNodeSize();

        byte[] currentNodeData = new byte[init.bthr.getNodeSize()];
        init.catalogFile.seek(currentNodeNumber * init.bthr.getNodeSize());
        init.catalogFile.readFully(currentNodeData);
        CommonBTNodeDescriptor nodeDescriptor = createCommonBTNodeDescriptor(currentNodeData, 0);

        if(nodeDescriptor.getNodeType() == NodeType.HEADER)
            return createCommonBTHeaderNode(currentNodeData, 0, init.bthr.getNodeSize());
        if(nodeDescriptor.getNodeType() == NodeType.INDEX)
            return catOps.newCatalogIndexNode(currentNodeData, 0, init.bthr.getNodeSize(), init.bthr);
        else if(nodeDescriptor.getNodeType() == NodeType.LEAF)
            return catOps.newCatalogLeafNode(currentNodeData, 0, init.bthr.getNodeSize(), init.bthr);
        else
            return null;
    }
    
    /**
     * Calculates the path in the file system hierarchy to <code>leaf</code>.
     * The path will be returned as a list where the first element is the root
     * of the tree, and the last element is <code>leaf</code>. All the elements
     * in between are the path components from the root to the leaf.
     * 
     * @param leafID the catalog node ID of the leaf.
     * @return a list of path components with the root record ('/') as head and
     * <code>leaf</code> as tail.
     */
    public LinkedList<CommonHFSCatalogLeafRecord> getPathTo(CommonHFSCatalogNodeID leafID) {
	CommonHFSCatalogLeafRecord leafRec = getRecord(leafID, CommonHFSCatalogString.EMPTY);
	if(leafRec != null)
	    return getPathTo(leafRec);
	else
	    throw new RuntimeException("No folder thread found for leaf id " +
                    leafID.toLong() + "!");
    }
    
    /**
     * Calculates the path in the file system hierarchy to <code>leaf</code>.
     * The path will be returned as a list where the first element is the root
     * of the tree, and the last element is <code>leaf</code>. All the elements
     * in between are the path components from the root to the leaf.
     * 
     * @param leaf the leaf to which the path from the root will go.
     * @return a list of path components with the root record ('/') as head and
     * <code>leaf</code> as tail.
     */
    public LinkedList<CommonHFSCatalogLeafRecord> getPathTo(CommonHFSCatalogLeafRecord leaf) {
        if(leaf == null)
            throw new IllegalArgumentException("argument \"leaf\" must not be null!");
        
	LinkedList<CommonHFSCatalogLeafRecord> pathList = new LinkedList<CommonHFSCatalogLeafRecord>();
	pathList.addLast(leaf);
	CommonHFSCatalogNodeID parentID = leaf.getKey().getParentID();
	while(!parentID.equals(parentID.getReservedID(ReservedID.ROOT_PARENT))) {
	    CommonHFSCatalogLeafRecord parent = getRecord(parentID, CommonHFSCatalogString.EMPTY); // Look for the thread record associated with the parent dir
	    if(parent == null)
		throw new RuntimeException("No folder thread found!");
	    //CommonHFSCatalogLeafRecord data = parent.getData();
	    if(parent instanceof CommonHFSCatalogFolderThreadRecord) {
		CommonHFSCatalogFolderThreadRecord threadRec = (CommonHFSCatalogFolderThreadRecord)parent;
                CommonHFSCatalogFolderThread thread = threadRec.getData();
		pathList.addFirst(getRecord(thread.getParentID(), thread.getNodeName()));
		parentID = thread.getParentID();
	    }
	    else if(parent instanceof CommonHFSCatalogFileThreadRecord)
		throw new RuntimeException("Tried to get folder thread (" + parentID + ",\"\") but found a file thread!");
	    else
		throw new RuntimeException("Tried to get folder thread (" + parentID + ",\"\") but found a " + parent.getClass() + "!");		
	}
	return pathList;
    }
	
    public CommonHFSCatalogLeafRecord getRecord(CommonHFSCatalogNodeID parentID, CommonHFSCatalogString nodeName) {
	CatalogInitProcedure init = new CatalogInitProcedure();
	
	final int nodeSize = init.bthr.getNodeSize();
	
	long currentNodeOffset = init.bthr.getRootNodeNumber()*nodeSize;
	
	// Search down through the layers of indices (O(log n) steps, where n is the size of the tree)
	
	byte[] currentNodeData = new byte[init.bthr.getNodeSize()];
	init.catalogFile.seek(currentNodeOffset);
	init.catalogFile.readFully(currentNodeData);
	CommonBTNodeDescriptor nodeDescriptor = createCommonBTNodeDescriptor(currentNodeData, 0);
	
	while(nodeDescriptor.getNodeType() == NodeType.INDEX) {
	    CommonHFSCatalogIndexNode currentNode =
                    catOps.newCatalogIndexNode(currentNodeData, 0, nodeSize, init.bthr);
	    CommonBTIndexRecord matchingRecord =
                    findLEKey(currentNode, catOps.newCatalogKey(parentID, nodeName, init.bthr));
	    
	    currentNodeOffset = matchingRecord.getIndexAsOffset(nodeSize);
	    init.catalogFile.seek(currentNodeOffset);
	    init.catalogFile.readFully(currentNodeData);
	    nodeDescriptor = createCommonBTNodeDescriptor(currentNodeData, 0);
	}
	
	// Leaf node reached. Find record.
	if(nodeDescriptor.getNodeType() == NodeType.LEAF) {
	    CommonHFSCatalogLeafNode leaf = catOps.newCatalogLeafNode(currentNodeData, 0, init.bthr.getNodeSize(), init.bthr);
	    CommonHFSCatalogLeafRecord[] recs = leaf.getLeafRecords();
	    for(CommonHFSCatalogLeafRecord rec : recs)
		if(rec.getKey().compareTo(catOps.newCatalogKey(parentID, nodeName, init.bthr)) == 0)
		    return rec;
	    return null;
	}
	else
	    throw new RuntimeException("Expected leaf node. Found other kind: " + 
				       nodeDescriptor.getNodeType());
    }
    
    
    /** More typesafe than <code>listRecords(HFSCatalogNodeID)</code> since it checks that folderRecord
	is of appropriate type first. */
    public CommonHFSCatalogLeafRecord[] listRecords(CommonHFSCatalogLeafRecord folderRecord) {
	if(folderRecord instanceof CommonHFSCatalogFolderRecord) {
	    CommonHFSCatalogFolder folder = ((CommonHFSCatalogFolderRecord)folderRecord).getData();
	    return listRecords(folder.getFolderID());
	}
	else
	    throw new RuntimeException("Invalid input (not a folder record).");
    }
    /** You should use the method above to access folder listings. However, the folderID is really all
	that's needed, but make sure it's a folder ID and not a file ID, or something bad will happen. */
    public CommonHFSCatalogLeafRecord[] listRecords(CommonHFSCatalogNodeID folderID) {	
	CatalogInitProcedure init = new CatalogInitProcedure();
	final ReadableRandomAccessStream catalogFile = init.forkFilterFile;
	return collectFilesInDir(folderID, init.bthr.getRootNodeNumber()*init.bthr.getNodeSize(),
            hfsFile, fsOffset, init.header, init.bthr, catalogFile);
    }

    public long extractDataForkToStream(CommonHFSCatalogLeafRecord fileRecord, OutputStream os) throws IOException {
	return extractDataForkToStream(fileRecord, os, NullProgressMonitor.getInstance());
    }
    public long extractDataForkToStream(CommonHFSCatalogLeafRecord fileRecord, OutputStream os,
					ProgressMonitor pm) throws IOException {
	// = fileRecord.getData();
	if(fileRecord instanceof CommonHFSCatalogFileRecord) {
	    CommonHFSCatalogFile catFile = ((CommonHFSCatalogFileRecord)fileRecord).getData();
	    CommonHFSForkData dataFork = catFile.getDataFork();
	    return extractForkToStream(dataFork, getAllDataExtentDescriptors(fileRecord), os, pm);
	}
	else
	    throw new IllegalArgumentException("fileRecord.getData() it not of type RECORD_TYPE_FILE");
    }
    public long extractResourceForkToStream(CommonHFSCatalogLeafRecord fileRecord, OutputStream os) throws IOException {
	return extractResourceForkToStream(fileRecord, os, NullProgressMonitor.getInstance());
    }
    public long extractResourceForkToStream(CommonHFSCatalogLeafRecord fileRecord, OutputStream os,
					    ProgressMonitor pm) throws IOException {
	//CommonHFSCatalogLeafRecordData recData = fileRecord.getData();
	if(fileRecord instanceof CommonHFSCatalogFileRecord) {
	    CommonHFSCatalogFile catFile = ((CommonHFSCatalogFileRecord)fileRecord).getData();
	    CommonHFSForkData resFork = catFile.getResourceFork();
	    return extractForkToStream(resFork, getAllResourceExtentDescriptors(fileRecord), os, pm);
	}
	else
	    throw new IllegalArgumentException("fileRecord.getData() it not of type RECORD_TYPE_FILE");
    }
    public long extractForkToStream(CommonHFSForkData forkData, CommonHFSExtentDescriptor[] extentDescriptors,
				    OutputStream os) throws IOException {
	return extractForkToStream(forkData, extentDescriptors, os, NullProgressMonitor.getInstance());
    }
    public long extractForkToStream(CommonHFSForkData forkData,
            CommonHFSExtentDescriptor[] extentDescriptors, OutputStream os,
            ProgressMonitor pm) throws IOException {
    CommonHFSVolumeHeader header = getVolumeHeader();
	ForkFilter forkFilter = new ForkFilter(forkData, extentDescriptors, hfsFile, fsOffset,
					       staticBlockSize, header.getFirstAllocationBlock());
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
    }
    
    /**
     * Returns a stream from which the data fork of the specified file record
     * can be accessed.
     * 
     * @return a stream from which the data fork of the specified file record
     * can be accessed.
     * @throws IllegalArgumentException if fileRecord is not a file record.
     */
    public ReadableRandomAccessStream getReadableDataForkStream(CommonHFSCatalogLeafRecord fileRecord) {
	if(fileRecord instanceof CommonHFSCatalogFileRecord) {
	    CommonHFSCatalogFile catFile = ((CommonHFSCatalogFileRecord)fileRecord).getData();
	    CommonHFSForkData fork = catFile.getDataFork();
	    return getReadableForkStream(fork, getAllDataExtentDescriptors(fileRecord));
	}
	else
	    throw new IllegalArgumentException("fileRecord.getData() it not of type RECORD_TYPE_FILE");
    }
    
    /**
     * Returns a stream from which the resource fork of the specified file record
     * can be accessed.
     * 
     * @return a stream from which the resource fork of the specified file record
     * can be accessed.
     * @throws IllegalArgumentException if fileRecord is not a file record.
     */
    public ReadableRandomAccessStream getReadableResourceForkStream(CommonHFSCatalogLeafRecord fileRecord) {
	if(fileRecord instanceof CommonHFSCatalogFileRecord) {
	    CommonHFSCatalogFile catFile = ((CommonHFSCatalogFileRecord)fileRecord).getData();
	    CommonHFSForkData fork = catFile.getResourceFork();
	    return getReadableForkStream(fork, getAllResourceExtentDescriptors(fileRecord));
	}
	else
	    throw new IllegalArgumentException("fileRecord.getData() it not of type RECORD_TYPE_FILE");
    }
    
    private ReadableRandomAccessStream getReadableForkStream(CommonHFSForkData forkData,
            CommonHFSExtentDescriptor[] extentDescriptors) {
        CommonHFSVolumeHeader header = getVolumeHeader();
        return new ForkFilter(forkData, extentDescriptors, hfsFile, fsOffset+fileReadOffset,
                staticBlockSize, header.getFirstAllocationBlock());
    }
    
    public CommonHFSExtentLeafRecord getOverflowExtent(CommonHFSExtentKey key) {
	System.err.println("getOverflowExtent(..)");
	//System.err.println("my key:");
	//key.printFields(System.err, "");
    System.err.println("  Doing ExtentsInitProcedure...");
	ExtentsInitProcedure init = new ExtentsInitProcedure();	
    System.err.println("  ExtentsInitProcedure done!");
	
	final int nodeSize = init.bthr.getNodeSize();
	
	long currentNodeOffset = init.bthr.getRootNodeNumber()*nodeSize;
	
	// Search down through the layers of indices (O(log n) steps, where n is the size of the tree)
	
	final byte[] currentNodeData = new byte[nodeSize];
	init.extentsFile.seek(currentNodeOffset);
	init.extentsFile.readFully(currentNodeData);
    System.err.println("  Calling createCommonBTNodeDescriptor(byte[" + currentNodeData.length + "], 0)...");
	CommonBTNodeDescriptor nodeDescriptor = createCommonBTNodeDescriptor(currentNodeData, 0);
	
	while(nodeDescriptor.getNodeType() == NodeType.INDEX) {
	    CommonBTIndexNode currentNode = createCommonHFSExtentIndexNode(currentNodeData, 0, nodeSize);
	    CommonBTIndexRecord matchingRecord = findLEKey(currentNode, key);
	    
	    currentNodeOffset = matchingRecord.getIndexAsOffset(nodeSize);
	    init.extentsFile.seek(currentNodeOffset);
	    init.extentsFile.readFully(currentNodeData);
        System.err.println("  Calling createCommonBTNodeDescriptor(byte[" + currentNodeData.length + "], 0)...");
	    nodeDescriptor = createCommonBTNodeDescriptor(currentNodeData, 0);
	}
	
	// Leaf node reached. Find record.
	if(nodeDescriptor.getNodeType() == NodeType.LEAF) {
	    CommonHFSExtentLeafNode leaf = createCommonHFSExtentLeafNode(currentNodeData, 0, nodeSize);
	    CommonHFSExtentLeafRecord[] recs = leaf.getLeafRecords();
	    for(CommonHFSExtentLeafRecord rec : recs) {
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
        System.err.println("Returning from getOverflowExtent(..)");
	    return null;
	}
	else
	    throw new RuntimeException("Expected leaf node. Found other kind: " + 
				       nodeDescriptor.getNodeType());
    }
    
    public CommonHFSExtentDescriptor[] getAllExtents(CommonHFSCatalogLeafRecord requestFile,
            CommonHFSForkType forkType) {
	if(requestFile instanceof CommonHFSCatalogFileRecord) {
	    CommonHFSCatalogFile catFile = ((CommonHFSCatalogFileRecord)requestFile).getData();
            
	    CommonHFSForkData forkData;
	    if(forkType == CommonHFSForkType.DATA_FORK)
		forkData = catFile.getDataFork();
	    else if(forkType == CommonHFSForkType.RESOURCE_FORK)
		forkData = catFile.getResourceFork();
	    else
		throw new IllegalArgumentException("Illegal fork type!");
	    return getAllExtents(catFile.getFileID(), forkData, forkType);
	}
	else
	    throw new IllegalArgumentException("Not a file record!");
    }

    public CommonHFSExtentDescriptor[] getAllExtents(CommonHFSCatalogNodeID fileID,
            CommonHFSForkData forkData, CommonHFSForkType forkType) {
        CommonHFSExtentDescriptor[] result;
        long blockSize = getVolumeHeader().getBlockSize();

        long basicExtentsBlockCount = 0;
        {
            CommonHFSExtentDescriptor[] basicExtents = forkData.getBasicExtents();
            for(int i = 0; i < basicExtents.length; ++i)
                basicExtentsBlockCount += basicExtents[i].getBlockCount();
        }

        if(basicExtentsBlockCount * blockSize >= forkData.getLogicalSize()) {
            result = forkData.getBasicExtents();
        }
        else {
            //System.err.println("Reading overflow extent for file " + fileID.toString());
            LinkedList<CommonHFSExtentDescriptor> resultList = new LinkedList<CommonHFSExtentDescriptor>();
            for(CommonHFSExtentDescriptor descriptor : forkData.getBasicExtents())
                resultList.add(descriptor);
            long currentBlock = basicExtentsBlockCount;

            while(currentBlock * blockSize < forkData.getLogicalSize()) {
                CommonHFSExtentKey extentKey =
                        createCommonHFSExtentKey(forkType, fileID, (int) currentBlock);

                CommonHFSExtentLeafRecord currentRecord = getOverflowExtent(extentKey);
                if(currentRecord == null)
                    System.err.println("ERROR: currentRecord == null!!");
                CommonHFSExtentDescriptor[] currentRecordData = currentRecord.getRecordData();
                for(CommonHFSExtentDescriptor cur : currentRecordData) {
                    resultList.add(cur);
                    currentBlock += cur.getBlockCount();
                }
            }
            //System.err.println("  Finished reading extents... (currentblock: " + currentBlock + " total: " + forkData.getTotalBlocks() + ")");

            result = resultList.toArray(new CommonHFSExtentDescriptor[resultList.size()]);
        }
        return result;
    }

    public CommonHFSExtentDescriptor[] getAllExtentDescriptors(CommonHFSCatalogLeafRecord requestFile,
            CommonHFSForkType forkType) {
	return getAllExtentDescriptors(getAllExtents(requestFile, forkType));
    }
    
    public CommonHFSExtentDescriptor[] getAllExtentDescriptors(CommonHFSCatalogNodeID fileID,
            CommonHFSForkData forkData, CommonHFSForkType forkType) {
	return getAllExtentDescriptors(getAllExtents(fileID, forkData, forkType));
    }
    
    protected CommonHFSExtentDescriptor[] getAllExtentDescriptors(
            CommonHFSExtentDescriptor[] descriptors) {
	LinkedList<CommonHFSExtentDescriptor> descTmp = new LinkedList<CommonHFSExtentDescriptor>();
        for(CommonHFSExtentDescriptor desc : descriptors) {
            if(desc.getStartBlock() == 0 && desc.getBlockCount() == 0) {
                break;
            } else {
                descTmp.addLast(desc);
            }
        }
	
	return descTmp.toArray(new CommonHFSExtentDescriptor[descTmp.size()]);
    }
    
    public CommonHFSExtentDescriptor[] getAllDataExtentDescriptors(
            CommonHFSCatalogNodeID fileID, CommonHFSForkData forkData) {
	return getAllExtentDescriptors(fileID, forkData, CommonHFSForkType.DATA_FORK);
    }
    
    public CommonHFSExtentDescriptor[] getAllDataExtentDescriptors(
            CommonHFSCatalogLeafRecord requestFile) {
	return getAllExtentDescriptors(requestFile, CommonHFSForkType.DATA_FORK);
    }
    
    public CommonHFSExtentDescriptor[] getAllResourceExtentDescriptors(
            CommonHFSCatalogNodeID fileID, CommonHFSForkData forkData) {
	return getAllExtentDescriptors(fileID, forkData, CommonHFSForkType.RESOURCE_FORK);
    }
    
    public CommonHFSExtentDescriptor[] getAllResourceExtentDescriptors(
            CommonHFSCatalogLeafRecord requestFile) {
	return getAllExtentDescriptors(requestFile, CommonHFSForkType.RESOURCE_FORK);
    }

    /** Returns the journal info block if a journal is present, null otherwise. */
    public abstract JournalInfoBlock getJournalInfoBlock();/* {
	HFSPlusVolumeHeader vh = getVolumeHeader();
	if(vh.getAttributeVolumeJournaled()) {
	    long blockNumber = Util2.unsign(vh.getJournalInfoBlock());
	    hfsFile.seek(fsOffset + blockNumber*staticBlockSize);
	    byte[] data = new byte[JournalInfoBlock.getStructSize()];
	    hfsFile.readFully(data);
	    return new JournalInfoBlock(data, 0);
	}
	else
	    return null;
    }*/
    
    // Utility methods
    
    private CommonHFSCatalogLeafRecord[] collectFilesInDir(CommonHFSCatalogNodeID dirID,
            long currentNodeOffset, ReadableRandomAccessStream hfsFile, long fsOffset,
            final CommonHFSVolumeHeader header, final CommonBTHeaderRecord bthr,
            final ReadableRandomAccessStream catalogFile) {
        final int nodeSize = bthr.getNodeSize();
        
	byte[] currentNodeData = new byte[nodeSize];
	catalogFile.seek(currentNodeOffset);
	catalogFile.readFully(currentNodeData);
	
	CommonBTNodeDescriptor nodeDescriptor = createCommonBTNodeDescriptor(currentNodeData, 0);
	if(nodeDescriptor.getNodeType() == NodeType.INDEX) {
	    CommonBTIndexNode currentNode =
                    catOps.newCatalogIndexNode(currentNodeData, 0, nodeSize, bthr);
	    CommonBTIndexRecord[] matchingRecords = findLEChildKeys(currentNode, dirID);
	    //System.out.println("Matching records: " + matchingRecords.length);
	    
	    LinkedList<CommonHFSCatalogLeafRecord> results =
                    new LinkedList<CommonHFSCatalogLeafRecord>();
            
	    for(CommonBTIndexRecord bir : matchingRecords) {
		CommonHFSCatalogLeafRecord[] partResult =
                        collectFilesInDir(dirID, bir.getIndexAsOffset(nodeSize), hfsFile, fsOffset,
                        header, bthr, catalogFile);
		for(CommonHFSCatalogLeafRecord curRes : partResult)
		    results.addLast(curRes);
	    }
	    return results.toArray(new CommonHFSCatalogLeafRecord[results.size()]);
	}
	else if(nodeDescriptor.getNodeType() == NodeType.LEAF) {
	    CommonHFSCatalogLeafNode currentNode =
                    catOps.newCatalogLeafNode(currentNodeData, 0, nodeSize, bthr);
	    
	    return getChildrenTo(currentNode, dirID);
	}
	else
	    throw new RuntimeException("Illegal type for node! (" + nodeDescriptor.getNodeType() + ")");
    }
    
    private static CommonBTIndexRecord[] findLEChildKeys(CommonBTIndexNode indexNode,
            CommonHFSCatalogNodeID rootFolderID) {
	LinkedList<CommonBTIndexRecord> result = new LinkedList<CommonBTIndexRecord>();
	CommonBTIndexRecord records[] = indexNode.getIndexRecords();
	CommonBTIndexRecord largestMatchingRecord = null;//records[0];
	CommonHFSCatalogKey largestMatchingKey = null;
	for(int i = 0; i < records.length; ++i) {
	    if(records[i].getKey() instanceof CommonHFSCatalogKey) {
		CommonHFSCatalogKey key = (CommonHFSCatalogKey)records[i].getKey();
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
	return result.toArray(new CommonBTIndexRecord[result.size()]);
    }
    
    /**
     * Find the actual entry in the index node corresponding to the id parentID.
     * This can only be used to find the root node (parentID 1), since that node would
     * always exist in the root index node at position 0, and the same all the way
     * down the tree.
     * So this method is actually too complicated for its purpose. (: But whatever...
     * @return the corresponding index record if found, null otherwise
     */
    private static CommonBTIndexRecord findKey(CommonHFSCatalogIndexNode indexNode,
            CommonHFSCatalogNodeID parentID) {
	for(CommonBTIndexRecord rec : indexNode.getIndexRecords()) {
	    CommonBTKey btKey = rec.getKey();
	    if(btKey instanceof CommonHFSCatalogKey) {
		CommonHFSCatalogKey key = (CommonHFSCatalogKey)btKey;
		if(key.getParentID().toInt() == parentID.toInt())
		    return rec;
	    }
	    else
		throw new RuntimeException("Unexpected key in HFSPlusCatalogIndexNode record.");
	}
	return null;
    }
    
    private static CommonBTIndexRecord findLEKey(CommonBTIndexNode indexNode, CommonBTKey searchKey) {
	/* 
	 * Algorithm:
	 *   input: Key searchKey
	 *   variables: Key greatestMatchingKey
	 *   For each n : records
	 *     If n.key <= searchKey && n.key > greatestMatchingKey
	 *       greatestMatchingKey = n.key
	 */
	CommonBTIndexRecord records[] = indexNode.getIndexRecords();
	CommonBTIndexRecord largestMatchingRecord = null;
	for(int i = 0; i < records.length; ++i) {
	    if(records[i].getKey().compareTo(searchKey) <= 0 && 
	       (largestMatchingRecord == null || records[i].getKey().compareTo(largestMatchingRecord.getKey()) > 0)) {
		largestMatchingRecord = records[i];
	    }
	}
	return largestMatchingRecord;
    }
    
    private static CommonHFSCatalogLeafRecord[] getChildrenTo(CommonHFSCatalogLeafNode leafNode,
            CommonHFSCatalogNodeID nodeID) {
	LinkedList<CommonHFSCatalogLeafRecord> children = new LinkedList<CommonHFSCatalogLeafRecord>();
	CommonHFSCatalogLeafRecord[] records = leafNode.getLeafRecords();
	for(int i = 0; i < records.length; ++i) {
	    CommonHFSCatalogLeafRecord curRec = records[i];
	    if(curRec.getKey().getParentID().toInt() == nodeID.toInt())
		children.addLast(curRec);
	}
	return children.toArray(new CommonHFSCatalogLeafRecord[children.size()]);
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
    
    protected long calculateDataForkSizeRecursive(CommonHFSCatalogLeafRecord[] recs) {
	return calculateForkSizeRecursive(recs, false);
    }
    protected long calculateDataForkSizeRecursive(CommonHFSCatalogLeafRecord rec) {
	return calculateForkSizeRecursive(rec, false);	
    }
    protected long calculateResourceForkSizeRecursive(CommonHFSCatalogLeafRecord[] recs) {
	return calculateForkSizeRecursive(recs, true);
    }
    protected long calculateResourceForkSizeRecursive(CommonHFSCatalogLeafRecord rec) {
	return calculateForkSizeRecursive(rec, true);
    }
    /** Calculates the complete size of the trees represented by <code>recs</code>. */
    protected long calculateForkSizeRecursive(CommonHFSCatalogLeafRecord[] recs, boolean resourceFork) {
	long totalSize = 0;
	for(CommonHFSCatalogLeafRecord rec : recs)
	    totalSize += calculateForkSizeRecursive(rec, resourceFork);
	return totalSize;
    }
    /** Calculates the complete size of the tree represented by <code>rec</code>. */
    protected long calculateForkSizeRecursive(CommonHFSCatalogLeafRecord rec, boolean resourceFork) {
	if(rec instanceof CommonHFSCatalogFileRecord) {
	    if(!resourceFork)
		return ((CommonHFSCatalogFileRecord)rec).getData().getDataFork().getLogicalSize();
	    else
		return ((CommonHFSCatalogFileRecord)rec).getData().getResourceFork().getLogicalSize();
	}
	else if(rec instanceof CommonHFSCatalogFolderRecord) {
	    CommonHFSCatalogNodeID requestedID =
                    ((CommonHFSCatalogFolderRecord)rec).getData().getFolderID();
	    CommonHFSCatalogLeafRecord[] contents = listRecords(requestedID);
	    long totalSize = 0;
	    for(CommonHFSCatalogLeafRecord outRec : contents) {
		totalSize += calculateForkSizeRecursive(outRec, resourceFork);
	    }
	    return totalSize;
	}
	else
	    return 0;
    }
}
