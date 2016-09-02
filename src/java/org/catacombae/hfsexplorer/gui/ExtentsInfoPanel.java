/*-
 * Copyright (C) 2007-2014 Erik Larsson
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

package org.catacombae.hfsexplorer.gui;

import java.util.List;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import org.catacombae.hfsexplorer.FileSystemBrowser.NoLeafMutableTreeNode;
import org.catacombae.hfs.ExtentsOverflowFile;
import org.catacombae.hfs.types.hfscommon.CommonBTIndexRecord;
import org.catacombae.hfs.types.hfscommon.CommonBTNode;
import org.catacombae.hfs.types.hfscommon.CommonHFSExtentIndexNode;
import org.catacombae.hfs.types.hfscommon.CommonHFSExtentKey;
import org.catacombae.hfs.types.hfscommon.CommonHFSExtentLeafNode;
import org.catacombae.hfs.types.hfscommon.CommonHFSExtentLeafRecord;
import org.catacombae.hfs.HFSVolume;
import org.catacombae.hfsexplorer.gui.BTreeInfoPanel.BTLeafStorage;
import org.catacombae.util.Util.Pair;

/**
 * @author <a href="http://www.catacombae.org/" target="_top">Erik Larsson</a>
 */
public class ExtentsInfoPanel
        extends BTreeInfoPanel<CommonHFSExtentLeafRecord, ExtentsOverflowFile>
{
    /** Creates new form CatalogInfoPanel */
    public ExtentsInfoPanel(final HFSVolume fsView) {
        super(fsView.getExtentsOverflowFile());
    }

    protected String getRootNodeName() {
        return "Extents overflow root";
    }

    protected String getHeaderText() {
        return "View of the extent overflow file's B*-tree:";
    }

    protected void createCustomPanels(List<Pair<JPanel, String>> panelsList)
    {
        /* No custom panels are implemented for the extents overflow file. */
    }

    protected void expandNode(DefaultMutableTreeNode dmtn, CommonBTNode node,
            ExtentsOverflowFile extentsOverflowFile)
    {
        if(node instanceof CommonHFSExtentIndexNode) {
            List<CommonBTIndexRecord<CommonHFSExtentKey>> recs = ((CommonHFSExtentIndexNode) node).getBTRecords();
            for(CommonBTIndexRecord<CommonHFSExtentKey> rec : recs) {

                final long nodeNumber = rec.getIndex();
                final CommonBTNode curNode =
                        extentsOverflowFile.getNode(nodeNumber);
                CommonHFSExtentKey key = rec.getKey();
                dmtn.add(new NoLeafMutableTreeNode(new BTNodeStorage(nodeNumber,
                        curNode, key.getFileID().toLong() + ":" +
                        key.getForkType() + ":" + key.getStartBlock())));
            }
        }
        else if(node instanceof CommonHFSExtentLeafNode) {
            CommonHFSExtentLeafNode leafNode = (CommonHFSExtentLeafNode) node;
            CommonHFSExtentLeafRecord[] recs = leafNode.getLeafRecords();
            int[] recordOffsets = leafNode.getRecordOffsets();

            for(int i = 0; i < recs.length; ++i) {
                final CommonHFSExtentLeafRecord rec = recs[i];
                CommonHFSExtentKey key = rec.getKey();
                dmtn.add(new DefaultMutableTreeNode(new BTLeafStorage(i,
                        recordOffsets[i],
                        recordOffsets[i + 1] - recordOffsets[i], rec,
                        key.getFileID().toLong() + ":" + key.getForkType() +
                        ":" + key.getStartBlock())));
            }
        }
        else
            throw new RuntimeException("Invalid node type in tree: " + node);
    }

    protected boolean handleLeafRecord(BTLeafStorage leafStorage) {
        /* No custom panels are implemented for the extents overflow file, so no
         * special handling is needed for leaf records. */
        return false;
    }
}
