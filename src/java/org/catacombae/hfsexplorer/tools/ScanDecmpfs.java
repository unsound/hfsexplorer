/*-
 * Copyright (C) 2014 Erik Larsson
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

package org.catacombae.hfsexplorer.tools;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import org.catacombae.hfs.AttributesFile;
import org.catacombae.hfs.types.decmpfs.DecmpfsHeader;
import org.catacombae.hfs.types.hfscommon.CommonBTIndexRecord;
import org.catacombae.hfs.types.hfscommon.CommonBTNode;
import org.catacombae.hfs.types.hfscommon.CommonHFSAttributesIndexNode;
import org.catacombae.hfs.types.hfscommon.CommonHFSAttributesKey;
import org.catacombae.hfs.types.hfscommon.CommonHFSAttributesLeafNode;
import org.catacombae.hfs.types.hfscommon.CommonHFSAttributesLeafRecord;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogLeafRecord;
import org.catacombae.hfs.types.hfsplus.HFSPlusAttributesData;
import org.catacombae.hfs.types.hfsplus.HFSPlusAttributesLeafRecordData;
import org.catacombae.io.ReadableFileStream;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.storage.fs.FileSystemHandler;
import org.catacombae.storage.fs.FileSystemHandlerFactory;
import org.catacombae.storage.fs.FileSystemMajorType;
import org.catacombae.storage.fs.hfscommon.HFSCommonFileSystemRecognizer;
import org.catacombae.storage.fs.hfsplus.HFSPlusFileSystemHandler;
import org.catacombae.storage.io.ReadableStreamDataLocator;
import org.catacombae.storage.io.win32.ReadableWin32FileStream;
import org.catacombae.util.Util;

/**
 * @author <a href="http://www.catacombae.org/" target="_top">Erik Larsson</a>
 */
