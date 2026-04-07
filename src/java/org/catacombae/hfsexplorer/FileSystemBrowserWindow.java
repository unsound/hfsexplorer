/*-
 * Copyright (C) 2006-2021 Erik Larsson
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
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.InvalidKeyException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import net.iharder.dnd.FileDrop;
import org.catacombae.dmg.encrypted.ReadableCEncryptedEncodingStream;
import org.catacombae.dmg.sparsebundle.ReadableSparseBundleStream;
import org.catacombae.dmg.sparseimage.ReadableSparseImageStream;
import org.catacombae.dmg.sparseimage.SparseImageRecognizer;
import org.catacombae.dmg.udif.UDIFDetector;
import org.catacombae.dmg.udif.UDIFRandomAccessStream;
import org.catacombae.dmgextractor.ui.PasswordDialog;
import org.catacombae.hfs.original.StringCodec.StringCodecException;
import org.catacombae.hfs.types.hfscommon.CommonHFSVolumeHeader;
import org.catacombae.hfs.types.hfsplus.HFSPlusVolumeHeader;
import org.catacombae.hfsexplorer.FileSystemBrowser.Record;
import org.catacombae.hfsexplorer.FileSystemBrowser.RecordType;
import org.catacombae.hfsexplorer.gui.ErrorSummaryPanel;
import org.catacombae.hfsexplorer.gui.FileOperationsPanel;
import org.catacombae.hfsexplorer.gui.HFSExplorerJFrame;
import org.catacombae.hfsexplorer.gui.MemoryStatisticsPanel;
import org.catacombae.hfsexplorer.helpbrowser.HelpBrowserPanel;
import org.catacombae.io.ReadableConcatenatedStream;
import org.catacombae.io.ReadableFileStream;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.io.ReadableRandomAccessSubstream;
import org.catacombae.io.SynchronizedReadableRandomAccessStream;
import org.catacombae.storage.fs.FSEntry;
import org.catacombae.storage.fs.FSFile;
import org.catacombae.storage.fs.FSFolder;
import org.catacombae.storage.fs.FSFork;
import org.catacombae.storage.fs.FSForkType;
import org.catacombae.storage.fs.FSLink;
import org.catacombae.storage.fs.FileSystemHandlerFactory;
import org.catacombae.storage.fs.FileSystemHandlerFactory.CustomAttribute;
import org.catacombae.storage.fs.FileSystemHandlerFactory.StandardAttribute;
import org.catacombae.storage.fs.FileSystemMajorType;
import org.catacombae.storage.fs.hfs.HFSFileSystemHandler;
import org.catacombae.storage.fs.hfscommon.HFSCommonFileSystemHandler;
import org.catacombae.storage.fs.hfscommon.HFSCommonFileSystemRecognizer;
import org.catacombae.storage.fs.hfscommon.HFSCommonFileSystemRecognizer.FileSystemType;
import org.catacombae.storage.io.DataLocator;
import org.catacombae.storage.io.ReadableStreamDataLocator;
import org.catacombae.storage.io.win32.ReadableWin32FileStream;
import org.catacombae.storage.ps.Partition;
import org.catacombae.storage.ps.PartitionSystemDetector;
import org.catacombae.storage.ps.PartitionSystemHandler;
import org.catacombae.storage.ps.PartitionSystemHandlerFactory;
import org.catacombae.storage.ps.PartitionSystemType;
import org.catacombae.storage.ps.PartitionType;
import org.catacombae.util.Log;

/**
 * The main window for the graphical part of HFSExplorer. This class contains a lot of
 * non-presentation code and is very large. Should be restructured in the future.
 *
 * @author <a href="https://catacombae.org" target="_top">Erik Larsson</a>
 */
public class FileSystemBrowserWindow extends HFSExplorerJFrame {
    private static final String TITLE_STRING = "HFSExplorer " + HFSExplorer.VERSION;
    private static final String[] VERSION_INFO_DICTIONARY = {
        "http://www.catacombae.org/hfsexplorer/version.sdic.txt",
        "http://catacombae.sourceforge.net/hfsexplorer/version.sdic.txt",
    };
    /**
     * The command line argument that makes HFSExplorer print stdout and
     * stderr to a special debug console.
     */
    private static final String DEBUG_CONSOLE_ARG = "-dbgconsole";

    private static final Log log =
            Log.getInstance(FileSystemBrowserWindow.class);

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

        fsb = new FileSystemBrowser<FSEntry>(new FileSystemProvider());

        //final Class objectClass = new Object().getClass();
        setUpMenus();

