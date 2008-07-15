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
 * Containins the main method for when the application is started
 * through a JAR manifest.
 */
public class JarMain {
    private static final String CONSOLE_ARG = "-dbgconsole";
    
    /**
     * This special main method for starting the application makes sure
     * the argument "-dbgconsole" is present in args, and adds it if
     * absent, to force the debug console to appear. Then it invokes
     * the normal application main method with the possibly modified
     * argument array.
     */
    public static void main(String[] args) {
	String[] fsbArgs;
	if(args.length <= 0 || !args[0].equals(CONSOLE_ARG)) {
	    fsbArgs = new String[args.length+1];
	    fsbArgs[0] = CONSOLE_ARG;
	    for(int i = 0; i < args.length; ++i)
		fsbArgs[i+1] = args[i];
	}
	else
	    fsbArgs = args;
	
	FileSystemBrowserWindow.main(fsbArgs);
    }
}