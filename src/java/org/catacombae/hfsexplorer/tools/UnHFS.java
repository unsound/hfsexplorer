/*-
 * Copyright (C) 2007-2014 Erik Larsson
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

package org.catacombae.hfsexplorer.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.catacombae.dmg.encrypted.ReadableCEncryptedEncodingStream;
import org.catacombae.dmg.sparsebundle.ReadableSparseBundleStream;
import org.catacombae.dmg.sparseimage.ReadableSparseImageStream;
import org.catacombae.dmg.sparseimage.SparseImageRecognizer;
import org.catacombae.dmg.udif.UDIFDetector;
import org.catacombae.dmg.udif.UDIFRandomAccessStream;
import org.catacombae.hfsexplorer.ExtractProgressMonitor;
import org.catacombae.hfsexplorer.Extractor;
import org.catacombae.hfsexplorer.HFSExplorer;
import org.catacombae.storage.io.win32.ReadableWin32FileStream;
import org.catacombae.io.ReadableFileStream;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.io.RuntimeIOException;
import org.catacombae.storage.io.DataLocator;
import org.catacombae.storage.io.ReadableStreamDataLocator;
import org.catacombae.storage.io.SubDataLocator;
import org.catacombae.storage.fs.FSEntry;
import org.catacombae.storage.fs.FileSystemDetector;
import org.catacombae.storage.fs.FileSystemHandler;
import org.catacombae.storage.fs.FileSystemHandlerFactory;
import org.catacombae.storage.fs.FileSystemHandlerFactory.CustomAttribute;
import org.catacombae.storage.fs.FileSystemMajorType;
import org.catacombae.storage.ps.Partition;
import org.catacombae.storage.ps.PartitionSystemDetector;
import org.catacombae.storage.ps.PartitionSystemHandler;
import org.catacombae.storage.ps.PartitionSystemHandlerFactory;
import org.catacombae.storage.ps.PartitionSystemType;
import org.catacombae.storage.ps.PartitionType;
import org.catacombae.util.Util;

/**
 * Command line program which extracts all or part of the contents of a
 * HFS/HFS+/HFSX file system to a specified path.
 *
 * @author <a href="https://catacombae.org" target="_top">Erik Larsson</a>
 */
public class UnHFS {
    private static boolean debug = false;

    private static final int RETVAL_NEED_PASSWORD = 10;
    private static final int RETVAL_INCORRECT_PASSWORD = 11;

    /**
     * Prints program usage instructions to the PrintStream <code>ps</code>.
     *
     * @param ps the PrintStream to print usage instruction to.
     */
    private static void printUsage(PrintStream ps) {
        final char SHORT_ARG_PREFIX = '-';
        final String LONG_ARG_PREFIX = "-";

        //     80 <-------------------------------------------------------------------------------->
        ps.println("unhfs " + HFSExplorer.VERSION);
        ps.println(HFSExplorer.COPYRIGHT.replaceAll("\u00A9", "(C)"));
        for(String s : HFSExplorer.NOTICES) {
            ps.println(s.replaceAll("\u00A9", "(C)"));
        }
        ps.println();
        ps.println("usage: unhfs [options...] <input file>");
        ps.println("  Input file can be in raw, UDIF (.dmg) and/or encrypted format.");
        ps.println("  Options:");
        ps.println("    " + SHORT_ARG_PREFIX + "o <output dir>");
        ps.println("      The target directory in the local file system where all extracted files");
        ps.println("      should go.");
        ps.println("      When this option is omitted, all files go to the currect working");
        ps.println("      directory.");
        ps.println("    " + LONG_ARG_PREFIX + "fsroot <path to extract>");
        ps.println("      A POSIX path in the HFS file system that should be extracted.");
        ps.println("      Example which extracts all the contents of joe's user dir from a backup");
        ps.println("      disk image to the current directory:");
        ps.println("        unhfs -o . -fsroot /Users/joe FullBackup.dmg");
        ps.println("      When this option is omitted, all the contents of the file system is");
        ps.println("      extracted.");
        ps.println("    " + LONG_ARG_PREFIX + "create");
        ps.println("      If the -fsroot path refers to a folder, create that folder inside");
        ps.println("      the output directory, rather than extracting into the output directory");
        ps.println("      itself.");
        ps.println("    " + LONG_ARG_PREFIX + "resforks NONE|APPLEDOUBLE");
        ps.println("      Determines whether resource forks should be extracted, and in what");
        ps.println("      format. Currently only the APPLEDOUBLE format, which puts each resource");
        ps.println("      fork in its own file with the '._' prefix, is supported.");
        ps.println("      When this option is omitted, no resource forks are extracted.");
        ps.println("    " + LONG_ARG_PREFIX + "partition <partition number>");
        ps.println("      If the input file is partitioned, extracts files from the specified HFS");
        ps.println("      partition. Partitions are numbered from 0 and up.");
        ps.println("      When this options is omitted, the application chooses the first");
        ps.println("      available HFS partition.");
        ps.println("    " + LONG_ARG_PREFIX + "password <password>");
        ps.println("      Specifies the password for an encrypted image. The special marker \"-\" ");
        ps.println("      causes the password to be read from stdin.");
        ps.println("    " + LONG_ARG_PREFIX + "sfm-substitutions");
        ps.println("      Translates the filenames to a format that is more compatible with Windows");
        ps.println("      filesystems, using the translation scheme that was used by the now");
        ps.println("      defunct Services for Mac component in Windows Server.");
        ps.println("    " + LONG_ARG_PREFIX + "auto-rename");
        ps.println("      Auto-rename files with names that cannot be represented in the target");
        ps.println("      filesystem.");
        ps.println("      Combine this with '-sfm-substitutions' when extracting to a Windows");
        ps.println("      filesystem (e.g. FAT, exFAT, NTFS, ReFS, ...) to preserve as much filename");
        ps.println("      information as possible while extracting as many files as possible.");
        ps.println("    " + SHORT_ARG_PREFIX + "v");
        ps.println("      Verbose mode. Prints the POSIX path of every extracted file to stdout.");
        ps.println("    --");
        ps.println("      Signals that there are no more option arguments. Useful for accessing");
        ps.println("      input files with names identical to an option signature.");
    }

