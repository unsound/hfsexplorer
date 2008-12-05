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

package org.catacombae.jparted.lib.ps;

import org.catacombae.jparted.lib.fs.FileSystemMajorType;
import org.catacombae.jparted.lib.ps.container.ContainerType;

/**
 * This enum presents a general representation of the partition types that may
 * be encountered. If FAT32 has several different partition types associated in
 * a MBR table, they all have the same general type.
 * 
 * @author Erik Larsson
 */
public enum PartitionType {
    EMPTY(ContentType.OTHER),
    FAT12(ContentType.FILE_SYSTEM, FileSystemMajorType.FAT12),
    FAT16(ContentType.FILE_SYSTEM, FileSystemMajorType.FAT16),
    FAT32(ContentType.FILE_SYSTEM, FileSystemMajorType.FAT32),
    DOS_EXTENDED(ContentType.PARTITION_SYSTEM, PartitionSystemType.DOS_EXTENDED),
    NT_OS2_IFS(ContentType.CONTAINER, ContainerType.NT_OS2_IFS),
    /** Partition contains a partition map. */
    APPLE_PARTITION_MAP(ContentType.OTHER),
    /** Partition contains a device driver. */
    APPLE_DRIVER(ContentType.OTHER),
    /** Partition contains a SCSI Manager 4.3 device driver. */
    APPLE_DRIVER43(ContentType.OTHER),
    /** Partition uses the original Macintosh File System (64K ROM version). */
    APPLE_MFS(ContentType.FILE_SYSTEM, FileSystemMajorType.APPLE_MFS),
    /**
     * Partition uses the Hierarchical File System (128K and later ROM versions).
     * This can mean HFS or HFS+, which is why this is a container type.
     */
    APPLE_HFS_CONTAINER(ContentType.CONTAINER, ContainerType.APPLE_HFS),
    /** Partition uses HFSX. */
    APPLE_HFSX(ContentType.FILE_SYSTEM, FileSystemMajorType.APPLE_HFSX),
    /**
     * Partition uses the Unix file system. This may include Linux or BSD file
     * systems, so we represent it as a container type.
     */
    APPLE_UNIX_SVR2(ContentType.CONTAINER, ContainerType.APPLE_UNIX_SVR2),
    /** Partition uses the Apple UFS file system format. */
    APPLE_UFS(ContentType.FILE_SYSTEM, FileSystemMajorType.APPLE_UFS),
    /** Partition uses the ProDOS file system. */
    APPLE_PRODOS(ContentType.FILE_SYSTEM, FileSystemMajorType.APPLE_PRODOS),
    LINUX_LVM(ContentType.PARTITION_SYSTEM),
    LINUX_SWAP(ContentType.OTHER),
    LINUX_NATIVE(ContentType.CONTAINER, ContainerType.LINUX_NATIVE),
    GPT_PROTECTIVE(ContentType.PARTITION_SYSTEM),
    EFI_SYSTEM(ContentType.FILE_SYSTEM),
    SPECIAL(ContentType.OTHER),
    UNKNOWN(ContentType.OTHER);
    
    /**
     * We can have three types of processable content in a partition:
     * <ul>
     * <li>File systems</li>
     * <li>Partition systems</li>
     * <li>Containers</li>
     * </ul>
     * File systems and partition systems are well known. The "container" type
     * is what I call partition identifiers that do not uniquely identify a file
     * system and therefore need further probing to decide which file system is
     * on the partition. Examples are Microsoft Basic Data partitions (used for
     * HPFS, NTFS and exFAT), Linux Native partitions (can contain ext3, xfs,
     * reiserfs, etc.), Apple Unix SVR2 (usually contains the Apple UFS file
     * system, but on occasion also contains any of the Linux Native file system
     * types when a PPC linux distribution is installed on an older mac).
     */
    public static enum ContentType {
        FILE_SYSTEM, PARTITION_SYSTEM, CONTAINER, OTHER;
    }
    
    private final ContentType contentType;
    
    // At most one of these three should be set to a value.
    private FileSystemMajorType fsType = null;
    private PartitionSystemType psType = null;
    private ContainerType containerType = null;
         
    private PartitionType(ContentType contentType) {
        this.contentType = contentType;
    }

    private PartitionType(ContentType contentType, FileSystemMajorType fsType) {
        this(contentType);
        if(contentType != ContentType.FILE_SYSTEM)
            throw new RuntimeException("Wrong content type for constructor.");
        this.fsType = fsType;
    }

    private PartitionType(ContentType contentType, PartitionSystemType psType) {
        this(contentType);
        if(contentType != ContentType.PARTITION_SYSTEM)
            throw new RuntimeException("Wrong content type for constructor.");
        this.psType = psType;
    }
    
    private PartitionType(ContentType contentType, ContainerType containerType) {
        this(contentType);
        if(contentType != ContentType.CONTAINER)
            throw new RuntimeException("Wrong content type for constructor.");
        this.containerType = containerType;
    }
    
    public ContentType getContentType() {
        return contentType;
    }
    
    public FileSystemMajorType getAssociatedFileSystemType() {
        if(contentType != ContentType.FILE_SYSTEM)
            throw new RuntimeException("Trying to get the file system type " +
                    "for non file system content!");
        else {
            return fsType;
        }
    }
    
    public PartitionSystemType getAssociatedPartitionSystemType() {
        if(contentType != ContentType.PARTITION_SYSTEM)
            throw new RuntimeException("Trying to get the partition system type " +
                    "for non partition system content!");
        else {
            return psType;
        }
    }
    
    public ContainerType getAssociatedContainerType() {
        if(contentType != ContentType.CONTAINER)
            throw new RuntimeException("Trying to get the container type " +
                    "for non container content!");
        else {
            return containerType;
        }
    }
}
