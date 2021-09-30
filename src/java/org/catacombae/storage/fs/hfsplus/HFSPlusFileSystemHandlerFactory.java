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

package org.catacombae.storage.fs.hfsplus;

import org.catacombae.hfs.types.hfs.ExtDescriptor;
import org.catacombae.hfs.types.hfs.HFSPlusWrapperMDB;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.storage.io.DataLocator;
import org.catacombae.storage.io.SubDataLocator;
import org.catacombae.storage.fs.DefaultFileSystemHandlerInfo;
import org.catacombae.storage.fs.FileSystemCapability;
import org.catacombae.storage.fs.FileSystemHandler;
import org.catacombae.storage.fs.FileSystemHandlerFactory;
import org.catacombae.storage.fs.FileSystemHandlerInfo;
import org.catacombae.storage.fs.FileSystemRecognizer;
import org.catacombae.storage.fs.hfscommon.HFSCommonFileSystemHandlerFactory;
import org.catacombae.storage.fs.hfscommon.HFSCommonFileSystemRecognizer;
import org.catacombae.storage.fs.hfscommon.HFSCommonFileSystemRecognizer.FileSystemType;
import org.catacombae.util.Log;
import org.catacombae.util.Util;

/**
 * @author <a href="https://catacombae.org" target="_top">Erik Larsson</a>
 */
public class HFSPlusFileSystemHandlerFactory extends HFSCommonFileSystemHandlerFactory {
    private static final FileSystemRecognizer recognizer = new HFSPlusFileSystemRecognizer();

    private static final FileSystemHandlerInfo handlerInfo =
            new DefaultFileSystemHandlerInfo("org.catacombae.hfs_plus_handler",
            "HFS+ file system handler", "1.0", 0, "Erik Larsson, Catacombae Software");


    private static final CustomAttribute compositionEnabledAttribute =
            createCustomAttribute(AttributeType.BOOLEAN, "COMPOSE_UNICODE_FILENAMES",
                    "Decides whether Unicode filenames should be composed or " +
                    "left in their original decomposed form.", true);

    private static final CustomAttribute hideProtectedAttribute =
            createCustomAttribute(AttributeType.BOOLEAN, "HIDE_PROTECTED_FILES",
                    "Decides whether protected files like the inode " +
                    "directories and the journal files should show up in a " +
                    "directory listing.", true);

    private static final Log log =
            Log.getInstance(HFSPlusFileSystemHandlerFactory.class);

    public FileSystemCapability[] getCapabilities() {
        return HFSPlusFileSystemHandler.getStaticCapabilities();
    }

    @Override
    public FileSystemHandler createHandler(DataLocator data) {
        boolean useCaching =
                createAttributes.getBooleanAttribute(StandardAttribute.CACHING_ENABLED);
        boolean posixFilenames =
                createAttributes.getBooleanAttribute(posixFilenamesAttribute);
        boolean sfmSubstitutions =
                createAttributes.getBooleanAttribute(sfmSubstitutionsAttribute);
        boolean composeFilename =
                createAttributes.getBooleanAttribute(compositionEnabledAttribute);
        boolean hideProtected =
                createAttributes.getBooleanAttribute(hideProtectedAttribute);

        ReadableRandomAccessStream recognizerStream = data.createReadOnlyFile();

        final DataLocator dataToLoad;
        try {
            FileSystemType type =
                    HFSCommonFileSystemRecognizer.detectFileSystem(
                    recognizerStream, 0);

            if(type == FileSystemType.HFS_WRAPPED_HFS_PLUS) {
                dataToLoad = hfsUnwrap(data);
            }
            else if(type == FileSystemType.UNKNOWN) {
                throw new RuntimeException("No HFS file system found at " +
                        "'data'.");
            }
            else {
                dataToLoad = data;
            }
        }
        finally {
            recognizerStream.close();
        }

        return createHandlerInternal(dataToLoad, useCaching, posixFilenames,
                sfmSubstitutions, composeFilename, hideProtected);
    }

    protected FileSystemHandler createHandlerInternal(DataLocator data,
            boolean useCaching, boolean posixFilenames,
            boolean sfmSubstitutions, boolean composeFilename,
            boolean hideProtected)
    {
        return new HFSPlusFileSystemHandler(data, useCaching, posixFilenames,
                sfmSubstitutions, composeFilename, hideProtected);
    }

    @Override
    public FileSystemHandlerInfo getHandlerInfo() {
        return handlerInfo;
    }

    @Override
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
                new CustomAttribute[superAttributes.length + 2];

        Util.arrayCopy(superAttributes, result);
        result[superAttributes.length + 0] = compositionEnabledAttribute;
        result[superAttributes.length + 1] = hideProtectedAttribute;

        return result;
    }

    @Override
    public FileSystemHandlerFactory newInstance() {
        return new HFSPlusFileSystemHandlerFactory();
    }

    @Override
    public FileSystemRecognizer getRecognizer() {
        return recognizer;
    }

    /**
     * Unwraps an HFS+ volume wrapped in a HFS container.
     *
     * @param data a locator defining the entire HFS wrapper volume.
     * @return a locator defining only the HFS+ part of the volume.
     */
    private static DataLocator hfsUnwrap(DataLocator data) {
        ReadableRandomAccessStream fsStream = data.createReadOnlyFile();

        log.debug("Found a wrapped HFS+ volume:");
        byte[] mdbData = new byte[HFSPlusWrapperMDB.STRUCTSIZE];
        fsStream.seek(1024);
        fsStream.read(mdbData);
        HFSPlusWrapperMDB mdb = new HFSPlusWrapperMDB(mdbData, 0);
        ExtDescriptor xd = mdb.getDrEmbedExtent();
        int hfsBlockSize = mdb.getDrAlBlkSiz();
        long fsOffset =
                ((long) Util.unsign(mdb.getDrAlBlSt())) * 512 +
                ((long) Util.unsign(xd.getXdrStABN())) * hfsBlockSize;
        long fsLength =
                ((long) Util.unsign(xd.getXdrNumABlks())) * hfsBlockSize;
        log.debug("  fsOffset: " + fsOffset);
        log.debug("  fsLength: " + fsLength);
        // redetect with adjusted fsOffset

        return new SubDataLocator(data, fsOffset, fsLength);
    }
}
