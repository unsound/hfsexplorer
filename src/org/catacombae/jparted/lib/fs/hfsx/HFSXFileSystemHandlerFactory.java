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

package org.catacombae.jparted.lib.fs.hfsx;

import org.catacombae.jparted.lib.DataLocator;
import org.catacombae.jparted.lib.fs.DefaultFileSystemHandlerInfo;
import org.catacombae.jparted.lib.fs.FileSystemHandler;
import org.catacombae.jparted.lib.fs.FileSystemHandlerFactory;
import org.catacombae.jparted.lib.fs.FileSystemHandlerInfo;
import org.catacombae.jparted.lib.fs.FileSystemRecognizer;
import org.catacombae.jparted.lib.fs.hfsplus.HFSPlusFileSystemHandlerFactory;

/**
 *
 * @author erik
 */
public class HFSXFileSystemHandlerFactory extends HFSPlusFileSystemHandlerFactory {
    private static final FileSystemRecognizer recognizer = new HFSXFileSystemRecognizer();

    private static final FileSystemHandlerInfo handlerInfo =
            new DefaultFileSystemHandlerInfo("HFSX file system handler", "1.0",
            0, "Erik Larsson, Catacombae Software");
    
    @Override
    protected FileSystemHandler createHandlerInternal(DataLocator data,
            boolean useCaching, boolean composeFilename) {
        return new HFSXFileSystemHandler(data, useCaching, composeFilename);
    }
    
    @Override
    public FileSystemHandlerInfo getHandlerInfo() {
        return handlerInfo;
    }
    
    @Override
    public FileSystemHandlerFactory newInstance() {
        return new HFSXFileSystemHandlerFactory();
    }

    @Override
    public FileSystemRecognizer getRecognizer() {
        return recognizer;
    }
}
