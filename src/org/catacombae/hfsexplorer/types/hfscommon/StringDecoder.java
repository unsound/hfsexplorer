/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.types.hfscommon;

/**
 *
 * @author erik
 */
public interface StringDecoder {
    public String decode(byte[] data, int off, int len);
}
