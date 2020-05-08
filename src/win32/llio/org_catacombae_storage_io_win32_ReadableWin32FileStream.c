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
#define _CRT_SECURE_NO_WARNINGS

/* Minimum Windows 2000 SDK required. */
#define _WIN32_WINNT 0x0500
#include <windows.h>
#include <winioctl.h>
#include <tchar.h>
#include "org_catacombae_storage_io_win32_ReadableWin32FileStream.h"
#include "llio_common.h"
#define DEBUG FALSE

/*
 * Class:     org_catacombae_storage_io_win32_ReadableWin32FileStream
 * Method:    openNative
 * Signature: (Ljava/lang/String;)[B
 */
JNIEXPORT jbyteArray JNICALL Java_org_catacombae_storage_io_win32_ReadableWin32FileStream_openNative(JNIEnv *env, jclass cls, jstring str) {
  if(DEBUG) printf("Java_org_catacombae_storage_io_win32_ReadableWin32FileStream_openNative called\n");
  return openWin32File(env, str, GENERIC_READ, (FILE_SHARE_READ | FILE_SHARE_WRITE),
		       NULL, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL);
}

/*
 * Class:     org_catacombae_storage_io_win32_ReadableWin32FileStream
 * Method:    seek
 * Signature: (J[B)V
 */
JNIEXPORT void JNICALL Java_org_catacombae_storage_io_win32_ReadableWin32FileStream_seek(JNIEnv *env, jclass cls, jlong pos, jbyteArray handleData) {
  // Microsoft's old-school C compiler forces me to go C90 strict. Hopefully MinGW GCC will be 64-bit soon.
  HANDLE hnd;
  LARGE_INTEGER newFP;

  if(DEBUG) printf("Java_WindowsLowLevelIO_seek called\n");
  hnd = getHandle(env, handleData);
  newFP.QuadPart = pos;
  if(SetFilePointer(hnd, newFP.LowPart, &newFP.HighPart, FILE_BEGIN) ==
    INVALID_SET_FILE_POINTER && GetLastError() != NO_ERROR)
  {
    throwByName(env, "java/lang/RuntimeException",
                "Error 0x%08X while attempting to set file pointer to %lld.",
                GetLastError(), (long long) pos);
  }
}

/*
 * Class:     org_catacombae_storage_io_win32_ReadableWin32FileStream
 * Method:    read
 * Signature: ([BII[B)I
 */
JNIEXPORT jint JNICALL Java_org_catacombae_storage_io_win32_ReadableWin32FileStream_read(JNIEnv *env, jclass cls, jbyteArray data,
						   jint offset, jint length, jbyteArray handleData) {
  // Microsoft's old-school C compiler forces me to go C90 strict. Hopefully MinGW GCC will be 64-bit soon.
  HANDLE hnd;
  LARGE_INTEGER position;
  BYTE *buffer;
  DWORD bytesRead = 0;

  //_tprintf(_T("Java_WindowsLowLevelIO_read called\n"));
  if(DEBUG) printf("Java_WindowsLowLevelIO_read called\n");
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
                "reading.",
                errno, strerror(errno), length);
    return -1;
  }

  //printf("Calling: ReadFile(0x%x, 0x%x, %d, 0x%x, NULL);\n", hnd, buffer, length, &bytesRead);
  if(ReadFile(hnd, buffer, length, &bytesRead, NULL) == FALSE) {
    //printf("bytesRead: %u\n", (int)bytesRead);
    //printf("GetLastError(): %u\n", (int)GetLastError());
    throwByName(env, "java/lang/RuntimeException",
                "Error 0x%08X while attempting to read %d bytes from position "
                "%lld in file (read %lu bytes).",
                GetLastError(), length, (long long) position.QuadPart,
                (unsigned long) bytesRead);
    bytesRead = -1;
  }
  else
    (*env)->SetByteArrayRegion(env, data, offset, bytesRead, buffer);
  free(buffer); // Clean up
  return bytesRead;
}

/*
 * Class:     org_catacombae_storage_io_win32_ReadableWin32FileStream
 * Method:    close
 * Signature: ([B)V
 */
JNIEXPORT void JNICALL Java_org_catacombae_storage_io_win32_ReadableWin32FileStream_close(JNIEnv *env, jclass cls, jbyteArray handleData) {
  // Microsoft's old-school C compiler forces me to go C90 strict. Hopefully MinGW GCC will be 64-bit soon.
  HANDLE hnd;

  hnd = getHandle(env, handleData);
  if(CloseHandle(hnd) == FALSE) {
    throwByName(env, "java/lang/RuntimeException",
                "Error 0x%08X while closing file.",
                GetLastError());
  }
}

/*
 * Class:     org_catacombae_storage_io_win32_ReadableWin32FileStream
 * Method:    ejectMedia
 * Signature: ([B)V
 */
