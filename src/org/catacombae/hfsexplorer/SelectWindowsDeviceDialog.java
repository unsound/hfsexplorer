/*-
 * Copyright (C) 2006 Erik Larsson
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

package org.catacombae.hfsexplorer;

import org.catacombae.hfsexplorer.win32.WindowsLowLevelIO;
import org.catacombae.hfsexplorer.gui.SelectWindowsDevicePanel;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;

public class SelectWindowsDeviceDialog extends JDialog {
    private static final String DEVICE_PREFIX = "\\\\?\\GLOBALROOT\\Device\\";

    private SelectWindowsDevicePanel guiPanel;
    
    // Shortcuts to variables in guiPanel
    private JButton autodetectButton;
    private JRadioButton selectDeviceButton;
    private JRadioButton specifyDeviceNameButton;
    private JButton loadButton;
    private JButton cancelButton;
    private JTextField specifyDeviceNameField;
    private JComboBox detectedDevicesCombo;

    // Additional gui stuff
    private ButtonGroup selectSpecifyGroup;
    
    private String result = null;
    private String[] detectedDeviceNames;
    
    public SelectWindowsDeviceDialog(Frame owner, boolean modal, String title) {
	super(owner, modal);
	setTitle(title);
	
	this.guiPanel = new SelectWindowsDevicePanel();
	this.autodetectButton = guiPanel.autodetectButton;
	this.selectDeviceButton = guiPanel.selectDeviceButton;
	this.specifyDeviceNameButton = guiPanel.specifyDeviceNameButton;
	this.loadButton = guiPanel.loadButton;
	this.cancelButton = guiPanel.cancelButton;
	this.specifyDeviceNameField = guiPanel.specifyDeviceNameField;
	this.detectedDevicesCombo = guiPanel.detectedDevicesCombo;
	
	this.selectSpecifyGroup = new ButtonGroup();
	selectSpecifyGroup.add(selectDeviceButton);
	selectSpecifyGroup.add(specifyDeviceNameButton);
	
	detectedDevicesCombo.removeAllItems();
	
	detectedDeviceNames = detectDevices();
	for(String name : detectedDeviceNames)
	    detectedDevicesCombo.addItem(name);
	
	if(detectedDeviceNames.length > 0) {
	    detectedDevicesCombo.setSelectedIndex(0);
	    specifyDeviceNameField.setText(DEVICE_PREFIX + detectedDevicesCombo.getSelectedItem().toString());
	}
	
	autodetectButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
		    autodetectFilesystems();
		}
	    });
	detectedDevicesCombo.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent ie) {
		    if(ie.getStateChange() == ItemEvent.SELECTED)
			specifyDeviceNameField.setText(DEVICE_PREFIX + ie.getItem().toString());
		}
	    });
	selectDeviceButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
		    detectedDevicesCombo.setEnabled(true);
		    specifyDeviceNameField.setEnabled(false);
		    specifyDeviceNameField.setText(DEVICE_PREFIX + detectedDevicesCombo.getSelectedItem().toString());
		}
	    });
	specifyDeviceNameButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
		    detectedDevicesCombo.setEnabled(false);
		    specifyDeviceNameField.setEnabled(true);
		}
	    });
	loadButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
		    result = specifyDeviceNameField.getText();
		    setVisible(false);
		}
	    });
	cancelButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
		    setVisible(false);
		}
	    });
	
// 	JPanel container = new JPanel();
// 	container.setLayout(new BorderLayout());
// 	container.setBorder(new EmptyBorder(5, 5, 5, 5));
// 	container.add(guiPanel, BorderLayout.CENTER);
	
	add(guiPanel, BorderLayout.CENTER);
	pack();
	setLocationRelativeTo(null);
	setResizable(false);
    }

    public String getPathName() { return result; }
    
    /**
     * This method is only tested with Windows XP (SP2, x86).
     * Also, it won't work with devices that are not mounted
     * using the Windows XP standard names. For example, Bo
     * Brantén's filedisk creates a device with another name.
     * However, if your file system is on a file, this method
     * is not needed.
     * @return a list of the names of the detected devices
     */
    protected String[] detectDevices() {
	//int numDevices = 0;
	LinkedList<String> activeDeviceNames = new LinkedList<String>();
	/*
	 * Since I've been too lazy to figure out how to implement
	 * a native method for reading the contents of the device
	 * tree, I'll just make up names for 20 harddrives, with
	 * 20 partitions in each and check for existence.
	 * Have more than 20 harddrives? You're out of luck. ;)
	 */
	// 20 hard drives...
	for(int i = 0; i < 20; ++i) {
	    // 20 partitions each...
	    for(int j = 0; j < 20; ++j) {
		try {
		    /* Should I add Partition0 to the list? It really means
		     * "the whole drive". Partition1 is the first partition... */
		    String currentDevice = "Harddisk"+i+"\\Partition"+j;
		    WindowsLowLevelIO curFile = new WindowsLowLevelIO(DEVICE_PREFIX + currentDevice);
		    curFile.close();
		    activeDeviceNames.addLast(currentDevice);
		    //deviceCombo.addItem(currentDevice);
		    //++numDevices;
		} catch(Exception e) {}
	    }
	}

	// ...and 20 CD-ROMs
	for(int i = 0; i < 20; ++i) {
	    try {
		String currentDevice = "CdRom"+i;
		WindowsLowLevelIO curFile = new WindowsLowLevelIO(DEVICE_PREFIX + currentDevice);
		curFile.close();
		//deviceCombo.addItem(currentDevice);
		activeDeviceNames.addLast(currentDevice);
		//++numDevices;
	    } catch(Exception e) {}
	}
	return activeDeviceNames.toArray(new String[activeDeviceNames.size()]);
    }
    protected void autodetectFilesystems() {
	LinkedList<String> plainFileSystems = new LinkedList<String>();
	for(String name : detectedDeviceNames) {
	    try {
		LowLevelFile llf = new WindowsLowLevelIO(DEVICE_PREFIX + name);
		FileSystemRecognizer fsr = new FileSystemRecognizer(llf, 0);
		if(fsr.detectFileSystem() == FileSystemRecognizer.FileSystemType.HFS_PLUS)
		    plainFileSystems.add(name);
		llf.close();
	    } catch(Exception e) {
		System.out.println("INFO: Non-critical exception while detecting file systems: " + e.toString());
// 		e.printStackTrace();
// 		JOptionPane.showMessageDialog(this, "Exception while detecting file system for \"" +
// 					      name + "\":\n" + e.toString(),
// 					      "Exception", JOptionPane.ERROR_MESSAGE);
	    }
	}
	
	if(plainFileSystems.size() > 0) {
	    String[] devices = plainFileSystems.toArray(new String[plainFileSystems.size()]);
	    Object selectedValue = JOptionPane.showInputDialog(this, "Please choose which file system to load:", 
							       "Select device", 
							       JOptionPane.QUESTION_MESSAGE,
							       null, devices, devices[0]);
	    if(selectedValue != null) {
		result = DEVICE_PREFIX + selectedValue.toString();
		setVisible(false);
	    }
	}
	else
	    JOptionPane.showMessageDialog(this, "No HFS+ file systems found...",
					  "Result", JOptionPane.INFORMATION_MESSAGE);
    }
}
