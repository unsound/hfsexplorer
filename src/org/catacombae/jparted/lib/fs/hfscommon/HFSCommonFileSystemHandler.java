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

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import org.catacombae.hfsexplorer.IOUtil;
import org.catacombae.hfsexplorer.UnicodeNormalizationToolkit;
import org.catacombae.hfsexplorer.Util;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogFolderRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogFileRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogFolderThreadRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogFileThreadRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogFolderThread;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogLeafRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSCatalogNodeID;
import org.catacombae.hfsexplorer.fs.BaseHFSFileSystemView;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.jparted.lib.fs.FSFolder;
import org.catacombae.jparted.lib.fs.FSForkType;
import org.catacombae.jparted.lib.fs.FSLink;
import org.catacombae.jparted.lib.fs.FileSystemHandler;
import org.catacombae.jparted.lib.fs.FSEntry;

/**
 * HFS+ implementation of a FileSystemHandler. This implementation can be used
 * to access HFS+ file systems.
 * 
 * @author Erik Larsson
 */
public class HFSCommonFileSystemHandler extends FileSystemHandler {
    private static final String FILE_HARD_LINK_DIR = "\u0000\u0000\u0000\u0000HFS+ Private Data";
    private static final String FILE_HARD_LINK_PREFIX = "iNode";
    private static final String DIRECTORY_HARD_LINK_DIR = ".HFS+ Private Directory Data" + (char)0x0d;
    private static final String DIRECTORY_HARD_LINK_PREFIX = "dir_";
    private BaseHFSFileSystemView view;
    private boolean doUnicodeFileNameComposition;
    
    protected HFSCommonFileSystemHandler(BaseHFSFileSystemView iView,
                    boolean iDoUnicodeFileNameComposition) {
        this.view = iView;
        this.doUnicodeFileNameComposition = iDoUnicodeFileNameComposition;
    }
    
    @Override
    public FSEntry[] list(String... path) {
        CommonHFSCatalogFolderRecord curFolder = view.getRoot();
        for(String nextFolderName : path) {
            CommonHFSCatalogLeafRecord subRecord = getRecord(curFolder, nextFolderName);
            
            if(subRecord != null && subRecord instanceof CommonHFSCatalogFolderRecord)
                curFolder = (CommonHFSCatalogFolderRecord) subRecord;
            else
                return null; // Invalid path, no matching child folder was found.
        }
        return listFSEntries(curFolder);
    }


    @Override
    public FSEntry getEntry(String... path) {
        return getEntry(view.getRoot(), path);
    }
    
    FSEntry getEntry(CommonHFSCatalogFolderRecord rootRecord, String... path) {
        CommonHFSCatalogLeafRecord rec = getRecord(rootRecord, path);
        
        if(rec == null)
            return null;
        else if(rec instanceof CommonHFSCatalogFileRecord)
            return entryFromRecord((CommonHFSCatalogFileRecord)rec);
        else if(rec instanceof CommonHFSCatalogFolderRecord)
            return entryFromRecord((CommonHFSCatalogFolderRecord)rec);
        else
            throw new RuntimeException("Did not excpect a " + rec.getClass() + " here!");
    }
    
