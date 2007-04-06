/*-
 * Copyright (C) 2006-2007 Erik Larsson
 * 
 * All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA
 */

/*
 * This file represents a Win32 launcher for HFSExplorer, making it
 * easier to associate files with the program and to create
 * shortcuts to it. In most cases, it also creates one unique
 * process for HFSExplorer, by the name of the executable file that
 * results from compiling this. However, this is only true when a
 * suitable "jvm.dll" can be found. Otherwise a new process is
 * created from running "javaw.exe/java.exe".
 *
 * The majority of the work here goes into locating a suitable JVM
 * and initializing it with the data defined in START_CLASS,
 * START_CLASS_PKGSYNTAX and USER_CLASSPATH. The argument vector
 * is passed on to the main method of START_CLASS, with the
 * exception of argv[0], which is the command line used to start
 * the launcher.
 *
 * The scheme for finding a way to load the Java application is as
 * follows:
 * 1. Search for the path to jvm.dll in the registry, at
 *    "HKLM\SOFTWARE\JavaSoft\Java Runtime Environment\<Version>
 *    \RuntimeLib" in the registry. (This only works for Sun JVMs)
 * 2. If nothing is found in the registry, look for the environment
 *    variable JAVA_HOME, and if found, look for the dll under
 *    "<JAVA_HOME>\jre\bin\client\jvm.dll". (JAVA_HOME is supposed
 *    to point to a JDK, if I understand it correctly, and those
 *    "ususally" (as in the Sun JDKs) have a jre directory and a
 *    client VM)
 * 3. If all else fails, try to execute javaw.exe or java.exe with
 *    the correct arguments. This will create a new process called
 *    java.exe/javaw.exe, so this is the least pretty way to do it.
 *
 * If all of the above fails, the user is informed about it through
 * a MessageBox message, and encouraged to go to
 * http://java.sun.com and download a JRE.
 */

/*
 * TODO!
 * - Make the launcher Unicode compatible, and build it for
 *   Unicode. Otherwise it will not be able to handle argments
 *   which specify paths with unicode characters in them.
 * - Try to find a way to execute java(w).exe in the context of the
 *   current process (i.e. same process id and name). I want it to
 *   look nice.
 * - How do I specify extended properties for an executable, like
 *   its name and version etc...? The emacs executable has it...
 * - It would probably be nice to get MessageBox error messages in
 *   case stuff goes wrong when locating START_CLASS, invoking main
 *   method etc...
 */
#include <stdio.h>
#include <jni.h>
#include <windows.h>
#include <tchar.h>

#define START_CLASS_PKGSYNTAX "org.catacombae.hfsexplorer.FileSystemBrowserWindow"
#define START_CLASS           "org/catacombae/hfsexplorer/FileSystemBrowserWindow"
#define USER_CLASSPATH        "lib\\hfsx.jar;lib\\swing-layout-1.0.1.jar;lib\\hfsx_dmglib.jar"

#define FALSE 0
#define TRUE 1
#define DEBUGMODE TRUE
#if DEBUGMODE
#define DEBUG(...) fprintf(stderr, __VA_ARGS__);
#else
#define DEBUG(...) ;
#endif

/* The following variables can be used to disable certain invocation methods, for testing. */
#define DISABLE_REGISTRY_SEARCH        FALSE
#define DISABLE_JAVA_HOME_SEARCH       FALSE
#define DISABLE_JAVA_PROCESS_CREATION  FALSE

static JavaVM *staticJvm = NULL;
static HINSTANCE jvmLib = NULL;

/* Begin: Code from Sun's forums. http://forum.java.sun.com/thread.jspa?threadID=5124559&messageID=9441051 */
typedef jint (JNICALL JNI_CreateJavaVM_t)(JavaVM ** pJvm, JNIEnv ** pEnv, void * vmArgs);
 
#define JRE_KEY         "SOFTWARE\\JavaSoft\\Java Runtime Environment"
#define CURVER_STR      "CurrentVersion"
#define RUNLIB_STR      "RuntimeLib"
 
