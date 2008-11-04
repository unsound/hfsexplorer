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
package org.catacombae.hfsexplorer;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

/**
 *
 * @author Erik Larsson
 */
public class FileNameTools {
    private static HashSet<String> reservedWindowsFilenames = null;

    /**
     * This method should only return a non-null value when it's ABSOLUTELY SURE that the file
     * can be created.
     * 
     * @param curDirName
     * @param outDir
     * @return
     */
    public static String autoRenameIllegalFilename(String filename, File outDir, boolean isDirectory) {
        final char substituteChar = '_';

        if(FileNameTools.isReservedWindowsFilename(filename)) {
            filename = FileNameTools.getSafeRandomFilename("hfsx");
        }

        char[] filenameChars = filename.toCharArray();

        //System.err.println("filenameChars before: " + new String(filenameChars));
        // Clean out all the usual suspects
        for(int i = 0; i < filenameChars.length; ++i) {
            int c = Util.unsign(filenameChars[i]);

            if(c < 32 || c == 127 || (c >= 0x80 && c <= 0x9F)) {// ASCII/ISO-8859 control characters
                filenameChars[i] = substituteChar;
                //System.err.println("'" + (char)c + "' (" + c + ") matches control character criteria.");
            }
            else if(FileNameTools.isIllegalWindowsCharacter(c)) {
                filenameChars[i] = substituteChar;
                //System.err.println("'" + (char)c + "' (" + c + ") is an illegal Windows character.");
            }
        }
        //System.err.println("filenameChars middle: " + new String(filenameChars));
        
        // Check for trailing dots and spaces
        for(int i = filenameChars.length-1; i >= 0; --i) {
            char c = filenameChars[i];
            if(c == ' ' || c == '.')
                filenameChars[i] = substituteChar;
            else
                break;
        }
        //System.err.println("filenameChars after: " + new String(filenameChars));

        filename = new String(filenameChars);

        String createdFilename = FileNameTools.tryCreate(filename, outDir, isDirectory);
        if(createdFilename != null)
            return createdFilename;

        // Still no-go. Maybe the file name is too long?
        if(filename.length() > 240) { // 255 is a common limit... and 240 is 15 characters less. Might be useful.
            filename = filename.substring(0, 240);
            createdFilename = FileNameTools.tryCreate(filename, outDir, isDirectory);
            if(createdFilename != null)
                return createdFilename;
        }
        if(filename.length() > 27) { // 31 is a common limit... and 27 is 4 characters less. Might be useful.
            filename = filename.substring(0, 27);
            createdFilename = FileNameTools.tryCreate(filename, outDir, isDirectory);
            if(createdFilename != null)
                return createdFilename;
        }
        if(filename.length() > 8) { // 8 is the DOS limit... and 27 is 4 characters less. Might be useful.
            filename = filename.substring(0, 8);
            createdFilename = FileNameTools.tryCreate(filename, outDir, isDirectory);
            if(createdFilename != null)
                return createdFilename;
        }

        // Last resort
        filename = FileNameTools.getSafeRandomFilename("hfsx");
        createdFilename = FileNameTools.tryCreate(filename, outDir, isDirectory);
        if(createdFilename != null)
            return createdFilename;
        else
            return null; // We give up.
    }

    public static String getSafeRandomFilename(String prefix) {
        int suffixLength = 8 - prefix.length();
        if(suffixLength <= 0) {
            return prefix.substring(0, 8);
        }
        else {
            int suffixBase = 1;
            for(int i = 0; i < suffixLength; ++i)
                suffixBase *= 10;
            suffixBase -= 1; // 100 -> 99, 1000 -> 999
            String suffixString = Integer.toString((int) (Math.random() * suffixBase));
            return prefix + suffixString;
        }
    }

    public static String tryCreate(String filename, File outDir, boolean asDirectory) {
        final String originalFilename = filename;
        File f = new File(outDir, filename);

        // Deal with the situation where we already have a file by that name.
        final int limit = 999; // 0-999: 3 characters... maximum DOS file extension.
        int i = 0;
        while(f.exists() && i < limit) {
            filename = originalFilename + "." + i++;
            f = new File(outDir, filename);
        }
        if(f.exists())
            return null;

        try {
            if(asDirectory) {
                if(f.mkdir()) {
                    f.delete();
                    return filename;
                }
            }
            else {
                if(f.createNewFile()) {
                    f.delete();
                    return filename;
                }
            }
        } catch(IOException e) {
            System.err.println("IOException while trying to create \"" + f.getAbsolutePath() +
                    "\" as " + (asDirectory ? "directory" : "file") + ":");
            e.printStackTrace();
        }
        return null;
    }
    
    public static boolean isIllegalWindowsCharacter(char c) {
        return isIllegalWindowsCharacter(Util.unsign(c));
    }
    
    private static boolean isIllegalWindowsCharacter(int c) {
        if(c < 32)
            return true;

        switch(c) {
            case '/':
            case '\\':
            case ':':
            case '*':
            case '?':
            case '"':
            case '<':
            case '>':
            case '|':
                return true;
            default:
                return false;
        }
    }

    public static boolean isReservedWindowsFilename(String filename) {
        if(reservedWindowsFilenames == null)
            reservedWindowsFilenames = buildReservedWindowsFilenames();
        if(reservedWindowsFilenames.contains(filename))
            return true;
        if(filename.charAt(4) == '.' && reservedWindowsFilenames.contains(filename.substring(0, 4)))
            return true;
        if(filename.charAt(3) == '.' && reservedWindowsFilenames.contains(filename.substring(0, 3)))
            return true;

        return false;
    }

