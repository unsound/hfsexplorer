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

import java.math.BigInteger;

/**
 * Convenient interface containing some essential limits of generic types.
 *
 * @author <a href="https://catacombae.org" target="_top">Erik Larsson</a>
 */
public interface Limits {
    public static final int UINT8_MAX = 0xFF;
    public static final int UINT16_MAX = 0xFFFF;
    public static final long UINT32_MAX = 0xFFFFFFFFL;
    public static final BigInteger UINT64_MAX =
            new BigInteger(1, new byte[] {
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            });
}
