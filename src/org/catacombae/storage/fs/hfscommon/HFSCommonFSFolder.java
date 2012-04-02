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

import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogFileRecord;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogFolder;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogFolderRecord;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogLeafRecord;
import org.catacombae.storage.fs.FSAttributes;
import org.catacombae.storage.fs.FSEntry;
import org.catacombae.storage.fs.FSFolder;

/**
 *
 * @author Erik Larsson
 */
public class HFSCommonFSFolder extends HFSCommonFSEntry implements FSFolder {
    /**
     * The record from which this file was referenced. In the case of a
     * non-hardlinked directory, this variable is equal to
     * <code>folderRecord</code>.
     * The key record supplies the name/location of the folder, but all other
     * data is taken from <code>folderRecord</code>.
     */
    private final CommonHFSCatalogLeafRecord keyRecord;

    /**
     * The folder record, from which folder data and attributes are retrieved.
     * Could be called the 'inode' although it's not really proper in regard to
     * the structure of HFS.
     */
    private final CommonHFSCatalogFolderRecord folderRecord;

    private final HFSCommonFSAttributes attributes;
    
    public HFSCommonFSFolder(HFSCommonFileSystemHandler iParent, CommonHFSCatalogFolderRecord iFolderRecord) {
        this(iParent, null, iFolderRecord);
    }

    HFSCommonFSFolder(HFSCommonFileSystemHandler iParent, CommonHFSCatalogFileRecord iHardLinkFileRecord, CommonHFSCatalogFolderRecord iFolderRecord) {
        super(iParent, iFolderRecord.getData());

        // Input check
        if(iParent == null)
            throw new IllegalArgumentException("iParent must not be null!");
        if(iFolderRecord == null)
            throw new IllegalArgumentException("iFolderRecord must not be null!");

        if(iHardLinkFileRecord != null)
            this.keyRecord = iHardLinkFileRecord;
        else
            this.keyRecord = iFolderRecord;
        this.folderRecord = iFolderRecord;
        
        this.attributes =
                new HFSCommonFSAttributes(this, folderRecord.getData());
    }
    
    /* @Override */
    public String[] list() {
        return fsHandler.listNames(folderRecord);
    }
    
    /* @Override */
    public FSEntry[] listEntries() {
        return fsHandler.listFSEntries(folderRecord);
    }
    
    /* @Override */
    public FSEntry getChild(String name) {
        return fsHandler.getEntry(folderRecord, name);
    }

    /* @Override */
    public long getValence() {
        return folderRecord.getData().getValence();
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
        return fsHandler.lookupParentFolder(keyRecord);
    }
     * */
    
    public CommonHFSCatalogFolder getInternalCatalogFolder() {
        return folderRecord.getData();
    }
    CommonHFSCatalogFolderRecord getInternalCatalogFolderRecord() {
        return folderRecord;
    }
}
