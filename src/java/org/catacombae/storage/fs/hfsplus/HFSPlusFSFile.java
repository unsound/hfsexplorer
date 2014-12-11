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
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogFileRecord;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogLeafRecord;
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

        Iterator<FSFork> it = attributeForkList.iterator();
        while(it.hasNext()) {
            FSFork curFork = it.next();
            if(curFork.hasXattrName() &&
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

        LinkedList<FSFork> attributeForkList = new LinkedList<FSFork>();

        /* Note: Need to call super's implementation because this class
         *       overrides fillAttributeForks to filter out the
         *       "com.apple.decmpfs" attribute fork. */
        super.fillAttributeForks(attributeForkList);

        for(FSFork f : attributeForkList) {
            if(DEBUG) {
                System.err.println("getDataFork: Checking out attribute fork " +
                        f + (f.hasXattrName() ? " with xattr name \"" +
                                f.getXattrName() + "\"" : "") +
                        ".");
            }

            if(f.hasXattrName() && f.getXattrName().equals("com.apple.decmpfs"))
            {
                return new HFSPlusCompressedDataFork(f, getResourceFork());
            }
        }

        return super.getDataFork();
    }
}
