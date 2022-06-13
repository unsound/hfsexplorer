/*-
 * Copyright (C) 2021 Erik Larsson
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

import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import org.catacombae.hfsexplorer.types.alias.AliasHeader;
import org.catacombae.bplist.BinaryPlist;
import org.catacombae.hfsexplorer.types.dsstore.DSStoreFreeList;
import org.catacombae.hfsexplorer.types.dsstore.DSStoreHeader;
import org.catacombae.hfsexplorer.types.dsstore.DSStoreRootBlock;
import org.catacombae.hfsexplorer.types.dsstore.DSStoreTableOfContents;
import org.catacombae.hfsexplorer.types.dsstore.DSStoreTableOfContentsEntry;
import org.catacombae.hfsexplorer.types.dsstore.DSStoreTreeBlock;
import org.catacombae.util.Util;

/**
 * Utility which prints the contents of a .DS_Store file, including the content
 * inside some supported blobs.
 *
 * @author Erik Larsson
 */
public class DSStoreInfo {
    private static boolean verbose = false;

    private static void hexDump(byte[] data, int dataOffset, int dataSize,
            String insetString)
    {
        int hexdigits = 0;
        for(int j = dataSize; j != 0; j >>>= 8) {
            hexdigits += 2;
        }

        for(int j = 0; j < dataSize; j += 16) {
            int kMax = ((j + 16) <= dataSize) ? 16 : (dataSize - j);
            System.out.print(insetString);
            System.out.printf("%0" + hexdigits + "X | ", j);
            for(int k = 0; k < 16; ++k) {
                if(k != 0) {
                    System.out.print(" ");
                }
                if(k < kMax) {
                    System.out.print(Util.toHexStringBE(
                            (byte) data[dataOffset + j + k]));
                }
                else {
                    System.out.print("  ");
                }
            }
            System.out.print(" | ");
            for(int k = 0; k < 16; ++k) {
                if(k < kMax) {
                    byte curByte = data[dataOffset + j + k];
                    if(curByte < 0x20 || curByte >= 127) {
                        /* ASCII control character or outside ASCII range.
                         Represented by '.'. */
                        System.out.print(".");
                    }
                    else {
                        System.out.print((char) curByte);
                    }
                }
                else {
                    System.out.print("  ");
                }
            }

            System.out.println();
        }
    }

    private static void hexDump(byte[] data, String insetString) {
        hexDump(data, 0, data.length, insetString);
    }

    private static enum EntryType {
        EntryTypeInvalid(null),
        EntryTypeExtendedInfoEnd(-1),
        EntryTypeDirectoryName(0),
        EntryTypeDirectoryIDs(1),
        EntryTypeAbsolutePath(2),
        EntryTypeAppleShareZoneName(3),
        EntryTypeAppleShareServerName(4),
        EntryTypeAppleShareUserName(5),
        EntryTypeDriverName(6),
        EntryTypeRevisedAppleShareInfo(9),
        EntryTypeAppleRemoteAccessDialupInfo(10),
        EntryTypeFileNameUnicode(14),
        EntryTypeDirectoryNameUnicode(15),
        EntryTypeUNIXRelativePath(18),
        EntryTypeUNIXVolumePath(19),
        /**
         * Nested alias referencing the disk image where this file is located.
         */
        EntryTypeDiskImageAlias(20);

        private final Integer value;

        private EntryType(Integer value) {
            this.value = value;
        }

        public static EntryType lookupByValue(int value) {
            for(EntryType e : EntryType.class.getEnumConstants()) {
                if(e.value != null && e.value == value) {
                    return e;
                }
            }

            return EntryTypeInvalid;
        }
    };

