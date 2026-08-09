/*-
 * Copyright (C) 2006-2026 Erik Larsson
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.catacombae.hfs.ProgressMonitor;
import org.catacombae.hfsexplorer.fs.AppleSingleBuilder;
import org.catacombae.hfsexplorer.ExtractProgressMonitor.
        CreateDirectoryFailedAction;
import org.catacombae.hfsexplorer.ExtractProgressMonitor.CreateFileFailedAction;
import org.catacombae.hfsexplorer.ExtractProgressMonitor.DirectoryExistsAction;
import org.catacombae.hfsexplorer.ExtractProgressMonitor.ExtractProperties;
import org.catacombae.hfsexplorer.ExtractProgressMonitor.FileExistsAction;
import org.catacombae.hfsexplorer.ExtractProgressMonitor.
        UnhandledExceptionAction;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.storage.fs.FSAttributes;
import org.catacombae.storage.fs.FSEntry;
import org.catacombae.storage.fs.FSFile;
import org.catacombae.storage.fs.FSFolder;
import org.catacombae.storage.fs.FSFork;
import org.catacombae.storage.fs.FSForkType;
import org.catacombae.storage.fs.FSLink;
import org.catacombae.storage.fs.FileSystemHandler;
import org.catacombae.util.ObjectContainer;
import org.catacombae.util.Util.Pair;

/**
 * Directory tree extraction logic for HFSExplorer and UnHFS.
 *
 * @author <a href="https://catacombae.org" target="_top">Erik Larsson</a>
 */
public class Extractor {
    /**
     * Calculates the combined size of the forks of types <code>forkTypes</code>
     * for the selection, including for all files in subdirectories,
     * recursively. If <code>forkTypes</code> is empty, all forks are included
     * in the calculation.
     *
     * @param fsHandler
     *      the {@link FileSystemHandler} of the filesystem to extract from.
     * @param parentPath
     *      the parent path of the entries in <code>selection</code>.
     * @param selection the source entries for the calculation.
     * @param progress
     *      the progress monitor that recieves updates about our current state
     *      and decides whether or not to abort.
     * @param calculateDataForkSize
     *      whether the size of the data forks should be included.
     * @param calculateAdditionalForksSize
     *      whether the size of the non-data data forks should be included.
     * @param followSymlinks
     *      whether or not symbolic links should be followed in the tree
     *      traversal.
     * @return the combined size of the forks of types <code>forkTypes</code>
     *      for the selection, including for all files in subdirectories,
     *      recursively.
     */
    public static long calculateForkSizeRecursive(
            final FileSystemHandler fsHandler,
            final String[] parentPath,
            final List<FSEntry> selection,
            final ExtractProgressMonitor progress,
            final boolean calculateDataForkSize,
            final boolean calculateAdditionalForksSize,
            final boolean followSymlinks)
    {
        CalculateTreeSizeVisitor sizeVisitor =
                new CalculateTreeSizeVisitor(
                        /* ExtractProgressMonitor pm */
                        progress,
                        /* boolean includeMainFork */
                        calculateDataForkSize,
                        /* boolean includeAdditionalForks */
                        calculateAdditionalForksSize);

        traverseTree(
                /* FileSystemHandler fsHandler */
                fsHandler,
                /* String[] parentPath */
                parentPath,
                /* List<FSEntry> entries */
                selection,
                /* TreeVisitor visitor */
                sizeVisitor,
                /* boolean followSymbolicLinks */
                followSymlinks);

        return sizeVisitor.getSize();
    }

    private static void traverseTree(
            final FileSystemHandler fsHandler,
            final String[] parentPath,
            final List<FSEntry> entries,
            final TreeVisitor visitor,
            final boolean followSymbolicLinks)
    {
        LinkedList<String[]> absPathsStack = new LinkedList<String[]>();
        LinkedList<String> pathStack = new LinkedList<String>();

        if(parentPath != null) {
            absPathsStack.addLast(parentPath);
            for(String pathComponent : parentPath)
                pathStack.addLast(pathComponent);
        }

        FSEntry[] children = entries.toArray(new FSEntry[entries.size()]);

        traverseTreeRecursive(
                /* FileSystemHandler fsHandler */
                fsHandler,
                /* FSEntry[] selection */
                children,
                /* LinkedList<String> pathStack */
                pathStack,
                /* LinkedList<String[]> absPathsStack */
                absPathsStack,
                /* TreeVisitor visitor */
                visitor,
                /* boolean followSymbolicLinks */
                followSymbolicLinks);
    }

    private static void traverseTreeRecursive(
            final FileSystemHandler fsHandler,
            final FSEntry[] selection,
            final LinkedList<String> pathStack,
            final LinkedList<String[]> absPathsStack,
            final TreeVisitor visitor,
            final boolean followSymbolicLinks)
    {
        if(visitor.cancelTraversal()) {
            return;
        }

        //System.err.println("calculateForkSizeRecursive")
        String[] pathStackArray =
                pathStack.toArray(new String[pathStack.size()]);
        String pathStackString = Util.concatenateStrings(pathStack, "/");

        //System.err.print("Directory: \"");
        //System.err.print(pathStackString);
        //System.err.println("\"...");

        for(FSEntry curEntry : selection) {
            if(visitor.cancelTraversal()) {
                break;
            }

            String curEntryString =
                    (pathStackString.length() > 0 ? pathStackString + "/" :
                    "") + curEntry.getName();
            //System.err.println("Processing \"" + curEntryString + "\"...");

            String[] linkTargetPath = null;
            if(followSymbolicLinks && curEntry instanceof FSLink) {
                FSLink curLink = (FSLink)curEntry;

                //System.err.print("  Getting link target for " +
                //        "\"" + curEntryString + "\"...");
                String[] targetPath =
                        fsHandler.getTargetPath(curLink, pathStackArray);
                if(targetPath != null) {
                    if(Util.contains(absPathsStack, targetPath)) {
                        String msg = "Circular symlink detected: " +
                                "\"" + curEntryString + "\" -> \"" +
                                curLink.getLinkTargetString() + "\"";
                        System.err.println();
                        System.err.println("traverseTreeRecursive: " + msg);
                        System.err.println();
                        visitor.traversalError(msg);
                        continue;
                    }

                    FSEntry linkTarget = fsHandler.getEntry(targetPath);
                    if(linkTarget != null) {
                        //System.err.println("  Happily resolved link " +
                        //        "\"" + curLink.getLinkTargetString() + "\" " +
                        //        "to an FSEntry by the name " +
                        //        "\"" + linkTarget.getName() + "\"");
                        curEntry = linkTarget;
                        linkTargetPath = targetPath;
                    }
                    else {
                        String msg = "Could not get link target entry " +
                                "\"" + curLink.getLinkTargetString() + "\"";
                        System.err.println("WARNING: " + msg);
                        visitor.traversalError(msg);
                    }
                }
                else {
                    String msg = "Could not resolve link " +
                            "\"" + curEntryString + "\" -> \"" +
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
                    absolutePath = Util.concatenate(absPathsStack.getLast(),
                            curEntry.getName());
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
                        try {
                            traverseTreeRecursive(
                                    /* FileSystemHandler fsHandler */
                                    fsHandler,
                                    /* FSEntry[] selection */
                                    curFolder.listEntries(),
                                    /* LinkedList<String> pathStack */
                                    pathStack,
                                    /* LinkedList<String[]> absPathsStack */
                                    absPathsStack,
                                    /* TreeVisitor visitor */
                                    visitor,
                                    /* boolean followSymbolicLinks */
                                    followSymbolicLinks);
                        } finally {
                            if(!visitor.cancelTraversal()) {
                                visitor.endDirectory(
                                        /* String[] parentPath */
                                        pathStackArray,
                                        /* FSFolder folder */
                                        curFolder);
                            }
                        }
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
                    String msg = "Unresolved link \"" + curEntryString + "\" " +
                            "-> \"" + curLink.getLinkTargetString() + "\"";
                    System.err.println(msg);
                    //visitor.traversalError(msg);
                }

                visitor.link((FSLink) curEntry);
            }
            else {
                throw new RuntimeException("Unexpected FSEntry subclass: " +
                        curEntry.getClass());
            }
        }
    }

