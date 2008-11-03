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

package org.catacombae.hfsexplorer;

import org.catacombae.hfsexplorer.fs.NullProgressMonitor;

public abstract class BasicExtractProgressMonitor extends NullProgressMonitor implements ExtractProgressMonitor {
    
    protected BasicExtractProgressMonitor() {}
    
    @Override
    public void updateTotalProgress(double fraction, String message) {}

    @Override
    public void updateCurrentDir(String dirname) {}

    @Override
    public void updateCurrentFile(String filename, long fileSize) {}

    @Override
    public void setDataSize(long totalSize) {}

    @Override
    public void updateCalculateDir(String dirname) {}
}
