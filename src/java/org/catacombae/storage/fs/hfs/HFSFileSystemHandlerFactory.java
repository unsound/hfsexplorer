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

package org.catacombae.storage.fs.hfs;

import org.catacombae.storage.io.DataLocator;
import org.catacombae.storage.fs.DefaultFileSystemHandlerInfo;
import org.catacombae.storage.fs.FileSystemCapability;
import org.catacombae.storage.fs.FileSystemHandler;
import org.catacombae.storage.fs.FileSystemHandlerFactory;
import org.catacombae.storage.fs.FileSystemHandlerInfo;
import org.catacombae.storage.fs.FileSystemRecognizer;
import org.catacombae.storage.fs.hfscommon.HFSCommonFileSystemHandlerFactory;
import org.catacombae.util.Util;

/**
 * @author <a href="http://www.catacombae.org/" target="_top">Erik Larsson</a>
 */
public class HFSFileSystemHandlerFactory extends HFSCommonFileSystemHandlerFactory {
    private static final FileSystemRecognizer recognizer = new HFSFileSystemRecognizer();

    private static final FileSystemHandlerInfo handlerInfo =
            new DefaultFileSystemHandlerInfo("org.catacombae.hfs_handler",
            "HFS file system handler", "1.0", 0, "Erik Larsson, Catacombae Software");


    private static final CustomAttribute stringEncodingAttribute =
            createCustomAttribute(AttributeType.STRING, "HFS_STRING_ENCODING",
            "The string encoding for filenames in the current HFS file system",
            "MacRoman");


    public FileSystemCapability[] getCapabilities() {
        return HFSFileSystemHandler.getStaticCapabilities();
    }

    public FileSystemHandler createHandler(DataLocator data) {
        boolean useCaching =
                createAttributes.getBooleanAttribute(StandardAttribute.CACHING_ENABLED);
        boolean posixFilenames =
                createAttributes.getBooleanAttribute(posixFilenamesAttribute);
        boolean sfmSubstitutions =
                createAttributes.getBooleanAttribute(sfmSubstitutionsAttribute);
        String encoding =
                createAttributes.getStringAttribute(stringEncodingAttribute);

        return createHandlerInternal(data, useCaching, posixFilenames,
                sfmSubstitutions, encoding);
    }

    protected FileSystemHandler createHandlerInternal(DataLocator data,
            boolean useCaching, boolean posixFilenames,
            boolean sfmSubstitutions, String encoding)
    {
        return new HFSFileSystemHandler(data, useCaching, posixFilenames,
                sfmSubstitutions, encoding);
    }

    public FileSystemHandlerInfo getHandlerInfo() {
        return handlerInfo;
    }

    public StandardAttribute[] getSupportedStandardAttributes() {
        // Set default values for standard attributes
        setStandardAttributeDefaultValue(StandardAttribute.CACHING_ENABLED, true);

        return new StandardAttribute[] { StandardAttribute.CACHING_ENABLED };
    }

    @Override
    public CustomAttribute[] getSupportedCustomAttributes() {
        final CustomAttribute[] superAttributes =
                super.getSupportedCustomAttributes();
        final CustomAttribute[] result =
                new CustomAttribute[superAttributes.length + 1];

        Util.arrayCopy(superAttributes, result);
        result[superAttributes.length + 0] = stringEncodingAttribute;

        return result;
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
