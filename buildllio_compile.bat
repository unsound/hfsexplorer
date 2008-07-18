@echo off

setlocal

set TARGET_PREFIX=%~dp0dist\lib\llio
set I386_ARCHID=i386
set AMD64_ARCHID=amd64
set IA64_ARCHID=ia64
set SOURCE_DIR=%~dp0src.win32\llio
set SOURCE_FILES="%SOURCE_DIR%\llio_common.c" "%SOURCE_DIR%\org_catacombae_hfsexplorer_win32_WindowsLowLevelIO.c" "%SOURCE_DIR%\org_catacombae_hfsexplorer_win32_WritableWin32File.c"

:start

if "%1"=="gcc" goto build_gcc
if "%1"=="vc" goto build_vc

goto printusage

:printusage
echo usage: %0 gcc
echo OR
echo usage: %0 vc [x86^|x64^|ia64]
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
cl -c "/Fo%BUILD_DIR%\\" /W3 "/I%JDK_PATH%\include" "/I%JDK_PATH%\include\win32" "%SOURCE_DIR%\*.c"
set EXIT_CODE=%ERRORLEVEL%
if not "%EXIT_CODE%"=="0" goto error

echo Linking...
REM "/libpath:%JDK_PATH%\lib" 
link /dll "/out:%TARGET_DLL%" "/implib:%BUILD_DIR%\llio.lib" "/pdb:%BUILD_DIR%\llio.pdb" "%BUILD_DIR%\*.obj"
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