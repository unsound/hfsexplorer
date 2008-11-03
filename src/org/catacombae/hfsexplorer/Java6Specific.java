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
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * This class should encapsulate all of the logic in HFSExplorer that is
 * Java 6-specific. I.E. when compiling the source code using a JDK 1.5, the
 * only class that should fail to compile would be this one.
 * 
 * @author <a href="http://hem.bredband.net/catacombae">Erik Larsson</a>
 */
public class Java6Specific {
    /**
     * Checks whether openFile can be invoked for this platform. (Internally,
     * checks whether the Java 6 operation Desktop.open(..) is supported for the
     * currently running platform.<br>
     * <b>Invoking this method on a non-Java 6 JRE will cause a class loading
     * exception.</b>
     * 
     * @return whether openFile can be invoked for this platform.
     */
    public static boolean canOpen() {
	return Desktop.getDesktop().isSupported(Desktop.Action.OPEN);
    }
    
    /**
     * Sends an OS signal via Java6's Desktop.open() method to open the
     * specified file with its default handler.<br>
     * <b>Invoking this method on a non-Java 6 JRE will cause a class loading
     * exception.</b>
     * 
     * @param f the file to open.
     * @throws java.io.IOException if the file could not be opened.
     */
    public static void openFile(File f) throws IOException {
	Desktop.getDesktop().open(f);
    }
    
    /**
     * Sets the icon images for the specified Window. Java 6 supports icon
     * images of multiple sizes to better adapt across platforms.<br>
     * <b>Invoking this method on a non-Java 6 JRE will cause a class loading
     * exception.</b>
     * 
     * @param icons the different sizes of icon images that should be displayed
     * for the window.
     * @param window the window that the icons should be applied to.
     */
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
     * @param table
     * @param tableModel
     * @param defaultSortColumn the column on which to sort on by default.
     * @param columnComparators
     */
    public static void addRowSorter(JTable table, DefaultTableModel tableModel,
            int defaultSortColumn, List<Comparator<?>> columnComparators) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(tableModel);
        int i = 0;
        for(Comparator<?> c : columnComparators) {
            if(c != null)
                sorter.setComparator(i, c);
            ++i;
        }
        sorter.toggleSortOrder(defaultSortColumn);
        table.setRowSorter(sorter);
    }
}