    /** <code>progressMonitor</code> may NOT be null. */
    public static void extract(
            final FileSystemHandler fsHandler,
            final String[] parentPath,
            final FSEntry rec,
            final File outDir,
            final ExtractProgressMonitor progressMonitor,
            final LinkedList<String> errorMessages,
            final boolean followSymbolicLinks)
    {
        extract(
                /* FileSystemHandler fsHandler */
                fsHandler,
                /* String[] parentPath */
                parentPath,
                /* List<FSEntry> recs */
                Arrays.asList(rec),
                /* File outDir */
                outDir,
                /* ExtractProgressMonitor progressMonitor */
                progressMonitor,
                /* LinkedList<String> errorMessages */
                errorMessages,
                /* boolean followSymbolicLinks */
                followSymbolicLinks,
                /* boolean extractMainFork */
                true,
                /* boolean extractAdditionalForks */
                false);
    }

    /** <code>progressMonitor</code> may NOT be null. */
    /*
    public static void extract(String[] parentPath, FSEntry rec, File outDir,
            ExtractProgressMonitor progressMonitor,
            LinkedList<String> errorMessages, boolean dataFork,
            boolean resourceFork)
    {
        extract(parentPath, Arrays.asList(rec), outDir, progressMonitor,
                errorMessages, dataFork, resourceFork);
    }
    */

    /** <code>progressMonitor</code> may NOT be null. */
    /*
    public static void extract(String[] parentPath, FSEntry[] recs, File outDir,
            ExtractProgressMonitor progressMonitor,
            LinkedList<String> errorMessages)
    {
        extract(parentPath, Arrays.asList(recs), outDir, progressMonitor,
                errorMessages, true, false);
    }
    */

    /** <code>progressMonitor</code> may NOT be null. */
    /*
    public static void extract(String[] parentPath, FSEntry[] recs, File outDir,
            ExtractProgressMonitor progressMonitor,
            LinkedList<String> errorMessages, boolean dataFork,
            boolean resourceFork)
    {
        extract(parentPath, Arrays.asList(recs), outDir, progressMonitor,
                errorMessages, dataFork, resourceFork);
    }
    */

    /** <code>progressMonitor</code> may NOT be null. */
    /*
    public static void extract(String[] parentPath, List<FSEntry> recs,
            File outDir, ExtractProgressMonitor progressMonitor,
            LinkedList<String> errorMessages)
    {
        extract(parentPath, recs, outDir, progressMonitor, errorMessages,
                true, false);
    }
    */

    /**
     * Utility method that checks for the existence of a file. This method tries
     * to overcome some limitations of Java. For instance, the File.exists()
     * method trims spaces in filenames automatically in Windows, which is
     * undesirable.
     */
    private static boolean deepExists(File f) {
        if(!f.exists())
            return false; // We trust that Java never returns false negatives.

        File parentDir = f.getParentFile();
        if(parentDir == null) {
            /* There is no parent, so this must be one of the file system
             * roots (which definitely exists). */
            return true;
        }

        for(File child : parentDir.listFiles()) {
            if(child.getName().equals(f.getName()))
                return true;
        }

        return false;
    }

    public static void extract(
            final FileSystemHandler fsHandler,
            final String[] parentPath,
            final List<FSEntry> recs,
            final File outDir,
            final ExtractProgressMonitor progressMonitor,
            final LinkedList<String> errorMessages,
            final boolean followSymbolicLinks,
            final boolean extractMainFork,
            final boolean extractAdditionalForks)
    {
        if(!deepExists(outDir)) {
            if(!progressMonitor.confirmCreateDirectory(outDir)) {
                //++errorCount;
                errorMessages.addLast("Skipping all files in " +
                        outDir.getAbsolutePath() + " as user chose not to " +
                        "create directory.");
                progressMonitor.signalCancel();
                return;
            }
            else {
                if(!outDir.mkdirs() || !deepExists(outDir)) {
                    progressMonitor.errorMessage("Could not create " +
                            "directory:\n    \"" + outDir.getAbsolutePath() +
                            "\"");
                    errorMessages.addLast("Could not create directory \"" +
                            outDir.getAbsolutePath() + "\".");
                    progressMonitor.signalCancel();
                    return;
                }
            }
        }
        else if(!outDir.isDirectory()) {
            progressMonitor.errorMessage("Target directory is a file:\n" +
                    "    \"" + outDir.getAbsolutePath() + "\"");
            errorMessages.addLast("Could not create directory \"" +
                    outDir.getAbsolutePath() + "\", since a file was in the " +
                    "way.");
            progressMonitor.signalCancel();
            return;
        }

        ExtractVisitor ev =
                new ExtractVisitor(
                        /* ExtractProgressMonitor pm */
                        progressMonitor,
                        /* LinkedList<String> errorMessages */
                        errorMessages,
                        /* File outDir */
                        outDir,
                        /* boolean extractMainFork */
                        extractMainFork,
                        /* boolean extractAdditionalForks */
                        extractAdditionalForks);

        traverseTree(
                /* FileSystemHandler fsHandler */
                fsHandler,
                /* String[] parentPath */
                parentPath,
                /* List<FSEntry> entries */
                recs,
                /* TreeVisitor visitor */
                ev,
                /* boolean followSymbolicLinks */
                followSymbolicLinks);
    }

