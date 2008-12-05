/*-
 * Copyright (C) 2006 Erik Larsson
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

package org.catacombae.hfsexplorer.win32;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.catacombae.hfsexplorer.Util;
import org.catacombae.io.ReadableRandomAccessStream;

public class WindowsLowLevelIO implements ReadableRandomAccessStream {
    protected byte[] fileHandle;
    protected final int sectorSize; //Detect this later..
    protected long filePointer = 0;
    private static final Object loadLibSync = new Object();
    private static boolean libraryLoaded = false;
    
    /**
     * Set this variable to true if you want some messages printed to stderr when the library is
     * loaded.
     */
    public static boolean verboseLoadLibrary = false;
    
    private enum ArchitectureIdentifier {

        I386("i386"), AMD64("amd64"), IA64("ia64"),
        POWERPC("ppc32"), POWERPC64("ppc64"),
        SPARC("sparc32"), SPARC64("sparc64"),
        MIPS("mips32"), MIPS64("mips64"), ALPHA("alpha"),
        UNKNOWN;
        
        private final String idString;

        private ArchitectureIdentifier() {
            this.idString = null;
        }

        private ArchitectureIdentifier(String idString) {
            this.idString = idString;
        }

        public String getArchitectureString() {
            return idString;
        }
    };
    
    private static ArchitectureIdentifier getJVMArchitecture() {
        // Trying to cover all thinkable cases here...
        // Got some hints from http://lopica.sourceforge.net/os.html
        final String osArch = System.getProperty("os.arch");
        if(osArch.equalsIgnoreCase("x86") ||
                osArch.equalsIgnoreCase("i386") ||
                osArch.equalsIgnoreCase("i486") ||
                osArch.equalsIgnoreCase("i586") ||
                osArch.equalsIgnoreCase("i686"))
            return ArchitectureIdentifier.I386;
        else if(osArch.equalsIgnoreCase("amd64") ||
                osArch.equalsIgnoreCase("x86_64") ||
                osArch.equalsIgnoreCase("x64"))
            return ArchitectureIdentifier.AMD64;
        else if(osArch.equalsIgnoreCase("ia64") ||
                osArch.equalsIgnoreCase("ia64n"))
            return ArchitectureIdentifier.IA64;
        else
            return ArchitectureIdentifier.UNKNOWN;
    }

    public static boolean isSystemSupported() {
        ArchitectureIdentifier archId = getJVMArchitecture();
        return System.getProperty("os.name").toLowerCase().startsWith("windows") &&
                (archId == ArchitectureIdentifier.I386 ||
                archId == ArchitectureIdentifier.AMD64 ||
                archId == ArchitectureIdentifier.IA64);
    }
    
    /**
     * Does not check if the system is supported, and just tries to load the approprate library
     * from the architecture string specified in this system's ArchitectureIdentifier.
     */
    private static void loadLibrary() {
        final ArchitectureIdentifier archId = getJVMArchitecture();
        if(archId == ArchitectureIdentifier.UNKNOWN) {
            System.err.println(System.getProperty("os.arch") + ": architecture unknown! Cannot locate appropriate native I/O library.");
            throw new RuntimeException("loadLibrary(): CPU architecture unknown!");
        }
        else {
            final String libName = "llio_" + archId.getArchitectureString();
            try {
                if(verboseLoadLibrary) System.err.println("Trying to load native library \"" + libName + "\"...");
                System.loadLibrary(libName);
                if(verboseLoadLibrary) System.err.println("Native library \"" + libName + "\" successfully loaded.");
                libraryLoaded = true;
            } catch(UnsatisfiedLinkError e) {
                System.err.println("ERROR: Native library \"" + libName + "\" failed to load!");
                System.err.println("java.library.path=\"" + System.getProperty("java.library.path") + "\"");
                throw e;
            }
        }
    }
    
    public WindowsLowLevelIO(String filename) {
        synchronized(loadLibSync) {
            if(!libraryLoaded) {
                loadLibrary();
            }
        }
        boolean verbose = false;
        fileHandle = open(filename);
        //System.out.println("fileHandle: 0x" + Util.byteArrayToHexString(fileHandle));
        int tmpSectorSize = getSectorSize(fileHandle);
        if(tmpSectorSize > 0) {
            if(verbose)
                System.out.println("Sector size determined: " + tmpSectorSize);
            sectorSize = tmpSectorSize;
        }
        else {
            if(verbose)
                System.out.println("Could not determine sector size.");
            sectorSize = 512; // The only reasonable standard value
        }
    }
    
    @Override
    public void seek(long pos) {
        //System.err.println("WindowsLowLevelIO.seek(" + pos + ");");

        if(fileHandle != null) {
            // We seek to the beginning of the sector containing pos
            seek((pos / sectorSize) * sectorSize, fileHandle);
            filePointer = pos;
        }
        else
            throw new RuntimeException("File closed!");
    }
    
    @Override
    public int read() {
        //System.err.println("WindowsLowLevelIO.read();");
        byte[] oneByte = new byte[1];
        if(read(oneByte) == 1)
            return oneByte[0] & 0xFF;
        else
            return -1;
    }
    
    @Override
    public int read(byte[] data) {
        //System.err.println("WindowsLowLevelIO.read(byte[" + data.length + "]);");
        return read(data, 0, data.length);
    }

    @Override
    public int read(byte[] data, int pos, int len) {
        //System.err.println("WindowsLowLevelIO.read(byte[" + data.length + "], " + pos + ", " + len + ");");
        if(fileHandle != null) {
            /*
             * First make sure that we are at the beginning of the sector containing
             * filePointer.
             */
            seek((filePointer / sectorSize) * sectorSize, fileHandle);

            /*
             * Calculate how many bytes we have to skip in order to get to the data that
             * filePointer references. (fpDiff)
             */
            long trueFp = getFilePointer(fileHandle);
            long fpDiff = filePointer - trueFp;
            if(fpDiff < 0)
                throw new RuntimeException("Program error: fpDiff < 0 (" + fpDiff + " < 0)");
            else if(fpDiff > sectorSize)
                throw new RuntimeException("Program error: fpDiff > sectorSize (" + fpDiff + " > " + sectorSize + ")");

            /* Add the bytes that we will have to skip to the total read length. */
            int alignedLen = (int) fpDiff + len;

            /*
             * System.err.println("Before crash:");
             * System.err.println("  trueFp=" + trueFp);
             * System.err.println("  fpDiff=" + fpDiff);
             * System.err.println("  alignedLen=" + alignedLen);
             * System.err.println("  sectorSize=" + sectorSize);
             * System.err.println("  alignedLen/sectorSize=" + (alignedLen/sectorSize));
             * System.err.println("  alignedLen%sectorSize=" + (alignedLen%sectorSize));
             * System.err.println("  (alignedLen%sectorSize!=0?1:0))*sectorSize=" + ((alignedLen%sectorSize!=0?1:0))*sectorSize);
             */

            /* Allocate a sufficiently large temp buffer aligned to the sector size. */
            byte[] tmp = new byte[(alignedLen / sectorSize + (alignedLen % sectorSize != 0 ? 1 : 0)) * sectorSize];

            /*
             * Read into the array tmp, which now should be aligned to sector size. Our
             * position in the file should also be aligned to sector size through the
             * initial seek. No problem should occur. I hope.
             */
            int bytesRead = read(tmp, 0, tmp.length, fileHandle);

            /* Trim away the unnecessary leading and trailing data length. */
            bytesRead = (bytesRead >= alignedLen) ? len : bytesRead - (int) fpDiff; // trim bytesRead to len if >= len
            filePointer += bytesRead; // update the (virtual) file pointer
            System.arraycopy(tmp, (int) fpDiff, data, pos, bytesRead);
            return bytesRead;
        }
        else
            throw new RuntimeException("File closed!");
    }

    @Override
    public void readFully(byte[] data) {
        readFully(data, 0, data.length);
    }

    @Override
    public void readFully(byte[] data, int offset, int length) {
        if(fileHandle != null) {
            int bytesRead = 0;
            while(bytesRead < length) {
                int curBytesRead = read(data, offset + bytesRead, length - bytesRead);
                if(curBytesRead > 0)
                    bytesRead += curBytesRead;
                else
                    throw new RuntimeException("Couldn't read the entire length.");
            }
        }
        else
            throw new RuntimeException("File closed!");
    }

    @Override
    public long length() {
        //System.err.println("WindowsLowLevelIO.length();");
        if(fileHandle != null) {
            //throw new RuntimeException("Could not get file size.");
            //System.err.println("  returning " + length(fileHandle));
            return length(fileHandle);
        }
        else
            throw new RuntimeException("File closed!");
    }
	
    @Override
    public long getFilePointer() {
        //System.err.println("WindowsLowLevelIO.getFilePointer();");
        if(fileHandle != null) {
            //System.err.println("  returning " + filePointer);
            return filePointer;//getFilePointer(fileHandle);
        }
        else
            throw new RuntimeException("File closed!");
    }
    
    @Override
    public void close() {
        if(fileHandle != null) {
            close(fileHandle);
            fileHandle = null;
        }
        else
            throw new RuntimeException("File closed!");
    }

    public void ejectMedia() {
        if(fileHandle != null)
            ejectMedia(fileHandle);
        else
            throw new RuntimeException("File closed!");
    }

    public void loadMedia() {
        if(fileHandle != null)
            loadMedia(fileHandle);
        else
            throw new RuntimeException("File closed!");
    }

    protected byte[] open(String filename) {
        //System.out.println("Java: WindowsLowLevelIO.open(" + filename + ");");
        return openNative(filename);
    }
    
    protected static native byte[] openNative(String filename);
    protected static native void seek(long pos, byte[] handle);
    protected static native int read(byte[] data, int pos, int len, byte[] handle);
    protected static native void close(byte[] handle);
    protected static native void ejectMedia(byte[] handle);
    protected static native void loadMedia(byte[] handle);
    protected static native long length(byte[] handle);
    protected static native long getFilePointer(byte[] handle);
    protected static native int getSectorSize(byte[] handle);
