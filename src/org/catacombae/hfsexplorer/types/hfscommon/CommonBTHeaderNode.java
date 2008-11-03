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

package org.catacombae.hfsexplorer.types.hfscommon;

import org.catacombae.hfsexplorer.types.hfsplus.BTHeaderRec;
import org.catacombae.hfsexplorer.types.hfs.BTHdrRec;


/**
 *
 * @author erik
 */
public abstract class CommonBTHeaderNode extends CommonBTNode<CommonBTRecord> {
    protected CommonBTHeaderNode(byte[] data, int offset, int nodeSize, FSType type) {
        super(data, offset, nodeSize, type);

        validate();
    }

    public static CommonBTHeaderNode createHFS(byte[] data, int offset, int nodeSize) {
        return new HFSImplementation(data, offset, nodeSize);
    }

    public static CommonBTHeaderNode createHFSPlus(byte[] data, int offset, int nodeSize) {
        return new HFSPlusImplementation(data, offset, nodeSize);
    }

    private void validate() throws IllegalArgumentException {
        if(ic.records.size() != 3)
            throw new IllegalArgumentException("Illegal length of record array: " +
                    ic.records.size() + " (expected 3)");
        
        if(!(ic.records.get(0) instanceof CommonBTHeaderRecord)) {
            throw new IllegalArgumentException("Illegal record type at index 0: " +
                    ic.records.get(0).getClass());
        }

        if(!(ic.records.get(1) instanceof CommonBTGenericDataRecord)) {
            throw new IllegalArgumentException("Illegal record type at index 0: " +
                    ic.records.get(1).getClass());
        }

        if(!(ic.records.get(1) instanceof CommonBTGenericDataRecord)) {
            throw new IllegalArgumentException("Illegal record type at index 0: " +
                    ic.records.get(1).getClass());
        }
    }

    public CommonBTHeaderRecord getHeaderRecord() {
        CommonBTRecord btr = ic.records.get(0);
        if(btr instanceof CommonBTHeaderRecord) {
            return (CommonBTHeaderRecord)btr;
        }
        else
            throw new RuntimeException("Unexpected type at records[0]: " + btr.getClass());
    }

    public CommonBTGenericDataRecord getUserDataRecord() {
        CommonBTRecord btr = ic.records.get(1);
        if(btr instanceof CommonBTGenericDataRecord) {
            return (CommonBTGenericDataRecord)btr;
        }
        else
            throw new RuntimeException("Unexpected type at records[1]: " + btr.getClass());
    }

    public CommonBTGenericDataRecord getBTreeMapRecord() {
        CommonBTRecord btr = ic.records.get(2);
        if(btr instanceof CommonBTGenericDataRecord) {
            return (CommonBTGenericDataRecord)btr;
        }
        else
            throw new RuntimeException("Unexpected type at records[2]: " + btr.getClass());
    }

    @Override
    protected CommonBTRecord createBTRecord(int recordNumber,
            byte[] data, int offset, int length) {
        switch(recordNumber) {
            case 0:
                return createHeaderRecord(data, offset, length);
            case 1:
            case 2:
                return new CommonBTGenericDataRecord(data, offset, length);
            default:
                throw new RuntimeException("Too many records for a header node!");
        }
    }

    protected abstract CommonBTHeaderRecord createHeaderRecord(byte[] data, int offset, int length);

    private static class HFSPlusImplementation extends CommonBTHeaderNode {
        public HFSPlusImplementation(byte[] data, int offset, int nodeSize) {
            super(data, offset, nodeSize, FSType.HFS_PLUS);
        }

        @Override
        protected CommonBTHeaderRecord createHeaderRecord(byte[] data, int offset, int length) {
            if(length != BTHeaderRec.length()) {
                throw new IllegalArgumentException("length (" + length + ") != " +
                        "BTHeaderRec.length() (" + BTHeaderRec.length() + ")");
            }

            BTHeaderRec bthr = new BTHeaderRec(data, offset);
            return CommonBTHeaderRecord.create(bthr);
        }
    }
    private static class HFSImplementation extends CommonBTHeaderNode {
        public HFSImplementation(byte[] data, int offset, int nodeSize) {
            super(data, offset, nodeSize, FSType.HFS);
        }

        @Override
        protected CommonBTHeaderRecord createHeaderRecord(byte[] data, int offset, int length) {
            if(length != BTHdrRec.length()) {
                throw new IllegalArgumentException("length (" + length + ") != " +
                        "BTHdrRec.length() (" + BTHdrRec.length() + ")");
            }

            BTHdrRec bthr = new BTHdrRec(data, offset);
            return CommonBTHeaderRecord.create(bthr);
        }

    }
}
