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

#include <stdio.h>
#include <jni.h>
#include <windows.h>
#include <tchar.h>

#include "WOW64Operations.hh"

#define FIRSTARG _T("-dbgconsole")
#define START_CLASS_PKGSYNTAX _T("org.catacombae.hfsexplorer.FileSystemBrowserWindow ") FIRSTARG
#define START_CLASS           "org/catacombae/hfsexplorer/FileSystemBrowserWindow"
#define USER_CLASSPATH        "lib\\hfsx.jar;lib\\swing-layout-1.0.1.jar;lib\\hfsx_dmglib.jar;lib\\apache-ant-1.7.0-bzip2.jar;lib\\iharder-base64.jar"
#define CLASSPATH_COMPONENT_COUNT 5
static const _TCHAR *classpathComponents[CLASSPATH_COMPONENT_COUNT] = { _T("lib\\hfsx.jar"), _T("lib\\swing-layout-1.0.1.jar"), _T("lib\\hfsx_dmglib.jar"), _T("lib\\apache-ant-1.7.0-bzip2.jar"), _T("lib\\iharder-base64.jar") };
static _TCHAR *classpathString = NULL; // Set by calling resolveClasspath()

#define FALSE 0
#define TRUE 1
#define DEBUGMODE TRUE
#if DEBUGMODE
#define DEBUG(...) _ftprintf(stderr, __VA_ARGS__);
#else
#define DEBUG(...) ;
#endif

/* The following variables can be used to disable certain invocation methods, for testing. */
#define DISABLE_REGISTRY_SEARCH        FALSE
#define DISABLE_JAVA_HOME_SEARCH       FALSE
#define DISABLE_JAVA_PROCESS_CREATION  FALSE

static JavaVM *staticJvm = NULL;
static HINSTANCE jvmLib = NULL;

/* <Utility methods> */

static inline void printError(_TCHAR *prefix, DWORD errorVal) {
  _TCHAR *message;
  FormatMessage(FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_SYSTEM, NULL, errorVal, LANG_USER_DEFAULT, (LPTSTR)(&message), 0, NULL);
  DEBUG(_T("%sERROR %i: %s\n"), prefix, errorVal, message);
  LocalFree(message);
}
static inline void printError(DWORD errorVal) {
  printError(_T(""), errorVal);
}

/* </Utility methods> */

/* Begin: Code from Sun's forums. http://forum.java.sun.com/thread.jspa?threadID=5124559&messageID=9441051 */
typedef jint (JNICALL JNI_CreateJavaVM_t)(JavaVM ** pJvm, JNIEnv ** pEnv, void * vmArgs);
 
#define JRE_KEY         _T("SOFTWARE\\JavaSoft\\Java Runtime Environment")
#define CURVER_STR      _T("CurrentVersion")
#define RUNLIB_STR      _T("RuntimeLib")
 
static int readStringFromRegistry(HKEY key, const _TCHAR *name, LPBYTE buf, DWORD bufsize) {
    DWORD type, size;
    
    if ((RegQueryValueEx(key, name, NULL, &type, NULL, &size) == ERROR_SUCCESS)
	&& (type == REG_SZ)
	&& (size < bufsize)) {
      if (RegQueryValueEx(key, name, NULL, NULL, buf, &size) == ERROR_SUCCESS)
            return 0;
    }
    return -1;
}
 
