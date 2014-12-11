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

package org.catacombae.storage.fs.hfscommon;

import java.util.List;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogFile;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogFileRecord;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogLeafRecord;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogNodeID;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.storage.fs.FSAttributes;
import org.catacombae.storage.fs.FSFile;
import org.catacombae.storage.fs.FSFork;
import org.catacombae.storage.fs.FSForkType;

/**
 * @author <a href="http://www.catacombae.org/" target="_top">Erik Larsson</a>
 */
public class HFSCommonFSFile extends HFSCommonFSEntry implements FSFile {
    /**
     * The record from which this file was referenced. In the case of a
     * non-hardlinked file, this variable is equal to <code>fileRecord</code>.
     * The key record supplies the name/location of the file, but all other data
     * is taken from <code>fileRecord</code>.
     */
    private final CommonHFSCatalogLeafRecord keyRecord;

    /**
     * The file record, from which file data and attributes are retrieved. Could
     * be called the 'inode' although it's not really proper in regard to the
     * structure of HFS.
     */
    private final CommonHFSCatalogFileRecord fileRecord;

    private final HFSCommonFSAttributes attributes;
    private final FSFork rawDataFork;
    private final FSFork resourceFork;

    protected HFSCommonFSFile(HFSCommonFileSystemHandler iParent,
            CommonHFSCatalogFileRecord iFileRecord)
    {
        this(iParent, null, iFileRecord);
    }

    protected HFSCommonFSFile(HFSCommonFileSystemHandler iParent,
            CommonHFSCatalogLeafRecord iHardLinkRecord,
            CommonHFSCatalogFileRecord iFileRecord)
    {
        super(iParent, iFileRecord.getData());

        // Input check
        if(iParent == null)
            throw new IllegalArgumentException("iParent must not be null!");
        if(iFileRecord == null)
            throw new IllegalArgumentException("iFileRecord must not be null!");

        this.fileRecord = iFileRecord;
        if(iHardLinkRecord != null)
            this.keyRecord = iHardLinkRecord;
        else
            this.keyRecord = iFileRecord;
        CommonHFSCatalogFile catalogFile = fileRecord.getData();
        this.attributes = new HFSCommonFSAttributes(this, catalogFile);
        this.rawDataFork =
                new HFSCommonFSFork(this, FSForkType.DATA,
                        catalogFile.getDataFork());
        this.resourceFork = new HFSCommonFSFork(this, FSForkType.MACOS_RESOURCE, catalogFile.getResourceFork());
    }

    /* @Override */
    public FSAttributes getAttributes() {
        return attributes;
    }

    /* @Override */
    public String getName() {
        return fsHandler.getProperNodeName(keyRecord);
    }

    /*
    @Override
    public FSFolder getParent() {
        return parent.lookupParentFolder(keyRecord);
    }
     * */

    /* @Override */
    public FSFork getMainFork() {
        return getForkByType(FSForkType.DATA);
    }

    @Override
    protected void fillForks(List<FSFork> forkList) {
        forkList.add(getDataFork());
        super.fillForks(forkList);

        /*
         * TODO: Remove duplicates, in case we are overriding a fork.
         * (...which we are not, so this is unneccessary at this point.)
         */
    }

    @Override
    public FSFork getForkByType(FSForkType type) {
        switch (type) {
            case DATA:
                return getDataFork();
            case MACOS_RESOURCE:
                return resourceFork;
            default:
                return super.getForkByType(type);
        }
    }

    @Override
    public long getCombinedLength() {
        return super.getCombinedLength() + getDataFork().getLength() +
                resourceFork.getLength();
    }

    /* @Override */
    protected CommonHFSCatalogNodeID getCatalogNodeID() {
        return fileRecord.getData().getFileID();
    }

    protected FSFork getDataFork() {
        return rawDataFork;
    }

    /* @Override */
    protected FSFork getResourceFork() {
        return resourceFork.getLength() > 0 ? resourceFork : null;
    }

    ReadableRandomAccessStream getReadableDataForkStream() {
        return fsHandler.getReadableDataForkStream(fileRecord);
    }

    ReadableRandomAccessStream getReadableResourceForkStream() {
        return fsHandler.getReadableResourceForkStream(fileRecord);
    }

    public CommonHFSCatalogFile getInternalCatalogFile() {
        return fileRecord.getData();
    }
}
