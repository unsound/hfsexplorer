@echo off
gcc -Wall -D_JNI_IMPLEMENTATION_ -Wl,--kill-at -shared %SOURCE_FILES% -o "%~dp0\llio.dll"
