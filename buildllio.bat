@echo off
setlocal

:vars
set CV_JAVA_BUILD_DIR=%~dp0.antbuild~
set COMPILER=%1
set ARCHITECTURE=%2
set LLIO_SOURCEDIR=%~dp0\src\win32\llio

:start
if "%COMPILER%" == "gcc" goto check_gcc_arch
if "%COMPILER%" == "vc" goto check_vc_arch
echo You must specify which compiler to use ("gcc" or "vc")...
goto end

:check_vc_arch
if "%ARCHITECTURE%" == "x86" goto build
if "%ARCHITECTURE%" == "x64" goto build
if "%ARCHITECTURE%" == "ia64" goto build
if "%ARCHITECTURE%" == "" (
    echo You must specify which architecture to build ("x86", "x64" or "ia64"^).
) else (
    echo Invalid architecture "%ARCHITECTURE%" for vc build ("x86", "x64" and "ia64" are valid^).
)
goto end

:check_gcc_arch
if "%ARCHITECTURE%"=="" goto build
if "%ARCHITECTURE%"=="x86" goto build
echo Invalid architecture "%ARCHITECTURE%" for gcc build (only "x86" is valid).
goto end

:build

echo Building all classes to make sure all is well...
call "%~dp0\buildall.bat"
if not "%ERRORLEVEL%"=="0" goto error

echo Creating header file for org.catacombae.storage.io.win32.ReadableWin32FileStream...
javah -jni -classpath "%CV_JAVA_BUILD_DIR%" -d "%LLIO_SOURCEDIR%" org.catacombae.storage.io.win32.ReadableWin32FileStream
if not "%ERRORLEVEL%"=="0" goto error

echo Creating header file for org.catacombae.storage.io.win32.Win32FileStream...
javah -jni -classpath "%CV_JAVA_BUILD_DIR%" -d "%LLIO_SOURCEDIR%" org.catacombae.storage.io.win32.Win32FileStream
if not "%ERRORLEVEL%"=="0" goto error

if "%COMPILER%"=="gcc" goto gcc_compile
if "%COMPILER%"=="vc" goto vc_compile
goto error

:gcc_compile

echo Compiling with GCC...
call "%~dp0\buildllio_compile.bat" gcc
if not "%ERRORLEVEL%"=="0" goto error
goto completed

:vc_compile

echo Compiling with Visual C++...
call "%~dp0\buildllio_compile.bat" vc %ARCHITECTURE%
if not "%ERRORLEVEL%"=="0" goto error
goto completed

:error
echo There were errors!
goto end

:completed
echo Done!
goto end

:end

endlocal
