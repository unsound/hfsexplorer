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
import org.catacombae.csjc.StructElements;
import org.catacombae.csjc.structelements.Dictionary;
import org.catacombae.hfsexplorer.Util;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusExtentDescriptor;
import org.catacombae.hfsexplorer.types.hfs.ExtDescriptor;

/**
 *
 * @author erik
 */
public abstract class CommonHFSExtentDescriptor implements StructElements {
    public abstract long getStartBlock();
    public abstract long getBlockCount();

    public abstract void print(PrintStream ps, String prefix);
    
    public static CommonHFSExtentDescriptor create(HFSPlusExtentDescriptor hped) {
        return new HFSPlusImplementation(hped);
    }
    
    public static CommonHFSExtentDescriptor create(ExtDescriptor hped) {
        return new HFSImplementation(hped);
    }
    
    public static class HFSPlusImplementation extends CommonHFSExtentDescriptor {
        private final HFSPlusExtentDescriptor hped;
        
        public HFSPlusImplementation(HFSPlusExtentDescriptor hped) {
            this.hped = hped;
        }
        
        @Override
        public long getStartBlock() {
            return Util.unsign(hped.getStartBlock());
        }
        
        @Override
        public long getBlockCount() {
            return Util.unsign(hped.getBlockCount());
        }

        @Override
        public void print(PrintStream ps, String prefix) {
            hped.print(ps, prefix);
        }

        public Dictionary getStructElements() {
            return hped.getStructElements();
        }
    }
    
    public static class HFSImplementation extends CommonHFSExtentDescriptor {
        private final ExtDescriptor hped;
        
        public HFSImplementation(ExtDescriptor hped) {
            this.hped = hped;
        }
        
        @Override
        public long getStartBlock() {
            return Util.unsign(hped.getXdrStABN());
        }
        
        @Override
        public long getBlockCount() {
            return Util.unsign(hped.getXdrNumABlks());
        }

        @Override
        public void print(PrintStream ps, String prefix) {
            hped.print(ps, prefix);
        }

        public Dictionary getStructElements() {
            return hped.getStructElements();
        }
    }
}
