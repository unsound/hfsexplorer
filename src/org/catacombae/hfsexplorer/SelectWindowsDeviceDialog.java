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
import org.catacombae.hfsexplorer.partitioning.*;
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
    
    private LowLevelFile result = null;
    private String resultCreatePath = null;
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
		    resultCreatePath = specifyDeviceNameField.getText();
		    result = new WindowsLowLevelIO(resultCreatePath);
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

    public LowLevelFile getPartitionStream() { return result; }
    
    /** Could include an identifier of a partitioning scheme. This should only be used to display a descriptive locator. */
    public String getPathName() { return resultCreatePath; }
    
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
	 * You have more than 20 harddrives? You're out of luck. ;)
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
		} catch(Exception e) {}
	    }
	}

	// ...and 20 CD-ROMs
	for(int i = 0; i < 20; ++i) {
	    try {
		String currentDevice = "CdRom"+i;
		WindowsLowLevelIO curFile = new WindowsLowLevelIO(DEVICE_PREFIX + currentDevice);
		curFile.close();
		activeDeviceNames.addLast(currentDevice);
	    } catch(Exception e) {}
	}
	return activeDeviceNames.toArray(new String[activeDeviceNames.size()]);
    }
    protected void autodetectFilesystems() {
	LinkedList<String> plainFileSystems = new LinkedList<String>();
	LinkedList<String[]> embeddedFileSystems = new LinkedList<String[]>();
	
	// Look for file systems in Windows supported partitioning schemes
	for(String name : detectedDeviceNames) {
	    try {
		LowLevelFile llf = new WindowsLowLevelIO(DEVICE_PREFIX + name);
		FileSystemRecognizer fsr = new FileSystemRecognizer(llf, 0);
		if(fsr.detectFileSystem() == FileSystemRecognizer.FileSystemType.HFS_PLUS)
		    plainFileSystems.add(name);
		llf.close();
	    } catch(Exception e) {
		System.out.println("INFO: Non-critical exception while detecting file system at \"" + DEVICE_PREFIX + name + "\": " + e.toString());
	    }
	}
	// Look for file systems that sit inside partition systems unsupported by Windows.
	for(int i = 0; i < detectedDeviceNames.length; ++i) {
	    String name = detectedDeviceNames[i];
// 	    System.out.println("Checking if \"" + name + "\" might be an unsupported partition system...");
// 	    System.out.println("  name.startsWith(\"CdRom\") == " + name.startsWith(DEVICE_PREFIX + "CdRom"));
// 	    System.out.println("  name.endsWith(\"Partition0\") == " + name.endsWith("Partition0"));
	    
	    if(name.startsWith("CdRom") ||
	       (name.endsWith("Partition0") &&
		!(i+1 < detectedDeviceNames.length && detectedDeviceNames[i+1].endsWith("Partition1"))) ) {
		// We have an unidentifed partition system at "name"
// 		System.out.println("TRUE!");
		try {
		    LowLevelFile llf = new WindowsLowLevelIO(DEVICE_PREFIX + name);
		    PartitionSystemRecognizer psr = new PartitionSystemRecognizer(llf);
		    PartitionSystemRecognizer.PartitionSystemType pst = psr.detectPartitionSystem();
		    if(pst == PartitionSystemRecognizer.PartitionSystemType.APPLE_PARTITION_MAP) {
// 			System.out.println("Detected APM... reading ddr");
			DriverDescriptorRecord ddr = new DriverDescriptorRecord(llf, 0);
			if(ddr.isValid()) {
// 			    System.out.println("valid ddr");
// 			    ddr.print(System.out, "  ");
			    ApplePartitionMap apm = new ApplePartitionMap(llf, ddr.getSbBlkSize()*1, ddr.getSbBlkSize());
			    Partition[] parts = apm.getUsedPartitionEntries();
			    for(int j = 0; j < parts.length; ++j) {
// 				System.out.println("Looping partition " + j);
				Partition part = parts[j];
				if(part.getType() == Partition.PartitionType.APPLE_HFS) {
				    FileSystemRecognizer fsr = new FileSystemRecognizer(llf, part.getStartOffset());
				    if(fsr.detectFileSystem() == FileSystemRecognizer.FileSystemType.HFS_PLUS) {
					embeddedFileSystems.add(new String[] { "APM", name, j + "" });
				    }
				}
			    }
			}
// 			else System.out.println("invalid ddr");
		    }
		    else if(pst == PartitionSystemRecognizer.PartitionSystemType.GUID_PARTITION_TABLE) {}
		    else if(pst == PartitionSystemRecognizer.PartitionSystemType.MASTER_BOOT_RECORD) {}
		    
		    llf.close();
		} catch(Exception e) {
		    System.out.println("INFO: Non-critical exception while detecting partition system at \"" +
				       DEVICE_PREFIX + name + "\": " + e.toString());
		}
	    }
// 	    else
// 		System.out.println("FALSE");
	}
	
	if(plainFileSystems.size() >= 1 || embeddedFileSystems.size() >= 1) {
	    String[] plainStrings = plainFileSystems.toArray(new String[plainFileSystems.size()]);
	    String[] embeddedStrings = new String[embeddedFileSystems.size()];
	    int i = 0;
	    for(String[] cur : embeddedFileSystems)
		embeddedStrings[i++] = cur[1] + "[" + cur[0] + ":Partition" + cur[2] + "]";
	    
	    String[] allOptions = new String[plainStrings.length+embeddedStrings.length];
	    for(i = 0; i < plainStrings.length; ++i)
		allOptions[i] = plainStrings[i];
	    for(i = 0; i < embeddedStrings.length; ++i)
		allOptions[plainStrings.length+i] = embeddedStrings[i];
	    
	    Object selectedValue = JOptionPane.showInputDialog(this, "Autodetection complete! Found " +
							       allOptions.length + " HFS+ file systems.\n" +
							       "Please choose which one to load:", 
							       "Load HFS+ file system", 
							       JOptionPane.QUESTION_MESSAGE,
							       null, allOptions, allOptions[0]);
	    if(selectedValue != null) {
		int selectedIndex = -1;
		for(i = 0; i < allOptions.length; ++i) {
		    if(selectedValue.equals(allOptions[i])) {
			selectedIndex = i;
			break;
		    }
		}
		if(selectedIndex == -1) {
		    throw new RuntimeException("Internal error!");
		}
		else {
		    if(selectedIndex >= plainStrings.length) {
			// We have an embedded FS
			selectedIndex -= plainStrings.length;
			String[] embeddedInfo = embeddedFileSystems.get(selectedIndex);
			if(embeddedInfo == null)
			    throw new RuntimeException("Internal error again.");
			
			if(embeddedInfo[0].equals("APM")) {
			    LowLevelFile llf = new WindowsLowLevelIO(DEVICE_PREFIX + embeddedInfo[1]);
			    DriverDescriptorRecord ddr = new DriverDescriptorRecord(llf, 0);
			    ApplePartitionMap apm = new ApplePartitionMap(llf, ddr.getSbBlkSize()*1, ddr.getSbBlkSize());
			    Partition p = apm.getPartitionEntry(Integer.parseInt(embeddedInfo[2]));
			    resultCreatePath = DEVICE_PREFIX + selectedValue.toString();
			    result = new ConcatenatedFile(llf, p.getStartOffset(), p.getLength());
			    setVisible(false);
			}
			else if(embeddedInfo[0].equals("GPT")) {}
			else if(embeddedInfo[0].equals("MBR")) {}
		    }
		    else {
			resultCreatePath = DEVICE_PREFIX + selectedValue.toString();
			result = new WindowsLowLevelIO(resultCreatePath);
			setVisible(false);
		    }
		}
	    }
	}
// 	else if(plainFileSystems.size() > 0) {
// 	    int res = JOptionPane.showConfirmDialog(this, "Autodetection complete! Found an " +
// 						    "HFS+ file system at \"" + 
// 						    plainFileSystems.getFirst() +"\".\n" +
// 						    "Do you want to load it?", 
// 						    "Load HFS+ file system",
// 						    JOptionPane.YES_NO_OPTION,
// 						    JOptionPane.QUESTION_MESSAGE);
// 	    if(res == JOptionPane.YES_OPTION) {
// 		result = DEVICE_PREFIX + plainFileSystems.getFirst();
// 		setVisible(false);
// 	    }
// 	}
	else
	    JOptionPane.showMessageDialog(this, "No HFS+ file systems found...",
					  "Result", JOptionPane.INFORMATION_MESSAGE);
    }
}