    /*
    private void extractRecursive(
            FSEntry rec,
            LinkedList<String> pathStack,
            LinkedList<String[]> absPathsStack,
            File outDir,
            ExtractProgressMonitor progressMonitor,
            LinkedList<String> errorMessages,
            ObjectContainer<Boolean> overwriteAll,
            boolean dataFork, boolean resourceFork) {

        if(!dataFork && !resourceFork) {
            throw new IllegalArgumentException("Neither data fork nor " +
                    "resource fork were selected for extraction. Won't do " +
                    "nothing...");
        }
        if(progressMonitor.cancelSignaled()) {
            //progressMonitor.confirmCancel(); // Done by caller.
            return;
        }

        //int errorCount = 0;

        String[] absolutePath = null;
        if(rec instanceof FSLink) {
            FSLink curLink = (FSLink) rec;
            String[] pathStackArray =
                    pathStack.toArray(new String[pathStack.size()]);

            String[] targetPath =
                    fsHandler.getTargetPath(curLink, pathStackArray);
            if(targetPath != null) {
                if(Util.contains(absPathsStack, targetPath)) {
                    System.err.println();
                    System.err.println("extractRecursive: CIRCULAR SYMLINK " +
                            "DETECTED!");
                    System.err.println();
                    errorMessages.addLast("Detected circular soft link " +
                            "\"" + curLink.getName() + "\" in directory \"" +
                            Util.concatenateStrings(pathStackArray, "/") +
                            "\"... skipping this entry.");
                    return;
                }

                FSEntry linkTarget = fsHandler.getEntry(targetPath);
                if(linkTarget != null) {
                    rec = linkTarget;
                    absolutePath = targetPath;
                }
                else {
                    errorMessages.addLast("Could not get entry for link " +
                            "target \"" +
                            Util.concatenateStrings(targetPath, "/") + "\"" +
                            "... skipping this entry.");
                    return;
                }
            }
            else {
                errorMessages.addLast("Could not resolve soft link \"" +
                        curLink.getLinkTargetString() + "\" from directory " +
                        "\"" + Util.concatenateStrings(pathStackArray, "/") +
                        "\"... skipping this entry.");
                return;
            }
            //FSEntry linkTarget = curLink.getLinkTarget(pathStackArray);
        }

        if(rec instanceof FSFile) {
            if(dataFork) {
                extractFile((FSFile) rec, outDir, progressMonitor,
                        errorMessages, overwriteAll, FSForkType.DATA);
            }
            if(resourceFork) {
                extractFile((FSFile) rec, outDir, progressMonitor,
                        errorMessages, overwriteAll, FSForkType.MACOS_RESOURCE);
            }
        }
        else if(rec instanceof FSFolder) {
            String curDirName = rec.getName();
            progressMonitor.updateCurrentDir(curDirName);

            FSEntry[] contents = ((FSFolder) rec).listEntries();
            //System.out.println("folder: \"" + curDirName + "\" valence: " +
            //        contents.length + " range: " + fractionLowLimit + "-" +
            //        fractionHighLimit);

            // We now have the contents of the requested directory
            File thisDir = new File(outDir, curDirName);
            if(!overwriteAll.o && thisDir.exists()) {
                String[] options = new String[]{"Continue", "Cancel"};
                int reply = JOptionPane.showOptionDialog(this,
                        "Warning! Directory:\n" +
                        "    \"" + thisDir.getAbsolutePath() + "\"\n" +
                        "already exists. Do you want to continue extracting " +
                        "to this directory?",
                        "Warning", JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE, null, options, options[0]);
                if(reply != 0) {
                    //++errorCount;
                    errorMessages.addLast("Skipping all files in " +
                            "\"" + thisDir.getAbsolutePath() + "\" due to " +
                            "user interaction.");
                    progressMonitor.signalCancel();
                    return;
                }
            }

            if(thisDir.mkdir() || thisDir.exists()) {
                pathStack.addLast(rec.getName());
                if(absolutePath != null)
                    absPathsStack.addLast(absolutePath);
                try {
                    System.err.println("extractRecursive: pathStack=" +
                            Util.concatenateStrings(pathStack, "/"));
                    System.err.println("extractRecursive: absPathsStack:");
                    for(String[] cur : absPathsStack) {
                        System.err.println("                     " +
                                Util.concatenateStrings(cur, "/"));
                    }

                    for(FSEntry outRec : contents) {
                        extractRecursive(outRec, pathStack, absPathsStack,
                                thisDir, progressMonitor, errorMessages,
                                overwriteAll, dataFork, resourceFork);
                    }
                } finally {
                    if(absolutePath != null)
                        absPathsStack.removeLast();
                    pathStack.removeLast();
                }
            }
            else {
                int reply = JOptionPane.showConfirmDialog(this,
                        "Could not create directory:\n" +
                        "  " + thisDir.getAbsolutePath() + "\n" +
                        "Do you want to continue? (All files under this " +
                        "directory will be skipped)",
                        "Error",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.ERROR_MESSAGE);
                if(reply == JOptionPane.NO_OPTION) {
                    progressMonitor.signalCancel();
                }
                else
                    errorMessages.addLast("Could not create directory " +
                            "\"" + thisDir.getAbsolutePath() + "\". All " +
                            "files under this directory will be skipped.");
                return;
            }
        }
        else {
            //System.out.println("thread with range: " + fractionLowLimit +
            //        "-" + fractionHighLimit);
        }
    }
    */

    private static void handleUnhandledException(
            final Exception e,
            final String actionDescription,
            final String curFileName,
            final ExtractProgressMonitor progressMonitor,
            final LinkedList<String> errorMessages,
            final ExtractProperties extractProperties)
    {
        final UnhandledExceptionAction defaultUnhandledExceptionAction =
                extractProperties.getUnhandledExceptionAction();
        UnhandledExceptionAction a;

        if(defaultUnhandledExceptionAction ==
                UnhandledExceptionAction.PROMPT_USER)
        {
            a = progressMonitor.unhandledException(
                    /* String filename */
                    curFileName,
                    /* Throwable t */
                    e,
                    /* String actionDescription */
                    actionDescription);
        }
        else {
            a = defaultUnhandledExceptionAction;
        }

        if(a != UnhandledExceptionAction.HANDLED) {
            final String message =
                    "An unhandled exception occurred when " + actionDescription;
            System.err.println(message + ":");
            e.printStackTrace();

            errorMessages.addLast(message + ". See debug console for " +
                    "more info.");
        }

        switch(a) {
        case ABORT:
            progressMonitor.signalCancel();
            break;
        case HANDLED:
        case CONTINUE:
            break;
        case ALWAYS_CONTINUE:
            extractProperties.setUnhandledExceptionAction(
                    UnhandledExceptionAction.CONTINUE);
            break;
        default:
            throw new RuntimeException("Internal error! Did not " +
                    "expect a " + a + " here.");
        }
    }

