@echo off
setlocal
REM ** The local extension of the PATH environment variable is neccessary for java to find llio_*.dll
PATH=%PATH%;%~dp0lib
java -cp "%~dp0lib\hfsx.jar" org.catacombae.hfsexplorer.tools.ResourceViewer %*
endlocal
