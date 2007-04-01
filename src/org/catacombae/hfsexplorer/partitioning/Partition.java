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

public class Partition {
    // Just listing the partition types that come to mind...
    public static enum PartitionType  { APPLE_UFS, APPLE_HFS, APPLE_HFS_PLUS, APPLE_HFSX, 
					 FAT12, FAT16, FAT32, NTFS, HPFS, 
					 LINUX_SWAP, EXT2, EXT3, REISERFS, XFS, ZFS };
    protected long startOffset;
    protected long length;
    protected PartitionType type;
    
    public Partition(long startOffset, long length, PartitionType type) {
	this.startOffset = startOffset;
	this.length = length;
	this.type = type;
    }
    protected Partition() {} // Only for those that know what they're doing ;)
    
    public long getStartOffset() { return startOffset; }
    public long getLength() { return length; }
    public PartitionType getType() { return type; }
}