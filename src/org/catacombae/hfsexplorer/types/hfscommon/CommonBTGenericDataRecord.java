/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.types.hfscommon;

import java.io.PrintStream;
import org.catacombae.hfsexplorer.Util;

/**
 * Record with unspecified data.
 *
 * @author erik
 */
public class CommonBTGenericDataRecord extends CommonBTRecord {
    private final byte[] data;

    public CommonBTGenericDataRecord(byte[] data, int offset, int length) {
        this.data = new byte[length];
        System.arraycopy(data, offset, this.data, 0, length);
    }

    @Override
    public int getSize() {
        return data.length;
    }

    @Override
    public byte[] getBytes() {
        return Util.createCopy(data);
    }

    public void print(PrintStream ps, String prefix) {
        ps.println(prefix + "CommonBTGenericDataRecord:");
        printFields(ps, prefix + " ");
    }

    public void printFields(PrintStream ps, String prefix) {
        ps.println(prefix + "data: byte[" + data.length + "] (" + data + ")");
    }

}
