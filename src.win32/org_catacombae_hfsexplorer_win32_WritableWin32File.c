#define UNICODE
#define _UNICODE
#define _WIN32_WINNT 0x0500 // Minimum windows 2000 required, beacuse of GetFileSizeEx. (could be done in a more compatible way, but why care about legacy crap?)
#include <windows.h>
#include <winioctl.h>
#include <tchar.h>
#include "org_catacombae_hfsexplorer_win32_WritableWin32File.h"
#include "llio_common.h"
#define DEBUG FALSE

/*
 * Class:     org_catacombae_hfsexplorer_win32_WritableWin32File
 * Method:    open
 * Signature: (Ljava/lang/String;)[B
 */
/* The contents of this function is basically copied from llio.c with minor modifications.
   Any major modifications should be backported. (...and more reusable code should be written...) */
JNIEXPORT jbyteArray JNICALL Java_org_catacombae_hfsexplorer_win32_WritableWin32File_openNative
(JNIEnv *env, jclass cls, jstring str) {
  if(DEBUG) printf("Java_WritableWin32File_openNative called\n");
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
  HANDLE hnd = CreateFileW(wcFilename, GENERIC_READ | GENERIC_WRITE, // MODIFIED HERE (added write)
			   FILE_SHARE_READ /*| FILE_SHARE_WRITE*/ /*0*/, // MODIFIED HERE
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

/*
 * Class:     org_catacombae_hfsexplorer_win32_WritableWin32File
 * Method:    write
 * Signature: ([BII[B)V
 */
JNIEXPORT void JNICALL Java_org_catacombae_hfsexplorer_win32_WritableWin32File_write
(JNIEnv *env, jclass cls, jbyteArray data, jint offset, jint length, jbyteArray handleData) {
  //_tprintf(_T("Java_WindowsLowLevelIO_read called\n"));  
  if(DEBUG) printf("Java_WritableWin32File_write called\n");

  HANDLE hnd = getHandle(env, handleData);
  BYTE* buffer = (BYTE*)malloc(length);
  (*env)->GetByteArrayRegion(env, data, offset, length, buffer); // (jbyteArray)data->(BYTE*)buffer
  DWORD bytesWritten;
  //printf("Calling: ReadFile(0x%x, 0x%x, %d, 0x%x, NULL);\n", hnd, buffer, length, &bytesRead);
  if(WriteFile(hnd, buffer, length, &bytesWritten, NULL) == FALSE) {
    //printf("bytesRead: %u\n", (int)bytesRead);
    //printf("GetLastError(): %u\n", (int)GetLastError());
    throwByName(env, "java/lang/RuntimeException", "Couldn't write to file.");
    //bytesRead = -1;
  }
  else if(bytesWritten != length)
    throwByName(env, "java/lang/RuntimeException", "Could not write entire buffer to file! Some part of it may have been written.");
  
  free(buffer); // Clean up
}
