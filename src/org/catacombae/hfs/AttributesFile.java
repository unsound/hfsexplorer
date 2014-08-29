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
import org.catacombae.hfs.types.hfscommon.CommonBTNode;
import org.catacombae.hfs.types.hfscommon.CommonBTNodeDescriptor;
import org.catacombae.hfs.types.hfscommon.CommonBTNodeDescriptor.NodeType;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogNodeID.ReservedID;
import org.catacombae.hfs.types.hfscommon.CommonHFSExtentDescriptor;
import org.catacombae.hfs.types.hfscommon.CommonHFSVolumeHeader;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.hfs.plus.HFSPlusVolume;
import org.catacombae.hfs.types.hfscommon.CommonBTHeaderNode;
import org.catacombae.hfs.types.hfscommon.CommonHFSAttributesIndexNode;
import org.catacombae.hfs.types.hfscommon.CommonHFSAttributesLeafNode;

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
    }

    public AttributesFile(HFSPlusVolume view) {
        super(view);
        this.view = view;
    }

    private ReadableRandomAccessStream getAttributesFileStream(
            CommonHFSVolumeHeader.HFSPlusImplementation header) {

        return new ForkFilter(ForkFilter.ForkType.DATA,
                vol.getCommonHFSCatalogNodeID(ReservedID.ATTRIBUTES_FILE).
                toLong(),
                header.getAttributesFile(),
                vol.extentsOverflowFile,
                view.createFSStream(),
                0,
                header.getAllocationBlockSize(),
                header.getAllocationBlockStart() * view.getPhysicalBlockSize());
    }

    protected BTreeFileSession openSession() {
        return new Session();
    }

    protected CommonBTNode createIndexNode(byte[] nodeData, int offset,
            int nodeSize)
    {
        return CommonHFSAttributesIndexNode.createHFSPlus(nodeData, 0, nodeSize);
    }

    protected CommonBTNode createLeafNode(byte[] nodeData, int offset,
            int nodeSize)
    {
        return CommonHFSAttributesLeafNode.createHFSPlus(nodeData, 0, nodeSize);
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
}
