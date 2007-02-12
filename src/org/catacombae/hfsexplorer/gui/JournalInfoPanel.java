package org.catacombae.hfsexplorer.gui;

import org.catacombae.hfsexplorer.types.JournalInfoBlock;
import javax.swing.*;
import java.awt.*;

public class JournalInfoPanel extends JPanel {
    private JPanel contentsPanel;
    private JournalInfoBlockPanel infoBlockPanel;
    private JPanel noJournalPanel;
    private JLabel noJournalLabel;
    private CardLayout layout;

    public JournalInfoPanel() {
	contentsPanel = new JPanel();
	infoBlockPanel = new JournalInfoBlockPanel();
	noJournalPanel = new JPanel();
	noJournalLabel = new JLabel("No journal present", SwingConstants.CENTER);
	layout = new CardLayout();
	
	contentsPanel.setLayout(new BorderLayout());
	contentsPanel.add(infoBlockPanel, BorderLayout.CENTER);
	
	noJournalPanel.setLayout(new BorderLayout());
	noJournalPanel.add(noJournalLabel, BorderLayout.CENTER);
	
	setLayout(layout);
	add(noJournalPanel, "A");
	add(contentsPanel, "B");
	layout.show(this, "A");
	
	//pack();
    }
    
    public void setFields(JournalInfoBlock jib) {
	infoBlockPanel.setFields(jib);
	layout.show(this, "B");
    }
}