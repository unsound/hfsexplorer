/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.types.hfscommon;

import org.catacombae.hfsexplorer.types.BTHeaderRec;
import org.catacombae.hfsexplorer.types.hfs.BTHdrRec;


/**
 *
 * @author erik
 */
public abstract class CommonBTHeaderNode extends CommonBTNode {
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
        if(ic.records.length != 3)
            throw new IllegalArgumentException("Illegal length of record array: " +
                    ic.records.length + " (expected 3)");
        
        if(!(ic.records[0] instanceof CommonBTHeaderRecord)) {
            throw new IllegalArgumentException("Illegal record type at index 0: " +
                    ic.records[0].getClass());
        }

        if(!(ic.records[1] instanceof CommonBTGenericDataRecord)) {
            throw new IllegalArgumentException("Illegal record type at index 0: " +
                    ic.records[1].getClass());
        }

        if(!(ic.records[2] instanceof CommonBTGenericDataRecord)) {
            throw new IllegalArgumentException("Illegal record type at index 0: " +
                    ic.records[2].getClass());
        }
    }

    public CommonBTHeaderRecord getHeaderRecord() {
        CommonBTRecord btr = ic.records[0];
        if(btr instanceof CommonBTHeaderRecord) {
            return (CommonBTHeaderRecord)btr;
        }
        else
            throw new RuntimeException("Unexpected type at records[0]: " + btr.getClass());
    }

    public CommonBTGenericDataRecord getUserDataRecord() {
        CommonBTRecord btr = ic.records[1];
        if(btr instanceof CommonBTGenericDataRecord) {
            return (CommonBTGenericDataRecord)btr;
        }
        else
            throw new RuntimeException("Unexpected type at records[1]: " + btr.getClass());
    }

    public CommonBTGenericDataRecord getBTreeMapRecord() {
        CommonBTRecord btr = ic.records[2];
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
