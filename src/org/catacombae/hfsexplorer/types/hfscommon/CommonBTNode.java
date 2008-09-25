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

import org.catacombae.hfsexplorer.Util;
import org.catacombae.hfsexplorer.types.BTNodeDescriptor;
import org.catacombae.hfsexplorer.types.hfs.NodeDescriptor;

/**
 * Generalization of a B-tree node which suits both HFS and HFS+/HFSX
 * implementations.
 * 
 * @author Erik Larsson
 */
public abstract class CommonBTNode {
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
    
    public CommonBTRecord getBTRecord(int index) {
        return ic.getBTRecord(index);
    }
    
    public CommonBTRecord[] getBTRecords() {
        return ic.getBTRecords();
    }
    
    protected abstract CommonBTRecord createBTRecord(byte[] data, int offset, int length);
    
    protected abstract class InternalContainer {
        protected final CommonBTNodeDescriptor nodeDescriptor;
        protected final CommonBTRecord[] records;
        protected final short[] offsets;
        
        protected InternalContainer(CommonBTNodeDescriptor nodeDescriptor,
                byte[] data, int offset, int nodeSize) {
            this.nodeDescriptor = nodeDescriptor;
            offsets = new short[nodeDescriptor.getNumberOfRecords()+1]; //Last one is free space index
            for(int i = 0; i < offsets.length; ++i) {
                offsets[i] = Util.readShortBE(data, offset+nodeSize-((i+1)*2));
            }
            records = new CommonBTRecord[offsets.length-1];
            for(int i = 0; i < records.length; ++i) {
                int len = offsets[i+1] - offsets[i];
                records[i] = createBTRecord(data, offset+offsets[i], len);
            }
        }
        
        public abstract CommonBTNodeDescriptor getNodeDescriptor();
        public abstract CommonBTRecord getBTRecord(int index);
        public abstract CommonBTRecord[] getBTRecords();
    }
    
    private class HFSImplementation extends InternalContainer {
        public HFSImplementation(byte[] data, int offset, int nodeSize) {
            super(CommonBTNodeDescriptor.create(new NodeDescriptor(data, offset)),
                    data, offset, nodeSize);
        }
        
        public CommonBTNodeDescriptor getNodeDescriptor() {
            return nodeDescriptor;
        }
        
        public CommonBTRecord getBTRecord(int index) {
            return records[index];
        }

        public CommonBTRecord[] getBTRecords() {
            CommonBTRecord[] copy = new CommonBTRecord[records.length];
            for(int i = 0; i < copy.length; ++i)
                copy[i] = records[i];
            return copy;
        }
    }

    private class HFSPlusImplementation extends InternalContainer {
        public HFSPlusImplementation(byte[] data, int offset, int nodeSize) {
            super(CommonBTNodeDescriptor.create(new BTNodeDescriptor(data, offset)),
                    data, offset, nodeSize);
        }
        
        public CommonBTNodeDescriptor getNodeDescriptor() {
            return nodeDescriptor;
        }
        
        public CommonBTRecord getBTRecord(int index) {
            return records[index];
        }

        public CommonBTRecord[] getBTRecords() {
            CommonBTRecord[] copy = new CommonBTRecord[records.length];
            for(int i = 0; i < copy.length; ++i)
                copy[i] = records[i];
            return copy;
        }
    }
}