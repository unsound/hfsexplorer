@echo off

REM If using the x86, x64 or ia64 suffixes, make sure you have Visual Studio 2008 installed, including the
REM x64 and ia64 cross compilers.

setlocal

set BUILD_DIR=%~dp0build.~
set SOURCE_DIR=%~dp0src\win32\launcher
set TARGET_EXE_PREFIX=%~dp0dist\bin\hfsexplorer
set RES_TARGET=%BUILD_DIR%\launcher.res
set OBJ_TARGET=%BUILD_DIR%\launcher.obj
set "VS2019_COMMUNITY_PATH=%ProgramFiles% (x86)\Microsoft Visual Studio\2019\Community\VC\Auxiliary\Build"

if "%JAVA_HOME%"=="" (
    echo Please set the JAVA_HOME environment variable to point at a Windows JDK before executing this script.
    goto error
)

if not "%VS90COMNTOOLS%"=="" (
    echo Building with Visual Studio 2008 tools...
    set "VSDIR=%VS90COMNTOOLS%\..\..\VC"
    goto buildtype
)

if exist "%VS2019_COMMUNITY_PATH%" (
    echo Building with Visual Studio 2019 Community edition tools...
    set "VSDIR=%VS2019_COMMUNITY_PATH%"
    goto buildtype
)

echo Can not find a supported Visual Studio version!
goto error

:buildtype
if "%1"=="console" goto console
if "%1"=="windows" goto windows

:printusage
echo usage: %0 [console^|windows] ^<x86^|x64^|ia64^|arm^|arm64^>
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
if "%2"=="arm" set TARGET_EXE=%TARGET_EXE_PREFIX%_arm.exe & goto setvars_arm
if "%2"=="arm64" set TARGET_EXE=%TARGET_EXE_PREFIX%_arm64.exe & goto setvars_arm64

echo Unknown architecture "%2"!
goto printusage

:setvars_x86
pushd "%VSDIR%"
call vcvarsall.bat x86
popd
goto build

:setvars_x64
pushd "%VSDIR%"
call vcvarsall.bat x86_amd64
popd
goto build

:setvars_ia64
pushd "%VSDIR%"
call vcvarsall.bat x86_ia64
popd
goto build

:setvars_arm
pushd "%VSDIR%"
call vcvarsall.bat x86_arm
popd
goto build

:setvars_arm64
pushd "%VSDIR%"
call vcvarsall.bat x86_arm64
popd
goto build

:build
echo Cleaning build dir...
if exist "%BUILD_DIR%" rmdir /s /q "%BUILD_DIR%"
if exist "%BUILD_DIR%" echo Could not clean build dir! & goto error
mkdir "%BUILD_DIR%"

echo Compiling resources...
rc /i "%~dp0doc\dmg_iconsource" /fo "%RES_TARGET%" "%SOURCE_DIR%\launcher.rc"
if not "%ERRORLEVEL%"=="0" goto error

echo Compiling source code...
REM "/Fe%TARGET_EXE%" 
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
