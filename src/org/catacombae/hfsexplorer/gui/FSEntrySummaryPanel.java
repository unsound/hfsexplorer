/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FSEntrySummaryPanel.java
 *
 * Created on 2008-okt-25, 06:02:01
 */

package org.catacombae.hfsexplorer.gui;

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.DecimalFormat;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import org.catacombae.hfsexplorer.ObjectContainer;
import org.catacombae.hfsexplorer.SpeedUnitUtils;
import org.catacombae.jparted.lib.fs.FSAttributes;
import org.catacombae.jparted.lib.fs.FSEntry;
import org.catacombae.jparted.lib.fs.FSFile;
import org.catacombae.jparted.lib.fs.FSFolder;
import org.catacombae.jparted.lib.fs.FSLink;

/**
 *
 * @author erik
 */
public class FSEntrySummaryPanel extends javax.swing.JPanel {
    private volatile boolean cancelSignaled = false;
    private DecimalFormat sizeFormatter = new DecimalFormat("0.00");
    
    FSEntrySummaryPanel() {
        initComponents();
    }

    /* NOTES
     *
     * An FSEntry always has:
     * - Name (every FSEntry must have a name, even if it may be blank)
     * - Location (the parent to the FSEntry). A POSIX pathname (possibly translated) must also
     *   exist.
     * - Type (Folder, Link or File)
     * - Size _can_ be derived from all FSEntries (through recursive search or by looking up a file
     *   entry) and should be considered a must-have field.
     *
     * An FSEntry may optionally have:
     * - Various date variables.
     * - POSIX permissions.
     * - Global attributes such as "hidden", "write protected", "bundle"...
     *
     * An FSFile always has:
     * - A number of forks (possibly 0), where each fork has
     *   - Identifier
     *   - Length
     *
     * An FSFolder always has:
     * - Valence
     *
     * An FSLink always has:
     * - Link target
     *
     * Note to self: The file system model does not support forks tied to folders. This is a
     * limitation. Some file system out there may be using this method to store metadata about
     * folders.
     */

