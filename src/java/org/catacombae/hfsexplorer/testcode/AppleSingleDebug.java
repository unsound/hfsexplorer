/*-
 * Copyright (C) 2008 Erik Larsson
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

import org.catacombae.hfs.types.finder.ExtendedFileInfo;
import org.catacombae.hfs.types.finder.FileInfo;
import org.catacombae.hfsexplorer.fs.AppleSingleHandler;
import org.catacombae.hfsexplorer.types.applesingle.AttributeEntry;
import org.catacombae.hfsexplorer.types.applesingle.AttributeHeader;
import org.catacombae.hfsexplorer.types.applesingle.EntryDescriptor;
import org.catacombae.io.ReadableFileStream;

/**
 * @author <a href="https://catacombae.org" target="_top">Erik Larsson</a>
 */
public class AppleSingleDebug {
    public static void main(String[] args) {
        int retval = 1;
        if(args.length != 1) {
            System.err.println("usage: AppleSingleDebug <AppleSingle file>");
        }
        else {
            ReadableFileStream is = new ReadableFileStream(args[0]);
            AppleSingleHandler handler = new AppleSingleHandler(is);
            System.out.println("Header:");
            handler.getHeader().print(System.out, "  ");

            int i = 0;
            for(EntryDescriptor ed : handler.getEntryDescriptors()) {
                System.out.println("EntryDescriptor[" + i++ +"]:");
                ed.print(System.out, "  ");

                if(ed.getEntryId() == EntryDescriptor.ENTRY_ID_FINDERINFO &&
                        ed.getEntryLength() >= 32)
                {
                    final byte[] finderInfoData = new byte[32];
                    final int entryOffset = ed.getEntryOffset();

                    is.seek(entryOffset);
                    is.readFully(finderInfoData);

                    FileInfo fi = new FileInfo(finderInfoData, 0);
                    ExtendedFileInfo ei =
                            new ExtendedFileInfo(finderInfoData, 16);
                    System.out.println("    Finder info:");
                    fi.print(System.out, "     ");
                    ei.print(System.out, "     ");
                }

                if(ed.getEntryId() == EntryDescriptor.ENTRY_ID_FINDERINFO &&
                        ed.getEntryLength() >
                        (32 + 2 + AttributeHeader.STRUCTSIZE))
                {
                    final byte[] finderInfoData = new byte[ed.getEntryLength()];
                    final int entryOffset = ed.getEntryOffset();

                    is.seek(entryOffset);
                    is.readFully(finderInfoData);

                    final AttributeHeader header =
                            new AttributeHeader(finderInfoData, 32 + 2);
                    if(header.getMagic() == AttributeHeader.MAGIC) {
                        header.print(System.out, "    ");

                        final int numAttrs = header.getNumAttrs();
                        int curOffset = 32 + 2 + AttributeHeader.STRUCTSIZE;
                        for(i = 0; i < numAttrs; ++i) {
                            final AttributeEntry ae =
                                    new AttributeEntry(finderInfoData,
                                    curOffset);

                            System.out.println("    Attribute entry " +
                                    (i + 1) + " @ " + (entryOffset + curOffset) + ":");
                            ae.print(System.out, "     ");

                            int nextOffset = curOffset + ae.occupiedSize();

                            /* Entries are always 4-byte aligned, so skip any
                             * bytes up to the next 4 byte boundary. */
                            final int remainder =
                                    (entryOffset + nextOffset) & 0x3;
                            if(remainder != 0) {
                                nextOffset += 4 - remainder;
                            }

                            curOffset = nextOffset;
                        }
                    }
                }
            }
            retval = 0;
        }

        System.exit(retval);
    }
}
