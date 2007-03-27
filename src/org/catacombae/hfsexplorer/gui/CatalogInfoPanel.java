/*-
 * Copyright (C) 2007 Erik Larsson
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

/*
 * CatalogInfoPanel.java
 *
 * Created on den 20 mars 2007, 19:28
 */

package org.catacombae.hfsexplorer.gui;
import static org.catacombae.hfsexplorer.FileSystemBrowserWindow.NoLeafMutableTreeNode;
import org.catacombae.hfsexplorer.*;
import org.catacombae.hfsexplorer.types.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

/**
 *
 * @author  Erik
 */
public class CatalogInfoPanel extends javax.swing.JPanel {
    
    private static class BTNodeStorage {
	private BTNode node;
	private String text;
	public BTNodeStorage(BTNode node, String text) {
	    this.node = node;
	    this.text = text;
	}
	public BTNode getNode() { return node; }
	public String toString() { return text; }
    }
    private static class BTLeafStorage {
	private HFSPlusCatalogLeafRecord rec;
	private String text;
	public BTLeafStorage(HFSPlusCatalogLeafRecord rec, String text) {
	    this.rec = rec;
	    this.text = text;
	}
	public HFSPlusCatalogLeafRecord getRecord() { return rec; }
	public String toString() { return text; }
    }
    
