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

package org.catacombae.hfsexplorer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import org.catacombae.hfsexplorer.fs.ResourceForkReader;
import org.catacombae.hfsexplorer.gui.FileInfoPanel;
import org.catacombae.hfsexplorer.gui.FSEntrySummaryPanel;
import org.catacombae.hfsexplorer.gui.FolderInfoPanel;
import org.catacombae.hfsexplorer.gui.ResourceForkViewPanel;
import org.catacombae.hfsexplorer.gui.StructViewPanel;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogFile;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogFolder;
import org.catacombae.hfsexplorer.fs.ResourceForkReader.
        MalformedResourceForkException;
import org.catacombae.io.FileStream;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.storage.fs.FSEntry;
import org.catacombae.storage.fs.FSFile;
import org.catacombae.storage.fs.FSFork;
import org.catacombae.storage.fs.FSForkType;
import org.catacombae.storage.fs.hfscommon.HFSCommonFSFile;
import org.catacombae.storage.fs.hfscommon.HFSCommonFSFolder;
import org.catacombae.storage.fs.hfscommon.HFSCommonFSLink;

public class FileInfoWindow extends JFrame {

    public FileInfoWindow(final FSEntry fsEntry, String[] parentPath) {
        super("Info - " + fsEntry.getName());

        if(Java6Util.isJava6OrHigher()) {
            Java6Util.setIconImages(FileSystemBrowserWindow.WINDOW_ICONS, this);
        }
        else {
            setIconImage(FileSystemBrowserWindow.WINDOW_ICONS[0].getImage());
        }

        JScrollPane summaryPanelScroller = null;
        JScrollPane infoPanelScroller = null;

        JTabbedPane tabs = new JTabbedPane();

        // Summary panel
        try {
            final FSEntrySummaryPanel summaryPanel = new FSEntrySummaryPanel(this, fsEntry, parentPath);
            summaryPanelScroller = new JScrollPane(summaryPanel,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

            tabs.addTab("Summary", summaryPanelScroller);
        } catch(Exception e) {
            GUIUtil.displayExceptionDialog(e, 20, this, "Exception while " +
                    "creating FSEntrySummaryPanel.");
            e.printStackTrace();
        }

        // Details panel
        try {
            JPanel infoPanel = null;
            if(fsEntry instanceof HFSCommonFSFile || fsEntry instanceof HFSCommonFSLink) {
                CommonHFSCatalogFile hfsFile;
                if(fsEntry instanceof HFSCommonFSFile)
                    hfsFile = ((HFSCommonFSFile) fsEntry).getInternalCatalogFile();
                else if(fsEntry instanceof HFSCommonFSLink)
                    hfsFile = ((HFSCommonFSLink) fsEntry).getInternalCatalogFileRecord().getData();
                else
                    throw new RuntimeException();

                if(hfsFile instanceof CommonHFSCatalogFile.HFSPlusImplementation) {
                    FileInfoPanel fip = new FileInfoPanel();
                    fip.setFields(((CommonHFSCatalogFile.HFSPlusImplementation) hfsFile).getUnderlying());
                    infoPanel = fip;
                }
                else {
                    StructViewPanel svp = new StructViewPanel("File", hfsFile.getStructElements());
                    infoPanel = svp;
                }
            }
            else if(fsEntry instanceof HFSCommonFSFolder) {
                CommonHFSCatalogFolder fld = ((HFSCommonFSFolder) fsEntry).getInternalCatalogFolder();
                if(fld instanceof CommonHFSCatalogFolder.HFSPlusImplementation) {
                    FolderInfoPanel fip = new FolderInfoPanel();
                    fip.setFields(((CommonHFSCatalogFolder.HFSPlusImplementation) fld).getUnderlying());
                    infoPanel = fip;
                }
                else {
                    StructViewPanel svp = new StructViewPanel("Folder", fld.getStructElements());
                    infoPanel = svp;
                }
            }

            if(infoPanel != null) {
                infoPanelScroller = new JScrollPane(infoPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

                tabs.addTab("Detailed", infoPanelScroller);
            }

        } catch(Exception e) { e.printStackTrace(); }

        // Resource fork panel
        JPanel resffPanel = null;
        try {
            if(fsEntry instanceof FSFile) {
                FSFile fsFile = (FSFile) fsEntry;
                FSFork resourceFork = fsFile.getForkByType(FSForkType.MACOS_RESOURCE);
                if(resourceFork != null && resourceFork.getLength() > 0) {
                    ReadableRandomAccessStream s =
                            resourceFork.getReadableRandomAccessStream();
                    ResourceForkReader resffReader = null;
                    try {
                        resffReader = new ResourceForkReader(s);
                        resffPanel = new ResourceForkViewPanel(resffReader);
                    } finally {
                        if(resffReader != null) {
                            resffReader.close();
                        }
                        else if(s != null) {
                            s.close();
                        }
                    }
                }
            }
        } catch(MalformedResourceForkException e) {
            System.err.println("Malformed resource fork:");
            e.printStackTrace(System.err);

            resffPanel = new JPanel();
            resffPanel.setLayout(new BorderLayout());

            resffPanel.add(new JLabel("Invalid resource fork data",
                    SwingConstants.CENTER), BorderLayout.CENTER);

            JButton saveDataButton = new JButton("Save data...");
            saveDataButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        final JFileChooser fc = new JFileChooser();
                        if(fc.showSaveDialog(fc) == JFileChooser.APPROVE_OPTION)
                        {
                            final ReadableRandomAccessStream rs =
                                    ((FSFile) fsEntry).
                                    getForkByType(FSForkType.MACOS_RESOURCE).
                                    getReadableRandomAccessStream();
                            final FileStream fs =
                                    new FileStream(fc.getSelectedFile());

                            IOUtil.streamCopy(rs, fs, 1024*1024);
                        }
                    } catch(Throwable t) {
                        System.err.println("Exception while extracting " +
                                "resource fork to file:");
                        t.printStackTrace(System.err);

                        GUIUtil.displayExceptionDialog(t, 20,
                                FileInfoWindow.this,
                                "Exception while extracting resource fork to " +
                                "file:");
                    }
                }
            });
            resffPanel.add(saveDataButton, BorderLayout.SOUTH);
        } catch(Exception e) {
            e.printStackTrace();
            GUIUtil.displayExceptionDialog(e, 20, this, "Exception while creating ResourceForkViewPanel.");
        }

        if(resffPanel != null) {
            tabs.addTab("Resource fork", resffPanel);
        }

        add(tabs, BorderLayout.CENTER);

        if(summaryPanelScroller != null)
            summaryPanelScroller.getVerticalScrollBar().setUnitIncrement(10);
        if(infoPanelScroller != null)
            infoPanelScroller.getVerticalScrollBar().setUnitIncrement(10);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        int width = getSize().width;
        int height = getSize().height;
        int adjustedHeight = width + width / 2;

        if(adjustedHeight < height)
            setSize(width, adjustedHeight);

        setLocationRelativeTo(null);
    }

    /*
    public void setFields(FSFile file) {
        if(file instanceof HFSCommonFSFile) {
            CommonHFSCatalogFile hfsFile = ((HFSCommonFSFile) file).getInternalCatalogFile();
            if(hfsFile instanceof CommonHFSCatalogFile.HFSPlusImplementation) {
                FileInfoPanel infoPanel = new FileInfoPanel();
                infoPanel.setFields(((CommonHFSCatalogFile.HFSPlusImplementation)hfsFile).getUnderlying());
                infoPanelScroller.setViewportView(infoPanel);
            }
            else {
                StructViewPanel svp = new StructViewPanel("Folder:", hfsFile.getStructElements());
                infoPanelScroller.setViewportView(svp);
            }
        }
        else
            throw new RuntimeException("FSFolder type " + file.getClass() +
                    " not yet supported!");
    }
     * */

    /*
    public void setFields(HFSPlusCatalogFile vh) {
	infoPanel.setFields(vh);
    }
     * */
//     public void setJournalFields(JournalInfoBlock jib) {
// 	journalInfoPanel.setFields(jib);
//     }
}