JNIEXPORT void JNICALL Java_org_catacombae_storage_io_win32_ReadableWin32FileStream_ejectMedia(JNIEnv *env, jclass cls, jbyteArray handleData) {
  // Microsoft's old-school C compiler forces me to go C90 strict. Hopefully MinGW GCC will be 64-bit soon.
  HANDLE hnd;
  DWORD bytesReturned;

  hnd = getHandle(env, handleData);
  if(DeviceIoControl(hnd, IOCTL_STORAGE_EJECT_MEDIA, NULL, 0, NULL, 0, &bytesReturned, NULL) == FALSE)
    throwByName(env, "java/lang/RuntimeException",
                "Error 0x%08X while attempting to eject media.",
                GetLastError());
}

/*
 * Class:     org_catacombae_storage_io_win32_ReadableWin32FileStream
 * Method:    loadMedia
 * Signature: ([B)V
 */
JNIEXPORT void JNICALL Java_org_catacombae_storage_io_win32_ReadableWin32FileStream_loadMedia(JNIEnv *env, jclass cls, jbyteArray handleData) {
  // Microsoft's old-school C compiler forces me to go C90 strict. Hopefully MinGW GCC will be 64-bit soon.
  HANDLE hnd;
  DWORD bytesReturned;

  hnd = getHandle(env, handleData);
  if(DeviceIoControl(hnd, IOCTL_STORAGE_LOAD_MEDIA, NULL, 0, NULL, 0, &bytesReturned, NULL) == FALSE)
    throwByName(env, "java/lang/RuntimeException",
                "Error 0x%08X while attempting to load media.",
                GetLastError());
}

/*
 * Class:     WindowsLowLevelIO
 * Method:    length
 * Signature: ([B)J
 */
JNIEXPORT jlong JNICALL Java_org_catacombae_storage_io_win32_ReadableWin32FileStream_length(JNIEnv *env, jclass cls, jbyteArray handleData) {
  // Microsoft's old-school C compiler forces me to go C90 strict. Hopefully MinGW GCC will be 64-bit soon.
  HANDLE hnd;
  BY_HANDLE_FILE_INFORMATION fi;

  hnd = getHandle(env, handleData);

  if(GetFileInformationByHandle(
    /* HANDLE hFile */
    hnd,
    /* LPBY_HANDLE_FILE_INFORMATION lpFileInformation */
    &fi) == FALSE)
  {
    GET_LENGTH_INFORMATION info;
    PARTITION_INFORMATION partition_info;
    DWORD bytesReturned;
    if(DeviceIoControl(
      /* __in        HANDLE hDevice */
      hnd,
      /* __in        DWORD dwIoControlCode */
      IOCTL_DISK_GET_LENGTH_INFO,
      /* __in_bcount_opt(nInBufferSize) LPVOID lpInBuffer */
      NULL,
      /* __in        DWORD nInBufferSize */
      0,
      /* __out_bcount_part_opt(nOutBufferSize, *lpBytesReturned)
       * LPVOID lpOutBuffer */
      &info,
      /* __in        DWORD nOutBufferSize */
      sizeof(GET_LENGTH_INFORMATION),
      /* __out_opt   LPDWORD lpBytesReturned */
      &bytesReturned,
      /* __inout_opt LPOVERLAPPED lpOverlapped */
      NULL) != FALSE)
    {
      if(bytesReturned != sizeof(GET_LENGTH_INFORMATION)) {
        fprintf(stderr, "WARNING: Expected %u bytes in return from "
            "DeviceIoControl, got %u.\n",
            sizeof(GET_LENGTH_INFORMATION), (int) bytesReturned);
      }

      fprintf(stderr, "Got partition sector count with "
          "IOCTL_DISK_GET_LENGTH_INFO: %lld\n",
          info.Length.QuadPart);
      return (jlong) (info.Length.QuadPart);
    }
    else if(DeviceIoControl(
      /* __in        HANDLE hDevice */
      hnd,
      /* __in        DWORD dwIoControlCode */
      IOCTL_DISK_GET_PARTITION_INFO,
      /* __in_bcount_opt(nInBufferSize) LPVOID lpInBuffer */
      NULL,
      /* __in        DWORD nInBufferSize */
      0,
      /* __out_bcount_part_opt(nOutBufferSize, *lpBytesReturned)
       * LPVOID lpOutBuffer */
      &partition_info,
      /* __in        DWORD nOutBufferSize */
      sizeof(PARTITION_INFORMATION),
      /* __out_opt   LPDWORD lpBytesReturned */
      &bytesReturned,
      /* __inout_opt LPOVERLAPPED lpOverlapped */
      NULL) != FALSE)
    {
      if(bytesReturned != sizeof(PARTITION_INFORMATION)) {
        fprintf(stderr, "WARNING: Expected %u bytes in return from "
            "DeviceIoControl, got %u.\n",
            sizeof(PARTITION_INFORMATION), (int) bytesReturned);
      }

      fprintf(stderr, "Got partition sector count with "
          "IOCTL_DISK_GET_PARTITION_INFO: %lld\n",
          partition_info.PartitionLength.QuadPart);
      return (jlong) (partition_info.PartitionLength.QuadPart);
    }
    else {
      throwByName(env, "java/lang/RuntimeException",
                  "Error 0x%08X while attempting to get file size.",
                  GetLastError());
      return -1;
    }
  }
  else {
    return (jlong)((((jlong) fi.nFileSizeHigh) << 32) | fi.nFileSizeLow);
  }
}

