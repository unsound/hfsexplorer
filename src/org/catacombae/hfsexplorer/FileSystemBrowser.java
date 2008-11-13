/*-
 * Copyright (C) 2007-2008 Erik Larsson
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

import java.util.Date;
import java.util.Vector;
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
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
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
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.catacombae.hfsexplorer.gui.FilesystemBrowserPanel;

/**
 * A generalization of the file system browser into a very autonomous component with very few
 * dependencies, so that it can be easily reused in the future.
 * 
 * @author Erik Larsson
 */
public class FileSystemBrowser<A> {
    private final FileSystemProvider<A> controller;
    private final FilesystemBrowserPanel viewComponent;

    private final JTextField addressField;
    private final JButton upButton;
    private final JButton extractButton;
    private final JButton infoButton;
    private final JButton goButton;
    private final JLabel statusLabel;
    private final JTable fileTable;
    private final JScrollPane fileTableScroller;
    private final JTree dirTree;
    
    //private final JPopupMenu treeNodePopupMenu;
    //private final JPopupMenu tableNodePopupMenu;
    
    private final Vector<String> colNames = new Vector<String>();
    private final DefaultTableModel tableModel;
    
    // Focus timestamps (for determining what to extract)
    private long fileTableLastFocus = 0;
    private long dirTreeLastFocus = 0;

    /** For determining the standard layout size of the columns in the table. */
    private int totalColumnWidth = 0;
    
    /** Used for formatting byte size strings, like 234,12 MiB. */
    private final DecimalFormat sizeFormat = new DecimalFormat("0.00");
    
    // Communication between adjustColumnsWidths and the column listener
    private final boolean[] disableColumnListener = { false };
    private final ObjectContainer<int[]> lastWidths = new ObjectContainer<int[]>(null);
    private DefaultTreeModel treeModel;
    
    private final GenericPlaceholder<A> genericPlaceholder = new GenericPlaceholder<A>();
    private TreePath lastTreeSelectionPath = null;
    
