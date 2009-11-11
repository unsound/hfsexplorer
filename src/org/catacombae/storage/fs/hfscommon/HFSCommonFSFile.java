/*-
 * Copyright (C) 2008-2009 Erik Larsson
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

import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogFile;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogFileRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogLeafRecord;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.storage.fs.FSAttributes;
import org.catacombae.storage.fs.FSFile;
import org.catacombae.storage.fs.FSFork;
import org.catacombae.storage.fs.FSForkType;

/**
 *
 * @author Erik Larsson
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
    private final FSFork dataFork;
    private final FSFork resourceFork;
    
    HFSCommonFSFile(HFSCommonFileSystemHandler iParent, CommonHFSCatalogFileRecord iFileRecord) {
        this(iParent, null, iFileRecord);
    }
    
    HFSCommonFSFile(HFSCommonFileSystemHandler iParent, CommonHFSCatalogLeafRecord iHardLinkRecord, CommonHFSCatalogFileRecord iFileRecord) {
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
        this.dataFork = new HFSCommonFSFork(this, FSForkType.DATA, catalogFile.getDataFork());
        this.resourceFork = new HFSCommonFSFork(this, FSForkType.MACOS_RESOURCE, catalogFile.getResourceFork());
    }
    
    @Override
    public FSAttributes getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return fsHandler.getProperNodeName(keyRecord);
    }

    /*
    @Override
    public FSFolder getParent() {
        return parent.lookupParentFolder(keyRecord);
    }
     * */

    @Override
    public FSFork getMainFork() {
        return getForkByType(FSForkType.DATA);
    }

    @Override
    public FSFork[] getAllForks() {
        FSFork[] superForks = super.getAllForks();

        boolean hasResourceFork = resourceFork.getLength() > 0;
        int numForks = superForks.length + 1;
        if(hasResourceFork)
            ++numForks;

        FSFork[] res = new FSFork[numForks];
        System.arraycopy(superForks, 0, res, 0, superForks.length);
        res[superForks.length] = dataFork;
        if(hasResourceFork)
            res[superForks.length+1] = resourceFork;

        /*
         * TODO: Remove duplicates, in case we are overriding a fork.
         * (...which we are not, so this is unneccessary at this point.)
         */

        return res;
    }

    @Override
    public FSFork getForkByType(FSForkType type) {
        switch (type) {
            case DATA:
                return dataFork;
            case MACOS_RESOURCE:
                return resourceFork;
            default:
                return super.getForkByType(type);
        }
    }

    @Override
    public long getCombinedLength() {
        return super.getCombinedLength() + dataFork.getLength() +
                resourceFork.getLength();
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
