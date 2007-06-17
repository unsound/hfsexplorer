#include "llio_common.h"

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

