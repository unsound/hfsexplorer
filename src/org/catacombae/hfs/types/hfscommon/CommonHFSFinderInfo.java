/*-
 * Copyright (C) 2009 Erik Larsson
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
import org.catacombae.hfs.types.hfs.CdrDirRec;
import org.catacombae.hfs.types.hfs.CdrFilRec;
import org.catacombae.hfs.types.hfsplus.HFSPlusCatalogFile;
import org.catacombae.hfs.types.hfsplus.HFSPlusCatalogFolder;

/**
 *
 * @author erik
 */
public abstract class CommonHFSFinderInfo implements PrintableStruct {

    /**
     * Returns a byte array with 32 elements containing the raw FinderInfo data
     * as retreived from the catalog.
     *
     * @return a byte array with 32 elements containing the raw FinderInfo data.
     */
    public abstract byte[] getBytes();

    /**
     * Sets the FinderInfo data to the specified bytes. <code>finderInfo</code>
     * must be exactly 32 bytes, or an InvalidArgumentException will be thrown.
     *
     * @param finderInfo the new FinderInfo data.
     *
     * @throws IllegalArgumentException if <code>finderInfo</code> is not 32
     * elements in length.
     */
    public abstract void setBytes(byte[] finderInfo);

    /**
     * Creates a CommonHFSFinderInfo from an HFS file record.
     *
     * @param rec the record used for backing storage of the FinderInfo.
     * @return a new CommonHFSFinderInfo object.
     */
    public static CommonHFSFinderInfo create(CdrFilRec rec) {
        return new HFSFileImplementation(rec);
    }

    /**
     * Creates a CommonHFSFinderInfo from an HFS folder record.
     *
     * @param rec the record used for backing storage of the FinderInfo.
     * @return a new CommonHFSFinderInfo object.
     */
    public static CommonHFSFinderInfo create(CdrDirRec rec) {
        return new HFSFolderImplementation(rec);
    }

    /**
     * Creates a CommonHFSFinderInfo from an HFS+ file record.
     *
     * @param rec the record used for backing storage of the FinderInfo.
     * @return a new CommonHFSFinderInfo object.
     */
    public static CommonHFSFinderInfo create(HFSPlusCatalogFile rec) {
        return new HFSPlusFileImplementation(rec);
    }

    /**
     * Creates a CommonHFSFinderInfo from an HFS+ folder record.
     *
     * @param rec the record used for backing storage of the FinderInfo.
     * @return a new CommonHFSFinderInfo object.
     */
    public static CommonHFSFinderInfo create(HFSPlusCatalogFolder rec) {
        return new HFSPlusFolderImplementation(rec);
    }

    private static class HFSFileImplementation extends CommonHFSFinderInfo {
        private CdrFilRec filRec;
        public HFSFileImplementation(CdrFilRec filRec) {
            this.filRec = filRec;
        }

        @Override
        public byte[] getBytes() {
            byte[] res = new byte[32];

            System.arraycopy(filRec.getFilUsrWds().getBytes(), 0, res, 0, 16);
            System.arraycopy(filRec.getFilFndrInfo().getBytes(), 0, res, 16, 16);

            return res;
        }

        @Override
        public void setBytes(byte[] finderInfo) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        /* @Override */
        public void print(PrintStream ps, String prefix) {
            filRec.print(ps, prefix);
        }

        /* @Override */
        public void printFields(PrintStream ps, String prefix) {
            filRec.printFields(ps, prefix);
        }
    }

    private static class HFSFolderImplementation extends CommonHFSFinderInfo {
        private CdrDirRec dirRec;

        public HFSFolderImplementation(CdrDirRec dirRec) {
            this.dirRec = dirRec;
        }

        @Override
        public byte[] getBytes() {
            byte[] res = new byte[32];

            System.arraycopy(dirRec.getDirUsrInfo().getBytes(), 0, res, 0, 16);
            System.arraycopy(dirRec.getDirFndrInfo().getBytes(), 0, res, 16, 16);

            return res;
        }

        @Override
        public void setBytes(byte[] finderInfo) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        /* @Override */
        public void print(PrintStream ps, String prefix) {
            dirRec.print(ps, prefix);
        }

        /* @Override */
        public void printFields(PrintStream ps, String prefix) {
            dirRec.printFields(ps, prefix);
        }
    }

    private static class HFSPlusFileImplementation extends CommonHFSFinderInfo {
        private HFSPlusCatalogFile file;

        public HFSPlusFileImplementation(HFSPlusCatalogFile file) {
            this.file = file;
        }

        @Override
        public byte[] getBytes() {
            byte[] res = new byte[32];

            System.arraycopy(file.getUserInfo().getBytes(), 0, res, 0, 16);
            System.arraycopy(file.getFinderInfo().getBytes(), 0, res, 16, 16);

            return res;
        }

        @Override
        public void setBytes(byte[] finderInfo) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        /* @Override */
        public void print(PrintStream ps, String prefix) {
            file.print(ps, prefix);
        }

        /* @Override */
        public void printFields(PrintStream ps, String prefix) {
            file.printFields(ps, prefix);
        }
    }

    private static class HFSPlusFolderImplementation extends CommonHFSFinderInfo {
        private HFSPlusCatalogFolder folder;

        public HFSPlusFolderImplementation(HFSPlusCatalogFolder folder) {
            this.folder = folder;
        }

        @Override
        public byte[] getBytes() {
            byte[] res = new byte[32];

            System.arraycopy(folder.getUserInfo().getBytes(), 0, res, 0, 16);
            System.arraycopy(folder.getFinderInfo().getBytes(), 0, res, 16, 16);

            return res;
        }

        @Override
        public void setBytes(byte[] finderInfo) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        /* @Override */
        public void print(PrintStream ps, String prefix) {
            folder.print(ps, prefix);
        }

        /* @Override */
        public void printFields(PrintStream ps, String prefix) {
            folder.printFields(ps, prefix);
        }
    }
}
