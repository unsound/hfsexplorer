/*-
 * Copyright (C) 2006-2007 Erik Larsson
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

/*
 * This file constitutes a Win32 launcher for HFSExplorer, making
 * it easier to associate files with the program and to create
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
 * - Try to find a way to execute java(w).exe in the context of the
 *   current process (i.e. same process id and name). I want it to
 *   look nice.
 * - It would probably be nice to get MessageBox error messages in
 *   case stuff goes wrong when locating START_CLASS, invoking main
 *   method etc...
 */

#define UNICODE
#define _UNICODE
#define _WIN32_WINNT 0x0400 // Minimum Windows NT 4.0 required for build because of some constants.

#include <malloc.h>
#include <stdio.h>
#include <jni.h>
#include <windows.h>
#include <tchar.h>

#include "WOW64Operations.hh"
#include "StringBuilder.hh"
#include "logging.hh"

/* The following variables can be used to disable certain invocation methods, for testing. */
#define DISABLE_REGISTRY_SEARCH        FALSE
#define DISABLE_JAVA_HOME_SEARCH       FALSE
#define DISABLE_JAVA_PROCESS_CREATION  FALSE

/* The following defines specify the valid return values from the launcher executable. */
/** One of the possible return values from this executable. */
#define RETVAL_OK                              0
/** One of the possible return values from this executable. */
#define RETVAL_UNKNOWN_ERROR                  -1
/** One of the possible return values from this executable. */
#define RETVAL_JVM_NOT_FOUND                  -2
/** One of the possible return values from this executable. */
#define RETVAL_COULD_NOT_GET_CWD              -3
/** One of the possible return values from this executable. */
#define RETVAL_COMMANDLINETOARGVW_FAILED      -4
/** One of the possible return values from this executable. */
#define RETVAL_COULD_NOT_GET_EXE_PATH         -5
/** One of the possible return values from this executable. */
#define RETVAL_INVOKEUAC_FAILED               -6
/** One of the possible return values from this executable. */
#define RETVAL_ERROR_STARTING_JVM             -7


/* <Some string #defines that alter the program's behavior.> */

/**
 * Relative path to the place where JNI .dll files used by the Java application are located.<br>
 * The path is resolved against the parent directory of the executable.
 */
#define DLL_HOME        "lib"

/** Path separator (':' on unix, ';' on Windows... and this is a Windows application...). */
#define PATH_SEPARATOR  ";"

/** The Windows registry path to the base key for locating a Java Runtime Environment. */
#define JRE_KEY         _T("SOFTWARE\\JavaSoft\\Java Runtime Environment")

/** The Windows registry key for "CurrentVersion". */
#define CURVER_STR      _T("CurrentVersion")

/** The Windows registry key for "RuntimeLib". */
#define RUNLIB_STR      _T("RuntimeLib")

/* </Some string #defines that alter the program's behavior.> */


/* Debug macros. */
#define FALSE     0
#define TRUE      1

/* Constants for the lookup of Java registry keys. */

/* Other */
#define ASCII_CODEPAGE 20127
 
/* Function prototype for entry to dynamically loaded JVM library. */
typedef jint (JNICALL JNI_CreateJavaVM_t)(JavaVM ** pJvm, JNIEnv ** pEnv, void * vmArgs);

/*
 * CUSTOMIZE:
 *   Here you can set the hard coded package path to the start class.
 */
static const int startClassComponentsLength = 4;
static const _TCHAR *startClassComponents[startClassComponentsLength] =
  {
    _T("org"),
    _T("catacombae"),
    _T("hfsexplorer"),
    _T("FileSystemBrowserWindow")
  };

/*
 * CUSTOMIZE:
 *   Here you can set the hard coded class path that the JVM will use.
 */
static const int classpathComponentsLength = 5;
static const _TCHAR *classpathComponents[classpathComponentsLength] =
  {
    _T("lib\\hfsx.jar"),
    _T("lib\\swing-layout-1.0.1.jar"),
    _T("lib\\hfsx_dmglib.jar"),
    _T("lib\\apache-ant-1.7.0-bzip2.jar"),
    _T("lib\\iharder-base64.jar")
  };

/*
 * CUSTOMIZE:
 *   Here you can set any hard coded arguments that will be sent to the start
 *   class before the argv supplied as input.
 */
static const int prefixArgsLength = 1;
static const _TCHAR *prefixArgs[prefixArgsLength] =
  {
    _T("-dbgconsole")
  };



/* <Global variables> */
static _TCHAR *classpathString = NULL; // Set by calling resolveClasspath()
/* </Global variables> */


/* <Utility functions and macros> */

/*
 * The macros T2A (_TCHAR to ASCII) and A2T (ASCII to _TCHAR) are defined below.
 */
#if defined _UNICODE

static inline int ___T2A_countChars(const _TCHAR *source) {
  int len;
  for(len = 0; source[len++] != _T('\0'); );
  return len;
}
static inline char* ___T2A_copyChars(const _TCHAR *source, char* dest) {
  int i;
  for(i = 0; source[i] != _T('\0'); ++i)
    dest[i] = ((char)source[i]) & 0x7F;
  dest[i] = '\0';
  return dest;
}
#define T2A(str) ___T2A_copyChars(str, (char*)_alloca((___T2A_countChars(str)+1)*sizeof(char)))

static inline int ___A2T_countChars(const char *source) {
  int len;
  for(len = 0; source[len++] != '\0'; );
  return len;
}
static inline _TCHAR* ___A2T_copyChars(const char *source, _TCHAR* dest) {
  int i;
  for(i = 0; source[i] != '\0'; ++i)
    dest[i] = (_TCHAR)(source[i] & 0x7F);
  dest[i] = _T('\0');
  return dest;
}
#define A2T(str) ___A2T_copyChars(str, (_TCHAR*)_alloca((___A2T_countChars(str)+1)*sizeof(_TCHAR)))


#else /* defined _UNICODE */

#define T2A(str) str
#define A2T(str) str

#endif /* defined _UNICODE */

/**
 * Prints a Windows error value, obtained via GetLastError, in a human-readable way with an associated
 * message obtained from the windows API (FormatMessage).
 * This function also takes a prefix string, printed before the error LOG-message.
 */
