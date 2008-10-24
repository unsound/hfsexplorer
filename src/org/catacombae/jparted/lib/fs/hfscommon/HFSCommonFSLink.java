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

package org.catacombae.jparted.lib.fs.hfscommon;

import org.catacombae.hfsexplorer.Util;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogFileRecord;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.jparted.lib.fs.FSAttributes;
import org.catacombae.jparted.lib.fs.FSEntry;
import org.catacombae.jparted.lib.fs.FSFolder;
import org.catacombae.jparted.lib.fs.FSLink;

/**
 *
 * @author erik
 */
public class HFSCommonFSLink extends FSLink {
    private CommonHFSCatalogFileRecord linkRecord;
    private HFSCommonFileSystemHandler fsHandler;

    public HFSCommonFSLink(HFSCommonFileSystemHandler fsHandler,
            CommonHFSCatalogFileRecord linkRecord) {
        super(fsHandler);

        this.fsHandler = fsHandler;
        this.linkRecord = linkRecord;

        if(!linkRecord.getData().isSymbolicLink())
            throw new IllegalArgumentException("linkRecord is no symbolic link!");
    }

    public String getLinkTargetPosixPath() {
        // Read the data associated with the link.
        ReadableRandomAccessStream linkDataStream =
                fsHandler.getReadableDataForkStream(linkRecord);
        byte[] linkBytes = Util.readFully(linkDataStream);
        linkDataStream.close();

        return Util.readString(linkBytes, "UTF-8");
    }
    /*
    String[] getLinkTargetPath() {
        // Read the data associated with the link.
        ReadableRandomAccessStream linkDataStream =
                fsHandler.getReadableDataForkStream(linkRecord);
        byte[] linkBytes = new byte[(int)linkDataStream.length()];
        linkDataStream.readFully(linkBytes);
        linkDataStream.close();

        return HFSCommonFileSystemHandler.splitPOSIXUTF8Path(linkBytes);
    }
     * */

    @Override
    public FSEntry getLinkTarget() {
        return fsHandler.getEntryByPosixPath(getLinkTargetPosixPath(),
                fsHandler.lookupParentFolder(linkRecord));
    }

    @Override
    public FSAttributes getAttributes() {
        return new HFSCommonFSAttributes(this, linkRecord.getData());
    }

    @Override
    public String getName() {
        return fsHandler.getProperNodeName(linkRecord);
    }

    @Override
    public FSFolder getParent() {
        return fsHandler.lookupParentFolder(linkRecord);
    }

    public CommonHFSCatalogFileRecord getInternalCatalogFileRecord() {
        return linkRecord;
    }

    @Override
    public String getLinkTargetString() {
        return getLinkTargetPosixPath();
    }
}
