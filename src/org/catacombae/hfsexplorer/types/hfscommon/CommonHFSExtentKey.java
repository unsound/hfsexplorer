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
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusExtentKey;
import org.catacombae.hfsexplorer.types.hfs.ExtKeyRec;

/**
 *
 * @author erik
 */
public abstract class CommonHFSExtentKey extends CommonBTKey {

    public void print(PrintStream ps, String prefix) {
        ps.println(prefix + getClass().getSimpleName() + ":");
        printFields(ps, prefix + " ");
    }
    
    public static CommonHFSExtentKey create(HFSPlusExtentKey key) {
        return new HFSPlusImplementation(key);
    }

    public static CommonHFSExtentKey create(ExtKeyRec key) {
        return new HFSImplementation(key);
    }
    
    public static class HFSPlusImplementation extends CommonHFSExtentKey {
        private final HFSPlusExtentKey key;
        
        public HFSPlusImplementation(HFSPlusExtentKey key) {
            this.key = key;
        }
        
        @Override
        public byte[] getBytes() {
            return key.getBytes();
        }

        public int maxSize() {
            return key.length();
        }

        public int occupiedSize() {
            return key.length();
        }

        public void printFields(PrintStream ps, String prefix) {
            ps.println(prefix + "key:");
            key.print(ps, prefix + " ");
        }
    }
    
    public static class HFSImplementation extends CommonHFSExtentKey {
        private final ExtKeyRec key;
        
        public HFSImplementation(ExtKeyRec key) {
            this.key = key;
        }
        
        @Override
        public byte[] getBytes() {
            return key.getBytes();
        }

        public int maxSize() {
            return key.length();
        }

        public int occupiedSize() {
            return key.length();
        }

        public void printFields(PrintStream ps, String prefix) {
            ps.println(prefix + "key:");
            key.print(ps, prefix + " ");
        }
    }
}
    