static inline void printError(const _TCHAR *const prefix, const DWORD errorVal) {
  LOG(trace, "void printError((_TCHAR*) 0x%X, %d)", prefix, errorVal);
  
  /* Get human-readable message from Win32 API.  */
  _TCHAR *fmtMsgMessage;
  _TCHAR *message;
  DWORD messageLength =
    FormatMessage(FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_SYSTEM, NULL, errorVal,
		  LANG_USER_DEFAULT, (LPTSTR)(&fmtMsgMessage), 0, NULL);
  
  //LOG(debug, "&message=0x%X messageLength=%d", &fmtMsgMessage, messageLength);
  
  if(messageLength == 0) {
    //printError(_T("printError: Error after FormatMessage: "), GetLastError());
    _TCHAR *disasterMessage = _T("[NO ERROR MESSAGE FOUND]");
    size_t disasterMessageLength = _tcslen(disasterMessage);
    message = new _TCHAR[disasterMessageLength+1]; // Add one for '\0'
    _tcsncpy(message, disasterMessage, disasterMessageLength);
    message[disasterMessageLength] = _T('\0');
  }
  else {
    /* Cut message at first linebreak character to make logs look clean. We only want one line in the log. */
    DWORD cutPosition = 0;

    while(cutPosition < messageLength) {
      _TCHAR curChar = fmtMsgMessage[cutPosition++];
      /* Linebreak characters are '\n' and '\r'... and of course we break at '\0' too. */
      if(curChar == _T('\n') || curChar == _T('\r') || curChar == _T('\0'))
	break; // cut here
    }
    
    message = new _TCHAR[cutPosition]; // Add one for '\0'
    _tcsncpy(message, fmtMsgMessage, cutPosition-1);
    message[cutPosition-1] = _T('\0');
  }
  
  LOG(error, "%s%i: \"%s\"", prefix, errorVal, message);
  
  delete[] message;
  if(fmtMsgMessage != NULL)
    LocalFree(fmtMsgMessage);
  
  LOG(trace, "returning from void printError((_TCHAR*) 0x%X, %d)", prefix, errorVal);
}

/**
 * Prints a Windows error value, obtained via GetLastError, in a human-readable way with an associated
 * message obtained from the windows API (FormatMessage).
 */
static inline void printError(const DWORD errorVal) {
  LOG(trace, "void printError(%d)", errorVal);
  printError(_T(""), errorVal);
  LOG(trace, "returning from void printError(%d)", errorVal);
}

/**
 * Convenience method for intializing the COM subsystem. Doesn't depend on static linking
 * with OLE/COM libraries.
 */
static bool initializeCOM() {
  LOG(trace, "bool initializeCOM()");
  typedef HRESULT (WINAPI *FN_COINITIALIZEEX) (void*, DWORD);
  bool retval = false;
  
  HMODULE libHandle = LoadLibrary(_T("ole32.dll"));
  //LOG(debug, "libHandle=0x%X", libHandle);
  if(libHandle != NULL) {
    FN_COINITIALIZEEX fnCoInitializeEx =
      (FN_COINITIALIZEEX) GetProcAddress(libHandle, "CoInitializeEx");
    if(fnCoInitializeEx != NULL) {
      HRESULT res =
	fnCoInitializeEx(NULL, COINIT_APARTMENTTHREADED | COINIT_DISABLE_OLE1DDE);
      if(res == S_OK || res == S_FALSE || res == RPC_E_CHANGED_MODE)
	retval = true;
      else
	LOG(error, "fnCoInitializeEx did not execute successfully. Return value: %d", res);
    }
    else
      LOG(error, "Could not locate CoInitializeEx in ole32.dll!");
    
    FreeLibrary(libHandle);
  }
  else
    printError(_T("LoadLibrary returned error "), GetLastError());
  
  LOG(trace, "returning from bool initializeCOM() with retval %d (true=%d, false=%d)", retval, true, false);
  return retval;
}

/**
 * Convenience method for un-intializing the COM subsystem. Doesn't depend on static linking
 * with OLE/COM libraries.
 */
static void uninitializeCOM() {
  LOG(trace, "void uninitializeCOM()");
  typedef void (WINAPI *FN_COUNINITIALIZE) (void);
  
  HMODULE libHandle = LoadLibrary(_T("ole32.dll"));
  //LOG(debug, "libHandle=0x%X", libHandle);
  if(libHandle != NULL) {
    FN_COUNINITIALIZE fnCoUninitialize =
      (FN_COUNINITIALIZE) GetProcAddress(libHandle, "CoUninitialize");
    if(fnCoUninitialize != NULL)
      fnCoUninitialize();
    else
      LOG(error, "Could not locate CoUninitializeEx in ole32.dll!");
    
    FreeLibrary(libHandle);
  }
  else
    printError(_T("LoadLibrary returned error "), GetLastError());
  
  LOG(trace, "returning from void uninitializeCOM()");
}

/**
 * Inline utility function for getting the correct string for a bool value
 * (i.e. not 1 or 0, but "true" or "false").
 */
static inline const _TCHAR* bool2str(bool b) {
  return b?_T("true"):_T("false");
}

/* </Utility functions and macros> */

/* Begin: Code from Sun's forums. http://forum.java.sun.com/thread.jspa?threadID=5124559&messageID=9441051 */
 
static int readStringFromRegistry(HKEY key, const _TCHAR *name, LPBYTE buf, DWORD bufsize) {
  LOG(trace, "int readStringFromRegistry(%d, \"%s\", (_TCHAR*) 0x%X, %d)", key, name, buf, bufsize);
  
  DWORD type, size;
  
  if((RegQueryValueEx(key, name, NULL, &type, NULL, &size) == ERROR_SUCCESS)
     && (type == REG_SZ)
     && (size < bufsize)) {
    if(RegQueryValueEx(key, name, NULL, NULL, buf, &size) == ERROR_SUCCESS)
      return 0;
  }
  
  return -1;
}
 
static int readJvmPathFromRegistry(_TCHAR *buf, int bufsize) {
  LOG(trace, "int readJvmPathFromRegistry((_TCHAR*) 0x%X, %d)", buf, bufsize);
  
  HKEY key, subkey;
  BYTE version[MAX_PATH];
  
  LOG(debug, "  readJvmPathFromRegistry(__out _TCHAR *buf [ptr: 0x%X], %i)", (int)buf, bufsize);
  if(RegOpenKeyEx(HKEY_LOCAL_MACHINE, JRE_KEY, 0, KEY_READ, &key) != ERROR_SUCCESS) {
    LOG(debug, "  Could not open key HKLM\\%s. Aborting.", JRE_KEY);
    return -1;
  }
  if(readStringFromRegistry(key, CURVER_STR, version, sizeof(version)) != 0) {
    LOG(debug, "  Could not read %s string (CURVER) from Java registry location. Aborting...", CURVER_STR);
    RegCloseKey(key);
    return -2;
  }
  // If we call the unicode version of the function, we will get unicode data back, so we can cast to _TCHAR
  if(RegOpenKeyEx(key, (_TCHAR*)version, 0, KEY_READ, &subkey) != ERROR_SUCCESS) {
    LOG(debug, "  Could not open subkey %s. Aborting...", (_TCHAR*)version);
    RegCloseKey(key);
    return -3;
  }
  if(readStringFromRegistry(subkey, RUNLIB_STR, (LPBYTE)buf, bufsize) != 0) {
    LOG(debug, "  Could not read %s string (RUNLIB) from Java registry location. Aborting...", RUNLIB_STR);
    RegCloseKey(subkey);
    RegCloseKey(key);
    return -4;
  }
  RegCloseKey(subkey);
  RegCloseKey(key);
  return 0;
}
/* End: Code from Sun's forums. */

