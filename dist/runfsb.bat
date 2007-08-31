@echo off
setlocal
REM ** The local extension of the PATH environment variable is neccessary for java to find llio.dll
PATH=%PATH%;%~dp0
java -cp "%~dp0lib\hfsx.jar";"%~dp0lib\swing-layout-1.0.1.jar";"%~dp0lib\hfsx_dmglib.jar";"%~dp0lib\apache-ant-1.7.0-bzip2.jar";"%~dp0lib\iharder-base64.jar" org.catacombae.hfsexplorer.FileSystemBrowserWindow %1 %2 %3 %4 %5 %6 %7 %8 %9
endlocal
