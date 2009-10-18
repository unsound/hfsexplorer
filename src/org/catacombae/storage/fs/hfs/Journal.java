/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.storage.fs.hfs;

/**
 *
 * @author erik
 */
public abstract class Journal {
    public abstract byte[] getInfoBlockData();
    public abstract byte[] getJournalData();
}
