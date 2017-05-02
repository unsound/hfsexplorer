/*-
 * Copyright (C) 2016-2017 Erik Larsson
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

package org.catacombae.hfs.util;

/**
 * Helper functions for Services For Mac substitutions.
 *
 * @author <a href="http://www.catacombae.org/" target="_top">Erik Larsson</a>
 */
public class ServicesForMac {
    private static final char[] wrapTable = {
        (char) 0x0000, (char) 0xF001, (char) 0xF002, (char) 0xF003,
        (char) 0xF004, (char) 0xF005, (char) 0xF006, (char) 0xF007,
        (char) 0xF008, (char) 0xF009, (char) 0xF00A, (char) 0xF00B,
        (char) 0xF00C, (char) 0xF00D, (char) 0xF00E, (char) 0xF00F,
        (char) 0xF010, (char) 0xF011, (char) 0xF012, (char) 0xF013,
        (char) 0xF014, (char) 0xF015, (char) 0xF016, (char) 0xF017,
        (char) 0xF018, (char) 0xF019, (char) 0xF01A, (char) 0xF01B,
        (char) 0xF01C, (char) 0xF01D, (char) 0xF01E, (char) 0xF01F,
        (char) 0x0020, (char) 0x0021, (char) 0xF020, (char) 0x0023,
        (char) 0x0024, (char) 0x0025, (char) 0x0026, (char) 0x0027,
        (char) 0x0028, (char) 0x0029, (char) 0xF021, (char) 0x002B,
        (char) 0x002C, (char) 0x002D, (char) 0x002E, (char) 0x002F,
        (char) 0x0030, (char) 0x0031, (char) 0x0032, (char) 0x0033,
        (char) 0x0034, (char) 0x0035, (char) 0x0036, (char) 0x0037,
        (char) 0x0038, (char) 0x0039, (char) 0xF022, (char) 0x003B,
        (char) 0xF023, (char) 0x003D, (char) 0xF024, (char) 0xF025,
        (char) 0x0040, (char) 0x0041, (char) 0x0042, (char) 0x0043,
        (char) 0x0044, (char) 0x0045, (char) 0x0046, (char) 0x0047,
        (char) 0x0048, (char) 0x0049, (char) 0x004A, (char) 0x004B,
        (char) 0x004C, (char) 0x004D, (char) 0x004E, (char) 0x004F,
        (char) 0x0050, (char) 0x0051, (char) 0x0052, (char) 0x0053,
        (char) 0x0054, (char) 0x0055, (char) 0x0056, (char) 0x0057,
        (char) 0x0058, (char) 0x0059, (char) 0x005A, (char) 0x005B,
        (char) 0xF026, (char) 0x005D, (char) 0x005E, (char) 0x005F,
        (char) 0x0060, (char) 0x0061, (char) 0x0062, (char) 0x0063,
        (char) 0x0064, (char) 0x0065, (char) 0x0066, (char) 0x0067,
        (char) 0x0068, (char) 0x0069, (char) 0x006A, (char) 0x006B,
        (char) 0x006C, (char) 0x006D, (char) 0x006E, (char) 0x006F,
        (char) 0x0070, (char) 0x0071, (char) 0x0072, (char) 0x0073,
        (char) 0x0074, (char) 0x0075, (char) 0x0076, (char) 0x0077,
        (char) 0x0078, (char) 0x0079, (char) 0x007A, (char) 0x007B,
        (char) 0xF027,
    };

    private static final char[] unwrapTable = {
        (char) 0x0001, (char) 0x0002, (char) 0x0003, (char) 0x0004,
        (char) 0x0005, (char) 0x0006, (char) 0x0007, (char) 0x0008,
        (char) 0x0009, (char) 0x000A, (char) 0x000B, (char) 0x000C,
        (char) 0x000D, (char) 0x000E, (char) 0x000F, (char) 0x0010,
        (char) 0x0011, (char) 0x0012, (char) 0x0013, (char) 0x0014,
        (char) 0x0015, (char) 0x0016, (char) 0x0017, (char) 0x0018,
        (char) 0x0019, (char) 0x001A, (char) 0x001B, (char) 0x001C,
        (char) 0x001D, (char) 0x001E, (char) 0x001F, (char) 0x0022,
        (char) 0x002A, (char) 0x003A, (char) 0x003C, (char) 0x003E,
        (char) 0x003F, (char) 0x005C, (char) 0x007C,
    };

    public static String remap(String nodeName, boolean unwrap) {
        final char[] nodeNameChars = nodeName.toCharArray();

        int char_value;
        int i;

        for(char_value = 0, i = 0; i < nodeNameChars.length; ++i) {
            char_value = nodeNameChars[i] & 0xFFFF;

            if(char_value <= 0x7C) {
                nodeNameChars[i] = wrapTable[char_value];
            }
            else if(char_value == 0xF8FF) {
                nodeNameChars[i] = 0xF02A;
            }
            else if(char_value >= 0xF001 && char_value <= 0xF027) {
                nodeNameChars[i] = unwrapTable[char_value - 0xF001];
            }
            else if(char_value == 0xF02A) {
                nodeNameChars[i] = 0xF8FF;
            }
        }

        if(i != 0) {
            if(!unwrap) {
                if(char_value == 0x20) {
                    nodeNameChars[i - 1] = 0xF028;
                }
                else if(char_value == 0x2E) {
                    nodeNameChars[i - 1] = 0xF029;
                }
            }
            else {
                if(char_value == 0xF028) {
                    nodeNameChars[i - 1] = 0x20;
                }
                else if(char_value == 0xF029) {
                    nodeNameChars[i - 1] = 0x2E;
                }
            }
        }

        return new String(nodeNameChars);
    }
}
