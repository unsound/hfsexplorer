/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.fsframework;

import java.util.List;

/**
 *
 * @author Erik
 */
public interface FileSystem {
    public FSEntry getEntry(String... path) throws FSFileNotFoundException;
    public FSFile getFile(String... path) throws FSFileNotFoundException;
    public FSFolder getFolder(String... path) throws FSFileNotFoundException;
    
    public List<FSEntry> listFolder(FSFolder folder);
    public List<FSEntry> listFolder(String... path) throws FSFileNotFoundException;
}
