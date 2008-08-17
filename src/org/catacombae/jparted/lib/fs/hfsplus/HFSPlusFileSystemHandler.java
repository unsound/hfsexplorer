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

package org.catacombae.jparted.lib.fs.hfsplus;

import java.util.LinkedList;
import org.catacombae.hfsexplorer.HFSPlusFileSystemView;
import org.catacombae.hfsexplorer.types.HFSCatalogNodeID;
import org.catacombae.hfsexplorer.types.HFSPlusCatalogFolder;
import org.catacombae.hfsexplorer.types.HFSPlusCatalogLeafRecord;
import org.catacombae.hfsexplorer.types.HFSPlusCatalogLeafRecordData;
import org.catacombae.hfsexplorer.types.HFSPlusCatalogThread;
import org.catacombae.hfsexplorer.types.HFSUniStr255;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.jparted.lib.fs.*;
import org.catacombae.jparted.lib.DataLocator;

/**
 * HFS+ implementation of a FileSystemHandler. This implementation can be used
 * to access HFS+ file systems.
 * 
 * @author Erik Larsson
 */
public class HFSPlusFileSystemHandler extends FileSystemHandler {
    private HFSPlusFileSystemView view;
    private boolean doUnicodeFileNameComposition;
    
    public HFSPlusFileSystemHandler(DataLocator fsLocator, boolean useCaching,
            boolean iDoUnicodeFileNameComposition) {
        this(new HFSPlusFileSystemView(fsLocator.createReadOnlyFile(), 0,
                    useCaching), iDoUnicodeFileNameComposition);
    }
    
    protected HFSPlusFileSystemHandler(HFSPlusFileSystemView iView,
                    boolean iDoUnicodeFileNameComposition) {
        this.view = iView;
        this.doUnicodeFileNameComposition = iDoUnicodeFileNameComposition;
    }
    
    @Override
    public FSEntry[] list(String... path) {
        HFSPlusCatalogLeafRecord curFolder = view.getRoot();
        for(String curFolderName : path) {
            final HFSPlusCatalogLeafRecord originalFolder = curFolder;
            HFSPlusCatalogLeafRecord[] subRecords = view.listRecords(curFolder);
            for(HFSPlusCatalogLeafRecord subRecord : subRecords) {
                HFSPlusCatalogLeafRecordData recData = subRecord.getData();
                if(getProperNodeName(subRecord).equals(curFolderName) &&
                   recData.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER &&
                   recData instanceof HFSPlusCatalogFolder) {
                    curFolder = subRecord;
                    break;
                }
            }
            
            if(curFolder == originalFolder)
                return null; // Invalid path, no matching child was found.
        }
        return listFSEntries(curFolder);
    }

    @Override
    public FSForkType[] getSupportedForkTypes() {
        return new FSForkType[] { FSForkType.DATA, FSForkType.MACOS_RESOURCE };
    }

    String getProperNodeName(HFSPlusCatalogLeafRecord record) {
        if(doUnicodeFileNameComposition)
            return record.getKey().getNodeName().getUnicodeAsComposedString();
        else
            return record.getKey().getNodeName().getUnicodeAsDecomposedString();

    }    

    ReadableRandomAccessStream getReadableDataForkStream(HFSPlusCatalogLeafRecord fileRecord) {
        return view.getReadableDataForkStream(fileRecord);
    }
    
    ReadableRandomAccessStream getReadableResourceForkStream(HFSPlusCatalogLeafRecord fileRecord) {
        return view.getReadableResourceForkStream(fileRecord);
    }
    
    /*
    boolean isUnicodeCompositionEnabled() {
        return doUnicodeFileNameComposition;
    }
     * */
    
    FSEntry[] listFSEntries(HFSPlusCatalogLeafRecord folderRecord) {
        HFSPlusCatalogLeafRecord[] subRecords = view.listRecords(folderRecord);
        LinkedList<FSEntry> result = new LinkedList<FSEntry>();
        for(int i = 0; i < subRecords.length; ++i) {
            HFSPlusCatalogLeafRecord curRecord = subRecords[i];
            if(curRecord.getData().getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FILE)
                result.addLast(new HFSPlusFSFile(this, curRecord));
            else if(curRecord.getData().getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER)
                result.addLast(new HFSPlusFSFolder(this, curRecord));
        }
        return result.toArray(new FSEntry[result.size()]);
    }
    
    FSFolder lookupParentFolder(HFSPlusCatalogLeafRecord childRecord) {
        HFSCatalogNodeID parentID = childRecord.getKey().getParentID();
        
        // Look for the thread record associated with the parent dir
        HFSPlusCatalogLeafRecord parent =
                view.getRecord(parentID, new HFSUniStr255(""));
        if(parent == null) {
            throw new RuntimeException("INTERNAL ERROR: No folder thread found!");
        }
        HFSPlusCatalogLeafRecordData data = parent.getData();
        if(data.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FOLDER_THREAD &&
                data instanceof HFSPlusCatalogThread) {
            HFSPlusCatalogThread threadData = (HFSPlusCatalogThread) data;
            return new HFSPlusFSFolder(this,
                    view.getRecord(threadData.getParentID(),
                        threadData.getNodeName()));
        }
        else if(data.getRecordType() == HFSPlusCatalogLeafRecordData.RECORD_TYPE_FILE_THREAD &&
                data instanceof HFSPlusCatalogThread) {
            throw new RuntimeException("Tried to get folder thread (" + parentID + ",\"\") but found a file thread!");
        }
        else {
            throw new RuntimeException("Tried to get folder thread (" + parentID + ",\"\") but found a " + data.getClass() + "!");
        }
    }
    
    /**
     * This method is transitional and will be removed.
     * 
     * @deprecated
     * @return
     */
    public HFSPlusFileSystemView getFSView() {
        return view;
    }

    @Override
    public void close() {
        view.getStream().close();
    }

    @Override
    public FSFolder getRoot() {
        return new HFSPlusFSFolder(this, view.getRoot());
    }
}
