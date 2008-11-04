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

import java.io.File;
import java.util.LinkedList;
import org.catacombae.hfsexplorer.fs.ProgressMonitor;

/**
 *
 * @author Erik
 */
public interface ExtractProgressMonitor extends ProgressMonitor {

    public void updateCalculateDir(String dirname);
    public void updateTotalProgress(double fraction, String message);
    public void updateCurrentDir(String dirname);
    public void updateCurrentFile(String filename, long fileSize);
    public void setDataSize(long totalSize);
    //public boolean confirmOverwriteDirectory(File dir);
    //public boolean confirmSkipDirectory(String... messageLines);
    public CreateDirectoryFailedAction createDirectoryFailed(String dirname, File parentDirectory);
    public CreateFileFailedAction createFileFailed(String filename, File parentDirectory);
    public DirectoryExistsAction directoryExists(File directory);
    public FileExistsAction fileExists(File file);
    public String displayRenamePrompt(String currentName, File outDir);
    public ExtractProperties getExtractProperties();

    public static interface ExtractPropertiesListener {
        public void propertyChanged(Object changedProperty);
    }
    
    public static class ExtractProperties {
        private final LinkedList<ExtractPropertiesListener> listeners =
                new LinkedList<ExtractPropertiesListener>();
        private volatile CreateDirectoryFailedAction createDirAction = CreateDirectoryFailedAction.PROMPT_USER;
        private volatile CreateFileFailedAction createFileAction = CreateFileFailedAction.PROMPT_USER;
        private volatile DirectoryExistsAction dirExistsAction = DirectoryExistsAction.PROMPT_USER;
        private volatile FileExistsAction fileExistsAction = FileExistsAction.PROMPT_USER;
        
        public CreateDirectoryFailedAction getCreateDirectoryFailedAction() {
            return createDirAction;
        }
        
        public CreateFileFailedAction getCreateFileFailedAction() {
            return createFileAction;
        }
        
        public DirectoryExistsAction getDirectoryExistsAction() {
            return dirExistsAction;
        } 
        
        public FileExistsAction getFileExistsAction() {
            return fileExistsAction;
        }
        
        public void setCreateDirectoryFailedAction(CreateDirectoryFailedAction action) {
            createDirAction = action;
            notifyListeners(action);
        }
        
        public void setCreateFileFailedAction(CreateFileFailedAction action) {
            createFileAction = action;
            notifyListeners(action);
        }
        
        public void setDirectoryExistsAction(DirectoryExistsAction action) {
            dirExistsAction = action;
            notifyListeners(action);
        }
        
        public void setFileExistsAction(FileExistsAction action) {
            fileExistsAction = action;
            notifyListeners(action);
        }
        
        public void addListener(ExtractPropertiesListener listener) {
            listeners.addLast(listener);
        }
        
        private void notifyListeners(Object changedProperty) {
            for(ExtractPropertiesListener listener : listeners) {
                try {
                    listener.propertyChanged(changedProperty);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static enum CreateDirectoryFailedAction { PROMPT_USER, SKIP_DIRECTORY, RENAME, AUTO_RENAME, CANCEL }
    public static enum CreateFileFailedAction { PROMPT_USER, SKIP_FILE, SKIP_DIRECTORY, RENAME, AUTO_RENAME, CANCEL }
    public static enum DirectoryExistsAction { PROMPT_USER, CONTINUE, SKIP_DIRECTORY, RENAME, AUTO_RENAME, CANCEL }
    public static enum FileExistsAction { PROMPT_USER, SKIP_FILE, SKIP_DIRECTORY, OVERWRITE, OVERWRITE_ALL, RENAME, AUTO_RENAME, CANCEL }
}
