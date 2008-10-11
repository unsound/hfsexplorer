/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FlagViewPanel.java
 *
 * Created on 2008-sep-30, 09:54:16
 */

package org.catacombae.hfsexplorer.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.catacombae.csjc.structelements.FlagField;

/**
 *
 * @author erik
 */
public class FlagViewPanel extends javax.swing.JPanel {

    /** Creates new form FlagViewPanel */
    public FlagViewPanel(String label, final FlagField data) {
        initComponents();
        
        flagBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                flagBox.setSelected(data.getValueAsBoolean());
            }
            
        });
        flagBox.setSelected(data.getValueAsBoolean());
        flagBox.setText(label);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        flagLabel = new javax.swing.JLabel();
        flagBox = new javax.swing.JCheckBox();

        flagLabel.setText("jLabel1");

        flagBox.setText(" ");
        flagBox.setFocusable(false);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(flagBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(flagBox)
        );
    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox flagBox;
    private javax.swing.JLabel flagLabel;
    // End of variables declaration//GEN-END:variables

}
