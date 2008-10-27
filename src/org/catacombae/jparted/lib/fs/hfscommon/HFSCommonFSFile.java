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

import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogFile;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogFileRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogLeafRecord;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.jparted.lib.fs.FSAttributes;
import org.catacombae.jparted.lib.fs.FSFile;
import org.catacombae.jparted.lib.fs.FSFolder;
import org.catacombae.jparted.lib.fs.FSFork;
import org.catacombae.jparted.lib.fs.FSForkType;


/**
 *
 * @author Erik Larsson
 */
public class HFSCommonFSFile extends FSFile {
    private final HFSCommonFileSystemHandler parent;
    private final CommonHFSCatalogLeafRecord keyRecord;
    private final CommonHFSCatalogFileRecord fileRecord;
    private final CommonHFSCatalogFile catalogFile;
    private final HFSCommonFSAttributes attributes;
    private final FSFork dataFork;
    private final FSFork resourceFork;
    
    HFSCommonFSFile(HFSCommonFileSystemHandler iParent, CommonHFSCatalogFileRecord iFileRecord) {
        this(iParent, null, iFileRecord);
    }
    
    HFSCommonFSFile(HFSCommonFileSystemHandler iParent, CommonHFSCatalogLeafRecord iHardLinkRecord, CommonHFSCatalogFileRecord iFileRecord) {
        super(iParent);
        
        // Input check
        if(iParent == null)
            throw new IllegalArgumentException("iParent must not be null!");
        if(iFileRecord == null)
            throw new IllegalArgumentException("iFileRecord must not be null!");
        
        this.parent = iParent;
        this.fileRecord = iFileRecord;
        if(iHardLinkRecord != null)
            this.keyRecord = iHardLinkRecord;
        else
            this.keyRecord = iFileRecord;
        this.catalogFile = fileRecord.getData();
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
        return parent.getProperNodeName(keyRecord);
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
        return new FSFork[] { dataFork, resourceFork };
    }

    @Override
    public FSFork getForkByType(FSForkType type) {
        switch(type) {
            case DATA:
                return dataFork;
            case MACOS_RESOURCE:
                return resourceFork;
            default:
                return null;
        }
    }

    @Override
    public long getCombinedLength() {
        return dataFork.getLength() + resourceFork.getLength();
    }

    ReadableRandomAccessStream getReadableDataForkStream() {
        return parent.getReadableDataForkStream(fileRecord);
    }
    
    ReadableRandomAccessStream getReadableResourceForkStream() {
        return parent.getReadableResourceForkStream(fileRecord);
    }
    
    HFSCommonFileSystemHandler getFileSystemHandler() {
        return parent;
    }
    
    public CommonHFSCatalogFile getInternalCatalogFile() {
        return catalogFile;
    }
}
