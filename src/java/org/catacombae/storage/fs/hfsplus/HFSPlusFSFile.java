/*-
 * Copyright (C) 2014 Erik Larsson
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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.catacombae.hfs.types.decmpfs.DecmpfsHeader;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogFileRecord;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogLeafRecord;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.storage.fs.FSFork;
import org.catacombae.storage.fs.hfscommon.HFSCommonFSFile;
import org.catacombae.storage.fs.hfscommon.HFSCommonFileSystemHandler;
import org.catacombae.util.Util;

/**
 * @author <a href="http://www.catacombae.org/" target="_top">Erik Larsson</a>
 */
public class HFSPlusFSFile extends HFSCommonFSFile {
    private static final boolean DEBUG = Util.booleanEnabledByProperties(false,
            "org.catacombae.debug",
            "org.catacombae.storage.debug",
            "org.catacombae.storage.fs.debug",
            "org.catacombae.storage.fs.hfsplus.debug",
            "org.catacombae.storage.fs.hfsplus." +
            HFSPlusFSFile.class.getSimpleName() + ".debug");

    private FSFork dataFork = null;

    HFSPlusFSFile(HFSCommonFileSystemHandler parentHandler,
            CommonHFSCatalogFileRecord fileRecord)
    {
        super(parentHandler, fileRecord);
    }

    HFSPlusFSFile(HFSCommonFileSystemHandler parentHandler,
            CommonHFSCatalogLeafRecord hardLinkRecord,
            CommonHFSCatalogFileRecord fileRecord)
    {
        super(parentHandler, hardLinkRecord, fileRecord);
    }

    @Override
    protected void fillAttributeForks(List<FSFork> forkList) {
        LinkedList<FSFork> attributeForkList = new LinkedList<FSFork>();
        super.fillAttributeForks(attributeForkList);

        final boolean isCompressed =
                getDataFork() instanceof HFSPlusCompressedDataFork;

        Iterator<FSFork> it = attributeForkList.iterator();
        while(it.hasNext()) {
            FSFork curFork = it.next();
            if(isCompressed && curFork.hasXattrName() &&
                    curFork.getXattrName().equals("com.apple.decmpfs"))
            {
                it.remove();
            }
        }

        forkList.addAll(attributeForkList);
    }

    @Override
    protected FSFork getDataFork() {
        if(DEBUG) {
            System.err.println("getDataFork(): Entering...");
        }

        if(dataFork == null) {
            LinkedList<FSFork> attributeForkList = new LinkedList<FSFork>();

            /* Note: Need to call super's implementation because this class
             *       overrides fillAttributeForks to call back into
             *       getDataFork() in order to determine if we should filter out
             *       the "com.apple.decmpfs" attribute fork. */
            super.fillAttributeForks(attributeForkList);

            for(FSFork f : attributeForkList) {
                if(DEBUG) {
                    System.err.println("getDataFork: Checking out attribute " +
                            "fork " + f + (f.hasXattrName() ? " with xattr " +
                            "name \"" + f.getXattrName() + "\"" : "") + ".");
                }

                if(f.hasXattrName() &&
                        f.getXattrName().equals("com.apple.decmpfs"))
                {
                    byte[] headerData = new byte[DecmpfsHeader.STRUCTSIZE];

                    ReadableRandomAccessStream forkStream =
                            f.getReadableRandomAccessStream();
                    try {
                        forkStream.readFully(headerData);
                    } finally {
                        forkStream.close();
                    }

                    DecmpfsHeader header = new DecmpfsHeader(headerData, 0);
                    if(header.getMagic() != DecmpfsHeader.MAGIC) {
                        /* If magic doesn't match, the decmpfs fork is broken
                         * and we treat this attribute fork as a normal extended
                         * attribute for data recovery purposes. */
                        continue;
                    }

                    switch(header.getRawCompressionType()) {
                        case DecmpfsHeader.COMPRESSION_TYPE_INLINE:
                        case DecmpfsHeader.COMPRESSION_TYPE_RESOURCE:
                            break;
                        default:
                            /* No support for other compression types than type
                             * "inline" (3) and "resource" (4) at this point.
                             * All other compression types will lead to the
                             * attribute being exposed as-is for recovery
                             * purposes. */
                            continue;
                    }

                    dataFork =
                            new HFSPlusCompressedDataFork(f, getResourceFork());
                    break;
                }
            }

            if(dataFork == null) {
                /* We haven't created any compressed data fork when going
                 * through the attributes, so this is a regular data fork. Just
                 * call super. */
                dataFork = super.getDataFork();
            }
        }

        return dataFork;
    }
}
