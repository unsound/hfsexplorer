/*-
 * Copyright (C) Erik Larsson
 *
 * All rights reserved.
 */
package org.catacombae.hfsexplorer.types;

import java.util.Date;

/**
 * Generalization of the common attributes of HFSPlusCatalogFolder and HFSPlusCatalogFile.
 * 
 * @author Erik Larsson
 */
public interface HFSPlusCatalogAttributes {
    public short getRecordType();
    public short getFlags();
    public int getCreateDate();
    public int getContentModDate();
    public int getAttributeModDate();
    public int getAccessDate();
    public int getBackupDate();
    public HFSPlusBSDInfo getPermissions();
    public int getTextEncoding();
    
    public Date getCreateDateAsDate();
    public Date getContentModDateAsDate();
    public Date getAttributeModDateAsDate();
    public Date getAccessDateAsDate();
    public Date getBackupDateAsDate();
}
