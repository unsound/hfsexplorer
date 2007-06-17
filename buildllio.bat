@echo off
set SOURCE_FILES="%~dp0src.win32\llio_common.c" "%~dp0src.win32\llio.c" "%~dp0src.win32\org_catacombae_hfsexplorer_win32_WritableWin32File.c"
call "%~dp0\buildall.bat"
echo Creating header file for org.catacombae.hfsexplorer.win32.WindowsLowLevelIO...
javah -jni -classpath "%~dp0\build.~" -d "%~dp0\src.win32" org.catacombae.hfsexplorer.win32.WindowsLowLevelIO
echo Creating header file for org.catacombae.hfsexplorer.win32.WritableWin32File...
javah -jni -classpath "%~dp0\build.~" -d "%~dp0\src.win32" org.catacombae.hfsexplorer.win32.WritableWin32File
echo Compiling with gcc...
call "%~dp0\buildllio_compile.bat"
echo Done!
