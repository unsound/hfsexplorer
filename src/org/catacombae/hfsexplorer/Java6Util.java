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

package org.catacombae.hfsexplorer;

/**
 * Utility class for Java6Specific only used to check the version number of the
 * currently running JRE. Separated from Java6Specific because of class loading
 * issues when invoking its static constructor.
 * 
 * @see Java6Specific
 * @author <a href="http://hem.bredband.net/catacombae">Erik Larsson</a>
 */
public class Java6Util {
    /**
     * Tests whether or not the current VM is a Java 6 or higher VM. This method
     * should always be invoked to check that the version number of the
     * currently running JRE is higher than or equal to 1.6 before invoking any
     * of the methods in Java6Specific.
     * 
     * @return whether or not the current VM is a Java 6 or higher VM.
     */
    public static boolean isJava6OrHigher() {
    	return System.getProperty("java.vm.version").compareTo("1.6") >= 0;
    }
}
