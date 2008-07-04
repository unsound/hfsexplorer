@echo off
setlocal

:vars
echo Calling common_vars
call "%~dp0common_vars.bat"
echo Finished calling common_vars
set COMPILER=%1
set ARCHITECTURE=%2
set LLIO_SOURCEDIR=%~dp0\src.win32\llio

:start
if "%COMPILER%"=="gcc" goto build
if "%COMPILER%"=="vc" goto build
echo You must specify which compiler to use ("gcc" or "vc")...
goto end

:build

echo Building all classes to make sure all is well...
call "%~dp0\buildall.bat"
if not "%ERRORLEVEL%"=="0" goto error

echo Creating header file for org.catacombae.hfsexplorer.win32.WindowsLowLevelIO...
javah -jni -classpath "%CV_JAVA_BUILD_DIR%" -d "%LLIO_SOURCEDIR%" org.catacombae.hfsexplorer.win32.WindowsLowLevelIO
if not "%ERRORLEVEL%"=="0" goto error

echo Creating header file for org.catacombae.hfsexplorer.win32.WritableWin32File...
javah -jni -classpath "%CV_JAVA_BUILD_DIR%" -d "%LLIO_SOURCEDIR%" org.catacombae.hfsexplorer.win32.WritableWin32File
if not "%ERRORLEVEL%"=="0" goto error

if "%COMPILER%"=="gcc" goto gcc_compile
if "%COMPILER%"=="vc" goto vc_compile
goto error

:gcc_compile

echo Compiling with GCC...
call "%~dp0\buildllio_compile.bat" gcc "%ARCHITECTURE%"
if not "%ERRORLEVEL%"=="0" goto error
goto completed

:vc_compile

echo Compiling with Visual C++...
call "%~dp0\buildllio_compile.bat" vc "%ARCHITECTURE%"
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