    private static void printAlias(byte[] data, int offset, int size,
            String insetString)
    {
        AliasHeader ah = new AliasHeader(data, offset);
        System.out.println(insetString + "    Alias data:");
        ah.printFields(System.out, insetString + "     ");

        /* After the fixed-size header follows variable sized
         * entries each starting with a 2-byte type and a 2-byte
         * size field. */
        for(int j = AliasHeader.STRUCTSIZE; j < size; ) {
            short entryTypeValue =
                    Util.readShortBE(data, offset + j);
            j += 2;

            int entrySize =
                    Util.unsign(Util.readShortBE(data,
                    offset + j));
            j += 2;

            EntryType entryType =
                    EntryType.lookupByValue(entryTypeValue);

            System.out.print(insetString + "      ");
            switch(entryType) {
            case EntryTypeExtendedInfoEnd:
                System.out.println("Extended info end:");
                break;
            case EntryTypeDirectoryName:
                System.out.println("Directory name:");
                break;
            case EntryTypeDirectoryIDs:
                System.out.println("Directory IDs:");
                break;
            case EntryTypeAbsolutePath:
                System.out.println("Absolute path:");
                break;
            case EntryTypeAppleShareZoneName:
                System.out.println("AppleShare zone name:");
                break;
            case EntryTypeAppleShareServerName:
                System.out.println("AppleShare server name:");
                break;
            case EntryTypeAppleShareUserName:
                System.out.println("AppleShare user name:");
                break;
            case EntryTypeDriverName:
                System.out.println("Driver name:");
                break;
            case EntryTypeRevisedAppleShareInfo:
                System.out.println("Revised AppleShare info:");
                break;
            case EntryTypeAppleRemoteAccessDialupInfo:
                System.out.println("AppleRemoteAccess dialup " +
                        "info:");
                break;
            case EntryTypeFileNameUnicode:
                System.out.println("File name (Unicode):");
                break;
            case EntryTypeDirectoryNameUnicode:
                System.out.println("Directory name (Unicode):");
                break;
            case EntryTypeUNIXRelativePath:
                System.out.println("Relative path (UNIX-style):");
                break;
            case EntryTypeUNIXVolumePath:
                System.out.println("Volume path (UNIX-style):");
                break;
            case EntryTypeDiskImageAlias:
                System.out.println("Source disk image alias:");
                break;
            case EntryTypeInvalid:
            default:
                System.out.println("Unknown entry type " +
                        entryTypeValue + ":");
                break;
            }

            System.out.println(insetString + "        Size: " +
                    entrySize + " " +
                    "(0x" + Util.toHexStringBE((short) entrySize) +
                    ")");

            switch(entryType) {
            case EntryTypeExtendedInfoEnd:
                break;
            case EntryTypeDirectoryName:
                System.out.println(insetString + "        Name: " +
                        Util.readString(data, offset + j,
                        entrySize, "MacRoman"));
                break;
            case EntryTypeDirectoryIDs:
                hexDump(data, offset + j, entrySize,
                        insetString + "        ");
                break;
            case EntryTypeAbsolutePath:
                System.out.println(insetString + "        Path: " +
                        Util.readString(data, offset + j,
                        entrySize, "MacRoman"));
                break;
            case EntryTypeAppleShareZoneName:
                hexDump(data, offset + j, entrySize,
                        insetString + "        ");
                break;
            case EntryTypeAppleShareServerName:
                hexDump(data, offset + j, entrySize,
                        insetString + "        ");
                break;
            case EntryTypeAppleShareUserName:
                hexDump(data, offset + j, entrySize,
                        insetString + "        ");
                break;
            case EntryTypeDriverName:
                hexDump(data, offset + j, entrySize,
                        insetString + "        ");
                break;
            case EntryTypeRevisedAppleShareInfo:
                hexDump(data, offset + j, entrySize,
                        insetString + "        ");
                break;
            case EntryTypeAppleRemoteAccessDialupInfo:
                hexDump(data, offset + j, entrySize,
                        insetString + "        ");
                break;
            case EntryTypeFileNameUnicode:
            case EntryTypeDirectoryNameUnicode:
                System.out.println(insetString + "        Name " +
                        "length: " + Util.readShortBE(data,
                        offset + j));
                System.out.println(insetString + "        Name: " +
                        Util.readString(data,
                        offset + j + 2, entrySize - 2, "UTF-16BE"));
                break;
            case EntryTypeUNIXRelativePath:
                System.out.println(insetString + "        Path: " +
                        Util.readString(data, offset + j,
                        entrySize, "UTF-8"));
                break;
            case EntryTypeUNIXVolumePath:
                System.out.println(insetString + "        Path: " +
                        Util.readString(data, offset + j,
                        entrySize, "UTF-8"));
                break;
            case EntryTypeDiskImageAlias:
                printAlias(data, offset + j, entrySize,
                        insetString + "    ");
                break;
            case EntryTypeInvalid:
            default:
                hexDump(data, offset + j, entrySize,
                        insetString + "        ");
                break;
            }

            j += entrySize;
            if((entrySize % 2) != 0) {
                /* Odd lengths are padded with an extra 0x0 byte. */
                ++j;
            }
        }
    }

