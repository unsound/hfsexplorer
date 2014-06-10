/*-
 * Copyright (C) 2014 Erik Larsson
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

package org.catacombae.hfsexplorer;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Erik Larsson
 */
public class Java7Util {
    /**
     * Tests whether or not the current VM is a Java 7 or higher VM. This method
     * should always be invoked to check that the version number of the
     * currently running JRE is higher than or equal to 1.7 before invoking any
     * of the methods in Java7Specific.
     *
     * @return whether or not the current VM is a Java 7 or higher VM.
     */
    public static boolean isJava7OrHigher() {
    	return System.getProperty("java.specification.version").
                compareTo("1.7") >= 0;
    }

    public static void setFileTimes(String path, Date creationTime,
            Date lastAccessTime, Date lastModifiedTime)
            throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchFieldException
    {
        Class<?> pathClass = Class.forName("java.nio.file.Path");
        Class<?> fileSystemsClass = Class.forName("java.nio.file.FileSystems");
        Class<?> fileSystemClass = Class.forName("java.nio.file.FileSystem");
        Class<?> basicFileAttributeViewClass =
                Class.forName("java.nio.file.attribute.BasicFileAttributeView");
        Class<?> filesClass = Class.forName("java.nio.file.Files");
        Class<?> linkOptionClass = Class.forName("java.nio.file.LinkOption");
        Class<?> fileTimeClass =
                Class.forName("java.nio.file.attribute.FileTime");

        /* FileSystem defaultFileSystem = FileSystems.getDefault(); */
        Method fileSystemsGetDefaultMethod =
                fileSystemsClass.getMethod("getDefault");
        Object defaultFileSystemObject =
                fileSystemsGetDefaultMethod.invoke(null);

        /* Path p = defaultFileSystem.getPath(path); */
        Method fileSystemGetPathMethod =
                fileSystemClass.getMethod("getPath", String.class,
                String[].class);
        Object pObject =
                fileSystemGetPathMethod.invoke(defaultFileSystemObject, path,
                new String[0]);

        /* BasicFileAttributeView attrView =
         *         Files.getFileAttributeView(p, BasicFileAttributeView.class,
         *         LinkOption.NOFOLLOW_LINKS); */
        Field noFollowLinksField = linkOptionClass.getField("NOFOLLOW_LINKS");
        Object noFollowLinksObject = noFollowLinksField.get(null);

        Object linkOptionsArray = Array.newInstance(linkOptionClass, 1);
        Array.set(linkOptionsArray, 0, noFollowLinksObject);

        Method getFileAttributeViewMethod =
                filesClass.getMethod("getFileAttributeView", pathClass,
                Class.class, linkOptionsArray.getClass());
        Object attrViewObject =
                getFileAttributeViewMethod.invoke(null, pObject,
                basicFileAttributeViewClass, linkOptionsArray);

        /*
         * FileTime creationFileTime;
         * if(creationTime != null) {
         *     creationFileTime = FileTime.fromMillis(creationTime.getTime());
         * }
         * else {
         *     creationFileTime = null;
         * }
         */
        Object creationFileTimeObject;
        if(creationTime != null) {
            Method fileTimefromMillisMethod =
                    fileTimeClass.getMethod("fromMillis", long.class);
            creationFileTimeObject =
                    fileTimefromMillisMethod.invoke(null,
                    Long.valueOf(creationTime.getTime()));
        }
        else {
            creationFileTimeObject = null;
        }

        /*
         * FileTime lastAccessFileTime;
         * if(lastAccessTime != null) {
         *     lastAccessFileTime =
         *         FileTime.fromMillis(lastAccessTime.getTime());
         * }
         * else {
         *     lastAccessFileTime = null;
         * }
         */
        Object lastAccessFileTimeObject;
        if(lastAccessTime != null) {
            Method fileTimefromMillisMethod =
                    fileTimeClass.getMethod("fromMillis", long.class);
            lastAccessFileTimeObject =
                    fileTimefromMillisMethod.invoke(null,
                    Long.valueOf(lastAccessTime.getTime()));
        }
        else {
            lastAccessFileTimeObject = null;
        }

        /*
         * FileTime lastModifiedFileTime;
         * if(lastModifiedTime != null) {
         *     lastModifiedFileTime =
         *         FileTime.fromMillis(lastModifiedTime.getTime());
         * }
         * else {
         *     lastModifiedFileTime = null;
         * }
         */
        Object lastModifiedFileTimeObject;
        if(lastModifiedTime != null) {
            Method fileTimefromMillisMethod =
                    fileTimeClass.getMethod("fromMillis", long.class);
            lastModifiedFileTimeObject =
                    fileTimefromMillisMethod.invoke(null,
                    Long.valueOf(lastModifiedTime.getTime()));
        }
        else {
            lastModifiedFileTimeObject = null;
        }

        /* attrView.setTimes(lastModifiedFileTime, lastAccessFileTime,
         *     creationFileTime); */
        Method basicFileAttributeViewSetTimesMethod =
                basicFileAttributeViewClass.getMethod("setTimes", fileTimeClass,
                fileTimeClass, fileTimeClass);
        basicFileAttributeViewSetTimesMethod.invoke(attrViewObject,
                lastModifiedFileTimeObject, lastAccessFileTimeObject,
                creationFileTimeObject);
    }

