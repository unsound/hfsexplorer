@echo off
setlocal
pushd %~dp0dist
call runfsb.bat %1 %2 %3 %4 %5 %6 %7 %8 %9
popd
endlocal
