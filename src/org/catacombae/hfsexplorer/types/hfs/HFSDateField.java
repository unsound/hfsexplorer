/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.types.hfs;

import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusDateField;

/**
 *
 * @author erik
 */
public class HFSDateField extends HFSPlusDateField {
    public HFSDateField(byte[] data) {
        this(data, 0, data.length);
    }

    public HFSDateField(byte[] data, int offset, int length) {
        super("HFSDate", data, offset, length, true);
    }
}
