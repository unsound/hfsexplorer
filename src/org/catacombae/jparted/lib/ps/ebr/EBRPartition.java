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

package org.catacombae.jparted.lib.ps.ebr;

import org.catacombae.hfsexplorer.partitioning.MBRPartition;

public class EBRPartition extends MBRPartition {
    
    private final long baseOffset;

    /**
     *
     * @param data
     * @param offset offset in <code>data</code> to the EBR partition data.
     * @param baseOffset the base offset which we will resolve startOffset
     * against.
     * @param sectorSize
     */
    public EBRPartition(byte[] data, int offset, long baseOffset, int sectorSize) {
        super(data, offset, sectorSize);
        this.baseOffset = baseOffset;
    }
    
    /** Copy constructor. */
    public EBRPartition(EBRPartition source) {
        super(source);
        this.baseOffset = source.baseOffset;
    }
    
    // Defined in Partition
    @Override
    public long getStartOffset() { return super.getStartOffset()+baseOffset; }
}
