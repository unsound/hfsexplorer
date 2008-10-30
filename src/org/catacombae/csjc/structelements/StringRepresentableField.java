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

package org.catacombae.csjc.structelements;

/**
 * @author <a href="http://hem.bredband.net/catacombae">Erik Larsson</a>
 */
public abstract class StringRepresentableField extends Field {
    private final String unitComponent;

    public StringRepresentableField(String typeName, FieldType type) {
        this(typeName, null, type, null);
    }

    public StringRepresentableField(String typeName, FieldType type, String unitComponent) {
        this(typeName, null, type, unitComponent);
    }

    public StringRepresentableField(String typeName, String typeDescription, FieldType type, String unitComponent) {
        super(typeName, typeDescription, type);
        this.unitComponent = unitComponent;
    }

    public abstract String getValueAsString();

    public abstract void setStringValue(String value) throws IllegalArgumentException;

    public abstract String validateStringValue(String s);

    /**
     * Returns the unit for this field, if applicable. For instance, a field
     * with an integer value might have "bytes" as units, or "inches". The unit
     * property is non-mandatory, so this method may return <code>null</code>.
     *
     * @return the unit for this field, or <code>null</code> if none is defined.
     */
    public String getUnitComponent() {
        return unitComponent;
    }
}
