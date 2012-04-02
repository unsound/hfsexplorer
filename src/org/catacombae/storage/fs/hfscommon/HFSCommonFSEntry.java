/*-
 * Copyright (C) 2009 Erik Larsson
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

package org.catacombae.storage.fs.hfscommon;

import org.catacombae.util.Util;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogAttributes;
import org.catacombae.hfs.types.hfscommon.CommonHFSFinderInfo;
import org.catacombae.storage.fs.BasicFSEntry;
import org.catacombae.storage.fs.FSFork;
import org.catacombae.storage.fs.FSForkType;

/**
 *
 * @author erik
 */
public abstract class HFSCommonFSEntry extends BasicFSEntry {

    protected final HFSCommonFileSystemHandler fsHandler;
    protected final CommonHFSCatalogAttributes catalogAttributes;
    private FSFork finderInfoFork = null;
    private boolean finderInfoForkLoaded = false;

    protected HFSCommonFSEntry(HFSCommonFileSystemHandler parentFileSystem,
            CommonHFSCatalogAttributes catalogAttributes) {
        super(parentFileSystem);

        this.fsHandler = parentFileSystem;
        this.catalogAttributes = catalogAttributes;
    }

    HFSCommonFileSystemHandler getFileSystemHandler() {
        return fsHandler;
    }

    /* @Override */
    public FSFork[] getAllForks() {
        FSFork fork = getFinderInfoFork();
        if(fork != null)
            return new FSFork[] { fork };
        else
            return new FSFork[0];
    }

    /* @Override */
    public FSFork getForkByType(FSForkType type) {

        if(type == FSForkType.MACOS_FINDERINFO)
            return getFinderInfoFork();
        else
            return null;
    }

    /* @Override */
    public long getCombinedLength() {
        
        FSFork fork = getFinderInfoFork();
        if(fork != null)
            return finderInfoFork.getLength();
        else
            return 0;
    }

    public FSFork getFinderInfoFork() {
        if(!finderInfoForkLoaded) {
            CommonHFSFinderInfo finderInfo = catalogAttributes.getFinderInfo();
            byte[] finderInfoBytes = finderInfo.getBytes();

            if(!Util.zeroed(finderInfoBytes)) {
                finderInfoFork = new HFSCommonFinderInfoFork(finderInfo);
            }
            else
                finderInfoFork = null;
            finderInfoForkLoaded = true;
        }

        return finderInfoFork;
    }
}
