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

package org.catacombae.hfsexplorer.types.hfsplus;

import java.text.DateFormat;
import org.catacombae.csjc.structelements.FieldType;
import org.catacombae.csjc.structelements.StringRepresentableField;
import org.catacombae.hfsexplorer.Util;

/**
 *
 * @author erik
 */
public class HFSPlusDateField extends StringRepresentableField {
    private static final Object dtiSync = new Object();
    private static final DateFormat dti =
            DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

    private final byte[] data;
    private final int offset;
    private final int length;
    private final boolean localTime;

    public HFSPlusDateField(byte[] data, boolean localTime) {
        this(data, 0, data.length, localTime);
    }

    public HFSPlusDateField(byte[] data, int offset, int length, boolean localTime) {
        this("HFSDate", data, offset, length, localTime);
    }

    protected HFSPlusDateField(String typeName, byte[] data, int offset, int length, boolean localTime) {
        super(typeName, FieldType.DATE);
        this.data = data;
        this.offset = offset;
        this.length = length;
        this.localTime = localTime;
    }

    @Override
    public String getValueAsString() {
        synchronized(dtiSync) {
            if(localTime)
                return dti.format(HFSPlusDate.localTimestampToDate(Util.readIntBE(data, offset)));
            else
                return dti.format(HFSPlusDate.gmtTimestampToDate(Util.readIntBE(data, offset)));
        }
    }

    @Override
    public void setStringValue(String value) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String validateStringValue(String s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
