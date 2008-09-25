/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.types.hfscommon;

/**
 *
 * @author erik
 */
public interface StringComparator {
    public int compare(byte[] a, int aoff, int alen, byte[] b, int boff, int blen);
}
