/*-
 * Copyright (C) 2006-2009 Erik Larsson
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

package org.catacombae.hfs.types.hfsplus;

import org.catacombae.util.Util;
import java.io.PrintStream;
import java.lang.reflect.Field;
import org.catacombae.csjc.PrintableStruct;
import org.catacombae.csjc.StructElements;
import org.catacombae.csjc.structelements.Dictionary;

/**
 * This struct is a representation of the C typedef HFSCatalogNodeID present in
 * the HFS+ specification. As the catalog node ID has some special properties
 * compared to a normal integer, we treat this as a separate class.
 *
 * @author Erik Larsson
 */
public class HFSCatalogNodeID implements StructElements, PrintableStruct {
    /*
     * HFSCatalogNodeID (typedef UInt32)
     * size: 4 bytes
     *
     * BP   Size  Type              Variable name     Description
     * -----------------------------------------------------------
     * 0    4     UInt32            hfsCatalogNodeID
     */

    /** This catalog node ID is reserved for the parent of the root folder. */
    public static final HFSCatalogNodeID kHFSRootParentID            = new HFSCatalogNodeID(1);
    /** This catalog node ID is reserved for the root folder. */
    public static final HFSCatalogNodeID kHFSRootFolderID            = new HFSCatalogNodeID(2);
    /** This catalog node ID is reserved for the extents overflow file. */
    public static final HFSCatalogNodeID kHFSExtentsFileID           = new HFSCatalogNodeID(3);
    /** This catalog node ID is reserved for the catalog file. */
    public static final HFSCatalogNodeID kHFSCatalogFileID           = new HFSCatalogNodeID(4);
    /** This catalog node ID is reserved for the bad blocks file. */
    public static final HFSCatalogNodeID kHFSBadBlockFileID          = new HFSCatalogNodeID(5);
    /** This catalog node ID is reserved for the allocation file. */
    public static final HFSCatalogNodeID kHFSAllocationFileID        = new HFSCatalogNodeID(6);
    /** This catalog node ID is reserved for the startup file. */
    public static final HFSCatalogNodeID kHFSStartupFileID           = new HFSCatalogNodeID(7);
    /** This catalog node ID is reserved for the attributes file. */
    public static final HFSCatalogNodeID kHFSAttributesFileID        = new HFSCatalogNodeID(8);
    /** This catalog node ID is reserved for a temporary repair catalog file. */
    public static final HFSCatalogNodeID kHFSRepairCatalogFileID     = new HFSCatalogNodeID(14);
    /** This catalog node ID is reserved for the ExchangeFiles operation. */
    public static final HFSCatalogNodeID kHFSBogusExtentFileID       = new HFSCatalogNodeID(15);
    /** This catalog node ID is the first ID that's available for user files. */
    public static final HFSCatalogNodeID kHFSFirstUserCatalogNodeID  = new HFSCatalogNodeID(16);

    private int hfsCatalogNodeID;

    /**
     * Creates a new HFSCatalogNodeID from raw data located at offset
     * <code>offset</code> in the array <code>data</code>.
     *
     * @param data an array containing the catalog node ID data.
     * @param offset offset in the array where the catalog node ID data begins.
     */
    public HFSCatalogNodeID(byte[] data, int offset) {
        this.hfsCatalogNodeID = Util.readIntBE(data, offset);
    }

    /**
     * Creates a new HFSCatalogNodeID from the int value <code>nodeID</code>.
     * <code>nodeID</code>, being a Java integer and thus signed, is
     * nevertheless regarded as an unsigned value.
     *
     * @param nodeID the catalog node ID value. This is interpreted as being
     * unsigned.
     */
    public HFSCatalogNodeID(int nodeID) {
        this.hfsCatalogNodeID = nodeID;
    }

    /**
     * Returns the length of the data that makes up this struct.
     * @return the length of the data that makes up this struct.
     */
    public static int length() {
        return 4;
    }

    /**
     * Returns the value of the catalog node ID as an signed 32-bit integer.
     * This is really inappropriate, since the catalog node ID is an unsigned
     * value. In most cases you should use the {@link #toLong()} method instead.
     *
     * @return the value of the catalog node ID as an signed 32-bit integer.
     */
    public int toInt() {
        return hfsCatalogNodeID;
    }

