@echo off
:begin
setlocal
pushd %~dp0

REM call "%~dp0common_vars.bat"
REM set LIBRARY_PATH=%~dp0lib
REM set JARFILE_PATH=%~dp0dist\lib
REM set JARFILENAME=hfsx.jar
REM set BUILD_CLASSPATH="%~dp0build.~";"%~dp0dist\lib\catacombae_io.jar";"%~dp0dist\lib\swing-layout-1.0.3.jar";"%~dp0dist\lib\hfsx_dmglib.jar";"%~dp0lib\AppleJavaExtensions.jar";"%~dp0lib\java_awt_Desktop.jar"
set SOURCES_DIR=%~dp0src
REM set BUILD_DIR=%CV_JAVA_BUILD_DIR%
REM set RESOURCE_SRC_DIR=%~dp0resource
REM set RESOURCE_DST_DIR=%BUILD_DIR%\res
set BUILDENUM_CP="%~dp0lib\buildenumerator.jar"
REM set MANIFEST=%~dp0src.META-INF\MANIFEST.MF

REM echo REMoving all class files...
REM if exist "%BUILD_DIR%" rmdir /s /q "%BUILD_DIR%"
REM if not "%ERRORLEVEL%"=="0" goto error
REM mkdir "%BUILD_DIR%"
REM if not "%ERRORLEVEL%"=="0" goto error

REM echo Copying resources to classpath...
REM mkdir "%RESOURCE_DST_DIR%"
REM copy /y \\.\\"%RESOURCE_SRC_DIR%\*.png" "%RESOURCE_DST_DIR%" > NUL:

echo Incrementing build number...
java -cp %BUILDENUM_CP% BuildEnumerator "%SOURCES_DIR%\org\catacombae\hfsexplorer\BuildNumber.java" 1

REM Found out the trick for wildcard expansion of
REM spaced file names! (enclosed within "") Just 
REM prepend \\.\\ (\\.\ really, but last \ needed for ")

echo Building with ant...
call ant build-all
if not "%ERRORLEVEL%"=="0" goto error

goto end

:error
echo There were errors...
echo Decrementing build number...
java -cp %BUILDENUM_CP% BuildEnumerator "%SOURCES_DIR%\org\catacombae\hfsexplorer\BuildNumber.java" -1
goto end

:end
popd
endlocal