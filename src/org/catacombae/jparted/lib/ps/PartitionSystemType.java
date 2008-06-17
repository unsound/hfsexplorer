/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.jparted.lib.ps;

import org.catacombae.jparted.lib.ps.mbr.MBRHandlerFactory;
import org.catacombae.jparted.lib.ps.gpt.GPTHandlerFactory;
import org.catacombae.jparted.lib.ps.apm.APMHandlerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

/**
 * Defines the partition system types that the library knows of, and provides a
 * way of retrieving their handlers, if any implementations exist.
 * @author erik
 */
public enum PartitionSystemType {
    MBR(true, MBRHandlerFactory.class),
    GPT(true, GPTHandlerFactory.class),
    APM(true, APMHandlerFactory.class),
    DOS_EXTENDED(false),
    LINUX_LVM(false); // or can it exist in top level?
    
    private final boolean isTopLevelCapable;
    
    private LinkedList<Class<? extends PartitionSystemHandlerFactory>> factoryClasses =
            new LinkedList<Class<? extends PartitionSystemHandlerFactory>>();
    
    private PartitionSystemType(boolean pIsTopLevelCapable) {
        this.isTopLevelCapable = pIsTopLevelCapable;
    }
    
    private PartitionSystemType(boolean pIsTopLevelCapable, 
            Class<? extends PartitionSystemHandlerFactory>... pFactoryClasses) {
        this(pIsTopLevelCapable);
        
        for(Class<? extends PartitionSystemHandlerFactory> c : pFactoryClasses)
            this.factoryClasses.addLast(c);
    }
    
    /**
     * Tells whether this is a partitioning system that can be used as a top
     * level partition system (i.e. can be put directly onto a raw,
     * unformatted disk, unaided by an outer partition system).
     * @return whether or not this partition system is top level capable.
     */
    public boolean isTopLevelCapable() {
        return isTopLevelCapable;
    }
    
    public PartitionSystemHandlerFactory createDefaultHandlerFactory() {
        if(factoryClasses.size() == 0)
            return null;
        else {
            Class<? extends PartitionSystemHandlerFactory> factoryClass =
                    factoryClasses.getFirst();
            return createHandlerFactory(factoryClass);
        }
    }
    
    public PartitionSystemHandlerFactory createHandlerFactory(Class<? extends PartitionSystemHandlerFactory> factoryClass) {
        try {
            Constructor<? extends PartitionSystemHandlerFactory> c =
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
