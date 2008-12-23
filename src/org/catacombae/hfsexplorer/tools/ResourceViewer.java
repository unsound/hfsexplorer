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
 * ResourceViewer.java
 *
 * Created on 2008-nov-28, 07:10:27
 */

package org.catacombae.hfsexplorer.tools;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import org.catacombae.hfsexplorer.GUIUtil;
import org.catacombae.hfsexplorer.PrefixFileFilter;
import org.catacombae.hfsexplorer.fs.AppleSingleHandler;
import org.catacombae.hfsexplorer.fs.ResourceForkReader;
import org.catacombae.hfsexplorer.gui.ResourceForkViewPanel;
import org.catacombae.hfsexplorer.types.applesingle.EntryDescriptor;
import org.catacombae.io.ReadableFileStream;
import org.catacombae.io.ReadableRandomAccessStream;

/**
 *
 * @author Erik
 */
public class ResourceViewer extends javax.swing.JFrame {
    private final ResourceForkViewPanel resourceForkViewPanel;
    /** Creates new form ResourceViewer */
    public ResourceViewer() {
        super("Resource Viewer");
        
        this.resourceForkViewPanel = new ResourceForkViewPanel(null);

        initComponents();

        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        backgroundPanel.add(resourceForkViewPanel);

        openMenuItem.addActionListener(new ActionListener() {
            private JFileChooser jfc = new JFileChooser();

            {
                jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                jfc.setMultiSelectionEnabled(false);
                jfc.setFileFilter(new PrefixFileFilter("AppleDouble resource forks (._*)", "._"));
            }

            public void actionPerformed(ActionEvent e) {
                if(jfc.showOpenDialog(ResourceViewer.this) == JFileChooser.APPROVE_OPTION) {
                    loadFile(jfc.getSelectedFile());
                }
            }

        });
    }

    private void loadFile(File f) {
        ReadableRandomAccessStream fileStream = null;
        ResourceForkReader reader = null;

        try {
            fileStream = new ReadableFileStream(f);

            // Detect AppleSingle format
            //System.err.println("Detecting AppleSingle format...");
            if(AppleSingleHandler.detectFileFormat(fileStream, 0) != null) {
                try {
                    //System.err.println("AppleSingle format found! Creating handler...");
                    AppleSingleHandler handler = new AppleSingleHandler(fileStream);
                    //System.err.println("Getting resource entry descriptor...");
                    EntryDescriptor desc = handler.getResourceEntryDescriptor();
                    if(desc != null) {
                        //System.err.println("Getting entry stream...");
                        fileStream = handler.getEntryStream(desc);
                        //System.err.println("done!");
                    }
                    //else
                    //    System.err.println("No resource entry found in AppleSingle structure.");
                } catch(Exception e) {
                    System.err.println("Unhandled exception while detecting AppleSingle format:");
                    e.printStackTrace();
                }
            }
            else {
                int res = JOptionPane.showConfirmDialog(this, "Invalid AppleDouble file.\n" +
                        "Do you want to attempt to load the file as raw resource fork data?",
                        "Invalid file format", JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if(res != JOptionPane.YES_OPTION) {
                    fileStream.close();
                    return;
                }
            }

            //System.err.println("Creating new ResourceForkReader...");
            reader = new ResourceForkReader(fileStream);
            //System.err.println("Loading resource fork into panel...");
            resourceForkViewPanel.loadResourceFork(reader);
            //System.err.println("done!");

            setTitle("Resource Viewer - [" + f.getName() + "]");
        } catch(Exception e) {
            e.printStackTrace();
            GUIUtil.displayExceptionDialog(e, this);

            resourceForkViewPanel.loadResourceFork(null);
            if(reader != null)
                reader.close();
            if(fileStream != null)
                fileStream.close();
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

        backgroundPanel = new javax.swing.JPanel();
        menuBar = new javax.swing.JMenuBar();
        openMenu = new javax.swing.JMenu();
        openMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        backgroundPanel.setLayout(new java.awt.BorderLayout());

        openMenu.setText("File");

        openMenuItem.setText("Open...");
        openMenu.add(openMenuItem);

        menuBar.add(openMenu);

        setJMenuBar(menuBar);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(backgroundPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(backgroundPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(final String args[]) {
        if(System.getProperty("os.name").toLowerCase().startsWith("mac os x"))
            System.setProperty("apple.laf.useScreenMenuBar", "true");

        try {
            /*
             * Description of look&feels:
             *   http://java.sun.com/docs/books/tutorial/uiswing/misc/plaf.html
             */
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) {
            //It's ok. Non-critical.
        }
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ResourceViewer rv = new ResourceViewer();
                rv.pack();
                rv.setLocationRelativeTo(null);
                rv.setVisible(true);

                if(args.length > 0)
                    rv.loadFile(new File(args[0]));
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel backgroundPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu openMenu;
    private javax.swing.JMenuItem openMenuItem;
    // End of variables declaration//GEN-END:variables

}
