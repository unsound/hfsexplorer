/* Utility functions common to all llio source files. */

#ifndef _Included_llio_common
#define _Included_llio_common

#include <windows.h>
#include <winioctl.h>
#include <tchar.h>
#include <jni.h>

void throwByName(JNIEnv *env, const char *name, const char *msg);
void handleMultiByteToWideCharError(JNIEnv *env, DWORD errorCode);
jbyteArray getHandleData(JNIEnv *env, HANDLE hnd);
HANDLE getHandle(JNIEnv *env, jbyteArray handleData);

#endif