    /**
     * UnHFS entry point. The main method's only responsibility is to parse and
     * validate program arguments. It then passes them on to the static method
     * {@link #unhfs(java.io.PrintStream,
     * org.catacombae.io.ReadableRandomAccessStream, java.io.File,
     * java.lang.String, char[], boolean, boolean, int, boolean, boolean,
     * boolean)}, which contains the actual program logic.
     *
     * @param args program arguments.
     */
    public static void main(String[] args) {
        String outputDirname = ".";
        String fsRoot = "/";
        boolean extractFolderDirectly = true;
        boolean extractResourceForks = false;
        boolean verbose = false;
        boolean sfmSubstitutions = false;
        boolean autoRename = false;
        int partitionNumber = -1; // -1 means search for first supported partition
        char[] password = null;

        int i;
        for(i = 0; i < args.length; ++i) {
            String curArg = args[i];
            char firstChar = curArg.length() > 0 ? curArg.charAt(0) : '\0';
            char secondChar = curArg.length() > 1 ? curArg.charAt(1) : '\0';
            String argString;

            if(curArg.equals("--")) {
                ++i;
                break;
            }

            if((firstChar == '-' || firstChar == '/') && curArg.length() > 1 &&
                    secondChar != '-')
            {
                argString = curArg.substring(1);
            }
            else if(curArg.length() > 3 && secondChar == '-') {
                argString = curArg.substring(2);
            }
            else {
                /* Not an option argument. Supported ways of specifying options
                 * are:
                 * - The Java way, prefixed with a single '-' regardless of
                 *   whether it's long or short.
                 * - The Windows way, prefixed with a '/' regardless of whether
                 *   it's long or short.
                 * - The Unix way, with short (single character) arguments
                 *   prefixed by '-' and long (more than one character)
                 *   arguments prefixed by '--'.
                 *
                 * The special token '--' ends argument parsing and allows
                 * opening a file that has the same name or format as an option
                 * argument. E.g. to open a file named '--', specify '-- --'. */
                break;
            }

            if(argString.equals("o")) {
                if(i+1 < args.length)
                    outputDirname = args[++i];
                else {
                    printUsage(System.err);
                    System.exit(1);
                }
            }
            else if(argString.equals("fsroot")) {
                if(i+1 < args.length)
                    fsRoot = args[++i];
                else {
                    printUsage(System.err);
                    System.exit(1);
                }
            }
            else if(argString.equals("create")) {
                extractFolderDirectly = false;
            }
            else if(argString.equals("resforks")) {
                if(i+1 < args.length) {
                    String value = args[++i];
                    if(value.equalsIgnoreCase("NONE")) {
                        extractResourceForks = false;
                    }
                    else if(value.equalsIgnoreCase("APPLEDOUBLE")) {
                        extractResourceForks = true;
                    }
                    else {
                        System.err.println("Error: Invalid value \"" + value +
                                "\" for -resforks!");
                        printUsage(System.err);
                        System.exit(1);
                    }
                }
                else {
                    printUsage(System.err);
                    System.exit(1);
                }
            }
            else if(argString.equals("partition")) {
                if(i+1 < args.length) {
                    try {
                        partitionNumber = Integer.parseInt(args[++i]);
                    } catch(NumberFormatException nfe) {
                        System.err.println("Error: Invalid partition number " +
                                "\"" + args[i] + "\"!");
                        printUsage(System.err);
                        System.exit(1);
                    }
                }
                else {
                    printUsage(System.err);
                    System.exit(1);
                }
            }
            else if(argString.equals("password")) {
                if(i+1 < args.length) {
                    password = args[++i].toCharArray();

                    if(password.length == 1 && password[0] == '-') {
                        /* Read password from stdin. */
                        InputStreamReader r = new InputStreamReader(System.in);
                        char[] tmp = new char[4096];
                        int offset = 0;
                        int readLength = 0;
                        try {
                            while((readLength = r.read(tmp, offset,
                                    tmp.length - offset)) > 0)
                            {
                                System.err.println("readLength: " + readLength);

                                char[] newTmp = new char[tmp.length * 2];
                                System.arraycopy(tmp, 0, newTmp, 0, tmp.length);
                                Arrays.fill(tmp, '\0');
                                offset += readLength;
                                tmp = newTmp;
                            }
                        } catch(IOException ex) {
                            System.err.println("Got IOException while " +
                                    "reading password from stdin:");
                            ex.printStackTrace();
                        }

                        int passwordLength = offset;
                        char[] lineSeparator =
                                System.getProperty("line.separator").
                                        toCharArray();
                        boolean trailingLineSeparator = true;
                        for(int j = 0; j < lineSeparator.length; ++j) {
                            int lineSeparatorIndex =
                                    lineSeparator.length - 1 - j;
                            int tmpIndex =
                                    passwordLength - 1 - j;

                            if(tmp[tmpIndex] !=
                                    lineSeparator[lineSeparatorIndex])
                            {
                                trailingLineSeparator = false;
                                break;
                            }
                        }

                        if(trailingLineSeparator) {
                            passwordLength -= lineSeparator.length;
                        }

                        password = new char[passwordLength];
                        System.arraycopy(tmp, 0, password, 0, passwordLength);
                        Arrays.fill(tmp, '\0');
                    }
                }
                else {
                    printUsage(System.err);
                    System.exit(1);
                }
            }
            else if(argString.equals("sfm-substitutions")) {
                sfmSubstitutions = true;
            }
            else if(argString.equals("auto-rename")) {
                autoRename = true;
            }
            else if(argString.equals("v")) {
                verbose = true;
            }
            else
                break;
        }

        if(i != args.length-1) {
            printUsage(System.err);
            System.exit(1);
        }

        String inputFilename = args[i];
        File inputFile = new File(inputFilename);
        if(!inputFile.isDirectory() &&
                !(inputFile.exists() && inputFile.canRead()))
        {
            System.err.println("Error: Input file \"" + inputFilename + "\" " +
                    "can not be read!");
            printUsage(System.err);
            System.exit(1);
        }

        File outputDir = new File(outputDirname);
        if(!(outputDir.exists() && outputDir.isDirectory())) {
            System.err.println("Error: Invalid output directory \"" +
                    outputDirname + "\"!");
            printUsage(System.err);
            System.exit(1);
        }

        ReadableRandomAccessStream inputStream;
        if(inputFile.isDirectory()) {
            inputStream = new ReadableSparseBundleStream(inputFile);
        }
        else if(ReadableWin32FileStream.isSystemSupported())
            inputStream = new ReadableWin32FileStream(inputFilename);
        else
            inputStream = new ReadableFileStream(inputFilename);

        try {
            unhfs(
                    /* PrintStream outputStream */
                    System.out,
                    /* ReadableRandomAccessStream inFileStream */
                    inputStream,
                    /* File outputDir */
                    outputDir,
                    /* String fsRoot */
                    fsRoot,
                    /* char[] password */
                    password,
                    /* boolean extractFolderDirectly */
                    extractFolderDirectly,
                    /* boolean extractResourceForks */
                    extractResourceForks,
                    /* int partitionNumber */
                    partitionNumber,
                    /* boolean verbose */
                    verbose,
                    /* boolean sfmSubstitutions */
                    sfmSubstitutions,
                    /* boolean autoRename */
                    autoRename);
            System.exit(0);
        } catch(RuntimeIOException e) {
            System.err.println("Exception while executing main routine:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static class ProgressMonitor implements ExtractProgressMonitor {
        private final ExtractProperties extractProperties =
                new ExtractProperties();
        private final File targetDir;
        private final boolean verbose;
        private final boolean autoRename;

        private String currentDir = null;

        public ProgressMonitor(File targetDir, boolean verbose,
                boolean autoRename)
        {
            this.targetDir = targetDir;
            this.verbose = verbose;
            this.autoRename = autoRename;
        }

        public void updateCalculateDir(
                final String dirname)
        {
        }

        public void updateTotalProgress(
                final double fraction,
                final String message)
        {
        }

        public void updateCurrentDir(
                final String dirname)
        {
            if(verbose) {
                currentDir = dirname;
                System.out.println(targetDir.getPath() + "/" +
                        (currentDir != null ? currentDir + "/" : "") + dirname);
            }
        }

        public void updateCurrentFile(
                final String filename,
                final long fileSize)
        {
            if(verbose) {
                System.out.println(targetDir.getPath() + "/" +
                        (currentDir != null ? currentDir + "/" : "") +
                        filename);
            }
        }

        public void setDataSize(
                final long totalSize)
        {
        }

        public boolean confirmCreateDirectory(
                final File dir)
        {
            return true;
        }

        public CreateDirectoryFailedAction createDirectoryFailed(
                final String dirname,
                final File parentDirectory)
        {
            System.err.println("Failed to create directory " +
                    parentDirectory.getPath() + "/" + dirname + ".");
            return autoRename ? CreateDirectoryFailedAction.AUTO_RENAME :
                CreateDirectoryFailedAction.SKIP_DIRECTORY;
        }

        public CreateFileFailedAction createFileFailed(
                final String filename,
                final File parentDirectory)
        {
            System.err.println("Failed to create directory " +
                    parentDirectory.getPath() + "/" + filename + ".");
            return autoRename ? CreateFileFailedAction.AUTO_RENAME :
                CreateFileFailedAction.SKIP_FILE;
        }

        public DirectoryExistsAction directoryExists(
                final File directory)
        {
            logDebug("Directory \"" + directory.getPath() + "\" " +
                    "already exists. Continuing anyway...");
            return autoRename ? DirectoryExistsAction.AUTO_RENAME :
                DirectoryExistsAction.CONTINUE;
        }

        public FileExistsAction fileExists(
                final File file)
        {
            logDebug("File \"" + file.getPath() + "\" already " +
                    "exists. Overwriting...");
            return autoRename ? FileExistsAction.AUTO_RENAME :
                FileExistsAction.OVERWRITE;
        }

        public UnhandledExceptionAction unhandledException(
                final String filename,
                final Throwable t,
                final String actionDescription)
        {
            Throwable cause = null;
            Throwable realThrowable =
                    (t instanceof InvocationTargetException &&
                    (cause = t.getCause()) != null) ? cause : t;

            System.err.println("Error while " + actionDescription + ": " +
                    realThrowable.toString());
            //realThrowable.printStackTrace();
            return UnhandledExceptionAction.HANDLED;
        }

        public void errorMessage(
                final String message)
        {
            System.err.println("Error: " + message);
        }

        public String displayRenamePrompt(
                final String currentName,
                final File outDir)
        {
            /* unhfs operates based on command line switches only. */
            return null;
        }

        public boolean displayIoErrorPrompt(
                final String fileName,
                final File outDir,
                final IOException ioe)
        {
            System.err.println("Got I/O error when extracting file " +
                    "\"" + fileName + "\" to directory " +
                    "\"" + outDir.getPath() + "\": " + ioe.getMessage());
            return true;
        }

        public ExtractProperties getExtractProperties() {
            return extractProperties;
        }

        public void signalCancel() {
        }

        public boolean cancelSignaled() {
            return false;
        }

        public void confirmCancel() {
        }

        public void addDataProgress(
                final long dataSize)
        {
            /* There's no progress bar in UnHFS at this point. */
        }
    }

    /**
     * The main routine in the program, which gets invoked after arguments
     * parsing is complete. The routine expects all arguments to be fully parsed
     * and valid.
     *
     * @param outputStream
     *      the PrintStream where all the messages will go (should normally be
     *      {@link System#out}).
     * @param inFileStream
     *      the random access stream that the file system data will be read
     *      from.
     * @param outputDir
     *      the directory where the specified files and directories will be
     *      extracted to.
     * @param fsRoot
     *      the file or directory tree to extract, specified as a POSIX path.
     * @param password
     *      the password used to unlock an encrypted image, if any (may be
     *      <code>NULL</code>).
     * @param extractFolderDirectly
     *      if fsRoot is a folder, don't create the folder but extract its
     *      contents to <code>outputDir</code>.
     * @param extractResourceForks
     *      extract resource forks and other extended attributes to the target
     *      filesystem.
     * @param partitionNumber
     *      the number of the partition to load from <code>inFileStream</code>
     *      (0 means use the whole device).
     * @param verbose
     *      print the name of every extracted file and directory.
     * @param sfmSubstitutions
     *      map characters incompatible with Windows filesystems to the Unicode
     *      private range used by Services for Mac.
     * @param autoRename
     *      automatically rename files and directories that cannot be created on
     *      the target filesystem.
     * @throws org.catacombae.io.RuntimeIOException
     */
    public static void unhfs(
            final PrintStream outputStream,
            final ReadableRandomAccessStream inFileStream,
            final File outputDir,
            final String fsRoot,
            final char[] password,
            final boolean extractFolderDirectly,
            final boolean extractResourceForks,
            final int partitionNumber,
            final boolean verbose,
            final boolean sfmSubstitutions,
            final boolean autoRename) throws RuntimeIOException
    {
        ReadableRandomAccessStream stream = inFileStream;

        // First detect any outer layers of UDIF and/or encryption.
        logDebug("Trying to detect encrypted structure...");
        if(ReadableCEncryptedEncodingStream.isCEncryptedEncoding(stream)) {
            if(password != null) {
                try {
                    stream = new ReadableCEncryptedEncodingStream(
                            /* ReadableRandomAccessStream backingStream */
                            stream,
                            /* char[] password */
                            password);
                } catch(Exception e) {
                    // TODO: Differentiate between exceptions...
                    System.err.println("Incorrect password for encrypted image.");
                    System.exit(RETVAL_INCORRECT_PASSWORD);
                }
            }
            else {
                System.err.println("Image is encrypted, and no password was specified.");
                System.exit(RETVAL_NEED_PASSWORD);
            }
        }

        logDebug("Trying to detect sparseimage structure...");
        if(SparseImageRecognizer.isSparseImage(stream)) {
            try {
                stream = new ReadableSparseImageStream(
                        /* ReadableRandomAccessStream backingStream */
                        stream);
            } catch(Exception e) {
                System.err.println("Exception while creating readable " +
                        "sparseimage stream:");
                e.printStackTrace();
                System.exit(1);
            }
        }

        logDebug("Trying to detect UDIF structure...");
        if(UDIFDetector.isUDIFEncoded(stream)) {
            try {
                stream = new UDIFRandomAccessStream(
                        /* ReadableRandomAccessStream stream */
                        stream);
            } catch(Exception e) {
                e.printStackTrace();
                System.err.println("Unhandled exception while trying to load UDIF wrapper.");
                System.exit(1);
            }
        }

        DataLocator inputDataLocator = new ReadableStreamDataLocator(stream);

        PartitionSystemType[] psTypes =
                PartitionSystemDetector.detectPartitionSystem(inputDataLocator,
                false);
        if(psTypes.length >= 1) {

            outer:
            for(PartitionSystemType chosenType : psTypes) {

                PartitionSystemHandlerFactory fact = chosenType.createDefaultHandlerFactory();
                PartitionSystemHandler psHandler =
                        fact.createHandler(inputDataLocator);

                if(psHandler.getPartitionCount() > 0) {
                    Partition[] partitionsToProbe;
                    if(partitionNumber >= 0) {
                        if(partitionNumber < psHandler.getPartitionCount()) {
                            partitionsToProbe = new Partition[] { psHandler.getPartition(partitionNumber) };
                        }
                        else {
                            break;
                        }
                    }
                    else if(partitionNumber == -1) {
                        partitionsToProbe = psHandler.getPartitions();
                    }
                    else {
                        System.err.println("Invalid partition number: " + partitionNumber);
                        System.exit(1);
                        return;
                    }

                    for(Partition p : partitionsToProbe) {
                        if(p.getType() == PartitionType.APPLE_HFS_CONTAINER) {
                            // DataLocator subDataLocator =
                            //         new SubDataLocator(inputDataLocator, p.getStartOffset(), p.getLength());
                            // ContainerHandlerFactory chFact =
                            //         p.getType().getAssociatedContainerType().createDefaultHandlerFactory();
                            // ContainerHandler ch = chFact.createHandler(subDataLocator);
                            // if(ch.containsFileSystem()) {
                            //     FileSystemMajorType fsType = ch.detectFileSystemType();
                            //     switch(fsType) {
                            //         case APPLE_HFS:
                            //         case APPLE_HFS_PLUS:
                            //         case APPLE_HFSX:
                            //             inputDataLocator = subDataLocator;
                            //             break outer;
                            //         default:
                            //     }
                            // }
                            inputDataLocator =
                                    new SubDataLocator(inputDataLocator, p.getStartOffset(), p.getLength());
                            break outer;
                        }
                        else if(p.getType() == PartitionType.APPLE_HFSX) {
                            inputDataLocator =
                                    new SubDataLocator(inputDataLocator, p.getStartOffset(), p.getLength());
                            break outer;
                        }
                    }
                }
            }
        }


        FileSystemMajorType[] fsTypes = FileSystemDetector.detectFileSystem(inputDataLocator);

        FileSystemHandlerFactory fact = null;
        outer:
        for(FileSystemMajorType type : fsTypes) {
            switch(type) {
                case APPLE_HFS:
                case APPLE_HFS_PLUS:
                case APPLE_HFSX:
                    fact = type.createDefaultHandlerFactory();
                    break outer;
                default:
            }
        }

        if(fact == null) {
            System.err.println("No HFS file system found.");
            System.exit(1);
        }

        CustomAttribute posixFilenamesAttribute =
                fact.getCustomAttribute("POSIX_FILENAMES");
        if(posixFilenamesAttribute == null) {
            System.err.println("Unexpected: HFS-ish file system handler does " +
                    "not support POSIX_FILENAMES attribute.");
            System.exit(1);
            return;
        }

        fact.getCreateAttributes().setBooleanAttribute(posixFilenamesAttribute,
                true);

        CustomAttribute sfmSubstitutionsAttribute =
                fact.getCustomAttribute("SFM_SUBSTITUTIONS");
        if(sfmSubstitutionsAttribute == null) {
            System.err.println("Unexpected: HFS-ish file system handler does " +
                    "not support SFM_SUBSTITUTIONS attribute.");
            System.exit(1);
            return;
        }

        fact.getCreateAttributes().setBooleanAttribute(
                sfmSubstitutionsAttribute, sfmSubstitutions);

        FileSystemHandler fsHandler = fact.createHandler(inputDataLocator);

        logDebug("Getting entry by posix path: \"" + fsRoot + "\"");
        FSEntry entry = fsHandler.getEntryByPosixPath(fsRoot);

        String[] path = fsHandler.getTruePathFromPosixPath(fsRoot);
        List<FSEntry> entries;
        if(path == null || path.length == 0 || extractFolderDirectly) {
            entries = Arrays.asList(fsHandler.getRoot().listEntries());
        }
        else {
            entries = Arrays.asList(entry);
        }

        Extractor.extract(
                /* FileSystemHandler fsHandler */
                fsHandler,
                /* String[] parentPath */
                (path.length > 0) ?
                        Util.arrayCopy(path, 0, new String[path.length - 1], 0,
                        path.length - 1) : path,
                /* List<FSEntry> rec */
                entries,
                /* File outDir */
                outputDir,
                /* ExtractProgressMonitor progressMonitor */
                new ProgressMonitor(outputDir, verbose, autoRename),
                /* LinkedList<String> errorMessages */
                new LinkedList<String>(),
                /* boolean followSymbolicLinks */
                false,
                /* boolean extractMainFork */
                true,
                /* boolean extractAdditionalForks */
                extractResourceForks);
    }

    private static void logDebug(String s) {
        if(debug)
            System.err.println("DEBUG: " + s);
    }
}
