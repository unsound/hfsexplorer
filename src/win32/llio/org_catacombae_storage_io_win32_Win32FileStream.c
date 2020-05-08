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
#define _CRT_SECURE_NO_WARNINGS

/* Minimum Windows 2000 SDK required. */
#define _WIN32_WINNT 0x0500
#include <windows.h>
#include <winioctl.h>
#include <tchar.h>
#include "org_catacombae_storage_io_win32_Win32FileStream.h"
#include "llio_common.h"
#define DEBUG FALSE

/*
 * Class:     org_catacombae_catacombae_storage_io_win32_Win32FileStream
 * Method:    openNative
 * Signature: (Ljava/lang/String;)[B
 */
JNIEXPORT jbyteArray JNICALL Java_org_catacombae_catacombae_storage_io_win32_Win32FileStream_openNative(JNIEnv *env, jclass cls, jstring str) {
  if(DEBUG) printf("Java_org_catacombae_catacombae_storage_io_win32_Win32FileStream_openNative called\n");
  return openWin32File(env, str, (GENERIC_READ | GENERIC_WRITE), FILE_SHARE_READ,
		       NULL, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL);
}

/*
 * Class:     org_catacombae_catacombae_storage_io_win32_Win32FileStream
 * Method:    write
 * Signature: ([BII[B)V
 */
JNIEXPORT void JNICALL Java_org_catacombae_catacombae_storage_io_win32_Win32FileStream_write(JNIEnv *env, jclass cls, jbyteArray data, jint offset, jint length, jbyteArray handleData) {
  // Microsoft's old-school C compiler forces me to go C90 strict. Hopefully MinGW GCC will be 64-bit soon.
  HANDLE hnd;
  LARGE_INTEGER position;
  BYTE *buffer;
  DWORD bytesWritten = 0;

  //_tprintf(_T("Java_WindowsLowLevelIO_read called\n"));
  if(DEBUG) printf("Java_WritableWin32File_write called\n");

  hnd = getHandle(env, handleData);

  position.QuadPart = 0;
  position.LowPart = SetFilePointer(
    /* HANDLE hFile */
    hnd,
    /* LONG lDistanceToMove */
    position.LowPart,
    /* PLONG lpDistanceToMoveHigh */
    &position.HighPart,
    /* DWORD dwMoveMethod */
    FILE_CURRENT);
  if(position.LowPart == INVALID_SET_FILE_POINTER &&
    GetLastError() != NO_ERROR)
  {
    position.QuadPart = 0x7FFFFFFFFFFFFFFFLL;
  }

  buffer = (BYTE*)malloc(length);
  if(!buffer) {
    throwByName(env, "java/lang/RuntimeException",
                "Error %d (%s) while allocating %d-byte temporary buffer for "
                "writing.",
                errno, strerror(errno), length);
    return;
  }

  (*env)->GetByteArrayRegion(env, data, offset, length, buffer); // (jbyteArray)data->(BYTE*)buffer
  //printf("Calling: ReadFile(0x%x, 0x%x, %d, 0x%x, NULL);\n", hnd, buffer, length, &bytesRead);
  if(WriteFile(hnd, buffer, length, &bytesWritten, NULL) == FALSE) {
    //printf("bytesRead: %u\n", (int)bytesRead);
    //printf("GetLastError(): %u\n", (int)GetLastError());
    throwByName(env, "java/lang/RuntimeException",
                "Error 0x%08X while attempting to write %d bytes to position "
                "%lld in file (wrote %lu bytes).",
                GetLastError(), length, (long long) position.QuadPart,
                (unsigned long) bytesWritten);
    //bytesRead = -1;
  }
  else if(bytesWritten != length)
    throwByName(env, "java/lang/RuntimeException",
                "Could not write entire buffer to file! Managed to write %lu / "
                "%d bytes.",
                (unsigned long) bytesWritten, length);

  free(buffer); // Clean up
}
