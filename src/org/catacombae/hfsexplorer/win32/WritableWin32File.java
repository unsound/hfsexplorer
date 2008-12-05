/*-
 * Copyright (C) 2007 Erik Larsson
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
import org.catacombae.io.RandomAccessStream;
import org.catacombae.hfsexplorer.*;

/** BUG: Writing at the end of a file will always expand it to fit the sector size! Not very good. Fixit.
    Note: Is this fixed? TODO ?! */

public class WritableWin32File extends WindowsLowLevelIO implements RandomAccessStream {
    private byte[] sectorBuffer;
    public WritableWin32File(String filename) {
        super(filename);
        sectorBuffer = new byte[sectorSize];
    }
    
    public void write(byte[] b) { write(b, 0, b.length); }
    public void write(byte[] b, int pos, int len) {
	/* NOTE: The reason for all this code is that we have to align write operations to sectors on the disk. */
	if(fileHandle != null) {
	    final long fileLength = length(fileHandle); // We only record the length of the file at the beginning
	    // Will we allow writing beyond this file (extending it)? We currently do (but maybe windows prohibits it)
	    
	    System.out.println("write(b, " + pos + ", " + len + ");");
	    long startFP = filePointer;
	    long endFP = filePointer+len;
	    long startSectorFP = (startFP/sectorSize)*sectorSize;
	    int startSectorFPOffset = (int)(startFP-startSectorFP);
	    long endSectorFP = (endFP/sectorSize)*sectorSize;
	    int endSectorFPLength = (int)(endFP-endSectorFP);
	    
	    int inputPos = pos;
	    
	    /* First make sure that we are at the beginning of the sector containing
	       filePointer. */
	    seek(startSectorFP, fileHandle);
	    
	    /* Read the contents of the start sector. */
	    int bytesToRead = (int)((sectorBuffer.length > fileLength) ? fileLength : sectorBuffer.length);
	    if(read(sectorBuffer, 0, bytesToRead, fileHandle) != bytesToRead)
		throw new RuntimeException("Could not read contents of starting sector!");
	    
	    System.out.println("Writing first bytes.");
	    
	    /* Replace the contents starting at filePointer, seek back to start pos and write to disk. */
	    seek(startSectorFP, fileHandle);
	    int remainingSpaceInBuffer = sectorBuffer.length-startSectorFPOffset;
	    int bytesToReplace = (len < remainingSpaceInBuffer) ? len : remainingSpaceInBuffer;
	    System.arraycopy(b, inputPos, sectorBuffer, startSectorFPOffset, bytesToReplace);
	    inputPos += bytesToReplace;
	    write(sectorBuffer, 0, startSectorFPOffset+bytesToReplace, fileHandle);


	    /* Burst all non-complicated sectors... */
	    long currentSectorFP = startSectorFP+sectorSize;
	    while(currentSectorFP < endSectorFP) {
		System.out.println("Writing non-complicated sector " + ((currentSectorFP-startSectorFP)/sectorSize) + "...");
		System.arraycopy(b, inputPos, sectorBuffer, 0, sectorBuffer.length);
		inputPos += sectorBuffer.length;
		write(sectorBuffer, 0, sectorBuffer.length, fileHandle);
		
		currentSectorFP += sectorSize;
	    }


	    /* If start and end sector are the same, the work has already been done (before the loop). */
	    if(startSectorFP != endSectorFP && endSectorFPLength > 0) {
		int finalWriteLength = sectorSize;
		
		if(fileLength < endSectorFP+sectorSize)
		    finalWriteLength = endSectorFPLength; // The file isn't sector aligned, so we'll just write the final bytes.
		
		/* Read the contents of the end sector. */
		if(read(sectorBuffer, 0, finalWriteLength, fileHandle) != finalWriteLength)
		    throw new RuntimeException("Could not read contents of end sector!");
		
		System.arraycopy(b, inputPos, sectorBuffer, 0, endSectorFPLength);
		
		seek(endSectorFP, fileHandle);
		write(sectorBuffer, 0, finalWriteLength, fileHandle);
	    }
	    
	    /* That should be all. Restore state and update variables. */
	    seek(endFP); // Since we have gone beyond it when writing the last sector
	}
	else
	    throw new RuntimeException("File closed!");
	
    }
    public void write(int b) { write(new byte[] { (byte)(b & 0xFF) }); }
    
