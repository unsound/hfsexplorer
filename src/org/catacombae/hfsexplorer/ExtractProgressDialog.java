/*-
 * Copyright (C) 2006-2008 Erik Larsson
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

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.DecimalFormat;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import org.catacombae.hfsexplorer.gui.ExtractProgressPanel;
import org.catacombae.hfsexplorer.gui.ExtractSettingsPanel;

public class ExtractProgressDialog extends JDialog implements ExtractProgressMonitor {

    private final ExtractProgressPanel progressPanel;
    private final ExtractSettingsPanel settingsPanel;
    private final ExtractProperties extractProperties;
    private final JButton cancelButton;
    private volatile boolean cancelSignaled = false;
    private long completedSize = 0;
    private long totalSize = -1;
    private DecimalFormat sizeFormatter = new DecimalFormat("0.00");

    public ExtractProgressDialog(Frame owner) {
        this(owner, false);
    }

    private ExtractProgressDialog(Frame owner, boolean modal) {
        super(owner, "Extracting...", modal);
        
        final JPanel backgroundPanel = new JPanel();
        
        extractProperties = new ExtractProperties();
        progressPanel = new ExtractProgressPanel();
        settingsPanel = new ExtractSettingsPanel(extractProperties);
        
        cancelButton = progressPanel.cancelButton;
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                signalCancel();
            }
        });
        
        progressPanel.addShowSettingsButtonListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                boolean selected = progressPanel.getShowSettingsButtonSelected();
                if(selected)
                    backgroundPanel.add(settingsPanel);
                else
                    backgroundPanel.remove(settingsPanel);
                pack();
            }
            
        });

        backgroundPanel.setLayout(new BoxLayout(backgroundPanel, BoxLayout.PAGE_AXIS));
        backgroundPanel.add(progressPanel);
        
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent we) {
                requestCloseWindow();
            }
        });
        
        add(backgroundPanel);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    @Override
    public void updateCalculateDir(String dirname) {
        progressPanel.updateCalculateDir(dirname);
    }

    @Override
    public void updateTotalProgress(double fraction, String message) {
        progressPanel.updateTotalProgress(fraction, message);
    }

    @Override
    public void updateCurrentDir(String dirname) {
        progressPanel.updateCurrentDir(dirname);
    }

    @Override
    public void updateCurrentFile(String filename, long fileSize) {
        progressPanel.updateCurrentFile(filename, fileSize);
    }

    @Override
    public synchronized void signalCancel() {
        cancelButton.setEnabled(false);
        cancelSignaled = true;
    }

    @Override
    public boolean cancelSignaled() {
        return cancelSignaled;
    }

    @Override
    public void confirmCancel() {
        if(isVisible())
            dispose();
    }

    private synchronized void requestCloseWindow() {
        if(!cancelSignaled)
            signalCancel();
        dispose();
    }

    @Override
    public void setDataSize(long totalSize) {
        this.totalSize = totalSize;
        addDataProgress(0);
    }

    @Override
    public void addDataProgress(long dataSize) {
        completedSize += dataSize;
        String message = SpeedUnitUtils.bytesToBinaryUnit(completedSize, sizeFormatter) + "/" +
                SpeedUnitUtils.bytesToBinaryUnit(totalSize, sizeFormatter);
        updateTotalProgress(((double) completedSize) / totalSize, message);
    }

    /*
    @Override
    public boolean confirmOverwriteDirectory(File dir) {
        return SimpleGUIProgressMonitor.confirmOverwriteDirectory(this, dir);
    }

    @Override
    public boolean confirmSkipDirectory(String... messageLines) {
        return SimpleGUIProgressMonitor.confirmSkipDirectory(this, messageLines);
    }
    */

    @Override
    public CreateDirectoryFailedAction createDirectoryFailed(String dirname, File parentDirectory) {
        return SimpleGUIProgressMonitor.createDirectoryFailed(this, dirname, parentDirectory);
    }

    @Override
    public CreateFileFailedAction createFileFailed(String filename, File parentDirectory) {
        return SimpleGUIProgressMonitor.createFileFailed(this, filename, parentDirectory);
    }

    @Override
    public DirectoryExistsAction directoryExists(File directory) {
        return SimpleGUIProgressMonitor.directoryExists(this, directory);
    }

    @Override
    public FileExistsAction fileExists(File file) {
        return SimpleGUIProgressMonitor.fileExists(this, file);
    }

    @Override
    public String displayRenamePrompt(String currentName, File outDir) {
        return SimpleGUIProgressMonitor.displayRenamePrompt(this, currentName, outDir);
    }

    @Override
    public ExtractProperties getExtractProperties() {
        return extractProperties;
    }
    
    /*
    public static void main(String[] args) {
        ExtractProgressDialog d = new ExtractProgressDialog(null);
        d.pack();
        d.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        d.setLocationRelativeTo(null);
        d.setVisible(true);
    }
    */
}
