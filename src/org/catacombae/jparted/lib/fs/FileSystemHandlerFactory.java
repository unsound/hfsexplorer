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

package org.catacombae.jparted.lib.fs;

import java.util.HashMap;
import java.util.Map;
import org.catacombae.jparted.lib.DataLocator;

/**
 * A factory for creating FileSystemHandlers. By setting attributes in the
 * factory, you affect how new FileSystemHandlers are created.
 * 
 * @author Erik Larsson
 */
public abstract class FileSystemHandlerFactory {
    
    /**
     * This instance holds the create attributes for the current factory.
     * Subclasses can access this instance directly to query for relevant
     * attribute definitions and their values.
     */
    protected final Attributes createAttributes;
    
    protected FileSystemHandlerFactory() {
       this.createAttributes = new Attributes(getSupportedStandardAttributes(),
               getSupportedCustomAttributes());
    }

    /**
     * Returns the file system recognizer for the current file system type.
     * 
     * @return the file system recognizer for the current file system type.
     */
    public abstract FileSystemRecognizer getRecognizer();
    
    public abstract FileSystemHandler createHandler(DataLocator data);
    public abstract FileSystemHandlerInfo getHandlerInfo();
    
    /**
     * Creates a new instance of this particular factory subclass.
     * @return a new instance of this particular factory subclass.
     */
    public abstract FileSystemHandlerFactory newInstance();
    
    /**
     * Returns the attributes that are used to create new file system handlers.
     * By changing values in this structure, you affect how new handlers are
     * created.
     * 
     * @return the attributes that are used to create new file system handlers.
     */
    public Attributes getCreateAttributes() {
        return createAttributes;
    }
    
    /**
     * Returns the standard create attributes supported by this implementation.
     * IMPORTANT: Implementors can set the default values for their supported
     * standard attributes <b>only in this method</b>. Setting them somewhere
     * else will be pointless and without effect.
     * 
     * @return the standard create attributes supported by this implementation.
     */
    public abstract StandardAttribute[] getSupportedStandardAttributes();

    /**
     * Returns the custom create attributes supported by this implementation.
     * 
     * @return the custom create attributes supported by this implementation.
     */
    public abstract CustomAttribute[] getSupportedCustomAttributes();
    
    /**
     * Returns whether or not this standard attribute is supported by the
     * implementation.
     * 
     * @param attr the standard attribute to query.
     * @return whether or not <code>attr</code> is supported.
     */
    public boolean isSupported(StandardAttribute attr) {
        for(StandardAttribute sa : getSupportedStandardAttributes()) {
            if(sa == attr)
                return true;
        }
        return false;
    }
    
    /**
     * Sets the default value of this standard attribute. Factory implementors
     * can call this method to override the default value for an attribute, in
     * order to better suit their implementation.<br>
     * <b>This method must only be called from within
     * <code>getSupportedStandardAttributes()</code>!</b>
     * 
     * @param defaultValue the new default value for this standard attribute.
     */
    protected void setStandardAttributeDefaultValue(StandardAttribute attr,
            Object defaultValue) {
        attr.setDefaultValue(defaultValue);
    }
    
    /**
     * Creates a new CustomAttribute, for implementor-defined attributes
     * that are not general, but specific to the current implementation.
     * 
     * @param iType the type of the custom attribute.
     * @param iName the name of the custom attribute. This should typically
     * be written in C constants-style, like
     * <code>A_NEW_CUSTOM_ATTRIBUTE</code>, to fit in with the general
     * aesthetic approach. ;-)
     * @param iDescription a description of the attribute.
     * @param iDefaultValue the default newValue of the attribute.
     * @return a new CustomAttribute object, created from the given parameters.
     */
    protected static CustomAttribute createCustomAttribute(AttributeType iType,
            String iName, String iDescription, Object iDefaultValue) {
        //System.err.println("createCustomAttribute(" + iType + ", " + iName + ", " + iDescription + ", " + iDefaultValue + "); invoked");
        CustomAttribute createdCustomAttribute =
                new CustomAttribute(iType, iName, iDescription, iDefaultValue);
        //System.err.println("Returning a custom attribute: " + createdCustomAttribute);
        return createdCustomAttribute;
    }
    
