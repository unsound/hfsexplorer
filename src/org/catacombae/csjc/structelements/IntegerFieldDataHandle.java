/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.csjc.structelements;

import java.lang.reflect.Field;
import org.catacombae.util.Util;

/**
 *
 * @author erik
 */
class IntegerFieldDataHandle implements DataHandle {

    private final Field field;
    private final Object object;
    private final int length;

    public IntegerFieldDataHandle(Object object, Field field, int length) {

        switch(length) {
            case 1:
            case 2:
            case 4:
            case 8:
                break;
            default:
                throw new IllegalArgumentException("Invalid length: " + length);
        }

        this.object = object;
        this.field = field;
        this.length = length;
    }

    public byte[] getBytesAsCopy() {
        try {
            byte[] res;

            switch(length) {
                case 1:
                    res = Util.toByteArrayBE(field.getByte(object));
                    break;
                case 2:
                    res = Util.toByteArrayBE(field.getShort(object));
                    break;
                case 4:
                    res = Util.toByteArrayBE(field.getInt(object));
                    break;
                case 8:
                    res = Util.toByteArrayBE(field.getLong(object));
                    break;
                default:
                    throw new RuntimeException(); // Won't happen.
            }

            return res;
        } catch(IllegalAccessException e) {
            throw new RuntimeException("Illegal access while trying to " +
                    "read field: [" + field, e);
        }
    }

    public byte[] getBytesAsCopy(int offset, int length) {
        return Util.createCopy(getBytesAsCopy(), offset, length);
    }

    public int getLength() {
        return length;
    }
}
