/*-
 * Copyright (C) 2007 Erik Larsson
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

//import org.catacombae.hfsexplorer.HFSPlusFileSystemView;
import org.catacombae.hfsexplorer.gui.VolumeInfoPanel;
import org.catacombae.hfsexplorer.gui.CatalogInfoPanel;
import org.catacombae.hfsexplorer.gui.JournalInfoPanel;
import org.catacombae.hfsexplorer.types.HFSPlusVolumeHeader;
import org.catacombae.hfsexplorer.types.JournalInfoBlock;
import java.awt.*;
import javax.swing.*;

public class VolumeInfoWindow extends JFrame {
    private JTabbedPane tabs;
    private JScrollPane volumeInfoPanelScroller;
    private JScrollPane catalogInfoPanelScroller;
    private JScrollPane journalInfoPanelScroller;
    private VolumeInfoPanel volumeInfoPanel;
    private CatalogInfoPanel catalogInfoPanel;
    private JournalInfoPanel journalInfoPanel;
    
    public VolumeInfoWindow(HFSPlusFileSystemView fsView) {
	super("File system info");
	
	tabs = new JTabbedPane();
	volumeInfoPanel = new VolumeInfoPanel();
	catalogInfoPanel = new CatalogInfoPanel(fsView);
	journalInfoPanel = new JournalInfoPanel();
	
	volumeInfoPanelScroller = new JScrollPane(volumeInfoPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	catalogInfoPanelScroller = new JScrollPane(catalogInfoPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	journalInfoPanelScroller = new JScrollPane(journalInfoPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	tabs.addTab("Volume info", volumeInfoPanelScroller);
	tabs.addTab("Catalog file info", catalogInfoPanelScroller);
	tabs.addTab("Journal info", journalInfoPanelScroller);
	add(tabs, BorderLayout.CENTER);
	
	volumeInfoPanelScroller.getVerticalScrollBar().setUnitIncrement(10);
	catalogInfoPanelScroller.getVerticalScrollBar().setUnitIncrement(10);
	journalInfoPanelScroller.getVerticalScrollBar().setUnitIncrement(10);

	setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	pack();
	int width = getSize().width;
	int height = getSize().height;
	int adjustedHeight = width + width/2;
	//System.err.println("w: " + width + " h: " + height + " ah: " + adjustedHeight);
	if(adjustedHeight < height)
	    setSize(width, adjustedHeight);
	
	setLocationRelativeTo(null);
     }
    
    public void setVolumeFields(HFSPlusVolumeHeader vh) {
	volumeInfoPanel.setFields(vh);
    }
    public void setJournalFields(JournalInfoBlock jib) {
	journalInfoPanel.setFields(jib);
    }
}
