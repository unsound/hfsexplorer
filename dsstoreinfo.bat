@echo off
setlocal
REM ** The local extension of the PATH environment variable is neccessary for java to find llio_*.dll
PATH=%PATH%;%~dp0dist\lib
java -cp "%~dp0dist\lib\hfsx.jar" org.catacombae.hfsexplorer.tools.DSStoreInfo %*
endlocal