    private static void setExtractedEntryPermissions(
            final File outNode,
            final FSEntry entry,
            final ExtractProgressMonitor progressMonitor,
            final LinkedList<String> errorMessages,
            final ExtractProperties extractProperties)
    {
        if(entry.getAttributes().hasPOSIXFileAttributes() &&
                Java7Util.isJava7OrHigher())
        {
            FSAttributes.POSIXFileAttributes attrs =
                    entry.getAttributes().getPOSIXFileAttributes();
            try {
                Java7Util.setPosixPermissions(outNode.getPath(),
                        attrs.canUserRead(),
                        attrs.canUserWrite(),
                        attrs.canUserExecute(),
                        attrs.canGroupRead(),
                        attrs.canGroupWrite(),
                        attrs.canGroupExecute(),
                        attrs.canOthersRead(),
                        attrs.canOthersWrite(),
                        attrs.canOthersExecute());
            } catch(Exception e) {
                handleUnhandledException(
                        /* Exception e */
                        e,
                        /* String actionDescription */
                        "setting permissions of " +
                        (entry instanceof FSFolder ? "directory" : "file") +
                        " \"" + outNode.getPath() + "\"",
                        /* String curFileName */
                        outNode.getName(),
                        /* ExtractProgressMonitor progressMonitor */
                        progressMonitor,
                        /* LinkedList<String> errorMessages */
                        errorMessages,
                        /* ExtractProperties extractProperties */
                        extractProperties);
            }

            if(progressMonitor.cancelSignaled());
            else try {
                Java7Util.setPosixOwners(outNode.getPath(),
                        (int) attrs.getUserID(),
                        (int) attrs.getGroupID());
            } catch(Exception e) {
                handleUnhandledException(
                        /* Exception e */
                        e,
                        /* String actionDescription */
                        "setting ownership of " +
                        (entry instanceof FSFolder ? "directory" : "file") +
                        " \"" + outNode.getPath() + "\"",
                        /* String curFileName */
                        outNode.getName(),
                        /* ExtractProgressMonitor progressMonitor */
                        progressMonitor,
                        /* LinkedList<String> errorMessages */
                        errorMessages,
                        /* ExtractProperties extractProperties */
                        extractProperties);
            }
        }
    }

    private static void setExtractedEntryFileTimes(
            final File outNode,
            final FSEntry entry,
            final ExtractProgressMonitor progressMonitor,
            final LinkedList<String> errorMessages,
            final ExtractProperties extractProperties)
    {
        Long createTime = null;
        Long lastAccessTime = null;
        Long lastModifiedTime = null;

        if(entry.getAttributes().hasCreateDate()) {
            createTime = entry.getAttributes().getCreateDate().getTime();
        }

        if(entry.getAttributes().hasAccessDate()) {
            lastAccessTime =
                    entry.getAttributes().getAccessDate().getTime();
        }

        if(entry.getAttributes().hasModifyDate()) {
            lastModifiedTime =
                    entry.getAttributes().getModifyDate().getTime();
        }

        boolean fileTimesSet = false;
        if(Java7Util.isJava7OrHigher()) {
            try {
                Java7Util.setFileTimes(outNode.getPath(),
                        createTime != null ? new Date(createTime) :
                        null,
                        lastAccessTime != null ?
                        new Date(lastAccessTime) : null,
                        lastModifiedTime != null ?
                        new Date(lastModifiedTime) : null);
                fileTimesSet = true;
            } catch(Exception e) {
                handleUnhandledException(
                        /* Exception e */
                        e,
                        /* String actionDescription */
                        "setting timestamps of " +
                        (entry instanceof FSFolder ? "directory" : "file") +
                        " \"" + outNode.getPath() + "\"",
                        /* String curFileName */
                        outNode.getName(),
                        /* ExtractProgressMonitor progressMonitor */
                        progressMonitor,
                        /* LinkedList<String> errorMessages */
                        errorMessages,
                        /* ExtractProperties extractProperties */
                        extractProperties);
            }
        }

        if(!fileTimesSet && lastModifiedTime != null) {
            boolean setLastModifiedResult;

            if(lastModifiedTime < 0) {
                errorMessages.addLast("Cannot to set last modified time for " +
                        "\"" + outNode.getPath() + "\" to pre-1970 date. " +
                        "Adjusting last modified time from " +
                        new Date(lastModifiedTime) + " to " + new Date(0) +
                        ".");

                lastModifiedTime = (long) 0;
            }

            setLastModifiedResult =
                    outNode.setLastModified(lastModifiedTime);

            if(!setLastModifiedResult) {
                errorMessages.addLast("Failed to set last modified time for " +
                        "\"" + outNode.getPath() + "\" to " +
                        new Date(lastModifiedTime) + " (raw: " +
                        lastModifiedTime + ").");
            }
        }
    }

    private static void setExtractedEntryAttributes(
            final File outNode,
            final FSEntry entry,
            final ExtractProgressMonitor progressMonitor,
            final LinkedList<String> errorMessages,
            final ExtractProperties extractProperties)
    {
        setExtractedEntryFileTimes(
                /* File outNode */
                outNode,
                /* FSEntry entry */
                entry,
                /* ExtractProgressMonitor progressMonitor */
                progressMonitor,
                /* LinkedList<String> errorMessages */
                errorMessages,
                /* ExtractProperties extractProperties */
                extractProperties);

        if(!progressMonitor.cancelSignaled()) {
            setExtractedEntryPermissions(
                    /* File outNode */
                    outNode,
                    /* FSEntry entry */
                    entry,
                    /* ExtractProgressMonitor progressMonitor */
                    progressMonitor,
                    /* LinkedList<String> errorMessages */
                    errorMessages,
                    /* ExtractProperties extractProperties */
                    extractProperties);
        }
    }