    public static void setPosixPermissions(String path,
            int ownerId, int groupId,
            boolean ownerRead, boolean ownerWrite, boolean ownerExecute,
            boolean groupRead, boolean groupWrite, boolean groupExecute,
            boolean othersRead, boolean othersWrite, boolean othersExecute)
            throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchFieldException
    {
        Class<?> fileSystemsClass = Class.forName("java.nio.file.FileSystems");
        Class<?> fileSystemClass = Class.forName("java.nio.file.FileSystem");
        Class<?> pathClass = Class.forName("java.nio.file.Path");
        Class<?> posixFileAttributeViewClass =
                Class.forName("java.nio.file.attribute.PosixFileAttributeView");
        Class<?> filesClass = Class.forName("java.nio.file.Files");
        Class<?> linkOptionClass = Class.forName("java.nio.file.LinkOption");
        Class<?> posixFilePermissionClass =
                Class.forName("java.nio.file.attribute.PosixFilePermission");

        /* java.nio.file.FileSystem defaultFileSystem =
                   java.nio.file.FileSystems.getDefault(); */
        Method fileSystemsGetDefaultMethod =
                fileSystemsClass.getMethod("getDefault");
        Object defaultFileSystemObject =
                fileSystemsGetDefaultMethod.invoke(null);

        /* java.nio.file.Path p = defaultFileSystem.getPath(path); */
        Method fileSystemGetPathMethod =
                fileSystemClass.getMethod("getPath", String.class,
                String[].class);
        Object pObject =
                fileSystemGetPathMethod.invoke(defaultFileSystemObject, path,
                new String[0]);

        /* java.nio.file.attribute.PosixFileAttributeView attrView =
         *         java.nio.file.Files.getFileAttributeView(p,
         *         java.nio.file.attribute.PosixFileAttributeView.class,
         *         java.nio.file.LinkOption.NOFOLLOW_LINKS); */
        Field noFollowLinksField = linkOptionClass.getField("NOFOLLOW_LINKS");
        Object noFollowLinksObject = noFollowLinksField.get(null);

        Object linkOptionsArray = Array.newInstance(linkOptionClass, 1);
        Array.set(linkOptionsArray, 0, noFollowLinksObject);

        Method getFileAttributeViewMethod =
                filesClass.getMethod("getFileAttributeView", pathClass,
                Class.class, linkOptionsArray.getClass());
        Object attrViewObject =
                getFileAttributeViewMethod.invoke(null, pObject,
                posixFileAttributeViewClass, linkOptionsArray);

        if(attrViewObject == null) {
            /* No PosixFileAttributeView available. Platform does not support
             * POSIX attributes. Just return quietly here. */
            return;
        }

        HashSet<Object> perms = new HashSet<Object>();

        if(ownerRead) {
            Field curField = posixFilePermissionClass.getField("OWNER_READ");
            Object curObject = curField.get(null);
            perms.add(curObject);
        }

        if(ownerWrite) {
            Field curField = posixFilePermissionClass.getField("OWNER_WRITE");
            Object curObject = curField.get(null);
            perms.add(curObject);
        }

        if(ownerExecute) {
            Field curField = posixFilePermissionClass.getField("OWNER_EXECUTE");
            Object curObject = curField.get(null);
            perms.add(curObject);
        }

        if(groupRead) {
            Field curField = posixFilePermissionClass.getField("GROUP_READ");
            Object curObject = curField.get(null);
            perms.add(curObject);
        }

        if(groupWrite) {
            Field curField = posixFilePermissionClass.getField("GROUP_WRITE");
            Object curObject = curField.get(null);
            perms.add(curObject);
        }

        if(groupExecute) {
            Field curField = posixFilePermissionClass.getField("GROUP_EXECUTE");
            Object curObject = curField.get(null);
            perms.add(curObject);
        }

        if(othersRead) {
            Field curField = posixFilePermissionClass.getField("OTHERS_READ");
            Object curObject = curField.get(null);
            perms.add(curObject);
        }

        if(othersWrite) {
            Field curField = posixFilePermissionClass.getField("OTHERS_WRITE");
            Object curObject = curField.get(null);
            perms.add(curObject);
        }

        if(othersExecute) {
            Field curField =
                    posixFilePermissionClass.getField("OTHERS_EXECUTE");
            Object curObject = curField.get(null);
            perms.add(curObject);
        }

        /* attrView.setPermissions(perms); */
        Method posixFileAttributeViewSetPermissionMethod =
                posixFileAttributeViewClass.getMethod("setPermissions",
                Set.class);
        posixFileAttributeViewSetPermissionMethod.invoke(attrViewObject,
                perms);

        /* java.nio.file.Files.setAttribute(p, "unix:uid",
                Integer.valueOf(ownerId),
                java.nio.file.LinkOption.NOFOLLOW_LINKS);*/
        Method filesSetAttributeMethod =
                filesClass.getMethod("setAttribute", pathClass,
                String.class, Object.class, linkOptionsArray.getClass());
        filesSetAttributeMethod.invoke(null, pObject, "unix:uid",
                Integer.valueOf(ownerId), linkOptionsArray);

        /* java.nio.file.Files.setAttribute(p, "unix:gid",
                Integer.valueOf(groupId),
                java.nio.file.LinkOption.NOFOLLOW_LINKS); */
        filesSetAttributeMethod.invoke(null, pObject, "unix:gid",
                Integer.valueOf(groupId), linkOptionsArray);
    }