static bool locateSunJVMInRegistry(JNI_CreateJavaVM_t **JNI_CreateJavaVM_f, HINSTANCE *jvmLibOut) {
  LOG(trace, "bool locateSunJVMInRegistry((JNI_CreateJavaVM_t**) 0x%X, (HINSTANCE*) 0x%X)", JNI_CreateJavaVM_f, jvmLibOut);
  bool retval = false;
  
  _TCHAR jvmPath[MAX_PATH];
  
  *JNI_CreateJavaVM_f = NULL;
  *jvmLibOut = NULL;
  
  LOG(debug, "calling locateSunJVMInRegistry(jvmPath, sizeof(jvmPath)");
  if(readJvmPathFromRegistry(jvmPath, MAX_PATH) == 0) {
    LOG(debug, "LoadLibrary(%s);", jvmPath);
    HINSTANCE jvmLib = LoadLibrary(jvmPath);
    
    if(jvmLib == NULL) {
      printError(_T("LoadLibrary failed with error "), GetLastError());
    }
    else {
      LOG(debug, "GetProcAddress(..)");
      JNI_CreateJavaVM_t *jniCreateAddress = (JNI_CreateJavaVM_t *)GetProcAddress(jvmLib, "JNI_CreateJavaVM");
      LOG(debug, "JNI_CreateJavaVM_f set to 0x%X", jniCreateAddress);
      
      if(jniCreateAddress != NULL) {
	retval = true;
	*JNI_CreateJavaVM_f = jniCreateAddress;
	*jvmLibOut = jvmLib;
      }
      else {
	LOG(debug, "FreeLibrary(jvmLib);");
	FreeLibrary(jvmLib);
      }
    }
  }
  
  LOG(trace, "returning from bool locateSunJVMInRegistry((JNI_CreateJavaVM_t**) 0x%X, (HINSTANCE*) 0x%X) with retval %s",
      JNI_CreateJavaVM_f, jvmLibOut, bool2str(retval));
  return retval;
}

static bool locateJVMThroughJavaHome(JNI_CreateJavaVM_t **JNI_CreateJavaVM_f, HINSTANCE *jvmLibOut) {
  LOG(trace, "bool locateJVMThroughJavaHome((JNI_CreateJavaVM_t**) 0x%X, (HINSTANCE*) 0x%X)", JNI_CreateJavaVM_f, jvmLibOut);
  
  bool returnValue = false;
  
  *JNI_CreateJavaVM_f = NULL;
  *jvmLibOut = NULL;
  
  _TCHAR *envString = new _TCHAR[32767];
  
  const int endingsLength = 2;
  _TCHAR *endings[endingsLength] = { _T("\\jre\\bin\\client\\jvm.dll"), _T("\\jre\\bin\\server\\jvm.dll") };
  
  for(int i = 0; i < endingsLength; ++i) {
    if(GetEnvironmentVariable(_T("JAVA_HOME"), envString, 32767) > 0) {
      // The JAVA_HOME variable should (?) contain a single string pointing to the JDK(JRE?) home dir
      _TCHAR *trailing = endings[i];
      
      int trailingLength = _tcslen(trailing);
      LOG(debug, "sizeof(trailing)=%d", trailingLength);
    
      _TCHAR *destination = new _TCHAR[32767+trailingLength];
      destination[0] = _T('\0');
      _tcscat(destination, envString);
      _tcscat(destination, trailing);
      LOG(debug, "LoadLibrary(%s);", destination);
      HINSTANCE jvmLib = LoadLibrary(destination);
      delete[] destination;
      
      if(jvmLib == NULL) {
	printError(_T("LoadLibrary failed with error "), GetLastError());
      }
      else {
	LOG(debug, "GetProcAddress(..)");
	JNI_CreateJavaVM_t *jniCreateAddress = (JNI_CreateJavaVM_t *)GetProcAddress(jvmLib, "JNI_CreateJavaVM");
	LOG(debug, "JNI_CreateJavaVM_f set to %X", jniCreateAddress);
      
	if(jniCreateAddress != NULL) {
	  LOG(debug, "Returning from locateJVMThroughJavaHome...");
	  returnValue = true;
	  *JNI_CreateJavaVM_f = jniCreateAddress;
	  *jvmLibOut = jvmLib;
	  break;
	}
	else {
	  LOG(debug, "FreeLibrary(jvmLib);");
	  FreeLibrary(jvmLib);
	}
      }
    }
    else
      LOG(debug, " JAVA_HOME environment variable not found.");
  
  }
  
  delete[] envString;
  
  return returnValue;
}

static bool locateJavaVM(JNI_CreateJavaVM_t **JNI_CreateJavaVM_f, HINSTANCE *jvmLibOut) {
  LOG(trace, "int locateJavaVM((JNI_CreateJavaVM_t**) 0x%X, (HINSTANCE*) 0x%X)", JNI_CreateJavaVM_f, jvmLibOut);
  bool retval = true;

  if(DISABLE_REGISTRY_SEARCH || locateSunJVMInRegistry(JNI_CreateJavaVM_f, jvmLibOut) == FALSE) {
    if(DISABLE_JAVA_HOME_SEARCH || locateJVMThroughJavaHome(JNI_CreateJavaVM_f, jvmLibOut) == FALSE)
      retval = false;
  }
  
  return retval;
}

