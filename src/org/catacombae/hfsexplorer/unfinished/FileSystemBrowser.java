/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.unfinished;

import org.catacombae.hfsexplorer.*;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.catacombae.hfsexplorer.fsframework.FSEntry;
import org.catacombae.hfsexplorer.fsframework.FSFile;
import org.catacombae.hfsexplorer.fsframework.FSFolder;

/**
 *
 * @author Erik
 */
public class FileSystemBrowser {
    private static class ObjectContainer<A> {
	public A o;
	public ObjectContainer(A o) { this.o = o; }
    }
    
    /** Aggregation class for storage in the first column of fileTable. */
    private static class RecordContainer {
	private FSEntry rec;
	private String composedNodeName;
	private RecordContainer() {}
	public RecordContainer(FSEntry rec) {
	    this.rec = rec;
	    
	    this.composedNodeName = rec.getName();
	}
	public FSEntry getRecord() { return rec; }
        
        @Override public String toString() { return composedNodeName; }
    }
    
    /*
    private static class RecordNodeStorage {
	private HFSPlusCatalogLeafRecord parentRecord;
	private HFSPlusCatalogLeafRecord threadRecord = null;
	private String composedNodeName;

	public RecordNodeStorage(HFSPlusCatalogLeafRecord parentRecord) {
	    this.parentRecord = parentRecord;
	    if(parentRecord.getData().getRecordType() != HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER)
		throw new IllegalArgumentException("Illegal type for record data.");
	    this.composedNodeName = parentRecord.getKey().getNodeName().getUnicodeAsComposedString();
	}
	public HFSPlusCatalogLeafRecord getRecord() { return parentRecord; }
	public HFSPlusCatalogLeafRecord getThread() { return threadRecord; }
	public void setThread(HFSPlusCatalogLeafRecord threadRecord) {
	    if(threadRecord.getData().getRecordType() != HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER_THREAD)
		throw new IllegalArgumentException("Illegal type for thread data.");
	    this.threadRecord = threadRecord;
	}
        @Override
	public String toString() {
	    //HFSPlusCatalogLeafRecordData recData = parentRecord.getData();
	    return composedNodeName;//parentRecord.getKey().getNodeName().getUnicodeAsComposedString();
	}
    }*/
    
    public static class NoLeafMutableTreeNode extends DefaultMutableTreeNode {
	public NoLeafMutableTreeNode(Object o) { super(o); }
	/** Hack to avoid that JTree paints leaf nodes. We have no leafs, only dirs. */
	@Override public boolean isLeaf() { return false; }
    }
    
    private final FileSystemBrowserWindow controller;
    
    private final JTable fileTable;
    private final JScrollPane fileTableScroller;
    private final JTree dirTree;
    
    private final Vector<String> colNames = new Vector<String>();
    private final DefaultTableModel tableModel;
    
    // Focus timestamps (for determining what to extract)
    private long fileTableLastFocus = 0;
    private long dirTreeLastFocus = 0;

    /** For determining the standard layout size of the columns in the table. */
    private int totalColumnWidth = 0;
    
    // Communication between adjustColumnsWidths and the column listener
    private final boolean[] disableColumnListener = { false };
    private final ObjectContainer<int[]> lastWidths = new ObjectContainer<int[]>(null);


