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

import org.catacombae.hfsexplorer.fs.AppleSingleBuilder.FileType;
import org.catacombae.hfsexplorer.fs.AppleSingleBuilder.EntryType;
import org.catacombae.hfsexplorer.io.ReadableRandomAccessSubstream;
import org.catacombae.hfsexplorer.io.SynchronizedReadableRandomAccessStream;
import org.catacombae.hfsexplorer.types.applesingle.AppleSingleHeader;
import org.catacombae.hfsexplorer.types.applesingle.EntryDescriptor;
import org.catacombae.io.ReadableConcatenatedStream;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.util.Util;

/**
 *
 * @author Erik Larsson
 */
public class AppleSingleHandler {
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

    private final SynchronizedReadableRandomAccessStream stream;

    public AppleSingleHandler(ReadableRandomAccessStream stream) {
        this(new SynchronizedReadableRandomAccessStream(stream));
    }

    public AppleSingleHandler(SynchronizedReadableRandomAccessStream stream) {
        this.stream = stream;
    }

    public AppleSingleHeader getHeader() {
        byte[] headerData = new byte[AppleSingleHeader.length()];
        stream.readFullyFrom(0, headerData);
        return new AppleSingleHeader(headerData, 0);
    }

    public EntryDescriptor[] getEntryDescriptors() {
        AppleSingleHeader header = getHeader();
        long pos = AppleSingleHeader.length();

        EntryDescriptor[] result = new EntryDescriptor[Util.unsign(header.getNumEntries())];
        byte[] descriptorData = new byte[EntryDescriptor.length()];

        for(int i = 0; i < result.length; ++i) {
            stream.readFullyFrom(pos, descriptorData);
            pos += EntryDescriptor.length();
            result[i] = new EntryDescriptor(descriptorData, 0);
        }
        return result;
    }

    public ReadableRandomAccessStream getEntryStream(EntryDescriptor descriptor) {
        long pos = Util.unsign(descriptor.getEntryOffset());
        long len = Util.unsign(descriptor.getEntryLength());

        return new ReadableConcatenatedStream(new ReadableRandomAccessSubstream(stream), pos, len);
    }

    /**
     * Checks for an AppleSingle or AppleDouble signature and returns the type of file format is on
     * the supplied stream. Returns <code>null</code> if no valid signature is found.
     */
    public static FileType detectFileFormat(ReadableRandomAccessStream stream, long offset) {
        byte[] magicBytes = new byte[4];
        stream.seek(offset);
        stream.readFully(magicBytes);
        int magic = Util.readIntBE(magicBytes);
        for(FileType f : FileType.values()) {
            if(f.getMagic() == magic)
                return f;
        }
        return null;
    }
    
    /**
     * Looks up the resource descriptor for the resource fork type, if any. Returns
     * <code>null</code> if none exists.
     * 
     * @return the resource descriptor for the resource fork type, if any. Returns <code>null</code>
     * if none exists.
     */
    public EntryDescriptor getResourceEntryDescriptor() {
        for(EntryDescriptor descriptor : getEntryDescriptors()) {
            if(descriptor.getEntryId() == EntryType.RESOURCE_FORK.getTypeNumber())
                return descriptor;
        }
        return null;
    }
}