    private static void printBinaryPlistValue(BinaryPlist.Entry entry,
            String insetString)
    {
        byte marker = entry.getMarker();

        if(entry == null) {
            System.out.println(insetString + "<unsupported format>");
        }
        else if(entry instanceof BinaryPlist.NullEntry) {
            System.out.println(insetString + "NULL");
        }
        else if(entry instanceof BinaryPlist.BooleanEntry) {
            BinaryPlist.BooleanEntry e = (BinaryPlist.BooleanEntry) entry;
            System.out.println(insetString + "Boolean: " +
                    (e.getValue() ? "true" : "false"));
        }
        else if(entry instanceof BinaryPlist.FillEntry) {
            System.out.println(insetString + "Fill");
        }
        else if(entry instanceof BinaryPlist.IntegerEntry) {
            BinaryPlist.IntegerEntry e = (BinaryPlist.IntegerEntry) entry;
            byte[] valueData = e.getValueData();
            BigInteger value = e.getValue();
            System.out.println(insetString + "Integer " +
                    "(" + (valueData.length * 8) + "-bit): " + value + " " +
                    "(0x" + Util.byteArrayToHexString(valueData) + ")");
        }
        else if(entry instanceof BinaryPlist.DecimalEntry) {
            BinaryPlist.DecimalEntry e = (BinaryPlist.DecimalEntry) entry;
            byte[] valueData = e.getValueData();
            BigDecimal value = e.getValue();
            System.out.println(insetString + "Real number " +
                    "(" + (valueData.length * 8) + "-bit): " +
                    (value != null ? value : "<unsupported format>"));
        }
        else if(entry instanceof BinaryPlist.DateEntry) {
            BinaryPlist.DateEntry e = (BinaryPlist.DateEntry) entry;
            byte[] valueData = e.getValueData();
            Date d = e.getValue();
            System.out.println(insetString + "Date " +
                    "(" + (valueData.length * 8) + "-bit): " +
                    ((d != null) ? d : "<unsupported format>"));
        }
        else if(entry instanceof BinaryPlist.DataEntry) {
            BinaryPlist.DataEntry e = (BinaryPlist.DataEntry) entry;
            byte[] valueData = e.getValueData();
            System.out.println(insetString + "Data " +
                    "(" + valueData.length + " bytes):");
            hexDump(valueData, insetString + "  ");
        }
        else if(entry instanceof BinaryPlist.ASCIIStringEntry) {
            BinaryPlist.ASCIIStringEntry e =
                    (BinaryPlist.ASCIIStringEntry) entry;
            byte[] valueData = e.getValueData();
            System.out.println(insetString + "ASCII string " +
                    "(" + valueData.length + " characters):");
            System.out.println(insetString + "  " + e.getValue());
        }
        else if(entry instanceof BinaryPlist.UnicodeStringEntry) {
            BinaryPlist.UnicodeStringEntry e =
                    (BinaryPlist.UnicodeStringEntry) entry;
            byte[] valueData = e.getValueData();
            System.out.println(insetString + "Unicode " +
                    "string (" + (valueData.length / 2) + " characters):");
            System.out.println(insetString + "  " + e.getValue());
        }
        else if(entry instanceof BinaryPlist.ArrayEntry ||
                entry instanceof BinaryPlist.SetEntry)
        {
            BinaryPlist.ArrayEntry e = (BinaryPlist.ArrayEntry) entry;
            LinkedList<BinaryPlist.Entry> entries = e.getEntries();

            System.out.println(insetString +
                    ((marker & 0xF0) == 0xC0 ? "Set" : "Array") +
                    " (" + entries.size() + " elements):");

            for(BinaryPlist.Entry value : entries) {
                System.out.println(insetString + "  Value:");
                printBinaryPlistValue(value, insetString + "    ");
            }
        }
        else if(entry instanceof BinaryPlist.DictionaryEntry) {
            BinaryPlist.DictionaryEntry e = (BinaryPlist.DictionaryEntry) entry;
            LinkedList<BinaryPlist.Entry> keys = e.getKeys();

            System.out.println(insetString + "Dictionary (" + keys.size() +
                    " entries):");

            int i = 0;
            for(BinaryPlist.Entry key : keys) {
                System.out.println(insetString + "  Key:");
                printBinaryPlistValue(key, insetString + "    ");

                System.out.println(insetString + "  Value:");
                printBinaryPlistValue(e.getValue(i++), insetString + "    ");
            }
        }
        else if(entry instanceof BinaryPlist.UidEntry) {
            System.out.println(insetString + "<uid: 0x" +
                        Util.toHexStringBE(entry.getMarker()) + ">");
        }
        else {
            System.out.println(insetString + "<unknown: 0x" +
                        Util.toHexStringBE(entry.getMarker()) + ">");
        }
    }

