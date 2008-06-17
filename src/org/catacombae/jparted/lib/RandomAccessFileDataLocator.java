/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.jparted.lib;

import java.io.File;
import org.catacombae.hfsexplorer.io.ConcatenatedFile;
import org.catacombae.hfsexplorer.io.LowLevelFile;
import org.catacombae.hfsexplorer.io.RandomAccessLLF;
import org.catacombae.hfsexplorer.io.WritableConcatenatedFile;
import org.catacombae.hfsexplorer.io.WritableLowLevelFile;
import org.catacombae.hfsexplorer.io.WritableRandomAccessLLF;

/**
 *
 * @author erik
 */
public class RandomAccessFileDataLocator extends DataLocator {
    private final File file;
    private final Long pos, len;
    
    public RandomAccessFileDataLocator(String pPath) {
        this(new File(pPath));
    }
    
    public RandomAccessFileDataLocator(String pPath, long pPos, long pLen) {
        this(new File(pPath), pPos, pLen);
    }
    public RandomAccessFileDataLocator(File pFile) {
        this(pFile, null, null);
    }
    
    public RandomAccessFileDataLocator(File pFile, long pPos, long pLen) {
        this(pFile, new Long(pPos), new Long(pLen));
    }
    
    private RandomAccessFileDataLocator(File pFile, Long pPos, Long pLen) {
        if(!pFile.canRead())
            throw new RuntimeException("Can not read from file!");

        this.file = pFile;
        this.pos = pPos;
        this.len = pLen;
    }
    
    @Override
    public LowLevelFile createReadOnlyFile() {
        LowLevelFile llf = new RandomAccessLLF(file);
        if(pos != null && len != null)
            return new ConcatenatedFile(llf, pos, len);
        else
            return llf;
    }

    @Override
    public WritableLowLevelFile createReadWriteFile() {
        WritableLowLevelFile wllf = new WritableRandomAccessLLF(file);
        if(pos != null && len != null)
            return new WritableConcatenatedFile(wllf, pos, len);
        else
            return wllf;
    }
}