static int readJvmPathFromRegistry(_TCHAR *buf, int bufsize) {
    HKEY key, subkey;
    BYTE version[MAX_PATH];
    
    DEBUG(_T(" readJvmPathFromRegistry(__out _TCHAR *buf [ptr: 0x%X], %i)\n"), (int)buf, bufsize);
    if (RegOpenKeyEx(HKEY_LOCAL_MACHINE, JRE_KEY, 0, KEY_READ, &key) != ERROR_SUCCESS) {
      DEBUG(_T("  Could not open key HKLM\\%s. Aborting.\n"), JRE_KEY);
      return -1;
    }
    if (readStringFromRegistry(key, CURVER_STR, version, sizeof(version)) != 0) {
      DEBUG(_T("  Could not read %s string (CURVER) from Java registry location. Aborting...\n"), CURVER_STR);
      RegCloseKey(key);
      return -2;
    }
    // If we call the unicode version of the function, we will get unicode data back, so we can cast to _TCHAR
    if (RegOpenKeyEx(key, (_TCHAR*)version, 0, KEY_READ, &subkey) != ERROR_SUCCESS) {
      DEBUG(_T("  Could not open subkey %s. Aborting...\n"), (_TCHAR*)version);
      RegCloseKey(key);
      return -3;
    }
    if (readStringFromRegistry(subkey, RUNLIB_STR, (LPBYTE)buf, bufsize) != 0) {
      DEBUG(_T("  Could not read %s string (RUNLIB) from Java registry location. Aborting...\n"), RUNLIB_STR);
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
  _TCHAR jvmPath[MAX_PATH];
  //JNI_CreateJavaVM_t * JNI_CreateJavaVM_f;
  DEBUG(_T("locateSunJVMInRegistry(jvmPath, sizeof(jvmPath)\n"));
  if (readJvmPathFromRegistry(jvmPath, MAX_PATH) != 0) {
    return FALSE;
  }
  
  DEBUG(_T("LoadLibrary(%s);\n"), jvmPath);
  jvmLib = LoadLibrary(jvmPath);
  
  if (jvmLib == NULL) {
    printError(_T(" "), GetLastError());
    return FALSE;
  }
  
  DEBUG(_T("GetProcAddress(..)\n"));
  *JNI_CreateJavaVM_f = (JNI_CreateJavaVM_t *)GetProcAddress(jvmLib, "JNI_CreateJavaVM");
  DEBUG(_T("JNI_CreateJavaVM_f set to %X\n"), (int)(*JNI_CreateJavaVM_f));
  
  if (*JNI_CreateJavaVM_f == NULL) {
    DEBUG(_T("FreeLibrary(jvmLib);\n"));
    FreeLibrary(jvmLib);
    return FALSE;
  }
  DEBUG(_T("Returning from locateSunJVMInRegistry...\n"));
  return TRUE;
}

static int locateJVMThroughJavaHome(JNI_CreateJavaVM_t** JNI_CreateJavaVM_f) {
  int returnValue = FALSE;
  DEBUG(_T("locateJVMThroughJavaHome(...)\n"));
  _TCHAR *envString = (_TCHAR*) malloc(32767*sizeof(_TCHAR));
  if(GetEnvironmentVariable(_T("JAVA_HOME"), envString, 32767) > 0) {
    // The JAVA_HOME variable should (?) contain a single string pointing to the JDK(JRE?) home dir
    _TCHAR *trailing = _T("\\jre\\bin\\client\\jvm.dll");
    int trailingLength = _tcslen(trailing);
    DEBUG(_T("sizeof(trailing)=%d\n"), trailingLength);
    _TCHAR *destination = (_TCHAR*) malloc((32767+trailingLength)*sizeof(_TCHAR));
    destination[0] = _T('\0');
    _tcscat(destination, envString);
    _tcscat(destination, trailing);
    DEBUG(_T("LoadLibrary(%s);\n"), destination);
    jvmLib = LoadLibrary(destination);
    free(destination);
    if(jvmLib == NULL)
      printError(_T(" "), GetLastError());
    else {
      DEBUG(_T("GetProcAddress(..)\n"));
      *JNI_CreateJavaVM_f = (JNI_CreateJavaVM_t *)GetProcAddress(jvmLib, "JNI_CreateJavaVM");
      DEBUG(_T("JNI_CreateJavaVM_f set to %X\n"), (int)(*JNI_CreateJavaVM_f));
      
      if(*JNI_CreateJavaVM_f != NULL) {
	DEBUG(_T("Returning from locateJVMThroughJavaHome...\n"));
	returnValue = TRUE;
      }
      else {
	DEBUG(_T("FreeLibrary(jvmLib);\n"));
	FreeLibrary(jvmLib);
      }
    }
  }
  else
    DEBUG(_T(" JAVA_HOME environment variable not found.\n"));
  free(envString);
  return returnValue;
}

static int locateJavaVM(JNI_CreateJavaVM_t** JNI_CreateJavaVM_f) {
  DEBUG(_T("locateJavaVM(..)\n"));
  
  if(DISABLE_REGISTRY_SEARCH || locateSunJVMInRegistry(JNI_CreateJavaVM_f) == FALSE) {
    if(DISABLE_JAVA_HOME_SEARCH || locateJVMThroughJavaHome(JNI_CreateJavaVM_f) == FALSE)
      return FALSE;
  }
  return TRUE;
}

static int createJavaVM() {
  JNI_CreateJavaVM_t * JNI_CreateJavaVM_f = NULL;
  int createVMRes = locateJavaVM(&JNI_CreateJavaVM_f);
  DEBUG(_T("JNI_CreateJavaVM_f=%X\n"), (int)(JNI_CreateJavaVM_f));
  if(createVMRes == FALSE)
    return FALSE;
/*   DEBUG(_T("Checkpoint 1\n")); */
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
/*     DEBUG(_T("Checkpoint 4\n")); */
    return TRUE;
  }
  else {
    FreeLibrary(jvmLib);
    return FALSE;
  }
}

