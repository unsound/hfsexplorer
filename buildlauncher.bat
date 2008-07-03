@echo off
setlocal
set LAUNCHER_SRC=%~dp0src.win32\launcher
set OUTDIR=%~dp0dist
set OUTFILE=hfsexplorer.exe
set BUILD_DIR=%~dp0build.~

if "%1"=="console" goto console
if "%1"=="windows" goto win
echo You must specify console or windows application...
goto error

:win
set BUILDTYPE=windows
goto build

:console
set BUILDTYPE=console
goto build

:build
echo Cleaning build dir...
if exist "%BUILD_DIR%" rmdir /s /q "%BUILD_DIR%"
if exist "%BUILD_DIR%" echo Could not clean build dir!
mkdir "%BUILD_DIR%"

echo Compiling resources...
pushd "%LAUNCHER_SRC%"
windres .\launcher.rc ..\..\build.~\launcher_res.o
set WINDRES_RES=%ERRORLEVEL%
popd
if not "%WINDRES_RES%"=="0" goto error

echo Compiling launcher.cpp...
g++ -g -Wall -D_JNI_IMPLEMENTATION_ -c "%LAUNCHER_SRC%\launcher.cpp" -o "%BUILD_DIR%\launcher.o"
if not "%ERRORLEVEL%"=="0" goto error

echo Building %BUILDTYPE% app...
g++ -g -m%BUILDTYPE% -Wall -D_JNI_IMPLEMENTATION_ -Wl,--kill-at "%BUILD_DIR%\launcher_res.o" "%BUILD_DIR%\launcher.o" -o "%OUTDIR%\%OUTFILE%"
if not "%ERRORLEVEL%"=="0" goto error
echo Done!
goto end

:error
echo There were errors...
goto end

:end
endlocal