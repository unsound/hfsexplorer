#ifndef __STRINGBUILDER_HH
#define __STRINGBUILDER_HH

#include <stdio.h>
#include <windows.h>

#include "logging.hh"

/**
 * Implementation of a simple string builder class. Could probably have used std::string
 * for this purpose, but I wasn't sure about how well it integrated with windows wchar_t
 * types and conversions. Not much effort to write your own one.<br>
 * Implementation and specification are both in the header file, for seamless integration
 * with current code.
 */
class StringBuilder {
private:
  static const int CODEPAGE_ASCII = 20127;
  wchar_t *backBuffer;
  char *asciiBackBuffer;
  int backBufferLength;
public:
  /** Creates a new empty StringBuilder with length 0. */
  StringBuilder() {
    backBuffer = NULL;
    asciiBackBuffer = NULL;
    backBufferLength = 0;
  }
  
  /** Destroys the StringBuilder and deallocates its structures. */
  virtual ~StringBuilder() {
    if(backBuffer != NULL)
      delete[] backBuffer;
    if(asciiBackBuffer != NULL)
      delete[] asciiBackBuffer;
  }
  
  /**
   * The length, in UTF-16 characters, of the built string. Does NOT include the terminating NULL
   * character that may or may not be needed in an exported NULL-terminated string.
   */
  int length() {
    return backBufferLength;
  }
  
  void append(const char *cstr) {
    append(cstr, 0, strlen(cstr));
  }
  
  void append(const char *cstr, int pos, int len) {
    append(cstr, pos, len, CODEPAGE_ASCII, 0);
  }
  
  void append(const char *cstr, int pos, int len, UINT codePage, DWORD dwFlags) {
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
    append(wstr, 0, wcslen(wstr));
  }
  
  void append(const wchar_t *wstr, int pos, int len) {
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
    asciiBackBuffer = toASCIIString(new char[backBufferLength+1]);
    if(oldAsciiBackBuffer != NULL)
      delete[] oldAsciiBackBuffer;
  }
  
  /**
   * Returns a pre-generated wchar_t (UTF-16LE?) representation of the built string.
   */
  const wchar_t* toWideCharString() {
    return backBuffer;
  }
  
  /**
   * Stores the built string in <code>wstr</code>, which is assumed to be at least length()+1
   * wchar_t elements long to accommodate the string and the null terminator.<br>
   * It the caller's responsibility to make sure that the allocated memory area is large enough.
   *
   * @param wstr the target memory area where the string is to be stored.
   * @return <code>wstr</code> (for chaining purposes)
   */
  wchar_t* toWideCharString(wchar_t *wstr) {
    for(int i = 0; i < backBufferLength; ++i)
      wstr[i] = backBuffer[i];
    wstr[backBufferLength] = _T('\0');
    return wstr;
  }
  
  /**
   * Returns a pre-generated US-ASCII representation of the built string.
   */
  const char* toASCIIString() {
    return asciiBackBuffer;
  }
  
  /**
   * Stores the built string in <code>cstr</code>, which is assumed to be at least length()+1
   * char elements long to accommodate the string and the null terminator.<br>
   * It the caller's responsibility to make sure that the allocated memory area is large enough.
   *
   * @param cstr the target memory area where the string is to be stored.
   * @return <code>cstr</code> (for chaining purposes)
   */
  char* toASCIIString(char* cstr) {
    return toCString(cstr, CODEPAGE_ASCII, 0, NULL, NULL);
  }
  
  /**
   * Stores the built string in <code>cstr</code>, which is assumed to be at least length()+1
   * char elements long to accommodate the string and the null terminator.<br>
   * It the caller's responsibility to make sure that the allocated memory area is large enough.<br>
   * BUG/WARNING: If specifying a code page which converts one wide character to one or more chars,
   * instead of just one 8-bit char, memory usage will be unpredictable, as the length()+1 assumption
   * will not hold. If you calculate conservatively, the limit (length()+1)*16 will probably be enough
   * for all thinkable encodings, as no currently known character encoding uses more than 16 bytes per
   * character in the worst case (16 is a ridiculous number too, 4 is more realistic).
   *
   * @param cstr the target memory area where the string is to be stored.
   * @param codePage the Windows code page for the target string. See http://msdn.microsoft.com/en-us/library/ms776446(VS.85).aspx
   * @param dwFlags see http://msdn.microsoft.com/en-us/library/ms776420.aspx
   * @param lpDefaultChar see http://msdn.microsoft.com/en-us/library/ms776420.aspx
   * @param lpDefaultChar see http://msdn.microsoft.com/en-us/library/ms776420.aspx
   * @return <code>cstr</code> (for chaining purposes)
   */
  char* toCString(char* cstr, UINT codePage, DWORD dwFlags, LPCSTR lpDefaultChar, LPBOOL lpUsedDefaultChar) {
    int cstrLength = WideCharToMultiByte(codePage, dwFlags, backBuffer, -1, cstr, backBufferLength+1, lpDefaultChar, lpUsedDefaultChar);
    if(cstrLength != backBufferLength+1) {
      LOG(error, "FATAL ERROR: cstrLength(%d) != backBufferLength+1(%d)", cstrLength, (backBufferLength+1));
      throw "WideCharToMultiByte failed...";
    }
    return cstr;
  }
};

#endif