        if(MacUtil.isMacOSX()) {
            MacUtil.registerMacApplicationHandler(new MacUtil.MacApplicationHandler() {
                /* @Override */
                public boolean acceptQuit() {
                    exitApplication();
                    return false;
                }

                /* @Override */
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

        // Register handler for file drag&drop events
        new FileDrop(this, new FileDrop.Listener() {

            public void filesDropped(java.io.File[] files) {
                if(files.length > 0) {
                    loadFSWithUDIFAutodetect(files[0].getAbsolutePath());
                }
            }
        });

        fsb.registerHFSEncodingChangedListener(new ActionListener() {
            /* @Override */
            public void actionPerformed(ActionEvent ae) {
                if(!(fsHandler instanceof HFSFileSystemHandler)) {
                    return;
                }

                String newEncoding = fsb.getSelectedHFSEncoding();
                String oldEncoding;
                log.debug("Setting encoding: " + newEncoding);
                oldEncoding = ((HFSFileSystemHandler) fsHandler).getEncoding();
                try {
                    ((HFSFileSystemHandler) fsHandler).setEncoding(newEncoding);
                    populateFilesystemGUI(fsHandler.getRoot());
                    fsb.saveSelectedHFSEncoding();
                } catch(Exception e) {
                    GUIUtil.displayExceptionDialog(e, 20,
                            FileSystemBrowserWindow.this,
                            "Exception while changing HFS string encoding:");
                    ((HFSFileSystemHandler) fsHandler).setEncoding(oldEncoding);
                    populateFilesystemGUI(fsHandler.getRoot());
                }
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
        if(SelectDeviceDialog.isSystemSupported()) {
            // Only for Windows systems...
            loadFSFromDeviceItem = new JMenuItem("Load file system from device...");
            loadFSFromDeviceItem.addActionListener(new ActionListener() {
                /* @Override */
                public void actionPerformed(ActionEvent ae) {
                    SelectDeviceDialog deviceDialog =
                            SelectDeviceDialog.createSelectDeviceDialog(
                            FileSystemBrowserWindow.this,
                            true,
                            "Load file system from device");
                    deviceDialog.setVisible(true);
                    ReadableRandomAccessStream io = deviceDialog.getPartitionStream();
                    String pathName = deviceDialog.getPathName();
                    if(io != null) {
                        SynchronizedReadableRandomAccessStream syncStream =
                                new SynchronizedReadableRandomAccessStream(io);
                        try {
                            loadFS(syncStream, pathName);
                        } catch(Exception e) {
                            System.err.print("INFO: Non-critical exception when trying to load file system from \"" + pathName + "\": ");
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
                                    "Could not find any file systems on device!",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        } finally {
                            syncStream.close();
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
            /* @Override */
            public void actionPerformed(ActionEvent ae) {
                //JFileChooser fileChooser = new JFileChooser();
                fileChooser.setMultiSelectionEnabled(false);
                fileChooser.setFileSelectionMode(
                        JFileChooser.FILES_AND_DIRECTORIES);

                SimpleFileFilter dmgFilter = new SimpleFileFilter();
                dmgFilter.addExtension("dmg");
                dmgFilter.setDescription("Mac OS X disk images (*.dmg)");
                fileChooser.addChoosableFileFilter(dmgFilter);

                SimpleFileFilter cdrFilter = new SimpleFileFilter();
                cdrFilter.addExtension("iso");
                cdrFilter.addExtension("cdr");
                cdrFilter.setDescription("CD/DVD image (*.iso,*.cdr)");
                fileChooser.addChoosableFileFilter(cdrFilter);

                FileFilter sparseBundleFilter = new FileFilter() {

                    @Override
                    public boolean accept(File file) {
                        if(file.isDirectory() &&
                                file.getName().endsWith(".sparsebundle")) {
                            return true;
                        }
                        else
                            return false;
                    }

                    @Override
                    public String getDescription() {
                        return "Mac OS X sparse bundles (*.sparsebundle)";
                    }

                };
                fileChooser.addChoosableFileFilter(sparseBundleFilter);

                SimpleFileFilter sparseimageFilter = new SimpleFileFilter();
                sparseimageFilter.addExtension("sparseimage");
                sparseimageFilter.setDescription("Mac OS X sparse images " +
                        "(*.sparseimage)");
                fileChooser.addChoosableFileFilter(sparseimageFilter);

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

        JMenuItem loadFromPathItem = new JMenuItem("Load file system from path...");
        loadFromPathItem.addActionListener(new ActionListener() {
            /* @Override */
            public void actionPerformed(ActionEvent ae) {
                String path = JOptionPane.showInputDialog(FileSystemBrowserWindow.this,
                        "Pathname to load:", "Load file system from path", JOptionPane.QUESTION_MESSAGE);

                if(path != null) {
                    try {
                        loadFSWithUDIFAutodetect(path);
                    } catch(Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
                                "Could not read contents of partition!",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        loadFromPathItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
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
                /* @Override */
                public void actionPerformed(ActionEvent ae) {
                    dcw.setVisible(true);
                }
            });

        }

        JMenuItem exitProgramItem = null;
        if(!System.getProperty("os.name").toLowerCase().startsWith("mac os x")) {
            exitProgramItem = new JMenuItem("Exit");
            exitProgramItem.addActionListener(new ActionListener() {
                /* @Override */
                public void actionPerformed(ActionEvent ae) {
                    exitApplication();
                }
            });
            exitProgramItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        }

        JMenuItem volumeInfoItem = new JMenuItem("Volume information");
        volumeInfoItem.addActionListener(new ActionListener() {
            /* @Override */
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
        volumeInfoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        toggleCachingItem = new JCheckBoxMenuItem("Use file system caching");
        toggleCachingItem.setState(true);
        toggleCachingItem.addActionListener(new ActionListener() {
            /* @Override */
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
            /* @Override */
            public void actionPerformed(ActionEvent ae) {
                MemoryStatisticsPanel.createMemoryStatisticsWindow().setVisible(true);
            }
        });

        JMenuItem createDiskImageItem = new JMenuItem("Create disk image...");
        createDiskImageItem.addActionListener(new ActionListener() {
            /* @Override */
            public void actionPerformed(ActionEvent ae) {
                if(ensureFileSystemLoaded()) {
                    actionExtractToDiskImage();
                }
            }


        });


        JMenuItem startHelpBrowserItem = new JMenuItem("Help browser");
        startHelpBrowserItem.addActionListener(new ActionListener() {
            /* @Override */
            public void actionPerformed(ActionEvent ae) {
                try {
                    URL url = new URL(FileSystemBrowserWindow.class.
                            getProtectionDomain().getCodeSource().getLocation(),
                            "../doc/html/index.html");
                    HelpBrowserPanel.showHelpBrowserWindow("HFSExplorer help " +
                            "browser", url);
                } catch(MalformedURLException ex) {
                    ex.printStackTrace();
                    Logger.getLogger(FileSystemBrowserWindow.class.getName()).
                            log(Level.WARNING, null, ex);
                }
            }
        });

        JMenuItem checkUpdatesItem = new JMenuItem("Check for updates...");
        checkUpdatesItem.addActionListener(new ActionListener() {
            /* @Override */
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
                            final String message =
                                    "There are updates available!\n" +
                                    "Latest version is: " + dictVersion +
                                    (dictVersion.equals(HFSExplorer.VERSION) ?
                                    " (build number #" + dictBuildNumber + ")" :
                                    "");
                            if(Java6Util.isJava6OrHigher() &&
                                Java6Util.canBrowse())
                            {
                                final String[] options =
                                        new String[] { "Download...", "Close" };
                                int selection = JOptionPane.showOptionDialog(
                                        /* Component parentComponent */
                                        FileSystemBrowserWindow.this,
                                        /* Object message */
                                        message,
                                        /* String title */
                                        "Information",
                                        /* int optionType */
                                        JOptionPane.OK_CANCEL_OPTION,
                                        /* int messageType */
                                        JOptionPane.INFORMATION_MESSAGE,
                                        /* Icon icon */
                                        null,
                                        /* Object[] options */
                                        options,
                                        /* Object initialValue */
                                        options[0]);
                                if(selection == 0) {
                                    Java6Util.browse(
                                            new URI(HFSExplorer.WEB_SITE_URL));
                                }
                            }
                            else {
                                JOptionPane.showMessageDialog(
                                        /* Component parentComponent */
                                        FileSystemBrowserWindow.this,
                                        /* Object message */
                                        message,
                                        /* String title */
                                        "Information",
                                        /* int messageType */
                                        JOptionPane.INFORMATION_MESSAGE);
                            }
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
                /* @Override */
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
        fileMenu.add(loadFromPathItem);
        //fileMenu.add(openFromPosItem);
        if(debugConsoleItem != null) {
            fileMenu.add(debugConsoleItem);
        }
        if(exitProgramItem != null) {
            fileMenu.add(exitProgramItem);
        }
        infoMenu.add(volumeInfoItem);
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
        boolean doExit = true;

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
                    doExit = false;
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
            if(doExit) {
                /*
                 * The debug console can be created without its window being
                 * displayed. Since it has never been displayed it's not in a
                 * "disposed" state. So unless we dispose of it here the
                 * application may not quit.
                 */
                if(dcw != null) {
                    dcw.dispose();
                }

                dispose();
            }
        }
    }

    public void loadFSWithUDIFAutodetect(String filename) {
        loadFSWithUDIFAutodetect(filename, 0);
    }

    public void loadFSWithUDIFAutodetect(String filename, long pos) {
        ReadableRandomAccessStream fsFile;
        try {
            File f = new File(filename);
            if(f.isDirectory()) {
                fsFile = new ReadableSparseBundleStream(f);
            }
            else if(ReadableWin32FileStream.isSystemSupported()) {
                fsFile = new ReadableWin32FileStream(filename);
            }
            else {
                fsFile = new ReadableFileStream(filename);
            }

            String displayName;
            try {
                displayName = f.getCanonicalFile().getName();
            } catch(Exception e) {
                displayName = filename;
            }

            loadFSWithUDIFAutodetect(displayName, fsFile, pos);
        } catch(Exception e) {
            System.err.println("Could not open file! Exception thrown:");
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Could not open file:\n" +
                    "    \"" + filename + "\"",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadFSWithUDIFAutodetect(String displayName,
            ReadableRandomAccessStream fsFile, long pos)
    {
        {
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

                                try {
                                    stream.read(new byte[512]);

                                    fsFile = stream;
                                    break;
                                } catch(Exception e) {
                                    Throwable cause = e.getCause();

                                    if(!(cause instanceof InvalidKeyException))
                                    {
                                        throw e;
                                    }

                                    JOptionPane.showMessageDialog(null,
                                            "If you were trying to load an " +
                                            "AES-256 encrypted image and\n" +
                                            "are using Sun/Oracle's Java " +
                                            "Runtime Environment, then \n" +
                                            "please check if you have " +
                                            "installed the Java " +
                                            "Cryptography\n" +
                                            "Extension (JCE) Unlimited " +
                                            "Strength Jurisdiction Policy\n" +
                                            "Files, which are required for " +
                                            "AES-256 support in Java.",
                                            "Unsupported AES key size",
                                            JOptionPane.ERROR_MESSAGE);
                                    break;
                                }
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
                System.err.println("Detecting sparseimage structure...");
                if(SparseImageRecognizer.isSparseImage(fsFile)) {
                    System.err.println("sparseimage structure found! Creating " +
                            "filter stream...");

                    try {
                        ReadableSparseImageStream stream =
                                new ReadableSparseImageStream(fsFile);
                        fsFile = stream;
                    } catch(Exception e) {
                        System.err.println("Exception while creating readable " +
                                "sparseimage stream:");
                        e.printStackTrace();
                    }
                }
            } catch(Exception e) {
                System.err.println("[INFO] Non-critical exception while " +
                        "trying to detect sparseimage structure:");
                e.printStackTrace();
            }

            try {
                System.err.println("Trying to detect UDIF structure...");
                if(UDIFDetector.isUDIFEncoded(fsFile)) {
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

            SynchronizedReadableRandomAccessStream syncStream =
                    new SynchronizedReadableRandomAccessStream(fsFile);
            try {
                loadFS(syncStream, displayName);
            } finally {
                syncStream.close();
            }
        }
    }

    public void loadFS(String filename) {
        ReadableRandomAccessStream fsFile;
        if(ReadableWin32FileStream.isSystemSupported()) {
            fsFile = new ReadableWin32FileStream(filename);
        }
        else {
            fsFile = new ReadableFileStream(filename);
        }

        SynchronizedReadableRandomAccessStream syncStream =
                new SynchronizedReadableRandomAccessStream(fsFile);
        try {
            loadFS(syncStream, new File(filename).getName());
        } finally {
            syncStream.close();
        }
    }

    public void loadFS(SynchronizedReadableRandomAccessStream fsFile,
            String displayName)
    {
        long fsOffset;
        long fsLength;

        // Detect partition system
        PartitionSystemType[] matchingTypes =
                PartitionSystemDetector.detectPartitionSystem(fsFile, false);

        if(matchingTypes.length > 1) {
            String message = "Multiple partition system types detected:";
            for(PartitionSystemType type : matchingTypes)
                message += "\n" + type;
            JOptionPane.showMessageDialog(this, message,
                    "Partition system detection error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        else if(matchingTypes.length == 1) {
            PartitionSystemType psType = matchingTypes[0];
            PartitionSystemHandlerFactory psFact =
                    psType.createDefaultHandlerFactory();

            if(psFact == null) {
                JOptionPane.showMessageDialog(this, "Can't find handler for " +
                        "partition system type " + psType,
                        "Unsupported partition system",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            PartitionSystemHandler psHandler =
                    psFact.createHandler(new ReadableStreamDataLocator(
                    new ReadableRandomAccessSubstream(fsFile)));

            Partition[] partitions;
            try {
                partitions = psHandler.getPartitions();
            } finally {
                psHandler.close();
            }

            if(partitions.length == 0) {
                // Proceed to detect file system
                fsOffset = 0;
                try {
                    fsLength = fsFile.length();
                } catch(Exception e) {
                    e.printStackTrace();
                    fsLength = -1;
                }
            }
            else {
                /* Search for suitable partitions, and make the first one that
                 * was found the default value for the dialog box. */
                int defaultSelection = 0;
                for(int i = 0; i < partitions.length; ++i) {
                    Partition p = partitions[i];
                    PartitionType pt = p.getType();
                    if(pt == PartitionType.APPLE_HFS_CONTAINER ||
                            pt == PartitionType.APPLE_HFSX) {
                        defaultSelection = i;
                        break;
                    }
                }

                /* Prompt user to choose a partition to load. */
                Partition selectedPartition = null;
                while(true) {
                    Object selectedValue = JOptionPane.showInputDialog(this,
                            "Select which partition to load",
                            "Choose " + psType.getLongName() + " partition",
                            JOptionPane.QUESTION_MESSAGE,
                            null, partitions, partitions[defaultSelection]);

                    if(selectedValue == null)
                        return; // Abort the load process.
                    else if(selectedValue instanceof Partition) {
                        selectedPartition = (Partition) selectedValue;
                        PartitionType pt = selectedPartition.getType();

                        if(pt == PartitionType.APPLE_HFS_CONTAINER ||
                                pt == PartitionType.APPLE_HFSX) {
                            break;
                        }
                        else {
                            JOptionPane.showMessageDialog(this,
                                    "Can't find handler for partition type \"" +
                                    selectedPartition.getType() + "\"",
                                    "Unknown partition type",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    else
                        throw new RuntimeException("selectedValue not " +
                                "instanceof Partition! selectedValue: " +
                                selectedValue.getClass());
                }

                /* A selection was made. */
                fsOffset = selectedPartition.getStartOffset();
                fsLength = selectedPartition.getLength();

                //System.err.println("DEBUG Selected partition:");
                //selectedPartition.print(System.err, "  ");
            }
        }
        else {
            fsOffset = 0;
            try {
                fsLength = fsFile.length();
            } catch(Exception e) {
                e.printStackTrace();
                fsLength = -1;
            }
        }

        // Detect HFS file system
        FileSystemType fsType = HFSCommonFileSystemRecognizer.detectFileSystem(
                fsFile, fsOffset);

        switch(fsType) {
            case HFS:
            case HFS_WRAPPED_HFS_PLUS:
            case HFS_PLUS:
            case HFSX:

                // Reset browser
                fsb.setRoot(null);
                if(fsHandler != null) {
                    fsHandler.close();
                    fsHandler = null;
                }
                if(fsDataLocator != null) {
                    fsDataLocator.close();
                    fsDataLocator = null;
                }

                final FileSystemMajorType fsMajorType;
                switch(fsType) {
                    case HFS:
                        fsMajorType = FileSystemMajorType.APPLE_HFS;
                        break;
                    case HFS_PLUS:
                    case HFS_WRAPPED_HFS_PLUS:
                        fsMajorType = FileSystemMajorType.APPLE_HFS_PLUS;
                        break;
                    case HFSX:
                        fsMajorType = FileSystemMajorType.APPLE_HFSX;
                        break;
                    default:
                        throw new RuntimeException("Unhandled type: " + fsType);
                }

                FileSystemHandlerFactory factory =
                        fsMajorType.createDefaultHandlerFactory();
                if(factory.isSupported(StandardAttribute.CACHING_ENABLED)) {
                    factory.getCreateAttributes().setBooleanAttribute(
                            StandardAttribute.CACHING_ENABLED,
                            toggleCachingItem.getState());
                }

                CustomAttribute hfsStringEncodingAttribute =
                        factory.getCustomAttribute("HFS_STRING_ENCODING");
                if(hfsStringEncodingAttribute != null) {
                    factory.getCreateAttributes().setStringAttribute(
                            hfsStringEncodingAttribute,
                            fsb.getSelectedHFSEncoding());
                    fsb.setHFSFieldsVisible(true);
                }
                else {
                    fsb.setHFSFieldsVisible(false);
                }

                //System.err.println("loadFS(): fsFile=" + fsFile);
                //System.err.println("loadFS(): Creating ReadableConcatenatedStream...");

                ReadableRandomAccessStream stage1;
                if(fsLength > 0)
                    stage1 = new ReadableConcatenatedStream(
                            new ReadableRandomAccessSubstream(fsFile), fsOffset,
                            fsLength);
                else if(fsOffset == 0)
                    stage1 = new ReadableRandomAccessSubstream(fsFile);
                else
                    throw new RuntimeException("length undefined and offset " +
                            "!= 0 (fsLength=" + fsLength + " fsOffset=" +
                            fsOffset + ")");

                //System.err.println("loadFS(): Creating ReadableStreamDataLocator...");
                this.fsDataLocator = new ReadableStreamDataLocator(stage1);
                //System.err.println("loadFS(): Creating fsHandler...");

                fsHandler = (HFSCommonFileSystemHandler)
                        factory.createHandler(fsDataLocator);

                setTitle(TITLE_STRING + " - [" + displayName + "]");

                try {
                    FSFolder rootRecord = fsHandler.getRoot();
                    populateFilesystemGUI(rootRecord);
                } catch(StringCodecException e) {
                    JOptionPane.showMessageDialog(this,
                            "Invalid string encoding for HFS volume: " +
                            fsb.getSelectedHFSEncoding(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                } catch(Exception e) {
                    GUIUtil.displayExceptionDialog(e, 1, this,
                            "An error occurred while listing the root " +
                            "directory:");
                }

                //adjustTableWidth();
                break;

            default:
                JOptionPane.showMessageDialog(this, "Invalid HFS type.\n" +
                        "HFSExplorer supports:\n" +
                        "    " + FileSystemType.HFS_PLUS + "\n" +
                        "    " + FileSystemType.HFSX + "\n" +
                        "    " + FileSystemType.HFS_WRAPPED_HFS_PLUS + "\n" +
                        "    " + FileSystemType.HFS + "\n" +
                        "\nDetected type is (" + fsType + ").",
                        "Unsupported file system type",
                        JOptionPane.ERROR_MESSAGE);
                break;
        }
    }

    private void populateFilesystemGUI(FSFolder rootFolder) {
        FileSystemBrowser.Record<FSEntry> rootRecord =
                new FileSystemBrowser.Record<FSEntry>(
                FileSystemBrowser.RecordType.FOLDER,
                rootFolder.getName(),
                0,
                rootFolder.getAttributes().getModifyDate(),
                false,
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
                /* @Override */
                public void actionPerformed(ActionEvent ae) {
                    File tempDir = new File(System.getProperty("java.io.tmpdir"));
                    LinkedList<String> errorMessages = new LinkedList<String>();
                    Extractor.extract(
                            /* FileSystemHandler fsHandler */
                            fsHandler,
                            /* String[] parentPath */
                            parentPath,
                            /* FSEntry rec */
                            rec,
                            /* File outDir */
                            tempDir,
                            /* ExtractProgressMonitor progressMonitor */
                            new SimpleGUIProgressMonitor(fopFrame),
                            /* LinkedList<String> errorMessages */
                            errorMessages,
                            /* boolean followSymbolicLinks */
                            true);
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
                            e.printStackTrace();

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
        else if(Java6Util.isJava6OrHigher() && Java6Util.canOpen()) {
            //System.err.println("Java 1.6 detected.");
            alOpen = new ActionListener() {
                /* @Override */
                public void actionPerformed(ActionEvent ae) {
                    File tempDir = new File(System.getProperty("java.io.tmpdir"));
                    LinkedList<String> errorMessages = new LinkedList<String>();
                    Extractor.extract(
                            /* FileSystemHandler fsHandler */
                            fsHandler,
                            /* String[] parentPath */
                            parentPath,
                            /* FSEntry rec */
                            rec,
                            /* File outDir */
                            tempDir,
                            /* ExtractProgressMonitor progressMonitor */
                            new SimpleGUIProgressMonitor(fopFrame),
                            /* LinkedList<String> errorMessages */
                            errorMessages,
                            /* boolean followSymbolicLinks */
                            true);
                    if(errorMessages.size() == 0) {
                        File extractedFile = new File(tempDir, rec.getName());
                        tempFiles.add(new File(tempDir, rec.getName()));
                        try {
                            Java6Util.openFile(extractedFile);
                            fopFrame.dispose();
                        } catch(IOException e) {
                            JOptionPane.showMessageDialog(FileSystemBrowserWindow.this,
                                    "Could not find a handler to open the file with.\n" +
                                    "The file remains in\n" +
                                    "    \"" + tempDir + "\"\n" +
                                    "until you exit the program.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        } catch(Exception e) {
                            e.printStackTrace();

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
                /* @Override */
                public void actionPerformed(ActionEvent ae) {
                    File tempDir = new File(System.getProperty("java.io.tmpdir"));
                    LinkedList<String> errorMessages = new LinkedList<String>();
                    Extractor.extract(
                            /* FileSystemHandler fsHandler */
                            fsHandler,
                            /* String[] parentPath */
                            parentPath,
                            /* FSEntry rec */
                            rec,
                            /* File outDir */
                            tempDir,
                            /* ExtractProgressMonitor progressMonitor */
                            new SimpleGUIProgressMonitor(fopFrame),
                            /* LinkedList<String> errorMessages */
                            errorMessages,
                            /* boolean followSymbolicLinks */
                            true);
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
                            e.printStackTrace();

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
            /* @Override */
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
                /* @Override */
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
            final boolean dataFork, final boolean additionalForks)
    {
        if(!dataFork && !additionalForks) {
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
                        /* @Override */
                        public void run() {
                            // Caching is now turned on or off manually, either for the entire device/file, or not at all.
                            //fsView.retainCatalogFile(); // Cache the catalog file to speed up operations
                            //fsView.enableFileSystemCache();
                            try {
                                LinkedList<String> dirStack = new LinkedList<String>();
                                long dataSize;

                                if(parentPath != null) {
                                    //System.err.println("parentPath: " + Util.concatenateStrings(parentPath, "/"));
                                    for(String pathComponent : parentPath)
                                        dirStack.addLast(pathComponent);
                                }

                                boolean couldHaveSymlinks = false;
                                for(FSEntry e : selection) {
                                    if(!(e instanceof FSFile)) {
                                        couldHaveSymlinks = true;
                                    }
                                }

                                int res =
                                        !couldHaveSymlinks ? JOptionPane.NO_OPTION :
                                        JOptionPane.showConfirmDialog(progress,
                                        "Do you want to follow symbolic links while extracting?\n" +
                                        "Following symbolic links means that the extracted tree will " +
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

                                dataSize = Extractor.calculateForkSizeRecursive(
                                        /* FileSystemHandler fsHandler */
                                        fsHandler,
                                        /* String[] parentPath */
                                        parentPath,
                                        /* List<FSEntry> selection */
                                        selection,
                                        /* ExtractProgressMonitor progress */
                                        progress,
                                        /* boolean calculateDataForkSize */
                                        dataFork,
                                        /* boolean
                                         *     calculateAdditionalForksSize */
                                        additionalForks,
                                        /* boolean followSymlinks */
                                        followSymlinks);
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
                                    Extractor.extract(
                                            /* FileSystemHandler fsHandler */
                                            fsHandler,
                                            /* String[] parentPath */
                                            parentPath,
                                            /* List<FSEntry> recs */
                                            selection,
                                            /* File outDir */
                                            outDir,
                                            /* ExtractProgressMonitor
                                             *     progressMonitor */
                                            progress,
                                            /* LinkedList<String>
                                             *     errorMessages */
                                            errorMessages,
                                            /* boolean followSymbolicLinks */
                                            followSymlinks,
                                            /* boolean extractMainFork */
                                            dataFork,
                                            /* boolean extractAdditionalForks */
                                            additionalForks);
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

    private void actionOpenInHFSExplorer(final List<FSEntry> selection)
    {
        for(FSEntry e : selection) {
            ReadableRandomAccessStream stream;
            FileSystemBrowserWindow fsbWindow;

            if(e instanceof FSFolder) {
                stream = new ReadableSparseBundleStream(
                        new FSEntryFileAccessor(e));
            }
            else {
                FSFork dataFork = e.getForkByType(FSForkType.DATA);
                if(dataFork == null || dataFork.getLength() <= 0) {
                    log.debug("Ignoring file in selection without data.");
                    continue;
                }

                stream = dataFork.getReadableRandomAccessStream();
            }

            fsbWindow = openFileSystemBrowserWindow(
                    /* boolean debugConsole */
                    dcw != null);

            fsbWindow.loadFSWithUDIFAutodetect(
                    /* String displayName */
                    e.getName(),
                    /* ReadableRandomAccessStream fsFile */
                    stream,
                    /* long pos */
                    0);
        }
    }

    private void actionShowAboutDialog() {
        String message = "";
        message += "HFSExplorer " + HFSExplorer.VERSION + "\n";
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

    private class FileSystemProvider implements FileSystemBrowser.FileSystemProvider<FSEntry> {
        /* @Override */
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

        /* @Override */
        public void actionExtractToDir(List<Record<FSEntry>> parentPathList, List<Record<FSEntry>> recordList) {
            String[] parentPath = getFSPath(parentPathList);

            List<FSEntry> fsEntryList = new ArrayList<FSEntry>(recordList.size());
            for(Record<FSEntry> rec : recordList) {
                fsEntryList.add(rec.getUserObject());
            }

            FileSystemBrowserWindow.this.actionExtractToDir(parentPath, fsEntryList, true, false);
        }

        /* @Override */
        public void actionGetInfo(final List<Record<FSEntry>> parentPathList,
                final List<Record<FSEntry>> recordList) {
            List<FSEntry> entryList = new ArrayList<FSEntry>(recordList.size());
            for(Record<FSEntry> rec : recordList)
                entryList.add(rec.getUserObject());
            FileSystemBrowserWindow.this.actionGetInfo(getFSPath(parentPathList), entryList);
        }

        /* @Override */
        public JPopupMenu getRightClickRecordPopupMenu(final List<Record<FSEntry>> parentPathList,
                final List<Record<FSEntry>> recordList) {
            final String[] parentPath = getFSPath(parentPathList);

            final ArrayList<FSEntry> userObjectList = new ArrayList<FSEntry>(recordList.size());
            for(Record<FSEntry> rec : recordList)
                userObjectList.add(rec.getUserObject());

            JPopupMenu jpm = new JPopupMenu();

            JMenuItem infoItem = new JMenuItem("Information");
            infoItem.addActionListener(new ActionListener() {
                /* @Override */
                public void actionPerformed(ActionEvent e) {
                    FileSystemBrowserWindow.this.actionGetInfo(parentPath, userObjectList);
                }
            });
            jpm.add(infoItem);

            JMenuItem dataExtractItem = new JMenuItem("Extract data");
            dataExtractItem.addActionListener(new ActionListener() {
                /* @Override */
                public void actionPerformed(ActionEvent e) {
                    FileSystemBrowserWindow.this.actionExtractToDir(parentPath, userObjectList, true, false);
                }
            });
            jpm.add(dataExtractItem);

            JMenuItem xattrExtractItem =
                    new JMenuItem("Extract extended attributes");
            xattrExtractItem.addActionListener(new ActionListener() {
                /* @Override */
                public void actionPerformed(ActionEvent e) {
                    FileSystemBrowserWindow.this.actionExtractToDir(parentPath, userObjectList, false, true);
                }
            });
            jpm.add(xattrExtractItem);

            JMenuItem bothExtractItem = new
                    JMenuItem("Extract data and extended attributes");
            bothExtractItem.addActionListener(new ActionListener() {
                /* @Override */
                public void actionPerformed(ActionEvent e) {
                    FileSystemBrowserWindow.this.actionExtractToDir(parentPath, userObjectList, true, true);
                }
            });
            jpm.add(bothExtractItem);

            JMenuItem openInHFSExplorerItem = new
                    JMenuItem("Open in HFSExplorer");
            openInHFSExplorerItem.addActionListener(new ActionListener() {
                /* @Override */
                public void actionPerformed(ActionEvent e) {
                    FileSystemBrowserWindow.this.actionOpenInHFSExplorer(
                            /* List<FSEntry> selection */
                            userObjectList);
                }
            });
            jpm.add(openInHFSExplorerItem);

            return jpm;
        }

        /* @Override */
        public boolean isFileSystemLoaded() {
            return fsHandler != null;
        }

        /* @Override */
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

        /* @Override */
        public String getAddressPath(List<String> pathComponents) {
            StringBuilder sb = new StringBuilder("/");

            for(String name : pathComponents) {
                sb.append(fsHandler.generatePosixPathnameComponent(name));
                sb.append("/");
            }
            return sb.toString();
        }

        /* @Override */
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
         * Converts a FileSystemBrowser parent path into a FSFramework
         * compatible raw string path.
         *
         * @param fsbPathList A list of {@link Record records}.
         * @return An array with all the pathname string elements of
         *     <code>fsbPathList</code>.
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
                    getEntrySize(entry, parentDirPath),
                    entry.getAttributes().getModifyDate(), entry.isCompressed(),
                    entry);
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

    private static FileSystemBrowserWindow openFileSystemBrowserWindow(
            boolean debugConsole)
    {
        final FileSystemBrowserWindow fsbWindow;

        if(debugConsole) {
            DebugConsoleWindow dcw = new DebugConsoleWindow(System.err);
            System.setOut(new PrintStream(dcw.getDebugStream()));
            System.setErr(new PrintStream(dcw.getDebugStream()));
            fsbWindow = new FileSystemBrowserWindow(dcw);
        }
        else {
            fsbWindow = new FileSystemBrowserWindow();
        }

        fsbWindow.setVisible(true);

        return fsbWindow;
    }

    public static void main(String[] args) {
        if(System.getProperty("os.name").toLowerCase().startsWith("mac os x"))
            System.setProperty("apple.laf.useScreenMenuBar", "true");

        /*
         * Description of look&feels:
         *   http://java.sun.com/docs/books/tutorial/uiswing/misc/plaf.html
         */
        String lookAndFeelClassName =
                javax.swing.UIManager.getSystemLookAndFeelClassName();
        System.out.println("System L&F class name: " + lookAndFeelClassName);
        if(lookAndFeelClassName.equals(
                "javax.swing.plaf.metal.MetalLookAndFeel") ||
                lookAndFeelClassName.equals(
                "com.sun.java.swing.plaf.motif.MotifLookAndFeel"))
        {
            /* Metal and Motif are really quite terrible L&Fs. Try the GTK+ L&F
             * instead. */
            try {
                System.err.println("Attempting to force GTK+ L&F...");
                javax.swing.UIManager.setLookAndFeel(
                        "com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
                lookAndFeelClassName = null;
            } catch(Exception e) {
                /* Non-critical. */
            }
        }

        if(lookAndFeelClassName != null) {
            try {
                javax.swing.UIManager.setLookAndFeel(lookAndFeelClassName);
            } catch(Exception e) {
                /* Non-critical. */
            }
        }

        int parsedArgs = 0;

        boolean debugConsole = false;
        if(args.length > 0 && args[0].equals(DEBUG_CONSOLE_ARG)) {
            debugConsole = true;
            ++parsedArgs;
        }

        final FileSystemBrowserWindow fsbWindow = openFileSystemBrowserWindow(
                /* boolean debugConsole */
                debugConsole);

        if(false) {
            System.err.println(FileSystemBrowserWindow.class.getName() +
                    ".main invoked.");
            for(int i = 0; i < args.length; ++i) {
                System.err.println("  args[" + i + "]: \"" + args[i] + "\"");
            }
            System.err.println();
            System.err.println("java.library.path=\"" +
                    System.getProperty("java.library.path") + "\"");
        }

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
                    /* @Override */
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
