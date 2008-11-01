/*-
 * Copyright (C) 2006-2008 Erik Larsson
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

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;


/**
 * This class implements all the calls in the application that are purely Mac-specific. A static
 * initialization of this class on other platforms than Mac OS X would probably lead to a
 * ClassLoader exception.
 * 
 * @author <a href="http://hem.bredband.net/catacombae">Erik Larsson</a>
 */
public class MacSpecific {
    /**
     * Interface for a handler for Mac OS X application events.
     */
    public static interface MacApplicationHandler {

        /**
         * Returns whether or not this application accepts to be terminated.
         * @return whether or not this application accepts to be terminated.
         */
        public boolean acceptQuit();

        /**
         * 
         */
        public void showAboutDialog();
    }
    
    /**
     * Registers a QuitHandler with the Mac OS X API:s.
     * 
     * @param qh the QuitHandler to register.
     */
    public static void registerMacApplicationHandler(final MacApplicationHandler qh) {
        Application thisApplication = Application.getApplication();
        thisApplication.addApplicationListener(new ApplicationAdapter() {
            @Override
            public void handleQuit(ApplicationEvent ae) {
                if(qh.acceptQuit())
                    ae.setHandled(true);
                else
                    ae.setHandled(false);
            }

            @Override
            public void handleAbout(ApplicationEvent event) {
                qh.showAboutDialog();
                event.setHandled(true);
            }
        });
    }
}
