/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.jparted.lib.fs;

/**
 *
 * @author erik
 */
public abstract class FSLink extends FSEntry {

    public FSLink(FileSystemHandler parentFileSystem) {
        super(parentFileSystem);
    }

    /** Returns the target for this link (may be null). */
    public abstract FSEntry getLinkTarget();
}
