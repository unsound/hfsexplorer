/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.csjc.structelements;

/**
 *
 * @author erik
 */
public enum IntegerFieldRepresentation {
    DECIMAL("", 10), HEXADECIMAL("0x", 16), OCTAL("0", 8), BINARY("0b", 2);

    private final String prefix;
    private final int radix;

    private IntegerFieldRepresentation(String prefix, int radix) {
        this.prefix = prefix;
        this.radix = radix;
    }

    public String getPrefix() {
        return prefix;
    }

    public int getRadix() {
        return radix;
    }
}