    /**
     * Searches the hierarchy rooted in <code>rootRecord</code> for the record addressed by
     * <code>path</code>. If any symbolic or hard links exist in the path to the requested entry,
     * they will be resolved, but the requested destination will be returned as it is.
     * 
     * @param rootRecord (non-null) the root record from which we will begin searching.
     * @param path the path to our requested entry. May be empty, in which case
     * <code>rootRecord</code> is returned.
     * @return the requested entry, or <code>null</code> if it wasn't found.
     */
    CommonHFSCatalogLeafRecord getRecord(final CommonHFSCatalogFolderRecord rootRecord, final String... path) {
        /*
         * Algorithm (variables are prefixed with $):
         * 
         * $currentRoot = $root
         * for each path component $pc except the last one:
         *   while currentRoot is a link:
         *     $currentRoot = resolveLink(entry)
         *
         *   if currentRoot is a directory:
         *     $currentRoot = find($pc, $currentRoot)
         *   else:
         *     return null
         *
         * return currentRoot
         */
        /*
        String prefix = globalPrefix;
        globalPrefix += "    ";
        log(prefix + "getRecord(" + (rootRecord != null ? rootRecord.getKey().getParentID().toLong() +
                ":\"" + getProperNodeName(rootRecord) + "\"" : "null" ) + ", { " +
                (path != null && path.length > 0 ? "\"" + Util.concatenateStrings(path, "\", \"") +
                "\"" : path == null ? "null" : "" ) + " });");
        try {
        */
            if(rootRecord == null)
            throw new IllegalArgumentException("rootRecord == null");
        if(path == null)
            throw new IllegalArgumentException("path == null");

        LinkedList<String[]> visitedList = null;
        CommonHFSCatalogLeafRecord currentRoot = rootRecord;

        // We iterate over all records except the last one, which is our target.
        for(int i = 0; i < path.length; ++i) {
            String curPathComponent = path[i];
            //log(prefix + "  getRecord: Processing path element " + (i + 1) + "/" +
            //        path.length + ": \"" + curPathComponent + "\"");

            LinkedList<String[]> curVisitedList = null;
            
            // Iterate through all links.
            while(currentRoot instanceof CommonHFSCatalogFileRecord) {
                CommonHFSCatalogFileRecord fr =
                        (CommonHFSCatalogFileRecord) currentRoot;
                final String[] absPath;

                if(fr.getData().isSymbolicLink()) {
                    byte[] data = IOUtil.readFully(getReadableDataForkStream(fr));
                    String posixPath = Util.readString(data, "UTF-8");
                    String[] basePath = Util.arrayCopy(path, 0, new String[i - 1], 0, i - 1);
                    absPath = getTruePathFromPosixPath(posixPath, basePath);
                    if(absPath == null) {
                        // Sorry pal, no luck in findin' yarr link target
                        //log(prefix + " getRecord: no link target found for posix path \"" + posixPath + "\" with base path \"" + Util.concatenateStrings(basePath, "/") + "\"");
                        return null;
                    }
                    //else
                    //  log(prefix + "  getRecord: absPath=" + Util.concatenateStrings(absPath, "/"));
                }
                else if(fr.getData().isHardFileLink()) {
                    absPath = new String[]{
                                FILE_HARD_LINK_DIR,
                                FILE_HARD_LINK_PREFIX + Util.unsign(fr.getData().getHardLinkInode())
                            };
                }
                else if(fr.getData().isHardDirectoryLink()) {
                    absPath = new String[]{
                                DIRECTORY_HARD_LINK_DIR,
                                DIRECTORY_HARD_LINK_PREFIX + Util.unsign(fr.getData().getHardLinkInode())
                            };
                }
                else
                    break;

                // Reset visited list before usage if this is the first time
                if(curVisitedList == null) {
                    if(visitedList == null)
                        visitedList = new LinkedList<String[]>();
                    else
                        visitedList.clear();
                    curVisitedList = visitedList;
                }

                if(absPath == null)
                    throw new RuntimeException("CHECK YOUR CODE FFS.");
                else if(Util.contains(curVisitedList, absPath)) {
                    System.err.println("WARNING: Detected cyclic link structure when resolving link target.");
                    System.err.println("         Resolve stack:");
                    for(String[] sa : curVisitedList)
                        System.err.println("           " + Util.concatenateStrings(sa, "/"));
                    System.err.println("           " + Util.concatenateStrings(absPath, "/"));
                    return null; // Circular linking.
                }
                else {
                    curVisitedList.addLast(absPath);
                    //log(prefix + "  getRecord: Trying to get record for absolute link target...");
                    CommonHFSCatalogLeafRecord linkTarget =
                            getRecord(view.getRoot(), absPath);
                    //log(prefix + "  getRecord: target record = " + linkTarget);
                    if(linkTarget != null) {
                        currentRoot = linkTarget;
                    }
                }
            }

            CommonHFSCatalogFolderRecord currentRootFolder;
            if(currentRoot instanceof CommonHFSCatalogFolderRecord)
                currentRootFolder = (CommonHFSCatalogFolderRecord) currentRoot;
            else {
                //log(prefix + "  getRecord: Returning with error - currentRoot not instanceof CommonHFSCatalogFolderRecord (" + currentRoot + ")");
                return null; // We encountered a pathname component which wasn't a folder.
            }

            //log(prefix + "  getting record (" + currentRootFolder.getData().getFolderID().toLong() + ":\"" + curPathComponent + "\")");
            CommonHFSCatalogLeafRecord newRoot =
                    view.getRecord(currentRootFolder.getData().getFolderID(), view.encodeString(curPathComponent));

            if(newRoot != null)
                currentRoot = newRoot;
            else {
                //log(prefix + "  getRecord: Returning with error - no match was found for \"" + curPathComponent + "\"");
                return null; // Invalid path, no matching child was found.
            }
        }


        //log(prefix + "  getRecord: Returning successfully with " + currentRoot + " (" + currentRoot.getKey().getParentID().toLong() + ":\"" + getProperNodeName(currentRoot) + "\")");
        return currentRoot;
        /*
        } finally {
            log(prefix + "Returning from getRecord.");
            globalPrefix = prefix;
        }
        */
    }
    
