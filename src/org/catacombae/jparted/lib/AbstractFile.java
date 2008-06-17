/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.jparted.lib;

/**
 *
 * @author erik
 */
public interface AbstractFile {
    public void seek(long pos);
    public int read();
    public int read(byte[] data);
    public int read(byte[] data, int pos, int len);
    public void readFully(byte[] data);
    public void readFully(byte[] data, int offset, int length);
    public long length();
    public long getFilePointer();
    public void close();
}
