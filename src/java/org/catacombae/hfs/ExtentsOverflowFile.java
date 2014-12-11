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
import org.catacombae.hfs.io.ForkFilter;
import org.catacombae.hfs.types.hfscommon.CommonBTHeaderNode;
import org.catacombae.io.ReadableRandomAccessSubstream;
import org.catacombae.hfs.types.hfscommon.CommonBTIndexRecord;
import org.catacombae.hfs.types.hfscommon.CommonBTKeyedNode;
import org.catacombae.hfs.types.hfscommon.CommonBTNode;
import org.catacombae.hfs.types.hfscommon.CommonBTNodeDescriptor;
import org.catacombae.hfs.types.hfscommon.CommonBTNodeDescriptor.NodeType;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogFile;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogFileRecord;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogLeafRecord;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogNodeID;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogNodeID.ReservedID;
import org.catacombae.hfs.types.hfscommon.CommonHFSExtentDescriptor;
import org.catacombae.hfs.types.hfscommon.CommonHFSExtentIndexNode;
import org.catacombae.hfs.types.hfscommon.CommonHFSExtentKey;
import org.catacombae.hfs.types.hfscommon.CommonHFSExtentLeafNode;
import org.catacombae.hfs.types.hfscommon.CommonHFSExtentLeafRecord;
import org.catacombae.hfs.types.hfscommon.CommonHFSForkData;
import org.catacombae.hfs.types.hfscommon.CommonHFSForkType;
import org.catacombae.hfs.types.hfscommon.CommonHFSVolumeHeader;
import org.catacombae.io.ReadableRandomAccessStream;

/**
 * @author <a href="http://www.catacombae.org/" target="_top">Erik Larsson</a>
 */
public class ExtentsOverflowFile
    extends BTreeFile<CommonHFSExtentKey, CommonHFSExtentLeafRecord>
{
    ExtentsOverflowFile(HFSVolume vol) {
        super(vol);
    }

    class ExtentsOverflowFileSession extends BTreeFileSession {
        @Override
        protected ReadableRandomAccessStream getBTreeStream(
                CommonHFSVolumeHeader header) {
            return new ForkFilter(ForkFilter.ForkType.DATA,
                    vol.getCommonHFSCatalogNodeID(ReservedID.EXTENTS_FILE).
                    toLong(),
                    header.getExtentsOverflowFile(),
                    null,
                    new ReadableRandomAccessSubstream(vol.hfsFile),
                    0,
                    header.getAllocationBlockSize(),
                    header.getAllocationBlockStart()*vol.physicalBlockSize);
        }
    }

    protected BTreeFileSession openSession() {
        return new ExtentsOverflowFileSession();
    }

    protected CommonHFSExtentIndexNode createIndexNode(byte[] nodeData,
            int offset, int nodeSize)
    {
        return createCommonHFSExtentIndexNode(nodeData, 0, nodeSize);
    }

    protected CommonHFSExtentLeafNode createLeafNode(byte[] nodeData,
            int offset, int nodeSize)
    {
        return createCommonHFSExtentLeafNode(nodeData, 0, nodeSize);
    }

    public CommonBTHeaderNode getHeaderNode() {
        CommonBTNode firstNode = getNode(0);
        if(firstNode instanceof CommonBTHeaderNode) {
            return (CommonBTHeaderNode) firstNode;
        }
        else {
            throw new RuntimeException("Unexpected node type at catalog node " +
                    "0: " + firstNode.getClass());
        }
    }

    public CommonHFSExtentLeafRecord getOverflowExtent(CommonHFSExtentKey key) {
	//System.err.println("getOverflowExtent(..)");
	//System.err.println("my key:");
	//key.printFields(System.err, "");
        //System.err.println("  Doing ExtentsInitProcedure...");
        BTreeFileSession init = openSession();
        //System.err.println("  ExtentsInitProcedure done!");

	final int nodeSize = init.bthr.getNodeSize();

	long currentNodeOffset = init.bthr.getRootNodeNumber()*nodeSize;

	// Search down through the layers of indices (O(log n) steps, where n is the size of the tree)

	final byte[] currentNodeData = new byte[nodeSize];
        init.btreeStream.seek(currentNodeOffset);
        init.btreeStream.readFully(currentNodeData);
        //System.err.println("  Calling createCommonBTNodeDescriptor(byte[" + currentNodeData.length + "], 0)...");
	CommonBTNodeDescriptor nodeDescriptor = createCommonBTNodeDescriptor(currentNodeData, 0);

	while(nodeDescriptor.getNodeType() == NodeType.INDEX) {
            //System.err.println("getOverflowExtent(): Processing index node...");
	    CommonBTKeyedNode<CommonBTIndexRecord<CommonHFSExtentKey>>
                    currentNode =
                    createCommonHFSExtentIndexNode(currentNodeData, 0,
                    nodeSize);

	    CommonBTIndexRecord<CommonHFSExtentKey> matchingRecord = findLEKey(currentNode, key);
            //System.err.println("getOverflowExtent(): findLEKey found a child node with key: " +
            //        getDebugString(matchingRecord.getKey()));
            //matchingRecord.getKey().printFields(System.err, "getOverflowExtent():   ");

	    currentNodeOffset = matchingRecord.getIndex()*nodeSize;
            init.btreeStream.seek(currentNodeOffset);
            init.btreeStream.readFully(currentNodeData);
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

    public CommonHFSExtentLeafRecord getOverflowExtent(boolean isResource,
            int cnid, long startBlock)
    {
        return getOverflowExtent(vol.createCommonHFSExtentKey(isResource, cnid,
                startBlock));
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

        return vol.createCommonHFSExtentIndexNode(currentNodeData,
                offset, nodeSize);
    }

    protected CommonHFSExtentLeafNode createCommonHFSExtentLeafNode(
            byte[] currentNodeData, int offset, int nodeSize) {

        return vol.createCommonHFSExtentLeafNode(currentNodeData, offset,
                nodeSize);
    }

    protected CommonHFSExtentKey createCommonHFSExtentKey(
            CommonHFSForkType forkType, CommonHFSCatalogNodeID fileID,
            int startBlock) {

        return vol.createCommonHFSExtentKey(forkType, fileID, startBlock);
    }

}
