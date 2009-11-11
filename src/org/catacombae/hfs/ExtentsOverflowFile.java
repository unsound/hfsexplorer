/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.storage.fs.hfs;

import java.util.LinkedList;
import org.catacombae.hfsexplorer.io.ForkFilter;
import org.catacombae.hfsexplorer.io.ReadableRandomAccessSubstream;
import org.catacombae.hfsexplorer.types.hfscommon.CommonBTIndexNode;
import org.catacombae.hfsexplorer.types.hfscommon.CommonBTIndexRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonBTNode;
import org.catacombae.hfsexplorer.types.hfscommon.CommonBTNodeDescriptor;
import org.catacombae.hfsexplorer.types.hfscommon.CommonBTNodeDescriptor.NodeType;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogFile;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogFileRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogLeafRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogNodeID;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSExtentDescriptor;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSExtentIndexNode;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSExtentKey;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSExtentLeafNode;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSExtentLeafRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSForkData;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSForkType;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSVolumeHeader;
import org.catacombae.io.ReadableRandomAccessStream;

/**
 *
 * @author erik
 */
public class ExtentsOverflowFile extends BTreeFile {
    private final ExtentsOverflowOperations ops;

    ExtentsOverflowFile(HFSVolume vol, BTreeOperations superOps,
            ExtentsOverflowOperations ops) {
        super(vol, superOps);

        this.ops = ops;
    }

    class ExtentsOverflowFileSession extends BTreeFileSession {
        final ReadableRandomAccessStream extentsFile;

        public ExtentsOverflowFileSession() {
            this.extentsFile = btreeStream;
        }

        public void close() {}

        @Override
        protected ReadableRandomAccessStream getBTreeStream(
                CommonHFSVolumeHeader header) {
            return new ForkFilter(header.getExtentsOverflowFile(),
                    header.getExtentsOverflowFile().getBasicExtents(),
                    new ReadableRandomAccessSubstream(vol.hfsFile),
                    vol.fsOffset,
                    header.getAllocationBlockSize(),
                    header.getAllocationBlockStart()*vol.physicalBlockSize);
        }
    }

    ExtentsOverflowFileSession openSession() {
        return new ExtentsOverflowFileSession();
    }

    /**
     * Returns extents overflow node number <code>nodeNumber</code> Node number 0 is always the
     * B*-tree header node. The node numbers of the rest of the node are determined by the contents
     * of the header node.<br>
     * A value of -1 for nodeNumber is special and means that the root index node should be
     * retrieved. If the root index node does not exist, null is returned.
     *
     * @param nodeNumber
     * @return
     */
    public CommonBTNode getExtentsOverflowNode(long nodeNumber) {
        ExtentsOverflowFileSession init = openSession();

        long currentNodeNumber;
        if(nodeNumber < 0) { // Means that we should get the root index node
            currentNodeNumber = init.bthr.getRootNodeNumber();
            if(currentNodeNumber == 0) // There is no index node, or other content. So the node we
                return null;           // seek does not exist. Return null.
        }
        else
            currentNodeNumber = nodeNumber;

        final int nodeSize = init.bthr.getNodeSize();

        byte[] currentNodeData = new byte[nodeSize];
        try {
            init.extentsFile.seek(currentNodeNumber * nodeSize);
            init.extentsFile.readFully(currentNodeData);
        } catch(RuntimeException e) {
            System.err.println("RuntimeException in getCatalogNode. Printing additional information:");
            System.err.println("  nodeNumber=" + nodeNumber);
            System.err.println("  currentNodeNumber=" + currentNodeNumber);
            System.err.println("  nodeSize=" + nodeSize);
            System.err.println("  init.extentsFile.length()=" + init.extentsFile.length());
            System.err.println("  (currentNodeNumber * nodeSize)=" + (currentNodeNumber * nodeSize));
            //System.err.println("  =" + );
            throw e;
        }
        CommonBTNodeDescriptor nodeDescriptor = createCommonBTNodeDescriptor(currentNodeData, 0);

        if(nodeDescriptor.getNodeType() == NodeType.HEADER)
            return createCommonBTHeaderNode(currentNodeData, 0, nodeSize);
        if(nodeDescriptor.getNodeType() == NodeType.INDEX)
            return createCommonHFSExtentIndexNode(currentNodeData, 0, nodeSize);
        else if(nodeDescriptor.getNodeType() == NodeType.LEAF)
            return createCommonHFSExtentLeafNode(currentNodeData, 0, nodeSize);
        else
            return null;
    }

