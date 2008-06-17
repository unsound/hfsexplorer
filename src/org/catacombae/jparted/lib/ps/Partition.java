/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.jparted.lib.ps;

import java.io.PrintStream;

/**
 *
 * @author erik
 */
public abstract class Partition {
    /** Returns the start offset in bytes. */
    public abstract long getStartOffset();
    
    /** Returns the length of the partition in bytes. */
    public abstract long getLength();
    
    /** Returns the type of the partition. */
    public abstract PartitionType getType();
    
    /** Prints the values of the fields of this partition. */
    public abstract void printFields(PrintStream ps, String prefix);
    
    /** Prints the type name followed by the values of its fields. */
    public abstract void print(PrintStream ps, String prefix);
}
