/*-
 * Copyright (C) 2008 Erik Larsson
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catacombae.csjc;

import java.io.PrintStream;

/**
 * Represents a struct which is printable, i.e. a view of its contents can be printed to a
 * java.io.PrintStream.<br>
 * The printed contents will be space-indented for readability.
 * 
 * @author <a href="http://hem.bredband.net/catacombae">Erik Larsson</a>
 */
public interface PrintableStruct {
    /**
     * Prints the name of the struct along with the fields of the struct to <code>ps</code>
     * prepending <code>prefix</code> to each line.
     * 
     * @param ps the stream to print the contents to.
     * @param prefix the string prefix to prepend to each line (useful for indenting).
     */
    public void print(PrintStream ps, String prefix);
    
    /**
     * Prints <b>only the fields of the struct</b> to <code>ps</code> prepending
     * <code>prefix</code> to each line.
     * 
     * @param ps the stream to print the contents to.
     * @param prefix the string prefix to prepend to each line (useful for indenting).
     */
    public void printFields(PrintStream ps, String prefix);
}
