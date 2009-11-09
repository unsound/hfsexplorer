/*-
 * Copyright (C) 2006-2008 Erik Larsson
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

package org.catacombae.hfsexplorer;

import java.io.PrintStream;

public class Util extends org.catacombae.util.Util {
    public static <A> boolean contains(A[] array, A element) {
        for(A a : array) {
            if(a == element)
                return true;
        }
        return false;
    }

    /**
     * Reverses the order of the array <code>data</code>.
     *
     * @param data the array to reverse.
     * @return <code>data</code>.
     */
    public static byte[] byteSwap(byte[] data) {
        return byteSwap(data, 0, data.length);
    }

    /**
     * Reverses the order of the range defined by <code>offset</code> and
     * <code>length</code> in the array <code>data</code>.
     *
     * @param data the array to reverse.
     * @param offset the start offset of the region to reverse.
     * @param length the length of the region to reverse.
     * @return <code>data</code>.
     */
    public static byte[] byteSwap(byte[] data, int offset, int length) {
        int endOffset = offset + length - 1;
        int middleOffset = offset + (length / 2);
        byte tmp;

        for(int head = offset; head < middleOffset; ++head) {
            int tail = endOffset - head;
            if(head == tail)
                break;

            // Swap data[head] and data[tail]
            tmp = data[head];
            data[head] = data[tail];
            data[tail] = tmp;
        }

        return data;
    }
}
