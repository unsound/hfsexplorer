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

package org.catacombae.jparted.lib.fs;

/**
 *
 * @author Erik
 */
public class WindowsFileAttributes {
    private static final int FILE_ATTRIBUTE_READONLY = 0x1;
    private static final int FILE_ATTRIBUTE_HIDDEN = 0x2;
    private static final int FILE_ATTRIBUTE_SYSTEM = 0x4;
    private static final int FILE_ATTRIBUTE_DIRECTORY = 0x10;
    private static final int FILE_ATTRIBUTE_ARCHIVE = 0x20;
    private static final int FILE_ATTRIBUTE_NORMAL = 0x80;
    private static final int FILE_ATTRIBUTE_TEMPORARY = 0x100;
    private static final int FILE_ATTRIBUTE_SPARSE_FILE = 0x200;
    private static final int FILE_ATTRIBUTE_REPARSE_POINT = 0x400;
    private static final int FILE_ATTRIBUTE_COMPRESSED = 0x800;
    private static final int FILE_ATTRIBUTE_ENCRYPTED = 0x4000;
    private static final int FILE_ATTRIBUTE_OFFLINE = 0x1000;
    private static final int FILE_ATTRIBUTE_VIRTUAL = 0x10000;
    
    private final int attributeDword;
    
    /**
     * Creates a new WindowsFileAttributes from a 32 bit integer holding the
     * flags as specified in the <code>dwFileAttributes</code> member of
     * <code>struct BY_HANDLE_FILE_INFORMATION</code> (see
     * <a href="http://msdn.microsoft.com/en-us/library/aa363788(VS.85).aspx">
     * MSDN</a>).
     * 
     * @param iAttributeDword
     */
    public WindowsFileAttributes(int iAttributeDword) {
        attributeDword = iAttributeDword;
    }
    
    /**
     * The file or directory is read-only. Applications can read the file,
     * but cannot write to it or delete it. If it is a directory,
     * applications cannot delete it.
     */
    public boolean isReadOnly() {
        return (attributeDword & FILE_ATTRIBUTE_READONLY) != 0;
    }

    /**
     * The file or directory is hidden. It is not included in an ordinary
     * directory listing.
     */
    public boolean isHidden() {
        return (attributeDword & FILE_ATTRIBUTE_HIDDEN) != 0;
    }

    /**
     * The file or directory is part of the operating system or is used
     * exclusively by the operating system.
     */
    public boolean isSystem() {
        return (attributeDword & FILE_ATTRIBUTE_SYSTEM) != 0;
    }

    /** The handle identifies a directory. */
    public boolean isDirectory() {
        return (attributeDword & FILE_ATTRIBUTE_DIRECTORY) != 0;
    }

    /**
     * The file or directory is an archive file. Applications use this
     * attribute to mark files for backup or removal.
     */
    public boolean isArchive() {
        return (attributeDword & FILE_ATTRIBUTE_ARCHIVE) != 0;
    }

    /**
     * The file does not have other attributes. This attribute is valid only
     * if used alone.
     */
    public boolean isNormal() {
        return (attributeDword & FILE_ATTRIBUTE_NORMAL) != 0;
    }

    /**
     * The file is being used for temporary storage. File systems avoid
     * writing data back to mass storage if sufficient cache memory is
     * available, because often the application deletes the temporary file
     * after the handle is closed. In that case, the system can entirely
     * avoid writing the data. Otherwise, the data will be written after the
     * handle is closed.
     */
    public boolean isTemporary() {
        return (attributeDword & FILE_ATTRIBUTE_TEMPORARY) != 0;
    }

    /** The file is a sparse file. */
    public boolean isSparseFile() {
        return (attributeDword & FILE_ATTRIBUTE_SPARSE_FILE) != 0;
    }

    /** The file or directory has an associated reparse point. */
    public boolean isReparsePoint() {
        return (attributeDword & FILE_ATTRIBUTE_REPARSE_POINT) != 0;
    }

    /**
     * The file or directory is compressed. For a file, this means that all
     * of the data in the file is compressed. For a directory, this means
     * that compression is the default for newly created files and
     * subdirectories.
     */
    public boolean isCompressed() {
        return (attributeDword & FILE_ATTRIBUTE_COMPRESSED) != 0;
    }

    /**
     * The file or directory is encrypted. For a file, this means that all
     * data in the file is encrypted. For a directory, this means that
     * encryption is the default for newly created files and subdirectories.
     */
    public boolean isEncrypted() {
        return (attributeDword & FILE_ATTRIBUTE_ENCRYPTED) != 0;
    }

    /**
     * The file data is not available immediately. This attribute indicates
     * that the file data is physically moved to offline storage. This
     * attribute is used by Remote Storage, the hierarchical storage
     * management software. Applications should not arbitrarily change this
     * attribute.
     */
    public boolean isOffline() {
        return (attributeDword & FILE_ATTRIBUTE_OFFLINE) != 0;
    }

    /** A file is a virtual file. */
    public boolean isVirtual() {
        return (attributeDword & FILE_ATTRIBUTE_VIRTUAL) != 0;
    }
}

