/*-
 * Copyright (C) 2006-2007 Erik Larsson
 * 
 * All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 */

package org.catacombae.hfsexplorer.partitioning;
import org.catacombae.hfsexplorer.Util;

public class GPTHeader {
    private static final long GPT_SIGNATURE = 0x4546492050415254L;
    private final byte[] signature = new byte[8];
    
    public GPTHeader(byte[] data, int offset) {
	System.arraycopy(data, offset+0, signature, 0, 8);
    }
    
    public long getSignature() { return Util.readLongBE(signature); }
    
    public boolean isValid() { return getSignature() == GPT_SIGNATURE; }
}