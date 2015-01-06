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

package org.catacombae.hfsexplorer.fs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.catacombae.hfsexplorer.types.applesingle.AppleSingleHeader;
import org.catacombae.hfsexplorer.types.applesingle.AttributeEntry;
import org.catacombae.hfsexplorer.types.applesingle.AttributeHeader;
import org.catacombae.hfsexplorer.types.applesingle.EntryDescriptor;
import org.catacombae.util.Util;
import org.catacombae.util.Util.Pair;

/**
 * @author <a href="http://www.catacombae.org/" target="_top">Erik Larsson</a>
 */
public class AppleSingleBuilder {
    /*
     * Layout of an AppleSingle file:
     * -------------------------------|
     * | AppleSingleHeader (26 bytes) |
     * |------------------------------|
     * | EntryDescriptor[0] (12 bytes)|
     * --------------------------------
     * :                              : Number of entries is determined by the numEntries field in
     * -------------------------------| the header.
     * | EntryDescriptor[n]           |
     * |------------------------------|
     * :                              : <- the format allows for padding
     * |------------------------------| <- pos, len determined by EntryDescriptor[0]
     * | entry[0]                     |
     * :                              :
     * -------------------------------| <- pos, len determined by EntryDescriptor[n]
     * | entry[n]                     |
     * :                              :
     * -------------------------------|
     */

    /**
     * Type enumerating all valid file types for an AppleSingle file.
     */
    public static enum FileType {
        /**
         * Indicates an AppleSingle file, containing both a data fork and a resource fork (and
         * optionally other attributes).
         */
        APPLESINGLE(0x00051600),
        /**
         * Indicates an AppleDouble file, containing only a resource fork (and optionally other
         * attributes). The same file format (AppleSingle) is used, but the data fork is omitted.
         */
        APPLEDOUBLE(0x00051607);

        private final int magic;

        private FileType(int magic) {
            this.magic = magic;
        }

        /**
         * Returns the magic number associated with this file type.
         * @return the magic number associated with this file type.
         */
        public int getMagic() { return magic; }
    }

    public static enum AppleSingleVersion {
        /** The AppleSingle format used in A/UX and possibly Mac OS Classic / early Mac OS X versions. */
        VERSION_1_0(0x00010000),
        /** The version used in Mac OS X Leopard and possibly earlier Mac OS X versions. */
        VERSION_2_0(0x00020000);

        private final int versionNumber;

        private AppleSingleVersion(int versionNumber) {
            this.versionNumber = versionNumber;
        }

        public int getVersionNumber() {
            return versionNumber;
        }
    }

    public static enum FileSystem {
        MACOS("Macintosh"),
        MACOS_X("Mac OS X"),
        PRODOS("ProDOS"),
        MS_DOS("MS-DOS"),
        UNIS("Unix"),
        VMS("VAX VMS");

        private final String identifier;

        private FileSystem(String identifier) {
            this.identifier = identifier;
        }

        public byte[] getIdentifier() {
            char[] chars = identifier.toCharArray();
            byte[] result = new byte[16];
            for(int i = 0; i < result.length; ++i) {
                if(i < chars.length)
                    result[i] = (byte)(chars[i] & 0x7F);
                else
                    result[i] = (byte)' '; // Padding with spaces
            }
            return result;
        }
    }

    public static enum EntryType {
        DATA_FORK(EntryDescriptor.ENTRY_ID_DATA),
        RESOURCE_FORK(EntryDescriptor.ENTRY_ID_RESOURCE),
        REAL_NAME(EntryDescriptor.ENTRY_ID_REALNAME),
        COMMENT(EntryDescriptor.ENTRY_ID_COMMENT),
        ICON_BW(EntryDescriptor.ENTRY_ID_ICONBW),
        ICON_COLOR(EntryDescriptor.ENTRY_ID_ICONCOLOR),
        FILE_INFO(EntryDescriptor.ENTRY_ID_UNUSED),
        FINDER_INFO(EntryDescriptor.ENTRY_ID_FINDERINFO);

        private final int typeNumber;

        private EntryType(int typeNumber) {
            this.typeNumber = typeNumber;
        }

        public int getTypeNumber() { return typeNumber; }
    }

