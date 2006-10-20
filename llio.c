#define UNICODE
#define _UNICODE
#include <windows.h>
#include <winioctl.h>
#include <tchar.h>
#include "WindowsLowLevelIO.h"

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
  throwByName(env, "java/lang/RuntimeException", message);
}

/* Stores down the data in the HANDLE pointer to a java byte array. The
   endianness of the byte array will be platform dependent, but it
   shouldn't matter as it will never be transfered between platforms.
   The size of the byte array will be equal to sizeof(HANDLE). */
jbyteArray getHandleData(JNIEnv *env, HANDLE hnd) {
  jbyteArray result = (*env)->NewByteArray(env, sizeof(HANDLE));
  jbyte byteArray[sizeof(HANDLE)];
  int i;
  BYTE *rawHnd = (BYTE*)&hnd;
  for(i = 0; i < sizeof(HANDLE); ++i) {
    byteArray[i] = rawHnd[i];
    // Assuming below that HANDLE is a DWORD... 
    //byteArray[i] = ((DWORD)hnd >> ((sizeof(HANDLE)-i)*8)) & 0xFF;
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

JNIEXPORT jbyteArray JNICALL Java_WindowsLowLevelIO_open(JNIEnv *env, jclass cls, jstring str) {
  printf("Java_WindowsLowLevelIO_open called\n");
  
  /* First, we convert the jstring to a jbyte array with the string encoded
     into UTF-8. */
  jsize utf8FilenameLength = (*env)->GetStringUTFLength(env, str);
  const jbyte *utf8Filename = (*env)->GetStringUTFChars(env, str, NULL);
  printf("utf8FilenameLength: %d bytes\n", utf8FilenameLength);

  
  /* Then, we convert the UTF-8 string into windows WCHARs */
  int wcFilenameSize = MultiByteToWideChar(CP_UTF8, 0, utf8Filename, utf8FilenameLength, NULL, 0);
  if(wcFilenameSize == 0) {
    handleMultiByteToWideCharError(env, GetLastError());
    return 0;
  }
  ++wcFilenameSize; // the last WCHAR is the null terminator
  printf("wcFilenameSize: %d\n", wcFilenameSize);
  WCHAR *wcFilename = (WCHAR*)malloc(sizeof(WCHAR)*wcFilenameSize);
  if(MultiByteToWideChar(CP_UTF8, 0, utf8Filename, utf8FilenameLength, wcFilename, wcFilenameSize) == 0) {
    handleMultiByteToWideCharError(env, GetLastError());
    return 0;
  }
  wcFilename[wcFilenameSize-1] = L'\0'; // Null termination.
  (*env)->ReleaseStringUTFChars(env, str, utf8Filename); // Release allocated resources.
  
  
  /* Perfect. */
  printf("Attempting to open \"");
  wprintf(L"%s", wcFilename);
  printf("\"\n");
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
JNIEXPORT void JNICALL Java_WindowsLowLevelIO_seek(JNIEnv *env, jclass cls, jlong pos, jbyteArray handleData) {
  printf("Java_WindowsLowLevelIO_seek called\n");
  HANDLE hnd = getHandle(env, handleData);
  LARGE_INTEGER oldFP = (LARGE_INTEGER)pos;
  LARGE_INTEGER newFP;
  if(SetFilePointerEx(hnd, oldFP, &newFP, FILE_BEGIN) == FALSE)
    throwByName(env, "java/lang/RuntimeException", "Couldn't set file pointer.");
}
JNIEXPORT jint JNICALL Java_WindowsLowLevelIO_read(JNIEnv *env, jclass cls, jbyteArray data, 
						   jint offset, jint length, jbyteArray handleData) {
  //_tprintf(_T("Java_WindowsLowLevelIO_read called\n"));  
  printf("Java_WindowsLowLevelIO_read called\n");
  HANDLE hnd = getHandle(env, handleData);
  BYTE* buffer = (BYTE*)malloc(length);
  DWORD bytesRead;
  //printf("Calling: ReadFile(0x%x, 0x%x, %d, 0x%x, NULL);\n", hnd, buffer, length, &bytesRead);
  if(ReadFile(hnd, buffer, length, &bytesRead, NULL) == FALSE) {
    printf("bytesRead: %d\n", bytesRead);
    printf("GetLastError(): %d\n", GetLastError());
    throwByName(env, "java/lang/RuntimeException", "Couldn't read from file.");
    bytesRead = -1;
  }
  else
    (*env)->SetByteArrayRegion(env, data, offset, bytesRead, buffer);
  free(buffer); // Clean up
  return bytesRead;
}

JNIEXPORT void JNICALL Java_WindowsLowLevelIO_close(JNIEnv *env, jclass cls, jbyteArray handleData) {
  HANDLE hnd = getHandle(env, handleData);
  if(CloseHandle(hnd) == FALSE) {
    throwByName(env, "java/lang/RuntimeException", "Could not close file.");
  }
}

JNIEXPORT void JNICALL Java_WindowsLowLevelIO_ejectMedia(JNIEnv *env, jclass cls, jbyteArray handleData) {
  HANDLE hnd = getHandle(env, handleData);
  DWORD bytesReturned;
  if(DeviceIoControl(hnd, IOCTL_STORAGE_EJECT_MEDIA, NULL, 0, NULL, 0, &bytesReturned, NULL) == FALSE)
    throwByName(env, "java/lang/RuntimeException", "Could not eject media.");  
}

JNIEXPORT void JNICALL Java_WindowsLowLevelIO_loadMedia(JNIEnv *env, jclass cls, jbyteArray handleData) {
  HANDLE hnd = getHandle(env, handleData);
  DWORD bytesReturned;
  if(DeviceIoControl(hnd, IOCTL_STORAGE_LOAD_MEDIA, NULL, 0, NULL, 0, &bytesReturned, NULL) == FALSE)
    throwByName(env, "java/lang/RuntimeException", "Could not eject media.");
  
}
