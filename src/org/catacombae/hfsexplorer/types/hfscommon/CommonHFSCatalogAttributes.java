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

package org.catacombae.hfsexplorer.types.hfscommon;

import java.util.Date;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusBSDInfo;

/**
 * Generalization of the common attributes of CommonHFSCatalogFolder and CommonHFSCatalogFile.
 * 
 * Please note that this class is NOT designed to cope with subtle differences
 * between HFS and HFS+. For instance HFS does not have the concept of separate
 * content modify and attribute modify dates, and does not at all store access
 * date. This interface should be updated in a clever way some time later.
 *
 * @author Erik Larsson
 */
public interface CommonHFSCatalogAttributes {
    public short getRecordType();
    public short getFlags();
    public int getCreateDate();
    public int getContentModDate();
    public int getAttributeModDate();
    public int getAccessDate();
    public int getBackupDate();

    /**
     * Returns whether or not this attribute set has a defined value for the "entry created"
     * timestamp.
     * 
     * @return whether or not this attribute set has a defined value for the "entry created"
     * timestamp.
     */
    public boolean hasCreateDate();
    
    /**
     * Returns whether or not this attribute set has a defined value for the "contents modified"
     * timestamp.
     * 
     * @return whether or not this attribute set has a defined value for the "contents modified"
     * timestamp.
     */
    public boolean hasContentModDate();
    
    /**
     * Returns whether or not this attribute set has a defined value for the "attributes modified"
     * timestamp.
     * 
     * @return whether or not this attribute set has a defined value for the "attribtues modified"
     * timestamp.
     */
    public boolean hasAttributeModDate();
    
    /**
     * Returns whether or not this attribute set has a defined value for the "entry last accessed"
     * timestamp.
     * 
     * @return whether or not this attribute set has a defined value for the "entry last accessed"
     * timestamp.
     */
    public boolean hasAccessDate();
    
    /**
     * Returns whether or not this attribute set has a defined value for the "entry last backuped"
     * timestamp.
     * 
     * @return whether or not this attribute set has a defined value for the "entry last backuped"
     * timestamp.
     */
    public boolean hasBackupDate();
    
    /**
     * Returns whether or not this object contains HFS+ permissions.
     * @return whether or not this object contains HFS+ permissions.
     */
    public boolean hasPermissions();
    public HFSPlusBSDInfo getPermissions();
    //public int getTextEncoding();
    
    public Date getCreateDateAsDate();
    public Date getContentModDateAsDate();
    public Date getAttributeModDateAsDate();
    public Date getAccessDateAsDate();
    public Date getBackupDateAsDate();
}
