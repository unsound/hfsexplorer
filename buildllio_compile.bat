@echo off

setlocal

set TARGET_DLL=%~dp0dist\llio.dll
set SOURCE_DIR=%~dp0src.win32\llio
set SOURCE_FILES="%SOURCE_DIR%\llio_common.c" "%SOURCE_DIR%\org_catacombae_hfsexplorer_win32_WindowsLowLevelIO.c" "%SOURCE_DIR%\org_catacombae_hfsexplorer_win32_WritableWin32File.c"

:start

if "%1"=="gcc" goto compile_gcc
if "%1"=="vc" goto compile_vc

goto printusage

:printusage
echo usage: %0 [gcc^|vc]
goto end

:compile_gcc
echo Compiling with gcc...
gcc -Wall -D_JNI_IMPLEMENTATION_ -Wl,--kill-at -shared %SOURCE_FILES% -o "%TARGET_DLL%"
if not "%ERRORLEVEL%"=="0" goto error
goto completed


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
echo Done!
goto end

:error
echo There were errors...
goto end

:end

endlocal