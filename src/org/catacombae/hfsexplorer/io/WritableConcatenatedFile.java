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

package org.catacombae.hfsexplorer.io;

/**
 *
 * @author Erik Larsson, erik82@kth.se
 */

public class WritableConcatenatedFile implements WritableLowLevelFile {
    private ConcatenatedFile backingFile;
    
    public WritableConcatenatedFile(WritableLowLevelFile firstPart, long startOffset, long length) {
	backingFile = new ConcatenatedFile(firstPart, startOffset, length);
    }
    
    public void addPart(WritableLowLevelFile newFile, long off, long len) {
        ConcatenatedFile.Part newPart = new ConcatenatedFile.Part(newFile, off, len);
        backingFile.parts.add(newPart);
    }
    
    public void seek(long pos) {
        backingFile.seek(pos);
    }
    
    public int read() {
        return backingFile.read();
    }

    public int read(byte[] data) {
        return backingFile.read(data);
    }

    public int read(byte[] data, int pos, int len) {
        return backingFile.read(data, pos, len);
    }

    public void readFully(byte[] data) {
        backingFile.readFully(data);
    }

    public void readFully(byte[] data, int offset, int length) {
        backingFile.readFully(data, offset, length);
    }
    
    public void write(int b) {
        write(new byte[] { (byte)b }, 0, 1);
    }
    
    public void write(byte[] b) {
        write(b, 0, b.length);
    }

    public void write(byte[] data, int off, int len) {
        int bytesWritten = 0;
        WritableLowLevelFile currentWritableFile = (WritableLowLevelFile)backingFile.currentPart.file;
        
	while(true) {
	    long bytesLeftInFile = backingFile.currentPart.length - 
                    (currentWritableFile.getFilePointer() - backingFile.currentPart.startOffset);
	    int bytesLeftToWrite = len - bytesWritten;
	    int bytesToWrite = 
                    (int)((bytesLeftInFile < bytesLeftToWrite) ? bytesLeftInFile : bytesLeftToWrite);
            currentWritableFile.write(data, off+bytesWritten, bytesToWrite);
            bytesWritten += bytesToWrite;
            if(bytesWritten < len) {
                // move pointer forward, so that currentPart advances.
                backingFile.currentPartIndex++;
                backingFile.currentPart = backingFile.parts.get(backingFile.currentPartIndex);
                currentWritableFile = (WritableLowLevelFile)backingFile.currentPart.file;
                currentWritableFile.seek(backingFile.currentPart.startOffset);
            }
            else if(bytesWritten == len)
                return;
            else
                throw new RuntimeException("Wrote more than I was supposed to! This can't happen.");
        }
    }

    public long length() {
        return backingFile.length();
    }

    public long getFilePointer() {
        return backingFile.getFilePointer();
    }

    public void close() {
        backingFile.close();
    }

}

