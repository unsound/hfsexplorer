/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
