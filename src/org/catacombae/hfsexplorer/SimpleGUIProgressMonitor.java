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
import javax.swing.JOptionPane;
import org.catacombae.hfsexplorer.ExtractProgressMonitor.ExtractProperties;

/**
 *
 * @author Erik Larsson
 */
public class SimpleGUIProgressMonitor extends BasicExtractProgressMonitor {
    private final Component parentComponent;
    private final ExtractProperties extractProperties = new ExtractProperties();
    
    public SimpleGUIProgressMonitor(Component parentComponent) {
        this.parentComponent = parentComponent;
    }
        
    @Override
    public ExtractProperties getExtractProperties() {
        return extractProperties;
    }

    /*
    @Override
    public boolean confirmOverwriteDirectory(File dir) {
        return confirmOverwriteDirectory(parentComponent, dir);
    }
    */
    
    /**
     * A simple default GUI implementation of a confirm dialog for overwriting a directory.
     * 
     * @param parentComponent
     * @param dir
     * @return whether or not the user accepts to overwrite <code>dir</code>.
     */
    /*
    public static boolean confirmOverwriteDirectory(Component parentComponent, File dir) {
        String[] options = new String[]{"Continue", "Cancel"};
        int reply = JOptionPane.showOptionDialog(parentComponent, "Warning! Directory:\n    \"" +
                dir.getAbsolutePath() + "\"\n" +
                "already exists. Do you want to continue extracting to this directory?",
                "Warning", JOptionPane.YES_NO_CANCEL_OPTION,
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
    public static boolean confirmSkipDirectory(Component parentComponent, String... messageLines) {
        StringBuilder sb = new StringBuilder();
        for(String messageLine : messageLines)
            sb.append(messageLine).append("\n");
        
        int reply = JOptionPane.showConfirmDialog(parentComponent, sb.toString() +
                        "Do you want to continue? (All files under this directory will be " +
                        "skipped)", "Error", JOptionPane.YES_NO_OPTION,
                        JOptionPane.ERROR_MESSAGE);
        return reply != JOptionPane.NO_OPTION;
    }
    */

    /**
     * @see #createDirectoryFailed(java.awt.Component, java.io.File)
     */
    @Override
    public CreateDirectoryFailedAction createDirectoryFailed(String dirname, File parentDirectory) {
        return createDirectoryFailed(parentComponent, dirname, parentDirectory);
    }

    /**
     * @see #createFileFailed(java.awt.Component, java.io.File)
     */
    @Override
    public CreateFileFailedAction createFileFailed(String filename, File parentDirectory) {
        return createFileFailed(parentComponent, filename, parentDirectory);
    }

    /**
     * @see #directoryExists(java.awt.Component, java.io.File) 
     */
    @Override
    public DirectoryExistsAction directoryExists(File directory) {
        return directoryExists(parentComponent, directory);
    }
    
    /**
     * @see #fileExists(java.awt.Component, java.io.File)
     */
    @Override
    public FileExistsAction fileExists(File file) {
        return fileExists(parentComponent, file);
    }

    /**
     * @see #displayRenamePrompt(java.awt.Component, java.lang.String, java.io.File) 
     */
    @Override
    public String displayRenamePrompt(String currentName, File outDir) {
        return displayRenamePrompt(parentComponent, currentName, outDir);
    }

    /**
     * Default Swing implementation of a "Failed to create directory" user prompt.<br>
     * This method will never return null.
     * 
     * @param parentComponent the parent component of the user prompt dialog box.
     * @param file the file for which this prompt was triggered.
     * @return one of RENAME, SKIP_DIRECTORY, AUTO_RENAME or CANCEL.
     */
    public static CreateDirectoryFailedAction createDirectoryFailed(Component parentComponent,
            String dirname, File parentDirectory) {
        String[] options = new String[] { "Rename", "Skip directory", "Auto-rename", "Cancel" };
        
        int reply = JOptionPane.showOptionDialog(parentComponent, "Could not create directory \"" + 
                dirname + "\" in:\n    \"" + parentDirectory.getAbsolutePath() + "\"",
                "Error", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, options,
                options[0]);
        
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
     * Default Swing implementation of a "Failed to create file" user prompt.<br>
     * This method will never return null.
     * 
     * @param parentComponent the parent component of the user prompt dialog box.
     * @param file the file for which this prompt was triggered.
     * @return one of RENAME, SKIP_FILE, SKIP_DIRECTORY, AUTO_RENAME or CANCEL.
     */
    public static CreateFileFailedAction createFileFailed(Component parentComponent, String filename,
            File parentDirectory) {
        String[] options = new String[] { "Rename", "Skip file", "Skip directory", "Auto-rename", "Cancel" };
        
        int reply = JOptionPane.showOptionDialog(parentComponent, "Could not create file \"" +
                filename + "\" in:\n    \"" + parentDirectory.getAbsolutePath() + "\"",
                "Error", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null,
                options, options[0]);
        
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
     * @param parentComponent the parent component of the user prompt dialog box.
     * @param directory the directory for which this prompt was triggered.
     * @return one of CONTINUE, SKIP_DIRECTORY, RENAME, AUTO_RENAME or CANCEL.
     */
    public static DirectoryExistsAction directoryExists(Component parentComponent, File directory) {
        String[] options = new String[] { "Continue", "Rename", "Skip directory", "Auto-rename", "Cancel" };
        
        int reply = JOptionPane.showOptionDialog(parentComponent, "Warning! Directory:\n    \"" +
                directory.getAbsolutePath() + "\"\n" +
                "already exists.",
                "Warning", JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE, null, options, options[0]);
        
        switch(reply) {
            case 0:
                return DirectoryExistsAction.CONTINUE;
            case 1:
                return DirectoryExistsAction.RENAME;
            case 2:
                return DirectoryExistsAction.SKIP_DIRECTORY;
            case 3:
                return DirectoryExistsAction.AUTO_RENAME;
            default:
                return DirectoryExistsAction.CANCEL;
        }
    }

    /**
     * Default Swing implementation of a "File exists" user prompt.<br>
     * This method will never return null.
     * 
     * @param parentComponent the parent component of the user prompt dialog box.
     * @param file the file for which this prompt was triggered.
     * @return one of OVERWRITE, OVERWRITE_ALL, RENAME, SKIP_FILE, SKIP_DIRECTORY, AUTO_RENAME or CANCEL.
     */
    public static FileExistsAction fileExists(Component parentComponent, File file) {
        String[] options = new String[] { "Overwrite", "Overwrite all", "Rename", "Skip file", "Skip directory", "Auto-rename", "Cancel" };
        
        int reply = JOptionPane.showOptionDialog(parentComponent, "Warning! File:\n    \"" +
                file.getAbsolutePath() + "\"\n" + "already exists.",
                "Warning", JOptionPane.YES_NO_CANCEL_OPTION,
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
     * Default Swing implementation of a rename file prompt.<br>
     * If the user aborted the prompt, this method will return <code>null</code>. Otherwise, it will
     * return the string that the user typed.
     * 
     * @param parentComponent the parent component of the user prompt dialog box.
     * @param currentName the current name of the file.
     * @param outDir the directory where the file is to be located.
     * @return a new file name, or <code>null</code> if the user canceled the dialog.
     */
    public static String displayRenamePrompt(Component parentComponent, String currentName, File outDir) {
        Object selection = JOptionPane.showInputDialog(parentComponent, "Enter new name:",
                "Rename", JOptionPane.PLAIN_MESSAGE, null, null, currentName);
        
        if(selection != null)
            return selection.toString();
        else
            return null;
    }
}
