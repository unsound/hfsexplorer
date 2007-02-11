package org.catacombae.hfsexplorer;

import org.catacombae.hfsexplorer.gui.VolumeInfoPanel;
import org.catacombae.hfsexplorer.types.HFSPlusVolumeHeader;
import java.awt.*;
import javax.swing.*;

public class VolumeInfoWindow extends JFrame {
    private JScrollPane infoPanelScroller;
    private VolumeInfoPanel infoPanel;
    
    public VolumeInfoWindow() {
	super("Volume info");
	
	infoPanel = new VolumeInfoPanel();
	infoPanelScroller = new JScrollPane(infoPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	add(infoPanelScroller, BorderLayout.CENTER);
	
	pack();
	setLocationRelativeTo(null);
    }
    
    public void setFields(HFSPlusVolumeHeader vh) {
	infoPanel.setFields(vh);
    }
}