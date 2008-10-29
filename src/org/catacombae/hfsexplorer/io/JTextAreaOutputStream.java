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

package org.catacombae.hfsexplorer.io;

import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * An implementation of OutputStream that writes all output to a JTextArea, decoded with the
 * standard platform encoding, or a user supplied one.
 * 
 * @author Erik Larsson
 */
public class JTextAreaOutputStream extends OutputStream {
    private final JTextArea textArea;
    private final JScrollPane textAreaScroller;
    private final Object syncObject;
    private final String encoding;
    private boolean updateRequested = false;
    
    /**
     * Creates a new JTextAreaOutputStream which writes to <code>textArea</code> and synchronizes
     * all writes using <code>synchronized(textArea) { ... }</code> statements.
     * 
     * @param textArea the text area to write to (non-null).
     */
    public JTextAreaOutputStream(JTextArea textArea) {
        this(textArea, textArea);
    }
    
    /**
     * Creates a new JTextAreaOutputStream which writes to <code>textArea</code> and synchronizes
     * all writes using <code>synchronized(syncObject) { ... }</code> statements.
     * 
     * @param textArea the text area to write to (non-null).
     * @param syncObject the object to synchronize on (non-null).
     */
    public JTextAreaOutputStream(JTextArea textArea, Object syncObject) {
        this(textArea, syncObject, null);
    }
    
    /**
     * Creates a new JTextAreaOutputStream which writes to <code>textArea</code> and synchronizes
     * all writes using <code>synchronized(syncObject) { ... }</code> statements.
     * 
     * @param textArea the text area to write to (non-null).
     * @param syncObject the object to synchronize on (non-null).
     * @param encoding the encoding to use when decoding the stream data into Unicode characters.
     */
    public JTextAreaOutputStream(JTextArea textArea, Object syncObject, String encoding) {
        this(textArea, null, syncObject, encoding);
    }

    /**
     * Creates a new JTextAreaOutputStream which writes to <code>textArea</code> and synchronizes
     * all writes using <code>synchronized(syncObject) { ... }</code> statements. In addition, when
     * updating the JTextArea, this constructor makes JTextAreaOutputStream adjust its JScrollPane,
     * <code>textAreaScroller</code>, accordingly so that the view always follows the latest written
     * text.
     *
     * @param textArea the text area to write to (non-null).
     * @param textAreaScroller the scroll pane to adjust when updating <code>textArea</code>.
     * @param syncObject the object to synchronize on (non-null).
     * @param encoding the encoding to use when decoding the stream data into Unicode characters.
     */
    public JTextAreaOutputStream(JTextArea textArea, JScrollPane textAreaScroller,
            Object syncObject, String encoding) {
        if(textArea == null)
            throw new IllegalArgumentException("textArea == null");
        if(syncObject == null)
            throw new IllegalArgumentException("syncObject == null");
        
        this.textArea = textArea;
        this.textAreaScroller = textAreaScroller;
        this.syncObject = syncObject;
        this.encoding = encoding;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void write(int b) throws IOException {
        this.write(new byte[]{(byte) b}, 0, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }

    //private StringBuilder curBuilder = new StringBuilder();

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        synchronized(syncObject) {
            final String s;
            if(encoding == null)
                s = new String(b, off, len);
            else
                s = new String(b, off, len, encoding);

            textArea.append(s);
            //curBuilder.append(s);

            if(textAreaScroller != null && !updateRequested) {
                updateRequested = true;
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        synchronized(syncObject) {
                            //textArea.append(curBuilder.toString());
                            //curBuilder.setLength(0);
                            updateRequested = false;
                            JScrollBar sb = textAreaScroller.getVerticalScrollBar();
                            sb.setValue(sb.getMaximum() - sb.getVisibleAmount());
                        }
                        //textArea.append(s);
                        //textArea.append(" [Update!] ");
                    }
                });
            }
        }
    }
}

