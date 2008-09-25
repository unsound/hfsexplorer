/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.csjc;

/**
 * Represents a dynamic struct, i.e. a struct with a size that varies between
 * instances.
 * @author erik
 */
public interface DynamicStruct {
    public int maxSize();
    public int occupiedSize();
}
