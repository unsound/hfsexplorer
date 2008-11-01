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

import java.io.PrintStream;
import java.util.Date;
import org.catacombae.csjc.StaticStruct;
import org.catacombae.csjc.StructElements;
import org.catacombae.csjc.structelements.Dictionary;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusCatalogFile;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusDate;
import org.catacombae.hfsexplorer.types.hfs.CdrFilRec;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusBSDInfo;

/**
 *
 * @author erik
 */
public abstract class CommonHFSCatalogFile implements StaticStruct, CommonHFSCatalogAttributes, StructElements {
    
    public abstract CommonHFSCatalogNodeID getFileID();
    public abstract CommonHFSForkData getDataFork();
    public abstract CommonHFSForkData getResourceFork();
    public abstract byte[] getBytes();

    public abstract boolean isHardFileLink();
    public abstract boolean isHardDirectoryLink();
    public abstract boolean isSymbolicLink();

    public abstract int getHardLinkInode();
    
    public void print(PrintStream ps, String prefix) {
        ps.println(prefix + CommonHFSCatalogFile.class.getSimpleName() + ":");
        printFields(ps, prefix + " ");
    }

    public abstract void printFields(PrintStream ps, String string);
    
    public static CommonHFSCatalogFile create(HFSPlusCatalogFile data) {
        return new HFSPlusImplementation(data);
    }

    public static CommonHFSCatalogFile create(CdrFilRec data) {
        return new HFSImplementation(data);
    }

    public static class HFSPlusImplementation extends CommonHFSCatalogFile {
        private static final int HARD_FILE_LINK_FILE_TYPE = 0x686C6E6B; // "hlnk"
        private static final int HARD_FILE_LINK_CREATOR = 0x6866732B; // "hfs+"
        private static final int HARD_DIRECTORY_LINK_FILE_TYPE = 0x66647270; // "fdrp"
        private static final int HARD_DIRECTORY_LINK_CREATOR = 0x4d414353; // "MACS"
        private HFSPlusCatalogFile data;
        
        private HFSPlusImplementation(HFSPlusCatalogFile data) {
            this.data = data;
        }

        //@Deprecated
        public HFSPlusCatalogFile getUnderlying() {
            return data;
        }

        @Override
        public CommonHFSCatalogNodeID getFileID() {
            return CommonHFSCatalogNodeID.create(data.getFileID());
        }

        @Override
        public CommonHFSForkData getDataFork() {
            return CommonHFSForkData.create(data.getDataFork());
        }

        @Override
        public CommonHFSForkData getResourceFork() {
            return CommonHFSForkData.create(data.getResourceFork());
        }

        @Override
        public int size() {
            return data.size();
        }

        @Override
        public byte[] getBytes() {
            return data.getBytes();
        }

        @Override
        public short getRecordType() {
            return data.getRecordType();
        }

        @Override
        public short getFlags() {
            return data.getFlags();
        }

        @Override
        public int getCreateDate() {
            return data.getCreateDate();
        }

        @Override
        public int getContentModDate() {
            return data.getContentModDate();
        }

        @Override
        public int getAttributeModDate() {
            return data.getAttributeModDate();
        }

        @Override
        public int getAccessDate() {
            return data.getAccessDate();
        }

        @Override
        public int getBackupDate() {
            return data.getBackupDate();
        }

        @Override
        public Date getCreateDateAsDate() {
            return data.getCreateDateAsDate();
        }

        @Override
        public Date getContentModDateAsDate() {
            return data.getContentModDateAsDate();
        }

        @Override
        public Date getAttributeModDateAsDate() {
            return data.getAttributeModDateAsDate();
        }

        @Override
        public Date getAccessDateAsDate() {
            return data.getAccessDateAsDate();
        }

        @Override
        public Date getBackupDateAsDate() {
            return data.getBackupDateAsDate();
        }

        @Override
        public void printFields(PrintStream ps, String prefix) {
            ps.println(prefix + "data:");
            data.print(ps, prefix + " ");
        }

        @Override
        public boolean isSymbolicLink() {
            return data.getPermissions().getFileModeFileType() == HFSPlusBSDInfo.FILETYPE_SYMBOLIC_LINK;
        }

        @Override
        public Dictionary getStructElements() {
            return data.getStructElements();
        }

        public boolean hasPermissions() {
            return true;
        }

        public HFSPlusBSDInfo getPermissions() {
            return data.getPermissions();
        }

