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

import java.util.Iterator;
import java.util.LinkedList;
import org.catacombae.hfs.types.hfsplus.HFSPlusVolumeHeader;
import org.catacombae.hfs.types.hfsplus.JournalInfoBlock;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.hfs.Journal;
import org.catacombae.hfs.types.hfsplus.BlockInfo;
import org.catacombae.hfs.types.hfsplus.BlockList;
import org.catacombae.hfs.types.hfsplus.BlockListHeader;
import org.catacombae.hfs.types.hfsplus.JournalHeader;
import org.catacombae.io.ReadableConcatenatedStream;
import org.catacombae.io.RuntimeIOException;
import org.catacombae.util.ObjectContainer;
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
    public ReadableRandomAccessStream getJournalDataStream() {
        JournalInfoBlock infoBlock = getJournalInfoBlock();

        return getJournalDataStream(infoBlock);
    }

    private ReadableRandomAccessStream getJournalDataStream(
            JournalInfoBlock infoBlock)
    {
        if(!infoBlock.getFlagJournalInFS()) {
            /* Searching other devices for the journal is unsupported at this
             * time. */
            return null;
        }

        if(infoBlock.getFlagJournalNeedInit()) {
            /* Journal needs to be initialized and does not contain any valid
             * data. In this case we also return null, because whatever data
             * might be in the journal is definitely invalid. */
            return null;
        }

        if(infoBlock.getRawOffset() < 0)
            throw new Error("SInt64 overflow for JournalInfoBlock.offset!");
        if(infoBlock.getRawSize() < 0)
            throw new Error("SInt64 overflow for JournalInfoBlock.size!");

        return new ReadableConcatenatedStream(vol.createFSStream(),
                infoBlock.getRawOffset(), infoBlock.getRawSize());
    }

    @Override
    public byte[] getJournalData() {
        /* TODO: Maybe it's sane to return a stream and not a byte[] since we
         * don't know the size of the journal? */
        ReadableRandomAccessStream fsStream = getJournalDataStream();
        if(fsStream.length() > Integer.MAX_VALUE)
            throw new RuntimeException("Java int overflow!");

        byte[] dataBuffer = new byte[(int) fsStream.length()];
        try {
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

    @Override
    public JournalHeader getJournalHeader() {
        final JournalInfoBlock infoBlock = getJournalInfoBlock();
        final ReadableRandomAccessStream journalStream =
                getJournalDataStream(infoBlock);

        try {
            return journalStream != null ?
                getJournalHeader(infoBlock, journalStream) : null;
        } finally {
            if(journalStream != null) {
                journalStream.close();
            }
        }
    }

    private JournalHeader getJournalHeader(JournalInfoBlock infoBlock,
            ReadableRandomAccessStream journalStream)
    {
        final byte[] headerData = new byte[JournalHeader.length()];

        journalStream.readFully(headerData);

        JournalHeader jh = new JournalHeader(headerData, 0);
        if(jh.getRawChecksum() != jh.calculateChecksum()) {
            throw new RuntimeException("Invalid journal header checksum " +
                    "(expected 0x" + Util.toHexStringBE(jh.getRawChecksum()) +
                    ", got 0x" + Util.toHexStringBE(jh.calculateChecksum()) +
                    ").");
        }
        if(infoBlock.getRawSize() != jh.getRawSize()) {
            throw new RuntimeException("Inconsistency between journal size " +
                    "as described by journal info block (" +
                    infoBlock.getSize() + ") and journal header (" +
                    jh.getSize() + ").");
        }

        return jh;
    }

    @Override
    public boolean isClean() {
        final JournalHeader journalHeader = getJournalHeader();

        return journalHeader.getRawStart() == journalHeader.getRawEnd();
    }

    private long wrappedReadFully(ReadableRandomAccessStream stream,
            long currentPos, byte[] data, int offset, int length,
            ObjectContainer<Boolean> wrappedAround)
    {
        int bytesRead;
        long res;

        bytesRead = stream.read(data, offset, length);
        if(bytesRead != length) {
            /* Short read. Wrap around. */
            if(wrappedAround.o) {
                throw new RuntimeException("Wrapped around twice!");
            }

            res = length - bytesRead;
            wrappedAround.o = true;
            stream.seek(0);
            bytesRead += stream.read(data, offset + bytesRead,
                    length - bytesRead);
        }
        else {
            res = currentPos + length;
        }

        if(bytesRead != length) {
            throw new RuntimeIOException("Failed to read requested " +
                    "amount when doing wrapped read. Expected " + length + " " +
                    "bytes, got " + bytesRead + " bytes.");
        }

        return res;
    }

    private long wrappedReadFully(ReadableRandomAccessStream stream,
            long currentPos, byte[] data,
            ObjectContainer<Boolean> wrappedAround)
    {
        return wrappedReadFully(stream, currentPos, data, 0, data.length,
                wrappedAround);
    }

    public Transaction[] getPendingTransactions() {
        final JournalInfoBlock infoBlock = getJournalInfoBlock();
        final ReadableRandomAccessStream journalStream =
                getJournalDataStream(infoBlock);
        final JournalHeader jh = getJournalHeader(infoBlock, journalStream);
        final long start = jh.getRawStart();
        final long end = jh.getRawEnd();
        final long size = jh.getRawSize();
        final int blockListHeaderSize = jh.getRawBlhdrSize();

        if(start < 0)
            throw new RuntimeException("'start' overflows.");
        if(end < 0)
            throw new RuntimeException("'end' overflows.");
        if(size < 0)
            throw new RuntimeException("'size' overflows.");
        if(blockListHeaderSize < 0)
            throw new RuntimeException("'blockListHeaderSize' overflows.");
        if(start == end) {
            return new Transaction[0];
        }

        final LinkedList<Transaction> pendingTransactionList =
                new LinkedList<Transaction>();
        final LinkedList<BlockList> curBlockListList =
                new LinkedList<BlockList>();
        final LinkedList<BlockInfo> curBlockInfoList =
                new LinkedList<BlockInfo>();

        ObjectContainer<Boolean> wrappedAround =
                new ObjectContainer<Boolean>(false);
        byte[] tmpData = new byte[Math.max(BlockListHeader.length(),
                BlockInfo.length())];

        journalStream.seek(start);
        for(long i = start; i != end;) {
            long curBytesRead = 0;

            i = wrappedReadFully(journalStream, i, tmpData, 0,
                BlockListHeader.length(), wrappedAround);

            BlockListHeader curHeader =
                    new BlockListHeader(tmpData, 0, jh.isLittleEndian());
            if(curHeader.getNumBlocks() < 1) {
                throw new RuntimeException("Empty block list makes no sense.");
            }
            else if((curHeader.getMaxBlocks() * 16 + 16) != blockListHeaderSize)
            {
                throw new RuntimeException("Unexpected value for maxBlocks " +
                        "member of BlockListHeader: " +
                        curHeader.getMaxBlocks());
            }

            curBytesRead += BlockListHeader.length();

            curBlockInfoList.clear();
            for(int j = 0; j < curHeader.getNumBlocks(); ++j) {
                i = wrappedReadFully(journalStream, i, tmpData, 0,
                    BlockInfo.length(), wrappedAround);

                curBlockInfoList.add(new BlockInfo(tmpData, 0,
                        jh.isLittleEndian()));

                curBytesRead += BlockInfo.length();
            }

            if(curHeader.calculateChecksum(curBlockInfoList.getFirst()) !=
                    curHeader.getRawChecksum())
            {
                throw new RuntimeException("Checksum mismatch for header "
                        + "(expected: 0x" +
                        Util.toHexStringBE(curHeader.getRawChecksum()) + " "
                        + "actual: 0x" +
                        Util.toHexStringBE(curHeader.calculateChecksum(
                        curBlockInfoList.getFirst())) + ")");
            }

            byte[] curReserved =
                    new byte[(int) (blockListHeaderSize - curBytesRead)];
            i = wrappedReadFully(journalStream, i, curReserved, wrappedAround);

            curBytesRead += curReserved.length;

            LinkedList<byte[]> curBlockDataList = new LinkedList<byte[]>();
            for(Iterator<BlockInfo> it = curBlockInfoList.iterator();
                it.hasNext();)
            {
                final BlockInfo bi = it.next();

                if(curBlockDataList.size() < 1) {
                    /* Skip first BlockInfo because it's not actually
                     * referencing any data. */
                    curBlockDataList.add(new byte[0]);
                    continue;
                }

                final int bsize = bi.getRawBsize();

                if(bsize > Integer.MAX_VALUE) {
                    throw new RuntimeException("'int' overflow in 'bsize' (" +
                            bi.getBsize() + ").");
                }

                final byte[] data = new byte[bsize];
                i = wrappedReadFully(journalStream, i, data, wrappedAround);

                curBytesRead += data.length;

                curBlockDataList.add(data);
            }

            BlockList curBlockList = new BlockList(curHeader,
                    curBlockInfoList.toArray(
                    new BlockInfo[curBlockInfoList.size()]),
                    curReserved,
                    curBlockDataList.toArray(
                    new byte[curBlockDataList.size()][]));
            curBlockListList.add(curBlockList);

            if(curBlockList.getBlockInfo(0).getNext() == 0) {
                pendingTransactionList.add(new Transaction(
                        curBlockListList.toArray(
                        new BlockList[curBlockListList.size()])));
                curBlockListList.clear();
            }
        }

        if(curBlockListList.size() != 0) {
            pendingTransactionList.add(new Transaction(curBlockListList.toArray(
                    new BlockList[curBlockListList.size()])));
        }

        return pendingTransactionList.toArray(
                new Transaction[pendingTransactionList.size()]);
    }
}
