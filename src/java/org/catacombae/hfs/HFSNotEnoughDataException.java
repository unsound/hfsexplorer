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
 * @author <a href="https://catacombae.org" target="_top">Erik Larsson</a>
 */
public class HFSNotEnoughDataException extends HFSException {

    public HFSNotEnoughDataException(boolean dynamic, int arrayLength,
            int arrayOffset, int minimumLength, Class<?> clazz)
    {
        super("Not enough data for " + clazz.getSimpleName() + " " +
                (dynamic ? "dynamic" : "static") + " fields (array " +
                "length: " + arrayLength + ", offset: " +
                arrayOffset + ", available: " + (arrayLength - arrayOffset) +
                ", minimum: " + minimumLength + ").");
    }
}
