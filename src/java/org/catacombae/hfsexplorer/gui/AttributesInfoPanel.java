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
import org.catacombae.hfs.AttributesFile;
import org.catacombae.hfs.BTreeFile;
import org.catacombae.hfs.HFSVolume;
import org.catacombae.hfs.types.hfscommon.CommonBTIndexRecord;
import org.catacombae.hfs.types.hfscommon.CommonBTNode;
import org.catacombae.hfs.types.hfscommon.CommonHFSAttributesIndexNode;
import org.catacombae.hfs.types.hfscommon.CommonHFSAttributesKey;
import org.catacombae.hfs.types.hfscommon.CommonHFSAttributesLeafNode;
import org.catacombae.hfs.types.hfscommon.CommonHFSAttributesLeafRecord;
import org.catacombae.hfsexplorer.FileSystemBrowser.NoLeafMutableTreeNode;
import org.catacombae.hfsexplorer.gui.BTreeInfoPanel.BTLeafStorage;
import org.catacombae.util.Util.Pair;

/**
 * @author <a href="http://www.catacombae.org/" target="_top">Erik Larsson</a>
 */
public class AttributesInfoPanel
        extends BTreeInfoPanel<CommonHFSAttributesLeafRecord, AttributesFile>
{
    /** Creates new form CatalogInfoPanel */
    public AttributesInfoPanel(final AttributesFile attributesFile) {
        super(attributesFile);
    }

    public String getRootNodeName() {
        return "Attributes root";
    }

    public String getHeaderText() {
        return "View of the attributes file's B*-tree:";
    }

    public void createCustomPanels(List<Pair<JPanel, String>> customPanelsList)
    {
        /* No custom panels are implemented for the attributes file. */
    }

    public void expandNode(DefaultMutableTreeNode dmtn, CommonBTNode node,
            AttributesFile attributesFile)
    {
        if(node instanceof CommonHFSAttributesIndexNode) {
            List<CommonBTIndexRecord<CommonHFSAttributesKey>> recs =
                    ((CommonHFSAttributesIndexNode) node).getBTRecords();
            for(CommonBTIndexRecord<CommonHFSAttributesKey> rec : recs) {

                final long nodeNumber = rec.getIndex();
                final CommonBTNode curNode = attributesFile.getNode(nodeNumber);
                CommonHFSAttributesKey key = rec.getKey();
                dmtn.add(new NoLeafMutableTreeNode(new BTNodeStorage(nodeNumber,
                        curNode, key.getFileID().toLong() + ":" +
                        new String(key.getAttrName()) + ":" +
                        key.getStartBlock())));
            }
        }
        else if(node instanceof CommonHFSAttributesLeafNode) {
            CommonHFSAttributesLeafNode leafNode =
                    (CommonHFSAttributesLeafNode) node;
            CommonHFSAttributesLeafRecord[] recs = leafNode.getLeafRecords();
            int[] recordOffsets = leafNode.getRecordOffsets();

            for(int i = 0; i < recs.length; ++i) {
                final CommonHFSAttributesLeafRecord rec = recs[i];
                CommonHFSAttributesKey key = rec.getKey();
                dmtn.add(new DefaultMutableTreeNode(new BTLeafStorage(i,
                        recordOffsets[i],
                        recordOffsets[i + 1] - recordOffsets[i], rec,
                        key.getFileID().toLong() + ":" +
                        new String(key.getAttrName()) + ":" +
                        key.getStartBlock())));
            }
        }
        else
            throw new RuntimeException("Invalid node type in tree: " + node);
    }

    public boolean handleLeafRecord(BTLeafStorage leafStorage) {
        /* No custom panels are implemented for the attributes file, so no
         * special handling is needed for leaf records. */
        return false;
    }
}
