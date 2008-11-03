/*-
 * Copyright (C) 2007 Erik Larsson
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
import java.awt.BorderLayout;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.catacombae.hfsexplorer.io.JTextAreaOutputStream;

public class DebugConsoleWindow extends JFrame {
    private static final int WINDOW_NUMBER_OF_COLUMNS = 80;
    private static final int WINDOW_NUMBER_OF_LINES = 25;
    
    private final JScrollPane debugAreaScroller;
    private final JTextArea debugArea;
    
    private final Object syncObject = new Object();
    private final OutputStream debugStream;
        
    public DebugConsoleWindow(PrintStream stdErr) {
        super("Debug Console");
        setLayout(new BorderLayout());
        this.debugArea = new JTextArea(WINDOW_NUMBER_OF_LINES, WINDOW_NUMBER_OF_COLUMNS);
        this.debugAreaScroller = new JScrollPane(debugArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.debugArea.setLineWrap(true);
        this.debugArea.setEditable(false);

        add(debugAreaScroller, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        this.debugStream = new JTextAreaOutputStream(stdErr, debugArea, debugAreaScroller, syncObject, null);
    }

    /**
     * Returns an OutputStream which sends its output to the DebugConsoleWindow's JTextArea.
     * @return an OutputStream which sends its output to the DebugConsoleWindow's JTextArea.
     */
    public OutputStream getDebugStream() {
        return debugStream;
    }
}
