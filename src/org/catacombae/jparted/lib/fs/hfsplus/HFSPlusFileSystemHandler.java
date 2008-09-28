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
import org.catacombae.hfsexplorer.UnicodeNormalizationToolkit;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogFolderRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogFileRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogFolderThreadRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogFileThreadRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogFolderThread;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogLeafRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogNodeID;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogString;
import org.catacombae.hfsexplorer.fs.BaseHFSFileSystemView;
import org.catacombae.hfsexplorer.fs.ImplHFSPlusFileSystemView;
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
    private BaseHFSFileSystemView view;
    private boolean doUnicodeFileNameComposition;
    
    public HFSPlusFileSystemHandler(DataLocator fsLocator, boolean useCaching,
            boolean iDoUnicodeFileNameComposition) {
        this(new ImplHFSPlusFileSystemView(fsLocator.createReadOnlyFile(), 0,
                    useCaching), iDoUnicodeFileNameComposition);
    }
    
    protected HFSPlusFileSystemHandler(BaseHFSFileSystemView iView,
                    boolean iDoUnicodeFileNameComposition) {
        this.view = iView;
        this.doUnicodeFileNameComposition = iDoUnicodeFileNameComposition;
    }
    
    @Override
    public FSEntry[] list(String... path) {
        CommonHFSCatalogLeafRecord curFolder = view.getRoot();
        for(String curFolderName : path) {
            final CommonHFSCatalogLeafRecord originalFolder = curFolder;
            CommonHFSCatalogLeafRecord[] subRecords = view.listRecords(curFolder);
            for(CommonHFSCatalogLeafRecord subRecord : subRecords) {
                if(getProperNodeName(subRecord).equals(curFolderName) &&
                   subRecord instanceof CommonHFSCatalogFolderRecord) {
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

    String getProperNodeName(CommonHFSCatalogLeafRecord record) {
        
        //if(doUnicodeFileNameComposition)
        //    return record.getKey().getNodeName().decode(COMPOSED_UTF16_DECODER);
        //else
        //    return record.getKey().getNodeName().decode(DECOMPOSED_UTF16_DECODER);
        String nodeNameRaw = view.getString(record.getKey().getNodeName());
        if(doUnicodeFileNameComposition)
            return UnicodeNormalizationToolkit.getDefaultInstance().compose(nodeNameRaw);
        else
            return nodeNameRaw;
    }    

    ReadableRandomAccessStream getReadableDataForkStream(CommonHFSCatalogFileRecord fileRecord) {
        return view.getReadableDataForkStream(fileRecord);
    }
    
    ReadableRandomAccessStream getReadableResourceForkStream(CommonHFSCatalogFileRecord fileRecord) {
        return view.getReadableResourceForkStream(fileRecord);
    }
    
    /*
    boolean isUnicodeCompositionEnabled() {
        return doUnicodeFileNameComposition;
    }
     * */
    
    FSEntry[] listFSEntries(CommonHFSCatalogLeafRecord folderRecord) {
        CommonHFSCatalogLeafRecord[] subRecords = view.listRecords(folderRecord);
        LinkedList<FSEntry> result = new LinkedList<FSEntry>();
        for(int i = 0; i < subRecords.length; ++i) {
            CommonHFSCatalogLeafRecord curRecord = subRecords[i];
            if(curRecord instanceof CommonHFSCatalogFileRecord)
                result.addLast(new HFSPlusFSFile(this, (CommonHFSCatalogFileRecord)curRecord));
            else if(curRecord instanceof CommonHFSCatalogFolderRecord) {
                result.addLast(new HFSPlusFSFolder(this, (CommonHFSCatalogFolderRecord)curRecord));
            }
        }
        return result.toArray(new FSEntry[result.size()]);
    }
    
    FSFolder lookupParentFolder(CommonHFSCatalogLeafRecord childRecord) {
        CommonHFSCatalogNodeID parentID = childRecord.getKey().getParentID();
        
        // Look for the thread record associated with the parent dir
        CommonHFSCatalogLeafRecord parent =
                view.getRecord(parentID, CommonHFSCatalogString.EMPTY);
        if(parent == null) {
            throw new RuntimeException("INTERNAL ERROR: No folder thread found!");
        }

        if(parent instanceof CommonHFSCatalogFolderThreadRecord) {
            CommonHFSCatalogFolderThread data =
                    ((CommonHFSCatalogFolderThreadRecord)parent).getData();
            CommonHFSCatalogLeafRecord rec =
                    view.getRecord(data.getParentID(), data.getNodeName());
            if(rec instanceof CommonHFSCatalogFolderRecord)
                return new HFSPlusFSFolder(this, (CommonHFSCatalogFolderRecord)rec);
            else
                throw new RuntimeException("Internal error: rec not instanceof " +
                        "CommonHFSCatalogFolderRecord, but instead:" +
                        rec.getClass());
        }
        else if(parent instanceof CommonHFSCatalogFileThreadRecord) {
            throw new RuntimeException("Tried to get folder thread record (" +
                    parentID + ",\"\") but found a file thread record!");
        }
        else {
            throw new RuntimeException("Tried to get folder thread record (" +
                    parentID + ",\"\") but found a " + parent.getClass() + "!");
        }
    }
    
    /**
     * This method is transitional and will be removed.
     * 
     * @deprecated
     * @return
     */
    public BaseHFSFileSystemView getFSView() {
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
