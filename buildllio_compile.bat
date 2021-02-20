@echo off

setlocal

set TARGET_PREFIX=%~dp0dist\lib\llio
set I386_ARCHID=i386
set AMD64_ARCHID=amd64
set IA64_ARCHID=ia64
set ARM_ARCHID=arm
set ARM64_ARCHID=arm64
set SOURCE_DIR=%~dp0src\win32\llio
set SOURCE_FILES="%SOURCE_DIR%\llio_common.c" "%SOURCE_DIR%\org_catacombae_storage_io_win32_ReadableWin32FileStream.c" "%SOURCE_DIR%\org_catacombae_storage_io_win32_Win32FileStream.c"
set "VS2019_BUILD_PATH=C:\Program Files (x86)\Microsoft Visual Studio\2019\Community\VC\Auxiliary\Build"

:start

if "%1"=="gcc" goto build_gcc
if "%1"=="vc" goto build_vc
if "%1"=="wdk" goto build_wdk

goto printusage

:printusage
echo usage: %0 gcc
echo OR
echo usage: %0 vc [x86^|x64^|ia64^|arm^|arm64]
echo OR
echo usage: %0 wdk [x86^|x64^|ia64]
goto end

:build_gcc
echo Compiling with gcc...
set TARGET_DLL=%TARGET_PREFIX%_%I386_ARCHID%.dll
gcc -Wall -D_JNI_IMPLEMENTATION_ -Wl,--kill-at -shared %SOURCE_FILES% -o "%TARGET_DLL%"
if not "%ERRORLEVEL%"=="0" goto error
goto completed


:build_vc
if "%2"=="x86" goto setvars_x86
if "%2"=="x64" goto setvars_x64
if "%2"=="ia64" goto setvars_ia64
if "%2"=="arm" goto setvars_arm
if "%2"=="arm64" goto setvars_arm64
goto printusage

:setvars_x86
if "%VS90COMNTOOLS%"=="" echo Can not find Visual Studio 9 (environment variable VS90COMNTOOLS)! & goto error
pushd "%VS90COMNTOOLS%\..\..\VC"
call vcvarsall.bat x86
popd
set TARGET_DLL=%TARGET_PREFIX%_%I386_ARCHID%.dll
goto compile_vc

:setvars_x64
if "%VS90COMNTOOLS%"=="" echo Can not find Visual Studio 9 (environment variable VS90COMNTOOLS)! & goto error
pushd "%VS90COMNTOOLS%\..\..\VC"
call vcvarsall.bat x86_amd64
popd
set TARGET_DLL=%TARGET_PREFIX%_%AMD64_ARCHID%.dll
goto compile_vc

:setvars_ia64
if "%VS90COMNTOOLS%"=="" echo Can not find Visual Studio 9 (environment variable VS90COMNTOOLS)! & goto error
pushd "%VS90COMNTOOLS%\..\..\VC"
call vcvarsall.bat x86_ia64
popd
set TARGET_DLL=%TARGET_PREFIX%_%IA64_ARCHID%.dll
goto compile_vc

:setvars_arm
if not exist "%VS2019_BUILD_PATH%" echo Can not find Visual Studio 2019! & goto error
pushd "%VS2019_BUILD_PATH%"
call vcvarsall.bat x86_arm
popd
set TARGET_DLL=%TARGET_PREFIX%_%ARM_ARCHID%.dll
goto compile_vc

:setvars_arm64
if not exist "%VS2019_BUILD_PATH%" echo Can not find Visual Studio 2019! & goto error
pushd "%VS2019_BUILD_PATH%"
call vcvarsall.bat x86_arm64
popd
set TARGET_DLL=%TARGET_PREFIX%_%ARM64_ARCHID%.dll
goto compile_vc

:build_wdk
set WDK_PATH=C:\WinDDK\7600.16385.1
if not exist "%WDK_PATH%\" echo Can not find WDK 7.1.0 at %WDK_PATH%! & goto error

set COMPILE_FLAGS="/I%WDK_PATH%\inc\crt"

if "%2"=="x86" goto setvars_wdk_x86
if "%2"=="x64" goto setvars_wdk_x64
if "%2"=="ia64" goto setvars_wdk_ia64
goto printusage

:setvars_wdk_x86
pushd "%WDK_PATH%"
call bin\setenv.bat %WDK_PATH% fre x86 WXP no_oacr
popd
set TARGET_DLL=%TARGET_PREFIX%_%I386_ARCHID%.dll
set LINK_FLAGS=/libpath:%SDK_LIB_DEST%\i386\ /libpath:%WDK_PATH%\lib\crt\i386\
goto compile_vc

:setvars_wdk_x64
pushd "%WDK_PATH%"
call bin\setenv.bat %WDK_PATH% fre x64 WNET no_oacr
popd
set TARGET_DLL=%TARGET_PREFIX%_%AMD64_ARCHID%.dll
set LINK_FLAGS=/libpath:%SDK_LIB_DEST%\amd64\ /libpath:%WDK_PATH%\lib\crt\amd64\
goto compile_vc

:setvars_wdk_ia64
pushd "%WDK_PATH%"
call bin\setenv.bat %WDK_PATH% fre ia64 WNET no_oacr
popd
set TARGET_DLL=%TARGET_PREFIX%_%IA64_ARCHID%.dll
set LINK_FLAGS=/libpath:%SDK_LIB_DEST%\ia64\ /libpath:%WDK_PATH%\lib\crt\ia64\
goto compile_vc

:compile_vc

if "%JAVA_HOME%"=="" echo JAVA_HOME environment variable not defined! & goto error
set JDK_PATH=%JAVA_HOME%
set BUILD_DIR=%~dp0build.~

echo Cleaning build dir...
if exist "%BUILD_DIR%" rmdir /s /q "%BUILD_DIR%"
if exist "%BUILD_DIR%" echo Could not clean build dir! & goto error
mkdir "%BUILD_DIR%"
if not "%ERRORLEVEL%"=="0" goto error

echo Compiling with Visual C++...
cl -c "/Fo%BUILD_DIR%\\" /W3 "/I%JDK_PATH%\include" "/I%JDK_PATH%\include\win32" %COMPILE_FLAGS% "%SOURCE_DIR%\*.c"
set EXIT_CODE=%ERRORLEVEL%
if not "%EXIT_CODE%"=="0" goto error

echo Linking...
REM "/libpath:%JDK_PATH%\lib" 
link /dll "/out:%TARGET_DLL%" "/implib:%BUILD_DIR%\llio.lib" "/pdb:%BUILD_DIR%\llio.pdb" %LINK_FLAGS% "%BUILD_DIR%\*.obj"
set EXIT_CODE=%ERRORLEVEL%
if not "%EXIT_CODE%"=="0" goto error
goto completed

:completed
echo Target .dll generated at %TARGET_DLL%
echo Done!
goto end

:error
echo There were errors...
goto end

:end

endlocal