        @Override
        public boolean isHardFileLink() {
            int fileType = data.getUserInfo().getFileType().getOSType().getFourCharCode();
            int creator = data.getUserInfo().getFileCreator().getOSType().getFourCharCode();
            return fileType == HARD_FILE_LINK_FILE_TYPE && creator == HARD_FILE_LINK_CREATOR;
        }

        @Override
        public boolean isHardDirectoryLink() {
            int fileType = data.getUserInfo().getFileType().getOSType().getFourCharCode();
            int creator = data.getUserInfo().getFileCreator().getOSType().getFourCharCode();
            return fileType == HARD_DIRECTORY_LINK_FILE_TYPE && creator == HARD_DIRECTORY_LINK_CREATOR;
        }

        @Override
        public int getHardLinkInode() {
            return data.getPermissions().getSpecial();
        }

        @Override
        public boolean hasCreateDate() {
            return true;
        }

        @Override
        public boolean hasContentModDate() {
            return true;
        }

        @Override
        public boolean hasAttributeModDate() {
            return true;
        }

        @Override
        public boolean hasAccessDate() {
            return true;
        }

        @Override
        public boolean hasBackupDate() {
            return true;
        }
    }
    
    public static class HFSImplementation extends CommonHFSCatalogFile {
        private CdrFilRec data;
        
        private HFSImplementation(CdrFilRec data) {
            this.data = data;
        }

        @Override
        public CommonHFSCatalogNodeID getFileID() {
            return CommonHFSCatalogNodeID.create(data.getFilFlNum());
        }

        @Override
        public CommonHFSForkData getDataFork() {
            return CommonHFSForkData.create(data.getFilExtRec(), data.getFilLgLen());
        }

        @Override
        public CommonHFSForkData getResourceFork() {
            return CommonHFSForkData.create(data.getFilRExtRec(), data.getFilRLgLen());
        }

        @Override
        public int size() {
            return data.size();
        }

        @Override
        public byte[] getBytes() {
            return data.getBytes();
        }

        @Override
        public short getRecordType() {
            return data.getCdrType();
        }

        @Override
        public short getFlags() {
            return data.getFilFlags();
        }

        @Override
        public int getCreateDate() {
            return data.getFilCrDat();
        }

        @Override
        public int getContentModDate() {
            return data.getFilMdDat();
        }

        @Override
        public int getAttributeModDate() {
            return data.getFilMdDat();
        }

        @Override
        public int getAccessDate() {
            return data.getFilMdDat();
        }

        @Override
        public int getBackupDate() {
            return data.getFilBkDat();
        }

        @Override
        public Date getCreateDateAsDate() {
            return HFSPlusDate.localTimestampToDate(getCreateDate());
        }

        @Override
        public Date getContentModDateAsDate() {
            return HFSPlusDate.localTimestampToDate(getContentModDate());
        }

        @Override
        public Date getAttributeModDateAsDate() {
            return HFSPlusDate.localTimestampToDate(getAttributeModDate());
        }

        @Override
        public Date getAccessDateAsDate() {
            return HFSPlusDate.localTimestampToDate(getAccessDate());
        }

        @Override
        public Date getBackupDateAsDate() {
            return HFSPlusDate.localTimestampToDate(getBackupDate());
        }

        @Override
        public void printFields(PrintStream ps, String prefix) {
            ps.println(prefix + "data:");
            data.print(ps, prefix + " ");
        }

        @Override
        public boolean isSymbolicLink() {
            // HFS doesn't support symbolic links.
            return false;
        }

        @Override
        public Dictionary getStructElements() {
            return data.getStructElements();
        }

        public boolean hasPermissions() {
            return false;
        }

        public HFSPlusBSDInfo getPermissions() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public boolean isHardFileLink() {
            return false; // No such thing in HFS.
        }

        @Override
        public boolean isHardDirectoryLink() {
            return false; // No such thing in HFS.
        }

        @Override
        public int getHardLinkInode() {
            throw new UnsupportedOperationException("Not supported for HFS.");
        }

        @Override
        public boolean hasCreateDate() {
            return true;
        }

        @Override
        public boolean hasContentModDate() {
            return true;
        }

        @Override
        public boolean hasAttributeModDate() {
            return false;
        }

        @Override
        public boolean hasAccessDate() {
            return false;
        }

        @Override
        public boolean hasBackupDate() {
            return true;
        }
    }
}