    public FileSystemBrowser(JTable iFileTable, JScrollPane iFileTableScroller, JTree iDirTree) {
        this.fileTable = iFileTable;
        this.fileTableScroller = iFileTableScroller;
        this.dirTree = iDirTree;
        
        	final Class objectClass = new Object().getClass();
	colNames.add("Name");
	colNames.add("Size");
	colNames.add("Type");
	colNames.add("Date Modified");
	colNames.add("");
	
	tableModel = new DefaultTableModel(colNames, 0)  {
                @Override
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
			FSEntry rec = ((RecordContainer)value).getRecord();
			if(rec instanceof FSFolder)
			    jl.setIcon(folderIcon);
			else if(rec instanceof FSFile)
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
                                @Override
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
			
			if(o instanceof RecordContainer) {
			    FSEntry rec = ((RecordContainer)o).getRecord();
			    if(rec instanceof FSFile)
				selectionSize += ((FSFile)rec).getSize();
			}
		    }
		    String sizeString = (selectionSize >= 1024)?SpeedUnitUtils.bytesToBinaryUnit(selectionSize, sizeFormat):selectionSize + " bytes";
		    controller.setStatusLabelText(selection.length + ((selection.length==1)?" object":" objects") + " selected (" + sizeString + ")");
		}
	    });
	fileTableScroller.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
		    int row = fileTable.rowAtPoint(e.getPoint());
		    if(row == -1) // If we click outside the table, clear selection in table
			fileTable.clearSelection();
		}
	    });
	fileTable.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
		    if(e.getButton() == MouseEvent.BUTTON3) {
			int row = fileTable.rowAtPoint(e.getPoint());
			int col = fileTable.columnAtPoint(e.getPoint());
			if(col == 0 && row >= 0) {
			    // These lines are here because right-clicking doesn't change focus or selection
			    fileTable.clearSelection();
			    fileTable.changeSelection(row, col, false, false);
			    fileTable.requestFocus();
			    
			    JPopupMenu jpm = new JPopupMenu();
                            
			    JMenuItem infoItem = new JMenuItem("Information");
			    infoItem.addActionListener(new ActionListener() {
				    public void actionPerformed(ActionEvent e) {
					controller.actionGetInfo();
				    }
				});
			    jpm.add(infoItem);
                            
			    JMenuItem dataExtractItem = new JMenuItem("Extract data");
			    dataExtractItem.addActionListener(new ActionListener() {
				    public void actionPerformed(ActionEvent e) {
					controller.actionExtractToDir(true, false);
				    }
				});
			    jpm.add(dataExtractItem);
                            
			    JMenuItem resExtractItem = new JMenuItem("Extract resource fork(s)");
			    resExtractItem.addActionListener(new ActionListener() {
				    public void actionPerformed(ActionEvent e) {
					controller.actionExtractToDir(false, true);
				    }
				});
			    jpm.add(resExtractItem);
                            
			    JMenuItem bothExtractItem = new JMenuItem("Extract data and resource fork(s)");
			    bothExtractItem.addActionListener(new ActionListener() {
				    public void actionPerformed(ActionEvent e) {
					controller.actionExtractToDir(true, true);
				    }
				});
			    jpm.add(bothExtractItem);
                            
                            jpm.show(fileTable, e.getX(), e.getY());
			}
		    }
		    else if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
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
				else if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FILE &&
					recData instanceof HFSPlusCatalogFile) {
				    if(true)
					controller.actionDoubleClickFile(rec);
				    else { // Some normalization testcode, currently disabled.
					UnicodeNormalizationToolkit ud = UnicodeNormalizationToolkit.getDefaultInstance();
					String filename = rec.getKey().getNodeName().toString();
					System.err.println("Decomposed (unmodified):     \"" + filename + "\"");
					System.err.print("Decomposed (unmodified) hex:");
					for(char c : filename.toCharArray()) System.err.print(" " + Util.toHexStringBE(c));
					System.err.println();
					String composedFilename = ud.compose(filename);
					System.err.println("Composed:                 \"" + composedFilename + "\"");
					System.err.print("Composed hex:            ");
					for(char c : composedFilename.toCharArray()) System.err.print(" " + Util.toHexStringBE(c));
					System.err.println();
				    }
				    return;
				}
				else throw new RuntimeException("recData instanceof " + recData.getClass().toString());
				
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
	
	dirTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
		    if(e.getButton() == MouseEvent.BUTTON3) {
			TreePath tp = dirTree.getPathForLocation(e.getX(), e.getY());
			if(tp != null) {
			    dirTree.clearSelection();
			    dirTree.setSelectionPath(tp);
			    dirTree.requestFocus();
			    
			    JPopupMenu jpm = new JPopupMenu();
			    JMenuItem infoItem = new JMenuItem("Information");
			    infoItem.addActionListener(new ActionListener() {
				    public void actionPerformed(ActionEvent e) {
					actionGetInfo();
				    }
				});
			    jpm.add(infoItem);
			    jpm.show(dirTree, e.getX(), e.getY());
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
                @Override
                public void componentResized(ComponentEvent e) {
 		    //System.err.println("Component resized");
		    adjustTableWidth();
		}
 	    });
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
}
