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

import org.catacombae.hfsexplorer.partitioning.*;
import org.catacombae.hfsexplorer.types.*;
import org.catacombae.hfsexplorer.win32.WindowsLowLevelIO;
import org.catacombae.hfsexplorer.gui.FilesystemBrowserPanel;
import org.catacombae.hfsexplorer.gui.JournalInfoBlockPanel;
import java.util.*;
import java.io.*;
import java.net.URL;
import java.text.DateFormat;
//import java.awt.*;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.table.*;
import javax.swing.event.*;

public class FileSystemBrowserWindow extends JFrame {
    private static final String TITLE_STRING = "HFSExplorer v" + HFSExplorer.VERSION;
    private static final String[] VERSION_INFO_DICTIONARY = {
	"http://www.typhontools.cjb.net/hfsx/version.sdic.txt",
	"http://hem.bredband.net/unsound/hfsx/version.sdic.txt"
    };
    
    private static class ObjectContainer<A> {
	public A o;
	public ObjectContainer(A o) { this.o = o; }
    }
    /** Aggregation class for storage in the first column of fileTable. */
    private static class RecordContainer {
	private HFSPlusCatalogLeafRecord rec;
	public RecordContainer(HFSPlusCatalogLeafRecord rec) {
	    this.rec = rec;
	}
	public HFSPlusCatalogLeafRecord getRecord() { return rec; }
	public String toString() { return rec.getKey().getNodeName().toString(); }
    }
    
    private FilesystemBrowserPanel fsbPanel;
    
    // Fast accessors for the corresponding variables in org.catacombae.hfsexplorer.gui.FilesystemBrowserPanel
    private JTable fileTable;
    private final JScrollPane fileTableScroller;
    private JTree dirTree;
    private JTextField addressField;
    private JButton backButton;
    private JButton goButton;
    private JButton extractButton;
    private JLabel statusLabel;
    
    // Focus timestamps (for determining what to extract)
    private long fileTableLastFocus = 0;
    private long dirTreeLastFocus = 0;

    // For determining the standard layout size of the columns in the table
    private int totalColumnWidth = 0;

    // Communication between adjustColumnsWidths and the column listener
    private final boolean[] disableColumnListener = { false };
    private final ObjectContainer<int[]> lastWidths = new ObjectContainer<int[]>(null);

    
    private final JFileChooser fileChooser = new JFileChooser();
    private final Vector<String> colNames = new Vector<String>();
    private final DefaultTableModel tableModel;
    private HFSFileSystemView fsView;
    
    private static class RecordNodeStorage {
	private HFSPlusCatalogLeafRecord parentRecord;
	private HFSPlusCatalogLeafRecord threadRecord = null;
	public RecordNodeStorage(HFSPlusCatalogLeafRecord parentRecord) {
	    this.parentRecord = parentRecord;
	    if(parentRecord.getData().getRecordType() != HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER)
		throw new IllegalArgumentException("Illegal type for record data.");
	}
	public HFSPlusCatalogLeafRecord getRecord() { return parentRecord; }
	public HFSPlusCatalogLeafRecord getThread() { return threadRecord; }
	public void setThread(HFSPlusCatalogLeafRecord threadRecord) {
	    if(threadRecord.getData().getRecordType() != HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER_THREAD)
		throw new IllegalArgumentException("Illegal type for thread data.");
	    this.threadRecord = threadRecord;
	}
	public String toString() {
	    HFSPlusCatalogLeafRecordData recData = parentRecord.getData();
	    return parentRecord.getKey().getNodeName().toString();
	}
    }
    
    public static class NoLeafMutableTreeNode extends DefaultMutableTreeNode {
	public NoLeafMutableTreeNode(Object o) { super(o); }
	/** Hack to avoid that JTree paints leaf nodes. We have no leafs, only dirs. */
	public boolean isLeaf() { return false; }
    }
    