    private static void extractEntry(
            final FSEntry rec,
            final File outDir,
            final ExtractProgressMonitor progressMonitor,
            final LinkedList<String> errorMessages,
            final ExtractProperties extractProperties,
            final ObjectContainer<Boolean> skipDirectory,
            final boolean extractAdditionalForks)
    {
        //int errorCount = 0;
        final String originalFileName;

        if(!extractAdditionalForks) {
            originalFileName = rec.getName();
        }
        else {
            // Special syntax for resource forks in foreign file systems.
            originalFileName = "._" + rec.getName();
        }

        CreateFileFailedAction defaultCreateFileFailedAction =
                extractProperties.getCreateFileFailedAction();
        FileExistsAction defaultFileExistsAction =
                extractProperties.getFileExistsAction();

        String fileName = originalFileName;

        while(fileName != null) {
            String curFileName = fileName;
            fileName = null;

            //System.out.println("file: \"" + filename + "\" range: " +
            //        fractionLowLimit + "-" + fractionHighLimit);
            long totalForkSize;
            if(extractAdditionalForks) {
                totalForkSize = 0;

                for(FSFork f : rec.getAllForks()) {
                    if(f.getType() == FSForkType.DATA) {
                        continue;
                    }

                    totalForkSize += f.getLength();
                }

                if(totalForkSize == 0) {
                    /* Don't create empty AppleDouble files. */
                    return;
                }
            }
            else if(rec instanceof FSFile) {
                totalForkSize = ((FSFile) rec).getMainFork().getLength();
            }
            else if(rec instanceof FSLink) {
                totalForkSize = 0;
            }
            else {
                /* Nothing to extract. */
                return;
            }

            progressMonitor.updateCurrentFile(curFileName, totalForkSize);

            final File outFile = new File(outDir, curFileName);
            //progressMonitor.updateTotalProgress(fractionLowLimit);

            /* Note: We may want to use deepExists here like in the directory
             * case, but it's less urgent here so I'll pass for now. */
            if(defaultFileExistsAction != FileExistsAction.OVERWRITE &&
                    outFile.exists())
            {
                FileExistsAction a;
                if(defaultFileExistsAction == FileExistsAction.PROMPT_USER)
                    a = progressMonitor.fileExists(outFile);
                else {
                    a = defaultFileExistsAction;
                    defaultFileExistsAction = FileExistsAction.PROMPT_USER;
                }

                if(a == FileExistsAction.OVERWRITE) {
                    if(!outFile.delete()) {
                        continue;
                    }
                }
                else if(a == FileExistsAction.OVERWRITE_ALL) {
                    if(!outFile.delete()) {
                        continue;
                    }

                    extractProperties.setFileExistsAction(
                            FileExistsAction.OVERWRITE);
                    defaultFileExistsAction = FileExistsAction.OVERWRITE;
                }
                else if(a == FileExistsAction.SKIP_FILE) {
                    errorMessages.addLast("Skipped extracting file " +
                            "\"" + outFile.getAbsolutePath() + "\" due to " +
                            "user interaction.");
                    break;
                }
                else if(a == FileExistsAction.SKIP_DIRECTORY) {
                    errorMessages.addLast("Skipping entire directory " +
                            "\"" + outDir.getAbsolutePath() + "\" due to " +
                            "user interaction.");
                    skipDirectory.o = true;
                    break;
                }
                else if(a == FileExistsAction.RENAME) {
                    fileName = progressMonitor.displayRenamePrompt(
                            /* String currentName */
                            curFileName,
                            /* File outDir */
                            outDir);

                    if(fileName == null)
                        fileName = curFileName;
                    continue;
                }
                else if(a == FileExistsAction.AUTO_RENAME) {
                    fileName = FileNameTools.autoRenameIllegalFilename(
                            /* String filename */
                            curFileName,
                            /* File outDir */
                            outDir,
                            /* boolean isDirectory */
                            false);

                    if(fileName == null)
                        fileName = curFileName;
                    continue;
                }
                else if(a == FileExistsAction.CANCEL) {
                    progressMonitor.signalCancel();
                    break;
                }
                else {
                    throw new RuntimeException("Internal error! Did not " +
                            "expect a: " + a);
                }
            }

            FileOutputStream fos = null;
            boolean extracted = false;
            try {
//              try {
//                  PrintStream p = System.out;
//                  File f = outFile;
//                  p.println("Printing some information about the output " +
//                          "file: ");
//                  p.println("f.getParent(): \"" + f.getParent() + "\"");
//                  p.println("f.getName(): \"" + f.getName() + "\"");
//                  p.println("f.getAbsolutePath(): \"" + f.getAbsolutePath() +
//                          "\"");
//                  p.println("f.exists(): \"" + f.exists() + "\"");
//                  p.println("f.getCanonicalPath(): \"" +
//                          f.getCanonicalPath() + "\"");
//                  //p.println("f.getParent(): \"" + f.getParent() + "\"");
//              } catch(Exception e) { e.printStackTrace(); }

                // Test that outFile is valid.
                try {
                    outFile.getCanonicalPath();
                } catch(Exception e) {
                    throw new FileNotFoundException();
                }

                if(!outFile.getParentFile().equals(outDir) ||
                        !outFile.getName().equals(curFileName))
                {
                    throw new FileNotFoundException();
                }

                if(extractAdditionalForks) {
                    fos = new FileOutputStream(outFile);
                    extractAdditionalForksToAppleDoubleStream(
                            /* FSEntry entry */
                            rec,
                            /* OutputStream os */
                            fos,
                            /* ProgressMonitor pm */
                            progressMonitor);
                }
                else if(rec instanceof FSFile) {
                    fos = new FileOutputStream(outFile);
                    extractForkToStream(
                            /* FSFork theFork */
                            ((FSFile) rec).getMainFork(),
                            /* OutputStream os */
                            fos,
                            /* ProgressMonitor pm */
                            progressMonitor);
                }
                else if(rec instanceof FSLink) {
                    if(Java7Util.isJava7OrHigher()) {
                        Java7Util.createSymbolicLink(
                                /* String linkPathString */
                                outFile.getPath(),
                                /* String targetPathString */
                                ((FSLink) rec).getLinkTargetString());
                    }
                    else {
                        /* Create the link in OS-specific way? Need native code
                         * for that... unless we create a new 'ln -s' process,
                         * but it will be slow... UNLESS we thread it out, and
                         * don't wait for it to finish. OK, flooding the OS with
                         * ln processes isn't good either... */
                    }
                }

                extracted = true;

                if(fos != null) {
                    fos.close();
                    fos = null;
                }

                if(curFileName != (Object) originalFileName &&
                        !curFileName.equals(originalFileName))
                {
                    errorMessages.addLast("File \"" + originalFileName + "\" " +
                            "was renamed to \"" + curFileName + "\" in " +
                            "parent folder \"" + outDir.getAbsolutePath() +
                            "\".");
                }
            } catch(FileNotFoundException fnfe) {
                // <Debug messages>
                System.out.println("Could not create file " +
                        "\"" + outFile + "\". The following exception was " +
                        "thrown:");
                fnfe.printStackTrace();
                char[] filenameChars = curFileName.toCharArray();
                System.out.println("Filename in hex (" + filenameChars.length +
                        " UTF-16BE units):");
                System.out.print("  0x");
                for(char c : filenameChars) {
                    System.out.print(" " + Util.toHexStringBE(c));
                }
                System.out.println();
                // </Debug messages>

                // <Prompt user for action, if needed>
                CreateFileFailedAction a;
                if(defaultCreateFileFailedAction ==
                        CreateFileFailedAction.PROMPT_USER)
                {
                    a = progressMonitor.createFileFailed(curFileName, outDir);
                }
                else {
                    a = defaultCreateFileFailedAction;
                    defaultCreateFileFailedAction =
                            CreateFileFailedAction.PROMPT_USER;
                }

                if(a == CreateFileFailedAction.SKIP_FILE) {
                    errorMessages.addLast("Skipped extracting file " +
                            "\"" + outFile.getAbsolutePath() + "\" due to " +
                            "user interaction.");
                    break;
                }
                else if(a == CreateFileFailedAction.SKIP_DIRECTORY) {
                    errorMessages.addLast("Skipping entire directory " +
                            "\"" + outDir.getAbsolutePath() + "\" due to " +
                            "user interaction.");
                    skipDirectory.o = true;
                    break;
                }
                else if(a == CreateFileFailedAction.RENAME) {
                    fileName = progressMonitor.displayRenamePrompt(
                            /* String currentName */
                            curFileName,
                            /* File outDir */
                            outDir);

                    if(fileName == null)
                        fileName = curFileName;
                    continue;
                }
                else if(a == CreateFileFailedAction.AUTO_RENAME) {
                    fileName = FileNameTools.autoRenameIllegalFilename(
                            /* String filename */
                            curFileName,
                            /* File outDir */
                            outDir,
                            /* boolean isDirectory */
                            false);

                    if(fileName == null)
                        fileName = curFileName;
                    continue;
                }
                else if(a == CreateFileFailedAction.CANCEL) {
                    progressMonitor.signalCancel();
                    break;
                }
                else {
                    throw new RuntimeException("Internal error! Did not " +
                            "expect a: " + a);
                }
                // </Prompt user for action, if needed>
            } catch(IOException ioe) {
                final String message =
                        "Encountered an I/O exception while " +
                        "trying to write to file " +
                        "\"" + outFile.getPath() + "\"";
                System.err.println(message + ":");
                ioe.printStackTrace();

                errorMessages.addLast(message + ". See debug console for " +
                        "more info.");
                if(!progressMonitor.displayIoErrorPrompt(
                        /* String fileName */
                        curFileName,
                        /* File outDir */
                        outDir,
                        /* IOException ioe */
                        ioe))
                {
                    progressMonitor.signalCancel();
                }
            } catch(Exception e) {
                handleUnhandledException(
                        /* Exception e */
                        e,
                        /* String actionDescription */
                        "extracting to file \"" + outFile.getPath() + "\"",
                        /* String curFileName */
                        curFileName,
                        /* ExtractProgressMonitor progressMonitor */
                        progressMonitor,
                        /* LinkedList<String> errorMessages */
                        errorMessages,
                        /* ExtractProperties extractProperties */
                        extractProperties);
            }
            finally {
                if(fos != null) {
                    try {
                        fos.close();
                    } catch(IOException ex) {
                        ex.printStackTrace();
                    }

                    fos = null;
                }

                if(extracted) {
                    setExtractedEntryAttributes(
                            /* File outNode */
                            outFile,
                            /* FSEntry entry */
                            rec,
                            /* ExtractProgressMonitor progressMonitor */
                            progressMonitor,
                            /* LinkedList<String> errorMessages */
                            errorMessages,
                            /* ExtractProperties extractProperties */
                            extractProperties);
                }
            }

            break;
        }

        //return errorCount;
    }