    public class Attributes {
        private final Map<StandardAttribute, Object> standardCreateAttributeMap =
                new HashMap<StandardAttribute, Object>();
        private final Map<CustomAttribute, Object> customCreateAttributeMap =
                new HashMap<CustomAttribute, Object>();

        private Attributes(StandardAttribute[] iSupportedStandardAttributes,
                CustomAttribute[] iSupportedCustomAttributes) {
            // Add the supported standard attributes to the list
            for(StandardAttribute sa : iSupportedStandardAttributes) {
                standardCreateAttributeMap.put(sa, sa.getDefaultValue());
            }

            // Add the custom attributes to the list
            for(CustomAttribute ca : iSupportedCustomAttributes) {
                customCreateAttributeMap.put(ca, ca.getDefaultValue());
            }
        }
        
        public final Boolean getBooleanAttribute(StandardAttribute attr) {
            return getBooleanAttribute(attr.getType(), standardCreateAttributeMap.get(attr));
        }

        public final Long getIntegerAttribute(StandardAttribute attr) {
            return getIntegerAttribute(attr.getType(), standardCreateAttributeMap.get(attr));
        }

        public final String getStringAttribute(StandardAttribute attr) {
            return getStringAttribute(attr.getType(), standardCreateAttributeMap.get(attr));
        }

        public final Boolean getBooleanAttribute(CustomAttribute attr) {
            return getBooleanAttribute(attr.getType(), customCreateAttributeMap.get(attr));
        }

        public final Long getIntegerAttribute(CustomAttribute attr) {
            return getIntegerAttribute(attr.getType(), customCreateAttributeMap.get(attr));
        }

        public final String getStringAttribute(CustomAttribute attr) {
            return getStringAttribute(attr.getType(), customCreateAttributeMap.get(attr));
        }

        
        public void setBooleanAttribute(StandardAttribute attr, Boolean value) {
            setAttribute(attr, value, standardCreateAttributeMap.get(attr));
        }

        public void setIntegerAttribute(StandardAttribute attr, Integer value) {
            setAttribute(attr, value, standardCreateAttributeMap.get(attr));
        }

        public void setStringAttribute(StandardAttribute attr, String value) {
            setAttribute(attr, value, standardCreateAttributeMap.get(attr));
        }

        public void setBooleanAttribute(CustomAttribute attr, Boolean value) {
            setAttribute(attr, value, customCreateAttributeMap.get(attr));
        }

        public void setIntegerAttribute(CustomAttribute attr, Integer value) {
            setAttribute(attr, value, customCreateAttributeMap.get(attr));
        }

        public void setStringAttribute(CustomAttribute attr, String value) {
            setAttribute(attr, value, customCreateAttributeMap.get(attr));
        }

        private final Boolean getBooleanAttribute(AttributeType type, Object value) {
            if(type != AttributeType.BOOLEAN) {
                throw new IllegalArgumentException("Tried to get BOOLEAN value " +
                        "from " + type + " type!");
            }
            else if(value == null) {
                throw new IllegalArgumentException("Attribute is not supported " +
                        "by this implementation!");
            }
            else {
                if(value instanceof Boolean) {
                    return (Boolean) value;
                }
                else {
                    throw new RuntimeException("INTERNAL ERROR: Kick the coder " +
                            "for inserting " + value.getClass() + " values in a " +
                            "BOOLEAN attribute.");
                }
            }
        }

        private final Long getIntegerAttribute(AttributeType type, Object value) {
            if(type != AttributeType.INTEGER) {
                throw new IllegalArgumentException("Tried to get INTEGER value " +
                        "from " + type + " type!");
            }
            else if(value == null) {
                throw new IllegalArgumentException("Attribute is not supported " +
                        "by this implementation!");
            }
            else {
                if(value instanceof Long) {
                    return (Long) value;
                }
                else if(value instanceof Number) {
                    return ((Number) value).longValue();
                }
                else {
                    throw new RuntimeException("INTERNAL ERROR: Kick the coder " +
                            "for inserting " + value.getClass() + " values in an " +
                            "INTEGER attribute.");
                }
            }
        }