    /** Creates new form CatalogInfoPanel */
    public CatalogInfoPanel(final HFSFileSystemView fsView) {
        initComponents();
	
	JTree dirTree = catalogTree;
	// Populate the root
	/* 
	 * What we need is a method that gets us the children of the "current" node.
	 * A B-tree starts with a header node, 
	 */
	BTNode iNode = fsView.getCatalogNode(-1); // Get root index node.
	
	DefaultMutableTreeNode rootNode = new NoLeafMutableTreeNode(new BTNodeStorage(iNode, "Catalog root"));
	expandNode(rootNode, iNode, fsView);
	
	DefaultTreeModel model = new DefaultTreeModel(rootNode);
	dirTree.setModel(model);

	dirTree.addTreeWillExpandListener(new TreeWillExpandListener() {
		public void treeWillExpand(TreeExpansionEvent e) 
                    throws ExpandVetoException {
		    
		    TreePath tp = e.getPath();
		    Object obj = tp.getLastPathComponent();
		    if(obj instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode dmtn = ((DefaultMutableTreeNode)obj);
			Object obj2 = dmtn.getUserObject();
			if(obj2 instanceof BTNodeStorage) {
			    BTNode node = ((BTNodeStorage)obj2).getNode();
			    expandNode(dmtn, node, fsView);
			}
			else
			    throw new RuntimeException("Wrong user object type in expandable node!");
		    }
		    else
			throw new RuntimeException("Wrong node type in tree!");		    
		}
		
		public void treeWillCollapse(TreeExpansionEvent e) {}
	    });
            
        //final JPanel infoPanel = new JPanel();
        final JPanel leafPanel = new JPanel();
        final FileInfoPanel fileInfoPanel = new FileInfoPanel();
        final FolderInfoPanel folderInfoPanel = new FolderInfoPanel();
        final CardLayout clRoot = new CardLayout();
        final CardLayout clLeaf = new CardLayout();
        leafPanel.setLayout(clLeaf);
        leafPanel.add(new JLabel("INTERNAL ERROR!", SwingConstants.CENTER), "other");
        leafPanel.add(new JLabel("Displaying file thread information is not yet supported.", SwingConstants.CENTER), "filethread");
        leafPanel.add(new JLabel("Displaying folder thread information is not yet supported.", SwingConstants.CENTER), "folderthread");
        JScrollPane fileInfoPanelScroller = new JScrollPane(fileInfoPanel);
        fileInfoPanelScroller.getVerticalScrollBar().setUnitIncrement(5);
        leafPanel.add(fileInfoPanelScroller, "file");
        JScrollPane folderInfoPanelScroller = new JScrollPane(folderInfoPanel);
        folderInfoPanelScroller.getVerticalScrollBar().setUnitIncrement(5);
        leafPanel.add(folderInfoPanelScroller, "folder");
        
        infoPanel.setLayout(clRoot);
        final JLabel indexNodeLabel = new JLabel("No selection.", SwingConstants.CENTER);
        infoPanel.add(indexNodeLabel, "index");
        infoPanel.add(leafPanel, "leaf");
        //infoScroller.setViewportView(infoPanel);
        
        catalogTree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent te) {
               //System.err.println("Tree selection");
               Object o = te.getPath().getLastPathComponent();
               if(o instanceof DefaultMutableTreeNode) {
                   Object o2 = ((DefaultMutableTreeNode)o).getUserObject();
                   if(o2 instanceof BTNodeStorage) {
                       BTNode btn = ((BTNodeStorage)o2).getNode();
                       BTNodeDescriptor btnd = btn.getNodeDescriptor();
                       if(btnd.getKind() == BTNodeDescriptor.BT_INDEX_NODE) {
                           indexNodeLabel.setText("Index node with " + btnd.getNumRecords() + " records.");
                       }
                       else if(btnd.getKind() == BTNodeDescriptor.BT_LEAF_NODE) {
                           indexNodeLabel.setText("Leaf node with " + btnd.getNumRecords() + " records.");
                       }
                       else {
                           indexNodeLabel.setText("Unknown error!");
                       }
                           
                       clRoot.show(infoPanel, "index");
                   }
                   else if(o2 instanceof BTLeafStorage) {
                       HFSPlusCatalogLeafRecord rec = ((BTLeafStorage)o2).getRecord();
                       HFSPlusCatalogLeafRecordData data = rec.getData();
                       if(data.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FILE &&
                          data instanceof HFSPlusCatalogFile) {
                            fileInfoPanel.setFields((HFSPlusCatalogFile)data);
                            clLeaf.show(leafPanel, "file");
                       }
                       else if(data.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER &&
                               data instanceof HFSPlusCatalogFolder) {
                            folderInfoPanel.setFields((HFSPlusCatalogFolder)data);
                            clLeaf.show(leafPanel, "folder");
                       }
                       else if(data.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FILE_THREAD)
                           clLeaf.show(leafPanel, "filethread");
                       else if(data.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER_THREAD)
                           clLeaf.show(leafPanel, "folderthread");
                       else
                           clLeaf.show(leafPanel, "other");
                       clRoot.show(infoPanel, "leaf");
                       
                   }
                   else
                       System.err.println("WARNING: unknown type in catalog tree user object - " + o2.getClass().toString());
               }
               else
                   System.err.println("WARNING: unknown type in catalog tree - " + o.getClass().toString());
            }
        });
    }

    public void expandNode(DefaultMutableTreeNode dmtn, BTNode node, HFSFileSystemView fsView) {
	if(node instanceof HFSPlusCatalogIndexNode) {
	    BTIndexRecord[] recs = ((HFSPlusCatalogIndexNode)node).getIndexRecords();
	    for(BTIndexRecord rec : recs) {
		BTNode curNode = fsView.getCatalogNode(rec.getIndex());
		BTKey key = rec.getKey();
		if(key instanceof HFSPlusCatalogKey) {
		    HFSPlusCatalogKey trueKey = (HFSPlusCatalogKey)key;
		    dmtn.add(new NoLeafMutableTreeNode(new BTNodeStorage(curNode, trueKey.getParentID().toString() + ":" + trueKey.getNodeName().toString())));
		}
		else
		    throw new RuntimeException("Wrong key type in catalog tree");
	    }
	}
	else if(node instanceof HFSPlusCatalogLeafNode) {
	    HFSPlusCatalogLeafRecord[] recs = ((HFSPlusCatalogLeafNode)node).getLeafRecords();
	    for(HFSPlusCatalogLeafRecord rec : recs)
		dmtn.add(new DefaultMutableTreeNode(new BTLeafStorage(rec, rec.getKey().getParentID().toString() + ":" + rec.getKey().getNodeName().toString())));
	}
	else
	    throw new RuntimeException("Invalid node type in tree.");
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        catalogTreeScroller = new javax.swing.JScrollPane();
        catalogTree = new javax.swing.JTree();
        infoPanel = new javax.swing.JPanel();
        descriptionLabel = new javax.swing.JLabel();

        jSplitPane1.setDividerLocation(330);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(1.0);
        catalogTreeScroller.setViewportView(catalogTree);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(catalogTreeScroller, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, catalogTreeScroller, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
        );
        jSplitPane1.setLeftComponent(jPanel1);

        infoPanel.setPreferredSize(new java.awt.Dimension(100, 140));
        org.jdesktop.layout.GroupLayout infoPanelLayout = new org.jdesktop.layout.GroupLayout(infoPanel);
        infoPanel.setLayout(infoPanelLayout);
        infoPanelLayout.setHorizontalGroup(
            infoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 361, Short.MAX_VALUE)
        );
        infoPanelLayout.setVerticalGroup(
            infoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 122, Short.MAX_VALUE)
        );
        jSplitPane1.setRightComponent(infoPanel);

        descriptionLabel.setText("View of the catalog file's B-tree:");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)
                    .add(descriptionLabel))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(descriptionLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JTree catalogTree;
    private javax.swing.JScrollPane catalogTreeScroller;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JPanel infoPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSplitPane jSplitPane1;
    // End of variables declaration//GEN-END:variables
    
}
