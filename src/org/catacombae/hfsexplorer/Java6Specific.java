/*-
 * Copyright (C) 2007 Erik Larsson
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

import java.util.LinkedList;
import java.io.File;
import java.io.IOException;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.Window;
import java.util.Comparator;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class Java6Specific {
    public static boolean canOpen() {
	return Desktop.getDesktop().isSupported(Desktop.Action.OPEN);
    }
    public static void openFile(File f) throws IOException {
	Desktop.getDesktop().open(f);
    }
    public static void setIconImages(ImageIcon[] icons, Window window) {
	LinkedList<Image> iconImages = new LinkedList<Image>();
	for(ImageIcon ii : icons)
	    iconImages.addLast(ii.getImage());
	window.setIconImages(iconImages);
    }
    
    /**
     * Adds a row sorter to <code>table</code> with the specified table model. Optionally, a list of
     * Comparators can be supplied, one for each column, that specify the correct way of comparing
     * the objects in that column. Null values means the default comparator will be used.<br>
     * <b>Only Java 6+ virtual machines will support this, so check first with isJava6OrHigher() or
     * risk to crash your program.</b>
     * 
     * @param fileTable
     * @param tableModel
     * @param columnComparators
     */
    public static void addRowSorter(JTable table, DefaultTableModel tableModel,
            Comparator... columnComparators) {
        TableRowSorter sorter = new TableRowSorter<DefaultTableModel>(tableModel);
        for(int i = 0; i < columnComparators.length; ++i) {
            Comparator c = columnComparators[i];
            if(c != null)
                sorter.setComparator(i, c);
        }
        table.setRowSorter(sorter);
    }
}
