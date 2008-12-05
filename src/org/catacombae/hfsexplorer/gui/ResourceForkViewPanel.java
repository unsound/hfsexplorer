/*-
 * Copyright (C) 2008 Erik Larsson
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

/*
 * ResourceForkViewPanel.java
 *
 * Created on 2008-nov-27, 11:27:34
 */

package org.catacombae.hfsexplorer.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.catacombae.hfsexplorer.GUIUtil;
import org.catacombae.hfsexplorer.IOUtil;
import org.catacombae.hfsexplorer.Util;
import org.catacombae.hfsexplorer.fs.ResourceForkReader;
import org.catacombae.hfsexplorer.types.resff.ReferenceListEntry;
import org.catacombae.hfsexplorer.types.resff.ResourceMap;
import org.catacombae.hfsexplorer.types.resff.ResourceName;
import org.catacombae.hfsexplorer.types.resff.ResourceType;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.util.Util.Pair;

/**
 *
 * @author Erik
 */
public class ResourceForkViewPanel extends javax.swing.JPanel {
    private ResourceForkReader reader = null;
    
    /**
     * An item as it is displayed in the list view over available resources.
     * Its toString method decides how it is displayed to the user.
     */
    private class ListItem {
        ResourceType type;
        ReferenceListEntry entry;
        ResourceName name;
        long size;

        public ListItem(ResourceType type,
                ReferenceListEntry entry,
                ResourceName name,
                long size) {
            this.type = type;
            this.entry = entry;
            this.name = name;
            this.size = size;
        }

