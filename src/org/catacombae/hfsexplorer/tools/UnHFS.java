/*-
 * Copyright (C) 2007-2008 Erik Larsson
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import org.catacombae.dmgextractor.encodings.encrypted.ReadableCEncryptedEncodingStream;
import org.catacombae.hfsexplorer.IOUtil;
import org.catacombae.hfsexplorer.UDIFRecognizer;
import org.catacombae.hfsexplorer.fs.AppleSingleBuilder;
import org.catacombae.hfsexplorer.fs.AppleSingleBuilder.AppleSingleVersion;
import org.catacombae.hfsexplorer.fs.AppleSingleBuilder.FileSystem;
import org.catacombae.hfsexplorer.fs.AppleSingleBuilder.FileType;
import org.catacombae.hfsexplorer.win32.WindowsLowLevelIO;
import org.catacombae.io.ReadableFileStream;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.io.RuntimeIOException;
import org.catacombae.jparted.lib.DataLocator;
import org.catacombae.jparted.lib.ReadableStreamDataLocator;
import org.catacombae.jparted.lib.SubDataLocator;
import org.catacombae.jparted.lib.fs.FSEntry;
import org.catacombae.jparted.lib.fs.FSFile;
import org.catacombae.jparted.lib.fs.FSFolder;
import org.catacombae.jparted.lib.fs.FSFork;
import org.catacombae.jparted.lib.fs.FSForkType;
import org.catacombae.jparted.lib.fs.FSLink;
import org.catacombae.jparted.lib.fs.FileSystemDetector;
import org.catacombae.jparted.lib.fs.FileSystemHandler;
import org.catacombae.jparted.lib.fs.FileSystemHandlerFactory;
import org.catacombae.jparted.lib.fs.FileSystemMajorType;
import org.catacombae.jparted.lib.ps.Partition;
import org.catacombae.jparted.lib.ps.PartitionSystemDetector;
import org.catacombae.jparted.lib.ps.PartitionSystemHandler;
import org.catacombae.jparted.lib.ps.PartitionSystemHandlerFactory;
import org.catacombae.jparted.lib.ps.PartitionSystemType;
import org.catacombae.jparted.lib.ps.PartitionType;
import org.catacombae.udif.UDIFRandomAccessStream;
//import org.catacombae.jparted.lib.ps.container.ContainerHandler;
//import org.catacombae.jparted.lib.ps.container.ContainerHandlerFactory;

/**
 * Command line program which extracts all or part of the contents of a
 * HFS/HFS+/HFSX file system to a specified path.
 *
 * @author Erik Larsson
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
        //     80 <-------------------------------------------------------------------------------->
        ps.println("usage: unhfs [options...] <input file>");
        ps.println("  Input file can be in raw, UDIF (.dmg) and/or encrypted format.");
        ps.println("  Options:");
        ps.println("    -o <output dir>");
        ps.println("      The target directory in the local file system where all extracted files");
        ps.println("      should go.");
        ps.println("      When this option is omitted, all files go to the currect working");
        ps.println("      directory.");
        ps.println("    -fsroot <path to extract>");
        ps.println("      A POSIX path in the HFS file system that should be extracted.");
        ps.println("      Example which extracts all the contents of joe's user dir from a backup");
        ps.println("      disk image to the current directory:");
        ps.println("        unhfs -o . -fsroot /Users/joe FullBackup.dmg");
        ps.println("      When this option is omitted, all the contents of the file system is");
        ps.println("      extracted.");
        ps.println("    -create");
        ps.println("      If the -fsroot path refers to a folder, create that folder inside");
        ps.println("      the output directory, rather than extracting into the output directory");
        ps.println("      itself.");
        ps.println("    -resforks NONE|APPLEDOUBLE");
        ps.println("      Determines whether resource forks should be extracted, and in what");
        ps.println("      format. Currently only the APPLEDOUBLE format, which puts each resource");
        ps.println("      fork in its own file with the '._' prefix, is supported.");
        ps.println("      When this option is omitted, no resource forks are extracted.");
        ps.println("    -partition <partition number>");
        ps.println("      If the input file is partitioned, extracts files from the specified HFS");
        ps.println("      partition. Partitions are numbered from 0 and up.");
        ps.println("      When this options is omitted, the application chooses the first");
        ps.println("      available HFS partition.");
        ps.println("    -password <password>");
        ps.println("      Specifies the password for an encrypted image.");
        ps.println("    -v");
        ps.println("      Verbose mode. Prints the POSIX path of every extracted file to stdout.");
        ps.println("    --");
        ps.println("      Signals that there are no more option arguments. Useful for accessing");
        ps.println("      input files with names identical to an option signature.");
    }

    /**
     * UnHFS entry point. The main method's only responsibility is to parse and
     * validate program arguments. It then passes them on to the static method
     * unhfs(...), which contains the actual program logic.
     *
     * @param args program arguments.
     */
    public static void main(String[] args) {
        String outputDirname = ".";
        String fsRoot = "/";
        boolean extractFolderDirectly = true;
        boolean extractResourceForks = false;
        boolean verbose = false;
        int partitionNumber = -1; // -1 means search for first supported partition
        char[] password = null;

        int i;
        for(i = 0; i < args.length; ++i) {
            String curArg = args[i];

            if(curArg.equals("-o")) {
                if(i+1 < args.length)
                    outputDirname = args[++i];
                else {
                    printUsage(System.err);
                    System.exit(1);
                }
            }
            else if(curArg.equals("-fsroot")) {
                if(i+1 < args.length)
                    fsRoot = args[++i];
                else {
                    printUsage(System.err);
                    System.exit(1);
                }
            }
            else if(curArg.equals("-create")) {
            	extractFolderDirectly = false;
            }
            else if(curArg.equals("-resforks")) {
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
            else if(curArg.equals("-partition")) {
                if(i+1 < args.length) {
                    try {
                        partitionNumber = Integer.parseInt(args[++i]);
                    } catch(NumberFormatException nfe) {
                        System.err.println("Error: Invalid partition number \"" +
                                args[i] + "\"!");
                        printUsage(System.err);
                        System.exit(1);
                    }
                }
                else {
                    printUsage(System.err);
                    System.exit(1);
                }
            }
            else if(curArg.equals("-password")) {
                if(i+1 < args.length) {
                    password = args[++i].toCharArray();
                }
                else {
                    printUsage(System.err);
                    System.exit(1);
                }
            }
            else if(curArg.equals("-v")) {
                verbose = true;
            }
            else if(curArg.equals("--")) {
                ++i;
                break;
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
        if(!(inputFile.exists() && inputFile.isFile() && inputFile.canRead())) {
            System.err.println("Error: Input file \"" + inputFilename + "\" can not be read!");
            printUsage(System.err);
            System.exit(1);
        }

        File outputDir = new File(outputDirname);
        if(!(outputDir.exists() && outputDir.isDirectory())) {
            System.err.println("Error: Invalid output directory \"" + outputDirname + "\"!");
            printUsage(System.err);
            System.exit(1);
        }

        ReadableRandomAccessStream inputStream;
        if(WindowsLowLevelIO.isSystemSupported())
            inputStream = new WindowsLowLevelIO(inputFilename);
        else
            inputStream = new ReadableFileStream(inputFilename);

        try {
            unhfs(System.out, inputStream, outputDir, fsRoot, password,
                    extractFolderDirectly, extractResourceForks, partitionNumber, verbose);
            System.exit(0);
        } catch(RuntimeIOException e) {
            System.err.println("Exception while executing main routine:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * The main routine in the program, which gets invoked after arguments
     * parsing is complete. The routine expects all arguments to be fully parsed
     * and valid.
     *
     * @param outputStream the PrintStream where all the messages will go
     * (should normally be System.out).
     * @param inFileStream the stream containing the file system data.
     * @param outputDir
     * @param fsRoot
     * @param password the password used to unlock an encrypted image.
     * @param extractFolderDirectly if fsRoot is a folder, extract directly into outputDir?
     * @param extractResourceForks
     * @param partitionNumber
     * @param verbose
     * @throws org.catacombae.io.RuntimeIOException
     */
    public static void unhfs(PrintStream outputStream,
            ReadableRandomAccessStream inFileStream, File outputDir,
            String fsRoot, char[] password, boolean extractFolderDirectly,
            boolean extractResourceForks, int partitionNumber, boolean verbose)
    		throws RuntimeIOException {

        // First detect any outer layers of UDIF and/or encryption.
        logDebug("Trying to detect encrypted structure...");
        if(ReadableCEncryptedEncodingStream.isCEncryptedEncoding(inFileStream)) {
            if(password != null) {
                try {
                    ReadableCEncryptedEncodingStream stream =
                            new ReadableCEncryptedEncodingStream(inFileStream, password);
                    inFileStream = stream;
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

        logDebug("Trying to detect UDIF structure...");
        if(UDIFRecognizer.isUDIF(inFileStream)) {
            UDIFRandomAccessStream stream = null;
            try {
                stream = new UDIFRandomAccessStream(inFileStream);
                inFileStream = stream;
            } catch(Exception e) {
                e.printStackTrace();
                System.err.println("Unhandled exception while trying to load UDIF wrapper.");
                System.exit(1);
            }
        }
        
        DataLocator inputDataLocator = new ReadableStreamDataLocator(inFileStream);

        PartitionSystemType[] psTypes =
                PartitionSystemDetector.detectPartitionSystem(inputDataLocator);
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
                        else
                            break outer;
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

        FileSystemHandler fsHandler = fact.createHandler(inputDataLocator);

        logDebug("Getting entry by posix path: \"" + fsRoot + "\"");
        FSEntry entry = fsHandler.getEntryByPosixPath(fsRoot);
        if(entry instanceof FSFolder) {
            FSFolder folder = (FSFolder)entry;
			File dirForFolder;
            String folderName = folder.getName();
			if(extractFolderDirectly || folderName.equals("/") || folderName.length() == 0) {
    			dirForFolder = outputDir;
            }
            else {
            	dirForFolder = getFileForFolder(outputDir, folder, verbose);
            }
			if(dirForFolder != null) {
				extractFolder(folder, dirForFolder, extractResourceForks, verbose);
			}
        }
        else if(entry instanceof FSFile) {
        	FSFile file = (FSFile)entry;
        	extractFile(file, outputDir, extractResourceForks, verbose);
        }
        else {
            System.err.println("Requested path is not a folder or a file!");
            System.exit(1);
        }
    }

    private static void extractFolder(FSFolder folder, File targetDir,
            boolean extractResourceForks, boolean verbose) {
    	boolean wasEmpty = targetDir.list().length == 0;
        for(FSEntry e : folder.listEntries()) {
            if(e instanceof FSFile) {
                FSFile file = (FSFile)e;
				extractFile(file, targetDir, extractResourceForks, verbose);
            }
            else if(e instanceof FSFolder) {
                FSFolder subFolder = (FSFolder)e;
                File subFolderFile = getFileForFolder(targetDir, subFolder, verbose);
                if(subFolderFile != null) {
                    extractFolder(subFolder, subFolderFile, extractResourceForks, verbose);
                }
            }
            else if(e instanceof FSLink) {
                // We don't currently handle links.
            }
        }
        if(wasEmpty) {
			long lastModified = folder.getAttributes().getModifyDate().getTime();
			targetDir.setLastModified(lastModified);
        }
    }

	private static void extractFile(FSFile file, File targetDir,
			boolean extractResourceForks, boolean verbose)
			throws RuntimeIOException {
		long lastModified = file.getAttributes().getModifyDate().getTime();
		File dataFile = new File(targetDir, scrub(file.getName()));
		if(!extractRawForkToFile(file.getMainFork(), dataFile)) {
		    System.err.println("Failed to extract data " +
		            "fork to " + dataFile.getPath());
		}
		else if(verbose) {
		    System.out.println(dataFile.getPath());
		}
		dataFile.setLastModified(lastModified);
		if(extractResourceForks) {
		    FSFork resourceFork = file.getForkByType(FSForkType.MACOS_RESOURCE);

		    if(resourceFork.getLength() > 0) {
		        File resFile = new File(targetDir, "._" + scrub(file.getName()));
		        if(!extractResourceForkToAppleDoubleFile(resourceFork, resFile)) {
		            System.err.println("Failed to extract resource " +
		                    "fork to " + resFile.getPath());
		        }
		        else if(verbose) {
		            System.out.println(resFile.getPath());
		        }
		        resFile.setLastModified(lastModified);
		    }
		}
	}

	private static File getFileForFolder(File targetDir, FSFolder folder,
			boolean verbose) {
		File folderFile = new File(targetDir, scrub(folder.getName()));
		if(folderFile.isDirectory() || folderFile.mkdir()) {
		    if(verbose)
		        System.out.println(folderFile.getPath());
		}
		else {
		    System.err.println("Failed to create directory " +
		            folderFile.getPath());
		    folderFile = null;
		}
		return folderFile;
	}

    private static boolean extractRawForkToFile(FSFork fork, File targetFile) throws RuntimeIOException {
        FileOutputStream os = null;
        ReadableRandomAccessStream in = null;

        try {
            os = new FileOutputStream(targetFile);
            
            in = fork.getReadableRandomAccessStream();
            
            long extractedBytes = IOUtil.streamCopy(in, os, 128*1024);
            if(extractedBytes != fork.getLength()) {
                System.err.println("WARNING: Did not extract intended number of bytes to \"" +
                        targetFile.getPath() + "\"! Intended: " + fork.getLength() +
                        " Extracted: " + extractedBytes);
            }
            
            return true;
        } catch(FileNotFoundException fnfe) {
            return false;
        } catch(Exception ioe) {
            return false;
            //throw new RuntimeIOException(ioe);
        } finally {
            if(os != null) {
                try { os.close(); }
                catch(Exception e) {}
            }
            if(in != null) {
                try { in.close(); }
                catch(Exception e) {}
            }
        }
    }

    private static boolean extractResourceForkToAppleDoubleFile(FSFork resourceFork, File targetFile) {
        FileOutputStream os = null;
        ReadableRandomAccessStream in = null;
        try {
            AppleSingleBuilder builder = new AppleSingleBuilder(FileType.APPLEDOUBLE,
                    AppleSingleVersion.VERSION_2_0, FileSystem.MACOS_X);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            in = resourceFork.getReadableRandomAccessStream();
            long extractedBytes = IOUtil.streamCopy(in, baos, 128*1024);
            if(extractedBytes != resourceFork.getLength()) {
                System.err.println("WARNING: Did not extract intended number of bytes to \"" +
                        targetFile.getPath() + "\"! Intended: " + resourceFork.getLength() +
                        " Extracted: " + extractedBytes);
            }

            builder.addResourceFork(baos.toByteArray());

            os = new FileOutputStream(targetFile);
            os.write(builder.getResult());
            return true;
        } catch(FileNotFoundException fnfe) {
            return false;
        } catch(Exception ioe) {
            ioe.printStackTrace();
            return false;
            //throw new RuntimeIOException(ioe);
        } finally {
            if(os != null) {
                try { os.close(); }
                catch(Exception e) {}
            }
            if(in != null) {
                try { in.close(); }
                catch(Exception e) {}
            }
        }
    }

    /**
     * Scrubs away all control characters from a string and replaces them with '_'.
     * @param s the string to be processed.
     * @return a scrubbed string.
     */
    private static String scrub(String s) {
        char[] cdata = s.toCharArray();
        for(int i = 0; i < cdata.length; ++i) {
            if((cdata[i] >= 0 && cdata[i] <= 31) ||
               (cdata[i] == 127))
                cdata[i] = '_';
        }
        return new String(cdata);
    }

    private static void logDebug(String s) {
        if(debug)
            System.err.println("DEBUG: " + s);
    }
}
