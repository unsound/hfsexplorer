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

import java.util.Date;
import org.catacombae.hfsexplorer.Util;

/**
 *
 * @author <a href="mailto:erik82@kth.se">Erik Larsson</a>
 */
public abstract class FSAttributes {

    public abstract boolean hasPOSIXFileAttributes();
    public abstract POSIXFileAttributes getPOSIXFileAttributes();
    
    public abstract boolean hasWindowsFileAttributes();
    public abstract WindowsFileAttributes getWindowsFileAttributes();
    
    public abstract boolean hasCreateDate();
    public abstract Date getCreateDate();
    
    public abstract boolean hasModifyDate();
    public abstract Date getModifyDate();
    
    public abstract boolean hasAttributeModifyDate();
    public abstract Date getAttributeModifyDate();
    
    public abstract boolean hasAccessDate();
    public abstract Date getAccessDate();
    
    public abstract boolean hasBackupDate();
    public abstract Date getBackupDate();
    
    //public abstract FSAccessControlList getAccessControlList();
    
    
    public static abstract class POSIXFileAttributes {

        /** One of the valid return values for <code>getFileType();</code> */
        public static final byte FILETYPE_UNDEFINED = 00;
        /** One of the valid return values for <code>getFileType();</code> */
        public static final byte FILETYPE_FIFO = 01;
        /** One of the valid return values for <code>getFileType();</code> */
        public static final byte FILETYPE_CHARACTER_SPECIAL = 02;
        /** One of the valid return values for <code>getFileType();</code> */
        public static final byte FILETYPE_DIRECTORY = 04;
        /** One of the valid return values for <code>getFileType();</code> */
        public static final byte FILETYPE_BLOCK_SPECIAL = 06;
        /** One of the valid return values for <code>getFileType();</code> */
        public static final byte FILETYPE_REGULAR = 010;
        /** One of the valid return values for <code>getFileType();</code> */
        public static final byte FILETYPE_SYMBOLIC_LINK = 012;
        /** One of the valid return values for <code>getFileType();</code> */
        public static final byte FILETYPE_SOCKET = 014;
        /** One of the valid return values for <code>getFileType();</code> */
        public static final byte FILETYPE_WHITEOUT = 016;

        public abstract long getUserID();
        public abstract long getGroupID();

        public abstract byte getFileType();

        public abstract boolean canUserRead();
        public abstract boolean canUserWrite();
        public abstract boolean canUserExecute();
        public abstract boolean canGroupRead();
        public abstract boolean canGroupWrite();
        public abstract boolean canGroupExecute();
        public abstract boolean canOthersRead();
        public abstract boolean canOthersWrite();
        public abstract boolean canOthersExecute();
        public abstract boolean isSetUID();
        public abstract boolean isSetGID();
        public abstract boolean isStickyBit();

        /**
         * Returns the POSIX-type file mode string for this file, as it would appear
         * when listing it with 'ls -l'. Example: <code>drwxr-x---</code>.
         *
         * @return the POSIX-type file mode string for this file.
         */
        public abstract String getPermissionString();
    }

    public static class DefaultPOSIXFileAttributes extends POSIXFileAttributes {
        private final short fileMode;
        private final long userID;
        private final long groupID;
        
        public DefaultPOSIXFileAttributes(long userID, long groupID, short fileMode) {
            this.userID = userID;
            this.groupID = groupID;
            this.fileMode = fileMode;
        }
        
        /** {@inheritDoc} */
        @Override
        public long getUserID() { return userID; }
        
        /** {@inheritDoc} */
        @Override
        public long getGroupID() { return groupID; }

        /** {@inheritDoc} */
        @Override
        public byte getFileType() {
            int type = (fileMode >> 12) & 017;
            return (byte) type;
        }

        /** {@inheritDoc} */
        @Override
        public boolean isSetUID() { return Util.getBit(fileMode, 11); }

        /** {@inheritDoc} */
        @Override
        public boolean isSetGID() { return Util.getBit(fileMode, 10); }

        /** {@inheritDoc} */
        @Override
        public boolean isStickyBit() { return Util.getBit(fileMode, 9); }

        /** {@inheritDoc} */
        @Override
        public boolean canUserRead() { return Util.getBit(fileMode, 8); }

        /** {@inheritDoc} */
        @Override
        public boolean canUserWrite() { return Util.getBit(fileMode, 7); }

        /** {@inheritDoc} */
        @Override
        public boolean canUserExecute() { return Util.getBit(fileMode, 6); }

        /** {@inheritDoc} */
        @Override
        public boolean canGroupRead() { return Util.getBit(fileMode, 5); }

        /** {@inheritDoc} */
        @Override
        public boolean canGroupWrite() { return Util.getBit(fileMode, 4); }

        /** {@inheritDoc} */
        @Override
        public boolean canGroupExecute() { return Util.getBit(fileMode, 3); }

        /** {@inheritDoc} */
        @Override
        public boolean canOthersRead() { return Util.getBit(fileMode, 2); }

        /** {@inheritDoc} */
        @Override
        public boolean canOthersWrite() { return Util.getBit(fileMode, 1); }

        /** {@inheritDoc} */
        @Override
        public boolean canOthersExecute() { return Util.getBit(fileMode, 0); }

        /** {@inheritDoc} */
        @Override
        public String getPermissionString() {
            String result;
            byte fileType = getFileType();
            switch(fileType) {
                case FILETYPE_UNDEFINED: // This one appears at the root node (CNID 2) sometimes. dunno what it would look like in ls -l
                    result = "?";
                    break;
                case FILETYPE_FIFO:
                    result = "p";
                    break;
                case FILETYPE_CHARACTER_SPECIAL:
                    result = "c";
                    break;
                case FILETYPE_DIRECTORY:
                    result = "d";
                    break;
                case FILETYPE_BLOCK_SPECIAL:
                    result = "b";
                    break;
                case FILETYPE_REGULAR:
                    result = "-";
                    break;
                case FILETYPE_SYMBOLIC_LINK:
                    result = "l";
                    break;
                case FILETYPE_SOCKET:
                    result = "s";
                    break;
                case FILETYPE_WHITEOUT:
                    result = "w";
                    break; // How does this appear in "ls -l" ? and what is it?
                default:
                    result = " ";
                    System.err.println(
                            "[FSAttributes.POSIXFileAttributes.getFileModeString()] " +
                            "Unknown file type:  " + fileType +
                            " Mode: 0x" + Util.toHexStringBE(fileMode));
            }

            if(canUserRead())
                result += "r";
            else
                result += "-";
            if(canUserWrite())
                result += "w";
            else
                result += "-";
            if(canUserExecute()) {
                if(isSetUID())
                    result += "s";
                else
                    result += "x";
            }
            else {
                if(isSetUID())
                    result += "S";
                else
                    result += "-";
            }

            if(canGroupRead())
                result += "r";
            else
                result += "-";
            if(canGroupWrite())
                result += "w";
            else
                result += "-";
            if(canGroupExecute()) {
                if(isSetGID())
                    result += "s";
                else
                    result += "x";
            }
            else {
                if(isSetGID())
                    result += "S";
                else
                    result += "-";
            }
            if(canOthersRead())
                result += "r";
            else
                result += "-";
            if(canOthersWrite())
                result += "w";
            else
                result += "-";
            if(canOthersExecute()) {
                if(isStickyBit())
                    result += "t";
                else
                    result += "x";
            }
            else {
                if(isStickyBit())
                    result += "T";
                else
                    result += "-";
            }

            return result;
        }
    }
}
