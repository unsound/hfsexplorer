/*-
 * Copyright (C) 2007-2008 Erik Larsson
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

package org.catacombae.hfsexplorer;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import org.catacombae.hfs.AttributesFile;
import org.catacombae.hfs.HFSVolume;
import org.catacombae.hfs.Journal;
import org.catacombae.hfs.types.hfscommon.CommonHFSVolumeHeader;
import org.catacombae.hfsexplorer.gui.AttributesInfoPanel;
import org.catacombae.hfsexplorer.gui.CatalogInfoPanel;
import org.catacombae.hfsexplorer.gui.ExtentsInfoPanel;
import org.catacombae.hfsexplorer.gui.HFSExplorerJFrame;
import org.catacombae.hfsexplorer.gui.JournalInfoPanel;
import org.catacombae.hfsexplorer.gui.StructViewPanel;

/**
 * A window that queries a HFSish file system about its volume properties and displays them
 * graphically.
 *
 * @author <a href="https://catacombae.org" target="_top">Erik Larsson</a>
 */
public class VolumeInfoWindow extends HFSExplorerJFrame {

    public VolumeInfoWindow(HFSVolume fsView) {
        super("Volume information");

        final JTabbedPane tabs = new JTabbedPane();


        // The "Volume header" tab

        try {
            final JPanel volumeInfoPanel;
            CommonHFSVolumeHeader volHeader = fsView.getVolumeHeader();
            volumeInfoPanel = new StructViewPanel("Volume header",
                    volHeader.getStructElements(), true);

            JScrollPane volumeInfoPanelScroller = new JScrollPane(volumeInfoPanel,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            tabs.addTab("Volume header", volumeInfoPanelScroller);
            volumeInfoPanelScroller.getVerticalScrollBar().setUnitIncrement(10);
        } catch(Exception e) {
            e.printStackTrace();
        }


        // The "Catalog file info" tab

        try {
            final JTabbedPane catalogTabs = new JTabbedPane();

            try {
                StructViewPanel headerRecordPanel =
                        new StructViewPanel("B-tree header record",
                        fsView.getCatalogFile().getCatalogHeaderNode().
                        getHeaderRecord().getStructElements());
                JScrollPane headerRecordPanelScroller = new JScrollPane(
                        headerRecordPanel,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                headerRecordPanelScroller.getVerticalScrollBar().
                        setUnitIncrement(10);
                catalogTabs.addTab("Header record", headerRecordPanelScroller);
            } catch(Exception e) {
                e.printStackTrace();
            }

            CatalogInfoPanel catalogInfoPanel =
                    new CatalogInfoPanel(fsView);
            JScrollPane catalogInfoPanelScroller = new JScrollPane(catalogInfoPanel,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            catalogInfoPanelScroller.getVerticalScrollBar().setUnitIncrement(10);
            catalogTabs.addTab("Tree", catalogInfoPanelScroller);

            tabs.addTab("Catalog file", catalogTabs);
        } catch(Exception e) {
            e.printStackTrace();
        }


        // The "Extents overflow file info" tab

        try {
            final JTabbedPane extentsTabs = new JTabbedPane();

            try {
                StructViewPanel headerRecordPanel =
                        new StructViewPanel("B-tree header record",
                        fsView.getExtentsOverflowFile().
                        getHeaderNode().getHeaderRecord().
                        getStructElements());
                JScrollPane headerRecordPanelScroller = new JScrollPane(
                        headerRecordPanel,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                headerRecordPanelScroller.getVerticalScrollBar().
                        setUnitIncrement(10);
                extentsTabs.addTab("Header record", headerRecordPanelScroller);
            } catch(Exception e) {
                e.printStackTrace();
            }

            ExtentsInfoPanel extentsInfoPanel = new ExtentsInfoPanel(fsView);
            JScrollPane scroller = new JScrollPane(extentsInfoPanel,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scroller.getVerticalScrollBar().setUnitIncrement(10);
            extentsTabs.addTab("Tree", scroller);

            tabs.addTab("Extents overflow file", extentsTabs);
        } catch(Exception e) {
            e.printStackTrace();
        }


        // The "Attributes file info" tab

        try {
            final AttributesFile attributesFile = fsView.getAttributesFile();

            if(attributesFile != null) {
                final JTabbedPane attributesTabs = new JTabbedPane();

                try {
                    StructViewPanel headerRecordPanel =
                            new StructViewPanel("B-tree header record",
                            attributesFile.getHeaderNode().getHeaderRecord().
                            getStructElements());
                    JScrollPane headerRecordPanelScroller = new JScrollPane(
                            headerRecordPanel,
                            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                    headerRecordPanelScroller.getVerticalScrollBar().
                            setUnitIncrement(10);
                    attributesTabs.addTab("Header record",
                            headerRecordPanelScroller);
                } catch(Exception e) {
                    e.printStackTrace();
                }

                AttributesInfoPanel attributesInfoPanel =
                        new AttributesInfoPanel(fsView.getAttributesFile());
                JScrollPane scroller = new JScrollPane(attributesInfoPanel,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                scroller.getVerticalScrollBar().setUnitIncrement(10);
                attributesTabs.addTab("Tree", scroller);

                tabs.addTab("Attributes file", attributesTabs);
}
        } catch(Exception e) {
            e.printStackTrace();
        }


        // The "Journal info" tab (optional)

        try {
            Journal journal = fsView.getJournal();
            if(journal != null) {
                JournalInfoPanel journalInfoPanel = new JournalInfoPanel(journal);
                tabs.addTab("Journal", journalInfoPanel);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }


        // The "Allocation file info" tab
        /*
        try {
            AllocationFileInfoPanel allocationFileInfoPanel = new AllocationFileInfoPanel(this,
                    fsView.getAllocationFileView());
            JScrollPane allocationFileInfoPanelScroller = new JScrollPane(allocationFileInfoPanel,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            tabs.addTab("Allocation file info", allocationFileInfoPanelScroller);
            allocationFileInfoPanelScroller.getVerticalScrollBar().setUnitIncrement(10);
        } catch(Exception e) {
            e.printStackTrace();
        }
        */

        add(tabs, BorderLayout.CENTER);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        int width = getSize().width;
        int height = getSize().height;
        int adjustedHeight = width + width / 2;
        //System.err.println("w: " + width + " h: " + height + " ah: " + adjustedHeight);
        if(adjustedHeight < height)
            setSize(width, adjustedHeight);

        setLocationRelativeTo(null);
    }

    /*
    public void setVolumeFields(HFSPlusVolumeHeader vh) {
        volumeInfoPanel.setFields(vh);
    }
    */
    /*
    public void setJournalFields(JournalInfoBlock jib) {
        journalInfoPanel.setFields(jib);
    }
     * */
}
