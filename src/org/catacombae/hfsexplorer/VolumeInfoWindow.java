package org.catacombae.hfsexplorer;

import org.catacombae.hfsexplorer.gui.VolumeInfoPanel;
import org.catacombae.hfsexplorer.gui.JournalInfoPanel;
import org.catacombae.hfsexplorer.types.HFSPlusVolumeHeader;
import org.catacombae.hfsexplorer.types.JournalInfoBlock;
import java.awt.*;
import javax.swing.*;

public class VolumeInfoWindow extends JFrame {
    private JTabbedPane tabs;
    private JScrollPane infoPanelScroller;
    private JScrollPane journalInfoPanelScroller;
    private VolumeInfoPanel infoPanel;
    private JournalInfoPanel journalInfoPanel;
    
    public VolumeInfoWindow() {
	super("Volume info");
	
	tabs = new JTabbedPane();
	infoPanel = new VolumeInfoPanel();
	journalInfoPanel = new JournalInfoPanel();
	
	infoPanelScroller = new JScrollPane(infoPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	journalInfoPanelScroller = new JScrollPane(journalInfoPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	tabs.addTab("Volume info", infoPanelScroller);
	tabs.addTab("Journal info", journalInfoPanelScroller);
	add(tabs, BorderLayout.CENTER);
	
	setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	pack();
	setLocationRelativeTo(null);
    }
    
    public void setVolumeFields(HFSPlusVolumeHeader vh) {
	infoPanel.setFields(vh);
    }
    public void setJournalFields(JournalInfoBlock jib) {
	journalInfoPanel.setFields(jib);
    }
}