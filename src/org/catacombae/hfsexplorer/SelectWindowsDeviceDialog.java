/*-
 * Copyright (C) 2006-2008 Erik Larsson
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

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedList;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.io.ReadableConcatenatedStream;
import org.catacombae.hfsexplorer.win32.WindowsLowLevelIO;
import org.catacombae.hfsexplorer.gui.SelectWindowsDevicePanel;
import org.catacombae.hfsexplorer.partitioning.APMPartition;
import org.catacombae.hfsexplorer.partitioning.ApplePartitionMap;
import org.catacombae.hfsexplorer.partitioning.DriverDescriptorRecord;
import org.catacombae.hfsexplorer.partitioning.GPTEntry;
import org.catacombae.hfsexplorer.partitioning.GUIDPartitionTable;
import org.catacombae.hfsexplorer.partitioning.MBRPartition;
import org.catacombae.hfsexplorer.partitioning.MBRPartitionTable;
import org.catacombae.hfsexplorer.partitioning.Partition;
import org.catacombae.hfsexplorer.partitioning.PartitionSystem;
import org.catacombae.jparted.lib.ps.PartitionType;
import org.catacombae.jparted.lib.ps.ebr.EBRPartition;

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
    
    private ReadableRandomAccessStream result = null;
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
                @Override
		public void actionPerformed(ActionEvent ae) {
		    autodetectFilesystems();
		}
	    });
	detectedDevicesCombo.addItemListener(new ItemListener() {
                @Override
		public void itemStateChanged(ItemEvent ie) {
		    if(ie.getStateChange() == ItemEvent.SELECTED)
			specifyDeviceNameField.setText(DEVICE_PREFIX + ie.getItem().toString());
		}
	    });
	selectDeviceButton.addActionListener(new ActionListener() {
                @Override
		public void actionPerformed(ActionEvent ae) {
		    detectedDevicesCombo.setEnabled(true);
		    specifyDeviceNameField.setEnabled(false);
		    specifyDeviceNameField.setText(DEVICE_PREFIX + detectedDevicesCombo.getSelectedItem().toString());
		}
	    });
	specifyDeviceNameButton.addActionListener(new ActionListener() {
                @Override
		public void actionPerformed(ActionEvent ae) {
		    detectedDevicesCombo.setEnabled(false);
		    specifyDeviceNameField.setEnabled(true);
		}
	    });
	loadButton.addActionListener(new ActionListener() {
                @Override
		public void actionPerformed(ActionEvent ae) {
		    resultCreatePath = specifyDeviceNameField.getText();
		    result = new WindowsLowLevelIO(resultCreatePath);
		    setVisible(false);
		}
	    });
	cancelButton.addActionListener(new ActionListener() {
                @Override
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

    public ReadableRandomAccessStream getPartitionStream() { return result; }
    
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
	// 20 hard drives minimum...
	for(int i = 0; true; ++i) {
            boolean anyFound = false;
	    // 20 partitions each minimum...
	    for(int j = 0; true; ++j) {
		try {
		    /* Should I add Partition0 to the list? It really means
		     * "the whole drive". Partition1 is the first partition... */
		    String currentDevice = "Harddisk"+i+"\\Partition"+j;
		    WindowsLowLevelIO curFile = new WindowsLowLevelIO(DEVICE_PREFIX + currentDevice);
		    curFile.close();
		    activeDeviceNames.addLast(currentDevice);
                    anyFound = true;
		} catch(Exception e) {
                    if(j >= 20)
                        break;
                }
	    }
            if(!anyFound && i >= 20)
               break;
	}

	// ...and 20 CD-ROMs minimum
	for(int i = 0; true; ++i) {
	    try {
		String currentDevice = "CdRom"+i;
		WindowsLowLevelIO curFile = new WindowsLowLevelIO(DEVICE_PREFIX + currentDevice);
		curFile.close();
		activeDeviceNames.addLast(currentDevice);
            }
            catch(Exception e) {
                if(i >= 20) {
                    break;
                }
            }
	}
	return activeDeviceNames.toArray(new String[activeDeviceNames.size()]);
    }
    protected void autodetectFilesystems() {
	LinkedList<String> plainFileSystems = new LinkedList<String>();
	LinkedList<EmbeddedPartitionEntry> embeddedFileSystems = new LinkedList<EmbeddedPartitionEntry>();
	//String skipPrefix = null;
        
	// Look for file systems that sit inside partition systems unsupported by Windows.
	for(int i = 0; i < detectedDeviceNames.length; ++i) {
	    String deviceName = detectedDeviceNames[i];
// 	    System.out.println("Checking if \"" + name + "\" might be an unsupported partition system...");
// 	    System.out.println("  name.startsWith(\"CdRom\") == " + name.startsWith(DEVICE_PREFIX + "CdRom"));
// 	    System.out.println("  name.endsWith(\"Partition0\") == " + name.endsWith("Partition0"));
	    
	    /*
	    if(name.startsWith("CdRom") ||
	       (name.endsWith("Partition0") &&
		!(i+1 < detectedDeviceNames.length && detectedDeviceNames[i+1].endsWith("Partition1"))) ) {
	    */
		// We have an unidentifed partition system at "name"
// 		System.out.println("TRUE!");
            //if(skipPrefix != null && deviceName.startsWith(skipPrefix))
            //    continue;
            //else
            //    skipPrefix = null;
            
	    ReadableRandomAccessStream llf = null;
	    try {
		llf = new WindowsLowLevelIO(DEVICE_PREFIX + deviceName);
		PartitionSystemRecognizer psr = new PartitionSystemRecognizer(llf);
		PartitionSystemRecognizer.PartitionSystemType pst = psr.detectPartitionSystem();
                
                boolean fileSystemFound = false;
                
                if(pst != PartitionSystemRecognizer.PartitionSystemType.NONE_FOUND) {
                    PartitionSystem partSys = psr.getPartitionSystem();
                    Partition[] parts = partSys.getUsedPartitionEntries();
                    for(int j = 0; j < parts.length; ++j) {
                        Partition part = parts[j];
                        PartitionType pt = part.getType();
                        if(pt == PartitionType.APPLE_HFS_CONTAINER || pt == PartitionType.APPLE_HFSX) {
                            FileSystemRecognizer fsr = new FileSystemRecognizer(llf, part.getStartOffset());
                            if(fsr.isTypeSupported(fsr.detectFileSystem())) {
                                fileSystemFound = true;
                                embeddedFileSystems.add(new EmbeddedPartitionEntry(deviceName, j, part));
                            }
                        }
                    }
                }
                
                if(!fileSystemFound && deviceName.endsWith("Partition0")) {
		    FileSystemRecognizer fsr = new FileSystemRecognizer(llf, 0);
		    if(fsr.isTypeSupported(fsr.detectFileSystem()))
			plainFileSystems.add(deviceName);
		}
                /* If we found file systems in embedded partition systems,
                 * ignore windows-detected partitions if the embedded partition
                 * system is at Partition0. */
                //else if(deviceName.endsWith("Partition0"))
                //    skipPrefix = deviceName.substring(0, deviceName.length()-1);
		llf.close();
	    } catch(Exception e) {
		System.out.println("INFO: Non-critical exception while detecting partition system at \"" +
				   DEVICE_PREFIX + deviceName + "\": " + e.toString());
		if(llf != null) {
		    FileSystemRecognizer fsr = new FileSystemRecognizer(llf, 0);
		    if(fsr.isTypeSupported(fsr.detectFileSystem()))
			plainFileSystems.add(deviceName);
		    llf.close();
		}
	    }
	}
	
	if(plainFileSystems.size() >= 1 || embeddedFileSystems.size() >= 1) {
	    String[] plainStrings = plainFileSystems.toArray(new String[plainFileSystems.size()]);
	    String[] embeddedStrings = new String[embeddedFileSystems.size()];
	    int i = 0;
	    for(EmbeddedPartitionEntry cur : embeddedFileSystems) {
		embeddedStrings[i++] = cur.toString();
            }
	    
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
			EmbeddedPartitionEntry embeddedInfo = embeddedFileSystems.get(selectedIndex);
			if(embeddedInfo == null)
			    throw new RuntimeException("Internal error again.");
			
			if(embeddedInfo.partition instanceof APMPartition) {
			    ReadableRandomAccessStream llf = new WindowsLowLevelIO(DEVICE_PREFIX + embeddedInfo.deviceName);
			    DriverDescriptorRecord ddr = new DriverDescriptorRecord(llf, 0);
			    ApplePartitionMap apm = new ApplePartitionMap(llf, ddr.getSbBlkSize()*1, ddr.getSbBlkSize());
			    Partition p = apm.getPartitionEntry((int)embeddedInfo.partitionNumber);
			    resultCreatePath = DEVICE_PREFIX + selectedValue.toString();
			    result = new ReadableConcatenatedStream(llf, p.getStartOffset(), p.getLength());
			    setVisible(false);
			}
			else if(embeddedInfo.partition instanceof GPTEntry) {
                            ReadableRandomAccessStream llf = new WindowsLowLevelIO(DEVICE_PREFIX + embeddedInfo.deviceName);
			    GUIDPartitionTable gpt = new GUIDPartitionTable(llf, 0);
			    Partition p = gpt.getPartitionEntry((int)embeddedInfo.partitionNumber);
			    resultCreatePath = DEVICE_PREFIX + selectedValue.toString();
			    result = new ReadableConcatenatedStream(llf, p.getStartOffset(), p.getLength());
			    setVisible(false);
                        }
			else if(embeddedInfo.partition instanceof MBRPartition) {
                            ReadableRandomAccessStream llf = new WindowsLowLevelIO(DEVICE_PREFIX + embeddedInfo.deviceName);
			    MBRPartitionTable mbt = new MBRPartitionTable(llf, 0);
                            Partition p = mbt.getPartitionEntry((int)embeddedInfo.partitionNumber);
			    resultCreatePath = DEVICE_PREFIX + selectedValue.toString();
			    result = new ReadableConcatenatedStream(llf, p.getStartOffset(), p.getLength());
			    setVisible(false);
                        }
                        else
                            throw new RuntimeException("Unexpected partition system: " +
                                    embeddedInfo.partition.getClass());
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
    
    private static final class EmbeddedPartitionEntry {
        public final String deviceName;
        public final long partitionNumber;
        public final Partition partition;
        
        public EmbeddedPartitionEntry(String deviceName, long partitionNumber,
                Partition partition) {
            this.deviceName = deviceName;
            this.partitionNumber = partitionNumber;
            this.partition = partition;
        }
        
        private String getPartitionSystemString() {
            if(partition instanceof APMPartition)
                return "APM";
            else if(partition instanceof GPTEntry)
                return "GPT";
            else if(partition instanceof EBRPartition)
                return "EBR";
            else if(partition instanceof MBRPartition)
                return "MBR";
            else
                return "Unknown partition system";
        }
        
        @Override
        public String toString() {
            return deviceName + "[" + getPartitionSystemString() + ":Partition" +
                    partitionNumber + "]";
        }
    }
}
