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

package org.catacombae.jparted.lib.fs.hfs;

import org.catacombae.jparted.lib.DataLocator;
import org.catacombae.jparted.lib.fs.DefaultFileSystemHandlerInfo;
import org.catacombae.jparted.lib.fs.FileSystemHandler;
import org.catacombae.jparted.lib.fs.FileSystemHandlerFactory;
import org.catacombae.jparted.lib.fs.FileSystemHandlerInfo;
import org.catacombae.jparted.lib.fs.FileSystemRecognizer;

/**
 *
 * @author erik
 */
public class HFSFileSystemHandlerFactory extends FileSystemHandlerFactory {
    private static final FileSystemRecognizer recognizer = new HFSFileSystemRecognizer();

    private static final FileSystemHandlerInfo handlerInfo =
            new DefaultFileSystemHandlerInfo("HFS file system handler", "1.0",
            0, "Erik Larsson, Catacombae Software");


    private static final CustomAttribute stringEncodingAttribute =
            createCustomAttribute(AttributeType.STRING, "HFS_STRING_ENCODING",
            "The string encoding for filenames in the current HFS file system",
            "MacRoman");


    public FileSystemHandler createHandler(DataLocator data) {
        boolean useCaching =
                createAttributes.getBooleanAttribute(StandardAttribute.CACHING_ENABLED);
        String encoding =
                createAttributes.getStringAttribute(stringEncodingAttribute);

        return createHandlerInternal(data, useCaching, encoding);
    }

    protected FileSystemHandler createHandlerInternal(DataLocator data,
            boolean useCaching, String encoding) {
        return new HFSFileSystemHandler(data, useCaching, encoding);
    }

    public FileSystemHandlerInfo getHandlerInfo() {
        return handlerInfo;
    }

    public StandardAttribute[] getSupportedStandardAttributes() {
        // Set default values for standard attributes
        setStandardAttributeDefaultValue(StandardAttribute.CACHING_ENABLED, true);

        return new StandardAttribute[] { StandardAttribute.CACHING_ENABLED };
    }

    public CustomAttribute[] getSupportedCustomAttributes() {
        return new CustomAttribute[] {
            stringEncodingAttribute
        };
    }

    @Override
    public FileSystemHandlerFactory newInstance() {
        return new HFSFileSystemHandlerFactory();
    }

    @Override
    public FileSystemRecognizer getRecognizer() {
        return recognizer;
    }
}