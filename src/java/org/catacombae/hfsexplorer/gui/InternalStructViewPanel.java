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

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import org.catacombae.csjc.structelements.Array;
import org.catacombae.csjc.structelements.Dictionary;
import org.catacombae.csjc.structelements.FlagField;
import org.catacombae.csjc.structelements.StringRepresentableField;
import org.catacombae.csjc.structelements.StructElement;

/**
 * @author <a href="http://www.catacombae.org/" target="_top">Erik Larsson</a>
 */
public class InternalStructViewPanel extends javax.swing.JPanel {

    /** Creates new form StructViewPanel */
    public InternalStructViewPanel(String label, Dictionary dict) {
        this(label, null, dict, false);
    }

    public InternalStructViewPanel(String label, Dictionary dict,
            boolean noRootEntry)
    {
        this(label, null, dict, noRootEntry);
    }

    public InternalStructViewPanel(String label, String tooltip,
            Dictionary dict)
    {
        this(label, tooltip, false);
    }

    public InternalStructViewPanel(String label, String tooltip,
            Dictionary dict, boolean noRootEntry)
    {
        this(label, tooltip, noRootEntry);

        String[] keys = dict.getKeys();
        JPanel[] subPanels = new JPanel[keys.length];

        for(int i = 0; i < keys.length; ++i) {
            String curKey = keys[i];
            StructElement curElement = dict.getElement(curKey);
            String curDescription = dict.getDescription(curKey);
            String subLabel;
            if(curDescription != null)
                subLabel = curDescription;
            else
                subLabel = curKey;

            subPanels[i] = createPanel(subLabel, curElement);
            if(i != keys.length - 1) {
                subPanels[i].setBorder(new EmptyBorder(0, 0, 5, 0));
            }
        }

        fieldsPanel.removeAll();
        for(JPanel subPanel : subPanels)
            fieldsPanel.add(subPanel);
    }

    public InternalStructViewPanel(String label, Array array) {
        this(label, null, array);
    }

    public InternalStructViewPanel(String label, String tooltip, Array array) {
        this(label, tooltip);

        StructElement[] elems = array.getElements();
        JPanel[] subPanels = new JPanel[elems.length];

        for(int i = 0; i < elems.length; ++i) {
            StructElement curElement = elems[i];

            subPanels[i] = createPanel("[" + i + "]", curElement);
            subPanels[i].setBorder(new EmptyBorder(0, 0, 5, 0));
        }

        fieldsPanel.removeAll();
        for(JPanel subPanel : subPanels)
            fieldsPanel.add(subPanel);
    }

    private InternalStructViewPanel(String label, String tooltip) {
        this(label, tooltip, false);
    }

    private InternalStructViewPanel(String label, String tooltip,
            boolean noRootEntry)
    {
        if(!noRootEntry) {
            initComponents();

            structNameLabel.setText(label);
            if(tooltip != null) {
                structNameLabel.setToolTipText(tooltip);
            }
        }
        else {
            this.setLayout(new java.awt.BorderLayout());
            fieldsPanel = new JPanel();
            fieldsPanel.setLayout(new javax.swing.BoxLayout(fieldsPanel,
                    javax.swing.BoxLayout.PAGE_AXIS));
            this.add(fieldsPanel, java.awt.BorderLayout.CENTER);
        }
    }

    private static JPanel createPanel(String label, StructElement elem) {
        if(elem instanceof StringRepresentableField) {
            StringRepresentableField f = (StringRepresentableField) elem;
            return new TextViewPanel(label + ":", f);
        }
        else if(elem instanceof FlagField) {
            FlagField f = (FlagField) elem;
            return new FlagViewPanel(label, f);
        }
        else if(elem instanceof Array) {
            Array a = (Array) elem;
            return new InternalStructViewPanel(label + ":", a);
        }
        else if(elem instanceof Dictionary) {
            Dictionary d = (Dictionary) elem;
            return new InternalStructViewPanel(label + ":", d);
        }
        else
            throw new RuntimeException("Unsupported StructElement subtype: " +
                    elem.getClass());

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        structNameLabel = new javax.swing.JLabel();
        fieldsPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        structNameLabel.setText("structName");

        fieldsPanel.setLayout(new javax.swing.BoxLayout(fieldsPanel, javax.swing.BoxLayout.PAGE_AXIS));

        jLabel1.setText("jLabel1");
        fieldsPanel.add(jLabel1);

        jLabel2.setText("jLabel2");
        fieldsPanel.add(jLabel2);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(structNameLabel)
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .add(24, 24, 24)
                .add(fieldsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(structNameLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(fieldsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel fieldsPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel structNameLabel;
    // End of variables declaration//GEN-END:variables

}
