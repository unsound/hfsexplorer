/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.hfsexplorer.fsframework;

import java.io.InputStream;

/**
 *
 * @author Erik
 */
public abstract class FSFile extends FSEntry {
    public abstract long getMainStreamSize();
    public abstract InputStream getMainStream();
    
    public abstract long getNumberOfDataStreams();
    public abstract String getStreamName(long streamNumber);
    public abstract InputStream getStream(long streamNumber);
}
