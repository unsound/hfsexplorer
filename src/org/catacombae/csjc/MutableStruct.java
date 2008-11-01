/*-
 * Copyright (C) 2007-2008 Erik Larsson
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

package org.catacombae.csjc;

import org.catacombae.hfsexplorer.Util;
import java.lang.reflect.Field;

/**
 * 
 * @author <a href="http://hem.bredband.net/catacombae">Erik Larsson</a>
 */
public abstract class MutableStruct {
    private final boolean mutable;
    
    public MutableStruct() {
	this.mutable = false;
    }
    public MutableStruct(boolean mutable) {
	this.mutable = true;
    }
    public void setByteField(String fieldName, byte value) {
	if(mutable) {
	    byte[] fieldData = getFieldData(fieldName);
	    if(fieldData.length != 1)
		throw new IllegalArgumentException("Invalid input! length is not equal to the length of the field data");
	    else
		System.arraycopy(Util.toByteArrayBE(value), 0, fieldData, 0, 1);
	}
	else
	    accessViolation();
    }
    public void setShortField(String fieldName, short value) {
	if(mutable) {
	    byte[] fieldData = getFieldData(fieldName);
	    if(fieldData.length != 2)
		throw new IllegalArgumentException("Invalid input! length is not equal to the length of the field data");
	    else
		System.arraycopy(Util.toByteArrayBE(value), 0, fieldData, 0, fieldData.length);
	}
	else
	    accessViolation();
    }
    public void setCharField(String fieldName, char value) {
	if(mutable) {
	    byte[] fieldData = getFieldData(fieldName);
	    if(fieldData.length != 2)
		throw new IllegalArgumentException("Invalid input! length is not equal to the length of the field data");
	    else
		System.arraycopy(Util.toByteArrayBE(value), 0, fieldData, 0, fieldData.length);
	}
	else
	    accessViolation();
    }
    public void setIntField(String fieldName, int value) {
	if(mutable) {
	    byte[] fieldData = getFieldData(fieldName);
	    if(fieldData.length != 4)
		throw new IllegalArgumentException("Invalid input! length is not equal to the length of the field data");
	    else
		System.arraycopy(Util.toByteArrayBE(value), 0, fieldData, 0, fieldData.length);
	}
	else
	    accessViolation();
    }
    public void setLongField(String fieldName, long value) {
	if(mutable) {
	    byte[] fieldData = getFieldData(fieldName);
	    if(fieldData.length != 8)
		throw new IllegalArgumentException("Invalid input! length is not equal to the length of the field data");
	    else
		System.arraycopy(Util.toByteArrayBE(value), 0, fieldData, 0, fieldData.length);
	}
	else
	    accessViolation();
    }
    public void setByteArrayField(String fieldName, byte[] value) {
	setByteArrayField(fieldName, value, 0, value.length);
    }
    public void setByteArrayField(String fieldName, byte[] value, int offset, int length) {
	if(mutable) {
	    byte[] fieldData = getFieldData(fieldName);
	    if(length != fieldData.length)
		throw new IllegalArgumentException("Invalid input! length is not equal to the length of the field data");
	    else
		System.arraycopy(value, offset, fieldData, 0, length);
	}
	else
	    accessViolation();
    }
    public void setStructField(String fieldName, MutableStruct yada) {
	if(mutable) {
 	    Object fieldObject = getFieldObject(fieldName);
	    if(fieldObject instanceof MutableStruct) {
		// UNFINISHED
	    }
	    else
		throw new IllegalArgumentException("No such Struct field.");
// 	    if(length != fieldData.length)
// 		throw new IllegalArgumentException("Invalid input! length is not equal to the length of the field data");
// 	    else
// 		System.arraycopy(value, offset, fieldData, 0, length);
	}
	else
	    accessViolation();
    }
//     public void setField(String fieldName,  value) {
//     }

    protected void accessViolation() {
	throw new RuntimeException("Access violation: Tried to set fields in an immutable object.");
    }
    
    /** Looks up the byte array with identifer <code>fieldName</code> in
	the current class and returns it (an actual reference, no copy).
	If no identifier can be found, or the identifier is not of type
	<code>byte[]</code>, an IllegalArgumentException is thrown. */
    private byte[] getFieldData(String fieldName) {
	Object o = getFieldObject(fieldName);
	if(o instanceof byte[])
	    return (byte[]) o;
	else
	    throw new IllegalArgumentException("No such byte array field.");
    }
    private Object getFieldObject(String fieldName) {
	try {
	    Class thisClass = this.getClass();
	    Field f = thisClass.getField(fieldName);
	    return f.get(this);
	} catch(NoSuchFieldException nsfe) {
	    throw new IllegalArgumentException(nsfe);
	} catch(IllegalAccessException iae) {
	    throw new IllegalArgumentException(iae);
	}
    }
}