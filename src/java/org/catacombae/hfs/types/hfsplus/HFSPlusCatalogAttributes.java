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

package org.catacombae.hfs.types.hfsplus;

import java.util.Date;

/**
 * Generalization of the common attributes of HFSPlusCatalogFolder and HFSPlusCatalogFile.
 *
 * @author Erik Larsson
 */
public interface HFSPlusCatalogAttributes {
    public static short kHFSFileLockedBit = 0x0000;
    public static short kHFSFileLockedMask = 0x0001;

    public static short kHFSThreadExistsBit = 0x0001;
    public static short kHFSThreadExistsMask = 0x0002;

    public static short kHFSHasAttributesBit = 0x0002;
    public static short kHFSHasAttributesMask = 0x0004;

    public static short kHFSHasSecurityBit = 0x0003;
    public static short kHFSHasSecurityMask = 0x0008;

    public static short kHFSHasFolderCountBit = 0x0004;
    public static short kHFSHasFolderCountMask = 0x0010;

    public static short kHFSHasLinkChainBit = 0x0005;
    public static short kHFSHasLinkChainMask = 0x0020;

    public static short kHFSHasChildLinkBit = 0x0006;
    public static short kHFSHasChildLinkMask = 0x0040;

    public static short kHFSHasDateAddedBit = 0x0007;
    public static short kHFSHasDateAddedMask = 0x0080;

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
