/*-
 * Copyright (C) 2006 Erik Larsson
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 */

#define UNICODE
#define _UNICODE
#define _WIN32_WINNT 0x0500 // Minimum windows 2000 required, beacuse of GetFileSizeEx. (could be done in a more compatible way, but why care about legacy crap?)
#include <windows.h>
#include <winioctl.h>
#include <tchar.h>
#include "org_catacombae_hfsexplorer_win32_WindowsLowLevelIO.h"
#include "llio_common.h"
#define DEBUG FALSE

JNIEXPORT jbyteArray JNICALL Java_org_catacombae_hfsexplorer_win32_WindowsLowLevelIO_openNative(JNIEnv *env, jclass cls, jstring str) {
  if(DEBUG) printf("Java_WindowsLowLevelIO_openNative called\n");
  if(str == NULL) { // Must check input, or we can crash the jvm.
    throwByName(env, "java/lang/NullPointerException", "Filename is null.");
    return 0;
  }
  
  /* First, we convert the jstring to a jbyte array with the string encoded
     into UTF-8. */
  jsize utf8FilenameLength = (*env)->GetStringUTFLength(env, str);
  const jbyte *utf8Filename = (*env)->GetStringUTFChars(env, str, NULL);
  if(DEBUG) printf("utf8FilenameLength: %d bytes\n", utf8FilenameLength);

  
  /* Then, we convert the UTF-8 string into windows WCHARs */
  int wcFilenameSize = MultiByteToWideChar(CP_UTF8, 0, utf8Filename, utf8FilenameLength, NULL, 0);
  if(wcFilenameSize == 0) {
    handleMultiByteToWideCharError(env, GetLastError());
    return 0;
  }
  ++wcFilenameSize; // the last WCHAR is the null terminator
  if(DEBUG) printf("wcFilenameSize: %d\n", wcFilenameSize);
  WCHAR *wcFilename = (WCHAR*)malloc(sizeof(WCHAR)*wcFilenameSize);
  if(MultiByteToWideChar(CP_UTF8, 0, utf8Filename, utf8FilenameLength, wcFilename, wcFilenameSize) == 0) {
    handleMultiByteToWideCharError(env, GetLastError());
    free(wcFilename);
    return 0;
  }
  wcFilename[wcFilenameSize-1] = L'\0'; // Null termination.
  (*env)->ReleaseStringUTFChars(env, str, utf8Filename); // Release allocated resources.
  
  
  /* Perfect. */
  if(DEBUG) printf("Attempting to open \"");
  if(DEBUG) wprintf(L"%s", wcFilename);
  if(DEBUG) printf("\"\n");
  HANDLE hnd = CreateFileW(wcFilename, GENERIC_READ, (FILE_SHARE_READ | FILE_SHARE_WRITE), 
			   NULL, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL);
  free(wcFilename); // Done with that one now
  if(hnd == INVALID_HANDLE_VALUE) {
    throwByName(env, "java/lang/RuntimeException", "Could not open file.");
    return 0;
  }
  else {
    return getHandleData(env, hnd);
  }
}
JNIEXPORT void JNICALL Java_org_catacombae_hfsexplorer_win32_WindowsLowLevelIO_seek(JNIEnv *env, jclass cls, jlong pos, jbyteArray handleData) {
  if(DEBUG) printf("Java_WindowsLowLevelIO_seek called\n");
  HANDLE hnd = getHandle(env, handleData);
  LARGE_INTEGER oldFP = (LARGE_INTEGER)pos;
  LARGE_INTEGER newFP;
  if(SetFilePointerEx(hnd, oldFP, &newFP, FILE_BEGIN) == FALSE)
    throwByName(env, "java/lang/RuntimeException", "Couldn't set file pointer.");
}
JNIEXPORT jint JNICALL Java_org_catacombae_hfsexplorer_win32_WindowsLowLevelIO_read(JNIEnv *env, jclass cls, jbyteArray data, 
						   jint offset, jint length, jbyteArray handleData) {
  //_tprintf(_T("Java_WindowsLowLevelIO_read called\n"));  
  if(DEBUG) printf("Java_WindowsLowLevelIO_read called\n");
  HANDLE hnd = getHandle(env, handleData);
  BYTE* buffer = (BYTE*)malloc(length);
  DWORD bytesRead;
  //printf("Calling: ReadFile(0x%x, 0x%x, %d, 0x%x, NULL);\n", hnd, buffer, length, &bytesRead);
  if(ReadFile(hnd, buffer, length, &bytesRead, NULL) == FALSE) {
    //printf("bytesRead: %u\n", (int)bytesRead);
    //printf("GetLastError(): %u\n", (int)GetLastError());
    throwByName(env, "java/lang/RuntimeException", "Couldn't read from file.");
    bytesRead = -1;
  }
  else
    (*env)->SetByteArrayRegion(env, data, offset, bytesRead, buffer);
  free(buffer); // Clean up
  return bytesRead;
}