/*
 * Class:     WindowsLowLevelIO
 * Method:    getFilePointer
 * Signature: ([B)J
 */
JNIEXPORT jlong JNICALL Java_org_catacombae_storage_io_win32_ReadableWin32FileStream_getFilePointer(JNIEnv *env, jclass cls, jbyteArray handleData) {
  // Microsoft's old-school C compiler forces me to go C90 strict. Hopefully MinGW GCC will be 64-bit soon.
  HANDLE hnd;
  LARGE_INTEGER fp;

  fp.QuadPart = 0;
  hnd = getHandle(env, handleData);

  fp.LowPart = SetFilePointer(
    /* HANDLE hFile */
    hnd,
    /* LONG lDistanceToMove */
    fp.LowPart,
    /* PLONG lpDistanceToMoveHigh */
    &fp.HighPart,
    /* DWORD dwMoveMethod */
    FILE_CURRENT);
  if(fp.LowPart == INVALID_SET_FILE_POINTER &&
    GetLastError() != NO_ERROR)
  {
    throwByName(env, "java/lang/RuntimeException",
                "Error 0x%08X while attempting to get file pointer!",
                GetLastError());
    return -1;
  }
  else {
    return (jlong)(fp.QuadPart);
  }
}

/*
 * Class:     WindowsLowLevelIO
 * Method:    getSectorSize
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL Java_org_catacombae_storage_io_win32_ReadableWin32FileStream_getSectorSize(JNIEnv *env, jclass cls, jbyteArray handleData) {
  // Microsoft's old-school C compiler forces me to go C90 strict. Hopefully MinGW GCC will be 64-bit soon.
  HANDLE hnd;
  DISK_GEOMETRY_EX geom;
  DISK_GEOMETRY geom_legacy;
  DWORD bytesReturned;

  hnd = getHandle(env, handleData);
  if(DeviceIoControl(
    /* __in        HANDLE hDevice */
    hnd,
    /* __in        DWORD dwIoControlCode */
    IOCTL_DISK_GET_DRIVE_GEOMETRY_EX,
    /* __in_bcount_opt(nInBufferSize) LPVOID lpInBuffer */
    NULL,
    /* __in        DWORD nInBufferSize */
    0,
    /* __out_bcount_part_opt(nOutBufferSize, *lpBytesReturned)
     * LPVOID lpOutBuffer */
    &geom,
    /* __in        DWORD nOutBufferSize */
    sizeof(DISK_GEOMETRY_EX),
    /* __out_opt   LPDWORD lpBytesReturned */
    &bytesReturned,
    /* __inout_opt LPOVERLAPPED lpOverlapped */
    NULL) != FALSE)
  {
    fprintf(stderr, "Got bytes per sector with "
        "IOCTL_DISK_GET_DRIVE_GEOMETRY_EX: %lu\n",
        geom.Geometry.BytesPerSector);
    return geom.Geometry.BytesPerSector;
  }
  else if(DeviceIoControl(
    /* __in        HANDLE hDevice */
    hnd,
    /* __in        DWORD dwIoControlCode */
    IOCTL_DISK_GET_DRIVE_GEOMETRY,
    /* __in_bcount_opt(nInBufferSize) LPVOID lpInBuffer */
    NULL,
    /* __in        DWORD nInBufferSize */
    0,
    /* __out_bcount_part_opt(nOutBufferSize, *lpBytesReturned)
     * LPVOID lpOutBuffer */
    &geom_legacy,
    /* __in        DWORD nOutBufferSize */
    sizeof(DISK_GEOMETRY),
    /* __out_opt   LPDWORD lpBytesReturned */
    &bytesReturned,
    /* __inout_opt LPOVERLAPPED lpOverlapped */
    NULL) != FALSE)
  {
    fprintf(stderr, "Got bytes per sector with IOCTL_DISK_GET_DRIVE_GEOMETRY: "
        "%lu\n", geom_legacy.BytesPerSector);
    return geom_legacy.BytesPerSector;
  }
  else {
    fprintf(stderr, "Failed to get bytes per sector: 0x%X\n", GetLastError());
    return -1;
  }
}