static int readStringFromRegistry(HKEY key, const char * name, char * buf, DWORD bufsize) {
    DWORD type, size;
    
    if ((RegQueryValueEx(key, name, NULL, &type, NULL, &size) == ERROR_SUCCESS)
	&& (type == REG_SZ)
	&& (size < bufsize)) {
      if (RegQueryValueEx(key, name, NULL, NULL, buf, &size) == ERROR_SUCCESS)
            return 0;
    }
    return -1;
}
 
static int readJvmPathFromRegistry(char * buf, int bufsize) {
    HKEY key, subkey;
    char version[MAX_PATH];
 
    if (RegOpenKeyEx(HKEY_LOCAL_MACHINE, JRE_KEY, 0, KEY_READ, &key) != ERROR_SUCCESS)
        return -1;
    if (readStringFromRegistry(key, CURVER_STR, version, sizeof(version)) != 0) {
        RegCloseKey(key);
        return -2;
    }
    if (RegOpenKeyEx(key, version, 0, KEY_READ, &subkey) != ERROR_SUCCESS) {
        RegCloseKey(key);
        return -3;
    }
    if (readStringFromRegistry(subkey, RUNLIB_STR, buf, bufsize) != 0) {
        RegCloseKey(subkey);
        RegCloseKey(key);
        return -4;
    }
    RegCloseKey(subkey);
    RegCloseKey(key);
    return 0;
}
/* End: Code from Sun's forums. */

static int locateSunJVMInRegistry(JNI_CreateJavaVM_t** JNI_CreateJavaVM_f) {
  char jvmPath[MAX_PATH];
  //JNI_CreateJavaVM_t * JNI_CreateJavaVM_f;
  DEBUG("locateSunJVMInRegistry(jvmPath, sizeof(jvmPath)\n");
  if (readJvmPathFromRegistry(jvmPath, MAX_PATH) != 0)
    return FALSE;
  
  DEBUG("LoadLibrary(%s);\n", jvmPath);
  jvmLib = LoadLibrary(jvmPath);
  
  if (jvmLib == NULL)
    return FALSE;
  
  DEBUG("GetProcAddress(..)\n");
  *JNI_CreateJavaVM_f = (JNI_CreateJavaVM_t *)GetProcAddress(jvmLib, "JNI_CreateJavaVM");
  DEBUG("JNI_CreateJavaVM_f set to %X\n", (int)(*JNI_CreateJavaVM_f));
  
  if (*JNI_CreateJavaVM_f == NULL) {
    DEBUG("FreeLibrary(jvmLib);\n");
    FreeLibrary(jvmLib);
    return FALSE;
  }
  DEBUG("Returning from locateSunJVMInRegistry...\n");
  return TRUE;
}

static int locateJVMThroughJavaHome(JNI_CreateJavaVM_t** JNI_CreateJavaVM_f) {
  DEBUG("locateJVMThroughJavaHome(...)\n");
  char envString[32767];
  if(GetEnvironmentVariable("JAVA_HOME", envString, 32767) > 0) {
    // The JAVA_HOME variable should (?) contain a single string pointing to the JDK(JRE?) home dir
    char *trailing = "\\jre\\bin\\client\\jvm.dll";
    DEBUG("sizeof(trailing)=%d\n", strlen(trailing));
    char destination[32767+strlen(trailing)];
    destination[0] = '\0';
    strcat(destination, envString);
    strcat(destination, trailing);
    DEBUG("LoadLibrary(%s);\n", destination);
    jvmLib = LoadLibrary(destination);
    if(jvmLib == NULL)
      return FALSE;
    
    DEBUG("GetProcAddress(..)\n");
    *JNI_CreateJavaVM_f = (JNI_CreateJavaVM_t *)GetProcAddress(jvmLib, "JNI_CreateJavaVM");
    DEBUG("JNI_CreateJavaVM_f set to %X\n", (int)(*JNI_CreateJavaVM_f));
    
    if (*JNI_CreateJavaVM_f == NULL) {
      DEBUG("FreeLibrary(jvmLib);\n");
      FreeLibrary(jvmLib);
      return FALSE;
    }
    DEBUG("Returning from locateJVMThroughJavaHome...\n");
    return TRUE;
  }
  else return FALSE;
}

