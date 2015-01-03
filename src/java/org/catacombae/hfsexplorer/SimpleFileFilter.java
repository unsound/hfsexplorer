/*-
 * Copyright (C) 2006 Erik Larsson
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
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.filechooser.FileFilter;

public class SimpleFileFilter extends FileFilter {
    private final LinkedList<String> extensions;
    private String description;

    public SimpleFileFilter() {
        extensions = new LinkedList<String>();
        description = "";
    }

    public void addExtension(String extension) {
        extensions.add(extension);
    }

    public void setDescription(String idescription) {
        description = idescription;
    }

    public void removeExtension(String iextension) {
        final Iterator<String> it = extensions.iterator();
        while(it.hasNext()) {
            if(it.next().equals(iextension)) {
                it.remove();
            }
        }
    }

    public boolean accept(File f) {

        if(f.isDirectory())
            return true;

        for(String curExtension : extensions) {
            if(f.getName().endsWith(curExtension)) {
                return true;
            }
        }

        return false;
    }

    public String getDescription() {
        return description;
    }
}