        private final String getStringAttribute(AttributeType type, Object value) {
            if(type != AttributeType.STRING) {
                throw new IllegalArgumentException("Tried to get STRING value " +
                        "from " + type + " type!");
            }
            else if(value == null) {
                throw new IllegalArgumentException("Attribute is not supported " +
                        "by this implementation!");
            }
            else {
                if(value instanceof String) {
                    return (String) value;
                }
                else {
                    throw new RuntimeException("INTERNAL ERROR: Kick the coder " +
                            "for inserting " + value.getClass() + " values in a " +
                            "STRING attribute.");
                }
            }
        }

        private void setAttribute(StandardAttribute attr, Object newValue,
                Object oldValue) {
            if(!attr.getType().isValidValue(newValue)) {
                throw new IllegalArgumentException("Invalid value type (" +
                        newValue.getClass() + ") for attribute (" + attr + ")!");
            }
            else if(oldValue == null) {
                throw new IllegalArgumentException("Attribute " + attr + " is not supported " +
                        "by this implementation!");
            }
            else {
                standardCreateAttributeMap.put(attr, newValue);
            }
        }

        private void setAttribute(CustomAttribute attr, Object newValue,
                Object oldValue) {
            if(!attr.getType().isValidValue(newValue)) {
                throw new IllegalArgumentException("Invalid value type (" +
                        newValue.getClass() + ") for attribute (" + attr + ")!");
            }
            else if(oldValue == null) {
                throw new IllegalArgumentException("Attribute " + attr + " is not supported " +
                        "by this implementation!");
            }
            else {
                customCreateAttributeMap.put(attr, newValue);
            }
        }
    }
    
    /**
     * This enum defines the valid attribute types that can be used to create
     * new custom attributes or change existing standard or custom attributes.
     */
    public static enum AttributeType {
        BOOLEAN(Boolean.class),
        INTEGER(Byte.class, Short.class, Integer.class, Long.class),
        STRING(String.class);
        
        private final Class[] valueSuperClasses;
        
        private AttributeType(Class... iValueSuperClasses) {
            this.valueSuperClasses = iValueSuperClasses;
        }
        
        /**
         * Returns whether or not the supplied newValue is valid for this type.<br>
         * Example: For a BOOLEAN AttributeType only a java.lang.Boolean
         * is a valid newValue type. For an INTEGER AttributeType all the standard
         * integer-values are valid (java.lang.Byte, java.lang.Short,
         * java.lang.Integer, java.lang.Long... BigInteger is not supported).
         * 
         * @param value the value to check for validity.
         * @return whether or not the supplied value is valid for this type.
         */
        public boolean isValidValue(Object value) {
            for(Class c : valueSuperClasses)
                if(c.isInstance(value))
                    return true;
            return false;
        }
    }    

    /**
     * This enum defines the standard attributes that are defined for creating
     * new FileSystemHandlers. They may or may not be supported by the
     * implementation. You can find out which standard attributes are supported
     * by the current factory by calling
     * <code>getSupportedStandardAttributes()</code>.
     */
    public enum StandardAttribute {
        /**
         * Boolean attribute which can be applied to file systems with an
         * internal caching mechanism. Default newValue for this standard attribute
         * is <code>false</code>.
         */
        CACHING_ENABLED(AttributeType.BOOLEAN, new Boolean(false));
        
        private final AttributeType type;
        private Object defaultValue;
        
        private StandardAttribute(AttributeType iType, Object iDefaultValue) {
            this.type = iType;
            
            setDefaultValue(iDefaultValue);
        }
        
        /**
         * Returns the type of this standard attribute.
         * @return the type of this standard attribute.
         */
        public AttributeType getType() {
            return type;
        }
        
