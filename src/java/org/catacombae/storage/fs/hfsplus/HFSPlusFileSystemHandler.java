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

package org.catacombae.storage.fs.hfsplus;

import org.catacombae.hfs.HFSVolume;
import org.catacombae.storage.fs.hfscommon.HFSCommonFileSystemHandler;
import org.catacombae.storage.io.DataLocator;
import org.catacombae.hfs.plus.HFSPlusVolume;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogFileRecord;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogFolderRecord;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogLeafRecord;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogNodeID;
import org.catacombae.storage.fs.FSEntry;
import org.catacombae.storage.fs.hfscommon.HFSCommonFSFile;
import org.catacombae.storage.fs.hfscommon.HFSCommonFSFolder;
import org.catacombae.storage.fs.hfscommon.HFSCommonFSLink;
import org.catacombae.util.IOUtil;
import org.catacombae.util.Util;

/**
 * @author <a href="http://www.catacombae.org/" target="_top">Erik Larsson</a>
 */
public class HFSPlusFileSystemHandler extends HFSCommonFileSystemHandler {
    private static final String FILE_HARD_LINK_DIR =
            "\u0000\u0000\u0000\u0000HFS+ Private Data";
    private static final String FILE_HARD_LINK_PREFIX = "iNode";
    private static final String DIRECTORY_HARD_LINK_DIR =
            ".HFS+ Private Directory Data" + (char)0x0d;
    private static final String DIRECTORY_HARD_LINK_PREFIX = "dir_";
    private static final String JOURNAL_INFO_BLOCK_FILE = ".journal_info_block";
    private static final String JOURNAL_FILE = ".journal";

    public HFSPlusFileSystemHandler(DataLocator fsLocator, boolean useCaching,
            boolean doUnicodeFileNameComposition, boolean hideProtected) {

        super(new HFSPlusVolume(fsLocator.createReadOnlyFile(),
                    useCaching), doUnicodeFileNameComposition, hideProtected);
    }

    protected HFSPlusFileSystemHandler(HFSVolume vol,
            boolean doUnicodeFileNameComposition, boolean hideProtected)
    {
        super(vol, doUnicodeFileNameComposition, hideProtected);
    }

    protected boolean shouldHide(CommonHFSCatalogLeafRecord rec) {
        // The only folder that contains hidden files is the root folder.
        CommonHFSCatalogNodeID parentID = rec.getKey().getParentID();
        if(!parentID.equals(parentID.getReservedID(CommonHFSCatalogNodeID.
                ReservedID.ROOT_FOLDER)))
        {
            return false;
        }

        String name = view.decodeString(rec.getKey().getNodeName());
        if(rec instanceof CommonHFSCatalogFileRecord) {
            if(name.equals(JOURNAL_INFO_BLOCK_FILE))
                return hideProtected;
            if(name.equals(JOURNAL_FILE))
                return hideProtected;
        }
        else if(rec instanceof CommonHFSCatalogFolderRecord) {
            if(name.equals(FILE_HARD_LINK_DIR))
                return hideProtected;
            if(name.equals(DIRECTORY_HARD_LINK_DIR))
                return hideProtected;
        }

        return false;
    }

    private String[] getHardFileLinkPath(int inodeNumber) {
        return new String[] {
            FILE_HARD_LINK_DIR,
            FILE_HARD_LINK_PREFIX + Util.unsign(inodeNumber)
        };
    }

    private String[] getHardDirectoryLinkPath(int inodeNumber) {
        return new String[] {
            DIRECTORY_HARD_LINK_DIR,
            DIRECTORY_HARD_LINK_PREFIX + Util.unsign(inodeNumber)
        };
    }

    @Override
    protected FSEntry entryFromRecord(CommonHFSCatalogFileRecord fileRecord) {
        if(fileRecord.getData().isSymbolicLink())
            return new HFSCommonFSLink(this, fileRecord);
        else if(fileRecord.getData().isHardFileLink()) {
            CommonHFSCatalogFileRecord iNode =
                    lookupFileInode(fileRecord.getData().getHardLinkInode());
            if(iNode != null) {
                return createFSFile(fileRecord, iNode);
            }
            else {
                System.err.println("Looking up file iNode " +
                        fileRecord.getData().getHardLinkInode() +
                        " (" + fileRecord.getKey().getParentID().toLong() +
                        ":\"" + getProperNodeName(fileRecord) + "\") FAILED!");

                return createFSFile(fileRecord);
            }
        }
        else if(fileRecord.getData().isHardDirectoryLink()) {
            CommonHFSCatalogFolderRecord iNode =
                    lookupDirectoryInode(fileRecord.getData().
                    getHardLinkInode());
            if(iNode != null) {
                return createFSFolder(fileRecord, iNode);
            }
            else {
                System.err.println("Looking up directory iNode " +
                        fileRecord.getData().getHardLinkInode() +
                        " (" + fileRecord.getKey().getParentID().toLong() +
                        ":\"" + getProperNodeName(fileRecord) + "\") FAILED!");

                return createFSFile(fileRecord);
            }
        }
        else {
            return super.entryFromRecord(fileRecord);
        }
    }

