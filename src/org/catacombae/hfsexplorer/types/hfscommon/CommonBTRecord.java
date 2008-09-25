/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.types.hfscommon;

/**
 *
 * @author erik
 */
public abstract class CommonBTRecord {
    public abstract int getSize();
    public abstract byte[] getBytes();
}