    private FSEntry entryFromRecord(CommonHFSCatalogFileRecord fileRecord) {

            if(fileRecord.getData().isSymbolicLink())
                return new HFSCommonFSLink(this, fileRecord);
            else if(fileRecord.getData().isHardFileLink()) {
                CommonHFSCatalogFileRecord iNode = lookupFileInode(fileRecord.getData().getHardLinkInode());
                if(iNode != null) {
                    return new HFSCommonFSFile(this, fileRecord, iNode);
                }
                else {
                    System.err.println("Looking up file iNode " + fileRecord.getData().getHardLinkInode() +
                        " (" + fileRecord.getKey().getParentID().toLong() +
                        ":\"" + getProperNodeName(fileRecord) + "\") FAILED!");
                    return new HFSCommonFSFile(this, fileRecord);
                }
            }
            else if(fileRecord.getData().isHardDirectoryLink()) {
                CommonHFSCatalogFolderRecord iNode = lookupDirectoryInode(fileRecord.getData().getHardLinkInode());
                if(iNode != null) {
                    return new HFSCommonFSFolder(this, fileRecord, iNode);
                }
                else {
                    System.err.println("Looking up directory iNode " + fileRecord.getData().getHardLinkInode() +
                        " (" + fileRecord.getKey().getParentID().toLong() +
                        ":\"" + getProperNodeName(fileRecord) + "\") FAILED!");
                    return new HFSCommonFSFile(this, fileRecord);
                }
            }
            else
                return new HFSCommonFSFile(this, fileRecord);
        
    }
    private FSEntry entryFromRecord(CommonHFSCatalogFolderRecord folderRecord) {
        return new HFSCommonFSFolder(this, folderRecord);
    }
    
    /*
    private FSEntry entriFromRecord(CommonHFSCatalogLeafRecord rec) {
        if(rec instanceof CommonHFSCatalogFileRecord) {
            return entryFromRecord((CommonHFSCatalogFileRecord)rec);
        }
        else if(rec instanceof CommonHFSCatalogFolderRecord) {
            return entryFromRecord((CommonHFSCatalogFolderRecord)rec);
        }
        else
            //throw new RuntimeException("Did not expect a " + rec.getClass() +
            //        " here.")
            return null;
    }
    */
    
    private CommonHFSCatalogFileRecord lookupFileInode(int inodeNumber) {
        long trueInodeNumber = Util.unsign(inodeNumber);
        CommonHFSCatalogLeafRecord res = getRecord(view.getRoot(), FILE_HARD_LINK_DIR,
                FILE_HARD_LINK_PREFIX + trueInodeNumber);
        if(res == null)
            return null; // Could not find any inode
        else if(res instanceof CommonHFSCatalogFileRecord)
            return (CommonHFSCatalogFileRecord) res;
        else
            throw new RuntimeException("Error in HFS+ file system structure: Found a " +
                    res.getClass() + " in file hard link dir for iNode" + trueInodeNumber);
    }

    private CommonHFSCatalogFolderRecord lookupDirectoryInode(int inodeNumber) {
        long trueInodeNumber = Util.unsign(inodeNumber);
        CommonHFSCatalogLeafRecord res = getRecord(view.getRoot(), DIRECTORY_HARD_LINK_DIR,
                DIRECTORY_HARD_LINK_PREFIX + trueInodeNumber);
        if(res == null)
            return null; // Could not find any inode
        else if(res instanceof CommonHFSCatalogFolderRecord)
            return (CommonHFSCatalogFolderRecord) res;
        else
            throw new RuntimeException("Error in HFS+ file system structure: Found a " +
                    res.getClass() + " in directory hard link dir for dir_" + trueInodeNumber);
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
        String nodeNameRaw = view.decodeString(record.getKey().getNodeName());
        if(doUnicodeFileNameComposition)
            return UnicodeNormalizationToolkit.getDefaultInstance().compose(nodeNameRaw);
        else
            return nodeNameRaw;
    }

    /**
     * Converts a HFS+ POSIX UTF-8 pathname into pathname component strings.
     *
     * @param path the bytes that make up the HFS+ POSIX UTF-8 pathname string.
     * @return the pathname components of the HFS+ POSIX UTF-8 pathname.
     */
    public static String[] splitPOSIXUTF8Path(byte[] path) {
        return splitPOSIXUTF8Path(path, 0, path.length);
    }