    public static HashSet<String> buildReservedWindowsFilenames() {
        HashSet<String> result = new HashSet<String>();
        /*
         * <http://threebit.net/mail-archive/carbon-dev/msg01314.html>
         * 
         * this is what my Visual Studio 2005 has to say about file name:
         * 
         * ------------
         * Naming a File
         * Although each file system can have specific rules about the formation of individual components in a directory or file name, all file systems follow the same general conventions: a base file name and an optional extension, separated by a period.
         * 
         * For example, the MS-DOS FAT file system supports 8 characters for the base file name and 3 characters for the extension. This is known as an 8.3 file name. The FAT file system and the NTFS file system are not limited to 8.3 file names, because they support a long file name.
         * 
         * Naming Conventions
         * 
         * 
         * The following rules enable applications to create and process valid names for files and directories regardless of the file system:
         * 
         * 
         * a.. Use a period (.) to separate the base file name from the extension in a directory name or file name.
         * b.. Backslashes (\) are used to separate components in paths, which divides the file name from the path to it, or one directory from one another in a path. You cannot use backslashes in file or directory names. However, they can be required as part of volume names, for example, "C:\". UNC names must adhere to the following format: \\<server>\<share>.
         * c.. Use any character in the current code page for a name, except characters in the range of 0 through 31, or any character that the file system does not allow. A name can contain characters in the extended character set (128-255). However, it cannot contain the following reserved characters:
         * < > : " / \ |
         * 
         * d.. The following reserved device names cannot be used as the name of a file: CON, PRN, AUX, NUL, COM1, COM2, COM3, COM4, COM5, COM6, COM7, COM8, COM9, LPT1, LPT2, LPT3, LPT4, LPT5, LPT6, LPT7, LPT8, and LPT9. Also avoid these names followed by an extension, for example, NUL.tx7.
         * Windows NT: CLOCK$ is also a reserved device name.
         * e.. Do not assume case sensitivity. Consider names such as OSCAR, Oscar, and oscar to be the same.
         * f.. Do not end a file or directory name with a trailing space or a period. Although the underlying file system may support such names, the operating system does not.
         * g.. Use a period (.) as a directory component in a path to represent the current directory.
         * h.. Use two consecutive periods (..) as a directory component in a path to represent the parent of the current directory.
         * 
         * Maximum Path Length
         * In the Windows API, the maximum length for a path is MAX_PATH, which is defined as 260 characters. A path is structured in the following order: drive letter, colon, backslash, components separated by backslashes, and a null-terminating character, for example, the maximum path on the D drive is D:\<256 chars>NUL.
         * 
         * The Unicode versions of several functions permit a maximum path length of approximately 32,000 characters composed of components up to 255 characters in length. To specify that kind of path, use the "\\?\" prefix.
         * 
         * Note The maximum path of 32,000 characters is approximate, because the "\\?\" prefix can be expanded to a longer string, and the expansion applies to the total length.
         * 
         * For example, "\\?\D:\<path>". To specify such a UNC path, use the "\\?\UNC\" prefix. For example, "\\?\UNC\<server>\<share>". These prefixes are not used as part of the path itself. They indicate that the path should be passed to the system with minimal modification. An implication of this is that you cannot use forward slashes to represent path separators, or a period to represent the current directory. You cannot use the "\\?\" prefix with a relative path. Relative paths are limited to MAX_PATH characters.
         * 
         * When using the API to create a directory, the specified path cannot be so long that you could not append an 8.3 file name.
         * 
         * The shell and the file system may have different requirements. It is possible to create a path with the API that the shell UI cannot handle.
         * 
         * 
         * Relative Paths
         * 
         * 
         * For functions that manipulate files, the file names may be relative to the current directory. A file name is relative to the current directory if it does not begin with a disk designator or directory name separator, such as a backslash (\). If the file name begins with a disk designator, it is a full path.
         * 
         * 
         * Short and Long File Names
         * 
         * 
         * Windows normally stores the long file names on disk as special directory entries (this can be disabled for performance reasons). When you create a long file name, Windows also creates the short MS-DOS (8.3) form of the name. On many file systems, a short file name contains a tilde character (~). However, not all file systems follow this convention. Therefore, do not make this assumption.
         * 
         * To get MS-DOS file names, long file names, or the full path of a file you can do the following:
         * 
         * 
         * a.. To get an MS-DOS file name that has a long file name, use the GetShortPathName function.
         * b.. To get the long file name that has a short name, use the GetLongPathName function.
         * c.. To get the full path of a file, use the GetFullPathName function.
         * 
         * Windows stores the long file names on disk in Unicode. This means that the original long file name is always preserved, even if it contains extended characters, and regardless of the code page that is active during a disk read or write operation. The case of the file name is preserved, but the file system is not case-sensitive.
         * 
         * The valid character set for long file names is the NTFS file system character set less one character, which is the colon (':') that the NTFS file system uses to open alternate file streams. This means that you can copy files between the NTFS file system and FAT file system partitions without losing any file name information.
         */
        result.add("CON");
        result.add("PRN");
        result.add("AUX");
        result.add("NUL");
        for(int i = 0; i < 10; ++i)
            result.add("COM" + i);
        for(int i = 0; i < 10; ++i)
            result.add("LPT" + i);
        result.add("CLOCK$");

        return result;
    }
}
