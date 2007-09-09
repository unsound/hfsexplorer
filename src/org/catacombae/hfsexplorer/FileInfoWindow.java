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

import org.catacombae.hfsexplorer.gui.FileInfoPanel;
//import org.catacombae.hfsexplorer.gui.JournalInfoPanel;
import org.catacombae.hfsexplorer.types.HFSPlusCatalogFile;
//import org.catacombae.hfsexplorer.types.JournalInfoBlock;
import java.awt.*;
import javax.swing.*;

public class FileInfoWindow extends JFrame {
    private JTabbedPane tabs;
    private JScrollPane infoPanelScroller;
    //private JScrollPane journalInfoPanelScroller;
    private FileInfoPanel infoPanel;
    //private JournalInfoPanel journalInfoPanel;
    
    public FileInfoWindow(String filename) {
	super("Info - " + filename);
	
	tabs = new JTabbedPane();
	infoPanel = new FileInfoPanel();
	//journalInfoPanel = new JournalInfoPanel();
	
	infoPanelScroller = new JScrollPane(infoPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	//journalInfoPanelScroller = new JScrollPane(journalInfoPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	tabs.addTab("Detailed", infoPanelScroller);
	//tabs.addTab("Journal info", journalInfoPanelScroller);
	add(tabs, BorderLayout.CENTER);
	
	infoPanelScroller.getVerticalScrollBar().setUnitIncrement(10);
	
	setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	pack();
	int width = getSize().width;
	int height = getSize().height;
	int adjustedHeight = width + width/2;
	
	if(adjustedHeight < height)
	    setSize(width, adjustedHeight);
	
	setLocationRelativeTo(null);
    }
    
    public void setFields(HFSPlusCatalogFile vh) {
	infoPanel.setFields(vh);
    }
//     public void setJournalFields(JournalInfoBlock jib) {
// 	journalInfoPanel.setFields(jib);
//     }
}
