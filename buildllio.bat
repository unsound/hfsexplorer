@echo off
call "%~dp0\buildall.bat"
javah -jni -classpath "%~dp0\build.~" -d "%~dp0\src.win32" org.catacombae.hfsexplorer.win32.WindowsLowLevelIO
gcc -Wall -D_JNI_IMPLEMENTATION_ -Wl,--kill-at -shared "%~dp0\src.win32\llio.c" -o "%~dp0\llio.dll"