public class ScanDecmpfs {
    public static void main(String[] args) {
        final boolean verbose;
        final String fsPath;

        if(args.length == 2 && args[0].equals("-v")) {
            verbose = true;
            fsPath = args[1];
        }
        else if(args.length == 1) {
            verbose = false;
            fsPath = args[0];
        }
        else {
            System.err.println("usage: ScanDecmpfs [-v] <device|file>");
            System.err.println();
            System.err.println("    Scans an HFS+/HFSX volume for decmpfs " +
                    "compressed files and prints the");
            System.err.println("    file's CNID, compression type and " +
                    "decompressed size.");
            System.err.println("    If '-v' is supplied, the path of the " +
                    "compressed file is also resolved and");
            System.err.println("    printed after the decompressed size.");
            System.exit(1);
            return;
        }

        final ReadableRandomAccessStream fsStream =
                ReadableWin32FileStream.isSystemSupported() ?
                    new ReadableWin32FileStream(fsPath) :
                    new ReadableFileStream(fsPath);

        final FileSystemHandlerFactory fsHandlerFactory;
        switch(HFSCommonFileSystemRecognizer.detectFileSystem(fsStream, 0)) {
            case HFS_WRAPPED_HFS_PLUS:
            case HFS_PLUS:
                fsHandlerFactory =
                        FileSystemMajorType.APPLE_HFS_PLUS.
                        createDefaultHandlerFactory();
                break;
            case HFSX:
                fsHandlerFactory =
                        FileSystemMajorType.APPLE_HFSX.
                        createDefaultHandlerFactory();
                break;
            default:
                System.err.println("No HFS+/HFSX filesystem detected.");
                System.exit(1);
                return;
        }

        final FileSystemHandler fsHandlerGeneric =
                fsHandlerFactory.createHandler(
                new ReadableStreamDataLocator(fsStream));
        if(!(fsHandlerGeneric instanceof HFSPlusFileSystemHandler)) {
            System.err.println("Unexpected: File system handler object is " +
                    "not of HFSPlusFileSystemHandler class (class: " +
                    fsHandlerGeneric.getClass() + ").");
            System.exit(1);
            return;
        }

        final HFSPlusFileSystemHandler fsHandler =
                (HFSPlusFileSystemHandler) fsHandlerGeneric;
        final AttributesFile attributesFile =
                fsHandler.getFSView().getAttributesFile();

        LinkedList<Long> nodeQueue = new LinkedList<Long>();
        nodeQueue.addLast(attributesFile.getRootNodeNumber());

        /* Depth-first search for "com.apple.decmpfs" attribute records. */
        while(!nodeQueue.isEmpty()) {
            long curNodeNumber = nodeQueue.removeFirst();
            CommonBTNode curNode = attributesFile.getNode(curNodeNumber);

            if(curNode instanceof CommonHFSAttributesIndexNode) {
                CommonHFSAttributesIndexNode indexNode =
                        (CommonHFSAttributesIndexNode) curNode;

                List<CommonBTIndexRecord<CommonHFSAttributesKey>> records =
                        indexNode.getBTKeyedRecords();
                ListIterator<CommonBTIndexRecord<CommonHFSAttributesKey>> it =
                        records.listIterator(records.size());

                /* For the search to be depth first, add elements in reverse
                 * order. */
                while(it.hasPrevious()) {
                    nodeQueue.addFirst(it.previous().getIndex());
                }
            }
            else if(curNode instanceof CommonHFSAttributesLeafNode) {
                final CommonHFSAttributesLeafNode leafNode =
                        (CommonHFSAttributesLeafNode) curNode;
                for(CommonHFSAttributesLeafRecord rec :
                        leafNode.getLeafRecords())
                {
                    final CommonHFSAttributesKey k = rec.getKey();
                    if(!new String(k.getAttrName(), 0, k.getAttrNameLen()).
                            equals("com.apple.decmpfs"))
                    {
                        continue;
                    }
                    else if(k.getStartBlock() != 0) {
                        System.err.println("[WARNING] " +
                                k.getFileID().toLong() + " has " +
                                "com.apple.decmpfs attribute with non-0 " +
                                "start block (" + k.getStartBlock() + "). " +
                                "Skipping...");
                        continue;
                    }

                    final HFSPlusAttributesLeafRecordData data =
                            rec.getRecordData();
                    if(!(data instanceof HFSPlusAttributesData)) {
                        System.err.println("[WARNING] " +
                                k.getFileID().toLong() + " has " +
                                "com.apple.decmpfs attribute without inline " +
                                "data (" + data.getRecordTypeAsString() +
                                "). Skipping...");
                        continue;
                    }

                    final DecmpfsHeader header =
                            new DecmpfsHeader(
                            ((HFSPlusAttributesData) data).getAttrData(), 0);
                    if(header.getMagic() != DecmpfsHeader.MAGIC) {
                        System.err.println("[WARNING] " +
                                k.getFileID().toLong() + " has " +
                                "com.apple.decmpfs attribute with " +
                                "mismatching magic (expected: 0x" +
                                Util.toHexStringBE((int) DecmpfsHeader.MAGIC) +
                                ", actual: 0x" +
                                Util.toHexStringBE(header.getRawMagic()) +
                                "). Skipping...");
                        continue;
                    }

                    final StringBuilder pathBuilder;
                    if(verbose) {
                        pathBuilder = new StringBuilder();

                        boolean firstComponent = true;
                        for(CommonHFSCatalogLeafRecord pathComponent :
                                fsHandler.getFSView().getCatalogFile().
                                getPathTo(k.getFileID()))
                        {
                            /* Skip name of root directory. */
                            if(!firstComponent) {
                                final String nodeName =
                                        fsHandler.getFSView().decodeString(
                                        pathComponent.getKey().getNodeName());
                                pathBuilder.append('/').append(nodeName);
                            }
                            else {
                                firstComponent = false;
                            }
                        }
                    }
                    else {
                        pathBuilder = null;
                    }

                    System.out.println("CNID: " + k.getFileID().toLong() + " " +
                            "Type: " + header.getCompressionType() + " " +
                            "Size: " + header.getFileSize() +
                            (pathBuilder != null ? " Path: " +
                            pathBuilder.toString() : ""));
                }
            }
            else {
                System.err.println("[WARNING] Unexpected attributes B-tree " +
                        "node type: " + curNode.getClass());
            }
        }

        fsHandler.close();
        System.exit(0);
    }
}
