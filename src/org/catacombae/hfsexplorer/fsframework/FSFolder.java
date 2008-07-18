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
public class FSFolder extends FSEntry {
    public List<FSEntry> list() {
        FileSystem fs = getParentFileSystem();
        return fs.listFolder(this);
    }
}
