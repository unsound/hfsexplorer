/*-
 * Copyright (C) 2006 Erik Larsson
 * 
 * All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 */

package org.catacombae.hfsexplorer;

import java.util.*;
import java.io.*;

public class SimpleFileFilter extends javax.swing.filechooser.FileFilter {

    private Vector<String> extensions;
    private String description;
    
    public SimpleFileFilter() {
	extensions = new Vector<String>();
	description = "";
    }
    public void addExtension(String extension) { extensions.add(extension); }
    public void setDescription(String idescription) { description = idescription; }
    public void removeExtension(String iextension) {
	for(int i = 0; i < extensions.size(); i++) {
	    if(extensions.get(i).equals(iextension))
		extensions.remove(i);
	}
    }
    public boolean accept(File f) {

	if(f.isDirectory()) return true;

	for(int i = 0; i < extensions.size(); i++) {
	    if(f.getName().endsWith(extensions.get(i)))
		return true;
	}
	return false;
    }
    public String getDescription() { return description; }
}