    private CommonHFSCatalogFileRecord lookupFileInode(int inodeNumber) {
        long trueInodeNumber = Util.unsign(inodeNumber);
        CommonHFSCatalogLeafRecord res =
                getRecord(view.getCatalogFile().getRootFolder(),
                FILE_HARD_LINK_DIR, FILE_HARD_LINK_PREFIX + trueInodeNumber);
        if(res == null) {
            // Could not find any inode
            return null;
        }
        else if(res instanceof CommonHFSCatalogFileRecord) {
            return (CommonHFSCatalogFileRecord) res;
        }
        else {
            throw new RuntimeException("Error in HFS+ file system structure: " +
                    "Found a " + res.getClass() + " in file hard link dir " +
                    "for iNode" + trueInodeNumber);
        }
    }

    private CommonHFSCatalogFolderRecord lookupDirectoryInode(int inodeNumber)
    {
        long trueInodeNumber = Util.unsign(inodeNumber);
        CommonHFSCatalogLeafRecord res =
                getRecord(view.getCatalogFile().getRootFolder(),
                DIRECTORY_HARD_LINK_DIR,
                DIRECTORY_HARD_LINK_PREFIX + trueInodeNumber);
        if(res == null) {
            // Could not find any inode
            return null;
        }
        else if(res instanceof CommonHFSCatalogFolderRecord) {
            return (CommonHFSCatalogFolderRecord) res;
        }
        else {
            throw new RuntimeException("Error in HFS+ file system structure: " +
                    "Found a " + res.getClass() + " in directory hard link " +
                    "dir for dir_" + trueInodeNumber);
        }
    }

    protected Long getLinkCount(CommonHFSCatalogFileRecord fr) {
        if(fr.getData().isHardFileLink()) {
            int inodeNumber = fr.getData().getHardLinkInode();
            CommonHFSCatalogFileRecord rec = lookupFileInode(inodeNumber);

            return Util.unsign(rec.getData().getPermissions().getSpecial());
        }
        else if(fr.getData().isHardDirectoryLink()) {
            int inodeNumber = fr.getData().getHardLinkInode();
            CommonHFSCatalogFolderRecord rec =
                    lookupDirectoryInode(inodeNumber);

            return Util.unsign(rec.getData().getPermissions().getSpecial());
        }
        else {
            return null;
        }
    }

    protected String[] getAbsoluteLinkPath(String[] path, int pathLength,
            CommonHFSCatalogFileRecord rec)
    {
        String[] absPath;

        if(rec.getData().isSymbolicLink()) {
            byte[] data = IOUtil.readFully(getReadableDataForkStream(rec));
            String posixPath = Util.readString(data, "UTF-8");
            String[] basePath =
                    Util.arrayCopy(path, 0, new String[pathLength - 1], 0,
                    pathLength - 1);
            absPath = getTruePathFromPosixPath(posixPath, basePath);
            if(absPath == null) {
                // Sorry pal, no luck in finding your link target
                // log(prefix + " getRecord: no link target found for posix " +
                //         "path \"" + posixPath + "\" with base path \"" +
                //         Util.concatenateStrings(basePath, "/") + "\"");
                return null;
            }
            else {
                // log(prefix + "  getRecord: absPath=" +
                //         Util.concatenateStrings(absPath, "/"));
            }
        }
        else if(rec.getData().isHardFileLink()) {
            absPath = getHardFileLinkPath(rec.getData().getHardLinkInode());
        }
        else if(rec.getData().isHardDirectoryLink()) {
            absPath = getHardDirectoryLinkPath(rec.getData().
                    getHardLinkInode());
        }
        else {
            absPath = null;
        }

        return absPath;
   }
}
