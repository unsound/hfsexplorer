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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.catacombae.hfsexplorer.Util;
import org.catacombae.hfsexplorer.types.hfsplus.BTNodeDescriptor;
import org.catacombae.hfsexplorer.types.hfs.NodeDescriptor;

/**
 * Generalization of a B-tree node which suits both HFS and HFS+/HFSX
 * implementations.
 * 
 * @author Erik Larsson
 */
public abstract class CommonBTNode <R extends CommonBTRecord> {
    protected final InternalContainer ic;
    
    protected static enum FSType { HFS, HFS_PLUS };

    protected CommonBTNode(byte[] data, int offset, int nodeSize, FSType type) {
        switch(type) {
            case HFS:
                ic = new HFSImplementation(data, offset, nodeSize);
                break;
            case HFS_PLUS:
                ic = new HFSPlusImplementation(data, offset, nodeSize);
                break;
            default:
                throw new RuntimeException("unknown HFSType: " + type);
        }
    }
    
    public CommonBTNodeDescriptor getNodeDescriptor() {
        return ic.getNodeDescriptor();
    }

    public R getBTRecord(int index) {
        return ic.getBTRecord(index);
    }
    
    public List<R> getBTRecords() {
        return ic.getBTRecords();
    }
    
    protected abstract R createBTRecord(int recordNumber,
            byte[] data, int offset, int length);

    public void print(PrintStream ps, String prefix) {
        ps.println(prefix + "CommonBTNode:");
        ic.printFields(ps, prefix);
    }


    protected abstract class InternalContainer {
        protected final CommonBTNodeDescriptor nodeDescriptor;
        protected final List<R> records;
        protected final short[] offsets;
        
        protected InternalContainer(CommonBTNodeDescriptor nodeDescriptor,
                byte[] data, int offset, int nodeSize) {
            this.nodeDescriptor = nodeDescriptor;
            offsets = new short[nodeDescriptor.getNumberOfRecords()+1]; //Last one is free space index
            for(int i = 0; i < offsets.length; ++i) {
                offsets[i] = Util.readShortBE(data, offset+nodeSize-((i+1)*2));
            }
            ArrayList<R> tmpRecords = new ArrayList<R>(offsets.length-1);
            for(int i = 0; i < offsets.length-1; ++i) {
                int len = offsets[i+1] - offsets[i];
                tmpRecords.add(createBTRecord(i, data, offset+offsets[i], len));
            }
            this.records = Collections.unmodifiableList(tmpRecords);
        }
        
        public CommonBTNodeDescriptor getNodeDescriptor() {
            return nodeDescriptor;
        }
                
        public R getBTRecord(int index) {
            return records.get(index);
        }

        public List<R> getBTRecords() {
            return records;
        }
        
        public void printFields(PrintStream ps, String prefix) {
            ps.println(prefix + " nodeDescriptor: ");
            nodeDescriptor.print(ps, prefix + "  ");
            ps.println(prefix + " records (CommonBTRecord[" + records.size() + "]):");
            int i = 0;
            for(R record : records) {
                ps.println(prefix + "  [" + i++ + "]:");
                record.print(ps, prefix + "   ");
            }
            ps.println(prefix + " offsets (short[" + offsets.length + "]):");
            for(i = 0; i < offsets.length; ++i) {
                ps.println(prefix + "  [" + i + "]: " + offsets[i]);
            }
        }
    }
    
    private class HFSImplementation extends InternalContainer {
        public HFSImplementation(byte[] data, int offset, int nodeSize) {
            super(CommonBTNodeDescriptor.create(new NodeDescriptor(data, offset)),
                    data, offset, nodeSize);
        }
    }

    private class HFSPlusImplementation extends InternalContainer {
        public HFSPlusImplementation(byte[] data, int offset, int nodeSize) {
            super(CommonBTNodeDescriptor.create(new BTNodeDescriptor(data, offset)),
                    data, offset, nodeSize);
        }
    }
}