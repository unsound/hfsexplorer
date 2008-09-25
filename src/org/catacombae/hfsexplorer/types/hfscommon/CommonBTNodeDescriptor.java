/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.types.hfscommon;

import org.catacombae.hfsexplorer.Util;
import org.catacombae.hfsexplorer.types.BTNodeDescriptor;
import org.catacombae.hfsexplorer.types.hfs.NodeDescriptor;

/**
 *
 * @author erik
 */
public abstract class CommonBTNodeDescriptor {
    public enum NodeType {
        INDEX, HEADER, MAP, LEAF;
    }
    public abstract long getForwardLink();
    public abstract long getBackwardLink();
    public abstract NodeType getNodeType();
    public abstract short getHeight();
    public abstract int getNumberOfRecords();
    
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
                    throw new RuntimeException("Unknown node type: " + b);
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
                    throw new RuntimeException("Unknown node type: " + b);
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
    }
}
