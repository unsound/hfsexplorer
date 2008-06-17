/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.catacombae.jparted.lib.fs;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Contains the possible "major file system types" that the library will work
 * with, and defines all file system handler implementations for these file
 * systems that are available in the build.
 * 
 * @author Erik Larsson, erik82@kth.se
 */
public enum FileSystemMajorType {
    APPLE_MFS,
    APPLE_HFS,
    APPLE_HFS_PLUS(getFactories("HFS+")),
    APPLE_HFSX(HFSXFileSystemHandlerFactory.class),
    APPLE_PRODOS, FAT12, FAT16, FAT32, EXFAT, NTFS, HPFS, EXT3, REISERFS,
    REISER4, XFS, JFS, ZFS, UNKNOWN;
    
    private static final Map<String, List<Class<? extends FileSystemHandlerFactory>>> DEFINED_FACTORIES;
    
    static {
        DEFINED_FACTORIES = 
                new HashMap<String, List<Class<? extends FileSystemHandlerFactory>>>();
        
        LinkedList<Class<? extends FileSystemHandlerFactory>> hfsPlusHandlers =
                new LinkedList<Class<? extends FileSystemHandlerFactory>>();
        hfsPlusHandlers.addLast(HFSPlusFileSystemHandlerFactory.class);
        DEFINED_FACTORIES.put("HFS+", hfsPlusHandlers);
    }
    
    public static Class[] getFactories(String fsType) {
        List<Class<? extends FileSystemHandlerFactory>> factoryList =
                DEFINED_FACTORIES.get(fsType);
        return factoryList.toArray(new Class[factoryList.size()]);
    }
    
    private final LinkedList<FileSystemHandlerFactory> factories =
            new LinkedList<FileSystemHandlerFactory>();
    
    /**
     * Creates a FileSystemMajorType with no file system handler implementations
     * specified.
     */
    private FileSystemMajorType() {
    }
    
    /**
     * Creates a FileSystemMajorType specifing the factory classes of the available
     * file system handler implementations for this file system. The first argument
     * is assumed to be the default implementation.
     * 
     * @param pFactoryClasses
     */
    /*
    private FileSystemMajorType(Class<? extends FileSystemHandlerFactory> factoryClass) {
        this.factoryClasses.add(factoryClass);
        //Class<FileSystemHandlerFactory> cls = FileSystemHandlerFactory.class;
        Class<? extends FileSystemHandlerFactory> cls2 = factoryClass.asSubclass(FileSystemHandlerFactory.class);
    }
     */
    
    private FileSystemMajorType(Class... pFactoryClasses) {
        for(Class factoryClass : pFactoryClasses) {
            try {
                Constructor c =
                    factoryClass.getConstructor();
                Object o = c.newInstance();
                if(o instanceof FileSystemHandlerFactory)
                    factories.add((FileSystemHandlerFactory)o);
                else
                    throw new IllegalArgumentException("Factory class " +
                            factoryClass.getName() + " is not a superclass of " +
                            FileSystemHandlerFactory.class.getName());
            } catch(NoSuchMethodException e) {
                throw new IllegalArgumentException("No zero-argument constructor found!", e);
            } catch(InstantiationException e) {
                throw new IllegalArgumentException("Could not instantiate " +
                        factoryClass.getName() + "!", e);
            } catch(IllegalAccessException e) {
                throw new IllegalArgumentException("Inaccessible constructor for " +
                        factoryClass.getName() + "!", e);
            } catch(IllegalArgumentException e) {
                throw new IllegalArgumentException("IllegalArgumentException while " +
                        "invoking constructor for " + factoryClass.getName() + "!", e);
            } catch(InvocationTargetException e) {
                throw new RuntimeException("Constructor for " +
                        factoryClass.getName() + "threw an Exception!", e);
            }            
        }
    }
    
    public List<FileSystemHandlerFactory> getAllHandlers() {
        return new ArrayList<FileSystemHandlerFactory>(factories);
    }
    
    /**
     * If this file system has an associated file system handler, its
     * FileSystemHandlerFactory is returned. If there is no file system handler
     * implementation null is returned.
     * @return
     */
    public FileSystemHandlerFactory getDefaultHandlerFactory() {
        if(factories.size() < 1)
            return null;
        else {
            return factories.getFirst();
        }
    }
    
    public static FileSystemHandlerFactory createHandlerFactory(Class <? extends FileSystemHandlerFactory> factoryClass) {
        try {
            Constructor<? extends FileSystemHandlerFactory> c =
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
