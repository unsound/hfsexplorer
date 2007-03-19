package org.catacombae.hfsexplorer;

import org.catacombae.hfsexplorer.gui.FolderInfoPanel;
//import org.catacombae.hfsexplorer.gui.JournalInfoPanel;
import org.catacombae.hfsexplorer.types.HFSPlusCatalogFolder;
//import org.catacombae.hfsexplorer.types.JournalInfoBlock;
import java.awt.*;
import javax.swing.*;

public class FolderInfoWindow extends JFrame {
    private JTabbedPane tabs;
    private JScrollPane infoPanelScroller;
    //private JScrollPane journalInfoPanelScroller;
    private FolderInfoPanel infoPanel;
    //private JournalInfoPanel journalInfoPanel;
    
    public FolderInfoWindow(String filename) {
	super("Info - " + filename);
	
	tabs = new JTabbedPane();
	infoPanel = new FolderInfoPanel();
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
	//System.err.println("w: " + width + " h: " + height + " ah: " + adjustedHeight);
	if(adjustedHeight < height)
	    setSize(width, adjustedHeight);
	
	setLocationRelativeTo(null);
    }
    
    public void setFields(HFSPlusCatalogFolder vh) {
	infoPanel.setFields(vh);
    }
//     public void setJournalFields(JournalInfoBlock jib) {
// 	journalInfoPanel.setFields(jib);
//     }
}
