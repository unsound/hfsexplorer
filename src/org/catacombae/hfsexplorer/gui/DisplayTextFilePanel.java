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

package org.catacombae.hfsexplorer.gui;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.catacombae.io.InputStreamReadable;
import org.catacombae.io.Readable;
//import net.iharder.dnd.FileDrop;

/**
 *
 * @author  Erik
 */
public class DisplayTextFilePanel extends javax.swing.JPanel {
    private static final String[] sortingPrefixes =
            new String[] { "US-ASCII", "UTF-8", "ISO-8859", "UTF", "IBM4", "IBM8", "IBM" };
    private byte[] fileData = new byte[] { 0 };
    private final Frame parentFrame;
    private final String baseTitle;
    
    /** Creates new form DisplayTextFilePanel. */
    public DisplayTextFilePanel() {
        this(null);
    }
    /**
     * Creates new form DisplayTextFilePanel. Supplying a parent frame will lead to the frame's
     * title changing to reflect the currently displayed file.
     */
    public DisplayTextFilePanel(final Frame parentFrame) {
        this.parentFrame = parentFrame;

        if(parentFrame != null)
            this.baseTitle = parentFrame.getTitle();
        else
            this.baseTitle = "";
        
        initComponents();
        
        textPaneScroller.getVerticalScrollBar().setMinimum(0);
        textPaneScroller.getVerticalScrollBar().setMaximum(Integer.MAX_VALUE);
        textPaneScroller.getHorizontalScrollBar().setMinimum(0);
        textPaneScroller.getHorizontalScrollBar().setMaximum(Integer.MAX_VALUE);
        
        Set<String> keySet = Charset.availableCharsets().keySet();
        ArrayList<String> charsets = new ArrayList<String>(keySet);
        LinkedList<String> listItems = new LinkedList<String>();
        for(String prefix : sortingPrefixes) {
            for(int i = 0; i < charsets.size();) {
                String curCharset = charsets.get(i);
                if(curCharset.startsWith(prefix)) {
                    listItems.add(curCharset);
                    charsets.remove(i);
                }
                else
                    ++i;
            }
        }
        for(String curCharset : charsets)
            listItems.add(curCharset);
        
        encodingBox.removeAllItems();
        for(String curItem : listItems)
            encodingBox.addItem(curItem);
        if(encodingBox.getItemCount() > 0)
            encodingBox.setSelectedIndex(0);
        
        encodingBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                refreshView();
            }
            
        });
    }

    /*
    public void addFileDropListener() {
        new FileDrop(textPaneScroller, new FileDrop.Listener() {
            public void filesDropped(java.io.File[] files) {
                // handle file drop
                if(files.length == 1) {
                    if(files[0].isFile()) {
                        loadFile(files[0]);
                        
                    }
                    else
                        JOptionPane.showMessageDialog(DisplayTextFilePanel.this, "You can only view files.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                }
                else if(files.length > 1) {
                    JOptionPane.showMessageDialog(DisplayTextFilePanel.this, "You can only view one file at a time.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }   // end filesDropped
            }); // end FileDrop.Listener
    }
    */

    public void loadFile(File file) {
        if(file.length() < Integer.MAX_VALUE) {
            try {
                FileInputStream fis = new FileInputStream(file);
                loadStream(fis);
                fis.close();
                if(parentFrame != null)
                    parentFrame.setTitle(baseTitle + " - [" + file.getName() + "]");
            } catch(Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Exception while loading file:\n  " + e + "\"");
            }
        }
        else
            JOptionPane.showMessageDialog(this, "File too large for memory address space! (" +
                    file.length() + "bytes)");
    }
    
    public void loadStream(InputStream is) {
        loadStream(new InputStreamReadable(is));
    }
    
    public void loadStream(Readable is) {
        try {
            byte[] tmp = new byte[65536];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int bytesRead;
            while((bytesRead = is.read(tmp)) > 0)
                baos.write(tmp, 0, bytesRead);

            fileData = baos.toByteArray();
            baos = null;
            
            refreshView();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    int vMin = textPaneScroller.getVerticalScrollBar().getMinimum();
                    int hMin = textPaneScroller.getHorizontalScrollBar().getMinimum();
                    textPaneScroller.getVerticalScrollBar().setValue(vMin);
                    textPaneScroller.getHorizontalScrollBar().setValue(hMin);
                }
            });

        } catch(Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Exception while loading file:\n  " + e + "\"");
        }
    }

    private void refreshView() {
        try {
            final int vValue = textPaneScroller.getVerticalScrollBar().getValue();
            final int hValue = textPaneScroller.getHorizontalScrollBar().getValue();
            textPane.setText(new String(fileData, getSelectedEncoding()));
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    textPaneScroller.getVerticalScrollBar().setValue(vValue);
                    textPaneScroller.getHorizontalScrollBar().setValue(hValue);
                }
            });
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Exception while decoding file data:\n  " + e +
                    "\"");
        }
    }

    private String getSelectedEncoding() {
        return encodingBox.getSelectedItem().toString();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        textPaneScroller = new javax.swing.JScrollPane();
        textPane = new javax.swing.JTextArea();
        encodingLabel = new javax.swing.JLabel();
        encodingBox = new javax.swing.JComboBox();

        textPane.setColumns(20);
        textPane.setEditable(false);
        textPane.setRows(5);
        textPaneScroller.setViewportView(textPane);

        encodingLabel.setText("Encoding:");

        encodingBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(encodingLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(encodingBox, 0, 650, Short.MAX_VALUE)
                .addContainerGap())
            .add(textPaneScroller, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 721, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(encodingLabel)
                    .add(encodingBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(textPaneScroller, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox encodingBox;
    private javax.swing.JLabel encodingLabel;
    private javax.swing.JTextArea textPane;
    private javax.swing.JScrollPane textPaneScroller;
    // End of variables declaration//GEN-END:variables
}
