@echo off

REM If using the x86, x64 or ia64 suffixes, make sure you have Visual Studio 2008 installed, including the
REM x64 and ia64 cross compilers.

setlocal

set BUILD_DIR=%~dp0build.~
set TARGET_EXE_PREFIX=%~dp0dist\launcher
set RES_TARGET=%BUILD_DIR%\launcher.res
set OBJ_TARGET=%BUILD_DIR%\launcher.obj

if "%1"=="console" goto console
if "%1"=="windows" goto windows

:printusage
echo usage: %0 [console^|windows] ^<x86^|x64^|ia64^>
goto end

:console
set SUBSYSTEM=CONSOLE
goto arch

:windows
set SUBSYSTEM=WINDOWS
goto arch

:arch
if "%2"=="" set TARGET_EXE=%TARGET_EXE_PREFIX%.exe & goto build
if "%2"=="x86" set TARGET_EXE=%TARGET_EXE_PREFIX%_x86.exe & goto setvars_x86
if "%2"=="x64" set TARGET_EXE=%TARGET_EXE_PREFIX%_x64.exe & goto setvars_x64
if "%2"=="ia64" set TARGET_EXE=%TARGET_EXE_PREFIX%_ia64.exe & goto setvars_ia64

echo Unknown architecture "%2"!
goto printusage

:setvars_x86
if "%VS90COMNTOOLS%"=="" echo Can not find Visual Studio 9 (environment variable VS90COMNTOOLS)! & goto error
pushd "%VS90COMNTOOLS%\..\..\VC"
call vcvarsall.bat x86
popd
goto build

:setvars_x64
if "%VS90COMNTOOLS%"=="" echo Can not find Visual Studio 9 (environment variable VS90COMNTOOLS)! & goto error
pushd "%VS90COMNTOOLS%\..\..\VC"
call vcvarsall.bat x86_amd64
popd
goto build

:setvars_ia64
if "%VS90COMNTOOLS%"=="" echo Can not find Visual Studio 9 (environment variable VS90COMNTOOLS)! & goto error
pushd "%VS90COMNTOOLS%\..\..\VC"
call vcvarsall.bat x86_ia64
popd
goto build

:build
echo Cleaning build dir...
if exist "%BUILD_DIR%" rmdir /s /q "%BUILD_DIR%"
if exist "%BUILD_DIR%" echo Could not clean build dir! & goto error
mkdir "%BUILD_DIR%"

echo Compiling resources...
rc /fo "%RES_TARGET%" "%~dp0src.win32\launcher\launcher.rc"
if not "%ERRORLEVEL%"=="0" goto error

echo Compiling source code...
REM "/Fe%TARGET_EXE%" 
cl /c "/Fo%OBJ_TARGET%" "/IC:\Program Files\Java\jdk\include" "/IC:\Program Files\Java\jdk\include\win32" src.win32\launcher\launcher.cpp
if not "%ERRORLEVEL%"=="0" goto error

echo Linking...
link /defaultlib:user32 /defaultlib:shell32 /defaultlib:advapi32 /defaultlib:ole32 /subsystem:%SUBSYSTEM% /entry:mainCRTStartup "/out:%TARGET_EXE%" "%OBJ_TARGET%" "%RES_TARGET%"
if not "%ERRORLEVEL%"=="0" goto error
goto completed

:completed
echo Target generated at %TARGET_EXE%
echo Done!
goto end

:error
echo There were errors...
goto end

:end
endlocal