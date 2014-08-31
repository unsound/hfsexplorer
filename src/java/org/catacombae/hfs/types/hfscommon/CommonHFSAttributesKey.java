/*-
 * Copyright (C) 2008-2012 Erik Larsson
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
import org.catacombae.csjc.StructElements;
import org.catacombae.csjc.structelements.Dictionary;
import org.catacombae.util.Util;
import org.catacombae.hfs.types.hfsplus.HFSPlusAttributesKey;

/**
 *
 * @author erik
 */
public abstract class CommonHFSAttributesKey
        extends CommonBTKey<CommonHFSAttributesKey> implements StructElements
{
    /* @Override */
    public void print(PrintStream ps, String prefix) {
        ps.println(prefix + getClass().getSimpleName() + ":");
        printFields(ps, prefix + " ");
    }

    public static CommonHFSAttributesKey create(HFSPlusAttributesKey key) {
        return new HFSPlusImplementation(key);
    }

    private static int commonCompare(final CommonHFSAttributesKey k1,
            final CommonHFSAttributesKey k2)
    {
        /* Compare order for keys in the attributes file:
         *   1. File ID.
         *   2. Attribute name length.
         *   3. Attribute name data (char by char unsigned binary comparison).
         *   4. Start block.
         */
        final long fileID1 = k1.getFileID().toLong();
        final long fileID2 = k2.getFileID().toLong();

        if(fileID1 == fileID2) {
            final int attrNameLen1 = k1.getAttrNameLen();
            final int attrNameLen2 = k2.getAttrNameLen();
            final int minAttrNameLen = Math.min(attrNameLen1, attrNameLen2);

            final char[] attrName1 = k1.getAttrName();
            final char[] attrName2 = k2.getAttrName();

            for(int i = 0; i < minAttrNameLen; ++i) {
                final int curChar1 = Util.unsign(attrName1[i]);
                final int curChar2 = Util.unsign(attrName2[i]);

                if(curChar1 < curChar2)
                    return -1;
                else if(curChar1 > curChar2)
                    return 1;
            }

            if(attrNameLen1 < attrNameLen2)
                return -1;
            else if(attrNameLen1 > attrNameLen2)
                return 1;
            else {
                /* If we got here, then the names are equal. */
                final long startBlock1 = k1.getStartBlock();
                final long startBlock2 = k2.getStartBlock();
                if(startBlock1 == startBlock2)
                    return 0;
                else if(startBlock1 < startBlock2)
                    return -1;
                else
                    return 1;
            }
        }
        else if(fileID1 < fileID2)
            return -1;
        else
            return 1;
    }

    /** file associated with attribute */
    public abstract CommonHFSCatalogNodeID getFileID();

    /** first allocation block number for extents */
    public abstract long getStartBlock();

    /** number of unicode characters */
    public abstract int getAttrNameLen();

    /** attribute name (Unicode) */
    public abstract char[] getAttrName();

    public static class HFSPlusImplementation extends CommonHFSAttributesKey {
        private final HFSPlusAttributesKey key;

        public HFSPlusImplementation(HFSPlusAttributesKey key) {
            this.key = key;
        }

        @Override
        public CommonHFSCatalogNodeID getFileID() {
            return CommonHFSCatalogNodeID.create(key.getFileID());
        }

        @Override
        public final long getStartBlock() {
            return key.getStartBlock();
        }

        @Override
        public final int getAttrNameLen() {
            return key.getAttrNameLen();
        }

        @Override
        public final char[] getAttrName() {
            return key.getAttrName();
        }

        @Override
        public final byte[] getBytes() {
            return key.getBytes();
        }

        /* @Override */
        public final int maxSize() {
            return key.length();
        }

        /* @Override */
        public final int occupiedSize() {
            return key.length();
        }

        /* @Override */
        public final void printFields(PrintStream ps, String prefix) {
            ps.println(prefix + "key:");
            key.print(ps, prefix + " ");
        }

        /* @Override */
        public final Dictionary getStructElements() {
            return key.getStructElements();
        }

        /* @Override */
        public int compareTo(CommonHFSAttributesKey o) {
            if(o instanceof HFSPlusImplementation) {
                return commonCompare(this, o);
            }
            else {
                if(o != null)
                    throw new RuntimeException("Can't compare a " +
                            o.getClass() + " with a " + this.getClass());
                else
                    throw new RuntimeException("o == null !!");
            }
        }
    }
}
