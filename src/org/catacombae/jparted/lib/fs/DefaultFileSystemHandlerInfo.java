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

package org.catacombae.jparted.lib.fs;

/**
 * Default implementation of a FileSystemHandlerInfo object which holds static
 * fields.
 * 
 * @author Erik Larsson
 */
public class DefaultFileSystemHandlerInfo implements FileSystemHandlerInfo {
    private final String handlerName;
    private final String handlerVersion;
    private final long revision;
    private final String author;

    public DefaultFileSystemHandlerInfo(String iHandlerName, String iHandlerVersion,
            long iRevision, String iAuthor) {
        this.handlerName = iHandlerName;
        this.handlerVersion = iHandlerVersion;
        this.revision = iRevision;
        this.author = iAuthor;
    }

    public String getHandlerName() {
        return handlerName;
    }

    public String getHandlerVersion() {
        return handlerVersion;
    }

    public long getRevision() {
        return revision;
    }

    public String getAuthor() {
        return author;
    }
}
