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

package org.catacombae.jparted.lib.ps.container;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.catacombae.jparted.lib.ps.container.hfs.HFSContainerHandlerFactory;

/**
 *
 * @author erik
 */
public enum ContainerType {
    NT_OS2_IFS,
    APPLE_HFS(HFSContainerHandlerFactory.class),
    APPLE_UNIX_SVR2,
    LINUX_NATIVE;
    
    private LinkedList<Class<? extends ContainerHandlerFactory>> factoryClasses =
            new LinkedList<Class<? extends ContainerHandlerFactory>>();

    private ContainerType() {}
    
    private ContainerType(Class<? extends ContainerHandlerFactory> defaultFactoryClass) {
        this.factoryClasses.addLast(defaultFactoryClass);
    }

    /**
     * If an external implementor wants to register a factory class for a type,
     * it calls this method. If there are no current factory classes tied to
     * this type, the added class will become its default factory.
     *
     * @param factoryClass the factory class to register with this type.
     */
    public void addFactoryClass(Class<? extends ContainerHandlerFactory> factoryClass) {
        this.factoryClasses.addLast(factoryClass);
    }
    
    /**
     * Returns all registered factory classes for this type. The first entry in
     * the list will be the default factory class.
     * @return all registered factory classes for this type.
     */
    public List<Class<? extends ContainerHandlerFactory>> getFactoryClasses() {
        return new ArrayList<Class<? extends ContainerHandlerFactory>>(factoryClasses);
    }
    
    /**
     * Returns a newly created factory from the type's default factory class.
     * If there is no factory classes defined for the type, <code>null</code> is
     * returned.
     *
     * @return a newly created factory from the type's default factory class.
     */
    public ContainerHandlerFactory createDefaultHandlerFactory() {
        if(factoryClasses.size() == 0)
            return null;
        else {
            Class<? extends ContainerHandlerFactory> factoryClass =
                    factoryClasses.getFirst();
            return createHandlerFactory(factoryClass);
        }
    }
    
    /**
     * Returns a newly created factory from a specified factory class.
     *
     * @param factoryClass the factory class of the new object.
     * @return a newly created factory from a specified factory class.
     */
    public static ContainerHandlerFactory createHandlerFactory(Class<? extends ContainerHandlerFactory> factoryClass) {
        try {
            Constructor<? extends ContainerHandlerFactory> c =
                    factoryClass.getConstructor();
            return c.newInstance();
        } catch(NoSuchMethodException e) {
            e.printStackTrace();
        } catch(InstantiationException e) {
            e.printStackTrace();
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
        } catch(InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
