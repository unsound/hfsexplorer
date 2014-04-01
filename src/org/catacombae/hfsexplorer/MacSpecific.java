/*-
 * Copyright (C) 2006-2014 Erik Larsson
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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


/**
 * This class implements all the calls in the application that are purely
 * Mac OS X-specific. Reflection is used for all the Mac OS X-specific calls, so
 * there is no danger during static initialization of the class. However all
 * methods will throw exceptions if invoked on other platforms than Mac OS X.
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
    public static void registerMacApplicationHandler(
            final MacApplicationHandler qh)
    {
        try {
            registerMacApplicationHandlerInternal(qh);
        } catch(Exception e) {
            if(e instanceof InvocationTargetException) {
                Throwable cause = e.getCause();

                if(cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                }
            }

            throw new RuntimeException(e);
        }
    }

    private static void registerMacApplicationHandlerInternal(
            final MacApplicationHandler qh)
            throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, IllegalAccessException
    {
        Class<?> applicationClass = Class.forName("com.apple.eawt.Application");
        Class<?> applicationListenerClass =
                Class.forName("com.apple.eawt.ApplicationListener");
        Class<?> applicationEventClass =
                Class.forName("com.apple.eawt.ApplicationEvent");

        Method applicationGetApplicationMethod =
                applicationClass.getMethod("getApplication");
        Object applicationObject =
                applicationGetApplicationMethod.invoke(null);

        final Method applicationEventSetHandledMethod =
                applicationEventClass.getMethod("setHandled", boolean.class);

        InvocationHandler invocationHandler = new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args)
                    throws Throwable
            {
                if(method.getName().equals("handleQuit")) {
                    Object event = args[0];

                    if(qh.acceptQuit()) {
                        applicationEventSetHandledMethod.invoke(event, true);
                    }
                    else {
                        applicationEventSetHandledMethod.invoke(event, false);
                    }

                    return null;
                }
                else if(method.getName().equals("handleAbout")) {
                    Object event = args[0];

                    qh.showAboutDialog();
                    applicationEventSetHandledMethod.invoke(event, true);

                    return null;
                }
                else if(method.getName().equals("handleOpenApplication") ||
                        method.getName().equals("handleOpenFile") ||
                        method.getName().equals("handlePreferences") ||
                        method.getName().equals("handlePrintFile") ||
                        method.getName().equals("handleReOpenApplication"))
                {
                    return null;
                }

                throw new NoSuchMethodException("No " +
                        "\"" + method.getName() + "\" defined.");
            }
        };

        Object applicationAdapterObject =
                Proxy.newProxyInstance(
                applicationListenerClass.getClassLoader(),
                new Class[] { applicationListenerClass },
                invocationHandler);

        Method applicationAddApplicationListenerMethod =
                applicationClass.getMethod("addApplicationListener",
                applicationListenerClass);

        applicationAddApplicationListenerMethod.invoke(applicationObject,
                applicationAdapterObject);
    }
}
