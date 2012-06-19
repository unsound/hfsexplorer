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
import org.catacombae.hfs.types.hfsplus.HFSPlusAttributesLeafRecord;
import org.catacombae.hfs.types.hfsplus.HFSPlusAttributesLeafRecordData;

/**
 *
 * @author erik
 */
public abstract class CommonHFSAttributesLeafRecord extends CommonBTRecord
        implements StructElements
{
    public static CommonHFSAttributesLeafRecord create(
            HFSPlusAttributesLeafRecord record)
    {
        return new HFSPlusImplementation(record);
    }

    public abstract CommonHFSAttributesKey getKey();

    public abstract HFSPlusAttributesLeafRecordData getRecordData();

    /* @Override */
    public void print(PrintStream ps, String prefix) {
        ps.println(prefix + getClass().getSimpleName() + ":");
        printFields(ps, prefix + " ");
    }

    private static class HFSPlusImplementation
            extends CommonHFSAttributesLeafRecord
    {
        private final HFSPlusAttributesLeafRecord record;

        public HFSPlusImplementation(HFSPlusAttributesLeafRecord record) {
            this.record = record;
        }

        @Override
        public CommonHFSAttributesKey getKey() {
            return CommonHFSAttributesKey.create(record.getKey());
        }

        @Override
        public HFSPlusAttributesLeafRecordData getRecordData() {
            return record.getData();
        }

        @Override
        public int getSize() {
            return record.size();
        }

        @Override
        public byte[] getBytes() {
            return record.getBytes();
        }

        /* @Override */
        public void printFields(PrintStream ps, String prefix) {
            record.printFields(ps, prefix);
        }

        /* @Override */
        public Dictionary getStructElements() {
            DictionaryBuilder db = new DictionaryBuilder(
                    "CommonHFSAttributesLeafRecord.HFSPlusImplementation",
                    "HFS+ attributes file leaf record");

            db.addAll(record.getStructElements());

            return db.getResult();
        }
    }
}