static bool createJavaVM(JavaVM **jvmOut, HINSTANCE *jvmLibOut) {
  LOG(trace, "int createJavaVM(JavaVM**) 0x%X, (HINSTANCE*) 0x%X)", jvmOut, jvmLibOut);
  bool retval = false;
  *jvmOut = NULL;
  *jvmLibOut = NULL;

  JNI_CreateJavaVM_t * JNI_CreateJavaVM_f = NULL;
  HINSTANCE jvmLibInstance = NULL;
  bool createVMRes = locateJavaVM(&JNI_CreateJavaVM_f, &jvmLibInstance);
  LOG(debug, "createVMRes=%s, JNI_CreateJavaVM_f=0x%X, jvmLibInstance=0x%X", bool2str(createVMRes), JNI_CreateJavaVM_f, jvmLibInstance);
  if(createVMRes) {
    //LOG(debug, "Checkpoint 1");
    JNIEnv *env;
    JavaVM *jvm;
    jint res;
    
    /* <Build classpath string> */
    int classpathStringLength = 0;
    int pathSeparatorLength = strlen(PATH_SEPARATOR);
    for(int i = 0; i < classpathComponentsLength; ++i) {
      if(i > 0)
	classpathStringLength += pathSeparatorLength; // PATH_SEPARATOR
      classpathStringLength += strlen(T2A(classpathComponents[i]));
    }
    ++classpathStringLength; // '\0'
    
    char* classpathString = new char[classpathStringLength];
    int pos = 0;
    for(int i = 0; i < classpathComponentsLength; ++i) {
      if(i > 0) {
	strncpy(classpathString+pos, PATH_SEPARATOR, pathSeparatorLength); pos += pathSeparatorLength;
      }
      
      char *currentComponent = T2A(classpathComponents[i]);
      int currentComponentLength = strlen(currentComponent);
      strncpy(classpathString+pos, currentComponent, currentComponentLength); pos += currentComponentLength;
    }
    classpathString[pos++] = '\0';
    if(pos != classpathStringLength)
      LOG(error, "FATAL ERROR: classpath string did not build correctly! pos(%d) != classpathStringLength(%d)", pos, classpathStringLength);
    /* </Build classpath string> */
    
#ifdef JNI_VERSION_1_2
    const char* optionPrefix = "-Djava.class.path=";
    const int optionPrefixLength = strlen(optionPrefix);
    int classpathOptionStringLength = optionPrefixLength + classpathStringLength;
    char* classpathOptionString = new char[classpathOptionStringLength];
    strncpy(classpathOptionString, optionPrefix, optionPrefixLength);
    strncpy(classpathOptionString+optionPrefixLength, classpathString, classpathStringLength);
    classpathOptionString[optionPrefixLength+classpathStringLength-1] = '\0';
    _TCHAR *tClasspathOptionString = A2T(classpathOptionString);
    LOG(debug, "Classpath option string: \"%s\"", tClasspathOptionString);
    
    JavaVMInitArgs vm_args;
    const int nOptions = 2;
    JavaVMOption options[nOptions];
    options[0].optionString =
      classpathOptionString;
    options[1].optionString =
      T2A(_T("-Djava.library.path=") _T(DLL_HOME));
    vm_args.version = 0x00010002;
    vm_args.options = options;
    vm_args.nOptions = nOptions;
    vm_args.ignoreUnrecognized = JNI_TRUE;
    /* Create the Java VM */
    res = JNI_CreateJavaVM_f(&jvm, &env, &vm_args);
    
    //res = JNI_CreateJavaVM(&jvm, (void**)&env, &vm_args); // We now use the dynamically loaded address
#else
    JDK1_1InitArgs vm_args;
    char classpath[1024];
    vm_args.version = 0x00010001;
    JNI_GetDefaultJavaVMInitArgs(&vm_args);
    /* Append classpathString to the default system class path */
    sprintf(classpath, "%s%c%s",
	    vm_args.classpath, PATH_SEPARATOR, classpathString);
    vm_args.classpath = classpath;
    /* Create the Java VM */
    res = JNI_CreateJavaVM_f(&jvm, &env, &vm_args);
#endif /* JNI_VERSION_1_2 */
    
    if(res == 0) {
      *jvmOut = jvm;
      *jvmLibOut = jvmLibInstance;
      //LOG(debug, "Checkpoint 4");
      retval = true;
    }
    else {
      FreeLibrary(jvmLibInstance);
    }
  }
  
  LOG(trace, "Returning from int createJavaVM(JavaVM**) 0x%X, (HINSTANCE*) 0x%X) with retval=%s",
      jvmOut, jvmLibOut, bool2str(retval));
  return retval;
}

static void destroyJavaVM(JavaVM *jvmInstance, HINSTANCE jvmLibInstance) {
  LOG(trace, "void destroyJavaVM((JavaVM*) 0x%X, (HINSTANCE) 0x%X)", jvmInstance, jvmLibInstance);
  jvmInstance->DestroyJavaVM();
  FreeLibrary(jvmLibInstance);
  LOG(trace, "Returning from void destroyJavaVM((JavaVM*) 0x%X, (HINSTANCE) 0x%X)", jvmInstance, jvmLibInstance);
}

