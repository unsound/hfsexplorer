@echo off
setlocal
set LAUNCHER_SRC=%~dp0src\win32\launcher
set OUTDIR=%~dp0dist\bin
set OUTFILE=hfsexplorer.exe
set BUILD_DIR=%~dp0build.~

if "%JAVA_HOME%"=="" echo Please set the JAVA_HOME environment variable to point at a Windows JDK before executing this script. & goto error

if not "%CPATH%"=="" set "CPATH=%CPATH%;"
set CPATH=%CPATH%%JAVA_HOME%\include;%JAVA_HOME%\include\win32
echo "CPATH: %CPATH%"

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
windres -I "%~dp0doc\dmg_iconsource" "%LAUNCHER_SRC%\launcher.rc" "%BUILD_DIR%"\launcher_res.o
if not "%ERRORLEVEL%"=="0" goto error

echo Compiling launcher.cpp...
g++ -g -Wall -D_JNI_IMPLEMENTATION_ -c "%LAUNCHER_SRC%\launcher.cpp" -o "%BUILD_DIR%\launcher.o"
if not "%ERRORLEVEL%"=="0" goto error

echo Building %BUILDTYPE% app...
g++ -static-libgcc -static-libstdc++ -g -m%BUILDTYPE% -Wall -D_JNI_IMPLEMENTATION_ -Wl,--kill-at "%BUILD_DIR%\launcher_res.o" "%BUILD_DIR%\launcher.o" -o "%OUTDIR%\%OUTFILE%"
if not "%ERRORLEVEL%"=="0" goto error
echo Done!
goto end

:error
echo There were errors...
goto end

:end
endlocal
