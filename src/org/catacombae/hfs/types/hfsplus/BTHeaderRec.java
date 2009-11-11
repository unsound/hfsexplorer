/*-
 * Copyright (C) 2006 Erik Larsson
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

package org.catacombae.hfsexplorer.types.hfsplus;

import org.catacombae.hfsexplorer.Util;
import java.io.PrintStream;

public class BTHeaderRec {
    /*
     * struct BTHeaderRec
     * size: 106 bytes
     * description: 
     * 
     * BP  Size  Type        Identifier      Description          
     * -----------------------------------------------------------
     * 0   2     UInt16      treeDepth                            
     * 2   4     UInt32      rootNode                             
     * 6   4     UInt32      leafRecords                          
     * 10  4     UInt32      firstLeafNode                        
     * 14  4     UInt32      lastLeafNode                         
     * 18  2     UInt16      nodeSize                             
     * 20  2     UInt16      maxKeyLength                         
     * 22  4     UInt32      totalNodes                           
     * 26  4     UInt32      freeNodes                            
     * 30  2     UInt16      reserved1                            
     * 32  4     UInt32      clumpSize       // misaligned        
     * 36  1     UInt8       btreeType                            
     * 37  1     UInt8       keyCompareType                       
     * 38  4     UInt32      attributes      // long aligned again
     * 42  4*16  UInt32[16]  reserved3                            
     */
    
    /** Case folding (case-insensitive). Possible return value from getKeyCompareType(). */
    public static final byte kHFSCaseFolding = (byte)0xCF;
    /** Binary compare (case-sensitive). Possible return value from getKeyCompareType(). */
    public static final byte kHFSBinaryCompare = (byte)0xBC;
    
    private final byte[] treeDepth = new byte[2];
    private final byte[] rootNode = new byte[4];
    private final byte[] leafRecords = new byte[4];
    private final byte[] firstLeafNode = new byte[4];
    private final byte[] lastLeafNode = new byte[4];
    private final byte[] nodeSize = new byte[2];
    private final byte[] maxKeyLength = new byte[2];
    private final byte[] totalNodes = new byte[4];
    private final byte[] freeNodes = new byte[4];
    private final byte[] reserved1 = new byte[2];
    private final byte[] clumpSize = new byte[4];
    private final byte[] btreeType = new byte[1];
    private final byte[] keyCompareType = new byte[1];
    private final byte[] attributes = new byte[4];
    private final byte[] reserved3 = new byte[4*16];
    
    public BTHeaderRec(byte[] data, int offset) {
	System.arraycopy(data, offset+0, treeDepth, 0, 2);
	System.arraycopy(data, offset+2, rootNode, 0, 4);
	System.arraycopy(data, offset+6, leafRecords, 0, 4);
	System.arraycopy(data, offset+10, firstLeafNode, 0, 4);
	System.arraycopy(data, offset+14, lastLeafNode, 0, 4);
	System.arraycopy(data, offset+18, nodeSize, 0, 2);
	System.arraycopy(data, offset+20, maxKeyLength, 0, 2);
	System.arraycopy(data, offset+22, totalNodes, 0, 4);
	System.arraycopy(data, offset+26, freeNodes, 0, 4);
	System.arraycopy(data, offset+30, reserved1, 0, 2);
	System.arraycopy(data, offset+32, clumpSize, 0, 4);
	System.arraycopy(data, offset+36, btreeType, 0, 1);
	System.arraycopy(data, offset+37, keyCompareType, 0, 1);
	System.arraycopy(data, offset+38, attributes, 0, 4);
	System.arraycopy(data, offset+42, reserved3, 0, 4*16);
    }

    public short getTreeDepth() { return Util.readShortBE(treeDepth); }
    public int getRootNode() { return Util.readIntBE(rootNode); }
    public int getLeafRecords() { return Util.readIntBE(leafRecords); }
    public int getFirstLeafNode() { return Util.readIntBE(firstLeafNode); }
    public int getLastLeafNode() { return Util.readIntBE(lastLeafNode); }
    public short getNodeSize() { return Util.readShortBE(nodeSize); }
    public short getMaxKeyLength() { return Util.readShortBE(maxKeyLength); }
    public int getTotalNodes() { return Util.readIntBE(totalNodes); }
    public int getFreeNodes() { return Util.readIntBE(freeNodes); }
    public short getReserved1() { return Util.readShortBE(reserved1); }
    public int getClumpSize() { return Util.readIntBE(clumpSize); }
    public byte getBtreeType() { return Util.readByteBE(btreeType); }
    /** Specifies what type of key compare algorithm to use. For HFS+ volumes, this field is to be treated as reserved. 
	For HFSX (at least version 5) volumes, the value of this field will be one of the constants
	<code>kHFSCaseFolding</code> or <code>kHFSBinaryCompare</code> defined as static constants of this class. */
    public byte getKeyCompareType() { return Util.readByteBE(keyCompareType); }
    public int getAttributes() { return Util.readIntBE(attributes); }
    public int[] getReserved3() { return Util.readIntArrayBE(reserved3); }
    
    // Access to attributes:
    public boolean isBTBadCloseSet() { return Util.getBit(getAttributes(), 0); }
    public boolean isBTBigKeysSet() { return Util.getBit(getAttributes(), 1); }
    public boolean isBTVariableIndexKeysSet() { return Util.getBit(getAttributes(), 2); }
    
    public static int length() { return 106; }

    public void printFields(PrintStream ps, String prefix) {
	ps.println(prefix + " treeDepth: " + getTreeDepth());
	ps.println(prefix + " rootNode: " + getRootNode());
	ps.println(prefix + " leafRecords: " + getLeafRecords());
	ps.println(prefix + " firstLeafNode: " + getFirstLeafNode());
	ps.println(prefix + " lastLeafNode: " + getLastLeafNode());
	ps.println(prefix + " nodeSize: " + getNodeSize());
	ps.println(prefix + " maxKeyLength: " + getMaxKeyLength());
	ps.println(prefix + " totalNodes: " + getTotalNodes());
	ps.println(prefix + " freeNodes: " + getFreeNodes());
	ps.println(prefix + " reserved1: " + getReserved1());
	ps.println(prefix + " clumpSize: " + getClumpSize());
	ps.println(prefix + " btreeType: " + getBtreeType());
	ps.println(prefix + " keyCompareType: " + getKeyCompareType());
	ps.println(prefix + " attributes: " + getAttributes());
	ps.println(prefix + " reserved3: " + getReserved3());
    }
    
    public void print(PrintStream ps, String prefix) {
	ps.println(prefix + "BTHeaderRec:");
	printFields(ps, prefix);
    }
    
    public byte[] getBytes() {
        byte[] result = new byte[length()];
	int offset = 0;
        
        System.arraycopy(treeDepth, 0, result, offset, treeDepth.length); offset += treeDepth.length;
        System.arraycopy(rootNode, 0, result, offset, rootNode.length); offset += rootNode.length;
        System.arraycopy(leafRecords, 0, result, offset, leafRecords.length); offset += leafRecords.length;
        System.arraycopy(firstLeafNode, 0, result, offset, firstLeafNode.length); offset += firstLeafNode.length;
        System.arraycopy(lastLeafNode, 0, result, offset, lastLeafNode.length); offset += lastLeafNode.length;
        System.arraycopy(nodeSize, 0, result, offset, nodeSize.length); offset += nodeSize.length;
        System.arraycopy(maxKeyLength, 0, result, offset, maxKeyLength.length); offset += maxKeyLength.length;
        System.arraycopy(totalNodes, 0, result, offset, totalNodes.length); offset += totalNodes.length;
        System.arraycopy(freeNodes, 0, result, offset, freeNodes.length); offset += freeNodes.length;
        System.arraycopy(reserved1, 0, result, offset, reserved1.length); offset += reserved1.length;
        System.arraycopy(clumpSize, 0, result, offset, clumpSize.length); offset += clumpSize.length;
        System.arraycopy(btreeType, 0, result, offset, btreeType.length); offset += btreeType.length;
        System.arraycopy(keyCompareType, 0, result, offset, keyCompareType.length); offset += keyCompareType.length;
        System.arraycopy(attributes, 0, result, offset, attributes.length); offset += attributes.length;
        System.arraycopy(reserved3, 0, result, offset, reserved3.length); offset += reserved3.length;
        
        return result;
    }
}
