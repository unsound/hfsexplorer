/*-
 * Copyright (C) 2008 Erik Larsson
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

package org.catacombae.hfsexplorer.types.hfscommon;

/**
 * Decodes data, byte by byte, into a Java string.
 * 
 * @author Erik Larsson
 */
public interface StringDecoder {
    /**
     * Decodes the specified data into a string.
     * 
     * @param data the data to decode.
     * @param off the offset in <code>data</code> to start reading at.
     * @param len the amount of data to process.
     * @return the decoded string.
     */
    public String decode(byte[] data, int off, int len);
}
