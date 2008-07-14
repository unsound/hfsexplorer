/*-
 * Copyright (C) 2008 Erik Larsson
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

#ifndef WOW64OPERATIONS_HH
#define WOW64OPERATIONS_HH

#include <windows.h>

/*
 * This class wraps some WOW64 operations that are commonly used, in a way that
 * allows them to be called from contexts where they are not supported by the
 * currently running operating system. This is achieved by dynamically looking up
 * the module handles in the dynamic library kernel32.dll, which exists in all
 * versions of Windows NT.
 */
class WOW64Operations {
public:
  /*
   * Determines whether the current process is running under WOW64.
   * This is a convenience method with different semantics from those below. If you
   * need to get more information on whether or not the system call was successfully
   * executed, use the BOOL(BOOL) or BOOL(HANDLE,BOOL) signature methods.
   * 
   * Return value:
   *   TRUE  - if we can be sure that the process is running under WOW64.
   *   FALSE - if the process is not running under WOW64 or if a problem occurred
   *           while executing the system call.
   */
  static BOOL isWOW64Process();
  
  /*
   * Determines whether the specified process is running under WOW64.
   * This is a convenience method with different semantics from those below. If you
   * need to get more information on whether or not the system call was successfully
   * executed, use the BOOL(BOOL) or BOOL(HANDLE,BOOL) signature methods.
   * 
   * Input parameters:
   *   hProcess  - the process on which the query is to be executed.
   *
   * Return value:
   *   TRUE  - if we can be sure that the process is running under WOW64.
   *   FALSE - if the process is not running under WOW64 or if a problem occurred
   *           while executing the system call.
   */
  static BOOL isWOW64Process(HANDLE hProcess);
  
  /*
   * Determines whether the current process is running under WOW64.
   * 
   * Output parameters:
   *   pbIsWow64 - pointer to the BOOL which receives the result of the system call.
   * 
   * Return value:
   *   TRUE  - if the system call completed successfully.
   *   FALSE - if a problem occurred while executing the system call.
   */
  static BOOL isWOW64Process(PBOOL pbIsWow64);
  
  /*
   * Determines whether the specified process is running under WOW64.
   * 
   * Input parameters:
   *   hProcess  - the process on which the query is to be executed.
   *
   * Output parameters:
   *   pbIsWow64 - pointer to the BOOL which receives the result of the system call.
   * 
   * Return value:
   *   TRUE  - if the system call completed successfully.
   *   FALSE - if a problem occurred while executing the system call.
   */
  static BOOL isWOW64Process(HANDLE hProcess, PBOOL pbIsWow64);
    
  /*
   * Restores file system redirection for the calling thread.
   * 
   * Output parameters:
   *   oldValue - the current value of the state of WOW64 file system redirection.
   * 
   * Return value:
   *   TRUE  - if the system call completed successfully.
   *   FALSE - if a problem occurred while executing the system call.
   */
  static BOOL disableWOW64FileSystemRedirection(PVOID *oldValue);
  
  /*
   * Restores file system redirection for the calling thread.
   * 
   * Input parameters:
   *   oldValue - the value previously aquired through a call to
   *              disableWOW64FileSystemRedirection.
   * 
   * Return value:
   *   TRUE  - if the system call completed successfully.
   *   FALSE - if a problem occurred while executing the system call.
   */
  static BOOL revertWOW64FileSystemRedirection(PVOID *oldValue);
  
private:
  typedef BOOL (WINAPI *FN_ISWOW64PROCESS) (HANDLE, PBOOL);
  typedef BOOL (WINAPI *FN_WOW64DISABLEWOW64FSREDIRECTION) (PVOID*);
  typedef BOOL (WINAPI *FN_WOW64REVERTWOW64FSREDIRECTION) (PVOID*);
};

// Implementation starts here

inline BOOL WOW64Operations::isWOW64Process() {
  return isWOW64Process(GetCurrentProcess());
}

inline BOOL WOW64Operations::isWOW64Process(HANDLE hProcess) {
  BOOL res;
  if(!isWOW64Process(hProcess, &res))
    return FALSE;
  else
    return res;
}

inline BOOL WOW64Operations::isWOW64Process(PBOOL pbIsWow64) {
  return isWOW64Process(GetCurrentProcess(), pbIsWow64);
}

inline BOOL WOW64Operations::isWOW64Process(HANDLE hProcess, PBOOL pbIsWow64) {
  FN_ISWOW64PROCESS fnIsWow64Process =
    (FN_ISWOW64PROCESS) GetProcAddress(GetModuleHandle(TEXT("kernel32")),
				       "IsWow64Process");
  
  if(fnIsWow64Process != NULL)
    return fnIsWow64Process(hProcess, pbIsWow64);
  else
    return FALSE;
}

inline BOOL WOW64Operations::disableWOW64FileSystemRedirection(PVOID *oldValue) {
  FN_WOW64DISABLEWOW64FSREDIRECTION fnWow64DisableWow64FsRedirection =
    (FN_WOW64DISABLEWOW64FSREDIRECTION) GetProcAddress(GetModuleHandle(TEXT("kernel32")),
						       "Wow64DisableWow64FsRedirection");
  
  if(fnWow64DisableWow64FsRedirection != NULL)
    return fnWow64DisableWow64FsRedirection(oldValue);
  else
    return FALSE;
}

inline BOOL WOW64Operations::revertWOW64FileSystemRedirection(PVOID *oldValue) {
  FN_WOW64REVERTWOW64FSREDIRECTION fnWow64RevertWow64FsRedirection =
    (FN_WOW64REVERTWOW64FSREDIRECTION) GetProcAddress(GetModuleHandle(TEXT("kernel32")),
						      "Wow64RevertWow64FsRedirection");
  
  if(fnWow64RevertWow64FsRedirection != NULL)
    return fnWow64RevertWow64FsRedirection(oldValue);
  else
    return FALSE;
}

#endif // WOW64OPERATIONS_HH
