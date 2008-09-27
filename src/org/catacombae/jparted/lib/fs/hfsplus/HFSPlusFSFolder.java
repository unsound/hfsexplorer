/*-
 * Copyright (C) Erik Larsson
 *
 * All rights reserved.
 */
package org.catacombae.jparted.lib.fs.hfsplus;

import org.catacombae.hfsexplorer.Util;
import org.catacombae.hfsexplorer.types.HFSPlusCatalogFolder;
import org.catacombae.hfsexplorer.types.HFSPlusCatalogLeafRecord;
import org.catacombae.hfsexplorer.types.HFSPlusCatalogLeafRecordData;
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
public class HFSPlusFSFolder extends FSFolder {
    private final HFSPlusFileSystemHandler fsHandler;
    private final CommonHFSCatalogFolderRecord folderRecord;
    private final CommonHFSCatalogFolder catalogFolder;
    private final HFSPlusFSAttributes attributes;
    
    public HFSPlusFSFolder(HFSPlusFileSystemHandler iParent, CommonHFSCatalogFolderRecord iFolderRecord) {
        super(iParent);
        
        // Input check
        if(iParent == null)
            throw new IllegalArgumentException("iParent must not be null!");
        if(iFolderRecord == null)
            throw new IllegalArgumentException("iFolderRecord must not be null!");
        
        this.fsHandler = iParent;
        this.folderRecord = iFolderRecord;
        //CommonHFSCatalogLeafRecordData data = folderRecord.getData();
        this.catalogFolder = folderRecord.getData();
        this.attributes = new HFSPlusFSAttributes(this, catalogFolder);
    }

    @Override
    public FSEntry[] list() {
        return fsHandler.listFSEntries(folderRecord);
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
        return fsHandler.getProperNodeName(folderRecord);
    }

    @Override
    public FSFolder getParent() {
        return fsHandler.lookupParentFolder(folderRecord);
    }
    
    public CommonHFSCatalogFolder getInternalCatalogFolder() {
        return catalogFolder;
    }
}
