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

package org.catacombae.hfsexplorer;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JOptionPane;
import org.catacombae.hfsexplorer.ExtractProgressMonitor.DirectoryExistsAction;
import org.catacombae.hfsexplorer.ExtractProgressMonitor.ExtractProperties;

/**
 * @author <a href="https://catacombae.org" target="_top">Erik Larsson</a>
 */
public class SimpleGUIProgressMonitor extends BasicExtractProgressMonitor {
    private final Component parentComponent;
    private final ExtractProperties extractProperties = new ExtractProperties();

    public SimpleGUIProgressMonitor(Component parentComponent) {
        this.parentComponent = parentComponent;
    }

    /* @Override */
    public ExtractProperties getExtractProperties() {
        return extractProperties;
    }

    /**
     * @see #confirmCreateDirectory(java.awt.Component, java.io.File)
     */
    public boolean confirmCreateDirectory(File dir) {
        return confirmCreateDirectory(parentComponent, dir);
    }

    /*
    @Override
    public boolean confirmOverwriteDirectory(File dir) {
        return confirmOverwriteDirectory(parentComponent, dir);
    }
    */

    /**
     * A simple default GUI implementation of a confirm dialog for overwriting a
     * directory.
     *
     * @param parentComponent
     * @param dir
     * @return whether or not the user accepts to overwrite <code>dir</code>.
     */
    /*
    public static boolean confirmOverwriteDirectory(Component parentComponent,
            File dir) {
        String[] options = new String[]{"Continue", "Cancel"};
        int reply = JOptionPane.showOptionDialog(parentComponent,
                "Warning! Directory:\n    \"" + dir.getAbsolutePath() + "\"\n" +
                "already exists. Do you want to continue extracting to this " +
                "directory?", "Warning", JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE, null, options, options[0]);
        return reply == 0;
    }
    */

    /*
    @Override
    public boolean confirmSkipDirectory(String... messageLines) {
        return confirmSkipDirectory(parentComponent, messageLines);
    }
    */

    /*
    public static boolean confirmSkipDirectory(Component parentComponent,
            String... messageLines) {
        StringBuilder sb = new StringBuilder();
        for(String messageLine : messageLines)
            sb.append(messageLine).append("\n");

        int reply = JOptionPane.showConfirmDialog(parentComponent,
                sb.toString() + "Do you want to continue? (All files under " +
                "this directory will be skipped)", "Error",
                JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
        return reply != JOptionPane.NO_OPTION;
    }
    */

    /**
     * @see #createDirectoryFailed(java.awt.Component, java.lang.String,
     * java.io.File)
     */
    /* @Override */
    public CreateDirectoryFailedAction createDirectoryFailed(String dirname,
            File parentDirectory) {
        return createDirectoryFailed(parentComponent, dirname, parentDirectory);
    }

    /**
     * @see #createFileFailed(java.awt.Component, java.lang.String,
     * java.io.File)
     */
    /* @Override */
    public CreateFileFailedAction createFileFailed(String filename,
            File parentDirectory) {
        return createFileFailed(parentComponent, filename, parentDirectory);
    }

    /**
     * @see #directoryExists(java.awt.Component, java.io.File)
     */
    /* @Override */
    public DirectoryExistsAction directoryExists(File directory) {
        return directoryExists(parentComponent, directory);
    }

    /**
     * @see #fileExists(java.awt.Component, java.io.File)
     */
    /* @Override */
    public FileExistsAction fileExists(File file) {
        return fileExists(parentComponent, file);
    }

    /**
     * @see #unhandledException(java.awt.Component, java.lang.String,
     * java.lang.Throwable)
     */
    /* @Override */
    public UnhandledExceptionAction unhandledException(
            final String filename,
            final Throwable t,
            final String actionDescription)
    {
        return unhandledException(parentComponent, filename, t,
                actionDescription);
    }

