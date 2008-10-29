/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
        
        public short getRecordType() {
            return data.getRecordType();
        }

        public short getFlags() {
            return data.getFlags();
        }

        public int getCreateDate() {
            return data.getCreateDate();
        }

        public int getContentModDate() {
            return data.getContentModDate();
        }

        public int getAttributeModDate() {
            return data.getAttributeModDate();
        }

        public int getAccessDate() {
            return data.getAccessDate();
        }

        public int getBackupDate() {
            return data.getBackupDate();
        }

        public Date getCreateDateAsDate() {
            return data.getCreateDateAsDate();
        }

        public Date getContentModDateAsDate() {
            return data.getContentModDateAsDate();
        }

        public Date getAttributeModDateAsDate() {
            return data.getAttributeModDateAsDate();
        }

        public Date getAccessDateAsDate() {
            return data.getAccessDateAsDate();
        }

        public Date getBackupDateAsDate() {
            return data.getBackupDateAsDate();
        }

        public void printFields(PrintStream ps, String prefix) {
            ps.println(prefix + "data:");
            data.print(ps, prefix + " ");
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
        

        public short getRecordType() {
            return data.getCdrType();
        }

        public short getFlags() {
            return data.getDirFlags();
        }

        public int getCreateDate() {
            return data.getDirCrDat();
        }

        public int getContentModDate() {
            return data.getDirMdDat();
        }

        public int getAttributeModDate() {
            return data.getDirMdDat();
        }

        public int getAccessDate() {
            return data.getDirMdDat();
        }

        public int getBackupDate() {
            return data.getDirBkDat();
        }

        public Date getCreateDateAsDate() {
            return HFSPlusDate.localTimestampToDate(getCreateDate());
        }

        public Date getContentModDateAsDate() {
            return HFSPlusDate.localTimestampToDate(getContentModDate());
        }

        public Date getAttributeModDateAsDate() {
            return HFSPlusDate.localTimestampToDate(getAttributeModDate());
        }

        public Date getAccessDateAsDate() {
            return HFSPlusDate.localTimestampToDate(getAccessDate());
        }

        public Date getBackupDateAsDate() {
            return HFSPlusDate.localTimestampToDate(getBackupDate());
        }

        public void printFields(PrintStream ps, String prefix) {
            ps.println(prefix + "data:");
            data.print(ps, prefix + " ");
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
    }
}
