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

package org.catacombae.hfs;

import java.util.LinkedList;
import java.util.List;
import org.catacombae.hfs.io.ForkFilter;
import org.catacombae.io.ReadableRandomAccessSubstream;
import org.catacombae.hfs.types.hfscommon.CommonBTHeaderNode;
import org.catacombae.hfs.types.hfscommon.CommonBTHeaderRecord;
import org.catacombae.hfs.types.hfscommon.CommonBTIndexNode;
import org.catacombae.hfs.types.hfscommon.CommonBTIndexRecord;
import org.catacombae.hfs.types.hfscommon.CommonBTNode;
import org.catacombae.hfs.types.hfscommon.CommonBTNodeDescriptor;
import org.catacombae.hfs.types.hfscommon.CommonBTNodeDescriptor.NodeType;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogFileThreadRecord;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogFolder;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogFolderRecord;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogFolderThread;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogFolderThreadRecord;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogIndexNode;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogKey;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogLeafNode;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogLeafRecord;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogNodeID;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogNodeID.ReservedID;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogString;
import org.catacombae.hfs.types.hfscommon.CommonHFSExtentDescriptor;
import org.catacombae.hfs.types.hfscommon.CommonHFSVolumeHeader;
import org.catacombae.io.ReadableRandomAccessStream;

/**
 *
 * @author erik
 */
public class CatalogFile extends BTreeFile {
    private final CatalogOperations ops;

    CatalogFile(HFSVolume vol, BTreeOperations superOps,
            CatalogOperations ops) {
        super(vol, superOps);
        this.ops = ops;
    }

    class CatalogFileSession extends BTreeFileSession {
        final ReadableRandomAccessStream catalogFile;

        public CatalogFileSession() {
            this.catalogFile = btreeStream;
        }

        public void close() {}

        @Override
        protected ReadableRandomAccessStream getBTreeStream(
                CommonHFSVolumeHeader header) {
            //if(catalogCache != null)
            //    return catalogCache;
            CommonHFSExtentDescriptor[] allCatalogFileDescriptors =
                    vol.extentsOverflowFile.getAllDataExtentDescriptors(
                    vol.getCommonHFSCatalogNodeID(ReservedID.CATALOG_FILE),
                    header.getCatalogFile());
            return new ForkFilter(header.getCatalogFile(),
                    allCatalogFileDescriptors,
                    new ReadableRandomAccessSubstream(vol.hfsFile),
                    0,
                    header.getAllocationBlockSize(),
                    header.getAllocationBlockStart()*vol.physicalBlockSize);
        }
    }

    /**
     * Opens the catalog file for reading and reads the value of some important
     * variables (the "session").
     */
    CatalogFileSession openSession() {
        return new CatalogFileSession();
    }

    /** Switches to cached mode for reading the catalog file. */
    /*
    public void retainCatalogFile() {
        CatalogInitProcedure init = new CatalogInitProcedure();
        ReadableRandomAccessStream ff = init.forkFilterFile;
        catalogCache = new ReadableBlockCachingStream(ff, 512 * 1024, 32); // 512 KiB blocks, 32 of them
        catalogCache.preloadBlocks();
    }
    */

    /** Disables cached mode for reading the catalog file. */
    /*
    public void releaseCatalogFile() {
        catalogCache = null;
    }
    */
    
