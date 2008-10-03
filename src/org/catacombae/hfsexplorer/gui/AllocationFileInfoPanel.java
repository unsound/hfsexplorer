/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * AllocationFileInfoPanel.java
 *
 * Created on 2008-okt-03, 11:29:35
 */

package org.catacombae.hfsexplorer.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.catacombae.csjc.structelements.ArrayBuilder;
import org.catacombae.hfsexplorer.fs.BaseHFSAllocationFileView;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSExtentDescriptor;

/**
 *
 * @author erik
 */
public class AllocationFileInfoPanel extends javax.swing.JPanel {
    private final BaseHFSAllocationFileView afView;

    /** Creates new form AllocationFileInfoPanel */
    public AllocationFileInfoPanel(final BaseHFSAllocationFileView afView) {
        this.afView = afView;

        initComponents();

        Thread t = new Thread(new Runnable() {
            public void run() {
                final long allocatedBlocks = afView.countAllocatedBlocks();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        allocatedBlocksField.setText("" + allocatedBlocks);
                    }
                });

                final long freeBlocks = afView.countFreeBlocks();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        freeBlocksField.setText("" + freeBlocks);
                    }
                });
            }
        });
        t.start();

        allocateButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    long l = Long.parseLong(allocateSizeField.getText());
                    CommonHFSExtentDescriptor[] descs = afView.findFreeSpace(l);
                    if(descs != null) {
                        ArrayBuilder ab = new ArrayBuilder("CommonHFSExtentDescriptor[" + descs.length + "]");
                        for(CommonHFSExtentDescriptor desc : descs) {
                            System.err.println("Found descriptor: ");
                            desc.print(System.err, "  ");
                            ab.add(desc.getStructElements());
                        }
                        resultScroller.setViewportView(new StructViewPanel("Possible allocations", ab.getResult()));
                    }
                    else
                        JOptionPane.showMessageDialog(AllocationFileInfoPanel.this,
                                "Not enough space on volume!", "Info",
                                JOptionPane.INFORMATION_MESSAGE);
                } catch(NumberFormatException ee) {
                    JOptionPane.showMessageDialog(AllocationFileInfoPanel.this,
                            "Invalid long value.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        allocatedBlocksLabel = new javax.swing.JLabel();
        allocatedBlocksField = new javax.swing.JTextField();
        freeBlocksLabel = new javax.swing.JLabel();
        freeBlocksField = new javax.swing.JTextField();
        allocationHeader = new javax.swing.JLabel();
        allocateSizeField = new javax.swing.JTextField();
        allocateSizeLabel = new javax.swing.JLabel();
        allocateButton = new javax.swing.JButton();
        allocateUnitLabel = new javax.swing.JLabel();
        resultScroller = new javax.swing.JScrollPane();
        resultPanel = new javax.swing.JPanel();
        resultLabel = new javax.swing.JLabel();

        allocatedBlocksLabel.setText("Number of allocated blocks:");

        allocatedBlocksField.setEditable(false);
        allocatedBlocksField.setText("calculating...");
        allocatedBlocksField.setBorder(null);
        allocatedBlocksField.setOpaque(false);

        freeBlocksLabel.setText("Number of free blocks:");

        freeBlocksField.setEditable(false);
        freeBlocksField.setText("calculating...");
        freeBlocksField.setBorder(null);
        freeBlocksField.setOpaque(false);

        allocationHeader.setText("Attempt allocation of a region");

        allocateSizeField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        allocateSizeLabel.setText("Size:");

        allocateButton.setText("Allocate");

        allocateUnitLabel.setText("bytes");

        org.jdesktop.layout.GroupLayout resultPanelLayout = new org.jdesktop.layout.GroupLayout(resultPanel);
        resultPanel.setLayout(resultPanelLayout);
        resultPanelLayout.setHorizontalGroup(
            resultPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 340, Short.MAX_VALUE)
        );
        resultPanelLayout.setVerticalGroup(
            resultPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 194, Short.MAX_VALUE)
        );

        resultScroller.setViewportView(resultPanel);

        resultLabel.setText("Result");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, resultScroller, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(allocationHeader)
                            .add(layout.createSequentialGroup()
                                .add(allocateSizeLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(allocateSizeField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(allocateUnitLabel)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(allocateButton))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(allocatedBlocksLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(allocatedBlocksField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(freeBlocksLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(freeBlocksField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, resultLabel))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(allocatedBlocksLabel)
                    .add(allocatedBlocksField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(freeBlocksLabel)
                    .add(freeBlocksField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(allocationHeader)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(allocateSizeLabel)
                    .add(allocateSizeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(allocateButton)
                    .add(allocateUnitLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(resultLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(resultScroller, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton allocateButton;
    private javax.swing.JTextField allocateSizeField;
    private javax.swing.JLabel allocateSizeLabel;
    private javax.swing.JLabel allocateUnitLabel;
    private javax.swing.JTextField allocatedBlocksField;
    private javax.swing.JLabel allocatedBlocksLabel;
    private javax.swing.JLabel allocationHeader;
    private javax.swing.JTextField freeBlocksField;
    private javax.swing.JLabel freeBlocksLabel;
    private javax.swing.JLabel resultLabel;
    private javax.swing.JPanel resultPanel;
    private javax.swing.JScrollPane resultScroller;
    // End of variables declaration//GEN-END:variables

}
