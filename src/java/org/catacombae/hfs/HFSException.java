/*-
 * Copyright (C) 2016 Erik Larsson
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

package org.catacombae.hfs;

/**
 * The superclass of all exceptions that are thrown because of inconsistent HFS
 * on-disk data.<br>
 * It should not be thrown as a result of inconsistencies in the internal state,
 * only when encountering inconsistencies in structs read from disk that prevent
 * us from proceeding with the requested operation.
 *
 * @author <a href="https://catacombae.org" target="_top">Erik Larsson</a>
 */
public class HFSException extends RuntimeException {
    public HFSException(String message) {
        super(message);
    }

    public HFSException(String message, Throwable cause) {
        super(message, cause);
    }
}