    public CommonHFSCatalogFolderRecord getRootFolder() {
        CatalogFileSession ses = openSession();

        // Search down through the layers of indices to the record with parentID 1.
        CommonHFSCatalogNodeID parentID =
                vol.getCommonHFSCatalogNodeID(ReservedID.ROOT_PARENT);
        final int nodeSize = ses.bthr.getNodeSize();
        long currentNodeOffset = ses.bthr.getRootNodeNumber() * ses.bthr.getNodeSize();

        //System.err.println("Got header record: ");
        //init.bthr.print(System.err, " ");

        byte[] currentNodeData = new byte[nodeSize];
        ses.catalogFile.seek(currentNodeOffset);
        ses.catalogFile.readFully(currentNodeData);
        CommonBTNodeDescriptor nodeDescriptor = createCommonBTNodeDescriptor(currentNodeData, 0);
        while(nodeDescriptor.getNodeType() == NodeType.INDEX) {
            CommonHFSCatalogIndexNode currentNode =
                    newCatalogIndexNode(currentNodeData, 0, ses.bthr.getNodeSize(), ses.bthr);
            //System.err.println("currentNode:");
            //currentNode.print(System.err, "  ");
            CommonBTIndexRecord matchingRecord = findKey(currentNode, parentID);

            //currentNodeNumber = matchingRecord.getIndex();
            currentNodeOffset = matchingRecord.getIndex()*nodeSize;
            ses.catalogFile.seek(currentNodeOffset);
            ses.catalogFile.readFully(currentNodeData);
            nodeDescriptor = createCommonBTNodeDescriptor(currentNodeData, 0);
        }

        // Leaf node reached. Find record with parent id 1. (or whatever value is in the parentID variable :) )
        if(nodeDescriptor.getNodeType() == NodeType.LEAF) {
            CommonHFSCatalogLeafNode leaf =
                    newCatalogLeafNode(currentNodeData, 0, nodeSize, ses.bthr);
            CommonHFSCatalogLeafRecord[] recs = leaf.getLeafRecords();
            for(CommonHFSCatalogLeafRecord rec : recs) {
                if(rec.getKey().getParentID().toLong() == parentID.toLong()) {
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
        CatalogFileSession ses = openSession();

        long currentNodeNumber;
        if(nodeNumber < 0) { // Means that we should get the root node
            currentNodeNumber = ses.bthr.getRootNodeNumber();
            if(currentNodeNumber == 0) // There is no index node, or other content. So the node we
                return null;           // seek does not exist. Return null.
        }
        else
            currentNodeNumber = nodeNumber;

        final int nodeSize = ses.bthr.getNodeSize();

        byte[] currentNodeData = new byte[nodeSize];
        try {
            ses.catalogFile.seek(currentNodeNumber * nodeSize);
            ses.catalogFile.readFully(currentNodeData);
        } catch(RuntimeException e) {
            System.err.println("RuntimeException in getCatalogNode. Printing additional information:");
            System.err.println("  nodeNumber=" + nodeNumber);
            System.err.println("  currentNodeNumber=" + currentNodeNumber);
            System.err.println("  nodeSize=" + nodeSize);
            System.err.println("  init.catalogFile.length()=" + ses.catalogFile.length());
            System.err.println("  (currentNodeNumber * nodeSize)=" + (currentNodeNumber * nodeSize));
            //System.err.println("  =" + );
            throw e;
        }
        CommonBTNodeDescriptor nodeDescriptor = createCommonBTNodeDescriptor(currentNodeData, 0);

        if(nodeDescriptor.getNodeType() == NodeType.HEADER)
            return createCommonBTHeaderNode(currentNodeData, 0, ses.bthr.getNodeSize());
        if(nodeDescriptor.getNodeType() == NodeType.INDEX)
            return newCatalogIndexNode(currentNodeData, 0, ses.bthr.getNodeSize(), ses.bthr);
        else if(nodeDescriptor.getNodeType() == NodeType.LEAF)
            return newCatalogLeafNode(currentNodeData, 0, ses.bthr.getNodeSize(), ses.bthr);
        else
            return null;
    }

    /**
     * Find the actual entry in the index node corresponding to the id parentID.
     * This can only be used to find the root node (parentID 1), since that node would
     * always exist in the root index node at position 0, and the same all the way
     * down the tree.
     * So this method is actually too complicated for its purpose. (: But whatever...
     * @return the corresponding index record if found, null otherwise
     */
    private static CommonBTIndexRecord<CommonHFSCatalogKey> findKey(CommonHFSCatalogIndexNode indexNode,
            CommonHFSCatalogNodeID parentID) {

	for(CommonBTIndexRecord<CommonHFSCatalogKey> rec : indexNode.getBTRecords()) {
	    CommonHFSCatalogKey key = rec.getKey();
            if(key.getParentID().toLong() == parentID.toLong())
                return rec;
	}
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
	CommonHFSCatalogLeafRecord leafRec = getRecord(leafID, vol.getEmptyString());
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
	    CommonHFSCatalogLeafRecord parent = getRecord(parentID, vol.getEmptyString()); // Look for the thread record associated with the parent dir
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

    /**
     * Gets a record from the catalog file's B* tree with the specified parent
     * ID and node name. If none is found, the method returns <code>null</code>.
     * <br>
     * If <code>n</code> is the number of elements in the tree, this method
     * should execute in O(log n) time.
     *
     * @param parentID the parent ID of the requested record.
     * @param nodeName the node name of the requested record.
     * @return the requested record, if any, or <code>null</code> if no such
     * record was found.
     */
    public CommonHFSCatalogLeafRecord getRecord(CommonHFSCatalogNodeID parentID,
            CommonHFSCatalogString nodeName) {
	CatalogFileSession ses = openSession();

	final int nodeSize = ses.bthr.getNodeSize();

	long currentNodeOffset = ses.bthr.getRootNodeNumber()*nodeSize;

	// Search down through the layers of indices (O(log n) steps, where n is the size of the tree)

	byte[] currentNodeData = new byte[ses.bthr.getNodeSize()];
	ses.catalogFile.seek(currentNodeOffset);
	ses.catalogFile.readFully(currentNodeData);
	CommonBTNodeDescriptor nodeDescriptor = createCommonBTNodeDescriptor(currentNodeData, 0);

	while(nodeDescriptor.getNodeType() == NodeType.INDEX) {
	    CommonHFSCatalogIndexNode currentNode =
                    newCatalogIndexNode(currentNodeData, 0, nodeSize, ses.bthr);
	    CommonBTIndexRecord matchingRecord =
                    findLEKey(currentNode, newCatalogKey(parentID, nodeName, ses.bthr));

            if(matchingRecord == null)
                return null;
	    currentNodeOffset = matchingRecord.getIndex()*nodeSize;
	    ses.catalogFile.seek(currentNodeOffset);
	    ses.catalogFile.readFully(currentNodeData);
	    nodeDescriptor = createCommonBTNodeDescriptor(currentNodeData, 0);
	}

	// Leaf node reached. Find record.
	if(nodeDescriptor.getNodeType() == NodeType.LEAF) {
	    CommonHFSCatalogLeafNode leaf = newCatalogLeafNode(currentNodeData, 0, ses.bthr.getNodeSize(), ses.bthr);
	    CommonHFSCatalogLeafRecord[] recs = leaf.getLeafRecords();
	    for(CommonHFSCatalogLeafRecord rec : recs)
		if(rec.getKey().compareTo(newCatalogKey(parentID, nodeName, ses.bthr)) == 0)
		    return rec;
	    return null;
	}
	else
	    throw new RuntimeException("Expected leaf node. Found other kind: " +
				       nodeDescriptor.getNodeType());
    }


    /**
     * More typesafe than <code>listRecords(HFSCatalogNodeID)</code> since it
     * checks that folderRecord is of appropriate type first.
     */
    public CommonHFSCatalogLeafRecord[] listRecords(CommonHFSCatalogLeafRecord folderRecord) {
	if(folderRecord instanceof CommonHFSCatalogFolderRecord) {
	    CommonHFSCatalogFolder folder = ((CommonHFSCatalogFolderRecord)folderRecord).getData();
	    return listRecords(folder.getFolderID());
	}
	else
	    throw new RuntimeException("Invalid input (not a folder record).");
    }

    /**
     * You should use the method above to access folder listings. However, the
     * folderID is really all that's needed, but make sure it's a folder ID and
     * not a file ID, or something bad will happen.
     */
    public CommonHFSCatalogLeafRecord[] listRecords(CommonHFSCatalogNodeID folderID) {
	CatalogFileSession init = openSession();
	final ReadableRandomAccessStream catalogFile = init.catalogFile;
	return collectFilesInDir(folderID, init.bthr.getRootNodeNumber(),
            new ReadableRandomAccessSubstream(vol.hfsFile), 0,
            init.header, init.bthr, catalogFile);
    }

    private CommonHFSCatalogLeafRecord[] collectFilesInDir(CommonHFSCatalogNodeID dirID,
            long currentNodeIndex, ReadableRandomAccessStream hfsFile, long fsOffset,
            final CommonHFSVolumeHeader header, final CommonBTHeaderRecord bthr,
            final ReadableRandomAccessStream catalogFile) {
        final int nodeSize = bthr.getNodeSize();

	byte[] currentNodeData = new byte[nodeSize];
	catalogFile.seek(currentNodeIndex*nodeSize);
	catalogFile.readFully(currentNodeData);

	CommonBTNodeDescriptor nodeDescriptor = createCommonBTNodeDescriptor(currentNodeData, 0);
	if(nodeDescriptor.getNodeType() == NodeType.INDEX) {
	    CommonBTIndexNode<CommonHFSCatalogKey> currentNode =
                    newCatalogIndexNode(currentNodeData, 0, nodeSize, bthr);
	    List<CommonBTIndexRecord<CommonHFSCatalogKey>> matchingRecords =
                    findLEChildKeys(currentNode, dirID);
	    //System.out.println("Matching records: " + matchingRecords.length);

	    LinkedList<CommonHFSCatalogLeafRecord> results =
                    new LinkedList<CommonHFSCatalogLeafRecord>();

	    for(CommonBTIndexRecord bir : matchingRecords) {
		CommonHFSCatalogLeafRecord[] partResult =
                        collectFilesInDir(dirID, bir.getIndex(), hfsFile, fsOffset,
                        header, bthr, catalogFile);
		for(CommonHFSCatalogLeafRecord curRes : partResult)
		    results.addLast(curRes);
	    }
	    return results.toArray(new CommonHFSCatalogLeafRecord[results.size()]);
	}
	else if(nodeDescriptor.getNodeType() == NodeType.LEAF) {
	    CommonHFSCatalogLeafNode currentNode =
                    newCatalogLeafNode(currentNodeData, 0, nodeSize, bthr);

	    return getChildrenTo(currentNode, dirID);
	}
	else
	    throw new RuntimeException("Illegal type for node! (" + nodeDescriptor.getNodeType() + ")");
    }

    private static List<CommonBTIndexRecord<CommonHFSCatalogKey>> findLEChildKeys(
            CommonBTIndexNode<CommonHFSCatalogKey> indexNode, CommonHFSCatalogNodeID rootFolderID) {

	LinkedList<CommonBTIndexRecord<CommonHFSCatalogKey>> result =
                new LinkedList<CommonBTIndexRecord<CommonHFSCatalogKey>>();

	//CommonBTIndexRecord records[] = indexNode.getIndexRecords();
	CommonBTIndexRecord<CommonHFSCatalogKey> largestMatchingRecord = null;//records[0];
	CommonHFSCatalogKey largestMatchingKey = null;
	for(CommonBTIndexRecord<CommonHFSCatalogKey> record : indexNode.getBTRecords()) {
            CommonHFSCatalogKey key = record.getKey();
            if(key.getParentID().toLong() < rootFolderID.toLong() &&
                    (largestMatchingKey == null || key.compareTo(largestMatchingKey) > 0)) {
                largestMatchingKey = key;
                largestMatchingRecord = record;
            }
            else if(key.getParentID().toLong() == rootFolderID.toLong())
                result.addLast(record);
	}

	if(largestMatchingKey != null)
	    result.addFirst(largestMatchingRecord);
	return result;
    }

    private static CommonHFSCatalogLeafRecord[] getChildrenTo(CommonHFSCatalogLeafNode leafNode,
            CommonHFSCatalogNodeID nodeID) {
	LinkedList<CommonHFSCatalogLeafRecord> children = new LinkedList<CommonHFSCatalogLeafRecord>();
	CommonHFSCatalogLeafRecord[] records = leafNode.getLeafRecords();
	for(int i = 0; i < records.length; ++i) {
	    CommonHFSCatalogLeafRecord curRec = records[i];
	    if(curRec.getKey().getParentID().toLong() == nodeID.toLong())
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

    protected CommonHFSCatalogIndexNode newCatalogIndexNode(byte[] data,
            int offset, int nodeSize, CommonBTHeaderRecord bthr) {
        return ops.newCatalogIndexNode(data, offset, nodeSize, bthr);
    }

    protected CommonHFSCatalogKey newCatalogKey(CommonHFSCatalogNodeID nodeID,
            CommonHFSCatalogString searchString, CommonBTHeaderRecord bthr) {
        return ops.newCatalogKey(nodeID, searchString, bthr);
    }

    protected CommonHFSCatalogLeafNode newCatalogLeafNode(byte[] data,
            int offset, int nodeSize, CommonBTHeaderRecord bthr) {
        return ops.newCatalogLeafNode(data, offset, nodeSize, bthr);
    }

    protected CommonHFSCatalogLeafRecord newCatalogLeafRecord(byte[] data,
            int offset, CommonBTHeaderRecord bthr) {
        return ops.newCatalogLeafRecord(data, offset, bthr);
    }
}
