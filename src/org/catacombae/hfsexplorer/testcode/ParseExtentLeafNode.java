/*-
 * Copyright (C) 2007 Erik Larsson
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

package org.catacombae.hfsexplorer.testcode;
import java.io.RandomAccessFile;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusExtentLeafNode;

public class ParseExtentLeafNode {
    
    public static void main(String[] args) throws Exception {
        RandomAccessFile raf = new RandomAccessFile(args[0], "r");
        byte[] data = new byte[(int) raf.length()];
        raf.readFully(data);
        raf.close();
        HFSPlusExtentLeafNode node = new HFSPlusExtentLeafNode(data, 0, data.length);
        node.print(System.out, "");

        System.out.println("Leaf record offsets:");
        short[] offsets = node.getLeafRecordOffsets();
        for(short s : offsets)
            System.out.println("  " + (s & 0xFFFF));
    }
}