        /**
         * Returns the default newValue for this standard attribute.
         * @return the default newValue for this standard attribute.
         */
        public Object getDefaultValue() {
            return defaultValue;
        }
        
        /**
         * Sets the default newValue of this standard attribute. Factory
         * implementors can override this default newValue specific to their
         * implementation as this enum is instance-bound and not static.<br>
         * <b>This method must only be called from within
         * <code>getSupportedStandardAttributes()</code>!</b>
         * 
         * @param iDefaultValue the new default newValue for this standard
         * attribute.
         */
        private void setDefaultValue(Object iDefaultValue) {
            if(!type.isValidValue(iDefaultValue))
                throw new IllegalArgumentException("Illegal default value!");
            else
                this.defaultValue = iDefaultValue;
        }
    }
    
    /**
     * This class represents a custom create attribute that a file system
     * implementor may want to be able to specify when creating a
     * FileSystemHandler. Example:
     * <pre>
     * CustomAttribute debugAttribute = new CustomAttribute(AttributeType.BOOLEAN,
     *      "DEBUG_ENABLED", "Controls if debug mode should be used.", false);
     * CustomAttribute outfileAttribute = new CustomAttribute(AttributeType.STRING,
     *      "DEBUG_OUTPUTFILE", "Controls which outputfile should be used for debug.",
     *      "C:\\Temp\\debug.log");
     * </pre>
     */
    public static final class CustomAttribute {
        private final AttributeType type;
        private final String name;
        private final String description;
        private final Object defaultValue;
        
        /**
         * Creates a new CustomAttribute, for implementor-defined attributes
         * that are not general, but specific to the current implementation.
         * 
         * @param iType the type of the custom attribute.
         * @param iName the name of the custom attribute. This should typically
         * be written in C constants-style, like
         * <code>A_NEW_CUSTOM_ATTRIBUTE</code>, to fit in with the general
         * aesthetic approach. ;-)
         * @param iDescription a description of the attribute.
         * @param iDefaultValue the default newValue of the attribute.
         */
        private CustomAttribute(AttributeType iType, String iName,
                String iDescription, Object iDefaultValue) {
            // Input check...
            if(iType == null)
                throw new IllegalArgumentException("An attribute must have a type.");
            
            if(iName == null)
                throw new IllegalArgumentException("An attribute must have a name.");
            
            if(iDescription == null)
                throw new IllegalArgumentException("An attribute must have a description.");
            
            if(iDefaultValue == null)
                throw new IllegalArgumentException("An attribute must have a default value.");
            else if(!iType.isValidValue(iDefaultValue))
                throw new IllegalArgumentException("Illegal default value!");
            
            this.type = iType;
            this.name = iName;
            this.description = iDescription;
            this.defaultValue = iDefaultValue;
        }
        
        /**
         * Returns the type of this custom attribute.
         * 
         * @return the type of this custom attribute.
         */
        public AttributeType getType() {
            return type;
        }
        
        /**
         * Returns the name of this custom attribute.
         *
         * @return the name of this custom attribute.
         */
        public String getName() {
            return name;
        }
        
        /**
         * Returns a brief description of this custom attribute.
         * 
         * @return a brief description of this custom attribute.
         */

        public String getDescription() {
            return description;
        }
        
        /**
         * Returns the default newValue for this custom attribute.
         * 
         * @return the default newValue for this custom attribute.
         */
        public Object getDefaultValue() {
            return defaultValue;
        }

        /**
         * Sets the default newValue of this custom attribute. Factory
         * implementors can ovveride this default newValue specific to their
         * implementation as this enum is instance-bound and not static.
         * 
         * @param iDefaultValue the new default newValue for this custom
         * attribute.
         */
        /* // Not needed here
        protected void setDefaultValue(Object iDefaultValue) {
            if(!type.isValidValue(iDefaultValue))
                throw new IllegalArgumentException("Illegal default newValue!");
            else
                this.defaultValue = iDefaultValue;
        }
         * */
    }
}