static int locateJavaVM(JNI_CreateJavaVM_t** JNI_CreateJavaVM_f) {
  DEBUG("locateJavaVM(..)\n");
  
  if(DISABLE_REGISTRY_SEARCH || locateSunJVMInRegistry(JNI_CreateJavaVM_f) == FALSE) {
    if(DISABLE_JAVA_HOME_SEARCH || locateJVMThroughJavaHome(JNI_CreateJavaVM_f) == FALSE)
      return FALSE;
  }
  return TRUE;
}

static int createJavaVM() {
  JNI_CreateJavaVM_t * JNI_CreateJavaVM_f = NULL;
  int createVMRes = locateJavaVM(&JNI_CreateJavaVM_f);
  DEBUG("JNI_CreateJavaVM_f=%X\n", (int)(JNI_CreateJavaVM_f));
  if(createVMRes == FALSE)
    return FALSE;
  
  JNIEnv *env;
  JavaVM *jvm;
  jint res;
  
#ifdef JNI_VERSION_1_2
  JavaVMInitArgs vm_args;
  JavaVMOption options[1];
  options[0].optionString =
    "-Djava.class.path=" USER_CLASSPATH;
  vm_args.version = 0x00010002;
  vm_args.options = options;
  vm_args.nOptions = 1;
  vm_args.ignoreUnrecognized = JNI_TRUE;
  /* Create the Java VM */
  res = JNI_CreateJavaVM_f(&jvm, &env, &vm_args);
  //res = JNI_CreateJavaVM(&jvm, (void**)&env, &vm_args); // We now use the dynamically loaded address
#else
  JDK1_1InitArgs vm_args;
  char classpath[1024];
  vm_args.version = 0x00010001;
  JNI_GetDefaultJavaVMInitArgs(&vm_args);
  /* Append USER_CLASSPATH to the default system class path */
  sprintf(classpath, "%s%c%s",
	  vm_args.classpath, PATH_SEPARATOR, USER_CLASSPATH);
  vm_args.classpath = classpath;
  /* Create the Java VM */
  res = JNI_CreateJavaVM(&jvm, &env, &vm_args);
#endif /* JNI_VERSION_1_2 */
  
  if(res == 0) {
    staticJvm = jvm;
    return TRUE;
  }
  else {
    FreeLibrary(jvmLib);
    return FALSE;
  }
}