    public static void createSymbolicLink(String linkPathString,
            String targetPathString)
            throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchFieldException
    {
        Class<?> fileSystemsClass = Class.forName("java.nio.file.FileSystems");
        Class<?> fileSystemClass = Class.forName("java.nio.file.FileSystem");
        Class<?> pathClass = Class.forName("java.nio.file.Path");
        Class<?> filesClass = Class.forName("java.nio.file.Files");
        Class<?> fileAttributeClass =
                Class.forName("java.nio.file.attribute.FileAttribute");

        Object emptyFileAttributeArray =
                Array.newInstance(fileAttributeClass, 0);

        Method fileSystemsGetDefaultMethod =
                fileSystemsClass.getMethod("getDefault");
        Method fileSystemGetPathMethod =
                fileSystemClass.getMethod("getPath", String.class,
                String[].class);
        Method fileSystemsCreateSymbolicLinkMethod =
                filesClass.getMethod("createSymbolicLink", pathClass,
                pathClass, emptyFileAttributeArray.getClass());

        /* java.nio.file.FileSystem defaultFileSystem =
                   java.nio.file.FileSystems.getDefault(); */
        Object defaultFileSystemObject =
                fileSystemsGetDefaultMethod.invoke(null);

        /* java.nio.file.Path linkPath =
                   defaultFileSystem.getPath(linkPathString); */
        Object linkPathObject =
                fileSystemGetPathMethod.invoke(defaultFileSystemObject,
                linkPathString, new String[0]);

        /* java.nio.file.Path targetPath =
                   defaultFileSystem.getPath(targetPathString); */
        Object targetPathObject =
                fileSystemGetPathMethod.invoke(defaultFileSystemObject,
                targetPathString, new String[0]);

        /* java.nio.file.Files.createSymbolicLink(linkPath, targetPath); */
        fileSystemsCreateSymbolicLinkMethod.invoke(null, linkPathObject,
                targetPathObject, emptyFileAttributeArray);
    }
}
