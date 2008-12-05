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

import org.catacombae.hfsexplorer.Util;
import org.catacombae.hfsexplorer.io.ReadableRandomAccessSubstream;
import org.catacombae.hfsexplorer.io.SynchronizedReadableRandomAccess;
import org.catacombae.hfsexplorer.io.SynchronizedReadableRandomAccessStream;
import org.catacombae.hfsexplorer.types.resff.ReferenceListEntry;
import org.catacombae.hfsexplorer.types.resff.ResourceHeader;
import org.catacombae.hfsexplorer.types.resff.ResourceMap;
import org.catacombae.io.ReadableConcatenatedStream;
import org.catacombae.io.ReadableRandomAccessStream;

/**
 * Accessor class for the data inside a resource fork.
 * 
 * @author Erik Larsson
 */
public class ResourceForkReader {
    /*
     * Resource fork abstract data model:
     * 
     * -----------------      -------------
     * | Resource fork |1----*| Data type | <- fourcc identifer
     * -----------------      -------------
     *               1          1
     *               |          |
     *               *          +
     *            -----------------
     *            | Resource item | <- actual data
     *            -----------------
     *
     * Or hierarchically represented as example:
     * <resourceFork>
     *      <dataType name="typ1">
     *          <resourceItem name="yada"/>
     *          <resourceItem name="bada"/>
     *          <resourceItem name="lada"/>
     *      </dataType>
     *      <dataType name="typ2">
     *          <resourceItem name="sada"/>
     *          <resourceItem name="gada"/>
     *      </dataType>
     * </resourceFork>
     */

    private final SynchronizedReadableRandomAccessStream trueForkStream;
    private final SynchronizedReadableRandomAccess forkStream;

    public ResourceForkReader(ReadableRandomAccessStream forkStream) {
        this.trueForkStream = new SynchronizedReadableRandomAccessStream(forkStream);
        this.forkStream = trueForkStream;
    }

    public void close() {
        trueForkStream.close();
    }

    public ResourceHeader getHeader() {
        byte[] headerData = new byte[ResourceHeader.length()];
        forkStream.readFullyFrom(0, headerData);
        return new ResourceHeader(headerData, 0);
    }

    public ResourceMap getResourceMap() {
        ResourceHeader header = getHeader();
        return new ResourceMap(forkStream, Util.unsign(header.getMapOffset()));
    }

    public long getDataLength(ReferenceListEntry entry) {
        long dataPos = getDataPos(entry);
        return getDataLength(dataPos);
    }

    private long getDataPos(ReferenceListEntry entry) {
        ResourceHeader header = getHeader();
        return Util.unsign(header.getDataOffset()) + entry.getResourceDataOffset();
    }

    private long getDataLength(long dataPos) {
        byte[] dataLengthBytes = new byte[4];
        forkStream.readFrom(dataPos, dataLengthBytes);
        return Util.unsign(Util.readIntBE(dataLengthBytes));
    }

    public ReadableRandomAccessStream getResourceStream(ReferenceListEntry entry) {
        long dataPos = getDataPos(entry);
        long dataLength = getDataLength(dataPos);

        //System.err.println("Creating a new stream for ReferenceListEntry:");
        //entry.printFields(System.err, "  ");
        //System.err.println("dataPos=" + dataPos);
        //System.err.println("dataLength=" + dataLength);

        return new ReadableConcatenatedStream(new ReadableRandomAccessSubstream(forkStream),
                dataPos + 4, dataLength);
    }
}
