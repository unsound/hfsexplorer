/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.jparted.lib.fs;

/**
 * Contains information about a specific implementation of a file system
 * handler.
 * @author erik
 */
public interface FileSystemHandlerInfo {
    public String getHandlerName();
    public String getHandlerVersion();
    public long getRevision();
    public String getAuthor();
}
