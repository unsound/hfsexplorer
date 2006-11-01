import org.catacombae.hfsexplorer.gui.SelectWindowsDevicePanel;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class SelectWindowsDeviceDialog extends JDialog {
    private static final String DEVICE_PREFIX = "\\\\?\\GLOBALROOT\\Device\\";

    private SelectWindowsDevicePanel guiPanel;
    
    // Shortcuts to variables in guiPanel
    private JRadioButton selectDeviceButton;
    private JRadioButton specifyDeviceNameButton;
    private JButton loadButton;
    private JButton cancelButton;
    private JTextField specifyDeviceNameField;
    private JComboBox detectedDevicesCombo;

    // Additional gui stuff
    private ButtonGroup selectSpecifyGroup;
    
    private String result = null;
    
    public SelectWindowsDeviceDialog(Frame owner, boolean modal, String title) {
	super(owner, modal);
	setTitle(title);
	
	this.guiPanel = new SelectWindowsDevicePanel();
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
	int detectedDevices = detectDevices(detectedDevicesCombo);
	if(detectedDevices > 0) {
	    detectedDevicesCombo.setSelectedIndex(0);
	    specifyDeviceNameField.setText(DEVICE_PREFIX + detectedDevicesCombo.getSelectedItem().toString());
	}
	
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
	
	JPanel container = new JPanel();
	container.setLayout(new BorderLayout());
	container.setBorder(new EmptyBorder(5, 5, 5, 5));
	container.add(guiPanel, BorderLayout.CENTER);
	
	add(container, BorderLayout.CENTER);
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
     * @return the number of devices found
     */
    protected int detectDevices(JComboBox deviceCombo) {
	int numDevices = 0;
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
		    deviceCombo.addItem(currentDevice);
		    ++numDevices;
		} catch(Exception e) {}
	    }
	}

	// ...and 20 CD-ROMs
	for(int i = 0; i < 20; ++i) {
	    try {
		String currentDevice = "CdRom"+i;
		WindowsLowLevelIO curFile = new WindowsLowLevelIO(DEVICE_PREFIX + currentDevice);
		curFile.close();
		deviceCombo.addItem(currentDevice);
		++numDevices;
	    } catch(Exception e) {}
	}
	return numDevices;
    }
}
