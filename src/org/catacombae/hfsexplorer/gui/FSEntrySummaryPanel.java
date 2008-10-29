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
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import org.catacombae.hfsexplorer.ObjectContainer;
import org.catacombae.jparted.lib.fs.FSAttributes;
import org.catacombae.jparted.lib.fs.FSAttributes.POSIXFileAttributes;
import org.catacombae.jparted.lib.fs.FSEntry;
import org.catacombae.jparted.lib.fs.FSFile;
import org.catacombae.jparted.lib.fs.FSFolder;
import org.catacombae.jparted.lib.fs.FSLink;

/**
 *
 * @author erik
 */
public class FSEntrySummaryPanel extends javax.swing.JPanel {
    private boolean cancelSignaled = false;

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
            public void windowClosing() {
                cancelSignaled = true;
            }
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
        if(attrs.hasPOSIXFileAttributes())
            setPOSIXFileAttributes(attrs.getPOSIXFileAttributes());
    }

    private void setPOSIXFileAttributes(POSIXFileAttributes attrs) {
        permissionStringField.setText(attrs.getPermissionString());

        userReadBox.setSelected(attrs.canUserRead());
        userWriteBox.setSelected(attrs.canUserWrite());
        userExecuteBox.setSelected(attrs.canUserExecute());
        groupReadBox.setSelected(attrs.canGroupRead());
        groupWriteBox.setSelected(attrs.canGroupWrite());
        groupExecuteBox.setSelected(attrs.canGroupExecute());
        otherReadBox.setSelected(attrs.canOthersRead());
        otherWriteBox.setSelected(attrs.canOthersWrite());
        otherExecuteBox.setSelected(attrs.canOthersExecute());
        
        ownerIDField.setText(attrs.getUserID() + "");
        groupIDField.setText(attrs.getGroupID() + "");


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
        posixPermissionsPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        userReadBox = new javax.swing.JCheckBox();
        userWriteBox = new javax.swing.JCheckBox();
        userExecuteBox = new javax.swing.JCheckBox();
        userLabel = new javax.swing.JLabel();
        groupLabel = new javax.swing.JLabel();
        groupReadBox = new javax.swing.JCheckBox();
        groupWriteBox = new javax.swing.JCheckBox();
        groupExecuteBox = new javax.swing.JCheckBox();
        otherLabel = new javax.swing.JLabel();
        otherReadBox = new javax.swing.JCheckBox();
        otherWriteBox = new javax.swing.JCheckBox();
        otherExecuteBox = new javax.swing.JCheckBox();
        ownerIDLabel = new javax.swing.JLabel();
        ownerIDField = new javax.swing.JTextField();
        groupIDLabel = new javax.swing.JLabel();
        groupIDField = new javax.swing.JTextField();
        jCheckBox10 = new javax.swing.JCheckBox();
        jCheckBox11 = new javax.swing.JCheckBox();
        jCheckBox12 = new javax.swing.JCheckBox();
        jLabel10 = new javax.swing.JLabel();
        permissionStringField = new javax.swing.JTextField();

        jLabel1.setText("Name:");

        nameField.setText("jTextField1");

        jLabel2.setText("Type:");

        typeField.setText("jTextField2");

        jLabel3.setText("Size:");

        sizeField.setText("jTextField3");

        jLabel4.setText("POSIX permissions:");

        userReadBox.setText("Read");

        userWriteBox.setText("Write");

        userExecuteBox.setText("Execute");

        userLabel.setText("User:");

        groupLabel.setText("Group:");

        groupReadBox.setText("Read");

        groupWriteBox.setText("Write");

        groupExecuteBox.setText("Execute");

        otherLabel.setText("Other:");

        otherReadBox.setText("Read");

        otherWriteBox.setText("Write");

        otherExecuteBox.setText("Execute");

        ownerIDLabel.setText("Owner ID:");

        ownerIDField.setEditable(false);
        ownerIDField.setText("jTextField4");
        ownerIDField.setOpaque(false);

        groupIDLabel.setText("Group ID:");

        groupIDField.setEditable(false);
        groupIDField.setText("jTextField5");
        groupIDField.setOpaque(false);

        jCheckBox10.setText("Set user ID on execution");

        jCheckBox11.setText("Set group ID on execution");

        jCheckBox12.setText("Sticky bit");

        jLabel10.setText("Permission string:");

        permissionStringField.setEditable(false);
        permissionStringField.setText("jTextField6");
        permissionStringField.setOpaque(false);

        org.jdesktop.layout.GroupLayout posixPermissionsPanelLayout = new org.jdesktop.layout.GroupLayout(posixPermissionsPanel);
        posixPermissionsPanel.setLayout(posixPermissionsPanelLayout);
        posixPermissionsPanelLayout.setHorizontalGroup(
            posixPermissionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(posixPermissionsPanelLayout.createSequentialGroup()
                .add(posixPermissionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel4)
                    .add(posixPermissionsPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel10)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(permissionStringField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)))
                .addContainerGap())
            .add(posixPermissionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(posixPermissionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(posixPermissionsPanelLayout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(posixPermissionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(groupLabel)
                            .add(userLabel)
                            .add(otherLabel))
                        .add(18, 18, 18)
                        .add(posixPermissionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(posixPermissionsPanelLayout.createSequentialGroup()
                                .add(otherReadBox)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(otherWriteBox)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(otherExecuteBox))
                            .add(posixPermissionsPanelLayout.createSequentialGroup()
                                .add(groupReadBox)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(groupWriteBox)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(groupExecuteBox))
                            .add(posixPermissionsPanelLayout.createSequentialGroup()
                                .add(userReadBox)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(userWriteBox)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(userExecuteBox))))
                    .add(jCheckBox10)
                    .add(jCheckBox11)
                    .add(jCheckBox12)
                    .add(posixPermissionsPanelLayout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(posixPermissionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(ownerIDLabel)
                            .add(groupIDLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(posixPermissionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(groupIDField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
                            .add(ownerIDField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE))))
                .add(17, 17, 17))
        );
        posixPermissionsPanelLayout.setVerticalGroup(
            posixPermissionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(posixPermissionsPanelLayout.createSequentialGroup()
                .add(jLabel4)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(posixPermissionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel10)
                    .add(permissionStringField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(posixPermissionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(posixPermissionsPanelLayout.createSequentialGroup()
                        .add(userLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(groupLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(otherLabel))
                    .add(posixPermissionsPanelLayout.createSequentialGroup()
                        .add(posixPermissionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(userReadBox)
                            .add(userWriteBox)
                            .add(userExecuteBox))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(posixPermissionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(groupReadBox)
                            .add(groupWriteBox)
                            .add(groupExecuteBox))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(posixPermissionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(otherReadBox)
                            .add(otherWriteBox)
                            .add(otherExecuteBox))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jCheckBox10)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jCheckBox11)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jCheckBox12)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(posixPermissionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(ownerIDLabel)
                    .add(ownerIDField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(posixPermissionsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(groupIDLabel)
                    .add(groupIDField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(nameField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(typeField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(jLabel3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(sizeField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, posixPermissionsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 364, Short.MAX_VALUE)
                .add(posixPermissionsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox groupExecuteBox;
    private javax.swing.JTextField groupIDField;
    private javax.swing.JLabel groupIDLabel;
    private javax.swing.JLabel groupLabel;
    private javax.swing.JCheckBox groupReadBox;
    private javax.swing.JCheckBox groupWriteBox;
    private javax.swing.JCheckBox jCheckBox10;
    private javax.swing.JCheckBox jCheckBox11;
    private javax.swing.JCheckBox jCheckBox12;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField nameField;
    private javax.swing.JCheckBox otherExecuteBox;
    private javax.swing.JLabel otherLabel;
    private javax.swing.JCheckBox otherReadBox;
    private javax.swing.JCheckBox otherWriteBox;
    private javax.swing.JTextField ownerIDField;
    private javax.swing.JLabel ownerIDLabel;
    private javax.swing.JTextField permissionStringField;
    private javax.swing.JPanel posixPermissionsPanel;
    private javax.swing.JTextField sizeField;
    private javax.swing.JTextField typeField;
    private javax.swing.JCheckBox userExecuteBox;
    private javax.swing.JLabel userLabel;
    private javax.swing.JCheckBox userReadBox;
    private javax.swing.JCheckBox userWriteBox;
    // End of variables declaration//GEN-END:variables

    private String getSizeString(long result) {
        return result + " bytes";
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
        if(cancelSignaled)
            return;

        for(FSEntry entry : folder.list()) {
            if(cancelSignaled)
                return;

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
