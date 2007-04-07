@echo off
if "%1"=="console" goto console
if "%1"=="windows" goto win
goto error

:win
echo Building windows app...
gcc -g -mwindows -Wall -D_JNI_IMPLEMENTATION_ -Wl,--kill-at "%~dp0src.win32\launcher.c" -o "%~dp0hfsexplorer.exe"
goto end

:console
echo Building console app...
gcc -g -mconsole -Wall -D_JNI_IMPLEMENTATION_ -Wl,--kill-at "%~dp0src.win32\launcher.c" -o "%~dp0hfsexplorer.exe"
goto end

:error
echo You must specify console or windows application...

:end
