@echo off
pushd %~dp0
javac WindowsLowLevelIO.java
javah -jni WindowsLowLevelIO
gcc -Wall -D_JNI_IMPLEMENTATION_ -Wl,--kill-at -shared llio.c -o llio.dll
popd
