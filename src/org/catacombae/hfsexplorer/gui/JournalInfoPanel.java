/*-
 * Copyright (C) 2007 Erik Larsson
 * 
 * All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 */

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