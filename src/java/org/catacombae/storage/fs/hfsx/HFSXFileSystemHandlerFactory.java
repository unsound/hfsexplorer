/*-
 * Copyright (C) 2008-2014 Erik Larsson
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

package org.catacombae.storage.fs.hfsx;

import org.catacombae.storage.io.DataLocator;
import org.catacombae.storage.fs.DefaultFileSystemHandlerInfo;
import org.catacombae.storage.fs.FileSystemCapability;
import org.catacombae.storage.fs.FileSystemHandler;
import org.catacombae.storage.fs.FileSystemHandlerFactory;
import org.catacombae.storage.fs.FileSystemHandlerInfo;
import org.catacombae.storage.fs.FileSystemRecognizer;
import org.catacombae.storage.fs.hfsplus.HFSPlusFileSystemHandlerFactory;

/**
 * @author <a href="http://www.catacombae.org/" target="_top">Erik Larsson</a>
 */
public class HFSXFileSystemHandlerFactory extends HFSPlusFileSystemHandlerFactory {
    private static final FileSystemRecognizer recognizer = new HFSXFileSystemRecognizer();

    private static final FileSystemHandlerInfo handlerInfo =
            new DefaultFileSystemHandlerInfo("org.catacombae.hfsx_handler",
            "HFSX file system handler", "1.0", 0, "Erik Larsson, Catacombae Software");

    @Override
    public FileSystemCapability[] getCapabilities() {
        return HFSXFileSystemHandler.getStaticCapabilities();
    }

    @Override
    protected FileSystemHandler createHandlerInternal(DataLocator data,
            boolean useCaching, boolean posixFilenames, boolean composeFilename,
            boolean hideProtected)
    {
        return new HFSXFileSystemHandler(data, useCaching, posixFilenames,
                composeFilename, hideProtected);
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