    private static long extractForkToStream(
            FSFork theFork,
            OutputStream os,
            ProgressMonitor pm) throws IOException
    {
        ReadableRandomAccessStream forkFilter =
                theFork.getReadableRandomAccessStream();
        //System.out.println("extractForkToStream working with a " +
        //        forkFilter.getClass());
        final long originalLength = theFork.getLength();
        long bytesToRead = originalLength;
        byte[] buffer = new byte[1*1024*1024];
        while(bytesToRead > 0) {
            if(pm.cancelSignaled()) {
                break;
            }
            //System.out.print("forkFilter.read([].length=" + buffer.length +
            //        ", 0, " + (bytesToRead < buffer.length ?
            //        (int) bytesToRead : buffer.length) + "...");
            int bytesRead = forkFilter.read(
                    /* byte[] data */
                    buffer,
                    /* int pos */
                    0,
                    /* int len */
                    (bytesToRead < buffer.length) ? (int) bytesToRead :
                    buffer.length);
            //System.out.println("done. bytesRead = " + bytesRead);
            if(bytesRead < 0) {
                break;
            }
            else {
                //System.out.println("Read the following from the forkfilter " +
                //        "(" + bytesRead + " bytes): ");
                //System.out.println(Util.byteArrayToHexString(buffer, 0,
                //        bytesRead));
                pm.addDataProgress(bytesRead);
                os.write(buffer, 0, bytesRead);
                bytesToRead -= bytesRead;
            }
        }
        return originalLength - bytesToRead;
    }

    private static long extractAdditionalForksToAppleDoubleStream(
            final FSEntry entry,
            final OutputStream os,
            final ProgressMonitor pm) throws IOException
    {
        ByteArrayOutputStream baos = null;
        ReadableRandomAccessStream in = null;
        try {
            final LinkedList<Pair<String, byte[]>> attributeList =
                    new LinkedList<Pair<String, byte[]>>();
            final AppleSingleBuilder builder =
                    new AppleSingleBuilder(
                            AppleSingleBuilder.FileType.APPLEDOUBLE,
                            AppleSingleBuilder.AppleSingleVersion.VERSION_2_0,
                            AppleSingleBuilder.FileSystem.MACOS_X);

            byte[] finderInfoData = null;
            byte[] resourceForkData = null;
            long extractedBytes = 0;

            for(FSFork f : entry.getAllForks()) {
                FSForkType forkType = f.getType();
                if(forkType == FSForkType.MACOS_RESOURCE) {
                    resourceForkData =
                            IOUtil.readFully(f.getReadableRandomAccessStream());
                    extractedBytes += resourceForkData.length;
                }
                else if(forkType == FSForkType.MACOS_FINDERINFO) {
                    finderInfoData =
                            IOUtil.readFully(f.getReadableRandomAccessStream());
                    extractedBytes += finderInfoData.length;
                }
                else if(f.hasXattrName()) {
                    final byte[] attributeData =
                            IOUtil.readFully(f.getReadableRandomAccessStream());
                    attributeList.add(new Pair<String, byte[]>(f.getXattrName(),
                            attributeData));
                    extractedBytes += attributeData.length;
                }
            }

            if(finderInfoData != null || attributeList.size() > 0) {
                builder.addFinderInfo(finderInfoData, attributeList);
            }

            if(resourceForkData != null) {
                builder.addResourceFork(resourceForkData);
            }
            else {
                builder.addEmptyResourceFork();
            }

            if(extractedBytes > 0) {
                os.write(builder.getResult());
                pm.addDataProgress(extractedBytes);
            }

            return extractedBytes;
        } finally {
            if(in != null) {
                try { in.close(); }
                catch(Exception e) {}
            }

            if(baos != null) {
                try { baos.close(); }
                catch(Exception e) {}
            }
        }
    }