    /**
     * @see #errorMessage(java.awt.Component, java.lang.String)
     */
    /* @Override */
    public void errorMessage(String message) {
        errorMessage(parentComponent, message);
    }

    /**
     * @see #displayRenamePrompt(java.awt.Component, java.lang.String,
     * java.io.File)
     */
    /* @Override */
    public String displayRenamePrompt(String currentName, File outDir) {
        return displayRenamePrompt(parentComponent, currentName, outDir);
    }

    /**
     * @see #displayIoErrorPrompt(java.awt.Component, java.lang.String,
     * java.io.File, java.io.IOException)
     */
    /* @Override */
    public boolean displayIoErrorPrompt(String fileName, File outDir,
            IOException ioe)
    {
        return displayIoErrorPrompt(parentComponent, fileName, outDir, ioe);
    }

    /**
     * Default Swing implementation of a "Do you want to create this directory?"
     * user prompt.<br>
     * Returns <code>true</code> if the directory should be created, and
     * <code>false</code> if not.
     *
     * @param parentComponent the parent component of the user prompt dialog
     * box.
     * @param dir
     *      the currently non-existent directory that we are asking to create.
     * @return <code>true</code> if we should create this directory and
     * <code>false</code> if not.
     */
    public static boolean confirmCreateDirectory(Component parentComponent,
            File dir)
    {
        final String[] options = new String[] { "Create directory", "Cancel" };
        int reply = JOptionPane.showOptionDialog(parentComponent,
                "Target directory:\n" +
                "    \"" + dir.getAbsolutePath() + "\"\n" +
                "does not exist. Do you want to create this directory?",
                "Warning", JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE, null, options, options[0]);
        return (reply == 0);
    }

    /**
     * Default Swing implementation of a "Failed to create directory" user
     * prompt.<br>
     * This method will never return <code>null</code>.
     *
     * @param parentComponent the parent component of the user prompt dialog
     * box.
     * @param dirname the name of the directory for which this prompt was
     * triggered.
     * @param parentDirectory the parent directory of the directory for which
     * this prompt was triggered.
     * @return one of RENAME, SKIP_DIRECTORY, AUTO_RENAME or CANCEL.
     */
    public static CreateDirectoryFailedAction createDirectoryFailed(
            Component parentComponent, String dirname, File parentDirectory) {
        String[] options = new String[] {
            "Rename", "Skip directory", "Auto-rename", "Cancel"
        };

        int reply = JOptionPane.showOptionDialog(parentComponent,
                "Could not create directory \"" + dirname + "\" in:\n    \"" +
                parentDirectory.getAbsolutePath() + "\"", "Error",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE,
                null, options, options[0]);

        switch(reply) {
            case 0:
                return CreateDirectoryFailedAction.RENAME;
            case 1:
                return CreateDirectoryFailedAction.SKIP_DIRECTORY;
            case 2:
                return CreateDirectoryFailedAction.AUTO_RENAME;
            default:
                return CreateDirectoryFailedAction.CANCEL;
        }
    }

    /**
     * Default Swing implementation of a "Failed to create file" user prompt.
     * <br>This method will never return <code>null</code>.
     *
     * @param parentComponent the parent component of the user prompt dialog
     * box.
     * @param filename the name of the file for which this prompt was triggered.
     * @param parentDirectory the parent directory of the file for which this
     * prompt was triggered.
     * @return one of RENAME, SKIP_FILE, SKIP_DIRECTORY, AUTO_RENAME or CANCEL.
     */
    public static CreateFileFailedAction createFileFailed(
            Component parentComponent, String filename, File parentDirectory) {
        String[] options = new String[] {
            "Rename", "Skip file", "Skip directory", "Auto-rename", "Cancel"
        };

        int reply = JOptionPane.showOptionDialog(parentComponent,
                "Could not create file \"" + filename + "\" in:\n    \"" +
                parentDirectory.getAbsolutePath() + "\"", "Error",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE,
                null, options, options[0]);

        switch(reply) {
            case 0:
                return CreateFileFailedAction.RENAME;
            case 1:
                return CreateFileFailedAction.SKIP_FILE;
            case 2:
                return CreateFileFailedAction.SKIP_DIRECTORY;
            case 3:
                return CreateFileFailedAction.AUTO_RENAME;
            default:
                return CreateFileFailedAction.CANCEL;
        }
    }

