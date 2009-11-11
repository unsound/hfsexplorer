/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfs.plus;

import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusVolumeHeader;
import org.catacombae.hfsexplorer.types.hfsplus.JournalInfoBlock;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.hfs.Journal;
import org.catacombae.util.Util;

/**
 *
 * @author erik
 */
class HFSPlusJournal extends Journal {
    private final HFSPlusVolume vol;

    public HFSPlusJournal(HFSPlusVolume vol) {
        this.vol = vol;
    }

    @Override
    public byte[] getInfoBlockData() {
        HFSPlusVolumeHeader vh = vol.getHFSPlusVolumeHeader();
        if(vh.getAttributeVolumeJournaled()) {
            long blockNumber = Util.unsign(vh.getJournalInfoBlock());
            byte[] data = new byte[JournalInfoBlock.getStructSize()];

            ReadableRandomAccessStream fsStream = vol.createFSStream();
            try {
                fsStream.seek(blockNumber * vh.getBlockSize());
                fsStream.readFully(data);
            } finally {
                fsStream.close();
            }

            return data;
        }
        else
            return null;
    }

    @Override
    public byte[] getJournalData() {
        /* TODO: Maybe it's sane to return a stream and not a byte[] since we
         * don't know the size of the journal? */
        JournalInfoBlock infoBlock = getJournalInfoBlock();

        if(infoBlock.getOffset() < 0)
            throw new Error("SInt64 overflow for JournalInfoBlock.offset!");
        if(infoBlock.getSize() < 0)
            throw new Error("SInt64 overflow for JournalInfoBlock.size!");
        else if(infoBlock.getSize() > Integer.MAX_VALUE)
            throw new RuntimeException("Java int overflow!");

        byte[] dataBuffer = new byte[(int)infoBlock.getSize()];

        ReadableRandomAccessStream fsStream = vol.createFSStream();
        try {
            fsStream.seek(infoBlock.getOffset());
            fsStream.readFully(dataBuffer);
        } finally {
            fsStream.close();
        }

        return dataBuffer;
    }

    @Override
    public JournalInfoBlock getJournalInfoBlock() {
        byte[] infoBlockData = getInfoBlockData();
        if(infoBlockData != null)
            return new JournalInfoBlock(infoBlockData, 0);
        else
            return null;
    }
}
