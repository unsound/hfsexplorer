/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.jparted.lib.fs;

/**
 *
 * @author Erik
 */
public abstract class FSFolder extends FSEntry {
    public abstract FSEntry[] list();
}
