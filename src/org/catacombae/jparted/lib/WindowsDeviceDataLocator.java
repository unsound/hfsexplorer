/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.jparted.lib;

import org.catacombae.io.ReadableConcatenatedStream;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.io.ConcatenatedStream;
import org.catacombae.io.RandomAccessStream;
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
    public ReadableRandomAccessStream createReadOnlyFile() {
        ReadableRandomAccessStream llf = new WindowsLowLevelIO(devicePath);
        if(pos != null && len != null)
            return new ReadableConcatenatedStream(llf, pos, len);
        else
            return llf;
    }

    @Override
    public RandomAccessStream createReadWriteFile() {
        RandomAccessStream wllf = new WritableWin32File(devicePath);
        if(pos != null && len != null)
            return new ConcatenatedStream(wllf, pos, len);
        else
            return wllf;
    }

    @Override
    public boolean isWritable() {
        return true;
    }
}
