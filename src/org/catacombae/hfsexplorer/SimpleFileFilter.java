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
import java.util.Vector;
import javax.swing.filechooser.FileFilter;

public class SimpleFileFilter extends FileFilter {
    private Vector<String> extensions;
    private String description;

    public SimpleFileFilter() {
        extensions = new Vector<String>();
        description = "";
    }

    public void addExtension(String extension) {
        extensions.add(extension);
    }

    public void setDescription(String idescription) {
        description = idescription;
    }

    public void removeExtension(String iextension) {
        for(int i = 0; i < extensions.size(); i++) {
            if(extensions.get(i).equals(iextension))
                extensions.remove(i);
        }
    }

    public boolean accept(File f) {

        if(f.isDirectory())
            return true;

        for(int i = 0; i < extensions.size(); i++) {
            if(f.getName().endsWith(extensions.get(i)))
                return true;
        }
        return false;
    }

    public String getDescription() {
        return description;
    }
}
