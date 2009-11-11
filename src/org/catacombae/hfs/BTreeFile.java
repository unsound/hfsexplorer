/*-
 * Copyright (C) 2006-2009 Erik Larsson
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

package org.catacombae.hfs;

import org.catacombae.hfsexplorer.types.hfscommon.CommonBTHeaderNode;
import org.catacombae.hfsexplorer.types.hfscommon.CommonBTHeaderRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonBTIndexNode;
import org.catacombae.hfsexplorer.types.hfscommon.CommonBTIndexRecord;
import org.catacombae.hfsexplorer.types.hfscommon.CommonBTKey;
import org.catacombae.hfsexplorer.types.hfscommon.CommonBTNodeDescriptor;
import org.catacombae.hfsexplorer.types.hfscommon.CommonHFSVolumeHeader;
import org.catacombae.io.Readable;
import org.catacombae.io.ReadableRandomAccessStream;

/**
 *
 * @author erik
 */
public abstract class BTreeFile {
    final HFSVolume vol;

    private final BTreeOperations ops;

    BTreeFile(HFSVolume vol, BTreeOperations ops) {
        this.vol = vol;
        this.ops = ops;
    }

    abstract class BTreeFileSession {
        final CommonHFSVolumeHeader header;
        final CommonBTNodeDescriptor btnd;
        final CommonBTHeaderRecord bthr;
        final ReadableRandomAccessStream btreeStream;

        public BTreeFileSession() {
            this.header = vol.getVolumeHeader();
            //header.print(System.err, "    ");
            this.btreeStream = getBTreeStream(header);

            this.btreeStream.seek(0);
            //byte[] nodeDescriptorData = new byte[14];
            //if(forkFilterFile.read(nodeDescriptorData) != nodeDescriptorData.length)
            //	System.out.println("ERROR: Did not read nodeDescriptor completely.");
            this.btnd = readNodeDescriptor(this.btreeStream);
            this.bthr = readHeaderRecord(this.btreeStream);
            //byte[] headerRec = new byte[BTHeaderRec.length()];
            //forkFilterFile.readFully(headerRec);
            //this.bthr = new BTHeaderRec(headerRec, 0);
        }

        protected abstract ReadableRandomAccessStream getBTreeStream(
                CommonHFSVolumeHeader header);
    }

    static <K extends CommonBTKey<K>> CommonBTIndexRecord<K> findLEKey(CommonBTIndexNode<K> indexNode, K searchKey) {
	/*
	 * Algorithm:
	 *   input: Key searchKey
	 *   variables: Key greatestMatchingKey
	 *   For each n : records
	 *     If n.key <= searchKey && n.key > greatestMatchingKey
	 *       greatestMatchingKey = n.key
	 */
	CommonBTIndexRecord<K> largestMatchingRecord = null;

        //System.err.println("findLEKey(): Entering loop...");
        for(CommonBTIndexRecord<K> record : indexNode.getBTRecords()) {
            K recordKey = record.getKey();

            //System.err.print("findLEKey():   Processing record");
            //if(recordKey instanceof CommonHFSExtentKey)
            //    System.err.print(" with key " + getDebugString((CommonHFSExtentKey)recordKey));
            //System.err.print("...");

	    if(recordKey.compareTo(searchKey) <= 0 &&
                    (largestMatchingRecord == null ||
                    recordKey.compareTo(largestMatchingRecord.getKey()) > 0)) {

		largestMatchingRecord = record;
                //System.err.print("match!");
	    }
            //else
            //    System.err.print("no match.");
            //System.err.println();
	}

        //System.err.println("findLEKey(): Returning...");
	return largestMatchingRecord;
    }

    protected CommonBTHeaderNode createCommonBTHeaderNode(byte[] currentNodeData,
            int offset, int nodeSize) {
        return ops.createCommonBTHeaderNode(currentNodeData, offset, nodeSize);
    }
    
    protected CommonBTNodeDescriptor readNodeDescriptor(Readable rd) {
        return ops.readNodeDescriptor(rd);
    }

    protected CommonBTHeaderRecord readHeaderRecord(Readable rd) {
        return ops.readHeaderRecord(rd);
    }

    protected CommonBTNodeDescriptor createCommonBTNodeDescriptor(
            byte[] currentNodeData, int offset) {
        return ops.createCommonBTNodeDescriptor(currentNodeData, offset);
    }
}