    public FileSystemBrowserWindow() {
	super(TITLE_STRING);
	fsbPanel = new FilesystemBrowserPanel();
	fileTable = fsbPanel.fileTable;
	fileTableScroller = fsbPanel.fileTableScroller;
	dirTree = fsbPanel.dirTree;
	addressField = fsbPanel.addressField;
	backButton = fsbPanel.backButton;
	goButton = fsbPanel.goButton;
	extractButton = fsbPanel.extractButton;
	statusLabel = fsbPanel.statusLabel;
	JButton infoButton = fsbPanel.infoButton;

	// UI Features for these are not implemented yet.
	backButton.setEnabled(false);
	//addressField.setEnabled(false);
	//goButton.setEnabled(false);
	
	extractButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    if(fsView != null)
			actionExtractToDir();
		    else
			JOptionPane.showMessageDialog(FileSystemBrowserWindow.this, "No file system loaded.",
						      "Error", JOptionPane.ERROR_MESSAGE);
		}
	    });
	
	infoButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    if(fsView != null)
			actionGetInfo();
		    else
			JOptionPane.showMessageDialog(FileSystemBrowserWindow.this, "No file system loaded.",
						      "Error", JOptionPane.ERROR_MESSAGE);
		}
	    });
	goButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    if(fsView != null)
			actionGotoDir();
		    else
			JOptionPane.showMessageDialog(FileSystemBrowserWindow.this, "No file system loaded.",
						      "Error", JOptionPane.ERROR_MESSAGE);
		}
	    });
	addressField.addKeyListener(new KeyAdapter() {
		public void keyPressed(KeyEvent e) {
		    if(fsView != null) {
			if(e.getKeyCode() == KeyEvent.VK_ENTER)
			    actionGotoDir();
		    }
		    else
			JOptionPane.showMessageDialog(FileSystemBrowserWindow.this, "No file system loaded.",
						      "Error", JOptionPane.ERROR_MESSAGE);
		}
	    });
	
	final Class recordContainerClass = new RecordContainer(null).getClass();
	final Class objectClass = new Object().getClass();
	colNames.add("Name");
	colNames.add("Size");
	colNames.add("Type");
	colNames.add("Date Modified");
	colNames.add("");
	//Vector<Vector<String>> = new Vector<Vector<String>>();
	tableModel = new DefaultTableModel(colNames, 0)  {
		public boolean isCellEditable(int rowIndex, int columnIndex) {
		    return false;
		}
 	    };
	
	fileTable.setModel(tableModel);
	fileTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	// AUTO_RESIZE_SUBSEQUENT_COLUMNS AUTO_RESIZE_OFF AUTO_RESIZE_LAST_COLUMN
	fileTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	fileTable.getColumnModel().getColumn(0).setPreferredWidth(180);
	fileTable.getColumnModel().getColumn(1).setPreferredWidth(96);
	fileTable.getColumnModel().getColumn(2).setPreferredWidth(120);
	fileTable.getColumnModel().getColumn(3).setPreferredWidth(120);
	fileTable.getColumnModel().getColumn(4).setPreferredWidth(0);
	totalColumnWidth = 180+96+120+120;
	fileTable.getColumnModel().getColumn(4).setMinWidth(0);
	fileTable.getColumnModel().getColumn(4).setResizable(false);

	TableColumnModelListener columnListener = new TableColumnModelListener() {
		private boolean locked = false;
		private int[] w1 = null;
		//public int[] lastWidths = null;
		public void columnAdded(TableColumnModelEvent e) { /*System.out.println("columnAdded");*/ }
		public void columnMarginChanged(ChangeEvent e) {
		    if(disableColumnListener[0])
			return;
		    synchronized(this) {
			if(!locked)
			    locked = true;
			else {
// 			    System.err.println("    BOUNCING!");
			    return;
			}
		    }
// 		    System.err.print("columnMarginChanged");
// 		    System.err.print("  Width diff:");
 		    int columnCount = fileTable.getColumnModel().getColumnCount();
		    TableColumn lastColumn = fileTable.getColumnModel().getColumn(columnCount-1);
		    if(lastWidths.o == null)
			lastWidths.o = new int[columnCount];
		    if(w1 == null || w1.length != columnCount)
			w1 = new int[columnCount];
		    int diffSum = 0;
		    int currentWidth = 0;
 		    for(int i = 0; i < w1.length; ++i) {
 			w1[i] = fileTable.getColumnModel().getColumn(i).getWidth();
			currentWidth += w1[i];
			int diff = (w1[i] - lastWidths.o[i]);
// 			System.err.print(" " + (w1[i] - lastWidths.o[i]));
			if(i < w1.length-1)
			    diffSum += diff;
			
		    }
		    int lastDiff = (w1[columnCount-1] - lastWidths.o[columnCount-1]);
// 		    System.err.print("  Diff sum: " + diffSum);
// 		    System.err.println("  Last diff: " + (w1[columnCount-1] - lastWidths.o[columnCount-1]));
		    if(lastDiff != -diffSum) {
			int importantColsWidth = currentWidth - w1[columnCount-1];

			//int newLastColumnWidth = lastWidths.o[columnCount-1] - diffSum;
			int newLastColumnWidth = totalColumnWidth-importantColsWidth;
			
			int nextTotalWidth = importantColsWidth + newLastColumnWidth;
// 			System.err.println("  totalColumnWidth=" + totalColumnWidth + " currentWidth=" + currentWidth + " nextTotalWidth=" + nextTotalWidth + " newLast..=" + newLastColumnWidth);
			
			if(newLastColumnWidth >= 0) {
			    if((nextTotalWidth <= totalColumnWidth || diffSum > 0)) {
				//if(currentWidth > totalColumnWidth)
				
// 				System.err.println("  (1)Adjusting last column from " + w1[columnCount-1] + " to " + newLastColumnWidth + "!");
				
				lastColumn.setPreferredWidth(newLastColumnWidth);
				lastColumn.setWidth(newLastColumnWidth);
				//fileTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
// 				System.err.println("  (1)Last column width: " + lastColumn.getWidth() + "  revalidating...");
				fileTableScroller.invalidate();
				fileTableScroller.validate();
// 				System.err.println("  (1)Adjustment complete. Final last column width: " + lastColumn.getWidth());
			    }
// 			    else
// 				System.err.println("  Outside bounds. Idling.");
			}
			else {
			    if(lastColumn.getWidth() != 0) {
				// System.err.println("  (2)Adjusting last column from " + w1[columnCount-1] + " to zero!");
				lastColumn.setPreferredWidth(0);
				lastColumn.setWidth(0);
				//fileTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
// 				System.err.println("  (2)Last column width: " + lastColumn.getWidth() + "  revalidating...");
				fileTableScroller.invalidate();
				fileTableScroller.validate();
// 				System.err.println("  (2)Adjustment complete. Final last column width: " + lastColumn.getWidth());
			    }
			}
		    }

		    
 		    for(int i = 0; i < w1.length; ++i) {
 			w1[i] = fileTable.getColumnModel().getColumn(i).getWidth();
		    }
		    int[] usedArray = lastWidths.o;
		    lastWidths.o = w1;
		    w1 = usedArray; // Switch arrays.
		    
		    synchronized(this) { locked = false; /*System.err.println();*/ }
		}
		public void columnMoved(TableColumnModelEvent e) { /*System.out.println("columnMoved");*/ }
		public void columnRemoved(TableColumnModelEvent e) { /*System.out.println("columnRemoved");*/ }
		public void columnSelectionChanged(ListSelectionEvent e) { /*System.out.println("columnSelectionChanged");*/ }
	    };
	fileTable.getColumnModel().addColumnModelListener(columnListener);
	
	final TableCellRenderer objectRenderer = fileTable.getDefaultRenderer(objectClass);
	fileTable.setDefaultRenderer(objectClass, new TableCellRenderer() {
		private JLabel theOne = new JLabel();
		private JLabel theTwo = new JLabel("", SwingConstants.RIGHT);
		private ImageIcon documentIcon = new ImageIcon(ClassLoader.getSystemResource("res/emptydocument.png"));
		private ImageIcon folderIcon = new ImageIcon(ClassLoader.getSystemResource("res/folder.png"));
		private ImageIcon emptyIcon = new ImageIcon(ClassLoader.getSystemResource("res/nothing.png"));
		
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, final int row, final int column) {
		    if(value instanceof RecordContainer) {
			final Component objectComponent = objectRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);			
			final JLabel jl = theOne;
			HFSPlusCatalogLeafRecord rec = ((RecordContainer)value).getRecord();
			if(rec.getData().getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER)
			    jl.setIcon(folderIcon);
			else if(rec.getData().getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FILE)
			    jl.setIcon(documentIcon);
			else
			    jl.setIcon(emptyIcon);
			jl.setVisible(true);
			Component c = new Component() {
				{
				    jl.setSize(jl.getPreferredSize());
				    jl.setLocation(0, 0);
				    objectComponent.setSize(objectComponent.getPreferredSize());
				    objectComponent.setLocation(jl.getWidth(), 0);
				    setSize(jl.getWidth()+objectComponent.getWidth(), Math.max(jl.getHeight(), objectComponent.getHeight()));
				}
				public void paint(Graphics g) {
 				    jl.paint(g);
				    int translatex = jl.getWidth();
				    g.translate(translatex, 0);
				    objectComponent.paint(g);
				    g.translate(-translatex, 0);
				}
			    };
			return c;
		    }
		    else if(column == 1) {
			theTwo.setText(value.toString());
			return theTwo;
		    }
		    else
			return objectRenderer.getTableCellRendererComponent(table, value, false, false, row, column);
		}
	    });
	fileTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
		private final java.text.DecimalFormat sizeFormat = new java.text.DecimalFormat("0.00");
		public void valueChanged(ListSelectionEvent e) {
		    //statusLabel.setText("Selection (" + e.getFirstIndex() + "-" + e.getLastIndex() + ") has changed. isAdjusting()==" + e.getValueIsAdjusting());
		    int[] selection = fileTable.getSelectedRows();
		    long selectionSize = 0;
		    for(int selectedRow : selection) {
			Object o = tableModel.getValueAt(selectedRow, 0);
			HFSPlusCatalogLeafRecord rec;
			HFSPlusCatalogLeafRecordData recData;
			if(o instanceof RecordContainer) {
			    rec = ((RecordContainer)o).getRecord();
			    HFSPlusCatalogLeafRecordData data = rec.getData();
			    if(data.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FILE &&
			       data instanceof HFSPlusCatalogFile)
				selectionSize += ((HFSPlusCatalogFile)data).getDataFork().getLogicalSize();
			}
		    }
		    String sizeString = (selectionSize >= 1024)?SpeedUnitUtils.bytesToBinaryUnit(selectionSize, sizeFormat):selectionSize + " bytes";
		    statusLabel.setText(selection.length + ((selection.length==1)?" object":" objects") + " selected (" + sizeString + ")");
		}
	    });
	fileTable.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if(e.getClickCount() == 2) {
			int row = fileTable.rowAtPoint(e.getPoint());
			int col = fileTable.columnAtPoint(e.getPoint());
			if(col == 0 && row >= 0) {
			    //System.err.println("Double click at (" + row + "," + col + ")");
			    Object colValue = fileTable.getModel().getValueAt(row, col);
			    //System.err.println("  Value class: " + colValue.getClass());
			    if(colValue instanceof RecordContainer) {
				HFSPlusCatalogLeafRecord rec = ((RecordContainer)colValue).getRecord();
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
				else {
				    actionExtractToDir();
// 				    UnicodeDecomposition ud = new UnicodeDecomposition();
// 				    String filename = rec.getKey().getNodeName().toString();
// 				    System.err.println("Decomposed (unmodified):     \"" + filename + "\"");
// 				    System.err.print("Decomposed (unmodified) hex:");
// 				    for(char c : filename.toCharArray()) System.err.print(" " + Util.toHexStringBE(c));
// 				    System.err.println();
// 				    String composedFilename = ud.compose(filename);
// 				    System.err.println("Composed:                 \"" + composedFilename + "\"");
// 				    System.err.print("Composed hex:            ");
// 				    for(char c : composedFilename.toCharArray()) System.err.print(" " + Util.toHexStringBE(c));
// 				    System.err.println();
// 				    return;
				}
				
				HFSPlusCatalogLeafRecord[] contents = fsView.listRecords(requestedID);
				populateTable(contents);
				fileTableScroller.getVerticalScrollBar().setValue(0);
				
				List<HFSPlusCatalogLeafRecord> path = fsView.getPathTo(requestedID);
// 				System.err.println("Path:");
// 				for(HFSPlusCatalogLeafRecord clf : path)
// 				    clf.getKey().print(System.err, "  ");
				
				path.remove(0); // The first element will be the root, and setTreePath doesn't want the root
				path.remove(path.size()-1); // The last element will be the thread record for the folder.
				TreePath selectionPath = dirTree.getSelectionPath();
				dirTree.expandPath(selectionPath);
				setTreePath(path);
				//TreePath selectionPath = dirTree.getSelectionPath();
				//dirTree.expandPath(selectionPath);
				selectionPath = dirTree.getSelectionPath();
				Object[] userObjectPath = selectionPath.getPath();
				StringBuilder pathString = new StringBuilder("/");
				for(int i = 1; i < userObjectPath.length; ++i) {
				    pathString.append(userObjectPath[i].toString());
				    pathString.append("/");
				}
				addressField.setText(pathString.toString()); 
			    }
			    else
				throw new RuntimeException("Invalid type in column 0 in fileTable!");
			}
		    }
		}
	    });
	
	DefaultMutableTreeNode rootNode = new NoLeafMutableTreeNode("No file system loaded");
	DefaultTreeModel model = new DefaultTreeModel(rootNode);
	dirTree.setModel(model);
	dirTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

	dirTree.addTreeSelectionListener(new TreeSelectionListener() {
		public void valueChanged(TreeSelectionEvent e) {
		    TreePath tp = e.getPath();
		    Object obj = tp.getLastPathComponent();
		    if(obj instanceof NoLeafMutableTreeNode) {
			Object obj2 = ((NoLeafMutableTreeNode)obj).getUserObject();
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
			    fileTableScroller.getVerticalScrollBar().setValue(0);
			    
			    StringBuilder path = new StringBuilder("/");
			    Object[] userObjectPath = ((NoLeafMutableTreeNode)obj).getUserObjectPath();
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
		    if(obj instanceof NoLeafMutableTreeNode) {
			Object obj2 = ((NoLeafMutableTreeNode)obj).getUserObject();
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
			    populateNode(((NoLeafMutableTreeNode)obj), contents);
			}
		    }
		}
		
		public void treeWillCollapse(TreeExpansionEvent e) {}
	    });
	
	// Focus monitoring
	fileTable.addFocusListener(new FocusListener() {
		public void focusGained(FocusEvent e) {
		    //System.err.println("fileTable gained focus!");
		    fileTableLastFocus = System.nanoTime();
		    //dirTree.clearSelection();
		}
		public void focusLost(FocusEvent e) {}
	    });
	dirTree.addFocusListener(new FocusListener() {
		public void focusGained(FocusEvent e) {
		    //System.err.println("dirTree gained focus!");
		    dirTreeLastFocus = System.nanoTime();
		    //fileTable.clearSelection(); // I'm unsure whether this behaviour is desired
		}
		public void focusLost(FocusEvent e) {}
	    });
	
 	fileTableScroller.addComponentListener(new ComponentAdapter() {
 		public void componentResized(ComponentEvent e) {
 		    //System.err.println("Component resized");
		    adjustTableWidth();
		}
 	    });
	
	
	// Menus
	JMenuItem loadFSFromDeviceItem = null;
	if(System.getProperty("os.name").toLowerCase().startsWith("windows") &&
	   System.getProperty("os.arch").toLowerCase().equals("x86")) {
	    // Only for Windows systems...
	    loadFSFromDeviceItem = new JMenuItem("Load file system from device...");
	    loadFSFromDeviceItem.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent ae) {
			SelectWindowsDeviceDialog deviceDialog = 
			    new SelectWindowsDeviceDialog(FileSystemBrowserWindow.this,
							  true,
							  "Load file system from device");
			deviceDialog.setVisible(true);
			String pathName = deviceDialog.getPathName();
			try {
			    if(pathName != null)
				loadFS(new WindowsLowLevelIO(pathName), pathName);
			} catch(Exception e) {
			    e.printStackTrace();
			    JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
							  "Could not read contents of partition!",
							  "Error", JOptionPane.ERROR_MESSAGE);
			}
		    }
		});
	    loadFSFromDeviceItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
 	}
	
	JMenuItem loadFSFromFileItem = new JMenuItem("Load file system from file...");
	loadFSFromFileItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
		    //JFileChooser fileChooser = new JFileChooser();
		    fileChooser.setMultiSelectionEnabled(false);
		    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		    if(fileChooser.showOpenDialog(FileSystemBrowserWindow.this) == 
		       JFileChooser.APPROVE_OPTION) {
			try {
			    String pathName = fileChooser.getSelectedFile().getCanonicalPath();
			    loadFS(pathName);
			} catch(IOException ioe) {
			    ioe.printStackTrace();
			    JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
							  "Count not resolve pathname!",
							  "Error", JOptionPane.ERROR_MESSAGE);
			} catch(Exception e) {
			    e.printStackTrace();
			    JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
							  "Could not read contents of partition!",
							  "Error", JOptionPane.ERROR_MESSAGE);
			}
		    }
		}
	    });
	loadFSFromFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	
	JMenuItem openUDIFItem = new JMenuItem("Open UDIF disk image (.dmg)...");
	openUDIFItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
		    //JFileChooser fileChooser = new JFileChooser();
		    fileChooser.setMultiSelectionEnabled(false);
		    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		    SimpleFileFilter sff = new SimpleFileFilter();
		    sff.addExtension("dmg");
		    sff.setDescription("Disk images");
		    fileChooser.setFileFilter(sff);
		    int res = fileChooser.showOpenDialog(FileSystemBrowserWindow.this);
		    if(res == JFileChooser.APPROVE_OPTION) {
			try {
			    File selectedFile = fileChooser.getSelectedFile();
			    String pathName = selectedFile.getCanonicalPath();
			    loadFSWithUDIFAutodetect(pathName);
// 			    UDIFRandomAccessLLF stream = null;
// 			    try {
// 				stream = new UDIFRandomAccessLLF(pathName);
// 			    }
// 			    catch(Exception e) {
// 				e.printStackTrace();
// 				if(e.getMessage().startsWith("java.lang.RuntimeException: No handler for block type")) {
// 				    JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
// 								  "UDIF file contains unsupported block types!\n" +
// 								  "(The file was probably created with BZIP2 or ADC " + 
// 								  "compression, which is unsupported currently)",
// 								  "Error", JOptionPane.ERROR_MESSAGE);
// 				}
// 				else {
// 				    JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
// 								  "UDIF file unsupported or damaged!",
// 								  "Error", JOptionPane.ERROR_MESSAGE);
// 				}
// 			    }
// 			    if(stream != null)
// 				loadFS(stream, selectedFile.getName());
			} catch(IOException ioe) {
			    ioe.printStackTrace();
			    JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
							  "Count not resolve pathname!",
							  "Error", JOptionPane.ERROR_MESSAGE);
			} catch(Exception e) {
			    e.printStackTrace();
			    JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
							  "Could not read contents of partition!",
							  "Error", JOptionPane.ERROR_MESSAGE);
			}
		    }
		    fileChooser.resetChoosableFileFilters();
		}
	    });
	openUDIFItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	
	JMenuItem exitProgramItem = new JMenuItem("Exit");
	exitProgramItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
		    exitApplication();
		}
	    });
	exitProgramItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	JMenuItem fsInfoItem = new JMenuItem("File system info");
	fsInfoItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
		    if(fsView != null) {
			VolumeInfoWindow infoWindow = new VolumeInfoWindow(fsView);
			infoWindow.setVisible(true);
			HFSPlusVolumeHeader vh = fsView.getVolumeHeader();
			infoWindow.setVolumeFields(vh);
			if(vh.getAttributeVolumeJournaled())
			    infoWindow.setJournalFields(fsView.getJournalInfoBlock());
		    }
		    else
			JOptionPane.showMessageDialog(FileSystemBrowserWindow.this, "No file system loaded.",
						      "Error", JOptionPane.ERROR_MESSAGE);
		}		
	    });
	fsInfoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	
	JMenuItem checkUpdatesItem = new JMenuItem("Check for updates...");
	checkUpdatesItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
		    InputStream infoDictStream = null;
		    for(String s : VERSION_INFO_DICTIONARY) {
			try {
			    infoDictStream = new URL(s).openStream();
			    SimpleDictionaryParser sdp = new SimpleDictionaryParser(infoDictStream);
			    String dictVersion = sdp.getValue("Version");
			    Boolean dictVersionIsHigher = null;
			    char[] dictVersionArray = dictVersion.toCharArray();
			    char[] myVersionArray = HFSExplorer.VERSION.toCharArray();
			    int minArrayLength = Math.min(dictVersionArray.length, myVersionArray.length);
			    for(int i = 0; i < minArrayLength; ++i) {
				if(dictVersionArray[i] > myVersionArray[i]) {
				    dictVersionIsHigher = true;
				    break;
				}
				else if(dictVersionArray[i] < myVersionArray[i]) {
				    dictVersionIsHigher = false;
				    break;
				}
			    }
			    if(dictVersionIsHigher == null)
				dictVersionIsHigher = dictVersionArray.length > myVersionArray.length;
			    if(dictVersionIsHigher)
				JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
							      "There are updates available!\n" +
							      "Latest version is: " + dictVersion, 
							      "Information", JOptionPane.INFORMATION_MESSAGE);
			    else
				JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
							      "There are no updates available.", 
							      "Information", JOptionPane.INFORMATION_MESSAGE);
			    return;
			}
			catch(Exception e) {}
		    }
		    JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
						  "Could not contact version URL.", 
						  "Error", JOptionPane.ERROR_MESSAGE);
		}
	    });
	
	JMenuItem aboutItem = new JMenuItem("About...");
	aboutItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
		    String message = "";
		    message += "HFSExplorer " + HFSExplorer.VERSION + "\n";
		    //message += "Build #" + BuildNumber.BUILD_NUMBER + "\n";
		    message += HFSExplorer.COPYRIGHT + "\n";
 		    for(String notice : HFSExplorer.NOTICES) {
 			message += notice + "\n";
 			// System.out.println("Message now: " + message);
 		    }
		    message += "\n Operating system: " + System.getProperty("os.name") + " " + System.getProperty("os.version");
		    message += "\n Architecture: " + System.getProperty("os.arch");
		    message += "\n Virtual machine: " + System.getProperty("java.vm.vendor") + " " + 
			System.getProperty("java.vm.name") + " "  + System.getProperty("java.vm.version");
		    JOptionPane.showMessageDialog(FileSystemBrowserWindow.this, message, 
						  "About", JOptionPane.INFORMATION_MESSAGE);
		}
	    });
	
	JMenu fileMenu = new JMenu("File");
	if(loadFSFromDeviceItem != null)
	    fileMenu.add(loadFSFromDeviceItem);
	fileMenu.add(loadFSFromFileItem);
	fileMenu.add(openUDIFItem);
	fileMenu.add(exitProgramItem);
	JMenu infoMenu = new JMenu("Info");
	infoMenu.add(fsInfoItem);
	JMenu helpMenu = new JMenu("Help");
	helpMenu.add(checkUpdatesItem);
	helpMenu.add(aboutItem);
	JMenuBar menuBar = new JMenuBar();
	menuBar.add(fileMenu);
	menuBar.add(infoMenu);
	menuBar.add(helpMenu);
	setJMenuBar(menuBar);
	// /Menus
	
	addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent we) {
		    exitApplication();
		}
	    });

	setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	add(fsbPanel, BorderLayout.CENTER);
	pack();
	setLocationRelativeTo(null);
    }
    
    private void exitApplication() {
	setVisible(false);
	if(fsView != null) {
	    fsView.getStream().close();
	}
	System.exit(0);
    }
    
    private void adjustTableWidth() {
	//System.err.println("adjustTableWidth()");
	int columnCount = fileTable.getColumnModel().getColumnCount();
	int[] w1 = new int[columnCount];
	for(int i = 0; i < w1.length; ++i)
	    w1[i] = fileTable.getColumnModel().getColumn(i).getPreferredWidth();
		    
// 	System.err.print("  Widths before =");
// 	for(int width : w1)
// 	    System.err.print(" " + width);
// 	System.err.println();

	disableColumnListener[0] = true;
		    
	fileTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
	fileTableScroller.invalidate();
	//fileTable.invalidate();
	//fileTable.validate();
	fileTableScroller.validate();
	int[] w2 = new int[columnCount];
	int newTotalWidth = 0;
	for(int i = 0; i < columnCount; ++i) {
	    w2[i] = fileTable.getColumnModel().getColumn(i).getWidth();
	    newTotalWidth += w2[i];
	}
	totalColumnWidth = newTotalWidth; // For telling marginChanged what size to adjust to
// 	System.err.println("  totalColumnWidth=" + totalColumnWidth);
	int newLastColumnWidth = newTotalWidth;
	for(int i = 0; i < w1.length-1; ++i)
	    newLastColumnWidth -= w1[i];
	if(newLastColumnWidth < 0)
	    newLastColumnWidth = 0;
	fileTable.getColumnModel().getColumn(columnCount-1).setPreferredWidth(newLastColumnWidth);
	fileTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	fileTableScroller.invalidate();
	fileTableScroller.validate();
// 	System.err.print("  Widths after =");
// 	for(int i = 0; i < columnCount; ++i)
// 	    System.err.print(" " + fileTable.getColumnModel().getColumn(i).getPreferredWidth());
// 	System.err.println();
		    
	lastWidths.o = null;
	disableColumnListener[0] = false;
    }
    
    public void loadFSWithUDIFAutodetect(String filename) {
	LowLevelFile fsFile;
	if(System.getProperty("os.name").toLowerCase().startsWith("windows") &&
	   System.getProperty("os.arch").toLowerCase().equals("x86"))
	    fsFile = new WindowsLowLevelIO(filename);
	else
	    fsFile = new RandomAccessLLF(filename);
	
	System.err.println("Trying to autodetect UDIF structure...");
	if(UDIFRecognizer.isUDIF(fsFile)) {
	    System.err.println("UDIF structure found! Creating stream...");
	    UDIFRandomAccessLLF stream = null;
	    try {
		stream = new UDIFRandomAccessLLF(filename);
	    }
	    catch(Exception e) {
		e.printStackTrace();
		if(e.getMessage().startsWith("java.lang.RuntimeException: No handler for block type")) {
		    JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
						  "UDIF file contains unsupported block types!\n" +
						  "(The file was probably created with BZIP2 or ADC " + 
						  "compression, which is unsupported currently)",
						  "Error", JOptionPane.ERROR_MESSAGE);
		}
		else {
		    JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
						  "UDIF file unsupported or damaged!",
						  "Error", JOptionPane.ERROR_MESSAGE);
		}
		return;
	    }
	    if(stream != null) {
		fsFile.close();
		fsFile = stream;
	    }
	}
	else {
	    System.err.println("UDIF structure not found. Proceeding normally...");
	}
	
	loadFS(fsFile, new File(filename).getName());
    }
    public void loadFS(String filename) {
	LowLevelFile fsFile;
	if(System.getProperty("os.name").toLowerCase().startsWith("windows") &&
	   System.getProperty("os.arch").toLowerCase().equals("x86"))
	    fsFile = new WindowsLowLevelIO(filename);
	else
	    fsFile = new RandomAccessLLF(filename);
	
	loadFS(fsFile, new File(filename).getName());
    }
    public void loadFS(LowLevelFile fsFile, String displayName) {
	
	int blockSize = 0x200; // == 512
	int ddrBlockSize;
	long fsOffset;
	long fsLength;
	
	// Detect partition system
	PartitionSystemRecognizer psRec = new PartitionSystemRecognizer(fsFile);
	PartitionSystem partSys = psRec.getPartitionSystem();
	if(partSys != null) {
	    Partition[] partitions = partSys.getUsedPartitionEntries();
	    if(partitions.length == 0) {
		// Proceed to detect file system
		fsOffset = 0;
		fsLength = fsFile.length();
	    }
	    else {
		Object selectedValue;
		int firstPreferredPartition = 0;
		for(int i = 0; i < partitions.length; ++i) {
		    Partition p = partitions[i];
		    if(p.getType() == Partition.PartitionType.APPLE_HFS) {
			firstPreferredPartition = i;
			break;
		    }
		}
		while(true) {
		    selectedValue = JOptionPane.showInputDialog(this, "Select which partition to read", 
								"Choose " + partSys.getLongName() +" partition", 
								JOptionPane.QUESTION_MESSAGE,
								null, partitions, partitions[firstPreferredPartition]);
		    if(selectedValue != null &&
		       selectedValue instanceof Partition) {
			Partition selectedPartition = (Partition)selectedValue;
			if(selectedPartition.getType() == Partition.PartitionType.APPLE_HFS)
			    break;
			else
			    JOptionPane.showMessageDialog(this, "Can't find handler for partition type \"" + selectedPartition.getType() +
							  "\"", "Unknown partition type", JOptionPane.ERROR_MESSAGE);
		    }
		    else
			return;
		}
		if(selectedValue instanceof Partition) {
		    Partition selectedPartition = (Partition)selectedValue;
		    fsOffset = selectedPartition.getStartOffset();//getPmPyPartStart()+selectedPartition.getPmLgDataStart())*blockSize;
		    fsLength = selectedPartition.getLength();//getPmDataCnt()*blockSize;
		    //System.err.println("DEBUG Selected partition:");
		    //selectedPartition.print(System.err, "  ");
		}
		else
		    throw new RuntimeException("Impossible error!");
	    }
	}
	else {
	    fsOffset = 0;
	    fsLength = fsFile.length();
	}

	// Detect HFS file system
	FileSystemRecognizer fsr = new FileSystemRecognizer(fsFile, fsOffset);
	FileSystemRecognizer.FileSystemType fsType = fsr.detectFileSystem();
	if(fsType == FileSystemRecognizer.FileSystemType.HFS_PLUS) {
	    if(fsView != null) {
		fsView.getStream().close();
	    }
	    fsView = new HFSFileSystemView(fsFile, fsOffset);
	    HFSPlusCatalogLeafRecord rootRecord = fsView.getRoot();
	    HFSPlusCatalogLeafRecord[] rootContents = fsView.listRecords(rootRecord);
	    populateFilesystemGUI(rootRecord, rootContents);
	    setTitle(TITLE_STRING + " - [" + displayName + "]");
	    statusLabel.setText("0 objects selected (0 bytes)");
	    //adjustTableWidth();
	}
	else
	    JOptionPane.showMessageDialog(this, "Invalid HFS type. Program supports (" + FileSystemRecognizer.FileSystemType.HFS_PLUS +
					  "), detected type is (" + fsType + ").",
					  "Unsupported file system type", JOptionPane.ERROR_MESSAGE);
		    
    }

    private void populateFilesystemGUI(HFSPlusCatalogLeafRecord root, HFSPlusCatalogLeafRecord[] contents) {
	DefaultMutableTreeNode rootNode = new NoLeafMutableTreeNode(new RecordNodeStorage(root));
	populateNode(rootNode, contents);
	DefaultTreeModel model = new DefaultTreeModel(rootNode);
	dirTree.setModel(model);

	populateTable(contents);
	addressField.setText("/");
    }
    private void populateNode(DefaultMutableTreeNode rootNode, HFSPlusCatalogLeafRecord[] contents) {
	boolean folderThreadSet = false;
	boolean hasChildren = false;
	Object o = rootNode.getUserObject();
	if(o instanceof RecordNodeStorage) {
	    RecordNodeStorage rootStorage = (RecordNodeStorage)o;
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
		    rootNode.add(new NoLeafMutableTreeNode(new RecordNodeStorage(rec)));
		    if(!hasChildren) hasChildren = true;
		}
		else if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER_THREAD &&
			recData instanceof HFSPlusCatalogThread) {
		    HFSPlusCatalogThread catThread = (HFSPlusCatalogThread)recData;
		    rootStorage.setThread(rec);
		    if(!folderThreadSet) folderThreadSet = true;
		}
		else if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FILE_THREAD &&
			recData instanceof HFSPlusCatalogThread) {
		    HFSPlusCatalogThread catThread = (HFSPlusCatalogThread)recData;
		    fileThreads.addLast(rec);
		}
		else
		    System.err.println("WARNING: Encountered unexpected record type (" + recData.getRecordType() + ")");
	    }
	    if(hasChildren && !folderThreadSet)
		System.err.println("ERROR: Found no folder thread!");
	    if(!fileThreads.isEmpty())
		System.err.println("INFORMATION: Found " + fileThreads.size() + " file threads unexpectedly.");
	}
	else
	    JOptionPane.showMessageDialog(this, "Unexpected type in node user object. Type: " + o.getClass(),
					  "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public void populateTable(HFSPlusCatalogLeafRecord[] contents) {
	while(tableModel.getRowCount() > 0) {
	    tableModel.removeRow(tableModel.getRowCount()-1);
	}
	
	for(HFSPlusCatalogLeafRecord rec : contents) {
	    Vector<Object> currentRow = new Vector<Object>(4);
	    
	    HFSPlusCatalogLeafRecordData recData = rec.getData();
	    if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FILE &&
	       recData instanceof HFSPlusCatalogFile) {
		HFSPlusCatalogFile catFile = (HFSPlusCatalogFile)recData;
		currentRow.add(new RecordContainer(rec));
		currentRow.add(SpeedUnitUtils.bytesToBinaryUnit(catFile.getDataFork().getLogicalSize()));
		currentRow.add("File");
		DateFormat dti = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
		currentRow.add("" + dti.format(catFile.getContentModDateAsDate()));
		currentRow.add("");
	    }
	    else if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER &&
		    recData instanceof HFSPlusCatalogFolder) {
		HFSPlusCatalogFolder catFolder = (HFSPlusCatalogFolder)recData;
		currentRow.add(new RecordContainer(rec));
		currentRow.add("");
		currentRow.add("Folder");
		DateFormat dti = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
		currentRow.add("" + dti.format(catFolder.getContentModDateAsDate()));
		currentRow.add("");
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
	adjustTableWidth();
    }

    private void actionExtractToDir() {
	if(dirTreeLastFocus > fileTableLastFocus) {
	    Object o = dirTree.getLastSelectedPathComponent();
	    //System.err.println(o.toString());
	    if(o == null) {
		JOptionPane.showMessageDialog(this, "No file or folder selected.",
					      "Information", JOptionPane.INFORMATION_MESSAGE);
	    }
	    else if(o instanceof DefaultMutableTreeNode) {
		Object o2 = ((DefaultMutableTreeNode)o).getUserObject();
		if(o2 instanceof RecordNodeStorage) {
		    final HFSPlusCatalogLeafRecord rec = ((RecordNodeStorage)o2).getRecord();
		    //System.err.println(rec.toString());
		    fileChooser.setMultiSelectionEnabled(false);
		    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    fileChooser.setSelectedFiles(new File[0]);
		    if(fileChooser.showDialog(FileSystemBrowserWindow.this, "Extract here") == JFileChooser.APPROVE_OPTION) {
			final File outDir = fileChooser.getSelectedFile();
			final ExtractProgressDialog progress = new ExtractProgressDialog(this);
			Runnable r = new Runnable() {
				public void run() {
				    progress.setDataSize(calculateDataForkSizeRecursive(rec));
				    int errorCount = extract(rec, outDir, progress);
				    if(!progress.cancelSignaled()) {
					if(errorCount == 0)
					    JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
									  "Extraction finished.\n",
									  "Information",
									  JOptionPane.INFORMATION_MESSAGE);
					else
					    JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
									  errorCount + " errors were encountered " +
									  "during the extraction.\n",
									  "Information",
									  JOptionPane.WARNING_MESSAGE);
				    }
				    else
					JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
								      "Extraction was aborted.\n" +
								      "Please remove the extracted files " +
								      "manually.\n",
								      "Aborted extraction",
								      JOptionPane.WARNING_MESSAGE);
				    progress.setVisible(false);
				}
			    };
			new Thread(r).start();
			progress.setVisible(true);
		    }
		}
		else
		   JOptionPane.showMessageDialog(this, "Unexpected data in tree user object. (Internal error, report to developer)" + "\nClass: " + o.getClass().toString(),
						 "Error", JOptionPane.ERROR_MESSAGE); 
	    }
	    else
		JOptionPane.showMessageDialog(this, "Unexpected data in tree model. (Internal error, report to developer)" + "\nClass: " + o.getClass().toString(),
					      "Error", JOptionPane.ERROR_MESSAGE);
	}
	else {
	    int[] selectedRows = fileTable.getSelectedRows();
	    if(selectedRows.length == 0) {
		JOptionPane.showMessageDialog(this, "No file or folder selected.",
					      "Error", JOptionPane.ERROR_MESSAGE);
		return;
	    }
	    else {
		// There is trouble with this approach in OS X... we don't get a proper DIRECTORIES_ONLY dialog. One idea is to use java.awt.FileDialog and see if it works better. (probably not)
		//java.awt.FileDialog fd = new java.awt.FileDialog(FileSystemBrowserWindow.this, "Extract here", SAVE);
		//File oldDir = fileChooser.getCurrentDirectory();
		//JFileChooser fileChooser2 = new JFileChooser();
		//fileChooser.setCurrentDirectory(oldDir);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		//System.err.println("curdir: " + fileChooser.getCurrentDirectory());
		//fileChooser.setSelectedFiles(new File[0]);
		//fileChooser.setCurrentDirectory(fileChooser.getCurrentDirectory());

		if(fileChooser.showDialog(FileSystemBrowserWindow.this, "Extract here") == JFileChooser.APPROVE_OPTION) {
		    final File outDir = fileChooser.getSelectedFile();
		    
		    // Gather all selected records
		    final HFSPlusCatalogLeafRecord[] selectedRecords = new HFSPlusCatalogLeafRecord[selectedRows.length];
		    for(int i = 0; i < selectedRows.length; ++i) {
			int selectedRow = selectedRows[i];
			Object o = tableModel.getValueAt(selectedRow, 0);
			HFSPlusCatalogLeafRecord rec;
			HFSPlusCatalogLeafRecordData recData;
			if(o instanceof RecordContainer) {
			    rec = ((RecordContainer)o).getRecord();
			    selectedRecords[i] = rec;
			}
			else {
			    JOptionPane.showMessageDialog(this, "Unexpected data in table model. (Internal error, report to developer)",
							  "Error", JOptionPane.ERROR_MESSAGE);
			    return;
			}
		    }
		    
		    // Extract
		    final ExtractProgressDialog progress = new ExtractProgressDialog(this);
		    Runnable r = new Runnable() {
			    public void run() {
				progress.setDataSize(calculateDataForkSizeRecursive(selectedRecords));
				int errorCount = extract(selectedRecords, outDir, progress);
				if(!progress.cancelSignaled()) {
				    if(errorCount == 0)
					JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
								      "Extraction finished.\n",
								      "Information",
								      JOptionPane.INFORMATION_MESSAGE);
				    else
					JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
								      errorCount + " errors were encountered " +
								      "during the extraction.\n",
								      "Information",
								      JOptionPane.WARNING_MESSAGE);
				}
				else
				    JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
								  "Extraction was aborted.\n" +
								  "Please remove the extracted files " +
								  "manually.\n",
								  "Aborted extraction",
								  JOptionPane.WARNING_MESSAGE);
				progress.setVisible(false);
			    }
			};
		    new Thread(r).start();
		    progress.setVisible(true);
		}
	    }
	}
    }
    
    private void actionGetInfo() {
	HFSPlusCatalogLeafRecord rec = null;
	if(dirTreeLastFocus > fileTableLastFocus) {
	    Object o = dirTree.getLastSelectedPathComponent();
	    //System.err.println(o.toString());
	    if(o == null) {
		JOptionPane.showMessageDialog(this, "No file or folder selected.",
					      "Information", JOptionPane.INFORMATION_MESSAGE);
	    }
	    else if(o instanceof DefaultMutableTreeNode) {
		Object o2 = ((DefaultMutableTreeNode)o).getUserObject();
		if(o2 instanceof RecordNodeStorage)
		    rec = ((RecordNodeStorage)o2).getRecord();
		else 
		    JOptionPane.showMessageDialog(this, "[actionGetInfo():Tree] Unexpected data in tree model. (Internal " +
						  "error, report to developer)", "Error", JOptionPane.ERROR_MESSAGE);
	    }
	    else
		JOptionPane.showMessageDialog(this, "[actionGetInfo():Tree] Unexpected tree node type! (Internal " +
					      "error, report to developer)", "Error", JOptionPane.ERROR_MESSAGE);
	}
	else {
	    int[] selectedRows = fileTable.getSelectedRows();
	    if(selectedRows.length == 0) {
		JOptionPane.showMessageDialog(this, "No file selected.",
					      "Information", JOptionPane.INFORMATION_MESSAGE);
		return;
	    }
	    else if(selectedRows.length == 1) {
		int selectedRow = selectedRows[0];
		Object o = tableModel.getValueAt(selectedRow, 0);
		if(o instanceof RecordContainer) {
		    rec = ((RecordContainer)o).getRecord();
		}
		else 
		    JOptionPane.showMessageDialog(this, "[actionGetInfo():Table] Unexpected data in table model. " + 
						  "(Internal error, report to developer)",
						  "Error", JOptionPane.ERROR_MESSAGE);
	    }
	    else {
		JOptionPane.showMessageDialog(this, "Please select one file at a time.",
					      "Error", JOptionPane.ERROR_MESSAGE);
	    }
	}

	if(rec != null) {
	    HFSPlusCatalogLeafRecordData recData = rec.getData();
	    if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FILE &&
	       recData instanceof HFSPlusCatalogFile) {
		HFSPlusCatalogFile file = (HFSPlusCatalogFile)recData;
		FileInfoWindow fiw = new FileInfoWindow(rec.getKey().getNodeName().toString());
		fiw.setFields(file);
		fiw.setVisible(true);
	    }
	    else if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER &&
		    recData instanceof HFSPlusCatalogFolder) {
		HFSPlusCatalogFolder folder = (HFSPlusCatalogFolder)recData;
		FolderInfoWindow fiw = new FolderInfoWindow(rec.getKey().getNodeName().toString());
		fiw.setFields(folder);
		fiw.setVisible(true);
	    }
	    else
		JOptionPane.showMessageDialog(this, "[actionGetInfo()] Record data has unexpected type (" +
					      recData.getRecordType() + ").\nReport bug to developer.",
					      "Error", JOptionPane.ERROR_MESSAGE);
	}
    }

    private void actionGotoDir() {
	String requestedPath = addressField.getText();
	String[] components = requestedPath.split("/");
	LinkedList<HFSPlusCatalogLeafRecord> recordPath = new LinkedList<HFSPlusCatalogLeafRecord>();
	HFSPlusCatalogLeafRecord currentRecord = fsView.getRoot();
	HFSPlusCatalogLeafRecordData currentRecordData = currentRecord.getData();
	//recordPath.addLast(currentRecord);
	for(String s : components) {
	    if(!s.trim().equals("")) {
		if(currentRecordData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER &&
		   currentRecordData instanceof HFSPlusCatalogFolder) {
		    HFSPlusCatalogFolder folder = (HFSPlusCatalogFolder) currentRecordData;
		    HFSPlusCatalogLeafRecord nextRecord = fsView.getRecord(folder.getFolderID(), new HFSUniStr255(s));
		    
		    if(nextRecord == null) {
			currentRecord = null;
			break;
		    }
		    else {
			currentRecord = nextRecord;
			currentRecordData = currentRecord.getData();
			recordPath.addLast(currentRecord);
		    }

		}
		else {
		    currentRecord = null;
		    break;
		}
	    }
	}
	
	if(currentRecord == null)
	    JOptionPane.showMessageDialog(this, "Path not found!", "Error", JOptionPane.ERROR_MESSAGE);
	else if(currentRecordData.getRecordType() != HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER)
	    JOptionPane.showMessageDialog(this, "Requested path points to a file. Can only browse folders...", "Error", JOptionPane.ERROR_MESSAGE);
	else {
	    setTreePath(recordPath);
	}
	
    }

    private void setTreePath(List<HFSPlusCatalogLeafRecord> recordPath) {
	Object root = dirTree.getModel().getRoot();
	DefaultMutableTreeNode rootNode;
	if(root instanceof DefaultMutableTreeNode)
	    rootNode = (DefaultMutableTreeNode)root;
	else
	    throw new RuntimeException("Invalid type in tree");
	TreePath treePath = new TreePath(rootNode);
	//System.out.println("Children:");
	
	for(HFSPlusCatalogLeafRecord rec : recordPath) {
	    DefaultMutableTreeNode rootNodeBefore = rootNode;
	    LinkedList<String> debug = new LinkedList<String>();
	    for(Enumeration e = rootNode.children() ; e.hasMoreElements() ;) {
		Object o = e.nextElement();
		if(o instanceof DefaultMutableTreeNode) {
		    DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)o;
		    Object o2 = currentNode.getUserObject();
		    debug.addLast(o2.toString());
		    if(o2.toString().equals(rec.getKey().getNodeName().toString())) {
			rootNode = currentNode;
			treePath = treePath.pathByAddingChild(rootNode);
			break;
		    }
		}
	    }
	    if(rootNode == rootNodeBefore) {
		System.err.println("Record string: \"" + rec.getKey().getNodeName().toString() + "\"");
		System.err.println("Contents:");
		for(String s : debug)
		    System.err.println("    " + s);
		throw new RuntimeException("Could not find record in tree!");
	    }
	}
	
	dirTree.setSelectionPath(treePath);
	
	//JOptionPane.showMessageDialog(this, "Found path, and path is folder! :)\nRoot of the tree has class: " + root.getClass(), "YEA", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /** <code>progressDialog</code> may NOT be null. */
    protected int extract(HFSPlusCatalogLeafRecord rec, File outDir, ExtractProgressDialog progressDialog) {
	return extractRecursive(rec, outDir, progressDialog/*, 0.0, 1.0*/);
    }
    /** <code>progressDialog</code> may NOT be null. */
    protected int extract(HFSPlusCatalogLeafRecord[] recs, File outDir, ExtractProgressDialog progressDialog) {
	int errorCount = 0;
// 	final double step = 1.0/recs.length;
// 	double floor = 0.0;
	for(HFSPlusCatalogLeafRecord rec : recs) {
	    errorCount += extractRecursive(rec, outDir, progressDialog/*, floor, floor+step*/);
	    //floor += step;
	}
	return errorCount;
    }
    private int extractRecursive(HFSPlusCatalogLeafRecord rec, File outDir, ExtractProgressDialog progressDialog/*, double fractionLowLimit, double fractionHighLimit*/) {
	if(progressDialog.cancelSignaled()) {
	    progressDialog.confirmCancel();
	    return 0;
	}
	
	int errorCount = 0;
	HFSPlusCatalogLeafRecordData recData = rec.getData();
	if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FILE &&
	   recData instanceof HFSPlusCatalogFile) {
	    String filename = rec.getKey().getNodeName().toString();
	    //System.out.println("file: \"" + filename + "\" range: " + fractionLowLimit + "-" + fractionHighLimit);
	    progressDialog.updateCurrentFile(filename);
	    //progressDialog.updateTotalProgress(fractionLowLimit);
	    File outFile = new File(outDir, filename);
	    try {
		FileOutputStream fos = new FileOutputStream(outFile);
		fsView.extractDataForkToStream(rec, fos);
		fos.close();
		//JOptionPane.showMessageDialog(this, "The file was successfully extracted!\n",
		//			  "Extraction complete!", JOptionPane.INFORMATION_MESSAGE);
	    } catch(FileNotFoundException fnfe) {
		fnfe.printStackTrace();
		char[] filenameChars = filename.toCharArray();
		System.out.println("Filename in hex (" + filenameChars.length + " UTF-16 characters):");
		System.out.print("  0x");
		for(char c : filenameChars) System.out.print(" " + Util.toHexStringBE(c));
		System.out.println();
		int reply = JOptionPane.showConfirmDialog(this, "Could not create file \"" + filename +
							  "\" in folder:\n  " + outDir.getAbsolutePath() + "\n" +
							  "Do you want to continue? (The file will be skipped)",
							  "Error", JOptionPane.YES_NO_OPTION,
							  JOptionPane.ERROR_MESSAGE);
		if(reply == JOptionPane.NO_OPTION)
		    progressDialog.signalCancel();
		++errorCount;
	    } catch(IOException ioe) {
		String msg = ioe.getMessage();
		int reply = JOptionPane.showConfirmDialog(this, "Could not write to file \"" + filename + 
							  "\" under folder:\n  " + outDir.getAbsolutePath() +
							  (msg!=null?"\nSystem message: \"" + msg + "\"":"") +
							  "\nDo you want to continue?",
							  "I/O Error", JOptionPane.YES_NO_OPTION,
							  JOptionPane.ERROR_MESSAGE);
		if(reply == JOptionPane.NO_OPTION)
		    progressDialog.signalCancel();
		++errorCount;
	    } catch(Exception e) {
		e.printStackTrace();
		String message = "An exception occurred while extracting \"" + filename + "\"!";
		message += "\n  " + e.toString();
		for(StackTraceElement ste : e.getStackTrace())
		    message += "\n    " + ste.toString();
		message += "\n\nThe file has probably not been extracted.";
		int reply = JOptionPane.showConfirmDialog(this, message +
							  "\nDo you want to continue with the extraction?",
							  "Error", JOptionPane.YES_NO_OPTION,
							  JOptionPane.ERROR_MESSAGE);
		if(reply == JOptionPane.NO_OPTION)
		    progressDialog.signalCancel();
		++errorCount;
	    }
	    progressDialog.addDataProgress(((HFSPlusCatalogFile)recData).getDataFork().getLogicalSize());
	}
	else if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER &&
		recData instanceof HFSPlusCatalogFolder) {
	    String dirName = rec.getKey().getNodeName().toString();
	    progressDialog.updateCurrentFile(dirName);
	    HFSCatalogNodeID requestedID;
	    HFSPlusCatalogFolder catFolder = (HFSPlusCatalogFolder)recData;
	    requestedID = catFolder.getFolderID();
	    
	    HFSPlusCatalogLeafRecord[] contents = fsView.listRecords(requestedID);
	    //System.out.println("folder: \"" + dirName + "\" valence: " + contents.length + " range: " + fractionLowLimit + "-" + fractionHighLimit);
	    // We now have the contents of the requested directory
	    File thisDir = new File(outDir, dirName);
	    if(thisDir.mkdir()) {
// 		final double step = (fractionHighLimit-fractionLowLimit)/contents.length;
// 		double floor = fractionLowLimit;
		for(HFSPlusCatalogLeafRecord outRec : contents) {
		    errorCount += extractRecursive(outRec, thisDir, progressDialog/*, floor, floor+step*/);
// 		    floor += step;
		}
	    }
	    else {
		int reply = JOptionPane.showConfirmDialog(this, "Could not create directory:\n  " + 
							  thisDir.getAbsolutePath() + "\nDo you want to " +
							  "continue? (All files under this directory will be " +
							  "skipped)", "Error", JOptionPane.YES_NO_OPTION,
							  JOptionPane.ERROR_MESSAGE);
		if(reply == JOptionPane.NO_OPTION)
		    progressDialog.signalCancel();
		++errorCount;
	    }
	}
