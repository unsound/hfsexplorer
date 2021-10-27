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

package org.catacombae.hfs.original.macroman;

import org.catacombae.hfs.original.SingleByteCodepageStringCodec;

/**
 * String codec for the MacRoman character set.
 *
 * @author <a href="https://catacombae.org" target="_top">Erik Larsson</a>
 */
public class MacRomanStringCodec extends SingleByteCodepageStringCodec {
    static final char[] mappingTable = {
        (char) 0x0000, // NULL
        (char) 0x0001, // START OF HEADING
        (char) 0x0002, // START OF TEXT
        (char) 0x0003, // END OF TEXT
        (char) 0x0004, // END OF TRANSMISSION
        (char) 0x0005, // ENQUIRY
        (char) 0x0006, // ACKNOWLEDGE
        (char) 0x0007, // BELL
        (char) 0x0008, // BACKSPACE
        (char) 0x0009, // HORIZONTAL TABULATION
        (char) 0x000A, // LINE FEED
        (char) 0x000B, // VERTICAL TABULATION
        (char) 0x000C, // FORM FEED
        (char) 0x000D, // CARRIAGE RETURN
        (char) 0x000E, // SHIFT OUT
        (char) 0x000F, // SHIFT IN
        (char) 0x0010, // DATA LINK ESCAPE
        (char) 0x0011, // DEVICE CONTROL ONE
        (char) 0x0012, // DEVICE CONTROL TWO
        (char) 0x0013, // DEVICE CONTROL THREE
        (char) 0x0014, // DEVICE CONTROL FOUR
        (char) 0x0015, // NEGATIVE ACKNOWLEDGE
        (char) 0x0016, // SYNCHRONOUS IDLE
        (char) 0x0017, // END OF TRANSMISSION BLOCK
        (char) 0x0018, // CANCEL
        (char) 0x0019, // END OF MEDIUM
        (char) 0x001A, // SUBSTITUTE
        (char) 0x001B, // ESCAPE
        (char) 0x001C, // FILE SEPARATOR
        (char) 0x001D, // GROUP SEPARATOR
        (char) 0x001E, // RECORD SEPARATOR
        (char) 0x001F, // UNIT SEPARATOR
        (char) 0x0020, // SPACE
        (char) 0x0021, // EXCLAMATION MARK
        (char) 0x0022, // QUOTATION MARK
        (char) 0x0023, // NUMBER SIGN
        (char) 0x0024, // DOLLAR SIGN
        (char) 0x0025, // PERCENT SIGN
        (char) 0x0026, // AMPERSAND
        (char) 0x0027, // APOSTROPHE
        (char) 0x0028, // LEFT PARENTHESIS
        (char) 0x0029, // RIGHT PARENTHESIS
        (char) 0x002A, // ASTERISK
        (char) 0x002B, // PLUS SIGN
        (char) 0x002C, // COMMA
        (char) 0x002D, // HYPHEN-MINUS
        (char) 0x002E, // FULL STOP
        (char) 0x002F, // SOLIDUS
        (char) 0x0030, // DIGIT ZERO
        (char) 0x0031, // DIGIT ONE
        (char) 0x0032, // DIGIT TWO
        (char) 0x0033, // DIGIT THREE
        (char) 0x0034, // DIGIT FOUR
        (char) 0x0035, // DIGIT FIVE
        (char) 0x0036, // DIGIT SIX
        (char) 0x0037, // DIGIT SEVEN
        (char) 0x0038, // DIGIT EIGHT
        (char) 0x0039, // DIGIT NINE
        (char) 0x003A, // COLON
        (char) 0x003B, // SEMICOLON
        (char) 0x003C, // LESS-THAN SIGN
        (char) 0x003D, // EQUALS SIGN
        (char) 0x003E, // GREATER-THAN SIGN
        (char) 0x003F, // QUESTION MARK
        (char) 0x0040, // COMMERCIAL AT
        (char) 0x0041, // LATIN CAPITAL LETTER A
        (char) 0x0042, // LATIN CAPITAL LETTER B
        (char) 0x0043, // LATIN CAPITAL LETTER C
        (char) 0x0044, // LATIN CAPITAL LETTER D
        (char) 0x0045, // LATIN CAPITAL LETTER E
        (char) 0x0046, // LATIN CAPITAL LETTER F
        (char) 0x0047, // LATIN CAPITAL LETTER G
        (char) 0x0048, // LATIN CAPITAL LETTER H
        (char) 0x0049, // LATIN CAPITAL LETTER I
        (char) 0x004A, // LATIN CAPITAL LETTER J
        (char) 0x004B, // LATIN CAPITAL LETTER K
        (char) 0x004C, // LATIN CAPITAL LETTER L
        (char) 0x004D, // LATIN CAPITAL LETTER M
        (char) 0x004E, // LATIN CAPITAL LETTER N
        (char) 0x004F, // LATIN CAPITAL LETTER O
        (char) 0x0050, // LATIN CAPITAL LETTER P
        (char) 0x0051, // LATIN CAPITAL LETTER Q
        (char) 0x0052, // LATIN CAPITAL LETTER R
        (char) 0x0053, // LATIN CAPITAL LETTER S
        (char) 0x0054, // LATIN CAPITAL LETTER T
        (char) 0x0055, // LATIN CAPITAL LETTER U
        (char) 0x0056, // LATIN CAPITAL LETTER V
        (char) 0x0057, // LATIN CAPITAL LETTER W
        (char) 0x0058, // LATIN CAPITAL LETTER X
        (char) 0x0059, // LATIN CAPITAL LETTER Y
        (char) 0x005A, // LATIN CAPITAL LETTER Z
        (char) 0x005B, // LEFT SQUARE BRACKET
        (char) 0x005C, // REVERSE SOLIDUS
        (char) 0x005D, // RIGHT SQUARE BRACKET
        (char) 0x005E, // CIRCUMFLEX ACCENT
        (char) 0x005F, // LOW LINE
        (char) 0x0060, // GRAVE ACCENT
        (char) 0x0061, // LATIN SMALL LETTER A
        (char) 0x0062, // LATIN SMALL LETTER B
        (char) 0x0063, // LATIN SMALL LETTER C
        (char) 0x0064, // LATIN SMALL LETTER D
        (char) 0x0065, // LATIN SMALL LETTER E
        (char) 0x0066, // LATIN SMALL LETTER F
        (char) 0x0067, // LATIN SMALL LETTER G
        (char) 0x0068, // LATIN SMALL LETTER H
        (char) 0x0069, // LATIN SMALL LETTER I
        (char) 0x006A, // LATIN SMALL LETTER J
        (char) 0x006B, // LATIN SMALL LETTER K
        (char) 0x006C, // LATIN SMALL LETTER L
        (char) 0x006D, // LATIN SMALL LETTER M
        (char) 0x006E, // LATIN SMALL LETTER N
        (char) 0x006F, // LATIN SMALL LETTER O
        (char) 0x0070, // LATIN SMALL LETTER P
        (char) 0x0071, // LATIN SMALL LETTER Q
        (char) 0x0072, // LATIN SMALL LETTER R
        (char) 0x0073, // LATIN SMALL LETTER S
        (char) 0x0074, // LATIN SMALL LETTER T
        (char) 0x0075, // LATIN SMALL LETTER U
        (char) 0x0076, // LATIN SMALL LETTER V
        (char) 0x0077, // LATIN SMALL LETTER W
        (char) 0x0078, // LATIN SMALL LETTER X
        (char) 0x0079, // LATIN SMALL LETTER Y
        (char) 0x007A, // LATIN SMALL LETTER Z
        (char) 0x007B, // LEFT CURLY BRACKET
        (char) 0x007C, // VERTICAL LINE
        (char) 0x007D, // RIGHT CURLY BRACKET
        (char) 0x007E, // TILDE
        (char) 0x007F, // DELETE
        (char) 0x00C4, // LATIN CAPITAL LETTER A WITH DIAERESIS
        (char) 0x00C5, // LATIN CAPITAL LETTER A WITH RING ABOVE
        (char) 0x00C7, // LATIN CAPITAL LETTER C WITH CEDILLA
        (char) 0x00C9, // LATIN CAPITAL LETTER E WITH ACUTE
        (char) 0x00D1, // LATIN CAPITAL LETTER N WITH TILDE
        (char) 0x00D6, // LATIN CAPITAL LETTER O WITH DIAERESIS
        (char) 0x00DC, // LATIN CAPITAL LETTER U WITH DIAERESIS
        (char) 0x00E1, // LATIN SMALL LETTER A WITH ACUTE
        (char) 0x00E0, // LATIN SMALL LETTER A WITH GRAVE
        (char) 0x00E2, // LATIN SMALL LETTER A WITH CIRCUMFLEX
        (char) 0x00E4, // LATIN SMALL LETTER A WITH DIAERESIS
        (char) 0x00E3, // LATIN SMALL LETTER A WITH TILDE
        (char) 0x00E5, // LATIN SMALL LETTER A WITH RING ABOVE
        (char) 0x00E7, // LATIN SMALL LETTER C WITH CEDILLA
        (char) 0x00E9, // LATIN SMALL LETTER E WITH ACUTE
        (char) 0x00E8, // LATIN SMALL LETTER E WITH GRAVE
        (char) 0x00EA, // LATIN SMALL LETTER E WITH CIRCUMFLEX
        (char) 0x00EB, // LATIN SMALL LETTER E WITH DIAERESIS
        (char) 0x00ED, // LATIN SMALL LETTER I WITH ACUTE
        (char) 0x00EC, // LATIN SMALL LETTER I WITH GRAVE
        (char) 0x00EE, // LATIN SMALL LETTER I WITH CIRCUMFLEX
        (char) 0x00EF, // LATIN SMALL LETTER I WITH DIAERESIS
        (char) 0x00F1, // LATIN SMALL LETTER N WITH TILDE
        (char) 0x00F3, // LATIN SMALL LETTER O WITH ACUTE
        (char) 0x00F2, // LATIN SMALL LETTER O WITH GRAVE
        (char) 0x00F4, // LATIN SMALL LETTER O WITH CIRCUMFLEX
        (char) 0x00F6, // LATIN SMALL LETTER O WITH DIAERESIS
        (char) 0x00F5, // LATIN SMALL LETTER O WITH TILDE
        (char) 0x00FA, // LATIN SMALL LETTER U WITH ACUTE
        (char) 0x00F9, // LATIN SMALL LETTER U WITH GRAVE
        (char) 0x00FB, // LATIN SMALL LETTER U WITH CIRCUMFLEX
        (char) 0x00FC, // LATIN SMALL LETTER U WITH DIAERESIS
        (char) 0x2020, // DAGGER
        (char) 0x00B0, // DEGREE SIGN
        (char) 0x00A2, // CENT SIGN
        (char) 0x00A3, // POUND SIGN
        (char) 0x00A7, // SECTION SIGN
        (char) 0x2022, // BULLET
        (char) 0x00B6, // PILCROW SIGN
        (char) 0x00DF, // LATIN SMALL LETTER SHARP S
        (char) 0x00AE, // REGISTERED SIGN
        (char) 0x00A9, // COPYRIGHT SIGN
        (char) 0x2122, // TRADE MARK SIGN
        (char) 0x00B4, // ACUTE ACCENT
        (char) 0x00A8, // DIAERESIS
        (char) 0x2260, // NOT EQUAL TO
        (char) 0x00C6, // LATIN CAPITAL LETTER AE
        (char) 0x00D8, // LATIN CAPITAL LETTER O WITH STROKE
        (char) 0x221E, // INFINITY
        (char) 0x00B1, // PLUS-MINUS SIGN
        (char) 0x2264, // LESS-THAN OR EQUAL TO
        (char) 0x2265, // GREATER-THAN OR EQUAL TO
        (char) 0x00A5, // YEN SIGN
        (char) 0x00B5, // MICRO SIGN
        (char) 0x2202, // PARTIAL DIFFERENTIAL
        (char) 0x2211, // N-ARY SUMMATION
        (char) 0x220F, // N-ARY PRODUCT
        (char) 0x03C0, // GREEK SMALL LETTER PI
        (char) 0x222B, // INTEGRAL
        (char) 0x00AA, // FEMININE ORDINAL INDICATOR
        (char) 0x00BA, // MASCULINE ORDINAL INDICATOR
        (char) 0x03A9, // GREEK CAPITAL LETTER OMEGA
        (char) 0x00E6, // LATIN SMALL LETTER AE
        (char) 0x00F8, // LATIN SMALL LETTER O WITH STROKE
        (char) 0x00BF, // INVERTED QUESTION MARK
        (char) 0x00A1, // INVERTED EXCLAMATION MARK
        (char) 0x00AC, // NOT SIGN
        (char) 0x221A, // SQUARE ROOT
        (char) 0x0192, // LATIN SMALL LETTER F WITH HOOK
        (char) 0x2248, // ALMOST EQUAL TO
        (char) 0x2206, // INCREMENT
        (char) 0x00AB, // LEFT-POINTING DOUBLE ANGLE QUOTATION MARK
        (char) 0x00BB, // RIGHT-POINTING DOUBLE ANGLE QUOTATION MARK
        (char) 0x2026, // HORIZONTAL ELLIPSIS
        (char) 0x00A0, // NO-BREAK SPACE
        (char) 0x00C0, // LATIN CAPITAL LETTER A WITH GRAVE
        (char) 0x00C3, // LATIN CAPITAL LETTER A WITH TILDE
        (char) 0x00D5, // LATIN CAPITAL LETTER O WITH TILDE
        (char) 0x0152, // LATIN CAPITAL LIGATURE OE
        (char) 0x0153, // LATIN SMALL LIGATURE OE
        (char) 0x2013, // EN DASH
        (char) 0x2014, // EM DASH
        (char) 0x201C, // LEFT DOUBLE QUOTATION MARK
        (char) 0x201D, // RIGHT DOUBLE QUOTATION MARK
        (char) 0x2018, // LEFT SINGLE QUOTATION MARK
        (char) 0x2019, // RIGHT SINGLE QUOTATION MARK
        (char) 0x00F7, // DIVISION SIGN
        (char) 0x25CA, // LOZENGE
        (char) 0x00FF, // LATIN SMALL LETTER Y WITH DIAERESIS
        (char) 0x0178, // LATIN CAPITAL LETTER Y WITH DIAERESIS
        (char) 0x2044, // FRACTION SLASH
        (char) 0x20AC, // EURO SIGN
        (char) 0x2039, // SINGLE LEFT-POINTING ANGLE QUOTATION MARK
        (char) 0x203A, // SINGLE RIGHT-POINTING ANGLE QUOTATION MARK
        (char) 0xFB01, // LATIN SMALL LIGATURE FI
        (char) 0xFB02, // LATIN SMALL LIGATURE FL
        (char) 0x2021, // DOUBLE DAGGER
        (char) 0x00B7, // MIDDLE DOT
        (char) 0x201A, // SINGLE LOW-9 QUOTATION MARK
        (char) 0x201E, // DOUBLE LOW-9 QUOTATION MARK
        (char) 0x2030, // PER MILLE SIGN
        (char) 0x00C2, // LATIN CAPITAL LETTER A WITH CIRCUMFLEX
        (char) 0x00CA, // LATIN CAPITAL LETTER E WITH CIRCUMFLEX
        (char) 0x00C1, // LATIN CAPITAL LETTER A WITH ACUTE
        (char) 0x00CB, // LATIN CAPITAL LETTER E WITH DIAERESIS
        (char) 0x00C8, // LATIN CAPITAL LETTER E WITH GRAVE
        (char) 0x00CD, // LATIN CAPITAL LETTER I WITH ACUTE
        (char) 0x00CE, // LATIN CAPITAL LETTER I WITH CIRCUMFLEX
        (char) 0x00CF, // LATIN CAPITAL LETTER I WITH DIAERESIS
        (char) 0x00CC, // LATIN CAPITAL LETTER I WITH GRAVE
        (char) 0x00D3, // LATIN CAPITAL LETTER O WITH ACUTE
        (char) 0x00D4, // LATIN CAPITAL LETTER O WITH CIRCUMFLEX
        (char) 0xF8FF, // Apple logo
        (char) 0x00D2, // LATIN CAPITAL LETTER O WITH GRAVE
        (char) 0x00DA, // LATIN CAPITAL LETTER U WITH ACUTE
        (char) 0x00DB, // LATIN CAPITAL LETTER U WITH CIRCUMFLEX
        (char) 0x00D9, // LATIN CAPITAL LETTER U WITH GRAVE
        (char) 0x0131, // LATIN SMALL LETTER DOTLESS I
        (char) 0x02C6, // MODIFIER LETTER CIRCUMFLEX ACCENT
        (char) 0x02DC, // SMALL TILDE
        (char) 0x00AF, // MACRON
        (char) 0x02D8, // BREVE
        (char) 0x02D9, // DOT ABOVE
        (char) 0x02DA, // RING ABOVE
        (char) 0x00B8, // CEDILLA
        (char) 0x02DD, // DOUBLE ACUTE ACCENT
        (char) 0x02DB, // OGONEK
        (char) 0x02C7, // CARON
    };

    public MacRomanStringCodec() {
        super(mappingTable);
    }

    public String getCharsetName() {
        return "MacRoman";
    }

    private static final MacRomanStringCodec singleton =
            new MacRomanStringCodec();

    public static MacRomanStringCodec getInstance() {
        return singleton;
    }
}