    /** We override the open method in order to get the file opened in write mode */
    protected byte[] open(String filename) {
	//System.out.println("Java: WritableWin32File.open(" + filename + ");");
	return openNative(filename);
    }
    protected static native byte[] openNative(String filename);
    /** Will throw exception whenever all bytes can't be written. */
    protected static native void write(byte[] data, int off, int len, byte[] handle);
    
    /*--------------------- TEST CODE FOLLOWS ---------------------*/
    private static java.io.BufferedReader stdin = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
    private static WritableWin32File wwf;
    private static java.io.RandomAccessFile ref;
    private static long currentSeekPos;
    private static int currentBufferSize;
    public static void main(String[] args) {
	if(args.length != 2) {
	    System.out.println("The program takes two different files with identical contents as arguments.");
	    return;
	}
	try {
	    /* Bashing test. Run this on a test file for some time. If the test file doesn't get corrupted,
	       we probably have a winner. */
	    wwf = new WritableWin32File(args[0]);
	    ref = new java.io.RandomAccessFile(args[1], "rw");
	    java.util.Random rnd = new java.util.Random();
	    if(wwf.length() != ref.length()) {
		System.out.println("The two files must be equal in length! (" + wwf.length() + " != " + ref.length() + ")");
		return;
	    }

	    java.io.BufferedReader stdin = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
	    int maxBufferSize = 100000;
	    
	    int constantBufferSize = 3912;
	    byte[] constantBufferLeft = new byte[constantBufferSize];
	    byte[] constantBufferRight = new byte[constantBufferSize];
	    while(true) {
		wwf.seek(0);
		
		/* For each iteration: randomize the position and read length. Read and write back. */
		long currentSeekPos = (long)((wwf.length()-1)*rnd.nextDouble());
		int currentBufferSize = rnd.nextInt(maxBufferSize);
		if(currentSeekPos+currentBufferSize > wwf.length())
		    currentBufferSize = (int)(wwf.length()-currentSeekPos);
		
		/*    - Seek to random position
		 *    - Read random amount of data, check if both equal
		 *    - Read constant amount of data, check if both equal
		 *    - Seek back to 0
		 *    - Seek to same position
		 *    - Write data back
		 *    - Read data again, check if equal
		 */
		byte[] currentBufferLeft = new byte[currentBufferSize];
		byte[] currentBufferRight = new byte[currentBufferSize];
		wwf.seek(currentSeekPos);
		ref.seek(currentSeekPos);
		
		wwf.readFully(currentBufferLeft);
		ref.readFully(currentBufferRight);
		testEquality(currentBufferLeft, currentBufferRight, "(1) Data not equal after reads!");
		
		if(currentSeekPos+currentBufferSize+constantBufferSize <= wwf.length()) {
		    wwf.readFully(constantBufferLeft);
		    ref.readFully(constantBufferRight);
		    testEquality(currentBufferLeft, currentBufferRight, "(2) Data following reads not equal!");
		}
		
		wwf.seek(0);
		ref.seek(0);
		
		wwf.seek(currentSeekPos);
		ref.seek(currentSeekPos);
		
		wwf.write(currentBufferLeft);
		ref.write(currentBufferRight);
		
		wwf.seek(currentSeekPos);
		ref.seek(currentSeekPos);
		
		wwf.readFully(currentBufferLeft);
		ref.readFully(currentBufferRight);
		
		testEquality(currentBufferLeft, currentBufferRight, "(3) Data not equal after writes!");
	    }
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }
    
    private static void testEquality(byte[] a, byte[] b, String idMessage) throws Exception {
	if(!Util.arraysEqual(a, b)) {
	    System.out.println(idMessage);
	    System.out.println("  currentSeekPos=" + currentSeekPos);
	    System.out.println("  currentBufferSize=" + currentBufferSize);
	    System.out.println("  wwf.length()=" + wwf.length());
	    System.out.println("  ref.length()=" + ref.length());
	    System.out.println("  wwf.getFilePointer()=" + wwf.getFilePointer());
	    System.out.println("  ref.getFilePointer()=" + ref.getFilePointer());
// 		    System.out.println("  
// 		    System.out.println("  
// 		    System.out.println("  
	    System.out.print("Press enter to continue: ");
	    stdin.readLine();
	}
    }
}
