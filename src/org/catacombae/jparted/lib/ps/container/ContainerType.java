/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.jparted.lib.ps.container;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
    
    private ContainerType(Class<? extends ContainerHandlerFactory>... factoryClasses) {
        for(Class<? extends ContainerHandlerFactory> cls : factoryClasses)
            this.factoryClasses.addLast(cls);
    }
    
    public List<Class<? extends ContainerHandlerFactory>> getHandlerFactoryImplementations() {
        return new LinkedList<Class<? extends ContainerHandlerFactory>>(factoryClasses);
    }
    
    public ContainerHandlerFactory createDefaultHandlerFactory() {
        if(factoryClasses.size() == 0)
            return null;
        else {
            Class<? extends ContainerHandlerFactory> factoryClass =
                    factoryClasses.getFirst();
            return createHandlerFactory(factoryClass);
        }
    }
    
    public ContainerHandlerFactory createHandlerFactory(Class<? extends ContainerHandlerFactory> factoryClass) {
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
