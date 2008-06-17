/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.jparted.lib.fs;

import org.catacombae.jparted.lib.DataLocator;

/**
 *
 * @author erik
 */
public class HFSXFileSystemHandlerFactory extends FileSystemHandlerFactory {
    public FileSystemHandler createHandler(DataLocator data) {
        return new HFSXFileSystemHandler(data);
    }
    
    public FileSystemHandlerInfo getHandlerInfo() {
        return null;
    }
}