static bool startJavaVM(JavaVM *jvmInstance, const int javaArgsLength, const _TCHAR **javaArgs) {
  LOG(trace, "bool startJavaVM((JavaVM*) 0x%X, %d, (_TCHAR**) 0x%X)", jvmInstance, javaArgsLength, javaArgs);
  bool retval = false;
  
  StringBuilder startClassDirsyntaxBuilder;
  for(int i = 0; i < startClassComponentsLength; ++i) {
    if(i > 0)
      startClassDirsyntaxBuilder.append("/");
    startClassDirsyntaxBuilder.append(startClassComponents[i]);
  }
  const char *startClassDirsyntax = startClassDirsyntaxBuilder.toASCIIString();
  
  _TCHAR currentWorkingDirectory[MAX_PATH];
  GetCurrentDirectory(MAX_PATH, currentWorkingDirectory);
  LOG(debug, "Current working dir: \"%s\"", currentWorkingDirectory);
  /* Proceed... */
  JNIEnv *env;
  jvmInstance->AttachCurrentThread((void **)&env, NULL);
  LOG(debug, "    - Initializing FileSystemBrowserWindow");
  jclass cls = env->FindClass(startClassDirsyntax);
  if(env->ExceptionOccurred()) {
    env->ExceptionDescribe();
    LOG(debug, "    - Exception occurred!");
    MessageBox(NULL, _T("Could not find the required class files!"), _T("HFSExplorer launch error"), MB_OK | MB_ICONERROR);
  }
      
  if(cls != NULL) {
    LOG(debug, "    - Class found");
    jmethodID mid = env->GetStaticMethodID(cls, "main", "([Ljava/lang/String;)V");
    if(mid != NULL) {
      LOG(debug, "    - main method found");
      jclass stringClass = env->FindClass("java/lang/String");
      LOG(debug, "    - got String class");
      
      // Create array to hold args (cast to jobjectArray is neccessary for g++ to accept code)
      jobjectArray argsArray = (jobjectArray)env->NewObjectArray(javaArgsLength, stringClass, NULL);
      LOG(debug, "    - created args array");
      if(argsArray != NULL) {
	int newi;
	for(newi = 0; newi < javaArgsLength; ++newi) {
	  const _TCHAR *cur = javaArgs[newi];
	  int curLength = _tcslen(cur);
	  WCHAR *wcCur = NULL;
	  int wcCurLength = -1;
	    
	    
#ifdef _UNICODE
	  wcCurLength = curLength;
	  wcCur = new WCHAR[wcCurLength];
	  wcsncpy(wcCur, cur, curLength);
#else
	  wcCurLength = MultiByteToWideChar(CP_OEMCP, 0, cur, curLength, NULL, 0);
	  if(wcCurLength == 0) {
	    printError(_T("MultiByteToWideChar (getting length) failed with error: "), GetLastError());
	    return -1;
	  }
	  else {
	    wcCur = new WCHAR[wcCurLength];
	    if(MultiByteToWideChar(CP_OEMCP, 0, cur, curLength, wcCur, wcCurLength) == 0) {
	      printError(_T("MultiByteToWideChar (converting) failed with error: "), getLastError());
	      delete[] wcCur;
	      wcCur = NULL;
	    }
	  }
#endif
	      
	  if(wcCur != NULL && wcCurLength != -1) {
	    LOG(debug, "Converting \"%s\" to UTF-8...", wcCur);
	    char* utf8String;
	    int utf8StringLength = WideCharToMultiByte(CP_UTF8, 0, wcCur, wcCurLength, NULL, 0, NULL, NULL);
	    LOG(debug, "utf8StringLength=%d", utf8StringLength);
	    utf8String = new char[utf8StringLength + 1]; // +1, beacause of terminating \0 character
	    if(WideCharToMultiByte(CP_UTF8, 0, wcCur, wcCurLength, utf8String, utf8StringLength, NULL, NULL) != 0) {
	      utf8String[utf8StringLength] = '\0';
	      jstring cur = env->NewStringUTF(utf8String);
	      env->SetObjectArrayElement(argsArray, newi, cur);
	    }
	    else
	      LOG(error, "Failed to convert wcCur (argv[%d]) to UTF-8!", newi);
	    delete[] utf8String;
	  }
	  else
	    LOG(error, "Failed to convert argv[%d] to WCHAR!", newi);
	  delete[] wcCur;
	}
	  
	LOG(debug, "    - args array built. Calling main(String[])...");
	env->CallStaticVoidMethod(cls, mid, argsArray);
	if(env->ExceptionOccurred()) {
	  env->ExceptionDescribe();
	  LOG(error, "    - Exception occurred!");
	}
	else {
	  LOG(debug, "    - Successfully invoked main method!");	  
	  retval = true;
	}
      }
    }
  }
  else {
    LOG(debug, "    - ERROR. Could not find class");
  }
  LOG(trace, "Returning from bool startJavaVM((JavaVM*) 0x%X, %d, (_TCHAR**) 0x%X) with retval=%s",
      jvmInstance, javaArgsLength, javaArgs, bool2str(retval));
  return retval;
}

/**
 * Creates an external Java process using imageFile as the Java executable.
 * imageFile is typically the path to an .exe file called "java.exe" or "javaw.exe".
 */
static bool createExternalJavaProcess(const _TCHAR *imageFile, int javaArgsLength, const _TCHAR **javaArgs) {
  LOG(trace, "bool createExternalJavaProcess((_TCHAR*) 0x%X, %d, (_TCHAR **) 0x%X)",
      imageFile, javaArgsLength, javaArgs);
  bool retval = false;
  
  if(!DISABLE_JAVA_PROCESS_CREATION) {
    STARTUPINFO si;
    PROCESS_INFORMATION pi;
    //LOG(debug, "  zeroing memory..."));
    ZeroMemory( &si, sizeof(si) );
    si.cb = sizeof(si);
    ZeroMemory( &pi, sizeof(pi) );
    
    StringBuilder argsStringBuilder;
    argsStringBuilder.append(imageFile);
    argsStringBuilder.append(" ");
    argsStringBuilder.append("-classpath ");
    for(int i = 0; i < classpathComponentsLength; ++i) {
      if(i > 0)
	argsStringBuilder.append(PATH_SEPARATOR);
      argsStringBuilder.append(classpathComponents[i]);
    }
    argsStringBuilder.append(" ");
    for(int i = 0; i < startClassComponentsLength; ++i) {
      if(i > 0)
	argsStringBuilder.append(".");
      argsStringBuilder.append(startClassComponents[i]);
    }
    for(int i = 0; i < javaArgsLength; ++i) {
      argsStringBuilder.append(" \"");
      argsStringBuilder.append(javaArgs[i]);
      argsStringBuilder.append("\"");
    }
    _TCHAR *commandLine = argsStringBuilder.toWideCharString(new _TCHAR[argsStringBuilder.length()+1]);
    
    LOG(debug, "commandLine=\"%s\"", commandLine);
    //LOG(debug, "printing characters in commandLine detailed:");
    //for(int i = 0; commandLine[i] != _T('\0'); ++i)
    //  LOG(debug, "  commandLine[%d]: '%c' (0x%X)", i, commandLine[i], commandLine[i]);
    LOG(debug, "Trying to spawn process from commandLine...");
    // This is PATHETIC. A simpler API please, MS.. null null false null 0 null bla bla unreadable
    if(CreateProcess(NULL, commandLine, 
		     NULL, NULL, FALSE, CREATE_UNICODE_ENVIRONMENT, NULL, NULL, &si, &pi)) {
      LOG(debug, "Process created successfully.");
      retval = true;
      
      // Wait until child process exits.
      WaitForSingleObject( pi.hProcess, INFINITE );
      
      // Close process and thread handles. 
      CloseHandle( pi.hProcess );
      CloseHandle( pi.hThread );
    }
    else {
      printError(_T("Could not create process! Error "), GetLastError());
    }
    
    delete[] commandLine;
  }
  else
    LOG(debug, "Creation of external Java processes disabled at build time. Returning without doing anything.");
  
  LOG(trace, "returning from bool createExternalJavaProcess((_TCHAR*) 0x%X, %d, (_TCHAR **) 0x%X) with retval %s",
      imageFile, javaArgsLength, javaArgs, bool2str(retval));
  return retval;  
}

