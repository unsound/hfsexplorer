package org.catacombae.hfsexplorer.types;

public class HFSXCatalogIndexNode extends HFSPlusCatalogIndexNode {
    public HFSXCatalogIndexNode(byte[] data, int offset, int nodeSize, BTHeaderRec catalogHeaderRec) {
	super(data, offset, nodeSize, catalogHeaderRec);
    }
}