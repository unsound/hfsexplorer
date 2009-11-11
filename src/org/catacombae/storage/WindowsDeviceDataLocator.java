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