static bool createExternalJavaProcesses(int javaArgsLength, const _TCHAR **javaArgs) {
  LOG(trace, "createExternalJavaProcesses(%d, (_TCHAR **) 0x%X)", javaArgsLength, javaArgs);
  bool retval = false;
  
  if(createExternalJavaProcess(_T("javaw.exe"), javaArgsLength, javaArgs) ||
     createExternalJavaProcess(_T("java.exe"), javaArgsLength, javaArgs))
    retval = true;

  // Try the same, but now with WOW64 file system redirection turned off
  if(!retval && WOW64Operations::isWOW64Process()) {
    PVOID oldValue;
    if(WOW64Operations::disableWOW64FileSystemRedirection(&oldValue)) {
      LOG(debug, "Disabled WOW64 fs redirection.");
      if(createExternalJavaProcess(_T("javaw.exe"), javaArgsLength, javaArgs) ||
	 createExternalJavaProcess(_T("java.exe"), javaArgsLength, javaArgs))
	retval = true;
      
      if(!WOW64Operations::revertWOW64FileSystemRedirection(&oldValue))
	printError(_T("Couldn't revert WOW64 file system redirection! Error: "), GetLastError());
    }
    else {
      LOG(debug, "Failed to disable WOW64 fs redirection!");
    }
  }

      

    
  LOG(trace, "returning from bool createExternalJavaProcesses(%d, (_TCHAR **) 0x%X) with retval %s",
      javaArgsLength, javaArgs, bool2str(retval));
  return retval;
}

/**
 * Modifies the global variable classpathString as a result.
 */
static void resolveClasspath(const _TCHAR *const prefix) {
  LOG(trace, "void resolveClasspath((_TCHAR*) 0x%X)", prefix);
  int prefixLength = _tcslen(prefix);
  int i;
  
  int classpathStringLength = -1;
  for(i = 0; i < classpathComponentsLength; ++i) {
    /*
     * Each part of the new classpath string consists of:
     * 
     * ';"'                   - 2 characters (except for first entry, where ';' is not included,
     *                          which is why classpathStringLength is initialized to -1)
     * prefix                 - prefixLength characters
     * '\'                    - 1 character (path separator)
     * classpathComponents[i] - _tcslen(classpathComponents[i]) characters
     * '"'                    - 1 character
     */
    classpathStringLength += 2 + prefixLength + 1 + _tcslen(classpathComponents[i]) + 1;
  }
  ++classpathStringLength; // null terminator
  
  if(classpathString != NULL)
    delete[] classpathString;
  classpathString = new _TCHAR[classpathStringLength];
  ZeroMemory(classpathString, classpathStringLength*sizeof(_TCHAR));
  for(i = 0; i < classpathComponentsLength; ++i) {
    if(i == 0)
      _tcscat(classpathString, _T("\""));
    else
      _tcscat(classpathString, _T(";\""));
    _tcscat(classpathString, prefix);
    _tcscat(classpathString, _T("\\"));
    _tcscat(classpathString, classpathComponents[i]);
    _tcscat(classpathString, _T("\""));
  }
  LOG(debug, "resolveClasspath(_TCHAR*) 0x%X) setting global variable classpathString to \"%s\"",
      prefix, classpathString);
  LOG(trace, "returning from void resolveClasspath((_TCHAR*) 0x%X)", prefix);
}

/**
 * If the first argument is "-invokeuac" we do a little magic in order to fork off
 * a new process with higher access rights than the current process. This will in
 * Windows Vista lead to an UAC dialog (if UAC is turned on) so the user can
 * authorize the program to run with Administrator privilegies.
 */
static bool spawnElevatedProcess(_TCHAR *imageFile, _TCHAR *currentWorkingDirectory, int argc, _TCHAR **argv) {
  LOG(trace, "bool spawnElevatedProcess((_TCHAR*) 0x%X, (_TCHAR*) 0x%X, %d, (_TCHAR**) 0x%X)",
      imageFile, currentWorkingDirectory, argc, argv);
  bool retval = false;
  
  // Initialize COM for ShellExecuteEx (because MSDN says it's safest)
  LOG(debug, "Initializing COM...");
  initializeCOM();
      
  /* Build a space separated argv string that we can pass to ShellExecuteEx
   * excluding the first argument (argv[1]), which is the "-invokeuac" switch. */
  int argvStringLength = 0;
  int i;
  for(i = 0; i < argc; ++i) {
    if(i == 0)
      argvStringLength += 2;
    else
      argvStringLength += 3;
    argvStringLength += _tcslen(argv[i]);
  }
  ++argvStringLength; // For the terminating '\0'

  _TCHAR *argvString = new _TCHAR[argvStringLength];
  ZeroMemory(argvString, argvStringLength*sizeof(_TCHAR));
  
  for(i = 0; i < argc; ++i) {
    _TCHAR *cur = argv[i];
    if(i == 0)
      _tcscat(argvString, _T("\""));
    else
      _tcscat(argvString, _T(" \""));
    _tcscat(argvString, cur);
    _tcscat(argvString, _T("\""));
  }
  argvString[argvStringLength-1] = _T('\0');

  SHELLEXECUTEINFO execInfo;
  ZeroMemory(&execInfo, sizeof(SHELLEXECUTEINFO));
  execInfo.cbSize = sizeof(SHELLEXECUTEINFO);
  execInfo.fMask = SEE_MASK_UNICODE;
  execInfo.hwnd = NULL;
  execInfo.lpVerb = _T("runas");
  execInfo.lpFile = imageFile;
  execInfo.lpParameters = argvString;
  execInfo.lpDirectory = currentWorkingDirectory;
  execInfo.nShow = SW_SHOW;
  execInfo.hInstApp = NULL;
  LOG(debug, "&execInfo=%d", (int)&execInfo);
  LOG(debug, "execInfo.lpVerb=\"%s\"", execInfo.lpVerb);
  LOG(debug, "execInfo.lpFile=\"%s\"", execInfo.lpFile);
  LOG(debug, "execInfo.lpParameters=\"%s\"", execInfo.lpParameters);
  LOG(debug, "execInfo.lpDirectory=\"%s\"", execInfo.lpDirectory);
  LOG(debug, "executing ShellExecuteEx...");
  if(ShellExecuteEx(&execInfo) == TRUE) {
    LOG(debug, "ShellExecuteEx success!");
    retval = true;
  }
  else {
    DWORD errorVal = GetLastError();
    printError(_T("ShellExecuteEx failed with error "), errorVal);
    LOG(debug, "execInfo.hInstApp=%d", execInfo.hInstApp);
    //0 The operating system is out of memory or resources.
    LOG(debug, "  [The operating system is out of memory or resources.]=0");
    LOG(debug, "  ERROR_FILE_NOT_FOUND=%d", ERROR_FILE_NOT_FOUND);
    LOG(debug, "  ERROR_PATH_NOT_FOUND=%d", ERROR_PATH_NOT_FOUND);
    LOG(debug, "  ERROR_BAD_FORMAT=%d", ERROR_BAD_FORMAT);
    LOG(debug, "  SE_ERR_ACCESSDENIED=%d", SE_ERR_ACCESSDENIED);
    LOG(debug, "  SE_ERR_ASSOCINCOMPLETE=%d", SE_ERR_ASSOCINCOMPLETE);
    LOG(debug, "  SE_ERR_DDEBUSY=%d", SE_ERR_DDEBUSY);
    LOG(debug, "  SE_ERR_DDEFAIL=%d", SE_ERR_DDEFAIL);
    LOG(debug, "  SE_ERR_DDETIMEOUT=%d", SE_ERR_DDETIMEOUT);
    LOG(debug, "  SE_ERR_DLLNOTFOUND=%d", SE_ERR_DLLNOTFOUND);
    LOG(debug, "  SE_ERR_FNF=%d", SE_ERR_FNF);
    LOG(debug, "  SE_ERR_NOASSOC=%d", SE_ERR_NOASSOC);
    LOG(debug, "  SE_ERR_OOM=%d", SE_ERR_OOM);
    LOG(debug, "  SE_ERR_PNF=%d", SE_ERR_PNF);
    LOG(debug, "  SE_ERR_SHARE=%d", SE_ERR_SHARE);
    LOG(debug, "errorVal=%d", errorVal);
    LOG(debug, "  ERROR_FILE_NOT_FOUND=%d", ERROR_FILE_NOT_FOUND);
    LOG(debug, "  ERROR_PATH_NOT_FOUND=%d", ERROR_PATH_NOT_FOUND);
    LOG(debug, "  ERROR_DDE_FAIL=%d", ERROR_DDE_FAIL);
    LOG(debug, "  ERROR_NO_ASSOCIATION=%d", ERROR_NO_ASSOCIATION);
    LOG(debug, "  ERROR_ACCESS_DENIED=%d", ERROR_ACCESS_DENIED);
    LOG(debug, "  ERROR_DLL_NOT_FOUND=%d", ERROR_DLL_NOT_FOUND);
    LOG(debug, "  ERROR_CANCELLED=%d", ERROR_CANCELLED);
    LOG(debug, "  ERROR_NOT_ENOUGH_MEMORY=%d", ERROR_NOT_ENOUGH_MEMORY);
    LOG(debug, "  ERROR_SHARING_VIOLATION=%d", ERROR_SHARING_VIOLATION);
    MessageBox(NULL, _T("Error while trying to create elevated process..."), _T("HFSExplorer launch error"), MB_OK);
  }
  
  // Clean up
  delete[] argvString;
  
  // Uninitialize COM (no more need for it)
  LOG(debug, "Uninitializing COM...");
  uninitializeCOM();

  LOG(trace, "returning from bool spawnElevatedProcess((_TCHAR*) 0x%X, (_TCHAR*) 0x%X, %d, (_TCHAR**) 0x%X) with retval %s",
      imageFile, currentWorkingDirectory, argc, argv, bool2str(retval));
  return retval;
}

