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

package org.catacombae.hfs.plus;

import org.catacombae.hfs.types.hfsplus.HFSPlusVolumeHeader;
import org.catacombae.hfs.types.hfsplus.JournalInfoBlock;
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

        if(infoBlock.getRawOffset() < 0)
            throw new Error("SInt64 overflow for JournalInfoBlock.offset!");
        if(infoBlock.getRawSize() < 0)
            throw new Error("SInt64 overflow for JournalInfoBlock.size!");
        else if(infoBlock.getRawSize() > Integer.MAX_VALUE)
            throw new RuntimeException("Java int overflow!");

        byte[] dataBuffer = new byte[(int)infoBlock.getRawSize()];

        ReadableRandomAccessStream fsStream = vol.createFSStream();
        try {
            fsStream.seek(infoBlock.getRawOffset());
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
