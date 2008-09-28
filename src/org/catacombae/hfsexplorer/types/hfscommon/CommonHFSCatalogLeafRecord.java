/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.types.hfscommon;

import org.catacombae.hfsexplorer.Util;
import org.catacombae.hfsexplorer.types.BTHeaderRec;
import org.catacombae.hfsexplorer.types.HFSPlusCatalogFile;
import org.catacombae.hfsexplorer.types.HFSPlusCatalogFolder;
import org.catacombae.hfsexplorer.types.HFSPlusCatalogKey;
import org.catacombae.hfsexplorer.types.HFSPlusCatalogLeafRecordData;
import org.catacombae.hfsexplorer.types.HFSPlusCatalogThread;
import org.catacombae.hfsexplorer.types.HFSXCatalogKey;
import org.catacombae.hfsexplorer.types.hfs.CatDataRec;
import org.catacombae.hfsexplorer.types.hfs.CatKeyRec;
import org.catacombae.hfsexplorer.types.hfs.CdrDirRec;
import org.catacombae.hfsexplorer.types.hfs.CdrFThdRec;
import org.catacombae.hfsexplorer.types.hfs.CdrFilRec;
import org.catacombae.hfsexplorer.types.hfs.CdrThdRec;

/**
 *
 * @author erik
 */
public abstract class CommonHFSCatalogLeafRecord extends CommonBTRecord {

    public static CommonHFSCatalogLeafRecord createHFS(byte[] data, int offset, int length) {
        final CatKeyRec key;
        final CatDataRec recordData;

        key = new CatKeyRec(data, offset);

        int recordOffset = offset + key.occupiedSize();
        // Align to word boundary (primitive...)
        if(recordOffset % 2 != 0)
            recordOffset++;

        // Peek at known 8-bit value indicating the record type
        byte recordType = data[recordOffset];
        switch (recordType) {
            case CatDataRec.HFS_DIRECTORY_RECORD:
                recordData = new CdrDirRec(data, recordOffset);
                break;
            case CatDataRec.HFS_FILE_RECORD:
                recordData = new CdrFilRec(data, recordOffset);
                break;
            case CatDataRec.HFS_DIRECTORY_THREAD_RECORD:
                recordData = new CdrThdRec(data, recordOffset);
                break;
            case CatDataRec.HFS_FILE_THREAD_RECORD:
                recordData = new CdrFThdRec(data, recordOffset);
                break;
            default:
                System.err.println("key:");
                key.print(System.err, " ");
                System.err.println("data: " + Util.byteArrayToHexString(data, offset, length));
                throw new RuntimeException("Invalid HFS record type: " + recordType);
        }
        
        return create(key, recordData);
    }
    
    public static CommonHFSCatalogLeafRecord createHFSPlus(byte[] data, int offset, int length) {
        HFSPlusCatalogKey key = new HFSPlusCatalogKey(data, offset);
        final HFSPlusCatalogLeafRecordData recordData;

        // Peek at known 16-bit value to determine proper subtype
        short recordType = Util.readShortBE(data, offset + key.length());
        switch(recordType) {
            case HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER:
                recordData = new HFSPlusCatalogFolder(data, offset + key.length());
                break;
            case HFSPlusCatalogLeafRecordData.RECORD_TYPE_FILE:
                recordData = new HFSPlusCatalogFile(data, offset + key.length());
                break;
            case HFSPlusCatalogLeafRecordData.RECORD_TYPE_FILE_THREAD:
            case HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER_THREAD:
                recordData = new HFSPlusCatalogThread(data, offset + key.length());
                break;
            default:
                throw new RuntimeException("Ivalid record type!");
        }
        return create(key, recordData);
    }
    
    public static CommonHFSCatalogLeafRecord createHFSX(byte[] data, int offset, int length, BTHeaderRec bthr) {
        HFSXCatalogKey key = new HFSXCatalogKey(data, offset, bthr);
        final HFSPlusCatalogLeafRecordData recordData;

        // Peek at known 16-bit value to determine proper subtype
        short recordType = Util.readShortBE(data, offset + key.length());
        switch(recordType) {
            case HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER:
                recordData = new HFSPlusCatalogFolder(data, offset + key.length());
                break;
            case HFSPlusCatalogLeafRecordData.RECORD_TYPE_FILE:
                recordData = new HFSPlusCatalogFile(data, offset + key.length());
                break;
            case HFSPlusCatalogLeafRecordData.RECORD_TYPE_FILE_THREAD:
            case HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER_THREAD:
                recordData = new HFSPlusCatalogThread(data, offset + key.length());
                break;
            default:
                throw new RuntimeException("Ivalid record type!");
        }
        return create(key, recordData);
    }
    
    public abstract CommonHFSCatalogKey getKey();
    
    public static CommonHFSCatalogLeafRecord create(HFSPlusCatalogKey key,
            HFSPlusCatalogLeafRecordData data) {
        if(data instanceof HFSPlusCatalogFolder) {
            return CommonHFSCatalogFolderRecord.create(key, (HFSPlusCatalogFolder)data);
        }
        else if(data instanceof HFSPlusCatalogFile) {
            return CommonHFSCatalogFileRecord.create(key, (HFSPlusCatalogFile)data);            
        }
        else if(data instanceof HFSPlusCatalogThread) {
            if(data.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FILE_THREAD) {
                return CommonHFSCatalogFileThreadRecord.create(key, (HFSPlusCatalogThread)data);
            }
            
            else if(data.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER_THREAD) {
                return CommonHFSCatalogFolderThreadRecord.create(key, (HFSPlusCatalogThread)data);
            }
            else
                throw new RuntimeException("Unknown catalog thread type: " + data.getRecordType());
        }
        else
            throw new RuntimeException("Unknown type of HFSPlusCatalogLeafRecordData: " + data.getClass());
    }
    
    public static CommonHFSCatalogLeafRecord create(CatKeyRec key,
            CatDataRec data) {
        if(data instanceof CdrDirRec) {
            return CommonHFSCatalogFolderRecord.create(key, (CdrDirRec)data);
        }
        else if(data instanceof CdrFilRec) {
            return CommonHFSCatalogFileRecord.create(key, (CdrFilRec)data);            
        }
        else if(data instanceof CdrFThdRec) {
            return CommonHFSCatalogFileThreadRecord.create(key, (CdrFThdRec)data);
        }
        else if(data instanceof CdrThdRec) {
            return CommonHFSCatalogFolderThreadRecord.create(key, (CdrThdRec)data);
        }
        else
            throw new RuntimeException("Unknown type of CatDataRec: " + data.getClass());
    }
}