int main(int original_argc, char** original_argv) {
  LOG(trace, "int main(%d, (char**)0x%X)", original_argc, original_argv);
#ifdef _UNICODE
  const _TCHAR *commandLine = GetCommandLine();
  int argc = 0;
  _TCHAR **argv = CommandLineToArgvW(commandLine, &argc);
  if(argv == NULL) {
    MessageBox(NULL, _T("Could not get argv! FATAL..."), _T("HFSExplorer launch error"), MB_OK);
    return RETVAL_COMMANDLINETOARGVW_FAILED;
  }
#else
  _TCHAR **argv = original_argv;
  int argc = original_argc;
#endif
  
  /* <Get the fully qualified path of this executable> */
  int processFilenameLength = MAX_PATH;
  _TCHAR *processFilename = NULL;
  while(1) {
    if(processFilename != NULL) {
      delete[] processFilename;
    }
    processFilename = new _TCHAR[processFilenameLength];
    
    int gmfRes = GetModuleFileName(NULL, processFilename, processFilenameLength);
    if(gmfRes == 0) {
      printError(_T("GetModuleFileName failed with error "), GetLastError());
      MessageBox(NULL, _T("Problem (1) with getting the fully qualified path of the executable! FATAL..."), 
		 _T("HFSExplorer launch error"), MB_OK);
      return RETVAL_COULD_NOT_GET_EXE_PATH;
    }
    else if(gmfRes == processFilenameLength) {
      processFilenameLength = processFilenameLength*2;
    }
    else {
      break;
    }
  }
  LOG(debug, "Got fully qualified path: \"%s\"", processFilename);
  /* </Get the fully qualified path of this executable> */
  
  
  /* <Extract the parent directory from the fully qualified path> */
  int processFilenameStrlen = _tcslen(processFilename);
  int psIndex;
  for(psIndex = processFilenameStrlen-1; psIndex >= 0; --psIndex) {
    if(processFilename[psIndex] == _T('\\'))
      break;
  }
  DWORD processParentDirLength = psIndex+1;
  _TCHAR *processParentDir = new _TCHAR[processParentDirLength];
  memcpy(processParentDir, processFilename, sizeof(_TCHAR)*(processParentDirLength-1));
  processParentDir[psIndex] = _T('\0');
  LOG(debug, "Got fully qualified parent dir: \"%s\"", processParentDir);
  /* <//Extract the parent directory from the fully qualified path> */
  
  
  // Resolve the absolute classpath variables
  resolveClasspath(processParentDir);
  
  // Get the current working directory
  const DWORD currentWorkingDirectoryLength = GetCurrentDirectory(0, NULL);
  _TCHAR *currentWorkingDirectory = new _TCHAR[currentWorkingDirectoryLength];
  
  int returnValue = RETVAL_UNKNOWN_ERROR;
  DWORD actualCWDLength = GetCurrentDirectory(currentWorkingDirectoryLength, currentWorkingDirectory);
  if(actualCWDLength+1 != currentWorkingDirectoryLength) {
    LOG(debug, "currentWorkingDirectoryLength=%d, actualCWDLength=%d", currentWorkingDirectoryLength, actualCWDLength);
    printError(_T("GetCurrentDirectory failed with error "), GetLastError());
    MessageBox(NULL, _T("Could not get the current working directory."), _T("HFSExplorer launch error"), MB_OK);
    returnValue = RETVAL_COULD_NOT_GET_CWD;
  }
  else {
    LOG(debug, "CWD: \"%s\"", currentWorkingDirectory);
    
    if(argc > 1 && _tcscmp(argv[1], _T("-invokeuac")) == 0) {
      LOG(debug, "\"-invokeuac\" specified. Forking off elevated process...");
      
      if(spawnElevatedProcess(argv[0], currentWorkingDirectory, argc-2, argv+2))
	returnValue = RETVAL_OK;
      else {
	returnValue = RETVAL_INVOKEUAC_FAILED;
	LOG(error, "Failed to create elevated (UAC) process!");
	MessageBox(NULL, _T("Failed to create elevated (UAC) process!"), _T("HFSExplorer launch error"), MB_OK);
      }
    }
    else { // No -invokeuac switch supplied
      // <Ugly hack which converts argv[1] into an absolute path name>
      _TCHAR *fullPathName = NULL;
      if(argc > 1) {
	const int fullPathNameLength =
	  SearchPath(currentWorkingDirectory,
		     argv[1],
		     NULL,
		     0,
		     NULL,
		     NULL);
	LOG(debug, "fullPathNameLength: %d", fullPathNameLength);
	if(fullPathNameLength == 0) {
	  printError(_T("Could not get length of full path name. Last error: "), GetLastError());
	}
	else {
	  fullPathName = new _TCHAR[fullPathNameLength];
	  ZeroMemory(fullPathName, fullPathNameLength*sizeof(_TCHAR));
	  int charsRead = SearchPath(currentWorkingDirectory,
				     argv[1],
				     NULL,
				     fullPathNameLength,
				     fullPathName,
				     NULL);
	  //int charsRead = GetFullPathName(argv[1], MAX_PATH, fullPathName, NULL);
	  if(charsRead == 0) {
	    printError(_T("Could not convert args[1] into pathname. Last error: "), GetLastError());
	  }
	  else {
	    LOG(debug, "full pathname: \"%s\"", fullPathName);
	    argv[1] = fullPathName;
	  }
	}
      }
      // </Ugly hack which converts argv[1] into an absolute path name>
      
      // <Set the working dir to the process parent dir />
      // This is really ugly too. If possible, try not to alter working dir.
      SetCurrentDirectory(processParentDir);
      
      /* <Alter environment variable PATH to include .dll dir> */
      {
	const DWORD envPathLength = GetEnvironmentVariable(_T("PATH"), NULL, 0);
	_TCHAR *envPath = new _TCHAR[envPathLength];
	DWORD envLen = GetEnvironmentVariable(_T("PATH"), envPath, envPathLength);
	if(envLen != envPathLength-1) {
	  LOG(error, "Could not get environment variable PATH. envLen=%d, envPathLength=%d.", envLen, envPathLength);
	  printError(_T("Error: "), GetLastError());
	}
	else {
	  LOG(debug, "Read old PATH variable: \"%s\"", envPath);
	  const DWORD newEnvPathLength = _tcslen(envPath) + 1 + _tcslen(processParentDir) + 1 + _tcslen(_T(DLL_HOME)) + 1;
	  _TCHAR *newEnvPath = new _TCHAR[newEnvPathLength];
	  {
	    int ptr = 0;
	    _tcsncpy(newEnvPath+ptr, envPath, _tcslen(envPath)); ptr += _tcslen(envPath);
	    newEnvPath[ptr++] = _T(';');
	    _tcsncpy(newEnvPath+ptr, processParentDir, _tcslen(processParentDir)); ptr += _tcslen(processParentDir);
	    newEnvPath[ptr++] = _T('\\');
	    _tcsncpy(newEnvPath+ptr, _T(DLL_HOME), _tcslen(_T(DLL_HOME))); ptr += _tcslen(_T(DLL_HOME));
	    newEnvPath[ptr++] = _T('\0');
	  }
	  LOG(debug, "Setting new PATH variable: \"%s\"", newEnvPath);
	  
	  if(SetEnvironmentVariable(_T("PATH"), newEnvPath) == TRUE)
	    LOG(debug, "Successfully set PATH variable!");
	  else
	    printError(_T("Path variable could not be set! Error "), GetLastError());
	  
	}
	
	LPTSTR lpszVariable; 
	LPTCH lpvEnv; 
	
	// Get a pointer to the environment block. 
	
	lpvEnv = GetEnvironmentStrings();
	
	// If the returned pointer is NULL, exit.
	if(lpvEnv == NULL) {
	  printError(_T("GetEnvironmentStrings failed: "), GetLastError());
	  return 0;
	}
	
	// Variable strings are separated by NULL byte, and the block is 
	// terminated by a NULL byte. 
	
	lpszVariable = (LPTSTR) lpvEnv;
	
	LOG(debug, "Current environment:");
	while(*lpszVariable) {
	  LOG(debug, "  %s", lpszVariable);
	  lpszVariable += lstrlen(lpszVariable) + 1;
	}
	FreeEnvironmentStrings(lpvEnv);
      }
      /* </Alter environment variable PATH to include .dll dir> */
      
      
      /* <Build the argument list to pass to the JVM and our main method> */      
      const int javaArgsLength = (argc - 1) + prefixArgsLength;
      const _TCHAR **javaArgs = new const _TCHAR*[javaArgsLength];
      
      int curArg = 0;
      for(int i = 0; i < prefixArgsLength; ++i) {
	javaArgs[curArg++] = prefixArgs[i];
      }
      for(int i = 1; i < argc; ++i) {
	javaArgs[curArg++] = argv[i];
      }
      
      if(curArg != javaArgsLength)
	LOG(error, "ASSERTION FAILED: curArg(%d) != javaArgsLength(%d)", curArg, javaArgsLength);
      /* </Build the argument list to pass to the JVM and our main method> */
      
      
      /* <Try different methods to locate and start a JVM> */
      JavaVM *jvmInstance;
      HINSTANCE jvmLibInstance;
      if(createJavaVM(&jvmInstance, &jvmLibInstance)) {
	bool jvmStarted = false;
	if(startJavaVM(jvmInstance, javaArgsLength, javaArgs)) {
	  returnValue = RETVAL_OK;
	  jvmStarted = true;
	}
	
	destroyJavaVM(jvmInstance, jvmLibInstance);
	
	if(!jvmStarted) {
	  if(createExternalJavaProcesses(javaArgsLength, javaArgs) == true)
	    returnValue = RETVAL_OK;
	  else
	    returnValue = RETVAL_ERROR_STARTING_JVM;
	}
      }
      else if(createExternalJavaProcesses(javaArgsLength, javaArgs) == true) {
	returnValue = RETVAL_OK;
      }
      else {
	MessageBox(NULL, _T("No Java Virtual Machine found! Please get one from http://java.sun.com ..."), _T("HFSExplorer launch error"), MB_OK);
	returnValue = RETVAL_JVM_NOT_FOUND;
      }
      /* </Try different methods to locate and start a JVM> */
      
      
      /* <Clean up> */
      if(fullPathName != NULL)
	delete[] fullPathName;
      /* </Clean up> */
    }
  }
  
  // <Cleanup>
  delete[] currentWorkingDirectory;
  delete[] processParentDir;
  delete[] processFilename;
  // </Cleanup>

  return returnValue;
}
