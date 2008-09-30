/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
