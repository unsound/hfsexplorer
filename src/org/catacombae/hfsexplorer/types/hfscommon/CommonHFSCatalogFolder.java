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
import org.catacombae.csjc.PrintableStruct;
import org.catacombae.csjc.StructElements;
import org.catacombae.csjc.structelements.Dictionary;
import org.catacombae.hfsexplorer.Util;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusBSDInfo;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusCatalogFolder;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusDate;
import org.catacombae.hfsexplorer.types.hfs.CdrDirRec;
import org.catacombae.hfsexplorer.types.hfs.HFSDate;

/**
 *
 * @author erik
 */
public abstract class CommonHFSCatalogFolder implements CommonHFSCatalogAttributes, PrintableStruct, StructElements {
    public abstract CommonHFSCatalogNodeID getFolderID();
    
    public static CommonHFSCatalogFolder create(HFSPlusCatalogFolder data) {
        return new HFSPlusImplementation(data);
    }

    public static CommonHFSCatalogFolder create(CdrDirRec data) {
        return new HFSImplementation(data);
    }

    public abstract long getValence();
    
    public abstract int length();

    public abstract byte[] getBytes();
    
    @Override
    public void print(PrintStream ps, String prefix) {
        ps.println(prefix + CommonHFSCatalogFolder.class.getSimpleName() + ":");
        printFields(ps, prefix + " ");
    }

    public static class HFSPlusImplementation extends CommonHFSCatalogFolder {
        private HFSPlusCatalogFolder data;
        
        public HFSPlusImplementation(HFSPlusCatalogFolder data) {
            this.data = data;
        }

        //@Deprecated
        public HFSPlusCatalogFolder getUnderlying() {
            return data;
        }
        
        @Override
        public CommonHFSCatalogNodeID getFolderID() {
            return CommonHFSCatalogNodeID.create(data.getFolderID());
        }

        @Override public long getValence() {
            return Util.unsign(data.getValence());
        }

        @Override
        public int length() {
            return data.length();
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
        public Dictionary getStructElements() {
            return data.getStructElements();
        }

        @Override
        public boolean hasPermissions() {
            return true;
        }

        @Override
        public HFSPlusBSDInfo getPermissions() {
            return data.getPermissions();
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
    
    public static class HFSImplementation extends CommonHFSCatalogFolder {
        private CdrDirRec data;
        
        public HFSImplementation(CdrDirRec data) {
            this.data = data;
        }

        @Override
        public CommonHFSCatalogNodeID getFolderID() {
            return CommonHFSCatalogNodeID.create(data.getDirDirID());
        }

        @Override public long getValence() {
            return Util.unsign(data.getDirVal());
        }

        @Override
        public int length() {
            return data.length();
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
            return data.getDirFlags();
        }

        @Override
        public int getCreateDate() {
            return data.getDirCrDat();
        }

        @Override
        public int getContentModDate() {
            return data.getDirMdDat();
        }

        @Override
        public int getAttributeModDate() {
            return data.getDirMdDat();
        }

        @Override
        public int getAccessDate() {
            return data.getDirMdDat();
        }

        @Override
        public int getBackupDate() {
            return data.getDirBkDat();
        }

        @Override
        public Date getCreateDateAsDate() {
            return HFSDate.localTimestampToDate(getCreateDate());
        }

        @Override
        public Date getContentModDateAsDate() {
            return HFSDate.localTimestampToDate(getContentModDate());
        }

        @Override
        public Date getAttributeModDateAsDate() {
            return HFSDate.localTimestampToDate(getAttributeModDate());
        }

        @Override
        public Date getAccessDateAsDate() {
            return HFSDate.localTimestampToDate(getAccessDate());
        }

        @Override
        public Date getBackupDateAsDate() {
            return HFSDate.localTimestampToDate(getBackupDate());
        }

        @Override
        public void printFields(PrintStream ps, String prefix) {
            ps.println(prefix + "data:");
            data.print(ps, prefix + " ");
        }

        @Override
        public Dictionary getStructElements() {
            return data.getStructElements();
        }

        @Override
        public boolean hasPermissions() {
            return false;
        }

        @Override
        public HFSPlusBSDInfo getPermissions() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public boolean hasAccessDate() {
            return false;
        }

        @Override
        public boolean hasBackupDate() {
            return true;
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
    }
}
