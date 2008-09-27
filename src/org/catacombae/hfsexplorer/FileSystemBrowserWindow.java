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

import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.catacombae.hfsexplorer.FileSystemBrowser.Record;
import org.catacombae.io.ReadableFileStream;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.hfsexplorer.io.ReadableUDIFStream;
import org.catacombae.hfsexplorer.partitioning.*;
import org.catacombae.hfsexplorer.win32.WindowsLowLevelIO;
import org.catacombae.hfsexplorer.gui.*;
import java.util.*;
import java.io.*;
import java.net.URL;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.*;
import javax.swing.*;
//import org.catacombae.hfsexplorer.fsframework.FSFile;
import org.catacombae.hfsexplorer.helpbrowser.HelpBrowserPanel;
import org.catacombae.hfsexplorer.types.HFSPlusVolumeHeader;
import org.catacombae.hfsexplorer.types.hfs.ExtDescriptor;
import org.catacombae.hfsexplorer.types.hfs.HFSPlusWrapperMDB;
import org.catacombae.hfsexplorer.FileSystemBrowser.RecordType;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSVolumeHeader;
import org.catacombae.io.ReadableConcatenatedStream;
import org.catacombae.jparted.lib.ReadableStreamDataLocator;
import org.catacombae.jparted.lib.fs.FSEntry;
import org.catacombae.jparted.lib.fs.FSFile;
import org.catacombae.jparted.lib.fs.FSFolder;
import org.catacombae.jparted.lib.fs.FSFork;
import org.catacombae.jparted.lib.fs.FSForkType;
import org.catacombae.jparted.lib.fs.FileSystemHandlerFactory;
import org.catacombae.jparted.lib.fs.FileSystemHandlerFactory.StandardAttribute;
import org.catacombae.jparted.lib.fs.FileSystemMajorType;
import org.catacombae.jparted.lib.fs.hfsplus.HFSPlusFileSystemHandler;

public class FileSystemBrowserWindow extends JFrame {

    private static final String TITLE_STRING = "HFSExplorer " + HFSExplorer.VERSION;
    private static final ImageIcon[] WINDOW_ICONS = {
        new ImageIcon(ClassLoader.getSystemResource("res/finderdrive_folderback_16.png")),
        new ImageIcon(ClassLoader.getSystemResource("res/finderdrive_folderback_32.png")),
        new ImageIcon(ClassLoader.getSystemResource("res/finderdrive_folderback_48.png"))
    };
    private static final String[] VERSION_INFO_DICTIONARY = {
        "http://www.typhontools.cjb.net/hfsx/version.sdic.txt",
        "http://hem.bredband.net/unsound/hfsx/version.sdic.txt"
    };
    /**
     * The command line argument that makes HFSExplorer print stdout and
     * stderr to a special debug console.
     */
    private static final String DEBUG_CONSOLE_ARG = "-dbgconsole";
    private FileSystemBrowser<FSEntry> fsb;
    // Fast accessors for the corresponding variables in org.catacombae.hfsexplorer.gui.FilesystemBrowserPanel
    private final JCheckBoxMenuItem toggleCachingItem;
    // For managing all files opened with the "open file" command
    private final LinkedList<File> tempFiles = new LinkedList<File>();
    private final JFileChooser fileChooser = new JFileChooser();
    //private HFSPlusFileSystemView fsView;
    private HFSPlusFileSystemHandler fsHandler = null;

    public FileSystemBrowserWindow() {
        this(null);
    }

