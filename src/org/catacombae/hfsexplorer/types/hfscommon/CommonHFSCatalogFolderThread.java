/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.types.hfscommon;

import java.io.PrintStream;
import org.catacombae.csjc.PrintableStruct;
import org.catacombae.csjc.StructElements;
import org.catacombae.csjc.structelements.Dictionary;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusCatalogThread;
import org.catacombae.hfsexplorer.types.hfs.CdrThdRec;

/**
 *
 * @author erik
 */
public abstract class CommonHFSCatalogFolderThread implements PrintableStruct, StructElements {
    public abstract CommonHFSCatalogNodeID getParentID();
    public abstract CommonHFSCatalogString getNodeName();
    public abstract int length();
    public abstract byte[] getBytes();
    
    @Override
    public void print(PrintStream ps, String prefix) {
        ps.println(prefix + CommonHFSCatalogFolderThread.class.getSimpleName() + ":");
        printFields(ps, prefix + " ");
    }

    public static CommonHFSCatalogFolderThread create(HFSPlusCatalogThread data) {
        return new HFSPlusImplementation(data);
    }
    
    public static CommonHFSCatalogFolderThread create(CdrThdRec data) {
        return new HFSImplementation(data);
    }
    
    private static class HFSPlusImplementation extends CommonHFSCatalogFolderThread {
        private final HFSPlusCatalogThread data;
        
        public HFSPlusImplementation(HFSPlusCatalogThread data) {
            this.data = data;
        }

        @Override
        public CommonHFSCatalogNodeID getParentID() {
            return CommonHFSCatalogNodeID.create(data.getParentID());
        }

        @Override
        public CommonHFSCatalogString getNodeName() {
            return CommonHFSCatalogString.createHFSPlus(data.getNodeName());
        }

        @Override
        public int length() {
            return data.length();
        }

        @Override
        public byte[] getBytes() {
            return data.getBytes();
        }
        
        @Override
        public void printFields(PrintStream ps, String prefix) {
            ps.println(prefix + "data:");
            data.print(ps, prefix + " ");
        }

        @Override
        public Dictionary getStructElements() {
            return data.getStructElements();
        }
    }
    
    private static class HFSImplementation extends CommonHFSCatalogFolderThread {
        private final CdrThdRec data;
        
        public HFSImplementation(CdrThdRec data) {
            this.data = data;
        }

        @Override
        public CommonHFSCatalogNodeID getParentID() {
            return CommonHFSCatalogNodeID.create(data.getThdParID());
        }

        @Override
        public CommonHFSCatalogString getNodeName() {
            return CommonHFSCatalogString.createHFS(data.getThdCName());
        }

        @Override
        public int length() {
            return data.length();
        }

        @Override
        public byte[] getBytes() {
            return data.getBytes();
        }

        @Override
        public void printFields(PrintStream ps, String prefix) {
            ps.println(prefix + "data:");
            data.print(ps, prefix + " ");
        }
        
        @Override
        public Dictionary getStructElements() {
            return data.getStructElements();
        }
    }
}