    public FileSystemBrowser(FileSystemProvider<A> iController) {
        this.controller = iController;
        this.viewComponent = new FilesystemBrowserPanel();

        this.addressField = viewComponent.addressField;
        this.upButton = viewComponent.upButton;
        this.infoButton = viewComponent.infoButton;
        this.extractButton = viewComponent.extractButton;
        this.goButton = viewComponent.goButton;
        this.statusLabel = viewComponent.statusLabel;
        this.fileTable = viewComponent.fileTable;
        this.fileTableScroller = viewComponent.fileTableScroller;
        this.dirTree = viewComponent.dirTree;

        upButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                actionGotoParentDir();
            }
        });
        extractButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionExtractToDir();
            }
        });

        infoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionGetInfo();
            }
        });
        
        goButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionGotoDir();
            }
        });
        
        addressField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionGotoDir();
            }
        });
        /*
        addressField.addKeyListener(new KeyAdapter() {
                @Override
		public void keyPressed(KeyEvent e) {
                    if(e.getKeyCode() == KeyEvent.VK_ENTER)
                        actionGotoDir();
		}
	    });
        */
        //this.treeNodePopupMenu = controller.createTreeNodePopupMenu();
        //this.tableNodePopupMenu = controller.createTableNodePopupMenu();
        
        final Class objectClass = new Object().getClass();
        colNames.add("Name");
        colNames.add("Size");
        colNames.add("Type");
        colNames.add("Date Modified");
        colNames.add("");

        tableModel = new DefaultTableModel(colNames, 0) {

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        fileTable.setModel(tableModel);
        fileTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        // AUTO_RESIZE_SUBSEQUENT_COLUMNS AUTO_RESIZE_OFF AUTO_RESIZE_LAST_COLUMN
        fileTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        final int WIDTH_NAME_COLUMN = 180;
        final int WIDTH_SIZE_COLUMN = 96;
        final int WIDTH_TYPE_COLUMN = 120;
        final int WIDTH_DATE_COLUMN = 130;
        fileTable.getColumnModel().getColumn(0).setPreferredWidth(WIDTH_NAME_COLUMN);
        fileTable.getColumnModel().getColumn(1).setPreferredWidth(WIDTH_SIZE_COLUMN);
        fileTable.getColumnModel().getColumn(2).setPreferredWidth(WIDTH_TYPE_COLUMN);
        fileTable.getColumnModel().getColumn(3).setPreferredWidth(WIDTH_DATE_COLUMN);
        fileTable.getColumnModel().getColumn(4).setPreferredWidth(0);
        totalColumnWidth = WIDTH_NAME_COLUMN + WIDTH_SIZE_COLUMN + WIDTH_TYPE_COLUMN + WIDTH_DATE_COLUMN;
        fileTable.getColumnModel().getColumn(4).setMinWidth(0);
        fileTable.getColumnModel().getColumn(4).setResizable(false);

        if(Java6Util.isJava6OrHigher()) {
            
            Comparator<?> c = new ComparableComparator();
            ArrayList<Comparator<?>> rowComparators = new ArrayList<Comparator<?>>(5);
            for(int i = 0; i < 5; ++i) // 5 rows currently
                rowComparators.add(c);
            Java6Specific.addRowSorter(fileTable, tableModel, 4, rowComparators);
        }

        TableColumnModelListener columnListener = new TableColumnModelListener() {

            private boolean locked = false;
            private int[] w1 = null;
            //public int[] lastWidths = null;

            @Override
            public void columnAdded(TableColumnModelEvent e) { /*System.out.println("columnAdded");*/ }

            @Override
            public void columnMarginChanged(ChangeEvent e) {
                if (disableColumnListener[0]) {
                    return;
                }
                synchronized (this) {
                    if (!locked)
                        locked = true;
                    else {
// 			    System.err.println("    BOUNCING!");
                        return;
                    }
                }
//              System.err.print("columnMarginChanged");
//              System.err.print("  Width diff:");
 		             int columnCount = fileTable.getColumnModel().getColumnCount();
                TableColumn lastColumn = fileTable.getColumnModel().getColumn(columnCount - 1);
                if (lastWidths.o == null) {
                    lastWidths.o = new int[columnCount];
                }
                if (w1 == null || w1.length != columnCount) {
                    w1 = new int[columnCount];
                }
                int diffSum = 0;
                int currentWidth = 0;
                for (int i = 0; i < w1.length; ++i) {
                    w1[i] = fileTable.getColumnModel().getColumn(i).getWidth();
                    currentWidth += w1[i];
                    int diff = (w1[i] - lastWidths.o[i]);
//                  System.err.print(" " + (w1[i] - lastWidths.o[i]));
                    if (i < w1.length - 1) {
                        diffSum += diff;
                    }

                }
                int lastDiff = (w1[columnCount - 1] - lastWidths.o[columnCount - 1]);
                // System.err.print("  Diff sum: " + diffSum);
                // System.err.println("  Last diff: " + (w1[columnCount-1] - lastWidths.o[columnCount-1]));
                if (lastDiff != -diffSum) {
                    int importantColsWidth = currentWidth - w1[columnCount - 1];

                    //int newLastColumnWidth = lastWidths.o[columnCount-1] - diffSum;
                    int newLastColumnWidth = totalColumnWidth - importantColsWidth;

                    int nextTotalWidth = importantColsWidth + newLastColumnWidth;
                    // System.err.println("  totalColumnWidth=" + totalColumnWidth + " currentWidth=" + currentWidth + " nextTotalWidth=" + nextTotalWidth + " newLast..=" + newLastColumnWidth);

                    if (newLastColumnWidth >= 0) {
                        if ((nextTotalWidth <= totalColumnWidth || diffSum > 0)) {
                            //if(currentWidth > totalColumnWidth)

                            // System.err.println("  (1)Adjusting last column from " + w1[columnCount-1] + " to " + newLastColumnWidth + "!");

                            lastColumn.setPreferredWidth(newLastColumnWidth);
                            lastColumn.setWidth(newLastColumnWidth);
                            //fileTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                            // System.err.println("  (1)Last column width: " + lastColumn.getWidth() + "  revalidating...");
                            fileTableScroller.invalidate();
                            fileTableScroller.validate();
                        // System.err.println("  (1)Adjustment complete. Final last column width: " + lastColumn.getWidth());
                        }
                    // else
                    // System.err.println("  Outside bounds. Idling.");
                    } else {
                        if (lastColumn.getWidth() != 0) {
                            // System.err.println("  (2)Adjusting last column from " + w1[columnCount-1] + " to zero!");
                            lastColumn.setPreferredWidth(0);
                            lastColumn.setWidth(0);
                            //fileTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                            // System.err.println("  (2)Last column width: " + lastColumn.getWidth() + "  revalidating...");
                            fileTableScroller.invalidate();
                            fileTableScroller.validate();
                        // System.err.println("  (2)Adjustment complete. Final last column width: " + lastColumn.getWidth());
                        }
                    }
                }


                for (int i = 0; i < w1.length; ++i) {
                    w1[i] = fileTable.getColumnModel().getColumn(i).getWidth();
                }
                int[] usedArray = lastWidths.o;
                lastWidths.o = w1;
                w1 = usedArray; // Switch arrays.

                synchronized (this) {
                    locked = false; /*System.err.println();*/ }
            }

            @Override
            public void columnMoved(TableColumnModelEvent e) { /*System.out.println("columnMoved");*/ }

            @Override
            public void columnRemoved(TableColumnModelEvent e) { /*System.out.println("columnRemoved");*/ }

            @Override
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
		
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, final int row, final int column) {
                if(value instanceof RecordContainer) {
                    final Component objectComponent = objectRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    final JLabel jl = theOne;
                    Record rec = ((RecordContainer) value).getRecord(genericPlaceholder);
                    
                    switch(rec.getType()) {
                        case FOLDER:
                        case FOLDER_LINK:
                            jl.setIcon(folderIcon);
                            break;
                        case FILE:
                        case FILE_LINK:
                            jl.setIcon(documentIcon);
                            break;
                        case BROKEN_LINK:
                            jl.setIcon(emptyIcon);
                            break;
                        default:
                            throw new RuntimeException("Unhandled RecordType: " + rec.getType());
                    }
                    
                    jl.setVisible(true);
                    Component c = new Component() {
                        {
                            jl.setSize(jl.getPreferredSize());
                            jl.setLocation(0, 0);
                            objectComponent.setSize(objectComponent.getPreferredSize());
                            objectComponent.setLocation(jl.getWidth(), 0);
                            setSize(jl.getWidth() + objectComponent.getWidth(), Math.max(jl.getHeight(), objectComponent.getHeight()));
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
                } else if(column == 1) {
                    theTwo.setText(value.toString());
                    return theTwo;
                } else{
                    return objectRenderer.getTableCellRendererComponent(table, value, false, false, row, column);
                }
            }
        });

        fileTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                /* When the selection in the file table changes, update the
                 * selection status field with the new selection count and
                 * selection size. */

                int[] selection = fileTable.getSelectedRows();

                //Object[] selection = fileTable.getSelection();
                long selectionSize = 0;
                for(int selectedRow : selection) {
                    Object o = fileTable.getValueAt(selectedRow, 0);

                    if(o instanceof RecordContainer) {
                        Record rec = ((RecordContainer) o).getRecord(genericPlaceholder);
                        if(rec.getType() == RecordType.FILE || rec.getType() == RecordType.FILE_LINK)
                            selectionSize += rec.getSize();
                    }
                }
                setSelectionStatus(selection.length, selectionSize);
            }
        });

        fileTableScroller.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                /* If we click outside the table, i.e. in the JScrollPane,
                 * clear selection in table. */

                int row = fileTable.rowAtPoint(e.getPoint());
                if(row == -1)
                    fileTable.clearSelection();
            }
        });

        fileTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON3) {
                    /* When the user clicks the secondary mouse button
                     * (usually the right mouse button) in the table,
                     * possibly open a JPopupMenu with some options. */

                    int row = fileTable.rowAtPoint(e.getPoint());
                    int col = fileTable.columnAtPoint(e.getPoint());
                    if(col == 0 && row >= 0) {
                        /* These lines are here because right-clicking
                         * doesn't change focus or selection. */
                        int[] currentSelection = fileTable.getSelectedRows();
                        if(!Util.contains(currentSelection, row)) {
                            fileTable.clearSelection();
                            fileTable.changeSelection(row, col, false, false);
                        }
                        fileTable.requestFocus();

                        List<Record<A>> selection = getTableSelection();
                        List<Record<A>> selectionParentPath = getRecordPath(lastTreeSelectionPath);
                        /*if(selection.size() != 1)
                            throw new RuntimeException("Right click selection with more than " +
                                    "one entry! (" + selection.size() + " entries)");*/


                        JPopupMenu jpm = controller.getRightClickRecordPopupMenu(selectionParentPath, selection);
                        jpm.show(fileTable, e.getX(), e.getY());
                    }
                }
                else if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
                    /* When the user double-clicks using the primary mouse
                     * button, send the event on to the controller, which
                     * may handle it as it likes. */

                    int row = fileTable.rowAtPoint(e.getPoint());
                    int col = fileTable.columnAtPoint(e.getPoint());
                    if(col == 0 && row >= 0) {
                        //System.err.println("Double click at (" + row + "," + col + ")");
                        Object colValue = fileTable.getValueAt(row, col);
                        //System.err.println("  Value class: " + colValue.getClass());
                        if(colValue instanceof RecordContainer) {
                            Record<A> rec = ((RecordContainer) colValue).getRecord(genericPlaceholder);
                            if(rec.getType() == RecordType.FILE || rec.getType() == RecordType.FILE_LINK) {
                                List<Record<A>> dirPath = getRecordPath(lastTreeSelectionPath);
                                ArrayList<Record<A>> completePath = new ArrayList<Record<A>>(dirPath.size()+1);
                                completePath.addAll(dirPath);
                                completePath.add(rec);
                                controller.actionDoubleClickFile(completePath);
                            }
                            else if(rec.getType() == RecordType.FOLDER || rec.getType() == RecordType.FOLDER_LINK)
                                actionChangeDir(rec);
                        }
                        else
                            throw new RuntimeException("Invalid type in column 0 in fileTable!");
                    }
                }
            }
        });
	
	dirTree.addMouseListener(new MouseAdapter() {
            @Override
        public void mousePressed(MouseEvent e) {
            if(e.getButton() == MouseEvent.BUTTON3 &&
                    controller.isFileSystemLoaded()) {
                TreePath tp = dirTree.getPathForLocation(e.getX(), e.getY());
                if(tp != null) {
                    dirTree.clearSelection();
                    dirTree.setSelectionPath(tp);
                    dirTree.requestFocus();

                    List<Record<A>> recList = Collections.singletonList(getTreeSelection());
                    List<Record<A>> selectionParentPath = getRecordPath(lastTreeSelectionPath.getParentPath());
                    controller.getRightClickRecordPopupMenu(selectionParentPath, recList).show(dirTree,
                            e.getX(), e.getY());
                }
            }
		}
	    });

        setRoot(null);
       	dirTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        
	dirTree.addTreeSelectionListener(new TreeSelectionListener() {
                @Override
		public void valueChanged(TreeSelectionEvent e) {
		    TreePath tp = e.getPath();
                    actionTreeNodeSelected(tp);
                }
	    });
	dirTree.addTreeWillExpandListener(new TreeWillExpandListener() {
                @Override
		public void treeWillExpand(TreeExpansionEvent e) 
                    throws ExpandVetoException {
		    //System.out.println("Tree will expand!");
                    actionExpandDirTreeNode(e.getPath());
		}
		
                @Override
		public void treeWillCollapse(TreeExpansionEvent e) {}

	    });
	
	// Focus monitoring
	fileTable.addFocusListener(new FocusListener() {
                @Override
		public void focusGained(FocusEvent e) {
		    //System.err.println("fileTable gained focus!");
		    fileTableLastFocus = System.nanoTime();
		    //dirTree.clearSelection();
		}

                @Override
		public void focusLost(FocusEvent e) {}
	    });
	dirTree.addFocusListener(new FocusListener() {
                @Override
		public void focusGained(FocusEvent e) {
		    //System.err.println("dirTree gained focus!");
		    dirTreeLastFocus = System.nanoTime();
		    //fileTable.clearSelection(); // I'm unsure whether this behaviour is desired
		}

                @Override
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
    
    /**
     * Action code for the action "go to parent directory" in the file system browser.
     */
    private void actionGotoParentDir() {
        if(ensureFileSystemLoaded()) {
            if(lastTreeSelectionPath.getPathCount() > 1) {
                TreePath parentPath = lastTreeSelectionPath.getParentPath();
                selectInTree(parentPath);
            }
        }
    }

    /**
     * Action code for the action "extract selection to directory" in the file system browser.
     */
    private void actionExtractToDir() {
        if(ensureFileSystemLoaded()) {
            controller.actionExtractToDir(getSelectionParentPath(), getSelection());
        }
    }

    private void actionChangeDir(Record<A> subDir) {
        TreePath currentTreeSelection = lastTreeSelectionPath;
        Object objectToPopulate = lastTreeSelectionPath.getLastPathComponent();
        FolderTreeNode nodeToPopulate;
        if(objectToPopulate instanceof FolderTreeNode) {
            nodeToPopulate = (FolderTreeNode) objectToPopulate;
            
            List<Record<A>> recordPath = getRecordPath(currentTreeSelection);
            
            // First make sure we have updated contents
            populateTreeNodeFromPath(nodeToPopulate, recordPath);
            dirTree.expandPath(lastTreeSelectionPath);
            
            int childCount = treeModel.getChildCount(nodeToPopulate);
            
            Object finalChild = null;
            for(int i = 0; i < childCount; ++i) {
                Object curChild = treeModel.getChild(nodeToPopulate, i);
                if(curChild instanceof FolderTreeNode &&
                        ((FolderTreeNode) curChild).getRecordContainer()
                        .getRecord(genericPlaceholder).getName().equals(subDir.getName())) {
                    TreePath childPath = lastTreeSelectionPath.pathByAddingChild(curChild);
                    //dirTree.expandPath(childPath);
                    selectInTree(childPath);
                    finalChild = curChild;
                    break;
                }
            }
            if(finalChild == null)
                throw new RuntimeException("Selection path to leaf child not found!");
        }
         /*       
        String[] rawPath = new String[recordPath.size()-1+1];
        int i = 0;
        for(Record<A> rec : recordPath) {
            if(i > 0)
                rawPath[i-1] = rec.getName();
            ++i;
        }
        rawPath[i] = subDir.getName();
        
        setCurrentDirectory(rawPath);
          * */
    }
    
    /**
     * Action code for the action "get info about selection" in the file system browser.
     */
    private void actionGetInfo() {
        if(ensureFileSystemLoaded()) {
            controller.actionGetInfo(getSelectionParentPath(), getSelection());
        }
    }

    /**
     * Action code for the action "go to specified directory" in the file system browser.
     */
    private void actionGotoDir() {
        if(ensureFileSystemLoaded()) {
            String targetAddress = addressField.getText();
            String[] addressComponents = controller.parseAddressPath(targetAddress);
            if(addressComponents != null)
                setCurrentDirectory(addressComponents);
            else
                JOptionPane.showMessageDialog(viewComponent, "Invalid pathname.", "Error",
                        JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actionExpandDirTreeNode(TreePath targetNodePath) {
        if(ensureFileSystemLoaded()) {
            try {
                final FolderTreeNode nodeToPopulate;
                {
                    Object objToExpand = targetNodePath.getLastPathComponent();
                    if(objToExpand instanceof FolderTreeNode) {
                        nodeToPopulate = (FolderTreeNode) objToExpand;
                    }
                    else {
                        throw new RuntimeException("Unexpected node class in tree: " +
                                objToExpand.getClass());
                    }
                }

                List<Record<A>> recordPath = getRecordPath(targetNodePath);

                populateTreeNodeFromPath(nodeToPopulate, recordPath);
            } catch(Throwable e) {
                displayUnhandledException(e);
            }
        }
    }
    
    private void actionTreeNodeSelected(TreePath selectionPath) {
        // System.err.println("actionTreeNodeSelected(" + selectionPath.toString() + ");");
        // System.err.println("  path count: " + selectionPath.getPathCount());
        // System.err.println("  type of last component: " + selectionPath.getLastPathComponent().getClass());
        // If we have selected another node type than FolderTreeNode, we don't do anything.
        if(selectionPath.getLastPathComponent() instanceof FolderTreeNode) {
            if(ensureFileSystemLoaded()) {
                try {
                    List<Record<A>> recordPath = getRecordPath(selectionPath);
                    populateTableFromPath(recordPath);
                    lastTreeSelectionPath = selectionPath;
                } catch(Throwable e) {
                    displayUnhandledException(e);
                }
            }
        }
    }
    
    private void displayUnhandledException(Throwable e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(viewComponent, e.getClass() + " while populating " +
                "tree node:\n  " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
                
    private List<Record<A>> getRecordPath(TreePath tp) {
        if(tp == null)
            return null;
        List<Record<A>> recordPath = new ArrayList<Record<A>>(tp.getPathCount());
        for(Object obj : tp.getPath()) {
            if(obj instanceof FolderTreeNode) {
                FolderTreeNode noLeafMutableTreeNode = (FolderTreeNode) obj;
                Object userObj = noLeafMutableTreeNode.getUserObject();
                if(userObj instanceof RecordContainer) {
                    Record<A> rec = ((RecordContainer) userObj).getRecord(genericPlaceholder);

                    if(rec.getType() == RecordType.FOLDER || rec.getType() == RecordType.FOLDER_LINK) {
                        recordPath.add(rec);
                    }
                    else {
                        throw new RuntimeException("Unexpected record type in tree: " +
                                rec.getType());
                    }
                }
                else {
                    throw new RuntimeException("Unexpected user object class in tree: " +
                            userObj.getClass());
                }
            }
            else {
                throw new RuntimeException("Unexpected node class in tree: " + obj.getClass());
            }
        }
        
        return recordPath;
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
    
    private void populateTreeNodeFromPath(FolderTreeNode nodeToPopulate, List<Record<A>> recordPath) {
        List<Record<A>> childRecords =
                controller.getFolderContents(recordPath);
        populateTreeNodeFromContents(nodeToPopulate, childRecords);
    }
    
    private void populateTreeNodeFromContents(FolderTreeNode nodeToPopulate,
            List<Record<A>> childRecords) {
        //System.err.println("populateTreeNodeFromContents called for " + nodeToPopulate.getUserObject().toString());
        
        final Queue<Record<A>> remainingQueue;

        { // Initialize remainingQueue
            final LinkedList<Record<A>> remainingRecords = new LinkedList<Record<A>>();
            for(Record<A> childRecord : childRecords) {
                if(childRecord.getType() == RecordType.FOLDER || childRecord.getType() == RecordType.FOLDER_LINK) {
                    remainingRecords.add(childRecord);
                }
            }
            remainingQueue = remainingRecords;
        }

        final List<FolderTreeNode> currentNodes;
        { // Initialize currentNodes
            currentNodes = new ArrayList<FolderTreeNode>(nodeToPopulate.getChildCount());
            Enumeration en = nodeToPopulate.children();
            while(en.hasMoreElements()) {
                Object o = en.nextElement();
                if(o instanceof FolderTreeNode) {
                    currentNodes.add((FolderTreeNode) o);
                }
                else {
                    throw new RuntimeException("Unexpected child type: " + o.getClass());
                }
            }
        }

        // Sort out all nodes to remove, add or change
        LinkedList<Pair<FolderTreeNode, Record<A>>> nodesToUpdate =
                new LinkedList<Pair<FolderTreeNode, Record<A>>>();
        LinkedList<FolderTreeNode> nodesToRemove = new LinkedList<FolderTreeNode>();
        LinkedList<Integer> insertedRecordIndices = new LinkedList<Integer>();
        int currentIndex = 0;
        for(FolderTreeNode node : currentNodes) {
            String nodeName = node.getRecordContainer().getRecord(genericPlaceholder).getName();

            Record<A> firstRemainingRecord = remainingQueue.peek();
            while(firstRemainingRecord != null &&
                    firstRemainingRecord.getName().compareTo(nodeName) < 0) {
                //recordsToInsert.add(remainingRecords.removeFirst());
                FolderTreeNode newNode =
                        new FolderTreeNode(new RecordContainer(remainingQueue.remove()));
                insertedRecordIndices.add(currentIndex);
                nodeToPopulate.insert(newNode, currentIndex++);
                firstRemainingRecord = remainingQueue.peek();
            }

            if(firstRemainingRecord != null &&
                    firstRemainingRecord.getName().compareTo(nodeName) == 0) {
                nodesToUpdate.add(new Pair<FolderTreeNode, Record<A>>(node, remainingQueue.remove()));
            }
            else {
                nodesToRemove.add(node);
            }
            ++currentIndex;
        }
        while(remainingQueue.peek() != null) {
            FolderTreeNode newNode =
                    new FolderTreeNode(new RecordContainer(remainingQueue.remove()));
            insertedRecordIndices.add(currentIndex);
            nodeToPopulate.insert(newNode, currentIndex++);
        }

        int[] insertedRecordIndicesArray = new int[insertedRecordIndices.size()];
        {
            int i = 0;
            for(int index : insertedRecordIndices) {
                insertedRecordIndicesArray[i++] = index;
            }
        }
        //System.err.println("nodesWereInserted: " + insertedRecordIndicesArray.length);
        if(insertedRecordIndicesArray.length > 0) {
            treeModel.nodesWereInserted(nodeToPopulate, insertedRecordIndicesArray);        // 1. Remove those nodes that should be removed
        }
        {
            FolderTreeNode[] removedChildren = new FolderTreeNode[nodesToRemove.size()];
            int[] removedIndices = new int[removedChildren.length];
            int index = 0;
            for(FolderTreeNode node : nodesToRemove) {
                removedChildren[index] = node;
                removedIndices[index] = nodeToPopulate.getIndex(node);
                if(removedIndices[index] < 0) {
                    throw new RuntimeException("INTERNAL ERROR: Can't find node in nodeToPopulate!");
                }
                ++index;
            }
            for(int i : removedIndices) {
                nodeToPopulate.remove(i);
            }
            //System.err.println("nodesWereRemoved: " + removedIndices.length);
            if(removedIndices.length > 0) {
                treeModel.nodesWereRemoved(nodeToPopulate, removedIndices, removedChildren);
            }
        }

        // 2. Update those nodes that should be updated
        {
            int[] updatedIndices = new int[nodesToUpdate.size()];
            int index = 0;
            for(Pair<FolderTreeNode, Record<A>> p : nodesToUpdate) {
                p.a.setUserObject(new RecordContainer(p.b));
                updatedIndices[index] = nodeToPopulate.getIndex(p.a);
                if(updatedIndices[index] < 0) {
                    throw new RuntimeException("INTERNAL ERROR: Can't find node in nodeToPopulate!");
                }
                ++index;
            }
            //System.err.println("nodesChanged: " + updatedIndices.length);
            if(updatedIndices.length > 0) {
                treeModel.nodesChanged(nodeToPopulate, updatedIndices);
            }
        }
    }
  
    private List<String> asNameList(List<Record<A>> recordList) {
        ArrayList<String> res = new ArrayList<String>();
        for(Record<A> rec : recordList)
            res.add(rec.getName());
        return res;
    }
    
    private void populateTableFromPath(List<Record<A>> folderRecordPath) {
        List<Record<A>> childRecords = controller.getFolderContents(folderRecordPath);
        List<String> nameList = asNameList(folderRecordPath.subList(1, folderRecordPath.size()));
        String displayPath =
                controller.getAddressPath(nameList);
        
        populateTableFromContents(childRecords, displayPath);
    }
    
    private void populateTableFromContents(List<Record<A>> contents, String displayPath) {
        while(tableModel.getRowCount() > 0) {
            tableModel.removeRow(tableModel.getRowCount()-1);
	}
	
        DateFormat dti = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        int i = 0;
	for(Record<A> rec : contents) {
	    Vector<Object> currentRow = new Vector<Object>(4);
	    
            currentRow.add(new RecordContainer(rec));
            currentRow.add(new SizeEntry(rec.getSize()));
            currentRow.add(new RecordTypeEntry(rec.getType()));
            currentRow.add(new DateEntry(rec.getModifyDate(), dti));
            currentRow.add(new IndexEntry(i++));
            
            tableModel.addRow(currentRow);
	}
	adjustTableWidth();
        
        fileTableScroller.getVerticalScrollBar().setValue(0);
        addressField.setText(displayPath);
    }

    /**
     * Returns the JComponent that can be used to display the FileSystemBrowser.
     * 
     * @return the JComponent that can be used to display the FileSystemBrowser.
     */
    public JComponent getViewComponent() {
        return viewComponent;
    }
    
    /**
     * Returns the current user selection as a list of the user objects
     * contained within the records, rather than a list of the records
     * themselves. This is a convenience method.
     * 
     * @return the current user selection as a list of user objects.
     */
    public List<A> getUserObjectSelection() {
        List<Record<A>> recs = getSelection();
        ArrayList<A> result = new ArrayList<A>(recs.size());
        for(Record<A> rec : recs) {
            result.add(rec.getUserObject());
        }
        return result;
    }
    
    /**
     * Returns the current user selection for the file system browser. The
     * selection may be from the tree, in which case there is only one object,
     * or from the table, in which case there can be several. Which one to
     * choose when there is a selection in both the table and the tree depends
     * on which component last had focus.
     * 
     * @return the current user selection for the file system browser.
     */
    public List<Record<A>> getSelection() {
        final List<Record<A>> result;
        if(dirTreeLastFocus > fileTableLastFocus) {
            Record<A> treeSelection = getTreeSelection();
            result = new ArrayList<Record<A>>(1);
            result.add(treeSelection);
        }
        else {
            result = getTableSelection();
        }
        return result;
    }

    public List<Record<A>> getSelectionParentPath() {
        if(dirTreeLastFocus > fileTableLastFocus)
            return getRecordPath(lastTreeSelectionPath.getParentPath());
        else
            return getRecordPath(lastTreeSelectionPath);
    }

    /**
     * Returns the current user selection for the folder tree.
     * 
     * @return the current user selection for the folder tree.
     */
    private Record<A> getTreeSelection() {
        //List<Record<A>> result;
        Record<A> result;
        Object o = lastTreeSelectionPath.getLastPathComponent();
        if(o == null) {
            JOptionPane.showMessageDialog(viewComponent, "No file or folder selected.",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            result = null;
        }
        else if(o instanceof DefaultMutableTreeNode) {
            Object o2 = ((DefaultMutableTreeNode) o).getUserObject();
            if(o2 instanceof RecordContainer) {
                Record<A> rec = ((RecordContainer) o2).getRecord(genericPlaceholder);
                //result = new ArrayList<Record<A>>(1);
                //result.add(rec);
                result = rec;
            }
            else {
                JOptionPane.showMessageDialog(viewComponent,
                        "[getTreeSelection()] Unexpected data in tree model: " +
                        o2.getClass() + ". (Internal error, report to " +
                        "developer)", "Error", JOptionPane.ERROR_MESSAGE);
                result = null;
            }
        }
        else {
            JOptionPane.showMessageDialog(viewComponent,
                    "[getTreeSelection()] Unexpected tree node type: " +
                    o.getClass() + "! (Internal error, report to developer)",
                    "Error", JOptionPane.ERROR_MESSAGE);
            result = null;
        }
        
        return result;
    }

    /*
    private List<Record<A>> getTreeSelectionPath() {
        //List<Record<A>> result;
        TreePath tmpLastTreeSelectionPath = lastTreeSelectionPath;
        ArrayList<Record<A>> result = new ArrayList<Record<A>>(tmpLastTreeSelectionPath.getPathCount());
        for(Object o : tmpLastTreeSelectionPath.getPath()) {
            if(o == null) {
                JOptionPane.showMessageDialog(viewComponent, "No file or folder selected.",
                        "Information", JOptionPane.INFORMATION_MESSAGE);
                result = null;
                break;
            }
            else if(o instanceof DefaultMutableTreeNode) {
                Object o2 = ((DefaultMutableTreeNode) o).getUserObject();
                if(o2 instanceof RecordContainer) {
                    Record<A> rec = ((RecordContainer) o2).getRecord(genericPlaceholder);
                    //result = new ArrayList<Record<A>>(1);
                    //result.add(rec);
                    result.add(rec);
                }
                else {
                    JOptionPane.showMessageDialog(viewComponent,
                            "[getTreeSelection()] Unexpected data in tree model: " +
                            o2.getClass() + ". (Internal error, report to " +
                            "developer)", "Error", JOptionPane.ERROR_MESSAGE);
                    result = null;
                    break;
                }
            }
            else {
                JOptionPane.showMessageDialog(viewComponent,
                        "[getTreeSelection()] Unexpected tree node type: " +
                        o.getClass() + "! (Internal error, report to developer)",
                        "Error", JOptionPane.ERROR_MESSAGE);
                result = null;
                break;
            }
        }

        return result;
    }*/

    /**
     * Returns the current user selection for the folder contents table.
     * 
     * @return the current user selection for the folder contents table.
     */
    private List<Record<A>> getTableSelection() {
        List<Record<A>> result;

        int[] selectedRows = fileTable.getSelectedRows();
        if(selectedRows.length == 0) {
            JOptionPane.showMessageDialog(viewComponent, "No file selected.",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            result = null;
        }
        else {
            ArrayList<Record<A>> actualResult =
                    new ArrayList<Record<A>>(selectedRows.length);
            for(int i = 0; i < selectedRows.length; ++i) {
                Object o = fileTable.getValueAt(selectedRows[i], 0);
                if(o instanceof RecordContainer) {
                    Record<A> rekk = ((RecordContainer) o).getRecord(genericPlaceholder);
                    actualResult.add(rekk);
                }
                else {
                    JOptionPane.showMessageDialog(viewComponent,
                            "[getTableSelection()] Unexpected data in " +
                            "table model. (Internal error, report to " +
                            "developer)", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    actualResult = null;
                    break;
                }
            }
            result = actualResult;
        }
        
        return result;
    }
    
    /**
     * Returns whether or not a file system is loaded by the controller. If a
     * file system is not loaded, an error message dialog is displayed to notify
     * the user that the operation it requested could not be performed, and then
     * <code>false</code> is returned.
     * 
     * @return whether or not a file system is loaded by the controller.
     */
    private boolean ensureFileSystemLoaded() {
        if(controller.isFileSystemLoaded()) {
            return true;
        }
        else {
            //new Exception().printStackTrace();
            JOptionPane.showMessageDialog(viewComponent, "No file system " +
                    "loaded.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public void setRoot(Record<A> rootRecord) {
        final TreeNode rootNode;
        final List<Record<A>> rootRecordPath;
        if(rootRecord != null) {
            rootRecordPath = new ArrayList<Record<A>>(1);
            rootRecordPath.add(rootRecord);

            FolderTreeNode rootTreeNode =
                    new FolderTreeNode(new RecordContainer(rootRecord));

            populateTreeNodeFromPath(rootTreeNode, rootRecordPath);
            rootNode = rootTreeNode;
        }
        else {
            rootRecordPath = null;
            rootNode = new NoLeafMutableTreeNode("No file system loaded");
        }

        treeModel = new DefaultTreeModel(rootNode);
        // System.err.print("Setting tree model...");
        dirTree.setModel(treeModel);
        // System.err.println("done!");
        
        lastTreeSelectionPath = new TreePath(rootNode);
        // System.err.print("Doing select in tree...");
        selectInTree(lastTreeSelectionPath);
        // System.err.println("done!");
        if(rootRecordPath != null) {
            populateTableFromPath(rootRecordPath);
        }
        else
            populateTableFromContents(new ArrayList<Record<A>>(0), "");
        
        // System.err.print("Setting selection status...");
        setSelectionStatus(0, 0);
        // System.err.println("done!");
    }

    private void selectInTree(TreePath childPath) {
        if(childPath.getPathCount() > 1)
            dirTree.expandPath(childPath.getParentPath());
        dirTree.setSelectionPath(childPath);
        dirTree.scrollPathToVisible(childPath);
    }
    
    /*
     * Notes on changing the current directory.
     * 
     * Directories can be changed by:
     * - Clicking on the requested directory
     * - Typing the address in the address bar and pressing enter or pushing the "Go"-button
     * - Double-clicking a directory entry in the directory contents table
     * 
     * Expected reaction:
     * - The directory contents table is populated with the contents of the requested directory
     * - The tree components leading up to the selected directory are expanded.
     * - The correct node in the directory tree is selected. No automatic expansion should take
     *   place, except if there are no subdirectories to this node, in which case the node should
     *   be expanded to remove the expansion sign.
     * - The address field is updated to reflect the currently selected directory.
     * 
     * Action entry points:
     * - actionChangeDir - triggered by a double-click in the contents table
     * - 
     * 
     * What we need to do:
     * 
     * Because of how events are triggered, 
     * - Look up the required Record<A> entry for the directory
     *   1. For 
     * - Look up the contents of the requested directory
     * 
     * When a change directory-event is triggered, the following should take place:
     * 
     * - 
     */
    private void setCurrentDirectory(String[] pathnameComponents) {
        System.err.println("setCurrentDirectory(): printing pathnameComponents");
        for(int i = 0; i < pathnameComponents.length; ++i)
            System.err.println("  [" + i + "]: " + pathnameComponents[i]);
        Object rootObj = treeModel.getRoot();
        FolderTreeNode curNode;
        if(rootObj instanceof FolderTreeNode) {
            curNode = (FolderTreeNode) rootObj;
        }
        else
            throw new RuntimeException("Unexpected root node class: " + rootObj.getClass());
        
        LinkedList<Record<A>> dirStack = new LinkedList<Record<A>>();
        //LinkedList<FolderTreeNode> nodeStack = new LinkedList<FolderTreeNode>();
        //nodeStack.addLast(curNode);
        TreePath treePath = new TreePath(curNode);
        
        for(String currentComponent : pathnameComponents) {
            //FolderTreeNode curNode = (FolderTreeNode) curObj;
            
            dirStack.addLast(curNode.getRecordContainer().getRecord(genericPlaceholder));
            populateTreeNodeFromPath(curNode, dirStack);
            dirTree.expandPath(treePath);
            
            int childCount = treeModel.getChildCount(curNode);
            FolderTreeNode requestedNode = null;
            for(int i = 0; i < childCount; ++i) {
                Object curChild = treeModel.getChild(curNode, i);
                if(curChild instanceof FolderTreeNode) {
                    FolderTreeNode curChildNode = (FolderTreeNode) curChild;
                    Record<A> rec = curChildNode.getRecordContainer().getRecord(genericPlaceholder);
                    if(rec.getName().equals(currentComponent)) {
                        requestedNode = curChildNode;
                        break;
                    }
                }
                else {
                    throw new RuntimeException("Unexpected tree node class: " + curChild.getClass());
                }
            }
            
            if(requestedNode != null) {
                curNode = requestedNode;
                //nodeStack.addLast(curNode);
                treePath = treePath.pathByAddingChild(curNode);
            }
            else {
                //String dir = controller.getAddressPath(Arrays.asList(pathnameComponents));
                JOptionPane.showMessageDialog(viewComponent, "No such directory.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        //TreePath tp = new TreePath(nodeStack.toArray(new FolderTreeNode[nodeStack.size()]));
        System.err.println("setCurrentDirectory(): selecting the following path in tree:");
        for(Object o : treePath.getPath())
            System.err.print(" \"" + o.toString() + "\"");
        
        selectInTree(treePath);
    }
    
    /**
     * This method is called each time the user makes/changes a selection in
     * the right pane. The resulting text is supposed to be printed somewhere
     * below the file system browser, but it's up to the controller to decide
     * where to display it.<br>
     * The text will look something like "3 objects selected (11,39 KiB)".
     * 
     * @param selectedFilesCount  number of files currently selected.
     * @param totalSize           the total size of the selection.
     */
    private void setSelectionStatus(long selectedFilesCount, long selectionSize) {
        String sizeString;
        if(selectionSize >= 1024) 
            sizeString = SpeedUnitUtils.bytesToBinaryUnit(selectionSize,
                    sizeFormat);
        else
            sizeString = selectionSize + " bytes";
        
        statusLabel.setText(selectedFilesCount +
                ((selectedFilesCount==1)?" object":" objects") +
                " selected (" + sizeString + ")");
    }
    
    public static enum RecordType {
        FILE, FOLDER, FILE_LINK, FOLDER_LINK, BROKEN_LINK;
    }
    
    public static class Record<A> {
        private RecordType type;
        private String name;
        private long size;
        private Date modifyDate;
        private A userObject;
        
        public Record(RecordType iType, String iName, long iSize,
                Date iModifyDate, A iUserObject) {
            this.type = iType;
            this.name = iName;
            this.size = iSize;
            this.modifyDate = iModifyDate;
            this.userObject = iUserObject;
        }
        
        public RecordType getType() {
            return type;
        }
        
        public String getName() {
            return name;
        }
        
        public long getSize() {
            return size;
        }
        
        public Date getModifyDate() {
            return modifyDate;
        }
        
        public A getUserObject() {
            return userObject;
        }
    }
        
    public static interface FileSystemProvider<A> {
        
        public void actionDoubleClickFile(List<Record<A>> fileRecordPath);

        public void actionExtractToDir(List<Record<A>> parentPath, List<Record<A>> recordList);
        
        public void actionGetInfo(List<Record<A>> parentPath, List<Record<A>> recordList);

        public JPopupMenu getRightClickRecordPopupMenu(List<Record<A>> parentPath, List<Record<A>> selectedRecords);
        
        public boolean isFileSystemLoaded();
        
        public List<Record<A>> getFolderContents(List<Record<A>> folderRecordPath);

        public String getAddressPath(List<String> pathComponents);
        
        /**
         * Parses the string <code>targetAddress</code> as a path specifier in the context of the
         * current file system. For example, in a unix-like file system environment you would want
         * to parse the string "/usr/bin" to <code>{ "usr", "bin" }</code>, and for a Windows file
         * system you can choose to parse the path "\Windows\System32" or "C:\Windows\System32" as
         * <code>{ "Windows", "System32" }</code>. The parsing must be consistent with the result of
         * <code>getAddressPath</code>.
         * 
         * @param targetAddress
         * @return the components of the address path if the parsing was successful, or
         * <code>null</code> if the target address string was invalid.
         */
        public String[] parseAddressPath(String targetAddress);
        
        
    }
    
    /** Aggregation class for storage in the first column of fileTable. */
    private static class RecordContainer implements Comparable {
	private Record rec;
        
	private RecordContainer() {}
	public RecordContainer(Record rec) {
	    this.rec = rec;
	}
        
        /*public Record getRecord() {
            return rec;
        }*/
        
        @SuppressWarnings("unchecked")
	public <T> Record<T> getRecord(GenericPlaceholder<T> placeholder) {
            return (Record<T>)rec;
        }
        
        @Override public String toString() { return rec.getName(); }

        @Override
        public int compareTo(Object o) {
            if(o instanceof RecordContainer) {
                RecordContainer rc = (RecordContainer)o;
                return toString().compareTo(rc.toString());
            }
            else
                throw new RuntimeException("Can not compare a RecordContainer with a " + o.getClass());
        }
    }
    
    /**
     * Wrapper for the size field in the table.
     */
    private static class SizeEntry implements Comparable {
        private final String presentedSize;
        private final long trueSize;

        public SizeEntry(long trueSize) {
            this.trueSize = trueSize;
            this.presentedSize = SpeedUnitUtils.bytesToBinaryUnit(trueSize);
        }
        
        public long getSize() { return trueSize; }
        
        @Override
        public int compareTo(Object o) {
            if(o instanceof SizeEntry) {
                SizeEntry se = (SizeEntry) o;
                long res = trueSize - se.trueSize;
                if(res > 0)
                    return 1;
                else if(res < 0)
                    return -1;
                else
                    return 0;
            }
            else
                throw new RuntimeException("Can not compare a SizeEntry with a " + o.getClass());
        }
        
        @Override
        public String toString() {
            return presentedSize;
        }
    }

    public static class RecordTypeEntry implements Comparable {
        private final RecordType recordType;
        private final String displayString;
        
        public RecordTypeEntry(RecordType recordType) {
            this.recordType = recordType;
            
            switch(recordType) {
                case FILE:
                    displayString = "File";
                    break;
                case FOLDER:
                    displayString = "Folder";
                    break;
                case FILE_LINK:
                    displayString = "File (symlink)";
                    break;
                case FOLDER_LINK:
                    displayString = "Folder (symlink)";
                    break;
                case BROKEN_LINK:
                    displayString = "Broken link";
                    break;
                default:
                    throw new RuntimeException("INTERNAL ERROR: Encountered " +
                        "unexpected record type (" + recordType + ")");
            }
        }
        
        public RecordType getRecordType() { return recordType; }
        
        @Override
        public String toString() {
            return displayString;
        }
        
        private int getPriority() {
            switch(recordType) {
                case FOLDER:
                    return 0;
                case FOLDER_LINK:
                    return 1;
                case FILE:
                    return 2;
                case FILE_LINK:
                    return 3;
                case BROKEN_LINK:
                    return 4;
                default:
                    throw new RuntimeException("INTERNAL ERROR: Encountered " +
                        "unexpected record type (" + recordType + ")");
            }
        }

        @Override
        public int compareTo(Object o) {
            if(o instanceof RecordTypeEntry) {
                RecordTypeEntry rte = (RecordTypeEntry)o;
                return getPriority()-rte.getPriority();
            }
            else
                throw new RuntimeException("Can not compare a RecordTypeEntry to a " + o.getClass());
        }
    }
    
    private static class DateEntry implements Comparable {
        private final Date date;
        private final String displayString;
        
        public DateEntry(Date date, DateFormat formatter) {
            this.date = date;
            this.displayString = formatter.format(date);
        }
        
        public Date getDate() { return date; }
        
        @Override
        public String toString() {
            return displayString;
        }

        @Override
        public int compareTo(Object o) {
            if(o instanceof DateEntry) {
                DateEntry de = (DateEntry)o;
                return date.compareTo(de.date);
            }
            else
                throw new RuntimeException("Can not compare a DateEntry to a " + o.getClass());
        }
    }
    
    private static class IndexEntry implements Comparable<IndexEntry> {
        private final int index;
        
        public IndexEntry(int index) {
            this.index = index;
        }
        
        @Override
        public int compareTo(IndexEntry o) {
            return index-o.index;
        }
        
        @Override
        public String toString() { return ""; }
    }
    
    private static class ComparableComparator implements Comparator<Comparable<Comparable>> {

        @Override
        public int compare(Comparable<Comparable> o1, Comparable<Comparable> o2) {
            //System.err.println("ComparableComparator comparing a " + o1.getClass() + " and a " + o2.getClass());
            //if(o1 instanceof Comparable && o2 instanceof Comparable) {
                return o1.compareTo(o2);
            /*}
            else
                throw new UnsupportedOperationException("Trying to compare non-Comparables.");*/
        }
        
    }
    
    public static class NoLeafMutableTreeNode extends DefaultMutableTreeNode {
        public NoLeafMutableTreeNode(Object userObject) {
            super(userObject);
        }
        
	/** Hack to avoid that JTree paints leaf nodes. We have no leafs, only dirs. */
	@Override public boolean isLeaf() { return false; }
    }
    
    private static class FolderTreeNode extends NoLeafMutableTreeNode {
        
        private final RecordContainer rc;
        
	public FolderTreeNode(RecordContainer o) {
            super(o);
            rc = o;
        }
        
        public RecordContainer getRecordContainer() { return rc; }
    }
    
    private static class ObjectContainer<A> {
	public A o;
	public ObjectContainer(A o) { this.o = o; }
    }
    
    private static class GenericPlaceholder<A> {}
}