    /**
     * Converts a HFS+ POSIX UTF-8 pathname into pathname component strings.
     * 
     * @param path the bytes that make up the HFS+ POSIX UTF-8 pathname string.
     * @param offset offset to the beginning of string data in <code>path</code>.
     * @param length length of string data in <code>path</code>.
     * @return the pathname components of the HFS+ POSIX UTF-8 pathname.
     */
    public static String[] splitPOSIXUTF8Path(byte[] path, int offset, int length) {
        try {
            String s = new String(path, offset, length, "UTF-8");
            String[] res = s.split("/");

            /* As per the MacOS <-> POSIX translation semantics, all POSIX ':'
             * characters are really '/' characters in the MacOS world. */
            for(int i = 0; i < res.length; ++i) {
                res[i] = res[i].replace(':', '/'); // Slightly inefficient.
            }
            return res;
        } catch(UnsupportedEncodingException e) {
            throw new RuntimeException("REALLY UNEXPECTED: Could not decode UTF-8!", e);
        }
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
    
    String[] listNames(CommonHFSCatalogFolderRecord folderRecord) {
        CommonHFSCatalogLeafRecord[] subRecords = view.listRecords(folderRecord);
        LinkedList<String> result = new LinkedList<String>();
        for(int i = 0; i < subRecords.length; ++i) {
            CommonHFSCatalogLeafRecord curRecord = subRecords[i];
            result.add(getProperNodeName(curRecord));
        }
        return result.toArray(new String[result.size()]);
    }

    FSEntry[] listFSEntries(CommonHFSCatalogFolderRecord folderRecord) {
        CommonHFSCatalogLeafRecord[] subRecords = view.listRecords(folderRecord);
        LinkedList<FSEntry> result = new LinkedList<FSEntry>();
        for(int i = 0; i < subRecords.length; ++i) {
            CommonHFSCatalogLeafRecord curRecord = subRecords[i];
            FSEntry curEntry = null;
            
            if(curRecord instanceof CommonHFSCatalogFileRecord)
                curEntry = entryFromRecord((CommonHFSCatalogFileRecord)curRecord);
            else if(curRecord instanceof CommonHFSCatalogFolderRecord)
                curEntry = entryFromRecord((CommonHFSCatalogFolderRecord)curRecord);
                
            if(curEntry != null)
                result.addLast(curEntry);
        }
        return result.toArray(new FSEntry[result.size()]);
    }
    
    HFSCommonFSFolder lookupParentFolder(CommonHFSCatalogLeafRecord childRecord) {
        CommonHFSCatalogFolderRecord folderRec = lookupParentFolderRecord(childRecord);
        if(folderRec != null)
            return new HFSCommonFSFolder(this, folderRec);
        else
            return null;
    }

    private CommonHFSCatalogFolderRecord lookupParentFolderRecord(
            CommonHFSCatalogLeafRecord childRecord) {
        CommonHFSCatalogNodeID parentID = childRecord.getKey().getParentID();

        // Look for the thread record associated with the parent dir
        CommonHFSCatalogLeafRecord parent =
                view.getRecord(parentID, view.getEmptyString());
        if(parent == null) {
            if(parentID.toLong() == 1)
                return null; // There is no parent to root.
            else
                throw new RuntimeException("INTERNAL ERROR: No folder thread found for ID " +
                        parentID.toLong() + "!");
        }

        if(parent instanceof CommonHFSCatalogFolderThreadRecord) {
            CommonHFSCatalogFolderThread data =
                    ((CommonHFSCatalogFolderThreadRecord)parent).getData();
            CommonHFSCatalogLeafRecord rec =
                    view.getRecord(data.getParentID(), data.getNodeName());
            if(rec == null)
                return null;
            else if(rec instanceof CommonHFSCatalogFolderRecord)
                return (CommonHFSCatalogFolderRecord)rec;
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
     * Returns the underlying BaseHFSFileSystemView that serves the file system
     * handler with data.<br>
     * <b>Don't use this method if you want your code to be file system
     * independent!</b>
     * 
     * @return the underlying BaseHFSFileSystemView.
     */
    public BaseHFSFileSystemView getFSView() {
        return view;
    }

    @Override
    public void close() {
        view.close();
    }

    @Override
    public FSFolder getRoot() {
        return new HFSCommonFSFolder(this, view.getRoot());
    }

    @Override
    public String parsePosixPathnameComponent(String posixPathnameComponent) {
        return posixPathnameComponent.replace(':', '/'); // Slightly inefficient.
    }

    @Override
    public String generatePosixPathnameComponent(String fsPathnameComponent) {
        return fsPathnameComponent.replace("/", ":");
    }

    @Override
    public String[] getTargetPath(FSLink link, String[] parentDir) {
        if(link instanceof HFSCommonFSLink) {
            HFSCommonFSLink hfsLink = (HFSCommonFSLink) link;
            return getTruePathFromPosixPath(hfsLink.getLinkTargetPosixPath(), parentDir);
        }
        else
            throw new RuntimeException("Invalid type: " + link.getClass());
    }
}
