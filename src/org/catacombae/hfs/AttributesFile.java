/*-
 * Copyright (C) 2009 Erik Larsson
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

import org.catacombae.hfs.io.ForkFilter;
import org.catacombae.hfs.types.hfscommon.CommonBTHeaderRecord;
import org.catacombae.hfs.types.hfscommon.CommonBTNode;
import org.catacombae.hfs.types.hfscommon.CommonBTNodeDescriptor;
import org.catacombae.hfs.types.hfscommon.CommonBTNodeDescriptor.NodeType;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogNodeID.ReservedID;
import org.catacombae.hfs.types.hfscommon.CommonHFSExtentDescriptor;
import org.catacombae.hfs.types.hfscommon.CommonHFSVolumeHeader;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.hfs.plus.HFSPlusVolume;
import org.catacombae.hfs.types.hfscommon.CommonHFSAttributesIndexNode;
import org.catacombae.hfs.types.hfscommon.CommonHFSAttributesLeafNode;
import org.catacombae.hfs.types.hfsplus.BTHeaderRec;

/**
 *
 * @author erik
 */
public class AttributesFile extends BTreeFile {
    private final HFSPlusVolume view;

    private class Session extends BTreeFileSession {
        ReadableRandomAccessStream attributesFileStream;

        protected ReadableRandomAccessStream getBTreeStream(
                CommonHFSVolumeHeader header)
        {
            if(!(header instanceof CommonHFSVolumeHeader.HFSPlusImplementation))
            {
                throw new RuntimeException("Illegal CommonHFSVolumeHeader " +
                        "flavour (expected HFSPlusImplementation, got " +
                        header.getClass() + ").");
            }

            if(this.attributesFileStream == null) {
                this.attributesFileStream = getAttributesFileStream(
                        (CommonHFSVolumeHeader.HFSPlusImplementation) header);
            }

            return this.attributesFileStream;
        }

        private void close() {
            if(attributesFileStream != null)
                attributesFileStream.close();
        }
    }

    public AttributesFile(HFSPlusVolume view, BTreeOperations ops) {
        super(view, ops);
        this.view = view;
    }

    private ReadableRandomAccessStream getAttributesFileStream(
            CommonHFSVolumeHeader.HFSPlusImplementation header) {

        CommonHFSExtentDescriptor[] allExtents =
                view.getExtentsOverflowFile().getAllDataExtentDescriptors(
                view.getCommonHFSCatalogNodeID(ReservedID.ATTRIBUTES_FILE),
                header.getAttributesFile());

        return new ForkFilter(
                header.getAttributesFile(),
                allExtents, view.createFSStream(),
                0,
                header.getAllocationBlockSize(),
                header.getAllocationBlockStart() * view.getPhysicalBlockSize());
    }

    private Session openSession() {
        return new Session();
    }

    /**
     * Returns the B-tree root node of the attributes file. If it does not exist
     * <code>null</code> is returned. The attributes file will have no
     * meaningful content if there is no root node.
     *
     * @return the B-tree root node of the attributes file.
     */
    public CommonBTNode getRootNode() {
        Session ses = openSession();

        try {
            long rootNode = ses.bthr.getRootNodeNumber();

            if(rootNode == 0) {
                // There is no index node, or other content. So the node we
                // seek does not exist. Return null.
                return null;
            }
            else if(rootNode < 0 || rootNode > Integer.MAX_VALUE * 2L) {
                throw new RuntimeException("Internal error - rootNode out of " +
                        "range: " + rootNode);
            }
            else
                return getNodeInternal(rootNode, ses);

        } finally {
            ses.close();
        }
    }

    public CommonBTNode getNode(long nodeNumber) {

        if(nodeNumber < 0) {
            throw new IllegalArgumentException("Invalid node number: " +
                    nodeNumber);
        }

        final Session ses = openSession();
        try {
            return getNodeInternal(nodeNumber, ses);
        } finally {
            ses.close();
        }
    }

    private CommonBTNode getNodeInternal(long nodeNumber, Session ses) {
        final String METHOD = "getNodeInternal";
        final int nodeSize = ses.bthr.getNodeSize();

        byte[] nodeData = new byte[nodeSize];
        try {
            ses.btreeStream.seek(nodeNumber * nodeSize);
            ses.btreeStream.readFully(nodeData);
        } catch(RuntimeException e) {
            System.err.println("RuntimeException in " + METHOD + ". " +
                    "Printing additional information:");
            System.err.println("  nodeNumber=" + nodeNumber);
            System.err.println("  nodeSize=" + nodeSize);
            System.err.println("  init.btreeStream.length()=" +
                    ses.btreeStream.length());
            System.err.println("  (currentNodeNumber * nodeSize)=" +
                    (nodeNumber * nodeSize));
            throw e;
        }

        CommonBTNodeDescriptor nodeDescriptor =
                createCommonBTNodeDescriptor(nodeData, 0);

        if(nodeDescriptor.getNodeType() == NodeType.HEADER)
            return createCommonBTHeaderNode(nodeData, 0, nodeSize);
        else if(nodeDescriptor.getNodeType() == NodeType.INDEX)
            return CommonHFSAttributesIndexNode.createHFSPlus(nodeData, 0, nodeSize);
        else if(nodeDescriptor.getNodeType() == NodeType.LEAF)
            return CommonHFSAttributesLeafNode.createHFSPlus(nodeData, 0,
                    nodeSize);
        else
            return null;
    }
}