    /** Creates new form FSEntrySummaryPanel */
    public FSEntrySummaryPanel(Window window, FSEntry entry, String[] parentPath) {
        this();

        window.addWindowListener(new WindowAdapter() {

            /*
            @Override
            public void windowOpened(WindowEvent e) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
            */

            @Override
            public void windowClosing(WindowEvent e) {
                //System.err.println("Window closing. Signaling any calculate process to stop.");
                cancelSignaled = true;
            }
            
            /*
            @Override
            public void windowClosed(WindowEvent e) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void windowIconified(WindowEvent e) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void windowActivated(WindowEvent e) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
            */
        });

        nameField.setText(entry.getName());
        final String typeString;
        final String sizeString;
        if(entry instanceof FSFile) {
            FSFile file = (FSFile) entry;
            typeString= "File";
            sizeString = getSizeString(file.getMainFork().getLength());
        }
        else if(entry instanceof FSFolder) {
            FSFolder folder = (FSFolder) entry;
            typeString = "Folder";
            sizeString = "Calculating...";
            startFolderSizeCalculation(folder);
        }
        else if(entry instanceof FSLink) {
            FSLink link = (FSLink) entry;
            FSEntry linkTarget = link.getLinkTarget(parentPath);
            if(linkTarget == null) {
                typeString = "Symbolic link (broken)";
                sizeString = "- (broken link)";
            }
            else if(linkTarget instanceof FSFile) {
                FSFile file = (FSFile) linkTarget;
                typeString = "Symbolic link (file)";
                sizeString = getSizeString(file.getMainFork().getLength());
            }
            else if(linkTarget instanceof FSFolder) {
                FSFolder folder = (FSFolder) linkTarget;
                typeString = "Symbolic link (folder)";
                sizeString = "Calculating...";
                startFolderSizeCalculation(folder);
            }
            else {
                typeString = "Symbolic link (unknown [" +
                        linkTarget.getClass() + "])";
                sizeString = "- (unknown type)";
            }
        }
        else {
            typeString = "Unknown [" + entry.getClass() + "]";
            sizeString = "- (unknown type)";
        }
        typeField.setText(typeString);
        sizeField.setText(sizeString);

        FSAttributes attrs = entry.getAttributes();
        if(attrs.hasPOSIXFileAttributes()) {
            POSIXAttributesPanel attributesPanel =
                    new POSIXAttributesPanel(attrs.getPOSIXFileAttributes());
            extendedInfoStackPanel.add(attributesPanel);
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

        jLabel1 = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        typeField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        sizeField = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        extendedInfoStackPanel = new javax.swing.JPanel();

        jLabel1.setText("Name:");

        nameField.setEditable(false);
        nameField.setText("jTextField1");
        nameField.setOpaque(false);

        jLabel2.setText("Type:");

        typeField.setEditable(false);
        typeField.setText("jTextField2");
        typeField.setOpaque(false);

        jLabel3.setText("Size:");

        sizeField.setEditable(false);
        sizeField.setText("jTextField3");
        sizeField.setOpaque(false);

        extendedInfoStackPanel.setLayout(new javax.swing.BoxLayout(extendedInfoStackPanel, javax.swing.BoxLayout.PAGE_AXIS));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, extendedInfoStackPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(nameField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(typeField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(jLabel3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(sizeField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(nameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(typeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(sizeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(extendedInfoStackPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel extendedInfoStackPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField nameField;
    private javax.swing.JTextField sizeField;
    private javax.swing.JTextField typeField;
    // End of variables declaration//GEN-END:variables

    private String getSizeString(long result) {
        String baseString = Long.toString(result);
        if(result >= 1000) {
            int parts = baseString.length() / 3;
            StringBuilder sizeStringBuilder = new StringBuilder();
            String head = baseString.substring(0, baseString.length() - parts * 3);
            if(head.length() > 0)
                sizeStringBuilder.append(head).append(" ");
            for(int i = parts - 1; i >= 0; --i)
                sizeStringBuilder.append(baseString.substring(baseString.length() - (i + 1) * 3, baseString.length() - i * 3)).append(" ");

            if(result >= 1024)
                return SpeedUnitUtils.bytesToBinaryUnit(result, sizeFormatter) + " (" + sizeStringBuilder.toString() + "bytes" + ")";
            else
                return sizeStringBuilder.toString() + "bytes";
        }
        else
            return baseString + " bytes";
    }

    private void startFolderSizeCalculation(final FSFolder folder) {
        Runnable r = new Runnable () {

            public void run() {
                String resultString;
                try {
                    ObjectContainer<Long> result =
                            new ObjectContainer<Long>((long)0);
                    calculateFolderSize(folder, result);
                    resultString = getSizeString(result.o);
                } catch(Exception e) {
                    e.printStackTrace();
                    resultString = "Exception while calculating! See debug console for info...";
                }
                
                final String finalResultString;
                if(!cancelSignaled)
                    finalResultString = resultString;
                else
                    finalResultString = "Canceled";

                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        sizeField.setText(finalResultString);
                    }
                });
            }
            
        };
        
        new Thread(r).start();
    }

    private void calculateFolderSize(FSFolder folder, ObjectContainer<Long> result) {
        if(cancelSignaled) {
            System.err.println("Calculate process stopping for folder \"" + folder.getName() + "\"");
            return;
        }

        for(FSEntry entry : folder.list()) {
            if(cancelSignaled) {
                System.err.println("Calculate process stopping for folder \"" + folder.getName() + "\", entry \"" + entry.getName() + "\"");
                return;
            }

            if(entry instanceof FSFile) {
                result.o += ((FSFile) entry).getMainFork().getLength();
            }
            else if(entry instanceof FSFolder) {
                calculateFolderSize((FSFolder) entry, result);
            }
            else if(entry instanceof FSLink) {
                /* Do nothing. Symbolic link targets aren't part of the folder. */
            }
            else
                System.err.println("FSEntrySummaryPanel.calculateFolderSize():" +
                        " unexpected type " + entry.getClass());
        }
    }
    
    public static void main(String[] args) {
        JFrame jf = new JFrame("Test");
        jf.add(new JScrollPane(new FSEntrySummaryPanel()));
        jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jf.pack();
        jf.setLocationRelativeTo(null);
        jf.setVisible(true);
    }
}