JNIEXPORT void JNICALL Java_org_catacombae_hfsexplorer_win32_WindowsLowLevelIO_close(JNIEnv *env, jclass cls, jbyteArray handleData) {
  HANDLE hnd = getHandle(env, handleData);
  if(CloseHandle(hnd) == FALSE) {
    throwByName(env, "java/lang/RuntimeException", "Could not close file.");
  }
}

JNIEXPORT void JNICALL Java_org_catacombae_hfsexplorer_win32_WindowsLowLevelIO_ejectMedia(JNIEnv *env, jclass cls, jbyteArray handleData) {
  HANDLE hnd = getHandle(env, handleData);
  DWORD bytesReturned;
  if(DeviceIoControl(hnd, IOCTL_STORAGE_EJECT_MEDIA, NULL, 0, NULL, 0, &bytesReturned, NULL) == FALSE)
    throwByName(env, "java/lang/RuntimeException", "Could not eject media.");  
}

JNIEXPORT void JNICALL Java_org_catacombae_hfsexplorer_win32_WindowsLowLevelIO_loadMedia(JNIEnv *env, jclass cls, jbyteArray handleData) {
  HANDLE hnd = getHandle(env, handleData);
  DWORD bytesReturned;
  if(DeviceIoControl(hnd, IOCTL_STORAGE_LOAD_MEDIA, NULL, 0, NULL, 0, &bytesReturned, NULL) == FALSE)
    throwByName(env, "java/lang/RuntimeException", "Could not eject media.");
  
}

/*
 * Class:     WindowsLowLevelIO
 * Method:    length
 * Signature: ([B)J
 */
JNIEXPORT jlong JNICALL Java_org_catacombae_hfsexplorer_win32_WindowsLowLevelIO_length(JNIEnv *env, jclass cls, jbyteArray handleData) {
  HANDLE hnd = getHandle(env, handleData);
  LARGE_INTEGER length;
  if(GetFileSizeEx(hnd, &length) == FALSE) {
    GET_LENGTH_INFORMATION info;
    DWORD bytesReturned;
    if(DeviceIoControl(hnd, IOCTL_DISK_GET_LENGTH_INFO, NULL, 0, &info, sizeof(GET_LENGTH_INFORMATION), &bytesReturned, NULL) == FALSE) {
      throwByName(env, "java/lang/RuntimeException", "Could not get file size.");
      return -1;
    }
    else {
      if(bytesReturned != sizeof(GET_LENGTH_INFORMATION))
	fprintf(stderr, "WARNING: Expected %u bytes in return from DeviceIoControl, got %u.\n", sizeof(GET_LENGTH_INFORMATION), (int)bytesReturned);
      return (jlong)(info.Length.QuadPart);
    }
  }
  else
    return (jlong)(length.QuadPart);
}

/*
 * Class:     WindowsLowLevelIO
 * Method:    getFilePointer
 * Signature: ([B)J
 */
JNIEXPORT jlong JNICALL Java_org_catacombae_hfsexplorer_win32_WindowsLowLevelIO_getFilePointer(JNIEnv *env, jclass cls, jbyteArray handleData) {
  HANDLE hnd = getHandle(env, handleData);
  LARGE_INTEGER fp;
  //LARGE_INTEGER pos = (LONGLONG)0;
  if(SetFilePointerEx(hnd, (LARGE_INTEGER)((LONGLONG)0), &fp, FILE_CURRENT) == FALSE) {
    throwByName(env, "java/lang/RuntimeException", "Could not get file pointer!");
    return -1;
  }
  else
    return (jlong)(fp.QuadPart);
}

/*
 * Class:     WindowsLowLevelIO
 * Method:    getSectorSize
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL Java_org_catacombae_hfsexplorer_win32_WindowsLowLevelIO_getSectorSize(JNIEnv *env, jclass cls, jbyteArray handleData) {
  HANDLE hnd = getHandle(env, handleData);
  DISK_GEOMETRY_EX geom;
  DWORD bytesReturned;
  if(DeviceIoControl(hnd, IOCTL_DISK_GET_DRIVE_GEOMETRY_EX, NULL, 0, &geom, sizeof(DISK_GEOMETRY_EX), &bytesReturned, NULL) == FALSE)
    return -1;
  else
    return geom.Geometry.BytesPerSector;
}