    private static void printBinaryPlist(byte[] dsStoreData, int curOffset,
            int blobSize, String insetString)
    {
        BinaryPlist plist =
                new BinaryPlist(dsStoreData, curOffset, blobSize);

        /*
        plist.getFooter().printFields(System.out, insetString);
        */

        /* Start iterating at virtual offset 0, which is the root element. */
        printBinaryPlistValue(plist.getRootEntry(), insetString);
    }

    private static String getOffsetString(long offset) {
        return verbose ? " @ " + offset : "";
    }

    private static void printTreeBlockRecursive(byte[] dsStoreData,
            DSStoreRootBlock rootBlock, int inset, int blockID)
    {
        String insetString = "";
        while(inset != 0) {
            insetString += "        ";
            --inset;
        }

        long dataBlockLocator = rootBlock.getOffsetList()[blockID];
        long dataBlockOffset = (dataBlockLocator & ~0x1FL);
        long dataBlockSize = 1L << (dataBlockLocator & 0x1F);

        System.out.println(insetString + "  First data block:");
        System.out.println(insetString + "    (offset: " + dataBlockOffset +
                ")");
        System.out.println(insetString + "    (size: " + dataBlockSize + ")");

        int blockMode =
                Util.readIntBE(dsStoreData, 4 + (int) dataBlockOffset + 0);
        int recordCount =
                Util.readIntBE(dsStoreData, 4 + (int) dataBlockOffset + 4);
        System.out.println(insetString + "    block mode: 0x" +
                Util.toHexStringBE(blockMode));
        System.out.println(insetString + "    record count: 0x" +
                Util.toHexStringBE(recordCount));

        /* Read the records. */
        int curOffset = 4 + (int) dataBlockOffset + 8;
        for(int i = 0; i < recordCount; ++i) {
            int childNodeBlockID = 0;
            System.out.println(insetString + "    Record " + (i + 1) +
                    getOffsetString(curOffset) + ":");
            if(blockMode != 0x0) {
                /* "index nodes" have the child node block id prepended to
                 * the record so that we can descend further into the
                 * tree. */
                childNodeBlockID = Util.readIntBE(dsStoreData, curOffset);
                System.out.println(insetString + "      Child node block ID" +
                        getOffsetString(curOffset) + ": " + childNodeBlockID);
                curOffset += 4;
            }

            int filenameLength = Util.readIntBE(dsStoreData, curOffset);
            System.out.println(insetString + "      Filename length" +
                    getOffsetString(curOffset) + ": " + filenameLength);
            curOffset += 4;

            String filename =
                    Util.readString(dsStoreData, curOffset, filenameLength * 2,
                    "UTF-16BE");
            System.out.println(insetString + "      Filename" +
                    getOffsetString(curOffset) + ": " + filename);
            curOffset += filenameLength * 2;

            int structID = Util.readIntBE(dsStoreData, curOffset);
            System.out.println(insetString + "      Structure ID" +
                    getOffsetString(curOffset) + ": " +
                    Util.toASCIIString(structID) + " " +
                    "(0x" + Util.toHexStringBE(structID) + ")");
            curOffset += 4;

            int structType = Util.readIntBE(dsStoreData, curOffset);
            System.out.println(insetString + "      Structure type" +
                    getOffsetString(curOffset) + ": " +
                    Util.toASCIIString(structType) + " " +
                    "(0x" + Util.toHexStringBE(structType) + ")");
            curOffset += 4;

            if(Util.toASCIIString(structType).equals("long")) {
                /* A long is a 4 byte (32-bit) big-endian integer. */
                int value = Util.readIntBE(dsStoreData, curOffset);
                System.out.println(insetString + "      Value" +
                        getOffsetString(curOffset) + ": " + value + " " + "/ " +
                        "0x" + Util.toHexStringBE(value));
                curOffset += 4;
            }
            else if(Util.toASCIIString(structType).equals("shor")) {
                /* A long is a 2 byte (16-bit) big-endian integer, padded to 4
                 * bytes. */
                short padding = Util.readShortBE(dsStoreData, curOffset);
                System.out.println(insetString + "      Padding" +
                        getOffsetString(curOffset) + ": " + padding + " / " +
                        "0x" + Util.toHexStringBE(padding));
                curOffset += 2;

                short value = Util.readShortBE(dsStoreData, curOffset);
                System.out.println(insetString + "      Value" +
                        getOffsetString(curOffset) + ": " + value + " / " +
                        "0x" + Util.toHexStringBE(value));
                curOffset += 2;
            }
            else if(Util.toASCIIString(structType).equals("blob")) {
                /* A blob has a size (32 bits) followed by variable-size binary
                 * data. */
                int blobSize = Util.readIntBE(dsStoreData, curOffset);
                System.out.println(insetString + "      Blob size" +
                        getOffsetString(curOffset) + ": " +
                        blobSize);
                curOffset += 4;

                System.out.println(insetString + "      Blob data" +
                        getOffsetString(curOffset) + ":");
                hexDump(dsStoreData, curOffset, blobSize,
                        insetString + "        ");

                if(Util.toASCIIString(structID).equals("pict")) {
                    /* This data is in Mac OS alias format, pointing at the
                     * location of the background image. */
                    printAlias(dsStoreData, curOffset, blobSize, insetString);
                }
                else if(Util.toASCIIString(structID).equals("BKGD") &&
                        blobSize == 12) {
                    /*
                     * Background of the Finder window. Starts with a FourCC
                     * which then defines the rest of the fields.
                     */

                    System.out.println(insetString + "      Finder " +
                            "background:");

                    int backgroundType = Util.readIntBE(dsStoreData, curOffset);
                    System.out.print(insetString + "        Type: '" +
                            Util.toASCIIString(backgroundType) + "' ");

                    if(Util.toASCIIString(backgroundType).equals("DefB")) {
                        System.out.println("(Default background)");
                        System.out.println(insetString + "        Reserved: " +
                                "0x" + Util.byteArrayToHexString(dsStoreData,
                                curOffset + 4, 8));
                    }
                    else if(Util.toASCIIString(backgroundType).equals("ClrB")) {
                        System.out.println("(Solid color)");

                        short red =
                                Util.readShortBE(dsStoreData, curOffset + 4);
                        short green =
                                Util.readShortBE(dsStoreData, curOffset + 6);
                        short blue =
                                Util.readShortBE(dsStoreData, curOffset + 8);
                        short unknown =
                                Util.readShortBE(dsStoreData, curOffset + 10);
                        System.out.println(insetString + "        Red: " + red);
                        System.out.println(insetString + "        Green: " +
                                green);
                        System.out.println(insetString + "        Blue: " +
                                blue);
                        System.out.println(insetString + "        Reserved / " +
                                "unknown: 0x" + Util.toHexStringBE(unknown));
                    }
                    else if(Util.toASCIIString(backgroundType).equals("PctB")) {
                        System.out.println("(Picture background)");
                        int pictBlobSize =
                                Util.readIntBE(dsStoreData, curOffset + 4);
                        int unknown =
                                Util.readIntBE(dsStoreData, curOffset + 8);
                        System.out.println(insetString + "        Blob size: " +
                                pictBlobSize);
                        System.out.println(insetString + "        Reserved / " +
                                "unknown: 0x" + Util.toHexStringBE(unknown));
                    }
                    else {
                        System.out.println(insetString + "        Type: " +
                                "Unknown ('" +
                                Util.toASCIIString(backgroundType) + "')");
                        System.out.println(insetString + "        Unknown / " +
                                "Reserved: 0x" + Util.byteArrayToHexString(
                                dsStoreData, curOffset + 4, 8));
                    }
                }
                else if(Util.toASCIIString(structID).equals("Iloc") &&
                        blobSize == 16)
                {
                    /*
                     * Location of file's icon within the Finder window.
                     */

                    System.out.println(insetString + "      Finder " +
                            "icon location:");

                    int x = Util.readIntBE(dsStoreData, curOffset);
                    int y = Util.readIntBE(dsStoreData, curOffset + 4);
                    System.out.println(insetString + "        Horizontal " +
                            "(X): " + x);
                    System.out.println(insetString + "        Vertical " +
                            "(Y): " + y);
                    System.out.println(insetString + "        Unknown / " +
                            "Reserved: 0x" + Util.byteArrayToHexString(
                            dsStoreData, curOffset + 8, 8));
                }
                else if(Util.toASCIIString(structID).equals("bwsp")) {
                    System.out.println(insetString + "      Finder " +
                            "window properties:");

                    printBinaryPlist(dsStoreData, curOffset, blobSize,
                            insetString + "        ");
                }
                else if(Util.toASCIIString(structID).equals("fwi0")) {
                    System.out.println(insetString + "      Finder " +
                            "window information:");

                    short top = Util.readShortBE(dsStoreData, curOffset);
                    short left = Util.readShortBE(dsStoreData, curOffset + 2);
                    short bottom = Util.readShortBE(dsStoreData, curOffset + 4);
                    short right = Util.readShortBE(dsStoreData, curOffset + 6);

                    System.out.println(insetString + "        Top: " + top);
                    System.out.println(insetString + "        Left: " + left);
                    System.out.println(insetString + "        Bottom: " +
                            bottom);
                    System.out.println(insetString + "        Right: " + right);

                    int view = Util.readIntBE(dsStoreData, curOffset + 8);
                    final String viewLabel;
                    if(Util.toASCIIString(view).equals("icnv")) {
                        viewLabel = "Icon view";
                    }
                    else if(Util.toASCIIString(view).equals("Nlsv")) {
                        viewLabel = "List view";
                    }
                    else if(Util.toASCIIString(view).equals("clmv")) {
                        viewLabel = "Column view";
                    }
                    else if(Util.toASCIIString(view).equals("Flwv")) {
                        viewLabel = "Cover flow";
                    }
                    else {
                        viewLabel = "Unknown";
                    }

                    System.out.println(insetString + "        View: " +
                            viewLabel + " (" + Util.toASCIIString(view) + " " +
                            "/ 0x" + Util.toHexStringBE(view) + ")");

                    System.out.println(insetString + "        Unknown / " +
                            "Reserved: 0x" + Util.byteArrayToHexString(
                            dsStoreData, curOffset + 12, 4));
                }
                else if(Util.toASCIIString(structID).equals("icvo")) {
                    System.out.println(insetString + "      Finder " +
                            "icon view options:");

                    int magic = Util.readIntBE(dsStoreData, curOffset);

                    Long unknown = null;
                    Short iconViewSize = null;
                    Integer keepArrangedBy = null;
                    Integer iconLabelPosition = null;
                    byte[] flags = null;

                    if(Util.toASCIIString(magic).equals("icvo")) {
                        /* 18-byte format */
                        unknown = Util.readLongBE(dsStoreData, curOffset + 4);
                        iconViewSize =
                                Util.readShortBE(dsStoreData, curOffset + 12);
                        keepArrangedBy =
                                Util.readIntBE(dsStoreData, curOffset + 14);
                    }
                    else if(Util.toASCIIString(magic).equals("icv4")) {
                        /* 26-byte format */
                        iconViewSize =
                                Util.readShortBE(dsStoreData, curOffset + 4);
                        keepArrangedBy =
                                Util.readIntBE(dsStoreData, curOffset + 6);
                        iconLabelPosition =
                                Util.readIntBE(dsStoreData, curOffset + 10);
                        flags =
                                Util.readByteArrayBE(dsStoreData,
                                curOffset + 14, 12);
                    }
                    else {
                        System.out.println(insetString + "        " +
                                "<Unrecognized magic for type: " +
                                "'" + Util.toASCIIString(magic) + "' " +
                                "(0x" + Util.toHexStringBE(magic) + ")>");
                    }

                    if(unknown != null) {
                        System.out.println(insetString + "        Unknown: " +
                                "0x" + Util.toHexStringBE(unknown));
                    }
                    if(iconViewSize != null) {
                        System.out.println(insetString + "        Icon view " +
                                "size: " + iconViewSize);
                    }
                    if(keepArrangedBy != null) {
                        System.out.println(insetString + "        Keep " +
                                "arranged by: '" +
                                Util.toASCIIString(keepArrangedBy) + "' (0x" +
                                Util.toHexStringBE(keepArrangedBy) + ")");
                    }
                    if(iconLabelPosition != null) {
                        System.out.println(insetString + "        Icon label " +
                                "position: '" +
                                Util.toASCIIString(iconLabelPosition) + "' " +
                                "(0x" + Util.toHexStringBE(iconLabelPosition) +
                                ")");
                    }
                    if(flags != null) {
                        System.out.println(insetString + "        Flags: 0x" +
                                Util.byteArrayToHexString(flags));
                        if((flags[1] & 0x1) != 0) {
                            System.out.println(insetString + "          " +
                                    "Show item info");
                        }
                        if((flags[11] & 0x1) != 0) {
                            System.out.println(insetString + "          " +
                                    "Show icon preview");
                        }
                    }
                }

                curOffset += blobSize;
            }
            else if(Util.toASCIIString(structType).equals("type")) {
                /* A long is a 4 byte (32-bit) big-endian integer. */
                int value = Util.readIntBE(dsStoreData, curOffset);
                System.out.println(insetString + "      Value" +
                        getOffsetString(curOffset) + ": '" +
                        Util.toASCIIString(value) + "'");
                curOffset += 4;
            }
            else if(Util.toASCIIString(structType).equals("ustr")) {
                /* A long is a 4 byte (32-bit) big-endian integer. */
                int length = Util.readIntBE(dsStoreData, curOffset);
                System.out.println(insetString + "      String length" +
                        getOffsetString(curOffset) + ": " + length + " / " +
                        "0x" + Util.toHexStringBE(length));
                curOffset += 4;

                String string =
                        Util.readString(dsStoreData, curOffset, length * 2,
                        "UTF-16BE");
                System.out.println(insetString + "      String" +
                        getOffsetString(curOffset) + ": " + string);
                curOffset += length * 2;
            }
            else if(Util.toASCIIString(structType).equals("comp")) {
                /* A comp is an 8 byte (64-bit) big-endian integer. */
                long value = Util.readLongBE(dsStoreData, curOffset);
                System.out.println(insetString + "      Value" +
                        getOffsetString(curOffset) + ": " + value + " " + "/ " +
                        "0x" + Util.toHexStringBE(value));
                curOffset += 8;
            }
            else if(Util.toASCIIString(structType).equals("dutc")) {
                /* A dutc is a UTC timestamp, 8 bytes and stored as 1/65536
                 * second intervals since 1904 (start of the pre-UNIX Mac OS
                 * epoch). */
                long value = Util.readLongBE(dsStoreData, curOffset);
                System.out.println(insetString + "      Timestamp" +
                        getOffsetString(curOffset) + ": " + value +
                        " / 0x" + Util.toHexStringBE(value));
                curOffset += 8;
            }
            else if(Util.toASCIIString(structType).equals("bool")) {
                /* A bool is a one-byte boolean value expected to be 0 or 1. */
                byte value = dsStoreData[curOffset];
                System.out.println(insetString + "      Value" +
                        getOffsetString(curOffset) + ": " + value + " / " +
                        "0x" + Util.toHexStringBE((byte) value));
                curOffset += 1;
            }
            else {
                throw new RuntimeException("Unknown struct type " +
                        "\"" + Util.toASCIIString(structType) + "\" " +
                        "(0x" + Util.toHexStringBE(structType) + ").");
            }

            if(blockMode != 0x0) {
                /* Print the contents of the child node in a depth-first
                 * manner. */
                System.out.println(insetString + "      Child node " +
                        childNodeBlockID + ":");
                printTreeBlockRecursive(dsStoreData, rootBlock, inset + 1,
                        childNodeBlockID);
            }
        }
    }