    public FileSystemBrowserWindow(final DebugConsoleWindow dcw) {
        super(TITLE_STRING);

        if(Java6Specific.isJava6OrHigher())
            Java6Specific.setIconImages(WINDOW_ICONS, this);
        else
            setIconImage(WINDOW_ICONS[0].getImage());

        fsb = new FileSystemBrowser<FSEntry>(new FileSystemProvider());

        final Class objectClass = new Object().getClass();

        // Menus
        JMenuItem loadFSFromDeviceItem = null;
        if(WindowsLowLevelIO.isSystemSupported()) {
            // Only for Windows systems...
            loadFSFromDeviceItem = new JMenuItem("Load file system from device...");
            loadFSFromDeviceItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent ae) {
                    SelectWindowsDeviceDialog deviceDialog =
                            new SelectWindowsDeviceDialog(FileSystemBrowserWindow.this,
                            true,
                            "Load file system from device");
                    deviceDialog.setVisible(true);
                    ReadableRandomAccessStream io = deviceDialog.getPartitionStream();
                    String pathName = deviceDialog.getPathName();
                    if(io != null) {
                        try {
                            loadFS(io, pathName);
                        } catch(Exception e) {
                            System.err.print("INFO: Non-critical exception when trying to load file system from \"" + pathName + "\": ");
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
                                    "Could not find any file systems on device!",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });
            loadFSFromDeviceItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        }

        JMenuItem loadFSFromFileItem = new JMenuItem("Load file system from file...");
        loadFSFromFileItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                //JFileChooser fileChooser = new JFileChooser();
                fileChooser.setMultiSelectionEnabled(false);
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                if(fileChooser.showOpenDialog(FileSystemBrowserWindow.this) ==
                        JFileChooser.APPROVE_OPTION) {
                    try {
                        String pathName = fileChooser.getSelectedFile().getCanonicalPath();
                        loadFS(pathName);
                    } catch(IOException ioe) {
                        ioe.printStackTrace();
                        JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
                                "Count not resolve pathname!",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } catch(Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
                                "Could not read contents of partition!",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        loadFSFromFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        JMenuItem openUDIFItem = new JMenuItem("Open UDIF disk image (.dmg)...");
        openUDIFItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                //JFileChooser fileChooser = new JFileChooser();
                fileChooser.setMultiSelectionEnabled(false);
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                SimpleFileFilter sff = new SimpleFileFilter();
                sff.addExtension("dmg");
                sff.setDescription("Disk images");
                fileChooser.setFileFilter(sff);
                int res = fileChooser.showOpenDialog(FileSystemBrowserWindow.this);
                if(res == JFileChooser.APPROVE_OPTION) {
                    try {
                        File selectedFile = fileChooser.getSelectedFile();
                        String pathName = selectedFile.getCanonicalPath();
                        loadFSWithUDIFAutodetect(pathName);
                    } catch(IOException ioe) {
                        ioe.printStackTrace();
                        JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
                                "Count not resolve pathname!",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } catch(Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
                                "Could not read contents of partition!",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                fileChooser.resetChoosableFileFilters();
            }
        });
        openUDIFItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        JMenuItem openFromPosItem = new JMenuItem("Read file system from specified position in file...");
        openFromPosItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                //JFileChooser fileChooser = new JFileChooser();
                fileChooser.setMultiSelectionEnabled(false);
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                SimpleFileFilter sff = new SimpleFileFilter();
                sff.addExtension("dmg");
                sff.setDescription("Disk images");
                fileChooser.setFileFilter(sff);
                int res = fileChooser.showOpenDialog(FileSystemBrowserWindow.this);
                if(res == JFileChooser.APPROVE_OPTION) {
                    try {
                        File selectedFile = fileChooser.getSelectedFile();
                        String pathName = selectedFile.getCanonicalPath();

                        String s = JOptionPane.showInputDialog(FileSystemBrowserWindow.this,
                                "Enter the byte position of the start of the file system.");
                        long pos = Long.parseLong(s);

                        loadFSWithUDIFAutodetect(pathName, pos);
                    } catch(IOException ioe) {
                        ioe.printStackTrace();
                        JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
                                "Count not resolve pathname!",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } catch(Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
                                "Could not read contents of partition!",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                fileChooser.resetChoosableFileFilters();
            }
        });

        JMenuItem debugConsoleItem = null;
        if(dcw != null) {
            debugConsoleItem = new JMenuItem("Debug console");
            debugConsoleItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent ae) {
                    dcw.setVisible(true);
                }
            });

        }

        JMenuItem exitProgramItem = null;
        if(!System.getProperty("os.name").toLowerCase().startsWith("mac os x")) {
            exitProgramItem = new JMenuItem("Exit");
            exitProgramItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent ae) {
                    exitApplication();
                }
            });
            exitProgramItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        }
        else {
            MacSpecific.registerQuitHandler(new MacSpecific.QuitHandler() {

                public boolean acceptQuit() {
                    exitApplication();
                    return false;
                }
            });
        }

        JMenuItem fsInfoItem = new JMenuItem("File system info");
        fsInfoItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                if(fsHandler != null) {
                    VolumeInfoWindow infoWindow = new VolumeInfoWindow(fsHandler.getFSView());
                    infoWindow.setVisible(true);
                    CommonHFSVolumeHeader cvh = fsHandler.getFSView().getVolumeHeader();
                    if(cvh instanceof CommonHFSVolumeHeader.HFSPlusImplementation) {
                        HFSPlusVolumeHeader vh =
                                ((CommonHFSVolumeHeader.HFSPlusImplementation) cvh).getUnderlying();
                        infoWindow.setVolumeFields(vh);
                        if(vh.getAttributeVolumeJournaled())
                            infoWindow.setJournalFields(fsHandler.getFSView().getJournalInfoBlock());
                        else
                            JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
                                    "No file system loaded.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    else
                        JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
                                "Info window only supported for HFS+/HFSX.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                }

            }
        });
        fsInfoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        toggleCachingItem = new JCheckBoxMenuItem("Use file system caching");
        toggleCachingItem.setState(true);
        toggleCachingItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                if(fsHandler != null) {
                    if(toggleCachingItem.getState()) {
                        System.out.print("Enabling caching...");
                        fsHandler.getFSView().enableFileSystemCaching();
                        System.out.println("done!");
                    }
                    else {
                        System.out.print("Disabling caching...");
                        fsHandler.getFSView().disableFileSystemCaching();
                        System.out.println("done!");
                    }
                }
            }
        });

        JMenuItem setFileReadOffsetItem = new JMenuItem("Set file read offset...");
        setFileReadOffsetItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                String s = JOptionPane.showInputDialog(FileSystemBrowserWindow.this,
                        "Enter offset:", HFSPlusFileSystemView.fileReadOffset);
                if(s != null) {
                    HFSPlusFileSystemView.fileReadOffset = Long.parseLong(s);
                }
            }
        });


        JMenuItem startHelpBrowserItem = new JMenuItem("Help browser");
        startHelpBrowserItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                File f = new File("doc/html/index.html");
                if(f.exists()) {
                    try {
                        HelpBrowserPanel.showHelpBrowserWindow("HFSExplorer help browser", f.toURI().toURL());
                    } catch(MalformedURLException ex) {
                        ex.printStackTrace();
                        Logger.getLogger(FileSystemBrowserWindow.class.getName()).log(Level.WARNING, null, ex);
                    }
                }
            }
        });

        JMenuItem checkUpdatesItem = new JMenuItem("Check for updates...");
        checkUpdatesItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                InputStream infoDictStream = null;
                for(String s : VERSION_INFO_DICTIONARY) {
                    try {
                        System.out.println("Retrieving version info from " + s + "...");
                        infoDictStream = new URL(s).openStream();
                        SimpleDictionaryParser sdp = new SimpleDictionaryParser(infoDictStream);
                        String dictVersion = sdp.getValue("Version");
                        long dictBuildNumber = Long.parseLong(sdp.getValue("Build"));
                        System.out.println("  Version: " + dictVersion);
                        System.out.println("  Build number: " + dictBuildNumber);
                        boolean dictVersionIsHigher = false;
                        if(true) {
                            dictVersionIsHigher = dictBuildNumber > BuildNumber.BUILD_NUMBER;
                        }
                        else { // Old disabled code
                            char[] dictVersionArray = dictVersion.toCharArray();
                            char[] myVersionArray = HFSExplorer.VERSION.toCharArray();
                            int minArrayLength = Math.min(dictVersionArray.length, myVersionArray.length);
                            boolean foundDifference = false;
                            for(int i = 0; i < minArrayLength; ++i) {
                                if(dictVersionArray[i] > myVersionArray[i]) {
                                    dictVersionIsHigher = true;
                                    foundDifference = true;
                                    break;
                                }
                                else if(dictVersionArray[i] < myVersionArray[i]) {
                                    dictVersionIsHigher = false;
                                    foundDifference = true;
                                    break;
                                }
                            }
                            if(!foundDifference)
                                dictVersionIsHigher = dictVersionArray.length > myVersionArray.length;
                        }

                        if(dictVersionIsHigher)
                            JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
                                    "There are updates available!\n" +
                                    "Latest version is: " + dictVersion +
                                    " (build number #" + dictBuildNumber + ")",
                                    "Information", JOptionPane.INFORMATION_MESSAGE);
                        else
                            JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
                                    "There are no updates available.",
                                    "Information", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
                JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
                        "Could not contact version URL.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JMenuItem aboutItem = new JMenuItem("About...");
        aboutItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                String message = "";
                message += "HFSExplorer " + HFSExplorer.VERSION + " Build #" + BuildNumber.BUILD_NUMBER + "\n";
                message += HFSExplorer.COPYRIGHT + "\n";
                for(String notice : HFSExplorer.NOTICES) {
                    message += notice + "\n";
                // System.out.println("Message now: " + message);
                }
                message += "\n Operating system: " + System.getProperty("os.name") + " " + System.getProperty("os.version");
                message += "\n Architecture: " + System.getProperty("os.arch");
                message += "\n Virtual machine: " + System.getProperty("java.vm.vendor") + " " +
                        System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version");
                JOptionPane.showMessageDialog(FileSystemBrowserWindow.this, message,
                        "About", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JMenu fileMenu = new JMenu("File");
        JMenu infoMenu = new JMenu("Tools");
        JMenu helpMenu = new JMenu("Help");
        if(loadFSFromDeviceItem != null)
            fileMenu.add(loadFSFromDeviceItem);
        fileMenu.add(loadFSFromFileItem);
        fileMenu.add(openUDIFItem);
        fileMenu.add(openFromPosItem);
        if(debugConsoleItem != null)
            fileMenu.add(debugConsoleItem);
        if(exitProgramItem != null)
            fileMenu.add(exitProgramItem);
        infoMenu.add(fsInfoItem);
        infoMenu.add(toggleCachingItem);
        infoMenu.add(setFileReadOffsetItem);
        helpMenu.add(startHelpBrowserItem);
        helpMenu.add(checkUpdatesItem);
        helpMenu.add(aboutItem);
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(infoMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
        // /Menus

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent we) {
                exitApplication();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        add(fsb.getViewComponent(), BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
    }

//    public void actionDoubleClickTableEntry(HFSPlusCatalogLeafRecord rec) {
//        HFSPlusCatalogLeafRecordData recData = rec.getData();
//        HFSCatalogNodeID requestedID;
//        if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER &&
//                recData instanceof HFSPlusCatalogFolder) {
//            HFSPlusCatalogFolder catFolder = (HFSPlusCatalogFolder) recData;
//            requestedID = catFolder.getFolderID();
//        }
//        else if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER_THREAD &&
//                recData instanceof HFSPlusCatalogThread) {
//            HFSPlusCatalogThread catThread = (HFSPlusCatalogThread) recData;
//            requestedID = rec.getKey().getParentID();
//        }
//        else if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FILE &&
//                recData instanceof HFSPlusCatalogFile) {
//            if(true) {
//                actionDoubleClickFile(rec, (HFSPlusCatalogFile)recData);
//            }
//            else { // Some normalization testcode, currently disabled.
//                UnicodeNormalizationToolkit ud = UnicodeNormalizationToolkit.getDefaultInstance();
//                String filename = rec.getKey().getNodeName().toString();
//                System.err.println("Decomposed (unmodified):     \"" + filename + "\"");
//                System.err.print("Decomposed (unmodified) hex:");
//                for(char c : filename.toCharArray()) {
//                    System.err.print(" " + Util.toHexStringBE(c));
//                }
//                System.err.println();
//                String composedFilename = ud.compose(filename);
//                System.err.println("Composed:                 \"" + composedFilename + "\"");
//                System.err.print("Composed hex:            ");
//                for(char c : composedFilename.toCharArray()) {
//                    System.err.print(" " + Util.toHexStringBE(c));
//                }
//                System.err.println();
//            }
//            return;
//        }
//        else {
//            throw new RuntimeException("recData instanceof " + recData.getClass().toString());
//        }
//        HFSPlusCatalogLeafRecord[] contents = fsView.listRecords(requestedID);
//        populateTable(contents);
//        fileTableScroller.getVerticalScrollBar().setValue(0);
//
//        List<HFSPlusCatalogLeafRecord> path = fsView.getPathTo(requestedID);
//// 				System.err.println("Path:");
//// 				for(HFSPlusCatalogLeafRecord clf : path)
//// 				    clf.getKey().print(System.err, "  ");
//
//        path.remove(0); // The first element will be the root, and setTreePath doesn't want the root
//        path.remove(path.size() - 1); // The last element will be the thread record for the folder.
//        TreePath selectionPath = dirTree.getSelectionPath();
//        dirTree.expandPath(selectionPath);
//        setTreePath(path);
//        //TreePath selectionPath = dirTree.getSelectionPath();
//        //dirTree.expandPath(selectionPath);
//        selectionPath = dirTree.getSelectionPath();
//        Object[] userObjectPath = selectionPath.getPath();
//        StringBuilder pathString = new StringBuilder("/");
//        for(int i = 1; i < userObjectPath.length; ++i) {
//            pathString.append(userObjectPath[i].toString());
//            pathString.append("/");
//        }
//        addressField.setText(pathString.toString());
//    }

//    private void actionExpandDirTreeNode(NoLeafMutableTreeNode noLeafMutableTreeNode) {
//        Object obj2 = noLeafMutableTreeNode.getUserObject();
//        if(obj2 instanceof RecordNodeStorage) {
//            HFSPlusCatalogLeafRecord rec = ((RecordNodeStorage) obj2).getRecord();
//            HFSPlusCatalogLeafRecordData recData = rec.getData();
//            HFSCatalogNodeID requestedID;
//            if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER &&
//                    recData instanceof HFSPlusCatalogFolder) {
//                HFSPlusCatalogFolder catFolder = (HFSPlusCatalogFolder) recData;
//                requestedID = catFolder.getFolderID();
//            }
//            else if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER_THREAD &&
//                    recData instanceof HFSPlusCatalogThread) {
//                //HFSPlusCatalogThread catThread = (HFSPlusCatalogThread)recData;
//                requestedID = rec.getKey().getParentID();
//            }
//            else {
//                throw new RuntimeException("Invalid type.");
//            }
//            HFSPlusCatalogLeafRecord[] contents = fsView.listRecords(requestedID);
//            populateNode(noLeafMutableTreeNode, contents);
//        }
//    }
//    private boolean ensureFileSystemLoaded() {
//        if(fsView != null) {
//            return true;
//        }
//        else {
//            JOptionPane.showMessageDialog(this, "No file system loaded.",
//                    "Error", JOptionPane.ERROR_MESSAGE);
//            return false;
//        }
//    }

//    public void setStatusLabelText(String text) {
//        statusLabel.setText(text);
//    }
    private void exitApplication() {
        // Clean up temp files.
        if(tempFiles.size() > 0) {
            long totalFileSize = 0;
            for(File tempFile : tempFiles)
                totalFileSize += tempFile.length();
            int res = JOptionPane.showConfirmDialog(this, "You have " + tempFiles.size() + " temporary files with a total size of " + totalFileSize + " bytes in:\n    \"" +
                    System.getProperty("java.io.tmpdir") + "\"\nDo you want to delete them now?",
                    "Cleanup on program exit", JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if(res == JOptionPane.YES_OPTION) {
                for(File tempFile : tempFiles) {
                    if(!tempFile.exists())
                        continue;
                    boolean delRes = tempFile.delete();
                    while(!delRes) {
                        int res2 = JOptionPane.showConfirmDialog(this, "Could not delete file:\n    \"" +
                                tempFile.getAbsolutePath() + "\"\nTry again?",
                                "Could not delete file",
                                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
                        if(res2 == JOptionPane.YES_OPTION)
                            delRes = tempFile.delete();
                        else if(res2 == JOptionPane.NO_OPTION)
                            break;
                        else
                            return;
                    }
                }
            }
            else if(res != JOptionPane.NO_OPTION)
                return;
        }

        setVisible(false);
        if(fsHandler != null) {
            fsHandler.close();
        }
        System.exit(0);
    }

    public void loadFSWithUDIFAutodetect(String filename) {
        loadFSWithUDIFAutodetect(filename, 0);
    }

    public void loadFSWithUDIFAutodetect(String filename, long pos) {
        ReadableRandomAccessStream fsFile;
        try {
            if(WindowsLowLevelIO.isSystemSupported())
                fsFile = new WindowsLowLevelIO(filename);
            else
                fsFile = new ReadableFileStream(filename);

            //System.err.println("Trying to autodetect UDIF structure...");
            if(UDIFRecognizer.isUDIF(fsFile)) {
                //System.err.println("UDIF structure found! Creating stream...");
                ReadableUDIFStream stream = null;
                try {
                    stream = new ReadableUDIFStream(filename);
                } catch(Exception e) {
                    e.printStackTrace();
                    if(e.getMessage().startsWith("java.lang.RuntimeException: No handler for block type")) {
                        JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
                                "UDIF file contains unsupported block types!\n" +
                                "(The file was probably created with BZIP2 or ADC " +
                                "compression, which is unsupported currently)",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    else {
                        JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
                                "UDIF file unsupported or damaged!",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    return;
                }
                if(stream != null) {
                    fsFile.close();
                    fsFile = stream;
                }
            }
            else {
                System.err.println("UDIF structure not found. Proceeding normally...");
            }

            if(pos != 0)
                fsFile = new ReadableConcatenatedStream(fsFile, pos, fsFile.length() - pos);

            loadFS(fsFile, new File(filename).getName());
        } catch(Exception e) {
            System.err.println("Could not open file! Exception thrown:");
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Could not open file:\n    \"" + filename + "\"",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadFS(String filename) {
        ReadableRandomAccessStream fsFile;
        if(WindowsLowLevelIO.isSystemSupported())
            fsFile = new WindowsLowLevelIO(filename);
        else
            fsFile = new ReadableFileStream(filename);

        loadFS(fsFile, new File(filename).getName());
    }

    public void loadFS(ReadableRandomAccessStream fsFile, String displayName) {

        int blockSize = 0x200; // == 512
        int ddrBlockSize;
        long fsOffset;
        long fsLength;

        // Detect partition system
        PartitionSystemRecognizer psRec = new PartitionSystemRecognizer(fsFile);
        PartitionSystem partSys = psRec.getPartitionSystem();
        if(partSys != null) {
            Partition[] partitions = partSys.getUsedPartitionEntries();
            if(partitions.length == 0) {
                // Proceed to detect file system
                fsOffset = 0;
                fsLength = fsFile.length();
            }
            else {
                Object selectedValue;
                int firstPreferredPartition = 0;
                for(int i = 0; i < partitions.length; ++i) {
                    Partition p = partitions[i];
                    if(p.getType() == Partition.PartitionType.APPLE_HFS) {
                        firstPreferredPartition = i;
                        break;
                    }
                }
                while(true) {
                    selectedValue = JOptionPane.showInputDialog(this, "Select which partition to read",
                            "Choose " + partSys.getLongName() + " partition",
                            JOptionPane.QUESTION_MESSAGE,
                            null, partitions, partitions[firstPreferredPartition]);
                    if(selectedValue != null &&
                            selectedValue instanceof Partition) {
                        Partition selectedPartition = (Partition) selectedValue;
                        if(selectedPartition.getType() == Partition.PartitionType.APPLE_HFS)
                            break;
                        else
                            JOptionPane.showMessageDialog(this, "Can't find handler for partition type \"" + selectedPartition.getType() +
                                    "\"", "Unknown partition type", JOptionPane.ERROR_MESSAGE);
                    }
                    else
                        return;
                }
                if(selectedValue instanceof Partition) {
                    Partition selectedPartition = (Partition) selectedValue;
                    fsOffset = selectedPartition.getStartOffset();//getPmPyPartStart()+selectedPartition.getPmLgDataStart())*blockSize;
                    fsLength = selectedPartition.getLength();//getPmDataCnt()*blockSize;
                //System.err.println("DEBUG Selected partition:");
                //selectedPartition.print(System.err, "  ");
                }
                else
                    throw new RuntimeException("Impossible error!");
            }
        }
        else {
            fsOffset = 0;
            fsLength = fsFile.length();
        }

        // Detect HFS file system
        FileSystemRecognizer fsr = new FileSystemRecognizer(fsFile, fsOffset);
        FileSystemRecognizer.FileSystemType fsType = fsr.detectFileSystem();
        if(fsType == FileSystemRecognizer.FileSystemType.HFS_WRAPPED_HFS_PLUS) {
            //System.out.println("Found a wrapped HFS+ volume.");
            byte[] mdbData = new byte[HFSPlusWrapperMDB.STRUCTSIZE];
            fsFile.seek(fsOffset + 1024);
            fsFile.read(mdbData);
            HFSPlusWrapperMDB mdb = new HFSPlusWrapperMDB(mdbData, 0);
            ExtDescriptor xd = mdb.getDrEmbedExtent();
            int hfsBlockSize = mdb.getDrAlBlkSiz();
            //System.out.println("old fsOffset: " + fsOffset);
            fsOffset += mdb.getDrAlBlSt() * 512 + xd.getXdrStABN() * hfsBlockSize; // Lovely method names...
            //System.out.println("new fsOffset: " + fsOffset);
            // redetect with adjusted fsOffset
            fsr = new FileSystemRecognizer(fsFile, fsOffset);
            fsType = fsr.detectFileSystem();
        }
        if(fsType == FileSystemRecognizer.FileSystemType.HFS_PLUS ||
                fsType == FileSystemRecognizer.FileSystemType.HFSX ||
                fsType == FileSystemRecognizer.FileSystemType.HFS) {
            if(fsHandler != null) {
                fsHandler.close();
            }

            final FileSystemMajorType fsMajorType;
            switch(fsType) {
                case HFS:
                    fsMajorType = FileSystemMajorType.APPLE_HFS;
                    break;
                case HFS_PLUS:
                    fsMajorType = FileSystemMajorType.APPLE_HFS_PLUS;
                    break;
                case HFSX:
                    fsMajorType = FileSystemMajorType.APPLE_HFSX;
                    break;
                default:
                    fsMajorType = null;
                    break;
            }
            
            FileSystemHandlerFactory factory = fsMajorType.getDefaultHandlerFactory();
            if(factory.isSupported(StandardAttribute.CACHING_ENABLED)) {
                factory.getCreateAttributes().
                        setBooleanAttribute(StandardAttribute.CACHING_ENABLED,
                        toggleCachingItem.getState());
            }

            System.err.println("loadFS(): fsFile=" + fsFile);
            System.err.println("loadFS(): Creating ReadableConcatenatedStream...");
            ReadableConcatenatedStream stage1 = new ReadableConcatenatedStream(fsFile, fsOffset,
                    fsLength);
            System.err.println("loadFS(): Creating ReadableStreamDataLocator...");
            ReadableStreamDataLocator stage2 = new ReadableStreamDataLocator(stage1);
            System.err.println("loadFS(): Creating fsHandler...");

            fsHandler = (HFSPlusFileSystemHandler) factory.createHandler(stage2);
            FSFolder rootRecord = fsHandler.getRoot();
            //FSEntry[] rootContents = rootRecord.list();
            populateFilesystemGUI(rootRecord);
            setTitle(TITLE_STRING + " - [" + displayName + "]");
        //adjustTableWidth();
        }
        else
            JOptionPane.showMessageDialog(this, "Invalid HFS type.\nProgram supports:\n" +
                    "    " + FileSystemRecognizer.FileSystemType.HFS_PLUS + "\n" +
                    "    " + FileSystemRecognizer.FileSystemType.HFSX + "\n" +
                    "    " + FileSystemRecognizer.FileSystemType.HFS_WRAPPED_HFS_PLUS + "\n" +
                    "\nDetected type is (" + fsType + ").",
                    "Unsupported file system type", JOptionPane.ERROR_MESSAGE);

    }

    private long extractForkToStream(FSFork theFork, OutputStream os, ProgressMonitor pm) throws IOException {
        ReadableRandomAccessStream forkFilter = theFork.getReadableRandomAccessStream();
        //System.out.println("extractForkToStream working with a " + forkFilter.getClass());
        final long originalLength = theFork.getLength();
        long bytesToRead = originalLength;
        byte[] buffer = new byte[4096];
        while(bytesToRead > 0) {
            if(pm.cancelSignaled())
                break;
            //System.out.print("forkFilter.read([].length=" + buffer.length + ", 0, " + (bytesToRead < buffer.length ? (int)bytesToRead : buffer.length) + "...");
            int bytesRead = forkFilter.read(buffer, 0, (bytesToRead < buffer.length ? (int) bytesToRead : buffer.length));
            //System.out.println("done. bytesRead = " + bytesRead);
            if(bytesRead < 0)
                break;
            else {
                //System.out.println("Read the following from the forkfilter (" + bytesRead + " bytes): ");
                //System.out.println(Util.byteArrayToHexString(buffer, 0, bytesRead));
                pm.addDataProgress(bytesRead);
                os.write(buffer, 0, bytesRead);
                bytesToRead -= bytesRead;
            }
        }
        return originalLength - bytesToRead;
    }

    private void populateFilesystemGUI(FSFolder rootFolder) {
        FileSystemBrowser.Record<FSEntry> rootRecord =
                new FileSystemBrowser.Record<FSEntry>(
                FileSystemBrowser.RecordType.FOLDER,
                rootFolder.getName(),
                0,
                rootFolder.getAttributes().getModifyDate(),
                rootFolder);
        fsb.setRoot(rootRecord);
    }

    /*
    private void populateNode(DefaultMutableTreeNode rootNode, HFSPlusCatalogLeafRecord[] contents) {
    boolean folderThreadSet = false;
    boolean hasChildren = false;
    Object o = rootNode.getUserObject();
    if(o instanceof RecordNodeStorage) {
    RecordNodeStorage rootStorage = (RecordNodeStorage)o;
    LinkedList<HFSPlusCatalogLeafRecord> fileThreads = new LinkedList<HFSPlusCatalogLeafRecord>();
    for(HFSPlusCatalogLeafRecord rec : contents) {
    Vector<String> currentRow = new Vector<String>(4);

    HFSPlusCatalogLeafRecordData recData = rec.getData();
    if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FILE &&
    recData instanceof HFSPlusCatalogFile) {
    HFSPlusCatalogFile catFile = (HFSPlusCatalogFile)recData;
    if(!hasChildren) hasChildren = true;
    }
    else if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER &&
    recData instanceof HFSPlusCatalogFolder) {
    HFSPlusCatalogFolder catFolder = (HFSPlusCatalogFolder)recData;
    rootNode.add(new NoLeafMutableTreeNode(new RecordNodeStorage(rec)));
    if(!hasChildren) hasChildren = true;
    }
    else if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER_THREAD &&
    recData instanceof HFSPlusCatalogThread) {
    HFSPlusCatalogThread catThread = (HFSPlusCatalogThread)recData;
    rootStorage.setThread(rec);
    if(!folderThreadSet) folderThreadSet = true;
    }
    else if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FILE_THREAD &&
    recData instanceof HFSPlusCatalogThread) {
    HFSPlusCatalogThread catThread = (HFSPlusCatalogThread)recData;
    fileThreads.addLast(rec);
    }
    else
    System.err.println("WARNING: Encountered unexpected record type (" + recData.getRecordType() + ")");
    }
    if(hasChildren && !folderThreadSet)
    System.err.println("ERROR: Found no folder thread!");
    if(!fileThreads.isEmpty())
    System.err.println("INFORMATION: Found " + fileThreads.size() + " file threads unexpectedly.");
    }
    else
    JOptionPane.showMessageDialog(this, "Unexpected type in node user object. Type: " + o.getClass(),
    "Error", JOptionPane.ERROR_MESSAGE);
    }*/
    /*
    public void populateTable(HFSPlusCatalogLeafRecord[] contents) {
    while(tableModel.getRowCount() > 0) {
    tableModel.removeRow(tableModel.getRowCount()-1);
    }

    for(HFSPlusCatalogLeafRecord rec : contents) {
    Vector<Object> currentRow = new Vector<Object>(4);

    HFSPlusCatalogLeafRecordData recData = rec.getData();
    if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FILE &&
    recData instanceof HFSPlusCatalogFile) {
    HFSPlusCatalogFile catFile = (HFSPlusCatalogFile)recData;
    currentRow.add(new RecordContainer(rec));
    currentRow.add(SpeedUnitUtils.bytesToBinaryUnit(catFile.getDataFork().getLogicalSize()));
    currentRow.add("File");
    DateFormat dti = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
    currentRow.add("" + dti.format(catFile.getContentModDateAsDate()));
    currentRow.add("");
    }
    else if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER &&
    recData instanceof HFSPlusCatalogFolder) {
    HFSPlusCatalogFolder catFolder = (HFSPlusCatalogFolder)recData;
    currentRow.add(new RecordContainer(rec));
    currentRow.add("");
    currentRow.add("Folder");
    DateFormat dti = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
    currentRow.add("" + dti.format(catFolder.getContentModDateAsDate()));
    currentRow.add("");
    }
    else if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER_THREAD &&
    recData instanceof HFSPlusCatalogThread) {
    HFSPlusCatalogThread catThread = (HFSPlusCatalogThread)recData;
    }
    else if(recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FILE_THREAD &&
    recData instanceof HFSPlusCatalogThread) {
    HFSPlusCatalogThread catThread = (HFSPlusCatalogThread)recData;
    }
    else
    System.out.println("WARNING: Encountered unexpected record type (" + recData.getRecordType() + ")");
    if(!currentRow.isEmpty())
    tableModel.addRow(currentRow);


    }
    adjustTableWidth();
    }*/
    /*
    private FSFolder getDirTreeSelection() {
    Object o = dirTree.getLastSelectedPathComponent();
    if(o == null) {
    return null;
    }
    else if(o instanceof DefaultMutableTreeNode) {
    Object o2 = ((DefaultMutableTreeNode) o).getUserObject();
    if(o2 instanceof RecordNodeStorage) {
    return ((RecordNodeStorage) o2).getRecord();
    }
    else {
    throw new RuntimeException("Unexpected data in tree user object. (Internal error, report to " +
    "developer)" + "\nClass: " + o.getClass().toString());
    }
    }
    else {
    throw new RuntimeException("Unexpected data in tree model. (Internal error, report to developer)" +
    "\nClass: " + o.getClass().toString());
    }
    }
     * */
    /** Never returns null. If nothing is selected, a zero-length array is returned.
    A RuntimeException may be thrown in case of an implementation error (i.e. should never happen), in which case
    a detailed explanation is found through Exception.getMessage(). */
//    private HFSPlusCatalogLeafRecord[] getSelectedRecords() {
//	if(dirTreeLastFocus > fileTableLastFocus) {
//            HFSPlusCatalogLeafRecord rec = getDirTreeSelection();
//            if(rec == null)
//                return new HFSPlusCatalogLeafRecord[0];
//            else
//                return new HFSPlusCatalogLeafRecord[] { rec };
//	}
//	else {
//	    int[] selectedRows = fileTable.getSelectedRows();
//	    if(selectedRows.length == 0) {
//		return new HFSPlusCatalogLeafRecord[0];
//	    }
//	    else {
//		final HFSPlusCatalogLeafRecord[] selectedRecords = new HFSPlusCatalogLeafRecord[selectedRows.length];
//		for(int i = 0; i < selectedRows.length; ++i) {
//		    int selectedRow = selectedRows[i];
//		    Object o = tableModel.getValueAt(selectedRow, 0);
//		    HFSPlusCatalogLeafRecord rec;
//		    HFSPlusCatalogLeafRecordData recData;
//		    if(o instanceof RecordContainer) {
//			rec = ((RecordContainer)o).getRecord();
//			selectedRecords[i] = rec;
//		    }
//		    else {
//			throw new RuntimeException("Unexpected data in table model. (Internal error, report to developer)" +
//						   "\nClass: " + o.getClass().toString());
//		    }
//		}
//		return selectedRecords;
//	    }
//	}
//    }
    private void actionDoubleClickFile(final FSFile rec) {
        final JDialog fopFrame = new JDialog(this, rec.getName(), true);
        fopFrame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        ActionListener alOpen = null;
        if(System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            //System.err.println("Windows detected");
            final String finalCommand = "cmd.exe /c start \"HFSExplorer invoker\" \"" + rec.getName() + "\"";
            alOpen = new ActionListener() {

                public void actionPerformed(ActionEvent ae) {
                    File tempDir = new File(System.getProperty("java.io.tmpdir"));
                    if(extract(rec, tempDir, NullProgressMonitor.getInstance()) == 0) {
                        tempFiles.add(new File(tempDir, rec.getName()));
                        try {
// 				System.err.print("Trying to execute:");
// 				for(String s : finalCommand)
// 				    System.err.print(" \"" + s + "\"");
// 				System.err.println(" in directory \"" + tempDir + "\"");
                            Process p = Runtime.getRuntime().exec(finalCommand, null, tempDir);
                            fopFrame.dispose();
                        } catch(Exception e) {
                            String stackTrace = e.toString() + "\n";
                            for(StackTraceElement ste : e.getStackTrace())
                                stackTrace += "    " + ste.toString() + "\n";
                            JOptionPane.showMessageDialog(FileSystemBrowserWindow.this, "Open failed. Exception caught:\n" +
                                    stackTrace,
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    else
                        JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
                                "Error while extracting file to temp dir.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                }
            };
        }
        else if(Java6Specific.isJava6OrHigher() && Java6Specific.canOpen()) {
            //System.err.println("Java 1.6 detected.");
            alOpen = new ActionListener() {

                public void actionPerformed(ActionEvent ae) {
                    File tempDir = new File(System.getProperty("java.io.tmpdir"));
                    if(extract(rec, tempDir, NullProgressMonitor.getInstance()) == 0) {
                        File extractedFile = new File(tempDir, rec.getName());
                        tempFiles.add(new File(tempDir, rec.getName()));
                        try {
                            Java6Specific.openFile(extractedFile);
                            fopFrame.dispose();
                        } catch(IOException e) {
                            JOptionPane.showMessageDialog(FileSystemBrowserWindow.this, "Could not find a handler to open the file with.\n" +
                                    "The file remains in\n    \"" + tempDir + "\"\nuntil you exit the program.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        } catch(Exception e) {
                            String stackTrace = e.toString() + "\n";
                            for(StackTraceElement ste : e.getStackTrace())
                                stackTrace += "    " + ste.toString() + "\n";
                            JOptionPane.showMessageDialog(FileSystemBrowserWindow.this, "Open failed. Exception caught:\n" +
                                    stackTrace,
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            };

        }
        else if(System.getProperty("os.name").toLowerCase().startsWith("mac os x")) {
            //System.err.println("OS X detected");
            final String[] finalCommand = new String[] { "open", rec.getName() };
            alOpen = new ActionListener() {

                public void actionPerformed(ActionEvent ae) {
                    File tempDir = new File(System.getProperty("java.io.tmpdir"));
                    if(extract(rec, tempDir, NullProgressMonitor.getInstance()) == 0) {
                        tempFiles.add(new File(tempDir, rec.getName()));
                        try {
// 				System.err.print("Trying to execute:");
// 				for(String s : finalCommand)
// 				    System.err.print(" \"" + s + "\"");
// 				System.err.println(" in directory \"" + tempDir + "\"");
                            Process p = Runtime.getRuntime().exec(finalCommand, null, tempDir);
                            fopFrame.dispose();
                        } catch(Exception e) {
                            String stackTrace = e.toString() + "\n";
                            for(StackTraceElement ste : e.getStackTrace())
                                stackTrace += "    " + ste.toString() + "\n";
                            JOptionPane.showMessageDialog(FileSystemBrowserWindow.this, "Open failed. Exception caught:\n" +
                                    stackTrace,
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    else
                        JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
                                "Error while extracting file to temp dir.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                }
            };
        }

        ActionListener alSave = new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                actionExtractToDir(rec);
                fopFrame.dispose();
            }
        };
        FileOperationsPanel fop = new FileOperationsPanel(fopFrame, rec.getName(),
                rec.getMainFork().getLength(),
                alOpen, alSave);
        fopFrame.add(fop, BorderLayout.CENTER);
        fopFrame.pack();
        fopFrame.setLocationRelativeTo(null);
        fopFrame.setVisible(true);
    }

    /*
    private void actionGoToParentDir() {
    HFSPlusCatalogLeafRecord targetRecord = getDirTreeSelection();
    if(targetRecord != null) {
    LinkedList<HFSPlusCatalogLeafRecord> recordPath =
    fsView.getPathTo(targetRecord);
    recordPath.removeFirst(); // setTreePath takes a path excluding the root node
    if(recordPath.size() > 0) {
    recordPath.removeLast(); // We seek our parents, not ourselves.
    setTreePath(recordPath);
    }
    }
    }
     * */
    private void actionExtractToDir(FSEntry entry) {
        actionExtractToDir(Arrays.asList(entry));
    }

    private void actionExtractToDir(List<FSEntry> selection) {
        actionExtractToDir(selection, true, false);
    }

    private void actionExtractToDir(final List<FSEntry> selection, final boolean dataFork,
            final boolean resourceFork) {
        if(!dataFork && !resourceFork)
            throw new IllegalArgumentException("Can't choose to extract nothing!");
        try {
            //final List<FSEntry> selection = fsb.getUserObjectSelection();
            if(selection.size() > 0) {
                fileChooser.setMultiSelectionEnabled(false);
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setSelectedFiles(new File[0]);
                if(fileChooser.showDialog(FileSystemBrowserWindow.this, "Extract here") == JFileChooser.APPROVE_OPTION) {
                    final File outDir = fileChooser.getSelectedFile();
                    final ExtractProgressDialog progress = new ExtractProgressDialog(this);
                    Runnable r = new Runnable() {

                        public void run() {
                            // Caching is now turned on or off manually, either for the entire device/file, or not at all.
                            //fsView.retainCatalogFile(); // Cache the catalog file to speed up operations
                            //fsView.enableFileSystemCache();
                            try {
                                long dataSize = 0;
                                LinkedList<FSForkType> forkTypes = new LinkedList<FSForkType>();
                                if(dataFork)
                                    forkTypes.add(FSForkType.DATA);
                                if(resourceFork)
                                    forkTypes.add(FSForkType.MACOS_RESOURCE);

                                dataSize += calculateForkSizeRecursive(selection, forkTypes);
                                progress.setDataSize(dataSize);

                                int errorCount = extract(selection, outDir, progress, dataFork, resourceFork);
                                if(!progress.cancelSignaled()) {
                                    if(errorCount == 0)
                                        JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
                                                "Extraction finished.\n",
                                                "Information",
                                                JOptionPane.INFORMATION_MESSAGE);
                                    else
                                        JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
                                                errorCount + " errors were encountered " +
                                                "during the extraction.\n",
                                                "Information",
                                                JOptionPane.WARNING_MESSAGE);
                                }
                                else
                                    JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
                                            "Extraction was aborted.\n" +
                                            "Please remove the extracted files " +
                                            "manually.\n",
                                            "Aborted extraction",
                                            JOptionPane.WARNING_MESSAGE);
                                progress.setVisible(false);
                            } finally {
                                //fsView.disableFileSystemCache();
                                //fsView.releaseCatalogFile(); // Always release memory
                            }
                        }
                    };
                    new Thread(r).start();
                    progress.setVisible(true);
                }
            }
            else if(selection.size() == 0) {
                JOptionPane.showMessageDialog(this, "No file or folder selected.",
                        "Information", JOptionPane.INFORMATION_MESSAGE);
            }
            else
                throw new RuntimeException("wtf?"); // ;-)
        } catch(RuntimeException re) {
            JOptionPane.showMessageDialog(this, re.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    // There is trouble with this approach in OS X... we don't get a proper DIRECTORIES_ONLY dialog. One idea is to use java.awt.FileDialog and see if it works better. (probably not)
    //java.awt.FileDialog fd = new java.awt.FileDialog(FileSystemBrowserWindow.this, "Extract here", SAVE);
    //File oldDir = fileChooser.getCurrentDirectory();
    //JFileChooser fileChooser2 = new JFileChooser();
    //fileChooser.setCurrentDirectory(oldDir);
    //fileChooser.setMultiSelectionEnabled(false);
    //fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    //System.err.println("curdir: " + fileChooser.getCurrentDirectory());
    //fileChooser.setSelectedFiles(new File[0]);
    //fileChooser.setCurrentDirectory(fileChooser.getCurrentDirectory());
    }

    /**
     * Thank God for overloading.
     * 
     * @param selection
     * @param forkTypes
     * @return
     */
    private long calculateForkSizeRecursive(List<FSEntry> selection, List<FSForkType> forkTypes) {
        return calculateForkSizeRecursive(selection.toArray(new FSEntry[selection.size()]),
                forkTypes.toArray(new FSForkType[forkTypes.size()]));
    }

    /**
     * Calculates the combined sizes of the forks of types <code>forkTypes</code> for the selection,
     * including all files in subdirectories. If <code>forkTypes</code> is empty, all forks are
     * included in the calculation.
     * 
     * @param selection
     * @param forkTypes
     * @return
     */
    private long calculateForkSizeRecursive(FSEntry[] selection, FSForkType... forkTypes) {
        long res = 0;
        for(FSEntry curEntry : selection) {
            if(curEntry instanceof FSFile) {
                FSFile curFile = (FSFile) curEntry;

                if(forkTypes.length > 0) {
                    for(FSForkType t : forkTypes) {
                        FSFork curFork = curFile.getForkByType(t);
                        if(curFork != null)
                            res += curFork.getLength();
                    }
                }
                else
                    res += curFile.getCombinedLength();
            }
            else if(curEntry instanceof FSFolder) {
                FSFolder curFolder = (FSFolder) curEntry;
                res += calculateForkSizeRecursive(curFolder.list(), forkTypes);
            }
            else
                System.err.println("INFO: Silently ignoring FSEntry subtype " +
                        curEntry.getClass());
        }

        return res;
    }

    private void actionGetInfo(FSEntry entry) {
        if(entry instanceof FSFile) {
            FSFile file = (FSFile) entry;
            FileInfoWindow fiw = new FileInfoWindow(entry.getName());
            fiw.setFields(file);
            fiw.setVisible(true);
        }
        else if(entry instanceof FSFolder) {
            FSFolder folder = (FSFolder) entry;
            FolderInfoWindow fiw = new FolderInfoWindow(entry.getName());
            fiw.setFields(folder);
            fiw.setVisible(true);
        }
        else {
            JOptionPane.showMessageDialog(this, "[actionGetInfo()] Record data has unexpected type (" +
                    entry.getClass() + ").\nReport bug to developer.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

//    private void actionGotoDir() {
//	String requestedPath = addressField.getText();
//	String[] components = requestedPath.split("/");
//	LinkedList<HFSPlusCatalogLeafRecord> recordPath = new LinkedList<HFSPlusCatalogLeafRecord>();
//	HFSPlusCatalogLeafRecord currentRecord = fsView.getRoot();
//	HFSPlusCatalogLeafRecordData currentRecordData = currentRecord.getData();
//	//recordPath.addLast(currentRecord);
//	for(String s : components) {
//	    if(!s.trim().equals("")) {
//		if(currentRecordData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER &&
//		   currentRecordData instanceof HFSPlusCatalogFolder) {
//		    HFSPlusCatalogFolder folder = (HFSPlusCatalogFolder) currentRecordData;
//		    HFSPlusCatalogLeafRecord nextRecord = fsView.getRecord(folder.getFolderID(), new HFSUniStr255(s));
//		    
//		    if(nextRecord == null) {
//			currentRecord = null;
//			break;
//		    }
//		    else {
//			currentRecord = nextRecord;
//			currentRecordData = currentRecord.getData();
//			recordPath.addLast(currentRecord);
//		    }
//
//		}
//		else {
//		    currentRecord = null;
//		    break;
//		}
//	    }
//	}
//	
//	if(currentRecord == null)
//	    JOptionPane.showMessageDialog(this, "Path not found!", "Error", JOptionPane.ERROR_MESSAGE);
//	else if(currentRecordData.getRecordType() != HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER)
//	    JOptionPane.showMessageDialog(this, "Requested path points to a file. Can only browse folders...", "Error", JOptionPane.ERROR_MESSAGE);
//	else {
//	    setTreePath(recordPath);
//	}
//	
//    }

//    private void setTreePath(List<HFSPlusCatalogLeafRecord> recordPath) {
//	Object root = dirTree.getModel().getRoot();
//	DefaultMutableTreeNode rootNode;
//	if(root instanceof DefaultMutableTreeNode)
//	    rootNode = (DefaultMutableTreeNode)root;
//	else
//	    throw new RuntimeException("Invalid type in tree");
//	TreePath treePath = new TreePath(rootNode);
//	//System.out.println("Children:");
//	
//	for(HFSPlusCatalogLeafRecord rec : recordPath) {
//	    DefaultMutableTreeNode rootNodeBefore = rootNode;
//	    LinkedList<String> debug = new LinkedList<String>();
//	    for(Enumeration e = rootNode.children() ; e.hasMoreElements() ;) {
//		Object o = e.nextElement();
//		if(o instanceof DefaultMutableTreeNode) {
//		    DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)o;
//		    Object o2 = currentNode.getUserObject();
//		    debug.addLast(o2.toString());
//		    if(o2.toString().equals(rec.getKey().getNodeName().toString())) {
//			rootNode = currentNode;
//			treePath = treePath.pathByAddingChild(rootNode);
//			break;
//		    }
//		}
//	    }
//	    if(rootNode == rootNodeBefore) {
//		System.err.println("Record string: \"" + rec.getKey().getNodeName().toString() + "\"");
//		System.err.println("Contents:");
//		for(String s : debug)
//		    System.err.println("    " + s);
//		throw new RuntimeException("Could not find record in tree!");
//	    }
//	}
//	
//	dirTree.setSelectionPath(treePath);
//	
//	//JOptionPane.showMessageDialog(this, "Found path, and path is folder! :)\nRoot of the tree has class: " + root.getClass(), "YEA", JOptionPane.INFORMATION_MESSAGE);
//    }
    /** <code>progressDialog</code> may NOT be null. */
    protected int extract(FSEntry rec, File outDir, ProgressMonitor progressDialog) {
        return extract(Arrays.asList(rec), outDir, progressDialog, true, false);
    }

    /** <code>progressDialog</code> may NOT be null. */
    protected int extract(FSEntry rec, File outDir, ProgressMonitor progressDialog, boolean dataFork, boolean resourceFork) {
        return extract(Arrays.asList(rec), outDir, progressDialog, dataFork, resourceFork);
    }

    /** <code>progressDialog</code> may NOT be null. */
    protected int extract(FSEntry[] recs, File outDir, ProgressMonitor progressDialog) {
        return extract(Arrays.asList(recs), outDir, progressDialog, true, false);
    }

    /** <code>progressDialog</code> may NOT be null. */
    protected int extract(FSEntry[] recs, File outDir, ProgressMonitor progressDialog, boolean dataFork, boolean resourceFork) {
        return extract(Arrays.asList(recs), outDir, progressDialog, dataFork, resourceFork);
    }

    /** <code>progressDialog</code> may NOT be null. */
    protected int extract(List<FSEntry> recs, File outDir, ProgressMonitor progressDialog) {
        return extract(recs, outDir, progressDialog, true, false);
    }

    /** <code>progressDialog</code> may NOT be null. */
    protected int extract(List<FSEntry> recs, File outDir, ProgressMonitor progressDialog, boolean dataFork, boolean resourceFork) {
        int errorCount = 0;
        for(FSEntry rec : recs) {
            errorCount += extractRecursive(rec, outDir, progressDialog, new ObjectContainer<Boolean>(false), dataFork, resourceFork);
        }
        return errorCount;
    }

    private int extractRecursive(FSEntry rec, File outDir, ProgressMonitor progressDialog,
            ObjectContainer<Boolean> overwriteAll, boolean dataFork, boolean resourceFork) {
        if(!dataFork && !resourceFork)
            throw new IllegalArgumentException("Neither the data fork or resource fork were selected for extraction. Won't do nothing...");
        if(progressDialog.cancelSignaled()) {
            progressDialog.confirmCancel();
            return 0;
        }

        int errorCount = 0;
        if(!outDir.exists()) {
            String[] options = new String[] { "Create directory", "Cancel" };
            int reply =
                    JOptionPane.showOptionDialog(this, "Warning! Target directory:\n    \"" + outDir.getAbsolutePath() + "\"\n" +
                    "does not exist. Do you want to create this directory?",
                    "Warning", JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE, null, options, options[0]);
            if(reply != 0) {
                ++errorCount;
                progressDialog.signalCancel();
                return errorCount;
            }
            else {
                if(!outDir.mkdirs()) {
                    JOptionPane.showMessageDialog(this, "Could not create directory:\n    \"" +
                            outDir.getAbsolutePath() + "\"\n",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    ++errorCount;
                    progressDialog.signalCancel();
                    return errorCount;
                }
            }
        }


        if(rec instanceof FSFile) {
            if(dataFork)
                errorCount += extractFile((FSFile) rec, outDir, progressDialog, overwriteAll, false);
            if(resourceFork)
                errorCount += extractFile((FSFile) rec, outDir, progressDialog, overwriteAll, true);
        }
        else if(rec instanceof FSFolder) {
            String dirName = rec.getName();
            progressDialog.updateCurrentDir(dirName);

            FSEntry[] contents = ((FSFolder) rec).list();
            //System.out.println("folder: \"" + dirName + "\" valence: " + contents.length + " range: " + fractionLowLimit + "-" + fractionHighLimit);
            // We now have the contents of the requested directory
            File thisDir = new File(outDir, dirName);
            if(!overwriteAll.o && thisDir.exists()) {
                String[] options = new String[] { "Continue", "Cancel" };
                int reply = JOptionPane.showOptionDialog(this, "Warning! Directory:\n    \"" + thisDir.getAbsolutePath() + "\"\n" +
                        "already exists. Do you want to continue extracting to this directory?",
                        "Warning", JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE, null, options, options[0]);
                if(reply != 0) {
                    ++errorCount;
                    progressDialog.signalCancel();
                    return errorCount;
                }
            }

            if(thisDir.mkdir() || thisDir.exists()) {
                for(FSEntry outRec : contents) {
                    errorCount += extractRecursive(outRec, thisDir, progressDialog, overwriteAll, dataFork, resourceFork);
                }
            }
            else {
                int reply = JOptionPane.showConfirmDialog(this, "Could not create directory:\n  " +
                        thisDir.getAbsolutePath() + "\nDo you want to " +
                        "continue? (All files under this directory will be " +
                        "skipped)", "Error", JOptionPane.YES_NO_OPTION,
                        JOptionPane.ERROR_MESSAGE);
                if(reply == JOptionPane.NO_OPTION)
                    progressDialog.signalCancel();
                ++errorCount;
            }
        }
// 	else
// 	    System.out.println("thread with range: " + fractionLowLimit + "-" + fractionHighLimit);
        return errorCount;
    }

    private int extractFile(final FSFile rec, final File outDir, final ProgressMonitor progressDialog,
            final ObjectContainer<Boolean> overwriteAll, final boolean extractResourceFork) {
        int errorCount = 0;
        String filename = rec.getName();
        if(extractResourceFork)
            filename = "._" + filename; // Special syntax for resource forks in foreign file systems

        while(true) {
            //System.out.println("file: \"" + filename + "\" range: " + fractionLowLimit + "-" + fractionHighLimit);
            final FSFork theFork;
            if(!extractResourceFork)
                theFork = rec.getMainFork();
            else
                theFork = rec.getForkByType(FSForkType.MACOS_RESOURCE);

            if(theFork == null)
                throw new RuntimeException("Could not find " +
                        (extractResourceFork ? "resource" : "main") + " fork!");

            progressDialog.updateCurrentFile(filename, theFork.getLength());

            //progressDialog.updateTotalProgress(fractionLowLimit);
            File outFile = new File(outDir, filename);

            if(!overwriteAll.o && outFile.exists()) {
                String[] options = new String[] { "Overwrite", "Overwrite all", "Skip file and continue", "Rename file", "Cancel" };
                int reply = JOptionPane.showOptionDialog(this, "File:\n    \"" + outFile.getAbsolutePath() + "\"\n" +
                        "already exists.",
                        "Warning", JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE, null, options, options[0]);
                if(reply == 0);
                else if(reply == 1)
                    overwriteAll.o = true;
                else if(reply == 2) {
                    ++errorCount;
                    break;
                }
                else if(reply == 3) {
                    Object selection = JOptionPane.showInputDialog(this, "Enter the new filename:", "Input filename",
                            JOptionPane.PLAIN_MESSAGE, null, null, filename);
                    if(selection == null) {
                        ++errorCount;
                        progressDialog.signalCancel();
                        break;
                    }
                    else {
                        filename = selection.toString();
                        continue;
                    }
                }
                else {
                    ++errorCount;
                    progressDialog.signalCancel();
                    break;
                }
            }
            try {
// 		    try {
// 			PrintStream p = System.out;
// 			File f = outFile;
// 			p.println("Printing some information about the output file: ");
// 			p.println("f.getParent(): \"" + f.getParent() + "\"");
// 			p.println("f.getName(): \"" + f.getName() + "\"");
// 			p.println("f.getAbsolutePath(): \"" + f.getAbsolutePath() + "\"");
// 			p.println("f.exists(): \"" + f.exists() + "\"");
// 			p.println("f.getCanonicalPath(): \"" + f.getCanonicalPath() + "\"");
// 			//p.println("f.getParent(): \"" + f.getParent() + "\"");
// 		    } catch(Exception e) { e.printStackTrace(); }

                // Test that outFile is valid.
                try {
                    outFile.getCanonicalPath();
                } catch(Exception e) {
                    throw new FileNotFoundException();
                }

                if(!outFile.getParentFile().equals(outDir) || !outFile.getName().equals(filename))
                    throw new FileNotFoundException();
                FileOutputStream fos = new FileOutputStream(outFile);
                extractForkToStream(theFork, fos, progressDialog);
                fos.close();
            //JOptionPane.showMessageDialog(this, "The file was successfully extracted!\n",
            //			  "Extraction complete!", JOptionPane.INFORMATION_MESSAGE);
            } catch(FileNotFoundException fnfe) {
                System.out.println("Could not create file \"" + outFile + "\". The following exception was thrown:");
                fnfe.printStackTrace();
                char[] filenameChars = filename.toCharArray();
                System.out.println("Filename in hex (" + filenameChars.length + " UTF-16BE units):");
                System.out.print("  0x");
                for(char c : filenameChars)
                    System.out.print(" " + Util.toHexStringBE(c));
                System.out.println();

                String[] options = new String[] { "Skip file and continue", "Cancel", "Rename file" };
                int reply = JOptionPane.showOptionDialog(this, "Could not create file \"" + outFile +
                        "\" in folder:\n  " + outDir.getAbsolutePath() + "\n" //+
                        /*"Do you want to continue? (The file will be skipped)"*/,
                        "Error", JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.ERROR_MESSAGE, null, options, options[0]);
// 		    System.out.println("New optionDialog returned: " + reply);
// 		    System.out.println("YES_OPTION: \"" + JOptionPane.YES_OPTION + "\"");
// 		    System.out.println("NO_OPTION: \"" + JOptionPane.NO_OPTION + "\"");
// 		    System.out.println("CANCEL_OPTION: \"" + JOptionPane.CANCEL_OPTION + "\"");
// 		    System.out.println("CLOSED_OPTION: \"" + JOptionPane.CLOSED_OPTION + "\"");
                //System.out.println("_OPTION: \"" + JOptionPane._OPTION + "\"");

                if(reply == 0) {
                    ++errorCount;
                }
                else if(reply == 2) {
                    Object selection = JOptionPane.showInputDialog(this, "Enter the new filename:", "Input filename",
                            JOptionPane.PLAIN_MESSAGE, null, null, filename);
                    if(selection == null) {
                        ++errorCount;
                        progressDialog.signalCancel();
                    }
                    else {
                        filename = selection.toString();
                        continue;
                    }
                }
                else {
                    ++errorCount;
                    progressDialog.signalCancel();
                }

            } catch(IOException ioe) {
                System.err.println("Received I/O exception when trying to write to file \"" + outFile + "\":");
                ioe.printStackTrace();
                String msg = ioe.getMessage();
                int reply = JOptionPane.showConfirmDialog(this, "Could not write to file \"" + filename +
                        "\" under folder:\n  " + outDir.getAbsolutePath() +
                        (msg != null ? "\nSystem message: \"" + msg + "\"" : "") +
                        "\nDo you want to continue?",
                        "I/O Error", JOptionPane.YES_NO_OPTION,
                        JOptionPane.ERROR_MESSAGE);
                ++errorCount;
                if(reply == JOptionPane.NO_OPTION)
                    progressDialog.signalCancel();
            } catch(Throwable e) {
                e.printStackTrace();
                String message = "An exception occurred while extracting \"" + filename + "\"!";
                message += "\n  " + e.toString();
                for(StackTraceElement ste : e.getStackTrace())
                    message += "\n    " + ste.toString();
                message += "\n\nThe file has probably not been extracted.";
                int reply = JOptionPane.showConfirmDialog(this, message +
                        "\nDo you want to continue with the extraction?",
                        "Error", JOptionPane.YES_NO_OPTION,
                        JOptionPane.ERROR_MESSAGE);
                ++errorCount;
                if(reply == JOptionPane.NO_OPTION)
                    progressDialog.signalCancel();
            }
            break;
        }

        return errorCount;
    }

    private class FileSystemProvider implements FileSystemBrowser.FileSystemProvider<FSEntry> {

        public void actionDoubleClickFile(Record<FSEntry> record) {
            FSEntry entry = record.getUserObject();
            if(entry instanceof FSFile)
                FileSystemBrowserWindow.this.actionDoubleClickFile((FSFile) entry);
            else
                throw new RuntimeException("Unexpected FSEntry type: " + entry.getClass());
        }

        public void actionExtractToDir(List<Record<FSEntry>> recordList) {
            List<FSEntry> fsEntryList = new ArrayList<FSEntry>(recordList.size());
            for(Record<FSEntry> rec : recordList)
                fsEntryList.add(rec.getUserObject());
            FileSystemBrowserWindow.this.actionExtractToDir(fsEntryList, true, false);
        }

        public void actionGetInfo(List<Record<FSEntry>> recordList) {
            if(recordList.size() == 1)
                FileSystemBrowserWindow.this.actionGetInfo(recordList.get(0).getUserObject());
            else if(recordList.size() > 1) {
                JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
                        "Please select one entry at a time.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        public JPopupMenu getRightClickRecordPopupMenu(final Record<FSEntry> record) {
            JPopupMenu jpm = new JPopupMenu();

            JMenuItem infoItem = new JMenuItem("Information");
            infoItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    FileSystemBrowserWindow.this.actionGetInfo(record.getUserObject());
                }
            });
            jpm.add(infoItem);

            JMenuItem dataExtractItem = new JMenuItem("Extract data");
            dataExtractItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    List<FSEntry> oneItemList = new ArrayList<FSEntry>(1);
                    oneItemList.add(record.getUserObject());
                    FileSystemBrowserWindow.this.actionExtractToDir(oneItemList, true, false);
                }
            });
            jpm.add(dataExtractItem);

            JMenuItem resExtractItem = new JMenuItem("Extract resource fork(s)");
            resExtractItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    List<FSEntry> oneItemList = new ArrayList<FSEntry>(1);
                    oneItemList.add(record.getUserObject());
                    FileSystemBrowserWindow.this.actionExtractToDir(oneItemList, false, true);
                }
            });
            jpm.add(resExtractItem);

            JMenuItem bothExtractItem = new JMenuItem("Extract data and resource fork(s)");
            bothExtractItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    List<FSEntry> oneItemList = new ArrayList<FSEntry>(1);
                    oneItemList.add(record.getUserObject());
                    FileSystemBrowserWindow.this.actionExtractToDir(oneItemList, true, true);
                }
            });
            jpm.add(bothExtractItem);

            return jpm;
        }

        public boolean isFileSystemLoaded() {
            return fsHandler != null;
        }

        public List<Record<FSEntry>> getFolderContents(List<Record<FSEntry>> folderRecordPath) {
            FSEntry lastEntry = folderRecordPath.get(folderRecordPath.size() - 1).getUserObject();
            if(lastEntry instanceof FSFolder) {
                FSEntry[] entryArray = ((FSFolder) lastEntry).list();
                ArrayList<Record<FSEntry>> entryList = new ArrayList<Record<FSEntry>>(entryArray.length);
                for(FSEntry entry : entryArray) {
                    Record<FSEntry> rec = new FSEntryRecord(entry);
                    entryList.add(rec);
                }
                return entryList;
            }
            else
                throw new RuntimeException("Tried to get folder contents for type " +
                        lastEntry.getClass());
        }

        public String getAddressPath(List<String> pathComponents) {
            StringBuilder sb = new StringBuilder("/");

            for(String name : pathComponents) {
                sb.append(name);
                sb.append("/");
            }
            return sb.toString();
        }

        public String[] parseAddressPath(String targetAddress) {
            if(!targetAddress.startsWith("/"))
                return null;
            else {
                String remainder = targetAddress.substring(1);
                if(remainder.length() == 0)
                    return new String[0];
                else {
                    String[] res = remainder.split("/");
                    return res;
                }
            }
        }
    }

    private static class FSEntryRecord extends Record<FSEntry> {

        public FSEntryRecord(FSEntry entry) {
            super(entryTypeToRecordType(entry), entry.getName(), getEntrySize(entry),
                    entry.getAttributes().getModifyDate(), entry);
        }

        public static RecordType entryTypeToRecordType(FSEntry entry) {
            if(entry instanceof FSFile)
                return RecordType.FILE;
            else if(entry instanceof FSFolder)
                return RecordType.FOLDER;
            else
                throw new IllegalArgumentException("Unsupported FSEntry type: " + entry.getClass());
        }

        public static long getEntrySize(FSEntry entry) {
            if(entry instanceof FSFile)
                return ((FSFile) entry).getMainFork().getLength();
            else if(entry instanceof FSFolder)
                return 0;
            else
                throw new IllegalArgumentException("Unsupported FSEntry type: " + entry.getClass());
        }
    }

    private static class ObjectContainer<A> {

        public A o;

        public ObjectContainer(A o) {
            this.o = o;
        }
    }

    public static void main(String[] args) {
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        /*
         * Description of look&feels:
         *   http://java.sun.com/docs/books/tutorial/uiswing/misc/plaf.html
         */
        } catch(Exception e) {
            //It's ok. Non-critical.
        }

        int parsedArgs = 0;
        final FileSystemBrowserWindow fsbWindow;
        if(args.length > 0 && args[0].equals(DEBUG_CONSOLE_ARG)) {
            DebugConsoleWindow dcw = new DebugConsoleWindow();
            System.setOut(new PrintStream(dcw.debugStream));
            System.setErr(new PrintStream(dcw.debugStream));
            fsbWindow = new FileSystemBrowserWindow(dcw);
            ++parsedArgs;
        }
        else
            fsbWindow = new FileSystemBrowserWindow();

        /*
        System.err.println(FileSystemBrowserWindow.class.getName() + ".main invoked.");
        for(int i = 0; i < args.length; ++i)
        System.err.println("  args[" + i + "]: \"" + args[i] + "\"");
        System.err.println();
        System.err.println("java.library.path=\"" + System.getProperty("java.library.path") + "\"");
         */

        fsbWindow.setVisible(true);

        if(args.length > parsedArgs) {
            String filename = args[parsedArgs];
            try {
                final String pathName = new File(filename).getCanonicalPath();
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        fsbWindow.loadFSWithUDIFAutodetect(pathName);
                    }
                });
            } catch(Exception ioe) {
                if(ioe.getMessage().equals("Could not open file.")) {
                    JOptionPane.showMessageDialog(fsbWindow, "Failed to open file:\n\"" + filename + "\"",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
                else {
                    ioe.printStackTrace();
                    String msg = "Exception while loading file:\n" + "    \"" + filename + "\"\n" + ioe.toString();
                    for(StackTraceElement ste : ioe.getStackTrace())
                        msg += "\n" + ste.toString();
                    JOptionPane.showMessageDialog(fsbWindow, msg, "Exception while loading file", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
