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

import java.io.PrintStream;
import org.catacombae.csjc.PrintableStruct;
import org.catacombae.csjc.StructElements;
import org.catacombae.csjc.structelements.Dictionary;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusCatalogThread;
import org.catacombae.hfsexplorer.types.hfs.CdrFThdRec;

/**
 *
 * @author erik
 */
public abstract class CommonHFSCatalogFileThread implements PrintableStruct, StructElements {
    public abstract CommonHFSCatalogNodeID getParentID();
    public abstract CommonHFSCatalogString getNodeName();
    public abstract int length();
    public abstract byte[] getBytes();

    @Override
    public void print(PrintStream ps, String prefix) {
        ps.println(prefix + CommonHFSCatalogFileThread.class.getSimpleName() + ":");
        printFields(ps, prefix + " ");
    }

    public static CommonHFSCatalogFileThread create(HFSPlusCatalogThread data) {
        return new HFSPlusImplementation(data);
    }

    public static CommonHFSCatalogFileThread create(CdrFThdRec data) {
        return new HFSImplementation(data);
    }
    
    private static class HFSPlusImplementation extends CommonHFSCatalogFileThread {
        private final HFSPlusCatalogThread data;
        
        public HFSPlusImplementation(HFSPlusCatalogThread data) {
            this.data = data;
        }

        public int length() {
            return data.length();
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
        public byte[] getBytes() {
            return data.getBytes();
        }

        public void printFields(PrintStream ps, String prefix) {
            ps.println(prefix + "data:");
            data.print(ps, prefix + " ");
        }

        @Override
        public Dictionary getStructElements() {
            return data.getStructElements();
        }
    }
    
    private static class HFSImplementation extends CommonHFSCatalogFileThread {
        private final CdrFThdRec data;
        
        public HFSImplementation(CdrFThdRec data) {
            this.data = data;
        }
        
        public int length() {
            return data.length();
        }

        @Override
        public CommonHFSCatalogNodeID getParentID() {
            return CommonHFSCatalogNodeID.create(data.getFthdParID());
        }

        @Override
        public CommonHFSCatalogString getNodeName() {
            return CommonHFSCatalogString.createHFS(data.getFthdCName());
        }

        @Override
        public byte[] getBytes() {
            return data.getBytes();
        }

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
