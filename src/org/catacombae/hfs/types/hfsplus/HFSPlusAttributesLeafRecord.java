/*-
 * Copyright (C) 2006-2012 Erik Larsson
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

package org.catacombae.hfs.types.hfsplus;

import org.catacombae.csjc.structelements.Dictionary;
import org.catacombae.util.Util;
import java.io.PrintStream;
import org.catacombae.csjc.AbstractStruct;
import org.catacombae.csjc.PrintableStruct;
import org.catacombae.csjc.StructElements;

public class HFSPlusAttributesLeafRecord
        implements AbstractStruct, PrintableStruct, StructElements
{

    protected final HFSPlusAttributesKey key;
    protected final HFSPlusAttributesLeafRecordData recordData;

    public HFSPlusAttributesLeafRecord(byte[] data, int offset) {
        this.key = new HFSPlusAttributesKey(data, offset);

        int recordType = Util.readIntBE(data, offset + this.key.length());
        switch(recordType) {
            case HFSPlusAttributesLeafRecordData.kHFSPlusAttrInlineData:
                this.recordData = new HFSPlusAttributesData(data,
                        offset + this.key.length());
                break;
            case HFSPlusAttributesLeafRecordData.kHFSPlusAttrForkData:
                this.recordData = new HFSPlusAttributesForkData(data,
                        offset + this.key.length());
                break;
            case HFSPlusAttributesLeafRecordData.kHFSPlusAttrExtents:
                recordData = new HFSPlusAttributesExtents(data,
                        offset + this.key.length());
                break;
            default:
                throw new RuntimeException("Invalid record type: 0x" +
                        Util.toHexStringBE(recordType));
        }
    }

    public HFSPlusAttributesKey getKey() { return key; }
    public HFSPlusAttributesLeafRecordData getData() { return recordData; }

    public int size() {
        return key.length() + recordData.size();
    }

    public byte[] getBytes() {
        /* Inefficient, but not sure how to do it in any other way with the
         * primitive APIs that we currently have. */
        final byte[] keyBytes = key.getBytes();
        final byte[] recordDataBytes = recordData.getBytes();

        final byte[] result =
                new byte[keyBytes.length + recordDataBytes.length];
        Util.arrayCopy(keyBytes, result, 0);
        Util.arrayCopy(recordDataBytes, result, keyBytes.length);
        return result;
    }

    public void printFields(PrintStream ps, String prefix) {
        ps.println(prefix + " key:");
        key.printFields(ps, prefix + "  ");
        ps.println(prefix + " recordData:");
        recordData.printFields(ps, prefix + "  ");
    }

    public void print(PrintStream ps, String prefix) {
        ps.println(prefix + "HFSPlusAttributesLeafRecord:");
        printFields(ps, prefix);
    }

    public Dictionary getStructElements() {
        DictionaryBuilder db = new DictionaryBuilder(
                "HFSPlusAttributesLeafRecord",
                "HFS+ attributes file leaf record");

        db.add("key", key.getStructElements(), "Key");
        db.add("recordData", recordData.getStructElements(), "Record data");

        return db.getResult();
    }
}
