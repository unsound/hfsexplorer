/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfs;

import org.catacombae.hfsexplorer.types.hfsplus.JournalInfoBlock;

/**
 *
 * @author erik
 */
public abstract class Journal {
    public abstract byte[] getInfoBlockData();
    public abstract byte[] getJournalData();

    /**
     * Returns the journal info block if a journal is present, null otherwise.
     * @return the journal info block if a journal is present, null otherwise.
     */
    public abstract JournalInfoBlock getJournalInfoBlock();
}
