package org.catacombae.hfsexplorer.types;

public class HFSXCatalogLeafRecord extends HFSPlusCatalogLeafRecord {
    public HFSXCatalogLeafRecord(byte[] data, int offset, BTHeaderRec catalogHeaderRec) {
	super(data, offset, catalogHeaderRec);
    }
}