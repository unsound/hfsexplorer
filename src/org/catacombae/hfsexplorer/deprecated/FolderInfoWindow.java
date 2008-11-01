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

package org.catacombae.hfsexplorer.deprecated;

import org.catacombae.hfsexplorer.gui.FolderInfoPanel;
//import org.catacombae.hfsexplorer.gui.JournalInfoPanel;
//import org.catacombae.hfsexplorer.types.JournalInfoBlock;
import java.awt.*;
import javax.swing.*;
import org.catacombae.hfsexplorer.gui.StructViewPanel;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogFolder;
import org.catacombae.jparted.lib.fs.FSFolder;
import org.catacombae.jparted.lib.fs.hfscommon.HFSCommonFSFolder;

@Deprecated
public class FolderInfoWindow extends JFrame {

    private JTabbedPane tabs;
    private JScrollPane infoPanelScroller;
    //private JScrollPane journalInfoPanelScroller;
    //private JPanel backgroundPanel;
    //private JournalInfoPanel journalInfoPanel;

    public FolderInfoWindow(FSFolder fsFolder) {
        super("Info - " + fsFolder.getName());

        tabs = new JTabbedPane();
        //backgroundPanel = new JPanel();
        //backgroundPanel.setLayout(new BorderLayout());
        //journalInfoPanel = new JournalInfoPanel();

        infoPanelScroller = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        //journalInfoPanelScroller = new JScrollPane(journalInfoPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        if(fsFolder instanceof HFSCommonFSFolder) {
            CommonHFSCatalogFolder fld = ((HFSCommonFSFolder) fsFolder).getInternalCatalogFolder();
            if(fld instanceof CommonHFSCatalogFolder.HFSPlusImplementation) {
                FolderInfoPanel infoPanel = new FolderInfoPanel();
                infoPanel.setFields(((CommonHFSCatalogFolder.HFSPlusImplementation)fld).getUnderlying());
                infoPanelScroller.setViewportView(infoPanel);
            }
            else {
                StructViewPanel svp = new StructViewPanel("Folder", fld.getStructElements());
                infoPanelScroller.setViewportView(svp);
            }
        }
        else
            throw new RuntimeException("FSFolder type " + fsFolder.getClass() +
                    " not yet supported!");        
        
        tabs.addTab("Detailed", infoPanelScroller);
        //tabs.addTab("Journal info", journalInfoPanelScroller);
        add(tabs, BorderLayout.CENTER);

        infoPanelScroller.getVerticalScrollBar().setUnitIncrement(10);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        int width = getSize().width;
        int height = getSize().height;
        int adjustedHeight = width + width / 2;
        //System.err.println("w: " + width + " h: " + height + " ah: " + adjustedHeight);
        if(adjustedHeight < height)
            setSize(width, adjustedHeight);

        setLocationRelativeTo(null);
    }

    /*
    public void setFields(FSFolder folder) {
        if(folder instanceof HFSCommonFSFolder) {
            CommonHFSCatalogFolder fld = ((HFSCommonFSFolder) folder).getInternalCatalogFolder();
            if(fld instanceof CommonHFSCatalogFolder.HFSPlusImplementation) {
                FolderInfoPanel infoPanel = new FolderInfoPanel();
                infoPanel.setFields(((CommonHFSCatalogFolder.HFSPlusImplementation)fld).getUnderlying());
                infoPanelScroller.setViewportView(infoPanel);
            }
            else {
                StructViewPanel svp = new StructViewPanel("Folder:", fld.getStructElements());
                infoPanelScroller.setViewportView(svp);
            }
        }
        else
            throw new RuntimeException("FSFolder type " + folder.getClass() +
                    " not yet supported!");
    }
     * */
    
    /*
    public void setFields(HFSPlusCatalogFolder vh) {
        infoPanel.setFields(vh);
    }
     * */
//     public void setJournalFields(JournalInfoBlock jib) {
// 	journalInfoPanel.setFields(jib);
//     }
}