static int createExternalJavaProcess(int argc, _TCHAR **argv) {
  DEBUG(_T("createExternalJavaProcess()\n"));
  STARTUPINFO si;
  PROCESS_INFORMATION pi;
  //DEBUG(_T("  zeroing memory..."));
  ZeroMemory( &si, sizeof(si) );
  si.cb = sizeof(si);
  ZeroMemory( &pi, sizeof(pi) );

  //DEBUG(_T("  creating base command lines...\n"));
  TCHAR *szCmdline1 = _T("javaw.exe -classpath ") _T(USER_CLASSPATH) _T(" ") START_CLASS_PKGSYNTAX;
  TCHAR *szCmdline2 = _T("java.exe -classpath ") _T(USER_CLASSPATH) _T(" ") START_CLASS_PKGSYNTAX;
  int lenSzCmdline1 = _tcslen(szCmdline1);
  int lenSzCmdline2 = _tcslen(szCmdline2);
  //DEBUG(_T("  scCmdline1[len: %d]=\"%s\"\n"), lenSzCmdline1, szCmdline1);
  //DEBUG(_T("  scCmdline2[len: %d]=\"%s\"\n"), lenSzCmdline2, szCmdline2);
  
  /* copy the contents of argv into new strings */
  //DEBUG(_T("  copying the contents of argc into new strings...\n"));
  int i;
  unsigned int newArgStringLength = 0;
  for(i = 1; i < argc; ++i)
    newArgStringLength += _tcslen(argv[i]) + 3; // +3 for two "-characters and one whitespace
  newArgStringLength += 1; // For the null terminator;
  unsigned int newArgStringSize = newArgStringLength*sizeof(_TCHAR);

  //DEBUG(_T("  allocating memory for new string... (%d bytes)\n"), newArgStringSize);
  _TCHAR *newArgString = new _TCHAR[newArgStringLength]; // (_TCHAR*) malloc(newArgStringSize);
  unsigned int ptr = 0;
  //DEBUG(_T("  copying args...\n"));
  for(i = 1; i < argc; ++i) {
    //DEBUG(_T("    argv[%d]...\n"), i);
    _TCHAR* cur = argv[i];
    int curlen = _tcslen(cur);
    newArgString[ptr++] = _T(' ');
    newArgString[ptr++] = _T('\"');
    _tcscpy(newArgString+ptr, cur);
    ptr += curlen;
    newArgString[ptr++] = _T('\"');
  }
  if(ptr != (newArgStringLength-1))
    DEBUG(_T("INTERNAL ERROR! newArgString has been incorrectly copied! ptr=%d newArgStringSize=%d\n"), ptr, newArgStringSize);
  newArgString[ptr++] = _T('\0'); // No null terminator => chaos.
  
  //DEBUG(_T("  concatentating first string into true command line...\n"));

  _TCHAR *szTrueCmdline1 = (_TCHAR*) malloc(lenSzCmdline1*sizeof(_TCHAR) + newArgStringSize);
  _tcscpy(szTrueCmdline1, szCmdline1);
  _tcscpy(szTrueCmdline1+lenSzCmdline1, newArgString);
  DEBUG(_T("szTrueCmdline1=\"%s\"\n"), szTrueCmdline1);

  //DEBUG(_T("  concatentating second string into true command line...\n"));
  //DEBUG(_T("  malloc(%d)...\n"), (lenSzCmdline2*sizeof(_TCHAR) + newArgStringSize));
  _TCHAR *szTrueCmdline2 = (_TCHAR*) malloc(lenSzCmdline2*sizeof(_TCHAR) + newArgStringSize);
  //DEBUG(_T("  _tcscopy 1...\n"));
  _tcscpy(szTrueCmdline2, szCmdline2);
  //DEBUG(_T("  _tcscopy 2...\n"));
  _tcscpy(szTrueCmdline2+lenSzCmdline2, newArgString);

  //DEBUG(_T("  freeing newArgString...\n"));
  delete newArgString;
  
  DEBUG(_T("szTrueCmdline2=\"%s\"\n"), szTrueCmdline2);
  //MessageBox(NULL, szTrueCmdline1, _T("szTrueCmdline1"), MB_OK);
  //MessageBox(NULL, szTrueCmdline2, _T("szTrueCmdline2"), MB_OK);
  
  DEBUG(_T("trying with first process (javaw.exe)\n"));
  // This is PATHETIC. A simpler API please, MS.. null null false null 0 null bla bla unreadable
  if(!CreateProcess(NULL, szTrueCmdline1, 
		    NULL, NULL, FALSE, CREATE_UNICODE_ENVIRONMENT, NULL, NULL, &si, &pi)) {
    // Reset si and pi in case CreateProcess did something with them
    ZeroMemory( &si, sizeof(si) );
    si.cb = sizeof(si);
    ZeroMemory( &pi, sizeof(pi) );
    DEBUG(_T("trying with second process (java.exe)\n"));
    if(!CreateProcess(NULL, szTrueCmdline2, 
		      NULL, NULL, FALSE, CREATE_UNICODE_ENVIRONMENT, NULL, NULL, &si, &pi))
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

static void resolveClasspath(const _TCHAR *prefix) {
  int prefixLength = _tcslen(prefix);
  int i;
  int classpathStringLength = -1;
  for(i = 0; i < CLASSPATH_COMPONENT_COUNT; ++i) {
    classpathStringLength += 2 + prefixLength + 1 + _tcslen(classpathComponents[i]) + 1;
  }
  int classpathStringSize = (classpathStringLength+1)*sizeof(_TCHAR);
  classpathString = (_TCHAR*) malloc(classpathStringSize);
  ZeroMemory(classpathString, classpathStringSize);
  for(i = 0; i < CLASSPATH_COMPONENT_COUNT; ++i) {
    if(i == 0)
      _tcscat(classpathString, _T("\""));
    else
      _tcscat(classpathString, _T(";\""));
    _tcscat(classpathString, prefix);
    _tcscat(classpathString, _T("\\"));
    _tcscat(classpathString, classpathComponents[i]);
    _tcscat(classpathString, _T("\""));
  }
  DEBUG(_T("resolveClasspath: \"%s\"\n"), classpathString);
}

//int __cdecl _tmain(int argc, _TCHAR **argv, _TCHAR **envp) {
int main(int original_argc, char** original_argv) {
#ifdef _UNICODE
  const _TCHAR *commandLine = GetCommandLine();
  int argc = 0;
  _TCHAR **argv = CommandLineToArgvW(commandLine, &argc);
  if(argv == NULL) {
    MessageBox(NULL, _T("Could not get argv! FATAL..."), _T("HFSExplorer launch error"), MB_OK);
    return -1;
  }
#else
  _TCHAR **argv = original_argv;
  int argc = original_argc;
#endif
  
  // Make sure WOW64 file system redirection isn't active.
  if(WOW64Operations::isWOW64Process()) {
    PVOID oldValue;
    if(WOW64Operations::disableWOW64FileSystemRedirection(&oldValue)) {
      DEBUG(_T("Disabled WOW64 fs redirection.\n"));
    }
    else {
      DEBUG(_T("Failed to disable WOW64 fs redirection!\n"));
    }
  }

  // Get the fully qualified path of this executable
  int processFilenameLength = MAX_PATH;
  _TCHAR *processFilename = NULL;
  while(1) {
    if(processFilename != NULL) {
      processFilename = (_TCHAR*) realloc(processFilename, sizeof(_TCHAR)*processFilenameLength);
    }
    else {
      processFilename = (_TCHAR*) malloc(sizeof(_TCHAR)*processFilenameLength);
    }
    int gmfRes = GetModuleFileName(NULL, processFilename, processFilenameLength);
    if(gmfRes == 0) {
      DEBUG(_T("Last error: %d"), GetLastError());
      MessageBox(NULL, _T("Problem (1) with getting the fully qualified path of the executable! FATAL..."), 
		 _T("HFSExplorer launch error"), MB_OK);
      return -1;
    }
    else if(gmfRes == processFilenameLength) {
      processFilenameLength = processFilenameLength*2;
    }
    else {
      break;
    }
  }
  DEBUG(_T("Got fully qualified path: \"%s\"\n"), processFilename);

  // Extract the parent directory from the fully qualified path
  int processFilenameStrlen = _tcslen(processFilename);
  int psIndex;
  for(psIndex = processFilenameStrlen-1; psIndex >= 0; --psIndex) {
    if(processFilename[psIndex] == _T('\\'))
      break;
  }
  _TCHAR *processParentDir = (_TCHAR*) malloc(sizeof(_TCHAR)*(psIndex+1));
  memcpy(processParentDir, processFilename, sizeof(_TCHAR)*(psIndex));
  processParentDir[psIndex] = _T('\0');
  DEBUG(_T("Got fully qualified parent dir: \"%s\"\n"), processParentDir);

  // Resolve the absolute classpath variables
  resolveClasspath(processParentDir);
  
  int returnValue = -2;
  
  if(argc > 1 && _tcscmp(argv[1], _T("-invokeuac")) == 0) {
    /* We do a little process magic here in order to invoke an UAC prompt. */
    DWORD currentWorkingDirectoryLength = GetCurrentDirectory(0, NULL);
    _TCHAR *currentWorkingDirectory = (_TCHAR*) malloc(currentWorkingDirectoryLength);
    
    if(GetCurrentDirectory(currentWorkingDirectoryLength, currentWorkingDirectory) > 0) {
      DEBUG(_T("CWD: \"%s\"\n"), currentWorkingDirectory);
      
      // Build a space separated argv string
      int argvStringLength = 0;
      int i;
      for(i = 2; i < argc; ++i) { argvStringLength += _tcslen(argv[i]) + 3; }
      int argvStringSize = (argvStringLength+1)*sizeof(_TCHAR);
      _TCHAR *argvString = (_TCHAR*) malloc(argvStringSize);
      ZeroMemory(argvString, argvStringSize);
      for(i = 2; i < argc; ++i) {
	_TCHAR *cur = argv[i];
	//int curlen = _tcslen(cur);
	_tcscat(argvString, _T("\""));
	_tcscat(argvString, cur);
	_tcscat(argvString, _T("\" "));
      }
      argvString[argvStringLength-1] = _T('\0');
      DEBUG(_T("argvString=\"%s\"\n"), argvString);

      SHELLEXECUTEINFO execInfo;
      memset(&execInfo, 0, sizeof(SHELLEXECUTEINFO));
      execInfo.cbSize = sizeof(SHELLEXECUTEINFO);
      execInfo.fMask = SEE_MASK_UNICODE;
      execInfo.hwnd = NULL;
      execInfo.lpVerb = _T("runas");
      execInfo.lpFile = argv[0];
      execInfo.lpParameters = argvString;
      execInfo.lpDirectory = currentWorkingDirectory;
      execInfo.nShow = SW_SHOW;
      execInfo.hInstApp = NULL;
      DEBUG(_T("executing ShellExecuteEx...\n"));
      DEBUG(_T("  &execInfo=%d\n"), (int)&execInfo);
      if(ShellExecuteEx(&execInfo)) {
	DEBUG(_T("ShellExecuteEx success!\n"));
      }
      else {
	DEBUG(_T("ShellExecuteEx failed!\n"));
	MessageBox(NULL, _T("Error while trying to create new process..."), _T("HFSExplorer launch error"), MB_OK);
      }
    }
    else {
      DEBUG(_T("Error code: %d\n"), (int)GetLastError());
      MessageBox(NULL, _T("Could not get the current working directory."), _T("HFSExplorer launch error"), MB_OK);
    }
    free(currentWorkingDirectory);
    returnValue = 0;
  }
  else {
    _TCHAR *oldWorkingDir = (_TCHAR*) malloc(MAX_PATH*sizeof(_TCHAR));
    GetCurrentDirectory(MAX_PATH, oldWorkingDir);
    // Fulhack
    if(argc > 1) {
      _TCHAR *fullPathName = (_TCHAR*) malloc(MAX_PATH*sizeof(_TCHAR));
      ZeroMemory(fullPathName, MAX_PATH*sizeof(_TCHAR));
      int charsRead = SearchPath(oldWorkingDir,
				 argv[1],
				 NULL,
				 MAX_PATH,
				 fullPathName,
				 NULL);
      //int charsRead = GetFullPathName(argv[1], MAX_PATH, fullPathName, NULL);
      if(charsRead == 0) {
	DEBUG(_T("could not convert args[1] into pathname. last error: %d\n"), GetLastError());
      }
      else {
	DEBUG(_T("full pathname: \"%s\"\n"), fullPathName);
	argv[1] = fullPathName;
      }
      
    }
    SetCurrentDirectory(processParentDir);
    
    if(createJavaVM() == TRUE) {
      _TCHAR currentWorkingDirectory[MAX_PATH];
      GetCurrentDirectory(MAX_PATH, currentWorkingDirectory);
      DEBUG(_T("Current working dir: \"%s\"\n"), currentWorkingDirectory);
      /* Proceed... */
      JNIEnv *env;
      staticJvm->AttachCurrentThread((void **)&env, NULL);
      DEBUG(_T("    - Initializing FileSystemBrowserWindow\n"));
      jclass cls = env->FindClass(START_CLASS);
      if(env->ExceptionOccurred()) {
	env->ExceptionDescribe();
	DEBUG(_T("    - Exception occurred!\n"));
	MessageBox(NULL, _T("Could not find the required class files!"), _T("HFSExplorer launch error"), MB_OK | MB_ICONERROR);
      }
      
      if(cls != NULL) {
	DEBUG(_T("    - Class found\n"));
	jmethodID mid = env->GetStaticMethodID(cls, "main", "([Ljava/lang/String;)V");
	if(mid != NULL) {
	  DEBUG(_T("    - main method found\n"));
	  jclass stringClass = env->FindClass("java/lang/String");
	  DEBUG(_T("    - got String class\n"));
	
	  // Create array to hold args (cast to jobjectArray is neccessary for g++ to accept code)
	  jobjectArray argsArray = (jobjectArray)env->NewObjectArray(argc, stringClass, NULL);
	  DEBUG(_T("    - created args array\n"));
	  if(argsArray != NULL) {
	    int newi;
	    for(newi = 0; newi < argc; ++newi) {
	      _TCHAR *cur;
	      if(newi == 0) //hack
		cur = FIRSTARG;
	      else
		cur = argv[newi];
	      int curLength = _tcslen(cur);
	      WCHAR *wcCur = NULL;
	      int wcCurLength = -1;
	    
	    
#ifdef _UNICODE
	      wcCur = _tcsdup(cur);
	      wcCurLength = curLength;
#else
	      wcCurLength = MultiByteToWideChar(CP_OEMCP, 0, cur, curLength, NULL, 0);
	      if(wcCurLength == 0) {
		//handleMultiByteToWideCharError(env, GetLastError());
		DEBUG(_T("MultiByteToWideChar (getting length) failed...\n"));
		return -1;
	      }
	      else {
		wcCur = (WCHAR*)malloc(sizeof(WCHAR)*wcCurLength);
		if(MultiByteToWideChar(CP_OEMCP, 0, cur, curLength, wcCur, wcCurLength) == 0) {
		  //handleMultiByteToWideCharError(env, GetLastError());
		  DEBUG(_T("MultiByteToWideChar (converting) failed...\n"));
		  free(wcCur);
		  wcCur = NULL;
		}
	      }
#endif
	    
	      if(wcCur != NULL && wcCurLength != -1) {
		DEBUG(_T("Converting \"%s\" to UTF-8...\n"), wcCur);
		char* utf8String;
		int utf8StringLength = WideCharToMultiByte(CP_UTF8, 0, wcCur, wcCurLength, NULL, 0, NULL, NULL);
		DEBUG(_T("utf8StringLength=%d\n"), utf8StringLength);
		utf8String = (char*) malloc(sizeof(char)*utf8StringLength + 1); // +1, beacause of terminating \0 character
		if(WideCharToMultiByte(CP_UTF8, 0, wcCur, wcCurLength, utf8String, utf8StringLength, NULL, NULL) != 0) {
		  utf8String[utf8StringLength] = '\0';
		  fprintf(stderr, "UTF-8 string as ASCII: \"%s\"\n", utf8String);
		  jstring cur = env->NewStringUTF(utf8String);
		  env->SetObjectArrayElement(argsArray, newi, cur);
		}
		else
		  DEBUG(_T("Failed to convert wcCur (argv[%d]) to UTF-8!\n"), newi);
		free(utf8String);
	      }
	      else
		DEBUG(_T("Failed to convert argv[%d] to WCHAR!\n"), newi);
	      free(wcCur);
	    }
	  
	    DEBUG(_T("    - args array built\n"));
	    env->CallStaticVoidMethod(cls, mid, argsArray);
	    if (env->ExceptionOccurred()) {
	      env->ExceptionDescribe();
	      DEBUG(_T("    - Exception occurred!\n"));
	    }
	    staticJvm->DestroyJavaVM();
	  }
	}
      }
      else {
	DEBUG(_T("    - ERROR. Could not find class\n"));
      }
      FreeLibrary(jvmLib);
      returnValue = 0;
    }
    else if(!DISABLE_JAVA_PROCESS_CREATION && createExternalJavaProcess(argc, argv) == TRUE) {
      returnValue = 0;
    }
    else {
      MessageBox(NULL, _T("No Java Virtual Machine found! Please get one from http://java.sun.com ..."), _T("HFSExplorer launch error"), MB_OK);
      //getchar();
      returnValue = -1;
    }
  }
  free(processFilename);
  return returnValue;
}