    /**
     * An interface for visitors that can be used in the traverseTree method.
     */
    private static interface TreeVisitor {
        /**
         * Callback for when we are about to enter a new directory.
         *
         * @param parentPath the path of the parent of the new directory.
         * @param folder the {@link FSFolder} that we are about to enter.
         * @return <code>true</code> if tree traversal should enter this
         * directory and <code>false</code> otherwise or not. If the visitor
         * returns <code>false</code> for a directory, it will not get an
         * <code>endDirectory</code> event for that directory.
         */
        public boolean startDirectory(String[] parentPath, FSFolder folder);
        public void endDirectory(String[] parentPath, FSFolder folder);
        public void file(FSFile fsf);
        public void link(FSLink fsl);

        /**
         * This method is called when the traversal engine encounters a
         * non-critical error.
         * @param message the error message.
         */
        public void traversalError(String message);

        /**
         * Callback which should return true when the traversal process is to be
         * aborted.
         *
         * @return true if the visitor requests that the tree traversal be
         * aborted.
         */
        public boolean cancelTraversal();
    }

    private static class NullTreeVisitor implements TreeVisitor {
        /* @Override */
        public boolean startDirectory(String[] parentPath, FSFolder folder) {
            return true;
        }

        /* @Override */
        public void endDirectory(String[] parentPath, FSFolder folder) {}

        /* @Override */
        public void file(FSFile fsf) {}

        /* @Override */
        public void link(FSLink fsl) {}

        /* @Override */
        public void traversalError(String message) {}

        /* @Override */
        public boolean cancelTraversal() {
            return false;
        }
    }

    private static class CalculateTreeSizeVisitor extends NullTreeVisitor {
        private final ExtractProgressMonitor pm;
        private final boolean includeMainFork;
        private final boolean includeAdditionalForks;

        private final StringBuilder sb = new StringBuilder();
        private long size = 0;
        //private LinkedList<String> errorMessages = new LinkedList<String>();

        public CalculateTreeSizeVisitor(ExtractProgressMonitor pm,
                boolean includeMainFork, boolean includeAdditionalForks)
        {
            this.pm = pm;
            this.includeMainFork = includeMainFork;
            this.includeAdditionalForks = includeAdditionalForks;

            if(this.pm == null) {
                throw new IllegalArgumentException("pm == null");
            }

            if(!includeMainFork && !includeAdditionalForks) {
                throw new IllegalArgumentException("No fork types to extract.");
            }
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
            for(FSFork fork : file.getAllForks()) {
                final boolean isMainFork = fork.getType() == FSForkType.DATA;
                if((isMainFork && includeMainFork) ||
                        (!isMainFork && includeAdditionalForks))
                {
                    size += fork.getLength();
                }
            }
        }

        @Override
        public boolean cancelTraversal() { return pm.cancelSignaled(); }
    }

    private static class ExtractVisitor extends NullTreeVisitor {
        private final ExtractProgressMonitor pm;
        private final LinkedList<String> errorMessages;
        private final File outRootDir;
        /* private final ObjectContainer<Boolean> overwriteAll =
                new ObjectContainer<Boolean>(false); */
        private final ObjectContainer<Boolean> skipDirectory =
                new ObjectContainer<Boolean>(false);
        private final ExtractProperties extractProperties;
        private final boolean extractMainFork;
        private final boolean extractAdditionalForks;
        private final LinkedList<File> outDirStack = new LinkedList<File>();

        public ExtractVisitor(
                ExtractProgressMonitor pm,
                LinkedList<String> errorMessages,
                File outDir,
                boolean extractMainFork,
                boolean extractAdditionalForks)
        {
            this.pm = pm;
            this.errorMessages = errorMessages;
            this.outRootDir = outDir;
            this.extractMainFork = extractMainFork;
            this.extractAdditionalForks = extractAdditionalForks;
            this.extractProperties = this.pm.getExtractProperties();

            if(this.pm == null)
                throw new IllegalArgumentException("pm == null");
            if(this.errorMessages == null)
                throw new IllegalArgumentException("errorMessages == null");
            if(this.outRootDir == null)
                throw new IllegalArgumentException("outDir == null");

            if(!extractMainFork && !extractAdditionalForks) {
                throw new IllegalArgumentException("No fork types to extract.");
            }

            outDirStack.addLast(outDir);
        }

