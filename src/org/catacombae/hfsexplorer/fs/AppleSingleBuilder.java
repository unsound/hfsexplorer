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

import java.util.LinkedList;
import org.catacombae.hfsexplorer.types.applesingle.AppleSingleHeader;
import org.catacombae.hfsexplorer.types.applesingle.EntryDescriptor;
import org.catacombae.util.Util.Pair;

/**
 *
 * @author Erik Larsson
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

    public static enum FileType {
        APPLESINGLE(0x00051600), APPLEDOUBLE(0x00051607);

        private final int magic;

        private FileType(int magic) {
            this.magic = magic;
        }

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

    enum EntryType {
        DATA_FORK(1),
        RESOURCE_FORK(2),
        REAL_NAME(3),
        COMMENT(4),
        ICON_BW(5),
        ICON_COLOR(6),
        FILE_INFO(7),
        FINDER_INFO(9);

        private final int typeNumber;

        private EntryType(int typeNumber) {
            this.typeNumber = typeNumber;
        }

        public int getTypeNumber() { return typeNumber; }
    }

    private final FileType fileType;
    private final AppleSingleVersion version;
    private final FileSystem homeFileSystem;
    private final LinkedList<Pair<EntryType, byte[]>> entryList = new LinkedList<Pair<EntryType, byte[]>>();

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
        entryList.add(new Pair<EntryType, byte[]>(EntryType.DATA_FORK, resourceForkData));
    }
    
    public void addResourceFork(byte[] resourceForkData) {
        entryList.add(new Pair<EntryType, byte[]>(EntryType.RESOURCE_FORK, resourceForkData));
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
        
        for(Pair<EntryType, byte[]> p : entryList)
            dataSize += p.getB().length;
        

        byte[] result = new byte[dataSize];
        int pointer = 0;
        {
            AppleSingleHeader header = new AppleSingleHeader(fileType.getMagic(),
                    version.getVersionNumber(), homeFileSystem, entryList.size());
            byte[] headerData = header.getBytes();
            System.arraycopy(headerData, 0, result, pointer, headerData.length);
            pointer += headerData.length;
        }
        int dataOffset = dataStartOffset;
        for(Pair<EntryType, byte[]> p : entryList) {
            byte[] entryData = p.getB();
            EntryDescriptor ed = new EntryDescriptor(p.getA().getTypeNumber(), dataOffset, entryData.length);
            dataOffset += entryData.length;
            
            byte[] entryDescriptorData = ed.getBytes();
            System.arraycopy(entryDescriptorData, 0, result, pointer, entryDescriptorData.length);
            pointer += entryDescriptorData.length;
        }

        if(pointer != dataStartOffset)
            throw new RuntimeException("Internal error: Miscalculation of dataStartOffset (should be: " +
                    pointer + ", was: " + dataStartOffset + ")");

        for(Pair<EntryType, byte[]> p : entryList) {
            byte[] entryData = p.getB();
            System.arraycopy(entryData, 0, result, pointer, entryData.length);
            pointer += entryData.length;
        }

        if(pointer != result.length)
            throw new RuntimeException("Internal error: Miscalculation of result.length (should be: " +
                    pointer + ", was: " + result.length + ")");

        return result;
    }
}
