/*-
 * Copyright (C) Erik Larsson
 *
 * All rights reserved.
 */
package org.catacombae.jparted.lib.fs.hfscommon;

import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogFile;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogFileRecord;
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
    private final CommonHFSCatalogFileRecord fileRecord;
    private final CommonHFSCatalogFile catalogFile;
    private final HFSCommonFSAttributes attributes;
    private final FSFork dataFork;
    private final FSFork resourceFork;
    
    HFSCommonFSFile(HFSCommonFileSystemHandler iParent, CommonHFSCatalogFileRecord iFileRecord) {
        super(iParent);
        
        // Input check
        if(iParent == null)
            throw new IllegalArgumentException("iParent must not be null!");
        if(iFileRecord == null)
            throw new IllegalArgumentException("iFileRecord must not be null!");
        
        this.parent = iParent;
        this.fileRecord = iFileRecord;
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
        return parent.getProperNodeName(fileRecord);
    }

    @Override
    public FSFolder getParent() {
        return parent.lookupParentFolder(fileRecord);
    }

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
