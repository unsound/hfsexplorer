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

import java.awt.Component;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Utility functions that the GUI designer can use to ease his/her existential suffering.
 * 
 * @author Erik Larsson
 */
public class GUIUtil {
    /**
     * Displays an exception dialog with a stack trace. Convenience method that omits a user defined
     * message and sets the default window title to "Exception" and the message type to
     * JOptionPane.ERROR_MESSAGE. Maximum lines shown in the stack trace is set to 10.
     * 
     * @see #displayExceptionDialog(Throwable, int, Component, String, String, int)
     * @param t the exception thrown.
     * @param c the dialog's parent component.
     */
    public static void displayExceptionDialog(Throwable t, Component c) {
        displayExceptionDialog(t, 10, c, "", "Exception", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Displays an exception dialog with a stack trace. Convenience method that omits a user defined
     * message and sets the default window title to "Exception" and the message type to
     * JOptionPane.ERROR_MESSAGE.
     * 
     * @see #displayExceptionDialog(Throwable, int, Component, String, String, int)
     * @param t the exception thrown.
     * @param maxStackTraceLines the maximum number of lines of the stack trace to display.
     * @param c the dialog's parent component.
     */
    public static void displayExceptionDialog(Throwable t, int maxStackTraceLines, Component c) {
        displayExceptionDialog(t, maxStackTraceLines, c, "", "Exception", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Displays an exception dialog with a stack trace. Convenience method that sets the default
     * window title to "Exception" and the message type to JOptionPane.ERROR_MESSAGE.
     * 
     * @see #displayExceptionDialog(Throwable, int, Component, String, String, int)
     * @param t the exception thrown.
     * @param maxStackTraceLines the maximum number of lines of the stack trace to display.
     * @param c the dialog's parent component.
     * @param message the message to be printed above the exception.
     */
    public static void displayExceptionDialog(Throwable t, int maxStackTraceLines, Component c,
            String message) {
        displayExceptionDialog(t, maxStackTraceLines, c, message, "Exception", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Displays an exception dialog with a stack trace.
     * 
     * @param t the exception thrown.
     * @param maxStackTraceLines the maximum number of lines of the stack trace to display.
     * @param c the dialog's parent component.
     * @param message the message to be printed above the exception.
     * @param title the dialog's title.
     * @param messageType the dialog's message type (JOptionPane.ERROR_MESSAGE,
     * JOptionPane.INFORMATION_MESSAGE, etc.).
     */
    public static void displayExceptionDialog(final Throwable t, final int maxStackTraceLines,
            final Component c, final String message, final String title, final int messageType) {
        StringBuilder sb = new StringBuilder();
        if(message.length() > 0) {
            sb.append(message);
            sb.append("\n\n");
        }
        
        Util.buildStackTrace(t, maxStackTraceLines, sb);
        
        final String finalMessage = sb.toString();
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(c, finalMessage, title, messageType);
                }
            });
        } catch(Exception e) {
            throw new RuntimeException("Exception during invokeAndWait!", e);
        }
        
    }
}
