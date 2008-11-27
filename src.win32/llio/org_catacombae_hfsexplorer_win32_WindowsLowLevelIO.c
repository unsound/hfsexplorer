/*-
 * Copyright (C) 2006 Erik Larsson
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
#define _WIN32_WINNT 0x0500 // Minimum windows 2000 required, because of GetFileSizeEx.
#include <windows.h>
#include <winioctl.h>
#include <tchar.h>
#include "org_catacombae_hfsexplorer_win32_WindowsLowLevelIO.h"
#include "llio_common.h"
#define DEBUG FALSE

/*
 * Class:     org_catacombae_hfsexplorer_win32_WindowsLowLevelIO
 * Method:    openNative
 * Signature: (Ljava/lang/String;)[B
 */
JNIEXPORT jbyteArray JNICALL Java_org_catacombae_hfsexplorer_win32_WindowsLowLevelIO_openNative(JNIEnv *env, jclass cls, jstring str) {
  if(DEBUG) printf("Java_org_catacombae_hfsexplorer_win32_WindowsLowLevelIO_openNative called\n");
  return openWin32File(env, str, GENERIC_READ, (FILE_SHARE_READ | FILE_SHARE_WRITE), 
		       NULL, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL);
}

/*
 * Class:     org_catacombae_hfsexplorer_win32_WindowsLowLevelIO
 * Method:    seek
 * Signature: (J[B)V
 */
JNIEXPORT void JNICALL Java_org_catacombae_hfsexplorer_win32_WindowsLowLevelIO_seek(JNIEnv *env, jclass cls, jlong pos, jbyteArray handleData) {
  // Microsoft's old-school C compiler forces me to go C90 strict. Hopefully MinGW GCC will be 64-bit soon.
  HANDLE hnd;
  LARGE_INTEGER oldFP, newFP;
  
  if(DEBUG) printf("Java_WindowsLowLevelIO_seek called\n");
  hnd = getHandle(env, handleData);
  oldFP.QuadPart = pos;
  if(SetFilePointerEx(hnd, oldFP, &newFP, FILE_BEGIN) == FALSE)
    throwByName(env, "java/lang/RuntimeException", "Couldn't set file pointer.");
}

/*
 * Class:     org_catacombae_hfsexplorer_win32_WindowsLowLevelIO
 * Method:    read
 * Signature: ([BII[B)I
 */
JNIEXPORT jint JNICALL Java_org_catacombae_hfsexplorer_win32_WindowsLowLevelIO_read(JNIEnv *env, jclass cls, jbyteArray data, 
						   jint offset, jint length, jbyteArray handleData) {
  // Microsoft's old-school C compiler forces me to go C90 strict. Hopefully MinGW GCC will be 64-bit soon.
  HANDLE hnd;
  BYTE *buffer;
  DWORD bytesRead;
  
  //_tprintf(_T("Java_WindowsLowLevelIO_read called\n"));  
  if(DEBUG) printf("Java_WindowsLowLevelIO_read called\n");
  hnd = getHandle(env, handleData);
  buffer = (BYTE*)malloc(length);
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

/*
 * Class:     org_catacombae_hfsexplorer_win32_WindowsLowLevelIO
 * Method:    close
 * Signature: ([B)V
 */
JNIEXPORT void JNICALL Java_org_catacombae_hfsexplorer_win32_WindowsLowLevelIO_close(JNIEnv *env, jclass cls, jbyteArray handleData) {
  // Microsoft's old-school C compiler forces me to go C90 strict. Hopefully MinGW GCC will be 64-bit soon.
  HANDLE hnd;
  
  hnd = getHandle(env, handleData);
  if(CloseHandle(hnd) == FALSE) {
    throwByName(env, "java/lang/RuntimeException", "Could not close file.");
  }
}

/*
 * Class:     org_catacombae_hfsexplorer_win32_WindowsLowLevelIO
 * Method:    ejectMedia
 * Signature: ([B)V
 */
JNIEXPORT void JNICALL Java_org_catacombae_hfsexplorer_win32_WindowsLowLevelIO_ejectMedia(JNIEnv *env, jclass cls, jbyteArray handleData) {
  // Microsoft's old-school C compiler forces me to go C90 strict. Hopefully MinGW GCC will be 64-bit soon.
  HANDLE hnd;
  DWORD bytesReturned;
  
  hnd = getHandle(env, handleData);
  if(DeviceIoControl(hnd, IOCTL_STORAGE_EJECT_MEDIA, NULL, 0, NULL, 0, &bytesReturned, NULL) == FALSE)
    throwByName(env, "java/lang/RuntimeException", "Could not eject media.");  
}

/*
 * Class:     org_catacombae_hfsexplorer_win32_WindowsLowLevelIO
 * Method:    loadMedia
 * Signature: ([B)V
 */
JNIEXPORT void JNICALL Java_org_catacombae_hfsexplorer_win32_WindowsLowLevelIO_loadMedia(JNIEnv *env, jclass cls, jbyteArray handleData) {
  // Microsoft's old-school C compiler forces me to go C90 strict. Hopefully MinGW GCC will be 64-bit soon.
  HANDLE hnd;
  DWORD bytesReturned;
  
  hnd = getHandle(env, handleData);
  if(DeviceIoControl(hnd, IOCTL_STORAGE_LOAD_MEDIA, NULL, 0, NULL, 0, &bytesReturned, NULL) == FALSE)
    throwByName(env, "java/lang/RuntimeException", "Could not load media.");  
}

/*
 * Class:     WindowsLowLevelIO
 * Method:    length
 * Signature: ([B)J
 */
JNIEXPORT jlong JNICALL Java_org_catacombae_hfsexplorer_win32_WindowsLowLevelIO_length(JNIEnv *env, jclass cls, jbyteArray handleData) {
  // Microsoft's old-school C compiler forces me to go C90 strict. Hopefully MinGW GCC will be 64-bit soon.
  HANDLE hnd;
  LARGE_INTEGER length;
  
  hnd = getHandle(env, handleData);
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
  // Microsoft's old-school C compiler forces me to go C90 strict. Hopefully MinGW GCC will be 64-bit soon.
  HANDLE hnd;
  LARGE_INTEGER distance, fp;
  
  distance.QuadPart = 0;
  hnd = getHandle(env, handleData);
  
  if(SetFilePointerEx(hnd, distance, &fp, FILE_CURRENT) == FALSE) {
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
  // Microsoft's old-school C compiler forces me to go C90 strict. Hopefully MinGW GCC will be 64-bit soon.
  HANDLE hnd;
  DISK_GEOMETRY_EX geom;
  DWORD bytesReturned;

  hnd = getHandle(env, handleData);
  if(DeviceIoControl(hnd, IOCTL_DISK_GET_DRIVE_GEOMETRY_EX, NULL, 0, &geom, sizeof(DISK_GEOMETRY_EX), &bytesReturned, NULL) == FALSE)
    return -1;
  else
    return geom.Geometry.BytesPerSector;
}
