/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.types.hfscommon;

import java.io.PrintStream;
import java.util.Date;
import org.catacombae.csjc.StaticStruct;
import org.catacombae.hfsexplorer.types.HFSPlusCatalogFile;
import org.catacombae.hfsexplorer.types.HFSPlusDate;
import org.catacombae.hfsexplorer.types.hfs.CdrFilRec;

/**
 *
 * @author erik
 */
public abstract class CommonHFSCatalogFile implements StaticStruct, CommonHFSCatalogAttributes {
    
    public abstract CommonHFSCatalogNodeID getFileID();
    public abstract CommonHFSForkData getDataFork();
    public abstract CommonHFSForkData getResourceFork();
    public abstract byte[] getBytes();
    
    public void print(PrintStream ps, String prefix) {
        ps.println(prefix + "CommonHFSCatalogFile:");
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
        private HFSPlusCatalogFile data;
        
        private HFSPlusImplementation(HFSPlusCatalogFile data) {
            this.data = data;
        }

        @Deprecated
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

        @Override
        public void printFields(PrintStream ps, String prefix) {
            ps.println(prefix + "data:");
            data.print(ps, prefix + " ");
        }
    }
    
    public static class HFSImplementation extends CommonHFSCatalogFile {
        private CdrFilRec data;
        
        private HFSImplementation(CdrFilRec data) {
            System.err.println("CommonHFSCatalogFile.HFSImplementation invoked!");
            this.data = data;
            data.print(System.err, "  ");
            System.err.println("CommonHFSCatalogFile.HFSImplementation finished.");
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
            return data.getFilFlags();
        }

        public int getCreateDate() {
            return data.getFilCrDat();
        }

        public int getContentModDate() {
            return data.getFilMdDat();
        }

        public int getAttributeModDate() {
            return data.getFilMdDat();
        }

        public int getAccessDate() {
            return data.getFilMdDat();
        }

        public int getBackupDate() {
            return data.getFilBkDat();
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

        @Override
        public void printFields(PrintStream ps, String prefix) {
            ps.println(prefix + "data:");
            data.print(ps, prefix + " ");
        }
    }
}
