/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.types.hfscommon;

import org.catacombae.csjc.PrintableStruct;

/**
 *
 * @author erik
 */
public abstract class CommonBTRecord implements PrintableStruct {
    public abstract int getSize();
    public abstract byte[] getBytes();
}