    /**
     * Returns the value of the catalog node ID as an unsigned 32-bit integer
     * stored in a <code>long</code> value.
     *
     * @return the value of the catalog node ID as an unsigned 32-bit integer.
     */
    public long toLong() {
        return Util.unsign(toInt());
    }

    /**
     * Returns the constant name of this catalog node ID if it's a reserved ID,
     * and the string "User Defined ID" otherwise.
     *
     * @return the constant name of this catalog node ID if it's a reserved ID,
     * and the string "User Defined ID" otherwise.
     */
    public String getDescription() {
        /*
         * kHFSRootParentID            = 1,
         * kHFSRootFolderID            = 2,
         * kHFSExtentsFileID           = 3,
         * kHFSCatalogFileID           = 4,
         * kHFSBadBlockFileID          = 5,
         * kHFSAllocationFileID        = 6,
         * kHFSStartupFileID           = 7,
         * kHFSAttributesFileID        = 8,
         * kHFSRepairCatalogFileID     = 14,
         * kHFSBogusExtentFileID       = 15,
         * kHFSFirstUserCatalogNodeID  = 16
         */
        String result;
        switch(toInt()) {
            case 1:
                result = "kHFSRootParentID";
                break;
            case 2:
                result = "kHFSRootFolderID";
                break;
            case 3:
                result = "kHFSExtentsFileID";
                break;
            case 4:
                result = "kHFSCatalogFileID";
                break;
            case 5:
                result = "kHFSBadBlockFileID";
                break;
            case 6:
                result = "kHFSAllocationFileID";
                break;
            case 7:
                result = "kHFSStartupFileID";
                break;
            case 8:
                result = "kHFSAttributesFileID";
                break;
            case 14:
                result = "kHFSRepairCatalogFileID";
                break;
            case 15:
                result = "kHFSBogusExtentFileID";
                break;
            case 16:
                result = "kHFSFirstUserCatalogNodeID";
                break;
            default:
                result = "User Defined ID";
                break;
        }
        return result;
    }

    /**
     * Returns a string representation of this catalog node ID, which is simply
     * the unsigned numerical value.
     *
     * @return a string representation of this catalog node ID.
     */
    @Override
    public String toString() {
        return "" + toLong(); // + " (" + getDescription() + ")";
    }

    /**
     * {@inheritDoc}
     */
    public void printFields(PrintStream ps, String prefix) {
        ps.println(prefix + " hfsCatalogNodeID: " + toString() + " (" + getDescription() + ")");
    }

    /**
     * {@inheritDoc}
     */
    public void print(PrintStream ps, String prefix) {
        ps.println(prefix + "HFSCatalogNodeID:");
        printFields(ps, prefix);
    }

    /**
     * Returns the on-disk byte representation of this catalog node ID.
     * @return the on-disk byte representation of this catalog node ID.
     */
    public byte[] getBytes() {
        return Util.toByteArrayBE(hfsCatalogNodeID);
    }

    private Field getPrivateField(String name) throws NoSuchFieldException {
        Field f = getClass().getDeclaredField(name);
        f.setAccessible(true);
        return f;
    }

    /**
     * {@inheritDoc}
     */
    public Dictionary getStructElements() {
        try {
            DictionaryBuilder db = new DictionaryBuilder(HFSCatalogNodeID.class.getSimpleName());
            db.addInt("hfsCatalogNodeID", getPrivateField("hfsCatalogNodeID"), this, UNSIGNED, BIG_ENDIAN, null, null, DECIMAL);

            return db.getResult();
        } catch(NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private void _setValue(int nodeID) {
        this.hfsCatalogNodeID = nodeID;
    }

    public static class Mutable extends HFSCatalogNodeID {
        /**
         * Creates a new HFSCatalogNodeID.Mutable from raw data located at
         * offset <code>offset</code> in the array <code>data</code>.
         *
         * @param data an array containing the catalog node ID data.
         * @param offset offset in the array where the catalog node ID data
         * begins.
         */
        public Mutable(byte[] data, int offset) {
            super(data, offset);
        }

        /**
         * Creates a new HFSCatalogNodeID.Mutable from the int value
         * <code>nodeID</code>. <code>nodeID</code>, being a Java integer and
         * thus signed, is nevertheless regarded as an unsigned value.
         *
         * @param nodeID the catalog node ID value. This is interpreted as being
         * unsigned.
         */
        public Mutable(int nodeID) {
            super(nodeID);
        }

        public void setValue(int nodeID) {
            super._setValue(nodeID);
        }
    }
}
