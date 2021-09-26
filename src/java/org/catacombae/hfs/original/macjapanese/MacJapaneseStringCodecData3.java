/*-
 * Copyright (C) 2021 Erik Larsson
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

package org.catacombae.hfs.original.macjapanese;

/**
 * Data for @ref MacJapaneseStringCodec (part 3).
 *
 * @author <a href="https://catacombae.org" target="_top">Erik Larsson</a>
 */
class MacJapaneseStringCodecData3 {
    /**
     * Mapping table from MacJapanese to Unicode (part 3).
     *
     * Adapted from the Unicode document:
     *   https://unicode.org/Public/MAPPINGS/VENDORS/APPLE/JAPANESE.TXT
     *
     * Due to code size restrictions the table is split into three class files.
     */
    static final char[][] mappingTable = {
        { (char) 0xE6CB, (char) 0x8CE4 }, // <CJK>
        { (char) 0xE6CC, (char) 0x8CE3 }, // <CJK>
        { (char) 0xE6CD, (char) 0x8CDA }, // <CJK>
        { (char) 0xE6CE, (char) 0x8CFD }, // <CJK>
        { (char) 0xE6CF, (char) 0x8CFA }, // <CJK>
        { (char) 0xE6D0, (char) 0x8CFB }, // <CJK>
        { (char) 0xE6D1, (char) 0x8D04 }, // <CJK>
        { (char) 0xE6D2, (char) 0x8D05 }, // <CJK>
        { (char) 0xE6D3, (char) 0x8D0A }, // <CJK>
        { (char) 0xE6D4, (char) 0x8D07 }, // <CJK>
        { (char) 0xE6D5, (char) 0x8D0F }, // <CJK>
        { (char) 0xE6D6, (char) 0x8D0D }, // <CJK>
        { (char) 0xE6D7, (char) 0x8D10 }, // <CJK>
        { (char) 0xE6D8, (char) 0x9F4E }, // <CJK>
        { (char) 0xE6D9, (char) 0x8D13 }, // <CJK>
        { (char) 0xE6DA, (char) 0x8CCD }, // <CJK>
        { (char) 0xE6DB, (char) 0x8D14 }, // <CJK>
        { (char) 0xE6DC, (char) 0x8D16 }, // <CJK>
        { (char) 0xE6DD, (char) 0x8D67 }, // <CJK>
        { (char) 0xE6DE, (char) 0x8D6D }, // <CJK>
        { (char) 0xE6DF, (char) 0x8D71 }, // <CJK>
        { (char) 0xE6E0, (char) 0x8D73 }, // <CJK>
        { (char) 0xE6E1, (char) 0x8D81 }, // <CJK>
        { (char) 0xE6E2, (char) 0x8D99 }, // <CJK>
        { (char) 0xE6E3, (char) 0x8DC2 }, // <CJK>
        { (char) 0xE6E4, (char) 0x8DBE }, // <CJK>
        { (char) 0xE6E5, (char) 0x8DBA }, // <CJK>
        { (char) 0xE6E6, (char) 0x8DCF }, // <CJK>
        { (char) 0xE6E7, (char) 0x8DDA }, // <CJK>
        { (char) 0xE6E8, (char) 0x8DD6 }, // <CJK>
        { (char) 0xE6E9, (char) 0x8DCC }, // <CJK>
        { (char) 0xE6EA, (char) 0x8DDB }, // <CJK>
        { (char) 0xE6EB, (char) 0x8DCB }, // <CJK>
        { (char) 0xE6EC, (char) 0x8DEA }, // <CJK>
        { (char) 0xE6ED, (char) 0x8DEB }, // <CJK>
        { (char) 0xE6EE, (char) 0x8DDF }, // <CJK>
        { (char) 0xE6EF, (char) 0x8DE3 }, // <CJK>
        { (char) 0xE6F0, (char) 0x8DFC }, // <CJK>
        { (char) 0xE6F1, (char) 0x8E08 }, // <CJK>
        { (char) 0xE6F2, (char) 0x8E09 }, // <CJK>
        { (char) 0xE6F3, (char) 0x8DFF }, // <CJK>
        { (char) 0xE6F4, (char) 0x8E1D }, // <CJK>
        { (char) 0xE6F5, (char) 0x8E1E }, // <CJK>
        { (char) 0xE6F6, (char) 0x8E10 }, // <CJK>
        { (char) 0xE6F7, (char) 0x8E1F }, // <CJK>
        { (char) 0xE6F8, (char) 0x8E42 }, // <CJK>
        { (char) 0xE6F9, (char) 0x8E35 }, // <CJK>
        { (char) 0xE6FA, (char) 0x8E30 }, // <CJK>
        { (char) 0xE6FB, (char) 0x8E34 }, // <CJK>
        { (char) 0xE6FC, (char) 0x8E4A }, // <CJK>
        { (char) 0xE740, (char) 0x8E47 }, // <CJK>
        { (char) 0xE741, (char) 0x8E49 }, // <CJK>
        { (char) 0xE742, (char) 0x8E4C }, // <CJK>
        { (char) 0xE743, (char) 0x8E50 }, // <CJK>
        { (char) 0xE744, (char) 0x8E48 }, // <CJK>
        { (char) 0xE745, (char) 0x8E59 }, // <CJK>
        { (char) 0xE746, (char) 0x8E64 }, // <CJK>
        { (char) 0xE747, (char) 0x8E60 }, // <CJK>
        { (char) 0xE748, (char) 0x8E2A }, // <CJK>
        { (char) 0xE749, (char) 0x8E63 }, // <CJK>
        { (char) 0xE74A, (char) 0x8E55 }, // <CJK>
        { (char) 0xE74B, (char) 0x8E76 }, // <CJK>
        { (char) 0xE74C, (char) 0x8E72 }, // <CJK>
        { (char) 0xE74D, (char) 0x8E7C }, // <CJK>
        { (char) 0xE74E, (char) 0x8E81 }, // <CJK>
        { (char) 0xE74F, (char) 0x8E87 }, // <CJK>
        { (char) 0xE750, (char) 0x8E85 }, // <CJK>
        { (char) 0xE751, (char) 0x8E84 }, // <CJK>
        { (char) 0xE752, (char) 0x8E8B }, // <CJK>
        { (char) 0xE753, (char) 0x8E8A }, // <CJK>
        { (char) 0xE754, (char) 0x8E93 }, // <CJK>
        { (char) 0xE755, (char) 0x8E91 }, // <CJK>
        { (char) 0xE756, (char) 0x8E94 }, // <CJK>
        { (char) 0xE757, (char) 0x8E99 }, // <CJK>
        { (char) 0xE758, (char) 0x8EAA }, // <CJK>
        { (char) 0xE759, (char) 0x8EA1 }, // <CJK>
        { (char) 0xE75A, (char) 0x8EAC }, // <CJK>
        { (char) 0xE75B, (char) 0x8EB0 }, // <CJK>
        { (char) 0xE75C, (char) 0x8EC6 }, // <CJK>
        { (char) 0xE75D, (char) 0x8EB1 }, // <CJK>
        { (char) 0xE75E, (char) 0x8EBE }, // <CJK>
        { (char) 0xE75F, (char) 0x8EC5 }, // <CJK>
        { (char) 0xE760, (char) 0x8EC8 }, // <CJK>
        { (char) 0xE761, (char) 0x8ECB }, // <CJK>
        { (char) 0xE762, (char) 0x8EDB }, // <CJK>
        { (char) 0xE763, (char) 0x8EE3 }, // <CJK>
        { (char) 0xE764, (char) 0x8EFC }, // <CJK>
        { (char) 0xE765, (char) 0x8EFB }, // <CJK>
        { (char) 0xE766, (char) 0x8EEB }, // <CJK>
        { (char) 0xE767, (char) 0x8EFE }, // <CJK>
        { (char) 0xE768, (char) 0x8F0A }, // <CJK>
        { (char) 0xE769, (char) 0x8F05 }, // <CJK>
        { (char) 0xE76A, (char) 0x8F15 }, // <CJK>
        { (char) 0xE76B, (char) 0x8F12 }, // <CJK>
        { (char) 0xE76C, (char) 0x8F19 }, // <CJK>
        { (char) 0xE76D, (char) 0x8F13 }, // <CJK>
        { (char) 0xE76E, (char) 0x8F1C }, // <CJK>
        { (char) 0xE76F, (char) 0x8F1F }, // <CJK>
        { (char) 0xE770, (char) 0x8F1B }, // <CJK>
        { (char) 0xE771, (char) 0x8F0C }, // <CJK>
        { (char) 0xE772, (char) 0x8F26 }, // <CJK>
        { (char) 0xE773, (char) 0x8F33 }, // <CJK>
        { (char) 0xE774, (char) 0x8F3B }, // <CJK>
        { (char) 0xE775, (char) 0x8F39 }, // <CJK>
        { (char) 0xE776, (char) 0x8F45 }, // <CJK>
        { (char) 0xE777, (char) 0x8F42 }, // <CJK>
        { (char) 0xE778, (char) 0x8F3E }, // <CJK>
        { (char) 0xE779, (char) 0x8F4C }, // <CJK>
        { (char) 0xE77A, (char) 0x8F49 }, // <CJK>
        { (char) 0xE77B, (char) 0x8F46 }, // <CJK>
        { (char) 0xE77C, (char) 0x8F4E }, // <CJK>
        { (char) 0xE77D, (char) 0x8F57 }, // <CJK>
        { (char) 0xE77E, (char) 0x8F5C }, // <CJK>
        { (char) 0xE780, (char) 0x8F62 }, // <CJK>
        { (char) 0xE781, (char) 0x8F63 }, // <CJK>
        { (char) 0xE782, (char) 0x8F64 }, // <CJK>
        { (char) 0xE783, (char) 0x8F9C }, // <CJK>
        { (char) 0xE784, (char) 0x8F9F }, // <CJK>
        { (char) 0xE785, (char) 0x8FA3 }, // <CJK>
        { (char) 0xE786, (char) 0x8FAD }, // <CJK>
        { (char) 0xE787, (char) 0x8FAF }, // <CJK>
        { (char) 0xE788, (char) 0x8FB7 }, // <CJK>
        { (char) 0xE789, (char) 0x8FDA }, // <CJK>
        { (char) 0xE78A, (char) 0x8FE5 }, // <CJK>
        { (char) 0xE78B, (char) 0x8FE2 }, // <CJK>
        { (char) 0xE78C, (char) 0x8FEA }, // <CJK>
        { (char) 0xE78D, (char) 0x8FEF }, // <CJK>
        { (char) 0xE78E, (char) 0x9087 }, // <CJK>
        { (char) 0xE78F, (char) 0x8FF4 }, // <CJK>
        { (char) 0xE790, (char) 0x9005 }, // <CJK>
        { (char) 0xE791, (char) 0x8FF9 }, // <CJK>
        { (char) 0xE792, (char) 0x8FFA }, // <CJK>
        { (char) 0xE793, (char) 0x9011 }, // <CJK>
        { (char) 0xE794, (char) 0x9015 }, // <CJK>
        { (char) 0xE795, (char) 0x9021 }, // <CJK>
        { (char) 0xE796, (char) 0x900D }, // <CJK>
        { (char) 0xE797, (char) 0x901E }, // <CJK>
        { (char) 0xE798, (char) 0x9016 }, // <CJK>
        { (char) 0xE799, (char) 0x900B }, // <CJK>
        { (char) 0xE79A, (char) 0x9027 }, // <CJK>
        { (char) 0xE79B, (char) 0x9036 }, // <CJK>
        { (char) 0xE79C, (char) 0x9035 }, // <CJK>
        { (char) 0xE79D, (char) 0x9039 }, // <CJK>
        { (char) 0xE79E, (char) 0x8FF8 }, // <CJK>
        { (char) 0xE79F, (char) 0x904F }, // <CJK>
        { (char) 0xE7A0, (char) 0x9050 }, // <CJK>
        { (char) 0xE7A1, (char) 0x9051 }, // <CJK>
        { (char) 0xE7A2, (char) 0x9052 }, // <CJK>
        { (char) 0xE7A3, (char) 0x900E }, // <CJK>
        { (char) 0xE7A4, (char) 0x9049 }, // <CJK>
        { (char) 0xE7A5, (char) 0x903E }, // <CJK>
        { (char) 0xE7A6, (char) 0x9056 }, // <CJK>
        { (char) 0xE7A7, (char) 0x9058 }, // <CJK>
        { (char) 0xE7A8, (char) 0x905E }, // <CJK>
        { (char) 0xE7A9, (char) 0x9068 }, // <CJK>
        { (char) 0xE7AA, (char) 0x906F }, // <CJK>
        { (char) 0xE7AB, (char) 0x9076 }, // <CJK>
        { (char) 0xE7AC, (char) 0x96A8 }, // <CJK>
        { (char) 0xE7AD, (char) 0x9072 }, // <CJK>
        { (char) 0xE7AE, (char) 0x9082 }, // <CJK>
        { (char) 0xE7AF, (char) 0x907D }, // <CJK>
        { (char) 0xE7B0, (char) 0x9081 }, // <CJK>
        { (char) 0xE7B1, (char) 0x9080 }, // <CJK>
        { (char) 0xE7B2, (char) 0x908A }, // <CJK>
        { (char) 0xE7B3, (char) 0x9089 }, // <CJK>
        { (char) 0xE7B4, (char) 0x908F }, // <CJK>
        { (char) 0xE7B5, (char) 0x90A8 }, // <CJK>
        { (char) 0xE7B6, (char) 0x90AF }, // <CJK>
        { (char) 0xE7B7, (char) 0x90B1 }, // <CJK>
        { (char) 0xE7B8, (char) 0x90B5 }, // <CJK>
        { (char) 0xE7B9, (char) 0x90E2 }, // <CJK>
        { (char) 0xE7BA, (char) 0x90E4 }, // <CJK>
        { (char) 0xE7BB, (char) 0x6248 }, // <CJK>
        { (char) 0xE7BC, (char) 0x90DB }, // <CJK>
        { (char) 0xE7BD, (char) 0x9102 }, // <CJK>
        { (char) 0xE7BE, (char) 0x9112 }, // <CJK>
        { (char) 0xE7BF, (char) 0x9119 }, // <CJK>
        { (char) 0xE7C0, (char) 0x9132 }, // <CJK>
        { (char) 0xE7C1, (char) 0x9130 }, // <CJK>
        { (char) 0xE7C2, (char) 0x914A }, // <CJK>
        { (char) 0xE7C3, (char) 0x9156 }, // <CJK>
        { (char) 0xE7C4, (char) 0x9158 }, // <CJK>
        { (char) 0xE7C5, (char) 0x9163 }, // <CJK>
        { (char) 0xE7C6, (char) 0x9165 }, // <CJK>
        { (char) 0xE7C7, (char) 0x9169 }, // <CJK>
        { (char) 0xE7C8, (char) 0x9173 }, // <CJK>
        { (char) 0xE7C9, (char) 0x9172 }, // <CJK>
        { (char) 0xE7CA, (char) 0x918B }, // <CJK>
        { (char) 0xE7CB, (char) 0x9189 }, // <CJK>
        { (char) 0xE7CC, (char) 0x9182 }, // <CJK>
        { (char) 0xE7CD, (char) 0x91A2 }, // <CJK>
        { (char) 0xE7CE, (char) 0x91AB }, // <CJK>
        { (char) 0xE7CF, (char) 0x91AF }, // <CJK>
        { (char) 0xE7D0, (char) 0x91AA }, // <CJK>
        { (char) 0xE7D1, (char) 0x91B5 }, // <CJK>
        { (char) 0xE7D2, (char) 0x91B4 }, // <CJK>
        { (char) 0xE7D3, (char) 0x91BA }, // <CJK>
        { (char) 0xE7D4, (char) 0x91C0 }, // <CJK>
        { (char) 0xE7D5, (char) 0x91C1 }, // <CJK>
        { (char) 0xE7D6, (char) 0x91C9 }, // <CJK>
        { (char) 0xE7D7, (char) 0x91CB }, // <CJK>
        { (char) 0xE7D8, (char) 0x91D0 }, // <CJK>
        { (char) 0xE7D9, (char) 0x91D6 }, // <CJK>
        { (char) 0xE7DA, (char) 0x91DF }, // <CJK>
        { (char) 0xE7DB, (char) 0x91E1 }, // <CJK>
        { (char) 0xE7DC, (char) 0x91DB }, // <CJK>
        { (char) 0xE7DD, (char) 0x91FC }, // <CJK>
        { (char) 0xE7DE, (char) 0x91F5 }, // <CJK>
        { (char) 0xE7DF, (char) 0x91F6 }, // <CJK>
        { (char) 0xE7E0, (char) 0x921E }, // <CJK>
        { (char) 0xE7E1, (char) 0x91FF }, // <CJK>
        { (char) 0xE7E2, (char) 0x9214 }, // <CJK>
        { (char) 0xE7E3, (char) 0x922C }, // <CJK>
        { (char) 0xE7E4, (char) 0x9215 }, // <CJK>
        { (char) 0xE7E5, (char) 0x9211 }, // <CJK>
        { (char) 0xE7E6, (char) 0x925E }, // <CJK>
        { (char) 0xE7E7, (char) 0x9257 }, // <CJK>
        { (char) 0xE7E8, (char) 0x9245 }, // <CJK>
        { (char) 0xE7E9, (char) 0x9249 }, // <CJK>
        { (char) 0xE7EA, (char) 0x9264 }, // <CJK>
        { (char) 0xE7EB, (char) 0x9248 }, // <CJK>
        { (char) 0xE7EC, (char) 0x9295 }, // <CJK>
        { (char) 0xE7ED, (char) 0x923F }, // <CJK>
        { (char) 0xE7EE, (char) 0x924B }, // <CJK>
        { (char) 0xE7EF, (char) 0x9250 }, // <CJK>
        { (char) 0xE7F0, (char) 0x929C }, // <CJK>
        { (char) 0xE7F1, (char) 0x9296 }, // <CJK>
        { (char) 0xE7F2, (char) 0x9293 }, // <CJK>
        { (char) 0xE7F3, (char) 0x929B }, // <CJK>
        { (char) 0xE7F4, (char) 0x925A }, // <CJK>
        { (char) 0xE7F5, (char) 0x92CF }, // <CJK>
        { (char) 0xE7F6, (char) 0x92B9 }, // <CJK>
        { (char) 0xE7F7, (char) 0x92B7 }, // <CJK>
        { (char) 0xE7F8, (char) 0x92E9 }, // <CJK>
        { (char) 0xE7F9, (char) 0x930F }, // <CJK>
        { (char) 0xE7FA, (char) 0x92FA }, // <CJK>
        { (char) 0xE7FB, (char) 0x9344 }, // <CJK>
        { (char) 0xE7FC, (char) 0x932E }, // <CJK>
        { (char) 0xE840, (char) 0x9319 }, // <CJK>
        { (char) 0xE841, (char) 0x9322 }, // <CJK>
        { (char) 0xE842, (char) 0x931A }, // <CJK>
        { (char) 0xE843, (char) 0x9323 }, // <CJK>
        { (char) 0xE844, (char) 0x933A }, // <CJK>
        { (char) 0xE845, (char) 0x9335 }, // <CJK>
        { (char) 0xE846, (char) 0x933B }, // <CJK>
        { (char) 0xE847, (char) 0x935C }, // <CJK>
        { (char) 0xE848, (char) 0x9360 }, // <CJK>
        { (char) 0xE849, (char) 0x937C }, // <CJK>
        { (char) 0xE84A, (char) 0x936E }, // <CJK>
        { (char) 0xE84B, (char) 0x9356 }, // <CJK>
        { (char) 0xE84C, (char) 0x93B0 }, // <CJK>
        { (char) 0xE84D, (char) 0x93AC }, // <CJK>
        { (char) 0xE84E, (char) 0x93AD }, // <CJK>
        { (char) 0xE84F, (char) 0x9394 }, // <CJK>
        { (char) 0xE850, (char) 0x93B9 }, // <CJK>
        { (char) 0xE851, (char) 0x93D6 }, // <CJK>
        { (char) 0xE852, (char) 0x93D7 }, // <CJK>
        { (char) 0xE853, (char) 0x93E8 }, // <CJK>
        { (char) 0xE854, (char) 0x93E5 }, // <CJK>
        { (char) 0xE855, (char) 0x93D8 }, // <CJK>
        { (char) 0xE856, (char) 0x93C3 }, // <CJK>
        { (char) 0xE857, (char) 0x93DD }, // <CJK>
        { (char) 0xE858, (char) 0x93D0 }, // <CJK>
        { (char) 0xE859, (char) 0x93C8 }, // <CJK>
        { (char) 0xE85A, (char) 0x93E4 }, // <CJK>
        { (char) 0xE85B, (char) 0x941A }, // <CJK>
        { (char) 0xE85C, (char) 0x9414 }, // <CJK>
        { (char) 0xE85D, (char) 0x9413 }, // <CJK>
        { (char) 0xE85E, (char) 0x9403 }, // <CJK>
        { (char) 0xE85F, (char) 0x9407 }, // <CJK>
        { (char) 0xE860, (char) 0x9410 }, // <CJK>
        { (char) 0xE861, (char) 0x9436 }, // <CJK>
        { (char) 0xE862, (char) 0x942B }, // <CJK>
        { (char) 0xE863, (char) 0x9435 }, // <CJK>
        { (char) 0xE864, (char) 0x9421 }, // <CJK>
        { (char) 0xE865, (char) 0x943A }, // <CJK>
        { (char) 0xE866, (char) 0x9441 }, // <CJK>
        { (char) 0xE867, (char) 0x9452 }, // <CJK>
        { (char) 0xE868, (char) 0x9444 }, // <CJK>
        { (char) 0xE869, (char) 0x945B }, // <CJK>
        { (char) 0xE86A, (char) 0x9460 }, // <CJK>
        { (char) 0xE86B, (char) 0x9462 }, // <CJK>
        { (char) 0xE86C, (char) 0x945E }, // <CJK>
        { (char) 0xE86D, (char) 0x946A }, // <CJK>
        { (char) 0xE86E, (char) 0x9229 }, // <CJK>
        { (char) 0xE86F, (char) 0x9470 }, // <CJK>
        { (char) 0xE870, (char) 0x9475 }, // <CJK>
        { (char) 0xE871, (char) 0x9477 }, // <CJK>
        { (char) 0xE872, (char) 0x947D }, // <CJK>
        { (char) 0xE873, (char) 0x945A }, // <CJK>
        { (char) 0xE874, (char) 0x947C }, // <CJK>
        { (char) 0xE875, (char) 0x947E }, // <CJK>
        { (char) 0xE876, (char) 0x9481 }, // <CJK>
        { (char) 0xE877, (char) 0x947F }, // <CJK>
        { (char) 0xE878, (char) 0x9582 }, // <CJK>
        { (char) 0xE879, (char) 0x9587 }, // <CJK>
        { (char) 0xE87A, (char) 0x958A }, // <CJK>
        { (char) 0xE87B, (char) 0x9594 }, // <CJK>
        { (char) 0xE87C, (char) 0x9596 }, // <CJK>
        { (char) 0xE87D, (char) 0x9598 }, // <CJK>
        { (char) 0xE87E, (char) 0x9599 }, // <CJK>
        { (char) 0xE880, (char) 0x95A0 }, // <CJK>
        { (char) 0xE881, (char) 0x95A8 }, // <CJK>
        { (char) 0xE882, (char) 0x95A7 }, // <CJK>
        { (char) 0xE883, (char) 0x95AD }, // <CJK>
        { (char) 0xE884, (char) 0x95BC }, // <CJK>
        { (char) 0xE885, (char) 0x95BB }, // <CJK>
        { (char) 0xE886, (char) 0x95B9 }, // <CJK>
        { (char) 0xE887, (char) 0x95BE }, // <CJK>
        { (char) 0xE888, (char) 0x95CA }, // <CJK>
        { (char) 0xE889, (char) 0x6FF6 }, // <CJK>
        { (char) 0xE88A, (char) 0x95C3 }, // <CJK>
        { (char) 0xE88B, (char) 0x95CD }, // <CJK>
        { (char) 0xE88C, (char) 0x95CC }, // <CJK>
        { (char) 0xE88D, (char) 0x95D5 }, // <CJK>
        { (char) 0xE88E, (char) 0x95D4 }, // <CJK>
        { (char) 0xE88F, (char) 0x95D6 }, // <CJK>
        { (char) 0xE890, (char) 0x95DC }, // <CJK>
        { (char) 0xE891, (char) 0x95E1 }, // <CJK>
        { (char) 0xE892, (char) 0x95E5 }, // <CJK>
        { (char) 0xE893, (char) 0x95E2 }, // <CJK>
        { (char) 0xE894, (char) 0x9621 }, // <CJK>
        { (char) 0xE895, (char) 0x9628 }, // <CJK>
        { (char) 0xE896, (char) 0x962E }, // <CJK>
        { (char) 0xE897, (char) 0x962F }, // <CJK>
        { (char) 0xE898, (char) 0x9642 }, // <CJK>
        { (char) 0xE899, (char) 0x964C }, // <CJK>
        { (char) 0xE89A, (char) 0x964F }, // <CJK>
        { (char) 0xE89B, (char) 0x964B }, // <CJK>
        { (char) 0xE89C, (char) 0x9677 }, // <CJK>
        { (char) 0xE89D, (char) 0x965C }, // <CJK>
        { (char) 0xE89E, (char) 0x965E }, // <CJK>
        { (char) 0xE89F, (char) 0x965D }, // <CJK>
        { (char) 0xE8A0, (char) 0x965F }, // <CJK>
        { (char) 0xE8A1, (char) 0x9666 }, // <CJK>
        { (char) 0xE8A2, (char) 0x9672 }, // <CJK>
        { (char) 0xE8A3, (char) 0x966C }, // <CJK>
        { (char) 0xE8A4, (char) 0x968D }, // <CJK>
        { (char) 0xE8A5, (char) 0x9698 }, // <CJK>
        { (char) 0xE8A6, (char) 0x9695 }, // <CJK>
        { (char) 0xE8A7, (char) 0x9697 }, // <CJK>
        { (char) 0xE8A8, (char) 0x96AA }, // <CJK>
        { (char) 0xE8A9, (char) 0x96A7 }, // <CJK>
        { (char) 0xE8AA, (char) 0x96B1 }, // <CJK>
        { (char) 0xE8AB, (char) 0x96B2 }, // <CJK>
        { (char) 0xE8AC, (char) 0x96B0 }, // <CJK>
        { (char) 0xE8AD, (char) 0x96B4 }, // <CJK>
        { (char) 0xE8AE, (char) 0x96B6 }, // <CJK>
        { (char) 0xE8AF, (char) 0x96B8 }, // <CJK>
        { (char) 0xE8B0, (char) 0x96B9 }, // <CJK>
        { (char) 0xE8B1, (char) 0x96CE }, // <CJK>
        { (char) 0xE8B2, (char) 0x96CB }, // <CJK>
        { (char) 0xE8B3, (char) 0x96C9 }, // <CJK>
        { (char) 0xE8B4, (char) 0x96CD }, // <CJK>
        { (char) 0xE8B5, (char) 0x894D }, // <CJK>
        { (char) 0xE8B6, (char) 0x96DC }, // <CJK>
        { (char) 0xE8B7, (char) 0x970D }, // <CJK>
        { (char) 0xE8B8, (char) 0x96D5 }, // <CJK>
        { (char) 0xE8B9, (char) 0x96F9 }, // <CJK>
        { (char) 0xE8BA, (char) 0x9704 }, // <CJK>
        { (char) 0xE8BB, (char) 0x9706 }, // <CJK>
        { (char) 0xE8BC, (char) 0x9708 }, // <CJK>
        { (char) 0xE8BD, (char) 0x9713 }, // <CJK>
        { (char) 0xE8BE, (char) 0x970E }, // <CJK>
        { (char) 0xE8BF, (char) 0x9711 }, // <CJK>
        { (char) 0xE8C0, (char) 0x970F }, // <CJK>
        { (char) 0xE8C1, (char) 0x9716 }, // <CJK>
        { (char) 0xE8C2, (char) 0x9719 }, // <CJK>
        { (char) 0xE8C3, (char) 0x9724 }, // <CJK>
        { (char) 0xE8C4, (char) 0x972A }, // <CJK>
        { (char) 0xE8C5, (char) 0x9730 }, // <CJK>
        { (char) 0xE8C6, (char) 0x9739 }, // <CJK>
        { (char) 0xE8C7, (char) 0x973D }, // <CJK>
        { (char) 0xE8C8, (char) 0x973E }, // <CJK>
        { (char) 0xE8C9, (char) 0x9744 }, // <CJK>
        { (char) 0xE8CA, (char) 0x9746 }, // <CJK>
        { (char) 0xE8CB, (char) 0x9748 }, // <CJK>
        { (char) 0xE8CC, (char) 0x9742 }, // <CJK>
        { (char) 0xE8CD, (char) 0x9749 }, // <CJK>
        { (char) 0xE8CE, (char) 0x975C }, // <CJK>
        { (char) 0xE8CF, (char) 0x9760 }, // <CJK>
        { (char) 0xE8D0, (char) 0x9764 }, // <CJK>
        { (char) 0xE8D1, (char) 0x9766 }, // <CJK>
        { (char) 0xE8D2, (char) 0x9768 }, // <CJK>
        { (char) 0xE8D3, (char) 0x52D2 }, // <CJK>
        { (char) 0xE8D4, (char) 0x976B }, // <CJK>
        { (char) 0xE8D5, (char) 0x9771 }, // <CJK>
        { (char) 0xE8D6, (char) 0x9779 }, // <CJK>
        { (char) 0xE8D7, (char) 0x9785 }, // <CJK>
        { (char) 0xE8D8, (char) 0x977C }, // <CJK>
        { (char) 0xE8D9, (char) 0x9781 }, // <CJK>
        { (char) 0xE8DA, (char) 0x977A }, // <CJK>
        { (char) 0xE8DB, (char) 0x9786 }, // <CJK>
        { (char) 0xE8DC, (char) 0x978B }, // <CJK>
        { (char) 0xE8DD, (char) 0x978F }, // <CJK>
        { (char) 0xE8DE, (char) 0x9790 }, // <CJK>
        { (char) 0xE8DF, (char) 0x979C }, // <CJK>
        { (char) 0xE8E0, (char) 0x97A8 }, // <CJK>
        { (char) 0xE8E1, (char) 0x97A6 }, // <CJK>
        { (char) 0xE8E2, (char) 0x97A3 }, // <CJK>
        { (char) 0xE8E3, (char) 0x97B3 }, // <CJK>
        { (char) 0xE8E4, (char) 0x97B4 }, // <CJK>
        { (char) 0xE8E5, (char) 0x97C3 }, // <CJK>
        { (char) 0xE8E6, (char) 0x97C6 }, // <CJK>
        { (char) 0xE8E7, (char) 0x97C8 }, // <CJK>
        { (char) 0xE8E8, (char) 0x97CB }, // <CJK>
        { (char) 0xE8E9, (char) 0x97DC }, // <CJK>
        { (char) 0xE8EA, (char) 0x97ED }, // <CJK>
        { (char) 0xE8EB, (char) 0x9F4F }, // <CJK>
        { (char) 0xE8EC, (char) 0x97F2 }, // <CJK>
        { (char) 0xE8ED, (char) 0x7ADF }, // <CJK>
        { (char) 0xE8EE, (char) 0x97F6 }, // <CJK>
        { (char) 0xE8EF, (char) 0x97F5 }, // <CJK>
        { (char) 0xE8F0, (char) 0x980F }, // <CJK>
        { (char) 0xE8F1, (char) 0x980C }, // <CJK>
        { (char) 0xE8F2, (char) 0x9838 }, // <CJK>
        { (char) 0xE8F3, (char) 0x9824 }, // <CJK>
        { (char) 0xE8F4, (char) 0x9821 }, // <CJK>
        { (char) 0xE8F5, (char) 0x9837 }, // <CJK>
        { (char) 0xE8F6, (char) 0x983D }, // <CJK>
        { (char) 0xE8F7, (char) 0x9846 }, // <CJK>
        { (char) 0xE8F8, (char) 0x984F }, // <CJK>
        { (char) 0xE8F9, (char) 0x984B }, // <CJK>
        { (char) 0xE8FA, (char) 0x986B }, // <CJK>
        { (char) 0xE8FB, (char) 0x986F }, // <CJK>
        { (char) 0xE8FC, (char) 0x9870 }, // <CJK>
        { (char) 0xE940, (char) 0x9871 }, // <CJK>
        { (char) 0xE941, (char) 0x9874 }, // <CJK>
        { (char) 0xE942, (char) 0x9873 }, // <CJK>
        { (char) 0xE943, (char) 0x98AA }, // <CJK>
        { (char) 0xE944, (char) 0x98AF }, // <CJK>
        { (char) 0xE945, (char) 0x98B1 }, // <CJK>
        { (char) 0xE946, (char) 0x98B6 }, // <CJK>
        { (char) 0xE947, (char) 0x98C4 }, // <CJK>
        { (char) 0xE948, (char) 0x98C3 }, // <CJK>
        { (char) 0xE949, (char) 0x98C6 }, // <CJK>
        { (char) 0xE94A, (char) 0x98E9 }, // <CJK>
        { (char) 0xE94B, (char) 0x98EB }, // <CJK>
        { (char) 0xE94C, (char) 0x9903 }, // <CJK>
        { (char) 0xE94D, (char) 0x9909 }, // <CJK>
        { (char) 0xE94E, (char) 0x9912 }, // <CJK>
        { (char) 0xE94F, (char) 0x9914 }, // <CJK>
        { (char) 0xE950, (char) 0x9918 }, // <CJK>
        { (char) 0xE951, (char) 0x9921 }, // <CJK>
        { (char) 0xE952, (char) 0x991D }, // <CJK>
        { (char) 0xE953, (char) 0x991E }, // <CJK>
        { (char) 0xE954, (char) 0x9924 }, // <CJK>
        { (char) 0xE955, (char) 0x9920 }, // <CJK>
        { (char) 0xE956, (char) 0x992C }, // <CJK>
        { (char) 0xE957, (char) 0x992E }, // <CJK>
        { (char) 0xE958, (char) 0x993D }, // <CJK>
        { (char) 0xE959, (char) 0x993E }, // <CJK>
        { (char) 0xE95A, (char) 0x9942 }, // <CJK>
        { (char) 0xE95B, (char) 0x9949 }, // <CJK>
        { (char) 0xE95C, (char) 0x9945 }, // <CJK>
        { (char) 0xE95D, (char) 0x9950 }, // <CJK>
        { (char) 0xE95E, (char) 0x994B }, // <CJK>
        { (char) 0xE95F, (char) 0x9951 }, // <CJK>
        { (char) 0xE960, (char) 0x9952 }, // <CJK>
        { (char) 0xE961, (char) 0x994C }, // <CJK>
        { (char) 0xE962, (char) 0x9955 }, // <CJK>
        { (char) 0xE963, (char) 0x9997 }, // <CJK>
        { (char) 0xE964, (char) 0x9998 }, // <CJK>
        { (char) 0xE965, (char) 0x99A5 }, // <CJK>
        { (char) 0xE966, (char) 0x99AD }, // <CJK>
        { (char) 0xE967, (char) 0x99AE }, // <CJK>
        { (char) 0xE968, (char) 0x99BC }, // <CJK>
        { (char) 0xE969, (char) 0x99DF }, // <CJK>
        { (char) 0xE96A, (char) 0x99DB }, // <CJK>
        { (char) 0xE96B, (char) 0x99DD }, // <CJK>
        { (char) 0xE96C, (char) 0x99D8 }, // <CJK>
        { (char) 0xE96D, (char) 0x99D1 }, // <CJK>
        { (char) 0xE96E, (char) 0x99ED }, // <CJK>
        { (char) 0xE96F, (char) 0x99EE }, // <CJK>
        { (char) 0xE970, (char) 0x99F1 }, // <CJK>
        { (char) 0xE971, (char) 0x99F2 }, // <CJK>
        { (char) 0xE972, (char) 0x99FB }, // <CJK>
        { (char) 0xE973, (char) 0x99F8 }, // <CJK>
        { (char) 0xE974, (char) 0x9A01 }, // <CJK>
        { (char) 0xE975, (char) 0x9A0F }, // <CJK>
        { (char) 0xE976, (char) 0x9A05 }, // <CJK>
        { (char) 0xE977, (char) 0x99E2 }, // <CJK>
        { (char) 0xE978, (char) 0x9A19 }, // <CJK>
        { (char) 0xE979, (char) 0x9A2B }, // <CJK>
        { (char) 0xE97A, (char) 0x9A37 }, // <CJK>
        { (char) 0xE97B, (char) 0x9A45 }, // <CJK>
        { (char) 0xE97C, (char) 0x9A42 }, // <CJK>
        { (char) 0xE97D, (char) 0x9A40 }, // <CJK>
        { (char) 0xE97E, (char) 0x9A43 }, // <CJK>
        { (char) 0xE980, (char) 0x9A3E }, // <CJK>
        { (char) 0xE981, (char) 0x9A55 }, // <CJK>
        { (char) 0xE982, (char) 0x9A4D }, // <CJK>
        { (char) 0xE983, (char) 0x9A5B }, // <CJK>
        { (char) 0xE984, (char) 0x9A57 }, // <CJK>
        { (char) 0xE985, (char) 0x9A5F }, // <CJK>
        { (char) 0xE986, (char) 0x9A62 }, // <CJK>
        { (char) 0xE987, (char) 0x9A65 }, // <CJK>
        { (char) 0xE988, (char) 0x9A64 }, // <CJK>
        { (char) 0xE989, (char) 0x9A69 }, // <CJK>
        { (char) 0xE98A, (char) 0x9A6B }, // <CJK>
        { (char) 0xE98B, (char) 0x9A6A }, // <CJK>
        { (char) 0xE98C, (char) 0x9AAD }, // <CJK>
        { (char) 0xE98D, (char) 0x9AB0 }, // <CJK>
        { (char) 0xE98E, (char) 0x9ABC }, // <CJK>
        { (char) 0xE98F, (char) 0x9AC0 }, // <CJK>
        { (char) 0xE990, (char) 0x9ACF }, // <CJK>
        { (char) 0xE991, (char) 0x9AD1 }, // <CJK>
        { (char) 0xE992, (char) 0x9AD3 }, // <CJK>
        { (char) 0xE993, (char) 0x9AD4 }, // <CJK>
        { (char) 0xE994, (char) 0x9ADE }, // <CJK>
        { (char) 0xE995, (char) 0x9ADF }, // <CJK>
        { (char) 0xE996, (char) 0x9AE2 }, // <CJK>
        { (char) 0xE997, (char) 0x9AE3 }, // <CJK>
        { (char) 0xE998, (char) 0x9AE6 }, // <CJK>
        { (char) 0xE999, (char) 0x9AEF }, // <CJK>
        { (char) 0xE99A, (char) 0x9AEB }, // <CJK>
        { (char) 0xE99B, (char) 0x9AEE }, // <CJK>
        { (char) 0xE99C, (char) 0x9AF4 }, // <CJK>
        { (char) 0xE99D, (char) 0x9AF1 }, // <CJK>
        { (char) 0xE99E, (char) 0x9AF7 }, // <CJK>
        { (char) 0xE99F, (char) 0x9AFB }, // <CJK>
        { (char) 0xE9A0, (char) 0x9B06 }, // <CJK>
        { (char) 0xE9A1, (char) 0x9B18 }, // <CJK>
        { (char) 0xE9A2, (char) 0x9B1A }, // <CJK>
        { (char) 0xE9A3, (char) 0x9B1F }, // <CJK>
        { (char) 0xE9A4, (char) 0x9B22 }, // <CJK>
        { (char) 0xE9A5, (char) 0x9B23 }, // <CJK>
        { (char) 0xE9A6, (char) 0x9B25 }, // <CJK>
        { (char) 0xE9A7, (char) 0x9B27 }, // <CJK>
        { (char) 0xE9A8, (char) 0x9B28 }, // <CJK>
        { (char) 0xE9A9, (char) 0x9B29 }, // <CJK>
        { (char) 0xE9AA, (char) 0x9B2A }, // <CJK>
        { (char) 0xE9AB, (char) 0x9B2E }, // <CJK>
        { (char) 0xE9AC, (char) 0x9B2F }, // <CJK>
        { (char) 0xE9AD, (char) 0x9B32 }, // <CJK>
        { (char) 0xE9AE, (char) 0x9B44 }, // <CJK>
        { (char) 0xE9AF, (char) 0x9B43 }, // <CJK>
        { (char) 0xE9B0, (char) 0x9B4F }, // <CJK>
        { (char) 0xE9B1, (char) 0x9B4D }, // <CJK>
        { (char) 0xE9B2, (char) 0x9B4E }, // <CJK>
        { (char) 0xE9B3, (char) 0x9B51 }, // <CJK>
        { (char) 0xE9B4, (char) 0x9B58 }, // <CJK>
        { (char) 0xE9B5, (char) 0x9B74 }, // <CJK>
        { (char) 0xE9B6, (char) 0x9B93 }, // <CJK>
        { (char) 0xE9B7, (char) 0x9B83 }, // <CJK>
        { (char) 0xE9B8, (char) 0x9B91 }, // <CJK>
        { (char) 0xE9B9, (char) 0x9B96 }, // <CJK>
        { (char) 0xE9BA, (char) 0x9B97 }, // <CJK>
        { (char) 0xE9BB, (char) 0x9B9F }, // <CJK>
        { (char) 0xE9BC, (char) 0x9BA0 }, // <CJK>
        { (char) 0xE9BD, (char) 0x9BA8 }, // <CJK>
        { (char) 0xE9BE, (char) 0x9BB4 }, // <CJK>
        { (char) 0xE9BF, (char) 0x9BC0 }, // <CJK>
        { (char) 0xE9C0, (char) 0x9BCA }, // <CJK>
        { (char) 0xE9C1, (char) 0x9BB9 }, // <CJK>
        { (char) 0xE9C2, (char) 0x9BC6 }, // <CJK>
        { (char) 0xE9C3, (char) 0x9BCF }, // <CJK>
        { (char) 0xE9C4, (char) 0x9BD1 }, // <CJK>
        { (char) 0xE9C5, (char) 0x9BD2 }, // <CJK>
        { (char) 0xE9C6, (char) 0x9BE3 }, // <CJK>
        { (char) 0xE9C7, (char) 0x9BE2 }, // <CJK>
        { (char) 0xE9C8, (char) 0x9BE4 }, // <CJK>
        { (char) 0xE9C9, (char) 0x9BD4 }, // <CJK>
        { (char) 0xE9CA, (char) 0x9BE1 }, // <CJK>
        { (char) 0xE9CB, (char) 0x9C3A }, // <CJK>
        { (char) 0xE9CC, (char) 0x9BF2 }, // <CJK>
        { (char) 0xE9CD, (char) 0x9BF1 }, // <CJK>
        { (char) 0xE9CE, (char) 0x9BF0 }, // <CJK>
        { (char) 0xE9CF, (char) 0x9C15 }, // <CJK>
        { (char) 0xE9D0, (char) 0x9C14 }, // <CJK>
        { (char) 0xE9D1, (char) 0x9C09 }, // <CJK>
        { (char) 0xE9D2, (char) 0x9C13 }, // <CJK>
        { (char) 0xE9D3, (char) 0x9C0C }, // <CJK>
        { (char) 0xE9D4, (char) 0x9C06 }, // <CJK>
        { (char) 0xE9D5, (char) 0x9C08 }, // <CJK>
        { (char) 0xE9D6, (char) 0x9C12 }, // <CJK>
        { (char) 0xE9D7, (char) 0x9C0A }, // <CJK>
        { (char) 0xE9D8, (char) 0x9C04 }, // <CJK>
        { (char) 0xE9D9, (char) 0x9C2E }, // <CJK>
        { (char) 0xE9DA, (char) 0x9C1B }, // <CJK>
        { (char) 0xE9DB, (char) 0x9C25 }, // <CJK>
        { (char) 0xE9DC, (char) 0x9C24 }, // <CJK>
        { (char) 0xE9DD, (char) 0x9C21 }, // <CJK>
        { (char) 0xE9DE, (char) 0x9C30 }, // <CJK>
        { (char) 0xE9DF, (char) 0x9C47 }, // <CJK>
        { (char) 0xE9E0, (char) 0x9C32 }, // <CJK>
        { (char) 0xE9E1, (char) 0x9C46 }, // <CJK>
        { (char) 0xE9E2, (char) 0x9C3E }, // <CJK>
        { (char) 0xE9E3, (char) 0x9C5A }, // <CJK>
        { (char) 0xE9E4, (char) 0x9C60 }, // <CJK>
        { (char) 0xE9E5, (char) 0x9C67 }, // <CJK>
        { (char) 0xE9E6, (char) 0x9C76 }, // <CJK>
        { (char) 0xE9E7, (char) 0x9C78 }, // <CJK>
        { (char) 0xE9E8, (char) 0x9CE7 }, // <CJK>
        { (char) 0xE9E9, (char) 0x9CEC }, // <CJK>
        { (char) 0xE9EA, (char) 0x9CF0 }, // <CJK>
        { (char) 0xE9EB, (char) 0x9D09 }, // <CJK>
        { (char) 0xE9EC, (char) 0x9D08 }, // <CJK>
        { (char) 0xE9ED, (char) 0x9CEB }, // <CJK>
        { (char) 0xE9EE, (char) 0x9D03 }, // <CJK>
        { (char) 0xE9EF, (char) 0x9D06 }, // <CJK>
        { (char) 0xE9F0, (char) 0x9D2A }, // <CJK>
        { (char) 0xE9F1, (char) 0x9D26 }, // <CJK>
        { (char) 0xE9F2, (char) 0x9DAF }, // <CJK>
        { (char) 0xE9F3, (char) 0x9D23 }, // <CJK>
        { (char) 0xE9F4, (char) 0x9D1F }, // <CJK>
        { (char) 0xE9F5, (char) 0x9D44 }, // <CJK>
        { (char) 0xE9F6, (char) 0x9D15 }, // <CJK>
        { (char) 0xE9F7, (char) 0x9D12 }, // <CJK>
        { (char) 0xE9F8, (char) 0x9D41 }, // <CJK>
        { (char) 0xE9F9, (char) 0x9D3F }, // <CJK>
        { (char) 0xE9FA, (char) 0x9D3E }, // <CJK>
        { (char) 0xE9FB, (char) 0x9D46 }, // <CJK>
        { (char) 0xE9FC, (char) 0x9D48 }, // <CJK>
        { (char) 0xEA40, (char) 0x9D5D }, // <CJK>
        { (char) 0xEA41, (char) 0x9D5E }, // <CJK>
        { (char) 0xEA42, (char) 0x9D64 }, // <CJK>
        { (char) 0xEA43, (char) 0x9D51 }, // <CJK>
        { (char) 0xEA44, (char) 0x9D50 }, // <CJK>
        { (char) 0xEA45, (char) 0x9D59 }, // <CJK>
        { (char) 0xEA46, (char) 0x9D72 }, // <CJK>
        { (char) 0xEA47, (char) 0x9D89 }, // <CJK>
        { (char) 0xEA48, (char) 0x9D87 }, // <CJK>
        { (char) 0xEA49, (char) 0x9DAB }, // <CJK>
        { (char) 0xEA4A, (char) 0x9D6F }, // <CJK>
        { (char) 0xEA4B, (char) 0x9D7A }, // <CJK>
        { (char) 0xEA4C, (char) 0x9D9A }, // <CJK>
        { (char) 0xEA4D, (char) 0x9DA4 }, // <CJK>
        { (char) 0xEA4E, (char) 0x9DA9 }, // <CJK>
        { (char) 0xEA4F, (char) 0x9DB2 }, // <CJK>
        { (char) 0xEA50, (char) 0x9DC4 }, // <CJK>
        { (char) 0xEA51, (char) 0x9DC1 }, // <CJK>
        { (char) 0xEA52, (char) 0x9DBB }, // <CJK>
        { (char) 0xEA53, (char) 0x9DB8 }, // <CJK>
        { (char) 0xEA54, (char) 0x9DBA }, // <CJK>
        { (char) 0xEA55, (char) 0x9DC6 }, // <CJK>
        { (char) 0xEA56, (char) 0x9DCF }, // <CJK>
        { (char) 0xEA57, (char) 0x9DC2 }, // <CJK>
        { (char) 0xEA58, (char) 0x9DD9 }, // <CJK>
        { (char) 0xEA59, (char) 0x9DD3 }, // <CJK>
        { (char) 0xEA5A, (char) 0x9DF8 }, // <CJK>
        { (char) 0xEA5B, (char) 0x9DE6 }, // <CJK>
        { (char) 0xEA5C, (char) 0x9DED }, // <CJK>
        { (char) 0xEA5D, (char) 0x9DEF }, // <CJK>
        { (char) 0xEA5E, (char) 0x9DFD }, // <CJK>
        { (char) 0xEA5F, (char) 0x9E1A }, // <CJK>
        { (char) 0xEA60, (char) 0x9E1B }, // <CJK>
        { (char) 0xEA61, (char) 0x9E1E }, // <CJK>
        { (char) 0xEA62, (char) 0x9E75 }, // <CJK>
        { (char) 0xEA63, (char) 0x9E79 }, // <CJK>
        { (char) 0xEA64, (char) 0x9E7D }, // <CJK>
        { (char) 0xEA65, (char) 0x9E81 }, // <CJK>
        { (char) 0xEA66, (char) 0x9E88 }, // <CJK>
        { (char) 0xEA67, (char) 0x9E8B }, // <CJK>
        { (char) 0xEA68, (char) 0x9E8C }, // <CJK>
        { (char) 0xEA69, (char) 0x9E92 }, // <CJK>
        { (char) 0xEA6A, (char) 0x9E95 }, // <CJK>
        { (char) 0xEA6B, (char) 0x9E91 }, // <CJK>
        { (char) 0xEA6C, (char) 0x9E9D }, // <CJK>
        { (char) 0xEA6D, (char) 0x9EA5 }, // <CJK>
        { (char) 0xEA6E, (char) 0x9EA9 }, // <CJK>
        { (char) 0xEA6F, (char) 0x9EB8 }, // <CJK>
        { (char) 0xEA70, (char) 0x9EAA }, // <CJK>
        { (char) 0xEA71, (char) 0x9EAD }, // <CJK>
        { (char) 0xEA72, (char) 0x9761 }, // <CJK>
        { (char) 0xEA73, (char) 0x9ECC }, // <CJK>
        { (char) 0xEA74, (char) 0x9ECE }, // <CJK>
        { (char) 0xEA75, (char) 0x9ECF }, // <CJK>
        { (char) 0xEA76, (char) 0x9ED0 }, // <CJK>
        { (char) 0xEA77, (char) 0x9ED4 }, // <CJK>
        { (char) 0xEA78, (char) 0x9EDC }, // <CJK>
        { (char) 0xEA79, (char) 0x9EDE }, // <CJK>
        { (char) 0xEA7A, (char) 0x9EDD }, // <CJK>
        { (char) 0xEA7B, (char) 0x9EE0 }, // <CJK>
        { (char) 0xEA7C, (char) 0x9EE5 }, // <CJK>
        { (char) 0xEA7D, (char) 0x9EE8 }, // <CJK>
        { (char) 0xEA7E, (char) 0x9EEF }, // <CJK>
        { (char) 0xEA80, (char) 0x9EF4 }, // <CJK>
        { (char) 0xEA81, (char) 0x9EF6 }, // <CJK>
        { (char) 0xEA82, (char) 0x9EF7 }, // <CJK>
        { (char) 0xEA83, (char) 0x9EF9 }, // <CJK>
        { (char) 0xEA84, (char) 0x9EFB }, // <CJK>
        { (char) 0xEA85, (char) 0x9EFC }, // <CJK>
        { (char) 0xEA86, (char) 0x9EFD }, // <CJK>
        { (char) 0xEA87, (char) 0x9F07 }, // <CJK>
        { (char) 0xEA88, (char) 0x9F08 }, // <CJK>
        { (char) 0xEA89, (char) 0x76B7 }, // <CJK>
        { (char) 0xEA8A, (char) 0x9F15 }, // <CJK>
        { (char) 0xEA8B, (char) 0x9F21 }, // <CJK>
        { (char) 0xEA8C, (char) 0x9F2C }, // <CJK>
        { (char) 0xEA8D, (char) 0x9F3E }, // <CJK>
        { (char) 0xEA8E, (char) 0x9F4A }, // <CJK>
        { (char) 0xEA8F, (char) 0x9F52 }, // <CJK>
        { (char) 0xEA90, (char) 0x9F54 }, // <CJK>
        { (char) 0xEA91, (char) 0x9F63 }, // <CJK>
        { (char) 0xEA92, (char) 0x9F5F }, // <CJK>
        { (char) 0xEA93, (char) 0x9F60 }, // <CJK>
        { (char) 0xEA94, (char) 0x9F61 }, // <CJK>
        { (char) 0xEA95, (char) 0x9F66 }, // <CJK>
        { (char) 0xEA96, (char) 0x9F67 }, // <CJK>
        { (char) 0xEA97, (char) 0x9F6C }, // <CJK>
        { (char) 0xEA98, (char) 0x9F6A }, // <CJK>
        { (char) 0xEA99, (char) 0x9F77 }, // <CJK>
        { (char) 0xEA9A, (char) 0x9F72 }, // <CJK>
        { (char) 0xEA9B, (char) 0x9F76 }, // <CJK>
        { (char) 0xEA9C, (char) 0x9F95 }, // <CJK>
        { (char) 0xEA9D, (char) 0x9F9C }, // <CJK>
        { (char) 0xEA9E, (char) 0x9FA0 }, // <CJK>
        { (char) 0xEA9F, (char) 0x582F }, // <CJK>
        { (char) 0xEAA0, (char) 0x69C7 }, // <CJK>
        { (char) 0xEAA1, (char) 0x9059 }, // <CJK>
        { (char) 0xEAA2, (char) 0x7464 }, // <CJK>
        { (char) 0xEAA3, (char) 0x51DC }, // <CJK>
        { (char) 0xEAA4, (char) 0x7199 }, // <CJK>

        // Apple additions - vertical forms
        { (char) 0xEB41, (char) 0x3001, (char) 0xF87E }, // vertical form for IDEOGRAPHIC COMMA
        { (char) 0xEB42, (char) 0x3002, (char) 0xF87E }, // vertical form for IDEOGRAPHIC FULL STOP
        { (char) 0xEB50, (char) 0xFFE3, (char) 0xF87E }, // vertical form for FULLWIDTH MACRON
        { (char) 0xEB51, (char) 0xFE33 }, // PRESENTATION FORM FOR VERTICAL LOW LINE, U+FF3F
        { (char) 0xEB5B, (char) 0x30FC, (char) 0xF87E }, // vertical form for KATAKANA-HIRAGANA PROLONGED SOUND MARK
        { (char) 0xEB5C, (char) 0xFE31 }, // PRESENTATION FORM FOR VERTICAL EM DASH, U+2014
        { (char) 0xEB5D, (char) 0x2010, (char) 0xF87E }, // vertical form for HYPHEN
        { (char) 0xEB60, (char) 0x301C, (char) 0xF87E }, // vertical form for WAVE DASH
        { (char) 0xEB61, (char) 0x2016, (char) 0xF87E }, // vertical form for DOUBLE VERTICAL LINE
        { (char) 0xEB62, (char) 0xFF5C, (char) 0xF87E }, // vertical form for FULLWIDTH VERTICAL LINE
        { (char) 0xEB63, (char) 0x2026, (char) 0xF87E }, // vertical form for HORIZONTAL ELLIPSIS
        { (char) 0xEB64, (char) 0xFE30 }, // PRESENTATION FORM FOR VERTICAL TWO DOT LEADER, U+2025
        { (char) 0xEB69, (char) 0xFE35 }, // PRESENTATION FORM FOR VERTICAL LEFT PARENTHESIS, U+FF08
        { (char) 0xEB6A, (char) 0xFE36 }, // PRESENTATION FORM FOR VERTICAL RIGHT PARENTHESIS, U+FF09
        { (char) 0xEB6B, (char) 0xFE39 }, // PRESENTATION FORM FOR VERTICAL LEFT TORTOISE SHELL BRACKET, U+3014
        { (char) 0xEB6C, (char) 0xFE3A }, // PRESENTATION FORM FOR VERTICAL RIGHT TORTOISE SHELL BRACKET, U+3015
        { (char) 0xEB6D, (char) 0xFF3B, (char) 0xF87E }, // vertical form for FULLWIDTH LEFT SQUARE BRACKET # or for Unicode 4.0, (char) 0xFE47 PRESENTATION FORM FOR VERTICAL LEFT SQUARE BRACKET
        { (char) 0xEB6E, (char) 0xFF3D, (char) 0xF87E }, // vertical form for FULLWIDTH RIGHT SQUARE BRACKET # or for Unicode 4.0, (char) 0xFE48 PRESENTATION FORM FOR VERTICAL RIGHT SQUARE BRACKET
        { (char) 0xEB6F, (char) 0xFE37 }, // PRESENTATION FORM FOR VERTICAL LEFT CURLY BRACKET, U+FF5B
        { (char) 0xEB70, (char) 0xFE38 }, // PRESENTATION FORM FOR VERTICAL RIGHT CURLY BRACKET, U+FF5D
        { (char) 0xEB71, (char) 0xFE3F }, // PRESENTATION FORM FOR VERTICAL LEFT ANGLE BRACKET, U+3008
        { (char) 0xEB72, (char) 0xFE40 }, // PRESENTATION FORM FOR VERTICAL RIGHT ANGLE BRACKET, U+3009
        { (char) 0xEB73, (char) 0xFE3D }, // PRESENTATION FORM FOR VERTICAL LEFT DOUBLE ANGLE BRACKET, U+300A
        { (char) 0xEB74, (char) 0xFE3E }, // PRESENTATION FORM FOR VERTICAL RIGHT DOUBLE ANGLE BRACKET, U+300B
        { (char) 0xEB75, (char) 0xFE41 }, // PRESENTATION FORM FOR VERTICAL LEFT CORNER BRACKET, U+300C
        { (char) 0xEB76, (char) 0xFE42 }, // PRESENTATION FORM FOR VERTICAL RIGHT CORNER BRACKET, U+300D
        { (char) 0xEB77, (char) 0xFE43 }, // PRESENTATION FORM FOR VERTICAL LEFT WHITE CORNER BRACKET, U+300E
        { (char) 0xEB78, (char) 0xFE44 }, // PRESENTATION FORM FOR VERTICAL RIGHT WHITE CORNER BRACKET, U+300F
        { (char) 0xEB79, (char) 0xFE3B }, // PRESENTATION FORM FOR VERTICAL LEFT BLACK LENTICULAR BRACKET, U+3010
        { (char) 0xEB7A, (char) 0xFE3C }, // PRESENTATION FORM FOR VERTICAL RIGHT BLACK LENTICULAR BRACKET, U+3011
        { (char) 0xEB81, (char) 0xFF1D, (char) 0xF87E }, // vertical form for FULLWIDTH EQUALS SIGN
        { (char) 0xEC9F, (char) 0x3041, (char) 0xF87E }, // vertical form for HIRAGANA LETTER SMALL A
        { (char) 0xECA1, (char) 0x3043, (char) 0xF87E }, // vertical form for HIRAGANA LETTER SMALL I
        { (char) 0xECA3, (char) 0x3045, (char) 0xF87E }, // vertical form for HIRAGANA LETTER SMALL U
        { (char) 0xECA5, (char) 0x3047, (char) 0xF87E }, // vertical form for HIRAGANA LETTER SMALL E
        { (char) 0xECA7, (char) 0x3049, (char) 0xF87E }, // vertical form for HIRAGANA LETTER SMALL O
        { (char) 0xECC1, (char) 0x3063, (char) 0xF87E }, // vertical form for HIRAGANA LETTER SMALL TU
        { (char) 0xECE1, (char) 0x3083, (char) 0xF87E }, // vertical form for HIRAGANA LETTER SMALL YA
        { (char) 0xECE3, (char) 0x3085, (char) 0xF87E }, // vertical form for HIRAGANA LETTER SMALL YU
        { (char) 0xECE5, (char) 0x3087, (char) 0xF87E }, // vertical form for HIRAGANA LETTER SMALL YO
        { (char) 0xECEC, (char) 0x308E, (char) 0xF87E }, // vertical form for HIRAGANA LETTER SMALL WA
        { (char) 0xED40, (char) 0x30A1, (char) 0xF87E }, // vertical form for KATAKANA LETTER SMALL A
        { (char) 0xED42, (char) 0x30A3, (char) 0xF87E }, // vertical form for KATAKANA LETTER SMALL I
        { (char) 0xED44, (char) 0x30A5, (char) 0xF87E }, // vertical form for KATAKANA LETTER SMALL U
        { (char) 0xED46, (char) 0x30A7, (char) 0xF87E }, // vertical form for KATAKANA LETTER SMALL E
        { (char) 0xED48, (char) 0x30A9, (char) 0xF87E }, // vertical form for KATAKANA LETTER SMALL O
        { (char) 0xED62, (char) 0x30C3, (char) 0xF87E }, // vertical form for KATAKANA LETTER SMALL TU
        { (char) 0xED83, (char) 0x30E3, (char) 0xF87E }, // vertical form for KATAKANA LETTER SMALL YA
        { (char) 0xED85, (char) 0x30E5, (char) 0xF87E }, // vertical form for KATAKANA LETTER SMALL YU
        { (char) 0xED87, (char) 0x30E7, (char) 0xF87E }, // vertical form for KATAKANA LETTER SMALL YO
        { (char) 0xED8E, (char) 0x30EE, (char) 0xF87E }, // vertical form for KATAKANA LETTER SMALL WA
        { (char) 0xED95, (char) 0x30F5, (char) 0xF87E }, // vertical form for KATAKANA LETTER SMALL KA
        { (char) 0xED96, (char) 0x30F6, (char) 0xF87E }, // vertical form for KATAKANA LETTER SMALL KE
    };
}
