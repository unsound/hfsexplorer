/*-
 * Copyright (C) 2008 Erik Larsson
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

package org.catacombae.jparted.app;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import javax.swing.AbstractButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

/**
 *
 * @author erik
 */
public class MainWindow extends JFrame {
    private JMenuItem loadFileItem;
    private JMenuItem exitItem;
    private JMenuItem aboutItem;
    private JPanel mainPanel;

    
    public MainWindow(JPanel mainPanel) {
        super("jParted");
        this.mainPanel = mainPanel;
        
        this.setLayout(new BorderLayout());
        this.add(mainPanel, BorderLayout.CENTER);
        
        setupMenus();
        
        this.pack();
        this.setLocationRelativeTo(null);
    }
    
    private static void setAbstractButtonListener(AbstractButton button, ActionListener listener) {
        for(ActionListener al : button.getActionListeners())
            button.removeActionListener(al);
        button.addActionListener(listener);
    }
    
    public void setLoadFileItemListener(ActionListener listener) {
        setAbstractButtonListener(loadFileItem, listener);
    }
    
    public void setExitItemListener(ActionListener listener) {
        setAbstractButtonListener(exitItem, listener);
    }

    public void setAboutItemListener(ActionListener listener) {
        setAbstractButtonListener(aboutItem, listener);
    }
    
    private void setupMenus() {
        JMenu fileMenu = new JMenu("File");
        JMenu helpMenu = new JMenu("Help");
        
        // File menu items        
        loadFileItem = new JMenuItem("Load from file...");
        fileMenu.add(loadFileItem);
        exitItem = new JMenuItem("Quit");
        fileMenu.add(exitItem);
        
        // Help menu items
        aboutItem = new JMenuItem("About...");
        helpMenu.add(aboutItem);
        
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        this.setJMenuBar(menuBar);
    }
}
