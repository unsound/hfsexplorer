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
public class FileSystemRecognizer {
    public FileSystemMajorType detectFileSystem(DataLocator fsLocator) {
        // File system recognizer code should be modular.
        return FileSystemMajorType.UNKNOWN;
    }
}
