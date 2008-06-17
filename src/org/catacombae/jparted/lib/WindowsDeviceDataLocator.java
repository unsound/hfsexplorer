/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.jparted.lib;

import org.catacombae.hfsexplorer.io.ConcatenatedFile;
import org.catacombae.hfsexplorer.io.LowLevelFile;
import org.catacombae.hfsexplorer.io.WritableConcatenatedFile;
import org.catacombae.hfsexplorer.io.WritableLowLevelFile;
import org.catacombae.hfsexplorer.win32.WindowsLowLevelIO;
import org.catacombae.hfsexplorer.win32.WritableWin32File;

/**
 *
 * @author erik
 */
public class WindowsDeviceDataLocator extends DataLocator {
    private final String devicePath;
    private final Long pos, len;
    
    public WindowsDeviceDataLocator(String pDevicePath) {
        this.devicePath = pDevicePath;
        this.pos = null;
        this.len = null;
    }
    
    public WindowsDeviceDataLocator(String pDevicePath, long pPos, long pLen) {
        this.devicePath = pDevicePath;
        this.pos = pPos;
        this.len = pLen;
    }
    
    @Override
    public LowLevelFile createReadOnlyFile() {
        LowLevelFile llf = new WindowsLowLevelIO(devicePath);
        if(pos != null && len != null)
            return new ConcatenatedFile(llf, pos, len);
        else
            return llf;
    }

    @Override
    public WritableLowLevelFile createReadWriteFile() {
        WritableLowLevelFile wllf = new WritableWin32File(devicePath);
        if(pos != null && len != null)
            return new WritableConcatenatedFile(wllf, pos, len);
        else
            return wllf;
    }

}
