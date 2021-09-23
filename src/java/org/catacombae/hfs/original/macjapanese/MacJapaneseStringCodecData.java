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
 * Data for @ref MacJapaneseStringCodec (part 1).
 *
 * @author <a href="https://catacombae.org" target="_top">Erik Larsson</a>
 */
class MacJapaneseStringCodecData {
    /**
     * Mapping table from MacJapanese to Unicode (part 1).
     *
     * Adapted from the Unicode document:
     *   https://unicode.org/Public/MAPPINGS/VENDORS/APPLE/JAPANESE.TXT
     *
     * Due to code size restrictions the table is split into three class files.
     */
    static final char[][] mappingTable = {
        { (char) 0x20, (char) 0x0020 }, // SPACE
        { (char) 0x21, (char) 0x0021 }, // EXCLAMATION MARK
        { (char) 0x22, (char) 0x0022 }, // QUOTATION MARK
        { (char) 0x23, (char) 0x0023 }, // NUMBER SIGN
        { (char) 0x24, (char) 0x0024 }, // DOLLAR SIGN
        { (char) 0x25, (char) 0x0025 }, // PERCENT SIGN
        { (char) 0x26, (char) 0x0026 }, // AMPERSAND
        { (char) 0x27, (char) 0x0027 }, // APOSTROPHE
        { (char) 0x28, (char) 0x0028 }, // LEFT PARENTHESIS
        { (char) 0x29, (char) 0x0029 }, // RIGHT PARENTHESIS
        { (char) 0x2A, (char) 0x002A }, // ASTERISK
        { (char) 0x2B, (char) 0x002B }, // PLUS SIGN
        { (char) 0x2C, (char) 0x002C }, // COMMA
        { (char) 0x2D, (char) 0x002D }, // HYPHEN-MINUS
        { (char) 0x2E, (char) 0x002E }, // FULL STOP
        { (char) 0x2F, (char) 0x002F }, // SOLIDUS
        { (char) 0x30, (char) 0x0030 }, // DIGIT ZERO
        { (char) 0x31, (char) 0x0031 }, // DIGIT ONE
        { (char) 0x32, (char) 0x0032 }, // DIGIT TWO
        { (char) 0x33, (char) 0x0033 }, // DIGIT THREE
        { (char) 0x34, (char) 0x0034 }, // DIGIT FOUR
        { (char) 0x35, (char) 0x0035 }, // DIGIT FIVE
        { (char) 0x36, (char) 0x0036 }, // DIGIT SIX
        { (char) 0x37, (char) 0x0037 }, // DIGIT SEVEN
        { (char) 0x38, (char) 0x0038 }, // DIGIT EIGHT
        { (char) 0x39, (char) 0x0039 }, // DIGIT NINE
        { (char) 0x3A, (char) 0x003A }, // COLON
        { (char) 0x3B, (char) 0x003B }, // SEMICOLON
        { (char) 0x3C, (char) 0x003C }, // LESS-THAN SIGN
        { (char) 0x3D, (char) 0x003D }, // EQUALS SIGN
        { (char) 0x3E, (char) 0x003E }, // GREATER-THAN SIGN
        { (char) 0x3F, (char) 0x003F }, // QUESTION MARK
        { (char) 0x40, (char) 0x0040 }, // COMMERCIAL AT
        { (char) 0x41, (char) 0x0041 }, // LATIN CAPITAL LETTER A
        { (char) 0x42, (char) 0x0042 }, // LATIN CAPITAL LETTER B
        { (char) 0x43, (char) 0x0043 }, // LATIN CAPITAL LETTER C
        { (char) 0x44, (char) 0x0044 }, // LATIN CAPITAL LETTER D
        { (char) 0x45, (char) 0x0045 }, // LATIN CAPITAL LETTER E
        { (char) 0x46, (char) 0x0046 }, // LATIN CAPITAL LETTER F
        { (char) 0x47, (char) 0x0047 }, // LATIN CAPITAL LETTER G
        { (char) 0x48, (char) 0x0048 }, // LATIN CAPITAL LETTER H
        { (char) 0x49, (char) 0x0049 }, // LATIN CAPITAL LETTER I
        { (char) 0x4A, (char) 0x004A }, // LATIN CAPITAL LETTER J
        { (char) 0x4B, (char) 0x004B }, // LATIN CAPITAL LETTER K
        { (char) 0x4C, (char) 0x004C }, // LATIN CAPITAL LETTER L
        { (char) 0x4D, (char) 0x004D }, // LATIN CAPITAL LETTER M
        { (char) 0x4E, (char) 0x004E }, // LATIN CAPITAL LETTER N
        { (char) 0x4F, (char) 0x004F }, // LATIN CAPITAL LETTER O
        { (char) 0x50, (char) 0x0050 }, // LATIN CAPITAL LETTER P
        { (char) 0x51, (char) 0x0051 }, // LATIN CAPITAL LETTER Q
        { (char) 0x52, (char) 0x0052 }, // LATIN CAPITAL LETTER R
        { (char) 0x53, (char) 0x0053 }, // LATIN CAPITAL LETTER S
        { (char) 0x54, (char) 0x0054 }, // LATIN CAPITAL LETTER T
        { (char) 0x55, (char) 0x0055 }, // LATIN CAPITAL LETTER U
        { (char) 0x56, (char) 0x0056 }, // LATIN CAPITAL LETTER V
        { (char) 0x57, (char) 0x0057 }, // LATIN CAPITAL LETTER W
        { (char) 0x58, (char) 0x0058 }, // LATIN CAPITAL LETTER X
        { (char) 0x59, (char) 0x0059 }, // LATIN CAPITAL LETTER Y
        { (char) 0x5A, (char) 0x005A }, // LATIN CAPITAL LETTER Z
        { (char) 0x5B, (char) 0x005B }, // LEFT SQUARE BRACKET
        { (char) 0x5C, (char) 0x00A5 }, // YEN SIGN
        { (char) 0x5D, (char) 0x005D }, // RIGHT SQUARE BRACKET
        { (char) 0x5E, (char) 0x005E }, // CIRCUMFLEX ACCENT
        { (char) 0x5F, (char) 0x005F }, // LOW LINE
        { (char) 0x60, (char) 0x0060 }, // GRAVE ACCENT
        { (char) 0x61, (char) 0x0061 }, // LATIN SMALL LETTER A
        { (char) 0x62, (char) 0x0062 }, // LATIN SMALL LETTER B
        { (char) 0x63, (char) 0x0063 }, // LATIN SMALL LETTER C
        { (char) 0x64, (char) 0x0064 }, // LATIN SMALL LETTER D
        { (char) 0x65, (char) 0x0065 }, // LATIN SMALL LETTER E
        { (char) 0x66, (char) 0x0066 }, // LATIN SMALL LETTER F
        { (char) 0x67, (char) 0x0067 }, // LATIN SMALL LETTER G
        { (char) 0x68, (char) 0x0068 }, // LATIN SMALL LETTER H
        { (char) 0x69, (char) 0x0069 }, // LATIN SMALL LETTER I
        { (char) 0x6A, (char) 0x006A }, // LATIN SMALL LETTER J
        { (char) 0x6B, (char) 0x006B }, // LATIN SMALL LETTER K
        { (char) 0x6C, (char) 0x006C }, // LATIN SMALL LETTER L
        { (char) 0x6D, (char) 0x006D }, // LATIN SMALL LETTER M
        { (char) 0x6E, (char) 0x006E }, // LATIN SMALL LETTER N
        { (char) 0x6F, (char) 0x006F }, // LATIN SMALL LETTER O
        { (char) 0x70, (char) 0x0070 }, // LATIN SMALL LETTER P
        { (char) 0x71, (char) 0x0071 }, // LATIN SMALL LETTER Q
        { (char) 0x72, (char) 0x0072 }, // LATIN SMALL LETTER R
        { (char) 0x73, (char) 0x0073 }, // LATIN SMALL LETTER S
        { (char) 0x74, (char) 0x0074 }, // LATIN SMALL LETTER T
        { (char) 0x75, (char) 0x0075 }, // LATIN SMALL LETTER U
        { (char) 0x76, (char) 0x0076 }, // LATIN SMALL LETTER V
        { (char) 0x77, (char) 0x0077 }, // LATIN SMALL LETTER W
        { (char) 0x78, (char) 0x0078 }, // LATIN SMALL LETTER X
        { (char) 0x79, (char) 0x0079 }, // LATIN SMALL LETTER Y
        { (char) 0x7A, (char) 0x007A }, // LATIN SMALL LETTER Z
        { (char) 0x7B, (char) 0x007B }, // LEFT CURLY BRACKET
        { (char) 0x7C, (char) 0x007C }, // VERTICAL LINE
        { (char) 0x7D, (char) 0x007D }, // RIGHT CURLY BRACKET
        { (char) 0x7E, (char) 0x007E }, // TILDE # Apple change from standard Shift-JIS
        { (char) 0x80, (char) 0x005C }, // REVERSE SOLIDUS # Apple addition; changes mapping of 0x815F
        { (char) 0xA0, (char) 0x00A0 }, // NO-BREAK SPACE # Apple addition
        { (char) 0xA1, (char) 0xFF61 }, // HALFWIDTH IDEOGRAPHIC FULL STOP
        { (char) 0xA2, (char) 0xFF62 }, // HALFWIDTH LEFT CORNER BRACKET
        { (char) 0xA3, (char) 0xFF63 }, // HALFWIDTH RIGHT CORNER BRACKET
        { (char) 0xA4, (char) 0xFF64 }, // HALFWIDTH IDEOGRAPHIC COMMA
        { (char) 0xA5, (char) 0xFF65 }, // HALFWIDTH KATAKANA MIDDLE DOT
        { (char) 0xA6, (char) 0xFF66 }, // HALFWIDTH KATAKANA LETTER WO
        { (char) 0xA7, (char) 0xFF67 }, // HALFWIDTH KATAKANA LETTER SMALL A
        { (char) 0xA8, (char) 0xFF68 }, // HALFWIDTH KATAKANA LETTER SMALL I
        { (char) 0xA9, (char) 0xFF69 }, // HALFWIDTH KATAKANA LETTER SMALL U
        { (char) 0xAA, (char) 0xFF6A }, // HALFWIDTH KATAKANA LETTER SMALL E
        { (char) 0xAB, (char) 0xFF6B }, // HALFWIDTH KATAKANA LETTER SMALL O
        { (char) 0xAC, (char) 0xFF6C }, // HALFWIDTH KATAKANA LETTER SMALL YA
        { (char) 0xAD, (char) 0xFF6D }, // HALFWIDTH KATAKANA LETTER SMALL YU
        { (char) 0xAE, (char) 0xFF6E }, // HALFWIDTH KATAKANA LETTER SMALL YO
        { (char) 0xAF, (char) 0xFF6F }, // HALFWIDTH KATAKANA LETTER SMALL TU
        { (char) 0xB0, (char) 0xFF70 }, // HALFWIDTH KATAKANA-HIRAGANA PROLONGED SOUND MARK
        { (char) 0xB1, (char) 0xFF71 }, // HALFWIDTH KATAKANA LETTER A
        { (char) 0xB2, (char) 0xFF72 }, // HALFWIDTH KATAKANA LETTER I
        { (char) 0xB3, (char) 0xFF73 }, // HALFWIDTH KATAKANA LETTER U
        { (char) 0xB4, (char) 0xFF74 }, // HALFWIDTH KATAKANA LETTER E
        { (char) 0xB5, (char) 0xFF75 }, // HALFWIDTH KATAKANA LETTER O
        { (char) 0xB6, (char) 0xFF76 }, // HALFWIDTH KATAKANA LETTER KA
        { (char) 0xB7, (char) 0xFF77 }, // HALFWIDTH KATAKANA LETTER KI
        { (char) 0xB8, (char) 0xFF78 }, // HALFWIDTH KATAKANA LETTER KU
        { (char) 0xB9, (char) 0xFF79 }, // HALFWIDTH KATAKANA LETTER KE
        { (char) 0xBA, (char) 0xFF7A }, // HALFWIDTH KATAKANA LETTER KO
        { (char) 0xBB, (char) 0xFF7B }, // HALFWIDTH KATAKANA LETTER SA
        { (char) 0xBC, (char) 0xFF7C }, // HALFWIDTH KATAKANA LETTER SI
        { (char) 0xBD, (char) 0xFF7D }, // HALFWIDTH KATAKANA LETTER SU
        { (char) 0xBE, (char) 0xFF7E }, // HALFWIDTH KATAKANA LETTER SE
        { (char) 0xBF, (char) 0xFF7F }, // HALFWIDTH KATAKANA LETTER SO
        { (char) 0xC0, (char) 0xFF80 }, // HALFWIDTH KATAKANA LETTER TA
        { (char) 0xC1, (char) 0xFF81 }, // HALFWIDTH KATAKANA LETTER TI
        { (char) 0xC2, (char) 0xFF82 }, // HALFWIDTH KATAKANA LETTER TU
        { (char) 0xC3, (char) 0xFF83 }, // HALFWIDTH KATAKANA LETTER TE
        { (char) 0xC4, (char) 0xFF84 }, // HALFWIDTH KATAKANA LETTER TO
        { (char) 0xC5, (char) 0xFF85 }, // HALFWIDTH KATAKANA LETTER NA
        { (char) 0xC6, (char) 0xFF86 }, // HALFWIDTH KATAKANA LETTER NI
        { (char) 0xC7, (char) 0xFF87 }, // HALFWIDTH KATAKANA LETTER NU
        { (char) 0xC8, (char) 0xFF88 }, // HALFWIDTH KATAKANA LETTER NE
        { (char) 0xC9, (char) 0xFF89 }, // HALFWIDTH KATAKANA LETTER NO
        { (char) 0xCA, (char) 0xFF8A }, // HALFWIDTH KATAKANA LETTER HA
        { (char) 0xCB, (char) 0xFF8B }, // HALFWIDTH KATAKANA LETTER HI
        { (char) 0xCC, (char) 0xFF8C }, // HALFWIDTH KATAKANA LETTER HU
        { (char) 0xCD, (char) 0xFF8D }, // HALFWIDTH KATAKANA LETTER HE
        { (char) 0xCE, (char) 0xFF8E }, // HALFWIDTH KATAKANA LETTER HO
        { (char) 0xCF, (char) 0xFF8F }, // HALFWIDTH KATAKANA LETTER MA
        { (char) 0xD0, (char) 0xFF90 }, // HALFWIDTH KATAKANA LETTER MI
        { (char) 0xD1, (char) 0xFF91 }, // HALFWIDTH KATAKANA LETTER MU
        { (char) 0xD2, (char) 0xFF92 }, // HALFWIDTH KATAKANA LETTER ME
        { (char) 0xD3, (char) 0xFF93 }, // HALFWIDTH KATAKANA LETTER MO
        { (char) 0xD4, (char) 0xFF94 }, // HALFWIDTH KATAKANA LETTER YA
        { (char) 0xD5, (char) 0xFF95 }, // HALFWIDTH KATAKANA LETTER YU
        { (char) 0xD6, (char) 0xFF96 }, // HALFWIDTH KATAKANA LETTER YO
        { (char) 0xD7, (char) 0xFF97 }, // HALFWIDTH KATAKANA LETTER RA
        { (char) 0xD8, (char) 0xFF98 }, // HALFWIDTH KATAKANA LETTER RI
        { (char) 0xD9, (char) 0xFF99 }, // HALFWIDTH KATAKANA LETTER RU
        { (char) 0xDA, (char) 0xFF9A }, // HALFWIDTH KATAKANA LETTER RE
        { (char) 0xDB, (char) 0xFF9B }, // HALFWIDTH KATAKANA LETTER RO
        { (char) 0xDC, (char) 0xFF9C }, // HALFWIDTH KATAKANA LETTER WA
        { (char) 0xDD, (char) 0xFF9D }, // HALFWIDTH KATAKANA LETTER N
        { (char) 0xDE, (char) 0xFF9E }, // HALFWIDTH KATAKANA VOICED SOUND MARK
        { (char) 0xDF, (char) 0xFF9F }, // HALFWIDTH KATAKANA SEMI-VOICED SOUND MARK
        { (char) 0xFD, (char) 0x00A9 }, // COPYRIGHT SIGN # Apple addition
        { (char) 0xFE, (char) 0x2122 }, // TRADE MARK SIGN # Apple addition
        { (char) 0xFF, (char) 0x2026, (char) 0xF87F }, // halfwidth horizontal ellipsis # Apple addition
        { (char) 0x8140, (char) 0x3000 }, // IDEOGRAPHIC SPACE
        { (char) 0x8141, (char) 0x3001 }, // IDEOGRAPHIC COMMA
        { (char) 0x8142, (char) 0x3002 }, // IDEOGRAPHIC FULL STOP
        { (char) 0x8143, (char) 0xFF0C }, // FULLWIDTH COMMA
        { (char) 0x8144, (char) 0xFF0E }, // FULLWIDTH FULL STOP
        { (char) 0x8145, (char) 0x30FB }, // KATAKANA MIDDLE DOT
        { (char) 0x8146, (char) 0xFF1A }, // FULLWIDTH COLON
        { (char) 0x8147, (char) 0xFF1B }, // FULLWIDTH SEMICOLON
        { (char) 0x8148, (char) 0xFF1F }, // FULLWIDTH QUESTION MARK
        { (char) 0x8149, (char) 0xFF01 }, // FULLWIDTH EXCLAMATION MARK
        { (char) 0x814A, (char) 0x309B }, // KATAKANA-HIRAGANA VOICED SOUND MARK
        { (char) 0x814B, (char) 0x309C }, // KATAKANA-HIRAGANA SEMI-VOICED SOUND MARK
        { (char) 0x814C, (char) 0x00B4 }, // ACUTE ACCENT
        { (char) 0x814D, (char) 0xFF40 }, // FULLWIDTH GRAVE ACCENT
        { (char) 0x814E, (char) 0x00A8 }, // DIAERESIS
        { (char) 0x814F, (char) 0xFF3E }, // FULLWIDTH CIRCUMFLEX ACCENT
        { (char) 0x8150, (char) 0xFFE3 }, // FULLWIDTH MACRON
        { (char) 0x8151, (char) 0xFF3F }, // FULLWIDTH LOW LINE
        { (char) 0x8152, (char) 0x30FD }, // KATAKANA ITERATION MARK
        { (char) 0x8153, (char) 0x30FE }, // KATAKANA VOICED ITERATION MARK
        { (char) 0x8154, (char) 0x309D }, // HIRAGANA ITERATION MARK
        { (char) 0x8155, (char) 0x309E }, // HIRAGANA VOICED ITERATION MARK
        { (char) 0x8156, (char) 0x3003 }, // DITTO MARK
        { (char) 0x8157, (char) 0x4EDD }, // <CJK>
        { (char) 0x8158, (char) 0x3005 }, // IDEOGRAPHIC ITERATION MARK
        { (char) 0x8159, (char) 0x3006 }, // IDEOGRAPHIC CLOSING MARK
        { (char) 0x815A, (char) 0x3007 }, // IDEOGRAPHIC NUMBER ZERO
        { (char) 0x815B, (char) 0x30FC }, // KATAKANA-HIRAGANA PROLONGED SOUND MARK
        { (char) 0x815C, (char) 0x2014 }, // EM DASH # change UTC mapping to match JIS spec
        { (char) 0x815D, (char) 0x2010 }, // HYPHEN
        { (char) 0x815E, (char) 0xFF0F }, // FULLWIDTH SOLIDUS
        { (char) 0x815F, (char) 0xFF3C }, // FULLWIDTH REVERSE SOLIDUS # change UTC mapping to separate from 0x80
        { (char) 0x8160, (char) 0x301C }, // WAVE DASH
        { (char) 0x8161, (char) 0x2016 }, // DOUBLE VERTICAL LINE
        { (char) 0x8162, (char) 0xFF5C }, // FULLWIDTH VERTICAL LINE
        { (char) 0x8163, (char) 0x2026 }, // HORIZONTAL ELLIPSIS
        { (char) 0x8164, (char) 0x2025 }, // TWO DOT LEADER
        { (char) 0x8165, (char) 0x2018 }, // LEFT SINGLE QUOTATION MARK
        { (char) 0x8166, (char) 0x2019 }, // RIGHT SINGLE QUOTATION MARK
        { (char) 0x8167, (char) 0x201C }, // LEFT DOUBLE QUOTATION MARK
        { (char) 0x8168, (char) 0x201D }, // RIGHT DOUBLE QUOTATION MARK
        { (char) 0x8169, (char) 0xFF08 }, // FULLWIDTH LEFT PARENTHESIS
        { (char) 0x816A, (char) 0xFF09 }, // FULLWIDTH RIGHT PARENTHESIS
        { (char) 0x816B, (char) 0x3014 }, // LEFT TORTOISE SHELL BRACKET
        { (char) 0x816C, (char) 0x3015 }, // RIGHT TORTOISE SHELL BRACKET
        { (char) 0x816D, (char) 0xFF3B }, // FULLWIDTH LEFT SQUARE BRACKET
        { (char) 0x816E, (char) 0xFF3D }, // FULLWIDTH RIGHT SQUARE BRACKET
        { (char) 0x816F, (char) 0xFF5B }, // FULLWIDTH LEFT CURLY BRACKET
        { (char) 0x8170, (char) 0xFF5D }, // FULLWIDTH RIGHT CURLY BRACKET
        { (char) 0x8171, (char) 0x3008 }, // LEFT ANGLE BRACKET
        { (char) 0x8172, (char) 0x3009 }, // RIGHT ANGLE BRACKET
        { (char) 0x8173, (char) 0x300A }, // LEFT DOUBLE ANGLE BRACKET
        { (char) 0x8174, (char) 0x300B }, // RIGHT DOUBLE ANGLE BRACKET
        { (char) 0x8175, (char) 0x300C }, // LEFT CORNER BRACKET
        { (char) 0x8176, (char) 0x300D }, // RIGHT CORNER BRACKET
        { (char) 0x8177, (char) 0x300E }, // LEFT WHITE CORNER BRACKET
        { (char) 0x8178, (char) 0x300F }, // RIGHT WHITE CORNER BRACKET
        { (char) 0x8179, (char) 0x3010 }, // LEFT BLACK LENTICULAR BRACKET
        { (char) 0x817A, (char) 0x3011 }, // RIGHT BLACK LENTICULAR BRACKET
        { (char) 0x817B, (char) 0xFF0B }, // FULLWIDTH PLUS SIGN
        { (char) 0x817C, (char) 0x2212 }, // MINUS SIGN
        { (char) 0x817D, (char) 0x00B1 }, // PLUS-MINUS SIGN
        { (char) 0x817E, (char) 0x00D7 }, // MULTIPLICATION SIGN
        { (char) 0x8180, (char) 0x00F7 }, // DIVISION SIGN
        { (char) 0x8181, (char) 0xFF1D }, // FULLWIDTH EQUALS SIGN
        { (char) 0x8182, (char) 0x2260 }, // NOT EQUAL TO
        { (char) 0x8183, (char) 0xFF1C }, // FULLWIDTH LESS-THAN SIGN
        { (char) 0x8184, (char) 0xFF1E }, // FULLWIDTH GREATER-THAN SIGN
        { (char) 0x8185, (char) 0x2266 }, // LESS-THAN OVER EQUAL TO
        { (char) 0x8186, (char) 0x2267 }, // GREATER-THAN OVER EQUAL TO
        { (char) 0x8187, (char) 0x221E }, // INFINITY
        { (char) 0x8188, (char) 0x2234 }, // THEREFORE
        { (char) 0x8189, (char) 0x2642 }, // MALE SIGN
        { (char) 0x818A, (char) 0x2640 }, // FEMALE SIGN
        { (char) 0x818B, (char) 0x00B0 }, // DEGREE SIGN
        { (char) 0x818C, (char) 0x2032 }, // PRIME
        { (char) 0x818D, (char) 0x2033 }, // DOUBLE PRIME
        { (char) 0x818E, (char) 0x2103 }, // DEGREE CELSIUS
        { (char) 0x818F, (char) 0xFFE5 }, // FULLWIDTH YEN SIGN
        { (char) 0x8190, (char) 0xFF04 }, // FULLWIDTH DOLLAR SIGN
        { (char) 0x8191, (char) 0x00A2 }, // CENT SIGN
        { (char) 0x8192, (char) 0x00A3 }, // POUND SIGN
        { (char) 0x8193, (char) 0xFF05 }, // FULLWIDTH PERCENT SIGN
        { (char) 0x8194, (char) 0xFF03 }, // FULLWIDTH NUMBER SIGN
        { (char) 0x8195, (char) 0xFF06 }, // FULLWIDTH AMPERSAND
        { (char) 0x8196, (char) 0xFF0A }, // FULLWIDTH ASTERISK
        { (char) 0x8197, (char) 0xFF20 }, // FULLWIDTH COMMERCIAL AT
        { (char) 0x8198, (char) 0x00A7 }, // SECTION SIGN
        { (char) 0x8199, (char) 0x2606 }, // WHITE STAR
        { (char) 0x819A, (char) 0x2605 }, // BLACK STAR
        { (char) 0x819B, (char) 0x25CB }, // WHITE CIRCLE
        { (char) 0x819C, (char) 0x25CF }, // BLACK CIRCLE
        { (char) 0x819D, (char) 0x25CE }, // BULLSEYE
        { (char) 0x819E, (char) 0x25C7 }, // WHITE DIAMOND
        { (char) 0x819F, (char) 0x25C6 }, // BLACK DIAMOND
        { (char) 0x81A0, (char) 0x25A1 }, // WHITE SQUARE
        { (char) 0x81A1, (char) 0x25A0 }, // BLACK SQUARE
        { (char) 0x81A2, (char) 0x25B3 }, // WHITE UP-POINTING TRIANGLE
        { (char) 0x81A3, (char) 0x25B2 }, // BLACK UP-POINTING TRIANGLE
        { (char) 0x81A4, (char) 0x25BD }, // WHITE DOWN-POINTING TRIANGLE
        { (char) 0x81A5, (char) 0x25BC }, // BLACK DOWN-POINTING TRIANGLE
        { (char) 0x81A6, (char) 0x203B }, // REFERENCE MARK
        { (char) 0x81A7, (char) 0x3012 }, // POSTAL MARK
        { (char) 0x81A8, (char) 0x2192 }, // RIGHTWARDS ARROW
        { (char) 0x81A9, (char) 0x2190 }, // LEFTWARDS ARROW
        { (char) 0x81AA, (char) 0x2191 }, // UPWARDS ARROW
        { (char) 0x81AB, (char) 0x2193 }, // DOWNWARDS ARROW
        { (char) 0x81AC, (char) 0x3013 }, // GETA MARK
        { (char) 0x81B8, (char) 0x2208 }, // ELEMENT OF
        { (char) 0x81B9, (char) 0x220B }, // CONTAINS AS MEMBER
        { (char) 0x81BA, (char) 0x2286 }, // SUBSET OF OR EQUAL TO
        { (char) 0x81BB, (char) 0x2287 }, // SUPERSET OF OR EQUAL TO
        { (char) 0x81BC, (char) 0x2282 }, // SUBSET OF
        { (char) 0x81BD, (char) 0x2283 }, // SUPERSET OF
        { (char) 0x81BE, (char) 0x222A }, // UNION
        { (char) 0x81BF, (char) 0x2229 }, // INTERSECTION
        { (char) 0x81C8, (char) 0x2227 }, // LOGICAL AND
        { (char) 0x81C9, (char) 0x2228 }, // LOGICAL OR
        { (char) 0x81CA, (char) 0x00AC }, // NOT SIGN
        { (char) 0x81CB, (char) 0x21D2 }, // RIGHTWARDS DOUBLE ARROW
        { (char) 0x81CC, (char) 0x21D4 }, // LEFT RIGHT DOUBLE ARROW
        { (char) 0x81CD, (char) 0x2200 }, // FOR ALL
        { (char) 0x81CE, (char) 0x2203 }, // THERE EXISTS
        { (char) 0x81DA, (char) 0x2220 }, // ANGLE
        { (char) 0x81DB, (char) 0x22A5 }, // UP TACK
        { (char) 0x81DC, (char) 0x2312 }, // ARC
        { (char) 0x81DD, (char) 0x2202 }, // PARTIAL DIFFERENTIAL
        { (char) 0x81DE, (char) 0x2207 }, // NABLA
        { (char) 0x81DF, (char) 0x2261 }, // IDENTICAL TO
        { (char) 0x81E0, (char) 0x2252 }, // APPROXIMATELY EQUAL TO OR THE IMAGE OF
        { (char) 0x81E1, (char) 0x226A }, // MUCH LESS-THAN
        { (char) 0x81E2, (char) 0x226B }, // MUCH GREATER-THAN
        { (char) 0x81E3, (char) 0x221A }, // SQUARE ROOT
        { (char) 0x81E4, (char) 0x223D }, // REVERSED TILDE # This UTC mapping is questionable
        { (char) 0x81E5, (char) 0x221D }, // PROPORTIONAL TO
        { (char) 0x81E6, (char) 0x2235 }, // BECAUSE
        { (char) 0x81E7, (char) 0x222B }, // INTEGRAL
        { (char) 0x81E8, (char) 0x222C }, // DOUBLE INTEGRAL
        { (char) 0x81F0, (char) 0x212B }, // ANGSTROM SIGN
        { (char) 0x81F1, (char) 0x2030 }, // PER MILLE SIGN
        { (char) 0x81F2, (char) 0x266F }, // MUSIC SHARP SIGN
        { (char) 0x81F3, (char) 0x266D }, // MUSIC FLAT SIGN
        { (char) 0x81F4, (char) 0x266A }, // EIGHTH NOTE
        { (char) 0x81F5, (char) 0x2020 }, // DAGGER
        { (char) 0x81F6, (char) 0x2021 }, // DOUBLE DAGGER
        { (char) 0x81F7, (char) 0x00B6 }, // PILCROW SIGN
        { (char) 0x81FC, (char) 0x25EF }, // LARGE CIRCLE
        { (char) 0x824F, (char) 0xFF10 }, // FULLWIDTH DIGIT ZERO
        { (char) 0x8250, (char) 0xFF11 }, // FULLWIDTH DIGIT ONE
        { (char) 0x8251, (char) 0xFF12 }, // FULLWIDTH DIGIT TWO
        { (char) 0x8252, (char) 0xFF13 }, // FULLWIDTH DIGIT THREE
        { (char) 0x8253, (char) 0xFF14 }, // FULLWIDTH DIGIT FOUR
        { (char) 0x8254, (char) 0xFF15 }, // FULLWIDTH DIGIT FIVE
        { (char) 0x8255, (char) 0xFF16 }, // FULLWIDTH DIGIT SIX
        { (char) 0x8256, (char) 0xFF17 }, // FULLWIDTH DIGIT SEVEN
        { (char) 0x8257, (char) 0xFF18 }, // FULLWIDTH DIGIT EIGHT
        { (char) 0x8258, (char) 0xFF19 }, // FULLWIDTH DIGIT NINE
        { (char) 0x8260, (char) 0xFF21 }, // FULLWIDTH LATIN CAPITAL LETTER A
        { (char) 0x8261, (char) 0xFF22 }, // FULLWIDTH LATIN CAPITAL LETTER B
        { (char) 0x8262, (char) 0xFF23 }, // FULLWIDTH LATIN CAPITAL LETTER C
        { (char) 0x8263, (char) 0xFF24 }, // FULLWIDTH LATIN CAPITAL LETTER D
        { (char) 0x8264, (char) 0xFF25 }, // FULLWIDTH LATIN CAPITAL LETTER E
        { (char) 0x8265, (char) 0xFF26 }, // FULLWIDTH LATIN CAPITAL LETTER F
        { (char) 0x8266, (char) 0xFF27 }, // FULLWIDTH LATIN CAPITAL LETTER G
        { (char) 0x8267, (char) 0xFF28 }, // FULLWIDTH LATIN CAPITAL LETTER H
        { (char) 0x8268, (char) 0xFF29 }, // FULLWIDTH LATIN CAPITAL LETTER I
        { (char) 0x8269, (char) 0xFF2A }, // FULLWIDTH LATIN CAPITAL LETTER J
        { (char) 0x826A, (char) 0xFF2B }, // FULLWIDTH LATIN CAPITAL LETTER K
        { (char) 0x826B, (char) 0xFF2C }, // FULLWIDTH LATIN CAPITAL LETTER L
        { (char) 0x826C, (char) 0xFF2D }, // FULLWIDTH LATIN CAPITAL LETTER M
        { (char) 0x826D, (char) 0xFF2E }, // FULLWIDTH LATIN CAPITAL LETTER N
        { (char) 0x826E, (char) 0xFF2F }, // FULLWIDTH LATIN CAPITAL LETTER O
        { (char) 0x826F, (char) 0xFF30 }, // FULLWIDTH LATIN CAPITAL LETTER P
        { (char) 0x8270, (char) 0xFF31 }, // FULLWIDTH LATIN CAPITAL LETTER Q
        { (char) 0x8271, (char) 0xFF32 }, // FULLWIDTH LATIN CAPITAL LETTER R
        { (char) 0x8272, (char) 0xFF33 }, // FULLWIDTH LATIN CAPITAL LETTER S
        { (char) 0x8273, (char) 0xFF34 }, // FULLWIDTH LATIN CAPITAL LETTER T
        { (char) 0x8274, (char) 0xFF35 }, // FULLWIDTH LATIN CAPITAL LETTER U
        { (char) 0x8275, (char) 0xFF36 }, // FULLWIDTH LATIN CAPITAL LETTER V
        { (char) 0x8276, (char) 0xFF37 }, // FULLWIDTH LATIN CAPITAL LETTER W
        { (char) 0x8277, (char) 0xFF38 }, // FULLWIDTH LATIN CAPITAL LETTER X
        { (char) 0x8278, (char) 0xFF39 }, // FULLWIDTH LATIN CAPITAL LETTER Y
        { (char) 0x8279, (char) 0xFF3A }, // FULLWIDTH LATIN CAPITAL LETTER Z
        { (char) 0x8281, (char) 0xFF41 }, // FULLWIDTH LATIN SMALL LETTER A
        { (char) 0x8282, (char) 0xFF42 }, // FULLWIDTH LATIN SMALL LETTER B
        { (char) 0x8283, (char) 0xFF43 }, // FULLWIDTH LATIN SMALL LETTER C
        { (char) 0x8284, (char) 0xFF44 }, // FULLWIDTH LATIN SMALL LETTER D
        { (char) 0x8285, (char) 0xFF45 }, // FULLWIDTH LATIN SMALL LETTER E
        { (char) 0x8286, (char) 0xFF46 }, // FULLWIDTH LATIN SMALL LETTER F
        { (char) 0x8287, (char) 0xFF47 }, // FULLWIDTH LATIN SMALL LETTER G
        { (char) 0x8288, (char) 0xFF48 }, // FULLWIDTH LATIN SMALL LETTER H
        { (char) 0x8289, (char) 0xFF49 }, // FULLWIDTH LATIN SMALL LETTER I
        { (char) 0x828A, (char) 0xFF4A }, // FULLWIDTH LATIN SMALL LETTER J
        { (char) 0x828B, (char) 0xFF4B }, // FULLWIDTH LATIN SMALL LETTER K
        { (char) 0x828C, (char) 0xFF4C }, // FULLWIDTH LATIN SMALL LETTER L
        { (char) 0x828D, (char) 0xFF4D }, // FULLWIDTH LATIN SMALL LETTER M
        { (char) 0x828E, (char) 0xFF4E }, // FULLWIDTH LATIN SMALL LETTER N
        { (char) 0x828F, (char) 0xFF4F }, // FULLWIDTH LATIN SMALL LETTER O
        { (char) 0x8290, (char) 0xFF50 }, // FULLWIDTH LATIN SMALL LETTER P
        { (char) 0x8291, (char) 0xFF51 }, // FULLWIDTH LATIN SMALL LETTER Q
        { (char) 0x8292, (char) 0xFF52 }, // FULLWIDTH LATIN SMALL LETTER R
        { (char) 0x8293, (char) 0xFF53 }, // FULLWIDTH LATIN SMALL LETTER S
        { (char) 0x8294, (char) 0xFF54 }, // FULLWIDTH LATIN SMALL LETTER T
        { (char) 0x8295, (char) 0xFF55 }, // FULLWIDTH LATIN SMALL LETTER U
        { (char) 0x8296, (char) 0xFF56 }, // FULLWIDTH LATIN SMALL LETTER V
        { (char) 0x8297, (char) 0xFF57 }, // FULLWIDTH LATIN SMALL LETTER W
        { (char) 0x8298, (char) 0xFF58 }, // FULLWIDTH LATIN SMALL LETTER X
        { (char) 0x8299, (char) 0xFF59 }, // FULLWIDTH LATIN SMALL LETTER Y
        { (char) 0x829A, (char) 0xFF5A }, // FULLWIDTH LATIN SMALL LETTER Z
        { (char) 0x829F, (char) 0x3041 }, // HIRAGANA LETTER SMALL A
        { (char) 0x82A0, (char) 0x3042 }, // HIRAGANA LETTER A
        { (char) 0x82A1, (char) 0x3043 }, // HIRAGANA LETTER SMALL I
        { (char) 0x82A2, (char) 0x3044 }, // HIRAGANA LETTER I
        { (char) 0x82A3, (char) 0x3045 }, // HIRAGANA LETTER SMALL U
        { (char) 0x82A4, (char) 0x3046 }, // HIRAGANA LETTER U
        { (char) 0x82A5, (char) 0x3047 }, // HIRAGANA LETTER SMALL E
        { (char) 0x82A6, (char) 0x3048 }, // HIRAGANA LETTER E
        { (char) 0x82A7, (char) 0x3049 }, // HIRAGANA LETTER SMALL O
        { (char) 0x82A8, (char) 0x304A }, // HIRAGANA LETTER O
        { (char) 0x82A9, (char) 0x304B }, // HIRAGANA LETTER KA
        { (char) 0x82AA, (char) 0x304C }, // HIRAGANA LETTER GA
        { (char) 0x82AB, (char) 0x304D }, // HIRAGANA LETTER KI
        { (char) 0x82AC, (char) 0x304E }, // HIRAGANA LETTER GI
        { (char) 0x82AD, (char) 0x304F }, // HIRAGANA LETTER KU
        { (char) 0x82AE, (char) 0x3050 }, // HIRAGANA LETTER GU
        { (char) 0x82AF, (char) 0x3051 }, // HIRAGANA LETTER KE
        { (char) 0x82B0, (char) 0x3052 }, // HIRAGANA LETTER GE
        { (char) 0x82B1, (char) 0x3053 }, // HIRAGANA LETTER KO
        { (char) 0x82B2, (char) 0x3054 }, // HIRAGANA LETTER GO
        { (char) 0x82B3, (char) 0x3055 }, // HIRAGANA LETTER SA
        { (char) 0x82B4, (char) 0x3056 }, // HIRAGANA LETTER ZA
        { (char) 0x82B5, (char) 0x3057 }, // HIRAGANA LETTER SI
        { (char) 0x82B6, (char) 0x3058 }, // HIRAGANA LETTER ZI
        { (char) 0x82B7, (char) 0x3059 }, // HIRAGANA LETTER SU
        { (char) 0x82B8, (char) 0x305A }, // HIRAGANA LETTER ZU
        { (char) 0x82B9, (char) 0x305B }, // HIRAGANA LETTER SE
        { (char) 0x82BA, (char) 0x305C }, // HIRAGANA LETTER ZE
        { (char) 0x82BB, (char) 0x305D }, // HIRAGANA LETTER SO
        { (char) 0x82BC, (char) 0x305E }, // HIRAGANA LETTER ZO
        { (char) 0x82BD, (char) 0x305F }, // HIRAGANA LETTER TA
        { (char) 0x82BE, (char) 0x3060 }, // HIRAGANA LETTER DA
        { (char) 0x82BF, (char) 0x3061 }, // HIRAGANA LETTER TI
        { (char) 0x82C0, (char) 0x3062 }, // HIRAGANA LETTER DI
        { (char) 0x82C1, (char) 0x3063 }, // HIRAGANA LETTER SMALL TU
        { (char) 0x82C2, (char) 0x3064 }, // HIRAGANA LETTER TU
        { (char) 0x82C3, (char) 0x3065 }, // HIRAGANA LETTER DU
        { (char) 0x82C4, (char) 0x3066 }, // HIRAGANA LETTER TE
        { (char) 0x82C5, (char) 0x3067 }, // HIRAGANA LETTER DE
        { (char) 0x82C6, (char) 0x3068 }, // HIRAGANA LETTER TO
        { (char) 0x82C7, (char) 0x3069 }, // HIRAGANA LETTER DO
        { (char) 0x82C8, (char) 0x306A }, // HIRAGANA LETTER NA
        { (char) 0x82C9, (char) 0x306B }, // HIRAGANA LETTER NI
        { (char) 0x82CA, (char) 0x306C }, // HIRAGANA LETTER NU
        { (char) 0x82CB, (char) 0x306D }, // HIRAGANA LETTER NE
        { (char) 0x82CC, (char) 0x306E }, // HIRAGANA LETTER NO
        { (char) 0x82CD, (char) 0x306F }, // HIRAGANA LETTER HA
        { (char) 0x82CE, (char) 0x3070 }, // HIRAGANA LETTER BA
        { (char) 0x82CF, (char) 0x3071 }, // HIRAGANA LETTER PA
        { (char) 0x82D0, (char) 0x3072 }, // HIRAGANA LETTER HI
        { (char) 0x82D1, (char) 0x3073 }, // HIRAGANA LETTER BI
        { (char) 0x82D2, (char) 0x3074 }, // HIRAGANA LETTER PI
        { (char) 0x82D3, (char) 0x3075 }, // HIRAGANA LETTER HU
        { (char) 0x82D4, (char) 0x3076 }, // HIRAGANA LETTER BU
        { (char) 0x82D5, (char) 0x3077 }, // HIRAGANA LETTER PU
        { (char) 0x82D6, (char) 0x3078 }, // HIRAGANA LETTER HE
        { (char) 0x82D7, (char) 0x3079 }, // HIRAGANA LETTER BE
        { (char) 0x82D8, (char) 0x307A }, // HIRAGANA LETTER PE
        { (char) 0x82D9, (char) 0x307B }, // HIRAGANA LETTER HO
        { (char) 0x82DA, (char) 0x307C }, // HIRAGANA LETTER BO
        { (char) 0x82DB, (char) 0x307D }, // HIRAGANA LETTER PO
        { (char) 0x82DC, (char) 0x307E }, // HIRAGANA LETTER MA
        { (char) 0x82DD, (char) 0x307F }, // HIRAGANA LETTER MI
        { (char) 0x82DE, (char) 0x3080 }, // HIRAGANA LETTER MU
        { (char) 0x82DF, (char) 0x3081 }, // HIRAGANA LETTER ME
        { (char) 0x82E0, (char) 0x3082 }, // HIRAGANA LETTER MO
        { (char) 0x82E1, (char) 0x3083 }, // HIRAGANA LETTER SMALL YA
        { (char) 0x82E2, (char) 0x3084 }, // HIRAGANA LETTER YA
        { (char) 0x82E3, (char) 0x3085 }, // HIRAGANA LETTER SMALL YU
        { (char) 0x82E4, (char) 0x3086 }, // HIRAGANA LETTER YU
        { (char) 0x82E5, (char) 0x3087 }, // HIRAGANA LETTER SMALL YO
        { (char) 0x82E6, (char) 0x3088 }, // HIRAGANA LETTER YO
        { (char) 0x82E7, (char) 0x3089 }, // HIRAGANA LETTER RA
        { (char) 0x82E8, (char) 0x308A }, // HIRAGANA LETTER RI
        { (char) 0x82E9, (char) 0x308B }, // HIRAGANA LETTER RU
        { (char) 0x82EA, (char) 0x308C }, // HIRAGANA LETTER RE
        { (char) 0x82EB, (char) 0x308D }, // HIRAGANA LETTER RO
        { (char) 0x82EC, (char) 0x308E }, // HIRAGANA LETTER SMALL WA
        { (char) 0x82ED, (char) 0x308F }, // HIRAGANA LETTER WA
        { (char) 0x82EE, (char) 0x3090 }, // HIRAGANA LETTER WI
        { (char) 0x82EF, (char) 0x3091 }, // HIRAGANA LETTER WE
        { (char) 0x82F0, (char) 0x3092 }, // HIRAGANA LETTER WO
        { (char) 0x82F1, (char) 0x3093 }, // HIRAGANA LETTER N
        { (char) 0x8340, (char) 0x30A1 }, // KATAKANA LETTER SMALL A
        { (char) 0x8341, (char) 0x30A2 }, // KATAKANA LETTER A
        { (char) 0x8342, (char) 0x30A3 }, // KATAKANA LETTER SMALL I
        { (char) 0x8343, (char) 0x30A4 }, // KATAKANA LETTER I
        { (char) 0x8344, (char) 0x30A5 }, // KATAKANA LETTER SMALL U
        { (char) 0x8345, (char) 0x30A6 }, // KATAKANA LETTER U
        { (char) 0x8346, (char) 0x30A7 }, // KATAKANA LETTER SMALL E
        { (char) 0x8347, (char) 0x30A8 }, // KATAKANA LETTER E
        { (char) 0x8348, (char) 0x30A9 }, // KATAKANA LETTER SMALL O
        { (char) 0x8349, (char) 0x30AA }, // KATAKANA LETTER O
        { (char) 0x834A, (char) 0x30AB }, // KATAKANA LETTER KA
        { (char) 0x834B, (char) 0x30AC }, // KATAKANA LETTER GA
        { (char) 0x834C, (char) 0x30AD }, // KATAKANA LETTER KI
        { (char) 0x834D, (char) 0x30AE }, // KATAKANA LETTER GI
        { (char) 0x834E, (char) 0x30AF }, // KATAKANA LETTER KU
        { (char) 0x834F, (char) 0x30B0 }, // KATAKANA LETTER GU
        { (char) 0x8350, (char) 0x30B1 }, // KATAKANA LETTER KE
        { (char) 0x8351, (char) 0x30B2 }, // KATAKANA LETTER GE
        { (char) 0x8352, (char) 0x30B3 }, // KATAKANA LETTER KO
        { (char) 0x8353, (char) 0x30B4 }, // KATAKANA LETTER GO
        { (char) 0x8354, (char) 0x30B5 }, // KATAKANA LETTER SA
        { (char) 0x8355, (char) 0x30B6 }, // KATAKANA LETTER ZA
        { (char) 0x8356, (char) 0x30B7 }, // KATAKANA LETTER SI
        { (char) 0x8357, (char) 0x30B8 }, // KATAKANA LETTER ZI
        { (char) 0x8358, (char) 0x30B9 }, // KATAKANA LETTER SU
        { (char) 0x8359, (char) 0x30BA }, // KATAKANA LETTER ZU
        { (char) 0x835A, (char) 0x30BB }, // KATAKANA LETTER SE
        { (char) 0x835B, (char) 0x30BC }, // KATAKANA LETTER ZE
        { (char) 0x835C, (char) 0x30BD }, // KATAKANA LETTER SO
        { (char) 0x835D, (char) 0x30BE }, // KATAKANA LETTER ZO
        { (char) 0x835E, (char) 0x30BF }, // KATAKANA LETTER TA
        { (char) 0x835F, (char) 0x30C0 }, // KATAKANA LETTER DA
        { (char) 0x8360, (char) 0x30C1 }, // KATAKANA LETTER TI
        { (char) 0x8361, (char) 0x30C2 }, // KATAKANA LETTER DI
        { (char) 0x8362, (char) 0x30C3 }, // KATAKANA LETTER SMALL TU
        { (char) 0x8363, (char) 0x30C4 }, // KATAKANA LETTER TU
        { (char) 0x8364, (char) 0x30C5 }, // KATAKANA LETTER DU
        { (char) 0x8365, (char) 0x30C6 }, // KATAKANA LETTER TE
        { (char) 0x8366, (char) 0x30C7 }, // KATAKANA LETTER DE
        { (char) 0x8367, (char) 0x30C8 }, // KATAKANA LETTER TO
        { (char) 0x8368, (char) 0x30C9 }, // KATAKANA LETTER DO
        { (char) 0x8369, (char) 0x30CA }, // KATAKANA LETTER NA
        { (char) 0x836A, (char) 0x30CB }, // KATAKANA LETTER NI
        { (char) 0x836B, (char) 0x30CC }, // KATAKANA LETTER NU
        { (char) 0x836C, (char) 0x30CD }, // KATAKANA LETTER NE
        { (char) 0x836D, (char) 0x30CE }, // KATAKANA LETTER NO
        { (char) 0x836E, (char) 0x30CF }, // KATAKANA LETTER HA
        { (char) 0x836F, (char) 0x30D0 }, // KATAKANA LETTER BA
        { (char) 0x8370, (char) 0x30D1 }, // KATAKANA LETTER PA
        { (char) 0x8371, (char) 0x30D2 }, // KATAKANA LETTER HI
        { (char) 0x8372, (char) 0x30D3 }, // KATAKANA LETTER BI
        { (char) 0x8373, (char) 0x30D4 }, // KATAKANA LETTER PI
        { (char) 0x8374, (char) 0x30D5 }, // KATAKANA LETTER HU
        { (char) 0x8375, (char) 0x30D6 }, // KATAKANA LETTER BU
        { (char) 0x8376, (char) 0x30D7 }, // KATAKANA LETTER PU
        { (char) 0x8377, (char) 0x30D8 }, // KATAKANA LETTER HE
        { (char) 0x8378, (char) 0x30D9 }, // KATAKANA LETTER BE
        { (char) 0x8379, (char) 0x30DA }, // KATAKANA LETTER PE
        { (char) 0x837A, (char) 0x30DB }, // KATAKANA LETTER HO
        { (char) 0x837B, (char) 0x30DC }, // KATAKANA LETTER BO
        { (char) 0x837C, (char) 0x30DD }, // KATAKANA LETTER PO
        { (char) 0x837D, (char) 0x30DE }, // KATAKANA LETTER MA
        { (char) 0x837E, (char) 0x30DF }, // KATAKANA LETTER MI
        { (char) 0x8380, (char) 0x30E0 }, // KATAKANA LETTER MU
        { (char) 0x8381, (char) 0x30E1 }, // KATAKANA LETTER ME
        { (char) 0x8382, (char) 0x30E2 }, // KATAKANA LETTER MO
        { (char) 0x8383, (char) 0x30E3 }, // KATAKANA LETTER SMALL YA
        { (char) 0x8384, (char) 0x30E4 }, // KATAKANA LETTER YA
        { (char) 0x8385, (char) 0x30E5 }, // KATAKANA LETTER SMALL YU
        { (char) 0x8386, (char) 0x30E6 }, // KATAKANA LETTER YU
        { (char) 0x8387, (char) 0x30E7 }, // KATAKANA LETTER SMALL YO
        { (char) 0x8388, (char) 0x30E8 }, // KATAKANA LETTER YO
        { (char) 0x8389, (char) 0x30E9 }, // KATAKANA LETTER RA
        { (char) 0x838A, (char) 0x30EA }, // KATAKANA LETTER RI
        { (char) 0x838B, (char) 0x30EB }, // KATAKANA LETTER RU
        { (char) 0x838C, (char) 0x30EC }, // KATAKANA LETTER RE
        { (char) 0x838D, (char) 0x30ED }, // KATAKANA LETTER RO
        { (char) 0x838E, (char) 0x30EE }, // KATAKANA LETTER SMALL WA
        { (char) 0x838F, (char) 0x30EF }, // KATAKANA LETTER WA
        { (char) 0x8390, (char) 0x30F0 }, // KATAKANA LETTER WI
        { (char) 0x8391, (char) 0x30F1 }, // KATAKANA LETTER WE
        { (char) 0x8392, (char) 0x30F2 }, // KATAKANA LETTER WO
        { (char) 0x8393, (char) 0x30F3 }, // KATAKANA LETTER N
        { (char) 0x8394, (char) 0x30F4 }, // KATAKANA LETTER VU
        { (char) 0x8395, (char) 0x30F5 }, // KATAKANA LETTER SMALL KA
        { (char) 0x8396, (char) 0x30F6 }, // KATAKANA LETTER SMALL KE
        { (char) 0x839F, (char) 0x0391 }, // GREEK CAPITAL LETTER ALPHA
        { (char) 0x83A0, (char) 0x0392 }, // GREEK CAPITAL LETTER BETA
        { (char) 0x83A1, (char) 0x0393 }, // GREEK CAPITAL LETTER GAMMA
        { (char) 0x83A2, (char) 0x0394 }, // GREEK CAPITAL LETTER DELTA
        { (char) 0x83A3, (char) 0x0395 }, // GREEK CAPITAL LETTER EPSILON
        { (char) 0x83A4, (char) 0x0396 }, // GREEK CAPITAL LETTER ZETA
        { (char) 0x83A5, (char) 0x0397 }, // GREEK CAPITAL LETTER ETA
        { (char) 0x83A6, (char) 0x0398 }, // GREEK CAPITAL LETTER THETA
        { (char) 0x83A7, (char) 0x0399 }, // GREEK CAPITAL LETTER IOTA
        { (char) 0x83A8, (char) 0x039A }, // GREEK CAPITAL LETTER KAPPA
        { (char) 0x83A9, (char) 0x039B }, // GREEK CAPITAL LETTER LAMDA
        { (char) 0x83AA, (char) 0x039C }, // GREEK CAPITAL LETTER MU
        { (char) 0x83AB, (char) 0x039D }, // GREEK CAPITAL LETTER NU
        { (char) 0x83AC, (char) 0x039E }, // GREEK CAPITAL LETTER XI
        { (char) 0x83AD, (char) 0x039F }, // GREEK CAPITAL LETTER OMICRON
        { (char) 0x83AE, (char) 0x03A0 }, // GREEK CAPITAL LETTER PI
        { (char) 0x83AF, (char) 0x03A1 }, // GREEK CAPITAL LETTER RHO
        { (char) 0x83B0, (char) 0x03A3 }, // GREEK CAPITAL LETTER SIGMA
        { (char) 0x83B1, (char) 0x03A4 }, // GREEK CAPITAL LETTER TAU
        { (char) 0x83B2, (char) 0x03A5 }, // GREEK CAPITAL LETTER UPSILON
        { (char) 0x83B3, (char) 0x03A6 }, // GREEK CAPITAL LETTER PHI
        { (char) 0x83B4, (char) 0x03A7 }, // GREEK CAPITAL LETTER CHI
        { (char) 0x83B5, (char) 0x03A8 }, // GREEK CAPITAL LETTER PSI
        { (char) 0x83B6, (char) 0x03A9 }, // GREEK CAPITAL LETTER OMEGA
        { (char) 0x83BF, (char) 0x03B1 }, // GREEK SMALL LETTER ALPHA
        { (char) 0x83C0, (char) 0x03B2 }, // GREEK SMALL LETTER BETA
        { (char) 0x83C1, (char) 0x03B3 }, // GREEK SMALL LETTER GAMMA
        { (char) 0x83C2, (char) 0x03B4 }, // GREEK SMALL LETTER DELTA
        { (char) 0x83C3, (char) 0x03B5 }, // GREEK SMALL LETTER EPSILON
        { (char) 0x83C4, (char) 0x03B6 }, // GREEK SMALL LETTER ZETA
        { (char) 0x83C5, (char) 0x03B7 }, // GREEK SMALL LETTER ETA
        { (char) 0x83C6, (char) 0x03B8 }, // GREEK SMALL LETTER THETA
        { (char) 0x83C7, (char) 0x03B9 }, // GREEK SMALL LETTER IOTA
        { (char) 0x83C8, (char) 0x03BA }, // GREEK SMALL LETTER KAPPA
        { (char) 0x83C9, (char) 0x03BB }, // GREEK SMALL LETTER LAMDA
        { (char) 0x83CA, (char) 0x03BC }, // GREEK SMALL LETTER MU
        { (char) 0x83CB, (char) 0x03BD }, // GREEK SMALL LETTER NU
        { (char) 0x83CC, (char) 0x03BE }, // GREEK SMALL LETTER XI
        { (char) 0x83CD, (char) 0x03BF }, // GREEK SMALL LETTER OMICRON
        { (char) 0x83CE, (char) 0x03C0 }, // GREEK SMALL LETTER PI
        { (char) 0x83CF, (char) 0x03C1 }, // GREEK SMALL LETTER RHO
        { (char) 0x83D0, (char) 0x03C3 }, // GREEK SMALL LETTER SIGMA
        { (char) 0x83D1, (char) 0x03C4 }, // GREEK SMALL LETTER TAU
        { (char) 0x83D2, (char) 0x03C5 }, // GREEK SMALL LETTER UPSILON
        { (char) 0x83D3, (char) 0x03C6 }, // GREEK SMALL LETTER PHI
        { (char) 0x83D4, (char) 0x03C7 }, // GREEK SMALL LETTER CHI
        { (char) 0x83D5, (char) 0x03C8 }, // GREEK SMALL LETTER PSI
        { (char) 0x83D6, (char) 0x03C9 }, // GREEK SMALL LETTER OMEGA
        { (char) 0x8440, (char) 0x0410 }, // CYRILLIC CAPITAL LETTER A
        { (char) 0x8441, (char) 0x0411 }, // CYRILLIC CAPITAL LETTER BE
        { (char) 0x8442, (char) 0x0412 }, // CYRILLIC CAPITAL LETTER VE
        { (char) 0x8443, (char) 0x0413 }, // CYRILLIC CAPITAL LETTER GHE
        { (char) 0x8444, (char) 0x0414 }, // CYRILLIC CAPITAL LETTER DE
        { (char) 0x8445, (char) 0x0415 }, // CYRILLIC CAPITAL LETTER IE
        { (char) 0x8446, (char) 0x0401 }, // CYRILLIC CAPITAL LETTER IO
        { (char) 0x8447, (char) 0x0416 }, // CYRILLIC CAPITAL LETTER ZHE
        { (char) 0x8448, (char) 0x0417 }, // CYRILLIC CAPITAL LETTER ZE
        { (char) 0x8449, (char) 0x0418 }, // CYRILLIC CAPITAL LETTER I
        { (char) 0x844A, (char) 0x0419 }, // CYRILLIC CAPITAL LETTER SHORT I
        { (char) 0x844B, (char) 0x041A }, // CYRILLIC CAPITAL LETTER KA
        { (char) 0x844C, (char) 0x041B }, // CYRILLIC CAPITAL LETTER EL
        { (char) 0x844D, (char) 0x041C }, // CYRILLIC CAPITAL LETTER EM
        { (char) 0x844E, (char) 0x041D }, // CYRILLIC CAPITAL LETTER EN
        { (char) 0x844F, (char) 0x041E }, // CYRILLIC CAPITAL LETTER O
        { (char) 0x8450, (char) 0x041F }, // CYRILLIC CAPITAL LETTER PE
        { (char) 0x8451, (char) 0x0420 }, // CYRILLIC CAPITAL LETTER ER
        { (char) 0x8452, (char) 0x0421 }, // CYRILLIC CAPITAL LETTER ES
        { (char) 0x8453, (char) 0x0422 }, // CYRILLIC CAPITAL LETTER TE
        { (char) 0x8454, (char) 0x0423 }, // CYRILLIC CAPITAL LETTER U
        { (char) 0x8455, (char) 0x0424 }, // CYRILLIC CAPITAL LETTER EF
        { (char) 0x8456, (char) 0x0425 }, // CYRILLIC CAPITAL LETTER HA
        { (char) 0x8457, (char) 0x0426 }, // CYRILLIC CAPITAL LETTER TSE
        { (char) 0x8458, (char) 0x0427 }, // CYRILLIC CAPITAL LETTER CHE
        { (char) 0x8459, (char) 0x0428 }, // CYRILLIC CAPITAL LETTER SHA
        { (char) 0x845A, (char) 0x0429 }, // CYRILLIC CAPITAL LETTER SHCHA
        { (char) 0x845B, (char) 0x042A }, // CYRILLIC CAPITAL LETTER HARD SIGN
        { (char) 0x845C, (char) 0x042B }, // CYRILLIC CAPITAL LETTER YERU
        { (char) 0x845D, (char) 0x042C }, // CYRILLIC CAPITAL LETTER SOFT SIGN
        { (char) 0x845E, (char) 0x042D }, // CYRILLIC CAPITAL LETTER E
        { (char) 0x845F, (char) 0x042E }, // CYRILLIC CAPITAL LETTER YU
        { (char) 0x8460, (char) 0x042F }, // CYRILLIC CAPITAL LETTER YA
        { (char) 0x8470, (char) 0x0430 }, // CYRILLIC SMALL LETTER A
        { (char) 0x8471, (char) 0x0431 }, // CYRILLIC SMALL LETTER BE
        { (char) 0x8472, (char) 0x0432 }, // CYRILLIC SMALL LETTER VE
        { (char) 0x8473, (char) 0x0433 }, // CYRILLIC SMALL LETTER GHE
        { (char) 0x8474, (char) 0x0434 }, // CYRILLIC SMALL LETTER DE
        { (char) 0x8475, (char) 0x0435 }, // CYRILLIC SMALL LETTER IE
        { (char) 0x8476, (char) 0x0451 }, // CYRILLIC SMALL LETTER IO
        { (char) 0x8477, (char) 0x0436 }, // CYRILLIC SMALL LETTER ZHE
        { (char) 0x8478, (char) 0x0437 }, // CYRILLIC SMALL LETTER ZE
        { (char) 0x8479, (char) 0x0438 }, // CYRILLIC SMALL LETTER I
        { (char) 0x847A, (char) 0x0439 }, // CYRILLIC SMALL LETTER SHORT I
        { (char) 0x847B, (char) 0x043A }, // CYRILLIC SMALL LETTER KA
        { (char) 0x847C, (char) 0x043B }, // CYRILLIC SMALL LETTER EL
        { (char) 0x847D, (char) 0x043C }, // CYRILLIC SMALL LETTER EM
        { (char) 0x847E, (char) 0x043D }, // CYRILLIC SMALL LETTER EN
        { (char) 0x8480, (char) 0x043E }, // CYRILLIC SMALL LETTER O
        { (char) 0x8481, (char) 0x043F }, // CYRILLIC SMALL LETTER PE
        { (char) 0x8482, (char) 0x0440 }, // CYRILLIC SMALL LETTER ER
        { (char) 0x8483, (char) 0x0441 }, // CYRILLIC SMALL LETTER ES
        { (char) 0x8484, (char) 0x0442 }, // CYRILLIC SMALL LETTER TE
        { (char) 0x8485, (char) 0x0443 }, // CYRILLIC SMALL LETTER U
        { (char) 0x8486, (char) 0x0444 }, // CYRILLIC SMALL LETTER EF
        { (char) 0x8487, (char) 0x0445 }, // CYRILLIC SMALL LETTER HA
        { (char) 0x8488, (char) 0x0446 }, // CYRILLIC SMALL LETTER TSE
        { (char) 0x8489, (char) 0x0447 }, // CYRILLIC SMALL LETTER CHE
        { (char) 0x848A, (char) 0x0448 }, // CYRILLIC SMALL LETTER SHA
        { (char) 0x848B, (char) 0x0449 }, // CYRILLIC SMALL LETTER SHCHA
        { (char) 0x848C, (char) 0x044A }, // CYRILLIC SMALL LETTER HARD SIGN
        { (char) 0x848D, (char) 0x044B }, // CYRILLIC SMALL LETTER YERU
        { (char) 0x848E, (char) 0x044C }, // CYRILLIC SMALL LETTER SOFT SIGN
        { (char) 0x848F, (char) 0x044D }, // CYRILLIC SMALL LETTER E
        { (char) 0x8490, (char) 0x044E }, // CYRILLIC SMALL LETTER YU
        { (char) 0x8491, (char) 0x044F }, // CYRILLIC SMALL LETTER YA
        { (char) 0x849F, (char) 0x2500 }, // BOX DRAWINGS LIGHT HORIZONTAL
        { (char) 0x84A0, (char) 0x2502 }, // BOX DRAWINGS LIGHT VERTICAL
        { (char) 0x84A1, (char) 0x250C }, // BOX DRAWINGS LIGHT DOWN AND RIGHT
        { (char) 0x84A2, (char) 0x2510 }, // BOX DRAWINGS LIGHT DOWN AND LEFT
        { (char) 0x84A3, (char) 0x2518 }, // BOX DRAWINGS LIGHT UP AND LEFT
        { (char) 0x84A4, (char) 0x2514 }, // BOX DRAWINGS LIGHT UP AND RIGHT
        { (char) 0x84A5, (char) 0x251C }, // BOX DRAWINGS LIGHT VERTICAL AND RIGHT
        { (char) 0x84A6, (char) 0x252C }, // BOX DRAWINGS LIGHT DOWN AND HORIZONTAL
        { (char) 0x84A7, (char) 0x2524 }, // BOX DRAWINGS LIGHT VERTICAL AND LEFT
        { (char) 0x84A8, (char) 0x2534 }, // BOX DRAWINGS LIGHT UP AND HORIZONTAL
        { (char) 0x84A9, (char) 0x253C }, // BOX DRAWINGS LIGHT VERTICAL AND HORIZONTAL
        { (char) 0x84AA, (char) 0x2501 }, // BOX DRAWINGS HEAVY HORIZONTAL
        { (char) 0x84AB, (char) 0x2503 }, // BOX DRAWINGS HEAVY VERTICAL
        { (char) 0x84AC, (char) 0x250F }, // BOX DRAWINGS HEAVY DOWN AND RIGHT
        { (char) 0x84AD, (char) 0x2513 }, // BOX DRAWINGS HEAVY DOWN AND LEFT
        { (char) 0x84AE, (char) 0x251B }, // BOX DRAWINGS HEAVY UP AND LEFT
        { (char) 0x84AF, (char) 0x2517 }, // BOX DRAWINGS HEAVY UP AND RIGHT
        { (char) 0x84B0, (char) 0x2523 }, // BOX DRAWINGS HEAVY VERTICAL AND RIGHT
        { (char) 0x84B1, (char) 0x2533 }, // BOX DRAWINGS HEAVY DOWN AND HORIZONTAL
        { (char) 0x84B2, (char) 0x252B }, // BOX DRAWINGS HEAVY VERTICAL AND LEFT
        { (char) 0x84B3, (char) 0x253B }, // BOX DRAWINGS HEAVY UP AND HORIZONTAL
        { (char) 0x84B4, (char) 0x254B }, // BOX DRAWINGS HEAVY VERTICAL AND HORIZONTAL
        { (char) 0x84B5, (char) 0x2520 }, // BOX DRAWINGS VERTICAL HEAVY AND RIGHT LIGHT
        { (char) 0x84B6, (char) 0x252F }, // BOX DRAWINGS DOWN LIGHT AND HORIZONTAL HEAVY
        { (char) 0x84B7, (char) 0x2528 }, // BOX DRAWINGS VERTICAL HEAVY AND LEFT LIGHT
        { (char) 0x84B8, (char) 0x2537 }, // BOX DRAWINGS UP LIGHT AND HORIZONTAL HEAVY
        { (char) 0x84B9, (char) 0x253F }, // BOX DRAWINGS VERTICAL LIGHT AND HORIZONTAL HEAVY
        { (char) 0x84BA, (char) 0x251D }, // BOX DRAWINGS VERTICAL LIGHT AND RIGHT HEAVY
        { (char) 0x84BB, (char) 0x2530 }, // BOX DRAWINGS DOWN HEAVY AND HORIZONTAL LIGHT
        { (char) 0x84BC, (char) 0x2525 }, // BOX DRAWINGS VERTICAL LIGHT AND LEFT HEAVY
        { (char) 0x84BD, (char) 0x2538 }, // BOX DRAWINGS UP HEAVY AND HORIZONTAL LIGHT
        { (char) 0x84BE, (char) 0x2542 }, // BOX DRAWINGS VERTICAL HEAVY AND HORIZONTAL LIGHT

        // Apple additions
        { (char) 0x8540, (char) 0x2460 }, // CIRCLED DIGIT ONE
        { (char) 0x8541, (char) 0x2461 }, // CIRCLED DIGIT TWO
        { (char) 0x8542, (char) 0x2462 }, // CIRCLED DIGIT THREE
        { (char) 0x8543, (char) 0x2463 }, // CIRCLED DIGIT FOUR
        { (char) 0x8544, (char) 0x2464 }, // CIRCLED DIGIT FIVE
        { (char) 0x8545, (char) 0x2465 }, // CIRCLED DIGIT SIX
        { (char) 0x8546, (char) 0x2466 }, // CIRCLED DIGIT SEVEN
        { (char) 0x8547, (char) 0x2467 }, // CIRCLED DIGIT EIGHT
        { (char) 0x8548, (char) 0x2468 }, // CIRCLED DIGIT NINE
        { (char) 0x8549, (char) 0x2469 }, // CIRCLED NUMBER TEN
        { (char) 0x854A, (char) 0x246A }, // CIRCLED NUMBER ELEVEN
        { (char) 0x854B, (char) 0x246B }, // CIRCLED NUMBER TWELVE
        { (char) 0x854C, (char) 0x246C }, // CIRCLED NUMBER THIRTEEN
        { (char) 0x854D, (char) 0x246D }, // CIRCLED NUMBER FOURTEEN
        { (char) 0x854E, (char) 0x246E }, // CIRCLED NUMBER FIFTEEN
        { (char) 0x854F, (char) 0x246F }, // CIRCLED NUMBER SIXTEEN
        { (char) 0x8550, (char) 0x2470 }, // CIRCLED NUMBER SEVENTEEN
        { (char) 0x8551, (char) 0x2471 }, // CIRCLED NUMBER EIGHTEEN
        { (char) 0x8552, (char) 0x2472 }, // CIRCLED NUMBER NINETEEN
        { (char) 0x8553, (char) 0x2473 }, // CIRCLED NUMBER TWENTY
        { (char) 0x855E, (char) 0x2474 }, // PARENTHESIZED DIGIT ONE
        { (char) 0x855F, (char) 0x2475 }, // PARENTHESIZED DIGIT TWO
        { (char) 0x8560, (char) 0x2476 }, // PARENTHESIZED DIGIT THREE
        { (char) 0x8561, (char) 0x2477 }, // PARENTHESIZED DIGIT FOUR
        { (char) 0x8562, (char) 0x2478 }, // PARENTHESIZED DIGIT FIVE
        { (char) 0x8563, (char) 0x2479 }, // PARENTHESIZED DIGIT SIX
        { (char) 0x8564, (char) 0x247A }, // PARENTHESIZED DIGIT SEVEN
        { (char) 0x8565, (char) 0x247B }, // PARENTHESIZED DIGIT EIGHT
        { (char) 0x8566, (char) 0x247C }, // PARENTHESIZED DIGIT NINE
        { (char) 0x8567, (char) 0x247D }, // PARENTHESIZED NUMBER TEN
        { (char) 0x8568, (char) 0x247E }, // PARENTHESIZED NUMBER ELEVEN
        { (char) 0x8569, (char) 0x247F }, // PARENTHESIZED NUMBER TWELVE
        { (char) 0x856A, (char) 0x2480 }, // PARENTHESIZED NUMBER THIRTEEN
        { (char) 0x856B, (char) 0x2481 }, // PARENTHESIZED NUMBER FOURTEEN
        { (char) 0x856C, (char) 0x2482 }, // PARENTHESIZED NUMBER FIFTEEN
        { (char) 0x856D, (char) 0x2483 }, // PARENTHESIZED NUMBER SIXTEEN
        { (char) 0x856E, (char) 0x2484 }, // PARENTHESIZED NUMBER SEVENTEEN
        { (char) 0x856F, (char) 0x2485 }, // PARENTHESIZED NUMBER EIGHTEEN
        { (char) 0x8570, (char) 0x2486 }, // PARENTHESIZED NUMBER NINETEEN
        { (char) 0x8571, (char) 0x2487 }, // PARENTHESIZED NUMBER TWENTY
        { (char) 0x857C, (char) 0x2776 }, // DINGBAT NEGATIVE CIRCLED DIGIT ONE
        { (char) 0x857D, (char) 0x2777 }, // DINGBAT NEGATIVE CIRCLED DIGIT TWO
        { (char) 0x857E, (char) 0x2778 }, // DINGBAT NEGATIVE CIRCLED DIGIT THREE
        { (char) 0x8580, (char) 0x2779 }, // DINGBAT NEGATIVE CIRCLED DIGIT FOUR
        { (char) 0x8581, (char) 0x277A }, // DINGBAT NEGATIVE CIRCLED DIGIT FIVE
        { (char) 0x8582, (char) 0x277B }, // DINGBAT NEGATIVE CIRCLED DIGIT SIX
        { (char) 0x8583, (char) 0x277C }, // DINGBAT NEGATIVE CIRCLED DIGIT SEVEN
        { (char) 0x8584, (char) 0x277D }, // DINGBAT NEGATIVE CIRCLED DIGIT EIGHT
        { (char) 0x8585, (char) 0x277E }, // DINGBAT NEGATIVE CIRCLED DIGIT NINE
        { (char) 0x8591, (char) 0xF860, (char) 0x0030, (char) 0x002E }, // digit zero full stop
        { (char) 0x8592, (char) 0x2488 }, // DIGIT ONE FULL STOP
        { (char) 0x8593, (char) 0x2489 }, // DIGIT TWO FULL STOP
        { (char) 0x8594, (char) 0x248A }, // DIGIT THREE FULL STOP
        { (char) 0x8595, (char) 0x248B }, // DIGIT FOUR FULL STOP
        { (char) 0x8596, (char) 0x248C }, // DIGIT FIVE FULL STOP
        { (char) 0x8597, (char) 0x248D }, // DIGIT SIX FULL STOP
        { (char) 0x8598, (char) 0x248E }, // DIGIT SEVEN FULL STOP
        { (char) 0x8599, (char) 0x248F }, // DIGIT EIGHT FULL STOP
        { (char) 0x859A, (char) 0x2490 }, // DIGIT NINE FULL STOP
        { (char) 0x859F, (char) 0x2160 }, // ROMAN NUMERAL ONE
        { (char) 0x85A0, (char) 0x2161 }, // ROMAN NUMERAL TWO
        { (char) 0x85A1, (char) 0x2162 }, // ROMAN NUMERAL THREE
        { (char) 0x85A2, (char) 0x2163 }, // ROMAN NUMERAL FOUR
        { (char) 0x85A3, (char) 0x2164 }, // ROMAN NUMERAL FIVE
        { (char) 0x85A4, (char) 0x2165 }, // ROMAN NUMERAL SIX
        { (char) 0x85A5, (char) 0x2166 }, // ROMAN NUMERAL SEVEN
        { (char) 0x85A6, (char) 0x2167 }, // ROMAN NUMERAL EIGHT
        { (char) 0x85A7, (char) 0x2168 }, // ROMAN NUMERAL NINE
        { (char) 0x85A8, (char) 0x2169 }, // ROMAN NUMERAL TEN
        { (char) 0x85A9, (char) 0x216A }, // ROMAN NUMERAL ELEVEN
        { (char) 0x85AA, (char) 0x216B }, // ROMAN NUMERAL TWELVE
        { (char) 0x85AB, (char) 0xF862, (char) 0x0058, (char) 0x0049, (char) 0x0049, (char) 0x0049 }, // roman numeral thirteen
        { (char) 0x85AC, (char) 0xF861, (char) 0x0058, (char) 0x0049, (char) 0x0056 }, // roman numeral fourteen
        { (char) 0x85AD, (char) 0xF860, (char) 0x0058, (char) 0x0056 }, // roman numeral fifteen
        { (char) 0x85B3, (char) 0x2170 }, // SMALL ROMAN NUMERAL ONE
        { (char) 0x85B4, (char) 0x2171 }, // SMALL ROMAN NUMERAL TWO
        { (char) 0x85B5, (char) 0x2172 }, // SMALL ROMAN NUMERAL THREE
        { (char) 0x85B6, (char) 0x2173 }, // SMALL ROMAN NUMERAL FOUR
        { (char) 0x85B7, (char) 0x2174 }, // SMALL ROMAN NUMERAL FIVE
        { (char) 0x85B8, (char) 0x2175 }, // SMALL ROMAN NUMERAL SIX
        { (char) 0x85B9, (char) 0x2176 }, // SMALL ROMAN NUMERAL SEVEN
        { (char) 0x85BA, (char) 0x2177 }, // SMALL ROMAN NUMERAL EIGHT
        { (char) 0x85BB, (char) 0x2178 }, // SMALL ROMAN NUMERAL NINE
        { (char) 0x85BC, (char) 0x2179 }, // SMALL ROMAN NUMERAL TEN
        { (char) 0x85BD, (char) 0x217A }, // SMALL ROMAN NUMERAL ELEVEN
        { (char) 0x85BE, (char) 0x217B }, // SMALL ROMAN NUMERAL TWELVE
        { (char) 0x85BF, (char) 0xF862, (char) 0x0078, (char) 0x0069, (char) 0x0069, (char) 0x0069 }, // small roman numeral thirteen
        { (char) 0x85C0, (char) 0xF861, (char) 0x0078, (char) 0x0069, (char) 0x0076 }, // small roman numeral fourteen
        { (char) 0x85C1, (char) 0xF860, (char) 0x0078, (char) 0x0076 }, // small roman numeral fifteen
        { (char) 0x85DB, (char) 0x249C }, // PARENTHESIZED LATIN SMALL LETTER A
        { (char) 0x85DC, (char) 0x249D }, // PARENTHESIZED LATIN SMALL LETTER B
        { (char) 0x85DD, (char) 0x249E }, // PARENTHESIZED LATIN SMALL LETTER C
        { (char) 0x85DE, (char) 0x249F }, // PARENTHESIZED LATIN SMALL LETTER D
        { (char) 0x85DF, (char) 0x24A0 }, // PARENTHESIZED LATIN SMALL LETTER E
        { (char) 0x85E0, (char) 0x24A1 }, // PARENTHESIZED LATIN SMALL LETTER F
        { (char) 0x85E1, (char) 0x24A2 }, // PARENTHESIZED LATIN SMALL LETTER G
        { (char) 0x85E2, (char) 0x24A3 }, // PARENTHESIZED LATIN SMALL LETTER H
        { (char) 0x85E3, (char) 0x24A4 }, // PARENTHESIZED LATIN SMALL LETTER I
        { (char) 0x85E4, (char) 0x24A5 }, // PARENTHESIZED LATIN SMALL LETTER J
        { (char) 0x85E5, (char) 0x24A6 }, // PARENTHESIZED LATIN SMALL LETTER K
        { (char) 0x85E6, (char) 0x24A7 }, // PARENTHESIZED LATIN SMALL LETTER L
        { (char) 0x85E7, (char) 0x24A8 }, // PARENTHESIZED LATIN SMALL LETTER M
        { (char) 0x85E8, (char) 0x24A9 }, // PARENTHESIZED LATIN SMALL LETTER N
        { (char) 0x85E9, (char) 0x24AA }, // PARENTHESIZED LATIN SMALL LETTER O
        { (char) 0x85EA, (char) 0x24AB }, // PARENTHESIZED LATIN SMALL LETTER P
        { (char) 0x85EB, (char) 0x24AC }, // PARENTHESIZED LATIN SMALL LETTER Q
        { (char) 0x85EC, (char) 0x24AD }, // PARENTHESIZED LATIN SMALL LETTER R
        { (char) 0x85ED, (char) 0x24AE }, // PARENTHESIZED LATIN SMALL LETTER S
        { (char) 0x85EE, (char) 0x24AF }, // PARENTHESIZED LATIN SMALL LETTER T
        { (char) 0x85EF, (char) 0x24B0 }, // PARENTHESIZED LATIN SMALL LETTER U
        { (char) 0x85F0, (char) 0x24B1 }, // PARENTHESIZED LATIN SMALL LETTER V
        { (char) 0x85F1, (char) 0x24B2 }, // PARENTHESIZED LATIN SMALL LETTER W
        { (char) 0x85F2, (char) 0x24B3 }, // PARENTHESIZED LATIN SMALL LETTER X
        { (char) 0x85F3, (char) 0x24B4 }, // PARENTHESIZED LATIN SMALL LETTER Y
        { (char) 0x85F4, (char) 0x24B5 }, // PARENTHESIZED LATIN SMALL LETTER Z
        { (char) 0x8640, (char) 0x339C }, // SQUARE MM
        { (char) 0x8641, (char) 0x339F }, // SQUARE MM SQUARED
        { (char) 0x8642, (char) 0x339D }, // SQUARE CM
        { (char) 0x8643, (char) 0x33A0 }, // SQUARE CM SQUARED
        { (char) 0x8644, (char) 0x33A4 }, // SQUARE CM CUBED
        { (char) 0x8645, (char) 0xFF4D, (char) 0xF87F }, // square m
        { (char) 0x8646, (char) 0x33A1 }, // SQUARE M SQUARED
        { (char) 0x8647, (char) 0x33A5 }, // SQUARE M CUBED
        { (char) 0x8648, (char) 0x339E }, // SQUARE KM
        { (char) 0x8649, (char) 0x33A2 }, // SQUARE KM SQUARED
        { (char) 0x864A, (char) 0x338E }, // SQUARE MG
        { (char) 0x864B, (char) 0xFF47, (char) 0xF87F }, // square g
        { (char) 0x864C, (char) 0x338F }, // SQUARE KG
        { (char) 0x864D, (char) 0x33C4 }, // SQUARE CC
        { (char) 0x864E, (char) 0x3396 }, // SQUARE ML
        { (char) 0x864F, (char) 0x3397 }, // SQUARE DL
        { (char) 0x8650, (char) 0x2113 }, // SCRIPT SMALL L
        { (char) 0x8651, (char) 0x3398 }, // SQUARE KL
        { (char) 0x8652, (char) 0x33B3 }, // SQUARE MS
        { (char) 0x8653, (char) 0x33B2 }, // SQUARE MU S
        { (char) 0x8654, (char) 0x33B1 }, // SQUARE NS
        { (char) 0x8655, (char) 0x33B0 }, // SQUARE PS
        { (char) 0x8656, (char) 0x2109 }, // DEGREE FAHRENHEIT
        { (char) 0x8657, (char) 0x33D4 }, // SQUARE MB SMALL
        { (char) 0x8658, (char) 0x33CB }, // SQUARE HP
        { (char) 0x8659, (char) 0x3390 }, // SQUARE HZ
        { (char) 0x865A, (char) 0x3385 }, // SQUARE KB
        { (char) 0x865B, (char) 0x3386 }, // SQUARE MB
        { (char) 0x865C, (char) 0x3387 }, // SQUARE GB
        { (char) 0x865D, (char) 0xF860, (char) 0x0054, (char) 0x0042 }, // square TB
        { (char) 0x869B, (char) 0x2116 }, // NUMERO SIGN
        { (char) 0x869C, (char) 0x33CD }, // SQUARE KK
        { (char) 0x869D, (char) 0x2121 }, // TELEPHONE SIGN
        { (char) 0x869E, (char) 0xF861, (char) 0x0046, (char) 0x0041, (char) 0x0058 }, // FAX sign # or for Unicode 4.0, (char) 0x213B FACSIMILE SIGN
        { (char) 0x869F, (char) 0x2664 }, // WHITE SPADE SUIT
        { (char) 0x86A0, (char) 0x2667 }, // WHITE CLUB SUIT
        { (char) 0x86A1, (char) 0x2661 }, // WHITE HEART SUIT
        { (char) 0x86A2, (char) 0x2662 }, // WHITE DIAMOND SUIT
        { (char) 0x86A3, (char) 0x2660 }, // BLACK SPADE SUIT
        { (char) 0x86A4, (char) 0x2663 }, // BLACK CLUB SUIT
        { (char) 0x86A5, (char) 0x2665 }, // BLACK HEART SUIT
        { (char) 0x86A6, (char) 0x2666 }, // BLACK DIAMOND SUIT
        { (char) 0x86B3, (char) 0x3020 }, // POSTAL MARK FACE
        { (char) 0x86B4, (char) 0x260E }, // BLACK TELEPHONE
        { (char) 0x86B5, (char) 0x3004 }, // JAPANESE INDUSTRIAL STANDARD SYMBOL
        { (char) 0x86C7, (char) 0x261E }, // WHITE RIGHT POINTING INDEX
        { (char) 0x86C8, (char) 0x261C }, // WHITE LEFT POINTING INDEX
        { (char) 0x86C9, (char) 0x261D }, // WHITE UP POINTING INDEX
        { (char) 0x86CA, (char) 0x261F }, // WHITE DOWN POINTING INDEX
        { (char) 0x86CB, (char) 0x21C6 }, // LEFTWARDS ARROW OVER RIGHTWARDS ARROW
        { (char) 0x86CC, (char) 0x21C4 }, // RIGHTWARDS ARROW OVER LEFTWARDS ARROW
        { (char) 0x86CD, (char) 0x21C5 }, // UPWARDS ARROW LEFTWARDS OF DOWNWARDS ARROW
        { (char) 0x86CE, (char) 0xF860, (char) 0x2193, (char) 0x2191 }, // downwards arrow leftwards of upwards arrow
        { (char) 0x86CF, (char) 0x21E8 }, // RIGHTWARDS WHITE ARROW
        { (char) 0x86D0, (char) 0x21E6 }, // LEFTWARDS WHITE ARROW
        { (char) 0x86D1, (char) 0x21E7 }, // UPWARDS WHITE ARROW
        { (char) 0x86D2, (char) 0x21E9 }, // DOWNWARDS WHITE ARROW
        { (char) 0x86D3, (char) 0x21E8, (char) 0xF87A }, // rightwards black arrow # or 0x27A1 BLACK RIGHTWARDS ARROW
        { (char) 0x86D4, (char) 0x21E6, (char) 0xF87A }, // leftwards black arrow # or for Unicode 4.0, (char) 0x2B05 LEFTWARDS BLACK ARROW
        { (char) 0x86D5, (char) 0x21E7, (char) 0xF87A }, // upwards black arrow # or for Unicode 4.0, (char) 0x2B06 UPWARDS BLACK ARROW
        { (char) 0x86D6, (char) 0x21E9, (char) 0xF87A }, // downwards black arrow # or for Unicode 4.0, (char) 0x2B07 DOWNWARDS BLACK ARROW
        { (char) 0x8740, (char) 0x3230 }, // PARENTHESIZED IDEOGRAPH SUN
        { (char) 0x8741, (char) 0x322A }, // PARENTHESIZED IDEOGRAPH MOON
        { (char) 0x8742, (char) 0x322B }, // PARENTHESIZED IDEOGRAPH FIRE
        { (char) 0x8743, (char) 0x322C }, // PARENTHESIZED IDEOGRAPH WATER
        { (char) 0x8744, (char) 0x322D }, // PARENTHESIZED IDEOGRAPH WOOD
        { (char) 0x8745, (char) 0x322E }, // PARENTHESIZED IDEOGRAPH METAL
        { (char) 0x8746, (char) 0x322F }, // PARENTHESIZED IDEOGRAPH EARTH
        { (char) 0x8747, (char) 0x3240 }, // PARENTHESIZED IDEOGRAPH FESTIVAL
        { (char) 0x8748, (char) 0x3237 }, // PARENTHESIZED IDEOGRAPH CONGRATULATION
        { (char) 0x8749, (char) 0x3242 }, // PARENTHESIZED IDEOGRAPH SELF
        { (char) 0x874A, (char) 0x3243 }, // PARENTHESIZED IDEOGRAPH REACH
        { (char) 0x874B, (char) 0x3239 }, // PARENTHESIZED IDEOGRAPH REPRESENT
        { (char) 0x874C, (char) 0x323A }, // PARENTHESIZED IDEOGRAPH CALL
        { (char) 0x874D, (char) 0x3231 }, // PARENTHESIZED IDEOGRAPH STOCK
        { (char) 0x874E, (char) 0x323E }, // PARENTHESIZED IDEOGRAPH RESOURCE
        { (char) 0x874F, (char) 0x3234 }, // PARENTHESIZED IDEOGRAPH NAME
        { (char) 0x8750, (char) 0x3232 }, // PARENTHESIZED IDEOGRAPH HAVE
        { (char) 0x8751, (char) 0x323B }, // PARENTHESIZED IDEOGRAPH STUDY
        { (char) 0x8752, (char) 0x3236 }, // PARENTHESIZED IDEOGRAPH FINANCIAL
        { (char) 0x8753, (char) 0x3233 }, // PARENTHESIZED IDEOGRAPH SOCIETY
        { (char) 0x8754, (char) 0x3235 }, // PARENTHESIZED IDEOGRAPH SPECIAL
        { (char) 0x8755, (char) 0x323C }, // PARENTHESIZED IDEOGRAPH SUPERVISE
        { (char) 0x8756, (char) 0x323D }, // PARENTHESIZED IDEOGRAPH ENTERPRISE
        { (char) 0x8757, (char) 0x323F }, // PARENTHESIZED IDEOGRAPH ALLIANCE
        { (char) 0x8758, (char) 0x3238 }, // PARENTHESIZED IDEOGRAPH LABOR
        { (char) 0x8791, (char) 0x5927, (char) 0x20DD }, // ideograph big + COMBINING ENCLOSING CIRCLE
        { (char) 0x8792, (char) 0x5C0F, (char) 0x20DD }, // ideograph small + COMBINING ENCLOSING CIRCLE
        { (char) 0x8793, (char) 0x32A4 }, // CIRCLED IDEOGRAPH HIGH
        { (char) 0x8794, (char) 0x32A5 }, // CIRCLED IDEOGRAPH CENTRE
        { (char) 0x8795, (char) 0x32A6 }, // CIRCLED IDEOGRAPH LOW
        { (char) 0x8796, (char) 0x32A7 }, // CIRCLED IDEOGRAPH LEFT
        { (char) 0x8797, (char) 0x32A8 }, // CIRCLED IDEOGRAPH RIGHT
        { (char) 0x8798, (char) 0x32A9 }, // CIRCLED IDEOGRAPH MEDICINE
        { (char) 0x8799, (char) 0x3296 }, // CIRCLED IDEOGRAPH FINANCIAL
        { (char) 0x879A, (char) 0x329D }, // CIRCLED IDEOGRAPH EXCELLENT
        { (char) 0x879B, (char) 0x3298 }, // CIRCLED IDEOGRAPH LABOR
        { (char) 0x879C, (char) 0x329E }, // CIRCLED IDEOGRAPH PRINT
        { (char) 0x879D, (char) 0x63A7, (char) 0x20DD }, // ideograph memo + COMBINING ENCLOSING CIRCLE
        { (char) 0x879E, (char) 0x3299 }, // CIRCLED IDEOGRAPH SECRET
        { (char) 0x879F, (char) 0x3349 }, // SQUARE MIRI
        { (char) 0x87A0, (char) 0x3322 }, // SQUARE SENTI
        { (char) 0x87A1, (char) 0x334D }, // SQUARE MEETORU
        { (char) 0x87A2, (char) 0x3314 }, // SQUARE KIRO
        { (char) 0x87A3, (char) 0x3316 }, // SQUARE KIROMEETORU
        { (char) 0x87A4, (char) 0x3305 }, // SQUARE INTI
        { (char) 0x87A5, (char) 0x3333 }, // SQUARE HUIITO
        { (char) 0x87A6, (char) 0x334E }, // SQUARE YAADO
        { (char) 0x87A7, (char) 0x3303 }, // SQUARE AARU
        { (char) 0x87A8, (char) 0x3336 }, // SQUARE HEKUTAARU
        { (char) 0x87A9, (char) 0x3318 }, // SQUARE GURAMU
        { (char) 0x87AA, (char) 0x3315 }, // SQUARE KIROGURAMU
        { (char) 0x87AB, (char) 0x3327 }, // SQUARE TON
        { (char) 0x87AC, (char) 0x3351 }, // SQUARE RITTORU
        { (char) 0x87AD, (char) 0x334A }, // SQUARE MIRIBAARU
        { (char) 0x87AE, (char) 0x3339 }, // SQUARE HERUTU
        { (char) 0x87AF, (char) 0x3357 }, // SQUARE WATTO
        { (char) 0x87B0, (char) 0x330D }, // SQUARE KARORII
        { (char) 0x87B1, (char) 0x3342 }, // SQUARE HOON
        { (char) 0x87B2, (char) 0x3323 }, // SQUARE SENTO
        { (char) 0x87B3, (char) 0x3326 }, // SQUARE DORU
        { (char) 0x87B4, (char) 0x333B }, // SQUARE PEEZI
        { (char) 0x87B5, (char) 0x332B }, // SQUARE PAASENTO
        { (char) 0x87BD, (char) 0x3300 }, // SQUARE APAATO
        { (char) 0x87BE, (char) 0x331E }, // SQUARE KOOPO
        { (char) 0x87BF, (char) 0x332A }, // SQUARE HAITU
        { (char) 0x87C0, (char) 0x3331 }, // SQUARE BIRU
        { (char) 0x87C1, (char) 0x3347 }, // SQUARE MANSYON
        { (char) 0x87E5, (char) 0x337E }, // SQUARE ERA NAME MEIZI
        { (char) 0x87E6, (char) 0x337D }, // SQUARE ERA NAME TAISYOU
        { (char) 0x87E7, (char) 0x337C }, // SQUARE ERA NAME SYOUWA
        { (char) 0x87E8, (char) 0x337B }, // SQUARE ERA NAME HEISEI
        { (char) 0x87FA, (char) 0x337F }, // SQUARE CORPORATION
        { (char) 0x87FB, (char) 0xF862, (char) 0x6709, (char) 0x9650, (char) 0x4F1A, (char) 0x793E }, // square "limited company, ltd. [yuugen gaisha]"
        { (char) 0x87FC, (char) 0xF862, (char) 0x8CA1, (char) 0x56E3, (char) 0x6CD5, (char) 0x4EBA }, // square "foundation [zaidan houjin]"
        { (char) 0x8840, (char) 0x222E }, // CONTOUR INTEGRAL
        { (char) 0x8841, (char) 0x221F }, // RIGHT ANGLE
        { (char) 0x8842, (char) 0x22BF }, // RIGHT TRIANGLE
        { (char) 0x8854, (char) 0x301D }, // REVERSED DOUBLE PRIME QUOTATION MARK
        { (char) 0x8855, (char) 0x301F }, // LOW DOUBLE PRIME QUOTATION MARK
        { (char) 0x8868, (char) 0x3094 }, // HIRAGANA LETTER VU
        { (char) 0x886A, (char) 0x30F7 }, // KATAKANA LETTER VA
        { (char) 0x886B, (char) 0x30F8 }, // KATAKANA LETTER VI
        { (char) 0x886C, (char) 0x30F9 }, // KATAKANA LETTER VE
        { (char) 0x886D, (char) 0x30FA }, // KATAKANA LETTER VO

        // Standard
        { (char) 0x889F, (char) 0x4E9C }, // <CJK>
        { (char) 0x88A0, (char) 0x5516 }, // <CJK>
        { (char) 0x88A1, (char) 0x5A03 }, // <CJK>
        { (char) 0x88A2, (char) 0x963F }, // <CJK>
        { (char) 0x88A3, (char) 0x54C0 }, // <CJK>
        { (char) 0x88A4, (char) 0x611B }, // <CJK>
        { (char) 0x88A5, (char) 0x6328 }, // <CJK>
        { (char) 0x88A6, (char) 0x59F6 }, // <CJK>
        { (char) 0x88A7, (char) 0x9022 }, // <CJK>
        { (char) 0x88A8, (char) 0x8475 }, // <CJK>
        { (char) 0x88A9, (char) 0x831C }, // <CJK>
        { (char) 0x88AA, (char) 0x7A50 }, // <CJK>
        { (char) 0x88AB, (char) 0x60AA }, // <CJK>
        { (char) 0x88AC, (char) 0x63E1 }, // <CJK>
        { (char) 0x88AD, (char) 0x6E25 }, // <CJK>
        { (char) 0x88AE, (char) 0x65ED }, // <CJK>
        { (char) 0x88AF, (char) 0x8466 }, // <CJK>
        { (char) 0x88B0, (char) 0x82A6 }, // <CJK>
        { (char) 0x88B1, (char) 0x9BF5 }, // <CJK>
        { (char) 0x88B2, (char) 0x6893 }, // <CJK>
        { (char) 0x88B3, (char) 0x5727 }, // <CJK>
        { (char) 0x88B4, (char) 0x65A1 }, // <CJK>
        { (char) 0x88B5, (char) 0x6271 }, // <CJK>
        { (char) 0x88B6, (char) 0x5B9B }, // <CJK>
        { (char) 0x88B7, (char) 0x59D0 }, // <CJK>
        { (char) 0x88B8, (char) 0x867B }, // <CJK>
        { (char) 0x88B9, (char) 0x98F4 }, // <CJK>
        { (char) 0x88BA, (char) 0x7D62 }, // <CJK>
        { (char) 0x88BB, (char) 0x7DBE }, // <CJK>
        { (char) 0x88BC, (char) 0x9B8E }, // <CJK>
        { (char) 0x88BD, (char) 0x6216 }, // <CJK>
        { (char) 0x88BE, (char) 0x7C9F }, // <CJK>
        { (char) 0x88BF, (char) 0x88B7 }, // <CJK>
        { (char) 0x88C0, (char) 0x5B89 }, // <CJK>
        { (char) 0x88C1, (char) 0x5EB5 }, // <CJK>
        { (char) 0x88C2, (char) 0x6309 }, // <CJK>
        { (char) 0x88C3, (char) 0x6697 }, // <CJK>
        { (char) 0x88C4, (char) 0x6848 }, // <CJK>
        { (char) 0x88C5, (char) 0x95C7 }, // <CJK>
        { (char) 0x88C6, (char) 0x978D }, // <CJK>
        { (char) 0x88C7, (char) 0x674F }, // <CJK>
        { (char) 0x88C8, (char) 0x4EE5 }, // <CJK>
        { (char) 0x88C9, (char) 0x4F0A }, // <CJK>
        { (char) 0x88CA, (char) 0x4F4D }, // <CJK>
        { (char) 0x88CB, (char) 0x4F9D }, // <CJK>
        { (char) 0x88CC, (char) 0x5049 }, // <CJK>
        { (char) 0x88CD, (char) 0x56F2 }, // <CJK>
        { (char) 0x88CE, (char) 0x5937 }, // <CJK>
        { (char) 0x88CF, (char) 0x59D4 }, // <CJK>
        { (char) 0x88D0, (char) 0x5A01 }, // <CJK>
        { (char) 0x88D1, (char) 0x5C09 }, // <CJK>
        { (char) 0x88D2, (char) 0x60DF }, // <CJK>
        { (char) 0x88D3, (char) 0x610F }, // <CJK>
        { (char) 0x88D4, (char) 0x6170 }, // <CJK>
        { (char) 0x88D5, (char) 0x6613 }, // <CJK>
        { (char) 0x88D6, (char) 0x6905 }, // <CJK>
        { (char) 0x88D7, (char) 0x70BA }, // <CJK>
        { (char) 0x88D8, (char) 0x754F }, // <CJK>
        { (char) 0x88D9, (char) 0x7570 }, // <CJK>
        { (char) 0x88DA, (char) 0x79FB }, // <CJK>
        { (char) 0x88DB, (char) 0x7DAD }, // <CJK>
        { (char) 0x88DC, (char) 0x7DEF }, // <CJK>
        { (char) 0x88DD, (char) 0x80C3 }, // <CJK>
        { (char) 0x88DE, (char) 0x840E }, // <CJK>
        { (char) 0x88DF, (char) 0x8863 }, // <CJK>
        { (char) 0x88E0, (char) 0x8B02 }, // <CJK>
        { (char) 0x88E1, (char) 0x9055 }, // <CJK>
        { (char) 0x88E2, (char) 0x907A }, // <CJK>
        { (char) 0x88E3, (char) 0x533B }, // <CJK>
        { (char) 0x88E4, (char) 0x4E95 }, // <CJK>
        { (char) 0x88E5, (char) 0x4EA5 }, // <CJK>
        { (char) 0x88E6, (char) 0x57DF }, // <CJK>
        { (char) 0x88E7, (char) 0x80B2 }, // <CJK>
        { (char) 0x88E8, (char) 0x90C1 }, // <CJK>
        { (char) 0x88E9, (char) 0x78EF }, // <CJK>
        { (char) 0x88EA, (char) 0x4E00 }, // <CJK>
        { (char) 0x88EB, (char) 0x58F1 }, // <CJK>
        { (char) 0x88EC, (char) 0x6EA2 }, // <CJK>
        { (char) 0x88ED, (char) 0x9038 }, // <CJK>
        { (char) 0x88EE, (char) 0x7A32 }, // <CJK>
        { (char) 0x88EF, (char) 0x8328 }, // <CJK>
        { (char) 0x88F0, (char) 0x828B }, // <CJK>
        { (char) 0x88F1, (char) 0x9C2F }, // <CJK>
        { (char) 0x88F2, (char) 0x5141 }, // <CJK>
        { (char) 0x88F3, (char) 0x5370 }, // <CJK>
        { (char) 0x88F4, (char) 0x54BD }, // <CJK>
        { (char) 0x88F5, (char) 0x54E1 }, // <CJK>
        { (char) 0x88F6, (char) 0x56E0 }, // <CJK>
        { (char) 0x88F7, (char) 0x59FB }, // <CJK>
        { (char) 0x88F8, (char) 0x5F15 }, // <CJK>
        { (char) 0x88F9, (char) 0x98F2 }, // <CJK>
        { (char) 0x88FA, (char) 0x6DEB }, // <CJK>
        { (char) 0x88FB, (char) 0x80E4 }, // <CJK>
        { (char) 0x88FC, (char) 0x852D }, // <CJK>
        { (char) 0x8940, (char) 0x9662 }, // <CJK>
        { (char) 0x8941, (char) 0x9670 }, // <CJK>
        { (char) 0x8942, (char) 0x96A0 }, // <CJK>
        { (char) 0x8943, (char) 0x97FB }, // <CJK>
        { (char) 0x8944, (char) 0x540B }, // <CJK>
        { (char) 0x8945, (char) 0x53F3 }, // <CJK>
        { (char) 0x8946, (char) 0x5B87 }, // <CJK>
        { (char) 0x8947, (char) 0x70CF }, // <CJK>
        { (char) 0x8948, (char) 0x7FBD }, // <CJK>
        { (char) 0x8949, (char) 0x8FC2 }, // <CJK>
        { (char) 0x894A, (char) 0x96E8 }, // <CJK>
        { (char) 0x894B, (char) 0x536F }, // <CJK>
        { (char) 0x894C, (char) 0x9D5C }, // <CJK>
        { (char) 0x894D, (char) 0x7ABA }, // <CJK>
        { (char) 0x894E, (char) 0x4E11 }, // <CJK>
        { (char) 0x894F, (char) 0x7893 }, // <CJK>
        { (char) 0x8950, (char) 0x81FC }, // <CJK>
        { (char) 0x8951, (char) 0x6E26 }, // <CJK>
        { (char) 0x8952, (char) 0x5618 }, // <CJK>
        { (char) 0x8953, (char) 0x5504 }, // <CJK>
        { (char) 0x8954, (char) 0x6B1D }, // <CJK>
        { (char) 0x8955, (char) 0x851A }, // <CJK>
        { (char) 0x8956, (char) 0x9C3B }, // <CJK>
        { (char) 0x8957, (char) 0x59E5 }, // <CJK>
        { (char) 0x8958, (char) 0x53A9 }, // <CJK>
        { (char) 0x8959, (char) 0x6D66 }, // <CJK>
        { (char) 0x895A, (char) 0x74DC }, // <CJK>
        { (char) 0x895B, (char) 0x958F }, // <CJK>
        { (char) 0x895C, (char) 0x5642 }, // <CJK>
        { (char) 0x895D, (char) 0x4E91 }, // <CJK>
        { (char) 0x895E, (char) 0x904B }, // <CJK>
        { (char) 0x895F, (char) 0x96F2 }, // <CJK>
        { (char) 0x8960, (char) 0x834F }, // <CJK>
        { (char) 0x8961, (char) 0x990C }, // <CJK>
        { (char) 0x8962, (char) 0x53E1 }, // <CJK>
        { (char) 0x8963, (char) 0x55B6 }, // <CJK>
        { (char) 0x8964, (char) 0x5B30 }, // <CJK>
        { (char) 0x8965, (char) 0x5F71 }, // <CJK>
        { (char) 0x8966, (char) 0x6620 }, // <CJK>
        { (char) 0x8967, (char) 0x66F3 }, // <CJK>
        { (char) 0x8968, (char) 0x6804 }, // <CJK>
        { (char) 0x8969, (char) 0x6C38 }, // <CJK>
        { (char) 0x896A, (char) 0x6CF3 }, // <CJK>
        { (char) 0x896B, (char) 0x6D29 }, // <CJK>
        { (char) 0x896C, (char) 0x745B }, // <CJK>
        { (char) 0x896D, (char) 0x76C8 }, // <CJK>
        { (char) 0x896E, (char) 0x7A4E }, // <CJK>
        { (char) 0x896F, (char) 0x9834 }, // <CJK>
        { (char) 0x8970, (char) 0x82F1 }, // <CJK>
        { (char) 0x8971, (char) 0x885B }, // <CJK>
        { (char) 0x8972, (char) 0x8A60 }, // <CJK>
        { (char) 0x8973, (char) 0x92ED }, // <CJK>
        { (char) 0x8974, (char) 0x6DB2 }, // <CJK>
        { (char) 0x8975, (char) 0x75AB }, // <CJK>
        { (char) 0x8976, (char) 0x76CA }, // <CJK>
        { (char) 0x8977, (char) 0x99C5 }, // <CJK>
        { (char) 0x8978, (char) 0x60A6 }, // <CJK>
        { (char) 0x8979, (char) 0x8B01 }, // <CJK>
        { (char) 0x897A, (char) 0x8D8A }, // <CJK>
        { (char) 0x897B, (char) 0x95B2 }, // <CJK>
        { (char) 0x897C, (char) 0x698E }, // <CJK>
        { (char) 0x897D, (char) 0x53AD }, // <CJK>
        { (char) 0x897E, (char) 0x5186 }, // <CJK>
        { (char) 0x8980, (char) 0x5712 }, // <CJK>
        { (char) 0x8981, (char) 0x5830 }, // <CJK>
        { (char) 0x8982, (char) 0x5944 }, // <CJK>
        { (char) 0x8983, (char) 0x5BB4 }, // <CJK>
        { (char) 0x8984, (char) 0x5EF6 }, // <CJK>
        { (char) 0x8985, (char) 0x6028 }, // <CJK>
        { (char) 0x8986, (char) 0x63A9 }, // <CJK>
        { (char) 0x8987, (char) 0x63F4 }, // <CJK>
        { (char) 0x8988, (char) 0x6CBF }, // <CJK>
        { (char) 0x8989, (char) 0x6F14 }, // <CJK>
        { (char) 0x898A, (char) 0x708E }, // <CJK>
        { (char) 0x898B, (char) 0x7114 }, // <CJK>
        { (char) 0x898C, (char) 0x7159 }, // <CJK>
        { (char) 0x898D, (char) 0x71D5 }, // <CJK>
        { (char) 0x898E, (char) 0x733F }, // <CJK>
        { (char) 0x898F, (char) 0x7E01 }, // <CJK>
        { (char) 0x8990, (char) 0x8276 }, // <CJK>
        { (char) 0x8991, (char) 0x82D1 }, // <CJK>
        { (char) 0x8992, (char) 0x8597 }, // <CJK>
        { (char) 0x8993, (char) 0x9060 }, // <CJK>
        { (char) 0x8994, (char) 0x925B }, // <CJK>
        { (char) 0x8995, (char) 0x9D1B }, // <CJK>
        { (char) 0x8996, (char) 0x5869 }, // <CJK>
        { (char) 0x8997, (char) 0x65BC }, // <CJK>
        { (char) 0x8998, (char) 0x6C5A }, // <CJK>
        { (char) 0x8999, (char) 0x7525 }, // <CJK>
        { (char) 0x899A, (char) 0x51F9 }, // <CJK>
        { (char) 0x899B, (char) 0x592E }, // <CJK>
        { (char) 0x899C, (char) 0x5965 }, // <CJK>
        { (char) 0x899D, (char) 0x5F80 }, // <CJK>
        { (char) 0x899E, (char) 0x5FDC }, // <CJK>
        { (char) 0x899F, (char) 0x62BC }, // <CJK>
        { (char) 0x89A0, (char) 0x65FA }, // <CJK>
        { (char) 0x89A1, (char) 0x6A2A }, // <CJK>
        { (char) 0x89A2, (char) 0x6B27 }, // <CJK>
        { (char) 0x89A3, (char) 0x6BB4 }, // <CJK>
        { (char) 0x89A4, (char) 0x738B }, // <CJK>
        { (char) 0x89A5, (char) 0x7FC1 }, // <CJK>
        { (char) 0x89A6, (char) 0x8956 }, // <CJK>
        { (char) 0x89A7, (char) 0x9D2C }, // <CJK>
        { (char) 0x89A8, (char) 0x9D0E }, // <CJK>
        { (char) 0x89A9, (char) 0x9EC4 }, // <CJK>
        { (char) 0x89AA, (char) 0x5CA1 }, // <CJK>
        { (char) 0x89AB, (char) 0x6C96 }, // <CJK>
        { (char) 0x89AC, (char) 0x837B }, // <CJK>
        { (char) 0x89AD, (char) 0x5104 }, // <CJK>
        { (char) 0x89AE, (char) 0x5C4B }, // <CJK>
        { (char) 0x89AF, (char) 0x61B6 }, // <CJK>
        { (char) 0x89B0, (char) 0x81C6 }, // <CJK>
        { (char) 0x89B1, (char) 0x6876 }, // <CJK>
        { (char) 0x89B2, (char) 0x7261 }, // <CJK>
        { (char) 0x89B3, (char) 0x4E59 }, // <CJK>
        { (char) 0x89B4, (char) 0x4FFA }, // <CJK>
        { (char) 0x89B5, (char) 0x5378 }, // <CJK>
        { (char) 0x89B6, (char) 0x6069 }, // <CJK>
        { (char) 0x89B7, (char) 0x6E29 }, // <CJK>
        { (char) 0x89B8, (char) 0x7A4F }, // <CJK>
        { (char) 0x89B9, (char) 0x97F3 }, // <CJK>
        { (char) 0x89BA, (char) 0x4E0B }, // <CJK>
        { (char) 0x89BB, (char) 0x5316 }, // <CJK>
        { (char) 0x89BC, (char) 0x4EEE }, // <CJK>
        { (char) 0x89BD, (char) 0x4F55 }, // <CJK>
        { (char) 0x89BE, (char) 0x4F3D }, // <CJK>
        { (char) 0x89BF, (char) 0x4FA1 }, // <CJK>
        { (char) 0x89C0, (char) 0x4F73 }, // <CJK>
        { (char) 0x89C1, (char) 0x52A0 }, // <CJK>
        { (char) 0x89C2, (char) 0x53EF }, // <CJK>
        { (char) 0x89C3, (char) 0x5609 }, // <CJK>
        { (char) 0x89C4, (char) 0x590F }, // <CJK>
        { (char) 0x89C5, (char) 0x5AC1 }, // <CJK>
        { (char) 0x89C6, (char) 0x5BB6 }, // <CJK>
        { (char) 0x89C7, (char) 0x5BE1 }, // <CJK>
        { (char) 0x89C8, (char) 0x79D1 }, // <CJK>
        { (char) 0x89C9, (char) 0x6687 }, // <CJK>
        { (char) 0x89CA, (char) 0x679C }, // <CJK>
        { (char) 0x89CB, (char) 0x67B6 }, // <CJK>
        { (char) 0x89CC, (char) 0x6B4C }, // <CJK>
        { (char) 0x89CD, (char) 0x6CB3 }, // <CJK>
        { (char) 0x89CE, (char) 0x706B }, // <CJK>
        { (char) 0x89CF, (char) 0x73C2 }, // <CJK>
        { (char) 0x89D0, (char) 0x798D }, // <CJK>
        { (char) 0x89D1, (char) 0x79BE }, // <CJK>
        { (char) 0x89D2, (char) 0x7A3C }, // <CJK>
        { (char) 0x89D3, (char) 0x7B87 }, // <CJK>
        { (char) 0x89D4, (char) 0x82B1 }, // <CJK>
        { (char) 0x89D5, (char) 0x82DB }, // <CJK>
        { (char) 0x89D6, (char) 0x8304 }, // <CJK>
        { (char) 0x89D7, (char) 0x8377 }, // <CJK>
        { (char) 0x89D8, (char) 0x83EF }, // <CJK>
        { (char) 0x89D9, (char) 0x83D3 }, // <CJK>
        { (char) 0x89DA, (char) 0x8766 }, // <CJK>
        { (char) 0x89DB, (char) 0x8AB2 }, // <CJK>
        { (char) 0x89DC, (char) 0x5629 }, // <CJK>
        { (char) 0x89DD, (char) 0x8CA8 }, // <CJK>
        { (char) 0x89DE, (char) 0x8FE6 }, // <CJK>
        { (char) 0x89DF, (char) 0x904E }, // <CJK>
        { (char) 0x89E0, (char) 0x971E }, // <CJK>
        { (char) 0x89E1, (char) 0x868A }, // <CJK>
        { (char) 0x89E2, (char) 0x4FC4 }, // <CJK>
        { (char) 0x89E3, (char) 0x5CE8 }, // <CJK>
        { (char) 0x89E4, (char) 0x6211 }, // <CJK>
        { (char) 0x89E5, (char) 0x7259 }, // <CJK>
        { (char) 0x89E6, (char) 0x753B }, // <CJK>
        { (char) 0x89E7, (char) 0x81E5 }, // <CJK>
        { (char) 0x89E8, (char) 0x82BD }, // <CJK>
        { (char) 0x89E9, (char) 0x86FE }, // <CJK>
        { (char) 0x89EA, (char) 0x8CC0 }, // <CJK>
        { (char) 0x89EB, (char) 0x96C5 }, // <CJK>
        { (char) 0x89EC, (char) 0x9913 }, // <CJK>
        { (char) 0x89ED, (char) 0x99D5 }, // <CJK>
        { (char) 0x89EE, (char) 0x4ECB }, // <CJK>
        { (char) 0x89EF, (char) 0x4F1A }, // <CJK>
        { (char) 0x89F0, (char) 0x89E3 }, // <CJK>
        { (char) 0x89F1, (char) 0x56DE }, // <CJK>
        { (char) 0x89F2, (char) 0x584A }, // <CJK>
        { (char) 0x89F3, (char) 0x58CA }, // <CJK>
        { (char) 0x89F4, (char) 0x5EFB }, // <CJK>
        { (char) 0x89F5, (char) 0x5FEB }, // <CJK>
        { (char) 0x89F6, (char) 0x602A }, // <CJK>
        { (char) 0x89F7, (char) 0x6094 }, // <CJK>
        { (char) 0x89F8, (char) 0x6062 }, // <CJK>
        { (char) 0x89F9, (char) 0x61D0 }, // <CJK>
        { (char) 0x89FA, (char) 0x6212 }, // <CJK>
        { (char) 0x89FB, (char) 0x62D0 }, // <CJK>
        { (char) 0x89FC, (char) 0x6539 }, // <CJK>
        { (char) 0x8A40, (char) 0x9B41 }, // <CJK>
        { (char) 0x8A41, (char) 0x6666 }, // <CJK>
        { (char) 0x8A42, (char) 0x68B0 }, // <CJK>
        { (char) 0x8A43, (char) 0x6D77 }, // <CJK>
        { (char) 0x8A44, (char) 0x7070 }, // <CJK>
        { (char) 0x8A45, (char) 0x754C }, // <CJK>
        { (char) 0x8A46, (char) 0x7686 }, // <CJK>
        { (char) 0x8A47, (char) 0x7D75 }, // <CJK>
        { (char) 0x8A48, (char) 0x82A5 }, // <CJK>
        { (char) 0x8A49, (char) 0x87F9 }, // <CJK>
        { (char) 0x8A4A, (char) 0x958B }, // <CJK>
        { (char) 0x8A4B, (char) 0x968E }, // <CJK>
        { (char) 0x8A4C, (char) 0x8C9D }, // <CJK>
        { (char) 0x8A4D, (char) 0x51F1 }, // <CJK>
        { (char) 0x8A4E, (char) 0x52BE }, // <CJK>
        { (char) 0x8A4F, (char) 0x5916 }, // <CJK>
        { (char) 0x8A50, (char) 0x54B3 }, // <CJK>
        { (char) 0x8A51, (char) 0x5BB3 }, // <CJK>
        { (char) 0x8A52, (char) 0x5D16 }, // <CJK>
        { (char) 0x8A53, (char) 0x6168 }, // <CJK>
        { (char) 0x8A54, (char) 0x6982 }, // <CJK>
        { (char) 0x8A55, (char) 0x6DAF }, // <CJK>
        { (char) 0x8A56, (char) 0x788D }, // <CJK>
        { (char) 0x8A57, (char) 0x84CB }, // <CJK>
        { (char) 0x8A58, (char) 0x8857 }, // <CJK>
        { (char) 0x8A59, (char) 0x8A72 }, // <CJK>
        { (char) 0x8A5A, (char) 0x93A7 }, // <CJK>
        { (char) 0x8A5B, (char) 0x9AB8 }, // <CJK>
        { (char) 0x8A5C, (char) 0x6D6C }, // <CJK>
        { (char) 0x8A5D, (char) 0x99A8 }, // <CJK>
        { (char) 0x8A5E, (char) 0x86D9 }, // <CJK>
        { (char) 0x8A5F, (char) 0x57A3 }, // <CJK>
        { (char) 0x8A60, (char) 0x67FF }, // <CJK>
        { (char) 0x8A61, (char) 0x86CE }, // <CJK>
        { (char) 0x8A62, (char) 0x920E }, // <CJK>
        { (char) 0x8A63, (char) 0x5283 }, // <CJK>
        { (char) 0x8A64, (char) 0x5687 }, // <CJK>
        { (char) 0x8A65, (char) 0x5404 }, // <CJK>
        { (char) 0x8A66, (char) 0x5ED3 }, // <CJK>
        { (char) 0x8A67, (char) 0x62E1 }, // <CJK>
        { (char) 0x8A68, (char) 0x64B9 }, // <CJK>
        { (char) 0x8A69, (char) 0x683C }, // <CJK>
        { (char) 0x8A6A, (char) 0x6838 }, // <CJK>
        { (char) 0x8A6B, (char) 0x6BBB }, // <CJK>
        { (char) 0x8A6C, (char) 0x7372 }, // <CJK>
        { (char) 0x8A6D, (char) 0x78BA }, // <CJK>
        { (char) 0x8A6E, (char) 0x7A6B }, // <CJK>
        { (char) 0x8A6F, (char) 0x899A }, // <CJK>
        { (char) 0x8A70, (char) 0x89D2 }, // <CJK>
        { (char) 0x8A71, (char) 0x8D6B }, // <CJK>
        { (char) 0x8A72, (char) 0x8F03 }, // <CJK>
        { (char) 0x8A73, (char) 0x90ED }, // <CJK>
        { (char) 0x8A74, (char) 0x95A3 }, // <CJK>
        { (char) 0x8A75, (char) 0x9694 }, // <CJK>
        { (char) 0x8A76, (char) 0x9769 }, // <CJK>
        { (char) 0x8A77, (char) 0x5B66 }, // <CJK>
        { (char) 0x8A78, (char) 0x5CB3 }, // <CJK>
        { (char) 0x8A79, (char) 0x697D }, // <CJK>
        { (char) 0x8A7A, (char) 0x984D }, // <CJK>
        { (char) 0x8A7B, (char) 0x984E }, // <CJK>
        { (char) 0x8A7C, (char) 0x639B }, // <CJK>
        { (char) 0x8A7D, (char) 0x7B20 }, // <CJK>
        { (char) 0x8A7E, (char) 0x6A2B }, // <CJK>
        { (char) 0x8A80, (char) 0x6A7F }, // <CJK>
        { (char) 0x8A81, (char) 0x68B6 }, // <CJK>
        { (char) 0x8A82, (char) 0x9C0D }, // <CJK>
        { (char) 0x8A83, (char) 0x6F5F }, // <CJK>
        { (char) 0x8A84, (char) 0x5272 }, // <CJK>
        { (char) 0x8A85, (char) 0x559D }, // <CJK>
        { (char) 0x8A86, (char) 0x6070 }, // <CJK>
        { (char) 0x8A87, (char) 0x62EC }, // <CJK>
        { (char) 0x8A88, (char) 0x6D3B }, // <CJK>
        { (char) 0x8A89, (char) 0x6E07 }, // <CJK>
        { (char) 0x8A8A, (char) 0x6ED1 }, // <CJK>
        { (char) 0x8A8B, (char) 0x845B }, // <CJK>
        { (char) 0x8A8C, (char) 0x8910 }, // <CJK>
        { (char) 0x8A8D, (char) 0x8F44 }, // <CJK>
        { (char) 0x8A8E, (char) 0x4E14 }, // <CJK>
        { (char) 0x8A8F, (char) 0x9C39 }, // <CJK>
        { (char) 0x8A90, (char) 0x53F6 }, // <CJK>
        { (char) 0x8A91, (char) 0x691B }, // <CJK>
        { (char) 0x8A92, (char) 0x6A3A }, // <CJK>
        { (char) 0x8A93, (char) 0x9784 }, // <CJK>
        { (char) 0x8A94, (char) 0x682A }, // <CJK>
        { (char) 0x8A95, (char) 0x515C }, // <CJK>
        { (char) 0x8A96, (char) 0x7AC3 }, // <CJK>
        { (char) 0x8A97, (char) 0x84B2 }, // <CJK>
        { (char) 0x8A98, (char) 0x91DC }, // <CJK>
        { (char) 0x8A99, (char) 0x938C }, // <CJK>
        { (char) 0x8A9A, (char) 0x565B }, // <CJK>
        { (char) 0x8A9B, (char) 0x9D28 }, // <CJK>
        { (char) 0x8A9C, (char) 0x6822 }, // <CJK>
        { (char) 0x8A9D, (char) 0x8305 }, // <CJK>
        { (char) 0x8A9E, (char) 0x8431 }, // <CJK>
        { (char) 0x8A9F, (char) 0x7CA5 }, // <CJK>
        { (char) 0x8AA0, (char) 0x5208 }, // <CJK>
        { (char) 0x8AA1, (char) 0x82C5 }, // <CJK>
        { (char) 0x8AA2, (char) 0x74E6 }, // <CJK>
        { (char) 0x8AA3, (char) 0x4E7E }, // <CJK>
        { (char) 0x8AA4, (char) 0x4F83 }, // <CJK>
        { (char) 0x8AA5, (char) 0x51A0 }, // <CJK>
        { (char) 0x8AA6, (char) 0x5BD2 }, // <CJK>
        { (char) 0x8AA7, (char) 0x520A }, // <CJK>
        { (char) 0x8AA8, (char) 0x52D8 }, // <CJK>
        { (char) 0x8AA9, (char) 0x52E7 }, // <CJK>
        { (char) 0x8AAA, (char) 0x5DFB }, // <CJK>
        { (char) 0x8AAB, (char) 0x559A }, // <CJK>
        { (char) 0x8AAC, (char) 0x582A }, // <CJK>
        { (char) 0x8AAD, (char) 0x59E6 }, // <CJK>
        { (char) 0x8AAE, (char) 0x5B8C }, // <CJK>
        { (char) 0x8AAF, (char) 0x5B98 }, // <CJK>
        { (char) 0x8AB0, (char) 0x5BDB }, // <CJK>
        { (char) 0x8AB1, (char) 0x5E72 }, // <CJK>
        { (char) 0x8AB2, (char) 0x5E79 }, // <CJK>
        { (char) 0x8AB3, (char) 0x60A3 }, // <CJK>
        { (char) 0x8AB4, (char) 0x611F }, // <CJK>
        { (char) 0x8AB5, (char) 0x6163 }, // <CJK>
        { (char) 0x8AB6, (char) 0x61BE }, // <CJK>
        { (char) 0x8AB7, (char) 0x63DB }, // <CJK>
        { (char) 0x8AB8, (char) 0x6562 }, // <CJK>
        { (char) 0x8AB9, (char) 0x67D1 }, // <CJK>
        { (char) 0x8ABA, (char) 0x6853 }, // <CJK>
        { (char) 0x8ABB, (char) 0x68FA }, // <CJK>
        { (char) 0x8ABC, (char) 0x6B3E }, // <CJK>
        { (char) 0x8ABD, (char) 0x6B53 }, // <CJK>
        { (char) 0x8ABE, (char) 0x6C57 }, // <CJK>
        { (char) 0x8ABF, (char) 0x6F22 }, // <CJK>
        { (char) 0x8AC0, (char) 0x6F97 }, // <CJK>
        { (char) 0x8AC1, (char) 0x6F45 }, // <CJK>
        { (char) 0x8AC2, (char) 0x74B0 }, // <CJK>
        { (char) 0x8AC3, (char) 0x7518 }, // <CJK>
        { (char) 0x8AC4, (char) 0x76E3 }, // <CJK>
        { (char) 0x8AC5, (char) 0x770B }, // <CJK>
        { (char) 0x8AC6, (char) 0x7AFF }, // <CJK>
        { (char) 0x8AC7, (char) 0x7BA1 }, // <CJK>
        { (char) 0x8AC8, (char) 0x7C21 }, // <CJK>
        { (char) 0x8AC9, (char) 0x7DE9 }, // <CJK>
        { (char) 0x8ACA, (char) 0x7F36 }, // <CJK>
        { (char) 0x8ACB, (char) 0x7FF0 }, // <CJK>
        { (char) 0x8ACC, (char) 0x809D }, // <CJK>
        { (char) 0x8ACD, (char) 0x8266 }, // <CJK>
        { (char) 0x8ACE, (char) 0x839E }, // <CJK>
        { (char) 0x8ACF, (char) 0x89B3 }, // <CJK>
        { (char) 0x8AD0, (char) 0x8ACC }, // <CJK>
        { (char) 0x8AD1, (char) 0x8CAB }, // <CJK>
        { (char) 0x8AD2, (char) 0x9084 }, // <CJK>
        { (char) 0x8AD3, (char) 0x9451 }, // <CJK>
        { (char) 0x8AD4, (char) 0x9593 }, // <CJK>
        { (char) 0x8AD5, (char) 0x9591 }, // <CJK>
        { (char) 0x8AD6, (char) 0x95A2 }, // <CJK>
        { (char) 0x8AD7, (char) 0x9665 }, // <CJK>
        { (char) 0x8AD8, (char) 0x97D3 }, // <CJK>
        { (char) 0x8AD9, (char) 0x9928 }, // <CJK>
        { (char) 0x8ADA, (char) 0x8218 }, // <CJK>
        { (char) 0x8ADB, (char) 0x4E38 }, // <CJK>
        { (char) 0x8ADC, (char) 0x542B }, // <CJK>
        { (char) 0x8ADD, (char) 0x5CB8 }, // <CJK>
        { (char) 0x8ADE, (char) 0x5DCC }, // <CJK>
        { (char) 0x8ADF, (char) 0x73A9 }, // <CJK>
        { (char) 0x8AE0, (char) 0x764C }, // <CJK>
        { (char) 0x8AE1, (char) 0x773C }, // <CJK>
        { (char) 0x8AE2, (char) 0x5CA9 }, // <CJK>
        { (char) 0x8AE3, (char) 0x7FEB }, // <CJK>
        { (char) 0x8AE4, (char) 0x8D0B }, // <CJK>
        { (char) 0x8AE5, (char) 0x96C1 }, // <CJK>
        { (char) 0x8AE6, (char) 0x9811 }, // <CJK>
        { (char) 0x8AE7, (char) 0x9854 }, // <CJK>
        { (char) 0x8AE8, (char) 0x9858 }, // <CJK>
        { (char) 0x8AE9, (char) 0x4F01 }, // <CJK>
        { (char) 0x8AEA, (char) 0x4F0E }, // <CJK>
        { (char) 0x8AEB, (char) 0x5371 }, // <CJK>
        { (char) 0x8AEC, (char) 0x559C }, // <CJK>
        { (char) 0x8AED, (char) 0x5668 }, // <CJK>
        { (char) 0x8AEE, (char) 0x57FA }, // <CJK>
        { (char) 0x8AEF, (char) 0x5947 }, // <CJK>
        { (char) 0x8AF0, (char) 0x5B09 }, // <CJK>
        { (char) 0x8AF1, (char) 0x5BC4 }, // <CJK>
        { (char) 0x8AF2, (char) 0x5C90 }, // <CJK>
        { (char) 0x8AF3, (char) 0x5E0C }, // <CJK>
        { (char) 0x8AF4, (char) 0x5E7E }, // <CJK>
        { (char) 0x8AF5, (char) 0x5FCC }, // <CJK>
        { (char) 0x8AF6, (char) 0x63EE }, // <CJK>
        { (char) 0x8AF7, (char) 0x673A }, // <CJK>
        { (char) 0x8AF8, (char) 0x65D7 }, // <CJK>
        { (char) 0x8AF9, (char) 0x65E2 }, // <CJK>
        { (char) 0x8AFA, (char) 0x671F }, // <CJK>
        { (char) 0x8AFB, (char) 0x68CB }, // <CJK>
        { (char) 0x8AFC, (char) 0x68C4 }, // <CJK>
        { (char) 0x8B40, (char) 0x6A5F }, // <CJK>
        { (char) 0x8B41, (char) 0x5E30 }, // <CJK>
        { (char) 0x8B42, (char) 0x6BC5 }, // <CJK>
        { (char) 0x8B43, (char) 0x6C17 }, // <CJK>
        { (char) 0x8B44, (char) 0x6C7D }, // <CJK>
        { (char) 0x8B45, (char) 0x757F }, // <CJK>
        { (char) 0x8B46, (char) 0x7948 }, // <CJK>
        { (char) 0x8B47, (char) 0x5B63 }, // <CJK>
        { (char) 0x8B48, (char) 0x7A00 }, // <CJK>
        { (char) 0x8B49, (char) 0x7D00 }, // <CJK>
        { (char) 0x8B4A, (char) 0x5FBD }, // <CJK>
        { (char) 0x8B4B, (char) 0x898F }, // <CJK>
        { (char) 0x8B4C, (char) 0x8A18 }, // <CJK>
        { (char) 0x8B4D, (char) 0x8CB4 }, // <CJK>
        { (char) 0x8B4E, (char) 0x8D77 }, // <CJK>
        { (char) 0x8B4F, (char) 0x8ECC }, // <CJK>
        { (char) 0x8B50, (char) 0x8F1D }, // <CJK>
        { (char) 0x8B51, (char) 0x98E2 }, // <CJK>
        { (char) 0x8B52, (char) 0x9A0E }, // <CJK>
        { (char) 0x8B53, (char) 0x9B3C }, // <CJK>
        { (char) 0x8B54, (char) 0x4E80 }, // <CJK>
        { (char) 0x8B55, (char) 0x507D }, // <CJK>
        { (char) 0x8B56, (char) 0x5100 }, // <CJK>
        { (char) 0x8B57, (char) 0x5993 }, // <CJK>
        { (char) 0x8B58, (char) 0x5B9C }, // <CJK>
        { (char) 0x8B59, (char) 0x622F }, // <CJK>
        { (char) 0x8B5A, (char) 0x6280 }, // <CJK>
        { (char) 0x8B5B, (char) 0x64EC }, // <CJK>
        { (char) 0x8B5C, (char) 0x6B3A }, // <CJK>
        { (char) 0x8B5D, (char) 0x72A0 }, // <CJK>
        { (char) 0x8B5E, (char) 0x7591 }, // <CJK>
        { (char) 0x8B5F, (char) 0x7947 }, // <CJK>
        { (char) 0x8B60, (char) 0x7FA9 }, // <CJK>
        { (char) 0x8B61, (char) 0x87FB }, // <CJK>
        { (char) 0x8B62, (char) 0x8ABC }, // <CJK>
        { (char) 0x8B63, (char) 0x8B70 }, // <CJK>
        { (char) 0x8B64, (char) 0x63AC }, // <CJK>
        { (char) 0x8B65, (char) 0x83CA }, // <CJK>
        { (char) 0x8B66, (char) 0x97A0 }, // <CJK>
        { (char) 0x8B67, (char) 0x5409 }, // <CJK>
        { (char) 0x8B68, (char) 0x5403 }, // <CJK>
        { (char) 0x8B69, (char) 0x55AB }, // <CJK>
        { (char) 0x8B6A, (char) 0x6854 }, // <CJK>
        { (char) 0x8B6B, (char) 0x6A58 }, // <CJK>
        { (char) 0x8B6C, (char) 0x8A70 }, // <CJK>
        { (char) 0x8B6D, (char) 0x7827 }, // <CJK>
        { (char) 0x8B6E, (char) 0x6775 }, // <CJK>
        { (char) 0x8B6F, (char) 0x9ECD }, // <CJK>
        { (char) 0x8B70, (char) 0x5374 }, // <CJK>
        { (char) 0x8B71, (char) 0x5BA2 }, // <CJK>
        { (char) 0x8B72, (char) 0x811A }, // <CJK>
        { (char) 0x8B73, (char) 0x8650 }, // <CJK>
        { (char) 0x8B74, (char) 0x9006 }, // <CJK>
        { (char) 0x8B75, (char) 0x4E18 }, // <CJK>
        { (char) 0x8B76, (char) 0x4E45 }, // <CJK>
        { (char) 0x8B77, (char) 0x4EC7 }, // <CJK>
        { (char) 0x8B78, (char) 0x4F11 }, // <CJK>
        { (char) 0x8B79, (char) 0x53CA }, // <CJK>
        { (char) 0x8B7A, (char) 0x5438 }, // <CJK>
        { (char) 0x8B7B, (char) 0x5BAE }, // <CJK>
        { (char) 0x8B7C, (char) 0x5F13 }, // <CJK>
        { (char) 0x8B7D, (char) 0x6025 }, // <CJK>
        { (char) 0x8B7E, (char) 0x6551 }, // <CJK>
        { (char) 0x8B80, (char) 0x673D }, // <CJK>
        { (char) 0x8B81, (char) 0x6C42 }, // <CJK>
        { (char) 0x8B82, (char) 0x6C72 }, // <CJK>
        { (char) 0x8B83, (char) 0x6CE3 }, // <CJK>
        { (char) 0x8B84, (char) 0x7078 }, // <CJK>
        { (char) 0x8B85, (char) 0x7403 }, // <CJK>
        { (char) 0x8B86, (char) 0x7A76 }, // <CJK>
        { (char) 0x8B87, (char) 0x7AAE }, // <CJK>
        { (char) 0x8B88, (char) 0x7B08 }, // <CJK>
        { (char) 0x8B89, (char) 0x7D1A }, // <CJK>
        { (char) 0x8B8A, (char) 0x7CFE }, // <CJK>
        { (char) 0x8B8B, (char) 0x7D66 }, // <CJK>
        { (char) 0x8B8C, (char) 0x65E7 }, // <CJK>
        { (char) 0x8B8D, (char) 0x725B }, // <CJK>
        { (char) 0x8B8E, (char) 0x53BB }, // <CJK>
        { (char) 0x8B8F, (char) 0x5C45 }, // <CJK>
        { (char) 0x8B90, (char) 0x5DE8 }, // <CJK>
        { (char) 0x8B91, (char) 0x62D2 }, // <CJK>
        { (char) 0x8B92, (char) 0x62E0 }, // <CJK>
        { (char) 0x8B93, (char) 0x6319 }, // <CJK>
        { (char) 0x8B94, (char) 0x6E20 }, // <CJK>
        { (char) 0x8B95, (char) 0x865A }, // <CJK>
        { (char) 0x8B96, (char) 0x8A31 }, // <CJK>
        { (char) 0x8B97, (char) 0x8DDD }, // <CJK>
        { (char) 0x8B98, (char) 0x92F8 }, // <CJK>
        { (char) 0x8B99, (char) 0x6F01 }, // <CJK>
        { (char) 0x8B9A, (char) 0x79A6 }, // <CJK>
        { (char) 0x8B9B, (char) 0x9B5A }, // <CJK>
        { (char) 0x8B9C, (char) 0x4EA8 }, // <CJK>
        { (char) 0x8B9D, (char) 0x4EAB }, // <CJK>
        { (char) 0x8B9E, (char) 0x4EAC }, // <CJK>
        { (char) 0x8B9F, (char) 0x4F9B }, // <CJK>
        { (char) 0x8BA0, (char) 0x4FA0 }, // <CJK>
        { (char) 0x8BA1, (char) 0x50D1 }, // <CJK>
        { (char) 0x8BA2, (char) 0x5147 }, // <CJK>
        { (char) 0x8BA3, (char) 0x7AF6 }, // <CJK>
        { (char) 0x8BA4, (char) 0x5171 }, // <CJK>
        { (char) 0x8BA5, (char) 0x51F6 }, // <CJK>
        { (char) 0x8BA6, (char) 0x5354 }, // <CJK>
        { (char) 0x8BA7, (char) 0x5321 }, // <CJK>
        { (char) 0x8BA8, (char) 0x537F }, // <CJK>
        { (char) 0x8BA9, (char) 0x53EB }, // <CJK>
        { (char) 0x8BAA, (char) 0x55AC }, // <CJK>
        { (char) 0x8BAB, (char) 0x5883 }, // <CJK>
        { (char) 0x8BAC, (char) 0x5CE1 }, // <CJK>
        { (char) 0x8BAD, (char) 0x5F37 }, // <CJK>
        { (char) 0x8BAE, (char) 0x5F4A }, // <CJK>
        { (char) 0x8BAF, (char) 0x602F }, // <CJK>
        { (char) 0x8BB0, (char) 0x6050 }, // <CJK>
        { (char) 0x8BB1, (char) 0x606D }, // <CJK>
        { (char) 0x8BB2, (char) 0x631F }, // <CJK>
        { (char) 0x8BB3, (char) 0x6559 }, // <CJK>
        { (char) 0x8BB4, (char) 0x6A4B }, // <CJK>
        { (char) 0x8BB5, (char) 0x6CC1 }, // <CJK>
        { (char) 0x8BB6, (char) 0x72C2 }, // <CJK>
        { (char) 0x8BB7, (char) 0x72ED }, // <CJK>
        { (char) 0x8BB8, (char) 0x77EF }, // <CJK>
        { (char) 0x8BB9, (char) 0x80F8 }, // <CJK>
        { (char) 0x8BBA, (char) 0x8105 }, // <CJK>
        { (char) 0x8BBB, (char) 0x8208 }, // <CJK>
        { (char) 0x8BBC, (char) 0x854E }, // <CJK>
        { (char) 0x8BBD, (char) 0x90F7 }, // <CJK>
        { (char) 0x8BBE, (char) 0x93E1 }, // <CJK>
        { (char) 0x8BBF, (char) 0x97FF }, // <CJK>
        { (char) 0x8BC0, (char) 0x9957 }, // <CJK>
        { (char) 0x8BC1, (char) 0x9A5A }, // <CJK>
        { (char) 0x8BC2, (char) 0x4EF0 }, // <CJK>
        { (char) 0x8BC3, (char) 0x51DD }, // <CJK>
        { (char) 0x8BC4, (char) 0x5C2D }, // <CJK>
        { (char) 0x8BC5, (char) 0x6681 }, // <CJK>
        { (char) 0x8BC6, (char) 0x696D }, // <CJK>
        { (char) 0x8BC7, (char) 0x5C40 }, // <CJK>
        { (char) 0x8BC8, (char) 0x66F2 }, // <CJK>
        { (char) 0x8BC9, (char) 0x6975 }, // <CJK>
        { (char) 0x8BCA, (char) 0x7389 }, // <CJK>
        { (char) 0x8BCB, (char) 0x6850 }, // <CJK>
        { (char) 0x8BCC, (char) 0x7C81 }, // <CJK>
        { (char) 0x8BCD, (char) 0x50C5 }, // <CJK>
        { (char) 0x8BCE, (char) 0x52E4 }, // <CJK>
        { (char) 0x8BCF, (char) 0x5747 }, // <CJK>
        { (char) 0x8BD0, (char) 0x5DFE }, // <CJK>
        { (char) 0x8BD1, (char) 0x9326 }, // <CJK>
        { (char) 0x8BD2, (char) 0x65A4 }, // <CJK>
        { (char) 0x8BD3, (char) 0x6B23 }, // <CJK>
        { (char) 0x8BD4, (char) 0x6B3D }, // <CJK>
        { (char) 0x8BD5, (char) 0x7434 }, // <CJK>
        { (char) 0x8BD6, (char) 0x7981 }, // <CJK>
        { (char) 0x8BD7, (char) 0x79BD }, // <CJK>
        { (char) 0x8BD8, (char) 0x7B4B }, // <CJK>
        { (char) 0x8BD9, (char) 0x7DCA }, // <CJK>
        { (char) 0x8BDA, (char) 0x82B9 }, // <CJK>
        { (char) 0x8BDB, (char) 0x83CC }, // <CJK>
        { (char) 0x8BDC, (char) 0x887F }, // <CJK>
        { (char) 0x8BDD, (char) 0x895F }, // <CJK>
        { (char) 0x8BDE, (char) 0x8B39 }, // <CJK>
        { (char) 0x8BDF, (char) 0x8FD1 }, // <CJK>
        { (char) 0x8BE0, (char) 0x91D1 }, // <CJK>
        { (char) 0x8BE1, (char) 0x541F }, // <CJK>
        { (char) 0x8BE2, (char) 0x9280 }, // <CJK>
        { (char) 0x8BE3, (char) 0x4E5D }, // <CJK>
        { (char) 0x8BE4, (char) 0x5036 }, // <CJK>
        { (char) 0x8BE5, (char) 0x53E5 }, // <CJK>
        { (char) 0x8BE6, (char) 0x533A }, // <CJK>
        { (char) 0x8BE7, (char) 0x72D7 }, // <CJK>
        { (char) 0x8BE8, (char) 0x7396 }, // <CJK>
        { (char) 0x8BE9, (char) 0x77E9 }, // <CJK>
        { (char) 0x8BEA, (char) 0x82E6 }, // <CJK>
        { (char) 0x8BEB, (char) 0x8EAF }, // <CJK>
        { (char) 0x8BEC, (char) 0x99C6 }, // <CJK>
        { (char) 0x8BED, (char) 0x99C8 }, // <CJK>
        { (char) 0x8BEE, (char) 0x99D2 }, // <CJK>
        { (char) 0x8BEF, (char) 0x5177 }, // <CJK>
        { (char) 0x8BF0, (char) 0x611A }, // <CJK>
        { (char) 0x8BF1, (char) 0x865E }, // <CJK>
        { (char) 0x8BF2, (char) 0x55B0 }, // <CJK>
        { (char) 0x8BF3, (char) 0x7A7A }, // <CJK>
        { (char) 0x8BF4, (char) 0x5076 }, // <CJK>
        { (char) 0x8BF5, (char) 0x5BD3 }, // <CJK>
        { (char) 0x8BF6, (char) 0x9047 }, // <CJK>
        { (char) 0x8BF7, (char) 0x9685 }, // <CJK>
        { (char) 0x8BF8, (char) 0x4E32 }, // <CJK>
        { (char) 0x8BF9, (char) 0x6ADB }, // <CJK>
        { (char) 0x8BFA, (char) 0x91E7 }, // <CJK>
        { (char) 0x8BFB, (char) 0x5C51 }, // <CJK>
        { (char) 0x8BFC, (char) 0x5C48 }, // <CJK>
        { (char) 0x8C40, (char) 0x6398 }, // <CJK>
        { (char) 0x8C41, (char) 0x7A9F }, // <CJK>
        { (char) 0x8C42, (char) 0x6C93 }, // <CJK>
        { (char) 0x8C43, (char) 0x9774 }, // <CJK>
        { (char) 0x8C44, (char) 0x8F61 }, // <CJK>
        { (char) 0x8C45, (char) 0x7AAA }, // <CJK>
        { (char) 0x8C46, (char) 0x718A }, // <CJK>
        { (char) 0x8C47, (char) 0x9688 }, // <CJK>
        { (char) 0x8C48, (char) 0x7C82 }, // <CJK>
        { (char) 0x8C49, (char) 0x6817 }, // <CJK>
        { (char) 0x8C4A, (char) 0x7E70 }, // <CJK>
        { (char) 0x8C4B, (char) 0x6851 }, // <CJK>
        { (char) 0x8C4C, (char) 0x936C }, // <CJK>
        { (char) 0x8C4D, (char) 0x52F2 }, // <CJK>
        { (char) 0x8C4E, (char) 0x541B }, // <CJK>
        { (char) 0x8C4F, (char) 0x85AB }, // <CJK>
        { (char) 0x8C50, (char) 0x8A13 }, // <CJK>
        { (char) 0x8C51, (char) 0x7FA4 }, // <CJK>
        { (char) 0x8C52, (char) 0x8ECD }, // <CJK>
        { (char) 0x8C53, (char) 0x90E1 }, // <CJK>
        { (char) 0x8C54, (char) 0x5366 }, // <CJK>
        { (char) 0x8C55, (char) 0x8888 }, // <CJK>
        { (char) 0x8C56, (char) 0x7941 }, // <CJK>
        { (char) 0x8C57, (char) 0x4FC2 }, // <CJK>
        { (char) 0x8C58, (char) 0x50BE }, // <CJK>
        { (char) 0x8C59, (char) 0x5211 }, // <CJK>
        { (char) 0x8C5A, (char) 0x5144 }, // <CJK>
        { (char) 0x8C5B, (char) 0x5553 }, // <CJK>
        { (char) 0x8C5C, (char) 0x572D }, // <CJK>
        { (char) 0x8C5D, (char) 0x73EA }, // <CJK>
        { (char) 0x8C5E, (char) 0x578B }, // <CJK>
        { (char) 0x8C5F, (char) 0x5951 }, // <CJK>
        { (char) 0x8C60, (char) 0x5F62 }, // <CJK>
        { (char) 0x8C61, (char) 0x5F84 }, // <CJK>
        { (char) 0x8C62, (char) 0x6075 }, // <CJK>
        { (char) 0x8C63, (char) 0x6176 }, // <CJK>
        { (char) 0x8C64, (char) 0x6167 }, // <CJK>
        { (char) 0x8C65, (char) 0x61A9 }, // <CJK>
        { (char) 0x8C66, (char) 0x63B2 }, // <CJK>
        { (char) 0x8C67, (char) 0x643A }, // <CJK>
        { (char) 0x8C68, (char) 0x656C }, // <CJK>
        { (char) 0x8C69, (char) 0x666F }, // <CJK>
        { (char) 0x8C6A, (char) 0x6842 }, // <CJK>
        { (char) 0x8C6B, (char) 0x6E13 }, // <CJK>
        { (char) 0x8C6C, (char) 0x7566 }, // <CJK>
        { (char) 0x8C6D, (char) 0x7A3D }, // <CJK>
        { (char) 0x8C6E, (char) 0x7CFB }, // <CJK>
        { (char) 0x8C6F, (char) 0x7D4C }, // <CJK>
        { (char) 0x8C70, (char) 0x7D99 }, // <CJK>
        { (char) 0x8C71, (char) 0x7E4B }, // <CJK>
        { (char) 0x8C72, (char) 0x7F6B }, // <CJK>
        { (char) 0x8C73, (char) 0x830E }, // <CJK>
        { (char) 0x8C74, (char) 0x834A }, // <CJK>
        { (char) 0x8C75, (char) 0x86CD }, // <CJK>
        { (char) 0x8C76, (char) 0x8A08 }, // <CJK>
        { (char) 0x8C77, (char) 0x8A63 }, // <CJK>
        { (char) 0x8C78, (char) 0x8B66 }, // <CJK>
        { (char) 0x8C79, (char) 0x8EFD }, // <CJK>
        { (char) 0x8C7A, (char) 0x981A }, // <CJK>
        { (char) 0x8C7B, (char) 0x9D8F }, // <CJK>
        { (char) 0x8C7C, (char) 0x82B8 }, // <CJK>
        { (char) 0x8C7D, (char) 0x8FCE }, // <CJK>
        { (char) 0x8C7E, (char) 0x9BE8 }, // <CJK>
        { (char) 0x8C80, (char) 0x5287 }, // <CJK>
        { (char) 0x8C81, (char) 0x621F }, // <CJK>
        { (char) 0x8C82, (char) 0x6483 }, // <CJK>
        { (char) 0x8C83, (char) 0x6FC0 }, // <CJK>
        { (char) 0x8C84, (char) 0x9699 }, // <CJK>
        { (char) 0x8C85, (char) 0x6841 }, // <CJK>
        { (char) 0x8C86, (char) 0x5091 }, // <CJK>
        { (char) 0x8C87, (char) 0x6B20 }, // <CJK>
        { (char) 0x8C88, (char) 0x6C7A }, // <CJK>
        { (char) 0x8C89, (char) 0x6F54 }, // <CJK>
        { (char) 0x8C8A, (char) 0x7A74 }, // <CJK>
        { (char) 0x8C8B, (char) 0x7D50 }, // <CJK>
        { (char) 0x8C8C, (char) 0x8840 }, // <CJK>
        { (char) 0x8C8D, (char) 0x8A23 }, // <CJK>
        { (char) 0x8C8E, (char) 0x6708 }, // <CJK>
        { (char) 0x8C8F, (char) 0x4EF6 }, // <CJK>
        { (char) 0x8C90, (char) 0x5039 }, // <CJK>
        { (char) 0x8C91, (char) 0x5026 }, // <CJK>
        { (char) 0x8C92, (char) 0x5065 }, // <CJK>
        { (char) 0x8C93, (char) 0x517C }, // <CJK>
        { (char) 0x8C94, (char) 0x5238 }, // <CJK>
        { (char) 0x8C95, (char) 0x5263 }, // <CJK>
        { (char) 0x8C96, (char) 0x55A7 }, // <CJK>
        { (char) 0x8C97, (char) 0x570F }, // <CJK>
        { (char) 0x8C98, (char) 0x5805 }, // <CJK>
        { (char) 0x8C99, (char) 0x5ACC }, // <CJK>
        { (char) 0x8C9A, (char) 0x5EFA }, // <CJK>
        { (char) 0x8C9B, (char) 0x61B2 }, // <CJK>
        { (char) 0x8C9C, (char) 0x61F8 }, // <CJK>
        { (char) 0x8C9D, (char) 0x62F3 }, // <CJK>
        { (char) 0x8C9E, (char) 0x6372 }, // <CJK>
        { (char) 0x8C9F, (char) 0x691C }, // <CJK>
        { (char) 0x8CA0, (char) 0x6A29 }, // <CJK>
        { (char) 0x8CA1, (char) 0x727D }, // <CJK>
        { (char) 0x8CA2, (char) 0x72AC }, // <CJK>
        { (char) 0x8CA3, (char) 0x732E }, // <CJK>
        { (char) 0x8CA4, (char) 0x7814 }, // <CJK>
        { (char) 0x8CA5, (char) 0x786F }, // <CJK>
        { (char) 0x8CA6, (char) 0x7D79 }, // <CJK>
        { (char) 0x8CA7, (char) 0x770C }, // <CJK>
        { (char) 0x8CA8, (char) 0x80A9 }, // <CJK>
        { (char) 0x8CA9, (char) 0x898B }, // <CJK>
        { (char) 0x8CAA, (char) 0x8B19 }, // <CJK>
        { (char) 0x8CAB, (char) 0x8CE2 }, // <CJK>
        { (char) 0x8CAC, (char) 0x8ED2 }, // <CJK>
        { (char) 0x8CAD, (char) 0x9063 }, // <CJK>
        { (char) 0x8CAE, (char) 0x9375 }, // <CJK>
        { (char) 0x8CAF, (char) 0x967A }, // <CJK>
        { (char) 0x8CB0, (char) 0x9855 }, // <CJK>
        { (char) 0x8CB1, (char) 0x9A13 }, // <CJK>
        { (char) 0x8CB2, (char) 0x9E78 }, // <CJK>
        { (char) 0x8CB3, (char) 0x5143 }, // <CJK>
        { (char) 0x8CB4, (char) 0x539F }, // <CJK>
        { (char) 0x8CB5, (char) 0x53B3 }, // <CJK>
        { (char) 0x8CB6, (char) 0x5E7B }, // <CJK>
        { (char) 0x8CB7, (char) 0x5F26 }, // <CJK>
        { (char) 0x8CB8, (char) 0x6E1B }, // <CJK>
        { (char) 0x8CB9, (char) 0x6E90 }, // <CJK>
        { (char) 0x8CBA, (char) 0x7384 }, // <CJK>
        { (char) 0x8CBB, (char) 0x73FE }, // <CJK>
        { (char) 0x8CBC, (char) 0x7D43 }, // <CJK>
        { (char) 0x8CBD, (char) 0x8237 }, // <CJK>
        { (char) 0x8CBE, (char) 0x8A00 }, // <CJK>
        { (char) 0x8CBF, (char) 0x8AFA }, // <CJK>
        { (char) 0x8CC0, (char) 0x9650 }, // <CJK>
        { (char) 0x8CC1, (char) 0x4E4E }, // <CJK>
        { (char) 0x8CC2, (char) 0x500B }, // <CJK>
        { (char) 0x8CC3, (char) 0x53E4 }, // <CJK>
        { (char) 0x8CC4, (char) 0x547C }, // <CJK>
        { (char) 0x8CC5, (char) 0x56FA }, // <CJK>
        { (char) 0x8CC6, (char) 0x59D1 }, // <CJK>
        { (char) 0x8CC7, (char) 0x5B64 }, // <CJK>
        { (char) 0x8CC8, (char) 0x5DF1 }, // <CJK>
        { (char) 0x8CC9, (char) 0x5EAB }, // <CJK>
        { (char) 0x8CCA, (char) 0x5F27 }, // <CJK>
        { (char) 0x8CCB, (char) 0x6238 }, // <CJK>
        { (char) 0x8CCC, (char) 0x6545 }, // <CJK>
        { (char) 0x8CCD, (char) 0x67AF }, // <CJK>
        { (char) 0x8CCE, (char) 0x6E56 }, // <CJK>
        { (char) 0x8CCF, (char) 0x72D0 }, // <CJK>
        { (char) 0x8CD0, (char) 0x7CCA }, // <CJK>
        { (char) 0x8CD1, (char) 0x88B4 }, // <CJK>
        { (char) 0x8CD2, (char) 0x80A1 }, // <CJK>
        { (char) 0x8CD3, (char) 0x80E1 }, // <CJK>
        { (char) 0x8CD4, (char) 0x83F0 }, // <CJK>
        { (char) 0x8CD5, (char) 0x864E }, // <CJK>
        { (char) 0x8CD6, (char) 0x8A87 }, // <CJK>
        { (char) 0x8CD7, (char) 0x8DE8 }, // <CJK>
        { (char) 0x8CD8, (char) 0x9237 }, // <CJK>
        { (char) 0x8CD9, (char) 0x96C7 }, // <CJK>
        { (char) 0x8CDA, (char) 0x9867 }, // <CJK>
        { (char) 0x8CDB, (char) 0x9F13 }, // <CJK>
        { (char) 0x8CDC, (char) 0x4E94 }, // <CJK>
        { (char) 0x8CDD, (char) 0x4E92 }, // <CJK>
        { (char) 0x8CDE, (char) 0x4F0D }, // <CJK>
        { (char) 0x8CDF, (char) 0x5348 }, // <CJK>
        { (char) 0x8CE0, (char) 0x5449 }, // <CJK>
        { (char) 0x8CE1, (char) 0x543E }, // <CJK>
        { (char) 0x8CE2, (char) 0x5A2F }, // <CJK>
        { (char) 0x8CE3, (char) 0x5F8C }, // <CJK>
        { (char) 0x8CE4, (char) 0x5FA1 }, // <CJK>
        { (char) 0x8CE5, (char) 0x609F }, // <CJK>
        { (char) 0x8CE6, (char) 0x68A7 }, // <CJK>
        { (char) 0x8CE7, (char) 0x6A8E }, // <CJK>
        { (char) 0x8CE8, (char) 0x745A }, // <CJK>
        { (char) 0x8CE9, (char) 0x7881 }, // <CJK>
        { (char) 0x8CEA, (char) 0x8A9E }, // <CJK>
        { (char) 0x8CEB, (char) 0x8AA4 }, // <CJK>
        { (char) 0x8CEC, (char) 0x8B77 }, // <CJK>
        { (char) 0x8CED, (char) 0x9190 }, // <CJK>
        { (char) 0x8CEE, (char) 0x4E5E }, // <CJK>
        { (char) 0x8CEF, (char) 0x9BC9 }, // <CJK>
        { (char) 0x8CF0, (char) 0x4EA4 }, // <CJK>
        { (char) 0x8CF1, (char) 0x4F7C }, // <CJK>
        { (char) 0x8CF2, (char) 0x4FAF }, // <CJK>
        { (char) 0x8CF3, (char) 0x5019 }, // <CJK>
        { (char) 0x8CF4, (char) 0x5016 }, // <CJK>
        { (char) 0x8CF5, (char) 0x5149 }, // <CJK>
        { (char) 0x8CF6, (char) 0x516C }, // <CJK>
        { (char) 0x8CF7, (char) 0x529F }, // <CJK>
        { (char) 0x8CF8, (char) 0x52B9 }, // <CJK>
        { (char) 0x8CF9, (char) 0x52FE }, // <CJK>
        { (char) 0x8CFA, (char) 0x539A }, // <CJK>
        { (char) 0x8CFB, (char) 0x53E3 }, // <CJK>
        { (char) 0x8CFC, (char) 0x5411 }, // <CJK>
        { (char) 0x8D40, (char) 0x540E }, // <CJK>
        { (char) 0x8D41, (char) 0x5589 }, // <CJK>
        { (char) 0x8D42, (char) 0x5751 }, // <CJK>
        { (char) 0x8D43, (char) 0x57A2 }, // <CJK>
        { (char) 0x8D44, (char) 0x597D }, // <CJK>
        { (char) 0x8D45, (char) 0x5B54 }, // <CJK>
        { (char) 0x8D46, (char) 0x5B5D }, // <CJK>
        { (char) 0x8D47, (char) 0x5B8F }, // <CJK>
        { (char) 0x8D48, (char) 0x5DE5 }, // <CJK>
        { (char) 0x8D49, (char) 0x5DE7 }, // <CJK>
        { (char) 0x8D4A, (char) 0x5DF7 }, // <CJK>
        { (char) 0x8D4B, (char) 0x5E78 }, // <CJK>
        { (char) 0x8D4C, (char) 0x5E83 }, // <CJK>
        { (char) 0x8D4D, (char) 0x5E9A }, // <CJK>
        { (char) 0x8D4E, (char) 0x5EB7 }, // <CJK>
        { (char) 0x8D4F, (char) 0x5F18 }, // <CJK>
        { (char) 0x8D50, (char) 0x6052 }, // <CJK>
        { (char) 0x8D51, (char) 0x614C }, // <CJK>
        { (char) 0x8D52, (char) 0x6297 }, // <CJK>
        { (char) 0x8D53, (char) 0x62D8 }, // <CJK>
        { (char) 0x8D54, (char) 0x63A7 }, // <CJK>
        { (char) 0x8D55, (char) 0x653B }, // <CJK>
        { (char) 0x8D56, (char) 0x6602 }, // <CJK>
        { (char) 0x8D57, (char) 0x6643 }, // <CJK>
        { (char) 0x8D58, (char) 0x66F4 }, // <CJK>
        { (char) 0x8D59, (char) 0x676D }, // <CJK>
        { (char) 0x8D5A, (char) 0x6821 }, // <CJK>
        { (char) 0x8D5B, (char) 0x6897 }, // <CJK>
        { (char) 0x8D5C, (char) 0x69CB }, // <CJK>
        { (char) 0x8D5D, (char) 0x6C5F }, // <CJK>
        { (char) 0x8D5E, (char) 0x6D2A }, // <CJK>
        { (char) 0x8D5F, (char) 0x6D69 }, // <CJK>
        { (char) 0x8D60, (char) 0x6E2F }, // <CJK>
        { (char) 0x8D61, (char) 0x6E9D }, // <CJK>
        { (char) 0x8D62, (char) 0x7532 }, // <CJK>
        { (char) 0x8D63, (char) 0x7687 }, // <CJK>
        { (char) 0x8D64, (char) 0x786C }, // <CJK>
        { (char) 0x8D65, (char) 0x7A3F }, // <CJK>
        { (char) 0x8D66, (char) 0x7CE0 }, // <CJK>
        { (char) 0x8D67, (char) 0x7D05 }, // <CJK>
        { (char) 0x8D68, (char) 0x7D18 }, // <CJK>
        { (char) 0x8D69, (char) 0x7D5E }, // <CJK>
        { (char) 0x8D6A, (char) 0x7DB1 }, // <CJK>
        { (char) 0x8D6B, (char) 0x8015 }, // <CJK>
        { (char) 0x8D6C, (char) 0x8003 }, // <CJK>
        { (char) 0x8D6D, (char) 0x80AF }, // <CJK>
        { (char) 0x8D6E, (char) 0x80B1 }, // <CJK>
        { (char) 0x8D6F, (char) 0x8154 }, // <CJK>
        { (char) 0x8D70, (char) 0x818F }, // <CJK>
        { (char) 0x8D71, (char) 0x822A }, // <CJK>
        { (char) 0x8D72, (char) 0x8352 }, // <CJK>
        { (char) 0x8D73, (char) 0x884C }, // <CJK>
        { (char) 0x8D74, (char) 0x8861 }, // <CJK>
        { (char) 0x8D75, (char) 0x8B1B }, // <CJK>
        { (char) 0x8D76, (char) 0x8CA2 }, // <CJK>
        { (char) 0x8D77, (char) 0x8CFC }, // <CJK>
        { (char) 0x8D78, (char) 0x90CA }, // <CJK>
        { (char) 0x8D79, (char) 0x9175 }, // <CJK>
        { (char) 0x8D7A, (char) 0x9271 }, // <CJK>
        { (char) 0x8D7B, (char) 0x783F }, // <CJK>
        { (char) 0x8D7C, (char) 0x92FC }, // <CJK>
        { (char) 0x8D7D, (char) 0x95A4 }, // <CJK>
        { (char) 0x8D7E, (char) 0x964D }, // <CJK>
        { (char) 0x8D80, (char) 0x9805 }, // <CJK>
        { (char) 0x8D81, (char) 0x9999 }, // <CJK>
        { (char) 0x8D82, (char) 0x9AD8 }, // <CJK>
        { (char) 0x8D83, (char) 0x9D3B }, // <CJK>
        { (char) 0x8D84, (char) 0x525B }, // <CJK>
        { (char) 0x8D85, (char) 0x52AB }, // <CJK>
        { (char) 0x8D86, (char) 0x53F7 }, // <CJK>
        { (char) 0x8D87, (char) 0x5408 }, // <CJK>
        { (char) 0x8D88, (char) 0x58D5 }, // <CJK>
        { (char) 0x8D89, (char) 0x62F7 }, // <CJK>
        { (char) 0x8D8A, (char) 0x6FE0 }, // <CJK>
        { (char) 0x8D8B, (char) 0x8C6A }, // <CJK>
        { (char) 0x8D8C, (char) 0x8F5F }, // <CJK>
        { (char) 0x8D8D, (char) 0x9EB9 }, // <CJK>
        { (char) 0x8D8E, (char) 0x514B }, // <CJK>
        { (char) 0x8D8F, (char) 0x523B }, // <CJK>
        { (char) 0x8D90, (char) 0x544A }, // <CJK>
        { (char) 0x8D91, (char) 0x56FD }, // <CJK>
        { (char) 0x8D92, (char) 0x7A40 }, // <CJK>
        { (char) 0x8D93, (char) 0x9177 }, // <CJK>
        { (char) 0x8D94, (char) 0x9D60 }, // <CJK>
        { (char) 0x8D95, (char) 0x9ED2 }, // <CJK>
        { (char) 0x8D96, (char) 0x7344 }, // <CJK>
        { (char) 0x8D97, (char) 0x6F09 }, // <CJK>
        { (char) 0x8D98, (char) 0x8170 }, // <CJK>
        { (char) 0x8D99, (char) 0x7511 }, // <CJK>
        { (char) 0x8D9A, (char) 0x5FFD }, // <CJK>
        { (char) 0x8D9B, (char) 0x60DA }, // <CJK>
        { (char) 0x8D9C, (char) 0x9AA8 }, // <CJK>
        { (char) 0x8D9D, (char) 0x72DB }, // <CJK>
        { (char) 0x8D9E, (char) 0x8FBC }, // <CJK>
        { (char) 0x8D9F, (char) 0x6B64 }, // <CJK>
        { (char) 0x8DA0, (char) 0x9803 }, // <CJK>
        { (char) 0x8DA1, (char) 0x4ECA }, // <CJK>
        { (char) 0x8DA2, (char) 0x56F0 }, // <CJK>
        { (char) 0x8DA3, (char) 0x5764 }, // <CJK>
        { (char) 0x8DA4, (char) 0x58BE }, // <CJK>
        { (char) 0x8DA5, (char) 0x5A5A }, // <CJK>
        { (char) 0x8DA6, (char) 0x6068 }, // <CJK>
        { (char) 0x8DA7, (char) 0x61C7 }, // <CJK>
        { (char) 0x8DA8, (char) 0x660F }, // <CJK>
        { (char) 0x8DA9, (char) 0x6606 }, // <CJK>
        { (char) 0x8DAA, (char) 0x6839 }, // <CJK>
        { (char) 0x8DAB, (char) 0x68B1 }, // <CJK>
        { (char) 0x8DAC, (char) 0x6DF7 }, // <CJK>
        { (char) 0x8DAD, (char) 0x75D5 }, // <CJK>
        { (char) 0x8DAE, (char) 0x7D3A }, // <CJK>
        { (char) 0x8DAF, (char) 0x826E }, // <CJK>
        { (char) 0x8DB0, (char) 0x9B42 }, // <CJK>
        { (char) 0x8DB1, (char) 0x4E9B }, // <CJK>
        { (char) 0x8DB2, (char) 0x4F50 }, // <CJK>
        { (char) 0x8DB3, (char) 0x53C9 }, // <CJK>
        { (char) 0x8DB4, (char) 0x5506 }, // <CJK>
        { (char) 0x8DB5, (char) 0x5D6F }, // <CJK>
        { (char) 0x8DB6, (char) 0x5DE6 }, // <CJK>
        { (char) 0x8DB7, (char) 0x5DEE }, // <CJK>
        { (char) 0x8DB8, (char) 0x67FB }, // <CJK>
        { (char) 0x8DB9, (char) 0x6C99 }, // <CJK>
        { (char) 0x8DBA, (char) 0x7473 }, // <CJK>
        { (char) 0x8DBB, (char) 0x7802 }, // <CJK>
        { (char) 0x8DBC, (char) 0x8A50 }, // <CJK>
        { (char) 0x8DBD, (char) 0x9396 }, // <CJK>
        { (char) 0x8DBE, (char) 0x88DF }, // <CJK>
        { (char) 0x8DBF, (char) 0x5750 }, // <CJK>
        { (char) 0x8DC0, (char) 0x5EA7 }, // <CJK>
        { (char) 0x8DC1, (char) 0x632B }, // <CJK>
        { (char) 0x8DC2, (char) 0x50B5 }, // <CJK>
        { (char) 0x8DC3, (char) 0x50AC }, // <CJK>
        { (char) 0x8DC4, (char) 0x518D }, // <CJK>
        { (char) 0x8DC5, (char) 0x6700 }, // <CJK>
        { (char) 0x8DC6, (char) 0x54C9 }, // <CJK>
        { (char) 0x8DC7, (char) 0x585E }, // <CJK>
        { (char) 0x8DC8, (char) 0x59BB }, // <CJK>
        { (char) 0x8DC9, (char) 0x5BB0 }, // <CJK>
        { (char) 0x8DCA, (char) 0x5F69 }, // <CJK>
        { (char) 0x8DCB, (char) 0x624D }, // <CJK>
        { (char) 0x8DCC, (char) 0x63A1 }, // <CJK>
        { (char) 0x8DCD, (char) 0x683D }, // <CJK>
        { (char) 0x8DCE, (char) 0x6B73 }, // <CJK>
        { (char) 0x8DCF, (char) 0x6E08 }, // <CJK>
        { (char) 0x8DD0, (char) 0x707D }, // <CJK>
        { (char) 0x8DD1, (char) 0x91C7 }, // <CJK>
        { (char) 0x8DD2, (char) 0x7280 }, // <CJK>
        { (char) 0x8DD3, (char) 0x7815 }, // <CJK>
        { (char) 0x8DD4, (char) 0x7826 }, // <CJK>
        { (char) 0x8DD5, (char) 0x796D }, // <CJK>
        { (char) 0x8DD6, (char) 0x658E }, // <CJK>
        { (char) 0x8DD7, (char) 0x7D30 }, // <CJK>
        { (char) 0x8DD8, (char) 0x83DC }, // <CJK>
        { (char) 0x8DD9, (char) 0x88C1 }, // <CJK>
        { (char) 0x8DDA, (char) 0x8F09 }, // <CJK>
        { (char) 0x8DDB, (char) 0x969B }, // <CJK>
        { (char) 0x8DDC, (char) 0x5264 }, // <CJK>
        { (char) 0x8DDD, (char) 0x5728 }, // <CJK>
        { (char) 0x8DDE, (char) 0x6750 }, // <CJK>
        { (char) 0x8DDF, (char) 0x7F6A }, // <CJK>
        { (char) 0x8DE0, (char) 0x8CA1 }, // <CJK>
        { (char) 0x8DE1, (char) 0x51B4 }, // <CJK>
        { (char) 0x8DE2, (char) 0x5742 }, // <CJK>
        { (char) 0x8DE3, (char) 0x962A }, // <CJK>
        { (char) 0x8DE4, (char) 0x583A }, // <CJK>
        { (char) 0x8DE5, (char) 0x698A }, // <CJK>
        { (char) 0x8DE6, (char) 0x80B4 }, // <CJK>
        { (char) 0x8DE7, (char) 0x54B2 }, // <CJK>
        { (char) 0x8DE8, (char) 0x5D0E }, // <CJK>
        { (char) 0x8DE9, (char) 0x57FC }, // <CJK>
        { (char) 0x8DEA, (char) 0x7895 }, // <CJK>
        { (char) 0x8DEB, (char) 0x9DFA }, // <CJK>
        { (char) 0x8DEC, (char) 0x4F5C }, // <CJK>
        { (char) 0x8DED, (char) 0x524A }, // <CJK>
        { (char) 0x8DEE, (char) 0x548B }, // <CJK>
        { (char) 0x8DEF, (char) 0x643E }, // <CJK>
        { (char) 0x8DF0, (char) 0x6628 }, // <CJK>
        { (char) 0x8DF1, (char) 0x6714 }, // <CJK>
        { (char) 0x8DF2, (char) 0x67F5 }, // <CJK>
        { (char) 0x8DF3, (char) 0x7A84 }, // <CJK>
        { (char) 0x8DF4, (char) 0x7B56 }, // <CJK>
        { (char) 0x8DF5, (char) 0x7D22 }, // <CJK>
        { (char) 0x8DF6, (char) 0x932F }, // <CJK>
        { (char) 0x8DF7, (char) 0x685C }, // <CJK>
        { (char) 0x8DF8, (char) 0x9BAD }, // <CJK>
        { (char) 0x8DF9, (char) 0x7B39 }, // <CJK>
        { (char) 0x8DFA, (char) 0x5319 }, // <CJK>
        { (char) 0x8DFB, (char) 0x518A }, // <CJK>
        { (char) 0x8DFC, (char) 0x5237 }, // <CJK>
        { (char) 0x8E40, (char) 0x5BDF }, // <CJK>
        { (char) 0x8E41, (char) 0x62F6 }, // <CJK>
        { (char) 0x8E42, (char) 0x64AE }, // <CJK>
        { (char) 0x8E43, (char) 0x64E6 }, // <CJK>
        { (char) 0x8E44, (char) 0x672D }, // <CJK>
        { (char) 0x8E45, (char) 0x6BBA }, // <CJK>
        { (char) 0x8E46, (char) 0x85A9 }, // <CJK>
        { (char) 0x8E47, (char) 0x96D1 }, // <CJK>
        { (char) 0x8E48, (char) 0x7690 }, // <CJK>
        { (char) 0x8E49, (char) 0x9BD6 }, // <CJK>
        { (char) 0x8E4A, (char) 0x634C }, // <CJK>
        { (char) 0x8E4B, (char) 0x9306 }, // <CJK>
        { (char) 0x8E4C, (char) 0x9BAB }, // <CJK>
        { (char) 0x8E4D, (char) 0x76BF }, // <CJK>
        { (char) 0x8E4E, (char) 0x6652 }, // <CJK>
        { (char) 0x8E4F, (char) 0x4E09 }, // <CJK>
        { (char) 0x8E50, (char) 0x5098 }, // <CJK>
        { (char) 0x8E51, (char) 0x53C2 }, // <CJK>
        { (char) 0x8E52, (char) 0x5C71 }, // <CJK>
        { (char) 0x8E53, (char) 0x60E8 }, // <CJK>
        { (char) 0x8E54, (char) 0x6492 }, // <CJK>
        { (char) 0x8E55, (char) 0x6563 }, // <CJK>
        { (char) 0x8E56, (char) 0x685F }, // <CJK>
        { (char) 0x8E57, (char) 0x71E6 }, // <CJK>
        { (char) 0x8E58, (char) 0x73CA }, // <CJK>
        { (char) 0x8E59, (char) 0x7523 }, // <CJK>
        { (char) 0x8E5A, (char) 0x7B97 }, // <CJK>
        { (char) 0x8E5B, (char) 0x7E82 }, // <CJK>
        { (char) 0x8E5C, (char) 0x8695 }, // <CJK>
        { (char) 0x8E5D, (char) 0x8B83 }, // <CJK>
        { (char) 0x8E5E, (char) 0x8CDB }, // <CJK>
        { (char) 0x8E5F, (char) 0x9178 }, // <CJK>
        { (char) 0x8E60, (char) 0x9910 }, // <CJK>
        { (char) 0x8E61, (char) 0x65AC }, // <CJK>
        { (char) 0x8E62, (char) 0x66AB }, // <CJK>
        { (char) 0x8E63, (char) 0x6B8B }, // <CJK>
        { (char) 0x8E64, (char) 0x4ED5 }, // <CJK>
        { (char) 0x8E65, (char) 0x4ED4 }, // <CJK>
        { (char) 0x8E66, (char) 0x4F3A }, // <CJK>
        { (char) 0x8E67, (char) 0x4F7F }, // <CJK>
        { (char) 0x8E68, (char) 0x523A }, // <CJK>
        { (char) 0x8E69, (char) 0x53F8 }, // <CJK>
        { (char) 0x8E6A, (char) 0x53F2 }, // <CJK>
        { (char) 0x8E6B, (char) 0x55E3 }, // <CJK>
        { (char) 0x8E6C, (char) 0x56DB }, // <CJK>
        { (char) 0x8E6D, (char) 0x58EB }, // <CJK>
        { (char) 0x8E6E, (char) 0x59CB }, // <CJK>
        { (char) 0x8E6F, (char) 0x59C9 }, // <CJK>
        { (char) 0x8E70, (char) 0x59FF }, // <CJK>
        { (char) 0x8E71, (char) 0x5B50 }, // <CJK>
        { (char) 0x8E72, (char) 0x5C4D }, // <CJK>
        { (char) 0x8E73, (char) 0x5E02 }, // <CJK>
        { (char) 0x8E74, (char) 0x5E2B }, // <CJK>
        { (char) 0x8E75, (char) 0x5FD7 }, // <CJK>
        { (char) 0x8E76, (char) 0x601D }, // <CJK>
        { (char) 0x8E77, (char) 0x6307 }, // <CJK>
        { (char) 0x8E78, (char) 0x652F }, // <CJK>
        { (char) 0x8E79, (char) 0x5B5C }, // <CJK>
        { (char) 0x8E7A, (char) 0x65AF }, // <CJK>
        { (char) 0x8E7B, (char) 0x65BD }, // <CJK>
        { (char) 0x8E7C, (char) 0x65E8 }, // <CJK>
        { (char) 0x8E7D, (char) 0x679D }, // <CJK>
        { (char) 0x8E7E, (char) 0x6B62 }, // <CJK>
        { (char) 0x8E80, (char) 0x6B7B }, // <CJK>
        { (char) 0x8E81, (char) 0x6C0F }, // <CJK>
        { (char) 0x8E82, (char) 0x7345 }, // <CJK>
        { (char) 0x8E83, (char) 0x7949 }, // <CJK>
        { (char) 0x8E84, (char) 0x79C1 }, // <CJK>
        { (char) 0x8E85, (char) 0x7CF8 }, // <CJK>
        { (char) 0x8E86, (char) 0x7D19 }, // <CJK>
        { (char) 0x8E87, (char) 0x7D2B }, // <CJK>
        { (char) 0x8E88, (char) 0x80A2 }, // <CJK>
        { (char) 0x8E89, (char) 0x8102 }, // <CJK>
        { (char) 0x8E8A, (char) 0x81F3 }, // <CJK>
        { (char) 0x8E8B, (char) 0x8996 }, // <CJK>
        { (char) 0x8E8C, (char) 0x8A5E }, // <CJK>
        { (char) 0x8E8D, (char) 0x8A69 }, // <CJK>
        { (char) 0x8E8E, (char) 0x8A66 }, // <CJK>
        { (char) 0x8E8F, (char) 0x8A8C }, // <CJK>
        { (char) 0x8E90, (char) 0x8AEE }, // <CJK>
        { (char) 0x8E91, (char) 0x8CC7 }, // <CJK>
        { (char) 0x8E92, (char) 0x8CDC }, // <CJK>
        { (char) 0x8E93, (char) 0x96CC }, // <CJK>
        { (char) 0x8E94, (char) 0x98FC }, // <CJK>
        { (char) 0x8E95, (char) 0x6B6F }, // <CJK>
        { (char) 0x8E96, (char) 0x4E8B }, // <CJK>
        { (char) 0x8E97, (char) 0x4F3C }, // <CJK>
        { (char) 0x8E98, (char) 0x4F8D }, // <CJK>
        { (char) 0x8E99, (char) 0x5150 }, // <CJK>
        { (char) 0x8E9A, (char) 0x5B57 }, // <CJK>
        { (char) 0x8E9B, (char) 0x5BFA }, // <CJK>
        { (char) 0x8E9C, (char) 0x6148 }, // <CJK>
        { (char) 0x8E9D, (char) 0x6301 }, // <CJK>
        { (char) 0x8E9E, (char) 0x6642 }, // <CJK>
        { (char) 0x8E9F, (char) 0x6B21 }, // <CJK>
        { (char) 0x8EA0, (char) 0x6ECB }, // <CJK>
        { (char) 0x8EA1, (char) 0x6CBB }, // <CJK>
        { (char) 0x8EA2, (char) 0x723E }, // <CJK>
        { (char) 0x8EA3, (char) 0x74BD }, // <CJK>
        { (char) 0x8EA4, (char) 0x75D4 }, // <CJK>
        { (char) 0x8EA5, (char) 0x78C1 }, // <CJK>
        { (char) 0x8EA6, (char) 0x793A }, // <CJK>
        { (char) 0x8EA7, (char) 0x800C }, // <CJK>
        { (char) 0x8EA8, (char) 0x8033 }, // <CJK>
        { (char) 0x8EA9, (char) 0x81EA }, // <CJK>
        { (char) 0x8EAA, (char) 0x8494 }, // <CJK>
        { (char) 0x8EAB, (char) 0x8F9E }, // <CJK>
        { (char) 0x8EAC, (char) 0x6C50 }, // <CJK>
        { (char) 0x8EAD, (char) 0x9E7F }, // <CJK>
        { (char) 0x8EAE, (char) 0x5F0F }, // <CJK>
        { (char) 0x8EAF, (char) 0x8B58 }, // <CJK>
        { (char) 0x8EB0, (char) 0x9D2B }, // <CJK>
        { (char) 0x8EB1, (char) 0x7AFA }, // <CJK>
        { (char) 0x8EB2, (char) 0x8EF8 }, // <CJK>
        { (char) 0x8EB3, (char) 0x5B8D }, // <CJK>
        { (char) 0x8EB4, (char) 0x96EB }, // <CJK>
        { (char) 0x8EB5, (char) 0x4E03 }, // <CJK>
        { (char) 0x8EB6, (char) 0x53F1 }, // <CJK>
        { (char) 0x8EB7, (char) 0x57F7 }, // <CJK>
        { (char) 0x8EB8, (char) 0x5931 }, // <CJK>
        { (char) 0x8EB9, (char) 0x5AC9 }, // <CJK>
        { (char) 0x8EBA, (char) 0x5BA4 }, // <CJK>
        { (char) 0x8EBB, (char) 0x6089 }, // <CJK>
        { (char) 0x8EBC, (char) 0x6E7F }, // <CJK>
        { (char) 0x8EBD, (char) 0x6F06 }, // <CJK>
        { (char) 0x8EBE, (char) 0x75BE }, // <CJK>
        { (char) 0x8EBF, (char) 0x8CEA }, // <CJK>
        { (char) 0x8EC0, (char) 0x5B9F }, // <CJK>
        { (char) 0x8EC1, (char) 0x8500 }, // <CJK>
        { (char) 0x8EC2, (char) 0x7BE0 }, // <CJK>
        { (char) 0x8EC3, (char) 0x5072 }, // <CJK>
        { (char) 0x8EC4, (char) 0x67F4 }, // <CJK>
        { (char) 0x8EC5, (char) 0x829D }, // <CJK>
        { (char) 0x8EC6, (char) 0x5C61 }, // <CJK>
        { (char) 0x8EC7, (char) 0x854A }, // <CJK>
        { (char) 0x8EC8, (char) 0x7E1E }, // <CJK>
        { (char) 0x8EC9, (char) 0x820E }, // <CJK>
        { (char) 0x8ECA, (char) 0x5199 }, // <CJK>
        { (char) 0x8ECB, (char) 0x5C04 }, // <CJK>
        { (char) 0x8ECC, (char) 0x6368 }, // <CJK>
        { (char) 0x8ECD, (char) 0x8D66 }, // <CJK>
        { (char) 0x8ECE, (char) 0x659C }, // <CJK>
        { (char) 0x8ECF, (char) 0x716E }, // <CJK>
        { (char) 0x8ED0, (char) 0x793E }, // <CJK>
        { (char) 0x8ED1, (char) 0x7D17 }, // <CJK>
        { (char) 0x8ED2, (char) 0x8005 }, // <CJK>
        { (char) 0x8ED3, (char) 0x8B1D }, // <CJK>
        { (char) 0x8ED4, (char) 0x8ECA }, // <CJK>
        { (char) 0x8ED5, (char) 0x906E }, // <CJK>
        { (char) 0x8ED6, (char) 0x86C7 }, // <CJK>
        { (char) 0x8ED7, (char) 0x90AA }, // <CJK>
        { (char) 0x8ED8, (char) 0x501F }, // <CJK>
        { (char) 0x8ED9, (char) 0x52FA }, // <CJK>
        { (char) 0x8EDA, (char) 0x5C3A }, // <CJK>
        { (char) 0x8EDB, (char) 0x6753 }, // <CJK>
        { (char) 0x8EDC, (char) 0x707C }, // <CJK>
        { (char) 0x8EDD, (char) 0x7235 }, // <CJK>
        { (char) 0x8EDE, (char) 0x914C }, // <CJK>
        { (char) 0x8EDF, (char) 0x91C8 }, // <CJK>
        { (char) 0x8EE0, (char) 0x932B }, // <CJK>
        { (char) 0x8EE1, (char) 0x82E5 }, // <CJK>
        { (char) 0x8EE2, (char) 0x5BC2 }, // <CJK>
        { (char) 0x8EE3, (char) 0x5F31 }, // <CJK>
        { (char) 0x8EE4, (char) 0x60F9 }, // <CJK>
        { (char) 0x8EE5, (char) 0x4E3B }, // <CJK>
        { (char) 0x8EE6, (char) 0x53D6 }, // <CJK>
        { (char) 0x8EE7, (char) 0x5B88 }, // <CJK>
        { (char) 0x8EE8, (char) 0x624B }, // <CJK>
        { (char) 0x8EE9, (char) 0x6731 }, // <CJK>
        { (char) 0x8EEA, (char) 0x6B8A }, // <CJK>
        { (char) 0x8EEB, (char) 0x72E9 }, // <CJK>
        { (char) 0x8EEC, (char) 0x73E0 }, // <CJK>
        { (char) 0x8EED, (char) 0x7A2E }, // <CJK>
        { (char) 0x8EEE, (char) 0x816B }, // <CJK>
        { (char) 0x8EEF, (char) 0x8DA3 }, // <CJK>
        { (char) 0x8EF0, (char) 0x9152 }, // <CJK>
        { (char) 0x8EF1, (char) 0x9996 }, // <CJK>
        { (char) 0x8EF2, (char) 0x5112 }, // <CJK>
        { (char) 0x8EF3, (char) 0x53D7 }, // <CJK>
        { (char) 0x8EF4, (char) 0x546A }, // <CJK>
        { (char) 0x8EF5, (char) 0x5BFF }, // <CJK>
        { (char) 0x8EF6, (char) 0x6388 }, // <CJK>
        { (char) 0x8EF7, (char) 0x6A39 }, // <CJK>
        { (char) 0x8EF8, (char) 0x7DAC }, // <CJK>
        { (char) 0x8EF9, (char) 0x9700 }, // <CJK>
        { (char) 0x8EFA, (char) 0x56DA }, // <CJK>
        { (char) 0x8EFB, (char) 0x53CE }, // <CJK>
        { (char) 0x8EFC, (char) 0x5468 }, // <CJK>
        { (char) 0x8F40, (char) 0x5B97 }, // <CJK>
        { (char) 0x8F41, (char) 0x5C31 }, // <CJK>
        { (char) 0x8F42, (char) 0x5DDE }, // <CJK>
        { (char) 0x8F43, (char) 0x4FEE }, // <CJK>
        { (char) 0x8F44, (char) 0x6101 }, // <CJK>
        { (char) 0x8F45, (char) 0x62FE }, // <CJK>
        { (char) 0x8F46, (char) 0x6D32 }, // <CJK>
        { (char) 0x8F47, (char) 0x79C0 }, // <CJK>
        { (char) 0x8F48, (char) 0x79CB }, // <CJK>
        { (char) 0x8F49, (char) 0x7D42 }, // <CJK>
        { (char) 0x8F4A, (char) 0x7E4D }, // <CJK>
        { (char) 0x8F4B, (char) 0x7FD2 }, // <CJK>
        { (char) 0x8F4C, (char) 0x81ED }, // <CJK>
        { (char) 0x8F4D, (char) 0x821F }, // <CJK>
        { (char) 0x8F4E, (char) 0x8490 }, // <CJK>
        { (char) 0x8F4F, (char) 0x8846 }, // <CJK>
        { (char) 0x8F50, (char) 0x8972 }, // <CJK>
        { (char) 0x8F51, (char) 0x8B90 }, // <CJK>
        { (char) 0x8F52, (char) 0x8E74 }, // <CJK>
        { (char) 0x8F53, (char) 0x8F2F }, // <CJK>
        { (char) 0x8F54, (char) 0x9031 }, // <CJK>
        { (char) 0x8F55, (char) 0x914B }, // <CJK>
        { (char) 0x8F56, (char) 0x916C }, // <CJK>
        { (char) 0x8F57, (char) 0x96C6 }, // <CJK>
        { (char) 0x8F58, (char) 0x919C }, // <CJK>
        { (char) 0x8F59, (char) 0x4EC0 }, // <CJK>
        { (char) 0x8F5A, (char) 0x4F4F }, // <CJK>
        { (char) 0x8F5B, (char) 0x5145 }, // <CJK>
        { (char) 0x8F5C, (char) 0x5341 }, // <CJK>
        { (char) 0x8F5D, (char) 0x5F93 }, // <CJK>
        { (char) 0x8F5E, (char) 0x620E }, // <CJK>
        { (char) 0x8F5F, (char) 0x67D4 }, // <CJK>
        { (char) 0x8F60, (char) 0x6C41 }, // <CJK>
        { (char) 0x8F61, (char) 0x6E0B }, // <CJK>
        { (char) 0x8F62, (char) 0x7363 }, // <CJK>
        { (char) 0x8F63, (char) 0x7E26 }, // <CJK>
        { (char) 0x8F64, (char) 0x91CD }, // <CJK>
        { (char) 0x8F65, (char) 0x9283 }, // <CJK>
        { (char) 0x8F66, (char) 0x53D4 }, // <CJK>
        { (char) 0x8F67, (char) 0x5919 }, // <CJK>
        { (char) 0x8F68, (char) 0x5BBF }, // <CJK>
        { (char) 0x8F69, (char) 0x6DD1 }, // <CJK>
        { (char) 0x8F6A, (char) 0x795D }, // <CJK>
        { (char) 0x8F6B, (char) 0x7E2E }, // <CJK>
        { (char) 0x8F6C, (char) 0x7C9B }, // <CJK>
        { (char) 0x8F6D, (char) 0x587E }, // <CJK>
        { (char) 0x8F6E, (char) 0x719F }, // <CJK>
        { (char) 0x8F6F, (char) 0x51FA }, // <CJK>
        { (char) 0x8F70, (char) 0x8853 }, // <CJK>
        { (char) 0x8F71, (char) 0x8FF0 }, // <CJK>
        { (char) 0x8F72, (char) 0x4FCA }, // <CJK>
        { (char) 0x8F73, (char) 0x5CFB }, // <CJK>
        { (char) 0x8F74, (char) 0x6625 }, // <CJK>
        { (char) 0x8F75, (char) 0x77AC }, // <CJK>
        { (char) 0x8F76, (char) 0x7AE3 }, // <CJK>
        { (char) 0x8F77, (char) 0x821C }, // <CJK>
        { (char) 0x8F78, (char) 0x99FF }, // <CJK>
        { (char) 0x8F79, (char) 0x51C6 }, // <CJK>
        { (char) 0x8F7A, (char) 0x5FAA }, // <CJK>
        { (char) 0x8F7B, (char) 0x65EC }, // <CJK>
        { (char) 0x8F7C, (char) 0x696F }, // <CJK>
        { (char) 0x8F7D, (char) 0x6B89 }, // <CJK>
        { (char) 0x8F7E, (char) 0x6DF3 }, // <CJK>
        { (char) 0x8F80, (char) 0x6E96 }, // <CJK>
        { (char) 0x8F81, (char) 0x6F64 }, // <CJK>
        { (char) 0x8F82, (char) 0x76FE }, // <CJK>
        { (char) 0x8F83, (char) 0x7D14 }, // <CJK>
        { (char) 0x8F84, (char) 0x5DE1 }, // <CJK>
        { (char) 0x8F85, (char) 0x9075 }, // <CJK>
        { (char) 0x8F86, (char) 0x9187 }, // <CJK>
        { (char) 0x8F87, (char) 0x9806 }, // <CJK>
        { (char) 0x8F88, (char) 0x51E6 }, // <CJK>
        { (char) 0x8F89, (char) 0x521D }, // <CJK>
        { (char) 0x8F8A, (char) 0x6240 }, // <CJK>
        { (char) 0x8F8B, (char) 0x6691 }, // <CJK>
        { (char) 0x8F8C, (char) 0x66D9 }, // <CJK>
        { (char) 0x8F8D, (char) 0x6E1A }, // <CJK>
        { (char) 0x8F8E, (char) 0x5EB6 }, // <CJK>
        { (char) 0x8F8F, (char) 0x7DD2 }, // <CJK>
        { (char) 0x8F90, (char) 0x7F72 }, // <CJK>
        { (char) 0x8F91, (char) 0x66F8 }, // <CJK>
        { (char) 0x8F92, (char) 0x85AF }, // <CJK>
        { (char) 0x8F93, (char) 0x85F7 }, // <CJK>
        { (char) 0x8F94, (char) 0x8AF8 }, // <CJK>
        { (char) 0x8F95, (char) 0x52A9 }, // <CJK>
        { (char) 0x8F96, (char) 0x53D9 }, // <CJK>
        { (char) 0x8F97, (char) 0x5973 }, // <CJK>
        { (char) 0x8F98, (char) 0x5E8F }, // <CJK>
        { (char) 0x8F99, (char) 0x5F90 }, // <CJK>
        { (char) 0x8F9A, (char) 0x6055 }, // <CJK>
        { (char) 0x8F9B, (char) 0x92E4 }, // <CJK>
        { (char) 0x8F9C, (char) 0x9664 }, // <CJK>
        { (char) 0x8F9D, (char) 0x50B7 }, // <CJK>
        { (char) 0x8F9E, (char) 0x511F }, // <CJK>
        { (char) 0x8F9F, (char) 0x52DD }, // <CJK>
        { (char) 0x8FA0, (char) 0x5320 }, // <CJK>
        { (char) 0x8FA1, (char) 0x5347 }, // <CJK>
        { (char) 0x8FA2, (char) 0x53EC }, // <CJK>
        { (char) 0x8FA3, (char) 0x54E8 }, // <CJK>
        { (char) 0x8FA4, (char) 0x5546 }, // <CJK>
        { (char) 0x8FA5, (char) 0x5531 }, // <CJK>
        { (char) 0x8FA6, (char) 0x5617 }, // <CJK>
        { (char) 0x8FA7, (char) 0x5968 }, // <CJK>
        { (char) 0x8FA8, (char) 0x59BE }, // <CJK>
        { (char) 0x8FA9, (char) 0x5A3C }, // <CJK>
        { (char) 0x8FAA, (char) 0x5BB5 }, // <CJK>
        { (char) 0x8FAB, (char) 0x5C06 }, // <CJK>
        { (char) 0x8FAC, (char) 0x5C0F }, // <CJK>
        { (char) 0x8FAD, (char) 0x5C11 }, // <CJK>
        { (char) 0x8FAE, (char) 0x5C1A }, // <CJK>
        { (char) 0x8FAF, (char) 0x5E84 }, // <CJK>
        { (char) 0x8FB0, (char) 0x5E8A }, // <CJK>
        { (char) 0x8FB1, (char) 0x5EE0 }, // <CJK>
        { (char) 0x8FB2, (char) 0x5F70 }, // <CJK>
        { (char) 0x8FB3, (char) 0x627F }, // <CJK>
        { (char) 0x8FB4, (char) 0x6284 }, // <CJK>
        { (char) 0x8FB5, (char) 0x62DB }, // <CJK>
        { (char) 0x8FB6, (char) 0x638C }, // <CJK>
        { (char) 0x8FB7, (char) 0x6377 }, // <CJK>
        { (char) 0x8FB8, (char) 0x6607 }, // <CJK>
        { (char) 0x8FB9, (char) 0x660C }, // <CJK>
        { (char) 0x8FBA, (char) 0x662D }, // <CJK>
        { (char) 0x8FBB, (char) 0x6676 }, // <CJK>
        { (char) 0x8FBC, (char) 0x677E }, // <CJK>
        { (char) 0x8FBD, (char) 0x68A2 }, // <CJK>
        { (char) 0x8FBE, (char) 0x6A1F }, // <CJK>
        { (char) 0x8FBF, (char) 0x6A35 }, // <CJK>
        { (char) 0x8FC0, (char) 0x6CBC }, // <CJK>
        { (char) 0x8FC1, (char) 0x6D88 }, // <CJK>
        { (char) 0x8FC2, (char) 0x6E09 }, // <CJK>
        { (char) 0x8FC3, (char) 0x6E58 }, // <CJK>
        { (char) 0x8FC4, (char) 0x713C }, // <CJK>
        { (char) 0x8FC5, (char) 0x7126 }, // <CJK>
        { (char) 0x8FC6, (char) 0x7167 }, // <CJK>
        { (char) 0x8FC7, (char) 0x75C7 }, // <CJK>
        { (char) 0x8FC8, (char) 0x7701 }, // <CJK>
        { (char) 0x8FC9, (char) 0x785D }, // <CJK>
        { (char) 0x8FCA, (char) 0x7901 }, // <CJK>
        { (char) 0x8FCB, (char) 0x7965 }, // <CJK>
        { (char) 0x8FCC, (char) 0x79F0 }, // <CJK>
        { (char) 0x8FCD, (char) 0x7AE0 }, // <CJK>
        { (char) 0x8FCE, (char) 0x7B11 }, // <CJK>
        { (char) 0x8FCF, (char) 0x7CA7 }, // <CJK>
        { (char) 0x8FD0, (char) 0x7D39 }, // <CJK>
        { (char) 0x8FD1, (char) 0x8096 }, // <CJK>
        { (char) 0x8FD2, (char) 0x83D6 }, // <CJK>
        { (char) 0x8FD3, (char) 0x848B }, // <CJK>
        { (char) 0x8FD4, (char) 0x8549 }, // <CJK>
        { (char) 0x8FD5, (char) 0x885D }, // <CJK>
        { (char) 0x8FD6, (char) 0x88F3 }, // <CJK>
        { (char) 0x8FD7, (char) 0x8A1F }, // <CJK>
        { (char) 0x8FD8, (char) 0x8A3C }, // <CJK>
        { (char) 0x8FD9, (char) 0x8A54 }, // <CJK>
        { (char) 0x8FDA, (char) 0x8A73 }, // <CJK>
        { (char) 0x8FDB, (char) 0x8C61 }, // <CJK>
        { (char) 0x8FDC, (char) 0x8CDE }, // <CJK>
        { (char) 0x8FDD, (char) 0x91A4 }, // <CJK>
        { (char) 0x8FDE, (char) 0x9266 }, // <CJK>
        { (char) 0x8FDF, (char) 0x937E }, // <CJK>
        { (char) 0x8FE0, (char) 0x9418 }, // <CJK>
        { (char) 0x8FE1, (char) 0x969C }, // <CJK>
        { (char) 0x8FE2, (char) 0x9798 }, // <CJK>
        { (char) 0x8FE3, (char) 0x4E0A }, // <CJK>
        { (char) 0x8FE4, (char) 0x4E08 }, // <CJK>
        { (char) 0x8FE5, (char) 0x4E1E }, // <CJK>
        { (char) 0x8FE6, (char) 0x4E57 }, // <CJK>
        { (char) 0x8FE7, (char) 0x5197 }, // <CJK>
        { (char) 0x8FE8, (char) 0x5270 }, // <CJK>
        { (char) 0x8FE9, (char) 0x57CE }, // <CJK>
        { (char) 0x8FEA, (char) 0x5834 }, // <CJK>
        { (char) 0x8FEB, (char) 0x58CC }, // <CJK>
        { (char) 0x8FEC, (char) 0x5B22 }, // <CJK>
        { (char) 0x8FED, (char) 0x5E38 }, // <CJK>
        { (char) 0x8FEE, (char) 0x60C5 }, // <CJK>
        { (char) 0x8FEF, (char) 0x64FE }, // <CJK>
        { (char) 0x8FF0, (char) 0x6761 }, // <CJK>
        { (char) 0x8FF1, (char) 0x6756 }, // <CJK>
        { (char) 0x8FF2, (char) 0x6D44 }, // <CJK>
        { (char) 0x8FF3, (char) 0x72B6 }, // <CJK>
        { (char) 0x8FF4, (char) 0x7573 }, // <CJK>
        { (char) 0x8FF5, (char) 0x7A63 }, // <CJK>
        { (char) 0x8FF6, (char) 0x84B8 }, // <CJK>
        { (char) 0x8FF7, (char) 0x8B72 }, // <CJK>
        { (char) 0x8FF8, (char) 0x91B8 }, // <CJK>
        { (char) 0x8FF9, (char) 0x9320 }, // <CJK>
        { (char) 0x8FFA, (char) 0x5631 }, // <CJK>
        { (char) 0x8FFB, (char) 0x57F4 }, // <CJK>
        { (char) 0x8FFC, (char) 0x98FE }, // <CJK>
        { (char) 0x9040, (char) 0x62ED }, // <CJK>
        { (char) 0x9041, (char) 0x690D }, // <CJK>
        { (char) 0x9042, (char) 0x6B96 }, // <CJK>
        { (char) 0x9043, (char) 0x71ED }, // <CJK>
        { (char) 0x9044, (char) 0x7E54 }, // <CJK>
        { (char) 0x9045, (char) 0x8077 }, // <CJK>
        { (char) 0x9046, (char) 0x8272 }, // <CJK>
        { (char) 0x9047, (char) 0x89E6 }, // <CJK>
        { (char) 0x9048, (char) 0x98DF }, // <CJK>
        { (char) 0x9049, (char) 0x8755 }, // <CJK>
        { (char) 0x904A, (char) 0x8FB1 }, // <CJK>
        { (char) 0x904B, (char) 0x5C3B }, // <CJK>
        { (char) 0x904C, (char) 0x4F38 }, // <CJK>
        { (char) 0x904D, (char) 0x4FE1 }, // <CJK>
        { (char) 0x904E, (char) 0x4FB5 }, // <CJK>
        { (char) 0x904F, (char) 0x5507 }, // <CJK>
        { (char) 0x9050, (char) 0x5A20 }, // <CJK>
        { (char) 0x9051, (char) 0x5BDD }, // <CJK>
        { (char) 0x9052, (char) 0x5BE9 }, // <CJK>
        { (char) 0x9053, (char) 0x5FC3 }, // <CJK>
        { (char) 0x9054, (char) 0x614E }, // <CJK>
        { (char) 0x9055, (char) 0x632F }, // <CJK>
        { (char) 0x9056, (char) 0x65B0 }, // <CJK>
        { (char) 0x9057, (char) 0x664B }, // <CJK>
        { (char) 0x9058, (char) 0x68EE }, // <CJK>
        { (char) 0x9059, (char) 0x699B }, // <CJK>
        { (char) 0x905A, (char) 0x6D78 }, // <CJK>
        { (char) 0x905B, (char) 0x6DF1 }, // <CJK>
        { (char) 0x905C, (char) 0x7533 }, // <CJK>
        { (char) 0x905D, (char) 0x75B9 }, // <CJK>
        { (char) 0x905E, (char) 0x771F }, // <CJK>
        { (char) 0x905F, (char) 0x795E }, // <CJK>
        { (char) 0x9060, (char) 0x79E6 }, // <CJK>
        { (char) 0x9061, (char) 0x7D33 }, // <CJK>
        { (char) 0x9062, (char) 0x81E3 }, // <CJK>
        { (char) 0x9063, (char) 0x82AF }, // <CJK>
        { (char) 0x9064, (char) 0x85AA }, // <CJK>
        { (char) 0x9065, (char) 0x89AA }, // <CJK>
        { (char) 0x9066, (char) 0x8A3A }, // <CJK>
        { (char) 0x9067, (char) 0x8EAB }, // <CJK>
        { (char) 0x9068, (char) 0x8F9B }, // <CJK>
        { (char) 0x9069, (char) 0x9032 }, // <CJK>
        { (char) 0x906A, (char) 0x91DD }, // <CJK>
        { (char) 0x906B, (char) 0x9707 }, // <CJK>
        { (char) 0x906C, (char) 0x4EBA }, // <CJK>
        { (char) 0x906D, (char) 0x4EC1 }, // <CJK>
        { (char) 0x906E, (char) 0x5203 }, // <CJK>
        { (char) 0x906F, (char) 0x5875 }, // <CJK>
        { (char) 0x9070, (char) 0x58EC }, // <CJK>
        { (char) 0x9071, (char) 0x5C0B }, // <CJK>
        { (char) 0x9072, (char) 0x751A }, // <CJK>
        { (char) 0x9073, (char) 0x5C3D }, // <CJK>
        { (char) 0x9074, (char) 0x814E }, // <CJK>
        { (char) 0x9075, (char) 0x8A0A }, // <CJK>
        { (char) 0x9076, (char) 0x8FC5 }, // <CJK>
        { (char) 0x9077, (char) 0x9663 }, // <CJK>
        { (char) 0x9078, (char) 0x976D }, // <CJK>
        { (char) 0x9079, (char) 0x7B25 }, // <CJK>
        { (char) 0x907A, (char) 0x8ACF }, // <CJK>
        { (char) 0x907B, (char) 0x9808 }, // <CJK>
        { (char) 0x907C, (char) 0x9162 }, // <CJK>
        { (char) 0x907D, (char) 0x56F3 }, // <CJK>
        { (char) 0x907E, (char) 0x53A8 }, // <CJK>
        { (char) 0x9080, (char) 0x9017 }, // <CJK>
        { (char) 0x9081, (char) 0x5439 }, // <CJK>
        { (char) 0x9082, (char) 0x5782 }, // <CJK>
        { (char) 0x9083, (char) 0x5E25 }, // <CJK>
        { (char) 0x9084, (char) 0x63A8 }, // <CJK>
        { (char) 0x9085, (char) 0x6C34 }, // <CJK>
        { (char) 0x9086, (char) 0x708A }, // <CJK>
        { (char) 0x9087, (char) 0x7761 }, // <CJK>
        { (char) 0x9088, (char) 0x7C8B }, // <CJK>
        { (char) 0x9089, (char) 0x7FE0 }, // <CJK>
        { (char) 0x908A, (char) 0x8870 }, // <CJK>
        { (char) 0x908B, (char) 0x9042 }, // <CJK>
        { (char) 0x908C, (char) 0x9154 }, // <CJK>
        { (char) 0x908D, (char) 0x9310 }, // <CJK>
        { (char) 0x908E, (char) 0x9318 }, // <CJK>
        { (char) 0x908F, (char) 0x968F }, // <CJK>
        { (char) 0x9090, (char) 0x745E }, // <CJK>
        { (char) 0x9091, (char) 0x9AC4 }, // <CJK>
        { (char) 0x9092, (char) 0x5D07 }, // <CJK>
        { (char) 0x9093, (char) 0x5D69 }, // <CJK>
        { (char) 0x9094, (char) 0x6570 }, // <CJK>
        { (char) 0x9095, (char) 0x67A2 }, // <CJK>
        { (char) 0x9096, (char) 0x8DA8 }, // <CJK>
        { (char) 0x9097, (char) 0x96DB }, // <CJK>
        { (char) 0x9098, (char) 0x636E }, // <CJK>
        { (char) 0x9099, (char) 0x6749 }, // <CJK>
        { (char) 0x909A, (char) 0x6919 }, // <CJK>
        { (char) 0x909B, (char) 0x83C5 }, // <CJK>
        { (char) 0x909C, (char) 0x9817 }, // <CJK>
        { (char) 0x909D, (char) 0x96C0 }, // <CJK>
        { (char) 0x909E, (char) 0x88FE }, // <CJK>
        { (char) 0x909F, (char) 0x6F84 }, // <CJK>
        { (char) 0x90A0, (char) 0x647A }, // <CJK>
        { (char) 0x90A1, (char) 0x5BF8 }, // <CJK>
        { (char) 0x90A2, (char) 0x4E16 }, // <CJK>
        { (char) 0x90A3, (char) 0x702C }, // <CJK>
        { (char) 0x90A4, (char) 0x755D }, // <CJK>
        { (char) 0x90A5, (char) 0x662F }, // <CJK>
        { (char) 0x90A6, (char) 0x51C4 }, // <CJK>
        { (char) 0x90A7, (char) 0x5236 }, // <CJK>
        { (char) 0x90A8, (char) 0x52E2 }, // <CJK>
        { (char) 0x90A9, (char) 0x59D3 }, // <CJK>
        { (char) 0x90AA, (char) 0x5F81 }, // <CJK>
        { (char) 0x90AB, (char) 0x6027 }, // <CJK>
        { (char) 0x90AC, (char) 0x6210 }, // <CJK>
        { (char) 0x90AD, (char) 0x653F }, // <CJK>
        { (char) 0x90AE, (char) 0x6574 }, // <CJK>
        { (char) 0x90AF, (char) 0x661F }, // <CJK>
        { (char) 0x90B0, (char) 0x6674 }, // <CJK>
        { (char) 0x90B1, (char) 0x68F2 }, // <CJK>
        { (char) 0x90B2, (char) 0x6816 }, // <CJK>
        { (char) 0x90B3, (char) 0x6B63 }, // <CJK>
        { (char) 0x90B4, (char) 0x6E05 }, // <CJK>
        { (char) 0x90B5, (char) 0x7272 }, // <CJK>
        { (char) 0x90B6, (char) 0x751F }, // <CJK>
        { (char) 0x90B7, (char) 0x76DB }, // <CJK>
        { (char) 0x90B8, (char) 0x7CBE }, // <CJK>
        { (char) 0x90B9, (char) 0x8056 }, // <CJK>
        { (char) 0x90BA, (char) 0x58F0 }, // <CJK>
        { (char) 0x90BB, (char) 0x88FD }, // <CJK>
        { (char) 0x90BC, (char) 0x897F }, // <CJK>
        { (char) 0x90BD, (char) 0x8AA0 }, // <CJK>
        { (char) 0x90BE, (char) 0x8A93 }, // <CJK>
        { (char) 0x90BF, (char) 0x8ACB }, // <CJK>
        { (char) 0x90C0, (char) 0x901D }, // <CJK>
        { (char) 0x90C1, (char) 0x9192 }, // <CJK>
        { (char) 0x90C2, (char) 0x9752 }, // <CJK>
        { (char) 0x90C3, (char) 0x9759 }, // <CJK>
        { (char) 0x90C4, (char) 0x6589 }, // <CJK>
        { (char) 0x90C5, (char) 0x7A0E }, // <CJK>
        { (char) 0x90C6, (char) 0x8106 }, // <CJK>
        { (char) 0x90C7, (char) 0x96BB }, // <CJK>
        { (char) 0x90C8, (char) 0x5E2D }, // <CJK>
        { (char) 0x90C9, (char) 0x60DC }, // <CJK>
        { (char) 0x90CA, (char) 0x621A }, // <CJK>
        { (char) 0x90CB, (char) 0x65A5 }, // <CJK>
        { (char) 0x90CC, (char) 0x6614 }, // <CJK>
        { (char) 0x90CD, (char) 0x6790 }, // <CJK>
        { (char) 0x90CE, (char) 0x77F3 }, // <CJK>
        { (char) 0x90CF, (char) 0x7A4D }, // <CJK>
        { (char) 0x90D0, (char) 0x7C4D }, // <CJK>
        { (char) 0x90D1, (char) 0x7E3E }, // <CJK>
        { (char) 0x90D2, (char) 0x810A }, // <CJK>
        { (char) 0x90D3, (char) 0x8CAC }, // <CJK>
        { (char) 0x90D4, (char) 0x8D64 }, // <CJK>
        { (char) 0x90D5, (char) 0x8DE1 }, // <CJK>
        { (char) 0x90D6, (char) 0x8E5F }, // <CJK>
        { (char) 0x90D7, (char) 0x78A9 }, // <CJK>
        { (char) 0x90D8, (char) 0x5207 }, // <CJK>
        { (char) 0x90D9, (char) 0x62D9 }, // <CJK>
        { (char) 0x90DA, (char) 0x63A5 }, // <CJK>
        { (char) 0x90DB, (char) 0x6442 }, // <CJK>
        { (char) 0x90DC, (char) 0x6298 }, // <CJK>
        { (char) 0x90DD, (char) 0x8A2D }, // <CJK>
        { (char) 0x90DE, (char) 0x7A83 }, // <CJK>
        { (char) 0x90DF, (char) 0x7BC0 }, // <CJK>
        { (char) 0x90E0, (char) 0x8AAC }, // <CJK>
        { (char) 0x90E1, (char) 0x96EA }, // <CJK>
        { (char) 0x90E2, (char) 0x7D76 }, // <CJK>
        { (char) 0x90E3, (char) 0x820C }, // <CJK>
        { (char) 0x90E4, (char) 0x8749 }, // <CJK>
        { (char) 0x90E5, (char) 0x4ED9 }, // <CJK>
        { (char) 0x90E6, (char) 0x5148 }, // <CJK>
        { (char) 0x90E7, (char) 0x5343 }, // <CJK>
        { (char) 0x90E8, (char) 0x5360 }, // <CJK>
        { (char) 0x90E9, (char) 0x5BA3 }, // <CJK>
        { (char) 0x90EA, (char) 0x5C02 }, // <CJK>
        { (char) 0x90EB, (char) 0x5C16 }, // <CJK>
        { (char) 0x90EC, (char) 0x5DDD }, // <CJK>
        { (char) 0x90ED, (char) 0x6226 }, // <CJK>
        { (char) 0x90EE, (char) 0x6247 }, // <CJK>
        { (char) 0x90EF, (char) 0x64B0 }, // <CJK>
        { (char) 0x90F0, (char) 0x6813 }, // <CJK>
        { (char) 0x90F1, (char) 0x6834 }, // <CJK>
        { (char) 0x90F2, (char) 0x6CC9 }, // <CJK>
        { (char) 0x90F3, (char) 0x6D45 }, // <CJK>
        { (char) 0x90F4, (char) 0x6D17 }, // <CJK>
        { (char) 0x90F5, (char) 0x67D3 }, // <CJK>
        { (char) 0x90F6, (char) 0x6F5C }, // <CJK>
        { (char) 0x90F7, (char) 0x714E }, // <CJK>
        { (char) 0x90F8, (char) 0x717D }, // <CJK>
        { (char) 0x90F9, (char) 0x65CB }, // <CJK>
        { (char) 0x90FA, (char) 0x7A7F }, // <CJK>
        { (char) 0x90FB, (char) 0x7BAD }, // <CJK>
        { (char) 0x90FC, (char) 0x7DDA }, // <CJK>
        { (char) 0x9140, (char) 0x7E4A }, // <CJK>
        { (char) 0x9141, (char) 0x7FA8 }, // <CJK>
        { (char) 0x9142, (char) 0x817A }, // <CJK>
        { (char) 0x9143, (char) 0x821B }, // <CJK>
        { (char) 0x9144, (char) 0x8239 }, // <CJK>
        { (char) 0x9145, (char) 0x85A6 }, // <CJK>
        { (char) 0x9146, (char) 0x8A6E }, // <CJK>
        { (char) 0x9147, (char) 0x8CCE }, // <CJK>
        { (char) 0x9148, (char) 0x8DF5 }, // <CJK>
        { (char) 0x9149, (char) 0x9078 }, // <CJK>
        { (char) 0x914A, (char) 0x9077 }, // <CJK>
        { (char) 0x914B, (char) 0x92AD }, // <CJK>
        { (char) 0x914C, (char) 0x9291 }, // <CJK>
        { (char) 0x914D, (char) 0x9583 }, // <CJK>
        { (char) 0x914E, (char) 0x9BAE }, // <CJK>
        { (char) 0x914F, (char) 0x524D }, // <CJK>
        { (char) 0x9150, (char) 0x5584 }, // <CJK>
        { (char) 0x9151, (char) 0x6F38 }, // <CJK>
        { (char) 0x9152, (char) 0x7136 }, // <CJK>
        { (char) 0x9153, (char) 0x5168 }, // <CJK>
        { (char) 0x9154, (char) 0x7985 }, // <CJK>
        { (char) 0x9155, (char) 0x7E55 }, // <CJK>
        { (char) 0x9156, (char) 0x81B3 }, // <CJK>
        { (char) 0x9157, (char) 0x7CCE }, // <CJK>
        { (char) 0x9158, (char) 0x564C }, // <CJK>
        { (char) 0x9159, (char) 0x5851 }, // <CJK>
        { (char) 0x915A, (char) 0x5CA8 }, // <CJK>
        { (char) 0x915B, (char) 0x63AA }, // <CJK>
        { (char) 0x915C, (char) 0x66FE }, // <CJK>
        { (char) 0x915D, (char) 0x66FD }, // <CJK>
        { (char) 0x915E, (char) 0x695A }, // <CJK>
        { (char) 0x915F, (char) 0x72D9 }, // <CJK>
        { (char) 0x9160, (char) 0x758F }, // <CJK>
        { (char) 0x9161, (char) 0x758E }, // <CJK>
        { (char) 0x9162, (char) 0x790E }, // <CJK>
        { (char) 0x9163, (char) 0x7956 }, // <CJK>
        { (char) 0x9164, (char) 0x79DF }, // <CJK>
        { (char) 0x9165, (char) 0x7C97 }, // <CJK>
        { (char) 0x9166, (char) 0x7D20 }, // <CJK>
        { (char) 0x9167, (char) 0x7D44 }, // <CJK>
        { (char) 0x9168, (char) 0x8607 }, // <CJK>
        { (char) 0x9169, (char) 0x8A34 }, // <CJK>
        { (char) 0x916A, (char) 0x963B }, // <CJK>
        { (char) 0x916B, (char) 0x9061 }, // <CJK>
        { (char) 0x916C, (char) 0x9F20 }, // <CJK>
        { (char) 0x916D, (char) 0x50E7 }, // <CJK>
        { (char) 0x916E, (char) 0x5275 }, // <CJK>
        { (char) 0x916F, (char) 0x53CC }, // <CJK>
        { (char) 0x9170, (char) 0x53E2 }, // <CJK>
        { (char) 0x9171, (char) 0x5009 }, // <CJK>
        { (char) 0x9172, (char) 0x55AA }, // <CJK>
        { (char) 0x9173, (char) 0x58EE }, // <CJK>
        { (char) 0x9174, (char) 0x594F }, // <CJK>
        { (char) 0x9175, (char) 0x723D }, // <CJK>
        { (char) 0x9176, (char) 0x5B8B }, // <CJK>
        { (char) 0x9177, (char) 0x5C64 }, // <CJK>
        { (char) 0x9178, (char) 0x531D }, // <CJK>
        { (char) 0x9179, (char) 0x60E3 }, // <CJK>
        { (char) 0x917A, (char) 0x60F3 }, // <CJK>
        { (char) 0x917B, (char) 0x635C }, // <CJK>
        { (char) 0x917C, (char) 0x6383 }, // <CJK>
        { (char) 0x917D, (char) 0x633F }, // <CJK>
        { (char) 0x917E, (char) 0x63BB }, // <CJK>
        { (char) 0x9180, (char) 0x64CD }, // <CJK>
        { (char) 0x9181, (char) 0x65E9 }, // <CJK>
        { (char) 0x9182, (char) 0x66F9 }, // <CJK>
        { (char) 0x9183, (char) 0x5DE3 }, // <CJK>
        { (char) 0x9184, (char) 0x69CD }, // <CJK>
        { (char) 0x9185, (char) 0x69FD }, // <CJK>
        { (char) 0x9186, (char) 0x6F15 }, // <CJK>
        { (char) 0x9187, (char) 0x71E5 }, // <CJK>
        { (char) 0x9188, (char) 0x4E89 }, // <CJK>
        { (char) 0x9189, (char) 0x75E9 }, // <CJK>
        { (char) 0x918A, (char) 0x76F8 }, // <CJK>
        { (char) 0x918B, (char) 0x7A93 }, // <CJK>
        { (char) 0x918C, (char) 0x7CDF }, // <CJK>
        { (char) 0x918D, (char) 0x7DCF }, // <CJK>
        { (char) 0x918E, (char) 0x7D9C }, // <CJK>
        { (char) 0x918F, (char) 0x8061 }, // <CJK>
        { (char) 0x9190, (char) 0x8349 }, // <CJK>
        { (char) 0x9191, (char) 0x8358 }, // <CJK>
        { (char) 0x9192, (char) 0x846C }, // <CJK>
        { (char) 0x9193, (char) 0x84BC }, // <CJK>
        { (char) 0x9194, (char) 0x85FB }, // <CJK>
        { (char) 0x9195, (char) 0x88C5 }, // <CJK>
        { (char) 0x9196, (char) 0x8D70 }, // <CJK>
        { (char) 0x9197, (char) 0x9001 }, // <CJK>
        { (char) 0x9198, (char) 0x906D }, // <CJK>
        { (char) 0x9199, (char) 0x9397 }, // <CJK>
        { (char) 0x919A, (char) 0x971C }, // <CJK>
        { (char) 0x919B, (char) 0x9A12 }, // <CJK>
        { (char) 0x919C, (char) 0x50CF }, // <CJK>
        { (char) 0x919D, (char) 0x5897 }, // <CJK>
        { (char) 0x919E, (char) 0x618E }, // <CJK>
        { (char) 0x919F, (char) 0x81D3 }, // <CJK>
        { (char) 0x91A0, (char) 0x8535 }, // <CJK>
        { (char) 0x91A1, (char) 0x8D08 }, // <CJK>
        { (char) 0x91A2, (char) 0x9020 }, // <CJK>
        { (char) 0x91A3, (char) 0x4FC3 }, // <CJK>
        { (char) 0x91A4, (char) 0x5074 }, // <CJK>
        { (char) 0x91A5, (char) 0x5247 }, // <CJK>
        { (char) 0x91A6, (char) 0x5373 }, // <CJK>
        { (char) 0x91A7, (char) 0x606F }, // <CJK>
        { (char) 0x91A8, (char) 0x6349 }, // <CJK>
        { (char) 0x91A9, (char) 0x675F }, // <CJK>
        { (char) 0x91AA, (char) 0x6E2C }, // <CJK>
        { (char) 0x91AB, (char) 0x8DB3 }, // <CJK>
        { (char) 0x91AC, (char) 0x901F }, // <CJK>
        { (char) 0x91AD, (char) 0x4FD7 }, // <CJK>
        { (char) 0x91AE, (char) 0x5C5E }, // <CJK>
        { (char) 0x91AF, (char) 0x8CCA }, // <CJK>
        { (char) 0x91B0, (char) 0x65CF }, // <CJK>
        { (char) 0x91B1, (char) 0x7D9A }, // <CJK>
        { (char) 0x91B2, (char) 0x5352 }, // <CJK>
        { (char) 0x91B3, (char) 0x8896 }, // <CJK>
        { (char) 0x91B4, (char) 0x5176 }, // <CJK>
        { (char) 0x91B5, (char) 0x63C3 }, // <CJK>
        { (char) 0x91B6, (char) 0x5B58 }, // <CJK>
        { (char) 0x91B7, (char) 0x5B6B }, // <CJK>
        { (char) 0x91B8, (char) 0x5C0A }, // <CJK>
        { (char) 0x91B9, (char) 0x640D }, // <CJK>
        { (char) 0x91BA, (char) 0x6751 }, // <CJK>
        { (char) 0x91BB, (char) 0x905C }, // <CJK>
        { (char) 0x91BC, (char) 0x4ED6 }, // <CJK>
        { (char) 0x91BD, (char) 0x591A }, // <CJK>
        { (char) 0x91BE, (char) 0x592A }, // <CJK>
        { (char) 0x91BF, (char) 0x6C70 }, // <CJK>
        { (char) 0x91C0, (char) 0x8A51 }, // <CJK>
        { (char) 0x91C1, (char) 0x553E }, // <CJK>
        { (char) 0x91C2, (char) 0x5815 }, // <CJK>
        { (char) 0x91C3, (char) 0x59A5 }, // <CJK>
        { (char) 0x91C4, (char) 0x60F0 }, // <CJK>
        { (char) 0x91C5, (char) 0x6253 }, // <CJK>
        { (char) 0x91C6, (char) 0x67C1 }, // <CJK>
        { (char) 0x91C7, (char) 0x8235 }, // <CJK>
        { (char) 0x91C8, (char) 0x6955 }, // <CJK>
        { (char) 0x91C9, (char) 0x9640 }, // <CJK>
        { (char) 0x91CA, (char) 0x99C4 }, // <CJK>
        { (char) 0x91CB, (char) 0x9A28 }, // <CJK>
        { (char) 0x91CC, (char) 0x4F53 }, // <CJK>
        { (char) 0x91CD, (char) 0x5806 }, // <CJK>
        { (char) 0x91CE, (char) 0x5BFE }, // <CJK>
        { (char) 0x91CF, (char) 0x8010 }, // <CJK>
        { (char) 0x91D0, (char) 0x5CB1 }, // <CJK>
        { (char) 0x91D1, (char) 0x5E2F }, // <CJK>
        { (char) 0x91D2, (char) 0x5F85 }, // <CJK>
        { (char) 0x91D3, (char) 0x6020 }, // <CJK>
        { (char) 0x91D4, (char) 0x614B }, // <CJK>
        { (char) 0x91D5, (char) 0x6234 }, // <CJK>
        { (char) 0x91D6, (char) 0x66FF }, // <CJK>
        { (char) 0x91D7, (char) 0x6CF0 }, // <CJK>
        { (char) 0x91D8, (char) 0x6EDE }, // <CJK>
        { (char) 0x91D9, (char) 0x80CE }, // <CJK>
        { (char) 0x91DA, (char) 0x817F }, // <CJK>
        { (char) 0x91DB, (char) 0x82D4 }, // <CJK>
        { (char) 0x91DC, (char) 0x888B }, // <CJK>
        { (char) 0x91DD, (char) 0x8CB8 }, // <CJK>
        { (char) 0x91DE, (char) 0x9000 }, // <CJK>
        { (char) 0x91DF, (char) 0x902E }, // <CJK>
        { (char) 0x91E0, (char) 0x968A }, // <CJK>
        { (char) 0x91E1, (char) 0x9EDB }, // <CJK>
        { (char) 0x91E2, (char) 0x9BDB }, // <CJK>
        { (char) 0x91E3, (char) 0x4EE3 }, // <CJK>
        { (char) 0x91E4, (char) 0x53F0 }, // <CJK>
        { (char) 0x91E5, (char) 0x5927 }, // <CJK>
        { (char) 0x91E6, (char) 0x7B2C }, // <CJK>
        { (char) 0x91E7, (char) 0x918D }, // <CJK>
        { (char) 0x91E8, (char) 0x984C }, // <CJK>
        { (char) 0x91E9, (char) 0x9DF9 }, // <CJK>
        { (char) 0x91EA, (char) 0x6EDD }, // <CJK>
        { (char) 0x91EB, (char) 0x7027 }, // <CJK>
        { (char) 0x91EC, (char) 0x5353 }, // <CJK>
        { (char) 0x91ED, (char) 0x5544 }, // <CJK>
        { (char) 0x91EE, (char) 0x5B85 }, // <CJK>
        { (char) 0x91EF, (char) 0x6258 }, // <CJK>
        { (char) 0x91F0, (char) 0x629E }, // <CJK>
        { (char) 0x91F1, (char) 0x62D3 }, // <CJK>
        { (char) 0x91F2, (char) 0x6CA2 }, // <CJK>
        { (char) 0x91F3, (char) 0x6FEF }, // <CJK>
        { (char) 0x91F4, (char) 0x7422 }, // <CJK>
        { (char) 0x91F5, (char) 0x8A17 }, // <CJK>
        { (char) 0x91F6, (char) 0x9438 }, // <CJK>
        { (char) 0x91F7, (char) 0x6FC1 }, // <CJK>
        { (char) 0x91F8, (char) 0x8AFE }, // <CJK>
        { (char) 0x91F9, (char) 0x8338 }, // <CJK>
        { (char) 0x91FA, (char) 0x51E7 }, // <CJK>
        { (char) 0x91FB, (char) 0x86F8 }, // <CJK>
        { (char) 0x91FC, (char) 0x53EA }, // <CJK>
        { (char) 0x9240, (char) 0x53E9 }, // <CJK>
        { (char) 0x9241, (char) 0x4F46 }, // <CJK>
        { (char) 0x9242, (char) 0x9054 }, // <CJK>
        { (char) 0x9243, (char) 0x8FB0 }, // <CJK>
        { (char) 0x9244, (char) 0x596A }, // <CJK>
        { (char) 0x9245, (char) 0x8131 }, // <CJK>
        { (char) 0x9246, (char) 0x5DFD }, // <CJK>
        { (char) 0x9247, (char) 0x7AEA }, // <CJK>
        { (char) 0x9248, (char) 0x8FBF }, // <CJK>
        { (char) 0x9249, (char) 0x68DA }, // <CJK>
        { (char) 0x924A, (char) 0x8C37 }, // <CJK>
        { (char) 0x924B, (char) 0x72F8 }, // <CJK>
        { (char) 0x924C, (char) 0x9C48 }, // <CJK>
        { (char) 0x924D, (char) 0x6A3D }, // <CJK>
        { (char) 0x924E, (char) 0x8AB0 }, // <CJK>
        { (char) 0x924F, (char) 0x4E39 }, // <CJK>
        { (char) 0x9250, (char) 0x5358 }, // <CJK>
        { (char) 0x9251, (char) 0x5606 }, // <CJK>
        { (char) 0x9252, (char) 0x5766 }, // <CJK>
        { (char) 0x9253, (char) 0x62C5 }, // <CJK>
        { (char) 0x9254, (char) 0x63A2 }, // <CJK>
        { (char) 0x9255, (char) 0x65E6 }, // <CJK>
        { (char) 0x9256, (char) 0x6B4E }, // <CJK>
        { (char) 0x9257, (char) 0x6DE1 }, // <CJK>
        { (char) 0x9258, (char) 0x6E5B }, // <CJK>
        { (char) 0x9259, (char) 0x70AD }, // <CJK>
        { (char) 0x925A, (char) 0x77ED }, // <CJK>
        { (char) 0x925B, (char) 0x7AEF }, // <CJK>
        { (char) 0x925C, (char) 0x7BAA }, // <CJK>
        { (char) 0x925D, (char) 0x7DBB }, // <CJK>
        { (char) 0x925E, (char) 0x803D }, // <CJK>
        { (char) 0x925F, (char) 0x80C6 }, // <CJK>
        { (char) 0x9260, (char) 0x86CB }, // <CJK>
        { (char) 0x9261, (char) 0x8A95 }, // <CJK>
        { (char) 0x9262, (char) 0x935B }, // <CJK>
        { (char) 0x9263, (char) 0x56E3 }, // <CJK>
        { (char) 0x9264, (char) 0x58C7 }, // <CJK>
        { (char) 0x9265, (char) 0x5F3E }, // <CJK>
        { (char) 0x9266, (char) 0x65AD }, // <CJK>
        { (char) 0x9267, (char) 0x6696 }, // <CJK>
        { (char) 0x9268, (char) 0x6A80 }, // <CJK>
        { (char) 0x9269, (char) 0x6BB5 }, // <CJK>
        { (char) 0x926A, (char) 0x7537 }, // <CJK>
        { (char) 0x926B, (char) 0x8AC7 }, // <CJK>
        { (char) 0x926C, (char) 0x5024 }, // <CJK>
        { (char) 0x926D, (char) 0x77E5 }, // <CJK>
        { (char) 0x926E, (char) 0x5730 }, // <CJK>
        { (char) 0x926F, (char) 0x5F1B }, // <CJK>
        { (char) 0x9270, (char) 0x6065 }, // <CJK>
        { (char) 0x9271, (char) 0x667A }, // <CJK>
        { (char) 0x9272, (char) 0x6C60 }, // <CJK>
        { (char) 0x9273, (char) 0x75F4 }, // <CJK>
        { (char) 0x9274, (char) 0x7A1A }, // <CJK>
        { (char) 0x9275, (char) 0x7F6E }, // <CJK>
        { (char) 0x9276, (char) 0x81F4 }, // <CJK>
        { (char) 0x9277, (char) 0x8718 }, // <CJK>
        { (char) 0x9278, (char) 0x9045 }, // <CJK>
        { (char) 0x9279, (char) 0x99B3 }, // <CJK>
        { (char) 0x927A, (char) 0x7BC9 }, // <CJK>
        { (char) 0x927B, (char) 0x755C }, // <CJK>
        { (char) 0x927C, (char) 0x7AF9 }, // <CJK>
        { (char) 0x927D, (char) 0x7B51 }, // <CJK>
        { (char) 0x927E, (char) 0x84C4 }, // <CJK>
        { (char) 0x9280, (char) 0x9010 }, // <CJK>
        { (char) 0x9281, (char) 0x79E9 }, // <CJK>
        { (char) 0x9282, (char) 0x7A92 }, // <CJK>
        { (char) 0x9283, (char) 0x8336 }, // <CJK>
        { (char) 0x9284, (char) 0x5AE1 }, // <CJK>
        { (char) 0x9285, (char) 0x7740 }, // <CJK>
        { (char) 0x9286, (char) 0x4E2D }, // <CJK>
        { (char) 0x9287, (char) 0x4EF2 }, // <CJK>
        { (char) 0x9288, (char) 0x5B99 }, // <CJK>
        { (char) 0x9289, (char) 0x5FE0 }, // <CJK>
        { (char) 0x928A, (char) 0x62BD }, // <CJK>
        { (char) 0x928B, (char) 0x663C }, // <CJK>
        { (char) 0x928C, (char) 0x67F1 }, // <CJK>
        { (char) 0x928D, (char) 0x6CE8 }, // <CJK>
        { (char) 0x928E, (char) 0x866B }, // <CJK>
        { (char) 0x928F, (char) 0x8877 }, // <CJK>
        { (char) 0x9290, (char) 0x8A3B }, // <CJK>
        { (char) 0x9291, (char) 0x914E }, // <CJK>
        { (char) 0x9292, (char) 0x92F3 }, // <CJK>
        { (char) 0x9293, (char) 0x99D0 }, // <CJK>
        { (char) 0x9294, (char) 0x6A17 }, // <CJK>
        { (char) 0x9295, (char) 0x7026 }, // <CJK>
        { (char) 0x9296, (char) 0x732A }, // <CJK>
        { (char) 0x9297, (char) 0x82E7 }, // <CJK>
        { (char) 0x9298, (char) 0x8457 }, // <CJK>
        { (char) 0x9299, (char) 0x8CAF }, // <CJK>
        { (char) 0x929A, (char) 0x4E01 }, // <CJK>
        { (char) 0x929B, (char) 0x5146 }, // <CJK>
        { (char) 0x929C, (char) 0x51CB }, // <CJK>
        { (char) 0x929D, (char) 0x558B }, // <CJK>
        { (char) 0x929E, (char) 0x5BF5 }, // <CJK>
        { (char) 0x929F, (char) 0x5E16 }, // <CJK>
        { (char) 0x92A0, (char) 0x5E33 }, // <CJK>
        { (char) 0x92A1, (char) 0x5E81 }, // <CJK>
        { (char) 0x92A2, (char) 0x5F14 }, // <CJK>
        { (char) 0x92A3, (char) 0x5F35 }, // <CJK>
        { (char) 0x92A4, (char) 0x5F6B }, // <CJK>
        { (char) 0x92A5, (char) 0x5FB4 }, // <CJK>
        { (char) 0x92A6, (char) 0x61F2 }, // <CJK>
        { (char) 0x92A7, (char) 0x6311 }, // <CJK>
        { (char) 0x92A8, (char) 0x66A2 }, // <CJK>
        { (char) 0x92A9, (char) 0x671D }, // <CJK>
        { (char) 0x92AA, (char) 0x6F6E }, // <CJK>
        { (char) 0x92AB, (char) 0x7252 }, // <CJK>
        { (char) 0x92AC, (char) 0x753A }, // <CJK>
        { (char) 0x92AD, (char) 0x773A }, // <CJK>
        { (char) 0x92AE, (char) 0x8074 }, // <CJK>
        { (char) 0x92AF, (char) 0x8139 }, // <CJK>
        { (char) 0x92B0, (char) 0x8178 }, // <CJK>
        { (char) 0x92B1, (char) 0x8776 }, // <CJK>
        { (char) 0x92B2, (char) 0x8ABF }, // <CJK>
        { (char) 0x92B3, (char) 0x8ADC }, // <CJK>
        { (char) 0x92B4, (char) 0x8D85 }, // <CJK>
        { (char) 0x92B5, (char) 0x8DF3 }, // <CJK>
        { (char) 0x92B6, (char) 0x929A }, // <CJK>
        { (char) 0x92B7, (char) 0x9577 }, // <CJK>
        { (char) 0x92B8, (char) 0x9802 }, // <CJK>
        { (char) 0x92B9, (char) 0x9CE5 }, // <CJK>
        { (char) 0x92BA, (char) 0x52C5 }, // <CJK>
        { (char) 0x92BB, (char) 0x6357 }, // <CJK>
        { (char) 0x92BC, (char) 0x76F4 }, // <CJK>
        { (char) 0x92BD, (char) 0x6715 }, // <CJK>
        { (char) 0x92BE, (char) 0x6C88 }, // <CJK>
        { (char) 0x92BF, (char) 0x73CD }, // <CJK>
        { (char) 0x92C0, (char) 0x8CC3 }, // <CJK>
        { (char) 0x92C1, (char) 0x93AE }, // <CJK>
        { (char) 0x92C2, (char) 0x9673 }, // <CJK>
        { (char) 0x92C3, (char) 0x6D25 }, // <CJK>
        { (char) 0x92C4, (char) 0x589C }, // <CJK>
        { (char) 0x92C5, (char) 0x690E }, // <CJK>
        { (char) 0x92C6, (char) 0x69CC }, // <CJK>
        { (char) 0x92C7, (char) 0x8FFD }, // <CJK>
        { (char) 0x92C8, (char) 0x939A }, // <CJK>
        { (char) 0x92C9, (char) 0x75DB }, // <CJK>
        { (char) 0x92CA, (char) 0x901A }, // <CJK>
        { (char) 0x92CB, (char) 0x585A }, // <CJK>
        { (char) 0x92CC, (char) 0x6802 }, // <CJK>
        { (char) 0x92CD, (char) 0x63B4 }, // <CJK>
        { (char) 0x92CE, (char) 0x69FB }, // <CJK>
        { (char) 0x92CF, (char) 0x4F43 }, // <CJK>
        { (char) 0x92D0, (char) 0x6F2C }, // <CJK>
        { (char) 0x92D1, (char) 0x67D8 }, // <CJK>
        { (char) 0x92D2, (char) 0x8FBB }, // <CJK>
        { (char) 0x92D3, (char) 0x8526 }, // <CJK>
        { (char) 0x92D4, (char) 0x7DB4 }, // <CJK>
        { (char) 0x92D5, (char) 0x9354 }, // <CJK>
        { (char) 0x92D6, (char) 0x693F }, // <CJK>
        { (char) 0x92D7, (char) 0x6F70 }, // <CJK>
        { (char) 0x92D8, (char) 0x576A }, // <CJK>
        { (char) 0x92D9, (char) 0x58F7 }, // <CJK>
        { (char) 0x92DA, (char) 0x5B2C }, // <CJK>
        { (char) 0x92DB, (char) 0x7D2C }, // <CJK>
        { (char) 0x92DC, (char) 0x722A }, // <CJK>
        { (char) 0x92DD, (char) 0x540A }, // <CJK>
        { (char) 0x92DE, (char) 0x91E3 }, // <CJK>
        { (char) 0x92DF, (char) 0x9DB4 }, // <CJK>
        { (char) 0x92E0, (char) 0x4EAD }, // <CJK>
        { (char) 0x92E1, (char) 0x4F4E }, // <CJK>
        { (char) 0x92E2, (char) 0x505C }, // <CJK>
        { (char) 0x92E3, (char) 0x5075 }, // <CJK>
        { (char) 0x92E4, (char) 0x5243 }, // <CJK>
        { (char) 0x92E5, (char) 0x8C9E }, // <CJK>
        { (char) 0x92E6, (char) 0x5448 }, // <CJK>
        { (char) 0x92E7, (char) 0x5824 }, // <CJK>
        { (char) 0x92E8, (char) 0x5B9A }, // <CJK>
        { (char) 0x92E9, (char) 0x5E1D }, // <CJK>
        { (char) 0x92EA, (char) 0x5E95 }, // <CJK>
        { (char) 0x92EB, (char) 0x5EAD }, // <CJK>
        { (char) 0x92EC, (char) 0x5EF7 }, // <CJK>
        { (char) 0x92ED, (char) 0x5F1F }, // <CJK>
        { (char) 0x92EE, (char) 0x608C }, // <CJK>
        { (char) 0x92EF, (char) 0x62B5 }, // <CJK>
        { (char) 0x92F0, (char) 0x633A }, // <CJK>
        { (char) 0x92F1, (char) 0x63D0 }, // <CJK>
        { (char) 0x92F2, (char) 0x68AF }, // <CJK>
        { (char) 0x92F3, (char) 0x6C40 }, // <CJK>
        { (char) 0x92F4, (char) 0x7887 }, // <CJK>
        { (char) 0x92F5, (char) 0x798E }, // <CJK>
        { (char) 0x92F6, (char) 0x7A0B }, // <CJK>
        { (char) 0x92F7, (char) 0x7DE0 }, // <CJK>
        { (char) 0x92F8, (char) 0x8247 }, // <CJK>
        { (char) 0x92F9, (char) 0x8A02 }, // <CJK>
        { (char) 0x92FA, (char) 0x8AE6 }, // <CJK>
        { (char) 0x92FB, (char) 0x8E44 }, // <CJK>
        { (char) 0x92FC, (char) 0x9013 }, // <CJK>
        { (char) 0x9340, (char) 0x90B8 }, // <CJK>
        { (char) 0x9341, (char) 0x912D }, // <CJK>
        { (char) 0x9342, (char) 0x91D8 }, // <CJK>
        { (char) 0x9343, (char) 0x9F0E }, // <CJK>
        { (char) 0x9344, (char) 0x6CE5 }, // <CJK>
        { (char) 0x9345, (char) 0x6458 }, // <CJK>
        { (char) 0x9346, (char) 0x64E2 }, // <CJK>
        { (char) 0x9347, (char) 0x6575 }, // <CJK>
        { (char) 0x9348, (char) 0x6EF4 }, // <CJK>
        { (char) 0x9349, (char) 0x7684 }, // <CJK>
        { (char) 0x934A, (char) 0x7B1B }, // <CJK>
        { (char) 0x934B, (char) 0x9069 }, // <CJK>
        { (char) 0x934C, (char) 0x93D1 }, // <CJK>
        { (char) 0x934D, (char) 0x6EBA }, // <CJK>
        { (char) 0x934E, (char) 0x54F2 }, // <CJK>
        { (char) 0x934F, (char) 0x5FB9 }, // <CJK>
        { (char) 0x9350, (char) 0x64A4 }, // <CJK>
        { (char) 0x9351, (char) 0x8F4D }, // <CJK>
        { (char) 0x9352, (char) 0x8FED }, // <CJK>
        { (char) 0x9353, (char) 0x9244 }, // <CJK>
        { (char) 0x9354, (char) 0x5178 }, // <CJK>
        { (char) 0x9355, (char) 0x586B }, // <CJK>
        { (char) 0x9356, (char) 0x5929 }, // <CJK>
        { (char) 0x9357, (char) 0x5C55 }, // <CJK>
        { (char) 0x9358, (char) 0x5E97 }, // <CJK>
        { (char) 0x9359, (char) 0x6DFB }, // <CJK>
        { (char) 0x935A, (char) 0x7E8F }, // <CJK>
        { (char) 0x935B, (char) 0x751C }, // <CJK>
        { (char) 0x935C, (char) 0x8CBC }, // <CJK>
        { (char) 0x935D, (char) 0x8EE2 }, // <CJK>
        { (char) 0x935E, (char) 0x985B }, // <CJK>
        { (char) 0x935F, (char) 0x70B9 }, // <CJK>
        { (char) 0x9360, (char) 0x4F1D }, // <CJK>
        { (char) 0x9361, (char) 0x6BBF }, // <CJK>
        { (char) 0x9362, (char) 0x6FB1 }, // <CJK>
        { (char) 0x9363, (char) 0x7530 }, // <CJK>
        { (char) 0x9364, (char) 0x96FB }, // <CJK>
        { (char) 0x9365, (char) 0x514E }, // <CJK>
        { (char) 0x9366, (char) 0x5410 }, // <CJK>
        { (char) 0x9367, (char) 0x5835 }, // <CJK>
        { (char) 0x9368, (char) 0x5857 }, // <CJK>
        { (char) 0x9369, (char) 0x59AC }, // <CJK>
        { (char) 0x936A, (char) 0x5C60 }, // <CJK>
        { (char) 0x936B, (char) 0x5F92 }, // <CJK>
        { (char) 0x936C, (char) 0x6597 }, // <CJK>
        { (char) 0x936D, (char) 0x675C }, // <CJK>
        { (char) 0x936E, (char) 0x6E21 }, // <CJK>
        { (char) 0x936F, (char) 0x767B }, // <CJK>
        { (char) 0x9370, (char) 0x83DF }, // <CJK>
        { (char) 0x9371, (char) 0x8CED }, // <CJK>
        { (char) 0x9372, (char) 0x9014 }, // <CJK>
        { (char) 0x9373, (char) 0x90FD }, // <CJK>
        { (char) 0x9374, (char) 0x934D }, // <CJK>
        { (char) 0x9375, (char) 0x7825 }, // <CJK>
        { (char) 0x9376, (char) 0x783A }, // <CJK>
        { (char) 0x9377, (char) 0x52AA }, // <CJK>
        { (char) 0x9378, (char) 0x5EA6 }, // <CJK>
        { (char) 0x9379, (char) 0x571F }, // <CJK>
        { (char) 0x937A, (char) 0x5974 }, // <CJK>
        { (char) 0x937B, (char) 0x6012 }, // <CJK>
        { (char) 0x937C, (char) 0x5012 }, // <CJK>
        { (char) 0x937D, (char) 0x515A }, // <CJK>
        { (char) 0x937E, (char) 0x51AC }, // <CJK>
        { (char) 0x9380, (char) 0x51CD }, // <CJK>
        { (char) 0x9381, (char) 0x5200 }, // <CJK>
        { (char) 0x9382, (char) 0x5510 }, // <CJK>
        { (char) 0x9383, (char) 0x5854 }, // <CJK>
        { (char) 0x9384, (char) 0x5858 }, // <CJK>
        { (char) 0x9385, (char) 0x5957 }, // <CJK>
        { (char) 0x9386, (char) 0x5B95 }, // <CJK>
        { (char) 0x9387, (char) 0x5CF6 }, // <CJK>
        { (char) 0x9388, (char) 0x5D8B }, // <CJK>
        { (char) 0x9389, (char) 0x60BC }, // <CJK>
        { (char) 0x938A, (char) 0x6295 }, // <CJK>
        { (char) 0x938B, (char) 0x642D }, // <CJK>
        { (char) 0x938C, (char) 0x6771 }, // <CJK>
        { (char) 0x938D, (char) 0x6843 }, // <CJK>
        { (char) 0x938E, (char) 0x68BC }, // <CJK>
        { (char) 0x938F, (char) 0x68DF }, // <CJK>
        { (char) 0x9390, (char) 0x76D7 }, // <CJK>
        { (char) 0x9391, (char) 0x6DD8 }, // <CJK>
        { (char) 0x9392, (char) 0x6E6F }, // <CJK>
        { (char) 0x9393, (char) 0x6D9B }, // <CJK>
        { (char) 0x9394, (char) 0x706F }, // <CJK>
        { (char) 0x9395, (char) 0x71C8 }, // <CJK>
        { (char) 0x9396, (char) 0x5F53 }, // <CJK>
        { (char) 0x9397, (char) 0x75D8 }, // <CJK>
        { (char) 0x9398, (char) 0x7977 }, // <CJK>
        { (char) 0x9399, (char) 0x7B49 }, // <CJK>
        { (char) 0x939A, (char) 0x7B54 }, // <CJK>
        { (char) 0x939B, (char) 0x7B52 }, // <CJK>
        { (char) 0x939C, (char) 0x7CD6 }, // <CJK>
        { (char) 0x939D, (char) 0x7D71 }, // <CJK>
        { (char) 0x939E, (char) 0x5230 }, // <CJK>
        { (char) 0x939F, (char) 0x8463 }, // <CJK>
        { (char) 0x93A0, (char) 0x8569 }, // <CJK>
        { (char) 0x93A1, (char) 0x85E4 }, // <CJK>
        { (char) 0x93A2, (char) 0x8A0E }, // <CJK>
        { (char) 0x93A3, (char) 0x8B04 }, // <CJK>
        { (char) 0x93A4, (char) 0x8C46 }, // <CJK>
        { (char) 0x93A5, (char) 0x8E0F }, // <CJK>
        { (char) 0x93A6, (char) 0x9003 }, // <CJK>
        { (char) 0x93A7, (char) 0x900F }, // <CJK>
        { (char) 0x93A8, (char) 0x9419 }, // <CJK>
        { (char) 0x93A9, (char) 0x9676 }, // <CJK>
        { (char) 0x93AA, (char) 0x982D }, // <CJK>
        { (char) 0x93AB, (char) 0x9A30 }, // <CJK>
        { (char) 0x93AC, (char) 0x95D8 }, // <CJK>
        { (char) 0x93AD, (char) 0x50CD }, // <CJK>
        { (char) 0x93AE, (char) 0x52D5 }, // <CJK>
        { (char) 0x93AF, (char) 0x540C }, // <CJK>
        { (char) 0x93B0, (char) 0x5802 }, // <CJK>
        { (char) 0x93B1, (char) 0x5C0E }, // <CJK>
        { (char) 0x93B2, (char) 0x61A7 }, // <CJK>
        { (char) 0x93B3, (char) 0x649E }, // <CJK>
        { (char) 0x93B4, (char) 0x6D1E }, // <CJK>
        { (char) 0x93B5, (char) 0x77B3 }, // <CJK>
        { (char) 0x93B6, (char) 0x7AE5 }, // <CJK>
        { (char) 0x93B7, (char) 0x80F4 }, // <CJK>
        { (char) 0x93B8, (char) 0x8404 }, // <CJK>
        { (char) 0x93B9, (char) 0x9053 }, // <CJK>
        { (char) 0x93BA, (char) 0x9285 }, // <CJK>
        { (char) 0x93BB, (char) 0x5CE0 }, // <CJK>
        { (char) 0x93BC, (char) 0x9D07 }, // <CJK>
        { (char) 0x93BD, (char) 0x533F }, // <CJK>
        { (char) 0x93BE, (char) 0x5F97 }, // <CJK>
        { (char) 0x93BF, (char) 0x5FB3 }, // <CJK>
        { (char) 0x93C0, (char) 0x6D9C }, // <CJK>
        { (char) 0x93C1, (char) 0x7279 }, // <CJK>
        { (char) 0x93C2, (char) 0x7763 }, // <CJK>
        { (char) 0x93C3, (char) 0x79BF }, // <CJK>
        { (char) 0x93C4, (char) 0x7BE4 }, // <CJK>
        { (char) 0x93C5, (char) 0x6BD2 }, // <CJK>
        { (char) 0x93C6, (char) 0x72EC }, // <CJK>
        { (char) 0x93C7, (char) 0x8AAD }, // <CJK>
        { (char) 0x93C8, (char) 0x6803 }, // <CJK>
        { (char) 0x93C9, (char) 0x6A61 }, // <CJK>
        { (char) 0x93CA, (char) 0x51F8 }, // <CJK>
        { (char) 0x93CB, (char) 0x7A81 }, // <CJK>
        { (char) 0x93CC, (char) 0x6934 }, // <CJK>
        { (char) 0x93CD, (char) 0x5C4A }, // <CJK>
        { (char) 0x93CE, (char) 0x9CF6 }, // <CJK>
        { (char) 0x93CF, (char) 0x82EB }, // <CJK>
        { (char) 0x93D0, (char) 0x5BC5 }, // <CJK>
        { (char) 0x93D1, (char) 0x9149 }, // <CJK>
        { (char) 0x93D2, (char) 0x701E }, // <CJK>
        { (char) 0x93D3, (char) 0x5678 }, // <CJK>
        { (char) 0x93D4, (char) 0x5C6F }, // <CJK>
        { (char) 0x93D5, (char) 0x60C7 }, // <CJK>
        { (char) 0x93D6, (char) 0x6566 }, // <CJK>
        { (char) 0x93D7, (char) 0x6C8C }, // <CJK>
        { (char) 0x93D8, (char) 0x8C5A }, // <CJK>
        { (char) 0x93D9, (char) 0x9041 }, // <CJK>
        { (char) 0x93DA, (char) 0x9813 }, // <CJK>
        { (char) 0x93DB, (char) 0x5451 }, // <CJK>
        { (char) 0x93DC, (char) 0x66C7 }, // <CJK>
        { (char) 0x93DD, (char) 0x920D }, // <CJK>
        { (char) 0x93DE, (char) 0x5948 }, // <CJK>
        { (char) 0x93DF, (char) 0x90A3 }, // <CJK>
        { (char) 0x93E0, (char) 0x5185 }, // <CJK>
        { (char) 0x93E1, (char) 0x4E4D }, // <CJK>
        { (char) 0x93E2, (char) 0x51EA }, // <CJK>
        { (char) 0x93E3, (char) 0x8599 }, // <CJK>
        { (char) 0x93E4, (char) 0x8B0E }, // <CJK>
        { (char) 0x93E5, (char) 0x7058 }, // <CJK>
        { (char) 0x93E6, (char) 0x637A }, // <CJK>
        { (char) 0x93E7, (char) 0x934B }, // <CJK>
        { (char) 0x93E8, (char) 0x6962 }, // <CJK>
        { (char) 0x93E9, (char) 0x99B4 }, // <CJK>
        { (char) 0x93EA, (char) 0x7E04 }, // <CJK>
        { (char) 0x93EB, (char) 0x7577 }, // <CJK>
        { (char) 0x93EC, (char) 0x5357 }, // <CJK>
        { (char) 0x93ED, (char) 0x6960 }, // <CJK>
        { (char) 0x93EE, (char) 0x8EDF }, // <CJK>
        { (char) 0x93EF, (char) 0x96E3 }, // <CJK>
        { (char) 0x93F0, (char) 0x6C5D }, // <CJK>
        { (char) 0x93F1, (char) 0x4E8C }, // <CJK>
        { (char) 0x93F2, (char) 0x5C3C }, // <CJK>
        { (char) 0x93F3, (char) 0x5F10 }, // <CJK>
        { (char) 0x93F4, (char) 0x8FE9 }, // <CJK>
        { (char) 0x93F5, (char) 0x5302 }, // <CJK>
        { (char) 0x93F6, (char) 0x8CD1 }, // <CJK>
        { (char) 0x93F7, (char) 0x8089 }, // <CJK>
        { (char) 0x93F8, (char) 0x8679 }, // <CJK>
        { (char) 0x93F9, (char) 0x5EFF }, // <CJK>
        { (char) 0x93FA, (char) 0x65E5 }, // <CJK>
        { (char) 0x93FB, (char) 0x4E73 }, // <CJK>
        { (char) 0x93FC, (char) 0x5165 }, // <CJK>
        { (char) 0x9440, (char) 0x5982 }, // <CJK>
        { (char) 0x9441, (char) 0x5C3F }, // <CJK>
        { (char) 0x9442, (char) 0x97EE }, // <CJK>
        { (char) 0x9443, (char) 0x4EFB }, // <CJK>
        { (char) 0x9444, (char) 0x598A }, // <CJK>
        { (char) 0x9445, (char) 0x5FCD }, // <CJK>
        { (char) 0x9446, (char) 0x8A8D }, // <CJK>
        { (char) 0x9447, (char) 0x6FE1 }, // <CJK>
        { (char) 0x9448, (char) 0x79B0 }, // <CJK>
        { (char) 0x9449, (char) 0x7962 }, // <CJK>
        { (char) 0x944A, (char) 0x5BE7 }, // <CJK>
        { (char) 0x944B, (char) 0x8471 }, // <CJK>
        { (char) 0x944C, (char) 0x732B }, // <CJK>
        { (char) 0x944D, (char) 0x71B1 }, // <CJK>
        { (char) 0x944E, (char) 0x5E74 }, // <CJK>
        { (char) 0x944F, (char) 0x5FF5 }, // <CJK>
        { (char) 0x9450, (char) 0x637B }, // <CJK>
        { (char) 0x9451, (char) 0x649A }, // <CJK>
        { (char) 0x9452, (char) 0x71C3 }, // <CJK>
        { (char) 0x9453, (char) 0x7C98 }, // <CJK>
        { (char) 0x9454, (char) 0x4E43 }, // <CJK>
        { (char) 0x9455, (char) 0x5EFC }, // <CJK>
        { (char) 0x9456, (char) 0x4E4B }, // <CJK>
        { (char) 0x9457, (char) 0x57DC }, // <CJK>
        { (char) 0x9458, (char) 0x56A2 }, // <CJK>
        { (char) 0x9459, (char) 0x60A9 }, // <CJK>
        { (char) 0x945A, (char) 0x6FC3 }, // <CJK>
        { (char) 0x945B, (char) 0x7D0D }, // <CJK>
        { (char) 0x945C, (char) 0x80FD }, // <CJK>
        { (char) 0x945D, (char) 0x8133 }, // <CJK>
        { (char) 0x945E, (char) 0x81BF }, // <CJK>
        { (char) 0x945F, (char) 0x8FB2 }, // <CJK>
        { (char) 0x9460, (char) 0x8997 }, // <CJK>
        { (char) 0x9461, (char) 0x86A4 }, // <CJK>
        { (char) 0x9462, (char) 0x5DF4 }, // <CJK>
        { (char) 0x9463, (char) 0x628A }, // <CJK>
        { (char) 0x9464, (char) 0x64AD }, // <CJK>
        { (char) 0x9465, (char) 0x8987 }, // <CJK>
        { (char) 0x9466, (char) 0x6777 }, // <CJK>
        { (char) 0x9467, (char) 0x6CE2 }, // <CJK>
        { (char) 0x9468, (char) 0x6D3E }, // <CJK>
        { (char) 0x9469, (char) 0x7436 }, // <CJK>
        { (char) 0x946A, (char) 0x7834 }, // <CJK>
        { (char) 0x946B, (char) 0x5A46 }, // <CJK>
        { (char) 0x946C, (char) 0x7F75 }, // <CJK>
        { (char) 0x946D, (char) 0x82AD }, // <CJK>
        { (char) 0x946E, (char) 0x99AC }, // <CJK>
        { (char) 0x946F, (char) 0x4FF3 }, // <CJK>
        { (char) 0x9470, (char) 0x5EC3 }, // <CJK>
        { (char) 0x9471, (char) 0x62DD }, // <CJK>
        { (char) 0x9472, (char) 0x6392 }, // <CJK>
        { (char) 0x9473, (char) 0x6557 }, // <CJK>
        { (char) 0x9474, (char) 0x676F }, // <CJK>
        { (char) 0x9475, (char) 0x76C3 }, // <CJK>
        { (char) 0x9476, (char) 0x724C }, // <CJK>
        { (char) 0x9477, (char) 0x80CC }, // <CJK>
        { (char) 0x9478, (char) 0x80BA }, // <CJK>
        { (char) 0x9479, (char) 0x8F29 }, // <CJK>
        { (char) 0x947A, (char) 0x914D }, // <CJK>
        { (char) 0x947B, (char) 0x500D }, // <CJK>
        { (char) 0x947C, (char) 0x57F9 }, // <CJK>
        { (char) 0x947D, (char) 0x5A92 }, // <CJK>
        { (char) 0x947E, (char) 0x6885 }, // <CJK>
        { (char) 0x9480, (char) 0x6973 }, // <CJK>
        { (char) 0x9481, (char) 0x7164 }, // <CJK>
        { (char) 0x9482, (char) 0x72FD }, // <CJK>
        { (char) 0x9483, (char) 0x8CB7 }, // <CJK>
        { (char) 0x9484, (char) 0x58F2 }, // <CJK>
        { (char) 0x9485, (char) 0x8CE0 }, // <CJK>
        { (char) 0x9486, (char) 0x966A }, // <CJK>
        { (char) 0x9487, (char) 0x9019 }, // <CJK>
        { (char) 0x9488, (char) 0x877F }, // <CJK>
        { (char) 0x9489, (char) 0x79E4 }, // <CJK>
        { (char) 0x948A, (char) 0x77E7 }, // <CJK>
        { (char) 0x948B, (char) 0x8429 }, // <CJK>
        { (char) 0x948C, (char) 0x4F2F }, // <CJK>
        { (char) 0x948D, (char) 0x5265 }, // <CJK>
        { (char) 0x948E, (char) 0x535A }, // <CJK>
        { (char) 0x948F, (char) 0x62CD }, // <CJK>
        { (char) 0x9490, (char) 0x67CF }, // <CJK>
        { (char) 0x9491, (char) 0x6CCA }, // <CJK>
        { (char) 0x9492, (char) 0x767D }, // <CJK>
        { (char) 0x9493, (char) 0x7B94 }, // <CJK>
        { (char) 0x9494, (char) 0x7C95 }, // <CJK>
        { (char) 0x9495, (char) 0x8236 }, // <CJK>
        { (char) 0x9496, (char) 0x8584 }, // <CJK>
        { (char) 0x9497, (char) 0x8FEB }, // <CJK>
        { (char) 0x9498, (char) 0x66DD }, // <CJK>
        { (char) 0x9499, (char) 0x6F20 }, // <CJK>
        { (char) 0x949A, (char) 0x7206 }, // <CJK>
        { (char) 0x949B, (char) 0x7E1B }, // <CJK>
        { (char) 0x949C, (char) 0x83AB }, // <CJK>
        { (char) 0x949D, (char) 0x99C1 }, // <CJK>
        { (char) 0x949E, (char) 0x9EA6 }, // <CJK>
        { (char) 0x949F, (char) 0x51FD }, // <CJK>
        { (char) 0x94A0, (char) 0x7BB1 }, // <CJK>
        { (char) 0x94A1, (char) 0x7872 }, // <CJK>
        { (char) 0x94A2, (char) 0x7BB8 }, // <CJK>
        { (char) 0x94A3, (char) 0x8087 }, // <CJK>
        { (char) 0x94A4, (char) 0x7B48 }, // <CJK>
        { (char) 0x94A5, (char) 0x6AE8 }, // <CJK>
        { (char) 0x94A6, (char) 0x5E61 }, // <CJK>
        { (char) 0x94A7, (char) 0x808C }, // <CJK>
        { (char) 0x94A8, (char) 0x7551 }, // <CJK>
        { (char) 0x94A9, (char) 0x7560 }, // <CJK>
        { (char) 0x94AA, (char) 0x516B }, // <CJK>
        { (char) 0x94AB, (char) 0x9262 }, // <CJK>
        { (char) 0x94AC, (char) 0x6E8C }, // <CJK>
        { (char) 0x94AD, (char) 0x767A }, // <CJK>
        { (char) 0x94AE, (char) 0x9197 }, // <CJK>
        { (char) 0x94AF, (char) 0x9AEA }, // <CJK>
        { (char) 0x94B0, (char) 0x4F10 }, // <CJK>
        { (char) 0x94B1, (char) 0x7F70 }, // <CJK>
        { (char) 0x94B2, (char) 0x629C }, // <CJK>
        { (char) 0x94B3, (char) 0x7B4F }, // <CJK>
        { (char) 0x94B4, (char) 0x95A5 }, // <CJK>
        { (char) 0x94B5, (char) 0x9CE9 }, // <CJK>
        { (char) 0x94B6, (char) 0x567A }, // <CJK>
        { (char) 0x94B7, (char) 0x5859 }, // <CJK>
        { (char) 0x94B8, (char) 0x86E4 }, // <CJK>
        { (char) 0x94B9, (char) 0x96BC }, // <CJK>
        { (char) 0x94BA, (char) 0x4F34 }, // <CJK>
        { (char) 0x94BB, (char) 0x5224 }, // <CJK>
        { (char) 0x94BC, (char) 0x534A }, // <CJK>
        { (char) 0x94BD, (char) 0x53CD }, // <CJK>
        { (char) 0x94BE, (char) 0x53DB }, // <CJK>
        { (char) 0x94BF, (char) 0x5E06 }, // <CJK>
        { (char) 0x94C0, (char) 0x642C }, // <CJK>
        { (char) 0x94C1, (char) 0x6591 }, // <CJK>
        { (char) 0x94C2, (char) 0x677F }, // <CJK>
        { (char) 0x94C3, (char) 0x6C3E }, // <CJK>
        { (char) 0x94C4, (char) 0x6C4E }, // <CJK>
        { (char) 0x94C5, (char) 0x7248 }, // <CJK>
        { (char) 0x94C6, (char) 0x72AF }, // <CJK>
        { (char) 0x94C7, (char) 0x73ED }, // <CJK>
        { (char) 0x94C8, (char) 0x7554 }, // <CJK>
        { (char) 0x94C9, (char) 0x7E41 }, // <CJK>
        { (char) 0x94CA, (char) 0x822C }, // <CJK>
        { (char) 0x94CB, (char) 0x85E9 }, // <CJK>
        { (char) 0x94CC, (char) 0x8CA9 }, // <CJK>
        { (char) 0x94CD, (char) 0x7BC4 }, // <CJK>
        { (char) 0x94CE, (char) 0x91C6 }, // <CJK>
        { (char) 0x94CF, (char) 0x7169 }, // <CJK>
        { (char) 0x94D0, (char) 0x9812 }, // <CJK>
        { (char) 0x94D1, (char) 0x98EF }, // <CJK>
        { (char) 0x94D2, (char) 0x633D }, // <CJK>
        { (char) 0x94D3, (char) 0x6669 }, // <CJK>
        { (char) 0x94D4, (char) 0x756A }, // <CJK>
        { (char) 0x94D5, (char) 0x76E4 }, // <CJK>
        { (char) 0x94D6, (char) 0x78D0 }, // <CJK>
        { (char) 0x94D7, (char) 0x8543 }, // <CJK>
        { (char) 0x94D8, (char) 0x86EE }, // <CJK>
        { (char) 0x94D9, (char) 0x532A }, // <CJK>
        { (char) 0x94DA, (char) 0x5351 }, // <CJK>
        { (char) 0x94DB, (char) 0x5426 }, // <CJK>
        { (char) 0x94DC, (char) 0x5983 }, // <CJK>
        { (char) 0x94DD, (char) 0x5E87 }, // <CJK>
        { (char) 0x94DE, (char) 0x5F7C }, // <CJK>
        { (char) 0x94DF, (char) 0x60B2 }, // <CJK>
        { (char) 0x94E0, (char) 0x6249 }, // <CJK>
        { (char) 0x94E1, (char) 0x6279 }, // <CJK>
        { (char) 0x94E2, (char) 0x62AB }, // <CJK>
        { (char) 0x94E3, (char) 0x6590 }, // <CJK>
        { (char) 0x94E4, (char) 0x6BD4 }, // <CJK>
        { (char) 0x94E5, (char) 0x6CCC }, // <CJK>
        { (char) 0x94E6, (char) 0x75B2 }, // <CJK>
        { (char) 0x94E7, (char) 0x76AE }, // <CJK>
        { (char) 0x94E8, (char) 0x7891 }, // <CJK>
        { (char) 0x94E9, (char) 0x79D8 }, // <CJK>
        { (char) 0x94EA, (char) 0x7DCB }, // <CJK>
        { (char) 0x94EB, (char) 0x7F77 }, // <CJK>
        { (char) 0x94EC, (char) 0x80A5 }, // <CJK>
        { (char) 0x94ED, (char) 0x88AB }, // <CJK>
        { (char) 0x94EE, (char) 0x8AB9 }, // <CJK>
        { (char) 0x94EF, (char) 0x8CBB }, // <CJK>
        { (char) 0x94F0, (char) 0x907F }, // <CJK>
        { (char) 0x94F1, (char) 0x975E }, // <CJK>
        { (char) 0x94F2, (char) 0x98DB }, // <CJK>
        { (char) 0x94F3, (char) 0x6A0B }, // <CJK>
        { (char) 0x94F4, (char) 0x7C38 }, // <CJK>
        { (char) 0x94F5, (char) 0x5099 }, // <CJK>
        { (char) 0x94F6, (char) 0x5C3E }, // <CJK>
        { (char) 0x94F7, (char) 0x5FAE }, // <CJK>
        { (char) 0x94F8, (char) 0x6787 }, // <CJK>
        /* Continued in MacJapaneseStringCodecData2... */
    };
}
