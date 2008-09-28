/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.csjc;

import java.io.PrintStream;

/**
 *
 * @author erik
 */
public interface PrintableStruct {
    public void print(PrintStream ps, String prefix);
    public void printFields(PrintStream ps, String prefix);
}
