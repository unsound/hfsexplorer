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

import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import org.catacombae.hfs.CatalogFile;
import org.catacombae.hfs.HFSVolume;
import org.catacombae.hfs.types.hfscommon.CommonBTIndexRecord;
import org.catacombae.hfs.types.hfscommon.CommonBTLeafRecord;
import org.catacombae.hfs.types.hfscommon.CommonBTNode;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogFile;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogFileRecord;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogFolder;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogFolderRecord;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogIndexNode;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogKey;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogLeafNode;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogLeafRecord;
import org.catacombae.hfs.types.hfsplus.HFSPlusCatalogFile;
import org.catacombae.hfs.types.hfsplus.HFSPlusCatalogFolder;
import org.catacombae.hfsexplorer.FileSystemBrowser.NoLeafMutableTreeNode;
import org.catacombae.util.Util.Pair;

/**
 * @author <a href="https://catacombae.org" target="_top">Erik Larsson</a>
 */
public class CatalogInfoPanel
        extends BTreeInfoPanel<CommonHFSCatalogLeafRecord, CatalogFile>
{
    private final String FILE_NAME = "file";
    private final String FOLDER_NAME = "folder";

    private LeafInfoPanel fileInfoPanelHeader;
    private FileInfoPanel fileInfoPanel;
    private LeafInfoPanel folderInfoPanelHeader;
    private FolderInfoPanel folderInfoPanel;

    public CatalogInfoPanel(HFSVolume vol) {
        super(vol.getCatalogFile());
    }

    protected String getRootNodeName() {
        return "Catalog root";
    }

    protected String getHeaderText() {
        return "View of the catalog file's B*-tree:";
    }

    protected void createCustomPanels(List<Pair<JPanel, String>> panelsList) {
        fileInfoPanelHeader = new LeafInfoPanel();
        fileInfoPanel = new FileInfoPanel();
        final JPanel fileInfoPanelContainer =
                new JPanel(new BorderLayout());
        fileInfoPanelContainer.add(fileInfoPanelHeader, BorderLayout.NORTH);
        fileInfoPanelContainer.add(fileInfoPanel, BorderLayout.CENTER);
        panelsList.add(new Pair<JPanel, String>(fileInfoPanelContainer,
                FILE_NAME));

        folderInfoPanelHeader = new LeafInfoPanel();
        folderInfoPanel = new FolderInfoPanel();
        final JPanel folderInfoPanelContainer =
                new JPanel(new BorderLayout());
        folderInfoPanelContainer.add(folderInfoPanelHeader,
                BorderLayout.NORTH);
        folderInfoPanelContainer.add(folderInfoPanel, BorderLayout.CENTER);
        panelsList.add(new Pair<JPanel, String>(folderInfoPanelContainer,
                FOLDER_NAME));
    }

    protected void expandNode(DefaultMutableTreeNode dmtn, CommonBTNode node,
            CatalogFile catalogFile)
    {
        if(node instanceof CommonHFSCatalogIndexNode) {
            List<CommonBTIndexRecord<CommonHFSCatalogKey>> recs =
                    ((CommonHFSCatalogIndexNode) node).getBTRecords();
            for(CommonBTIndexRecord<CommonHFSCatalogKey> rec : recs) {

                final long nodeNumber = rec.getIndex();
                final CommonBTNode curNode =
                        catalogFile.getNode(nodeNumber);
                CommonHFSCatalogKey key = rec.getKey();
                dmtn.add(new NoLeafMutableTreeNode(new BTNodeStorage(nodeNumber,
                        curNode,
                        key.getParentID().toLong() + ":" + catalogFile.
                        getVolume().decodeString(key.getNodeName()))));
            }
        }
        else if(node instanceof CommonHFSCatalogLeafNode) {
            CommonHFSCatalogLeafNode leafNode =
                    (CommonHFSCatalogLeafNode) node;
            CommonHFSCatalogLeafRecord[] recs = leafNode.getLeafRecords();
            int[] recordOffsets = leafNode.getRecordOffsets();

            for(int i = 0; i < recs.length; ++i) {
                final CommonHFSCatalogLeafRecord rec = recs[i];
                if(rec != null) {
                    dmtn.add(new DefaultMutableTreeNode(new BTLeafStorage(i,
                            recordOffsets[i],
                            recordOffsets[i + 1] - recordOffsets[i], rec,
                            rec.getKey().getParentID().toLong() + ":" +
                            catalogFile.getVolume().decodeString(rec.getKey().
                            getNodeName()))));
                }
                else {
                    dmtn.add(new DefaultMutableTreeNode("<Invalid record " +
                            (i + 1) + ">", false));
                }
            }
        }
        else
            throw new RuntimeException("Invalid node type in tree.");
    }

    protected boolean handleLeafRecord(BTLeafStorage leafStorage) {
        CommonBTLeafRecord rec = leafStorage.getRecord();
        //HFSPlusCatalogLeafRecordData data = rec.getData();
        if(rec instanceof CommonHFSCatalogFileRecord.HFSPlusImplementation) {
            CommonHFSCatalogFile fil =
                    ((CommonHFSCatalogFileRecord)rec).getData();
            if(fil instanceof CommonHFSCatalogFile.HFSPlusImplementation) {
                HFSPlusCatalogFile underlying =
                        ((CommonHFSCatalogFile.HFSPlusImplementation) fil).
                        getUnderlying();
                fileInfoPanelHeader.setRecordNumber(
                        leafStorage.getRecordNumber());
                fileInfoPanelHeader.setRecordOffset(
                        leafStorage.getRecordOffset());
                fileInfoPanelHeader.setRecordSize(
                        leafStorage.getRecordSize());
                fileInfoPanel.setFields(underlying);
                clLeaf.show(leafPanel, FILE_NAME);
            }
            else {
                System.err.println("BTreeInfoPanel: Could not show file " +
                        "record type " + fil.getClass());
                clLeaf.show(leafPanel, OTHER_NAME);
            }
        }
        else if(rec instanceof CommonHFSCatalogFolderRecord.
                HFSPlusImplementation)
        {
            CommonHFSCatalogFolder fld =
                    ((CommonHFSCatalogFolderRecord)rec).getData();
            if(fld instanceof CommonHFSCatalogFolder.HFSPlusImplementation)
            {
                HFSPlusCatalogFolder underlying =
                        ((CommonHFSCatalogFolder.HFSPlusImplementation)
                        fld).getUnderlying();
                // System.err.println("folderInfoPanelHeader: " +
                //         folderInfoPanelHeader);
                // System.err.println("leafStorage: " + leafStorage);
                folderInfoPanelHeader.setRecordNumber(
                        leafStorage.getRecordNumber());
                folderInfoPanelHeader.setRecordOffset(
                        leafStorage.getRecordOffset());
                folderInfoPanelHeader.setRecordSize(
                        leafStorage.getRecordSize());
                folderInfoPanel.setFields(underlying);
                clLeaf.show(leafPanel, FOLDER_NAME);
            }
            else {
                System.err.println("BTreeInfoPanel: Could not show " +
                        "folder record type " + fld.getClass());
                clLeaf.show(leafPanel, OTHER_NAME);
            }
        }
        else {
            return false;
        }

        return true;
    }
}