    public static void main(String[] args) {
        final RandomAccessFile dsStoreFile;
        final byte[] dsStoreData;
        final String filename;

        if(args.length == 2 && args[0].equals("-v")) {
            verbose = true;
            filename = args[1];
        }
        else if(args.length == 1) {
            filename = args[0];
        }
        else {
            System.err.println("usage: DSStoreInfo <file>");
            System.exit(1);
            return;
        }

        try {
            dsStoreFile = new RandomAccessFile(filename, "r");
        } catch(IOException e) {
            System.err.println("Error while opening .DS_Store file: " +
                    e.getMessage());
            System.exit(1);
            return;
        }

        try {
            final long dsStoreFileLength = dsStoreFile.length();
            if(dsStoreFileLength < 0 || dsStoreFileLength > Integer.MAX_VALUE) {
                System.err.println(".DS_Store file is unreasonably large " +
                        "(" + dsStoreFileLength + "). Aborting...");
                System.exit(1);
                return;
            }
            dsStoreData = new byte[(int) dsStoreFileLength];
        } catch(IOException e) {
            System.err.println("Error while querying .DS_Store file size: " +
                    e.getMessage());
            System.exit(1);
            return;
        }

        try {
            int bytesRead = dsStoreFile.read(dsStoreData);
            if(bytesRead < dsStoreData.length) {
                System.err.println("Partial read while reading .DS_Store " +
                        "file data: " + bytesRead + " / " + dsStoreData.length +
                        " read");
                System.exit(1);
                return;
            }
        } catch(IOException e) {
            System.err.println("Error while reading .DS_Store file data: " +
                    e.getMessage());
            System.exit(1);
            return;
        }

        DSStoreHeader h = new DSStoreHeader(dsStoreData, 0);
        h.print(System.out, "");

        if(!Arrays.equals(h.getRawSignature(), DSStoreHeader.SIGNATURE)) {
            System.err.println("Mismatching root block offsets, this is not " +
                    "a valid .DS_Store file.");
            System.exit(1);
            return;
        }

        if(h.getRawRootBlockOffset1() != h.getRawRootBlockOffset2()) {
            System.err.println("Mismatching root block offsets, this is not " +
                    "a valid .DS_Store file.");
            System.exit(1);
            return;
        }

        DSStoreRootBlock rootBlock =
                new DSStoreRootBlock(dsStoreData, h.getRawRootBlockOffset1());
        rootBlock.print(System.out, "");

        DSStoreTableOfContents toc =
                new DSStoreTableOfContents(dsStoreData,
                h.getRawRootBlockOffset1() + rootBlock.maxSize());
        toc.print(System.out, "");

        DSStoreFreeList freeList =
                new DSStoreFreeList(dsStoreData, h.getRawRootBlockOffset1() +
                rootBlock.maxSize() + toc.occupiedSize());
        freeList.print(System.out, "");

        for(int i = 0; i < toc.getTocCount(); ++i) {
            DSStoreTableOfContentsEntry tocEntry = toc.getTocEntry(i);
            long treeLocator =
                    rootBlock.getOffsetList()[tocEntry.getRawTocValue()];
            long treeOffset = (treeLocator & ~0x1FL);
            long treeSize = 1L << (treeLocator & 0x1F);
            System.out.println("Printing contents of tree " + (i + 1) + " " +
                    "(\"" + Util.toASCIIString(tocEntry.getTocName()) + "\") " +
                    "/ raw locator 0x" + Util.toHexStringBE(treeLocator) + ":");
            System.out.println("  (offset: " + treeOffset + ")");
            System.out.println("  (size: " + treeSize + ")");

            DSStoreTreeBlock b =
                    new DSStoreTreeBlock(dsStoreData, 4 + (int) treeOffset);
            b.print(System.out, "  ");

            printTreeBlockRecursive(dsStoreData, rootBlock, 0,
                    b.getRawFirstDataBlockID());
        }

        try {
            dsStoreFile.close();
        } catch(IOException e) {
            System.err.println("Error while closing .DS_Store file: " +
                    e.getMessage());
            System.exit(1);
            return;
        }
    }
}
