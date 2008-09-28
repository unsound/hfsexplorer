/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.deprecated;

import java.util.List;

/**
 *
 * @author Erik
 * @deprecated
 */
class FSFolder extends FSEntry {
    public List<FSEntry> list() {
        FileSystem fs = getParentFileSystem();
        return fs.listFolder(this);
    }
}
