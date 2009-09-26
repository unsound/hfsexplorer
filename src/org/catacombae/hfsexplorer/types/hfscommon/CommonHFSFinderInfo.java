/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.types.hfscommon;

import org.catacombae.hfsexplorer.types.hfs.CdrDirRec;
import org.catacombae.hfsexplorer.types.hfs.CdrFilRec;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusCatalogFile;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusCatalogFolder;

/**
 *
 * @author erik
 */
public abstract class CommonHFSFinderInfo {

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
    }
}
