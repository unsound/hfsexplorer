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
import javax.swing.ImageIcon;

public class Java6Specific {
    public static boolean isJava6OrHigher() {
	return System.getProperty("java.vm.version").compareTo("1.6") >= 0;
    }
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
}
