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

package org.catacombae.hfsexplorer.types.hfsplus;

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
