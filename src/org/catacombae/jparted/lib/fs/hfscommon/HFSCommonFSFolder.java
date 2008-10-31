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

import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogFileRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogFolder;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogFolderRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogLeafRecord;
import org.catacombae.jparted.lib.fs.FSAttributes;
import org.catacombae.jparted.lib.fs.FSEntry;
import org.catacombae.jparted.lib.fs.FSFolder;

/**
 *
 * @author Erik Larsson
 */
public class HFSCommonFSFolder extends FSFolder {
    private final HFSCommonFileSystemHandler fsHandler;
    private final CommonHFSCatalogLeafRecord keyRecord;
    private final CommonHFSCatalogFolderRecord folderRecord;
    private final CommonHFSCatalogFolder catalogFolder;
    private final HFSCommonFSAttributes attributes;
    
    public HFSCommonFSFolder(HFSCommonFileSystemHandler iParent, CommonHFSCatalogFolderRecord iFolderRecord) {
        this(iParent, null, iFolderRecord);
    }

    HFSCommonFSFolder(HFSCommonFileSystemHandler iParent, CommonHFSCatalogFileRecord iHardLinkFileRecord, CommonHFSCatalogFolderRecord iFolderRecord) {
        super(iParent);

        // Input check
        if(iParent == null)
            throw new IllegalArgumentException("iParent must not be null!");
        if(iFolderRecord == null)
            throw new IllegalArgumentException("iFolderRecord must not be null!");

        this.fsHandler = iParent;
        if(iHardLinkFileRecord != null)
            this.keyRecord = iHardLinkFileRecord;
        else
            this.keyRecord = iFolderRecord;
        this.folderRecord = iFolderRecord;
        //CommonHFSCatalogLeafRecordData data = folderRecord.getData();
        this.catalogFolder = folderRecord.getData();
        this.attributes = new HFSCommonFSAttributes(this, catalogFolder);
    }
    
    @Override
    public String[] list() {
        return fsHandler.listNames(folderRecord);
    }
    
    @Override
    public FSEntry[] listEntries() {
        return fsHandler.listFSEntries(folderRecord);
    }
    
    @Override
    public FSEntry getChild(String name) {
        return fsHandler.getEntry(folderRecord, name);
    }

    @Override
    public long getValence() {
        return catalogFolder.getValence();
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
        return fsHandler.lookupParentFolder(keyRecord);
    }
     * */
    
    public CommonHFSCatalogFolder getInternalCatalogFolder() {
        return catalogFolder;
    }
    CommonHFSCatalogFolderRecord getInternalCatalogFolderRecord() {
        return folderRecord;
    }
}
