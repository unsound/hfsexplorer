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

#define UNICODE
#define _UNICODE

#include "llio_common.h"
#define DEBUG FALSE

/* Ripped from the tutorial at "http://java.sun.com/docs/books/jni/html/".
   Makes it a little bit easier to throw java exceptions from C code. */
void throwByName(JNIEnv *env, const char *name, const char *msg) {
  jclass cls = (*env)->FindClass(env, name);
  /* if cls is NULL, an exception has already been thrown */
  if (cls != NULL) {
    (*env)->ThrowNew(env, cls, msg);
  }
  /* free the local ref */
  (*env)->DeleteLocalRef(env, cls);
}

/* Takes care of parsing the error after a call to MultiByteToWideChar
   as well as converting it to a java exception with an appropriate
   error message. */
void handleMultiByteToWideCharError(JNIEnv *env, DWORD errorCode) {
  char* message;
  DWORD error = GetLastError();
  if(error == ERROR_INSUFFICIENT_BUFFER)
    message = "MultiByteToWideChar says: Insufficient buffer space.";
  else if(error == ERROR_INVALID_FLAGS)
    message = "MultiByteToWideChar says: Invalid flags.";
  else if(error == ERROR_INVALID_PARAMETER)
    message = "MultiByteToWideChar says: Invalid parameters.";
  else if(error == ERROR_NO_UNICODE_TRANSLATION)
    message = "MultiByteToWideChar says: No Unicode translation.";
  else
    message = "Unknown MultiByteToWideChar error!";
  throwByName(env, "java/lang/RuntimeException", message);
}

/* Stores down the data in the HANDLE pointer to a java byte array. The
   endianness of the byte array will be platform dependent, but it
   shouldn't matter as it will never be transfered between platforms.
   The size of the byte array will be equal to sizeof(HANDLE). */
jbyteArray getHandleData(JNIEnv *env, HANDLE hnd) {
  jbyteArray result = (*env)->NewByteArray(env, sizeof(HANDLE)); // I *think* this returns a local reference, so I won't have to free it manually.
  jbyte byteArray[sizeof(HANDLE)];
  int i;
  BYTE *rawHnd = (BYTE*)&hnd;
  for(i = 0; i < sizeof(HANDLE); ++i) {
    byteArray[i] = rawHnd[i];
  }
  (*env)->SetByteArrayRegion(env, result, 0, sizeof(HANDLE), byteArray);
  return result;
}

/* Reads out a HANDLE pointer from a java byte array (which must have a
   size equal to sizeof(HANDLE) ). */
HANDLE getHandle(JNIEnv *env, jbyteArray handleData) {
  jsize handleDataLength = (*env)->GetArrayLength(env, handleData);
  if(handleDataLength != sizeof(HANDLE))
    return INVALID_HANDLE_VALUE;
  else {
    //BYTE rawData[sizeof(HANDLE)];
    HANDLE hnd;
    BYTE* rawHnd = (BYTE*)&hnd;
    (*env)->GetByteArrayRegion(env, handleData, 0, sizeof(HANDLE), rawHnd);
    //HANDLE h = (HANDLE)rawData;
    return hnd;
  }
}

/*
 * Common code for Java_org_catacombae_hfsexplorer_win32_WritableWin32File_openNative
 * and Java_org_catacombae_hfsexplorer_win32_WindowsLowLevelIO_openNative.
 */
jbyteArray openWin32File(JNIEnv *env,
			 jstring str,
			 DWORD dwDesiredAccess,
			 DWORD dwShareMode,
			 LPSECURITY_ATTRIBUTES lpSecurityAttributes,
			 DWORD dwCreationDisposition,
			 DWORD dwFlagsAndAttributes,
			 HANDLE hTemplateFile) {
  // Microsoft's old-school C compiler forces me to go C90 strict. Hopefully MinGW GCC will be 64-bit soon.
  jsize utf8FilenameLength;
  const jbyte *utf8Filename;
  int wcFilenameSize;
  WCHAR *wcFilename;
  HANDLE hnd;
  
  if(DEBUG) printf("openWin32File called\n");
  if(str == NULL) { // Must check input, or we can crash the jvm.
    throwByName(env, "java/lang/NullPointerException", "Filename is null.");
    return 0;
  }
  
  /* First, we convert the jstring to a jbyte array with the string encoded
     into UTF-8. */
  utf8FilenameLength = (*env)->GetStringUTFLength(env, str);
  utf8Filename = (*env)->GetStringUTFChars(env, str, NULL);
  if(DEBUG) printf("utf8FilenameLength: %d bytes\n", utf8FilenameLength);

  
  /* Then, we convert the UTF-8 string into windows WCHARs */
  wcFilenameSize = MultiByteToWideChar(CP_UTF8, 0, utf8Filename, utf8FilenameLength, NULL, 0);
  if(wcFilenameSize == 0) {
    handleMultiByteToWideCharError(env, GetLastError());
    (*env)->ReleaseStringUTFChars(env, str, utf8Filename);
    return 0;
  }
  ++wcFilenameSize; // the last WCHAR is the null terminator
  if(DEBUG) printf("wcFilenameSize: %d\n", wcFilenameSize);
  wcFilename = (WCHAR*)malloc(sizeof(WCHAR)*wcFilenameSize);
  if(MultiByteToWideChar(CP_UTF8, 0, utf8Filename, utf8FilenameLength, wcFilename, wcFilenameSize) == 0) {
    handleMultiByteToWideCharError(env, GetLastError());
    (*env)->ReleaseStringUTFChars(env, str, utf8Filename);
    free(wcFilename);
    return 0;
  }
  wcFilename[wcFilenameSize-1] = L'\0'; // Null termination.
  (*env)->ReleaseStringUTFChars(env, str, utf8Filename); // Release allocated resources.
  
  
  /* Perfect. */
  if(DEBUG) printf("Attempting to open \"");
  if(DEBUG) wprintf(L"%s", wcFilename);
  if(DEBUG) printf("\"\n");
  hnd = CreateFileW(wcFilename, dwDesiredAccess, dwShareMode, lpSecurityAttributes, 
		    dwCreationDisposition, dwFlagsAndAttributes, hTemplateFile);
  free(wcFilename); // Done with that one now
  if(hnd == INVALID_HANDLE_VALUE) {
    throwByName(env, "java/lang/RuntimeException", "Could not open file.");
    return 0;
  }
  else {
    return getHandleData(env, hnd);
  }  
}

