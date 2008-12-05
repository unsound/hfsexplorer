/*-
 * Copyright (C) 2007 Erik Larsson
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

package org.catacombae.hfsexplorer.testcode;

import java.awt.BorderLayout;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeSet;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import org.catacombae.hfsexplorer.UnicodeNormalizationToolkit;
import org.catacombae.util.Util;

public class VisualizeUnicodeNormalization extends JFrame {

    public VisualizeUnicodeNormalization() {
        super("HFS+ Unicode Decomposition Table");
        JPanel mainPanel = new JPanel();
        JScrollPane mainPanelScroller = new JScrollPane(mainPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanelScroller.getVerticalScrollBar().setUnitIncrement(20);

        UnicodeNormalizationToolkit unt = UnicodeNormalizationToolkit.getDefaultInstance();
        Map<Character, char[]> table = unt.getDecompositionTable();

        StringBuilder sb = new StringBuilder();
        Comparator<Map.Entry<Character, char[]>> cmp = new Comparator<Map.Entry<Character, char[]>>() {

            public int compare(Map.Entry<Character, char[]> o1, Map.Entry<Character, char[]> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }

            @Override
            public boolean equals(Object obj) {
                return super.equals(obj);
            }
        };
        TreeSet<Map.Entry<Character, char[]>> ts = new TreeSet<Map.Entry<Character, char[]>>(cmp);
        for(Map.Entry<Character, char[]> ent : table.entrySet())
            ts.add(ent);
        //ts.addAll(table.entrySet());
        for(Map.Entry<Character, char[]> ent : ts) {
            Character key = ent.getKey();
            char[] value = ent.getValue();
            sb.append(Util.toHexStringBE(key.charValue()));
            sb.append(": \" ");
            sb.append(key.toString());
            sb.append(" \" -> \" ");
            sb.append(value[0]);
            for(int i = 1; i < value.length; ++i) {
                sb.append(" \", \" ");
                sb.append(value[i]);
            }
            sb.append(" \"");
            JLabel cur = new JLabel(sb.toString());
            cur.setFont(new java.awt.Font("Monospaced", 0, 20));
            mainPanel.add(cur);
            sb.setLength(0);
        }

        add(mainPanelScroller, BorderLayout.CENTER);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        JFrame frame = new VisualizeUnicodeNormalization();
        frame.setVisible(true);
    }
}
