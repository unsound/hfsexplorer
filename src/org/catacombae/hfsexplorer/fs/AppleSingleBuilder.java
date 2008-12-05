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

    private FileType fileType;
    private int version;
    private FileSystem homeFileSystem;
    private LinkedList<Pair<EntryType, byte[]>> entryList = new LinkedList<Pair<EntryType, byte[]>>();

    public AppleSingleBuilder(FileType fileType, int version, FileSystem homeFileSystem) {
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
}
