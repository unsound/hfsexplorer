/*-
 * Copyright (C) 2006-2007 Erik Larsson
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

import org.catacombae.hfsexplorer.gui.ExtractProgressPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

public class ExtractProgressDialog extends JDialog implements ProgressMonitor {
    private ExtractProgressPanel progressPanel;
    private JButton cancelButton;
//     private ActionListener cancelListener = null;
    private boolean cancelSignaled = false;
    private long completedSize = 0;
    private long totalSize = -1;
    private DecimalFormat sizeFormatter = new DecimalFormat("0.00");

    public ExtractProgressDialog(Frame owner) {
	this(owner, true);
    }
    public ExtractProgressDialog(Frame owner, boolean modal) {
	super(owner, "Extracting...", modal);
	
	progressPanel = new ExtractProgressPanel();
	cancelButton = progressPanel.cancelButton;
	cancelButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
		    signalCancel();
		}
	    });
	
	setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	addWindowListener(new WindowAdapter() {
                @Override
		public void windowClosing(WindowEvent we) {
		    requestCloseWindow();
		}
	    });
	
	add(progressPanel, BorderLayout.CENTER);
	pack();
	setLocationRelativeTo(null);
	setResizable(false);
    }
    
    public void updateTotalProgress(double fraction, String message) {
	progressPanel.updateTotalProgress(fraction, message);
    }
    
    public void updateCurrentDir(String dirname) {
	progressPanel.updateCurrentDir(dirname);
    }
    public void updateCurrentFile(String filename, long fileSize) {
	progressPanel.updateCurrentFile(filename, fileSize);
    }
    
    public synchronized void signalCancel() {
	cancelButton.setEnabled(false);
	cancelSignaled = true;	
    }
    public boolean cancelSignaled() {
	return cancelSignaled;
    }
    public void confirmCancel() {
	if(isVisible())
	    setVisible(false);
    }
    private synchronized void requestCloseWindow() {
        if(!cancelSignaled)
            signalCancel();
        setVisible(false);
    }
//     public void addCancelListener(ActionListener al) {
// 	cancelListener = al;
// 	cancelButton.addActionListener(al);
//     }
    
    public void setDataSize(long totalSize) {
	this.totalSize = totalSize;
    }
    public void addDataProgress(long dataSize) {
	completedSize += dataSize;
	String message = SpeedUnitUtils.bytesToBinaryUnit(completedSize, sizeFormatter) + "/" +
	    SpeedUnitUtils.bytesToBinaryUnit(totalSize, sizeFormatter);
	updateTotalProgress(((double)completedSize)/totalSize, message);
    }
}
