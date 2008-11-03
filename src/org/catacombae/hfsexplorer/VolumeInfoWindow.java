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
import org.catacombae.hfsexplorer.fs.BaseHFSFileSystemView;
import org.catacombae.hfsexplorer.gui.AllocationFileInfoPanel;
import org.catacombae.hfsexplorer.gui.CatalogInfoPanel;
import org.catacombae.hfsexplorer.gui.ExtentsInfoPanel;
import org.catacombae.hfsexplorer.gui.JournalInfoPanel;
import org.catacombae.hfsexplorer.gui.StructViewPanel;
import org.catacombae.hfsexplorer.gui.HFSPlusVolumeInfoPanel;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSVolumeHeader;
import org.catacombae.hfsexplorer.types.hfsplus.JournalInfoBlock;

/**
 * A window that queries a HFSish file system about its volume properties and displays them
 * graphically.
 * 
 * @author Erik Larsson
 */
public class VolumeInfoWindow extends JFrame {

    public VolumeInfoWindow(BaseHFSFileSystemView fsView) {
        super("File system info");

        final JTabbedPane tabs = new JTabbedPane();
        
        
        // The "Volume header" tab
        
        try {
            final JPanel volumeInfoPanel;
            CommonHFSVolumeHeader volHeader = fsView.getVolumeHeader();
            if(volHeader instanceof CommonHFSVolumeHeader.HFSPlusImplementation)
                volumeInfoPanel = new HFSPlusVolumeInfoPanel(
                        ((CommonHFSVolumeHeader.HFSPlusImplementation)volHeader).getUnderlying());
            else
                volumeInfoPanel = new StructViewPanel("Volume header",
                    volHeader.getStructElements());
            
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
            CatalogInfoPanel catalogInfoPanel = new CatalogInfoPanel(fsView);
            JScrollPane catalogInfoPanelScroller = new JScrollPane(catalogInfoPanel,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            tabs.addTab("Catalog file", catalogInfoPanelScroller);
            catalogInfoPanelScroller.getVerticalScrollBar().setUnitIncrement(10);
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        
        // The "Extents overflow file info" tab
        
        try {
            ExtentsInfoPanel extentsInfoPanel = new ExtentsInfoPanel(fsView);
            JScrollPane scroller = new JScrollPane(extentsInfoPanel,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            tabs.addTab("Extents overflow file", scroller);
            scroller.getVerticalScrollBar().setUnitIncrement(10);
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        
        // The "Journal info" tab (optional)
        
        try {
            JournalInfoBlock jib = fsView.getJournalInfoBlock();
            if(jib != null) {
                JournalInfoPanel journalInfoPanel = new JournalInfoPanel(jib);
                JScrollPane journalInfoPanelScroller = new JScrollPane(journalInfoPanel,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                tabs.addTab("Journal", journalInfoPanelScroller);
                journalInfoPanelScroller.getVerticalScrollBar().setUnitIncrement(10);
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
