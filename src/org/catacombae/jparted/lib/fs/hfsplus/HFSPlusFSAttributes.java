/*-
 * Copyright (C) Erik Larsson
 *
 * All rights reserved.
 */
package org.catacombae.jparted.lib.fs.hfsplus;

import java.util.Date;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogAttributes;
import org.catacombae.jparted.lib.fs.FSAttributes;
import org.catacombae.jparted.lib.fs.FSEntry;
import org.catacombae.jparted.lib.fs.WindowsFileAttributes;

/**
 *
 * @author Erik Larsson
 */
class HFSPlusFSAttributes extends FSAttributes {
    
    private final FSEntry parentEntry;
    private final CommonHFSCatalogAttributes attributes;
    
    public HFSPlusFSAttributes(FSEntry parentEntry, CommonHFSCatalogAttributes attributes) {
        this.parentEntry = parentEntry;
        this.attributes = attributes;
    }
    
    @Override
    public POSIXFileAttributes getPOSIXAttributes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public WindowsFileAttributes getWindowsFileAttributes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Date getModifyDate() {
        return attributes.getContentModDateAsDate();
    }

}
