@echo off
setlocal
REM ** The local extension of the PATH environment variable is neccessary for java to find llio.dll
PATH=%PATH%;%~dp0
java -cp "%~dp0lib\hfsx.jar";"%~dp0lib\swing-layout-1.0.1.jar" org.catacombae.hfsexplorer.FileSystemBrowserWindow
endlocal