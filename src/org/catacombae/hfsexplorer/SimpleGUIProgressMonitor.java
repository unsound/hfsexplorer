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

/**
 *
 * @author Erik Larsson
 */
public class SimpleGUIProgressMonitor extends BasicExtractProgressMonitor {
    private final Component parentComponent;
    
    public SimpleGUIProgressMonitor(Component parentComponent) {
        this.parentComponent = parentComponent;
    }
    
    @Override
    public boolean confirmOverwriteDirectory(File dir) {
        return confirmOverwriteDirectory(parentComponent, dir);
    }
    
    /**
     * A simple default GUI implementation of a confirm dialog for overwriting a directory.
     * 
     * @param parentComponent
     * @param dir
     * @return whether or not the user accepts to overwrite <code>dir</code>.
     */
    public static boolean confirmOverwriteDirectory(Component parentComponent, File dir) {
        String[] options = new String[]{"Continue", "Cancel"};
        int reply = JOptionPane.showOptionDialog(parentComponent, "Warning! Directory:\n    \"" +
                dir.getAbsolutePath() + "\"\n" +
                "already exists. Do you want to continue extracting to this directory?",
                "Warning", JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE, null, options, options[0]);
        return reply == 0;
    }

    @Override
    public boolean confirmSkipDirectory(String... messageLines) {
        return confirmSkipDirectory(parentComponent, messageLines);
    }

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
}
