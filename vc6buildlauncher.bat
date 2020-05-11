@echo off

setlocal

set BUILD_DIR=%~dp0build.~
set SOURCE_DIR=%~dp0src\win32\launcher
set TARGET_EXE_PREFIX=%~dp0dist\bin
set RES_TARGET=%BUILD_DIR%\launcher.res
set OBJ_TARGET=%BUILD_DIR%\launcher.obj

if "%JAVA_HOME%"=="" echo Please set JAVA_HOME to point at your JDK before running this script. & goto end

if "%1"=="console" goto console
if "%1"=="windows" goto windows

:printusage
echo usage: %0 [console^|windows] ^<x86^>
goto end

:console
set SUBSYSTEM=CONSOLE
goto arch

:windows
set SUBSYSTEM=WINDOWS
goto arch

:arch
if "%2"=="" set TARGET_EXE=%TARGET_EXE_PREFIX%\hfsexplorer.exe & goto build
if "%2"=="x86" set TARGET_EXE=%TARGET_EXE_PREFIX%\hfsexplorer_x86.exe & goto build

echo Unknown architecture "%2"!
goto printusage

:build
echo Cleaning build dir...
if exist "%BUILD_DIR%" rmdir /s /q "%BUILD_DIR%"
if exist "%BUILD_DIR%" echo Could not clean build dir! & goto error
mkdir "%BUILD_DIR%"

echo Compiling resources...
rc /fo "%RES_TARGET%" "%SOURCE_DIR%\launcher.rc"
if not "%ERRORLEVEL%"=="0" goto error

echo Compiling source code...
cl /c "/Fo%OBJ_TARGET%" "/I%JAVA_HOME%\include" "/I%JAVA_HOME%\include\win32" "%SOURCE_DIR%\launcher.cpp"
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