// 	else
// 	    System.out.println("thread with range: " + fractionLowLimit + "-" + fractionHighLimit);
	return errorCount;
    }
    
    /** Calculates the complete data size of the trees represented by <code>recs</code>. */
    protected long calculateDataForkSizeRecursive(HFSPlusCatalogLeafRecord[] recs) {
	long totalSize = 0;
	for(HFSPlusCatalogLeafRecord rec : recs)
	    totalSize += calculateDataForkSizeRecursive(rec);
	return totalSize;
    }
    /** Calculates the complete data size of the tree represented by <code>rec</code>. */
    protected long calculateDataForkSizeRecursive(HFSPlusCatalogLeafRecord rec) {
	HFSPlusCatalogLeafRecordData recData = rec.getData();
	if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FILE &&
	   recData instanceof HFSPlusCatalogFile) {
	    return ((HFSPlusCatalogFile)recData).getDataFork().getLogicalSize();
	}
	else if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER &&
		recData instanceof HFSPlusCatalogFolder) {
	    HFSCatalogNodeID requestedID = ((HFSPlusCatalogFolder)recData).getFolderID();
	    HFSPlusCatalogLeafRecord[] contents = fsView.listRecords(requestedID);
	    long totalSize = 0;
	    for(HFSPlusCatalogLeafRecord outRec : contents) {
		totalSize += calculateDataForkSizeRecursive(outRec);
	    }
	    return totalSize;
	}
	else
	    return 0;
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
	FileSystemBrowserWindow fsbWindow = new FileSystemBrowserWindow();
	fsbWindow.setVisible(true);
	if(args.length > 0) {
	    try {
		String pathName = new File(args[0]).getCanonicalPath();
		fsbWindow.loadFSWithUDIFAutodetect(pathName);
	    } catch(Exception ioe) {
		if(ioe.getMessage().equals("Could not open file.")) {
		    JOptionPane.showMessageDialog(fsbWindow, "Failed to open file:\n\"" + args[0] + "\"",
						  "Error", JOptionPane.ERROR_MESSAGE);
		}
		else {
		    ioe.printStackTrace();
		    String msg = "Exception while loading file:\n" + "    \"" + args[0]  + "\"\n" + ioe.toString();
		    for(StackTraceElement ste : ioe.getStackTrace())
			msg += "\n" + ste.toString();
		    JOptionPane.showMessageDialog(fsbWindow, msg, "Exception while loading file", JOptionPane.ERROR_MESSAGE);
		}
	    }
	}
    }
}