//     protected static native void getHandleType(byte[] handle);
//     protected static native void getDeviceLength(byte[] handle);
//     protected static native void getFileLength(byte[] handle);

    public static void main(String[] args) {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        WindowsLowLevelIO wllio1 = new WindowsLowLevelIO(args[0]);


        try {
            if(args[1].equals("testread")) {
                // When reading directly from block devices, the buffer must be a multiple of the sector size of the device. Also, reading must start at a value dividable by the sector size. Calling DeviceIoControl with IOCTL_DISK_GET_DRIVE_GEOMETRY_EX will get the drive geometry for the device.
                System.out.println("Seeking to 1024...");
                wllio1.seek(1024);
                byte[] buf = new byte[4096];
                System.out.println("Reading " + buf.length + " bytes from file: ");
                int bytesRead = wllio1.read(buf);
                System.out.println(" Bytes read: " + bytesRead);
                System.out.println(" As hex:    0x" + Util.byteArrayToHexString(buf));
                System.out.println(" As string: \"" + new String(buf, "US-ASCII") + "\"");
            }
            else if(args[1].equals("eject")) {
                System.out.print("Press any key to eject media...");
                stdin.readLine();
                wllio1.ejectMedia();
                System.out.print("Press any key to load media...");
                stdin.readLine();
                wllio1.loadMedia();
            }
            else
                System.out.println("Nothing to do.");
        } catch(Exception e) {
            e.printStackTrace();
        }
        wllio1.close();
    }
}
