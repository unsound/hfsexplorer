/*-
 * Copyright (C) 2008-2021 Erik Larsson
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
import org.catacombae.storage.fs.hfscommon.HFSCommonFileSystemHandler;
import org.catacombae.hfs.original.HFSOriginalVolume;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogFileRecord;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogLeafRecord;

/**
 * @author <a href="https://catacombae.org" target="_top">Erik Larsson</a>
 */
public class HFSFileSystemHandler extends HFSCommonFileSystemHandler {

    public HFSFileSystemHandler(DataLocator fsLocator, boolean useCaching,
            boolean posixNames, boolean sfmSubstitutions, String encodingName)
    {
        super(new HFSOriginalVolume(fsLocator.createReadOnlyFile(), useCaching,
                encodingName), posixNames, sfmSubstitutions, false, false);
    }

    protected boolean shouldHide(CommonHFSCatalogLeafRecord rec) {
        // For HFS we have nothing to hide.
        return false;
    }

    protected Long getLinkCount(CommonHFSCatalogFileRecord fr) {
        // HFS does not support links.
        return null;
    }

    protected String[] getAbsoluteLinkPath(String[] path, int pathLength,
            CommonHFSCatalogFileRecord rec)
    {
        // HFS does not support links.
        return null;
    }

    public void setEncoding(String encodingName) {
        ((HFSOriginalVolume) view).setStringEncoding(encodingName);
    }
}