    public CommonHFSExtentLeafRecord getOverflowExtent(CommonHFSExtentKey key) {
	//System.err.println("getOverflowExtent(..)");
	//System.err.println("my key:");
	//key.printFields(System.err, "");
        //System.err.println("  Doing ExtentsInitProcedure...");
	ExtentsOverflowFileSession init = openSession();
        //System.err.println("  ExtentsInitProcedure done!");

	final int nodeSize = init.bthr.getNodeSize();

	long currentNodeOffset = init.bthr.getRootNodeNumber()*nodeSize;

	// Search down through the layers of indices (O(log n) steps, where n is the size of the tree)

	final byte[] currentNodeData = new byte[nodeSize];
	init.extentsFile.seek(currentNodeOffset);
	init.extentsFile.readFully(currentNodeData);
        //System.err.println("  Calling createCommonBTNodeDescriptor(byte[" + currentNodeData.length + "], 0)...");
	CommonBTNodeDescriptor nodeDescriptor = createCommonBTNodeDescriptor(currentNodeData, 0);

	while(nodeDescriptor.getNodeType() == NodeType.INDEX) {
            //System.err.println("getOverflowExtent(): Processing index node...");
	    CommonBTIndexNode<CommonHFSExtentKey> currentNode = createCommonHFSExtentIndexNode(currentNodeData, 0, nodeSize);

	    CommonBTIndexRecord<CommonHFSExtentKey> matchingRecord = findLEKey(currentNode, key);
            //System.err.println("getOverflowExtent(): findLEKey found a child node with key: " +
            //        getDebugString(matchingRecord.getKey()));
            //matchingRecord.getKey().printFields(System.err, "getOverflowExtent():   ");

	    currentNodeOffset = matchingRecord.getIndex()*nodeSize;
	    init.extentsFile.seek(currentNodeOffset);
	    init.extentsFile.readFully(currentNodeData);
            //System.err.println("  Calling createCommonBTNodeDescriptor(byte[" + currentNodeData.length + "], 0)...");
	    nodeDescriptor = createCommonBTNodeDescriptor(currentNodeData, 0);
	}

	// Leaf node reached. Find record.
	if(nodeDescriptor.getNodeType() == NodeType.LEAF) {
	    CommonHFSExtentLeafNode leaf = createCommonHFSExtentLeafNode(currentNodeData, 0, nodeSize);
            //System.err.println("getOverflowExtent(): Processing leaf node...");
	    CommonHFSExtentLeafRecord[] recs = leaf.getLeafRecords();
	    for(CommonHFSExtentLeafRecord rec : recs) {
                CommonHFSExtentKey curKey = rec.getKey();
                //System.err.print("getOverflowExtent(): checking how " + getDebugString(curKey));
                //System.err.print(" compares to " + getDebugString(key));
                //System.err.println("...");
		if(curKey.compareTo(key) == 0)
		    return rec;
	    }
// 	    try {
// 		java.io.FileOutputStream dataDump = new java.io.FileOutputStream("node_dump.dmp");
// 		dataDump.write(currentNodeData);
// 		dataDump.close();
// 		System.err.println("A dump of the node has been written to node_dump.dmp");
// 	    } catch(Exception e) { e.printStackTrace(); }
            //System.err.println("Returning from getOverflowExtent(..)");
	    return null;
	}
	else
	    throw new RuntimeException("Expected leaf node. Found other kind: " +
				       nodeDescriptor.getNodeType());
    }

    public CommonHFSExtentDescriptor[] getAllExtents(CommonHFSCatalogNodeID fileID,
            CommonHFSForkData forkData, CommonHFSForkType forkType) {
        if(fileID == null)
            throw new IllegalArgumentException("fileID == null");
        if(forkData == null)
            throw new IllegalArgumentException("forkData == null");
        if(forkType == null)
            throw new IllegalArgumentException("forkType == null");

        CommonHFSExtentDescriptor[] result;
        long allocationBlockSize = vol.getVolumeHeader().getAllocationBlockSize();

        long basicExtentsBlockCount = 0;
        {
            CommonHFSExtentDescriptor[] basicExtents = forkData.getBasicExtents();
            for(int i = 0; i < basicExtents.length; ++i)
                basicExtentsBlockCount += basicExtents[i].getBlockCount();
        }

        if(basicExtentsBlockCount * allocationBlockSize >= forkData.getLogicalSize()) {
            result = forkData.getBasicExtents();
        }
        else {
            //System.err.println("Reading overflow extent for file " + fileID.toString());
            LinkedList<CommonHFSExtentDescriptor> resultList = new LinkedList<CommonHFSExtentDescriptor>();
            for(CommonHFSExtentDescriptor descriptor : forkData.getBasicExtents())
                resultList.add(descriptor);
            long totalBlockCount = basicExtentsBlockCount;

            while(totalBlockCount * allocationBlockSize < forkData.getLogicalSize()) {
                CommonHFSExtentKey extentKey =
                        createCommonHFSExtentKey(forkType, fileID, (int) totalBlockCount);

                CommonHFSExtentLeafRecord currentRecord = getOverflowExtent(extentKey);
                if(currentRecord == null) {
                    System.err.println("ERROR: currentRecord == null!!");
                    System.err.print(  "       extentKey");
                    if(extentKey != null) {
                        System.err.println(":");
                        extentKey.print(System.err, "         ");
                    }
                    else
                        System.err.println(" == null!!");
                }
                CommonHFSExtentDescriptor[] currentRecordData = currentRecord.getRecordData();
                for(CommonHFSExtentDescriptor cur : currentRecordData) {
                    resultList.add(cur);
                    totalBlockCount += cur.getBlockCount();
                }
            }
            //System.err.println("  Finished reading extents... (currentblock: " + currentBlock + " total: " + forkData.getTotalBlocks() + ")");

            result = resultList.toArray(new CommonHFSExtentDescriptor[resultList.size()]);
        }
        return result;
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

    // Flavour specific operations:

    protected CommonHFSExtentIndexNode createCommonHFSExtentIndexNode(
            byte[] currentNodeData, int offset, int nodeSize) {

        return ops.createCommonHFSExtentIndexNode(currentNodeData,
                offset, nodeSize);
    }

    protected CommonHFSExtentLeafNode createCommonHFSExtentLeafNode(
            byte[] currentNodeData, int offset, int nodeSize) {

        return ops.createCommonHFSExtentLeafNode(currentNodeData, offset,
                nodeSize);
    }

    protected CommonHFSExtentKey createCommonHFSExtentKey(
            CommonHFSForkType forkType, CommonHFSCatalogNodeID fileID,
            int startBlock) {
        
        return ops.createCommonHFSExtentKey(forkType, fileID, startBlock);
    }

}
