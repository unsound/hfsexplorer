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

import java.util.Date;
import org.catacombae.hfsexplorer.Util;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogAttributes;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogFileRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogFolderRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogLeafRecord;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusBSDInfo;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusCatalogAttributes;
import org.catacombae.jparted.lib.fs.FSAttributes;
import org.catacombae.jparted.lib.fs.FSEntry;
import org.catacombae.jparted.lib.fs.WindowsFileAttributes;

/**
 *
 * @author Erik Larsson
 */
class HFSCommonFSAttributes extends FSAttributes {
    
    private final HFSCommonFSEntry parentEntry;
    private final CommonHFSCatalogAttributes attributes;
    private POSIXFileAttributes posixAttributes = null;
    
    public HFSCommonFSAttributes(HFSCommonFSEntry parentEntry, CommonHFSCatalogAttributes attributes) {
        this.parentEntry = parentEntry;
        this.attributes = attributes;
    }

    @Override
    public boolean hasPOSIXFileAttributes() {
        return attributes.hasPermissions();
    }

    @Override
    public POSIXFileAttributes getPOSIXFileAttributes() {
        if(attributes.hasPermissions()) {
            if(posixAttributes == null) {
                HFSPlusBSDInfo permissions = attributes.getPermissions();

                posixAttributes = new DefaultPOSIXFileAttributes(
                        Util.unsign(permissions.getOwnerID()),
                        Util.unsign(permissions.getGroupID()),
                        permissions.getFileMode());
            }
            return posixAttributes;
        }
        else
            throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public WindowsFileAttributes getWindowsFileAttributes() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Date getModifyDate() {
        return attributes.getContentModDateAsDate();
    }


    @Override
    public boolean hasWindowsFileAttributes() {
        return false;
    }

    @Override
    public boolean hasCreateDate() {
        return attributes.hasCreateDate();
    }

    @Override
    public Date getCreateDate() {
        return attributes.getCreateDateAsDate();
    }

    @Override
    public boolean hasModifyDate() {
        return attributes.hasContentModDate();
    }

    @Override
    public boolean hasAttributeModifyDate() {
        return attributes.hasAttributeModDate();
    }

    @Override
    public boolean hasAccessDate() {
        return attributes.hasAccessDate();
    }

    @Override
    public Date getAccessDate() {
        return attributes.getAccessDateAsDate();
    }

    @Override
    public boolean hasBackupDate() {
        return attributes.hasBackupDate();
    }

    @Override
    public Date getBackupDate() {
        return attributes.getBackupDateAsDate();
    }

    @Override
    public Date getAttributeModifyDate() {
        return attributes.getAttributeModDateAsDate();
    }

    @Override
    public boolean hasLinkCount() {
        if(attributes instanceof CommonHFSCatalogFileRecord) {
            CommonHFSCatalogFileRecord fr = (CommonHFSCatalogFileRecord) attributes;
            if(fr.getData().isHardFileLink() /* || fr.getData().isHardDirectoryLink() */ )
                return true;
        }

        return false;
    }

    @Override
    public Long getLinkCount() {
        if(attributes instanceof CommonHFSCatalogFileRecord) {
            CommonHFSCatalogFileRecord fr = (CommonHFSCatalogFileRecord) attributes;
            if(fr.getData().isHardFileLink()) {
                int inodeNumber = fr.getData().getHardLinkInode();
                CommonHFSCatalogFileRecord rec =
                        parentEntry.getFileSystemHandler().lookupFileInode(inodeNumber);

                return Util.unsign(rec.getData().getPermissions().getSpecial());
            }
            else if(fr.getData().isHardDirectoryLink()) {
                int inodeNumber = fr.getData().getHardLinkInode();
                CommonHFSCatalogFolderRecord rec =
                        parentEntry.getFileSystemHandler().lookupDirectoryInode(inodeNumber);

                return Util.unsign(rec.getData().getPermissions().getSpecial());
            }
        }
        
        return null;
    }

}
