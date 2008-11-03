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
import org.catacombae.hfsexplorer.Util;

/**
 *
 * @author erik
 */
public abstract class CommonBTIndexRecord <K extends CommonBTKey<K>> extends CommonBTRecord {
    protected final K key;
    protected final byte[] index = new byte[4];
    
    public static <K extends CommonBTKey<K>> CommonBTIndexRecord<K> createHFS(K key, byte[] data, int offset) {
        return new HFSImplementation<K>(key, data, offset);
    }
    
    public static <K extends CommonBTKey<K>> CommonBTIndexRecord<K> createHFSPlus(K key, byte[] data, int offset) {
        return new HFSPlusImplementation<K>(key, data, offset);
    }
    
    protected CommonBTIndexRecord(K key, byte[] data, int offset) {
        this.key = key;
        System.arraycopy(data, offset+key.occupiedSize(), index, 0, index.length);
    }
    
    public K getKey() {
        return key;
    }

    public long getIndex() {
        return Util.unsign(Util.readIntBE(index));
    }

    @Override
    public byte[] getBytes() {
        byte[] res = new byte[getSize()];
        Util.zero(res);
        byte[] keyData = key.getBytes();
        int i = 0;

        System.arraycopy(keyData, 0, res, i, keyData.length);
        i += keyData.length;
        System.arraycopy(index, 0, res, i, index.length);
        i += index.length;
        if(i != res.length)
            throw new RuntimeException("Assertion failed: i == res.length (i=" +
                    i + ",res.length=" + res.length + ")");

        return res;
    }

    @Override
    public void print(PrintStream ps, String prefix) {
        ps.println(prefix + "CommonBTIndexRecord:");
        printFields(ps, prefix);
    }
    
    @Override
    public void printFields(PrintStream ps, String prefix) {
        ps.println(prefix + " key:");
        key.print(ps, prefix + "  ");
        ps.println(prefix + " index: " + getIndex());
    }


    private static class HFSImplementation <K extends CommonBTKey<K>> extends CommonBTIndexRecord<K> {
        
        public HFSImplementation(K key, byte[] data, int offset) {
            super(key, data, offset);
        }
        
        @Override
        public int getSize() {
            return key.occupiedSize() + index.length;
        }
        /*
        @Override
        public long getIndexAsOffset(int nodeSize) {
            return getIndex()*nodeSize;
        }
         * */
    }
    
    private static class HFSPlusImplementation <K extends CommonBTKey<K>> extends CommonBTIndexRecord<K> {
        public HFSPlusImplementation(K key, byte[] data, int offset) {
            super(key, data, offset);
        }
        
        @Override
        public int getSize() {
            return key.occupiedSize() + index.length;
        }
        
        /*
        @Override
        public long getIndexAsOffset(int nodeSize) {
            return getIndex()*nodeSize;
        }
         * */
    }
}
