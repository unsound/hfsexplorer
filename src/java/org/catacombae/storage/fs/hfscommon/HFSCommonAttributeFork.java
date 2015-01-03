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

package org.catacombae.storage.fs.hfscommon;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import org.catacombae.hfs.io.ForkFilter;
import org.catacombae.hfs.types.hfscommon.CommonHFSAttributesLeafRecord;
import org.catacombae.hfs.types.hfscommon.CommonHFSExtentDescriptor;
import org.catacombae.hfs.types.hfsplus.HFSPlusAttributesData;
import org.catacombae.hfs.types.hfsplus.HFSPlusAttributesExtents;
import org.catacombae.hfs.types.hfsplus.HFSPlusAttributesForkData;
import org.catacombae.hfs.types.hfsplus.HFSPlusAttributesLeafRecordData;
import org.catacombae.hfs.types.hfsplus.HFSPlusExtentDescriptor;
import org.catacombae.hfs.types.hfsplus.HFSPlusExtentRecord;
import org.catacombae.io.RandomAccessStream;
import org.catacombae.io.ReadableByteArrayStream;
import org.catacombae.io.ReadableRandomAccessInputStream;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.io.SynchronizedReadableRandomAccessStream;
import org.catacombae.io.TruncatableRandomAccessStream;
import org.catacombae.io.WritableRandomAccessStream;
import org.catacombae.storage.fs.FSFork;

/**
 * @author <a href="http://www.catacombae.org/" target="_top">Erik Larsson</a>
 */
public class HFSCommonAttributeFork implements FSFork {
    private final HFSCommonFSEntry parent;
    private final CommonHFSAttributesLeafRecord[] recordList;

    HFSCommonAttributeFork(HFSCommonFSEntry parent,
            CommonHFSAttributesLeafRecord... recordList)
    {
        if(recordList.length == 0) {
            throw new RuntimeException("Empty record list!");
        }

        this.parent = parent;
        this.recordList = recordList;
    }

    public long getLength() {
        final HFSPlusAttributesLeafRecordData firstRecordData =
                recordList[0].getRecordData();
        final long length;

        if(firstRecordData instanceof HFSPlusAttributesData) {
            HFSPlusAttributesData attributesData =
                    (HFSPlusAttributesData) firstRecordData;
            length = attributesData.getAttrSize();
        }
        else if(firstRecordData instanceof HFSPlusAttributesForkData) {
            HFSPlusAttributesForkData attributesForkData =
                    (HFSPlusAttributesForkData) firstRecordData;
            length = attributesForkData.getTheFork().getLogicalSize();
        }
        else {
            throw new RuntimeException("Unexpected record type of first " +
                    "record: " + firstRecordData.getClass());
        }

        return length;
    }

    public boolean isWritable() {
        return false;
    }

    public boolean isTruncatable() {
        return false;
    }

    public boolean isCompressed() {
        return false;
    }

    public String getForkIdentifier() {
        return "Attribute: " + new String(recordList[0].getKey().getAttrName());
    }

    public InputStream getInputStream() {
        return new ReadableRandomAccessInputStream(
                new SynchronizedReadableRandomAccessStream(
                        getReadableRandomAccessStream()));
    }

    public ReadableRandomAccessStream getReadableRandomAccessStream() {
        final HFSPlusAttributesLeafRecordData firstRecordData =
                recordList[0].getRecordData();
        final ReadableRandomAccessStream stream;

        if(firstRecordData instanceof HFSPlusAttributesData) {
            final HFSPlusAttributesData attributesData =
                    (HFSPlusAttributesData) firstRecordData;
            stream = new ReadableByteArrayStream(attributesData.getAttrData());
        }
        else if(firstRecordData instanceof HFSPlusAttributesForkData) {
            final HFSPlusAttributesForkData attributesForkData =
                    (HFSPlusAttributesForkData) firstRecordData;

            LinkedList<CommonHFSExtentDescriptor> allExtents =
                    new LinkedList<CommonHFSExtentDescriptor>();
            HFSPlusExtentRecord curRecord =
                    attributesForkData.getTheFork().getExtents();
            int i = 0;
            long totalBlocks = 0;
            do {
                for(HFSPlusExtentDescriptor desc :
                        curRecord.getExtentDescriptors())
                {
                    allExtents.addLast(CommonHFSExtentDescriptor.create(desc));
                    totalBlocks += desc.getBlockCount();
                }

                ++i;
                curRecord = null;

                if(i < recordList.length) {
                    final CommonHFSAttributesLeafRecord nextRecord =
                            recordList[++i];
                    final HFSPlusAttributesLeafRecordData nextRecordData =
                            nextRecord.getRecordData();

                    if(nextRecord.getKey().getStartBlock() != totalBlocks) {
                        throw new RuntimeException("Unexpected start block " +
                                "for record at index " + i + " (expected: " +
                                totalBlocks + " actual: " +
                                nextRecord.getKey().getStartBlock() + ").");
                    }

                    if(nextRecordData instanceof HFSPlusAttributesExtents) {
                        final HFSPlusAttributesExtents extentsData =
                                (HFSPlusAttributesExtents) nextRecordData;
                        curRecord = extentsData.getExtents();
                    }
                    else {
                        throw new RuntimeException("Unexpected attributes " +
                                "leaf record type at index " + i + ": " +
                                nextRecordData.getClass());
                    }
                }
            } while(curRecord != null);


            stream = new ForkFilter(
                    attributesForkData.getTheFork().getLogicalSize(),
                    allExtents.toArray(new CommonHFSExtentDescriptor[allExtents.
                    size()]),
                    parent.getFileSystemHandler().getFSView().createFSStream(),
                    0,
                    parent.getFileSystemHandler().getFSView().getVolumeHeader().
                    getAllocationBlockSize(), 0);
        }
        else {
            throw new RuntimeException("Unexpected record type of first " +
                    "record: " + firstRecordData.getClass());
        }

        return stream;
    }

    public WritableRandomAccessStream getWritableRandomAccessStream() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public RandomAccessStream getRandomAccessStream() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public OutputStream getOutputStream() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public TruncatableRandomAccessStream getForkStream() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean hasXattrName() {
        return true;
    }

    public String getXattrName() {
        return new String(recordList[0].getKey().getAttrName());
    }
}
