/*-
 * Copyright (C) 2006-2009 Erik Larsson
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catacombae.hfs;

import org.catacombae.hfs.types.hfsplus.JournalHeader;
import org.catacombae.hfs.types.hfsplus.JournalInfoBlock;

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

    /**
     * Returns the journal header if a journal is present, null otherwise.
     * @return the journal header if a journal is present, null otherwise.
     */
    public abstract JournalHeader getJournalHeader();

    /**
     * Returns whether the journal is clean, i.e. has no pending transactions.
     * @return whether the journal is clean, i.e. has no pending transactions.
     */
    public abstract boolean isClean();
}
