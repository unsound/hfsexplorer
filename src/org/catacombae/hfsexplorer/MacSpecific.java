/*-
 * Copyright (C) 2006 Erik Larsson
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

import com.apple.eawt.*;

public class MacSpecific {
    public static interface QuitHandler {
	public boolean acceptQuit();
    }
    public static void registerQuitHandler(final QuitHandler qh /*Runnable r*/) {
	Application thisApplication = Application.getApplication();
	thisApplication.addApplicationListener(new ApplicationAdapter() {
		public void handleQuit(ApplicationEvent ae) {
		    if(qh.acceptQuit())
			ae.setHandled(true);
		    else
			ae.setHandled(false);
		    //SwingUtilities.invokeLater(r);
		}
	    });
    }
}
