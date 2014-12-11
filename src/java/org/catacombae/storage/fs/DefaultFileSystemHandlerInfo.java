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

package org.catacombae.storage.fs;

/**
 * Default implementation of a FileSystemHandlerInfo object which holds static
 * fields.
 *
 * @author <a href="http://www.catacombae.org/" target="_top">Erik Larsson</a>
 */
public class DefaultFileSystemHandlerInfo implements FileSystemHandlerInfo {
    private final String handlerIdentifier;
    private final String handlerName;
    private final String handlerVersion;
    private final long revision;
    private final String author;

    /**
     *
     * @param iHandlerIdentifier a unique identifier for this file system handler.
     * @param iHandlerName the handler name.
     * @param iHandlerVersion a free-form user readable version string for this handler.
     * @param iRevision the revision number of this release of the handler.
     * @param iAuthor the author of the handler.s
     */
    public DefaultFileSystemHandlerInfo(String iHandlerIdentifier, String iHandlerName,
            String iHandlerVersion, long iRevision, String iAuthor) {
        this.handlerIdentifier = iHandlerIdentifier;
        this.handlerName = iHandlerName;
        this.handlerVersion = iHandlerVersion;
        this.revision = iRevision;
        this.author = iAuthor;
    }

    /**
     * {@inheritDoc}
     */
    public String getHandlerIdentifier() {
        return handlerIdentifier;
    }

    /**
     * {@inheritDoc}
     */
    public String getHandlerName() {
        return handlerName;
    }

    /**
     * {@inheritDoc}
     */
    public String getHandlerVersion() {
        return handlerVersion;
    }

    /**
     * {@inheritDoc}
     */
    public long getRevision() {
        return revision;
    }

    /**
     * {@inheritDoc}
     */
    public String getAuthor() {
        return author;
    }
}
