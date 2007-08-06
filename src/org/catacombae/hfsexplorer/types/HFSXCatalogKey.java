package org.catacombae.hfsexplorer.types;

import org.catacombae.hfsexplorer.Util;
import org.catacombae.hfsexplorer.FastUnicodeCompare;

public class HFSXCatalogKey extends HFSPlusCatalogKey {
    private final BTHeaderRec catalogHeaderRec;
    
    public HFSXCatalogKey(byte[] data, int offset, BTHeaderRec catalogHeaderRec) {
	super(data, offset);
	this.catalogHeaderRec = catalogHeaderRec;
    }
    
    public HFSXCatalogKey(HFSCatalogNodeID parentID, HFSUniStr255 nodeName, BTHeaderRec catalogHeaderRec) {
	super(parentID, nodeName);
	this.catalogHeaderRec = catalogHeaderRec;
    }
    public HFSXCatalogKey(int parentIDInt, String nodeNameString, BTHeaderRec catalogHeaderRec) {
	super(parentIDInt, nodeNameString);
	this.catalogHeaderRec = catalogHeaderRec;
    }
    public int compareTo(BTKey btk) {
	if(btk instanceof HFSPlusCatalogKey) {
	    HFSPlusCatalogKey catKey = (HFSPlusCatalogKey) btk;
	    if(Util.unsign(getParentID().toInt()) == Util.unsign(catKey.getParentID().toInt())) {
		switch(catalogHeaderRec.getKeyCompareType()) {
		case BTHeaderRec.kHFSCaseFolding:
		    return FastUnicodeCompare.compare(getNodeName().getUnicode(), catKey.getNodeName().getUnicode());
		case BTHeaderRec.kHFSBinaryCompare:
		    return Util.unsignedArrayCompare(getNodeName().getUnicode(), catKey.getNodeName().getUnicode());
		default:
		    throw new RuntimeException("Invalid value in file system structure! BTHeaderRec.getKeyCompareType() = " + 
					       catalogHeaderRec.getKeyCompareType());
		}
	    }
	    else return super.compareTo(btk);
	}
	else
	    return super.compareTo(btk);
    }
}