    private static final byte[] EMPTY_RESOURCE_FORK = {
        (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x1e,
        (byte) 0x54, (byte) 0x68, (byte) 0x69, (byte) 0x73,
        (byte) 0x20, (byte) 0x72, (byte) 0x65, (byte) 0x73,
        (byte) 0x6f, (byte) 0x75, (byte) 0x72, (byte) 0x63,
        (byte) 0x65, (byte) 0x20, (byte) 0x66, (byte) 0x6f,
        (byte) 0x72, (byte) 0x6b, (byte) 0x20, (byte) 0x69,
        (byte) 0x6e, (byte) 0x74, (byte) 0x65, (byte) 0x6e,
        (byte) 0x74, (byte) 0x69, (byte) 0x6f, (byte) 0x6e,
        (byte) 0x61, (byte) 0x6c, (byte) 0x6c, (byte) 0x79,
        (byte) 0x20, (byte) 0x6c, (byte) 0x65, (byte) 0x66,
        (byte) 0x74, (byte) 0x20, (byte) 0x62, (byte) 0x6c,
        (byte) 0x61, (byte) 0x6e, (byte) 0x6b, (byte) 0x20,
        (byte) 0x20, (byte) 0x20, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x1e,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x1c, (byte) 0x00, (byte) 0x1e,
        (byte) 0xff, (byte) 0xff,
    };

    private static final int ALIGNMENT = 4096;

    private final FileType fileType;
    private final AppleSingleVersion version;
    private final FileSystem homeFileSystem;
    private final LinkedList<Pair<EntryType, AppleSingleEntry>> entryList =
            new LinkedList<Pair<EntryType, AppleSingleEntry>>();

    public AppleSingleBuilder(FileType fileType, AppleSingleVersion version, FileSystem homeFileSystem) {
        if(fileType == null)
            throw new IllegalArgumentException("fileType == null");
        if(version == null)
            throw new IllegalArgumentException("version == null");
        if(homeFileSystem == null)
            throw new IllegalArgumentException("homeFileSystem == null");
        this.fileType = fileType;
        this.version = version;
        this.homeFileSystem = homeFileSystem;
    }

    public void addDataFork(byte[] resourceForkData) {
        entryList.add(new Pair<EntryType, AppleSingleEntry>(EntryType.DATA_FORK,
                new RawDataEntry(resourceForkData)));
    }

    public void addResourceFork(byte[] resourceForkData) {
        entryList.add(new Pair<EntryType, AppleSingleEntry>(
                EntryType.RESOURCE_FORK, new RawDataEntry(resourceForkData)));
    }

    public void addEmptyResourceFork() {
        entryList.add(new Pair<EntryType, AppleSingleEntry>(
                EntryType.RESOURCE_FORK,
                new RawDataEntry(EMPTY_RESOURCE_FORK)));
    }

    public void addFinderInfo(byte[] finderInfoData,
            List<Pair<String, byte[]>> extendedAttributeList)
    {
        if(finderInfoData != null && finderInfoData.length != 32) {
            throw new IllegalArgumentException("Incorrect Finder info data " +
                    "length (expected: 32, actual: " + finderInfoData.length +
                    ").");
        }

        ArrayList<Pair<byte[], byte[]>> attributeDataList =
                new ArrayList<Pair<byte[], byte[]>>(
                        extendedAttributeList.size());

        for(Pair<String, byte[]> extendedAttributePair : extendedAttributeList)
        {
            /* Note: Is normalization necessary? Other substitutions? */
            byte[] attributeNameUtf8 =
                    Util.encodeString(extendedAttributePair.getA() + "\0",
                    "UTF-8");
            if(attributeNameUtf8.length > 255) {
                throw new RuntimeException("Extended attribute name " +
                        "\"" + extendedAttributePair.getA() + "\" is too " +
                        "long (maximum length: 255, actual length: " +
                        attributeNameUtf8.length + ").");
            }

            byte[] attributeData = extendedAttributePair.getB();

            /* Because of limitations in the XNU kernel's AppleDouble
             * implementation, the maximum data size of extended attributes
             * stored inside an AppleDouble file is 128 KiB. */
            if(attributeData.length > (128 * 1024)) {
                throw new RuntimeException("Attribute data size is too large " +
                        "to be stored in an AppleDouble file (maximum " +
                        "allowed: " + (128 * 1024) + ", actual: " +
                        attributeData.length + ").");
            }

            attributeDataList.add(new Pair<byte[], byte[]>(attributeNameUtf8,
                    extendedAttributePair.getB()));

        }

        entryList.add(new Pair<EntryType, AppleSingleEntry>(
                EntryType.FINDER_INFO,
                new FinderInfoEntry(finderInfoData, attributeDataList)));
    }

    /**
     * Serializes the current state of the builder into a valid AppleSingle data representation that
     * can be written down to file.
     *
     * @return the data of an AppleSingle format file built from the current state of the builder.
     */
    public byte[] getResult() {
        int dataSize = AppleSingleHeader.length();
        dataSize += EntryDescriptor.length()*entryList.size();
        int dataStartOffset = dataSize;

        for(Pair<EntryType, AppleSingleEntry> p : entryList) {
            dataSize += p.getB().getBytes(dataSize, null, 0);
        }

        /* Adjust dataSize for alignment. */
        int remainingAlignmentSize = (ALIGNMENT - (dataSize % ALIGNMENT));
        dataSize = dataSize + remainingAlignmentSize;

        byte[] result = new byte[dataSize];
        int pointer = 0;
        {
            AppleSingleHeader header = new AppleSingleHeader(fileType.getMagic(),
                    version.getVersionNumber(), homeFileSystem, entryList.size());
            byte[] headerData = header.getBytes();
            System.arraycopy(headerData, 0, result, pointer, headerData.length);
            pointer += headerData.length;
        }

        final EntryDescriptor[] entryDescriptors =
                new EntryDescriptor[entryList.size()];
        int i = 0;
        int dataOffset = dataStartOffset;
        for(Pair<EntryType, AppleSingleEntry> p : entryList) {
            int entryDataLength = p.getB().getBytes(dataOffset, null, 0);
            if(p.getA() == EntryType.FINDER_INFO) {
                /* If we have a Finder info entry, then make sure that all the
                 * alignment padding is allocated to this entry. This is done in
                 * order to have headroom for expansion of extended attribute
                 * list/data. */
                entryDataLength += remainingAlignmentSize;
                remainingAlignmentSize = 0;
            }

            EntryDescriptor ed = new EntryDescriptor(p.getA().getTypeNumber(),
                    dataOffset, entryDataLength);
            entryDescriptors[i++] = ed;
            dataOffset += entryDataLength;

            byte[] entryDescriptorData = ed.getBytes();
            System.arraycopy(entryDescriptorData, 0, result, pointer, entryDescriptorData.length);
            pointer += entryDescriptorData.length;
        }

        if(pointer != dataStartOffset)
            throw new RuntimeException("Internal error: Miscalculation of dataStartOffset (should be: " +
                    pointer + ", was: " + dataStartOffset + ")");

        i = 0;
        for(Pair<EntryType, AppleSingleEntry> p : entryList) {
            final EntryDescriptor desc = entryDescriptors[i++];
            final int entryOffset = desc.getEntryOffset();
            final int entryLength = desc.getEntryLength();

            if(pointer != entryOffset) {
                throw new RuntimeException("Internal error: Miscalculation " +
                        "of data offset for entry " + i + " (calculated: " +
                        entryOffset + ", actual: " + pointer + ").");
            }

            final int entryDataLength =
                    p.getB().getBytes(pointer, result, pointer);
            pointer += entryDataLength;

            final int entryPaddingLength = entryLength - entryDataLength;
            if(entryPaddingLength != 0) {
                /* Fill trailing bytes in entry with zeroed data. */
                Arrays.fill(result, pointer, pointer + entryPaddingLength,
                        (byte) 0);
                pointer += entryPaddingLength;
            }
        }

        final int trailingPaddingLength = result.length - pointer;
        if(trailingPaddingLength != 0) {
            /* Fill trailing bytes in file with zeroed data. */
            Arrays.fill(result, pointer, pointer + trailingPaddingLength,
                    (byte) 0);
            pointer += trailingPaddingLength;
        }

        return result;
    }

    public interface AppleSingleEntry {
        public int getBytes(long fileOffset, byte[] data, int offset);
    }

    public class RawDataEntry implements AppleSingleEntry {
        private final byte[] rawData;

        public RawDataEntry(byte[] rawData) {
            this.rawData = rawData;
        }

        public int getBytes(long fileOffset, byte[] data, int offset) {
            if(data != null) {
                System.arraycopy(rawData, 0, data, offset, rawData.length);
            }

            return rawData.length;
        }
    }

    public class FinderInfoEntry implements AppleSingleEntry {
        private final byte[] finderInfoData;
        private final ArrayList<Pair<byte[], byte[]>> attributeDataList;

        private FinderInfoEntry(byte[] finderInfoData,
                ArrayList<Pair<byte[], byte[]>> attributeDataList)
        {
            this.finderInfoData = finderInfoData;
            this.attributeDataList = attributeDataList;
        }

        public int getBytes(long fileOffset, byte[] data, int offset) {
            int finderInfoDataSize = 0;

            if(data != null) {
                if(finderInfoData != null) {
                    System.arraycopy(finderInfoData, 0, data,
                            offset + finderInfoDataSize, 32);
                }
                else {
                    Arrays.fill(data, offset + finderInfoDataSize,
                            offset + finderInfoDataSize + 32, (byte) 0);
                }
            }

            finderInfoDataSize += 32;

            if(!attributeDataList.isEmpty()) {
                /* 2-byte padding. */
                if(data != null) {
                    Arrays.fill(data, offset + finderInfoDataSize,
                            offset + finderInfoDataSize + 2, (byte) 0);
                }

                finderInfoDataSize += 2;

                /* Calculate offsets of header and entries. This is non-trivial
                 * due to the requirement that entries are variable-sized and
                 * must be 4-byte aligned. */
                final int attributeHeaderOffset = offset + finderInfoDataSize;
                finderInfoDataSize += AttributeHeader.STRUCTSIZE;

                final int[] attributeEntryOffsets =
                        data != null ? new int[attributeDataList.size()] : null;
                int extendedAttributesDataSize = 0;
                int i = 0;
                for(Pair<byte[], byte[]> attributeData : attributeDataList) {
                    extendedAttributesDataSize += attributeData.getB().length;

                    /* Align to 4-byte boundaries. */
                    final int remainder =
                            (int) ((fileOffset + finderInfoDataSize) & 0x3);
                    if(remainder != 0) {
                        if(data != null) {
                            Arrays.fill(data, offset + finderInfoDataSize,
                                    offset + finderInfoDataSize + remainder,
                                    (byte) 0);
                        }

                        finderInfoDataSize += remainder;
                    }

                    if(data != null) {
                        attributeEntryOffsets[i++] =
                                offset + finderInfoDataSize;
                    }

                    finderInfoDataSize += AttributeEntry.STATIC_STRUCTSIZE +
                            attributeData.getA().length;
                }

                /* Header + entries must fit within the first 64 KiB of the file
                 * beacuse of limitations in the XNU AppleDouble
                 * implementation. */
                if(fileOffset + finderInfoDataSize > 65536) {
                    throw new RuntimeException("Attribute entry list extends " +
                            "beyond the first 64k of the file (ends at: " +
                            (fileOffset + finderInfoDataSize) + ").");
                }

                final int extendedAttributesDataStart =
                        (int) (fileOffset + finderInfoDataSize);
                int curDataOffset = finderInfoDataSize;

                /* Write out attribute header. */
                if(data != null) {
                    final AttributeHeader header = new AttributeHeader(0,
                            extendedAttributesDataStart +
                            extendedAttributesDataSize,
                            extendedAttributesDataStart,
                            extendedAttributesDataSize,
                            (short) 0, (short) attributeDataList.size());
                    final byte[] headerBytes = header.getBytes();
                    System.arraycopy(headerBytes, 0, data,
                            attributeHeaderOffset, headerBytes.length);
                }

                /* Write out attribute entry headers and associated data. */
                i = 0;
                for(Pair<byte[], byte[]> attributeData : attributeDataList) {
                    final byte[] content = attributeData.getB();

                    if(data != null) {
                        final byte[] name = attributeData.getA();

                        final AttributeEntry entry = new AttributeEntry(
                                curDataOffset, content.length, (short) 0, name,
                                0, (short) name.length);
                        final byte[] entryData = entry.getBytes();

                        /* Write out entry header. */
                        System.arraycopy(entryData, 0, data,
                                attributeEntryOffsets[i++], entryData.length);

                        /* Write out entry data. */
                        System.arraycopy(content, 0, data,
                                offset + finderInfoDataSize, content.length);
                    }

                    finderInfoDataSize += content.length;
                }
            }

            return finderInfoDataSize;
        }
    }
}
