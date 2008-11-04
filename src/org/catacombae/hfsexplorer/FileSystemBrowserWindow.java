/*-
 * Copyright (C) 2006-2008 Erik Larsson
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

import org.catacombae.hfsexplorer.FileSystemBrowser.Record;
import org.catacombae.hfsexplorer.FileSystemBrowser.RecordType;
import org.catacombae.hfsexplorer.fs.ProgressMonitor;
import org.catacombae.hfsexplorer.gui.FileOperationsPanel;
import org.catacombae.hfsexplorer.helpbrowser.HelpBrowserPanel;
import org.catacombae.hfsexplorer.partitioning.Partition;
import org.catacombae.hfsexplorer.partitioning.PartitionSystem;
import org.catacombae.hfsexplorer.types.hfs.ExtDescriptor;
import org.catacombae.hfsexplorer.types.hfs.HFSPlusWrapperMDB;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSVolumeHeader;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusVolumeHeader;
import org.catacombae.hfsexplorer.win32.WindowsLowLevelIO;
import org.catacombae.io.ReadableConcatenatedStream;
import org.catacombae.io.ReadableFileStream;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.jparted.lib.fs.FSEntry;
import org.catacombae.jparted.lib.fs.FSFile;
import org.catacombae.jparted.lib.fs.FSFolder;
import org.catacombae.jparted.lib.fs.FSFork;
import org.catacombae.jparted.lib.fs.FSForkType;
import org.catacombae.jparted.lib.fs.FileSystemHandlerFactory;
import org.catacombae.jparted.lib.fs.FileSystemHandlerFactory.StandardAttribute;
import org.catacombae.jparted.lib.fs.FileSystemMajorType;
import org.catacombae.jparted.lib.fs.hfscommon.HFSCommonFileSystemHandler;
import org.catacombae.jparted.lib.ReadableStreamDataLocator;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import org.catacombae.dmgextractor.encodings.encrypted.ReadableCEncryptedEncodingStream;
import org.catacombae.dmgextractor.ui.PasswordDialog;
import org.catacombae.hfsexplorer.ExtractProgressMonitor.CreateDirectoryFailedAction;
import org.catacombae.hfsexplorer.ExtractProgressMonitor.CreateFileFailedAction;
import org.catacombae.hfsexplorer.ExtractProgressMonitor.DirectoryExistsAction;
import org.catacombae.hfsexplorer.ExtractProgressMonitor.ExtractProperties;
import org.catacombae.hfsexplorer.ExtractProgressMonitor.FileExistsAction;
import org.catacombae.hfsexplorer.gui.ErrorSummaryPanel;
import org.catacombae.hfsexplorer.gui.MemoryStatisticsPanel;
import org.catacombae.jparted.lib.DataLocator;
import org.catacombae.jparted.lib.fs.FSLink;
import org.catacombae.udif.UDIFRandomAccessStream;

/**
 * The main window for the graphical part of HFSExplorer. This class contains a lot of
 * non-presentation code and is very large. Should be restructured in the future.
 * 
 * @author Erik
 */
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
    private JCheckBoxMenuItem toggleCachingItem;
    // For managing all files opened with the "open file" command
    private final LinkedList<File> tempFiles = new LinkedList<File>();
    private final JFileChooser fileChooser = new JFileChooser();
    //private HFSPlusFileSystemView fsView;
    private final DebugConsoleWindow dcw;
    private HFSCommonFileSystemHandler fsHandler = null;
    /** The backing data locator for the fsHandler. Useful when extracting raw data. */
    private DataLocator fsDataLocator;

    public FileSystemBrowserWindow() {
        this(null);
    }

    public FileSystemBrowserWindow(final DebugConsoleWindow dcw) {
        super(TITLE_STRING);
        this.dcw = dcw;

        if(Java6Util.isJava6OrHigher()) {
            Java6Specific.setIconImages(WINDOW_ICONS, this);
        }
        else {
            setIconImage(WINDOW_ICONS[0].getImage());
        }
        fsb = new FileSystemBrowser<FSEntry>(new FileSystemProvider());

        //final Class objectClass = new Object().getClass();
        setUpMenus();

        if(System.getProperty("os.name").toLowerCase().startsWith("mac os x")) {
            MacSpecific.registerMacApplicationHandler(new MacSpecific.MacApplicationHandler() {
                @Override
                public boolean acceptQuit() {
                    exitApplication();
                    return false;
                }
                
                @Override
                public void showAboutDialog() {
                    actionShowAboutDialog();
                }


            });
        }

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

    private void setUpMenus() {
        // Menus
        JMenuItem loadFSFromDeviceItem = null;
        if(WindowsLowLevelIO.isSystemSupported()) {
            // Only for Windows systems...
            loadFSFromDeviceItem = new JMenuItem("Load file system from device...");
            loadFSFromDeviceItem.addActionListener(new ActionListener() {
                @Override
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

        /*
        JMenuItem loadFSFromFileItem = new JMenuItem("Load file system from file...");
        loadFSFromFileItem.addActionListener(new ActionListener() {
            @Override
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
        */

        JMenuItem openUDIFItem = new JMenuItem("Load file system from file...");
        openUDIFItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                //JFileChooser fileChooser = new JFileChooser();
                fileChooser.setMultiSelectionEnabled(false);
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                
                SimpleFileFilter dmgFilter = new SimpleFileFilter();
                dmgFilter.addExtension("dmg");
                dmgFilter.setDescription("Mac OS X disk images (*.dmg)");
                fileChooser.addChoosableFileFilter(dmgFilter);
                
                SimpleFileFilter cdrFilter = new SimpleFileFilter();
                cdrFilter.addExtension("iso");
                cdrFilter.addExtension("cdr");
                cdrFilter.setDescription("CD/DVD image (*.iso,*.cdr)");
                fileChooser.addChoosableFileFilter(cdrFilter);
                
                SimpleFileFilter imgFilter = new SimpleFileFilter();
                imgFilter.addExtension("img");
                imgFilter.setDescription("Raw disk image (*.img)");
                fileChooser.addChoosableFileFilter(imgFilter);
                
                fileChooser.setFileFilter(fileChooser.getAcceptAllFileFilter());
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
        openUDIFItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        /*
        JMenuItem openFromPosItem = new JMenuItem("Read file system from specified position in file...");
        openFromPosItem.addActionListener(new ActionListener() {
            @Override
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
        */

        JMenuItem debugConsoleItem = null;
        if(dcw != null) {
            debugConsoleItem = new JMenuItem("Debug console");
            debugConsoleItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    dcw.setVisible(true);
                }
            });

        }

        JMenuItem exitProgramItem = null;
        if(!System.getProperty("os.name").toLowerCase().startsWith("mac os x")) {
            exitProgramItem = new JMenuItem("Exit");
            exitProgramItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    exitApplication();
                }
            });
            exitProgramItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        }

        JMenuItem fsInfoItem = new JMenuItem("File system info");
        fsInfoItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if(ensureFileSystemLoaded()) {
                    VolumeInfoWindow infoWindow = new VolumeInfoWindow(fsHandler.getFSView());
                    infoWindow.setVisible(true);

                    CommonHFSVolumeHeader cvh = fsHandler.getFSView().getVolumeHeader();

                    if(cvh instanceof CommonHFSVolumeHeader.HFSPlusImplementation) {
                        HFSPlusVolumeHeader vh =
                                ((CommonHFSVolumeHeader.HFSPlusImplementation) cvh).getUnderlying();
                        //infoWindow.setVolumeFields(vh);
                        /*
                        if(vh.getAttributeVolumeJournaled()) {
                            infoWindow.setJournalFields(fsHandler.getFSView().getJournalInfoBlock());
                        }
                         * */
                    }
                /*
                else
                JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
                "Info window only supported for HFS+/HFSX.", "Error",
                JOptionPane.ERROR_MESSAGE);
                 */

                }
            }
        });
        fsInfoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        toggleCachingItem = new JCheckBoxMenuItem("Use file system caching");
        toggleCachingItem.setState(true);
        toggleCachingItem.addActionListener(new ActionListener() {
            @Override
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
        
        /*
        JMenuItem setFileReadOffsetItem = new JMenuItem("Set file read offset...");
        setFileReadOffsetItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                String s = JOptionPane.showInputDialog(FileSystemBrowserWindow.this,
                        "Enter offset:", BaseHFSFileSystemView.fileReadOffset);
                if(s != null) {
                    BaseHFSFileSystemView.fileReadOffset = Long.parseLong(s);
                }
            }
        });
        */ 
        
        JMenuItem memoryStatisticsItem = new JMenuItem("Memory statistics");
        memoryStatisticsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                MemoryStatisticsPanel.createMemoryStatisticsWindow().setVisible(true);
            }
        });

        JMenuItem createDiskImageItem = new JMenuItem("Create disk image...");
        createDiskImageItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if(ensureFileSystemLoaded()) {
                    actionExtractToDiskImage();
                }
            }


        });


        JMenuItem startHelpBrowserItem = new JMenuItem("Help browser");
        startHelpBrowserItem.addActionListener(new ActionListener() {
            @Override
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
            @Override
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
                            if(!foundDifference) {
                                dictVersionIsHigher = dictVersionArray.length > myVersionArray.length;
                            }
                        }

                        if(dictVersionIsHigher) {
                            JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
                                    "There are updates available!\n" +
                                    "Latest version is: " + dictVersion +
                                    " (build number #" + dictBuildNumber + ")",
                                    "Information", JOptionPane.INFORMATION_MESSAGE);
                        }
                        else {
                            JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
                                    "There are no updates available.",
                                    "Information", JOptionPane.INFORMATION_MESSAGE);
                        }
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

        JMenuItem aboutItem = null;
        if(!System.getProperty("os.name").toLowerCase().startsWith("mac os x")) {
            aboutItem = new JMenuItem("About...");
            aboutItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    actionShowAboutDialog();
                }
            });
        }

        JMenu fileMenu = new JMenu("File");
        JMenu infoMenu = new JMenu("Tools");
        JMenu helpMenu = new JMenu("Help");
        if(loadFSFromDeviceItem != null) {
            fileMenu.add(loadFSFromDeviceItem);
        }
        //fileMenu.add(loadFSFromFileItem);
        fileMenu.add(openUDIFItem);
        //fileMenu.add(openFromPosItem);
        if(debugConsoleItem != null) {
            fileMenu.add(debugConsoleItem);
        }
        if(exitProgramItem != null) {
            fileMenu.add(exitProgramItem);
        }
        infoMenu.add(fsInfoItem);
        infoMenu.add(toggleCachingItem);
        infoMenu.add(createDiskImageItem);
        //infoMenu.add(setFileReadOffsetItem);
        infoMenu.add(memoryStatisticsItem);
        helpMenu.add(startHelpBrowserItem);
        helpMenu.add(checkUpdatesItem);
        if(aboutItem != null)
            helpMenu.add(aboutItem);
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(infoMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    // /Menus
    }

    private boolean ensureFileSystemLoaded() {
        if(fsHandler != null) {
            return true;
        }
        else {
            JOptionPane.showMessageDialog(this, "No file system " +
                    "loaded.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void exitApplication() {
        try {
        // Clean up temp files.
            if(tempFiles.size() > 0) {
                long totalFileSize = 0;
                for(File tempFile : tempFiles) {
                    totalFileSize += tempFile.length();
                }
                int res = JOptionPane.showConfirmDialog(this, "You have " + tempFiles.size() + " temporary files with a total size of " + totalFileSize + " bytes in:\n    \"" +
                        System.getProperty("java.io.tmpdir") + "\"\nDo you want to delete them now?",
                        "Cleanup on program exit", JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if(res == JOptionPane.YES_OPTION) {
                    for(File tempFile : tempFiles) {
                        if(!tempFile.exists()) {
                            continue;
                        }
                        boolean delRes = tempFile.delete();
                        while(!delRes) {
                            int res2 = JOptionPane.showConfirmDialog(this, "Could not delete file:\n    \"" +
                                    tempFile.getAbsolutePath() + "\"\nTry again?",
                                    "Could not delete file",
                                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
                            if(res2 == JOptionPane.YES_OPTION) {
                                delRes = tempFile.delete();
                            }
                            else if(res2 == JOptionPane.NO_OPTION) {
                                break;
                            }
                            else {
                                return;
                            }
                        }
                    }
                }
                else if(res != JOptionPane.NO_OPTION) {
                    return;
                }
            }

            setVisible(false);
            if(fsHandler != null) {
                fsHandler.close();
            }
        } catch(Throwable t) {
            GUIUtil.displayExceptionDialog(t, 20, this, "Exception when exiting application");
        } finally {
            System.exit(0);
        }
    }

    public void loadFSWithUDIFAutodetect(String filename) {
        loadFSWithUDIFAutodetect(filename, 0);
    }

    public void loadFSWithUDIFAutodetect(String filename, long pos) {
        ReadableRandomAccessStream fsFile;
        try {
            if(WindowsLowLevelIO.isSystemSupported()) {
                fsFile = new WindowsLowLevelIO(filename);
            }
            else {
                fsFile = new ReadableFileStream(filename);
            }
            try {
                System.err.println("Trying to detect CEncryptedEncoding structure...");
                if(ReadableCEncryptedEncodingStream.isCEncryptedEncoding(fsFile)) {
                    System.err.println("CEncryptedEncoding structure found! Creating filter stream...");
                    while(true) {
                        char[] res = PasswordDialog.showDialog(null, "Reading encrypted disk image...",
                                "You need to enter a password to unlock this disk image:");
                        if(res == null)
                            return;
                        else {
                            try {
                                ReadableCEncryptedEncodingStream stream =
                                        new ReadableCEncryptedEncodingStream(fsFile, res);
                                fsFile = stream;
                                break;
                            } catch(Exception e) {
                                JOptionPane.showMessageDialog(null,
                                        "Incorrect password.", "Reading encrypted disk image...",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }
                else {
                    System.err.println("CEncryptedEncoding structure not found. Proceeding...");
                }
            } catch(Exception e) {
                System.err.println("[INFO] Non-critical exception while trying to detect CEncryptedEncoding structure:");
                e.printStackTrace();
            }
            
            try {
                System.err.println("Trying to detect UDIF structure...");
                if(UDIFRecognizer.isUDIF(fsFile)) {
                    System.err.println("UDIF structure found! Creating filter stream...");
                    UDIFRandomAccessStream stream = null;
                    try {
                        stream = new UDIFRandomAccessStream(fsFile);
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
                        fsFile = stream;
                    }
                }
                else {
                    System.err.println("UDIF structure not found. Proceeding...");
                }
            } catch(Exception e) {
                System.err.println("[INFO] Non-critical exception while trying to detect UDIF structure:");
                e.printStackTrace();
            }

            if(pos != 0) {
                fsFile = new ReadableConcatenatedStream(fsFile, pos, fsFile.length() - pos);
            }
            String displayName;
            try {
                displayName = new File(filename).getCanonicalFile().getName();
            } catch(Exception e) {
                displayName = filename;
            }
            loadFS(fsFile, displayName);
        } catch(Exception e) {
            System.err.println("Could not open file! Exception thrown:");
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Could not open file:\n    \"" + filename + "\"",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadFS(String filename) {
        ReadableRandomAccessStream fsFile;
        if(WindowsLowLevelIO.isSystemSupported()) {
            fsFile = new WindowsLowLevelIO(filename);
        }
        else {
            fsFile = new ReadableFileStream(filename);
        }
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
                        if(selectedPartition.getType() == Partition.PartitionType.APPLE_HFS) {
                            break;
                        }
                        else {
                            JOptionPane.showMessageDialog(this, "Can't find handler for partition type \"" + selectedPartition.getType() +
                                    "\"", "Unknown partition type", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    else {
                        return;
                    }
                }
                if(selectedValue instanceof Partition) {
                    Partition selectedPartition = (Partition) selectedValue;
                    fsOffset = selectedPartition.getStartOffset();//getPmPyPartStart()+selectedPartition.getPmLgDataStart())*blockSize;
                    fsLength = selectedPartition.getLength();//getPmDataCnt()*blockSize;
		    //System.err.println("DEBUG Selected partition:");
		    //selectedPartition.print(System.err, "  ");
                }
                else {
                    throw new RuntimeException("Impossible error!");
                }
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
            
            fsb.setRoot(null);
            if(fsHandler != null) {
                fsHandler.close();
                fsHandler = null;
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

            FileSystemHandlerFactory factory = fsMajorType.createDefaultHandlerFactory();
            if(factory.isSupported(StandardAttribute.CACHING_ENABLED)) {
                factory.getCreateAttributes().
                        setBooleanAttribute(StandardAttribute.CACHING_ENABLED,
                        toggleCachingItem.getState());
            }

            //System.err.println("loadFS(): fsFile=" + fsFile);
            //System.err.println("loadFS(): Creating ReadableConcatenatedStream...");
            ReadableConcatenatedStream stage1 = new ReadableConcatenatedStream(fsFile, fsOffset,
                    fsLength);
            //System.err.println("loadFS(): Creating ReadableStreamDataLocator...");
            this.fsDataLocator = new ReadableStreamDataLocator(stage1);
            //System.err.println("loadFS(): Creating fsHandler...");

            fsHandler = (HFSCommonFileSystemHandler) factory.createHandler(fsDataLocator);
            FSFolder rootRecord = fsHandler.getRoot();
            //FSEntry[] rootContents = rootRecord.list();
            populateFilesystemGUI(rootRecord);
            setTitle(TITLE_STRING + " - [" + displayName + "]");
        //adjustTableWidth();
        }
        else {
            JOptionPane.showMessageDialog(this, "Invalid HFS type.\nProgram supports:\n" +
                    "    " + FileSystemRecognizer.FileSystemType.HFS_PLUS + "\n" +
                    "    " + FileSystemRecognizer.FileSystemType.HFSX + "\n" +
                    "    " + FileSystemRecognizer.FileSystemType.HFS_WRAPPED_HFS_PLUS + "\n" +
                    "\nDetected type is (" + fsType + ").",
                    "Unsupported file system type", JOptionPane.ERROR_MESSAGE);
        }
    }

    private long extractForkToStream(FSFork theFork, OutputStream os, ProgressMonitor pm) throws IOException {
        ReadableRandomAccessStream forkFilter = theFork.getReadableRandomAccessStream();
        //System.out.println("extractForkToStream working with a " + forkFilter.getClass());
        final long originalLength = theFork.getLength();
        long bytesToRead = originalLength;
        byte[] buffer = new byte[4096];
        while(bytesToRead > 0) {
            if(pm.cancelSignaled()) {
                break;
            //System.out.print("forkFilter.read([].length=" + buffer.length + ", 0, " + (bytesToRead < buffer.length ? (int)bytesToRead : buffer.length) + "...");
            }
            int bytesRead = forkFilter.read(buffer, 0, (bytesToRead < buffer.length ? (int) bytesToRead : buffer.length));
            //System.out.println("done. bytesRead = " + bytesRead);
            if(bytesRead < 0) {
                break;
            }
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

    private void actionDoubleClickFile(final String[] parentPath, final FSFile rec) {
        final JDialog fopFrame = new JDialog(this, rec.getName(), true);
        fopFrame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        ActionListener alOpen = null;
        if(System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            //System.err.println("Windows detected");
            final String finalCommand = "cmd.exe /c start \"HFSExplorer invoker\" \"" + rec.getName() + "\"";
            alOpen = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    File tempDir = new File(System.getProperty("java.io.tmpdir"));
                    LinkedList<String> errorMessages = new LinkedList<String>();
                    extract(parentPath, rec, tempDir, new SimpleGUIProgressMonitor(fopFrame), errorMessages, true);
                    if(errorMessages.size() == 0) {
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
                            for(StackTraceElement ste : e.getStackTrace()) {
                                stackTrace += "    " + ste.toString() + "\n";
                            }
                            JOptionPane.showMessageDialog(FileSystemBrowserWindow.this, "Open failed. Exception caught:\n" +
                                    stackTrace,
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    else {
                        JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
                                "Error while extracting file to temp dir.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
        }
        else if(Java6Util.isJava6OrHigher() && Java6Specific.canOpen()) {
            //System.err.println("Java 1.6 detected.");
            alOpen = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    File tempDir = new File(System.getProperty("java.io.tmpdir"));
                    LinkedList<String> errorMessages = new LinkedList<String>();
                    extract(parentPath, rec, tempDir, new SimpleGUIProgressMonitor(fopFrame), errorMessages, true);
                    if(errorMessages.size() == 0) {
                        File extractedFile = new File(tempDir, rec.getName());
                        tempFiles.add(new File(tempDir, rec.getName()));
                        try {
                            Java6Specific.openFile(extractedFile);
                            fopFrame.dispose();
                        } catch(IOException e) {
                            JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
                                    "Could not find a handler to open the file with.\n" +
                                    "The file remains in\n" +
                                    "    \"" + tempDir + "\"\n" +
                                    "until you exit the program.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        } catch(Exception e) {
                            String stackTrace = e.toString() + "\n";
                            for(StackTraceElement ste : e.getStackTrace()) {
                                stackTrace += "    " + ste.toString() + "\n";
                            }
                            JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
                                    "Open failed. Exception caught:\n" + stackTrace,
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            };

        }
        else if(System.getProperty("os.name").toLowerCase().startsWith("mac os x")) {
            //System.err.println("OS X detected");
            final String[] finalCommand = new String[]{"open", rec.getName()};
            alOpen = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    File tempDir = new File(System.getProperty("java.io.tmpdir"));
                    LinkedList<String> errorMessages = new LinkedList<String>();
                    extract(parentPath, rec, tempDir, new SimpleGUIProgressMonitor(fopFrame), errorMessages, true);
                    if(errorMessages.size() == 0) {
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
                            for(StackTraceElement ste : e.getStackTrace()) {
                                stackTrace += "    " + ste.toString() + "\n";
                            }
                            JOptionPane.showMessageDialog(FileSystemBrowserWindow.this, "Open failed. Exception caught:\n" +
                                    stackTrace,
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    else {
                        JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
                                "Error while extracting file to temp dir.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
        }

        ActionListener alSave = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                actionExtractToDir(parentPath, rec);
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

    private void actionExtractToDiskImage() {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.setMultiSelectionEnabled(false);
        SimplerFileFilter ffDmg = new SimplerFileFilter(".dmg", "Mac OS X read/write disk image (.dmg)");
        jfc.setFileFilter(ffDmg);
        if(jfc.showSaveDialog(FileSystemBrowserWindow.this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();
            final File saveFile;
            FileFilter selectedFileFilter = jfc.getFileFilter();
            if(selectedFileFilter instanceof SimplerFileFilter) {
                SimplerFileFilter sff = (SimplerFileFilter)selectedFileFilter;
                if(!selectedFile.getName().endsWith(sff.getExtension()))
                    saveFile = new File(selectedFile.getParentFile(), selectedFile.getName() + sff.getExtension());
                else
                    saveFile = selectedFile;
            }
            else {
                saveFile = selectedFile;
            }
            
            if(saveFile.exists()) {
                int res = JOptionPane.showConfirmDialog(this, "The file:\n  " + saveFile.getPath() +
                        "\nAlready exists. Do you want to overwrite?", "Confirm overwrite",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if(res != JOptionPane.YES_OPTION)
                    return;
            }
            
            final javax.swing.ProgressMonitor pm =
                    new javax.swing.ProgressMonitor(FileSystemBrowserWindow.this,
                    "Extracting file system data to disk image...",
                    "Starting extraction...", 0, Integer.MAX_VALUE);
            pm.setMillisToDecideToPopup(0);
            pm.setMillisToPopup(0);
            pm.setProgress(0);

            Runnable r = new Runnable() {
                @Override
                public void run() {
                    ReadableRandomAccessStream fsStream = fsDataLocator.createReadOnlyFile();
                    FileOutputStream fileOut = null;
                    try {
                        fileOut = new FileOutputStream(saveFile);

                        DecimalFormat df = new DecimalFormat("0.00");

                        CommonHFSVolumeHeader vh = fsHandler.getFSView().getVolumeHeader();
                        final long bytesToExtract = vh.getFileSystemEnd();

                        String bytesToExtractString = SpeedUnitUtils.bytesToBinaryUnit(bytesToExtract, df);
                        long lastUpdateTimestamp = 0;
                        long bytesExtracted = 0;
                        byte[] buffer = new byte[64 * 1024]; // We read in 64 KiB blocks
                        while(bytesExtracted < bytesToExtract && !pm.isCanceled()) {
                            long bytesLeftToRead = bytesToExtract - bytesExtracted;
                            int curBytesToRead = (int)(bytesLeftToRead < buffer.length ? bytesLeftToRead : buffer.length);
                            int bytesRead = fsStream.read(buffer, 0, curBytesToRead);
                            if(bytesRead > 0) {
                                fileOut.write(buffer, 0, bytesRead);
                                bytesExtracted += bytesRead;

                                // Update user progress (not too often)
                                long currentTimestamp = System.currentTimeMillis();
                                long millisSinceLastUpdate = currentTimestamp - lastUpdateTimestamp;
                                if(millisSinceLastUpdate >= 40) {
                                    pm.setProgress((int) ((bytesExtracted / (double) bytesToExtract) * Integer.MAX_VALUE));
                                    pm.setNote("Extracted " + SpeedUnitUtils.bytesToBinaryUnit(bytesExtracted, df) +
                                            " / " + bytesToExtractString + " ...");
                                    lastUpdateTimestamp = currentTimestamp;
                                }
                            }
                            else {
                                throw new RuntimeException("Unexpectedly reached end of file!" +
                                        " fp=" + fsStream.getFilePointer() + " length=" +
                                        fsStream.length() + " bytesExtracted=" + bytesExtracted +
                                        " bytesToExtract=" + bytesToExtract);
                            }
                        }


                    } catch(Exception e) {
                        e.printStackTrace();
                        GUIUtil.displayExceptionDialog(e, 15, FileSystemBrowserWindow.this,
                                "Exception while extracting data!");
                    } finally {
                        pm.close();
                        try {
                            fsStream.close();
                            if(fileOut != null) {
                                fileOut.close();
                            }
                        } catch(Exception e) {
                            e.printStackTrace();
                            GUIUtil.displayExceptionDialog(e, FileSystemBrowserWindow.this);
                        }
                    }
                }
            };

            new Thread(r).start();
        }
    }

    private void actionExtractToDir(final String[] parentPath, FSEntry entry) {
        actionExtractToDir(parentPath, Arrays.asList(entry));
    }

    private void actionExtractToDir(final String[] parentPath, List<FSEntry> selection) {
        actionExtractToDir(parentPath, selection, true, false);
    }
    
    private void actionExtractToDir(final String[] parentPath, final List<FSEntry> selection,
            final boolean dataFork, final boolean resourceFork) {
        if(!dataFork && !resourceFork) {
            throw new IllegalArgumentException("Can't choose to extract nothing!");
        }
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
                        @Override
                        public void run() {
                            // Caching is now turned on or off manually, either for the entire device/file, or not at all.
                            //fsView.retainCatalogFile(); // Cache the catalog file to speed up operations
                            //fsView.enableFileSystemCache();
                            try {
                                LinkedList<FSForkType> forkTypes = new LinkedList<FSForkType>();
                                if(dataFork) {
                                    forkTypes.add(FSForkType.DATA);
                                }
                                if(resourceFork) {
                                    forkTypes.add(FSForkType.MACOS_RESOURCE);
                                }

                                LinkedList<String> dirStack = new LinkedList<String>();
                                if(parentPath != null) {
                                    //System.err.println("parentPath: " + Util.concatenateStrings(parentPath, "/"));
                                    for(String pathComponent : parentPath)
                                        dirStack.addLast(pathComponent);
                                }

                                int res = JOptionPane.showConfirmDialog(progress,
                                        "Do you want to follow symbolic links while extracting?\n" +
                                        "Following symbolic links means that the extracted tree will" +
                                        "more closely match the percieved file system tree, but it\n" +
                                        "increases the size of the extracted data, the time that it " +
                                        "takes to extract it, and puts a lot of identical files at\n" +
                                        "different locations in your target folder.",
                                        "Follow symbolic links?", JOptionPane.YES_NO_CANCEL_OPTION,
                                        JOptionPane.QUESTION_MESSAGE);
                                
                                final boolean followSymlinks;
                                if(res == JOptionPane.YES_OPTION)
                                    followSymlinks = true;
                                else if(res == JOptionPane.NO_OPTION)
                                    followSymlinks = false;
                                else
                                    return;
                                    
                                /*
                                System.err.println("TEST traversing with traverseTree");
                                traverseTree(parentPath, selection, new NullTreeVisitor(), followSymlinks);
                                if(true)
                                    return;
                                */
                                
                                long dataSize = calculateForkSizeRecursive(parentPath, selection,
                                        progress, forkTypes, followSymlinks);
                                if(false) {
                                    if(progress.cancelSignaled())
                                        System.err.println("Size calculation aborted. Calculated size: " + dataSize + " bytes");
                                    else
                                        System.err.println("Size calculation completed. Total size: " + dataSize + " bytes");
                                    return;
                                }
                                
                                //JOptionPane.showMessageDialog(progress, "dataSize = " + dataSize);
                                
                                if(progress.cancelSignaled())
                                    progress.confirmCancel();
                                else {
                                    progress.setDataSize(dataSize);
                                    
                                    LinkedList<String> errorMessages = new LinkedList<String>();
                                    extract(parentPath, selection, outDir, progress, errorMessages,
                                            followSymlinks, forkTypes);
                                    if(progress.cancelSignaled())
                                        errorMessages.addLast("User aborted extraction.");
                                    
                                    if(!progress.cancelSignaled()) {
                                        if(errorMessages.size() == 0) {
                                            JOptionPane.showMessageDialog(progress,
                                                    "Extraction finished.", "Information",
                                                    JOptionPane.INFORMATION_MESSAGE);
                                        }
                                        else {
                                            ErrorSummaryPanel.createErrorSummaryDialog(progress, errorMessages).setVisible(true);
                                            /*
                                            JOptionPane.showMessageDialog(progress, errorMessages.size() +
                                                    " errors were encountered during the extraction.",
                                                    "Information", JOptionPane.WARNING_MESSAGE);
                                            */
                                        }
                                    }
                                    else {
                                        JOptionPane.showMessageDialog(progress, "Extraction was aborted.\n" +
                                                "Please remove the extracted files manually.",
                                                "Aborted extraction", JOptionPane.WARNING_MESSAGE);
                                        progress.confirmCancel();
                                    }
                                }
                            } catch(Throwable t) {
                                t.printStackTrace();
                                GUIUtil.displayExceptionDialog(t, progress);
                            } finally {
                                progress.dispose();
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
            else {
                throw new RuntimeException("wtf?"); // ;-)
            }
        } catch(RuntimeException re) {
            re.printStackTrace();
            GUIUtil.displayExceptionDialog(re, this);
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
     * Calculates the combined size of the forks of types <code>forkTypes</code> for the selection,
     * including for all files in subdirectories, recursively. If <code>forkTypes</code> is empty,
     * all forks are included in the calculation.
     * 
     * @param parentPath the parent path of the entries in <code>selection</code>.
     * @param selection the source entries for the calculation.
     * @param progress the progress monitor that recieves updates about our current state and
     * decides whether or not to abort.
     * @param forkTypes the fork types to include in the calculation.
     * @param followSymlinks whether or not symbolic links should be followed in the tree traversal.
     * @return the combined size of the forks of types <code>forkTypes</code> for the selection,
     * including for all files in subdirectories, recursively.
     */
    private long calculateForkSizeRecursive(String[] parentPath, List<FSEntry> selection,
            ExtractProgressMonitor progress, LinkedList<FSForkType> forkTypes, boolean followSymlinks) {
        // "If forkTypes is empty, all forks are included in the calculation."
        if(forkTypes.size() == 0) {
            for(FSForkType forkType : fsHandler.getSupportedForkTypes())
                forkTypes.add(forkType);
        }
        
        CalculateTreeSizeVisitor sizeVisitor =
                new CalculateTreeSizeVisitor(progress, forkTypes);
        traverseTree(parentPath, selection, sizeVisitor, followSymlinks);
        return sizeVisitor.getSize();
    }
    
    private void traverseTree(String[] parentPath, List<FSEntry> entries, TreeVisitor visitor,
            boolean followSymbolicLinks) {
        LinkedList<String[]> absPathsStack = new LinkedList<String[]>();
        LinkedList<String> pathStack = new LinkedList<String>();
        
        if(parentPath != null) {
            absPathsStack.addLast(parentPath);
            for(String pathComponent : parentPath)
                pathStack.addLast(pathComponent);
        }
        
        FSEntry[] children = entries.toArray(new FSEntry[entries.size()]);
        
        traverseTreeRecursive(children, pathStack, absPathsStack, visitor, followSymbolicLinks);
    }
    
    private void traverseTreeRecursive(final FSEntry[] selection, final LinkedList<String> pathStack,
            final LinkedList<String[]> absPathsStack, final TreeVisitor visitor,
            final boolean followSymbolicLinks) {
        
        if(visitor.cancelTraversal()) {
            return;
        }

        //System.err.println("calculateForkSizeRecursive")
        String[] pathStackArray = pathStack.toArray(new String[pathStack.size()]);
        String pathStackString = Util.concatenateStrings(pathStack, "/");
        
        //System.err.print("Directory: \"");
        //System.err.print(pathStackString);
        //System.err.println("\"...");
        
        for(FSEntry curEntry : selection) {
            if(visitor.cancelTraversal()) {
                break;
            }
            
            String curEntryString = (pathStackString.length() > 0 ? pathStackString + "/" : "") +
                                curEntry.getName();
            //System.err.println("Processing \"" + curEntryString + "\"...");
            
            String[] linkTargetPath = null;
            if(followSymbolicLinks && curEntry instanceof FSLink) {
                FSLink curLink = (FSLink)curEntry;
                
                //System.err.print("  Getting link target for \"" + curEntryString + "\"...");
                String[] targetPath = fsHandler.getTargetPath(curLink, pathStackArray);
                if(targetPath != null) {
                    if(Util.contains(absPathsStack, targetPath)) {
                        String msg = "Circular symlink detected: \"" + curEntryString + "\" -> \"" +
                                curLink.getLinkTargetString() + "\"";
                        System.err.println();
                        System.err.println("traverseTreeRecursive: " + msg);
                        System.err.println();
                        visitor.traversalError(msg);
                        continue;
                    }
                        
                    FSEntry linkTarget = fsHandler.getEntry(targetPath);
                    if(linkTarget != null) {
                        //System.err.println("  Happily resolved link \"" + curLink.getLinkTargetString() + "\" to an FSEntry by the name \"" + linkTarget.getName() + "\"");
                        curEntry = linkTarget;
                        linkTargetPath = targetPath;
                    }
                    else {
                        String msg = "Could not get link target entry \"" + curLink.getLinkTargetString() + "\"";
                        System.err.println("WARNING: " + msg);
                        visitor.traversalError(msg);
                    }
                }
                else {
                    String msg = "Could not resolve link \"" + curEntryString + "\" -> \"" +
                            curLink.getLinkTargetString() + "\"";
                    System.err.println("WARNING: " + msg);
                    visitor.traversalError(msg);
                }
            }
            
            final String[] absolutePath;
            if(linkTargetPath != null)
                absolutePath = linkTargetPath;
            else {
                if(absPathsStack.size() > 0)
                    absolutePath = Util.concatenate(absPathsStack.getLast(), curEntry.getName());
                else
                    absolutePath = new String[0];
            }
            
            if(curEntry instanceof FSFile) {
                visitor.file((FSFile) curEntry);
            }
            else if(curEntry instanceof FSFolder) {
                FSFolder curFolder = (FSFolder) curEntry;
                if(absPathsStack.size() > 0)
                    pathStack.addLast(curFolder.getName());
                        
                absPathsStack.addLast(absolutePath);
                
                try {
                    if(visitor.startDirectory(pathStackArray, curFolder)) {
                        
                        traverseTreeRecursive(curFolder.listEntries(), pathStack, absPathsStack,
                                visitor, followSymbolicLinks);
                    
                        visitor.endDirectory(pathStackArray, curFolder);
                    }
                } finally {
                    absPathsStack.removeLast();
                    if(absPathsStack.size() > 0)
                        pathStack.removeLast();
                }
            }
            else if(curEntry instanceof FSLink) {
                FSLink curLink = (FSLink) curEntry;
                if(followSymbolicLinks) {
                    String msg = "Unresolved link \"" + curEntryString + "\" -> \"" +
                            curLink.getLinkTargetString() + "\"";
                    System.err.println(msg);
                    //visitor.traversalError(msg);
                }
                
                visitor.link((FSLink) curEntry);
            }
            else {
                throw new RuntimeException("Unexpected FSEntry subclass: " + curEntry.getClass());
            }
        }
    }
    
    private void actionShowAboutDialog() {
        String message = "";
        message += "HFSExplorer " + HFSExplorer.VERSION + " Build #" + BuildNumber.BUILD_NUMBER + "\n";
        message += HFSExplorer.COPYRIGHT + "\n";
        for(String notice : HFSExplorer.NOTICES) {
            message += notice + "\n";
        }
        message += "\nOperating system: " + System.getProperty("os.name") + " " + System.getProperty("os.version");
        message += "\nArchitecture: " + System.getProperty("os.arch");
        message += "\nJava Runtime Environment: " + System.getProperty("java.version");
        message += "\nVirtual machine: " + System.getProperty("java.vm.vendor") + " " +
                System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version");
        
        JOptionPane.showMessageDialog(this, message, "About", JOptionPane.INFORMATION_MESSAGE);
        
    }
    
    private void actionGetInfo(String[] parentPath, List<FSEntry> entries) {
        if(entries.size() != 1) {
            JOptionPane.showMessageDialog(this, "Get info for multiple selections not yet possible.\n" +
                    "Please select one item at a time.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        FSEntry entry = entries.get(0);
        if(entry instanceof FSFile || entry instanceof FSLink || entry instanceof FSFolder) {
            FileInfoWindow fiw = new FileInfoWindow(entry, parentPath);
            fiw.setVisible(true);
        }
        else {
            JOptionPane.showMessageDialog(this, "[actionGetInfo()] Record data has unexpected type (" +
                    entry.getClass() + ").\nReport bug to developer.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /** <code>progressDialog</code> may NOT be null. */
    protected void extract(String[] parentPath, FSEntry rec, File outDir,
            ExtractProgressMonitor progressDialog, LinkedList<String> errorMessages,
            boolean followSymbolicLinks) {
        extract(parentPath, Arrays.asList(rec), outDir, progressDialog, errorMessages,
                followSymbolicLinks, FSForkType.DATA);
    }

    /** <code>progressDialog</code> may NOT be null. */
    /*
    protected void extract(String[] parentPath, FSEntry rec, File outDir,
            ExtractProgressMonitor progressDialog, LinkedList<String> errorMessages,
            boolean dataFork, boolean resourceFork) {
        extract(parentPath, Arrays.asList(rec), outDir, progressDialog, errorMessages,
                dataFork, resourceFork);
    }
    */

    /** <code>progressDialog</code> may NOT be null. */
    /*
    protected void extract(String[] parentPath, FSEntry[] recs, File outDir,
            ExtractProgressMonitor progressDialog, LinkedList<String> errorMessages) {
        extract(parentPath, Arrays.asList(recs), outDir, progressDialog, errorMessages,
                true, false);
    }
    */

    /** <code>progressDialog</code> may NOT be null. */
    /*
    protected void extract(String[] parentPath, FSEntry[] recs, File outDir,
            ExtractProgressMonitor progressDialog, LinkedList<String> errorMessages,
            boolean dataFork, boolean resourceFork) {
        extract(parentPath, Arrays.asList(recs), outDir, progressDialog, errorMessages,
                dataFork, resourceFork);
    }
    */

    /** <code>progressDialog</code> may NOT be null. */
    /*
    protected void extract(String[] parentPath, List<FSEntry> recs, File outDir,
            ExtractProgressMonitor progressDialog, LinkedList<String> errorMessages) {
        extract(parentPath, recs, outDir, progressDialog, errorMessages,
                true, false);
    }
    */

    /** <code>progressDialog</code> may NOT be null. */
    protected void extract(String[] parentPath, List<FSEntry> recs, File outDir,
            ExtractProgressMonitor progressDialog, LinkedList<String> errorMessages,
            boolean followSymbolicLinks, List<FSForkType> forkTypes) {
        extract(parentPath, recs, outDir, progressDialog, errorMessages, followSymbolicLinks,
                forkTypes.toArray(new FSForkType[forkTypes.size()]));
    }
    
    protected void extract(String[] parentPath, List<FSEntry> recs, File outDir,
            ExtractProgressMonitor progressDialog, LinkedList<String> errorMessages,
            boolean followSymbolicLinks, FSForkType... forkTypes) {
        
        if(!outDir.exists()) {
            String[] options = new String[]{"Create directory", "Cancel"};
            int reply = JOptionPane.showOptionDialog(this, "Warning! Target directory:\n" +
                    "    \"" + outDir.getAbsolutePath() + "\"\n" +
                    "does not exist. Do you want to create this directory?",
                    "Warning", JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE, null, options, options[0]);
            if(reply != 0) {
                //++errorCount;
                errorMessages.addLast("Skipping all files in " + outDir.getAbsolutePath() +
                        " as user chose not to create directory.");
                progressDialog.signalCancel();
                return;
            }
            else {
                if(!outDir.mkdirs()) {
                    JOptionPane.showMessageDialog(this, "Could not create directory:\n    \"" +
                            outDir.getAbsolutePath() + "\"\n",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    errorMessages.addLast("Could not create directory \"" + outDir.getAbsolutePath() +
                            "\".");
                    progressDialog.signalCancel();
                    return;
                }
            }
        }
        
        /*
        LinkedList<FSForkType> forkTypes = new LinkedList<FSForkType>();
        if(dataFork)
            forkTypes.addLast(FSForkType.DATA);
        if(resourceFork)
            forkTypes.addLast(FSForkType.MACOS_RESOURCE);
         * */
        
        ExtractVisitor ev = new ExtractVisitor(progressDialog, errorMessages, outDir, forkTypes);
        traverseTree(parentPath, recs, ev, followSymbolicLinks);
        
        /*
        LinkedList<String> pathStack = new LinkedList<String>();
        if(parentPath != null) {
            for(String pathComponent : parentPath)
                pathStack.addLast(pathComponent);
        }

        if(errorMessages == null) // The user can opt out of recieving error messages, but they must still be recorded.
            errorMessages = new LinkedList<String>();
        LinkedList<String[]> absPathStack = new LinkedList<String[]>();
        for(FSEntry rec : recs) {
            extractRecursive(rec, pathStack, absPathStack, outDir, progressDialog,
                    errorMessages, new ObjectContainer<Boolean>(false), dataFork, resourceFork);
        }
        */
    }

    /*
    private void extractRecursive(FSEntry rec, LinkedList<String> pathStack,
            LinkedList<String[]> absPathsStack, File outDir, ExtractProgressMonitor progressDialog,
            LinkedList<String> errorMessages, ObjectContainer<Boolean> overwriteAll,
            boolean dataFork, boolean resourceFork) {

        if(!dataFork && !resourceFork) {
            throw new IllegalArgumentException("Neither data fork nor resource fork were selected for extraction. Won't do nothing...");
        }
        if(progressDialog.cancelSignaled()) {
            //progressDialog.confirmCancel(); // Done by caller.
            return;
        }

        //int errorCount = 0;
        
        String[] absolutePath = null;
        if(rec instanceof FSLink) {
            FSLink curLink = (FSLink) rec;
            String[] pathStackArray = pathStack.toArray(new String[pathStack.size()]);
            
            String[] targetPath = fsHandler.getTargetPath(curLink, pathStackArray);
            if(targetPath != null) {
                if(Util.contains(absPathsStack, targetPath)) {
                    System.err.println();
                    System.err.println("extractRecursive: CIRCULAR SYMLINK DETECTED!");
                    System.err.println();
                    errorMessages.addLast("Detected circular soft link \"" + curLink.getName() +
                            "\" in directory \"" + Util.concatenateStrings(pathStackArray, "/") +
                            "\"... skipping this entry.");
                    return;
                }
                
                FSEntry linkTarget = fsHandler.getEntry(targetPath);
                if(linkTarget != null) {
                    rec = linkTarget;
                    absolutePath = targetPath;
                }
                else {
                    errorMessages.addLast("Could not get entry for link target \"" +
                            Util.concatenateStrings(targetPath, "/") + "\"... skipping this entry.");
                    return;
                }
            }
            else {
                errorMessages.addLast("Could not resolve soft link \"" + curLink.getLinkTargetString() +
                        "\" from directory \"" + Util.concatenateStrings(pathStackArray, "/") +
                        "\"... skipping this entry.");
                return;
            }
            //FSEntry linkTarget = curLink.getLinkTarget(pathStackArray);
        }
        
        if(rec instanceof FSFile) {
            if(dataFork) {
                extractFile((FSFile) rec, outDir, progressDialog, errorMessages, overwriteAll, FSForkType.DATA);
            }
            if(resourceFork) {
                extractFile((FSFile) rec, outDir, progressDialog, errorMessages, overwriteAll, FSForkType.MACOS_RESOURCE);
            }
        }
        else if(rec instanceof FSFolder) {
            String curDirName = rec.getName();
            progressDialog.updateCurrentDir(curDirName);

            FSEntry[] contents = ((FSFolder) rec).listEntries();
            //System.out.println("folder: \"" + curDirName + "\" valence: " + contents.length + " range: " + fractionLowLimit + "-" + fractionHighLimit);
            // We now have the contents of the requested directory
            File thisDir = new File(outDir, curDirName);
            if(!overwriteAll.o && thisDir.exists()) {
                String[] options = new String[]{"Continue", "Cancel"};
                int reply = JOptionPane.showOptionDialog(this, "Warning! Directory:\n    \"" + thisDir.getAbsolutePath() + "\"\n" +
                        "already exists. Do you want to continue extracting to this directory?",
                        "Warning", JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE, null, options, options[0]);
                if(reply != 0) {
                    //++errorCount;
                    errorMessages.addLast("Skipping all files in \"" + thisDir.getAbsolutePath() +
                            "\" due to user interaction.");
                    progressDialog.signalCancel();
                    return;
                }
            }

            if(thisDir.mkdir() || thisDir.exists()) {
                pathStack.addLast(rec.getName());
                if(absolutePath != null)
                    absPathsStack.addLast(absolutePath);
                try {
                    System.err.println("extractRecursive: pathStack=" + Util.concatenateStrings(pathStack, "/"));
                    System.err.println("extractRecursive: absPathsStack:");
                    for(String[] cur : absPathsStack) {
                        System.err.println("                     " + Util.concatenateStrings(cur, "/"));
                    }
                    
                    for(FSEntry outRec : contents) {
                        extractRecursive(outRec, pathStack, absPathsStack, thisDir, progressDialog,
                                errorMessages, overwriteAll, dataFork, resourceFork);
                    }
                } finally {
                    if(absolutePath != null)
                        absPathsStack.removeLast();
                    pathStack.removeLast();
                }
            }
            else {
                int reply = JOptionPane.showConfirmDialog(this, "Could not create directory:\n  " +
                        thisDir.getAbsolutePath() + "\nDo you want to " +
                        "continue? (All files under this directory will be " +
                        "skipped)", "Error", JOptionPane.YES_NO_OPTION,
                        JOptionPane.ERROR_MESSAGE);
                if(reply == JOptionPane.NO_OPTION) {
                    progressDialog.signalCancel();
                }
                else
                    errorMessages.addLast("Could not create directory \"" + thisDir.getAbsolutePath() +
                            "\". All files under this directory will be skipped.");
                return;
            }
        }
// 	else
// 	    System.out.println("thread with range: " + fractionLowLimit + "-" + fractionHighLimit);
    }
    */

    private void extractFile(final FSFile rec, final File outDir, final ExtractProgressMonitor progressDialog,
            final LinkedList<String> errorMessages, final ExtractProperties extractProperties,
            final ObjectContainer<Boolean> skipDirectory, final FSForkType forkType) {
        //int errorCount = 0;
        final String originalFileName;
        
        if(forkType == FSForkType.DATA)
            originalFileName = rec.getName();
        else if(forkType == FSForkType.MACOS_RESOURCE)
            originalFileName = "._" + rec.getName(); // Special syntax for resource forks in foreign file systems
        else 
            throw new RuntimeException("Unexpected fork type: " + forkType);
        
        CreateFileFailedAction defaultCreateFileFailedAction =
                extractProperties.getCreateFileFailedAction();
        FileExistsAction defaultFileExistsAction =
                extractProperties.getFileExistsAction();
        
        String fileName = originalFileName;
        
        while(fileName != null) {
            String curFileName = fileName;
            fileName = null;
            
            //System.out.println("file: \"" + filename + "\" range: " + fractionLowLimit + "-" + fractionHighLimit);
            final FSFork theFork = rec.getForkByType(forkType);
            
            if(theFork == null)
                throw new RuntimeException("Could not find a fork of type: " + forkType);
            else if(forkType == FSForkType.MACOS_RESOURCE && theFork.getLength() == 0)
                return; // Extracting empty resource forks is really pointless.
            
            progressDialog.updateCurrentFile(curFileName, theFork.getLength());

            final File outFile = new File(outDir, curFileName);
            //progressDialog.updateTotalProgress(fractionLowLimit);

            if(defaultFileExistsAction != FileExistsAction.OVERWRITE && outFile.exists()) {
                FileExistsAction a;
                if(defaultFileExistsAction == FileExistsAction.PROMPT_USER)
                    a = progressDialog.fileExists(outFile);
                else {
                    a = defaultFileExistsAction;
                    defaultFileExistsAction = FileExistsAction.PROMPT_USER;
                }
                
                if(a == FileExistsAction.OVERWRITE) {
                }
                else if(a == FileExistsAction.OVERWRITE_ALL) {
                    extractProperties.setFileExistsAction(FileExistsAction.OVERWRITE);
                    defaultFileExistsAction = FileExistsAction.OVERWRITE;
                }
                else if(a == FileExistsAction.SKIP_FILE) {
                    errorMessages.addLast("Skipped extracting file \"" + outFile.getAbsolutePath() +
                            "\" due to user interaction.");
                    break;
                }
                else if(a == FileExistsAction.SKIP_DIRECTORY) {
                    errorMessages.addLast("Skipping entire directory \"" + outDir.getAbsolutePath() +
                            "\" due to user interaction.");
                    skipDirectory.o = true;
                    break;
                }
                else if(a == FileExistsAction.RENAME) {
                    fileName = progressDialog.displayRenamePrompt(curFileName, outDir);
                    
                    if(fileName == null)
                        fileName = curFileName;
                    continue;
                }
                else if(a == FileExistsAction.AUTO_RENAME) {
                    fileName = FileNameTools.autoRenameIllegalFilename(curFileName, outDir, false);
                    
                    if(fileName == null)
                        fileName = curFileName;
                    continue;
                }
                else if(a == FileExistsAction.CANCEL) {
                    progressDialog.signalCancel();
                    break;
                }
                else {
                    throw new RuntimeException("Internal error! Did not expect a: " + a);
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

                if(!outFile.getParentFile().equals(outDir) || !outFile.getName().equals(curFileName)) {
                    throw new FileNotFoundException();
                }
                FileOutputStream fos = new FileOutputStream(outFile);
                extractForkToStream(theFork, fos, progressDialog);
                fos.close();
                
                if(curFileName != (Object) originalFileName && !curFileName.equals(originalFileName))
                    errorMessages.addLast("File \"" + originalFileName +
                            "\" was renamed to \"" + curFileName + "\" in parent folder \"" +
                            outDir.getAbsolutePath() + "\".");
            } catch(FileNotFoundException fnfe) {
                // <Debug messages>
                System.out.println("Could not create file \"" + outFile + "\". The following exception was thrown:");
                fnfe.printStackTrace();
                char[] filenameChars = curFileName.toCharArray();
                System.out.println("Filename in hex (" + filenameChars.length + " UTF-16BE units):");
                System.out.print("  0x");
                for(char c : filenameChars) {
                    System.out.print(" " + Util.toHexStringBE(c));
                }
                System.out.println();
                // </Debug messages>
                
                // <Prompt user for action, if needed>
                CreateFileFailedAction a;
                if(defaultCreateFileFailedAction == CreateFileFailedAction.PROMPT_USER)
                    a = progressDialog.createFileFailed(curFileName, outDir);
                else {
                    a = defaultCreateFileFailedAction;
                    defaultCreateFileFailedAction = CreateFileFailedAction.PROMPT_USER;
                }
                
                if(a == CreateFileFailedAction.SKIP_FILE) {
                    errorMessages.addLast("Skipped extracting file \"" + outFile.getAbsolutePath() +
                            "\" due to user interaction.");
                    break;
                }
                else if(a == CreateFileFailedAction.SKIP_DIRECTORY) {
                    errorMessages.addLast("Skipping entire directory \"" + outDir.getAbsolutePath() +
                            "\" due to user interaction.");
                    skipDirectory.o = true;
                    break;
                }
                else if(a == CreateFileFailedAction.RENAME) {
                    fileName = progressDialog.displayRenamePrompt(curFileName, outDir);
                    
                    if(fileName == null)
                        fileName = curFileName;
                    continue;
                }
                else if(a == CreateFileFailedAction.AUTO_RENAME) {
                    fileName = FileNameTools.autoRenameIllegalFilename(curFileName, outDir, false);

                    if(fileName == null)
                        fileName = curFileName;
                    continue;
                }
                else if(a == CreateFileFailedAction.CANCEL) {
                    progressDialog.signalCancel();
                    break;
                }
                else {
                    throw new RuntimeException("Internal error! Did not expect a: " + a);
                }
                // </Prompt user for action, if needed>                
            } catch(IOException ioe) {
                System.err.println("Received I/O exception when trying to write to file \"" + outFile + "\":");
                ioe.printStackTrace();
                String msg = ioe.getMessage();
                int reply = JOptionPane.showConfirmDialog(this, "Could not write to file \"" + curFileName +
                        "\" under folder:\n  " + outDir.getAbsolutePath() +
                        (msg != null ? "\nSystem message: \"" + msg + "\"" : "") +
                        "\nDo you want to continue?",
                        "I/O Error", JOptionPane.YES_NO_OPTION,
                        JOptionPane.ERROR_MESSAGE);
                errorMessages.addLast("Could not write to file \"" + curFileName + "\"");
                if(reply == JOptionPane.NO_OPTION) {
                    progressDialog.signalCancel();
                }
            } catch(Throwable e) {
                e.printStackTrace();
                String message = "An exception occurred while extracting \"" + curFileName + "\"!";
                message += "\n  " + e.toString();
                for(StackTraceElement ste : e.getStackTrace()) {
                    message += "\n    " + ste.toString();
                }
                message += "\n\nThe file has probably not been extracted.";
                int reply = JOptionPane.showConfirmDialog(this, message +
                        "\nDo you want to continue with the extraction?",
                        "Error", JOptionPane.YES_NO_OPTION,
                        JOptionPane.ERROR_MESSAGE);
                errorMessages.addLast("An unhandled exception occurred when exctracting file \"" +
                        curFileName + "\". See debug console for more info.");
                if(reply == JOptionPane.NO_OPTION) {
                    progressDialog.signalCancel();
                }
            }
            break;
        }

        //return errorCount;
    }

    /**
     * An interface for visitors that can be used in the traverseTree method.
     */
    public interface TreeVisitor {
        /**
         * 
         * @param parentPath
         * @param folder
         * @return whether tree traversal should enter this directory or not. If the visitor returns
         * false for a directory, it will not get an endDirectory event for that directory.
         */
        public boolean startDirectory(String[] parentPath, FSFolder folder);
        public void endDirectory(String[] parentPath, FSFolder folder);
        public void file(FSFile fsf);
        public void link(FSLink fsl);
        
        /**
         * This method is called when the traversal engine encounters a non-critical error.
         * @param message
         */
        public void traversalError(String message);
        
        /**
         * Implement this to return true when the traversal process is to be aborted.
         * @return true if the visitor requests that the tree traversal be aborted.
         */
        public boolean cancelTraversal();
    }
    
    public class NullTreeVisitor implements TreeVisitor {

        @Override
        public boolean startDirectory(String[] parentPath, FSFolder folder) { return true; }
        
        @Override
        public void endDirectory(String[] parentPath, FSFolder folder) {}
        
        @Override
        public void file(FSFile fsf) {}

        @Override
        public void link(FSLink fsl) {}

        @Override
        public void traversalError(String message) {}

        @Override
        public boolean cancelTraversal() { return false; }
    }
    
    public class CalculateTreeSizeVisitor extends NullTreeVisitor {
        private final ExtractProgressMonitor pm;
        private final StringBuilder sb = new StringBuilder();
        private final FSForkType[] forkTypes;
        private long size = 0;
        //private LinkedList<String> errorMessages = new LinkedList<String>();
        
        public CalculateTreeSizeVisitor(ExtractProgressMonitor pm, List<FSForkType> forkTypes) {
            this(pm, forkTypes.toArray(new FSForkType[forkTypes.size()]));
        }
        
        public CalculateTreeSizeVisitor(ExtractProgressMonitor pm, FSForkType... forkTypes) {
            this.pm = pm;
            this.forkTypes = forkTypes;
            
            if(this.pm == null)
                throw new IllegalArgumentException("pm == null");
            if(this.forkTypes == null)
                throw new IllegalArgumentException("forkTypes == null");
            
            if(forkTypes.length == 0)
                throw new IllegalArgumentException("No fork types to extract.");
        }
        
        public long getSize() {
            return size;
        }
        
        @Override
        public boolean startDirectory(String[] parentPath, FSFolder folder) {
            sb.setLength(0);
            for(String s : parentPath)
                sb.append(s).append("/");
            sb.append(folder.getName());
            pm.updateCalculateDir(sb.toString());
            return true;
        }
        
        @Override
        public void file(FSFile file) {
            for(FSForkType forkType : forkTypes) {
                FSFork fork = file.getForkByType(forkType);
                if(fork != null)
                    size += fork.getLength();
            }
        }
        
        @Override
        public boolean cancelTraversal() { return pm.cancelSignaled(); }
    }
    
    private class ExtractVisitor extends NullTreeVisitor {
        private final ExtractProgressMonitor pm;
        private final LinkedList<String> errorMessages;
        private final File outRootDir;
        //private final ObjectContainer<Boolean> overwriteAll = new ObjectContainer<Boolean>(false);
        private final ObjectContainer<Boolean> skipDirectory = new ObjectContainer<Boolean>(false);
        private final ExtractProperties extractProperties;
        private final FSForkType[] forkTypes;
        private final LinkedList<File> outDirStack = new LinkedList<File>();
        
        public ExtractVisitor(ExtractProgressMonitor pm, LinkedList<String> errorMessages, File outDir,
                List<FSForkType> forkTypes) {
            this(pm, errorMessages, outDir, forkTypes.toArray(new FSForkType[forkTypes.size()]));
        }
        public ExtractVisitor(ExtractProgressMonitor pm, LinkedList<String> errorMessages, File outDir,
                FSForkType... forkTypes) {
            this.pm = pm;
            this.errorMessages = errorMessages;
            this.outRootDir = outDir;
            this.forkTypes = forkTypes;
            this.extractProperties = this.pm.getExtractProperties();
            
            if(this.pm == null)
                throw new IllegalArgumentException("pm == null");
            if(this.errorMessages == null)
                throw new IllegalArgumentException("errorMessages == null");
            if(this.outRootDir == null)
                throw new IllegalArgumentException("outDir == null");
            if(this.forkTypes == null)
                throw new IllegalArgumentException("forkTypes == null");
            
            if(forkTypes.length == 0)
                throw new IllegalArgumentException("No fork types to extract.");
            
            outDirStack.addLast(outDir);
        }
        
        @Override
        public boolean startDirectory(String[] parentPath, FSFolder folder) {
            //System.err.println("startDirectory(" + Util.concatenateStrings(parentPath, "/") + ", " + folder.getName());
            //if(skipDirectory.o) {
            //    System.err.println("  skipping...");
            //    return false;
            //}
                
            //System.err.println("outDirStack.getLast()=" + outDirStack.getLast());
            final File outDir = outDirStack.getLast();
            
            final CreateDirectoryFailedAction originalCreateDirectoryFailedAction =
                    extractProperties.getCreateDirectoryFailedAction();
            final DirectoryExistsAction originalDirectoryExistsAction =
                    extractProperties.getDirectoryExistsAction();
            
            CreateDirectoryFailedAction defaultCreateDirectoryFailedAction =
                    originalCreateDirectoryFailedAction;
            DirectoryExistsAction defaultDirectoryExistsAction =
                    originalDirectoryExistsAction;

            final String originalDirName = folder.getName();
            String dirName = originalDirName;
            while(dirName != null) {
                String curDirName = dirName;
                dirName = null;
                
                pm.updateCurrentDir(curDirName);
                File thisDir = new File(outDir, curDirName);
                
                if(defaultDirectoryExistsAction != DirectoryExistsAction.CONTINUE && thisDir.exists()) {
                    DirectoryExistsAction a;
                    if(defaultDirectoryExistsAction == DirectoryExistsAction.PROMPT_USER)
                        a = pm.directoryExists(thisDir);
                    else
                        a = defaultDirectoryExistsAction;
                    
                    boolean resetLoop = false;
                    switch(a) {
                        case CONTINUE:
                            break;
                        case RENAME:
                            dirName = pm.displayRenamePrompt(curDirName, outDir);
                            if(dirName == null)
                                dirName = curDirName;
                            resetLoop = true;
                            break;
                        case AUTO_RENAME:
                            dirName = FileNameTools.autoRenameIllegalFilename(curDirName, outDir, true);                            
                            if(dirName == null)
                                dirName = curDirName;
                            resetLoop = true;
                            break;
                        case SKIP_DIRECTORY:
                            resetLoop = true;
                            break;
                        case CANCEL:
                            resetLoop = true;
                            pm.signalCancel();
                            break;
                        default:
                            throw new RuntimeException("Internal error! Did not expect a: " + a);
                    }
                    if(resetLoop)
                        continue;
                }

                if(thisDir.mkdir() || thisDir.exists()) {
                    if(curDirName != (Object)originalDirName && !curDirName.equals(originalDirName))
                        errorMessages.addLast("Directory \"" + originalDirName +
                                "\" was renamed to \"" + curDirName + "\" in parent folder \"" +
                                outDir.getAbsolutePath() + "\".");
                    
                    outDirStack.addLast(thisDir);
                    return true;
                }
                else {
                    CreateDirectoryFailedAction a;
                    if(defaultCreateDirectoryFailedAction == CreateDirectoryFailedAction.PROMPT_USER)
                        a = pm.createDirectoryFailed(curDirName, outDir);
                    else {
                        a = defaultCreateDirectoryFailedAction;
                        // Only perform the default action once... or else we would have an endless loop
                        defaultCreateDirectoryFailedAction = CreateDirectoryFailedAction.PROMPT_USER;
                    }
                    
                    switch(a) {
                        case SKIP_DIRECTORY:
                            errorMessages.addLast("Could not create directory \"" + thisDir.getAbsolutePath() +
                                    "\". All files under this directory will be skipped.");
                            break;
                        case RENAME:
                            dirName = pm.displayRenamePrompt(curDirName, outDir);
                            if(dirName == null)
                                dirName = curDirName;
                            break;
                        case AUTO_RENAME:
                            dirName = FileNameTools.autoRenameIllegalFilename(curDirName, outDir, true);                            
                            if(dirName == null) {
                                dirName = curDirName;
                                /*
                                if(originalCreateDirectoryFailedAction == CreateDirectoryFailedAction.AUTO_RENAME) {
                                    // If we got here by the default action, we don't want to bother the user...
                                    errorMessages.addLast("Auto-rename failed for dir name \"" +
                                            curDirName + "\" in parent directory \"" +
                                            outDir.getAbsolutePath() +
                                            "\". All files under this directory will be skipped.");
                                    defaultCreateDirectoryFailedAction = CreateDirectoryFailedAction.SKIP_DIRECTORY;
                                }
                                */
                            }
                            break;
                        case CANCEL:
                            pm.signalCancel();
                            break;
                        default:
                            throw new RuntimeException("Internal error! Did not expect a: " + a);
                    }
                    
                }
            }
            return false;
        }
        
        @Override
        public void endDirectory(String[] parentPath, FSFolder folder) {
            outDirStack.removeLast();
            skipDirectory.o = false;
        }
        
        @Override
        public void file(FSFile fsf) {
            if(skipDirectory.o)
                return;
            
            File outDir = outDirStack.getLast();
            for(FSForkType forkType : forkTypes)
                extractFile(fsf, outDir, pm, errorMessages, extractProperties, skipDirectory, forkType);
        }

        @Override
        public void link(FSLink fsl) {
            // Create the link in OS-specific way? Need native code for that... unless we create a
            // new 'ln -s' process, but it will be slow... UNLESS we thread it out, and don't wait
            // for it to finish. OK, flooding the OS with ln processes isn't good either...
        }

        @Override
        public void traversalError(String message) {
            errorMessages.addLast(message);
        }

        @Override
        public boolean cancelTraversal() {
            return pm.cancelSignaled();
        }
    }
    
    private class FileSystemProvider implements FileSystemBrowser.FileSystemProvider<FSEntry> {
        @Override
        public void actionDoubleClickFile(List<Record<FSEntry>> recordPath) {
            if(recordPath.size() < 1)
                throw new IllegalArgumentException("Empty path to file!");

            String[] parentPath = new String[recordPath.size() - 1];
            int i = 0;
            for(Record<FSEntry> curEntry : recordPath) {
                if(i < parentPath.length)
                    parentPath[i++] = curEntry.getUserObject().getName();
                else
                    break;
            }

            Record<FSEntry> record = recordPath.get(recordPath.size()-1);
            FSEntry entry = record.getUserObject();
            if(entry instanceof FSFile) {
                FileSystemBrowserWindow.this.actionDoubleClickFile(parentPath, (FSFile) entry);
            }
            else if(entry instanceof FSLink) {
                FSLink link = (FSLink)entry;

                FSEntry linkTarget = link.getLinkTarget(parentPath);
                if(linkTarget == null)
                    JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
                            "The link you clicked is broken.", "Error", JOptionPane.ERROR_MESSAGE);
                else if(linkTarget instanceof FSFile)
                    FileSystemBrowserWindow.this.actionDoubleClickFile(parentPath, (FSFile) linkTarget);
                else
                    throw new RuntimeException("Unexpected FSEntry link target: " + entry.getClass());
            }
            else {
                throw new RuntimeException("Unexpected FSEntry type: " + entry.getClass());
            }
        }

        @Override
        public void actionExtractToDir(List<Record<FSEntry>> parentPathList, List<Record<FSEntry>> recordList) {
            String[] parentPath = getFSPath(parentPathList);
            
            List<FSEntry> fsEntryList = new ArrayList<FSEntry>(recordList.size());
            for(Record<FSEntry> rec : recordList) {
                fsEntryList.add(rec.getUserObject());
            }

            FileSystemBrowserWindow.this.actionExtractToDir(parentPath, fsEntryList, true, false);
        }

        @Override
        public void actionGetInfo(final List<Record<FSEntry>> parentPathList,
                final List<Record<FSEntry>> recordList) {
            List<FSEntry> entryList = new ArrayList<FSEntry>(recordList.size());
            for(Record<FSEntry> rec : recordList)
                entryList.add(rec.getUserObject());
            FileSystemBrowserWindow.this.actionGetInfo(getFSPath(parentPathList), entryList);
        }

        @Override
        public JPopupMenu getRightClickRecordPopupMenu(final List<Record<FSEntry>> parentPathList,
                final List<Record<FSEntry>> recordList) {
            final String[] parentPath = getFSPath(parentPathList);

            final ArrayList<FSEntry> userObjectList = new ArrayList<FSEntry>(recordList.size());
            for(Record<FSEntry> rec : recordList)
                userObjectList.add(rec.getUserObject());

            JPopupMenu jpm = new JPopupMenu();

            JMenuItem infoItem = new JMenuItem("Information");
            infoItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    FileSystemBrowserWindow.this.actionGetInfo(parentPath, userObjectList);
                }
            });
            jpm.add(infoItem);

            JMenuItem dataExtractItem = new JMenuItem("Extract data");
            dataExtractItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    FileSystemBrowserWindow.this.actionExtractToDir(parentPath, userObjectList, true, false);
                }
            });
            jpm.add(dataExtractItem);

            JMenuItem resExtractItem = new JMenuItem("Extract resource fork(s)");
            resExtractItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    FileSystemBrowserWindow.this.actionExtractToDir(parentPath, userObjectList, false, true);
                }
            });
            jpm.add(resExtractItem);

            JMenuItem bothExtractItem = new JMenuItem("Extract data and resource fork(s)");
            bothExtractItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    FileSystemBrowserWindow.this.actionExtractToDir(parentPath, userObjectList, true, true);
                }
            });
            jpm.add(bothExtractItem);

            return jpm;
        }

        @Override
        public boolean isFileSystemLoaded() {
            return fsHandler != null;
        }

        @Override
        public List<Record<FSEntry>> getFolderContents(List<Record<FSEntry>> folderRecordPath) {
            FSEntry lastEntry = folderRecordPath.get(folderRecordPath.size() - 1).getUserObject();
            
            // Resolve any links first
            if(lastEntry instanceof FSLink) {
                String[] parentPath = getFSPath(folderRecordPath, folderRecordPath.size()-1);

                FSEntry linkTarget = ((FSLink)lastEntry).getLinkTarget(parentPath);
                if(linkTarget == null) {
                    JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
                            "The link you clicked is broken.", "Error", JOptionPane.ERROR_MESSAGE);
                    throw new RuntimeException("Broken link");
                }
                else if(linkTarget instanceof FSFolder)
                    lastEntry = linkTarget;
                else
                    throw new RuntimeException("Tried to get folder contents for link target type " +
                        lastEntry.getClass());
            }
            
            // Then check for folder
            if(lastEntry instanceof FSFolder) {
                String[] folderPath = getFSPath(folderRecordPath);
                
                FSEntry[] entryArray = ((FSFolder) lastEntry).listEntries();
                ArrayList<Record<FSEntry>> entryList = new ArrayList<Record<FSEntry>>(entryArray.length);
                for(FSEntry entry : entryArray) {
                    Record<FSEntry> rec = new FSEntryRecord(entry, folderPath);
                    entryList.add(rec);
                }
                return entryList;
            }
            else {
                throw new RuntimeException("Tried to get folder contents for type " +
                        lastEntry.getClass());
            }
        }

        @Override
        public String getAddressPath(List<String> pathComponents) {
            StringBuilder sb = new StringBuilder("/");

            for(String name : pathComponents) {
                sb.append(fsHandler.generatePosixPathnameComponent(name));
                sb.append("/");
            }
            return sb.toString();
        }

        @Override
        public String[] parseAddressPath(String targetAddress) {
            if(!targetAddress.startsWith("/")) {
                return null;
            }
            else {
                String remainder = targetAddress.substring(1);
                if(remainder.length() == 0) {
                    return new String[0];
                }
                else {
                    String[] res = remainder.split("/");
                    for(int i = 0; i < res.length; ++i)
                        res[i] = fsHandler.parsePosixPathnameComponent(res[i]);
                    return res;
                }
            }
        }

        /**
         * Converts a FileSystemBrowser parent path into a FSFramework compatible raw string path.
         *
         * @param parentPathList
         * @return
         */
        private String[] getFSPath(List<Record<FSEntry>> fsbPathList) {
            if(fsbPathList == null)
                return null;
            else
                return getFSPath(fsbPathList, fsbPathList.size());
        }

        private String[] getFSPath(List<Record<FSEntry>> fsbPathList, int len) {
            if(len < 1)
                throw new IllegalArgumentException("A FileSystemBrowser parent path list must " +
                        "have at least one component (the root folder).");
            else if(fsbPathList == null)
                return null;

            String[] res = new String[len-1];
            Iterator<Record<FSEntry>> it = fsbPathList.iterator();

            it.next(); // Skip over the root entry

            for(int i = 0; i < res.length; ++i) {
                res[i] = it.next().getUserObject().getName();
            }

            while(it.hasNext())
                it.next(); // Finish the iterator
            
            return res;
        }
    }

    private static class FSEntryRecord extends Record<FSEntry> {
        public FSEntryRecord(FSEntry entry, String[] parentDirPath) {
            super(entryTypeToRecordType(entry, parentDirPath), entry.getName(),
                    getEntrySize(entry, parentDirPath), entry.getAttributes().getModifyDate(), entry);
        }

        public static RecordType entryTypeToRecordType(FSEntry entry, String[] parentDirPath) {
            if(entry instanceof FSFile) {
                return RecordType.FILE;
            }
            else if(entry instanceof FSFolder) {
                return RecordType.FOLDER;
            }
            else if(entry instanceof FSLink) {
                FSLink fsl = (FSLink)entry;
                FSEntry linkTarget = fsl.getLinkTarget(parentDirPath);
                if(linkTarget == null) {
                    return RecordType.BROKEN_LINK;
                }
                if(linkTarget instanceof FSFile) {
                    return RecordType.FILE_LINK;
                }
                else if(linkTarget instanceof FSFolder) {
                    return RecordType.FOLDER_LINK;
                }
                else
                    throw new IllegalArgumentException("Unsupported FSEntry link target: " + entry.getClass());
            }
            else {
                throw new IllegalArgumentException("Unsupported FSEntry type: " + entry.getClass());
            }
        }

        public static long getEntrySize(FSEntry entry, String[] parentDirPath) {
            if(entry instanceof FSFile) {
                return ((FSFile) entry).getMainFork().getLength();
            }
            else if(entry instanceof FSFolder) {
                return 0;
            }
            else if(entry instanceof FSLink) {
                FSLink fsl = (FSLink)entry;
                FSEntry linkTarget = fsl.getLinkTarget(parentDirPath);
                if(linkTarget == null) {
                    return 0;
                }
                else if(linkTarget instanceof FSFile) {
                    return ((FSFile)linkTarget).getMainFork().getLength();
                }
                else if(linkTarget instanceof FSFolder) {
                    return 0;
                }
                else
                    throw new IllegalArgumentException("Unsupported FSEntry link target: " + entry.getClass());
            }
            else {
                throw new IllegalArgumentException("Unsupported FSEntry type: " + entry.getClass());
            }
        }
    }

    public static void main(String[] args) {
        if(System.getProperty("os.name").toLowerCase().startsWith("mac os x"))
            System.setProperty("apple.laf.useScreenMenuBar", "true");

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
            DebugConsoleWindow dcw = new DebugConsoleWindow(System.err);
            System.setOut(new PrintStream(dcw.getDebugStream()));
            System.setErr(new PrintStream(dcw.getDebugStream()));
            fsbWindow = new FileSystemBrowserWindow(dcw);
            ++parsedArgs;
        }
        else {
            fsbWindow = new FileSystemBrowserWindow();

        /*
        System.err.println(FileSystemBrowserWindow.class.getName() + ".main invoked.");
        for(int i = 0; i < args.length; ++i)
        System.err.println("  args[" + i + "]: \"" + args[i] + "\"");
        System.err.println();
        System.err.println("java.library.path=\"" + System.getProperty("java.library.path") + "\"");
         */
        }
        fsbWindow.setVisible(true);

        if(args.length > parsedArgs) {
            String filename = args[parsedArgs];
            try {
                String pathNameTmp;
                try {
                    pathNameTmp = new File(filename).getCanonicalPath();
                } catch(Exception e) {
                    pathNameTmp = filename; // Just swallow
                }

                final String pathName = pathNameTmp;
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
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
                    for(StackTraceElement ste : ioe.getStackTrace()) {
                        msg += "\n" + ste.toString();
                    }
                    JOptionPane.showMessageDialog(fsbWindow, msg, "Exception while loading file", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
