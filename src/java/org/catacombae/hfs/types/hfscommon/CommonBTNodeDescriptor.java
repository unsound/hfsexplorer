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

package org.catacombae.hfs.types.hfscommon;

import java.io.PrintStream;
import org.catacombae.csjc.PrintableStruct;
import org.catacombae.util.Util;
import org.catacombae.hfs.types.hfsplus.BTNodeDescriptor;
import org.catacombae.hfs.types.hfs.NodeDescriptor;

/**
 * @author <a href="http://www.catacombae.org/" target="_top">Erik Larsson</a>
 */
public abstract class CommonBTNodeDescriptor implements PrintableStruct {

    public enum NodeType {
        INDEX, HEADER, MAP, LEAF;
    }
    public abstract long getForwardLink();
    public abstract long getBackwardLink();
    public abstract NodeType getNodeType();
    public abstract short getHeight();
    public abstract int getNumberOfRecords();

    /*public void print(PrintStream ps, String prefix) {
        ps.println(prefix + "CommonBTNodeDescriptor:");
        printFields(ps, prefix);
    }
    public abstract void printFields(PrintStream ps, String prefix);
    */

    public static CommonBTNodeDescriptor create(BTNodeDescriptor btnd) {
        return new HFSPlusImplementation(btnd);
    }

    public static CommonBTNodeDescriptor create(NodeDescriptor nd) {
        return new HFSImplementation(nd);
    }

    private static class HFSPlusImplementation extends CommonBTNodeDescriptor {
        private final BTNodeDescriptor btnd;

        public HFSPlusImplementation(BTNodeDescriptor btnd) {
            this.btnd = btnd;
        }

        @Override
        public long getForwardLink() {
            return Util.unsign(btnd.getFLink());
        }

        @Override
        public long getBackwardLink() {
            return Util.unsign(btnd.getBLink());
        }

        @Override
        public NodeType getNodeType() {
            byte b = btnd.getKind();
            switch(b) {
                case BTNodeDescriptor.BT_HEADER_NODE:
                    return NodeType.HEADER;
                case BTNodeDescriptor.BT_INDEX_NODE:
                    return NodeType.INDEX;
                case BTNodeDescriptor.BT_MAP_NODE:
                    return NodeType.MAP;
                case BTNodeDescriptor.BT_LEAF_NODE:
                    return NodeType.LEAF;
                default:
                    throw new RuntimeException("Unknown HFS+ node type: " + b);
            }
        }

        @Override
        public short getHeight() {
            return Util.unsign(btnd.getHeight());
        }

        @Override
        public int getNumberOfRecords() {
            return Util.unsign(btnd.getNumRecords());
        }

        /* @Override */
        public void print(PrintStream ps, String prefix) {
            btnd.print(ps, prefix);
        }

        /* @Override */
        public void printFields(PrintStream ps, String prefix) {
            btnd.printFields(ps, prefix);
        }
    }

    public static class HFSImplementation extends CommonBTNodeDescriptor {
        private final NodeDescriptor nd;

        public HFSImplementation(NodeDescriptor nd) {
            this.nd = nd;
        }

        @Override
        public long getForwardLink() {
            return Util.unsign(nd.getNdFLink());
        }

        @Override
        public long getBackwardLink() {
            return Util.unsign(nd.getNdBLink());
        }

        @Override
        public NodeType getNodeType() {
            byte b = nd.getNdType();
            switch(b) {
                case NodeDescriptor.ndHdrNode:
                    return NodeType.HEADER;
                case NodeDescriptor.ndIndxNode:
                    return NodeType.INDEX;
                case NodeDescriptor.ndMapNode:
                    return NodeType.MAP;
                case NodeDescriptor.ndLeafNode:
                    return NodeType.LEAF;
                default:
                    throw new RuntimeException("Unknown HFS node type: " + b);
            }
        }

        @Override
        public short getHeight() {
            return Util.unsign(nd.getNdNHeight());
        }

        @Override
        public int getNumberOfRecords() {
            return Util.unsign(nd.getNdNRecs());
        }

        /* @Override */
        public void print(PrintStream ps, String prefix) {
            nd.print(ps, prefix);
        }

        /* @Override */
        public void printFields(PrintStream ps, String prefix) {
            nd.printFields(ps, prefix);
        }
    }
}
