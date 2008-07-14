#ifndef __STRINGBUILDER_HH
#define __STRINGBUILDER_HH

#include <stdio.h>
#include <windows.h>

#include "logging.hh"

class StringBuilder {
private:
  static const int CODEPAGE_ASCII = 20127;
  wchar_t *backBuffer;
  char *asciiBackBuffer;
  int backBufferLength;
public:
  StringBuilder() {
    //LOG(trace, "StringBuilder.<constructor>()");
    backBuffer = NULL;
    asciiBackBuffer = NULL;
    backBufferLength = 0;
    //LOG(trace, "Returning from StringBuilder.<constructor>()");
  }
  
  virtual ~StringBuilder() {
    //LOG(trace, "StringBuilder.<destructor>()");
    if(backBuffer != NULL)
      delete[] backBuffer;
    if(asciiBackBuffer != NULL)
      delete[] asciiBackBuffer;
    //LOG(trace, "Returning from StringBuilder.<destructor>()");
  }
  
  int length() {
    return backBufferLength;
  }
  
  void append(const char *cstr) {
    //LOG(trace, "void StringBuilder.append(const char *cstr)");
    append(cstr, 0, strlen(cstr));
    //LOG(trace, "Returning from void StringBuilder.append(const char *cstr)");
  }
  
  void append(const char *cstr, int pos, int len) {
    append(cstr, pos, len, CODEPAGE_ASCII, 0);
  }
  
  void append(const char *cstr, int pos, int len, UINT codePage, DWORD dwFlags) {
    //LOG(trace, "void StringBuilder.append(const char *cstr, int pos, int len, UINT codePage, DWORD dwFlags)");
    int wstrLength = MultiByteToWideChar(codePage, dwFlags, cstr+pos, len, NULL, 0);
    wchar_t *wstr = new wchar_t[wstrLength];
    int writtenChars = MultiByteToWideChar(codePage, dwFlags, cstr+pos, len, wstr, wstrLength);
    if(writtenChars != wstrLength) {
      LOG(error, "FATAL ERROR: writtenChars(%d) != wstrLength(%d)", writtenChars, wstrLength);
      throw "MultiByteToWideChar failed...";
    }
    append(wstr, 0, wstrLength);
    delete[] wstr;
  }
  
  void append(const wchar_t *wstr) {
    //LOG(trace, "void StringBuilder.append((wchar_t*) 0x%X)", wstr);
    append(wstr, 0, wcslen(wstr));
    //LOG(trace, "Returning from void StringBuilder.append((wchar_t*) 0x%X)", wstr);
  }
  
  void append(const wchar_t *wstr, int pos, int len) {
    //LOG(trace, "void StringBuilder.append((wchar_t*) 0x%X, %d, %d)", wstr, pos, len);
    int newBackBufferLength = backBufferLength+len;
    wchar_t *newBackBuffer = new wchar_t[newBackBufferLength+1];
    for(int i = 0; i < backBufferLength; ++i)
      newBackBuffer[i] = backBuffer[i];
    for(int i = 0; i < len; ++i)
      newBackBuffer[backBufferLength+i] = wstr[pos+i];
    newBackBuffer[newBackBufferLength] = L'\0';
    
    wchar_t *oldBackBuffer = backBuffer;
    backBuffer = newBackBuffer;
    backBufferLength = newBackBufferLength;
    if(oldBackBuffer != NULL)
      delete[] oldBackBuffer;
    
    char *oldAsciiBackBuffer = asciiBackBuffer;
    asciiBackBuffer = toCString(new char[backBufferLength+1]);
    if(oldAsciiBackBuffer != NULL)
      delete[] oldAsciiBackBuffer;
    //LOG(trace, "Returning from void StringBuilder.append((wchar_t*) 0x%X, %d, %d)", wstr, pos, len);
  }
  
  const wchar_t* toWideCharString() {
    return backBuffer;
  }
  
  wchar_t* toWideCharString(wchar_t *wstr) {
    for(int i = 0; i < backBufferLength; ++i)
      wstr[i] = backBuffer[i];
    return wstr;
  }
  
  const char* toASCIIString() {
    return asciiBackBuffer;
  }
  
  char* toCString(char* cstr) {
    return toCString(cstr, CODEPAGE_ASCII, 0, NULL, NULL);
  }
  
  char* toCString(char* cstr, UINT codePage, DWORD dwFlags, LPCSTR lpDefaultChar, LPBOOL lpUsedDefaultChar) {
    //LOG(trace, "char* toCString((char*) 0x%X, %d, 0x%X, (LPCSTR) 0x%X, (LPBOOL) 0x%X)", cstr, codePage, dwFlags, lpDefaultChar, lpUsedDefaultChar);
    int cstrLength = WideCharToMultiByte(codePage, dwFlags, backBuffer, -1, cstr, backBufferLength+1, lpDefaultChar, lpUsedDefaultChar);
    if(cstrLength != backBufferLength+1) {
      LOG(error, "FATAL ERROR: cstrLength(%d) != backBufferLength+1(%d)", cstrLength, (backBufferLength+1));
      throw "WideCharToMultiByte failed...";
    }
    /*LOG(trace, "Returning from char* toCString((char*) 0x%X, %d, 0x%X, (LPCSTR) 0x%X, (LPBOOL) 0x%X) with retval=0x%X",
      cstr, codePage, dwFlags, lpDefaultChar, lpUsedDefaultChar, cstr);*/
    return cstr;
  }
};

#endif
