import java.util.LinkedList;
import java.io.OutputStream;
import java.io.IOException;

/** Provides a view over the data in an HFS Plus file system (HFS and HFSX future
    support is planned), making data access easy.
    Unrelated to javax.swing.jfilechooser.FileSystemView. */

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
    /* old inaccurate text:
     * However, some assumptions would have to be made.
     * - The volume header will not be reread every time, we will assume it to
     *   be static. Otherwise performance would probably be poor.
     */
      
    private final LowLevelFile hfsFile;
    private final long fsOffset;
    //private HFSPlusVolumeHeader volHeader;
    
    public HFSFileSystemView(LowLevelFile hfsFile, long fsOffset) {
	this.hfsFile = hfsFile;
	this.fsOffset = fsOffset;
    }
    
    public HFSPlusVolumeHeader getVolumeHeader() {
	byte[] currentBlock = new byte[512]; // Could be made a global var?
	hfsFile.seek(fsOffset + 1024);
	hfsFile.read(currentBlock);
	return new HFSPlusVolumeHeader(currentBlock);
    }
    
    public HFSPlusCatalogLeafRecord getRoot() {
	// Boring intialization... (read everything)
	// Details of the catalog file should be moved to a catalog file view later
	HFSPlusVolumeHeader header = getVolumeHeader();
	long catalogFilePosition = header.getBlockSize()*header.getCatalogFile().getExtents().getExtentDescriptor(0).getStartBlock();
	long catalogFileLength = header.getBlockSize()*header.getCatalogFile().getExtents().getExtentDescriptor(0).getBlockCount();
	hfsFile.seek(fsOffset + catalogFilePosition);
	byte[] nodeDescriptorData = new byte[14];
	if(hfsFile.read(nodeDescriptorData) != nodeDescriptorData.length)
	    System.out.println("ERROR: Did not read nodeDescriptor completely.");
	BTNodeDescriptor btnd = new BTNodeDescriptor(nodeDescriptorData, 0);
	
	byte[] headerRec = new byte[BTHeaderRec.length()];
	hfsFile.readFully(headerRec);
	BTHeaderRec bthr = new BTHeaderRec(headerRec, 0);
	// End of boring intialization... 

	
	// Search down through the layers of indices to the record with parentID 1.
	HFSCatalogNodeID parentID = new HFSCatalogNodeID(1);
	int currentNodeNumber = bthr.getRootNode();
	
	byte[] currentNodeData = new byte[bthr.getNodeSize()];
	hfsFile.seek(fsOffset + catalogFilePosition + Util2.unsign(currentNodeNumber)*bthr.getNodeSize());
	hfsFile.readFully(currentNodeData);
	BTNodeDescriptor nodeDescriptor = new BTNodeDescriptor(currentNodeData, 0);
	while(nodeDescriptor.getKind() == BTNodeDescriptor.BT_INDEX_NODE) {
	    HFSPlusCatalogIndexNode currentNode = new HFSPlusCatalogIndexNode(currentNodeData, 0, bthr.getNodeSize());
	    BTIndexRecord matchingRecord = findKey(currentNode, parentID);
	    
	    currentNodeNumber = matchingRecord.getIndex();
	    hfsFile.seek(fsOffset + catalogFilePosition + Util2.unsign(currentNodeNumber)*bthr.getNodeSize());
	    hfsFile.readFully(currentNodeData);
	    nodeDescriptor = new BTNodeDescriptor(currentNodeData, 0);
	}
	
	// Leaf node reached. Find record with parent id 1. (or whatever value is in the parentID var :) )
	if(nodeDescriptor.getKind() == BTNodeDescriptor.BT_LEAF_NODE) {
	    HFSPlusCatalogLeafNode leaf = new HFSPlusCatalogLeafNode(currentNodeData, 0, bthr.getNodeSize());
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
    
    /*
    public HFSPlusCatalogLeafRecord getRecord(HFSCatalogNodeID id, UniStr...??) {
	// Boring intialization... (read everything)
	// Details of the catalog file should be moved to a catalog file view later
	HFSPlusVolumeHeader header = getVolumeHeader();
	long catalogFilePosition = header.getBlockSize()*header.getCatalogFile().getExtents().getExtentDescriptor(0).getStartBlock();
	long catalogFileLength = header.getBlockSize()*header.getCatalogFile().getExtents().getExtentDescriptor(0).getBlockCount();
	hfsFile.seek(fsOffset + catalogFilePosition);
	byte[] nodeDescriptorData = new byte[14];
	if(hfsFile.read(nodeDescriptorData) != nodeDescriptorData.length)
	    System.out.println("ERROR: Did not read nodeDescriptor completely.");
	BTNodeDescriptor btnd = new BTNodeDescriptor(nodeDescriptorData, 0);
	
	int nodeSize = bthr.getNodeSize();
	byte[] currentNode = new byte[nodeSize];
	
	HFSCatalogNodeID dirID = id;
	int currentNodeNumber = bthr.getRootNode();
	
	// Search down through the layers of indices (O(log n) steps, where n is the size of the tree)
	int requestedDir = dirID.toInt();
	long catalogFilePosition = header.getBlockSize()*header.getCatalogFile().getExtents().getExtentDescriptor(0).getStartBlock();
	
	byte[] currentNodeData = new byte[bthr.getNodeSize()];
	hfsFile.seek(fsOffset + catalogFilePosition + Util2.unsign(currentNodeNumber)*bthr.getNodeSize());
	hfsFile.readFully(currentNodeData);
	BTNodeDescriptor nodeDescriptor = new BTNodeDescriptor(currentNodeData, 0);
	while(nodeDescriptor.getKind() == BTNodeDescriptor.BT_INDEX_NODE) {
	    BTIndexNode currentNode = new HFSPlusCatalogIndexNode(currentNodeData, 0, bthr.getNodeSize());
	    BTIndexRecord[] matchingRecords = findLEKey(currentNode, dirID);
	    //System.out.println("Matching records: " + matchingRecords.length);
	    
	    LinkedList<HFSPlusCatalogLeafRecord> results = new LinkedList<HFSPlusCatalogLeafRecord>();
	    for(BTIndexRecord bir : matchingRecords) {
		HFSPlusCatalogLeafRecord[] partResult = collectFilesInDir(dirID, bir.getIndex(), hfsFile, 
									  fsOffset, header, bthr);
		for(HFSPlusCatalogLeafRecord curRes : partResult)
		    results.addLast(curRes);
	    }
	    return results.toArray(new HFSPlusCatalogLeafRecord[results.size()]);
	    
	}
    }
    */
    
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
    /** Not public because you really should use the method above to access folder listings. */
    HFSPlusCatalogLeafRecord[] listRecords(HFSCatalogNodeID folderID) {	
	// Boring intialization... (read everything)
	// Details of the catalog file should be moved to a catalog file view later
	HFSPlusVolumeHeader header = getVolumeHeader();
	long catalogFilePosition = header.getBlockSize()*header.getCatalogFile().getExtents().getExtentDescriptor(0).getStartBlock();
	long catalogFileLength = header.getBlockSize()*header.getCatalogFile().getExtents().getExtentDescriptor(0).getBlockCount();
	hfsFile.seek(fsOffset + catalogFilePosition);
	byte[] nodeDescriptorData = new byte[14];
	if(hfsFile.read(nodeDescriptorData) != nodeDescriptorData.length)
	    System.out.println("ERROR: Did not read nodeDescriptor completely.");
	BTNodeDescriptor btnd = new BTNodeDescriptor(nodeDescriptorData, 0);
	
	byte[] headerRec = new byte[BTHeaderRec.length()];
	hfsFile.readFully(headerRec);
	BTHeaderRec bthr = new BTHeaderRec(headerRec, 0);
	// End of boring intialization... 
	
	return collectFilesInDir(folderID, bthr.getRootNode(), hfsFile, fsOffset, header, bthr);
    }

    public long extractDataForkToStream(HFSPlusCatalogFile file, OutputStream os) throws IOException {
	HFSPlusForkData dataFork = file.getDataFork();
	return extractForkToStream(dataFork, os);
    }
    public long extractResourceForkToStream(HFSPlusCatalogFile file, OutputStream os) throws IOException {
	HFSPlusForkData resFork = file.getResourceFork();
	return extractForkToStream(resFork, os);
    }
    public long extractForkToStream(HFSPlusForkData forkData, OutputStream os) throws IOException {
	// Boring intialization... (read everything)
	// Details of the catalog file should be moved to a catalog file view later
	HFSPlusVolumeHeader header = getVolumeHeader();
	long catalogFilePosition = header.getBlockSize()*header.getCatalogFile().getExtents().getExtentDescriptor(0).getStartBlock();
	long catalogFileLength = header.getBlockSize()*header.getCatalogFile().getExtents().getExtentDescriptor(0).getBlockCount();
	hfsFile.seek(fsOffset + catalogFilePosition);
	byte[] nodeDescriptorData = new byte[14];
	if(hfsFile.read(nodeDescriptorData) != nodeDescriptorData.length)
	    System.out.println("ERROR: Did not read nodeDescriptor completely.");
	BTNodeDescriptor btnd = new BTNodeDescriptor(nodeDescriptorData, 0);
	
	byte[] headerRec = new byte[BTHeaderRec.length()];
	hfsFile.readFully(headerRec);
	BTHeaderRec bthr = new BTHeaderRec(headerRec, 0);
	// End of boring intialization... 
	
	int blockSize = header.getBlockSize(); // Okay, I should unsign this but.. seriously (:
	long totalBytesRemaining = forkData.getLogicalSize();
	HFSPlusExtentDescriptor[] descs = forkData.getExtents().getExtentDescriptors();
	byte[] buffer = new byte[4096];
	for(HFSPlusExtentDescriptor desc : descs) {
	    if(totalBytesRemaining == 0)
		break;
	    desc.print(System.out, "");
	    long startBlock = Util2.unsign(desc.getStartBlock());
	    long blockCount = Util2.unsign(desc.getBlockCount());
	    if(blockCount == 0)
		continue;
	    else {
		if(totalBytesRemaining < 1)
		    System.err.println("WTF! totalBytesRemaining == " + totalBytesRemaining + " and I'm trying to read? WHAT WAS _I_ THINKING?! DUUUH!!!"); // Yes I've been sitting here for too long...
		hfsFile.seek(fsOffset + startBlock*blockSize);
		
		long nrBytesToRead = blockCount*blockSize;
		if(nrBytesToRead > totalBytesRemaining)
		    nrBytesToRead = totalBytesRemaining;
		
		long bytesRead = 0;
		while(bytesRead < nrBytesToRead) {
		    int currentBytesRead = hfsFile.read(buffer, 0, 
							(nrBytesToRead-bytesRead < buffer.length?
							 (int)(nrBytesToRead-bytesRead):buffer.length));
		    if(currentBytesRead == -1)
			throw new RuntimeException("Unexpectedly reached end of file!");
		    else {
			bytesRead += currentBytesRead;
			os.write(buffer, 0, currentBytesRead);
		    }
		}
		
		totalBytesRemaining -= bytesRead;
	    }
	}
	if(totalBytesRemaining != 0)
	    System.err.println("WARNING: At end of extractForkToStream and totalBytesRemaining == " + totalBytesRemaining + " == not 0!");
	return forkData.getLogicalSize()-totalBytesRemaining;
    }
    
    // Utility methods
    private static HFSPlusCatalogLeafRecord[] collectFilesInDir(HFSCatalogNodeID dirID, int currentNodeNumber, 
								LowLevelFile hfsFile, long fsOffset, 
								HFSPlusVolumeHeader header, BTHeaderRec bthr) {
	// Try to list contents in specified dir
	int requestedDir = dirID.toInt();
	long catalogFilePosition = header.getBlockSize()*header.getCatalogFile().getExtents().getExtentDescriptor(0).getStartBlock();
	
	byte[] currentNodeData = new byte[bthr.getNodeSize()];
	hfsFile.seek(fsOffset + catalogFilePosition + Util2.unsign(currentNodeNumber)*bthr.getNodeSize());
	hfsFile.readFully(currentNodeData);
	
	BTNodeDescriptor nodeDescriptor = new BTNodeDescriptor(currentNodeData, 0);
	if(nodeDescriptor.getKind() == BTNodeDescriptor.BT_INDEX_NODE) {
	    BTIndexNode currentNode = new HFSPlusCatalogIndexNode(currentNodeData, 0, bthr.getNodeSize());
	    BTIndexRecord[] matchingRecords = findLEChildKeys(currentNode, dirID);
	    //System.out.println("Matching records: " + matchingRecords.length);
	    
	    LinkedList<HFSPlusCatalogLeafRecord> results = new LinkedList<HFSPlusCatalogLeafRecord>();
	    for(BTIndexRecord bir : matchingRecords) {
		HFSPlusCatalogLeafRecord[] partResult = collectFilesInDir(dirID, bir.getIndex(), hfsFile, 
									  fsOffset, header, bthr);
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
    
    public static BTIndexRecord[] findLEChildKeys(BTIndexNode indexNode, HFSCatalogNodeID rootFolderID) {
	LinkedList<BTIndexRecord> result = new LinkedList<BTIndexRecord>();
	BTIndexRecord records[] = indexNode.getIndexRecords();
	BTIndexRecord largestMatchingRecord = null;//records[0];
	HFSPlusCatalogKey largestMatchingKey = null;
	for(int i = 0; i < records.length; ++i) {
	    if(records[i].getKey() instanceof HFSPlusCatalogKey) {
		HFSPlusCatalogKey key = (HFSPlusCatalogKey)records[i].getKey();
		if(key.getParentID().toInt() < rootFolderID.toInt() && 
		   (largestMatchingKey == null || 
		    key.getParentID().toInt() > largestMatchingKey.getParentID().toInt())) {
		    largestMatchingKey = key;
		    largestMatchingRecord = records[i];
		}
		else if(key.getParentID().toInt() == rootFolderID.toInt())
		    result.addLast(records[i]);
	    }
	    else
		System.out.println("UNKNOWN KEY TYPE IN findLEChildKeys");
	}
	
	if(largestMatchingKey != null)
	    result.addFirst(largestMatchingRecord);
	return result.toArray(new BTIndexRecord[result.size()]);
	
	/*
	  if exists in index node a value equal to the nodeID
	 */
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
    
    private static BTIndexRecord findLEKey(BTIndexNode indexNode, HFSCatalogNodeID nodeID, String searchString) {
	/* 
	 * Algoritm:
	 *   Key searchKey
	 *   Key greatestMatchingKey;
	 *   For each n : records
	 *     If n.key <= searchKey && n.key > greatestMatchingKey
	 *       greatestMatchingKey = n.key
	 */
	HFSPlusCatalogKey searchKey = new HFSPlusCatalogKey(nodeID.toInt(), searchString);
	long unsignedNodeID = Util2.unsign(nodeID.toInt());
	BTIndexRecord records[] = indexNode.getIndexRecords();
	BTIndexRecord largestMatchingRecord = null;//records[0];
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