    /**
     * Default Swing implementation of a "Directory exists" user prompt.<br>
     * This method will never return null.
     *
     * @param parentComponent the parent component of the user prompt dialog
     * box.
     * @param directory the directory for which this prompt was triggered.
     * @return one of {@link DirectoryExistsAction#CONTINUE CONTINUE},
     *     {@link DirectoryExistsAction#ALWAYS_CONTINUE ALWAYS_CONTINUE},
     *     {@link DirectoryExistsAction#SKIP_DIRECTORY SKIP_DIRECTORY},
     *     {@link DirectoryExistsAction#RENAME RENAME},
     *     {@link DirectoryExistsAction#AUTO_RENAME AUTO_RENAME} or
     *     {@link DirectoryExistsAction#CANCEL CANCEL}.
     */
    public static DirectoryExistsAction directoryExists(
            Component parentComponent, File directory) {
        String[] options = new String[] {
            "Continue",
            "Always continue",
            "Rename",
            "Skip directory",
            "Auto-rename",
            "Cancel"
        };

        int reply = JOptionPane.showOptionDialog(parentComponent,
                "Directory:\n    \"" + directory.getAbsolutePath() + "\"\n" +
                "already exists.", "Warning",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
                null, options, options[0]);

        switch(reply) {
            case 0:
                return DirectoryExistsAction.CONTINUE;
            case 1:
                return DirectoryExistsAction.ALWAYS_CONTINUE;
            case 2:
                return DirectoryExistsAction.RENAME;
            case 3:
                return DirectoryExistsAction.SKIP_DIRECTORY;
            case 4:
                return DirectoryExistsAction.AUTO_RENAME;
            default:
                return DirectoryExistsAction.CANCEL;
        }
    }

    /**
     * Default Swing implementation of a "File exists" user prompt.<br>
     * This method will never return null.
     *
     * @param parentComponent the parent component of the user prompt dialog
     * box.
     * @param file the file for which this prompt was triggered.
     * @return one of OVERWRITE, OVERWRITE_ALL, RENAME, SKIP_FILE,
     * SKIP_DIRECTORY, AUTO_RENAME or CANCEL.
     */
    public static FileExistsAction fileExists(Component parentComponent,
            File file) {
        String[] options = new String[] {
            "Overwrite",
            "Overwrite all",
            "Rename",
            "Skip file",
            "Skip directory",
            "Auto-rename",
            "Cancel",
        };

        int reply = JOptionPane.showOptionDialog(parentComponent,
                "File:\n    \"" + file.getAbsolutePath() + "\"\nalready " +
                "exists.", "Warning", JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE, null, options, options[0]);

        switch(reply) {
            case 0:
                return FileExistsAction.OVERWRITE;
            case 1:
                return FileExistsAction.OVERWRITE_ALL;
            case 2:
                return FileExistsAction.RENAME;
            case 3:
                return FileExistsAction.SKIP_FILE;
            case 4:
                return FileExistsAction.SKIP_DIRECTORY;
            case 5:
                return FileExistsAction.AUTO_RENAME;
            default:
                return FileExistsAction.CANCEL;
        }
    }