        @Override
        public String toString() {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append(new String(type.getType(), "MacRoman"));

                if(name != null)
                    sb.append(" \"").append(new String(name.getName(), "MacRoman")).append("\"");
                return sb.toString();
            } catch(Exception e) {
                e.printStackTrace();
                return "{" + e.getClass().getSimpleName() + " in resource id " + entry.getResourceID() + "}";
            }
        }
    }

    /** Creates new form ResourceForkViewPanel */
    public ResourceForkViewPanel(ResourceForkReader startupReader) {
        initComponents();
        resourceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        loadResourceFork(startupReader);

        resourceList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                Object o = resourceList.getSelectedValue();
                if(o instanceof ListItem)
                    setSelectedItem((ListItem)o);
                else if(o != null)
                    JOptionPane.showMessageDialog(resourceList, "Unexpected type in list: " + o.getClass());
            }
        });

        viewButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Object selection = resourceList.getSelectedValue();
                if(selection != null && selection instanceof ListItem) {
                    ListItem selectedItem = (ListItem) selection;
                    JDialog d = new JDialog(JOptionPane.getFrameForComponent(ResourceForkViewPanel.this),
                            selection.toString(), true);

                    DisplayTextFilePanel dtfp = new DisplayTextFilePanel();
                    dtfp.loadStream(reader.getResourceStream(selectedItem.entry));

                    d.add(dtfp);
                    d.pack();
                    d.setLocationRelativeTo(null);
                    d.setVisible(true);
                }
            }

        });

        extractButton.addActionListener(new ActionListener() {
            private JFileChooser fileChooser = new JFileChooser();
            {
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setMultiSelectionEnabled(false);
            }
            public void actionPerformed(ActionEvent e) {
                Object selection = resourceList.getSelectedValue();
                if(selection != null && selection instanceof ListItem) {
                    ListItem selectedItem = (ListItem) selection;

                    if(fileChooser.showSaveDialog(ResourceForkViewPanel.this) == JFileChooser.APPROVE_OPTION) {
                        File saveFile = fileChooser.getSelectedFile();
                        if(saveFile.exists()) {
                            int res = JOptionPane.showConfirmDialog(ResourceForkViewPanel.this,
                                    "The file already exists. Do you want to overwrite?",
                                    "Confirm overwrite", JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE);
                            if(res != JOptionPane.YES_OPTION)
                                return;
                        }

                        ReadableRandomAccessStream in = null;
                        FileOutputStream fos = null;
                        try {
                            in = reader.getResourceStream(selectedItem.entry);
                            fos = new FileOutputStream(saveFile);

                            IOUtil.streamCopy(in, fos, 65536);
                        } catch(FileNotFoundException fnfe) {
                            JOptionPane.showMessageDialog(ResourceForkViewPanel.this,
                                    "Could not open file \"" + saveFile.getPath() + "\" for writing...",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        } catch(IOException ioe) {
                            ioe.printStackTrace();
                            GUIUtil.displayExceptionDialog(ioe, ResourceForkViewPanel.this);
                        } finally {
                            if(in != null)
                                in.close();
                            if(fos != null) {
                                try {
                                    fos.close();
                                } catch(IOException ex) {
                                    ex.printStackTrace();
                                    GUIUtil.displayExceptionDialog(ex, ResourceForkViewPanel.this);
                                }
                            }
                        }
                    }
                }
            }

        });
    }

    public void loadResourceFork(ResourceForkReader reader) {
        if(reader != null) {
            ListItem[] allItems = listAllItems(reader);
            resourceList.setEnabled(true);
            resourceList.setListData(allItems);
            resourceListLabel.setText("Resource list (" + allItems.length + " items):");
        }
        else {
            resourceList.setEnabled(false);
            resourceList.setListData(new Object[0]);
            resourceListLabel.setText("Resource list:");
        }
        setSelectedItem(null);
        this.reader = reader;
    }

    private ListItem[] listAllItems(ResourceForkReader reader) {
        //System.err.println("listAllItems(): getting resource map");
        ResourceMap resMap = reader.getResourceMap();

        LinkedList<ListItem> result = new LinkedList<ListItem>();

        //System.err.println("listAllItems(): getting reference list for " + resMap);
        List<Pair<ResourceType, ReferenceListEntry[]>> refList = resMap.getReferenceList();
        for(Pair<ResourceType, ReferenceListEntry[]> p : refList) {
            ResourceType type = p.getA();
            for(ReferenceListEntry entry : p.getB()) {
                //System.err.println("listAllItems(): getting name by reflist entry " + entry);
                ResourceName name = resMap.getNameByReferenceListEntry(entry);
                long size = reader.getDataLength(entry);

                result.add(new ListItem(type, entry, name, size));
            }
        }

        return result.toArray(new ListItem[result.size()]);
    }

    private void setSelectedItem(ListItem li) {
        final boolean enabled;
        if(li == null)
            enabled = false;
        else
            enabled = true;

        extractButton.setEnabled(enabled);
        viewButton.setEnabled(enabled);
        nameField.setEnabled(enabled);
        typeField.setEnabled(enabled);
        idField.setEnabled(enabled);
        sizeField.setEnabled(enabled);
        attributesField.setEnabled(enabled);

        if(!enabled) {
            nameField.setText("");
            typeField.setText("");
            idField.setText("");
            sizeField.setText("");
            attributesField.setText("");
        }
        else {
            String nameString;
            if(li.name != null) {
                try {
                    nameString = new String(li.name.getName(), "MacRoman");
                } catch(Exception e) {
                    e.printStackTrace();
                    nameString = "[Could not decode: " + e.toString() + "]";
                }
            }
            else {
                nameString = null;
            }

            String typeString;
            try {
                typeString = new String(li.type.getType(), "MacRoman");
            } catch(Exception e) {
                e.printStackTrace();
                typeString = "[Could not decode: " + e.toString() + "]";
            }

            if(nameField == null) {
                nameField.setEnabled(false);
                nameField.setName("");
            }
            else
                nameField.setText(nameString);
            
            typeField.setText(typeString);
            idField.setText("" + li.entry.getResourceID());
            sizeField.setText(li.size + " bytes");
            attributesField.setText("0x" + Util.toHexStringBE(li.entry.getResourceAttributes()));
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        resourceListLabel = new javax.swing.JLabel();
        resourceListScroller = new javax.swing.JScrollPane();
        resourceList = new javax.swing.JList();
        fieldsPanel = new javax.swing.JPanel();
        nameLabel = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        typeLabel = new javax.swing.JLabel();
        typeField = new javax.swing.JTextField();
        idLabel = new javax.swing.JLabel();
        idField = new javax.swing.JTextField();
        sizeLabel = new javax.swing.JLabel();
        sizeField = new javax.swing.JTextField();
        attributesLabel = new javax.swing.JLabel();
        attributesField = new javax.swing.JTextField();
        extractButton = new javax.swing.JButton();
        viewButton = new javax.swing.JButton();

        resourceListLabel.setText("[This label is set programmatically]");

        resourceList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        resourceListScroller.setViewportView(resourceList);

        nameLabel.setText("Name:");

        nameField.setEditable(false);
        nameField.setText("jTextField1");
        nameField.setOpaque(false);

        typeLabel.setText("Type:");

        typeField.setEditable(false);
        typeField.setText("jTextField2");
        typeField.setOpaque(false);

        idLabel.setText("ID:");

        idField.setEditable(false);
        idField.setText("jTextField3");
        idField.setOpaque(false);

        sizeLabel.setText("Size:");

        sizeField.setEditable(false);
        sizeField.setText("jTextField4");
        sizeField.setOpaque(false);

        attributesLabel.setText("Attributes:");

        attributesField.setEditable(false);
        attributesField.setText("jTextField5");
        attributesField.setOpaque(false);

        org.jdesktop.layout.GroupLayout fieldsPanelLayout = new org.jdesktop.layout.GroupLayout(fieldsPanel);
        fieldsPanel.setLayout(fieldsPanelLayout);
        fieldsPanelLayout.setHorizontalGroup(
            fieldsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(fieldsPanelLayout.createSequentialGroup()
                .add(fieldsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(nameLabel)
                    .add(typeLabel)
                    .add(idLabel)
                    .add(sizeLabel)
                    .add(attributesLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(fieldsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(attributesField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                    .add(sizeField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                    .add(idField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                    .add(nameField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                    .add(typeField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)))
        );
        fieldsPanelLayout.setVerticalGroup(
            fieldsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(fieldsPanelLayout.createSequentialGroup()
                .add(fieldsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(nameLabel)
                    .add(nameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(fieldsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(typeLabel)
                    .add(typeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(fieldsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(idLabel)
                    .add(idField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(fieldsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(sizeLabel)
                    .add(sizeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(fieldsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(attributesLabel)
                    .add(attributesField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        extractButton.setText("Save to file...");

        viewButton.setText("View as text");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, resourceListScroller, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, resourceListLabel)
                    .add(layout.createSequentialGroup()
                        .add(viewButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(extractButton))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, fieldsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(resourceListLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(resourceListScroller, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(fieldsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(extractButton)
                    .add(viewButton))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField attributesField;
    private javax.swing.JLabel attributesLabel;
    private javax.swing.JButton extractButton;
    private javax.swing.JPanel fieldsPanel;
    private javax.swing.JTextField idField;
    private javax.swing.JLabel idLabel;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JList resourceList;
    private javax.swing.JLabel resourceListLabel;
    private javax.swing.JScrollPane resourceListScroller;
    private javax.swing.JTextField sizeField;
    private javax.swing.JLabel sizeLabel;
    private javax.swing.JTextField typeField;
    private javax.swing.JLabel typeLabel;
    private javax.swing.JButton viewButton;
    // End of variables declaration//GEN-END:variables

}
