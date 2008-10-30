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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.catacombae.hfsexplorer.Util;

/**
 *
 * @author <a href="mailto:erik82@kth.se">Erik Larsson</a>
 */
public class MemoryStatisticsPanel extends javax.swing.JPanel {
    
    private final Object syncObj = new Object();
    private boolean abortThread = false;
    
    public MemoryStatisticsPanel() {
        initComponents();
        
        runGcButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Runtime.getRuntime().gc();
            }
            
        });
    }
    
    public void startThread() {
        Runnable r = new Runnable() {
            
            @Override
            public void run() {
                Runtime rt = Runtime.getRuntime();
                synchronized(syncObj) {
                    while(!abortThread) {
                        final long curMaxMem = rt.totalMemory();
                        final long maxMem = rt.maxMemory();
                        final long freeMem = rt.freeMemory();
                        final long allocatedMem = curMaxMem - freeMem;
                        
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                allocatedMemoryField.setText(Util.addUnitSpaces("" + allocatedMem, 3) + " bytes");
                                freeMemoryField.setText(Util.addUnitSpaces("" + freeMem, 3) + " bytes");
                                currentMaxMemoryField.setText(Util.addUnitSpaces("" + curMaxMem, 3) + " bytes");
                                maxMemoryField.setText(Util.addUnitSpaces("" + maxMem, 3) + " bytes");
                            }
                        });
                        
                        try {
                            syncObj.wait(500);
                        } catch(InterruptedException ie) {
                            ie.printStackTrace();
                        }
                    }
                    syncObj.notify();
                }
                
                System.err.println("MemoryStatisticsPanel thread aborted.");
            }
        };
        
        synchronized(syncObj) {
            new Thread(r).start();
        }
    }
    
    public void stopThread() {
        synchronized(syncObj) {
            abortThread = true;
            syncObj.notify();
            try {
                syncObj.wait();
            } catch(InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }
    
    /**
     * Creates a JFrame that encloses a MemoryStatisticsPanel, properly wired to accommodate any
     * window close-events and stop the thread when they happen. This is all that you need in order
     * to create a window showing memory statistics. (Just call setVisible(true) on the returned
     * JFrame to bring it up... it will automatically start collecting memory information.)
     * 
     * @return a JFrame enclosing a MemoryStatisticsPanel.
     */
    public static JFrame createMemoryStatisticsWindow() {
        final JFrame memoryStatisticsWindow = new JFrame("Memory statistics");
        
        final MemoryStatisticsPanel msp = new MemoryStatisticsPanel();
        memoryStatisticsWindow.add(msp);
        memoryStatisticsWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        memoryStatisticsWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                msp.startThread();
            }
            
            @Override
            public void windowClosing(WindowEvent e) {
                //System.err.println("Window closing. Signaling any calculate process to stop.");
                msp.stopThread();
                memoryStatisticsWindow.dispose();
            }
        });
        
        memoryStatisticsWindow.pack();
        memoryStatisticsWindow.setLocationRelativeTo(null);
        memoryStatisticsWindow.setResizable(false);
        return memoryStatisticsWindow;
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
        runGcButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        allocatedMemoryField = new javax.swing.JTextField();
        freeMemoryField = new javax.swing.JTextField();
        currentMaxMemoryField = new javax.swing.JTextField();
        maxMemoryField = new javax.swing.JTextField();

        jLabel1.setText("Allocated memory:");

        runGcButton.setText("Run GC");

        jLabel2.setText("Free memory:");

        jLabel3.setText("Current max:");

        jLabel4.setText("Complete max:");

        allocatedMemoryField.setEditable(false);
        allocatedMemoryField.setBorder(null);
        allocatedMemoryField.setOpaque(false);

        freeMemoryField.setEditable(false);
        freeMemoryField.setBorder(null);
        freeMemoryField.setOpaque(false);

        currentMaxMemoryField.setEditable(false);
        currentMaxMemoryField.setBorder(null);
        currentMaxMemoryField.setOpaque(false);

        maxMemoryField.setEditable(false);
        maxMemoryField.setBorder(null);
        maxMemoryField.setOpaque(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(freeMemoryField, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
                            .addComponent(allocatedMemoryField, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
                            .addComponent(currentMaxMemoryField, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
                            .addComponent(maxMemoryField, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)))
                    .addComponent(runGcButton, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(allocatedMemoryField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(freeMemoryField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(currentMaxMemoryField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(maxMemoryField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(runGcButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField allocatedMemoryField;
    private javax.swing.JTextField currentMaxMemoryField;
    private javax.swing.JTextField freeMemoryField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField maxMemoryField;
    private javax.swing.JButton runGcButton;
    // End of variables declaration//GEN-END:variables

}
