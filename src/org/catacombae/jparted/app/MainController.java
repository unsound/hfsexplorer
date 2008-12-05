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

package org.catacombae.jparted.app;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedList;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.jparted.lib.ps.PartitionSystemType;
import org.catacombae.jparted.lib.RandomAccessFileDataLocator;
import org.catacombae.jparted.lib.ps.Partition;
import org.catacombae.jparted.lib.ps.PartitionSystemRecognizer;
import org.catacombae.jparted.lib.ps.PartitionSystemHandler;
import org.catacombae.jparted.lib.ps.PartitionSystemHandlerFactory;

/**
 *
 * @author erik
 */
public class MainController {
    private MainWindow mainWindow;
    private MainPanel mainPanel;
    
    // Model variables
    LinkedList<PartitionSystemHandler> psHandlers =
            new LinkedList<PartitionSystemHandler>();
    
    public MainController() {
        this.mainPanel = new MainPanel();
        this.mainWindow = new MainWindow(this.mainPanel);
        
        mainWindow.setLoadFileItemListener(new LoadFileItemListener());
        mainWindow.setExitItemListener(new ExitItemListener());
        mainWindow.setAboutItemListener(new AboutItemListener());
        mainPanel.setPartitionSystemsBoxListener(new PartitionSystemsBoxListener());
        mainPanel.setSynchronizeButtonListener(new SynchronizeButtonListener());
        
        mainWindow.setDefaultCloseOperation(MainWindow.EXIT_ON_CLOSE);
    }
    public JPanel getPanel() {
        return mainPanel;
    }
    
    public void showMainWindow() {
        // Initialize
        mainPanel.setPartitionSystemsBoxContents(new Vector<String>());
        mainPanel.setPartitionSystemsBoxEnabled(false);
        mainPanel.setSynchronizeButtonEnabled(false);
        
        mainWindow.setVisible(true);
    }
    
    private void exitProgram() {
        mainWindow.setVisible(false);
        System.exit(0);
    }
    
    private class LoadFileItemListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(false);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if(fileChooser.showOpenDialog(mainPanel) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                RandomAccessFileDataLocator loc =
                        new RandomAccessFileDataLocator(selectedFile);
                
                
                Vector<PartitionSystemHandler> detectedPartitionSystems =
                        new Vector<PartitionSystemHandler>();
                Vector<String> detectedPartitionSystemDescriptions = new Vector<String>();
                for(PartitionSystemType curType : PartitionSystemType.values()) {
                    if(curType.isTopLevelCapable()) {
                        PartitionSystemHandlerFactory fac =
                                curType.createDefaultHandlerFactory();
                        if(fac != null) {
                            PartitionSystemRecognizer recognizer =
                                    fac.getRecognizer();
                            ReadableRandomAccessStream stream = loc.createReadOnlyFile();
                            long streamLength = -1;
                            try {
                                streamLength = stream.length();
                            } catch(Exception e) {}
                                    //fac.createDetector(loc);
                            if(recognizer.detect(stream, 0, streamLength)) {
                                PartitionSystemHandler handler = fac.createHandler(loc);
                                detectedPartitionSystems.add(fac.createHandler(loc));
                                detectedPartitionSystemDescriptions.add(fac.getInfo().getPartitionSystemName() +
                                        " (" + handler.getPartitionCount() + " partitions)");
                            }
                        }
                    }
                }
                
                if(detectedPartitionSystemDescriptions.size() > 0) {
                    mainPanel.setPartitionSystemsBoxEnabled(true);
                    mainPanel.setPartitionSystemsBoxContents(detectedPartitionSystemDescriptions);
                }
                else
                    mainPanel.setPartitionSystemsBoxEnabled(false);

                mainPanel.clearPartitionList();
                if(detectedPartitionSystems.size() > 0) {
                    PartitionSystemHandler handler = detectedPartitionSystems.get(0);
                    Partition[] partitions = handler.getPartitions();
                    int i = 0;
                    for(Partition p : partitions) {
                        mainPanel.addPartition("" + ((i++) + 1), p.getType().toString(), "", ""+p.getStartOffset(), (p.getStartOffset()+p.getLength())+"");
                    }
                }
                else
                    JOptionPane.showMessageDialog(mainPanel,
                            "No partition systems found.", "Info",
                            JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    private class ExitItemListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            exitProgram();
        }
    }

    private class AboutItemListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            JOptionPane.showMessageDialog(mainPanel, "jParted 0.1", "About",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
    private class PartitionSystemsBoxListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            
        }
    }
    private class SynchronizeButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
        }
    }

}
