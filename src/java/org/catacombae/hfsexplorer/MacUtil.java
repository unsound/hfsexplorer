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
 * @author <a href="https://catacombae.org" target="_top">Erik Larsson</a>
 */
public class MacUtil {
    /**
     * Check whether the running VM's underlying operating operating system is
     * Mac OS X.
     *
     * @return <code>true</code> if the operating system is Mac OS X,
     * <code>false</code> otherwise.
     */
    public static boolean isMacOSX() {
        return System.getProperty("os.name").toLowerCase().
                startsWith("mac os x");
    }

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
            try {
                registerMacApplicationHandlerInternal(qh);
            } catch(ClassNotFoundException e) {
                /* Newer Java versions have removed the com.apple.eawt APIs and
                 * replaced them with java.awt.Desktop equivalents. So if
                 * com.apple.eawt.Application registration fails due to missing
                 * class files we try the java.awt.Desktop method. */
                registerJavaAwtDesktopHandlersInternal(qh);
            }
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

    private static void registerJavaAwtDesktopHandlersInternal(
            final MacApplicationHandler qh)
            throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, IllegalAccessException
    {
        Class<?> desktopClass = Class.forName("java.awt.Desktop");
        Class<?> quitHandlerClass =
                Class.forName("java.awt.desktop.QuitHandler");
        Class<?> quitResponseClass =
                Class.forName("java.awt.desktop.QuitResponse");
        Class<?> aboutHandlerClass =
                Class.forName("java.awt.desktop.AboutHandler");

        Method desktopGetDesktopMethod =
                desktopClass.getMethod("getDesktop");
        Object desktopObject =
                desktopGetDesktopMethod.invoke(null);

        final Method quitResponsePerformQuitMethod =
                quitResponseClass.getMethod("performQuit");
        final Method quitResponseCancelQuitMethod =
                quitResponseClass.getMethod("cancelQuit");

        InvocationHandler quitInvocationHandler = new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args)
                    throws Throwable
            {
                if(method.getName().equals("handleQuitRequestWith")) {
                    /* void handleQuitRequestWith(AppEvent.QuitEvent e,
                     *     QuitResponse response) */
                    Object e = args[0];
                    Object response = args[1];

                    if(qh.acceptQuit()) {
                        quitResponsePerformQuitMethod.invoke(response);
                    }
                    else {
                        quitResponseCancelQuitMethod.invoke(response);
                    }

                    return null;

                }

                throw new NoSuchMethodException("No " +
                        "\"" + method.getName() + "\" defined.");
            }
        };

        Object quitHandlerObject =
                Proxy.newProxyInstance(
                quitHandlerClass.getClassLoader(),
                new Class[] { quitHandlerClass },
                quitInvocationHandler);

        Method desktopSetQuitHandlerMethod =
                desktopClass.getMethod("setQuitHandler",
                quitHandlerClass);

        desktopSetQuitHandlerMethod.invoke(desktopObject,
                quitHandlerObject);

        InvocationHandler aboutInvocationHandler = new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args)
                    throws Throwable
            {
                if(method.getName().equals("handleAbout")) {
                    /* void handleAbout(AppEvent.AboutEvent e) */
                    Object e = args[0];

                    qh.showAboutDialog();

                    return null;
                }

                throw new NoSuchMethodException("No " +
                        "\"" + method.getName() + "\" defined.");
            }
        };

        Object aboutHandlerObject =
                Proxy.newProxyInstance(
                aboutHandlerClass.getClassLoader(),
                new Class[] { aboutHandlerClass },
                aboutInvocationHandler);

        Method desktopSetAboutHandlerMethod =
                desktopClass.getMethod("setAboutHandler",
                aboutHandlerClass);

        desktopSetAboutHandlerMethod.invoke(desktopObject,
                aboutHandlerObject);
    }
}