        @Override
        public boolean startDirectory(String[] parentPath, FSFolder folder) {
            //System.err.println("startDirectory(" +
            //        Util.concatenateStrings(parentPath, "/") + ", " +
            //        folder.getName());
            //if(skipDirectory.o) {
            //    System.err.println("  skipping...");
            //    return false;
            //}

            //System.err.println("outDirStack.getLast()=" +
            //        outDirStack.getLast());
            final File outDir = outDirStack.getLast();

            final CreateDirectoryFailedAction
                    originalCreateDirectoryFailedAction =
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

                if(defaultDirectoryExistsAction !=
                        DirectoryExistsAction.CONTINUE && deepExists(thisDir))
                {
                    DirectoryExistsAction a;
                    if(defaultDirectoryExistsAction ==
                            DirectoryExistsAction.PROMPT_USER)
                    {
                        a = pm.directoryExists(thisDir);
                    }
                    else {
                        a = defaultDirectoryExistsAction;
                    }

                    boolean resetLoop = false;
                    switch(a) {
                        case CONTINUE:
                            break;
                        case ALWAYS_CONTINUE:
                            extractProperties.setDirectoryExistsAction(
                                    DirectoryExistsAction.CONTINUE);
                            break;
                        case RENAME:
                            dirName = pm.displayRenamePrompt(
                                    /* String currentName */
                                    curDirName,
                                    /* File outDir */
                                    outDir);
                            if(dirName == null)
                                dirName = curDirName;
                            resetLoop = true;
                            break;
                        case AUTO_RENAME:
                            dirName = FileNameTools.autoRenameIllegalFilename(
                                    /* String filename */
                                    curDirName,
                                    /* File outDir */
                                    outDir,
                                    /* boolean isDirectory */
                                    true);
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
                            throw new RuntimeException("Internal error! Did " +
                                    "not expect a: " + a);
                    }
                    if(resetLoop)
                        continue;
                }

                /* If the directory already exists, then fine. If not, we create
                 * it and double check that it exists afterwards (to avoid
                 * unexpected side effects, like in Windows). */
                if(deepExists(thisDir) ||
                        (thisDir.mkdir() && deepExists(thisDir)))
                {
                    if(curDirName != (Object)originalDirName &&
                            !curDirName.equals(originalDirName))
                    {
                        errorMessages.addLast("Directory \"" + originalDirName +
                                "\" was renamed to \"" + curDirName + "\" in " +
                                "parent folder \"" + outDir.getAbsolutePath() +
                                "\".");
                    }

                    outDirStack.addLast(thisDir);
                    return true;
                }
                else {
                    CreateDirectoryFailedAction a;
                    if(defaultCreateDirectoryFailedAction ==
                            CreateDirectoryFailedAction.PROMPT_USER)
                    {
                        a = pm.createDirectoryFailed(curDirName, outDir);
                    }
                    else {
                        a = defaultCreateDirectoryFailedAction;
                        /* Only perform the default action once... or else we
                         * would have an endless loop. */
                        defaultCreateDirectoryFailedAction =
                                CreateDirectoryFailedAction.PROMPT_USER;
                    }

                    switch(a) {
                        case SKIP_DIRECTORY:
                            errorMessages.addLast("Could not create " +
                                    "directory \"" + thisDir.getAbsolutePath() +
                                    "\". All files under this directory will " +
                                    "be skipped.");
                            break;
                        case RENAME:
                            dirName = pm.displayRenamePrompt(
                                    /* String currentName */
                                    curDirName,
                                    /* File outDir */
                                    outDir);
                            if(dirName == null)
                                dirName = curDirName;
                            break;
                        case AUTO_RENAME:
                            dirName = FileNameTools.autoRenameIllegalFilename(
                                    /* String filename */
                                    curDirName,
                                    /* File outDir */
                                    outDir,
                                    /* boolean isDirectory */
                                    true);
                            if(dirName == null) {
                                dirName = curDirName;
                                /*
                                if(originalCreateDirectoryFailedAction ==
                                        CreateDirectoryFailedAction.AUTO_RENAME)
                                {
                                    // If we got here by the default action,
                                    // then we don't want to bother the user...
                                    errorMessages.addLast("Auto-rename " +
                                            "failed for dir name \"" +
                                            curDirName + "\" in parent " +
                                            "directory \"" +
                                            outDir.getAbsolutePath() +
                                            "\". All files under this " +
                                            "directory will be skipped.");
                                    defaultCreateDirectoryFailedAction =
                                            CreateDirectoryFailedAction.
                                            SKIP_DIRECTORY;
                                }
                                */
                            }
                            break;
                        case CANCEL:
                            pm.signalCancel();
                            break;
                        default:
                            throw new RuntimeException("Internal error! Did " +
                                    "not expect a: " + a);
                    }

                }
            }
            return false;
        }

        @Override
        public void endDirectory(String[] parentPath, FSFolder folder) {
            File outDir = outDirStack.removeLast();

            /* Extract any extended attributes into an AppleDouble file in
             * outDir's parent. */
            if(extractAdditionalForks) {
                extractEntry(
                        /* FSEntry rec */
                        folder,
                        /* File outDir */
                        outDirStack.getLast(),
                        /* ExtractProgressMonitor progressMonitor */
                        pm,
                        /* LinkedList<String> errorMessages */
                        errorMessages,
                        /* ExtractProperties extractProperties */
                        extractProperties,
                        /* ObjectContainer<Boolean> skipDirectory */
                        skipDirectory,
                        /* boolean extractAdditionalForks */
                        true);
            }

            /* Finally reset the attributes of the directory to the attributes
             * of the FSFolder to make sure that times, mode, ownership, etc.
             * matches what we have in the file system (provided that there is
             * support for these attributes in the target file system). */
            setExtractedEntryAttributes(
                    /* File outNode */
                    outDir,
                    /* FSEntry entry */
                    folder,
                    /* ExtractProgressMonitor progressMonitor */
                    pm,
                    /* LinkedList<String> errorMessages */
                    errorMessages,
                    /* ExtractProperties extractProperties */
                    extractProperties);

            skipDirectory.o = false;
        }

        @Override
        public void file(FSFile fsf) {
            if(skipDirectory.o)
                return;

            File outDir = outDirStack.getLast();

            if(extractMainFork) {
                extractEntry(
                        /* FSEntry rec */
                        fsf,
                        /* File outDir */
                        outDir,
                        /* ExtractProgressMonitor progressMonitor */
                        pm,
                        /* LinkedList<String> errorMessages */
                        errorMessages,
                        /* ExtractProperties extractProperties */
                        extractProperties,
                        /* ObjectContainer<Boolean> skipDirectory */
                        skipDirectory,
                        /* boolean extractAdditionalForks */
                        false);
            }

            if(extractAdditionalForks) {
                extractEntry(
                        /* FSEntry rec */
                        fsf,
                        /* File outDir */
                        outDir,
                        /* ExtractProgressMonitor progressMonitor */
                        pm,
                        /* LinkedList<String> errorMessages */
                        errorMessages,
                        /* ExtractProperties extractProperties */
                        extractProperties,
                        /* ObjectContainer<Boolean> skipDirectory */
                        skipDirectory,
                        /* boolean extractAdditionalForks */
                        true);
            }
        }

        @Override
        public void link(FSLink fsl) {
            File outDir = outDirStack.getLast();

            extractEntry(
                    /* FSEntry rec */
                    fsl,
                    /* File outDir */
                    outDir,
                    /* ExtractProgressMonitor progressMonitor */
                    pm,
                    /* LinkedList<String> errorMessages */
                    errorMessages,
                    /* ExtractProperties extractProperties */
                    extractProperties,
                    /* ObjectContainer<Boolean> skipDirectory */
                    skipDirectory,
                    /* boolean extractAdditionalForks */
                    false);

            /* Extract any extended attributes belonging to the link itself. */
            if(extractAdditionalForks) {
                extractEntry(
                        /* FSEntry rec */
                        fsl,
                        /* File outDir */
                        outDir,
                        /* ExtractProgressMonitor progressMonitor */
                        pm,
                        /* LinkedList<String> errorMessages */
                        errorMessages,
                        /* ExtractProperties extractProperties */
                        extractProperties,
                        /* ObjectContainer<Boolean> skipDirectory */
                        skipDirectory,
                        /* boolean extractAdditionalForks */
                        true);
            }
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
}
