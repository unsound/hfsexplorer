import org.catacombae.hfsexplorer.gui.FilesystemBrowserPanel;
import java.util.*;
import java.io.IOException;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.table.*;
import javax.swing.event.*;

public class FileSystemBrowserWindow extends JFrame {
    
    private FilesystemBrowserPanel fsbPanel;
    
    // Fast accessors for the corresponding variables in org.catacombae.hfsexplorer.gui.FilesystemBrowserPanel
    private JTable fileTable;
    private JTree dirTree;
    private JTextField addressField;
    private JButton goButton;
    
    private final JFileChooser fileChooser = new JFileChooser();
    private final Vector<String> colNames = new Vector<String>();
    private final DefaultTableModel tableModel;
    private HFSFileSystemView fsView;
    
    private static class RecordNodeStorage {
	private HFSPlusCatalogLeafRecord record;
	public RecordNodeStorage(HFSPlusCatalogLeafRecord record) {
	    this.record = record;
	    if(record.getData().getRecordType() != HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER &&
	       record.getData().getRecordType() != HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER_THREAD)
		throw new IllegalArgumentException("Illegal type for record data.");
	}
	public HFSPlusCatalogLeafRecord getRecord() { return record; }
	public String toString() {
	    HFSPlusCatalogLeafRecordData recData = record.getData();
	    if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER)
		return record.getKey().getNodeName().toString();
	    else if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER_THREAD &&
		    recData instanceof HFSPlusCatalogThread)
		return ((HFSPlusCatalogThread)recData).getNodeName().toString();
	    else
		throw new RuntimeException("Illegal type for record data. (Should NOT happen here!)");
	}
    }
    
    private static class DefaultMutableTreeNode2 extends DefaultMutableTreeNode {
	public DefaultMutableTreeNode2(Object o) { super(o); }
	/** Hack to avoid that JTree paints leaf nodes. We have no leafs, only dirs. */
	public boolean isLeaf() { return false; }
    }
    
    public FileSystemBrowserWindow() {
	super("HFSExplorer v" + HFSExplorer.VERSION);
	fsbPanel = new FilesystemBrowserPanel();
	fileTable = fsbPanel.fileTable;
	dirTree = fsbPanel.dirTree;
	addressField = fsbPanel.addressField;
	goButton = fsbPanel.goButton;
	
	// UI Features for these are not implemented yet.
	addressField.setEnabled(false);
	goButton.setEnabled(false);
	
	colNames.add("Name");
	colNames.add("Size");
	colNames.add("Type");
	colNames.add("Date Modified");
	//Vector<Vector<String>> = new Vector<Vector<String>>();
	tableModel = new DefaultTableModel(colNames, 0);
	fileTable.setModel(tableModel);
	fileTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	fileTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	fileTable.getColumnModel().getColumn(0).setPreferredWidth(180);
	fileTable.getColumnModel().getColumn(1).setPreferredWidth(96);
	fileTable.getColumnModel().getColumn(2).setPreferredWidth(120);
	fileTable.getColumnModel().getColumn(3).setPreferredWidth(120);
	
	DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode2("No file system loaded");
// 	rootNode.add(new DefaultMutableTreeNode2("Barn1"));
// 	rootNode.add(new DefaultMutableTreeNode2("Barn2"));
	DefaultTreeModel model = new DefaultTreeModel(rootNode);
	dirTree.setModel(model);

	dirTree.addTreeSelectionListener(new TreeSelectionListener() {
		public void valueChanged(TreeSelectionEvent e) {
// 		    System.out.println("Value changed!");
// 		    System.out.println("  path: " + e.getPath());
// 		    System.out.println("  newLeadSelectionPath: " + e.getNewLeadSelectionPath());
// 		    System.out.println("  oldLeadSelectionPath: " + e.getOldLeadSelectionPath());
// 		    TreePath[] paths = e.getPaths();
// 		    System.out.println("  getPaths().length: " + paths.length);
// 		    for(int i = 0; i < paths.length; ++i)
// 			System.out.println("  getPaths()[" + i + "]: " + paths[i]);
		    TreePath tp = e.getPath();
		    Object obj = tp.getLastPathComponent();
		    if(obj instanceof DefaultMutableTreeNode2) {
			Object obj2 = ((DefaultMutableTreeNode2)obj).getUserObject();
			if(obj2 instanceof RecordNodeStorage) {
			    HFSPlusCatalogLeafRecord rec = ((RecordNodeStorage)obj2).getRecord();
			    HFSPlusCatalogLeafRecordData recData = rec.getData();
			    HFSCatalogNodeID requestedID;
			    if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER &&
			       recData instanceof HFSPlusCatalogFolder) {
				HFSPlusCatalogFolder catFolder = (HFSPlusCatalogFolder)recData;
				requestedID = catFolder.getFolderID();
			    }
			    else if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER_THREAD &&
				    recData instanceof HFSPlusCatalogThread) {
				HFSPlusCatalogThread catThread = (HFSPlusCatalogThread)recData;
				requestedID = rec.getKey().getParentID();
			    }
			    else
				throw new RuntimeException("Invalid type.");
			
			    HFSPlusCatalogLeafRecord[] contents = fsView.listRecords(requestedID);
			    populateTable(contents);
			    
			    StringBuilder path = new StringBuilder("/");
			    Object[] userObjectPath = ((DefaultMutableTreeNode2)obj).getUserObjectPath();
			    for(int i = 1; i < userObjectPath.length; ++i) {
				path.append(userObjectPath[i].toString());
				path.append("/");
			    }
			    addressField.setText(path.toString());
 
			}
		    }
		}
	    });
	dirTree.addTreeWillExpandListener(new TreeWillExpandListener() {
		public void treeWillExpand(TreeExpansionEvent e) 
                    throws ExpandVetoException {
		    //System.out.println("Tree will expand!");
		    TreePath tp = e.getPath();
		    Object obj = tp.getLastPathComponent();
		    if(obj instanceof DefaultMutableTreeNode2) {
			Object obj2 = ((DefaultMutableTreeNode2)obj).getUserObject();
			if(obj2 instanceof RecordNodeStorage) {
			    HFSPlusCatalogLeafRecord rec = ((RecordNodeStorage)obj2).getRecord();
			    HFSPlusCatalogLeafRecordData recData = rec.getData();
			    HFSCatalogNodeID requestedID;
			    if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER &&
			       recData instanceof HFSPlusCatalogFolder) {
				HFSPlusCatalogFolder catFolder = (HFSPlusCatalogFolder)recData;
				requestedID = catFolder.getFolderID();
			    }
			    else if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER_THREAD &&
				    recData instanceof HFSPlusCatalogThread) {
				HFSPlusCatalogThread catThread = (HFSPlusCatalogThread)recData;
				requestedID = rec.getKey().getParentID();
			    }
			    else
				throw new RuntimeException("Invalid type.");
			
			    HFSPlusCatalogLeafRecord[] contents = fsView.listRecords(requestedID);
			    populateNode(((DefaultMutableTreeNode2)obj), contents);
			}
		    }
		}
		
		public void treeWillCollapse(TreeExpansionEvent e) {}
	    });
	
	// Menus
	JMenuItem loadFSFromDeviceItem = null;
	if(System.getProperty("os.name").toLowerCase().startsWith("windows")) {
	    loadFSFromDeviceItem = new JMenuItem("Load file system from device");
	    loadFSFromDeviceItem.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent ae) {
			String pathName = JOptionPane.showInputDialog(FileSystemBrowserWindow.this,
								      "Enter the UNC path for the file system",
								      "\\\\?\\GLOBALROOT\\Device\\Harddisk2\\Partition2");
			loadFS(pathName, false, true);
		    }
		});
	}
	JMenuItem loadAPMFSFromFileItem = new JMenuItem("Load file system from file with APM");
	loadAPMFSFromFileItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
		    //JFileChooser fileChooser = new JFileChooser();
		    fileChooser.setMultiSelectionEnabled(false);
		    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		    if(fileChooser.showOpenDialog(FileSystemBrowserWindow.this) == 
		       JFileChooser.APPROVE_OPTION) {
			try {
			    String pathName = fileChooser.getSelectedFile().getCanonicalPath();
			    loadFS(pathName, true, false);
			} catch(IOException ioe) {
			    ioe.printStackTrace();
			}
		    }
		}
	    });
	
	JMenu fileMenu = new JMenu("File");
	if(loadFSFromDeviceItem != null)
	    fileMenu.add(loadFSFromDeviceItem);
	fileMenu.add(loadAPMFSFromFileItem);
	JMenuBar menuBar = new JMenuBar();
	menuBar.add(fileMenu);
	setJMenuBar(menuBar);
	// /Menus
	
	setDefaultCloseOperation(EXIT_ON_CLOSE);
	add(fsbPanel, BorderLayout.CENTER);
	pack();
	setLocationRelativeTo(null);
    }
    
    public void loadFS(String filename, boolean readAPM, boolean readFromDevice) {
	LowLevelFile fsFile = new WindowsLowLevelIO(filename);
	int blockSize = 0x200; // == 512
	int ddrBlockSize;
	long fsOffset;
	long fsLength;
	if(readAPM) {
	    byte[] firstBlock = new byte[blockSize];
	    fsFile.readFully(firstBlock);
	    DriverDescriptorRecord ddr = new DriverDescriptorRecord(firstBlock, 0);
	    ApplePartitionMap apm = new ApplePartitionMap(fsFile, 0x200, ddr.getSbBlkSize());
	    APMPartition[] partitions = apm.getPartitions();
	    Object selectedValue;
	    while(true) {
		selectedValue = JOptionPane.showInputDialog(this, "Select which partition to read", 
							    "Choose APM partition", JOptionPane.QUESTION_MESSAGE,
							    null, partitions, partitions[0]);
		if(selectedValue != null &&
		   selectedValue instanceof APMPartition) {
		    APMPartition selectedPartition = (APMPartition)selectedValue;
		    String partitionType = selectedPartition.getPmParTypeAsString();
		    if(selectedPartition.getPmParTypeAsString().trim().equals("Apple_HFS"))
			break;
		    else
			JOptionPane.showMessageDialog(this, "Can't find handler for partition type \"" + partitionType + "\"",
						      "Unknown partition type", JOptionPane.ERROR_MESSAGE);
		}
	    }
	    if(selectedValue instanceof APMPartition) {
		APMPartition selectedPartition = (APMPartition)selectedValue;
		fsOffset = (selectedPartition.getPmPyPartStart()+selectedPartition.getPmLgDataStart())*blockSize;
		fsLength = selectedPartition.getPmDataCnt()*blockSize;
	    }
	    else
		throw new RuntimeException("Impossible error!");
	}
	else {
	    fsOffset = 0;
	    fsLength = fsFile.length();
	}
	fsView = new HFSFileSystemView(fsFile, fsOffset);
	HFSPlusCatalogLeafRecord rootRecord = fsView.getRoot();
	HFSPlusCatalogLeafRecord[] rootContents = fsView.listRecords(rootRecord);
	populateFilesystemGUI(rootContents);
    }

    private void populateFilesystemGUI(HFSPlusCatalogLeafRecord[] contents) {
	DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode2("/");
	populateNode(rootNode, contents);
	DefaultTreeModel model = new DefaultTreeModel(rootNode);
	dirTree.setModel(model);

	populateTable(contents);
	addressField.setText("/");
    }
    private void populateNode(DefaultMutableTreeNode rootNode, HFSPlusCatalogLeafRecord[] contents) {
	boolean folderThreadSet = false;
	boolean hasChildren = false;
	LinkedList<HFSPlusCatalogLeafRecord> fileThreads = new LinkedList<HFSPlusCatalogLeafRecord>();
	for(HFSPlusCatalogLeafRecord rec : contents) {
	    Vector<String> currentRow = new Vector<String>(4);
	    
	    HFSPlusCatalogLeafRecordData recData = rec.getData();
	    if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FILE &&
	       recData instanceof HFSPlusCatalogFile) {
		HFSPlusCatalogFile catFile = (HFSPlusCatalogFile)recData;
		if(!hasChildren) hasChildren = true;
	    }
	    else if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER &&
		    recData instanceof HFSPlusCatalogFolder) {
		HFSPlusCatalogFolder catFolder = (HFSPlusCatalogFolder)recData;
		rootNode.add(new DefaultMutableTreeNode2(new RecordNodeStorage(rec)));
		if(!hasChildren) hasChildren = true;
	    }
	    else if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER_THREAD &&
		    recData instanceof HFSPlusCatalogThread) {
		HFSPlusCatalogThread catThread = (HFSPlusCatalogThread)recData;
		rootNode.setUserObject(new RecordNodeStorage(rec));
		if(!folderThreadSet) folderThreadSet = true;
	    }
	    else if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FILE_THREAD &&
		    recData instanceof HFSPlusCatalogThread) {
		HFSPlusCatalogThread catThread = (HFSPlusCatalogThread)recData;
		fileThreads.addLast(rec);
	    }
	    else
		System.out.println("WARNING: Encountered unexpected record type (" + recData.getRecordType() + ")");
	}
	if(hasChildren && !folderThreadSet)
	    System.out.println("ERROR: Found no folder thread!");
	if(!fileThreads.isEmpty())
	    System.out.println("INFORMATION: Found " + fileThreads.size() + " file threads!");
    }
    
    public void populateTable(HFSPlusCatalogLeafRecord[] contents) {
	while(tableModel.getRowCount() > 0) {
	    tableModel.removeRow(tableModel.getRowCount()-1);
	}
	
	for(HFSPlusCatalogLeafRecord rec : contents) {
	    Vector<String> currentRow = new Vector<String>(4);
	    
	    HFSPlusCatalogLeafRecordData recData = rec.getData();
	    if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FILE &&
	       recData instanceof HFSPlusCatalogFile) {
		HFSPlusCatalogFile catFile = (HFSPlusCatalogFile)recData;
		currentRow.add(rec.getKey().getNodeName().toString());
		currentRow.add(catFile.getDataFork().getLogicalSize() + " B");
		currentRow.add("File");
		currentRow.add("" + catFile.getContentModDate());
	    }
	    else if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER &&
		    recData instanceof HFSPlusCatalogFolder) {
		HFSPlusCatalogFolder catFolder = (HFSPlusCatalogFolder)recData;
		currentRow.add(rec.getKey().getNodeName().toString());
		currentRow.add("");
		currentRow.add("Folder");
		currentRow.add("" + catFolder.getContentModDate());
	    }
	    else if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER_THREAD &&
		    recData instanceof HFSPlusCatalogThread) {
		HFSPlusCatalogThread catThread = (HFSPlusCatalogThread)recData;
	    }
	    else if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FILE_THREAD &&
		    recData instanceof HFSPlusCatalogThread) {
		HFSPlusCatalogThread catThread = (HFSPlusCatalogThread)recData;
	    }
	    else
		System.out.println("WARNING: Encountered unexpected record type (" + recData.getRecordType() + ")");
	    if(!currentRow.isEmpty())
		tableModel.addRow(currentRow);
	}
    }
    
    public static void main(String[] args) {
       	try {
	    javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
	    /*
	     * Description of look&feels:
	     *   http://java.sun.com/docs/books/tutorial/uiswing/misc/plaf.html
	     */
	}
	catch(Exception e) {
	    //It's ok. Non-critical.
	}
	new FileSystemBrowserWindow().setVisible(true);
    }
}
