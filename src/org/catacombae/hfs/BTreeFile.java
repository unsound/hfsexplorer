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

import java.util.LinkedList;
import java.util.List;
import org.catacombae.hfs.types.hfscommon.CommonBTHeaderNode;
import org.catacombae.hfs.types.hfscommon.CommonBTHeaderRecord;
import org.catacombae.hfs.types.hfscommon.CommonBTKey;
import org.catacombae.hfs.types.hfscommon.CommonBTKeyedNode;
import org.catacombae.hfs.types.hfscommon.CommonBTKeyedRecord;
import org.catacombae.hfs.types.hfscommon.CommonBTNode;
import org.catacombae.hfs.types.hfscommon.CommonBTNodeDescriptor;
import org.catacombae.hfs.types.hfscommon.CommonBTNodeDescriptor.NodeType;
import org.catacombae.hfs.types.hfscommon.CommonHFSVolumeHeader;
import org.catacombae.io.Readable;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.io.RuntimeIOException;

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

            this.btnd = readNodeDescriptor(this.btreeStream);
            //this.btnd.print(System.err, "    ");
            if(btnd.getNodeType() != NodeType.HEADER) {
                throw new RuntimeIOException("Invalid node type for header " +
                        "node.");
            }

            this.bthr = readHeaderRecord(this.btreeStream);
            //this.bthr.print(System.err, "    ");
        }

        public final void close() {
            this.btreeStream.close();
        }

        protected abstract ReadableRandomAccessStream getBTreeStream(
                CommonHFSVolumeHeader header);
    }

    static <K extends CommonBTKey<K>, R extends CommonBTKeyedRecord<K>> R
            findLEKey(CommonBTKeyedNode<R> indexNode, K searchKey)
    {
	/*
	 * Algorithm:
	 *   input: Key searchKey
	 *   variables: Key greatestMatchingKey
	 *   For each n : records
	 *     If n.key <= searchKey && n.key > greatestMatchingKey
	 *       greatestMatchingKey = n.key
	 */
	R largestMatchingRecord = null;

        //System.err.println("findLEKey(): Entering loop...");
        for(R record : indexNode.getBTKeyedRecords()) {
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

    /**
     * Find all keys <code>k</code> in the range (<code>minKeyInclusive</code>
     * &lt;= <code>k</code> &lt; <code>maxKeyExclusive</code>) that exist in
     * <code>indexNode</code>.<br>
     *
     * If no matching keys are found, then the record with the largest key that
     * is less than <code>minKeyInclusive</code>, if any such record exists,
     * is returned in <code>result</code> and the function returns
     * <code>false</code>. If no such record does exist, nothing is added to
     * <code>result</code> (and <code>false</code> is still returned).
     *
     * @param indexNode
     *      <b>(in)</b> The index node to search.
     * @param minKeyInclusive
     *      <b>(in)</b> The smallest key in the range (inclusive).
     * @param maxKeyExclusive
     *      <b>(in)</b> The largest key in the range (exclusive).
     *
     * @return
     *      A {@link java.util.List} of keys.
     */
    public static <K extends CommonBTKey<K>, R extends CommonBTKeyedRecord<K>>
    List<R> findLEKeys(CommonBTKeyedNode<R> keyedNode,
            K minKeyInclusive, K maxKeyExclusive)
    {
	final LinkedList<R> result = new LinkedList<R>();

        findLEKeys(keyedNode, minKeyInclusive, maxKeyExclusive, result);

        return result;
    }

    /**
     * Find all keys <code>k</code> in the range (<code>minKeyInclusive</code>
     * &lt;= <code>k</code> &lt; <code>maxKeyExclusive</code>) that exist in
     * <code>keyedNode</code>.<br>
     *
     * If no matching keys are found, then the record with the largest key that
     * is less than <code>minKeyInclusive</code>, if any such record exists,
     * is returned in <code>result</code> and the function returns
     * <code>false</code>. If no such record does exist, nothing is added to
     * <code>result</code> (and <code>false</code> is still returned).
     *
     * @param keyedNode
     *      <b>(in)</b> The keyed node to search.
     * @param minKeyInclusive
     *      <b>(in)</b> The smallest key in the range (inclusive).
     * @param maxKeyExclusive
     *      <b>(in)</b> The largest key in the range (exclusive).
     * @param result
     *      <b>(out)</b> A {@link java.util.LinkedList} that will receive the
     *      matching keys.
     *
     * @return
     *      <code>true</code> if at least one key matching the specified
     *      conditions was found, and <code>false</code> otherwise.
     */
    public static <K extends CommonBTKey<K>, R extends CommonBTKeyedRecord<K>>
    boolean findLEKeys(CommonBTKeyedNode<R> keyedNode, K minKeyInclusive,
            K maxKeyExclusive, LinkedList<R> result)
    {
        boolean found = false;
	K largestLEKey = null;
	R largestLERecord = null;

        /* TODO: Iteration could be optimized to binary search since keys are
         *       (supposed to be) ordered. */
	for(R record : keyedNode.getBTKeyedRecords()) {
            K key = record.getKey();

            if(key.compareTo(minKeyInclusive) < 0) {
                if(largestLEKey == null ||
                        key.compareTo(largestLEKey) > 0)
                {
                    largestLEKey = key;
                    largestLERecord = record;
                }
            }
            else if(key.compareTo(maxKeyExclusive) < 0) {
                if(result != null) {
                    result.addLast(record);
                }

                found = true;
            }
	}

	if(largestLEKey != null) {
            if(result != null) {
                result.addFirst(largestLERecord);
            }
        }

        return found;
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

    public HFSVolume getVolume() {
        return vol;
    }

    public abstract CommonBTNode getRootNode();

    public abstract long getRootNodeNumber();

    public abstract CommonBTNode getNode(long nodeNumber);
}
