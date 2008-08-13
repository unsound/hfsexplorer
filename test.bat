@echo off
setlocal
PATH=%PATH%;%~dp0dist\lib
java -cp "%~dp0dist\lib\hfsx.jar" org.catacombae.hfsexplorer.%1 %2 %3 %4 %5 %6 %7 %8 %9
endlocal