static int createExternalJavaProcess(int argc, char** argv) {
  DEBUG("createExternalJavaProcess()\n");
  STARTUPINFO si;
  PROCESS_INFORMATION pi;
  ZeroMemory( &si, sizeof(si) );
  si.cb = sizeof(si);
  ZeroMemory( &pi, sizeof(pi) );

  TCHAR *szCmdline1 = _tcsdup(TEXT("javaw.exe -classpath " USER_CLASSPATH " " START_CLASS_PKGSYNTAX));
  TCHAR *szCmdline2 = _tcsdup(TEXT("java.exe -classpath " USER_CLASSPATH " " START_CLASS_PKGSYNTAX));
  int lenSzCmdline1 = strlen(szCmdline1);
  int lenSzCmdline2 = strlen(szCmdline2);

  /* copy the contents of argv into new strings */
  int i;
  int newArgStringSize = 0;
  for(i = 1; i < argc; ++i)
    newArgStringSize += strlen(argv[i]) + 3; // +3 for two "-characters and one whitespace
  char *newArgString = malloc(newArgStringSize);
  int ptr = 0;
  for(i = 1; i < argc; ++i) {
    char* cur = argv[i];
    int curlen = strlen(cur);
    strcpy(newArgString+ptr, " \"");
    ptr += 2;
    strcpy(newArgString+ptr, cur);
    ptr += curlen;
    strcpy(newArgString+ptr, "\"");
    ptr += 1;
  }
  
  // screw unicode for now..
  char *szTrueCmdline1 = malloc(lenSzCmdline1 + newArgStringSize);
  strcpy(szTrueCmdline1, szCmdline1);
  strcpy(szTrueCmdline1+lenSzCmdline1, newArgString);
  char *szTrueCmdline2 = malloc(lenSzCmdline2 + newArgStringSize);
  strcpy(szTrueCmdline2, szCmdline2);
  strcpy(szTrueCmdline2+lenSzCmdline2, newArgString);
  
  free(newArgString);
  
  DEBUG("szTrueCmdline1=%s\n", szTrueCmdline1);
  DEBUG("szTrueCmdline2=%s\n", szTrueCmdline2);

  DEBUG("trying with first process (javaw.exe)\n");
  // This is PATHETIC. A simpler API please, MS.. null null false null 0 null bla bla
  if(!CreateProcess(NULL, szTrueCmdline1, 
		    NULL, NULL, FALSE, 0x0, NULL, NULL, &si, &pi)) {
    // Reset si and pi in case CreateProcess did something with them
    ZeroMemory( &si, sizeof(si) );
    si.cb = sizeof(si);
    ZeroMemory( &pi, sizeof(pi) );
    DEBUG("trying with second process (java.exe)\n");
    if(!CreateProcess(NULL, szTrueCmdline2, 
		      NULL, NULL, FALSE, 0x0, NULL, NULL, &si, &pi))
      return FALSE;
  }
  // Wait until child process exits.
  WaitForSingleObject( pi.hProcess, INFINITE );
  
  free(szTrueCmdline1);
  free(szTrueCmdline2);
  
  // Close process and thread handles. 
  CloseHandle( pi.hProcess );
  CloseHandle( pi.hThread );

  return TRUE;
}

int main(int argc, char** argv) {
  if(createJavaVM() == TRUE) {
    /* Proceed... */
    JNIEnv *env;
    (*staticJvm)->AttachCurrentThread(staticJvm, (void **)&env, NULL);
    DEBUG("    - Initializing FileSystemBrowserWindow\n");
    jclass cls = (*env)->FindClass(env, START_CLASS);
    if ((*env)->ExceptionOccurred(env)) {
      (*env)->ExceptionDescribe(env);
      DEBUG("    - Exception occurred!\n");
    }
    
    if(cls != NULL) {
      DEBUG("    - Class found\n");
      jmethodID mid = (*env)->GetStaticMethodID(env, cls, "main", "([Ljava/lang/String;)V");
      if(mid != NULL) {
	DEBUG("    - main method found\n");
	jclass stringClass = (*env)->FindClass(env, "java/lang/String");
	DEBUG("    - got String class\n");
	
	// Create array to hold args
	jarray argsArray = (*env)->NewObjectArray(env, argc-1, stringClass, NULL);
	DEBUG("    - created args array\n");
	if(argsArray != NULL) {
	  int i;
	  for(i = 1; i < argc; ++i) {
	    jstring cur = (*env)->NewStringUTF(env, argv[i]);
	    (*env)->SetObjectArrayElement(env, argsArray, i-1, cur);
	  }
	  
	  DEBUG("    - args array built\n");
	  (*env)->CallStaticVoidMethod(env, cls, mid, argsArray);
	  if ((*env)->ExceptionOccurred(env)) {
	    (*env)->ExceptionDescribe(env);
	    DEBUG("    - Exception occurred!\n");
	  }
	  (*staticJvm)->DestroyJavaVM(staticJvm);
	}
      }
    }
    else {
      DEBUG("    - ERROR. Could not find class FuseModule\n");
    }
    FreeLibrary(jvmLib);
    return 0;
  }
  else if(!DISABLE_JAVA_PROCESS_CREATION && createExternalJavaProcess(argc, argv) == TRUE) {
    // Nothing needs to be done. everything is done in createExternalJavaProcess
    return 0;
  }
  else {
    MessageBox(NULL, "No Java Virtual Machine found! Please get one from http://java.sun.com ...", "Error", MB_OK);
    return -1;
  }  
}
