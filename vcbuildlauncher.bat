@echo off

setlocal

set BUILD_DIR=%~dp0build.~
set TARGET_EXE=%~dp0dist\launcher.exe

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
if "%2"=="" goto build
if "%2"=="x86" goto setvars_x86
if "%2"=="x64" goto setvars_x64
if "%2"=="ia64" goto setvars_ia64

echo Unknown architecture "%2"!
goto error

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
if exist "%BUILD_DIR%" echo Could not clean build dir!
if exist "%BUILD_DIR%" goto error
mkdir "%BUILD_DIR%"

cl "/Fo%BUILD_DIR%\\" "/Fe%TARGET_EXE%" "/IC:\Program Files\Java\jdk\include" "/IC:\Program Files\Java\jdk\include\win32" src.win32\launcher\launcher.cpp /link /defaultlib:user32 /defaultlib:shell32 /defaultlib:advapi32 /subsystem:%SUBSYSTEM% /entry:mainCRTStartup
REM  /out:dist\launcher.exe
goto completed

:completed
echo Done!
goto end

:error
echo There were errors...
goto end

:end
endlocal