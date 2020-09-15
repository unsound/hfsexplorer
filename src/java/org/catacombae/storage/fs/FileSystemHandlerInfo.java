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
 * Contains information about a specific implementation of a file system
 * handler.
 *
 * @author <a href="https://catacombae.org" target="_top">Erik Larsson</a>
 */
public interface FileSystemHandlerInfo {
    /**
     * Returns the handler name. This is the name of the handler, and not
     * necessarily the name of the file system itself. If a NTFS file system
     * handler is called "NTFS-3G", the handler name should be "NTFS-3G".
     *
     * @return the handler name;
     */
    public String getHandlerName();

    /**
     * Returns a unique identifier for this file system handler. This identifier
     * should be in reverse-DNS form, just like the naming of java packages.
     * An example could be: "com.yourcompany.our_filesystem_handler".<br>
     * The identifier should be chosen wisely and retained through the entire
     * lifecycle of the handler.
     *
     * @return a unique identifier for this file system handler.
     */
    public String getHandlerIdentifier();

    /**
     * Returns a free-form user readable version string for this handler.
     * @return a free-form user readable version string for this handler.
     */
    public String getHandlerVersion();

    /**
     * Returns the revision number of this release of the handler (striclty
     * increasing).<br>
     * The revision number should be increased for every release of the file
     * system handler. One should be able to tell a newer version from an older
     * version by comparing revision numbers.
     *
     * @return the revision number of this release of the handler.
     */
    public long getRevision();

    /**
     * Returns the company / group / individual(s) that produced this file
     * system handler. The string is free-form.
     *
     * @return the author of this file system handler.
     */
    public String getAuthor();
}