    /**
     * Default Swing implementation of an unhandled exception prompt.<br>
     * This method will never return null.
     *
     * @param parentComponent the parent component of the user prompt dialog
     * box.
     * @param filename the name of the file that is currently being extracted.
     * @param t the unhandled exception.
     * @param actionDescription
     *      a description of the ongoing action, e.g. "extracting file".
     * @return one of CONTINUE, ALWAYS_CONTINUE or ABORT.
     */
    public static UnhandledExceptionAction unhandledException(
            final Component parentComponent,
            final String filename,
            final Throwable t,
            final String actionDescription)
    {
        String[] options = new String[] {
            "Continue",
            "Always continue",
            "Abort",
        };

        String message =
                "An exception occurred while " + actionDescription + "!";

        /* When printing just one exception backtrace, make sure that it's not
         * the InvocationTargetException of a method invoked through reflection.
         * The real underlying exception cause is more useful. */
        final Throwable tPrintable =
                (t instanceof InvocationTargetException) ?
                ((InvocationTargetException) t).getCause() : t;
        message += "\n  " + tPrintable.toString();
        for(StackTraceElement ste : tPrintable.getStackTrace()) {
            message += "\n    " + ste.toString();
        }

        message += "\n\nThe file has not been completely extracted.";
        message += "\nDo you want to continue with the extraction?";

        int reply = JOptionPane.showOptionDialog(parentComponent,
                message,
                "Error",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.ERROR_MESSAGE,
                null,
                options,
                options[2]);

        UnhandledExceptionAction ret;
        switch(reply) {
            case 0:
                ret = UnhandledExceptionAction.CONTINUE;
                break;
            case 1:
                ret = UnhandledExceptionAction.ALWAYS_CONTINUE;
                break;
            default:
                ret = UnhandledExceptionAction.ABORT;
                break;
        }

        return ret;
    }

    /**
     * Default Swing implementation of an error message dialog.
     *
     * @param parentComponent the parent component of the user prompt dialog
     * box.
     * @param message the error message.
     */
    public static void errorMessage(Component parentComponent, String message) {
        JOptionPane.showMessageDialog(parentComponent, message, "Error",
                JOptionPane.ERROR_MESSAGE);

    }

    /**
     * Default Swing implementation of a rename file prompt.<br>
     * If the user aborted the prompt, this method will return
     * <code>null</code>. Otherwise, it will return the string that the user
     * typed.
     *
     * @param parentComponent the parent component of the user prompt dialog
     * box.
     * @param currentName the current name of the file.
     * @param outDir the directory where the file is to be located.
     * @return a new file name, or <code>null</code> if the user canceled the
     * dialog.
     */
    public static String displayRenamePrompt(Component parentComponent,
            String currentName, File outDir) {
        Object selection = JOptionPane.showInputDialog(parentComponent,
                "Enter new name:", "Rename", JOptionPane.PLAIN_MESSAGE, null,
                null, currentName);

        if(selection != null)
            return selection.toString();
        else
            return null;
    }

    /**
     * Default Swing implementation of a confirmation prompt on I/O error.<br>
     * Returns <code>true</code> if extraction should continue and
     * <code>false</code> if it should be aborted.
     *
     * @param parentComponent the parent component of the user prompt dialog
     * box.
     * @param fileName the name of the file that was being extracted.
     * @param outDir the directory where the file was being written.
     * @param ioe the I/O exception that occurred.
     * @return a new file name, or <code>null</code> if the user canceled the
     * dialog.
     */
    public static boolean displayIoErrorPrompt(Component parentComponent,
            String fileName, File outDir, IOException ioe)
    {
        String exceptionMessage = ioe.getMessage();
        int reply = JOptionPane.showConfirmDialog(parentComponent,
                "Encountered an I/O exception while attempting to write to " +
                "file \"" + fileName + "\" in folder:\n" +
                "  " + outDir.getAbsolutePath() + "\n" +
                (exceptionMessage != null ? "System message: " +
                "\"" + exceptionMessage + "\"\n" : "") +
                "Do you want to continue?",
                "I/O Error",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE);
        return (reply != JOptionPane.NO_OPTION);
    }
}
